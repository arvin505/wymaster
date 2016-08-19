package com.miqtech.master.client.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import com.miqtech.master.client.utils.LogUtil;

public class MyListViewForInfo extends ListView {

    public MyListViewForInfo(Context context) {
        super(context);
    }

    public MyListViewForInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListViewForInfo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
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
                if (xDistance > yDistance) {
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtil.e("ondraw", "ondraw");
    }
}
