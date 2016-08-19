package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.FirstCommentDetail;
import com.miqtech.master.client.entity.SecondCommentDetail;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.ImagePagerActivity;
import com.miqtech.master.client.ui.PersonalHomePageActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaosentao on 2016/1/8.
 */
public class CommentsSectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int current;//用来计数回复的条数
    private TextView tvReplyContent;
    private TextView tvReplytime;
    private TextView tvDelete;
    private LinearLayout reply_reply_comment_ll_item;

    private final int HOT_TYPE = 1;
    private final int NEWEST_TYPE = 2;
    private final int EMPTY_TYPE = 3;

    private List<FirstCommentDetail> newestCommentlist;//最新评论
    private List<FirstCommentDetail> hotCommentList;//热门评论
    private LayoutInflater mInflater;

    private String replyColon = " 回复 ";
    private String colon = " : ";

    private User user;
    private boolean isFirst = true;

    public CommentsSectionAdapter(Context context, List<FirstCommentDetail> newestCommentlist,
                                  List<FirstCommentDetail> hotCommentList) {
        this.context = context;
        this.newestCommentlist = newestCommentlist;
        this.hotCommentList = hotCommentList;
        mInflater = LayoutInflater.from(context);
        user = WangYuApplication.getUser(context);
    }

    public void setData() {
        user = WangYuApplication.getUser(context);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (hotCommentList.size() < 2 && newestCommentlist.size() < 2) {
            Log.i("TGA", "asd", null);
            return 1;
        } else {
            Log.i("TGA", "qwe", null);
            return hotCommentList.size() + newestCommentlist.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (hotCommentList.size() < 2 && newestCommentlist.size() < 2) {
            Log.i("TGA", "EMPTY_TYPE", null);
            return EMPTY_TYPE;
        } else {
            if (!hotCommentList.isEmpty() && position < hotCommentList.size()) {
                Log.i("TGA", "HOT_TYPE", null);
                return HOT_TYPE;
            }
            Log.i("TGA", "NEWEST_TYPE", null);
            return NEWEST_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view = null;
        switch (viewType) {
            case EMPTY_TYPE:
                view = mInflater.inflate(R.layout.exception_page, parent, false);
                holder = new EmptyViewHolder(view);
                break;
            default:
                view = mInflater.inflate(R.layout.layout_comment_item, parent, false);
                holder = new CommentViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case HOT_TYPE:
                CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
                if (position == 0) {
                    commentViewHolder.comment_top_title_ll.setVisibility(View.VISIBLE);
                    commentViewHolder.line.setVisibility(View.GONE);
                    commentViewHolder.commentDetailLl.setVisibility(View.GONE);
                } else if (position < hotCommentList.size()) {
                    commentViewHolder.comment_top_title_ll.setVisibility(View.GONE);
                    commentViewHolder.line.setVisibility(View.VISIBLE);
                    commentViewHolder.commentDetailLl.setVisibility(View.VISIBLE);
                    showCommentData(commentViewHolder, position, hotCommentList, position);
                }
                break;
            case NEWEST_TYPE:
                CommentViewHolder commentViewHolder1 = (CommentViewHolder) holder;
                if (!hotCommentList.isEmpty()) {
                    if (position == hotCommentList.size()) {//表示热门显示完后的第一条是显示“最新评论”
                        commentViewHolder1.comment_top_title_tv.setText("最新评论");
                        commentViewHolder1.comment_top_title_ll.setVisibility(View.VISIBLE);
                        commentViewHolder1.line.setVisibility(View.GONE);
                        commentViewHolder1.commentDetailLl.setVisibility(View.GONE);
                    } else {
                        commentViewHolder1.comment_top_title_ll.setVisibility(View.GONE);
                        commentViewHolder1.line.setVisibility(View.VISIBLE);
                        commentViewHolder1.commentDetailLl.setVisibility(View.VISIBLE);

                        showCommentData(commentViewHolder1, position, newestCommentlist, position - hotCommentList.size());
                    }
                } else {
                    if (position == 0) {
                        commentViewHolder1.comment_top_title_tv.setText("最新评论");
                        commentViewHolder1.comment_top_title_ll.setVisibility(View.VISIBLE);
                        commentViewHolder1.line.setVisibility(View.GONE);
                        commentViewHolder1.commentDetailLl.setVisibility(View.GONE);
                    } else {
                        commentViewHolder1.comment_top_title_ll.setVisibility(View.GONE);
                        commentViewHolder1.line.setVisibility(View.VISIBLE);
                        commentViewHolder1.commentDetailLl.setVisibility(View.VISIBLE);

                        showCommentData(commentViewHolder1, position, newestCommentlist, position);
                    }
                }
                break;
            case EMPTY_TYPE:
                EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
                if (isFirst) {
                    isFirst = false;
                    emptyViewHolder.tv_err_title.setVisibility(View.GONE);
                    emptyViewHolder.noDataImage.setVisibility(View.GONE);
                } else {
                    emptyViewHolder.tv_err_title.setVisibility(View.VISIBLE);
                    emptyViewHolder.noDataImage.setVisibility(View.VISIBLE);
                    emptyViewHolder.tv_err_title.setText("还没有评论，亲评论下吧!");
                }
                break;
        }

    }

    /**
     * 显示评价的数据
     *
     * @param myHolder 布局ViewHolder
     * @param position 显示的位置
     * @param list     数据
     * @param getid    在数据中的位置
     */
    private void showCommentData(CommentViewHolder myHolder, int position, List<FirstCommentDetail> list, int getid) {
        if (list.isEmpty()) {
            return;
        }
        FirstCommentDetail bean = list.get(getid);
        String strDate;

        //显示头像
        if (!TextUtils.isEmpty(bean.getIcon())) {
            if (myHolder.ivUserHeader.getTag() == null || !bean.getIcon().equals(myHolder.ivUserHeader.getTag().toString())) {
                AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + bean.getIcon() + "!small", myHolder.ivUserHeader);
                myHolder.ivUserHeader.setTag(bean.getIcon());
            }
        } else {
            myHolder.ivUserHeader.setImageResource(R.drawable.default_head);
        }

        //显示昵称
        if (!TextUtils.isEmpty(bean.getNickname())) {
            myHolder.tvUserName.setText(bean.getNickname());
        } else {
            myHolder.tvUserName.setText("");
        }

        //显示评论内容
        if (!TextUtils.isEmpty(bean.getContent())) {
            myHolder.tvContent.setText(bean.getContent());
        } else {
            myHolder.tvContent.setText("");
        }

        //显示时间
        if (!TextUtils.isEmpty(bean.getCreateDate())) {
            strDate = TimeFormatUtil.friendlyTime(bean.getCreateDate());
            myHolder.tvTime.setText(strDate);
        } else {
            myHolder.tvTime.setText("");
        }

        //判断是否显示删除按钮
        if (user != null && user.getId().equals(bean.getUserId() + "")) {
            myHolder.tvDelect.setVisibility(View.VISIBLE);
        } else {
            myHolder.tvDelect.setVisibility(View.INVISIBLE);
        }

        //显示是否评论的状态
        if (bean.getIsPraise() == 0) {
            myHolder.praiseIv.setImageResource(R.drawable.comment_praise_no);
        } else if (bean.getIsPraise() == 1) {
            myHolder.praiseIv.setImageResource(R.drawable.comment_praise_yes);
        }

        //显示是否是赏金猎人的标志
        if (bean.getBountyHunterFlag() == 1) {
            myHolder.ivHunter.setVisibility(View.VISIBLE);
        } else {
            myHolder.ivHunter.setVisibility(View.GONE);
        }


        //显示点赞数
        myHolder.praiseTv.setText(Utils.getnumberForms(bean.getLikeCount(), context));


        if (!TextUtils.isEmpty(bean.getImg())) {
            if (myHolder.ivCommentImg.getTag() == null || !bean.getImg().equals(myHolder.ivCommentImg.getTag().toString())) {
                AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + bean.getImg() + "!small", myHolder.ivCommentImg);
                myHolder.ivCommentImg.setVisibility(View.VISIBLE);
                myHolder.ivCommentImg.setTag(bean.getImg());
            }
        } else {
            myHolder.ivCommentImg.setVisibility(View.GONE);
        }

        AddReplyReplyView(myHolder.replyReplyLl, position, list, getid);

        if (getid + 1 == list.size()) {
            myHolder.line.setVisibility(View.GONE);
        }

        //设置监听事件
        MyOnCliclLister myOnCliclLister = new MyOnCliclLister(position, myHolder, list, getid);
        myHolder.repltLl.setOnClickListener(myOnCliclLister);//回复
        myHolder.tvDelect.setOnClickListener(myOnCliclLister);//删除
        myHolder.praiseLl.setOnClickListener(myOnCliclLister);//赞
        myHolder.ivUserHeader.setOnClickListener(myOnCliclLister);//头像
        myHolder.llContent.setOnClickListener(myOnCliclLister); //点击文字区域 跳到详情
        myHolder.ivCommentImg.setOnClickListener(myOnCliclLister); //点击图片跳转大图
    }

    /**
     * 添加楼中楼的评论
     *
     * @param linearLayout
     * @param position     recyclerview中的位置
     * @param getid        在数据中的位置
     */
    private void AddReplyReplyView(LinearLayout linearLayout, int position, List<FirstCommentDetail> list, int getid) {
        linearLayout.removeAllViews();
        linearLayout.setVisibility(View.VISIBLE);
        if (list.isEmpty() || list.get(getid).getReplyList() == null || list.get(getid).getReplyList().isEmpty() || linearLayout == null) {
            linearLayout.setVisibility(View.GONE);
            return;
        }
        //5条以内，不显示“还有更多”，否则显示
        if (list.get(getid).getReplyList().size() < 6) {
            current = list.get(getid).getReplyList().size();
        } else {
            if (list.get(getid).getReplyList().size() > 5) {
                current = 6;
            } else {
                current = list.get(getid).getReplyList().size() + 1;
            }
        }

        for (int i = 0; i < current; i++) {
            linearLayout.addView(creatReplyReplyView(getid, list, i, position));
        }
    }

    /**
     * 创建楼中楼
     *
     * @param getid        在数据中的位置
     * @param list         数据
     * @param isShowButtom
     * @param position     在屏幕中的位置
     * @return
     */
    private View creatReplyReplyView(final int getid, final List<FirstCommentDetail> list, final int isShowButtom, final int position) {

        View view = null;
        view = mInflater.inflate(R.layout.layout_item_reply_comment, null);
        reply_reply_comment_ll_item = (LinearLayout) view.findViewById(R.id.reply_reply_comment_ll_item);//最外层布局的
        tvReplyContent = (TextView) view.findViewById(R.id.comment_details_item);//显示的评论
        tvReplytime = (TextView) view.findViewById(R.id.comment_time_item);//显示的时间
        tvDelete = (TextView) view.findViewById(R.id.comment_time_delete);//删除

        if (isShowButtom < 5 && isShowButtom < list.get(getid).getReplyList().size()) {
            final SecondCommentDetail bean = list.get(getid).getReplyList().get(isShowButtom);
            tvReplyContent.setGravity(Gravity.LEFT);
            if (!TextUtils.isEmpty(bean.getNickname()) && !TextUtils.isEmpty(bean.getContent())) {
                tvReplyContent.setText(addconnent(bean));
                tvReplyContent.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                tvReplyContent.setText("");
            }

            //显示时间
            if (!TextUtils.isEmpty(bean.getCreateDate())) {
                String strDate = TimeFormatUtil.friendlyTime(bean.getCreateDate());
                tvReplytime.setText(strDate);
            } else {
                tvReplytime.setText("");
            }

            user = WangYuApplication.getUser(context);
            if (user != null && user.getId().equals(bean.getUserId() + "")) {
                tvDelete.setVisibility(View.VISIBLE);
                tvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processTheData.deleteReplyReply(bean.getId(), position, isShowButtom);
                    }
                });
            } else {
                tvDelete.setVisibility(View.GONE);
            }

        } else {
            tvReplyContent.setText("更多" + Utils.getnumberForms((list.get(getid).getReplyCount() - 5), context) + "条回复。。。");
            tvReplyContent.setTextColor(context.getResources().getColor(R.color.shop_font_black));
            tvReplyContent.setGravity(Gravity.RIGHT);
            tvReplytime.setVisibility(View.GONE);
            tvReplyContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processTheData.lookComment(list.get(getid).getId(), position, list.get(getid).getNickname());
                }
            });
        }
        return view;
    }

    /**
     * 显示楼中楼
     * 当replyName为空时表示回复楼主，不为空时表示回复层主
     *
     * @return 拼接好的内容
     */
    private SpannableStringBuilder addconnent(final SecondCommentDetail bean) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(bean.getNickname());
        int start = bean.getNickname().length();
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(context, PersonalHomePageActivity.class);
                intent.putExtra("id", bean.getUserId() + "");
                context.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(R.color.blue_comment_title));// 设置文本颜色
                // 去掉下划线
                ds.setUnderlineText(false);
            }
        }, 0, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (bean.getParent_id() != bean.getReply_id() && !TextUtils.isEmpty(bean.getReplyname())) {
            int end = start + replyColon.length() + bean.getReplyname().length();
            ssb.append(replyColon + bean.getReplyname());

            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(context, PersonalHomePageActivity.class);
                    intent.putExtra("id", bean.getReplyUserId() + "");
                    context.startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(context.getResources().getColor(R.color.blue_comment_title));// 设置文本颜色
                    // 去掉下划线
                    ds.setUnderlineText(false);
                }
            }, start + replyColon.length(), end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return ssb.append(colon + bean.getContent());
    }

    private class MyOnCliclLister implements View.OnClickListener {
        int position;
        CommentViewHolder viewHolder;
        List<FirstCommentDetail> mylist;
        int getid;

        public MyOnCliclLister(int position, CommentViewHolder viewHolder, List<FirstCommentDetail> mylist, int getid) {
            this.position = position;
            this.viewHolder = viewHolder;
            this.mylist = mylist;
            this.getid = getid;
        }

        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.reply_comment_ll_item://回复  回复楼主
                    processTheData.replyComment(mylist.get(getid).getId(), position, mylist.get(getid).getNickname());
                    break;
                case R.id.llContent://点击文字区域跳转  单个评论详情
                    processTheData.lookComment(mylist.get(getid).getId(), position, mylist.get(getid).getNickname());
                    break;
                case R.id.tvDelect://删除评论
                    processTheData.delectComment(mylist.get(getid).getId(), position);
                    break;
                case R.id.praise_comment_ll_item://点赞
                    processTheData.praiseComment(mylist.get(getid).getId(), position);
                    break;
                case R.id.ivUserHeader://点击头像跳转
                    intent = new Intent(context, PersonalHomePageActivity.class);
                    intent.putExtra("id", newestCommentlist.get(getid).getUserId() + "");
                    context.startActivity(intent);
                    break;
                case R.id.ivCommentImg://点击评论里的图片
                    ArrayList<Map<String, String>> imgs = new ArrayList<Map<String, String>>();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("url", mylist.get(getid).getImg());
                    imgs.add(map);
                    intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("images", imgs);
                    bundle.putInt("isHideGallery", 1);
                    intent.putExtras(bundle);
                    intent.setClass(context, ImagePagerActivity.class);
                    context.startActivity(intent);
                    break;
            }
        }
    }


    class CommentViewHolder extends RecyclerView.ViewHolder {
        LinearLayout comment_top_title_ll;//顶部热门评价这几个字栏
        TextView comment_top_title_tv;
        CircleImageView ivUserHeader;//头像
        TextView tvUserName;//名字
        TextView tvContent;//评论内容
        TextView tvTime;//时间
        TextView tvDelect;//删除

        LinearLayout praiseLl;//赞
        TextView praiseTv;//赞的显示数量
        ImageView praiseIv;//赞的图标

        LinearLayout repltLl;//回复

        LinearLayout replyReplyLl;//楼中楼

        LinearLayout commentDetailLl;//评论的内容（包括头像，名称等等）

        View line;//底下的那条线

        ImageView ivCommentImg;//评论图片
        ImageView ivHunter;//赏金猎人标志

        LinearLayout llContent;


        public CommentViewHolder(View view) {
            super(view);
            comment_top_title_ll = (LinearLayout) view.findViewById(R.id.comment_top_title_ll_item);
            comment_top_title_tv = (TextView) view.findViewById(R.id.comment_top_title_tv_item);
            ivUserHeader = (CircleImageView) view.findViewById(R.id.ivUserHeader);
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);
            tvContent = (TextView) view.findViewById(R.id.tvContent);
            llContent = (LinearLayout) view.findViewById(R.id.llContent);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            tvDelect = (TextView) view.findViewById(R.id.tvDelect);

            praiseLl = (LinearLayout) view.findViewById(R.id.praise_comment_ll_item);
            praiseTv = (TextView) view.findViewById(R.id.praise_comment_tv_item);
            praiseIv = (ImageView) view.findViewById(R.id.praise_comment_iv_item);
            ivCommentImg = (ImageView) view.findViewById(R.id.ivCommentImg);
            ivHunter = (ImageView) view.findViewById(R.id.ivHunter);

            repltLl = (LinearLayout) view.findViewById(R.id.reply_comment_ll_item);

            replyReplyLl = (LinearLayout) view.findViewById(R.id.reply_reply_comment_ll_item);

            commentDetailLl = (LinearLayout) view.findViewById(R.id.comment_detail_ll_item);

            line = view.findViewById(R.id.line_view_comment_item);
        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {
        ImageView noDataImage;
        TextView tv_err_title;

        public EmptyViewHolder(View view) {
            super(view);
            noDataImage = (ImageView) view.findViewById(R.id.noDataImage);
            tv_err_title = (TextView) view.findViewById(R.id.tv_err_title);
        }
    }


    /**
     * 处理数据
     */
    public interface ProcessTheData {
        /**
         * 删除数据
         *
         * @param id       该条数据的id
         * @param position 该条数据的位置
         */
        void delectComment(String id, int position);

        /**
         * 点赞或者取消
         *
         * @param id       该条数据的id
         * @param position 该条评论的位置
         */
        void praiseComment(String id, int position);

        /**
         * 回复楼主
         *
         * @param id       该条评论的id
         * @param position 该条评论的位置
         * @param nikeName 该条评论的昵称
         */
        void replyComment(String id, int position, String nikeName);

        /**
         * 查看
         *
         * @param id       该条评论的id
         * @param position 该条评论的位置
         * @param nikeName 该条评论的昵称
         */
        void lookComment(String id, int position, String nikeName);

//        /**
//         * 当没有更多数据时，隐藏最后一条“加载更多”
//         *
//         * @param view
//         */
//        void hideFooterView(View view);

        /**
         * 删除楼中楼
         *
         * @param id            楼中楼ID
         * @param position      该条评论的位子
         * @param replyPosition 楼主楼的位置
         */
        void deleteReplyReply(String id, int position, int replyPosition);

    }

    ;

    public ProcessTheData processTheData;

    public void setProcessTheData(ProcessTheData processTheData) {
        this.processTheData = processTheData;
    }


}
