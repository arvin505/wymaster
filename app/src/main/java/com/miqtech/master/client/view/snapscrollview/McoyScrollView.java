package com.miqtech.master.client.view.snapscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.miqtech.master.client.entity.Reward;
import com.miqtech.master.client.utils.LogUtil;

public class McoyScrollView extends ScrollView {
    // 滑动距离及坐标
    private float yLast;

    public McoyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Reward reward = new Reward();

    public void setReward(Reward reward) {
        this.reward = reward;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e("mcoy", "McoyScrollView--onInterceptTouchEvent");
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                yLast = ev.getY();
                if (reward.isShowListView()) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float curY = ev.getY();
                if (curY > yLast) {//向下滑动,当scrollView没到顶部时拦截事件，当到顶部时不拦截时间，将事件传递给cardView
                    if (getScrollY() != 0) {
                        return true;
                    } else {
                        return false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//		boolean touchEvent = super.onTouchEvent(ev);
//		Log.e("mcoy", "McoyScrollView--onTouchEvent return " + touchEvent);
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (onScrollListener != null) {
            onScrollListener.onScroll(x, y, oldx, oldy);
        }

        if (getScrollY() + getHeight() >= computeVerticalScrollRange()) {
            isBottom = true;
        } else {
            isBottom = false;
        }
    }

    public boolean isBottom = false;

    private OnJDScrollListener onScrollListener;

    public OnJDScrollListener getOnScrollListener() {
        return onScrollListener;
    }

    public void setOnJDScrollListener(OnJDScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public interface OnJDScrollListener {
        void onScroll(int x, int y, int oldx, int oldy);
    }

}
