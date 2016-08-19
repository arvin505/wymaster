package com.miqtech.master.client.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.GameItem;

import java.util.List;

/**
 * Created by Administrator on 2015/12/1.
 */
public class FilterGameAdapter extends BaseAdapter{

    private List<GameItem> list;
    private Context mContext;
    private int selected;
    public FilterGameAdapter(Context context, List<GameItem> data, int selected){
        this.mContext = context;
        this.list = data;
        this.selected = selected;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_filter_game_item, null);
        }
        GameItem item = list.get(position);
        TextView gamename = (TextView) convertView.findViewById(R.id.tv_item_game_name);
        gamename.setText(item.getItem_name());
        if (item.getItem_id() == selected){
            gamename.setTextColor(mContext.getResources().getColor(R.color.colorActionBarSelected));
            convertView.setBackgroundResource(R.drawable.cell_selected);
            convertView.setPadding(0,0,0,0);
        }else {
            gamename.setTextColor(mContext.getResources().getColor(R.color.colorActionBarUnSelected));
            convertView.setBackgroundColor(Color.WHITE);
        }
        return convertView;
    }
}
