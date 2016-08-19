package com.miqtech.master.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.Card;
import com.miqtech.master.client.entity.CardCompat;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.TimeFormatUtil;

import java.util.List;

public class MyCardListAdapter extends BaseAdapter {
    private Context context;
    private List<Card> bags;

    public static final int TYPE_CURRENT = 1;
    public static final int TYPE_HISTORY = 2;

    private int mFlag;

    public MyCardListAdapter(Context context, List<Card> bags, int flag) {
        this.context = context;
        this.bags = bags;
        this.mFlag = flag;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (TYPE_CURRENT == mFlag) {
            if (bags.size() <= 3) {
                return bags.size();
            } else {
                return bags.size() + 1;
            }
        } else {
            return bags.size();
        }

    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return bags.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (v == null) {
            if (mFlag == TYPE_CURRENT) {
                v = LayoutInflater.from(context).inflate(R.layout.layout_mycard_item_available, parent, false);
            } else {
                v = LayoutInflater.from(context).inflate(R.layout.layout_mycard_item_available, parent, false);
            }


            holder = new ViewHolder();
            holder.imgType = (ImageView) v.findViewById(R.id.img_redbag_type);
            holder.tvTitle = (TextView) v.findViewById(R.id.tv_redbag_money);
            holder.tvDate = (TextView) v.findViewById(R.id.tv_redbag_day);
            holder.tvNetbar = (TextView) v.findViewById(R.id.tv_redbag_netbar);
            holder.imgSelected = (ImageView) v.findViewById(R.id.img_selected);
            holder.imgStatus = (ImageView) v.findViewById(R.id.img_status);
            holder.imgIconType = (ImageView) v.findViewById(R.id.imgIconType);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        if (mFlag == TYPE_CURRENT) {
            ((ViewGroup) v).getChildAt(0).setBackgroundResource(R.drawable.bg_mycard_avaible);
            LogUtil.e("TAG", "type : 1 " + mFlag);

        } else {
            ((ViewGroup) v).getChildAt(0).setBackgroundResource(R.drawable.bg_mycard_unavible);
            LogUtil.e("TAG", "type : 2 " + mFlag);
        }
        if (position < bags.size()) {
            Card bag = bags.get(position);
            if (bag.getEnabled() == 1 && mFlag == TYPE_CURRENT) {
                ((ViewGroup) v).getChildAt(0).setBackgroundResource(R.drawable.bg_mycard_avaible);
                LogUtil.e("TAG", "type : 1 " + mFlag);
            } else {
                ((ViewGroup) v).getChildAt(0).setBackgroundResource(R.drawable.bg_mycard_unavible);
                LogUtil.e("TAG", "type : 2 " + mFlag);
            }
            holder.tvDate.setText("有效期：" + TimeFormatUtil.formatNoSecond(bag.getStart_date()) + " - " + TimeFormatUtil.formatNoSecond(bag.getEnd_date()));
            holder.tvNetbar.setText("仅限在" + bag.getNetbar_name() + "兑换");
            holder.tvTitle.setText(bag.getName());
            if (mFlag == TYPE_HISTORY) {
                holder.imgStatus.setVisibility(View.VISIBLE);
                if (bag.getStatus() == 2) {
                    holder.imgStatus.setImageResource(R.drawable.icon_card_used);
                } else {
                    holder.imgStatus.setImageResource(R.drawable.icon_card_invalid);
                }
            } else {
                if (mSelected != null && mSelected.id == bag.getId()) {
                    holder.imgSelected.setVisibility(View.VISIBLE);
                } else {
                    if (holder.imgStatus != null) {
                        holder.imgStatus.setVisibility(View.GONE);
                    }
                }
            }
            if (mSelected != null && mSelected.id == bag.getId()) {
                holder.imgSelected.setVisibility(View.VISIBLE);
            } else {
                if (holder.imgSelected != null) {
                    holder.imgSelected.setVisibility(View.GONE);
                }
            }


            //券类型
            if (bag.getType() == 1) {  //增值券
                holder.imgIconType.setImageResource(R.drawable.icon_ticket);
            } else {
                holder.imgIconType.setImageResource(R.drawable.icon_gift);
            }

        }

        if (bags.size() > 3 && position == bags.size()) {
            v.setVisibility(View.INVISIBLE);
        } else {
            v.setVisibility(View.VISIBLE);
        }
        return v;
    }

    private class ViewHolder {
        TextView tvTitle;
        TextView tvDate;
        ImageView imgType;
        TextView tvNetbar;
        ImageView imgSelected;
        ImageView imgStatus;
        ImageView imgIconType;
    }

    private CardCompat mSelected;

    public void setSelectedRedBag(CardCompat selected) {
        this.mSelected = selected;
    }

}
