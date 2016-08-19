package com.miqtech.master.client.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.CommentInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.ImagePagerActivity;
import com.miqtech.master.client.ui.PersonalHomePageActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;

/**
 * 网吧评论详情的适配器
 *
 * @author Administrator
 */
public class NetbarCommentAdapter extends BaseAdapter {

    private Context context;
    private Drawable dd;
    private TextView report_tv;
    private Display display;
    private DisplayMetrics displayMetrics;
    private int windowHigh_half;
    private ItemPostDate date;
    private List<CommentInfo> commentinfoList;
    private List<ImageView> imageViews = new ArrayList<ImageView>();

    public NetbarCommentAdapter(Context context, List<CommentInfo> commentinfoList) {
        this.context = context;
        this.commentinfoList = commentinfoList;
        displayMetrics = new DisplayMetrics();
        display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getMetrics(displayMetrics);
        windowHigh_half = display.getHeight() / 2;
    }

    public void setItemPostDate(ItemPostDate date) {
        this.date = date;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return commentinfoList.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return getItem(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        NetBarHolder holder = null;
        if (view == null) {
            holder = new NetBarHolder();
            view = LayoutInflater.from(context).inflate(R.layout.layout_detail_comment_item, null);
            holder.headIcon = (ImageView) view.findViewById(R.id.rating_people_icon_item);
            holder.name = (TextView) view.findViewById(R.id.comment_name_item);
            holder.starLevel = (RatingBar) view.findViewById(R.id.comment_level_item);
            holder.guanzhu_tv = (TextView) view.findViewById(R.id.guanzhu_tv_item);
            holder.guanzhu_ll = (RelativeLayout) view.findViewById(R.id.guanzhu_ll_item);
            holder.pull_down = (ImageView) view.findViewById(R.id.pull_down_item);
            holder.details = (TextView) view.findViewById(R.id.comment_details_item);
            holder.img_one = (ImageView) view.findViewById(R.id.image_one_iv);
            holder.img_two = (ImageView) view.findViewById(R.id.image_two_iv);
            holder.img_three = (ImageView) view.findViewById(R.id.image_three_iv);
            holder.time = (TextView) view.findViewById(R.id.comment_time_item);
            holder.line = (TextView) view.findViewById(R.id.tv_bottm_line_item);
            holder.image_ll = (LinearLayout) view.findViewById(R.id.three_image_ll);
            view.setTag(holder);
        } else {
            holder = (NetBarHolder) view.getTag();
        }


        updateView(commentinfoList.get(position), holder, position);
        MyItemOnClick listner = new MyItemOnClick(position, holder, commentinfoList.get(position));
        holder.guanzhu_ll.setOnClickListener(listner);
        holder.pull_down.setOnClickListener(listner);
        holder.headIcon.setOnClickListener(listner);
        holder.img_one.setOnClickListener(listner);
        holder.img_two.setOnClickListener(listner);
        holder.img_three.setOnClickListener(listner);
        // holder.image_ll.setOnClickListener(listner);
        return view;
    }

    /**
     * 填充数据
     *
     * @param info
     * @param hh
     */
    private void updateView(CommentInfo info, NetBarHolder hh, int poition) {
        if (info == null) {
            return;
        }
        if (1 == info.getIs_anonymous()) {// 匿名
            hh.name.setText("匿名用户");
            hh.headIcon.setImageResource(R.drawable.cryptonym);
        } else {
            hh.name.setText(info.getNickname());
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + info.getIcon(), hh.headIcon);// 显示头像
        }
        if (WangYuApplication.getUser(context) != null) {
            if (Integer.valueOf(WangYuApplication.getUser(context).getId()) == info.getUser_id()) {
                hh.pull_down.setVisibility(View.INVISIBLE);
                hh.guanzhu_ll.setVisibility(View.INVISIBLE);
            }
        }
        if (info.getAvgScore() < 1) {// 显示星级
            hh.starLevel.setVisibility(View.INVISIBLE);
        } else {
            hh.starLevel.setNumStars((int) info.getAvgScore());
            hh.starLevel.setRating(info.getAvgScore());
        }

        if (1 == info.getIsPraised()) {// 关注（赞）的按钮状态
            changeStatu(hh, 1);
        } else if (0 == info.getIsPraised()) {
            changeStatu(hh, 0);
        }

        // if (info.getImgs() == null) {// 显示评价的图片
        // hh.image_ll.setVisibility(View.GONE);
        // } else {
        showImage(info.getImgs(), hh);
        // }

        if ((commentinfoList.size() - 1) == poition) {// 如是最后一条评论，底部的先消失
            hh.line.setVisibility(View.INVISIBLE);
        }
        hh.details.setText(info.getContent());// 评论详情
        hh.guanzhu_tv.setText(getNum(info.getPraised()));// 关注（赞）的人事
        hh.time.setText(DateUtil.getStringDate(info.getCreate_date()));// 显示时间

    }

    private class MyItemOnClick implements OnClickListener {
        private int position;
        private NetBarHolder hh;
        private CommentInfo info;

        public MyItemOnClick(int position, NetBarHolder hh, CommentInfo info) {
            this.position = position;
            this.hh = hh;
            this.info = info;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.guanzhu_ll_item:// 赞
                    date.useful(position);
                    break;
                case R.id.pull_down_item:// 点击出现举报按钮
                    creatDialog(hh, position);
                    break;
                case R.id.rating_people_icon_item:// 点击头像到个人主页
                    if (1 != info.getIs_anonymous()) {
                        Intent ii = new Intent(context, PersonalHomePageActivity.class);
                        ii.putExtra("id", info.getUser_id() + "");
                        context.startActivity(ii);
                    }
                    break;
                // case R.id.three_image_ll://
                // seeBigImage(info, 0);
                // break;
                case R.id.image_one_iv://
                    seeBigImage(info, 0);
                    break;
                case R.id.image_two_iv://
                    seeBigImage(info, 1);
                    break;
                case R.id.image_three_iv://
                    seeBigImage(info, 2);
                    break;
                default:
                    break;
            }
        }
    }

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
     * 改变关注状态按钮 0表示未关注 1表示关注
     *
     * @param hh
     * @param i
     */
    private void changeStatu(NetBarHolder hh, int i) {
        if (i == 1) {
            dd = context.getResources().getDrawable(R.drawable.love_on);
            dd.setBounds(0, 0, dd.getMinimumWidth(), dd.getMinimumHeight());
            hh.guanzhu_tv.setTextColor(context.getResources().getColor(R.color.white));
            // hh.guanzhu_tv.setBackgroundResource(R.drawable.corner_bluegraysolid);
            hh.guanzhu_ll.setBackgroundResource(R.drawable.corner_bluegray_bg);
            hh.guanzhu_tv.setCompoundDrawables(dd, null, null, null);
        } else if (i == 0) {
            dd = context.getResources().getDrawable(R.drawable.love_off);
            dd.setBounds(0, 0, dd.getMinimumWidth(), dd.getMinimumHeight());
            hh.guanzhu_tv.setTextColor(context.getResources().getColor(R.color.gray));
            // hh.guanzhu_tv.setBackgroundResource(R.drawable.corner_graystroke_whitesolid);
            hh.guanzhu_ll.setBackgroundResource(R.drawable.corner_graystroke_whitesolid);
            hh.guanzhu_tv.setCompoundDrawables(dd, null, null, null);
        }
    }

    private void creatDialog(NetBarHolder hh, final int position) {
        int[] location = new int[2];
        final Dialog dialog = new Dialog(context, R.style.delete_style);
        dialog.setContentView(R.layout.layout_netbar_comment_report);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        report_tv = (TextView) dialog.findViewById(R.id.report_tv_item);
        hh.guanzhu_tv.getLocationOnScreen(location);
        params.x = location[0] / 2 + hh.guanzhu_tv.getWidth() / 2;
        if (windowHigh_half >= location[1]) {
            params.y = -Math.abs(windowHigh_half - location[1]) + hh.guanzhu_tv.getHeight();
        } else {
            params.y = Math.abs(windowHigh_half - location[1]) + hh.guanzhu_tv.getHeight();
        }
        dialog.getWindow().setAttributes(params);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        report_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                date.report(position);
                dialog.dismiss();
            }
        });
    }

    class NetBarHolder {
        ImageView headIcon;// 头像
        TextView name;// 名字
        RatingBar starLevel;// 评价用户的等级
        TextView guanzhu_tv;// 关注
        ImageView pull_down;// 下拉
        TextView details;// 评价详情
        ImageView img_one;// 图片1
        ImageView img_two;// 图片2
        ImageView img_three;// 图片3
        TextView time;// 评价时间
        TextView line;// 底部的线
        LinearLayout image_ll;
        RelativeLayout guanzhu_ll;
    }

    /**
     * 显示评价的图片
     *
     * @param Str 图片地址集
     * @param hh
     */
    private void showImage(String Str, NetBarHolder hh) {
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

    public interface ItemPostDate {
        /**
         * 赞，有用
         *
         * @param position
         */
        void useful(int position);

        /**
         * 举报
         *
         * @param position
         */
        void report(int position);
    }

    ;

}
