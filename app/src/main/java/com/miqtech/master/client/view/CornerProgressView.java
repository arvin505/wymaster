package com.miqtech.master.client.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.miqtech.master.client.R;
import com.miqtech.master.client.utils.Utils;

import java.util.Map;

/**
 * Created by Administrator on 2016/5/10.
 */
public class CornerProgressView extends View {
    private int startColor = Color.RED;
    private int endColor = Color.BLUE;
    private float maxCount = 100f;
    private float currentCount = 70f;
    private Paint mPaint;
    private int width, height;
    private int progressBackground;
    private int borderColor = Color.TRANSPARENT;


    public CornerProgressView(Context context) {
        this(context, null);
    }

    public CornerProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CornerProgressView);
        startColor = array.getColor(R.styleable.CornerProgressView_startColor, Color.GRAY);
        endColor = array.getColor(R.styleable.CornerProgressView_endColor, Color.GRAY);
        progressBackground = array.getColor(R.styleable.CornerProgressView_progressBackground, Color.GRAY);
        borderColor = array.getColor(R.styleable.CornerProgressView_borderColor, Color.TRANSPARENT);
        array.recycle();
        initView();
    }

    private void initView() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(progressBackground);
        int round = height / 2;
        RectF rectBg = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rectBg, round, round, mPaint);
        float section = currentCount / maxCount;
        RectF rectProgressBg = new RectF(0, 0, (width) * section, height);
        int count = (section <= 1.0f / 2.0f * 2) ? 2 : 2;
        int[] colors = new int[count];

        float[] positions = new float[count];
        if (count == 2) {
            positions[0] = 0.0f;
            positions[1] = 1.0f - positions[0];
            colors[0] = startColor;
            colors[1] = endColor;
        } else {
            positions[0] = 0.0f;
            positions[1] = (maxCount / 3) / currentCount;
            positions[2] = 1.0f - positions[0] * 2;
            colors[0] = startColor;
            colors[1] = endColor;
            colors[2] = endColor;
        }
        positions[positions.length - 1] = 1.0f;
        LinearGradient shader = new LinearGradient(0, 0, (width - 0) * section, height - 0, colors, null, Shader.TileMode.MIRROR);
        mPaint.setShader(shader);
        float percent = currentCount / maxCount;
        if (percent >= 0.04f) {
            canvas.save();
            if (percent<=0.05f){
                canvas.translate(Utils.dp2px(1),0);
            }
            canvas.drawRoundRect(rectProgressBg, round, round, mPaint);
            canvas.restore();
        } else {
            canvas.save();
            percent = percent < 0.02 ? 0.02f : percent;
            rectProgressBg = new RectF(0, 0, (width) * section, round / 2 * (percent * 100));
            canvas.translate( Utils.dp2px(1), height / 4 / (percent * 100) * 2);
            canvas.drawRoundRect(rectProgressBg, round, round, mPaint);
            canvas.restore();
        }
        if (borderColor != Color.TRANSPARENT) {
            RectF border = new RectF(rectBg);
            float borderWidth = Utils.dp2px(1);
            border.left += borderWidth / 2;
            border.right -= borderWidth / 2;
            border.top += borderWidth / 2;
            border.bottom -= borderWidth / 2;
            mPaint.setColor(borderColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setShader(null);
            mPaint.setStrokeWidth(borderWidth);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            canvas.drawRoundRect(border, round, round, mPaint);
        }

    }

    /***
     * 设置最大的进度值
     *
     * @param maxCount
     */
    public void setMaxCount(float maxCount) {
        this.maxCount = maxCount;
    }

    /***
     * 设置当前的进度值
     *
     * @param currentCount
     */
    public void setCurrentCount(float currentCount) {
        this.currentCount = currentCount > maxCount ? maxCount : currentCount;
        invalidate();
    }

    public float getMaxCount() {
        return maxCount;
    }

    public float getCurrentCount() {
        return currentCount;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.EXACTLY || widthSpecMode == MeasureSpec.AT_MOST) {
            width = widthSpecSize;
        } else {
            width = 0;
        }
        if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            height = Utils.dp2px(5);
        } else {
            height = heightSpecSize;
        }
        setMeasuredDimension(width, height);
    }

}
