package com.miqtech.master.client.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.MatchV2;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by wuxn on 2016/7/25.
 */
public class MyReleaseMatchAdapter extends BaseAdapter {

    private List<MatchV2> matches;

    private LayoutInflater inflater;



    public MyReleaseMatchAdapter(List<MatchV2> matches, Context context) {
        this.matches = matches;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    private Context context;

    @Override
    public int getCount() {
        return matches.size();
    }

    @Override
    public Object getItem(int position) {
        return matches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private MatctchItemClickListener listener;

    public void setListener(MatctchItemClickListener listener) {
        this.listener = listener;
    }

    public interface MatctchItemClickListener {


        public void officialMatchOnClick(int officialId);

        public void officialMatchRoundInfoOnClick(int position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MatchOfficialViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_match_official_item, parent, false);
            holder = new MatchOfficialViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (MatchOfficialViewHolder) convertView.getTag();
        }
        setMatchOfficialView(holder, position);
        return convertView;
    }

    //设置官方赛
    private void setMatchOfficialView(final MatchOfficialViewHolder mHolder, final int position) {

        final MatchV2 mMatch = matches.get(position);
        if (mMatch != null) {
            mHolder.tvName.setText(mMatch.getSponsor());
            mHolder.ivHead.setPadding(Utils.dp2px(2), 0, Utils.dp2px(2), 0);
            mHolder.ivHead.setBorderColor(Color.WHITE);
            mHolder.ivHead.setBorderWidth(Utils.dp2px(3));
            AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + mMatch.getSponsor_icon(), mHolder.ivHead);
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + mMatch.getIcon(), mHolder.ivImg);
            mHolder.tvTime.setText(DateUtil.dateToStrPoint(mMatch.getStart_time()) + " - " + DateUtil.dateToStrPoint(mMatch.getEnd_time()));
            mHolder.tvContent.setText(mMatch.getSummary()+"");
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
                            LogUtil.e("TAG","--positino---" + position);
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



    public static class MatchOfficialViewHolder {
        public TextView tvName;
        public CircleImageView ivHead;
        public ImageView ivImg;
        public ImageView ivSpontaneousBg;
        public TextView tvMatchName;
        public TextView tvMatchTitle;
        public ImageView ivStatus;
        public TextView tvTime;
        public TextView tvHasApplyNum;
        public TextView tvContent;
        public LinearLayout llContent;
        public RelativeLayout rlOtherRoundStatus;
        public LinearLayout llTime;
        public TextView tvApplyTime;
        public TextView tvDoingTime;
        public TextView tvWarmUpTime;
        public RelativeLayout rlApply;
        public RelativeLayout rlDoing;
        public RelativeLayout rlWarmUp;
        public ImageView ivArrows;

        public MatchOfficialViewHolder(View view){
            tvName = (TextView) view.findViewById(R.id.tvName);
            ivHead = (CircleImageView) view.findViewById(R.id.ivHead);
            ivImg = (ImageView) view.findViewById(R.id.ivImg);
            ivSpontaneousBg = (ImageView) view.findViewById(R.id.ivSpontaneousBg);
            tvMatchName = (TextView) view.findViewById(R.id.tvMatchName);
            tvMatchTitle = (TextView) view.findViewById(R.id.tvMatchTitle);
            ivStatus = (ImageView) view.findViewById(R.id.ivStatus);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            tvHasApplyNum = (TextView) view.findViewById(R.id.tvHasApplyNum);
            tvContent = (TextView) view.findViewById(R.id.tvContent);

            llContent = (LinearLayout) view.findViewById(R.id.llContent);
            rlOtherRoundStatus = (RelativeLayout) view.findViewById(R.id.rlOtherRoundStatus);
            tvDoingTime = (TextView) view.findViewById(R.id.tvDoingTime);
            llTime = (LinearLayout) view.findViewById(R.id.llTime);
            tvApplyTime = (TextView) view.findViewById(R.id.tvApplyTime);
            tvDoingTime = (TextView) view.findViewById(R.id.tvDoingTime);
            tvWarmUpTime = (TextView) view.findViewById(R.id.tvWarmUpTime);
            rlApply = (RelativeLayout) view.findViewById(R.id.rlApply);
            rlDoing = (RelativeLayout) view.findViewById(R.id.rlDoing);
            rlWarmUp = (RelativeLayout) view.findViewById(R.id.rlWarmUp);
            ivArrows = (ImageView) view.findViewById(R.id.ivArrows);
        }

    }


}

