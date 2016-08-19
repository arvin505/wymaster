package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.CommentInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.ImagePagerActivity;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.PersonalHomePageActivity;
import com.miqtech.master.client.ui.ReportActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/17.
 */
public class NetBarCommentListViewAdapter extends BaseAdapter {


    private Context context;
    private List<CommentInfo> commentinfoList;
    private LayoutInflater mInflater;
    private List<ImageView> imageViews = new ArrayList<ImageView>();

    public NetBarCommentListViewAdapter(Context context, List<CommentInfo> commentinfoList) {
        this.context = context;
        this.commentinfoList = commentinfoList;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return commentinfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentinfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        MyViewHolder holder = null;
        if (view == null) {
            view = mInflater.inflate(R.layout.layout_detail_comment_item_v2, parent, false);
            holder = new MyViewHolder();

            holder.headIcon = (ImageView) view.findViewById(R.id.rating_people_icon_item);
            holder.name = (TextView) view.findViewById(R.id.comment_name_item);
            holder.starLevel = (RatingBar) view.findViewById(R.id.comment_level_item);
            holder.details = (TextView) view.findViewById(R.id.comment_details_item);
            holder.img_one = (ImageView) view.findViewById(R.id.image_one_iv);
            holder.img_two = (ImageView) view.findViewById(R.id.image_two_iv);
            holder.img_three = (ImageView) view.findViewById(R.id.image_three_iv);
            holder.time = (TextView) view.findViewById(R.id.comment_time_item);
//            holder.line = (TextView) view.findViewById(R.id.tv_bottm_line_item);
            holder.image_ll = (LinearLayout) view.findViewById(R.id.three_image_ll);
            holder.reportLl = (LinearLayout) view.findViewById(R.id.report_comment_ll_item);

            holder.praiseTv = (TextView) view.findViewById(R.id.praise_comment_tv_item);
            holder.praiseIv = (ImageView) view.findViewById(R.id.praise_comment_iv_item);
            holder.praiseLl = (LinearLayout) view.findViewById(R.id.praise_comment_ll_item);

            view.setTag(holder);
        } else {
            holder = (MyViewHolder) view.getTag();
        }

        showComment(position, holder);

        return view;
    }

    /**
     * 显示评论数据
     *
     * @param position 在视图中的位子
     * @param hh
     */
    private void showComment(int position, MyViewHolder hh) {
        if (!commentinfoList.isEmpty()) {
            CommentInfo info = commentinfoList.get(position);
            if (1 == info.getIs_anonymous()) {// 匿名
                hh.name.setText(context.getResources().getString(R.string.anonymous_users));
                hh.headIcon.setImageResource(R.drawable.cryptonym);
            } else {
                hh.name.setText(info.getNickname());
                if (!TextUtils.isEmpty(info.getIcon())) {//避免在加载更多时出现闪一下的现状
                    if (hh.headIcon.getTag() == null) {
                        hh.headIcon.setTag("");
                    }
                    if (!hh.headIcon.getTag().equals(info.getIcon())) {//与标记不同时才加载图片
                        AsyncImage.loadCommentHeadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + info.getIcon(), hh.headIcon);// 显示头像
                        hh.headIcon.setTag(info.getIcon());
                    }
                }

            }

            if (WangYuApplication.getUser(context) != null) {
                if (Integer.valueOf(WangYuApplication.getUser(context).getId()) == info.getUser_id()) {
                    hh.reportLl.setVisibility(View.GONE);
                } else {
                    hh.reportLl.setVisibility(View.VISIBLE);
                }
            }
            if (info.getAvgScore() < 1) {// 显示星级
                hh.starLevel.setVisibility(View.INVISIBLE);
            } else {
                hh.starLevel.setNumStars((int) info.getAvgScore());
                hh.starLevel.setRating(info.getAvgScore());
            }

            if (1 == info.getIsPraised()) {// 关注（赞）的按钮状态
                hh.praiseIv.setImageResource(R.drawable.comment_praise_yes);
            } else if (0 == info.getIsPraised()) {
                hh.praiseIv.setImageResource(R.drawable.comment_praise_no);
            }

            if (info.getIs_no_comment() == 1) {
                hh.details.setTextColor(context.getResources().getColor(R.color.lv_item_content_text));
                hh.details.setText(context.getResources().getString(R.string.no_comment));
                hh.image_ll.setVisibility(View.GONE);
            } else {
                showImage(info.getImgs(), hh);
                hh.details.setTextColor(context.getResources().getColor(R.color.black_extend_intro));
                if (info != null) {
                    hh.details.setText(info.getContent());// 评论详情
                } else {
                    hh.details.setText("");
                }
            }
            hh.praiseTv.setText(getNum(info.getPraised()));// 关注（赞）的人事
            hh.time.setText(DateUtil.getStringDate(info.getCreate_date()));// 显示时间

            OnMyClickListener onMyClickListener = new OnMyClickListener(position, info);
            hh.headIcon.setOnClickListener(onMyClickListener);
            hh.img_one.setOnClickListener(onMyClickListener);
            hh.img_two.setOnClickListener(onMyClickListener);
            hh.img_three.setOnClickListener(onMyClickListener);
            hh.reportLl.setOnClickListener(onMyClickListener);
            hh.praiseLl.setOnClickListener(onMyClickListener);
        }
    }

    private class OnMyClickListener implements View.OnClickListener {
        int position;
        CommentInfo info;

        public OnMyClickListener(int position, CommentInfo info) {
            this.position = position;
            this.info = info;
        }

        @Override
        public void onClick(View v) {
            Intent ii = null;
            switch (v.getId()) {
                case R.id.praise_comment_ll_item:// 点赞
                    onItemPraiseAndHideView.praisingComment(position);
                    break;
                case R.id.rating_people_icon_item:// 点击头像到个人主页
                    if (1 != info.getIs_anonymous()) {
                        ii = new Intent(context, PersonalHomePageActivity.class);
                        ii.putExtra("id", info.getUser_id() + "");
                        context.startActivity(ii);
                    } else {
                        Toast.makeText(context, "无法查看匿名用户", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.image_one_iv://查看图片
                    seeBigImage(info, 0);
                    break;
                case R.id.image_two_iv://
                    seeBigImage(info, 1);
                    break;
                case R.id.image_three_iv://
                    seeBigImage(info, 2);
                    break;
                case R.id.report_comment_ll_item://举报
                    if (WangYuApplication.getUser(context) != null) {
                        ii = new Intent(context, ReportActivity.class);
                        ii.putExtra("type", 4);// 举报类别:1.用户2.评论3.约战4网吧评论
                        ii.putExtra("targetId", info.getId());// type=1被举报用户的id,type=2评论id,type=3约战id,type=4网吧评价id
                        context.startActivity(ii);
                    } else {
                        ii = new Intent(context, LoginActivity.class);
                        context.startActivity(ii);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 查看图片
     *
     * @param info
     * @param j
     */
    private void seeBigImage(CommentInfo info, int j) {
        if (info.getImgs().length() > 0) {
            String[] urls = info.getImgs().split(",");
            List<Map<String, String>> imgs = new ArrayList<Map<String, String>>();
            for (int i = 0; i < urls.length; i++) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("url", urls[i]);
                imgs.add(map);
            }
            Intent intent = new Intent();
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable("images", (Serializable) imgs);
            bundle2.putInt("image_index", j);
            intent.putExtras(bundle2);
            intent.setClass(context, ImagePagerActivity.class);
            context.startActivity(intent);
        }
    }

    /**
     * 显示评价的图片
     *
     * @param Str 图片地址集
     * @param hh
     */
    private void showImage(String Str, MyViewHolder hh) {
        if (Str == null || "".equals(Str)) {
            hh.image_ll.setVisibility(View.GONE);
            return;
        }
        hh.image_ll.setVisibility(View.VISIBLE);
        String[] urls = Str.split(",");
        imageViews.clear();
        imageViews.add(hh.img_one);
        imageViews.add(hh.img_two);
        imageViews.add(hh.img_three);

        if (hh.image_ll.getTag() == null) {
            hh.image_ll.setTag("");
        }

        if (!hh.image_ll.getTag().equals(Str)) {//与标记不同时才加载图片
            for (int i = 0; i < urls.length && i < 3; i++) {
                AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + urls[i] + "!small", imageViews.get(i));
            }
            hh.image_ll.setTag(Str);
        }
    }

    private String getNum(int i) {
        String str = "";
        if (i < 1000) {
            str = i + "";
            // str = "999+";
        } else {
            str = "999+";
        }
        return str;
    }

    /**
     * 对item进行处理
     */
    public interface OnItemPraiseAndHideView {
        /**
         * 点赞
         *
         * @param position 在数据中的位置
         */
        void praisingComment(int position);

    }

    ;

    public OnItemPraiseAndHideView onItemPraiseAndHideView;

    public void setOnItemPraiseAndHideView(OnItemPraiseAndHideView onItemPraiseAndHideView) {
        this.onItemPraiseAndHideView = onItemPraiseAndHideView;
    }


    class MyViewHolder {
        ImageView headIcon;// 头像
        TextView name;// 名字
        RatingBar starLevel;// 评价用户的等级
        TextView details;// 评价详情
        ImageView img_one;// 图片1
        ImageView img_two;// 图片2
        ImageView img_three;// 图片3
        TextView time;// 评价时间
        //        TextView line;// 底部的线
        LinearLayout image_ll;
        LinearLayout reportLl;//举报
        TextView praiseTv;//点赞数量
        ImageView praiseIv;//点赞图片
        LinearLayout praiseLl;//设置点赞监听
    }
}
