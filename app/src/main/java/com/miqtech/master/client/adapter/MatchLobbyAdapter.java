package com.miqtech.master.client.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.MatchV2;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CircleImageView;
import com.miqtech.master.client.view.CornerProgressView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/8/3.
 */
public class MatchLobbyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater inflater;
    private List<MatchV2> matches;
    private Context context;

    //悬赏令
    private final static int REWARD_TYPE = 1;
    //自发赛
    private final static int RELEASE_BY_SELF_TYPE = 2;
    //官方赛
    private final static int OFFICIAL_MATCH_TYPE = 3;

    private MatchLobbyItemOnClickListener listener;

    public MatchLobbyAdapter(Context context, List<MatchV2> matches, MatchLobbyItemOnClickListener listener) {
        this.context = context;
        this.matches = matches;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view;
        switch (viewType) {
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
        if (holder instanceof RewardMatchViewHolder) {
            setRewardView(holder, position);
        } else if (holder instanceof MatchOfficialViewHolder) {
            setMatchOfficialView(holder, position);
        } else if (holder instanceof ReleaseByShelfMatchViewHolder) {
            setReleaseBySelfView(holder, position);
        }
    }

    //设置悬赏令
    private void setRewardView(RecyclerView.ViewHolder holder, int position) {
        RewardMatchViewHolder mHolder = (RewardMatchViewHolder) holder;
        final MatchV2 rewardMatch = matches.get(position);
        if (rewardMatch != null) {
            mHolder.tvRewardTitle.setText(rewardMatch.getTitle());
            mHolder.tvCountDown.setText(DateUtil.secToTime((int)(rewardMatch.getCount_down()/1000)));
            mHolder.tvTarget.setText(rewardMatch.getTarget());
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + rewardMatch.getIcon(), mHolder.ivMatchImg);
            mHolder.llReward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.rewardMatchOnClick(rewardMatch.getId(), rewardMatch.getState());
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
        final MatchV2 mMatch = matches.get(position);
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
            mHolder.llReleaseMatchContent.setVisibility(View.VISIBLE);
            mHolder.tvTitle.setText(mMatch.getTitle());
            mHolder.tvStartTime.setText(DateUtil.dateToStrPoint(mMatch.getStart_time()));

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




        }
    }

    //设置官方赛
    private void setMatchOfficialView(RecyclerView.ViewHolder holder, final int position) {
        final MatchOfficialViewHolder mHolder = (MatchOfficialViewHolder) holder;
        final MatchV2 mMatch = matches.get(position);
        if (mMatch != null) {
            mHolder.tvName.setText(mMatch.getSponsor());
            mHolder.ivHead.setPadding(Utils.dp2px(2), 0, Utils.dp2px(2), 0);
            mHolder.ivHead.setBorderColor(Color.WHITE);
            mHolder.ivHead.setBorderWidth(Utils.dp2px(3));
            AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + mMatch.getSponsor_icon(), mHolder.ivHead);
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + mMatch.getIcon(), mHolder.ivImg);
            mHolder.tvTime.setText(DateUtil.dateToStrPoint(mMatch.getApply_begin()) + "-" + DateUtil.dateToStrPoint(mMatch.getApply_end()));
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
                                    mHolder.tvApplyTime.setText(round.getDate());
                                } else if (round.getState().equals("进行")) {
                                    mHolder.rlDoing.setVisibility(View.VISIBLE);
                                    mHolder.tvDoingTime.setText(round.getDate());
                                } else if (round.getState().equals("预热")) {
                                    mHolder.rlWarmUp.setVisibility(View.VISIBLE);
                                    mHolder.tvWarmUpTime.setText(round.getDate());
                                }
                            }
                            mHolder.ivArrows.setImageDrawable(context.getResources().getDrawable(R.drawable.match_filter_up));
                        } else {
                            listener.officialMatchRoundInfoOnClick(position);
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
    public int getItemViewType(int position) {
        MatchV2 match = matches.get(position);
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
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public class RewardMatchViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvRewardTitle)
        TextView tvRewardTitle;
        @Bind(R.id.tvCountDown)
        TextView tvCountDown;
        @Bind(R.id.ivStatus)
        ImageView ivStatus;
        @Bind(R.id.tvTarget)
        TextView tvTarget;
        @Bind(R.id.tvPeopleNum)
        TextView tvPeopleNum;
        @Bind(R.id.ivMatchImg)
        ImageView ivMatchImg;
        @Bind(R.id.ivRewardBg)
        public ImageView ivRewardBg;
        @Bind(R.id.llReward)
        LinearLayout llReward;


        public RewardMatchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class MatchOfficialViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvName)
        TextView tvName;
        @Bind(R.id.ivHead)
        CircleImageView ivHead;
        @Bind(R.id.ivImg)
        ImageView ivImg;
        @Bind(R.id.ivSpontaneousBg)
        ImageView ivSpontaneousBg;
        @Bind(R.id.tvMatchName)
        TextView tvMatchName;
        @Bind(R.id.tvMatchTitle)
        TextView tvMatchTitle;
        @Bind(R.id.ivStatus)
        ImageView ivStatus;
        @Bind(R.id.tvTime)
        TextView tvTime;
        @Bind(R.id.tvHasApplyNum)
        TextView tvHasApplyNum;
        @Bind(R.id.tvContent)
        TextView tvContent;
        @Bind(R.id.llContent)
        LinearLayout llContent;
        @Bind(R.id.rlOtherRoundStatus)
        RelativeLayout rlOtherRoundStatus;
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
        TextView tvName;
        @Bind(R.id.ivHead)
        CircleImageView ivHead;
        @Bind(R.id.ivImg)
        ImageView ivImg;
        @Bind(R.id.ivSpontaneousBg)
        ImageView ivSpontaneousBg;
        @Bind(R.id.tvMatchName)
        TextView tvMatchName;
        @Bind(R.id.tvCountDown)
        TextView tvCountDown;
        @Bind(R.id.ivStatus)
        ImageView ivStatus;
        @Bind(R.id.tvHasApplyNum)
        TextView tvHasApplyNum;
        @Bind(R.id.cpView)
        CornerProgressView cpView;
        @Bind(R.id.llTags)
        LinearLayout llTags;
        @Bind(R.id.llReleaseMatch)
        LinearLayout llReleaseMatch;
        @Bind(R.id.llReleaseMatchContent)
        LinearLayout llReleaseMatchContent;
        @Bind(R.id.tvTitle)
        TextView tvTitle;
        @Bind(R.id.tvStartTime)
        TextView tvStartTime;

        @Bind(R.id.tvGameName)
        TextView tvGameName;
        @Bind(R.id.tvMode)
        TextView tvMode;
        @Bind(R.id.tvRegime)
        TextView tvRegime;

        public ReleaseByShelfMatchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface MatchLobbyItemOnClickListener {


        public void officialMatchOnClick(int matchId);

        public void releaseBySelfMatchOnClick(int matchId);

        public void rewardMatchOnClick(int rewardId, int state);

        public void officialMatchRoundInfoOnClick(int position);
    }
}
