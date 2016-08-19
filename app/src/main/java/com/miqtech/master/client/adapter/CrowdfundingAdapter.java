package com.miqtech.master.client.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.CoinsStoreGoods;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CornerProgressView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/5/9.
 */
public class CrowdfundingAdapter extends RecyclerView.Adapter {

    private List<CoinsStoreGoods> mdatas;
    private Context mContext;
    private LayoutInflater inflater;
    private boolean showFooter = false;
    private final int VIEW_ITEM = 0;
    private final int VIEW_FOOTER = 1;

    public CrowdfundingAdapter(Context context, List<CoinsStoreGoods> datas) {
        this.mContext = context;
        this.mdatas = datas;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case VIEW_ITEM:
                view = inflater.inflate(R.layout.layout_crowdlist_recycle_item, parent, false);
                holder = new ItemHolder(view);
                break;
            case VIEW_FOOTER:
                view = inflater.inflate(R.layout.layout_footer_view, parent, false);
                holder = new FooterHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            initItemView((ItemHolder) holder, position);
        }
    }

    /**
     * init iten view
     *
     * @return
     */
    private void initItemView(ItemHolder holder, final int position) {
        CoinsStoreGoods data = mdatas.get(position);
        AsyncImage.loadNetPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + data.getMainIcon(),
                holder.imgGoodIcon);
        holder.tvGoodName.setText(data.getCommodityName());
        holder.tvCrowdWinNum.setText(Utils.changeString(data.getPrizePhone()));
        holder.llCrowdWinning.setVisibility(data.getCrowdfundStatus() == 2 ? View.VISIBLE : View.INVISIBLE);
        holder.pbGood.setVisibility(data.getCrowdfundStatus() == 2 ? View.INVISIBLE : View.VISIBLE);
        holder.pbGood.setMaxCount(100);
        holder.pbGood.setCurrentCount(data.getProgress());
        holder.tvGoodCost.setText(data.getPrice() + "");

        holder.tvGoodCount.setText("还剩" + data.getLeftNum() + "人");
        ForegroundColorSpan orangeSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.orange));
        SpannableStringBuilder builder = new SpannableStringBuilder(holder.tvGoodCount.getText().toString());
        builder.setSpan(orangeSpan, 2, builder.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvGoodCount.setText(builder);

        holder.tvPercent.setText((int)data.getProgress() + "%");
        holder.tvPercent.setVisibility(data.getCrowdfundStatus() == 2 ? View.INVISIBLE : View.VISIBLE);
        holder.imgCrowdRush.setImageResource(data.getLeftNum() == 0 ? R.drawable.icon_rush_end : R.drawable.icon_rush);
        holder.imgCrowdState.setVisibility(data.getCrowdfundStatus() == 0 ? View.GONE : View.VISIBLE);
        holder.imgCrowdState.setImageResource(data.getCrowdfundStatus() == 1 ? R.drawable.icon_stamp_begin : R.drawable.icon_stamp_end);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(v, position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mdatas == null || mdatas.isEmpty() ? 0 : showFooter ? mdatas.size() + 1 : mdatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (showFooter && position == getItemCount() - 1) {
            return VIEW_FOOTER;
        } else {
            return VIEW_ITEM;
        }
    }


    /**
     * 加载更多
     */
    class FooterHolder extends RecyclerView.ViewHolder {
        public FooterHolder(View itemView) {
            super(itemView);
        }
    }

    public void showFooter() {
        notifyItemInserted(getItemCount());
        showFooter = true;
    }

    public void hideFooter() {
        notifyItemRemoved(getItemCount() - 1);
        showFooter = false;
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imgGoodIcon)
        ImageView imgGoodIcon;
        @Bind(R.id.tvGoodName)
        TextView tvGoodName;
        @Bind(R.id.tvGoodCost)
        TextView tvGoodCost;
        @Bind(R.id.tvGoodCount)
        TextView tvGoodCount;
        @Bind(R.id.pbGood)
        CornerProgressView pbGood;
        @Bind(R.id.tvCrowdWinNum)
        TextView tvCrowdWinNum;
        @Bind(R.id.llCrowdWinning)
        LinearLayout llCrowdWinning;
        @Bind(R.id.imgCrowdRush)
        ImageView imgCrowdRush;
        @Bind(R.id.tvPercent)
        TextView tvPercent;
        @Bind(R.id.imgCrowdState)
        ImageView imgCrowdState;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int postion);
    }
}
