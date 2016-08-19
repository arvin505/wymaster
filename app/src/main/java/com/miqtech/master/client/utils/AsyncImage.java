package com.miqtech.master.client.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.widget.ImageView;

import com.miqtech.master.client.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

/**
 * 使用Picasso库加载网络图片和本地图片
 *
 * @author Alex
 */
public class AsyncImage {

    private static boolean isInited = false;
    static final String FILE = "file:///";
    static final String DRAWABLE = "drawable://";
    //
    static DisplayImageOptions noCacheOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.default_img).showImageOnFail(R.drawable.default_img).cacheOnDisk(true)
            .displayer(new FadeInBitmapDisplayer(200)).bitmapConfig(Config.RGB_565).build();
    //

    static DisplayImageOptions photoOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.default_img).showImageOnFail(R.drawable.default_img)
            .showImageOnLoading(R.drawable.default_img).cacheInMemory(true).cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(200))
            .bitmapConfig(Config.RGB_565).build();

    static DisplayImageOptions headphotoOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.cryptonym).showImageOnFail(R.drawable.default_img)
            .showImageOnLoading(R.drawable.cryptonym).cacheInMemory(true).cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(200))
            .bitmapConfig(Config.RGB_565).build();

    static DisplayImageOptions personalHomephotoOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.personalhome_bg).showImageOnFail(R.drawable.personalhome_bg)
            .showImageOnLoading(R.drawable.personalhome_bg).cacheInMemory(true).cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(200))
            .bitmapConfig(Config.RGB_565).build();
    // 带圆角的
    static DisplayImageOptions photoRoundOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.default_img).showImageOnFail(R.drawable.default_img)
            .showImageOnLoading(R.drawable.default_img).cacheInMemory(true).cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(200))
            .displayer(new RoundedBitmapDisplayer(15)).bitmapConfig(Config.RGB_565).build();

    static DisplayImageOptions avatarOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.default_head).showImageOnFail(R.drawable.default_head)
            .showStubImage(R.drawable.default_head).cacheInMemory(true).cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(200))
            .bitmapConfig(Config.RGB_565).build();

//	static DisplayImageOptions homeBgOption = new DisplayImageOptions.Builder()
//			.showImageForEmptyUri(R.drawable.home_bg).showImageOnFail(R.drawable.home_bg)
//			.showStubImage(R.drawable.home_bg).cacheInMemory(true).cacheOnDisk(true)
//			.imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(200))
//			.bitmapConfig(Config.RGB_565).build();

    static DisplayImageOptions netPhoto = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.default_img).showImageOnFail(R.drawable.default_img)
            .showImageOnLoading(R.drawable.default_img).cacheInMemory(true).cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(200))
            .bitmapConfig(Config.RGB_565).build();

    static DisplayImageOptions localPhoto = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(200))
            .bitmapConfig(Config.RGB_565).build();

    // 游戏圆角图标
    static DisplayImageOptions filletPhoto = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.default_img).showImageOnFail(R.drawable.default_img)
            .showImageOnLoading(R.drawable.default_img).cacheInMemory(true).cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(200))
            .displayer(new RoundedBitmapDisplayer(30)).bitmapConfig(Config.RGB_565).build();

    static DisplayImageOptions photoHeadOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.default_img).showImageOnFail(R.drawable.default_img)
            .showImageOnLoading(R.drawable.default_img).cacheInMemory(true).cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(200))
            .displayer(new RoundedBitmapDisplayer(150)).bitmapConfig(Config.RGB_565).build();

    static DisplayImageOptions yzmOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.default_img).showImageOnFail(R.drawable.default_img)
            .showImageOnLoading(R.drawable.default_img).cacheInMemory(false).cacheOnDisk(false)
            .imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(200))
            .displayer(new RoundedBitmapDisplayer(150)).bitmapConfig(Config.RGB_565).build();

    /**
     * 加载本地图片
     *
     * @param context
     * @param resId
     * @param view
     */
    public static void loadPhoto(Context context, int resId, ImageView view) {
        String uri = DRAWABLE + resId;
        ImageLoader.getInstance().displayImage(uri, view, photoOption);
    }

    public static void loadNoDefaultPhoto(Context context, String url, ImageLoadingListener listener) {
        try {
            ImageLoader.getInstance().loadImage(url, localPhoto, listener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载本地图片没有默认图
     *
     * @param context
     * @param resId
     * @param view
     */
    public static void loadLocalPhoto(Context context, int resId, ImageView view) {
        String uri = DRAWABLE + resId;
        ImageLoader.getInstance().displayImage(uri, view, localPhoto);
    }

    /**
     * 加载本地图片
     *
     * @param context
     * @param file
     * @param view
     */
    public static void loadPhoto(Context context, File file, ImageView view) {
        String uri = FILE + file.getAbsolutePath();
        ImageLoader.getInstance().displayImage(uri, view, photoOption);

    }

    public static void loadHeadPhoto(Context context, File file, ImageView view) {
        try {
            String uri = FILE + file.getAbsolutePath();
            ImageLoader.getInstance().displayImage(uri, view, photoHeadOption);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载本地图片（带监听）
     *
     * @param context
     * @param file
     * @param view
     */
    public static void loadPhoto(Context context, File file, ImageView view, ImageLoadingListener listener) {
        try {
            String uri = FILE + file.getAbsolutePath();
            ImageLoader.getInstance().displayImage(uri, view, noCacheOption, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 加载网路图片
     *
     * @param context
     * @param url
     * @param view
     */
    public static void loadPhoto(Context context, String url, ImageView view) {
        try {
            ImageLoader.getInstance().displayImage(url, view, photoOption);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载网路图片
     *
     * @param context
     * @param url
     * @param view
     */
    public static void loadCommentHeadPhoto(Context context, String url, ImageView view) {
        try {
            ImageLoader.getInstance().displayImage(url, view, headphotoOption);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadYZM(Context context, String url, ImageView view) {
        ImageLoader.getInstance().displayImage(url, view, yzmOption);
    }

    public static void loadRoundPhoto(Context context, String url, ImageView view) {
        try {
            ImageLoader.getInstance().displayImage(url, view, photoRoundOption);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadHeadPhoto(Context context, String url, ImageView view) {
        try {
            ImageLoader.getInstance().displayImage(url, view, photoHeadOption);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 加载个人主页背景
     *
     * @param context
     * @param url
     * @param view
     */
    public static void loadPersonalHomePhoto(Context context, String url, ImageView view) {
        ImageLoader.getInstance().displayImage(url, view, personalHomephotoOption);
    }

    public static void loadGameRoundPhoto(Context context, String url, ImageView view) {
        try {
            ImageLoader.getInstance().displayImage(url, view, filletPhoto);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void loadPhoto(Context context, String url, ImageView view, ImageLoadingListener listener) {

        try {
            ImageLoader.getInstance().displayImage(url, view, photoOption);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void loadAvatar(Context context, String url, ImageView view) {
        try {
            if (url != null && !url.equals("")) {
                ImageLoader.getInstance().displayImage(url, view, avatarOption);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void loadAvatar(Context context, int resId, ImageView view) {

        try {
            String uri = DRAWABLE + resId;
            ImageLoader.getInstance().displayImage(uri, view, avatarOption);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//	public static void loadHomeBg(Context context, String url, ImageView view) {
//		ImageLoader.getInstance().displayImage(url, view, homeBgOption);
//	}

    public static void loadNetPhoto(Context context, String url, ImageView view) {
        try {
            ImageLoader.getInstance().displayImage(url, view, netPhoto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeCacheWithKey(String key) {
        ImageLoader.getInstance().getDiskCache().remove(key);
        ImageLoader.getInstance().getMemoryCache().remove(key);
    }

    /**
     * 如果失败显示指定资源id
     */

    public static void loadImageWithResId(String url, int resId, ImageView view) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(resId).showImageOnFail(resId)
                .showImageOnLoading(resId).cacheInMemory(true).cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(200))
                .bitmapConfig(Config.RGB_565).build();
        ImageLoader.getInstance().displayImage(url, view, options);
    }

    /**
     * 带监听的加载
     */
    public static void loadNetPhotoWithListener(String url, ImageView view, ImageLoadingListener listener) {
        try {
            ImageLoader.getInstance().displayImage(url, view, netPhoto, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void loadNetPhotoWithListener(String url, ImageLoadingListener listener){
        ImageLoader.getInstance().loadImage(url,photoOption,listener);
    }
    public static Bitmap getBitmap(String url){
        return ImageLoader.getInstance().loadImageSync(url,photoOption);
    }

}
