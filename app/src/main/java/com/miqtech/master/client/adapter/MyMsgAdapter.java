package com.miqtech.master.client.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.MyMessage;
import com.miqtech.master.client.ui.MyMessageActivity;
import com.miqtech.master.client.utils.TimeUtil;

@SuppressLint("ResourceAsColor")
public class MyMsgAdapter extends BaseAdapter {
    Context context;
    List<MyMessage> messages;
    private int msgType;
    private final int click_yuezhan = 1;
    private final int click_pay = 2;
    private final int click_reserve = 3;
    private final int click_redbag = 4;
    private final int click_match = 5;
    private View currentItemView;

    public MyMsgAdapter(Context context, List<MyMessage> messages, int msgType) {
        this.context = context;
        this.messages = messages;
        this.msgType = msgType;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder;
        int type = -1;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.layout_mymsg_item, parent, false);
            holder = new ViewHolder();
            holder.tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            holder.tvTime = (TextView) v.findViewById(R.id.tvTime);
            holder.ivIcon = (ImageView) v.findViewById(R.id.ivIcon);
            holder.ivIsRead = (ImageView) v.findViewById(R.id.ivIsRead);
            holder.tvPraise = (TextView) v.findViewById(R.id.tvPraise);
            holder.tvContent = (TextView) v.findViewById(R.id.tvContent);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        initHodler(holder, position);
        return v;
    }

    private void initHodler(ViewHolder holder, int position) {
        String time = "";
        time = TimeUtil.friendlyTime(messages.get(position).getCreate_date());
        holder.tvTime.setText(time);
        if (messages.get(position).getIs_read() == 0) {
            holder.ivIsRead.setVisibility(View.VISIBLE);
        } else if (messages.get(position).getIs_read() > 0) {
            holder.ivIsRead.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(messages.get(position).getTitle())) {
            holder.tvTitle.setText(messages.get(position).getTitle());
        } else {
            holder.tvTitle.setText("");
        }

        if (!TextUtils.isEmpty(messages.get(position).getContent())) {
            holder.tvContent.setText(messages.get(position).getContent());
        } else {
            holder.tvContent.setText("");
        }

        holder.tvPraise.setVisibility(View.GONE);
        //-1系统消息, 0系统消息 1红包消息 2会员消息 3预定消息 4支付消息 5赛事消息 6约战消息
        switch (msgType) {
            case MyMessageActivity.SELECT_ACTIVITIES:// 活动消息
                if (messages.get(position).getType() == 5) {
                    // 赛事消息
                    holder.ivIcon.setImageResource(R.drawable.message_push);
                } else if (messages.get(position).getType() == 6) {
                    holder.ivIcon.setImageResource(R.drawable.message_yuezhan);
                    // 约战消息
                } else if (messages.get(position).getType() == 16) {//自发赛
                    holder.ivIcon.setImageResource(R.drawable.message_push);
                } else {
                    holder.ivIcon.setImageResource(R.drawable.message_sys);
                }
                break;
            case MyMessageActivity.SELECT_SYSTEM:// 系统消息
                if (messages.get(position).getType() == 1) {
                    holder.ivIcon.setImageResource(R.drawable.message_redbag);
                } else if (messages.get(position).getType() == 0 || messages.get(position).getType() == -1) {
                    holder.ivIcon.setImageResource(R.drawable.message_sys);
                } else if (messages.get(position).getType() == 2) {
                    holder.ivIcon.setImageResource(R.drawable.message_sys);
                } else if (messages.get(position).getType() == 8) {
                    holder.ivIcon.setImageResource(R.drawable.message_praise);
                } else if (messages.get(position).getType() == 12) {//众筹
                    holder.ivIcon.setImageResource(R.drawable.message_shop_detail);
                } else if (messages.get(position).getType() == 10) {//商品兑换
                    holder.ivIcon.setImageResource(R.drawable.message_exchange);
                } else if (messages.get(position).getType() == 16) {//自发赛
                    holder.ivIcon.setImageResource(R.drawable.message_push);
                }
                break;
//		case MyMessageActivity.SELECT_ORDER:// 订单消息
//			if (messages.get(position).getType() == 4) {
//				holder.tvType.setText("支付");
//			} else {
//				holder.tvType.setText("预定");
//			}
//			holder.tvType.setBackgroundResource(R.drawable.other_msg);
//			break;
        }
    }

    private class ViewHolder {
        TextView tvTitle;
        TextView tvTime;
        ImageView ivIcon;
        ImageView ivIsRead;
        TextView tvPraise;
        TextView tvContent;
    }

}
