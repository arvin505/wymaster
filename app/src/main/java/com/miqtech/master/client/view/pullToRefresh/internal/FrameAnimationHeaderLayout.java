package com.miqtech.master.client.view.pullToRefresh.internal;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.miqtech.master.client.R;
import com.miqtech.master.client.view.pullToRefresh.LoadingLayoutBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;

/**
 * Created by zwenkai on 2015/12/19.
 */
public class FrameAnimationHeaderLayout extends LoadingLayoutBase {

    static final String LOG_TAG = "";

    private FrameLayout mInnerLayout;

    private final TextView mHeaderText;
    private final TextView mSubHeaderText;

    private CharSequence mPullLabel;
    private CharSequence mRefreshingLabel;
    private CharSequence mReleaseLabel;

    private ImageView mGoodsImage;
    private ImageView mPersonImage;
    private AnimationDrawable animP;

    public FrameAnimationHeaderLayout(Context context) {
        this(context, PullToRefreshBase.Mode.PULL_FROM_START);
    }

    public FrameAnimationHeaderLayout(Context context, PullToRefreshBase.Mode mode) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.pr_frame_animation_header_loadinglayout, this);

        mInnerLayout = (FrameLayout) findViewById(R.id.fl_inner);
        mHeaderText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_text);
        mSubHeaderText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_sub_text);
        mGoodsImage = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_goods);
        mPersonImage = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_people);

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInnerLayout.getLayoutParams();
        lp.gravity = mode == PullToRefreshBase.Mode.PULL_FROM_END ? Gravity.TOP : Gravity.BOTTOM;

        // Load in labels
        mPullLabel = context.getResources().getString(R.string.pr_pull_to_refresh_pull_label);
        mRefreshingLabel =context.getResources().getString(R.string.pr_pull_to_refresh_refresh_lable);
        mReleaseLabel = context.getResources().getString(R.string.pr_pull_to_refresh_release_label);

        reset();
    }

    @Override
    public final int getContentSize() {
        return mInnerLayout.getHeight();
    }

    @Override
    public final void pullToRefresh() {
        mSubHeaderText.setText(mPullLabel);
    }

    /**
     * 上啦待去处理
     * @param scaleOfLayout scaleOfLayout
     */
    @Override
    public final void onPull(float scaleOfLayout) {
//        scaleOfLayout = scaleOfLayout > 1.0f ? 1.0f : scaleOfLayout;
//
//        if (mGoodsImage.getVisibility() != View.VISIBLE) {
//            mGoodsImage.setVisibility(View.VISIBLE);
//        }
//
//        //透明度动画
//        ObjectAnimator animAlphaP = ObjectAnimator.ofFloat(mPersonImage, "alpha", -1, 1).setDuration(300);
//        animAlphaP.setCurrentPlayTime((long) (scaleOfLayout * 300));
//        ObjectAnimator animAlphaG = ObjectAnimator.ofFloat(mGoodsImage, "alpha", -1, 1).setDuration(300);
//        animAlphaG.setCurrentPlayTime((long) (scaleOfLayout * 300));
//
//        //缩放动画
//        ViewHelper.setPivotX(mPersonImage, 0);  // 设置中心点
//        ViewHelper.setPivotY(mPersonImage, 0);
//        ObjectAnimator animPX = ObjectAnimator.ofFloat(mPersonImage, "scaleX", 0, 1).setDuration(300);
//        animPX.setCurrentPlayTime((long) (scaleOfLayout * 300));
//        ObjectAnimator animPY = ObjectAnimator.ofFloat(mPersonImage, "scaleY", 0, 1).setDuration(300);
//        animPY.setCurrentPlayTime((long) (scaleOfLayout * 300));
//
//        ViewHelper.setPivotX(mGoodsImage, mGoodsImage.getMeasuredWidth());
//        ObjectAnimator animGX = ObjectAnimator.ofFloat(mGoodsImage, "scaleX", 0, 1).setDuration(300);
//        animGX.setCurrentPlayTime((long) (scaleOfLayout * 300));
//        ObjectAnimator animGY = ObjectAnimator.ofFloat(mGoodsImage, "scaleY", 0, 1).setDuration(300);
//        animGY.setCurrentPlayTime((long) (scaleOfLayout * 300));
    }

    @Override
    public final void refreshing() {
        mSubHeaderText.setText(mRefreshingLabel);
        if (animP == null) {
            mPersonImage.setImageResource(R.drawable.pr_refreshing_anim);
            animP = (AnimationDrawable) mPersonImage.getDrawable();
        }
        animP.start();
        if (mGoodsImage.getVisibility() == View.VISIBLE) {
            mGoodsImage.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public final void releaseToRefresh() {
        mSubHeaderText.setText(mReleaseLabel);
    }

    @Override
    public final void reset() {
        if (animP != null) {
            animP.stop();
            animP = null;
        }
        mPersonImage.setImageResource(R.drawable.pr_refresh_1);
        if (mGoodsImage.getVisibility() == View.VISIBLE) {
            mGoodsImage.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
        mSubHeaderText.setText(label);
    }

    @Override
    public void setPullLabel(CharSequence pullLabel) {
        mPullLabel = pullLabel;
    }

    @Override
    public void setRefreshingLabel(CharSequence refreshingLabel) {
        mRefreshingLabel = refreshingLabel;
    }

    @Override
    public void setReleaseLabel(CharSequence releaseLabel) {
        mReleaseLabel = releaseLabel;
    }

    @Override
    public void setTextTypeface(Typeface tf) {
        mHeaderText.setTypeface(tf);
    }
}