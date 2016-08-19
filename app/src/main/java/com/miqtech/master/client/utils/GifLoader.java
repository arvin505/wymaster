package com.miqtech.master.client.utils;

import android.graphics.Movie;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.miqtech.master.client.application.WangYuApplication;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xiaoyi on 2016/5/24.
 * gif loader
 * lrucache acache net 三级缓存
 * 根据src地址缓存
 */
public class GifLoader {

    //缓存
    private LruCache<String, byte[]> mMemoryCache;

    //网络请求线程池
    private volatile ExecutorService mGifThreadPool = null;

    //acache  文件缓存
    private ACache mCache;


    //在构造方法中去初始化 cache
    private GifLoader() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        mCache = ACache.get(WangYuApplication.appContext);
        int mCacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, byte[]>(mCacheSize) {
            @Override
            protected int sizeOf(String key, byte[] value) {
                return value.length;
            }
        };
    }

    private static volatile GifLoader mInstance;

    //使用单利模式  向这种load helper 之类的工具 最好是单例模式
    public static GifLoader getInstance() {
        if (mInstance == null) {
            synchronized (GifLoader.class) {
                if (mInstance == null) {
                    mInstance = new GifLoader();
                }
            }
        }
        return mInstance;
    }

    /**
     * 通过线程池控制
     * gif 图片的请求
     */
    private ExecutorService getThreadPool() {
        if (mGifThreadPool == null) {
            synchronized (ExecutorService.class) {
                if (mGifThreadPool == null) {
                    mGifThreadPool = Executors.newFixedThreadPool(2);
                }
            }
        }
        return mGifThreadPool;
    }


    /**
     * 根据url 地址缓存gif byte
     *
     * @param key
     * @param bytes
     */
    private void addGif2Memory(String key, byte[] bytes) {
        if (getGifFromMemory(key) == null && bytes != null) {
            mMemoryCache.put(key, bytes);
        }
    }

    /**
     * 通过key 拿到movie
     *
     * @param key
     * @return
     */
    private Movie getGifFromMemory(String key) {
        byte[] bytes = mMemoryCache.get(key);
        if (bytes != null) {
            return Movie.decodeByteArray(bytes, 0, bytes.length);
        }
        return null;
    }

    /**
     * 网络请求获取到gif的字节数组，转化为movie
     *
     * @param url
     * @param listerner
     * @return
     * @throws Exception
     */
    public void getGifMovieFromHttp(final String url, final OnGifLoaderListerner listerner) throws Exception {
        final String subUrl = url.replaceAll("[^\\w]", "");
        final Movie bytes = showCacheBytes(subUrl);
        if (bytes != null) {
            listerner.onGifLoader(bytes, url);
        } else {
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    listerner.onGifLoader((Movie) msg.obj, url);
                }
            };

            getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] bytes = getBytesFromUrl(url);
                        Message msg = handler.obtainMessage();
                        msg.obj = Movie.decodeByteArray(bytes, 0, bytes.length);
                        handler.sendMessage(msg);
                        addGif2Memory(subUrl, bytes);
                        mCache.put(subUrl, bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 从内存缓存中获取movie
     *
     * @param url
     * @return
     * @throws Exception
     */
    private Movie showCacheBytes(String url) throws Exception {
        if (getGifFromMemory(url) != null) {
            return getGifFromMemory(url);
        } else {
            if (mCache.get(url) != null) {
                InputStream in = mCache.get(url);
                byte[] bytes = streamToBytes(in);
                mMemoryCache.put(url, bytes);
                return Movie.decodeByteArray(bytes, 0, bytes.length);
            }
            return null;
        }
    }

    /**
     * 网络请求 获取输入流
     *
     * @param url
     * @return
     * @throws Exception
     */
    private byte[] getBytesFromUrl(String url) throws Exception {
        URL gifUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) gifUrl.openConnection();
        InputStream is = conn.getInputStream();
        return streamToBytes(is);
    }

    /**
     * 输入流转byte 数组
     *
     * @param is
     * @return
     */
    private byte[] streamToBytes(InputStream is) {
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len;
        try {
            while ((len = is.read(buffer)) >= 0) {
                os.write(buffer, 0, len);
            }
        } catch (java.io.IOException e) {
        }
        return os.toByteArray();
    }

    /**
     * 加载gif的回调接口
     */
    public static interface OnGifLoaderListerner {
        void onGifLoader(Movie movie, String url);
    }
}
