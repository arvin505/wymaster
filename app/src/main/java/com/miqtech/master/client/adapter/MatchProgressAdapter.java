package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.EventAgainst;
import com.miqtech.master.client.ui.EventAgainstListActivity;
import com.miqtech.master.client.utils.TimeFormatUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/7/7.
 */
public class MatchProgressAdapter extends BaseAdapter {
    private List<EventAgainst> datas;
    private Context context;
    private SeeMoreMatchProcess listener;

    public MatchProgressAdapter(Context context, List<EventAgainst> datas,SeeMoreMatchProcess listener){
      this.context=context;
        this.datas=datas;
        this.listener=listener;
    }
    @Override
    public int getCount() {
        if(datas!=null && datas.size()!=0) {
            return datas.size();
        }else{
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.layout_match_process_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == 0) {//显示头
            holder.rlMatchProgress.setVisibility(View.GONE);
            holder.rlTopTitle.setVisibility(View.VISIBLE);
            holder.spliteLine1.setVisibility(View.VISIBLE);
            holder.llSeeMore.setVisibility(View.GONE);
            holder.tvSeeMatchPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.setMatchPic();
                }
            });
        } else if (position == (datas.size() - 1)) {//显示尾
            holder.rlMatchProgress.setVisibility(View.GONE);
            holder.rlTopTitle.setVisibility(View.GONE);
            holder.llSeeMore.setVisibility(View.VISIBLE);
            holder.spliteLine1.setVisibility(View.GONE);
            holder.llSeeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转页面
                    listener.setMore();
                }
            });

        } else {
            holder.rlMatchProgress.setVisibility(View.VISIBLE);
            holder.rlTopTitle.setVisibility(View.GONE);
            holder.llSeeMore.setVisibility(View.GONE);
            holder.spliteLine1.setVisibility(View.GONE);
            showCommentData(position, holder);
        }
        return convertView;
    }
    /**
     * 显示评论数据
     *
     * @param position 位置
     * @param myHolder
     */
    private void showCommentData(final int position, ViewHolder myHolder) {
        if (datas.isEmpty()) {
            return;
        }
        EventAgainst eventAgainst= datas.get(position);

        if (!TextUtils.isEmpty(eventAgainst.getName())) {
            myHolder.matchName.setText(eventAgainst.getName());
        } else {
            myHolder.matchName.setText("");
        }

        //显示评论内容
        if (!TextUtils.isEmpty(eventAgainst.getMatch_time()) && !TextUtils.isEmpty(eventAgainst.getOver_time())) {
            myHolder.matchTime.setText(TimeFormatUtil.formatNoYear2(eventAgainst.getMatch_time())+"-"+TimeFormatUtil.formatNoYear2(eventAgainst.getOver_time()));
        } else {
            myHolder.matchTime.setText("");
        }

        //显示是否评论的状态
        if (eventAgainst.getState()==0) {
            myHolder.ivMatchState.setImageResource(R.drawable.match_state_unstart);
        } else if (eventAgainst.getState()==1) {
            myHolder.ivMatchState.setImageResource(R.drawable.match_state_begining);
        }else if(eventAgainst.getState()==2){
            myHolder.ivMatchState.setImageResource(R.drawable.match_state_end);
        }

        myHolder.rlMatchProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });
    }
    static class ViewHolder {
        @Bind(R.id.rlTopTitle)
        RelativeLayout rlTopTitle; //顶部的赛事进程
        @Bind(R.id.spliteLine1)
        View spliteLine1;
        @Bind(R.id.matchName)
        TextView matchName;    //赛事名
        @Bind(R.id.matchTime)
        TextView matchTime;   //比赛时间
        @Bind(R.id.ivMatchState)
        ImageView ivMatchState; //比赛状态
        @Bind(R.id.rlMatchProgress)
        RelativeLayout rlMatchProgress; //赛事进程
        @Bind(R.id.spliteLine2)
        View spliteLine2;
        @Bind(R.id.llSeeMore)
        LinearLayout llSeeMore;//底部的点击查看更多
        @Bind(R.id.tvSeeMatchPic)
        TextView tvSeeMatchPic;//查看赛事图
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    public interface SeeMoreMatchProcess{
        void setMore(); //赛事进程
        void setMatchPic(); //赛事图
        void onItemClick(int position); //item点击事件
    }
}
