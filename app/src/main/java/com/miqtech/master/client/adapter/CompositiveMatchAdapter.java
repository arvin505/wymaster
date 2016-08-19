package com.miqtech.master.client.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.CompositiveMatchInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.TimeFormatUtil;

import java.util.List;

/**
 * Created by Administrator on 2015/11/27.
 */
public class CompositiveMatchAdapter extends BaseAdapter {

    private Context context;
    private List<CompositiveMatchInfo> matchs;

    public CompositiveMatchAdapter(Context context, List<CompositiveMatchInfo> matchs) {
        this.context = context;
        this.matchs = matchs;
    }


    public void setData(List<CompositiveMatchInfo> matchs) {
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
            holder.tvTime = (TextView) view.findViewById(R.id.tv_time);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        CompositiveMatchInfo match = matchs.get(position);
        AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + match.getIcon() , holder.ivImg);
        holder.tvType.setTextColor(context.getResources().getColor(R.color.black_extend_intro));
        if (match.getType() == 0) {
            holder.tvType.setText("官方活动/线下");
        } else if (match.getType() == 1) {
            holder.tvType.setText("娱乐比赛/线上");
        } else if (match.getType() == 2) {
            holder.tvType.setText("娱乐比赛/线上");
        } else if (match.getType() == 3) {
            holder.tvType.setText("官方比赛/线下");
        } else if (match.getType() == 4) {
            holder.tvType.setText("官方比赛/线上");
        } else if (match.getType() == 5){
            holder.tvType.setText("娱乐比赛/线上");
        }
        holder.rlGameStatus.setVisibility(View.VISIBLE);
        if (match.getState() == 1) {
            //报名中
            holder.tvGameStatus.setText("报名中");
        } else if (match.getState() == 2) {
            //报名预热中
            holder.tvGameStatus.setText("报名预热中");
        } else if (match.getState() == 3) {
            //报名已截至
            holder.tvGameStatus.setText("报名已截至");
        } else if (match.getState() == 4) {
            holder.tvGameStatus.setText("赛事已结束");
        } else if (match.getState() == 5) {
            holder.tvGameStatus.setText(" 赛事进行中");
        }
        holder.tvTime.setText(R.string.match_time);
        holder.tvTitle.setText(match.getTitle());
        holder.tvBeginTime.setText(TimeFormatUtil.formatNoYearNoTime(match.getStartTime()) + "-" + TimeFormatUtil.formatNoYearNoTime(match.getEndTime()));
        holder.tvHasApplyNum.setText(match.getApplyCount() + "");
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
        TextView tvTime;
    }
}
