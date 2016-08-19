package com.miqtech.master.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;

import java.util.List;


/**
 * Created by Administrator on 2015/12/30.
 */
public class SearchHistoryAdapter extends BaseAdapter  {

    private List<String> mData;
    private Context mContext;
    private OnItemClick onItemClick;

    public SearchHistoryAdapter(Context context, List<String> data, OnItemClick itemClick) {
        this.mData = data;
        this.mContext = context;
        this.onItemClick = itemClick;
    }

    public void setData(List<String> data){
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_search_his_item, null);
            holder = new ViewHolder();
            holder.ivDelete = (ImageView) convertView.findViewById(R.id.im_delete);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvTitle.setText(mData.get(position));
        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onClickItem(mData.get(position));
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.deleteItem(mData.get(position));
            }
        });
        return convertView;
    }


    class ViewHolder {
        TextView tvTitle;
        ImageView ivDelete;

    }

    public interface OnItemClick {
        void onClickItem(String key);
        void deleteItem(String key);
    }
}
