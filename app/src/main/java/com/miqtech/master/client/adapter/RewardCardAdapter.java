package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.Reward;
import com.miqtech.master.client.entity.RewardGrade;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.ImagePagerActivity;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.RewardDetailActivity;
import com.miqtech.master.client.ui.RewardThePrizeActivity;
import com.miqtech.master.client.ui.SubmitGradesActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.view.CircleImageView;
import com.miqtech.master.client.view.RewardTimeTaskLinearLayout;
import com.miqtech.master.client.view.VerticalImageSpan;
import com.miqtech.master.client.view.cardView.CardAdapter;
import com.miqtech.master.client.watcher.Observerable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhaosentao on 2016/7/25.
 */
public class RewardCardAdapter extends CardAdapter<Reward> {

    private Animation outAnimation;
    private Animation inAnimation;
    private String statuExplian = "";
    private int total;
    private int totalPosition;
    private Observerable observerable = Observerable.getInstance();
    private Animation animation;

    public RewardCardAdapter(Context context) {
        super(context);
        init();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void addAll(List<Reward> items) {
        super.addAll(items);
        total = mData.size();
        totalPosition = total - 1;
    }

    @Override
    protected View getCardView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_reward_up_page_cardview_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        showCardData(holder, position);
        return convertView;
    }

    /**
     * 显示卡片数据
     *
     * @param holder
     * @param position
     */
    private void showCardData(ViewHolder holder, int position) {

        if (mData.isEmpty()) {
            return;
        }

        Reward bean = mData.get(getShowPosition(position));

        initStatus(holder, bean);

        //显示卡片中的图片
        if (!TextUtils.isEmpty(bean.getIcon())) {
            AsyncImage.loadNetPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + bean.getIcon() + "!small", holder.ivCardImg);
        } else {
            holder.ivCardImg.setImageResource(R.drawable.wanted_killer);
        }

        //用户获奖
        if (bean.getIs_win() == 1) {
            holder.ivPass.setVisibility(View.VISIBLE);
        } else {
            holder.ivPass.setVisibility(View.INVISIBLE);
        }

        //显示目标
        if (!TextUtils.isEmpty(bean.getTarget())) {
            holder.tvGold.setText(addconnent(bean.getTarget(), 1));
        } else {
            holder.tvGold.setText("");
        }

        //显示奖励
        if (!TextUtils.isEmpty(bean.getReward())) {
            holder.tvReward.setText(addconnent(bean.getReward(), 2));
        } else {
            holder.tvReward.setText("");
        }

        if (bean.getCheckStatus() == -1) {//审核之前

            changeStatueLlBottomAndTvStatue(0, holder);
            //显示倒计时f
            if (bean.getTime() > 0) {
                holder.rewardUpCardLlTime.initView(getShowPosition(position),
                        holder.tvStatuExplian,
                        holder.llBottm,
                        holder.ivBottomImg,
                        holder.tvBottomStatu, mData);
                holder.rewardUpCardLlTime.setVisibility(View.VISIBLE);
            } else {
                changeStatueLlBottomAndTvStatue(1, holder);
            }

        } else if (bean.getCheckStatus() == 0) {//审核进行中

            changeStatueLlBottomAndTvStatue(1, holder);

        } else if (bean.getCheckStatus() == 1) {//审核结束
            holder.rewardUpCardLlTime.setVisibility(View.GONE);

            //显示底部橘色状态的字
            if (bean.getGradeList() == null || bean.getGradeList().isEmpty()) {//当这个字段为空时，就说明无人揭榜成功
                holder.tvBottomStatu.setText(mContext.getResources().getString(R.string.no_brought_the_bounty));
                holder.ivBottomImg.setImageResource(R.drawable.wanted_fight);
                if (bean.getType() == 1) {
                    holder.tvBottomStatu.setText(mContext.getResources().getString(R.string.look_at_list));
                    holder.ivBottomImg.setImageResource(R.drawable.wanted_list);
                }
            } else {
                switch (bean.getType()) {
                    case 1://普通
                        holder.tvBottomStatu.setText(mContext.getResources().getString(R.string.look_at_list));
                        holder.ivBottomImg.setImageResource(R.drawable.wanted_list);
                        break;
                    case 2://独占鳌头
                        showType2(bean, holder);
                        break;
                    case 3://排行榜
                        holder.ivBottomImg.setImageResource(R.drawable.wanted_rank);
                        holder.tvBottomStatu.setText(mContext.getResources().getString(R.string.look_at_ranking_list));
                        break;
                }
            }

            //显示橘色上面的状态文字
            holder.tvStatuExplian.setTextColor(mContext.getResources().getColor(R.color.card_text_color));
            holder.tvStatuExplian.setTextSize(13);
            if (WangYuApplication.getUser(mContext) == null) {
                statuExplian = mContext.getResources().getString(R.string.overed);
            } else {
                switch (bean.getStatus()) {
                    case -1://用户未参与
                        statuExplian = mContext.getResources().getString(R.string.overed);
                        break;
                    case 0://0-用户未获奖
                        statuExplian = mContext.getResources().getString(R.string.no_reward);
                        break;
                    case 1://1-奖品发放成功
                        holder.tvStatuExplian.setTextColor(mContext.getResources().getColor(R.color.colorActionBarSelected));
                        statuExplian = mContext.getResources().getString(R.string.reward_is_successFul);
                        holder.ivCardStatue.setVisibility(View.VISIBLE);
                        holder.tvCardState.setVisibility(View.GONE);
                        break;
                    case 2://2-奖品非网娱自有且用户未填写信息
                        statuExplian = mContext.getResources().getString(R.string.reward_is_haved);
                        holder.tvCardState.setVisibility(View.VISIBLE);
                        holder.ivCardStatue.setVisibility(View.VISIBLE);
                        break;
                    case 3://3-奖品非网娱自有商品且用户填写信息
                        holder.tvStatuExplian.setTextColor(mContext.getResources().getColor(R.color.colorActionBarSelected));
                        statuExplian = mContext.getResources().getString(R.string.reward_is_exchanging);//正在兑换中
                        break;
                    case 5://5-发放奖品异常
                        break;
                }
            }
            holder.tvStatuExplian.setText(statuExplian);
        }

        MyOnClick myOnClick = new MyOnClick(bean, holder);
        holder.llBottm.setOnClickListener(myOnClick);
        holder.ivShare.setOnClickListener(myOnClick);
        holder.ivRuleOne.setOnClickListener(myOnClick);
        holder.llCardStatue.setOnClickListener(myOnClick);
    }

    private class MyOnClick implements View.OnClickListener {
        Reward reward;
        ViewHolder holder;

        public MyOnClick(Reward reward, ViewHolder holder) {
            this.reward = reward;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.rewardUpCardLlBottom://底部橘色按钮
                    if (reward.getCheckStatus() == -1) {//审核之前，可以提交成绩
                        if (WangYuApplication.getUser(mContext) == null) {
                            intent = new Intent(mContext, LoginActivity.class);
                            mContext.startActivity(intent);
                        } else {
                            intent = new Intent(mContext, SubmitGradesActivity.class);
                            intent.putExtra("bountyId", reward.getId() + "");
                            mContext.startActivity(intent);
                        }
                    } else if (reward.getCheckStatus() == 1) {//已结束
                        if (reward.getGradeList() != null && !reward.getGradeList().isEmpty()) {//只有当有用户时，才会有点击事件
                            if (reward.getType() == 3) {//排行榜,显示listview
                                if (reward.isShowListView()) {
                                    hideListView(holder, reward);
                                    observerable.notifyChange(Observerable.ObserverableType.REWARD_COMMENT, 9);
                                } else {
                                    showListView(holder, reward);
                                    observerable.notifyChange(Observerable.ObserverableType.REWARD_COMMENT, 8);
                                }
                            }
                        } else {
                            if (reward.getType() == 1) {//普通，跳转到得奖名单
                                intent = new Intent(mContext, RewardThePrizeActivity.class);
                                intent.putExtra("bountyId", reward.getId() + "");
                                mContext.startActivity(intent);
                            }
                        }
                    }
                    break;
                case R.id.rewardUpCardIvShare://分享
                    observerable.notifyChange(Observerable.ObserverableType.REWARD_COMMENT, 4);
                    break;
                case R.id.rewardUpCardIvRuleOne://规则
                    observerable.notifyChange(Observerable.ObserverableType.REWARD_COMMENT, 3);
                    break;
                case R.id.rewardUpCardLlCardStatu://底部橘色上面的点击状态
                    if (reward.getCheckStatus() == 1 && reward.getIs_win() == 1) {
                        intent = new Intent(mContext, RewardDetailActivity.class);
                        intent.putExtra("bountyId", reward.getId() + "");
                        mContext.startActivity(intent);
                    }
                    break;
            }
        }
    }

    /**
     * 初始化卡片状态
     *
     * @param holder
     */
    private void initStatus(ViewHolder holder, Reward bean) {
        //规则下的那个图片进行摇摆
        holder.ivRuleTwo.startAnimation(animation);

        holder.ivCardStatue.setVisibility(View.GONE);
        holder.tvCardState.setVisibility(View.GONE);
        holder.llCardStatue.setEnabled(true);
//        holder.bottomViewStub.setVisibility(View.GONE);
        holder.tvBottomStatu.setVisibility(View.VISIBLE);
        holder.ivBottomImg.setVisibility(View.VISIBLE);
        holder.llBottm.setBackgroundResource(R.drawable.shape_orange_bg_corner);
        holder.llBottm.setEnabled(true);
    }

    /**
     * 当悬赏令结束，且得奖为一个时，底部显示得奖者头像等等
     *
     * @param bean
     */
    private void showType2(Reward bean, ViewHolder holder) {

        if (bean.getGradeList() == null || bean.getGradeList().isEmpty()) {
            return;
        }

        holder.tvBottomStatu.setVisibility(View.GONE);
        holder.ivBottomImg.setVisibility(View.GONE);
//        holder.bottomViewStub.setVisibility(View.VISIBLE);

        holder.bottomViewStub.setLayoutResource(R.layout.layout_reward_up_page_cardview_item_bottom_one);
        View view = holder.bottomViewStub.inflate();
        CircleImageView ivHeadIcon = (CircleImageView) view.findViewById(R.id.rewardUpCardIvBottomHeadIcon);
        TextView tvSee = (TextView) view.findViewById(R.id.rewardUpCardTvBottomSee);
        TextView tvName = (TextView) view.findViewById(R.id.rewardUpCardTvBottomName);

        final RewardGrade rewardGrade = bean.getGradeList().get(0);
        //显示名称
        if (!TextUtils.isEmpty(rewardGrade.getNickname())) {
            tvName.setText(rewardGrade.getNickname());
        } else {
            tvName.setText("");
        }

        AsyncImage.loadAvatar(mContext, HttpConstant.SERVICE_UPLOAD_AREA + rewardGrade.getIcon() + "!small", ivHeadIcon);

        tvSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Map<String, String>> imgs = new ArrayList<Map<String, String>>();
                Map<String, String> map = new HashMap<String, String>();
                map.put("url", rewardGrade.getImg());
                imgs.add(map);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", imgs);
                bundle.putInt("isHideGallery", 1);
                intent.putExtras(bundle);
                intent.setClass(mContext, ImagePagerActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    /**
     * 显示排行榜的排名
     *
     * @param holder
     * @param reward
     */
    private void showListView(final ViewHolder holder, Reward reward) {
        reward.setShowListView(true);
        RewardCardRankingListAdapter adapter = new RewardCardRankingListAdapter(mContext, reward.getGradeList());
        holder.listView.setAdapter(adapter);
        holder.listView.startAnimation(inAnimation);
        inAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                holder.listView.setVisibility(View.VISIBLE);
                holder.ivBottomImg.setVisibility(View.GONE);
                holder.tvBottomStatu.setVisibility(View.GONE);
                holder.tvBottomStatuSee.setVisibility(View.VISIBLE);
//                holder.tvBottomStatu.setText(mContext.getResources().getString(R.string.pack_up));
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 隐藏排行榜的排名
     *
     * @param holder
     * @param reward
     */
    private void hideListView(final ViewHolder holder, Reward reward) {
        reward.setShowListView(false);
        holder.listView.setVisibility(View.GONE);
        holder.listView.startAnimation(outAnimation);
        outAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                holder.ivBottomImg.setVisibility(View.VISIBLE);
                holder.tvBottomStatu.setVisibility(View.VISIBLE);
//                holder.tvBottomStatu.setText(mContext.getResources().getString(R.string.look_at_ranking_list));
//                holder.ivBottomImg.setImageResource(R.drawable.wanted_rank);
                holder.tvBottomStatuSee.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                holder.listView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 判断该显示哪条数据
     *
     * @param position
     * @return
     */
    private int getShowPosition(int position) {
        int showPosition = 0;
        if (position > totalPosition) {//当position大于total判断一下情况
            if (total < 3) {
                if (position > 2) {//当数据个数小于3个且position大于2.返回相应的数据位置
                    showPosition = (position) % total;
                } else {//当数据个数小于3个且position小于等于2.返回0
                    showPosition = 0;
                }
            } else {//当数据个数大于3个且position大于2.返回相应的数据位置
                showPosition = position % total;
            }
        } else {
            showPosition = position;
        }
        return showPosition;
    }

    /**
     * @param cotent1
     * @param type    1表示目标，2表示奖励
     * @return
     */
    private SpannableStringBuilder addconnent(String cotent1, int type) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(cotent1 + " ");
        if (type == 1) {
            ssb.append(spanStr);
            ssb.append(" ");
            ssb.append(spanStr1);
            ssb.append(" ");
            ssb.append(spanStr2);
        } else if (type == 2) {
            ssb.append(spanStr3);
            ssb.append(" ");
            ssb.append(spanStr4);
            ssb.append(" ");
            ssb.append(spanStr5);
        }
        return ssb;
    }

    /**
     * 改变底部橘色状态以橘色部分上面的状态
     *
     * @param i      1表示底部橘色不可点击且置灰  上面文字显示正在审核中；2表示揭榜可点击，上面文字显示剩余时间
     * @param holder
     */
    private void changeStatueLlBottomAndTvStatue(int i, ViewHolder holder) {
        holder.tvBottomStatu.setText(mContext.getResources().getString(R.string.brought_the_bounty));
        holder.ivBottomImg.setImageResource(R.drawable.wanted_fight);
        if (i == 1) {
            holder.rewardUpCardLlTime.setVisibility(View.GONE);
            holder.tvStatuExplian.setTextColor(mContext.getResources().getColor(R.color.card_text_color));
            holder.tvStatuExplian.setTextSize(13);
            holder.tvStatuExplian.setText(mContext.getResources().getString(R.string.is_under_review));
            holder.llBottm.setBackgroundResource(R.drawable.shape_conners_4_solid_cdcdcd);
            holder.llBottm.setEnabled(false);
            holder.ivBottomImg.setImageResource(R.drawable.wanted_fight02);
            holder.tvBottomStatu.setTextColor(mContext.getResources().getColor(R.color.shop_splite_line));
        } else if (i == 0) {
            //显示橘色上面的状态文字
            holder.tvStatuExplian.setTextColor(mContext.getResources().getColor(R.color.card_text_color_9d7a4c));
            holder.tvStatuExplian.setTextSize(12);
            holder.tvStatuExplian.setText(mContext.getResources().getString(R.string.surplus_time));
        }
    }

    VerticalImageSpan span;
    VerticalImageSpan span1;
    VerticalImageSpan span2;
    SpannableString spanStr;
    SpannableString spanStr1;
    SpannableString spanStr2;
    VerticalImageSpan span3;
    VerticalImageSpan span4;
    VerticalImageSpan span5;
    SpannableString spanStr3;
    SpannableString spanStr4;
    SpannableString spanStr5;

    private void init() {
        inAnimation = AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_in);
        outAnimation = AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_out);
        animation = AnimationUtils.loadAnimation(mContext, R.anim.rotate);

        span = new VerticalImageSpan(mContext, R.drawable.wanted_skeleton);
        span1 = new VerticalImageSpan(mContext, R.drawable.wanted_skeleton);
        span2 = new VerticalImageSpan(mContext, R.drawable.wanted_skeleton);
        spanStr = new SpannableString("p");
        spanStr1 = new SpannableString("p");
        spanStr2 = new SpannableString("p");


        spanStr.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spanStr1.setSpan(span1, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spanStr2.setSpan(span2, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);


        span3 = new VerticalImageSpan(mContext, R.drawable.wanted_present);
        span4 = new VerticalImageSpan(mContext, R.drawable.wanted_present);
        span5 = new VerticalImageSpan(mContext, R.drawable.wanted_present);
        spanStr3 = new SpannableString("p");
        spanStr4 = new SpannableString("p");
        spanStr5 = new SpannableString("p");
        spanStr3.setSpan(span3, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spanStr4.setSpan(span4, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spanStr5.setSpan(span5, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    class ViewHolder {
        @Bind(R.id.rewardUpCardIvRuleOne)
        ImageView ivRuleOne;//规则
        @Bind(R.id.rewardUpCardIvShare)
        ImageView ivShare;//分享
        @Bind(R.id.rewardUpCardIvRuleTwo)
        ImageView ivRuleTwo;//挂坠。需要摇摆
        @Bind(R.id.rewardUpCardIvCardImg)
        ImageView ivCardImg;//卡片内的图片
        @Bind(R.id.rewardUpCardIvPass)
        ImageView ivPass;//是否通过的图片
        @Bind(R.id.rewardUpCardTvGold)
        TextView tvGold;//悬赏令目标
        @Bind(R.id.rewardUpCardTvReward)
        TextView tvReward;//悬赏令的奖励

        @Bind(R.id.rewardUpCardLlCardStatu)
        LinearLayout llCardStatue;
        @Bind(R.id.rewardUpCardTvCardStatuExplain)
        TextView tvStatuExplian;
        @Bind(R.id.rewardUpCardIvCardStatuImage)
        LinearLayout ivCardStatue;
        @Bind(R.id.rewardUpCardTvCardState)
        TextView tvCardState;

        @Bind(R.id.rewardUpCardLlBottom)
        LinearLayout llBottm;
        @Bind(R.id.rewardUpCardIvBottomImg)
        ImageView ivBottomImg;//底部该显示的图片
        @Bind(R.id.rewardUpCardTvBottomStatu)
        TextView tvBottomStatu;//底部该显示的字体
        @Bind(R.id.rewardUpCardTvBottomStatuSee)
        TextView tvBottomStatuSee;//底部该显示的字体
        @Bind(R.id.rewardUpCardBottomViewStub)
        ViewStub bottomViewStub;//底部的
        @Bind(R.id.rewardUpCardLlTime)
        RewardTimeTaskLinearLayout rewardUpCardLlTime;//时间

        @Bind(R.id.rewardUpCardItemLv)
        ListView listView;

//        @Bind(R.id.rewardUpCardIvBottomHeadIcon)
//        CircleImageView ivHeadIcon;
//        @Bind(R.id.rewardUpCardTvBottomSee)
//        TextView tvSee;
//        @Bind(R.id.rewardUpCardTvBottomName)
//        TextView tvName;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
