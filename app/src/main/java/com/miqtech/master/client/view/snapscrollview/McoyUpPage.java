package com.miqtech.master.client.view.snapscrollview;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.RewardCardAdapter;
import com.miqtech.master.client.entity.Reward;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.BitmapUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.cardView.CardAdapter;
import com.miqtech.master.client.view.cardView.CardView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;


public class McoyUpPage implements McoySnapPageLayout.McoySnapPage, View.OnClickListener,
        CardView.GetWhichPosition {

    private Context context;
    private View rootView = null;

    private LinearLayout linearLayout;

    private LinearLayout llBack;//返回
    private McoyScrollView mcoyScrollView;
    private TextView tvTitle;//标题
    private CardView cardView;//卡片

    private RelativeLayout rlLove;//赞
    private ImageView ivLove;
    private TextView tvLoveNum;

    private RelativeLayout rlComment;//评论
    private ImageView ivComment;
    private TextView tvCommentNum;

    private RewardCardAdapter adapter;
    private List<Reward> rewardList = new ArrayList<>();
    private String loveNum = "";//点赞个数
    private String commentNum = "";//评论个数

    private McoySnapPageLayout mcoySnapPageLayout;
    private int position;//该悬赏令在数据中的位置
    private int isOne = -1;

    public McoyUpPage(Context context, View rootView, McoySnapPageLayout mcoySnapPageLayout, int isOne) {
        this.context = context;
        this.rootView = rootView;
        this.mcoySnapPageLayout = mcoySnapPageLayout;
        this.isOne = isOne;
        initView();
    }

    private void initView() {
        mcoyScrollView = (McoyScrollView) rootView.findViewById(R.id.mcoyScrollView);

        linearLayout = (LinearLayout) rootView.findViewById(R.id.reawrdUpLl);

        cardView = (CardView) rootView.findViewById(R.id.rewardUpCardView);
        llBack = (LinearLayout) rootView.findViewById(R.id.reawrdUpLlBack);

        tvTitle = (TextView) rootView.findViewById(R.id.rewardUpTitle);

        rlLove = (RelativeLayout) rootView.findViewById(R.id.rewardUpRlLove);
        ivLove = (ImageView) rootView.findViewById(R.id.rewardUpIvLove);
        tvLoveNum = (TextView) rootView.findViewById(R.id.rewardUpTvLoveNum);

        rlComment = (RelativeLayout) rootView.findViewById(R.id.rewardUpRlComment);
        ivComment = (ImageView) rootView.findViewById(R.id.rewardUpIvComment);
        tvCommentNum = (TextView) rootView.findViewById(R.id.rewardUpTvCommentNum);

        llBack.setOnClickListener(this);
        rlLove.setOnClickListener(this);
        rlComment.setOnClickListener(this);
    }

    /**
     * 填充数据
     *
     * @param rewardList
     */
    public void setUpPageData(List<Reward> rewardList) {
        if (rewardList == null || rewardList.isEmpty()) {
            return;
        }

        this.rewardList = rewardList;

        if (rewardList.get(0).getCheckStatus() == -1 && isOne != 1) {//当未到结束时间时，需要下滑；到了结束时间，只会有一张悬赏令，不滑动
            cardView.setGoDown(true);
            cardView.setMaxVisibleCount(3);
        } else {
            cardView.setMaxVisibleCount(1);
            cardView.setGoDown(false);
        }

        adapter = new RewardCardAdapter(context);
        adapter.addAll(rewardList);
        cardView.setAdapter(adapter, rewardList);
        cardView.setOnGetWhichPosition(this);
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reawrdUpLlBack://返回
                ((Activity) context).onBackPressed();
                break;
            case R.id.rewardUpRlLove://收藏
                if (getRewardCommentId != null && !rewardList.isEmpty()) {
                    getRewardCommentId.isFavReward(rewardList.get(position));
                }
                break;
            case R.id.rewardUpRlComment://评论
                if (!mcoyScrollView.isBottom) {
                    mcoyScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
                mcoySnapPageLayout.snapToNext();//滑动到下一个评论页面
                break;
        }
    }

    private void initData() {
        if (rewardList.isEmpty()) {
            return;
        }
        if (getRewardCommentId != null) {
            getRewardCommentId.getRewardCommentId(rewardList.get(0).getId(), 0);
        }

        showData(rewardList.get(0));
    }

    @Override
    public void getWhichPosition(int position) {
        if (rewardList.isEmpty()) {
            return;
        }
        this.position = position;

        if (getRewardCommentId != null) {
            getRewardCommentId.getRewardCommentId(rewardList.get(position).getId(), position);
        }

        showData(rewardList.get(position));
    }

    public interface GetRewardCommentId {
        /**
         * 得到相应评论的id
         *
         * @param id       悬赏令id
         * @param position 该悬赏令在数据中的位置
         */
        void getRewardCommentId(int id, int position);

        /**
         * 是否点赞
         *
         * @param bean
         */
        void isFavReward(Reward bean);

        /**
         * 显示背景图
         *
         * @param imgUrl
         * @param linearLayout
         */
        void showBackground(String imgUrl, LinearLayout linearLayout);
    }

    public GetRewardCommentId getRewardCommentId;

    public void setOnGetRewardCommentId(GetRewardCommentId getRewardCommentId) {
        this.getRewardCommentId = getRewardCommentId;
    }

    /**
     * 显示导航栏以及底部的数据
     *
     * @param bean Reward
     */
    private void showData(Reward bean) {
        if (bean == null) {
            return;
        }

        //显示标题
        if (!TextUtils.isEmpty(bean.getTitle())) {
            tvTitle.setText(bean.getTitle());
        }

        if (getRewardCommentId != null) {
            getRewardCommentId.showBackground(bean.getCover(), linearLayout);
        }

        mcoyScrollView.setReward(bean);

        showFavNum(bean);
        showCommentNum(bean);
    }

    /**
     * 显示点赞数
     *
     * @param bean
     */
    public void showFavNum(Reward bean) {
        //显示点赞数
        if (bean.getFavNum() == 0) {
            loveNum = "";
            ivLove.setImageResource(R.drawable.wanted_love);//显示对应的图片
            tvLoveNum.setText("");
        } else {
            if (bean.getHas_favor() == 1) {//当用户登陆时，显示为红心的图片，否则显示白心的图片
                ivLove.setImageResource(R.drawable.wanted_love_full);
            } else {
                ivLove.setImageResource(R.drawable.wanted_love_more);
            }

            if (bean.getFavNum() > 999) {
                loveNum = "999+";
            } else if (bean.getFavNum() > 99) {
                loveNum = bean.getFavNum() + "";
            } else if (bean.getFavNum() > 9) {
                loveNum = bean.getFavNum() + " ";
            } else if (bean.getFavNum() > 0) {
                loveNum = bean.getFavNum() + "  ";
            }
            tvLoveNum.setText(loveNum);
        }
    }

    /**
     * 显示评论数量
     *
     * @param bean
     */
    public void showCommentNum(Reward bean) {
        //显示评论数
        if (bean.getCommentNum() == 0) {
            commentNum = "";
            ivComment.setImageResource(R.drawable.wanted_comment);//显示对应的图片
            tvCommentNum.setText("");
        } else {
            ivComment.setImageResource(R.drawable.wanted_comment_more);
            if (bean.getCommentNum() > 999) {
                commentNum = "999+";
            } else if (bean.getCommentNum() > 99) {
                commentNum = bean.getCommentNum() + "";
            } else if (bean.getCommentNum() > 9) {
                commentNum = bean.getCommentNum() + " ";
            } else if (bean.getCommentNum() > 0) {
                commentNum = bean.getCommentNum() + "  ";
            }
            tvCommentNum.setText(commentNum);
        }
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    @Override
    public boolean isAtTop() {
        return true;
    }

    @Override
    public boolean isAtBottom() {
        int scrollY = mcoyScrollView.getScrollY();
        int height = mcoyScrollView.getHeight();
        int scrollViewMeasuredHeight = mcoyScrollView.getChildAt(0).getMeasuredHeight();
        if ((scrollY + height) >= scrollViewMeasuredHeight) {
            return true;
        }
        return false;
    }
}
