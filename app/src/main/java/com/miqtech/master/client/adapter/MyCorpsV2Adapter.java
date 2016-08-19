package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.Corps;
import com.miqtech.master.client.ui.CorpsDetailsV2Activity;
import com.miqtech.master.client.ui.OfficalEventActivity;

import java.util.List;

/**
 * Created by Administrator on 2016/5/18.
 */
public class MyCorpsV2Adapter extends BaseAdapter {


    private Context context;
    private List<Corps> corpsList;
    private LayoutInflater inflater;

    public MyCorpsV2Adapter(Context mContext, List<Corps> corpsList) {
        this.context = mContext;
        this.corpsList = corpsList;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return corpsList.size();
    }

    @Override
    public Object getItem(int position) {
        return corpsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_crops_v2_item, parent, false);
            holder = new ViewHolder();
            holder.flMatch = (FrameLayout) convertView.findViewById(R.id.flCorpsItemMatch);
            holder.tvMatchName = (TextView) convertView.findViewById(R.id.tvCorpsItemMatchName);
            holder.tvMatchRound = (TextView) convertView.findViewById(R.id.tvCorpsItemMatchRound);

            holder.flCoprs = (FrameLayout) convertView.findViewById(R.id.flCorpsItemCorps);
            holder.tvCorpsName = (TextView) convertView.findViewById(R.id.tvCorpsItemCorpsName);
            holder.tvCorpsNum = (TextView) convertView.findViewById(R.id.tvCorpsItemCorpsNum);
            holder.tvCorpsTotalNum = (TextView) convertView.findViewById(R.id.tvCorpsItemCorpsToatlNum);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Corps bean = corpsList.get(position);

        //显示战队
        if (!TextUtils.isEmpty(bean.getTeam_name())) {
            holder.tvCorpsName.setText(bean.getTeam_name());
        } else {
            holder.tvCorpsName.setText("");
        }

        //显示赛事名称
        if (!TextUtils.isEmpty(bean.getTitle())) {
            holder.tvMatchName.setText(bean.getTitle());
        } else {
            holder.tvMatchName.setText("");
        }

        //显示人数
        if (bean.getTotal_num() < 6) {
            holder.tvCorpsNum.setText(bean.getNum() + "");
        } else {
            holder.tvCorpsNum.setText(5 + "");
        }

        //显示总人数
        if (bean.getTotal_num() < 6) {
            holder.tvCorpsTotalNum.setText("/" + bean.getTotal_num());
        } else {
            holder.tvCorpsTotalNum.setText("/" + 5);
        }

        holder.tvMatchRound.setText("第" + bean.getRound() + "场");


        holder.flCoprs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CorpsDetailsV2Activity.class);
                intent.putExtra("teamId", bean.getTeam_Id());
                intent.putExtra("matchId", bean.getActivity_id());
                intent.putExtra("isJoin", bean.getIs_join());
                context.startActivity(intent);
            }
        });

        holder.flMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OfficalEventActivity.class);
                intent.putExtra("matchId", bean.getActivity_id() +"");
                context.startActivity(intent);
            }
        });
        return convertView;
    }






    class ViewHolder {
        FrameLayout flMatch;
        TextView tvMatchName;
        TextView tvMatchRound;

        FrameLayout flCoprs;
        TextView tvCorpsName;
        TextView tvCorpsNum;
        TextView tvCorpsTotalNum;
    }

}
