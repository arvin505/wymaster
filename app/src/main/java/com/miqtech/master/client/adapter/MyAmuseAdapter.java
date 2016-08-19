package com.miqtech.master.client.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.MatchV2;
import com.miqtech.master.client.entity.MyAmuse;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CircleImageView;
import com.miqtech.master.client.view.CornerProgressView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/14.
 */
public class MyAmuseAdapter extends BaseAdapter {
    private Context context;
    private List<MatchV2> myAmuseList;

    public MyAmuseAdapter(Context context, List<MatchV2> myAmuseList) {
        this.context = context;
        this.myAmuseList = myAmuseList;
    }


    @Override
    public int getCount() {
        return myAmuseList.size();
    }

    @Override
    public Object getItem(int position) {
        return myAmuseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReleaseByShelfMatchViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_match_spontaneous_item,parent,false);
            holder = new ReleaseByShelfMatchViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ReleaseByShelfMatchViewHolder) convertView.getTag();
        }


        return convertView;
    }

    public static class ReleaseByShelfMatchViewHolder extends RecyclerView.ViewHolder {
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

    //设置自发赛
    private void setReleaseBySelfView(RecyclerView.ViewHolder holder, int position) {
        ReleaseByShelfMatchViewHolder mHolder = (ReleaseByShelfMatchViewHolder) holder;
        final MatchV2 mMatch = myAmuseList.get(position);
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
}
