/*
 * Copyright (C) 2013 Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.miqtech.master.client.view;


import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;


public class StickyScrollManager implements OnScrollListener {
    /**
     * The carousel header
     */
    private final StickyContainer<?> mCarousel;

    /**
     * The position of the {@link ViewPager} to scroll to
     */
    private final int mPageIndex;

    private OnListScrollListener mOnListScrollListener=null;
    public StickyScrollManager(StickyContainer<?> carouselHeader, int pageIndex) {
        // Initialize the header
        mCarousel = carouselHeader;
        // Match the pager positions
        mPageIndex = pageIndex;
    }

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
    	if(mOnListScrollListener!=null){
    		mOnListScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    	}
    	
        // Don't move the carousel if: 1) It is already being animated
        if (mCarousel == null || mCarousel.isTabCarouselIsAnimating()) {
            return;
        }

        // If the FIRST item is not visible on the screen, then the carousel
        // must be pinned
        // at the top of the screen.
        int visibleItem = firstVisibleItem ;
        if(visibleItem<0){
        	return ;
        }
        if (visibleItem != 0 ) {
            mCarousel.moveToYCoordinate(mPageIndex, -mCarousel.getAllowedVerticalScrollLength());
            return;
        }

//        visibleItem =firstVisibleItem-mHeaderCount ;
        final View topView = view.getChildAt(visibleItem);
        if (topView == null) {
            return;
        }

        final float y = view.getChildAt(visibleItem).getTop();
        final float amtToScroll = Math.max(y, -mCarousel.getAllowedVerticalScrollLength());
        mCarousel.moveToYCoordinate(mPageIndex, amtToScroll);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(mOnListScrollListener!=null){
        	mOnListScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

	/**
	 * @return the mOnListScrollListener
	 */
	public OnListScrollListener getmOnListScrollListener() {
		return mOnListScrollListener;
	}

	/**
	 * @param mOnListScrollListener the mOnListScrollListener to set
	 */
	public void setmOnListScrollListener(OnListScrollListener mOnListScrollListener) {
		this.mOnListScrollListener = mOnListScrollListener;
	}
    
}
