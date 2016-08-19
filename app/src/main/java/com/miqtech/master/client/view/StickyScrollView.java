package com.miqtech.master.client.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;


public class StickyScrollView extends ScrollView {
    // Edge-effects don't mix well with the translucent action bar in Android 2.X
    private boolean mDisableEdgeEffects = true;

   
    public interface OnScrollChangedListener {
        void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt);
        void onScrollIdle();
    }

    private OnScrollChangedListener mOnScrollChangedListener;
    private boolean mScrollChangedSinceLastIdle;

    public StickyScrollView(Context context) {
        super(context);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
    }

    public StickyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mScrollChangedSinceLastIdle = true;
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mOnScrollChangedListener = listener;
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        if (mDisableEdgeEffects && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return 0.0f;
        }
        return super.getTopFadingEdgeStrength();
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        if (mDisableEdgeEffects && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return 0.0f;
        }
        return super.getBottomFadingEdgeStrength();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean b = super.onTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (mOnScrollChangedListener != null && mScrollChangedSinceLastIdle) {
                mScrollChangedSinceLastIdle = false;
                mOnScrollChangedListener.onScrollIdle();
            }
        }
        return b;
    }
}
