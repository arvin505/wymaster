package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.YueZhan2Info;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.PersonalHomePageActivity;
import com.miqtech.master.client.ui.YueZhanDetailsActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.view.CircleImageView;

import java.util.List;

/**
 * Created by Administrator on 2015/12/8 0008.
 */
public class YueZhanListAdapter extends BaseAdapter {

    private List<YueZhan2Info> mDatas;
    private Context mContext;

    public YueZhanListAdapter(Context context, List<YueZhan2Info> data) {
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final YueZhan2Info battle = mDatas.get(position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.recommend_battle_item, parent, false);
            holder.imageView = (CircleImageView) convertView.findViewById(R.id.battle_item_head);
            holder.tvNickName = (TextView) convertView.findViewById(R.id.battle_item_nickname);
            holder.tvServer = (TextView) convertView.findViewById(R.id.battle_item_server);
            holder.tvCount = (TextView) convertView.findViewById(R.id.battle_item_count);
            holder.tvType = (TextView) convertView.findViewById(R.id.battle_item_type);
            holder.tvStartTime = (TextView) convertView.findViewById(R.id.battle_item_starttime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String header = HttpConstant.SERVICE_UPLOAD_AREA + battle.getReleaser_icon();
        AsyncImage.loadAvatar(mContext, HttpConstant.SERVICE_UPLOAD_AREA + battle.getReleaser_icon(), holder.imageView);
        holder.tvNickName.setText(battle.getNickname());
        LogUtil.e("CoinsTaskAdapter------------------------", battle.getId() + "");
        if (battle.getSex() == 0) {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_boy);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.tvNickName.setCompoundDrawables(null, null, drawable, null);
        } else {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_boy);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.tvNickName.setCompoundDrawables(null, null, drawable, null);
        }

        holder.tvServer.setText(battle.getServer());
        if (battle.getWay() == 1) {  //线上
            holder.tvType.setText(mContext.getResources().getString(R.string.type_online));
        } else if (battle.getWay() == 1) {  //线下
            String server = mContext.getResources().getString(R.string.type_offline);
            if (battle.getNetbar_name() != null && !battle.getNetbar_name().trim().equals("")) {
                server += "/" + battle.getNetbar_name();
            }
            holder.tvType.setText(server);
        }
        holder.tvStartTime.setText(TimeFormatUtil.formatNoYear(battle.getBegin_time()));
        holder.tvCount.setText(battle.getApply_count() + "/" + battle.getPeople_num());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("id", battle.getReleaser_id() + "");
                intent.setClass(mContext, PersonalHomePageActivity.class);
                mContext.startActivity(intent);
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YueZhan2Info yueZhan = (YueZhan2Info) getItem(position);
                Intent intent = new Intent(mContext, YueZhanDetailsActivity.class);
                intent.putExtra("id", yueZhan.getId() + "");
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder {
        CircleImageView imageView;//= (CircleImageView) item.findViewById(R.id.battle_item_head);
        TextView tvNickName; // = (TextView) item.findViewById(R.id.battle_item_nickname);
        TextView tvServer; // = (TextView) item.findViewById(R.id.battle_item_server);
        TextView tvType; // = (TextView) item.findViewById(R.id.battle_item_type);
        TextView tvStartTime; // = (TextView) item.findViewById(R.id.battle_item_starttime);
        TextView tvCount;//= (TextView) item.findViewById(R.id.battle_item_count);
    }
}
