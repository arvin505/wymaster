package com.miqtech.master.client.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.miqtech.master.client.utils.LogUtil;

/**
 * Created by pc on 2016/7/8.
 */
public class ExpandableTextView extends TextView {
    // 最大的行，默认只显示10行
    public  final int MAX = 5;

    // 如果完全伸展需要多少行？
    private int lines;

    private ExpandableTextView mPhilTextView;

    // 标记当前TextView的展开/收缩状态
    // true，已经展开
    // false，以及收缩
    private boolean expandableStatus = false;

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPhilTextView = this;

    }
    public void initSet(){
        init();
    }
    private void init() {

        // ViewTreeObserver View观察者，在View即将绘制但还未绘制的时候执行的，在onDraw之前
        final ViewTreeObserver mViewTreeObserver = this.getViewTreeObserver();

        mViewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                // 避免重复监听
                mPhilTextView.getViewTreeObserver().removeOnPreDrawListener(this);
                lines = getLineCount();
                setMaxLines(MAX);
                setEllipsize(TextUtils.TruncateAt.END);
                LogUtil.d(this.getClass().getName(), "wwwwww"+lines);
                return true;
            }
        });

    }
    public int getLineCount(){
        return lines;
    }
    // 是否展开或者收缩，
    // true，展开；
    // false，不展开
    public void setExpandable(boolean isExpand) {
        if (isExpand) {
            setMaxLines(Integer.MAX_VALUE);
        } else
            setMaxLines(MAX);

        expandableStatus = isExpand;
    }

    public boolean getExpandableStatus() {
        return expandableStatus;
    }
}
