package com.miqtech.master.client.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

import com.miqtech.master.client.view.pull2refresh.Pullable;

public class PullableScrollView extends ScrollView implements Pullable{
	private MyScrollListener msl;
	public PullableScrollView(Context context)
	{
		super(context);
	}

	public PullableScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullableScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown()
	{
		Log.i("PullableScrollView","canPullDown"+getScrollY());
		if (getScrollY() == 0)
			return true;
		else
			return false;
	}

	@Override
	public boolean canPullUp()
	{
		Log.i("PullableScrollView","canPullDown"+getScrollY()+":::"+getChildAt(0).getHeight()+"::"+getMeasuredHeight()+":::"+(getChildAt(0).getHeight() - getMeasuredHeight())
		+getHeight());
		if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()))
			return true;
		else
			return false;
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
		Log.i("PullableScrollView","x::"+x+":::"+y+"::"+y+":::"+oldx+"::"+oldy);
		if (msl != null) {
			msl.move(x, y, oldx, oldy);
		}
//		if (listener != null) {
//			listener.scrollChanged(this, y, oldy);
//		}
	}
}
