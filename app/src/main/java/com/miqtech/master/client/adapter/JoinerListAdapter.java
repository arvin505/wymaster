package com.miqtech.master.client.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.MatchJoiner;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.view.CircleImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/2 0002.
 */
public class JoinerListAdapter extends BaseAdapter {
    private Context context;
    private List<MatchJoiner> joiners;

    public JoinerListAdapter(Context context, List<MatchJoiner> joiners) {
        this.context = context;
        this.joiners = joiners;
    }

    @Override
    public int getCount() {
        return joiners.size();
    }

    @Override
    public Object getItem(int position) {
        return joiners.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder ;
        if(v == null){
            v = View.inflate(context,R.layout.layout_joiner_item,null);
            holder = new ViewHolder(v);
            v.setTag(holder);
        }else{
            holder = (ViewHolder)v.getTag();
        }
        AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + joiners.get(position).getIcon(), holder.ivUserHeader);
        if(!TextUtils.isEmpty(joiners.get(position).getNickname())){
            holder.tvUserName.setText(joiners.get(position).getNickname());
        }
        return v;
    }

    static class ViewHolder {
        @Bind(R.id.ivUserHeader)
        CircleImageView ivUserHeader;
        @Bind(R.id.tvUserName)
        TextView tvUserName;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
