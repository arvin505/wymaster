package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.RewardGrade;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.ImagePagerActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.view.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 赏金猎人名单
 * Created by zhaosentao on 2016/7/28.
 */
public class RewardPrizeAdapter extends BaseAdapter {

    private List<RewardGrade> rewardGradeList;
    private Context context;

    public RewardPrizeAdapter(Context context, List<RewardGrade> rewardGradeList) {
        this.context = context;
        this.rewardGradeList = rewardGradeList;
    }


    @Override
    public int getCount() {
        return rewardGradeList.size();
    }

    @Override
    public Object getItem(int position) {
        return rewardGradeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_bounty_award_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RewardGrade bean = rewardGradeList.get(position);

        //显示头像
        AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + bean.getIcon() + "!small", holder.ivHead);

        //显示昵称
        if (!TextUtils.isEmpty(bean.getNickname())) {
            holder.tvName.setText(bean.getNickname());
        } else {
            holder.tvName.setText("");
        }

        //显示序列号
        holder.tvNumber.setText((position + 1) + "");

        //当为最后一个时，隐藏底部的线
        if (position + 1 == rewardGradeList.size()) {
            holder.line.setVisibility(View.GONE);
        }

        seeRecord(holder, bean);

        return convertView;
    }

    private void seeRecord(ViewHolder holder, final RewardGrade bean) {
        holder.tvSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Map<String, String>> imgs = new ArrayList<Map<String, String>>();
                Map<String, String> map = new HashMap<String, String>();
                map.put("url", bean.getImg());
                imgs.add(map);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", imgs);
                bundle.putInt("isHideGallery", 1);
                intent.putExtras(bundle);
                intent.setClass(context, ImagePagerActivity.class);
                context.startActivity(intent);
            }
        });
    }

    class ViewHolder {
        @Bind(R.id.bountyTvNumber)
        TextView tvNumber;
        @Bind(R.id.bountyIvHead)
        CircleImageView ivHead;
        @Bind(R.id.bountyTvSeeRecord)
        TextView tvSee;
        @Bind(R.id.bountyTvName)
        TextView tvName;
        @Bind(R.id.line)
        View line;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
