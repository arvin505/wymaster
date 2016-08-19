package com.miqtech.master.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.MatchNetbar;

import java.util.List;

/**
 * Created by Administrator on 2016/1/21.
 */
public class CorpsFilterNetbarAdapter extends BaseAdapter {

    private List<MatchNetbar> mNetbars;
    private Context mContext;
    private LayoutInflater inflater;

    public CorpsFilterNetbarAdapter(Context context, List<MatchNetbar> netbars){
        this.mNetbars = netbars;
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mNetbars.size();
    }

    @Override
    public Object getItem(int position) {
        return mNetbars.get(position);
    }

    public void setNetbars(List<MatchNetbar> netbars){
        this.mNetbars = netbars;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.layout_corpsfilter_netbar_item, null);
            holder = new ViewHolder();
            holder.imCheck = (ImageView) convertView.findViewById(R.id.im_check);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        MatchNetbar netbar = mNetbars.get(position);
        holder.tvName.setText(netbar.getName());
        if (selectIndex == position){
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.orange));
            holder.imCheck.setImageResource(R.drawable.ic_pay_checked);
        }else {
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.textColorBattle));
            holder.imCheck.setImageResource(R.drawable.icon_netbar_unchecked);
        }
        return convertView;
    }

    private int selectIndex = 0;

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    class ViewHolder{
        TextView tvName;
        ImageView imCheck;
    }

}
