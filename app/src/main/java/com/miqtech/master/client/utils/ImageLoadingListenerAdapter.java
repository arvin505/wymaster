package com.miqtech.master.client.utils;

import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by Administrator on 2016/3/11.
 */
public class ImageLoadingListenerAdapter implements ImageLoadingListener {
    @Override
    public void onLoadingStarted(String s, View view) {

    }

    @Override
    public void onLoadingFailed(String s, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap) {

    }

    @Override
    public void onLoadingCancelled(String s, View view) {

    }
}
