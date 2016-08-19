package com.miqtech.master.client.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.EventAgainst;
import com.miqtech.master.client.entity.EventAgainstDetail;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.view.CircleImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhaosentao on 2016/7/21.
 */
public class EventAgainstExpandableAdapter extends BaseExpandableListAdapter {


    private Context context;
    private List<EventAgainst> eventAgainstList;


    public EventAgainstExpandableAdapter(Context context, List<EventAgainst> eventAgainstList) {
        this.context = context;
        this.eventAgainstList = eventAgainstList;
    }


    @Override
    public int getGroupCount() {
        return eventAgainstList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (eventAgainstList.get(groupPosition).getDetailList() != null && !eventAgainstList.get(groupPosition).getDetailList().isEmpty()) {
            return eventAgainstList.get(groupPosition).getDetailList().size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return eventAgainstList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return eventAgainstList.get(groupPosition).getDetailList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderParent viewHolderParent = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_event_against_item, parent, false);
            viewHolderParent = new ViewHolderParent(convertView);
            convertView.setTag(viewHolderParent);
        } else {
            viewHolderParent = (ViewHolderParent) convertView.getTag();
        }

        if (!TextUtils.isEmpty(eventAgainstList.get(groupPosition).getName())) {
            viewHolderParent.tvTitle.setText(eventAgainstList.get(groupPosition).getName());
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderChild viewHolderChild = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_event_against_item_child_item, parent, false);
            viewHolderChild = new ViewHolderChild(convertView);
            convertView.setTag(viewHolderChild);
        } else {
            viewHolderChild = (ViewHolderChild) convertView.getTag();
        }

        showChildViewData(viewHolderChild, groupPosition, childPosition);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private void showChildViewData(ViewHolderChild childHolder, int groupPosition, int childPosition) {
        EventAgainstDetail bean = eventAgainstList.get(groupPosition).getDetailList().get(childPosition);
        if (childHolder == null || bean == null) {
            return;
        }
        //显示左边的头像
        if (childHolder.ivHeadLeft.getTag() == null || !(bean.getA_icon().equals((String) childHolder.ivHeadLeft.getTag()))) {
            AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + bean.getA_icon() + "!small", childHolder.ivHeadLeft);
            childHolder.ivHeadLeft.setTag(bean.getA_icon());
        }

        //显示右边的头像
        if (childHolder.ivHeadRight.getTag() == null || !(bean.getB_icon().equals((String) childHolder.ivHeadRight.getTag()))) {
            AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + bean.getB_icon() + "!small", childHolder.ivHeadRight);
            childHolder.ivHeadRight.setTag(bean.getA_icon());
        }

        //显示左边选手的昵称
        if (!TextUtils.isEmpty(bean.getA_nickname())) {
            childHolder.tvNameLeft.setText(bean.getA_nickname());
        } else {
            childHolder.tvNameLeft.setText("");
        }

        //显示右边选手的昵称
        if (!TextUtils.isEmpty(bean.getB_nickname())) {
            childHolder.tvNameRight.setText(bean.getB_nickname());
        } else {
            childHolder.tvNameRight.setText("");
        }

        //判断左边选手是否显示皇冠
        if (bean.getA_is_win() == 1) {
            childHolder.ivHeadBgLeft.setImageDrawable(context.getResources().getDrawable(R.drawable.fight_match_win));
            childHolder.tvScoreLeft.setTextColor(context.getResources().getColor(R.color.light_orange));
        } else {
            childHolder.ivHeadBgLeft.setImageDrawable(context.getResources().getDrawable(R.drawable.fight_match_convey));
            childHolder.tvScoreLeft.setTextColor(context.getResources().getColor(R.color.shop_font_black));
        }

        //判断右边选手是否显示皇冠
        if (bean.getB_is_win() == 1) {
            childHolder.ivHeadBgRight.setImageDrawable(context.getResources().getDrawable(R.drawable.fight_match_win));
            childHolder.tvScoreRight.setTextColor(context.getResources().getColor(R.color.light_orange));
        } else {
            childHolder.ivHeadBgRight.setImageDrawable(context.getResources().getDrawable(R.drawable.fight_match_convey));
            childHolder.tvScoreRight.setTextColor(context.getResources().getColor(R.color.shop_font_black));
        }

        childHolder.tvScoreLeft.setText(bean.getA_score() + "");
        childHolder.tvScoreRight.setText(bean.getB_score() + "");

        if (bean.getState() == 0) {//未开始
            childHolder.tvStatue.setBackgroundResource(R.drawable.bg_against_end);
            childHolder.tvStatue.setTextColor(context.getResources().getColor(R.color.shop_buy_record_gray));
            childHolder.tvStatue.setText(context.getResources().getString(R.string.unstart));
        } else if (bean.getState() == 1) {//进行中
            childHolder.tvStatue.setBackgroundResource(R.drawable.bg_against_start);
            childHolder.tvStatue.setTextColor(context.getResources().getColor(R.color.white));
            childHolder.tvStatue.setText(context.getResources().getString(R.string.proceeding));
        } else if (bean.getState() == 2) {//已结束
            childHolder.tvStatue.setBackgroundResource(R.drawable.bg_against_end);
            childHolder.tvStatue.setTextColor(context.getResources().getColor(R.color.shop_buy_record_gray));
            childHolder.tvStatue.setText(context.getResources().getString(R.string.overed));
        }
    }


    class ViewHolderParent {
        @Bind(R.id.eventAgsinstTvTitle)
        TextView tvTitle;//标题
        @Bind(R.id.eventAgsinstLlitem)
        LinearLayout item;

        public ViewHolderParent(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolderChild {
        @Bind(R.id.eventAgainstItemLlHeadLeft)
        LinearLayout llHeadLeft;//左选手头像图标点击
        @Bind(R.id.eventAgainstItemIvHeadLeft)
        CircleImageView ivHeadLeft;//左选手头像图标
        @Bind(R.id.eventAgainstItemIvHeadBgLeft)
        ImageView ivHeadBgLeft;//左选手头像图标背景（也就是那个皇冠）
        @Bind(R.id.eventAgainstItemTvNameLeft)
        TextView tvNameLeft;//左选手名称
        @Bind(R.id.eventAgainstItemTvScoreLeft)
        TextView tvScoreLeft;//左选手比分

        @Bind(R.id.eventAgainstItemLlHeadRight)
        LinearLayout llHeadRight;//右选手头像图标点击
        @Bind(R.id.eventAgainstItemIvHeadRight)
        CircleImageView ivHeadRight;//右选手头像图标
        @Bind(R.id.eventAgainstItemIvHeadBgRight)
        ImageView ivHeadBgRight;//右选手头像图标（也就是那个皇冠）
        @Bind(R.id.eventAgainstItemTvNameRight)
        TextView tvNameRight;//右选手名称
        @Bind(R.id.eventAgainstItemTvScoreRight)
        TextView tvScoreRight;//右选手比分

        @Bind(R.id.eventAgainstItemLlTvStatue)
        TextView tvStatue;//比赛状态
        @Bind(R.id.eventAgainstItemLine)
        View line;//底部线

        public ViewHolderChild(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
