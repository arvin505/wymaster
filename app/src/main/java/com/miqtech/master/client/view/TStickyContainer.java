package com.miqtech.master.client.view;


import com.miqtech.master.client.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


public class TStickyContainer extends StickyContainer{

	private View mTab;
	public TStickyContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/**
     * {@inheritDoc}
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    	if(mTab==null){
    		mTab = findViewById(R.id.indicator);
    	}
    	if(mTab==null){
    		throw new RuntimeException("CarouselContainer must has a child of TabIndicator class whose id is R.id.indicator");
    	}
    }
    

	@Override
	public int getCurrentTabItem() {
		return 0;
	}

	@Override
	public View getTabIndictor() {
		// TODO Auto-generated method stub
		return mTab;
	}

	@Override
	public int getTabIndictorHeight() {
		// TODO Auto-generated method stub
		return mTab.getMeasuredHeight();
	}

	
}
