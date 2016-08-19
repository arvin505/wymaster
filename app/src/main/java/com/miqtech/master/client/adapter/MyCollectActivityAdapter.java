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
import com.miqtech.master.client.entity.MyAmuse;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/1/18.
 */
public class MyCollectActivityAdapter extends BaseAdapter {

    private Context context;
    private List<MyAmuse> matchs;

    public MyCollectActivityAdapter(Context context, List<MyAmuse> matchs) {
        this.context = context;
        this.matchs = matchs;
    }


    public void setData(List<MyAmuse> matchs) {
        this.matchs = matchs;
    }

    @Override
    public int getCount() {
        return matchs.size();
    }

    @Override
    public Object getItem(int position) {
        return matchs.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_my_amuse_item, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.my_amuse_icon_item);
            holder.title = (TextView) convertView.findViewById(R.id.my_amuse_title_item);
            holder.statue = (TextView) convertView.findViewById(R.id.my_amuse_statu_item);
            holder.time = (TextView) convertView.findViewById(R.id.my_amuse_time_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MyAmuse match = matchs.get(position);

        AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + match.getIcon(), holder.icon);

        if (!TextUtils.isEmpty(match.getTitle())) {
            holder.title.setText(match.getTitle());
        } else {
            holder.title.setText("");
        }

        if (!TextUtils.isEmpty(match.getStart_time())) {
            holder.time.setText(DateUtil.dateToStrLong(match.getStart_time()));
        } else {
            holder.time.setText("");
        }

        if (match.getStatus() == 1 || match.getStatus() == 2) {//1 报名中, 2 报名预热中, 4 赛事已结束, 5 赛事进行中,
            holder.statue.setBackgroundResource(R.drawable.my_amuseapply_begin);
            holder.statue.setText(context.getResources().getString(R.string.appling));
            holder.statue.setTextColor(context.getResources().getColor(R.color.orange));
        } else if (match.getStatus() == 5) {
            holder.statue.setBackgroundResource(R.drawable.my_amuseapply_begin);
            holder.statue.setText(context.getResources().getString(R.string.proceeding));
            holder.statue.setTextColor(context.getResources().getColor(R.color.orange));
        } else if (match.getStatus() == 4) {
            holder.statue.setBackgroundResource(R.drawable.my_amuseapply_end);
            holder.statue.setText(context.getResources().getString(R.string.overing));
            holder.statue.setTextColor(context.getResources().getColor(R.color.lv_item_content_text));
        }
        return convertView;
    }

    class ViewHolder {
        ImageView icon;
        TextView title;
        TextView statue;
        TextView time;
    }
}
