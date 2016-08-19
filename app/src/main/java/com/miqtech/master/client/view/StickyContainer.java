package com.miqtech.master.client.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;

public abstract class StickyContainer<T extends View> extends LinearLayout implements View.OnTouchListener{
  
    private int TAB_COUNT = 6 ;

   
    private float[] Y_COORDINATE = new float[TAB_COUNT] ;

  
    private boolean mTabCarouselIsAnimating;

  
    private OnStickylListener mCarouselListener;

   
    private int mAllowedVerticalScrollLength = Integer.MIN_VALUE;

 
    private int mLastScrollPosition = Integer.MIN_VALUE;

    private float mScrollScaleFactor = 1.0f;

    private boolean mScrollToCurrentTab = false;


    public StickyContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Add the onTouchListener
        setOnTouchListener(this);
    }

    public abstract T getTabIndictor();
    
    public abstract int getCurrentTabItem();
    
    public abstract int getTabIndictorHeight();

	/**
     * {@inheritDoc}
     */
    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!mScrollToCurrentTab) {
            return;
        }
        mScrollToCurrentTab = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);


        if (mLastScrollPosition == x) {
            return;
        }

      
        final int scaledL = (int) (x * mScrollScaleFactor);
        final int oldScaledL = (int) (oldX * mScrollScaleFactor);
        if(mCarouselListener!=null){
        	mCarouselListener.onCarouselScrollChanged(scaledL, y, oldScaledL, oldY);
        }

        mLastScrollPosition = x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final boolean interceptTouch = super.onInterceptTouchEvent(ev);
        if (interceptTouch && mCarouselListener!=null) {
            mCarouselListener.onTouchDown();
        }
        return interceptTouch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	if(mCarouselListener!=null){
            		mCarouselListener.onTouchDown();
            	}
                return true;
            case MotionEvent.ACTION_UP:
            	if(mCarouselListener!=null){
            		mCarouselListener.onTouchUp();
            	}
                return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * @return True if the carousel is currently animating, false otherwise
     */
    public boolean isTabCarouselIsAnimating() {
        return mTabCarouselIsAnimating;
    }

    /**
     * Reset the carousel to the start position
     */
    public void reset() {
        scrollTo(0, 0);
        moveToYCoordinate(0, 0);
    }

    /**
     * Store this information as the last requested Y coordinate for the given
     * tabIndex.
     * 
     * @param tabIndex The tab index being stored
     * @param y The Y cooridinate to move to
     */
    public void storeYCoordinate(int tabIndex, float y) {
        Y_COORDINATE[tabIndex] = y;
    }

    /**
     * Restore the Y position of this view to the last manually requested value.
     * This can be done after the parent has been re-laid out again, where this
     * view's position could have been lost if the view laid outside its
     * parent's bounds.
     * 
     * @param duration The duration of the animation
     * @param tabIndex The index to restore
     */
    public void restoreYCoordinate(int duration, int tabIndex) {
        final float storedYCoordinate = getStoredYCoordinateForTab(tabIndex);
        final Interpolator interpolator = AnimationUtils.loadInterpolator(getContext(),
        		android.R.anim.accelerate_decelerate_interpolator);
        
        final ObjectAnimator animator = ObjectAnimator.ofFloat(this, "y", storedYCoordinate);
        animator.addListener(mTabCarouselAnimatorListener);
        animator.setInterpolator(interpolator);
        animator.setDuration(duration);
        animator.start();
    }

    /**
     * Request that the view move to the given Y coordinate. Also store the Y
     * coordinate as the last requested Y coordinate for the given tabIndex.
     * 
     * @param tabIndex The tab index being stored
     * @param y The Y cooridinate to move to
     */
    public void moveToYCoordinate(int tabIndex, float y) {
        storeYCoordinate(tabIndex, y);
        restoreYCoordinate(0, tabIndex);
    }

    /**
     * Set the given {@link OnStickylListener} to handle carousel events
     */
    public void setListener(OnStickylListener carouselListener) {
        mCarouselListener = carouselListener;
    }

    /**
     * Returns the stored Y coordinate of this view the last time the user was
     * on the selected tab given by tabIndex.
     * 
     * @param tabIndex The tab index use to return the Y value
     */
    public float getStoredYCoordinateForTab(int tabIndex) {
        return Y_COORDINATE[tabIndex];
    }

    /**
     * Returns the number of pixels that this view can be scrolled horizontally
     */
    public int getAllowedHorizontalScrollLength() {
        return 0;
    }

    /**
     * Returns the number of pixels that this view can be scrolled vertically
     * while still allowing the tab labels to still show
     */
    public int getAllowedVerticalScrollLength() {
    	mAllowedVerticalScrollLength = getMeasuredHeight()-getTabIndictorHeight() ;
        return mAllowedVerticalScrollLength;
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	static void doAfterLayout(final View view, final Runnable runnable) {
        final OnGlobalLayoutListener listener = new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
			@Override
            public void onGlobalLayout() {
                /* Layout pass done, unregister for further events */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                runnable.run();
            }
        };
        view.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    private final AnimatorListener mTabCarouselAnimatorListener = new AnimatorListener() {

      
        @Override
        public void onAnimationCancel(Animator animation) {
            mTabCarouselIsAnimating = false;
        }

      
        @Override
        public void onAnimationEnd(Animator animation) {
            mTabCarouselIsAnimating = false;
        }

        
        @Override
        public void onAnimationRepeat(Animator animation) {
            mTabCarouselIsAnimating = true;
        }

     
        @Override
        public void onAnimationStart(Animator animation) {
            mTabCarouselIsAnimating = true;
        }
    };

}
