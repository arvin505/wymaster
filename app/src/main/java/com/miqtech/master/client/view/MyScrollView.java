package com.miqtech.master.client.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * @blog http://blog.csdn.net/xiaanming
 * 
 * 
 */
public class MyScrollView extends ScrollView {
	private MyScrollListener msl;
	private float xDistance, yDistance, xLast, yLast;

	public MyScrollView(Context context) {
		super(context);
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

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

	public interface MyScrollListener {
		void move(int x, int y, int oldx, int oldy);

	}

	public void setOnMyScrollListener(MyScrollListener msl) {
		this.msl = msl;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		// TODO Auto-generated method stub
		super.onScrollChanged(x, y, oldx, oldy);
		if (msl != null) {
			msl.move(x, y, oldx, oldy);
		}
//		if (listener != null) {
//			listener.scrollChanged(this, y, oldy);
//		}
	}
}
