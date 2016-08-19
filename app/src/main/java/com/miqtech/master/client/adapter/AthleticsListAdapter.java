package com.miqtech.master.client.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.RecommendInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;

import java.util.List;

/**
 * Created by Administrator on 2015/11/25 0025.
 */
public class AthleticsListAdapter extends BaseAdapter {
    private Context context;
    private List<RecommendInfo> matchs;

    public AthleticsListAdapter(Context context, List<RecommendInfo> matchs) {
        this.context = context;
        this.matchs = matchs;
    }

    @Override
    public int getCount() {
        return matchs.size();
    }

    @Override
    public Object getItem(int position) {
        return matchs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.layout_newactivity_item, null);
            holder.tvType = (TextView) view.findViewById(R.id.tvType);
            holder.ivImg = (ImageView) view.findViewById(R.id.ivImg);
            holder.rlGameStatus = (RelativeLayout) view.findViewById(R.id.rlGameStatus);
            holder.tvGameStatus = (TextView) view.findViewById(R.id.tvGameStatus);
            holder.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            holder.tvBeginTime = (TextView) view.findViewById(R.id.tvBeginTime);
            holder.tvHasApplyNum = (TextView) view.findViewById(R.id.tvHasApplyNum);
            holder.tvSummary = (TextView) view.findViewById(R.id.tvSummary);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        RecommendInfo match = matchs.get(position);
        AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + match.getIcon(), holder.ivImg);
        if (match.getWay() == 1) {
            holder.tvType.setText("官方活动/线下");
        } else if (match.getWay() == 2) {
            holder.tvType.setText("官方活动/线上");
        }
        holder.rlGameStatus.setVisibility(View.VISIBLE);

        //状态：1-报名中；2-报名预热中；3-报名已截止;4-赛事已结束5赛事进行中
        if (match.getStatus() == 1) {
            holder.tvGameStatus.setText("进行中");
        } else if (match.getStatus() == 2) {
            holder.tvGameStatus.setText("未开始");
        } else if (match.getStatus() == 3) {
            holder.tvGameStatus.setText("截止");
        } else if (match.getStatus() == 4) {
            holder.tvGameStatus.setText("已结束");
        } else if (match.getStatus() == 5) {
            holder.tvGameStatus.setText("进行中");
        }
        holder.tvTitle.setText(match.getTitle());
        holder.tvBeginTime.setText(DateUtil.dateToStr(match.getStart_time()));
        holder.tvHasApplyNum.setText(match.getApplyNum());
        holder.tvSummary.setText(match.getSummary());
        return view;
    }

    private class ViewHolder {
        TextView tvType;
        ImageView ivImg;
        RelativeLayout rlGameStatus;
        TextView tvGameStatus;
        TextView tvTitle;
        TextView tvHasApplyNum;
        TextView tvBeginTime;
        TextView tvSummary;
    }
}
