package com.miqtech.master.client.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.Image;
import com.miqtech.master.client.entity.ZifaMatch;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.simcpux.Util;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.ImageUtils;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CornerProgressView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhy.autolayout.utils.ScreenUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaoyi on 2016/7/5.
 * 自发赛列表adapter
 */
public class ZifaMatchListAdapter extends RecyclerView.Adapter {
    private static final String TAG = "ZifaMatchListAdapter";
    private List<ZifaMatch> mDatas;
    private Context mContext;
    private OnItemClickListener mListener;

    public ZifaMatchListAdapter(List<ZifaMatch> mDatas, Context context) {
        this.mDatas = mDatas;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(mContext).inflate(R.layout.layout_zifamatch_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        showItem(itemHolder, position);
    }


    private void showItem(final ViewHolder itemHolder, final int position) {
        ZifaMatch match = mDatas.get(position);
        itemHolder.tvMatchTitle.setText(match.getName());
        itemHolder.tvMaxCount.setText(match.getMaxNum() + "");
        itemHolder.tvMatchStartTime.setText(TimeFormatUtil.formatWithMatchStr(match.getActivityBegin(), "MM.dd HH:mm"));
        itemHolder.imMatchState.setImageResource(match.getToApply() == 1 ?
                R.drawable.match_state_signup : R.drawable.match_state_show_detail);
        if (match.getApplyNum() > match.getMaxNum()) {
            itemHolder.tvCurrentCount.setTextSize(18);
            itemHolder.tvCurrentCount.getPaint().setFakeBoldText(true);
        } else {
            itemHolder.tvCurrentCount.setTextSize(14);
            itemHolder.tvCurrentCount.getPaint().setFakeBoldText(false);
        }
        itemHolder.pbMatch.setMaxCount(match.getMaxNum());
        itemHolder.pbMatch.setCurrentCount(match.getApplyNum());
        itemHolder.tvCurrentCount.setText("" + match.getApplyNum());
        itemHolder.tvMatchBy.setText(match.getSponsor());
        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(v, position);
                }
            }
        });
        itemHolder.imgMatchPoster.measure(-1, -1);
        final int width = itemHolder.imgMatchPoster.getMeasuredWidth();
        final int h = itemHolder.imgMatchPoster.getMeasuredHeight();
        AsyncImage.loadNetPhotoWithListener(HttpConstant.SERVICE_UPLOAD_AREA + match.getPoster() + "!middle",
                itemHolder.imgMatchPoster,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        /**
                         * 实际测算发现居然要测量两次才可以正确显示，
                         * 而且，那个宽度值。。。真是日了狗了
                         * 有谁知道原因？？？？
                         */
                        int imgWidth = loadedImage.getWidth();
                        int imgHeight = loadedImage.getHeight();
                        int height = width * imgHeight / imgWidth;

                        //LogUtil.e(TAG, "imgWidth : " + imgWidth + " imgHeight : " + imgHeight + " width : " + width + " height : " + height + " layoutheight : " +  h );
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height);
                        itemHolder.imgMatchPoster.setLayoutParams(params);
                        itemHolder.imgMatchPoster.setScaleType(ImageView.ScaleType.FIT_XY);
                        Bitmap roundedCornerBitmap = ImageUtils.getRoundedCornerBitmap(loadedImage, Utils.dp2px(4), Utils.dp2px(4));
                        ((ImageView) view).setImageBitmap(roundedCornerBitmap);
                        int endHeight = itemHolder.imgMatchPoster.getMeasuredWidth() * imgHeight / imgWidth;
                        itemHolder.imgMatchPoster.getLayoutParams().height = endHeight;
                        itemHolder.imgMatchPoster.requestLayout();
                       // LogUtil.e(TAG, "------   imgWidth : " + roundedCornerBitmap.getWidth() + " imgHeight : " + roundedCornerBitmap.getHeight() + " width : " + itemHolder.imgMatchPoster.getMeasuredWidth() + " height : " + height + " layoutheight : " +  itemHolder.imgMatchPoster.getMeasuredHeight());
                        itemHolder.llMatchBy.measure(-1, -1);
                        ((RelativeLayout.LayoutParams) itemHolder.llMatchBy.getLayoutParams()).topMargin = endHeight - itemHolder.llMatchBy.getMeasuredHeight();
                        itemHolder.llMatchBy.requestLayout();
                    }

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        super.onLoadingStarted(imageUri, view);
                        itemHolder.imgMatchPoster.setScaleType(ImageView.ScaleType.FIT_XY);
                        Bitmap bitmap = ((BitmapDrawable) ((ImageView) view).getDrawable()).getBitmap();
                        ((ImageView) view).setImageBitmap(ImageUtils.getRoundedCornerBitmap(bitmap, Utils.dp2px(4), Utils.dp2px(4)));
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        super.onLoadingFailed(imageUri, view, failReason);
                        itemHolder.imgMatchPoster.setScaleType(ImageView.ScaleType.FIT_XY);
                        Bitmap bitmap = ((BitmapDrawable) ((ImageView) view).getDrawable()).getBitmap();
                        ((ImageView) view).setImageBitmap(ImageUtils.getRoundedCornerBitmap(bitmap, Utils.dp2px(4), Utils.dp2px(4)));
                    }
                });


    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_match_poster)
        ImageView imgMatchPoster;
        @Bind(R.id.tv_match_title)
        TextView tvMatchTitle;
        @Bind(R.id.tv_match_start_time)
        TextView tvMatchStartTime;
        @Bind(R.id.pb_match)
        CornerProgressView pbMatch;
        @Bind(R.id.tv_current_count)
        TextView tvCurrentCount;
        @Bind(R.id.tv_max_count)
        TextView tvMaxCount;
        @Bind(R.id.im_match_state)
        ImageView imMatchState;
        @Bind(R.id.ll_match_by)
        LinearLayout llMatchBy;
        @Bind(R.id.tv_match_by)
        TextView tvMatchBy;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }
}
