package com.miqtech.master.client.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.view.CircleImageView;

import java.util.List;

/**
 * Created by Administrator on 2015/12/1.
 */
public class UserSearchAdapter extends BaseAdapter {
    private List<User> mDatas;
    private Context mContext;

    public UserSearchAdapter(Context context, List<User> data) {
        this.mContext = context;
        this.mDatas = data;
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

    public void setData(List<User> matchs) {
        this.mDatas = matchs;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final User user = mDatas.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_user_item, null);
            holder.imageView = (CircleImageView) convertView.findViewById(R.id.im_header);
            holder.tvNickName = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AsyncImage.loadAvatar(mContext, HttpConstant.SERVICE_UPLOAD_AREA + user.getIcon(), holder.imageView);
        holder.tvNickName.setText(user.getNickname());
        if (user.getSex().equals("0")) {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_boy);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.tvNickName.setCompoundDrawables(null, null, drawable, null);
        } else {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_girl);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.tvNickName.setCompoundDrawables(null, null, drawable, null);
        }
        return convertView;
    }

    class ViewHolder {
        CircleImageView imageView;//= (CircleImageView) item.findViewById(R.id.battle_item_head);
        TextView tvNickName; // = (TextView) item.findViewById(R.id.battle_item_nickname);
    }
}
