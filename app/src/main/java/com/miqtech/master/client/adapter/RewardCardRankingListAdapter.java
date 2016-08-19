package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.RewardGrade;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.ImagePagerActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.BitmapUtil;
import com.miqtech.master.client.view.CircleImageView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 悬赏令排行榜
 * Created by zhaosentao on 2016/7/27.
 */
public class RewardCardRankingListAdapter extends BaseAdapter {
    private Context context;
    private List<RewardGrade> rewardGradeList;

    private float distance40Dp;
    private float distance30Dp;

    private float distance5Dp;
    private float distance15Dp;

    //    private float distance12Dp;
//    private float distance3Dp;
//    private float distance42Dp;
//    private float distance32Dp;
    private RelativeLayout.LayoutParams paramsHead;
    private boolean isFirst = true;

    public RewardCardRankingListAdapter(Context context, List<RewardGrade> rewardGradeList) {
        this.context = context;
        this.rewardGradeList = rewardGradeList;

        distance40Dp = context.getResources().getDimension(R.dimen.distance_40dp);
        distance30Dp = context.getResources().getDimension(R.dimen.distance_30dp);

        distance5Dp = context.getResources().getDimension(R.dimen.distance_5dp);
        distance15Dp = context.getResources().getDimension(R.dimen.distance_15dp);

//        distance3Dp = context.getResources().getDimension(R.dimen.distance_3dp);
//        distance12Dp = context.getResources().getDimension(R.dimen.distance_12dp);
//        distance32Dp = context.getResources().getDimension(R.dimen.distance_32dp);
//        distance42Dp = context.getResources().getDimension(R.dimen.distance_42dp);
    }


    @Override
    public int getCount() {
        return rewardGradeList.size();
    }

    @Override
    public Object getItem(int position) {
        return rewardGradeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_reward_up_page_cardview_item_listview_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        showData(holder, rewardGradeList.get(position), position);
        return convertView;
    }

    private void showData(final ViewHolder holder, RewardGrade bean, int position) {
        if (bean == null) {
            return;
        }

        //加载头像
        AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + bean.getIcon() + "!small", holder.ivHead);

//        AsyncImage.loadNetPhotoWithListener(HttpConstant.SERVICE_UPLOAD_AREA + bean.getIcon() + "!small", new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String s, View view) {
//            }
//
//            @Override
//            public void onLoadingFailed(String s, View view, FailReason failReason) {
//                holder.ivHead.setImageResource(R.drawable.default_head);
//            }
//
//            @Override
//            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                Drawable drawable = new BitmapDrawable(BitmapUtil.makeRoundCorner(bitmap));
//                holder.ivHead.setImageDrawable(drawable);
//            }
//
//            @Override
//            public void onLoadingCancelled(String s, View view) {
//            }
//        });


        //显示宁城
        if (!TextUtils.isEmpty(bean.getNickname())) {
            holder.tvName.setText(bean.getNickname());
        } else {
            holder.tvName.setText("");
        }

        //显示成绩
        if (!TextUtils.isEmpty(bean.getGrade())) {
            holder.tvExplain.setText(bean.getGrade());
        } else {
            holder.tvExplain.setText("");
        }

        holder.ivRanking.setVisibility(View.VISIBLE);
        holder.ivIdTags.setVisibility(View.VISIBLE);
        holder.tvRanking.setText("");
        changeSize(holder, position);
        if (position == 0) {
            holder.ivRanking.setImageResource(R.drawable.wanted_first);
            holder.ivIdTags.setImageResource(R.drawable.wanted_golden);
        } else if (position == 1) {
            holder.ivRanking.setImageResource(R.drawable.wanted_secend);
            holder.ivIdTags.setImageResource(R.drawable.wanted_silver);
        } else if (position == 2) {
            holder.ivRanking.setImageResource(R.drawable.wanted_three);
            holder.ivIdTags.setImageResource(R.drawable.wanted_bronze);
        } else {
            holder.ivRanking.setVisibility(View.GONE);
            holder.ivIdTags.setVisibility(View.GONE);
            holder.tvRanking.setText((position + 1) + "");
        }
        seeRecord(holder, bean);
    }

    private void changeSize(ViewHolder holder, int position) {
        if (isFirst) {
            paramsHead = (RelativeLayout.LayoutParams) holder.ivHead.getLayoutParams();
            isFirst = false;
        }
        if (position > 2) {
            paramsHead.height = (int) distance30Dp;
//            paramsHead.width = (int) distance32Dp;
//            paramsHead.setMargins((int) distance12Dp, 0, 0, 0);
            paramsHead.width = (int) distance30Dp;
            paramsHead.setMargins((int) distance15Dp, 0, 0, 0);
            holder.ivHead.setLayoutParams(paramsHead);
        } else {
            paramsHead.height = (int) distance40Dp;
//            paramsHead.width = (int) distance42Dp;
//            paramsHead.setMargins((int) distance3Dp, 0, 0, 0);
            paramsHead.width = (int) distance40Dp;
            paramsHead.setMargins((int) distance5Dp, 0, 0, 0);
            holder.ivHead.setLayoutParams(paramsHead);
        }
    }


    /**
     * 查看成绩
     *
     * @param holder
     * @param bean
     */
    private void seeRecord(ViewHolder holder, final RewardGrade bean) {
        holder.tvSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(bean.getImg())) {
                    Toast.makeText(context, "该选手暂时成绩图", Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<Map<String, String>> imgs = new ArrayList<Map<String, String>>();
                Map<String, String> map = new HashMap<String, String>();
                map.put("url", bean.getImg());
                imgs.add(map);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", imgs);
                bundle.putInt("isHideGallery", 1);
                intent.putExtras(bundle);
                intent.setClass(context, ImagePagerActivity.class);
                context.startActivity(intent);
            }
        });
    }

    class ViewHolder {
        @Bind(R.id.rewardUpCardListItemRlHeadIcon)
        RelativeLayout rlHead;//头像
        @Bind(R.id.rewardUpCardListItemIvHeadIcon)
        CircleImageView ivHead;//头像
        @Bind(R.id.rewardUpCardListItemIvRanking)
        ImageView ivRanking;//第几名的标志，头像那边的
        @Bind(R.id.rewardUpCardListItemTvName)
        TextView tvName;//名称
        @Bind(R.id.rewardUpCardListItemIvIdTags)
        ImageView ivIdTags;//第几名的标识，名称旁边的
        @Bind(R.id.rewardUpCardListItemTvExplain)
        TextView tvExplain;//名称下面的解释语
        @Bind(R.id.rewardUpCardListItemTvSee)
        TextView tvSee;//查看
        @Bind(R.id.rewardUpCardListItemTvRanking)
        TextView tvRanking;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
