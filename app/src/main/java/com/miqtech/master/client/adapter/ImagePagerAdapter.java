package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.Banner;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.DownloadWebView;
import com.miqtech.master.client.ui.EventDetailActivity;
import com.miqtech.master.client.ui.InformationDetailActivity;
import com.miqtech.master.client.ui.OfficalEventActivity;
import com.miqtech.master.client.ui.RecreationMatchDetailsActivity;
import com.miqtech.master.client.ui.SubjectActivity;
import com.miqtech.master.client.ui.YueZhanDetailsActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.Types;
import com.salvage.RecyclingPagerAdapter;

import java.util.List;

/**
 * Created by arvin on 2015/11/15.
 */
public class ImagePagerAdapter<T> extends RecyclingPagerAdapter {

    private Context context;
    private List<T> list;

    public ImagePagerAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup container) {
        ViewHolder holder;
        final Banner banner = (Banner) list.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.headline_item, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.img_headlineitem_banner);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.lable = (TextView) convertView.findViewById(R.id.tv_headlineitem_lable);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
    }

        holder.lable.setText("" + Types.
                BANNERTYPE.get(banner.getType()));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                switch (banner.getType()) {
                    case 5://5-推广/广告
                        intent = new Intent(context, SubjectActivity.class);
                        intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.TUIGUANG);
                        intent.putExtra("coins_ad_url", banner.getUrl());
                        intent.putExtra("title", banner.getTitle());
                       // Toast.makeText(context,"" + banner.getUrl(),0).show();
                        if (banner.getUrl()!=null && banner.getUrl().startsWith("http"))
                        context.startActivity(intent);
                        break;
                    case 10: //10官方比赛
                        intent = new Intent();
                        intent.setClass(context, OfficalEventActivity.class);
                        intent.putExtra("matchId", banner.getTargetId() + "");
                        context.startActivity(intent);
                        break;
                    case 11:  //11娱乐赛
                        intent = new Intent();
                        intent.setClass(context, RecreationMatchDetailsActivity.class);
                        intent.putExtra("id", banner.getTargetId() + "");
                        context.startActivity(intent);
                        break;
                    case 12:  //12约战
                        intent = new Intent(context, YueZhanDetailsActivity.class);
                        intent.putExtra("id", banner.getTargetId() + "");
                        context.startActivity(intent);
                        break;
                    case 15: //15资讯
                        intent = new Intent();
                        intent.setClass(context, InformationDetailActivity.class);
                        intent.putExtra("id", banner.getTargetId() + "");
                        context.startActivity(intent);
                    break;
                 case 16: //16自发赛
                      intent = new Intent();
                      intent.setClass(context, EventDetailActivity.class);
                      intent.putExtra("matchId", banner.getTargetId() + "");
                      context.startActivity(intent);
                      break;
                    case 13:  //13福利
                        intent = new Intent(context, SubjectActivity.class);
                        intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.TUIGUANG);
                        //Toast.makeText(context,"" + banner.getUrl(),0).show();
                        intent.putExtra("coins_ad_url", banner.getUrl());
                        intent.putExtra("title", banner.getTitle());
                        if (banner.getUrl()!=null && banner.getUrl().startsWith("http"))
                        context.startActivity(intent);
                        break;
                    case 14: //下载
                        intent = new Intent(context, DownloadWebView.class);
                        intent.putExtra("download_url", banner.getUrl());
                        intent.putExtra("title", banner.getTitle());
                        if (banner.getUrl()!=null && banner.getUrl().startsWith("http"))
                        context.startActivity(intent);
                }
            }
        });
        AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + banner.getImg()+ "!middle1", holder.imageView);
        LogUtil.d("ImagePagerAdapter","URL:::"+HttpConstant.SERVICE_UPLOAD_AREA + banner.getImg()+ "!middle1");
        //图片展示逻辑
        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView lable;
    }
}
