package com.miqtech.master.client.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.CardCompat;
import com.miqtech.master.client.entity.RedBag;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.LogUtil;

import java.util.List;

public class MyCurrentListAdapter extends BaseAdapter {
    private Context context;
    private List<RedBag> bags;

    public static final int TYPE_CURRENT = 1;
    public static final int TYPE_HISTORY = 2;
    public static final int TYPE_CHOOSE = 3;

    private int mFlag;

    private CardCompat mSelected;

    public MyCurrentListAdapter(Context context, List<RedBag> bags, int flag) {
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
                v = LayoutInflater.from(context).inflate(R.layout.layout_currentredbag_item, parent, false);
            } else if (mFlag == TYPE_HISTORY) {
                v = LayoutInflater.from(context).inflate(R.layout.layout_outdate_redbag_item, parent, false);
            } else {
                v = LayoutInflater.from(context).inflate(R.layout.layout_currentredbag_item, parent, false);
            }
            holder = new ViewHolder();
            holder.tvMoney = (TextView) v.findViewById(R.id.tv_redbag_money);
            holder.imgType = (ImageView) v.findViewById(R.id.img_redbag_type);
            holder.tvExplain = (TextView) v.findViewById(R.id.tv_redbag_explain);
            holder.tvDate = (TextView) v.findViewById(R.id.tv_redbag_day);
            holder.minMoney = (TextView) v.findViewById(R.id.tv_min_money);
            holder.tvNetbar = (TextView) v.findViewById(R.id.tv_redbag_netbar);
            holder.imgSelected = (ImageView) v.findViewById(R.id.img_selected);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        if (position < bags.size()) {
            RedBag bag = bags.get(position);
            if (bag.getPay_amount_canuse() == 1 && mFlag == TYPE_CURRENT) {
                ((ViewGroup) v).getChildAt(0).setBackgroundResource(R.drawable.bg_redbag_normal);
                LogUtil.e("TAG", "type : 1 " + mFlag);
            } else {
                ((ViewGroup) v).getChildAt(0).setBackgroundResource(R.drawable.bg_redbag_outdate);
                LogUtil.e("TAG", "type : 2 " + mFlag);
            }
            holder.tvDate.setText("有效期：" + DateUtil.dateToDate(bag.getBegin_date()) + "-" + DateUtil.dateToDate(bag.getEnd_date()));
            if (!TextUtils.isEmpty(bag.getExplain())) {
                holder.tvExplain.setText(bag.getExplain() + "(一次可用一张)");
            } else {
                holder.tvExplain.setText("支付网费时使用(一次可用一张)");
            }
            holder.tvMoney.setText(bag.getMoney() + "");
            if (mFlag == TYPE_CURRENT){
                holder.minMoney.setText("满 " + bag.getMin_money() + " 元可用");
            }
            if (bag.getType() == 8) {
                holder.imgType.setVisibility(View.VISIBLE);
                holder.tvNetbar.setText("仅限在" + bag.getName() + "支付网费时使用");
                holder.tvNetbar.setVisibility(View.VISIBLE);
            } else {
                holder.tvNetbar.setVisibility(View.GONE);
                holder.imgType.setVisibility(View.GONE);
            }
            if (mSelected != null && mSelected.id == bag.getId()) {
                holder.imgSelected.setVisibility(View.VISIBLE);
            } else {
                if (holder.imgSelected != null) {
                    holder.imgSelected.setVisibility(View.GONE);
                }
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
        TextView tvMoney;
        TextView tvExplain;
        TextView tvDate;
        TextView minMoney;
        ImageView imgType;
        TextView tvNetbar;
        ImageView imgSelected;
    }

    public void setSelectedRedBag(CardCompat selected) {
        this.mSelected = selected;
    }
}
