package com.miqtech.master.client.utils;

import com.miqtech.master.client.application.WangYuApplication;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

/**
 * Utility class to create a split activity animation
 *
 * @author Udi Cohen (@udinic)
 */
@SuppressLint("NewApi") public class ActivitySplitAnimationUtil {

    public static Bitmap mBitmap = null ;
    private static int[] mLoc1;
    private static int[] mLoc2;
    private static ImageView mTopImage;
    private static ImageView mBottomImage;
    private static AnimatorSet mSetAnim;
    static int[] locaXY = new int[2];  
    private static int pingWidth;
    private static int pingHeigth;
    private static View custView;
    /**
     * Start a new Activity with a Split animation
     *
     * @param currActivity The current Activity
     * @param intent       The Intent needed tot start the new Activity
     * @param splitYCoord  The Y coordinate where we want to split the Activity on the animation. -1 will split the Activity equally
     */
    public static void startActivity(Activity currActivity, int splitYCoord,View view) {

        // Preparing the bitmaps that we need to show
    	custView = view;
        prepare(currActivity, splitYCoord,view);
        prepareAnimation(currActivity);
        animate(currActivity,200);
    }

    /**
     * Start a new Activity with a Split animation right in the middle of the Activity
     *
     * @param currActivity The current Activity
     * @param intent       The Intent needed tot start the new Activity
     */
    public static void startAnimation(Activity currActivity,View view) {
        startActivity(currActivity, -1,view);
    }

    /**
     * Preparing the graphics on the destination Activity.
     * Should be called on the destination activity on Activity#onCreate() BEFORE setContentView()
     *
     * @param destActivity the destination Activity
     */
    public static void prepareAnimation(final Activity destActivity) {
        mTopImage = createImageView(destActivity, mBitmap, mLoc1);
        mBottomImage = createImageView(destActivity, mBitmap, mLoc2);
        custView.setDrawingCacheEnabled(false);
        handler.sendEmptyMessageDelayed(0, 100);
    }
	static Handler handler =new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			custView.setVisibility(View.GONE);
		}
	};

    /**
     * Start the animation the reveals the destination Activity
     * Should be called on the destination activity on Activity#onCreate() AFTER setContentView()
     *
     * @param destActivity the destination Activity
     * @param duration The duration of the animation
     * @param interpolator The interpulator to use for the animation. null for no interpulation.
     */
    public static void animate(final Activity destActivity, final int duration) {
    	new Handler().post(new Runnable() {
            @SuppressLint("NewApi") @Override
            public void run() {
                mSetAnim = new AnimatorSet();
                mTopImage.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                mBottomImage.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                mSetAnim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        clean(destActivity);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        clean(destActivity);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                // Animating the 2 parts away from each other
                Animator anim1 = ObjectAnimator.ofFloat(mTopImage, "translationX", mTopImage.getWidth() * -1);
                Animator anim2 = ObjectAnimator.ofFloat(mBottomImage, "translationX", mBottomImage.getWidth());
                AccelerateInterpolator  interpolator =new AccelerateInterpolator();
                if (interpolator != null) {
                    anim1.setInterpolator(interpolator);
                    anim2.setInterpolator(interpolator);
                }

                mSetAnim.setDuration(duration);
                mSetAnim.playTogether(anim1, anim2);
                mSetAnim.start();
            }
        });
    }

    /**
     * Cancel an in progress animation
     */
    public static void cancel() {
        if (mSetAnim != null)
            mSetAnim.cancel();
    }

    /**
     * Clean stuff
     *
     * @param activity The Activity where the animation is occurring
     */
    private static void clean(Activity activity) {
        if (mTopImage != null) {
            mTopImage.setLayerType(View.LAYER_TYPE_NONE, null);
            try {
                // If we use the regular removeView() we'll get a small UI glitch
                activity.getWindowManager().removeViewImmediate(mBottomImage);
            } catch (Exception ignored) {
            }
        }
        if (mBottomImage != null) {
            mBottomImage.setLayerType(View.LAYER_TYPE_NONE, null);
            try {
                activity.getWindowManager().removeViewImmediate(mTopImage);
            } catch (Exception ignored) {
            }
        }

        mBitmap = null;
    }

    /**
     * Preparing the graphics for the animation
     *
     * @param currActivity the current Activity from where we start the new one
     * @param splitYCoord  The Y coordinate where we want to split the activity. -1 will split the activity equally
     */
    private static void prepare(Activity currActivity, int splitYCoord,View view) {

        // Get the content of the activity and put in a bitmap
//        View root = currActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        View root = view;
        root.setDrawingCacheEnabled(true);
        Bitmap bitmap = root.getDrawingCache();
        mBitmap = Bitmap.createBitmap(bitmap);
        root.setDrawingCacheEnabled(false);
        pingWidth = WangYuApplication.WIDTH;
        pingHeigth = WangYuApplication.HEIGHT;
        view.getLocationOnScreen(locaXY);  
        // If the split Y coordinate is -1 - We'll split the activity equally
        splitYCoord = (splitYCoord != -1 ? splitYCoord : (mBitmap.getWidth()) / 2);

        if (splitYCoord > mBitmap.getWidth())
            throw new IllegalArgumentException("Split Y coordinate [" + splitYCoord + "] exceeds the activity's height [" + mBitmap.getWidth() + "]");

        // Set the location to put the 2 bitmaps on the destination activity
        mLoc1 = new int[]{0, splitYCoord, root.getLeft()};
        mLoc2 = new int[]{splitYCoord, mBitmap.getWidth(), root.getLeft()};
    }

    /**
     * Creating the an image, containing one part of the animation on the destination activity
     *
     * @param destActivity The destination activity
     * @param bmp          The Bitmap of the part we want to add to the destination activity
     * @param loc          The location this part should be on the screen
     * @return
     */
    public static ImageView createImageView(Activity destActivity, Bitmap bmp, int loc[]) {
    	MyImageView imageView = new MyImageView(destActivity);
        imageView.setImageBitmap(bmp);
        imageView.setImageOffsets(bmp.getHeight(), loc[0], loc[1]);                     
        
        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        
        windowParams.x = Math.abs(locaXY[0]+bmp.getWidth()/4)-pingWidth/2+loc[0]+1;
        windowParams.y = Math.abs(locaXY[1]+bmp.getHeight()/2)-pingHeigth/2+1;
        windowParams.height = bmp.getHeight(); 
        windowParams.width = loc[1] - loc[0];
        windowParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
        destActivity.getWindowManager().addView(imageView, windowParams);
        return imageView;
    }
    
    /**
     * MyImageView
     * Extended ImageView that draws just part of an image, base on start/end position  
     */
    private static class MyImageView extends ImageView
    {
    	private Rect mSrcRect;
    	private Rect mDstRect;
    	private Paint mPaint;    	
    	
		public MyImageView(Context context) 
		{
			super(context);
			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		}
		
		/**
	     * Setting the bitmap offests to control the visible area
	     *
	     * @param width		   The bitmap image
	     * @param bmp          The start Y position
	     * @param loc          The end Y position
	     * @return
	     */
		public void setImageOffsets(int heigth, int startX, int endX)
		{
			mSrcRect = new Rect(startX, 0, endX, heigth);
			mDstRect = new Rect(0, 0,endX-startX, heigth);
		}
				
		@Override
		protected void onDraw(Canvas canvas)
		{
			Bitmap bm = null;
			Drawable drawable = getDrawable();
			if (null != drawable && drawable instanceof BitmapDrawable)
			{
				bm = ((BitmapDrawable)drawable).getBitmap();
			}
			
			if (null == bm)
			{
				super.onDraw(canvas);
			}
			else
			{
				canvas.drawBitmap(bm, mSrcRect, mDstRect, mPaint);
			}
		}    	
    }
}
