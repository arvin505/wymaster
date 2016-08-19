package com.miqtech.master.client.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.MatchArea;

import java.util.List;

/**
 * Created by Administrator on 2016/1/21.
 */
public class CorpsFilterAreaAdapter extends BaseAdapter {

    private List<MatchArea> mAreas;
    private Context mContext;
    private LayoutInflater inflater;

    public CorpsFilterAreaAdapter(Context context, List<MatchArea> areas) {
        this.mAreas = areas;
        this.mContext = context;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mAreas.size();
    }

    @Override
    public Object getItem(int position) {
        return mAreas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_corpsfilter_area_item, null);
            holder = new ViewHolder();
            holder.tvArea = (TextView) convertView.findViewById(R.id.tv_area);
            holder.leftLine = convertView.findViewById(R.id.left_line);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MatchArea area = mAreas.get(position);
        holder.tvArea.setText(area.getName());
        if (selectedIndex == position){
            holder.leftLine.setVisibility(View.VISIBLE);
            holder.tvArea.setTextColor(mContext.getResources().getColor(R.color.orange));
            holder.tvArea.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
            holder.tvArea.getPaint().setFakeBoldText(true);
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }else {
            holder.tvArea.setTextColor(mContext.getResources().getColor(R.color.textColorBattle));
            holder.tvArea.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
            holder.leftLine.setVisibility(View.GONE);
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.no_read));
            holder.tvArea.getPaint().setFakeBoldText(false);
        }
        return convertView;
    }

    private int selectedIndex = 0;
    public void setSelectedIndex(int index){
        this.selectedIndex = index;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    class ViewHolder {
        TextView tvArea;
        View leftLine;
    }
}
