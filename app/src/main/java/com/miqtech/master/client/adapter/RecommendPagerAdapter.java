package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.RecommendInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.OfficalEventActivity;
import com.miqtech.master.client.ui.RecreationMatchDetailsActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.salvage.RecyclingPagerAdapter;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by arvin on 2015/11/15.
 */
public class RecommendPagerAdapter extends RecyclingPagerAdapter {

    private Context context;
    private List<RecommendInfo> list;
    private List<View> views = new ArrayList<>();
    public RecommendPagerAdapter(Context context, List<RecommendInfo> list) {
        this.context = context;
        this.list = list;
       /* for (int i =0; i<list.size(); i++){
            View view = getView(i);
            views.add(view);
        }*/
    }

    @Override
    public int getCount() {
        return list.size();
    }



    @Override
    public View getView(final int position, View convertView, final ViewGroup container) {
        ViewHolder holder;
        View view = null;
        if (view == null) {
            view = View.inflate(context, R.layout.layout_matchshlv_item, null);
            holder = new ViewHolder(view);
            AutoUtils.autoSize(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        RecommendInfo match = list.get(position);
        if(match != null){
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + match.getIcon(), holder.ivImg);
            if (match.getWay() == 1) {
                holder.tvType.setText("官方活动/线下");
            } else if (match.getWay() == 2) {
                holder.tvType.setText("官方活动/线上");
            }
            holder.tvGameStatus.setVisibility(View.GONE);
            holder.tvTitle.setText(match.getTitle());
            holder.tvBeginTime.setText(TimeFormatUtil.formatNoSecond(match.getStart_time()));
            holder.tvHasApplyNum.setText(match.getApplyNum());
            //holder.tvSummary.setText(match.getSummary());
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecommendInfo info = (RecommendInfo) list.get(position);
                if (info != null) {
                    if (info.getType() == 1) {
                        Intent intent = new Intent();
                        intent.setClass(context, OfficalEventActivity.class);
                        intent.putExtra("matchId", info.getId() + "");
                        context.startActivity(intent);
                    } else if (info.getType() == 2) {
                        Intent intent = new Intent();
                        intent.setClass(context, RecreationMatchDetailsActivity.class);
                        intent.putExtra("id", info.getId() + "");
                        context.startActivity(intent);
                    }
                }
            }
        });
        return view;
    }

    static class ViewHolder {
        @Bind(R.id.tvType)
        TextView tvType;
        @Bind(R.id.rlGameStatus)
        RelativeLayout rlGameStatus;
        @Bind(R.id.tvTitle)
        TextView tvTitle;
        @Bind(R.id.ivImg)
        ImageView ivImg;
        @Bind(R.id.tvHasApplyNum)
        TextView tvHasApplyNum;
        @Bind(R.id.tvBeginTime)
        TextView tvBeginTime;
        @Bind(R.id.tvSummary)
        TextView tvSummary;
        @Bind(R.id.tvGameStatus)
        TextView tvGameStatus;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
