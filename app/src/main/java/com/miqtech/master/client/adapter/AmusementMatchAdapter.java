package com.miqtech.master.client.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.AmusementMatchInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.TimeFormatUtil;


import java.util.List;

/**
 * Created by Administrator on 2015/11/27.
 */
public class AmusementMatchAdapter extends BaseAdapter {

    private Context context ;
    private List<AmusementMatchInfo> matchs;
    public AmusementMatchAdapter(Context context ,List<AmusementMatchInfo> matchs){
        this.context = context ;
        this.matchs = matchs;
    }


    public void setData(List<AmusementMatchInfo> matchs){
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
        ViewHolder holder ;
        if(view == null){
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.layout_match_item_card,null);
            holder.tvType = (TextView)view.findViewById(R.id.tvType);
            holder.ivImg = (ImageView)view.findViewById(R.id.ivImg);
            holder.rlGameStatus = (RelativeLayout)view.findViewById(R.id.rlGameStatus);
            holder.tvGameStatus = (TextView)view.findViewById(R.id.tvGameStatus);
            holder.tvTitle = (TextView)view.findViewById(R.id.tvTitle);
            holder.tvBeginTime = (TextView)view.findViewById(R.id.tvBeginTime);
            holder.tvHasApplyNum = (TextView)view.findViewById(R.id.tvHasApplyNum);
            holder.tvSummary = (TextView)view.findViewById(R.id.tvSummary);
            holder.tvTime = (TextView) view.findViewById(R.id.tv_time);
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }
        AmusementMatchInfo match = matchs.get(position);
        AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + match.getMainIcon(), holder.ivImg);
        holder.tvType.setTextColor(context.getResources().getColor(R.color.black_extend_intro));
        if(match.getWay()==1){
            holder.tvType.setText("官方活动/线下");
        }else if(match.getWay()==2){
            holder.tvType.setText("官方活动/线上");
        }
        holder.rlGameStatus.setVisibility(View.VISIBLE);
        if(match.getTimeStatus() == 1){
            //报名中
            holder.tvGameStatus.setText("报名中");
        }else if(match.getTimeStatus() == 2){
            //报名预热中
            holder.tvGameStatus.setText("报名预热中");
        }else if(match.getTimeStatus() == 3){
            //报名已截止
            holder.tvGameStatus.setText("报名已截止");
        }else if(match.getTimeStatus() == 4){

            holder.tvGameStatus.setText("赛事已结束");
        }else if(match.getTimeStatus() == 5){

            holder.tvGameStatus.setText("赛事进行中");
        }else if(match.getTimeStatus() == 6){

            holder.tvGameStatus.setText("赛事未开始");
        }
        holder.tvTime.setText(R.string.match_time);
        holder.tvTitle.setText(match.getTitle());
        holder.tvBeginTime.setText(TimeFormatUtil.formatNoTime(match.getStartDate()));
        holder.tvHasApplyNum.setText(match.getApplyNum());
        holder.tvSummary.setText(match.getSummary());

        return view;
    }

    private class ViewHolder{
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
