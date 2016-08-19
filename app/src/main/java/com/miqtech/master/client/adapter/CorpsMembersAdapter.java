package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.TeammateInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.PersonalHomePageActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.view.MyScanItemLinearLayout;
import com.miqtech.master.client.view.MyScanListview;

import java.util.List;

/**
 * 战队详情队员的adapter
 * Created by zhaosentao on 2016/5/9.
 */
public class CorpsMembersAdapter extends BaseAdapter {

    List<TeammateInfo> teamers;
    private Context mContext;
    private LayoutInflater mInflater;
    private boolean isMonitor = false;
    private MyScanListview myScanListview;

    /**
     * @param mContext
     * @param teamers  队员列表
     */
    public CorpsMembersAdapter(Context mContext, List<TeammateInfo> teamers, boolean isMonitor) {
        this.mContext = mContext;
        this.teamers = teamers;
        this.isMonitor = isMonitor;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return teamers.size();
    }

    @Override
    public Object getItem(int position) {
        return teamers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_corps_members_item, parent, false);
            holder.llItem = (MyScanItemLinearLayout) convertView.findViewById(R.id.MyScanLlCorpsDetailsV2Item);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivCorpsDetailsV2ItemMemberIcon);
            holder.ivMemberPhone = (ImageView) convertView.findViewById(R.id.ivCorpsDetailsV2ItemMemberPhone);
            holder.icCaptain = (ImageView) convertView.findViewById(R.id.ivCorpsDetailV2ItemCaptain);
            holder.tvMemberName = (TextView) convertView.findViewById(R.id.tvCorpsDetailV2ItemMemberName);
            holder.tvMember = (TextView) convertView.findViewById(R.id.tvCorpsDetailV2ItemMember);
            holder.tvGoodLocaton = (TextView) convertView.findViewById(R.id.tvCorpsDetailV2ItemGoodLocation);
            holder.tvRemoveMember = (TextView) convertView.findViewById(R.id.tvCorpsDetailV2ItemRemoveMember);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TeammateInfo teammateInfo = teamers.get(position);
        MyOnclickView myOnclickView = new MyOnclickView(teammateInfo);

        //显示头像
        AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + teammateInfo.getIcon(),
                holder.ivIcon);
        holder.ivIcon.setOnClickListener(myOnclickView);//查看头像个人

        //显示队员名称
        if (!TextUtils.isEmpty(teammateInfo.getNickname())) {
            holder.tvMemberName.setText(teammateInfo.getNickname());
        } else {
            holder.tvMemberName.setText("");
        }

        //判断队长还是队员.1为队长
        if (teammateInfo.getIs_monitor().equals("1")) {
            holder.icCaptain.setVisibility(View.VISIBLE);
            holder.tvMember.setVisibility(View.GONE);
        } else {
            holder.icCaptain.setVisibility(View.GONE);
            holder.tvMember.setVisibility(View.VISIBLE);
        }

        //擅长位置
        if (!TextUtils.isEmpty(teammateInfo.getLabor())) {
            holder.tvGoodLocaton.setText(mContext.getResources().getString(R.string.corpsDetailsV2GoodLocation, teammateInfo.getLabor()));
        } else {
            holder.tvGoodLocaton.setText(mContext.getResources().getString(R.string.corpsDetailsV2GoodLocation, ""));
        }

        if (isMonitor) {
            holder.ivMemberPhone.setVisibility(View.VISIBLE);
            if (teammateInfo.getIs_monitor().equals("1")) {
                holder.ivMemberPhone.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.ivMemberPhone.setVisibility(View.INVISIBLE);
        }


        //拨打电话
        if (!TextUtils.isEmpty(teammateInfo.getTelephone())) {
            holder.ivMemberPhone.setOnClickListener(myOnclickView);
        } else {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.corpsDetailsV2NoMemberPhone), Toast.LENGTH_SHORT).show();
        }

        //是否显示移除队员
        if (isMonitor) {
            holder.llItem.setIsSlide(true);
            holder.tvRemoveMember.setVisibility(View.VISIBLE);
            holder.tvRemoveMember.setOnClickListener(myOnclickView);
            if (teammateInfo.getIs_monitor().equals("1")) {
                holder.llItem.setIsSlide(false);
                holder.tvRemoveMember.setVisibility(View.GONE);
            }
        } else {
            holder.llItem.setIsSlide(false);
            holder.tvRemoveMember.setVisibility(View.GONE);
        }


        return convertView;
    }

    private class MyOnclickView implements View.OnClickListener {
        private TeammateInfo bean;

        public MyOnclickView(TeammateInfo bean) {
            this.bean = bean;
        }

        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.ivCorpsDetailsV2ItemMemberPhone://拨打电话
                    intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + bean.getTelephone()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    break;
                case R.id.tvCorpsDetailV2ItemRemoveMember://移除队员
                    onItemRemoveMember.OnItemRemoveMomber(bean.getMember_id());
                    break;
                case R.id.ivCorpsDetailsV2ItemMemberIcon://查看头像
                    intent = new Intent(mContext, PersonalHomePageActivity.class);
                    intent.putExtra("id", bean.getMember_id() + "");
                    mContext.startActivity(intent);
                    break;
            }
        }
    }


    class ViewHolder {
        MyScanItemLinearLayout llItem;//最外层布局
        ImageView ivIcon;//头像
        ImageView ivMemberPhone;//队员电话
        ImageView icCaptain;//队长
        TextView tvMemberName;//队员名称
        TextView tvMember;//队员
        TextView tvGoodLocaton;//擅长位置
        TextView tvRemoveMember;//删除队员
    }

    public interface OnItemRemoveMember {
        /**
         * 移除队员
         *
         * @param memberId 队员id
         */
        void OnItemRemoveMomber(int memberId);
    }

    ;
    public OnItemRemoveMember onItemRemoveMember;

    public void setOnItemRemoveMember(OnItemRemoveMember onItemRemoveMember) {
        this.onItemRemoveMember = onItemRemoveMember;
    }

}
