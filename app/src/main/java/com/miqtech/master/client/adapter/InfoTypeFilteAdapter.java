package com.miqtech.master.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.InforCatalog;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;

import java.util.List;

/**
 * Created by Administrator on 2016/4/14.
 */
public class InfoTypeFilteAdapter extends BaseAdapter {
    private Context mContext;
    private List<InforCatalog> mDatas;
    private LayoutInflater inflater;

    public InfoTypeFilteAdapter(Context context, List<InforCatalog> datas) {
        this.mContext = context;
        this.mDatas = datas;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
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
            convertView = inflater.inflate(R.layout.layout_video_type_item, null);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_type_title);
            holder.imgIcon = (ImageView) convertView.findViewById(R.id.img_type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        InforCatalog data = mDatas.get(position);
        if (data != null) {
            holder.tvTitle.setText(data.getParent().getName());
            AsyncImage.loadImageWithResId(HttpConstant.SERVICE_UPLOAD_AREA + data.getParent().getImg(),R.drawable.icon_lol, holder.imgIcon);
        }
        return convertView;
    }

    class ViewHolder {
        TextView tvTitle;
        ImageView imgIcon;
    }
}
