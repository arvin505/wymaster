package com.miqtech.master.client.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoyi on 2016/5/31.
 * 标签布局
 */
public class FlowLayout extends ViewGroup {
    //按行存储所有view
    private List<List<View>> mAllViews = new ArrayList<>();
    //没行的行高
    private List<Integer> mLineHeights = new ArrayList<>();
    //子view数量
    private int childCount;

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 只关心margin值 marginlayoutparams
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        childCount = getChildCount();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int lineWidth = 0;
        int lineHeight = 0;
        int width = 0;
        int height = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int cWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int cHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            if (cWidth + lineWidth > widthSize - getPaddingLeft() - getPaddingRight()) {
                width = Math.max(lineWidth, width);
                lineWidth = cWidth;
                height += lineHeight;
                lineHeight = cHeight;
            } else {
                lineWidth += cWidth;
                lineHeight = Math.max(lineHeight, cHeight);
            }
        }
        height += lineHeight + getPaddingTop() + getPaddingBottom();
        width = Math.max(lineWidth, width) + getPaddingLeft() + getPaddingRight();
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width,
                heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeights.clear();
        List<View> lineViews = new ArrayList<>();
        int width = getWidth();
        int lineWidth = 0;
        int lineHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            if (lineWidth + child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin >
                    width - getPaddingLeft() - getPaddingRight()) {
                mAllViews.add(lineViews);
                mLineHeights.add(lineHeight);
                lineWidth = 0;
                lineHeight = 0;
                lineViews = new ArrayList<>();
            }
            lineViews.add(child);
            lineWidth += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
        }
        mAllViews.add(lineViews);
        mLineHeights.add(lineHeight);
        int left = getPaddingLeft();
        int top = getPaddingTop();
        for (int i = 0; i < mAllViews.size(); i++) {
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeights.get(i);
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                int cl = left + lp.leftMargin;
                int ct = top + lp.topMargin;
                int cr = cl + child.getMeasuredWidth();
                int cb = ct + child.getMeasuredHeight();
                child.layout(cl, ct, cr, cb);
                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
            left = getPaddingLeft();
            top += lineHeight;
        }
    }
}
