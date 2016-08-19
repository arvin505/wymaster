package com.miqtech.master.client.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

/**
 * Created by Administrator on 2015/12/10.
 */
public class MyGallery extends Gallery {

    public MyGallery(Context context) {
        super(context);
    }

    public MyGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGallery(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
