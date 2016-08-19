package com.miqtech.master.client.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
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
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.zhy.autolayout.utils.ScreenUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaoyi on 2016/5/11.
 * 商品兑换列表adapter
 */
public class ExpiryListAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<CoinsStoreGoods> mDatas;
    private LayoutInflater inflater;
    private boolean showFooter = false;

    private final int VIEW_ITEM = 1;
    private final int VIEW_FOOTER = 2;

    public ExpiryListAdapter(Context context, List<CoinsStoreGoods> datas) {
        this.mContext = context;
        this.mDatas = datas;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view = null;
        switch (viewType) {
            case VIEW_ITEM:
                view = inflater.inflate(R.layout.item_coins_store, parent, false);
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

    /***
     * 初始化item
     *
     * @return
     */
    private void initItemView(final ItemHolder holder, final int position) {
        CoinsStoreGoods data = mDatas.get(position);
        holder.tvGoodsName.setText(data.getCommodityName());
        holder.tvGoodsCoinsPrice.setText(data.getPrice() + "");
        AsyncImage.loadNetPhotoWithListener( HttpConstant.SERVICE_UPLOAD_AREA + data.getMainIcon(),
                holder.imgGoodsCoinsDuihuan,new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        holder.imgGoodsCoinsDuihuan.setScaleType(ImageView.ScaleType.FIT_XY);
                        super.onLoadingComplete(imageUri, view, loadedImage);
                    }

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        holder.imgGoodsCoinsDuihuan.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        super.onLoadingStarted(imageUri, view);
                    }
                });
        holder.imgGoodsCoinsDuihuan.measure(-1, -1);
        holder.itemView.setPadding(Utils.dp2px(6), 0, Utils.dp2px(6), 0);
        int width = (ScreenUtils.getScreenSize(mContext)[0] - holder.itemView.getPaddingLeft() + holder.itemView.getPaddingRight() - Utils.dp2px(12)) / 2;
        holder.imgGoodsCoinsDuihuan.getLayoutParams().height = width;
        holder.imgGoodsCoinsDuihuan.requestLayout();
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
        return mDatas == null || mDatas.isEmpty() ? 0 : showFooter ? mDatas.size() + 1 : mDatas.size();
    }

    class FooterHolder extends RecyclerView.ViewHolder {
        public FooterHolder(View itemView) {
            super(itemView);
        }
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_goods_coins_duihuan)
        ImageView imgGoodsCoinsDuihuan;
        @Bind(R.id.tvGoodsName)
        TextView tvGoodsName;
        @Bind(R.id.tvGoodsCoinsPrice)
        TextView tvGoodsCoinsPrice;
        @Bind(R.id.llGoodsDetail)
        LinearLayout llGoodsDetail;

        ItemHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
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

    @Override
    public int getItemViewType(int position) {
        return showFooter && position == getItemCount() - 1 ? VIEW_FOOTER : VIEW_ITEM;
    }

    private OnItemClickListener mListener;

    public void setOnitemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
