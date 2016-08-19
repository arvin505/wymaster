package com.miqtech.master.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.MyRegister;

import java.util.List;

/**
 * Created by Administrator on 2015/12/10.
 */
public class RegisterGalleryAdapter extends BaseAdapter {

    private Context context;
    private List<MyRegister> myRegisterList;

    public RegisterGalleryAdapter(Context context, List<MyRegister> myRegisterList) {
        this.context = context;
        this.myRegisterList = myRegisterList;
    }

    @Override
    public int getCount() {
        return myRegisterList.size();
    }

    @Override
    public Object getItem(int position) {
        return myRegisterList.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_register_grallery_item, null);
            holder.right = (ImageView) convertView.findViewById(R.id.right_arrows_register_item);
            holder.title = (TextView) convertView.findViewById(R.id.title_register_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MyRegister bean = myRegisterList.get(position);
        if (position == 0) {
            holder.right.setVisibility(View.GONE);
        } else {
            holder.right.setVisibility(View.VISIBLE);
        }
        holder.title.setText(bean.getTitle());

        if (0 == bean.getType()) {
            holder.right.setImageResource(R.drawable.register_right_gray);
            holder.title.setTextColor(context.getResources().getColor(R.color.gray));
        } else if (1 == bean.getType()) {
            holder.right.setImageResource(R.drawable.register_right_oranger);
            holder.title.setTextColor(context.getResources().getColor(R.color.orange));
        }
        return convertView;
    }

    class ViewHolder {
        ImageView right;
        TextView title;
    }
}
