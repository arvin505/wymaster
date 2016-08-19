/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.miqtech.master.client.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.entity.UserImage;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.photoview.PhotoView;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.HackyViewPager;
import com.viewpagerindicator.PageIndicator;

import org.json.JSONObject;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class PersonalHomePhotoActivity extends BaseActivity {

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
    private static final String STATE_POSITION = "STATE_POSITION";

    private static final String IMAGES = "images";

    private static final String IMAGE_POSITION = "image_index";

    HackyViewPager pager;
    PageIndicator mIndicator;
    private boolean isMyShelf;
    private List<UserImage> images;

    private Context context;
    private TextView photo_number;
    private boolean isFromComment=false; //是否来自评论也图片

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pager);

        initImageLoader(this);
        context = this;

        Bundle bundle = getIntent().getExtras();
        images = (List<UserImage>) bundle.getSerializable(IMAGES);
        isMyShelf = bundle.getBoolean("isMyShelf", false);
        int pagerPosition = bundle.getInt(IMAGE_POSITION, 0);
        isFromComment =bundle.getBoolean("isFromComment",false);

        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.default_img)
                .showImageOnFail(R.drawable.default_img).resetViewBeforeLoading(true).cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        pager = (HackyViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ImagePagerAdapter(images, this));
        pager.setCurrentItem(pagerPosition);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                photo_number.setText((position + 1) + "/" + images.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        photo_number = (TextView) findViewById(R.id.photo_number);
        if(isFromComment){
            photo_number.setVisibility(View.GONE);
        }else {
            photo_number.setText((pagerPosition + 1) + "/" + images.size());
        }
        mIndicator.setViewPager(pager);
    }

    public void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, pager.getCurrentItem());
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private List<UserImage> images;
        private LayoutInflater inflater;
        private Context mContext;

        ImagePagerAdapter(List<UserImage> images, Context context) {
            this.images = images;
            this.mContext = context;
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public void finishUpdate(View container) {
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);

            PhotoView imageView = (PhotoView) imageLayout.findViewById(R.id.image);
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
            TextView tvDeletePhoto = (TextView) imageLayout.findViewById(R.id.tvDeletePhoto);
            TextView tvSavePhoto = (TextView) imageLayout.findViewById(R.id.tvSavePhoto);
            if(isFromComment){
                tvDeletePhoto.setVisibility(View.GONE);
                tvSavePhoto.setVisibility(View.GONE);
            }else {
                if (isMyShelf) {
                    tvDeletePhoto.setVisibility(View.VISIBLE);
                } else {
                    tvSavePhoto.setVisibility(View.VISIBLE);
                }
            }
            PhotoListener listener = new PhotoListener(position);
            tvDeletePhoto.setOnClickListener(listener);
            tvSavePhoto.setOnClickListener(listener);

            imageLoader.displayImage(HttpConstant.SERVICE_UPLOAD_AREA + images.get(position).getImg(), imageView,
                    options, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            spinner.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            String message = null;
                            switch (failReason.getType()) {
                                case IO_ERROR:
                                    message = "图片获取失败";
                                    break;
                                case DECODING_ERROR:
                                    message = "Image can't be decoded";
                                    break;
                                case NETWORK_DENIED:
                                    message = "Downloads are denied";
                                    break;
                                case OUT_OF_MEMORY:
                                    message = "Out Of Memory error";
                                    break;
                                case UNKNOWN:
                                    message = "Unknown error";
                                    break;
                            }
                            Toast.makeText(PersonalHomePhotoActivity.this, message, Toast.LENGTH_SHORT).show();

                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            spinner.setVisibility(View.GONE);
                        }
                    });

            ((ViewPager) view).addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View container) {
        }
    }

    private class PhotoListener implements OnClickListener {
        int position;

        PhotoListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvDeletePhoto:
                    deletePhoto(position);
                    break;
                case R.id.tvSavePhoto:
                    UserImage image = images.get(position);
                    ImageHandler imageHandler = new ImageHandler();
                    new ImageThread(image.getImg(), imageHandler).start();
                    break;

                default:
                    break;
            }
        }

    }

    public class ImageThread extends Thread {
        String url;
        ImageHandler handler;

        ImageThread(String url, ImageHandler handler) {
            this.url = url;
            this.handler = handler;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            Bitmap bitmap = getBitMBitmap(HttpConstant.SERVICE_UPLOAD_AREA + url);
            Message msg = handler.obtainMessage();
            msg.obj = bitmap;
            handler.sendMessage(msg);
        }
    }

    private class ImageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            Bitmap bitmap = (Bitmap) msg.obj;
            saveImageToGallery(context, bitmap);
            finish();
            showToast("保存成功");
        }
    }

    // public static Bitmap GetLocalOrNetBitmap(String url)
    // {
    // Bitmap bitmap = null;
    // InputStream in = null;
    // BufferedOutputStream out = null;
    // try
    // {
    // in = new BufferedInputStream(new URL(url).openStream(), 2*1024);
    // final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
    // out = new BufferedOutputStream(dataStream, 2*1024);
    // // copy(in, out);
    // out.flush();
    // byte[] data = dataStream.toByteArray();
    // bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
    // data = null;
    // return bitmap;
    // }
    // catch (IOException e)
    // {
    // e.printStackTrace();
    // return null;
    // }
    // }

    public static Bitmap getBitMBitmap(String urlpath) {
        Bitmap map = null;
        try {
            URL url = new URL(urlpath);
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            map = BitmapFactory.decodeStream(in);
            // TODO Auto-generated catch block
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private void deletePhoto(int position) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(this);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("id", images.get(position).getId() + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DELETE_PHOTO, params, HttpConstant.DELETE_PHOTO);
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));

    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        if (HttpConstant.DELETE_PHOTO.equals(method)) {
            showToast("删除成功");
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onError(String method, String errorInfo) {
        hideLoading();
        super.onError(method, errorInfo);
    }

}