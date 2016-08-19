package com.miqtech.master.client.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.QuickCommentDetail;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.ImageLoadingListenerAdapter;
import com.miqtech.master.client.utils.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 快速评论
 * Created by zhaosentao on 2016/7/29.
 */
public class QuickCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<QuickCommentDetail> quickCommentDetailList;

    private float scale = 168 / 94;

    private float distance49Dp;

    public QuickCommentAdapter(Context context, List<QuickCommentDetail> quickCommentDetailList) {
        this.context = context;
        this.quickCommentDetailList = quickCommentDetailList;
        distance49Dp = context.getResources().getDimension(R.dimen.distance_49dp);
    }


    @Override
    public int getItemCount() {
        return quickCommentDetailList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_quick_comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (quickCommentDetailList == null || quickCommentDetailList.isEmpty()) {
            return;
        }

        ViewHolder viewHolder = (ViewHolder) holder;

        final QuickCommentDetail bean = quickCommentDetailList.get(position);

        context.getResources().getDimension(R.dimen.distance_41dp);

        AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + bean.getImg() + "!small", viewHolder.ivIcon);

        //内容
        if (!TextUtils.isEmpty(bean.getComment())) {
            viewHolder.tvConnent.setText(bean.getComment());
        } else {
            viewHolder.tvConnent.setText("");
        }

        viewHolder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItemListener.onClickItem(bean.getId());
            }
        });
    }


    public interface OnClickItemListener {
        void onClickItem(String id);
    }

    public OnClickItemListener onClickItemListener;

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.quickCommentLlItem)
        RelativeLayout llItem;
        @Bind(R.id.quickCommentIvImgItem)
        ImageView ivIcon;
        @Bind(R.id.quickCommentTvTextItem)
        TextView tvConnent;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
