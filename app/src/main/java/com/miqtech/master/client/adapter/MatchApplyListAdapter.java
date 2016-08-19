package com.miqtech.master.client.adapter;

import java.util.List;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MatchApplyListAdapter extends BaseAdapter {
    private Context context;
    private List<User> teammates;
    private HandleJoinStatusListener listener;

    public MatchApplyListAdapter(Context context, List<User> teammates, HandleJoinStatusListener listener) {
        this.context = context;
        this.teammates = teammates;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return teammates.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return teammates.get(position);
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
        final int index;
        if (v == null) {
            v = View.inflate(context, R.layout.layout_match_apply_item, null);
            holder = new ViewHolder();
            holder.ivHeader = (ImageView) v.findViewById(R.id.ivHeader);
            holder.tvApplyName = (TextView) v.findViewById(R.id.tvApplyName);
            holder.tvApplyStatus = (TextView) v.findViewById(R.id.tvApplyStatus);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        index = position;
        holder.tvApplyStatus.setBackgroundResource(R.drawable.shape_btn_tojoin);
        holder.tvApplyStatus.setTextColor(context.getResources().getColor(R.color.orange));
        holder.tvApplyStatus.setText(context.getResources().getString(R.string.agree));
        holder.tvApplyStatus.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String teammateId = teammates.get(index).getId();
                listener.acceptJoin(teammateId);
            }
        });

        AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + teammates.get(position).getIcon(),
                holder.ivHeader);
        holder.tvApplyName.setText(teammates.get(position).getNickname());

        return v;
    }

    public interface HandleJoinStatusListener {
        void acceptJoin(String teammateId);
    }

    private class ViewHolder {
        ImageView ivHeader;
        TextView tvApplyName;
        TextView tvApplyStatus;
    }

}
