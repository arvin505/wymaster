package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.miqtech.master.client.entity.InforBanner;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.InformationAtlasActivity;
import com.miqtech.master.client.ui.InformationTopicActivity;
import com.miqtech.master.client.ui.SubjectActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.salvage.RecyclingPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2015/11/25.
 */
public class ImagePagerInfoBannerAdapter<T> extends RecyclingPagerAdapter {


    private Context context;
    private List<T> list;

    //private MyImageLoader imageLoader ;
    public ImagePagerInfoBannerAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
        //    imageLoader = MyImageLoader.getInstance(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup container) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            convertView = holder.imageView = imageView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final InforBanner bean = (InforBanner) list.get(position);
        AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + bean.getCover() + "!middle1", holder.imageView);
        //图片展示逻辑
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if (bean.getType() == 1) {//类型:1图文
                    intent = new Intent(context, SubjectActivity.class);
                    intent.putExtra("id", bean.getId() + "");
                    intent.putExtra("title", bean.getTitle());
                    intent.putExtra("icon", bean.getCover());
                    intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.MATH);
                    context.startActivity(intent);
                } else if (bean.getType() == 2) {//2专题
                    intent = new Intent();
                    intent.putExtra("activityId", bean.getId());
                    intent.putExtra("zhuanTitle", bean.getTitle());
                    intent.putExtra("url", bean.getCover());
                    intent.setClass(context, InformationTopicActivity.class);
                    context.startActivity(intent);
                } else if (bean.getType() == 3) {//3图集
                    intent = new Intent();
                    intent.putExtra("activityId", bean.getId());
                    intent.setClass(context, InformationAtlasActivity.class);
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
    }
}
