package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.InforItemDetail;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.InfoListActivity;
import com.miqtech.master.client.ui.InformationAtlasActivity;
import com.miqtech.master.client.ui.InformationDetailActivity;
import com.miqtech.master.client.ui.InformationTopicActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.view.RoundedImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/7/7.
 */
public class InfoListAdapter extends BaseAdapter {
    private Context context;
    private List<InforItemDetail>datas;
    private int type=0; //0代表赛事详情页面的咨询动态adapter 1代表资讯动态页面的adapter
    private String roundId;
    public InfoListAdapter(Context context,List<InforItemDetail> datas,int type){
        this.context=context;
        if(type==0) {
            datas.add(0, null); //添加一个头布局
        }
        this.datas=datas;
        this.type=type;
    }
    public void setRoundId(String roundId){
     this.roundId=roundId;
    }
    @Override
    public int getCount() {
        if(datas!=null && datas.size()!=0){
            return datas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.info_dynamic_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(type==0) {
            holder.spliteLine2.setVisibility(View.GONE);
            if (position == 0) {//显示头
                holder.rlTopTitle.setVisibility(View.VISIBLE);
                if(datas!=null && datas.size()==1) {
                    holder.spliteLine.setVisibility(View.GONE);
                }else{
                    holder.spliteLine.setVisibility(View.VISIBLE);
                }
                holder.rlInfoDynamic.setVisibility(View.GONE);
                holder.tvSeeWhole.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, InfoListActivity.class);
                        intent.putExtra("roundId",roundId);
                        context.startActivity(intent);
                    }
                });
            } else {
                holder.rlTopTitle.setVisibility(View.GONE);
                holder.spliteLine.setVisibility(View.GONE);
                holder.rlInfoDynamic.setVisibility(View.VISIBLE);
                showData(position, holder);
            }
        }else{
            holder.spliteLine2.setVisibility(View.VISIBLE);
            holder.rlTopTitle.setVisibility(View.GONE);
            holder.spliteLine.setVisibility(View.GONE);
            holder.rlInfoDynamic.setVisibility(View.VISIBLE);
            showData(position, holder);
        }
        return convertView;
    }
    private void showData(int position, final ViewHolder myHolder){
        if (datas.isEmpty()) {
            return;
        }
        final InforItemDetail inforItemDetail= datas.get(position);
        //显示头像
        if (!TextUtils.isEmpty(inforItemDetail.getIcon())) {
            AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + inforItemDetail.getIcon(), myHolder.ivInfoPoster);
        } else {
            myHolder.ivInfoPoster.setImageResource(R.drawable.default_head);
        }

        if (!TextUtils.isEmpty(inforItemDetail.getTitle())) {
            myHolder.tvTitle.setText(inforItemDetail.getTitle());
        } else {
            myHolder.tvTitle.setText("");
        }
        //显示评论内容
        if (!TextUtils.isEmpty(inforItemDetail.getBrief())) {
            myHolder.tvContent.setText(inforItemDetail.getBrief());
        } else {
            myHolder.tvContent.setText("");
        }
        myHolder.tvReadNum.setText(inforItemDetail.getRead_num()+"阅");
        //是否显示
        if(inforItemDetail.getType()==4){
         myHolder.ivVideoPic.setVisibility(View.VISIBLE);
        }else{
            myHolder.ivVideoPic.setVisibility(View.GONE);
        }
        //TODO 处理
        myHolder.ll_info_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=null;
                if (inforItemDetail.getType() == 1) {//类型:1图文  跳转
                    intent = new Intent(context, InformationDetailActivity.class);
                    intent.putExtra("id", inforItemDetail.getId() + "");
                    intent.putExtra("type", inforItemDetail.getType());
                    context.startActivity(intent);
                }  else if (inforItemDetail.getType() == 4) {   //视频
                    Intent matchIntent = new Intent(context, InformationDetailActivity.class);
                    //TODO 此处需要传入指定的vid   为了测试现在暂时写死
                    matchIntent.putExtra("id", inforItemDetail.getId() + "");
                    matchIntent.putExtra("type", InformationDetailActivity.INFORMATION_VIDEO);
                    context.startActivity(matchIntent);
                }
            }
        });
    }
    static class ViewHolder {
        @Bind(R.id.ll_info_item)
        LinearLayout ll_info_item; //整个item
        @Bind(R.id.rlTopTitle)
        RelativeLayout rlTopTitle; //顶部资讯动态标题
        @Bind(R.id.rlInfoDynamic)
        RelativeLayout rlInfoDynamic;
        @Bind(R.id.tvSeeWhole)
        TextView tvSeeWhole;    //查看全部
        @Bind(R.id.spliteLine)
        View spliteLine;       //标题和listView分割线
        @Bind(R.id.spliteLine2)
        View spliteLine2;    //listView item分割线
        @Bind(R.id.ivInfoPoster)
        RoundedImageView ivInfoPoster;    //海报
        @Bind(R.id.tvTitle)
        TextView tvTitle;   //资讯动态标题
        @Bind(R.id.tvContent)
        TextView tvContent; //类容
        @Bind(R.id.tvReadNum)
        TextView tvReadNum; //阅读数量
        @Bind(R.id.ivVideoPic)
        ImageView ivVideoPic;//视频图标
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
