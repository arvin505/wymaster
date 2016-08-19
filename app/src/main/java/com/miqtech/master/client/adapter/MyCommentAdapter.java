package com.miqtech.master.client.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.Mycomment;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.view.CircleImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/1/18.
 */
public class MyCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int EMPTY_VIEW = 0;//没数据时显示的样子
    private final int REPLY_VIEW = 1;
    private final int FOOTER_VIEW = 2;//显示底部

    private Context context;
    private List<Mycomment> mycommentList;
    private LayoutInflater mInflater;
    private String strDate;

    private String replyColon = " 回复 ";
    private String replyMe = "回复我: ";
    private String me = " 我 ";
    private String meComment = " 我的评论: ";
    private String colon = " : ";
    private boolean isFirst = true;


    public MyCommentAdapter(Context context, List<Mycomment> mycommentList) {
        this.context = context;
        this.mycommentList = mycommentList;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getItemCount() {
        if (mycommentList.isEmpty()) {
            return 1;
        } else {
            return mycommentList.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        if (mycommentList.isEmpty()) {
            return EMPTY_VIEW;
        } else {
            if (position + 1 == getItemCount()) {
                return FOOTER_VIEW;
            }
            return REPLY_VIEW;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view = null;
        switch (viewType) {
            case EMPTY_VIEW:
                view = mInflater.inflate(R.layout.exception_page, parent, false);
                holder = new EmptyViewHolder(view);
                break;
            case FOOTER_VIEW:
                view = mInflater.inflate(R.layout.layout_footer_view, parent, false);
                holder = new FooterViewHolder(view);
                break;
            case REPLY_VIEW:
                view = mInflater.inflate(R.layout.layout_my_comment_item, parent, false);
                holder = new CommentViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case EMPTY_VIEW:
                EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
                if (isFirst) {
                    isFirst = false;
                    emptyViewHolder.tv_err_title.setVisibility(View.GONE);
                    emptyViewHolder.noDataImage.setVisibility(View.GONE);
                } else {
                    emptyViewHolder.tv_err_title.setVisibility(View.VISIBLE);
                    emptyViewHolder.noDataImage.setVisibility(View.VISIBLE);
                    emptyViewHolder.tv_err_title.setText("矮油，还没人回复你");
                }
                break;
            case FOOTER_VIEW:
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                dealWithCommentItem.hideFooterView(footerViewHolder.footerView);
                break;
            case REPLY_VIEW:
                CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
                showComment(commentViewHolder, position);
                break;
        }
    }

    /**
     * 显示评论
     *
     * @param myHolder
     * @param position 位置
     */
    private void showComment(final CommentViewHolder myHolder, final int position) {
        if (mycommentList.isEmpty()) {
            return;
        }

        Mycomment bean = mycommentList.get(position);

        //显示头像
        if (!TextUtils.isEmpty(bean.getIcon())) {
            AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + bean.getIcon() + "!small", myHolder.ivUserHeader);
        } else {
            myHolder.ivUserHeader.setImageResource(R.drawable.default_head);
        }

        //显示回复者的名称
        if (!TextUtils.isEmpty(bean.getNickname())) {
            myHolder.tvUserName.setText(bean.getNickname());
        } else {
            myHolder.tvUserName.setText("");
        }

        //显示回复时间
        if (!TextUtils.isEmpty(bean.getCreate_date())) {
            strDate = TimeFormatUtil.friendlyTime(bean.getCreate_date());
            myHolder.tvTime.setText(strDate);
        } else {
            myHolder.tvTime.setText("");
        }

        //显示回复的内容
        if (!TextUtils.isEmpty(bean.getContent())) {
            myHolder.tvContent.setText(replyMe + bean.getContent());
        } else {
            myHolder.tvContent.setText("");
        }

        //显示被回复的内容
        if (!TextUtils.isEmpty(bean.getMy_content())) {
            myHolder.tvReply.setText(addconnent(bean));
        } else {
            myHolder.tvReply.setText("");
        }

        //判断是否显示未读标记
        if (bean.getIs_read() == 0) {//未读
            myHolder.ivIsRead.setVisibility(View.VISIBLE);
        } else {
            myHolder.ivIsRead.setVisibility(View.GONE);
        }

        myHolder.commentItemLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dealWithCommentItem.onItemClickForItem(position);
            }
        });

        myHolder.commentItemLl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dealWithCommentItem.onItemLongClickForItem(position, myHolder.commentItemLl);
                return false;
            }
        });

    }

    /**
     * 显示楼中楼
     * 当replyName为空时表示回复楼主，不为空时表示回复层主
     *
     * @return 拼接好的内容
     */
    private SpannableStringBuilder addconnent(final Mycomment bean) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        int start;
        int end;
        if (TextUtils.isEmpty(bean.getReply_nickname())) {//显示：我的评论：XXXXXX
            start = meComment.length();
            ssb.append(meComment);
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {

                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(context.getResources().getColor(R.color.dark_black));// 设置文本颜色
                    // 去掉下划线
                    ds.setUnderlineText(false);
                    ds.setFakeBoldText(true);//加粗
                }
            }, 0, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return ssb.append(bean.getMy_content());
        } else {//显示：我 回复 YY ：XXXXXXXXXXX
            start = me.length();
            end = start + bean.getReply_nickname().length() + replyColon.length();

            ssb.append(me);

            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {

                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(context.getResources().getColor(R.color.dark_black));// 设置文本颜色
                    // 去掉下划线
                    ds.setUnderlineText(false);
                    ds.setFakeBoldText(true);//加粗
                }

            }, 0, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            ssb.append(replyColon + bean.getReply_nickname());

            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {

                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(context.getResources().getColor(R.color.dark_black));// 设置文本颜色
                    // 去掉下划线
                    ds.setUnderlineText(false);
                    ds.setFakeBoldText(true);//加粗
                }
            }, start + replyColon.length(), end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return ssb.append(colon + bean.getMy_content());
        }

    }


    class CommentViewHolder extends RecyclerView.ViewHolder {
        LinearLayout commentItemLl;//item
        CircleImageView ivUserHeader;//头像
        TextView tvUserName;//回复者名称
        TextView tvTime;//回复的时间
        ImageView ivIsRead;//是否已读
        TextView tvContent;//回复的内容
        TextView tvReply;//被回复的被容
        View line;//底部的线

        public CommentViewHolder(View view) {
            super(view);
            commentItemLl = (LinearLayout) view.findViewById(R.id.my_comment_item_ll);
            ivUserHeader = (CircleImageView) view.findViewById(R.id.ivUserHeader);
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            ivIsRead = (ImageView) view.findViewById(R.id.ivIsRead);
            tvContent = (TextView) view.findViewById(R.id.tvContent);
            tvReply = (TextView) view.findViewById(R.id.tvReply);
            line = view.findViewById(R.id.line_view_comment_item);
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
     * item的处理
     */
    public interface DealWithCommentItem {
        /**
         * item 的点击事件
         *
         * @param position 在屏幕中的位置
         */
        void onItemClickForItem(int position);

        /**
         * item 的长安事件
         *
         * @param position 在屏幕中的位置
         * @param view     item控件
         */
        void onItemLongClickForItem(int position, View view);

        /**
         * 隐藏 “加载更多”
         *
         * @param view 控件
         */
        void hideFooterView(View view);
    }

    ;

    /**
     * item的处理
     */
    public DealWithCommentItem dealWithCommentItem;

    public void setDealWithCommentItem(DealWithCommentItem dealWithCommentItem) {
        this.dealWithCommentItem = dealWithCommentItem;
    }

}
