package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.miqtech.master.client.ui.PersonalHomePageActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CircleImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/1/7.
 */
public class PersonalCommentDetailAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<SecondCommentDetail> list;

    private String replyColon = " 回复 ";
    private String colon = " : ";
    private User user;

    public PersonalCommentDetailAdapter(Context context, List<SecondCommentDetail> list) {
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
        user = WangYuApplication.getUser(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReplyViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_item_reply_comment_person, parent, false);
            holder = new ReplyViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ReplyViewHolder) convertView.getTag();
        }
        showReply(holder, position);
        return convertView;
    }

    /**
     * 显示 回复
     *
     * @param replyViewHolder
     * @param position
     */
    private void showReply(ReplyViewHolder replyViewHolder, int position) {
        if (list.isEmpty()) {
            return;
        }

        SecondCommentDetail bean = list.get(position);

        if (!TextUtils.isEmpty(bean.getNickname()) && !TextUtils.isEmpty(bean.getContent())) {
            replyViewHolder.tvReplyContent.setText(addconnent(bean));
            replyViewHolder.tvReplyContent.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            replyViewHolder.tvReplyContent.setText("");
        }

        //显示时间
        if (!TextUtils.isEmpty(bean.getCreateDate())) {
            String strDate = TimeFormatUtil.friendlyTime(bean.getCreateDate());
            replyViewHolder.tvReplytime.setText(strDate);
        } else {
            replyViewHolder.tvReplytime.setText("");
        }

        if (user != null && user.getId().equals(bean.getUserId() + "")) {
            replyViewHolder.tvDelete.setVisibility(View.VISIBLE);
        } else {
            replyViewHolder.tvDelete.setVisibility(View.GONE);
        }

        MyOnCliclLister myOnCliclLister = new MyOnCliclLister(position);
        replyViewHolder.viewLL.setOnClickListener(myOnCliclLister);
//        replyViewHolder.tvReplyContent.setOnClickListener(myOnCliclLister);
        replyViewHolder.tvDelete.setOnClickListener(myOnCliclLister);
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

        public MyOnCliclLister(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_comment_item://回复楼中楼
                    dealWithData.replyToReply(position);
                    break;
                case R.id.comment_time_delete://删除楼中楼
                    dealWithData.deleteReplyReply(position);
                    break;
            }
        }
    }

    class ReplyViewHolder {
        TextView tvReplyContent;
        TextView tvReplytime;
        LinearLayout viewLL;
        TextView tvDelete;

        public ReplyViewHolder(View view) {
            tvReplyContent = (TextView) view.findViewById(R.id.comment_details_item);
            tvReplytime = (TextView) view.findViewById(R.id.comment_time_item);
            viewLL = (LinearLayout) view.findViewById(R.id.ll_comment_item);
            tvDelete = (TextView) view.findViewById(R.id.comment_time_delete);
        }
    }


    public interface DealWithData {

        /**
         * 回复楼中楼
         *
         * @param position 所在位置
         */
        void replyToReply(int position);

        /**
         * 删除楼中楼回复
         *
         * @param position
         */
        void deleteReplyReply(int position);
    }

    ;

    public DealWithData dealWithData;

    public void setOnDealWithData(DealWithData dealWithData) {
        this.dealWithData = dealWithData;
    }


}
