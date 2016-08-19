package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.CommentInfo;
import com.miqtech.master.client.entity.Eva;
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
 * Created by Administrator on 2016/3/8.
 */
public class NetBarCommentAdapterV2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADER_TYPE = 1;
    private static final int COMMENT_TYPE = 2;
    private static final int FOOT_TYPE = 3;

    private Context context;
    private List<CommentInfo> commentinfoList;
    private LayoutInflater mInflater;
    private Eva eva;
    private List<ImageView> imageViews = new ArrayList<ImageView>();
    private boolean isFirst = true;

    public NetBarCommentAdapterV2(Context context, List<CommentInfo> commentinfoList, Eva eva) {
        this.context = context;
        this.commentinfoList = commentinfoList;
        this.eva = eva;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
//        if (commentinfoList.size() > 9) {
//            Log.e("getItemCount", commentinfoList.size() + 2 + "", null);
//            return commentinfoList.size() + 2;//当大于9个时除了要显示头部还要显示尾部的加载更多
//        } else {
//            Log.e("getItemCount", commentinfoList.size() + 1 + "", null);
//            return commentinfoList.size() + 1;//除了评论外 还有头部
//        }
        return commentinfoList.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        if (position == 0) {

            return HEADER_TYPE;
        }
//        if (commentinfoList.size() > 9 && position + 1 == getItemCount()) {
//            Log.e("getItemViewType", "FOOT_TYPE", null);
//            return FOOT_TYPE;
//        }
        if (position + 1 == getItemCount()) {

            return FOOT_TYPE;
        }
        return COMMENT_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view = null;
        switch (viewType) {
            case HEADER_TYPE:
                Log.e("onCreateViewHolder", "HEADER_TYPE", null);
                view = mInflater.inflate(R.layout.layout_rating_comment_v2, parent, false);
                holder = new HeaderViewHolder(view);
                break;
            case COMMENT_TYPE:
                Log.e("onCreateViewHolder", "COMMENT_TYPE", null);
                view = mInflater.inflate(R.layout.layout_detail_comment_item_v2, parent, false);
                holder = new CommentViewHolder(view);
                break;
            case FOOT_TYPE:
                Log.e("onCreateViewHolder", "FOOT_TYPE", null);
                view = mInflater.inflate(R.layout.layout_footer_view, parent, false);
                holder = new FooterViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case HEADER_TYPE:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                showScores(headerViewHolder);
                break;
            case COMMENT_TYPE:
                CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
                showComment(position, commentViewHolder);
                break;
            case FOOT_TYPE:
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                onItemPraiseAndHideView.hideView(footerViewHolder.footerView);
                break;
        }
    }

    /**
     * 显示网吧评分
     *
     * @param headerViewHolder 评分模块的
     */
    private void showScores(HeaderViewHolder headerViewHolder) {
        if (eva != null) {
            if (eva.getTotalEva() < 100000) {
                headerViewHolder.netbarByMarking_tv.setText(eva.getTotalEva() + "人评价");// 网吧评论人数
            } else {
                headerViewHolder.netbarByMarking_tv.setText("99999+人评价");
            }
            headerViewHolder.netbarMarking_tv.setText(eva.getAvgScore() + "");// 网吧评价数 例如4.8
            headerViewHolder.environment_rb.setRating(eva.getEnviroment());// 环境
            headerViewHolder.machineConfiguration_rb.setRating(eva.getEquipment());// 机器配置
            headerViewHolder.network_rb.setRating(eva.getNetwork());// 网络流畅
            headerViewHolder.sisterNum_rb.setRating(eva.getService());// 妹子数量评价
        }
    }

    /**
     * 显示评论数据
     *
     * @param position 在视图中的位子
     * @param hh
     */
    private void showComment(int position, CommentViewHolder hh) {
        if (!commentinfoList.isEmpty()) {
            CommentInfo info = commentinfoList.get(position - 1);
            if (1 == info.getIs_anonymous()) {// 匿名
                hh.name.setText(context.getResources().getString(R.string.anonymous_users));
                hh.headIcon.setImageResource(R.drawable.cryptonym);
            } else {
                hh.name.setText(info.getNickname());
                AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + info.getIcon(), hh.headIcon);// 显示头像
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

            OnMyClickListener onMyClickListener = new OnMyClickListener(position - 1, info);
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
    private void showImage(String Str, CommentViewHolder hh) {
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
        for (int i = 0; i < urls.length; i++) {
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + urls[i] + "!small", imageViews.get(i));
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

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        RatingBar environment_rb;
        RatingBar machineConfiguration_rb;
        RatingBar network_rb;
        RatingBar sisterNum_rb;
        TextView netbarMarking_tv;
        TextView netbarByMarking_tv;
        LinearLayout layoutGrade;//最外层布局

        public HeaderViewHolder(View view) {
            super(view);
            environment_rb = (RatingBar) view.findViewById(R.id.netbar_environment_rb);
            machineConfiguration_rb = (RatingBar) view.findViewById(R.id.netbar_machineConfiguration_rb);
            network_rb = (RatingBar) view.findViewById(R.id.netbar_network_rb);
            sisterNum_rb = (RatingBar) view.findViewById(R.id.netbar_sisterNum_rb);
            netbarMarking_tv = (TextView) view.findViewById(R.id.netbar_marking_tv);
            netbarByMarking_tv = (TextView) view.findViewById(R.id.netbar_by_marking_tv);
            layoutGrade = (LinearLayout) view.findViewById(R.id.layout_grade);
        }
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView headIcon;// 头像
        TextView name;// 名字
        RatingBar starLevel;// 评价用户的等级
        TextView details;// 评价详情
        ImageView img_one;// 图片1
        ImageView img_two;// 图片2
        ImageView img_three;// 图片3
        TextView time;// 评价时间
        TextView line;// 底部的线
        LinearLayout image_ll;
        LinearLayout reportLl;//举报
        TextView praiseTv;//点赞数量
        ImageView praiseIv;//点赞图片
        LinearLayout praiseLl;//设置点赞监听

        public CommentViewHolder(View view) {
            super(view);
            headIcon = (ImageView) view.findViewById(R.id.rating_people_icon_item);
            name = (TextView) view.findViewById(R.id.comment_name_item);
            starLevel = (RatingBar) view.findViewById(R.id.comment_level_item);
            details = (TextView) view.findViewById(R.id.comment_details_item);
            img_one = (ImageView) view.findViewById(R.id.image_one_iv);
            img_two = (ImageView) view.findViewById(R.id.image_two_iv);
            img_three = (ImageView) view.findViewById(R.id.image_three_iv);
            time = (TextView) view.findViewById(R.id.comment_time_item);
            line = (TextView) view.findViewById(R.id.tv_bottm_line_item);
            image_ll = (LinearLayout) view.findViewById(R.id.three_image_ll);
            reportLl = (LinearLayout) view.findViewById(R.id.report_comment_ll_item);

            praiseTv = (TextView) view.findViewById(R.id.praise_comment_tv_item);
            praiseIv = (ImageView) view.findViewById(R.id.praise_comment_iv_item);
            praiseLl = (LinearLayout) view.findViewById(R.id.praise_comment_ll_item);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ProgressBar progressBar;
        RelativeLayout footerView;

        public FooterViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.footer_tv);
            progressBar = (ProgressBar) view.findViewById(R.id.footer_progress);
            footerView = (RelativeLayout) view.findViewById(R.id.footerView);
        }
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

        /**
         * 隐藏末尾的view（其显示的是加载更多）
         *
         * @param view
         */
        void hideView(View view);
    }

    ;

    public OnItemPraiseAndHideView onItemPraiseAndHideView;

    public void setOnItemPraiseAndHideView(OnItemPraiseAndHideView onItemPraiseAndHideView) {
        this.onItemPraiseAndHideView = onItemPraiseAndHideView;
    }

}
