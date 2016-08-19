package com.miqtech.master.client.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Created by Administrator on 2015/11/24.
 */
public class WYSwipeRefreshLayout extends SwipeRefreshLayout {
    public WYSwipeRefreshLayout(Context context) {
        super(context);
    }

    public WYSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;
                if(xDistance > yDistance){
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
