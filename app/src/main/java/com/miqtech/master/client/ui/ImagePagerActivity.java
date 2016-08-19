/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.miqtech.master.client.ui;


import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.GalleyAdapter;
import com.miqtech.master.client.http.HttpConstant;
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

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImagePagerActivity extends Activity {

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
    private static final String STATE_POSITION = "STATE_POSITION";

    private static final String IMAGES = "images";

    private static final String IMAGE_POSITION = "image_index";

    private static final String IS_HIDE_GALLERY = "isHideGallery";

    private Gallery gallery;

    HackyViewPager pager;
    PageIndicator mIndicator;
    private TextView photo_number;
    private int totalNum;
    private int current;
    private Boolean isFirst = true;
    private int isHideGallery;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pager);

        initImageLoader(this);

        Bundle bundle = getIntent().getExtras();
        List<Map<String, String>> images = (List<Map<String, String>>) bundle.getSerializable(IMAGES);
        totalNum = images.size();
        int pagerPosition = bundle.getInt(IMAGE_POSITION, 0);

        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.default_img)
                .showImageOnFail(R.drawable.default_img).resetViewBeforeLoading(true).cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        photo_number = (TextView) findViewById(R.id.photo_number);
        gallery = (Gallery) findViewById(R.id.galley);

        pager = (HackyViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ImagePagerAdapter(images, this));
        //pager.setCurrentItem(pagerPosition);

        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(pager);
        photo_number.setText(1 + "/" + totalNum);

        GalleyAdapter adapter = new GalleyAdapter(this, images);
        gallery.setAdapter(adapter);

        showNUmAndClick();
        pager.setCurrentItem(pagerPosition);

        isHideGallery = getIntent().getIntExtra(IS_HIDE_GALLERY, -1);
        if (isHideGallery == 1) {//当为1时，隐藏底部的数字和gallery
            photo_number.setVisibility(View.GONE);
            gallery.setVisibility(View.GONE);
        }

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

        private List<Map<String, String>> images;
        private LayoutInflater inflater;
        private Context mContext;

        ImagePagerAdapter(List<Map<String, String>> images, Context context) {
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

            imageLoader.displayImage(HttpConstant.SERVICE_UPLOAD_AREA + images.get(position).get("url"), imageView,
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
                            Toast.makeText(ImagePagerActivity.this, message, Toast.LENGTH_SHORT).show();

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

    private void showNUmAndClick() {
        gallery.setUnselectedAlpha(0.3f);
        pager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                current = arg0 + 1;
                photo_number.setText(current + "/" + totalNum);
                gallery.setSelection(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

        gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                pager.setCurrentItem(arg2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }
}