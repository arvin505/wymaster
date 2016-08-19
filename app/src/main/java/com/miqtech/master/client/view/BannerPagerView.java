package com.miqtech.master.client.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author zhangp
 */
public class BannerPagerView extends ViewPager {


    private BannerPagerView viewpager;
    private Timer mCycleTimer;
    private TimerTask mCycleTask;

    private Long mydelay = 3000l;

    public BannerPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.viewpager = this;
    }

    public BannerPagerView(Context context) {
        super(context);
        this.viewpager = this;
    }

    /**
     * 处理
     */
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        switch (arg0.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startAutoCycle();
                break;
        }
        return super.onTouchEvent(arg0);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        detach();
    }

    public void detach() {
        releaseTimer();
        if (mCycleTask != null) {
            mCycleTask = null;
        }
        if (mCycleTimer != null) {
            mCycleTimer = null;
        }
        mh.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        switch (arg0.getAction()) {
            case MotionEvent.ACTION_DOWN:
                releaseTimer();
                break;
            case MotionEvent.ACTION_CANCEL:
                startAutoCycle();
                break;
        }
        return super.onInterceptTouchEvent(arg0);
    }


    private boolean shouldCycle = true;

    public void stopCycle(){
        shouldCycle = false;
        releaseTimer();
    }

    private void releaseTimer() {
        if (mCycleTimer != null) {
            mCycleTimer.cancel();
        }
        if (mCycleTask != null) {
            mCycleTask.cancel();
        }
    }

    public void startAutoCycle() {
        if (!shouldCycle){
            return;
        }
        if (mCycleTimer != null)
            mCycleTimer.cancel();
        if (mCycleTask != null)
            mCycleTask.cancel();
        mCycleTimer = new Timer();
        mCycleTask = new TimerTask() {
            @Override
            public void run() {
                mh.sendEmptyMessage(0);
            }
        };
        mCycleTimer.schedule(mCycleTask, mydelay, mydelay);
    }

    Handler mh = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            moveNextPosition(true);
        }
    };

    /**
     * move to next banner.
     */
    public void moveNextPosition(boolean smooth) {
        if (viewpager != null) {
            if (viewpager.getCurrentItem() == viewpager.getAdapter().getCount() - 1) {
                viewpager.setCurrentItem(0);
            } else {
                viewpager
                        .setCurrentItem(viewpager.getCurrentItem() + 1, smooth);
            }
        }
    }

    private boolean intercept = true;
    private float mLastMotionX;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        float x = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //requestDisallowInterceptTouchEvent(true);
                intercept = true;
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                if (intercept) {
                    if (x - mLastMotionX > 5 && getCurrentItem() == 0) {
                        intercept = false;
                        requestDisallowInterceptTouchEvent(false);
                    }
                    if (x - mLastMotionX < -5 && getCurrentItem() == getAdapter().getCount() - 1) {
                        intercept = false;
                        requestDisallowInterceptTouchEvent(false);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
