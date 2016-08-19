package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.BuyRecordData;
import com.miqtech.master.client.entity.CommentInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.simcpux.Util;
import com.miqtech.master.client.ui.ImagePagerActivity;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.PersonalHomePageActivity;
import com.miqtech.master.client.ui.ReportActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/17.
 */
public class BuyRecordAdapter extends BaseAdapter {

   private ArrayList<BuyRecordData> buyRecords;
    private Context context;
    private LayoutInflater mInflater;
    private List<ImageView> imageViews = new ArrayList<ImageView>();

    public BuyRecordAdapter(Context context,ArrayList<BuyRecordData> buyRecords) {
        this.context = context;
        this.buyRecords = buyRecords;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        if(buyRecords!=null && !buyRecords.isEmpty()) {
            return buyRecords.size();
        }else{
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return buyRecords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        MyViewHolder holder = null;
        if (view == null) {
            view = mInflater.inflate(R.layout.layout_shop_buy_record_item, parent, false);
            holder = new MyViewHolder();
            holder.tvTelephoneNum = (TextView) view.findViewById(R.id.tvTelephoneNum);
            holder.tvGoldCoinNum = (TextView) view.findViewById(R.id.tvGoldCoinNum);
            holder.tvTime = (TextView) view.findViewById(R.id.tvTime);
            view.setTag(holder);
        } else {
            holder = (MyViewHolder) view.getTag();
        }
        //设置数据
        BuyRecordData data= (BuyRecordData)buyRecords.get(position);
        holder.tvTelephoneNum.setText(Utils.changeString(data.getPrizePhone()));
        setFontDiffrentColor(data.getPayCoin()+"金币",0,(data.getPayCoin()+"金币").length()-2,holder.tvGoldCoinNum);
        holder.tvTime.setText(data.getCreateData());
        return view;
    }
    class MyViewHolder {
        TextView tvTelephoneNum;//电话号码
        TextView tvGoldCoinNum;// 金币数量
        TextView tvTime;// 时间
    }
    private void setFontDiffrentColor(String content,int start,int end,TextView tv){
        if(tv==null){
            return;
        }
        SpannableStringBuilder style=new SpannableStringBuilder(content);
        style.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.orange)),start,end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(style);
    }
}
