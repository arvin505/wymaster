package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.HotInternetBarInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.InternetBarActivityV2;
import com.miqtech.master.client.utils.AsyncImage;

import java.util.List;

/**
 * Created by Administrator on 2016/3/23.
 */
public class HotInternetBarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HotInternetBarInfo> hotInternetBarInfoList;
    private Context context;
    private LayoutInflater layoutInflater;

    public HotInternetBarAdapter(Context context, List<HotInternetBarInfo> hotInternetBarInfoList) {
        this.hotInternetBarInfoList = hotInternetBarInfoList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);

    }

    @Override
    public int getItemCount() {
        return hotInternetBarInfoList.size();
//        return 6;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.layout_hot_internetbar_item, parent, false);
        RecyclerView.ViewHolder holder = new NetBarViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NetBarViewHolder netBarViewHolder = (NetBarViewHolder) holder;
        if (!hotInternetBarInfoList.isEmpty() && hotInternetBarInfoList.size() > position) {
            HotInternetBarInfo bean = hotInternetBarInfoList.get(position);

            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + bean.getIcon(), netBarViewHolder.icon);
            if (!TextUtils.isEmpty(bean.getNetbar_name())) {
                netBarViewHolder.name.setText(bean.getNetbar_name());
            } else {
                netBarViewHolder.name.setText("");
            }
            final int id = bean.getId();
            netBarViewHolder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toNextActivity(id);
                }
            });
            netBarViewHolder.tv_get_discount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toNextActivity(id);
                }
            });
        }
    }

    private void toNextActivity(int id) {
        Intent intent = new Intent(context, InternetBarActivityV2.class);
        intent.putExtra("netbarId", id + "");
        context.startActivity(intent);
    }

    class NetBarViewHolder extends RecyclerView.ViewHolder {

        TextView name, tv_get_discount;
        ImageView icon;

        public NetBarViewHolder(View view) {
            super(view);
            icon = (ImageView) view.findViewById(R.id.img_netbar_icon);
            name = (TextView) view.findViewById(R.id.tv_netbat_name);
            tv_get_discount = (TextView) view.findViewById(R.id.tv_get_discount);

        }
    }

}
