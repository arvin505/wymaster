package com.miqtech.master.client.view.pullToRefresh.internal;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.miqtech.master.client.R;


public class LoadingView extends View {
	//enum for the various animations the LoadingView can do.
	private enum AnimMode {
        A_UP , A_DOWN, A_DOWN_UP, A_DRAW_FULL
    }
	
	//Scale used on different density screens, to calculate animation movements.
	private float mScale = getContext().getResources().getDisplayMetrics().density;
	//times used to control fps of animation.
	private long mTimeFrameLastDrawnAt = 0;
	private long mCurrentTime;
	
	//enum for which mode of animation the LoadingView is in.
    private AnimMode mMode;
    //boolean controlling the cycling between up and down animations.
	private boolean mIsAnimUp;
	
	//x,y coordinates for the animations start and end position.
	//x,y directions indicated in diagram, ie. y-axis is inverted.
	//            (-y)
	//              |
	//      (-x) ---+--- (+x)
	//              |
	//            (+y)
	private final int X_START_POS = (int) (-128*mScale + 0.5f);
	private final int Y_START_POS = (int) (-27*mScale + 0.5f);
    private final int Y_END_POS = (int) (20*mScale + 0.5f);
    
    //position to draw the animation frames when done.
    private final int mScaledX = (int) (-42*mScale + 0.5f);
    private final int mScaledY = (int) (-37*mScale + 0.5f);
    
    //movement in x,y of the beer level, for different screen densities.
    private float mDeltaX = (2*mScale);
    private float mDeltaY = (1*mScale);
    
    //since the pulltorefresh_library uses 4 instances of LoadingLayout, 
    //we need to sync the position of the beer level to make movements 
    //up and down appear smooth.
    private static float mCurrentX;
    private static float mCurrentY;
    
	//Instantiate static resources used in the animation.
    private Bitmap mBackground = BitmapFactory.decodeResource(getResources(),
    		R.drawable.pr_background);
    private Bitmap mBackgroundMask = BitmapFactory.decodeResource(getResources(), 
    		R.drawable.pr_backmask);
    private Bitmap mForeground = BitmapFactory.decodeResource(getResources(),
    		R.drawable.pr_foreground);
    //create resulting bitmap used to draw the LoadingView.
    private Bitmap mResult = Bitmap.createBitmap(mBackground.getWidth(), 
    		mBackground.getHeight(), Bitmap.Config.ARGB_8888);
    
    //drawing resources.
    private Paint mPaint = new Paint();
    private Canvas mResultCanvas = new Canvas(mResult);
    
    //constructor
    public LoadingView(Context context) {
		super(context);
		init();
    }

    //constructor
    public LoadingView(Context context, AttributeSet attrs) {
    	super(context, attrs);
        init();
	}

    //helper function for constructors.
	private void init() {
		mPaint.setFilterBitmap(false);
		mMode = AnimMode.A_DRAW_FULL;
		mIsAnimUp = true;
		mCurrentX = X_START_POS;
		mCurrentY = Y_START_POS;
	}

	//public function to call the animating of the LoadingView.
	//animates the beer level dropping.
	public void animDown() {
		mMode = AnimMode.A_DOWN;
		invalidate();
	}
	//public function to call the animating of the LoadingView.
	//animates the beer level rising.
	public void animUp() {
		mMode = AnimMode.A_UP;
		invalidate();
	}
	//public function to call the animating of the LoadingView.
	//animates the beer level cycling between falling first then rising.
	public void animDownUp() {
		mMode = AnimMode.A_DOWN_UP;
		invalidate();
	}
	//public function to call the animating of the LoadingView.
	//used to reset the LoadingView.
	public void animDrawFull() {
		mCurrentX = X_START_POS;
		mCurrentY = Y_START_POS;
		mMode = AnimMode.A_DRAW_FULL;
		invalidate();		
	}
	
	//helper function for the onDraw() method.
	//draws the static resources onto the canvas used to draw the LoadingView.
	private void drawFrame(int x, int y, Canvas c) {
		c.drawBitmap(mBackgroundMask, 0, 0, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        c.drawBitmap(mBackground, x, y, mPaint);
        mPaint.setXfermode(null);
        c.drawBitmap(mForeground, 0, 0, mPaint);
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mCurrentTime = SystemClock.uptimeMillis();
		
		//time controlled frame rate: +25ms => 40fps
        if ((mTimeFrameLastDrawnAt + 25) < mCurrentTime) {
        	//switch to control which animation is being played.
			switch (mMode) {
	        	//draws the glass full.
	        	case A_DRAW_FULL:
	        		drawFrame((int) mCurrentX, (int) mCurrentY, mResultCanvas);
	        		canvas.drawBitmap(mResult, mScaledX, mScaledY, null);
	        		mTimeFrameLastDrawnAt = SystemClock.uptimeMillis();
	        		invalidate();
	                break;
	            
	            //animates the beer level dropping.
	        	case A_DOWN:
	        		if (mCurrentY <= Y_END_POS) {
	        			mCurrentX += mDeltaX;
		            	mCurrentY += mDeltaY;
	        		}
	        		drawFrame((int) mCurrentX, (int) mCurrentY, mResultCanvas);
	        		canvas.drawBitmap(mResult, mScaledX, mScaledY, null);
	            	mTimeFrameLastDrawnAt = SystemClock.uptimeMillis();
	            	invalidate();
		            break;
		        
		        //animates the beer level rising.	
	        	case A_UP:
	        		if (mCurrentY >= Y_START_POS) {
	        			mCurrentX -= mDeltaX;
	        			mCurrentY -= mDeltaY;
	        		}
	        		drawFrame((int) mCurrentX, (int) mCurrentY, mResultCanvas);
	        		canvas.drawBitmap(mResult, mScaledX, mScaledY, null);
	        		mTimeFrameLastDrawnAt = SystemClock.uptimeMillis();
	            	invalidate();                	
		            break;

		        //animates the beer level cycling between rising and falling.	
	        	case A_DOWN_UP:
	        		//start by dropping the beer level to the bottom.
	        		if (mIsAnimUp == true) {
	        			if (mCurrentY <= Y_END_POS) {
	            			mCurrentX += mDeltaX;
	    	            	mCurrentY += mDeltaY;
	        			}
	        			drawFrame((int) mCurrentX, (int) mCurrentY, mResultCanvas);
	        			canvas.drawBitmap(mResult, mScaledX, mScaledY, null);
	        			
	        			if (mCurrentY <= Y_END_POS) {
	    	            	mTimeFrameLastDrawnAt = SystemClock.uptimeMillis();
	    	            	invalidate();
	        			}
	    	            else {
	    	            	//when beer level dropped past Y_END_POS, 
	    	            	//toggle beer level rising boolean.
	    	            	mIsAnimUp = false;
	    	            	invalidate();
	    	            }
	    	        }
	        		//then rise the beer level, repeat there after.
	        		else {
	        			if (mCurrentY >= Y_START_POS) {
	            			mCurrentX -= mDeltaX;
	            			mCurrentY -= mDeltaY;
	        			}
	        			drawFrame((int) mCurrentX, (int) mCurrentY, mResultCanvas);
	        			canvas.drawBitmap(mResult, mScaledX, mScaledY, null);
	        				        			
	    	            if (mCurrentY >= Y_START_POS) {
	    	            	mTimeFrameLastDrawnAt = SystemClock.uptimeMillis();
	    	            	invalidate();
	    	            }
	    	            else {
	    	            	//and repeat.
	    	            	mIsAnimUp = true;
	    	            	mTimeFrameLastDrawnAt = SystemClock.uptimeMillis();
	    	            	invalidate();
	    	            }
	    	        }
	        		break;
	        }
        }
        //else draw again without moving beer level and invalidate.
        else {
        	drawFrame((int) mCurrentX, (int) mCurrentY, mResultCanvas);
        	canvas.drawBitmap(mResult, mScaledX, mScaledY, null);
        	invalidate();
        }
    }
	
	//Sets the size of the view to 45x58 pixels @ 160dpi, scaled for other densities.
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = (int) (55*(mScale) + 0.5f);
        int width = (int) (42*(mScale) + 0.5f);
        setMeasuredDimension(width, height);
    }
}