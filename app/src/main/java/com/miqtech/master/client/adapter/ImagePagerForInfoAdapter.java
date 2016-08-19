package com.miqtech.master.client.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.miqtech.master.client.R;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.Utils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.photoview.PhotoView;

/**
 * 资讯图集浏览 的 adapter
 * Created by zhaosentao on 2015/12/1.
 */
public class ImagePagerForInfoAdapter extends PagerAdapter {

    private String[] _Images;
    private Context mContext;
    DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public ImagePagerForInfoAdapter(String[] _Images, Context context, DisplayImageOptions options) {
        this._Images = _Images;
        this.mContext = context;
        this.options = options;
        initImageLoader(context);
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
        return _Images.length;
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = LayoutInflater.from(mContext).inflate(R.layout.item_pager_image, view, false);

        PhotoView imageView = (PhotoView) imageLayout.findViewById(R.id.image);
        final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
        //TODO:图片
        imageLoader.displayImage(HttpConstant.SERVICE_UPLOAD_AREA + _Images[position], imageView,
//        imageLoader.displayImage(HttpConstant.SERVICE_UPLOAD_AREA + Utils.transformUTF8(_Images[position]), imageView,
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
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
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

    public void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}
