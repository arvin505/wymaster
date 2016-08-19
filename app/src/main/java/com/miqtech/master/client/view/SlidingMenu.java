package com.miqtech.master.client.view;

import android.content.Context;
import android.content.res.TypedArray;

import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import android.widget.LinearLayout;


import com.miqtech.master.client.R;

import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ScrollController;
import com.miqtech.master.client.utils.Utils;
import com.nineoldandroids.view.ViewHelper;


public class SlidingMenu extends HorizontalScrollView {
    /**
     * 屏幕宽度
     */
    private int mScreenWidth;
    /**
     * dp
     */
    private int mMenuRightPadding;
    /**
     * 菜单的宽度
     */
    private int mMenuWidth;
    private int mHalfMenuWidth;

    private boolean isOpen;

    private boolean once;

    private ViewGroup mMenu;
    private ViewGroup mContent;

    private float xDistance;
    private float yDistance;

    private float xLast;
    private float yLast;

    int startX = 0;

    Context slidingMenuContext;

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.slidingMenuContext = context;
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.slidingMenuContext = context;
        mScreenWidth = Utils.getScreenWidth(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.SlidingMenu, defStyle, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.SlidingMenu_rightPadding:
                    // 默认50
                    mMenuRightPadding = a.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP, 50f,
                                    getResources().getDisplayMetrics()));// 默认为10DP
                    break;
            }
        }
        a.recycle();
    }

    public SlidingMenu(Context context) {
        this(context, null, 0);
        this.slidingMenuContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * 显示的设置一个宽度
         */
        if (!once) {
            LinearLayout wrapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) wrapper.getChildAt(0);
            mContent = (ViewGroup) wrapper.getChildAt(1);

            mMenuWidth = mScreenWidth - mMenuRightPadding;
            mHalfMenuWidth = mMenuWidth / 2;
            mMenu.getLayoutParams().width = mMenuWidth;
            mContent.getLayoutParams().width = mScreenWidth;

        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            // 将菜单隐藏
            this.scrollTo(mMenuWidth, 0);
            once = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //return false;
        try {
            int action = ev.getAction();
            switch (action) {
                // Up时，进行判断，如果显示区域大于菜单宽度一半则完全显示，否则隐藏
                case MotionEvent.ACTION_UP:
                    int scrollX = getScrollX();
                    if (scrollX > mHalfMenuWidth) {
                        this.smoothScrollTo(mMenuWidth, 0);
                        LogUtil.e("TAG", "--------onTouchEvent-----close menu-------------");
                        isOpen = false;
                    } else {
                        this.smoothScrollTo(-1, 0);
                        LogUtil.e("TAG", "--------onTouchEvent-----open menu----------");
                        isOpen = true;
                        myisAutoPlay.stopPlay();
                    }

                    return true;
                case MotionEvent.ACTION_DOWN:
                    if (isOpen() && ev.getRawX() > mMenuWidth) {
                        closeMenu();
                        return false;
                    }
            }
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (!isOpen() && ScrollController.shouldIntercept(ev)) {
            return false;
        } else {
            if (isOpen() && ev.getRawX() > mMenuWidth) {
                return true;
            }
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                startX = (int) ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;

                if (xDistance < yDistance) {
                    return false;
                }
                if (startX > Utils.dp2px(50)) {   //左边50的dp为滑动区域
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isOpen) {
                    return false;
                }
                break;
        }
        //return false;
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 打开菜单
     */
    public void openMenu() {
        if (isOpen)
            return;
        LogUtil.e("TAG", "-------------open menu-------------");
        this.smoothScrollTo(-1, 0);
        isOpen = true;
        myisAutoPlay.stopPlay();
    }

    private boolean scrolling = false;

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        if (isOpen) {
            this.smoothScrollTo(mMenuWidth, 0);
            LogUtil.e("TAG", "-------------close menu-------------");
            isOpen = false;
            myisAutoPlay.startPlay();
        }
    }

    /**
     * 切换菜单状态
     */
    public void toggle() {
        if (isOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float scale = l * 1.0f / mMenuWidth;
        float leftScale = 1 - 0.3f * scale;
        float rightScale = 0.8f + scale * 0.2f;

        ViewHelper.setScaleX(mMenu, leftScale);
        ViewHelper.setScaleY(mMenu, leftScale);
        ViewHelper.setAlpha(mMenu, 0.3f + 0.7f * (1 - scale));
        ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.7f);

        ViewHelper.setPivotX(mContent, 0);
        ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
        ViewHelper.setScaleX(mContent, rightScale);
        ViewHelper.setScaleY(mContent, rightScale);

        if (l == 0 || l == mMenuWidth) {
            scrolling = false;
        } else {
            scrolling = true;
        }

    }

    public boolean isOpen() {
        return isOpen;
    }

    public interface isAutoPlay {
        /**
         * 侧边栏关闭了   可以播放轮播图
         */
        void startPlay();

        /**
         * 侧边栏打开看，关闭轮播图
         */
        void stopPlay();
    }

    public isAutoPlay myisAutoPlay;

    public void setOnIsAutoPlay(isAutoPlay myisAutoPlay) {
        this.myisAutoPlay = myisAutoPlay;
    }

    @Override
    public void scrollTo(int x, int y) {
        if (mCurrentItem != null && mCurrentItem.getCurrentItem() == 2 && x == 0) {
            return;
        }
        super.scrollTo(x, y);
    }

    private CurrentItem mCurrentItem;

    public void setCurrentItem(CurrentItem currentitem) {
        this.mCurrentItem = currentitem;
    }

    public interface CurrentItem {
        int getCurrentItem();
    }
}
