package com.miqtech.master.client.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.Reward;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.TimeFormatUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaoyi on 2016/8/3.
 */
public class MyRewardAdapter extends BaseAdapter {
    private List<Reward> mDatas;
    private Context mContext;
    private LayoutInflater mInflater;

    public MyRewardAdapter(List<Reward> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_rewardmatch_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Reward reward = mDatas.get(position);

        holder.tvTarget.setText(reward.getTarget());
        holder.tvRewardTitle.setText(reward.getTitle());

        SpannableStringBuilder builder = null;
        if (reward.getIsEnd() == 0) {
            holder.ivStatus.setImageResource(R.drawable.match_doing);
            builder = new SpannableStringBuilder(reward.getApplyNum() + "人正在抢榜");
            holder.tvTime.setText("倒计时：");
            holder.tvCountDown.setText(DateUtil.secToTime(reward.getCount_down()));
        } else if (reward.getIsEnd() == 1) {
            holder.ivStatus.setImageResource(R.drawable.match_end);
            builder = new SpannableStringBuilder(reward.getApplyNum() + "人抢榜");
            holder.tvTime.setText("结束时间：");
            holder.tvCountDown.setText(TimeFormatUtil.format(reward.getEnd_time(),"yyyy.MM.dd"));
            if (reward.getState().equals("1")) {
                holder.tvState.setVisibility(View.VISIBLE);
            } else {
                holder.tvState.setVisibility(View.GONE);
            }
        }
        builder.setSpan(new ForegroundColorSpan(mContext.getApplicationContext().getResources().getColor(R.color.light_orange)),
                0, builder.length() - (builder.toString().contains("正在") ? 5 : 3), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        holder.tvPeopleNum.setText(builder);
        AsyncImage.loadNetPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + reward.getIcon(), holder.ivMatchImg);
        if (reward.getIs_win() == 1) {
            holder.imgIsWin.setVisibility(View.VISIBLE);
        } else {
            holder.imgIsWin.setVisibility(View.GONE);
        }

        return convertView;
    }


    static class ViewHolder {
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
        ImageView ivRewardBg;
        @Bind(R.id.tvReward)
        TextView tvReward;
        @Bind(R.id.img_is_win)
        ImageView imgIsWin;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.tvState)
        TextView tvState;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
