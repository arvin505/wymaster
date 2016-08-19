/**
 * 
 */
package com.miqtech.master.client.view;

import android.widget.AbsListView;

/**
 * @date 2015-1-26 下午3:04:37
 */
public interface OnListScrollListener {
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount);
    public void onScrollStateChanged(AbsListView view, int scrollState);
}
