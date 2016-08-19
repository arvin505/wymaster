package com.miqtech.master.client.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

public class SuperLoadingProgress extends View {
    /**
     * 当前进度
     */
    private int progress = 0;
    /**
     * 最大进度
     */
    private static final int maxProgress = 100;
    /**
     * 小方块抛出动画
     */
    private ValueAnimator mRotateAnimation;
    /**
     * 小方块下落
     */
    private ValueAnimator mDownAnimation;
    /**
     * 打钩
     */
    private ValueAnimator mTickAnimation;
    /**
     * 绘制叉
     */
    private ValueAnimator mCommaAnimation;

    private RectF mRectF = new RectF();
    private Paint circlePaint = new Paint();
    private Paint smallRectPaint = new Paint();
    private Paint downRectPaint = new Paint();
    private Paint tickPaint = new Paint();
    private Paint forkPaint = new Paint();
    /**
     * 画笔宽度
     */
    private int strokeWidth = 20;
    /**
     *
     */
    private float radius = 0;
    //0画圆,1抛出方块,2下落,3,勾,4,叉
    private int status = 0;
    /**
     * 测量下落路径
     */
    private PathMeasure downPathMeasure1;
    private PathMeasure downPathMeasure2;
    /**
     * 测量打钩
     */
    private PathMeasure tickPathMeasure;
    /**
     * 叉
     */
    private PathMeasure forkPathMeasure1;
    private PathMeasure forkPathMeasure2;
    private PathMeasure forkPathMeasure3;
    private PathMeasure forkPathMeasure4;
    /**
     * 下落百分比
     */
    float downPrecent = 0;
    /**
     * 打钩百分比
     */
    float tickPrecent = 0;
    /**
     * 叉百分比
     */
    float forkPrecent = 0;
    /**
     * 是否loading成功
     */
    public boolean isSuccess = false;
    /**
     * 起始角度
     */
    private static final float startAngle = -90;
    /**
     * 扫过角度
     */
    private float curSweepAngle = 0;

    public SuperLoadingProgress(Context context) {
        super(context);
        init();
    }

    public SuperLoadingProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SuperLoadingProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        radius = Math.min(getMeasuredWidth(), getMeasuredHeight()) / 4 - strokeWidth;
        mRectF.set(new RectF(radius + strokeWidth, radius + strokeWidth, 3 * radius + strokeWidth, 3 * radius + strokeWidth));
        //初始化下落路径
        Path downPath1 = new Path();
        downPath1.moveTo(2 * radius + strokeWidth, strokeWidth);
        downPath1.lineTo(2 * radius + strokeWidth, radius + strokeWidth);
        Path downPath2 = new Path();
        downPath2.moveTo(2 * radius + strokeWidth, strokeWidth);
        downPath2.lineTo(2 * radius + strokeWidth, 1.1f * radius + strokeWidth);
        downPathMeasure1 = new PathMeasure(downPath1, false);
        downPathMeasure2 = new PathMeasure(downPath2, false);
        //初始化打钩路径
        Path tickPath = new Path();
        tickPath.moveTo(1.5f * radius + strokeWidth, 2 * radius + strokeWidth);
        tickPath.lineTo(1.5f * radius + 0.3f * radius + strokeWidth, 2 * radius + 0.3f * radius + strokeWidth);
        tickPath.lineTo(2 * radius + 0.5f * radius + strokeWidth, 2 * radius - 0.3f * radius + strokeWidth);
        tickPathMeasure = new PathMeasure(tickPath, false);

        //TODO:fvd
        //叉的路径
        Path forkPath1 = new Path();
        Path forkPath2 = new Path();
        Path forkPath3 = new Path();
        Path forkPath4 = new Path();

        forkPath1.moveTo(2f * radius + strokeWidth, 2f * radius + strokeWidth);
        forkPath1.lineTo(1.5f * radius + strokeWidth, 1.5f * radius + strokeWidth);//上左

        forkPath2.moveTo(2f * radius + strokeWidth, 2f * radius + strokeWidth);
        forkPath2.lineTo(2.5f * radius + strokeWidth, 1.5f * radius + strokeWidth);//上右

        forkPath3.moveTo(2f * radius + strokeWidth, 2f * radius + strokeWidth);
        forkPath3.lineTo(1.5f * radius + strokeWidth, 2.5f * radius + strokeWidth);//下左

        forkPath4.moveTo(2f * radius + strokeWidth, 2f * radius + strokeWidth);
        forkPath4.lineTo(2.5f * radius + strokeWidth, 2.5f * radius + strokeWidth);//下右

        forkPathMeasure1 = new PathMeasure(forkPath1, false);
        forkPathMeasure2 = new PathMeasure(forkPath2, false);
        forkPathMeasure3 = new PathMeasure(forkPath3, false);
        forkPathMeasure4 = new PathMeasure(forkPath4, false);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSpecSize + 10 * strokeWidth, heightSpecSize + 10 * strokeWidth);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private float endAngle;

    private void init() {
        //圆画笔
        circlePaint.setAntiAlias(true);
        circlePaint.setStrokeWidth(strokeWidth);
        circlePaint.setStyle(Paint.Style.STROKE);

        //抛出画笔
        smallRectPaint.setAntiAlias(true);
        smallRectPaint.setStrokeWidth(strokeWidth / 2);
        smallRectPaint.setStyle(Paint.Style.STROKE);

        //下落画笔
        downRectPaint.setAntiAlias(true);
        downRectPaint.setStrokeWidth(strokeWidth);
        downRectPaint.setStyle(Paint.Style.FILL);

        //叉画笔
        forkPaint.setAntiAlias(true);
        forkPaint.setStrokeWidth(strokeWidth);
        forkPaint.setStyle(Paint.Style.STROKE);

        //勾画笔
        tickPaint.setAntiAlias(true);
        tickPaint.setStrokeWidth(strokeWidth);
        tickPaint.setStyle(Paint.Style.STROKE);

        //抛出动画
        endAngle = (float) Math.atan(4f / 3);
        mRotateAnimation = ValueAnimator.ofFloat(0f, endAngle * 0.9f);
        mRotateAnimation.setDuration(500);
        mRotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mRotateAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                curSweepAngle = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mRotateAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                curSweepAngle = 0;
                status = 2;
                mDownAnimation.start();
            }
        });

        //下落动画
        mDownAnimation = ValueAnimator.ofFloat(0f, 1f);
        mDownAnimation.setDuration(500);
        mDownAnimation.setInterpolator(new AccelerateInterpolator());
        mDownAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                downPrecent = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mDownAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (isSuccess) {
                    mTickAnimation.start();
                } else {
                    mCommaAnimation.start();
                }
            }
        });


        //打钩动画
        mTickAnimation = ValueAnimator.ofFloat(0f, 1f);
        mTickAnimation.setDuration(500);
        mTickAnimation.setInterpolator(new AccelerateInterpolator());
        mTickAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tickPrecent = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mTickAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                status = 3;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingFinish.loadingFinish();
                    }
                },1000);
            }
        });

        //叉动画
        mCommaAnimation = ValueAnimator.ofFloat(0f, 1f);
        mCommaAnimation.setDuration(500);
        mCommaAnimation.setInterpolator(new AccelerateInterpolator());
        mCommaAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                forkPrecent = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mCommaAnimation.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                status = 4;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingFinish.loadingFinish();
                    }
                },1000);
            }
        });


    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (status) {
            case 0:
                if (colorId != -1) {
                    circlePaint.setColor(colorId);
                } else {
                    circlePaint.setColor(Color.argb(255, 255, 102, 0));
                }
                float precent = 1.0f * progress / maxProgress;
                canvas.drawArc(mRectF, startAngle - 270 * precent, -(60 + precent * 300), false, circlePaint);
                break;
            case 1:
                drawSmallRectFly(canvas);
                break;
            case 2:
                drawRectDown(canvas);
                break;
            case 3:
                drawTick(canvas);
                break;
            case 4:
                drawComma(canvas);
                break;
        }
    }

    /**
     * 抛出小方块
     *
     * @param canvas
     */
    private void drawSmallRectFly(Canvas canvas) {
        if (colorId != -1) {
            circlePaint.setColor(colorId);
            smallRectPaint.setColor(colorId);
        } else {
            circlePaint.setColor(Color.argb(255, 255, 102, 0));
            smallRectPaint.setColor(Color.argb(255, 255, 102, 0));
        }

        canvas.save();
        canvas.translate(radius / 2 + strokeWidth, 2 * radius + strokeWidth);//将坐标移动到大圆圆心
        float bigRadius = 5 * radius / 2;//大圆半径
        float x1 = (float) (bigRadius * Math.cos(curSweepAngle));
        float y1 = -(float) (bigRadius * Math.sin(curSweepAngle));
        float x2 = (float) (bigRadius * Math.cos(curSweepAngle + 0.05 * endAngle + 0.1 * endAngle * (1 - curSweepAngle / 0.9 * endAngle)));//
        float y2 = -(float) (bigRadius * Math.sin(curSweepAngle + 0.05 * endAngle + 0.1 * endAngle * (1 - curSweepAngle / 0.9 * endAngle)));
        canvas.drawLine(x1, y1, x2, y2, smallRectPaint);
        canvas.restore();
        canvas.drawArc(mRectF, 0, 360, false, circlePaint);
    }

    /**
     * 绘制下落过程
     *
     * @param canvas
     */

    private void drawRectDown(Canvas canvas) {
        if (colorId != -1) {
            downRectPaint.setColor(colorId);
            circlePaint.setColor(colorId);
        } else {
            circlePaint.setColor(Color.argb(255, 255, 102, 0));
            downRectPaint.setColor(Color.argb(255, 255, 102, 0));
        }
        //TODO:fvd
        //下落方块的起始端坐标
        float pos1[] = new float[2];
        float tan1[] = new float[2];
        downPathMeasure1.getPosTan(downPrecent * downPathMeasure1.getLength(), pos1, tan1);
        //下落方块的末端坐标
        float pos2[] = new float[2];
        float tan2[] = new float[2];
        downPathMeasure2.getPosTan(downPrecent * downPathMeasure2.getLength(), pos2, tan2);
        //椭圆形区域
        Rect mRect = new Rect(Math.round(mRectF.left), Math.round(mRectF.top + mRectF.height() * 0.1f * downPrecent),
                Math.round(mRectF.right), Math.round(mRectF.bottom - mRectF.height() * 0.1f * downPrecent));

        Region region = new Region(Math.round(pos1[0]) - strokeWidth / 4, Math.round(pos1[1]), Math.round(pos2[0] + strokeWidth / 4), Math.round(pos2[1]));
        region.op(mRect, Region.Op.DIFFERENCE);
        drawRegion(canvas, region, downRectPaint);
        canvas.drawArc(mRectF, 0, 360, false, circlePaint);
    }


    /**
     * 绘制打钩
     *
     * @param canvas
     */
    private void drawTick(Canvas canvas) {
        if (colorId != -1) {
            tickPaint.setColor(colorId);
        } else {
            tickPaint.setColor(Color.argb(255, 255, 102, 0));
        }
        Path path = new Path();
        tickPathMeasure.getSegment(0, tickPrecent * tickPathMeasure.getLength(), path, true);
        path.rLineTo(0, 0);
        canvas.drawPath(path, tickPaint);
        canvas.drawArc(mRectF, 0, 360, false, tickPaint);
    }

    /**
     * 绘制叉
     */
    private void drawComma(Canvas canvas) {
        if (colorId != -1) {
            forkPaint.setColor(colorId);
        } else {
            forkPaint.setColor(Color.argb(255, 255, 102, 0));
        }
        //TODO:fvd
        Path path1 = new Path();
        forkPathMeasure1.getSegment(0, forkPrecent * forkPathMeasure1.getLength(), path1, true);
        path1.rLineTo(0, 0);
        canvas.drawPath(path1, forkPaint);

        Path path2 = new Path();
        forkPathMeasure2.getSegment(0, forkPrecent * forkPathMeasure2.getLength(), path2, true);
        path2.rLineTo(0, 0);
        canvas.drawPath(path2, forkPaint);

        Path path3 = new Path();
        forkPathMeasure3.getSegment(0, forkPrecent * forkPathMeasure3.getLength(), path3, true);
        path3.rLineTo(0, 0);
        canvas.drawPath(path3, forkPaint);

        Path path4 = new Path();
        forkPathMeasure4.getSegment(0, forkPrecent * forkPathMeasure4.getLength(), path4, true);
        path4.rLineTo(0, 0);
        canvas.drawPath(path4, forkPaint);
        canvas.drawArc(mRectF, 0, 360, false, forkPaint);
    }

    /**
     * 绘制区域
     *
     * @param canvas
     * @param rgn
     * @param paint
     */
    private void drawRegion(Canvas canvas, Region rgn, Paint paint) {
        RegionIterator iter = new RegionIterator(rgn);
        Rect r = new Rect();
        while (iter.next(r)) {
            canvas.drawRect(r, paint);
        }
    }

    /**
     * 开始完成动画
     */
    private void start() {
        post(new Runnable() {
            @Override
            public void run() {
                mRotateAnimation.start();
            }
        });
    }

    public void setProgress(int progress) {
        this.progress = Math.min(progress, maxProgress);
        postInvalidate();
        if (progress == 0) {
            status = 0;
        }
    }

    public int getProgress() {
        return progress;
    }


    /**
     * loading成功或者失败后调用
     * @param isSuccess
     */
    public void finishIsSuccess(boolean isSuccess) {
        setProgress(maxProgress);
        this.isSuccess = isSuccess;
        status = 1;
        start();
    }

    private int colorId = -1;

    /**
     * loading动画颜色
     *
     * @param colorId
     */
    public void setLodingColor(int colorId) {
        this.colorId = colorId;
    }

    public interface LoadingFinish{
        /**
         * 动画完成
         */
        void loadingFinish();
    }

    private LoadingFinish loadingFinish;

    public void setLoadingFinish(LoadingFinish loadingFinish){
        this.loadingFinish = loadingFinish;
    }

}
