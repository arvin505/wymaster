package com.miqtech.master.client.ui.baseactivity;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.mingle.circletreveal.CircularRevealCompat;
import com.mingle.widget.animation.CRAnimation;
import com.mingle.widget.animation.SimpleAnimListener;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.LogUtil;

/**
 * Created by Administrator on 2016/3/29.
 */
public class AnimtionFragment extends BaseFragment {

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    protected TranslateAnimation translateAnimation;

    /**
     * 线性移动动画开始
     *
     * @param v          执行动画的控件
     * @param parentview 动画的外部控件
     * @param fromeX     起始X位置
     * @param fromeY     起始Y位置
     */
    protected void startAnimLine(final View v, View parentview, int fromeX, int fromeY) {
        if (v == null || parentview == null || fromeX == 0 || fromeY == 0) {
            return;
        }
        int toX;
        int toY;
        //设置执行动画的控件的位置与点击的位置一样
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(v.getLayoutParams());
        margin.setMargins(fromeX, fromeY, fromeX, fromeY);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        v.setLayoutParams(layoutParams);
        v.setVisibility(View.VISIBLE);
        parentview.setVisibility(View.VISIBLE);//相同等级的外层布局显示，
        //外层布局的透明度由0到1
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(400);
        parentview.setAnimation(alphaAnimation);
        alphaAnimation.startNow();
        toX = WangYuApplication.toX - fromeX;
        toY = WangYuApplication.toY - fromeY - WangYuApplication.statusBarTop;
        //线性动画开始
        translateAnimation = new TranslateAnimation(
                0, toX,
                0, toY);
        translateAnimation.setDuration(300);
        v.startAnimation(translateAnimation);
        translateAnimation.startNow();
        translateAnimation.setFillAfter(true);
        translateAnimation.setFillBefore(false);
    }

    /**
     * 线性移动动画结束
     *
     * @param v   执行动画的控件
     * @param toX 结束X位置
     * @param toY 结束Y位置
     */
    protected void endAnimLine(final View v, int toX, int toY) {
        if (v == null || toX == 0 || toY == 0) {
            return;
        }
        int fromeX = WangYuApplication.toX - toX;
        int fromeY = WangYuApplication.toY - toY;
        TranslateAnimation translate = new TranslateAnimation(
                fromeX, 0,
                fromeY, 0);
        translate.setDuration(300);
        v.startAnimation(translate);
        translate.startNow();
    }

    /**
     * 波形散开动画
     *
     * @param mRl 该界面最外层的布局(这个布局必须用的是baselibrary里的布局)
     * @param v   波形散开的起点
     */
    protected void startAnimeCircle(final View mRl, final View v) {
        if (v == null || mRl == null) {
            return;
        }
        mRl.postDelayed(new Runnable() {
            @Override
            public void run() {
                CRAnimation crA =
                        new CircularRevealCompat(mRl).circularReveal(
                                WangYuApplication.circleX,
                                WangYuApplication.circleY,
                                0,
                                mRl.getHeight());
                if (crA != null) {
                    crA.addListener(new SimpleAnimListener() {
                        @Override
                        public void onAnimationStart(CRAnimation animation) {
                            super.onAnimationStart(animation);
                            v.setVisibility(View.VISIBLE);
                        }
                    });
                    crA.start();
                }
            }
        }, 400);
    }

    /**
     * 波形收拢动画
     *
     * @param view     该界面最外层的布局
     * @param animView 要结束的点
     */
    protected void endAnimeCircle(final View view, final View animView) {
        if (animView == null || view == null) {
            return;
        }
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                CRAnimation crA =
                        new CircularRevealCompat(view).circularReveal(animView.getLeft() + animView.getWidth() / 2, animView.getTop() + animView.getHeight() / 2, view.getHeight(), 0);
                if (crA != null) {
                    crA.start();
                }
            }
        }, 0);
    }


    /**
     * 控件缩小动画 （缩小百分之10）
     *
     * @param animView 执行动画的控件，执行完后该控件会消失
     */
    protected void animSuoxiao(final View animView) {
        if (animView == null) {
            return;
        }
        ScaleAnimation animation = new ScaleAnimation(1f, 0.9f, 1f, 0.9f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(400);
        animView.startAnimation(animation);
        animation.startNow();
        animation.setFillAfter(true);
        animation.setFillBefore(false);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animView.setVisibility(View.GONE);//执行完动画后消失
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 赋值 移动的终点的X、Y；波形散开的点的X、Y；状态栏的高度
     *
     * @param v 赋值控件的位置
     */
    protected void evaluateXY(View v) {
        if (v == null) {
            return;
        }
        if (WangYuApplication.toX == 0 || WangYuApplication.toY == 0 || WangYuApplication.circleX == 0 || WangYuApplication.circleY == 0 || WangYuApplication.statusBarTop == 0) {
            int[] location = new int[2];
            Rect rect = new Rect();
            getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);///取得整个视图部分,注意，如果你要设置标题样式，这个必须出现在标题样式之后，否则会出错
            int top = rect.top;//状态栏的高度，所以rect.height,rect.width分别是系统的高度的宽度
            v.getLocationOnScreen(location);
            WangYuApplication.toX = location[0];
            WangYuApplication.toY = location[1];
            WangYuApplication.circleX = location[0] + v.getWidth() / 2;
            WangYuApplication.circleY = location[1] + v.getHeight() / 2 - top;
            WangYuApplication.statusBarTop = top;

            LogUtil.e("WangYuApplication.toX----------------------",location[0]+"");
            LogUtil.e("WangYuApplication.toY----------------------",location[1]+"");
        }
    }
}
