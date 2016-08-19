package com.miqtech.master.client.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.antonyt.infiniteviewpager.InfinitePagerAdapter;
import com.antonyt.infiniteviewpager.InfiniteViewPager;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.ImagePagerAdapter;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ScrollController;

import java.util.ArrayList;
import java.util.List;

/**
 * auto scroll infinite ViewPager with a title
 * include an open source project: InfiniteViewPager
 * Created by kinneyYan on 2015/07/01.
 */
public class HeadLinesView<T> extends FrameLayout {
    private int scrollDelayTime = 3000;//自动滑动的间隔时间
    private boolean isAutoScroll = true;//是否自动滑动
    private boolean isScrolling = false;//是否正在自动滑动

    private List<T> list = new ArrayList<T>(); // 数据集
    private InfiniteViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private TextView mLable;
    private Handler mHandler;
    private LinearLayout indicator;

    /**
     * 初始化数据
     *
     * @param feedList
     */
    public void refreshData(List<T> feedList) {
        if (feedList == null || feedList.isEmpty()) {
            return;
        }
        list.clear();
        list.addAll(feedList);
        mPagerAdapter = new InfinitePagerAdapter(new ImagePagerAdapter<>(getContext(), list));
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setFocusable(true);
        mViewPager.setFocusableInTouchMode(true);
        mViewPager.requestFocus();
        initDots(list);
        ScrollController.addViewPager("headline", mViewPager);
        this.setVisibility(VISIBLE);
        LogUtil.d("ImagePagerAdapter","URL:::显示View");
    }

    /**
     * 开始自动滑动 used in onResume()
     */
    public void startAutoScroll() {
        if (list.isEmpty() || list.size() == 1) {//当只有一条banner时，禁止滑动
            return;
        }
        if (isAutoScroll && !isScrolling) {
            mHandler.postDelayed(autoScrollTask, scrollDelayTime);
            isScrolling = true;
        }
    }

    /**
     * 停止自动滑动 used in onPause()
     */
    public void stopAutoScroll() {
        if (null != mHandler) {
            mHandler.removeCallbacks(autoScrollTask);
            isScrolling = false;
        }
    }

    /**
     * 设置是否自动滑动
     *
     * @param isAutoScroll
     */
    public void setIsAutoScroll(boolean isAutoScroll) {
        this.isAutoScroll = isAutoScroll;
    }

    /**
     * 设置自动滑动的间隔时间
     *
     * @param time_scroll_delay
     */
    public void setScrollDelayTime(int time_scroll_delay) {
        this.scrollDelayTime = time_scroll_delay;
    }

    public HeadLinesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HeadLinesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HeadLinesView);
        int layoutId = array.getResourceId(R.styleable.HeadLinesView_layoutId, R.layout.view_headline);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(layoutId, this);
        findViews();

        mHandler = new Handler();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < indicator.getChildCount(); i++) {
                    if (i == (position % indicator.getChildCount())) {
                        indicator.getChildAt(i).setSelected(true);
                    } else {
                        indicator.getChildAt(i).setSelected(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        this.setVisibility(GONE);
    }

    private void findViews() {
        mViewPager = (InfiniteViewPager) findViewById(R.id.head_vp_img);
        // mLable = (TextView) findViewById(R.id.head_lable);
        indicator = (LinearLayout) findViewById(R.id.ll_indicator);
    }

    /**
     * 自动滑动的task
     */
    private Runnable autoScrollTask = new Runnable() {
        @Override
        public void run() {
            int currentItem = mViewPager.getSuperCurrentItem();
            currentItem++;
            if (Integer.MAX_VALUE == currentItem) {
                currentItem = 0;
            }
            mViewPager.setSuperCurrentItem(currentItem);
            mHandler.postDelayed(this, scrollDelayTime);
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                stopAutoScroll();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startAutoScroll();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 随着图片的移动显示对应的点
     *
     * @param
     */
    private void initDots(List list) {
        indicator.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            indicator.addView(initDot());
        }
        indicator.getChildAt(0).setSelected(true);
    }

    /**
     * 轮播图的点
     *
     * @return
     */
    private View initDot() {
        return LayoutInflater.from(getContext()).inflate(R.layout.headline_indecator, null);
    }
}
