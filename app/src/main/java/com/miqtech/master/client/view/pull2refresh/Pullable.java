package com.miqtech.master.client.view.pull2refresh;

public interface Pullable {

	/**
	 * 判断是否可以下拉，如果不需要下拉功能可以直接return false
	 * 
	 * @return true如果可以下拉否则返回false
	 */
	boolean canPullDown();

	/**
	 * 判断是否可以上拉，如果不需要上拉功能可以直接return false
	 * 
	 * @return true如果可以上拉否则返回false
	 */
	boolean canPullUp();
	/**
	 * 判断是否有数据，如果没有数据显示   特定页面
	 * 
	 */
}
