package com.miqtech.master.client.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.HeadLine;
import com.miqtech.master.client.entity.HomePageRecommendAndBanner;
import com.miqtech.master.client.entity.MatchV2;
import com.miqtech.master.client.entity.MyMatch;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.EventDetailActivity;
import com.miqtech.master.client.ui.InformationDetailActivity;
import com.miqtech.master.client.ui.OfficalEventActivity;
import com.miqtech.master.client.ui.RewardActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CircleImageView;
import com.miqtech.master.client.view.CornerProgressView;
import com.miqtech.master.client.view.HeadLinesView;
import com.miqtech.master.client.view.RushBuyCountDownTimerView;
import com.miqtech.master.client.view.VerticalGestureDetector;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wuxn on 2016/7/25.
 */
public class HomePageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //banner 头条
    private final static int BANNER_MYMATCH_TYPE = 0;
    //悬赏令
    private final static int REWARD_TYPE = 1;
    //自发赛
    private final static int RELEASE_BY_SELF_TYPE = 2;
    //官方赛
    private final static int OFFICIAL_MATCH_TYPE = 3;
    //全部赛事
    private final static int ALL_MATCH_TYPE = 4;

    private List<MatchV2> matches;

    private LayoutInflater inflater;

    private HomePageRecommendAndBanner homePageRecommendAndBanner;

    private Context context;

    public boolean bannerRefresh = true;

    private HomePageOnClickListener listener;

    public HomePageAdapter(Context context, List<MatchV2> matches, HomePageRecommendAndBanner homePageRecommendAndBanner, HomePageOnClickListener listener) {
        this.context = context;
        this.matches = matches;
        this.inflater = LayoutInflater.from(context);
        this.homePageRecommendAndBanner = homePageRecommendAndBanner;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view;
        switch (viewType) {
            case BANNER_MYMATCH_TYPE:
                holder = new BannerAndTopAndMyMatchViewHolder(inflater.inflate(R.layout.layout_homepage_match_banner_item, parent, false));
                break;
            case ALL_MATCH_TYPE:
                holder = new AllMatchViewHolder(inflater.inflate(R.layout.layout_homepage_allmatch_item, parent, false));
                break;
            case REWARD_TYPE:
                holder = new RewardMatchViewHolder(inflater.inflate(R.layout.layout_rewardmatch_item, parent, false));
                break;
            case RELEASE_BY_SELF_TYPE:
                holder = new ReleaseByShelfMatchViewHolder(inflater.inflate(R.layout.layout_match_spontaneous_item, parent, false));
                break;
            case OFFICIAL_MATCH_TYPE:
                holder = new MatchOfficialViewHolder(inflater.inflate(R.layout.layout_match_official_item, parent, false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BannerAndTopAndMyMatchViewHolder) {
            setBannerAndMyMatchViews(holder);
        } else if (holder instanceof RewardMatchViewHolder) {
            setRewardView(holder, position);
        } else if (holder instanceof MatchOfficialViewHolder) {
            setMatchOfficialView(holder, position);
        } else if (holder instanceof ReleaseByShelfMatchViewHolder) {
            setReleaseBySelfView(holder, position);
        } else if (holder instanceof AllMatchViewHolder) {
            setAllMatchView(holder);
        }
    }

    public void setHomePageRecommendAndBanner(HomePageRecommendAndBanner homePageRecommendAndBanner) {
        this.homePageRecommendAndBanner = homePageRecommendAndBanner;
    }


    //  设置banner和我的赛事，电竞头条
    private void setBannerAndMyMatchViews(RecyclerView.ViewHolder holder) {
        BannerAndTopAndMyMatchViewHolder mHolder = (BannerAndTopAndMyMatchViewHolder) holder;
        if (homePageRecommendAndBanner != null) {
            if (bannerRefresh) {
                final MyMatch myMatch = homePageRecommendAndBanner.getMyMatch();
                List<HeadLine> headLines = homePageRecommendAndBanner.getHeadlines();
                if (headLines != null && headLines.size() > 0) {
                    View view = inflater.inflate(R.layout.flipp_layout, null);
                    final ViewFlipper viewFlipper = (ViewFlipper) view.findViewById(R.id.homepage_notice_vf);
                    mHolder.frameLayout.addView(view);
                    addFilView(headLines, viewFlipper);
                    viewFlipper.setInAnimation(context, R.anim.in_bottomtop);
                    viewFlipper.setOutAnimation(context, R.anim.out_bottomtop);
                    viewFlipper.setFlipInterval(2000);
                    viewFlipper.startFlipping();
                    mHolder.gesture.setOnGesture(new VerticalGestureDetector.OnGesture() {
                        @Override
                        public void up() {
                            viewFlipper.stopFlipping();
                            viewFlipper.setInAnimation(context, R.anim.in_bottomtop);
                            viewFlipper.setOutAnimation(context, R.anim.out_bottomtop);
                            viewFlipper.showNext();
                            viewFlipper.startFlipping();
                        }

                        @Override
                        public void down() {
                            viewFlipper.stopFlipping();
                            viewFlipper.setInAnimation(context, R.anim.in_topbottom);
                            viewFlipper.setOutAnimation(context, R.anim.out_topbottom);
                            viewFlipper.showPrevious();
                            viewFlipper.startFlipping();
                        }

                        @Override
                        public void left() {

                        }

                        @Override
                        public void right() {

                        }
                    });
                }
                if (myMatch == null) {
                    mHolder.llMyMatch.setVisibility(View.GONE);
                } else {
                    mHolder.tvTip.setText(myMatch.getTip());
                    setCountDown(mHolder.tvDay, mHolder.timeView, myMatch.getTime());
                    mHolder.tvMatchTitle.setText(myMatch.getTitle());
                    mHolder.tvMatchTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //1官方赛2自发赛
                            int matchType = myMatch.getType();
                            Intent intent = new Intent();
                            if (matchType == 1) {
                                intent.setClass(context, OfficalEventActivity.class);
                                intent.putExtra("matchId",myMatch.getId()+"");
                            } else if (matchType == 2) {
                                intent.setClass(context, EventDetailActivity.class);
                                intent.putExtra("matchId",myMatch.getId());
                            }
                            context.startActivity(intent);
                        }
                    });
                    mHolder.timeView.start();
                }
                mHolder.rlMyMatch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.myMatchOnClick();
                    }
                });
                mHolder.homePageBanner.refreshData(homePageRecommendAndBanner.getBanner());
                //防止再次加载
                bannerRefresh = false;
            }
        }
    }

    private void setCountDown(TextView tvDays, RushBuyCountDownTimerView timeView, long time) {
        long days = time / (1000 * 60 * 60 * 24);
        long hours = (time % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (time % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (time % (1000 * 60)) / 1000;
        if (days != 0) {
            tvDays.setVisibility(View.VISIBLE);
            tvDays.setText(((int) days) + "天");
        } else {
            tvDays.setVisibility(View.GONE);
        }
        timeView.setTime((int) hours, (int) minutes, (int) seconds);
    }

    public void setAllMatchView(RecyclerView.ViewHolder holder) {
        AllMatchViewHolder mHolder = (AllMatchViewHolder) holder;
        mHolder.ivType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.allMatchOnClick();
            }
        });
    }

    private void addFilView(List<HeadLine> headLines, ViewFlipper viewFlipper) {
        int count = headLines.size();
        for (int i = 0; i < count; i++) {
            final HeadLine headLine = headLines.get(i);
            View noticeView = inflater.inflate(R.layout.notice_item,
                    null);
            TextView tvNotice = (TextView) noticeView.findViewById(R.id.notice_tv);
            tvNotice.setText(headLines.get(i).getTitle());
            tvNotice.setTag(headLine);
            tvNotice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Log.e("HeadLine", arg0.getTag().toString());
                    Intent intent = new Intent();

                    intent.setClass(context, InformationDetailActivity.class);
                    intent.putExtra("id", headLine.getId() + "");
                    context.startActivity(intent);
                }
            });
            viewFlipper.addView(noticeView);
        }
    }

    //设置悬赏令
    private void setRewardView(RecyclerView.ViewHolder holder, int position) {
        RewardMatchViewHolder mHolder = (RewardMatchViewHolder) holder;
        final MatchV2 rewardMatch = matches.get(position - 2);
        if (rewardMatch != null) {
            mHolder.tvRewardTitle.setText(rewardMatch.getTitle());
            mHolder.tvCountDown.setText(DateUtil.secToTime((int)(rewardMatch.getCount_down() / 1000)));
            mHolder.tvTarget.setText(rewardMatch.getTarget());
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + rewardMatch.getIcon(), mHolder.ivMatchImg);
            mHolder.llReward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.rewardMatchOnClick(rewardMatch.getId(), rewardMatch.getState() + "");
                }
            });
            int state = rewardMatch.getState();
            if (state == 0) {
                mHolder.ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.match_warmup));
            } else if (state == 1) {
                mHolder.ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.match_registration));
            } else if (state == 2) {
                mHolder.ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.match_doing));
            } else if (state == 3) {
                mHolder.ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.match_state_end));
            }
            String peopleNumStr = rewardMatch.getApplyNum() + "人正在抢榜";
            SpannableStringBuilder builder = new SpannableStringBuilder(peopleNumStr);
            ForegroundColorSpan orangeSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.orange));
            ForegroundColorSpan graySpan = new ForegroundColorSpan(context.getResources().getColor(R.color.c7_gray));
            builder.setSpan(orangeSpan, 0, (rewardMatch.getApplyNum() + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(graySpan, (rewardMatch.getApplyNum() + "").length(), peopleNumStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mHolder.tvPeopleNum.setText(builder);
        }
    }

    //设置自发赛
    private void setReleaseBySelfView(RecyclerView.ViewHolder holder, int position) {
        ReleaseByShelfMatchViewHolder mHolder = (ReleaseByShelfMatchViewHolder) holder;
        final MatchV2 mMatch = matches.get(position - 2);
        if (mMatch != null) {
            mHolder.ivHead.setPadding(Utils.dp2px(2), 0, Utils.dp2px(2), 0);
            mHolder.ivHead.setBorderColor(Color.WHITE);
            mHolder.ivHead.setBorderWidth(Utils.dp2px(3));
            AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + mMatch.getSponsor_icon(), mHolder.ivHead);
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + mMatch.getIcon(), mHolder.ivImg);
            mHolder.tvName.setText(mMatch.getSponsor());
            if (TextUtils.isEmpty(mMatch.getApply_begin()) && TextUtils.isEmpty(mMatch.getApply_end())) {
                mHolder.tvCountDown.setText("不允许报名");
            } else {
                mHolder.tvCountDown.setText(DateUtil.dateToStrLong(mMatch.getApply_begin()) + " - " + DateUtil.dateToStrLong(mMatch.getApply_end()));
            }
            mHolder.cpView.setMaxCount(mMatch.getMax_num());
            mHolder.cpView.setCurrentCount(mMatch.getApplyNum());
            mHolder.tvHasApplyNum.setText(mMatch.getApplyNum() + "/" + mMatch.getMax_num());
            mHolder.llReleaseMatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.releaseBySelfMatchOnClick(mMatch.getId());
                }
            });
            int state = mMatch.getState();
            if (state == 0) {
                mHolder.ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.match_warmup));
            } else if (state == 1) {
                mHolder.ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.match_registration));
            } else if (state == 2) {
                mHolder.ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.match_doing));
            } else if (state == 3) {
                mHolder.ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.match_state_end));
            }

            if (!TextUtils.isEmpty(mMatch.getItem_name())) {
                mHolder.tvGameName.setVisibility(View.VISIBLE);
                mHolder.tvGameName.setText(mMatch.getItem_name());
            } else {
                mHolder.tvGameName.setVisibility(View.GONE);
            }
            //1-线上赛事,2-线下赛事,3-线上海选+线下决赛
            if (mMatch.getMode() == 1) {
                mHolder.tvMode.setText("线上赛事");
                mHolder.tvMode.setVisibility(View.VISIBLE);
            } else if (mMatch.getMode() == 2) {
                mHolder.tvMode.setText("线下赛事");
                mHolder.tvMode.setVisibility(View.VISIBLE);
            } else if (mMatch.getMode() == 3) {
                mHolder.tvMode.setText("线上海选+线下决赛");
                mHolder.tvMode.setVisibility(View.VISIBLE);
            } else {
                mHolder.tvMode.setVisibility(View.GONE);
            }
            //赛制:1-单败淘汰制,2-双败淘汰制,3-小组内单循环制,4-积分循环制
            if (mMatch.getRegime() == 1) {
                mHolder.tvRegime.setText("单败淘汰制");
                mHolder.tvRegime.setVisibility(View.VISIBLE);
            } else if (mMatch.getRegime() == 2) {
                mHolder.tvRegime.setText("双败淘汰制");
                mHolder.tvRegime.setVisibility(View.VISIBLE);
            } else if (mMatch.getRegime() == 3) {
                mHolder.tvRegime.setText("小组内单循环制");
                mHolder.tvRegime.setVisibility(View.VISIBLE);
            } else if (mMatch.getRegime() == 4) {
                mHolder.tvRegime.setText("积分循环制");
                mHolder.tvRegime.setVisibility(View.VISIBLE);
            }
            mHolder.llReleaseMatchContent.setVisibility(View.VISIBLE);
            mHolder.tvTitle.setText(mMatch.getTitle());
            mHolder.tvStartTime.setText(DateUtil.dateToStrPoint(mMatch.getStart_time()));
        }
    }

    //设置官方赛
    private void setMatchOfficialView(RecyclerView.ViewHolder holder, int position) {
        final MatchOfficialViewHolder mHolder = (MatchOfficialViewHolder) holder;
        final MatchV2 mMatch = matches.get(position - 2);
        if (mMatch != null) {
            mHolder.tvName.setText(mMatch.getSponsor());
            mHolder.ivHead.setPadding(Utils.dp2px(2), 0, Utils.dp2px(2), 0);
            mHolder.ivHead.setBorderColor(Color.WHITE);
            mHolder.ivHead.setBorderWidth(Utils.dp2px(3));
            AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + mMatch.getSponsor_icon(), mHolder.ivHead);
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + mMatch.getIcon(), mHolder.ivImg);
            mHolder.tvTime.setText(DateUtil.dateToStrPoint(mMatch.getApply_begin()) + " - " + DateUtil.dateToStrPoint(mMatch.getApply_end()));
            mHolder.tvContent.setText(mMatch.getSummary());
            mHolder.ivSpontaneousBg.setImageDrawable(context.getResources().getDrawable(R.drawable.match_blue_bg));
            mHolder.tvMatchName.setText("官方赛");
            mHolder.tvMatchTitle.setText(mMatch.getTitle());
            mHolder.llContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.officialMatchOnClick(mMatch.getId());
                }
            });
            int state = mMatch.getState();
            if (state == 0) {
                mHolder.ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.match_warmup));
            } else if (state == 1) {
                mHolder.ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.match_registration));
            } else if (state == 2) {
                mHolder.ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.match_doing));
            } else if (state == 3) {
                mHolder.ivStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.match_state_end));
            }
            final int currentPosition = position - 2;
            mHolder.rlOtherRoundStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mHolder.llTime.getVisibility() == View.VISIBLE) {
                        mHolder.llTime.setVisibility(View.GONE);
                        mHolder.ivArrows.setImageDrawable(context.getResources().getDrawable(R.drawable.nav_down));
                    } else {
                        mHolder.llTime.setVisibility(View.VISIBLE);
                        if (mMatch.getRounds() != null) {
                            ArrayList<MatchV2.RoundInfo> rounds = mMatch.getRounds();
                            for (int i = 0; i < rounds.size(); i++) {
                                MatchV2.RoundInfo round = rounds.get(i);
                                if (round.getState().equals("报名")) {
                                    mHolder.rlApply.setVisibility(View.VISIBLE);
                                    mHolder.tvApplyTime.setText(round.getDate() + "正在报名中");
                                } else if (round.getState().equals("进行")) {
                                    mHolder.rlDoing.setVisibility(View.VISIBLE);
                                    mHolder.tvDoingTime.setText(round.getDate() + "正在进行中");
                                } else if (round.getState().equals("预热")) {
                                    mHolder.rlWarmUp.setVisibility(View.VISIBLE);
                                    mHolder.tvWarmUpTime.setText(round.getDate() + "正在预热中");
                                }
                            }
                            mHolder.ivArrows.setImageDrawable(context.getResources().getDrawable(R.drawable.match_filter_up));
                        } else {
                            listener.officialMatchRoundInfoOnClick(currentPosition);
                        }
                    }
                }
            });
            String peopleNumStr = mMatch.getApplyNum() + "人报名";
            SpannableStringBuilder builder = new SpannableStringBuilder(peopleNumStr);
            ForegroundColorSpan orangeSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.orange));
            ForegroundColorSpan graySpan = new ForegroundColorSpan(context.getResources().getColor(R.color.c7_gray));
            builder.setSpan(orangeSpan, 0, (mMatch.getApplyNum() + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(graySpan, (mMatch.getApplyNum() + "").length(), peopleNumStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mHolder.tvHasApplyNum.setText(builder);
        }
    }


    @Override
    public int getItemCount() {
        if (matches != null && matches.size() > 0 && homePageRecommendAndBanner != null) {
            return 1 + 1 + matches.size();
        } else if (matches.size() == 0 && homePageRecommendAndBanner != null) {
            return 1;
        } else {
            return 0;
        }
        //       return matches.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return BANNER_MYMATCH_TYPE;
        } else {
            if (matches != null && matches.size() > 0 && position - 2 <= matches.size()) {
                if (position == 1) {
                    return ALL_MATCH_TYPE;
                } else {
                    MatchV2 match = matches.get(position - 2);
                    if (match != null) {
                        switch (match.getType()) {
                            case 1:
                                return OFFICIAL_MATCH_TYPE;
                            case 2:
                                return RELEASE_BY_SELF_TYPE;
                            case 3:
                                return REWARD_TYPE;
                        }
                    }
                }
            }

        }
        return super.getItemViewType(position);
    }

    public class BannerAndTopAndMyMatchViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.homePageBanner)
        public HeadLinesView homePageBanner;
        @Bind(R.id.timeView)
        public RushBuyCountDownTimerView timeView;
        @Bind(R.id.tvMoreMatch)
        public TextView tvMoreMatch;
        @Bind(R.id.tvMatchTitle)
        public TextView tvMatchTitle;
        @Bind(R.id.llMyMatch)
        public LinearLayout llMyMatch;
        @Bind(R.id.gesture)
        public VerticalGestureDetector gesture;
        @Bind(R.id.frameLayout)
        public FrameLayout frameLayout;
        @Bind(R.id.rlMyMatch)
        RelativeLayout rlMyMatch;
        @Bind(R.id.tvTip)
        TextView tvTip;
        @Bind(R.id.tvDay)
        TextView tvDay;


        public BannerAndTopAndMyMatchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class AllMatchViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivType)
        ImageView ivType;

        public AllMatchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class RewardMatchViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvRewardTitle)
        public TextView tvRewardTitle;
        @Bind(R.id.tvCountDown)
        public TextView tvCountDown;
        @Bind(R.id.ivStatus)
        public ImageView ivStatus;
        @Bind(R.id.tvTarget)
        public TextView tvTarget;
        @Bind(R.id.tvPeopleNum)
        public TextView tvPeopleNum;
        @Bind(R.id.ivMatchImg)
        public ImageView ivMatchImg;
        @Bind(R.id.ivRewardBg)
        public ImageView ivRewardBg;
        @Bind(R.id.llReward)
        public LinearLayout llReward;


        public RewardMatchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class MatchOfficialViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvName)
        public TextView tvName;
        @Bind(R.id.ivHead)
        public CircleImageView ivHead;
        @Bind(R.id.ivImg)
        public ImageView ivImg;
        @Bind(R.id.ivSpontaneousBg)
        public ImageView ivSpontaneousBg;
        @Bind(R.id.tvMatchName)
        public TextView tvMatchName;
        @Bind(R.id.tvMatchTitle)
        public TextView tvMatchTitle;
        @Bind(R.id.ivStatus)
        public ImageView ivStatus;
        @Bind(R.id.tvTime)
        public TextView tvTime;
        @Bind(R.id.tvHasApplyNum)
        public TextView tvHasApplyNum;
        @Bind(R.id.tvContent)
        public TextView tvContent;
        @Bind(R.id.llContent)
        public LinearLayout llContent;
        @Bind(R.id.rlOtherRoundStatus)
        public RelativeLayout rlOtherRoundStatus;
        @Bind(R.id.llTime)
        public LinearLayout llTime;
        @Bind(R.id.tvApplyTime)
        public TextView tvApplyTime;
        @Bind(R.id.tvDoingTime)
        public TextView tvDoingTime;
        @Bind(R.id.tvWarmUpTime)
        public TextView tvWarmUpTime;
        @Bind(R.id.rlApply)
        public RelativeLayout rlApply;
        @Bind(R.id.rlDoing)
        public RelativeLayout rlDoing;
        @Bind(R.id.rlWarmUp)
        public RelativeLayout rlWarmUp;
        @Bind(R.id.ivArrows)
        public ImageView ivArrows;


        public MatchOfficialViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ReleaseByShelfMatchViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvName)
        public TextView tvName;
        @Bind(R.id.ivHead)
        public CircleImageView ivHead;
        @Bind(R.id.ivImg)
        public ImageView ivImg;
        @Bind(R.id.ivSpontaneousBg)
        public ImageView ivSpontaneousBg;
        @Bind(R.id.tvMatchName)
        public TextView tvMatchName;
        @Bind(R.id.tvCountDown)
        public TextView tvCountDown;
        @Bind(R.id.ivStatus)
        public ImageView ivStatus;
        @Bind(R.id.tvHasApplyNum)
        public TextView tvHasApplyNum;
        @Bind(R.id.cpView)
        public CornerProgressView cpView;
        @Bind(R.id.llTags)
        public LinearLayout llTags;
        @Bind(R.id.llReleaseMatch)
        public LinearLayout llReleaseMatch;
        @Bind(R.id.tvGameName)
        TextView tvGameName;
        @Bind(R.id.tvMode)
        TextView tvMode;
        @Bind(R.id.tvRegime)
        TextView tvRegime;
        @Bind(R.id.llReleaseMatchContent)
        LinearLayout llReleaseMatchContent;
        @Bind(R.id.tvTitle)
        TextView tvTitle;
        @Bind(R.id.tvStartTime)
        TextView tvStartTime;

        public ReleaseByShelfMatchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface HomePageOnClickListener {

        public void myMatchOnClick();

        public void allMatchOnClick();

        public void headLineOnClick();

        public void officialMatchOnClick(int officialId);

        public void releaseBySelfMatchOnClick(int matchId);

        public void rewardMatchOnClick(int rewardId, String state);

        public void officialMatchRoundInfoOnClick(int position);
    }
}
