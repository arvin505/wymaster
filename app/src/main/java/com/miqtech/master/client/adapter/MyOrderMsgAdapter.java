package com.miqtech.master.client.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.MyMessage;
import com.miqtech.master.client.utils.DateUtil;

import java.util.List;

/**
 * 我的消息里的订单消息adapter
 * Created by Administrator on 2015/12/4.
 */
public class MyOrderMsgAdapter extends BaseAdapter {

    private Context mContext;
    private List<MyMessage> myMessageList;

    public MyOrderMsgAdapter(Context mContext, List<MyMessage> myMessageList) {
        this.mContext = mContext;
        this.myMessageList = myMessageList;
    }

    @Override
    public int getCount() {
        return myMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return myMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_order_msg, null);
            holder.title = (TextView) convertView.findViewById(R.id.title_order_msg_item);
            holder.time = (TextView) convertView.findViewById(R.id.time_order_msg_item);
            holder.content = (TextView) convertView.findViewById(R.id.content_order_msg_item);
            holder.rl = (RelativeLayout) convertView.findViewById(R.id.rl_order_msg_item);
            holder.ivIsRead = (ImageView) convertView.findViewById(R.id.ivIsRead);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MyMessage bean = myMessageList.get(position);

        if (!TextUtils.isEmpty(bean.getTitle())) {
            holder.title.setText(bean.getTitle());
        } else {
            holder.title.setText("");
        }

        if (!TextUtils.isEmpty(bean.getCreate_date())) {
            holder.time.setText(DateUtil.dateToStrLong(bean.getCreate_date()));
        } else {
            holder.time.setText("");
        }

        if (!TextUtils.isEmpty(bean.getContent())) {
            holder.content.setText(bean.getContent());
        } else {
            holder.content.setText("");
        }

        if (bean.getIs_read() == 0) {
            holder.ivIsRead.setVisibility(View.VISIBLE);
        } else {
            holder.ivIsRead.setVisibility(View.GONE);
        }
        return convertView;
    }


    class ViewHolder {
        TextView time;
        TextView title;
        TextView content;
        RelativeLayout rl;
        ImageView ivIsRead;
    }


}
