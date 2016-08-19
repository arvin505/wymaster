
package com.miqtech.master.client.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.utils.LogUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 继承自SwipeRefreshLayout,从而实现滑动到底部时上拉加载更多的功能.
 *
 * @author mrsimple
 */
public class RefreshLayout extends SwipeRefreshLayout implements
        OnScrollListener {

    /**
     * 滑动到最下面时的上拉操作
     */

    View errorView;

    private int mTouchSlop;
    /**
     * listview实例
     */
    private ListView mListView;

    private ScrollView mScrollView;

    private GridView mGridView;

    private AbsListView absListView;

    private boolean isListView;

    private boolean hasMore = true;

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    private OnLoadListener mOnLoadListener;

    /**
     * ListView的加载中footer
     */
    private View mListViewFooter;

    /**
     * 按下时的y坐标
     */
    private int mYDown;
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     */
    private int mLastY;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean isLoading = false;

    /**
     * @param context
     */
    public RefreshLayout(Context context) {
        this(context, null);
    }

    @SuppressLint("InflateParams")
    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mListViewFooter = LayoutInflater.from(context).inflate(
                R.layout.listview_footer, null, false);

        errorView = LayoutInflater.from(getContext()).inflate(R.layout.exception_page, null);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 初始化ListView对象
        if (mListView == null && mGridView == null) {
            getListView();
        }
    }

    /**
     * 获取ListView对象
     */
    private AbsListView getListView() {
        int childs = getChildCount();
        if (childs > 0) {
            View childView = getChildAt(0);
            if (childView instanceof AbsListView) {

            } else {
                childView = getChildAt(1);
            }

            if (childView instanceof ListView) {
                mListView = (ListView) childView;
                // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
                mListView.setOnScrollListener(this);
                isListView = true;
                LogUtil.d(VIEW_LOG_TAG, "### 找到listview");
                return mListView;
            } else if (childView instanceof GridView) {
                mGridView = (GridView) childView;
                mGridView.setOnScrollListener(this);
                LogUtil.d(VIEW_LOG_TAG, "### 找到Gridview");
                isListView = false;
                return mGridView;
            }
        }
        return null;
    }


    /*
     * (non-Javadoc)
     *
     * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                mYDown = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 移动
                mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                // 抬起
                if (canLoad()) {
                    loadData();
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     *
     * @return
     */
    private boolean canLoad() {
        //LogUtil.e("bottom", "isBottom == " + isBottom() + "   isloading == " + isLoading + "  is pullup == " + isPullUp());
        return isBottom() && !isLoading && isPullUp() && isFullForView();
    }


    private boolean isFullForView() {
        int count = mListView.getAdapter().getCount();
        if (count > 0) {
            View view = mListView.getChildAt(0);
            int viewHeight = view.getHeight();
            LogUtil.e("viewHieght", viewHeight + "");
            int listViewHeight = mListView.getHeight();
            float a = listViewHeight / viewHeight;
            if ((listViewHeight / viewHeight) < count) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    /**
     * 判断是否到了最底部
     */
    private boolean isBottom() {
        if (isListView) {
            if (mListView != null && mListView.getAdapter() != null) {
                return mListView.getLastVisiblePosition() == (mListView
                        .getAdapter().getCount() - 1);
            }
        } else {
            if (mGridView != null && mGridView.getAdapter() != null) {
                return mGridView.getLastVisiblePosition() == (mGridView
                        .getAdapter().getCount() - 1);
            }
        }
        return false;
    }

    /**
     * 是否是上拉操作
     *
     * @return
     */
    private boolean isPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        if (mOnLoadListener != null && isHasMore()) {
            // 设置状态
            setLoading(true);
            //
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mOnLoadListener.onLoad();
                }
            }, 1000);

            LogUtil.e("loading", mOnLoadListener + " lis state");
        }
    }

    /**
     * @param loading
     */
    public void setLoading(boolean loading) {
        isLoading = loading;
        LogUtil.e("loading", loading + " state");
        if (isLoading) {
            mListView.addFooterView(mListViewFooter);
        } else {
            if (mListView != null && mListViewFooter != null) {
                if (mListView.getAdapter() instanceof HeaderViewListAdapter) {
                    mListView.removeFooterView(mListViewFooter);
                    mYDown = 0;
                    mLastY = 0;
                }
            }
        }
    }

    /**
     * @param loadListener
     */
    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // 滚动时到了最底部也可以加载更多
        if (canLoad()) {
            loadData();
        }
    }

    /**
     * 设置刷新
     */
    public static void setRefreshing(SwipeRefreshLayout refreshLayout,
                                     boolean refreshing, boolean notify) {
        Class<? extends SwipeRefreshLayout> refreshLayoutClass = refreshLayout
                .getClass();
        if (refreshLayoutClass != null) {

            try {
                Method setRefreshing = refreshLayoutClass.getDeclaredMethod(
                        "setRefreshing", boolean.class, boolean.class);
                setRefreshing.setAccessible(true);
                setRefreshing.invoke(refreshLayout, refreshing, notify);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        super.setRefreshing(refreshing);
//        if(!refreshing){
//            if(isListView){
//                if(mListView.getChildCount()<=0){
//                    ((ViewGroup)mListView.getParent().getParent()).getChildAt(((ViewGroup)mListView.getParent().getParent()).getChildCount()-1).setVisibility(View.VISIBLE);
//                }else{
//                    ((ViewGroup)mListView.getParent().getParent()).getChildAt(((ViewGroup)mListView.getParent().getParent()).getChildCount()-1).setVisibility(View.VISIBLE);
//                }
//            }
//        }
    }

    /**
     * 加载更多的监听器
     *
     * @author mrsimple
     */
    public static interface OnLoadListener {
        public void onLoad();
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


}