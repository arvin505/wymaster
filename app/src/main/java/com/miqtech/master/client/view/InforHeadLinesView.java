package com.miqtech.master.client.view;

import android.content.Context;
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
import com.miqtech.master.client.adapter.ImagePagerInfoBannerAdapter;
import com.miqtech.master.client.entity.InforBanner;
import com.miqtech.master.client.utils.ScrollController;

import java.util.ArrayList;
import java.util.List;

/**
 * 资讯的轮播图
 * Created by zhaosentao on 2015/11/25.
 */
public class InforHeadLinesView<T> extends FrameLayout {

    private int scrollDelayTime = 3000;//自动滑动的间隔时间
    private boolean isAutoScroll = true;//是否自动滑动
    private boolean isScrolling = false;//是否正在自动滑动

    private List<InforBanner> list = new ArrayList<InforBanner>(); // 数据集
    private InfiniteViewPager mViewPager;//轮播的图片
    private PagerAdapter mPagerAdapter;
    private Handler mHandler;
    private LinearLayout indicator;//轮播添加的点
    private TextView tv_extend;//推广2字
    private TextView tv_banner_intro;//轮播介绍

    /**
     * 初始化数据
     *
     * @param feedList
     */
    public void refreshData(List<InforBanner> feedList) {
        if (feedList == null || feedList.isEmpty()) {
            return;
        }
        list.clear();
        list.addAll(feedList);
        mPagerAdapter = new InfinitePagerAdapter(new ImagePagerInfoBannerAdapter<>(getContext(), list));
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setFocusable(true);
        mViewPager.setFocusableInTouchMode(true);
        mViewPager.requestFocus();
        initDots(list);
        ScrollController.addViewPager((String) mViewPager.getTag(), mViewPager);
        this.setVisibility(VISIBLE);
    }

    /**
     * 开始自动滑动 used in onResume()
     */
    public void startAutoScroll() {
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

    private String mViewPagerName;

    public void setViewPagerName(String name) {
        this.mViewPagerName = name;
        mViewPager.setTag(mViewPagerName);
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

    public InforHeadLinesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InforHeadLinesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_info_headline, this);
        findViews();

        mHandler = new Handler();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < indicator.getChildCount(); i++) {
                    if (i == (position % indicator.getChildCount())) {//类型:1图文 2专题 3图集
                        indicator.getChildAt(i).setSelected(true);
                        tv_banner_intro.setText(list.get(i).getTitle());
                        if (list.get(i).getType() == 1) {
                            tv_extend.setText(getContext().getResources().getString(R.string.information));
                        } else if (list.get(i).getType() == 2) {
                            tv_extend.setText(getContext().getResources().getString(R.string.topic));
                        } else if (list.get(i).getType() == 3) {
                            tv_extend.setText(getContext().getResources().getString(R.string.atlas));
                        }
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
        indicator = (LinearLayout) findViewById(R.id.ll_indicator);
        tv_extend = (TextView) findViewById(R.id.banner_extend_infor_fragment);
        tv_banner_intro = (TextView) findViewById(R.id.banner_extend_intro_infor_fragment);
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
     * 随着图片的移动添加显示对应的点
     *
     * @param
     */
    private void initDots(List<InforBanner> list) {
        indicator.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            indicator.addView(initDot());
        }
        indicator.getChildAt(0).setSelected(true);
        if (list.size() > 0) {
            if (list.get(0).getType() == 1) {
                tv_extend.setText(getContext().getResources().getString(R.string.information));
            } else if (list.get(0).getType() == 2) {
                tv_extend.setText(getContext().getResources().getString(R.string.topic));
            } else if (list.get(0).getType() == 3) {
                tv_extend.setText(getContext().getResources().getString(R.string.atlas));
            }
        }
        if (list.size() > 0) {
            tv_banner_intro.setText(list.get(0).getTitle());
        }
    }

    /**
     * 轮播图的点
     *
     * @return
     */
    private View initDot() {
        return LayoutInflater.from(getContext()).inflate(R.layout.headline_indecator, null);
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
