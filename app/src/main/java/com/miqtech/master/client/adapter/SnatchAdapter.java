package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.CoinsStoreGoods;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.ShopDetailActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CornerProgressView;

import java.util.List;

/**
 * Created by admin on 2016/5/6.
 */
public class SnatchAdapter extends BaseAdapter {
    private List<CoinsStoreGoods> snatchs;

    private Context context;

    public SnatchAdapter(Context context, List<CoinsStoreGoods> snatchs) {
        this.context = context;
        this.snatchs = snatchs;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (snatchs == null) {
            return 0;
        }
        return snatchs.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return snatchs.get(arg0);
    }

    public List<CoinsStoreGoods> getItems() {
        return snatchs;
    }

    public void setData(List<CoinsStoreGoods> data) {
        snatchs = data;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(final int arg0, View arg1, final ViewGroup arg2) {
        // TODO Auto-generated method stub
        ViewHolder holder;

        if (arg1 == null) {
            holder = new ViewHolder();
            arg1 = LayoutInflater.from(context).inflate(R.layout.layout_crowdlist_recycle_item, null);
            holder.imgGoodIcon = (ImageView) arg1.findViewById(R.id.imgGoodIcon);
            holder.tvGoodName = (TextView) arg1.findViewById(R.id.tvGoodName);
            holder.tvGoodCost = (TextView) arg1.findViewById(R.id.tvGoodCost);
            holder.tvGoodCount = (TextView) arg1.findViewById(R.id.tvGoodCount);
            holder.pbGood = (CornerProgressView) arg1.findViewById(R.id.pbGood);
            holder.tvCrowdWinNum = (TextView) arg1.findViewById(R.id.tvCrowdWinNum);
            holder.llCrowdWinning = (LinearLayout) arg1.findViewById(R.id.llCrowdWinning);
            holder.imgCrowdRush = (ImageView) arg1.findViewById(R.id.imgCrowdRush);
            holder.tvPercent = (TextView) arg1.findViewById(R.id.tvPercent);
            holder.imgCrowdState = (ImageView) arg1.findViewById(R.id.imgCrowdState);
            holder.llmaster = (LinearLayout) arg1.findViewById(R.id.llmaster);
            arg1.setTag(holder);
        } else {
            holder = (ViewHolder) arg1.getTag();
        }
        final CoinsStoreGoods data = snatchs.get(arg0);

        AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + data.getMainIcon(), holder.imgGoodIcon);// 显示图片

        holder.tvGoodName.setText(data.getCommodityName());// 显示商品名字

        holder.tvGoodCost.setText(data.getPrice() + "");// 商品价格

        holder.tvCrowdWinNum.setText(Utils.changeString(data.getPrizePhone()));
        holder.llCrowdWinning.setVisibility(data.getCrowdfundStatus() == 2 ? View.VISIBLE : View.INVISIBLE);
        holder.pbGood.setVisibility(data.getCrowdfundStatus() == 2 ? View.INVISIBLE : View.VISIBLE);
        holder.pbGood.setMaxCount(100);
        holder.pbGood.setCurrentCount(data.getProgress());
        holder.tvGoodCost.setText(data.getPrice() + "");

        holder.tvGoodCount.setText("还剩" + data.getLeftNum() + "人");
        ForegroundColorSpan orangeSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.orange));
        SpannableStringBuilder builder = new SpannableStringBuilder(holder.tvGoodCount.getText().toString());
        builder.setSpan(orangeSpan, 2, ("还剩" + data.getLeftNum() + "人").length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvGoodCount.setText(builder);
        holder.tvPercent.setText((int) data.getProgress() + "%");
        holder.imgCrowdRush.setImageResource(data.getLeftNum() == 0 ? R.drawable.icon_rush_end : R.drawable.icon_rush);
        holder.tvPercent.setVisibility(data.getCrowdfundStatus() == 2 ? View.INVISIBLE : View.VISIBLE);
        holder.imgCrowdState.setVisibility(data.getCrowdfundStatus() == 0 ? View.GONE : View.VISIBLE);
        holder.imgCrowdState.setImageResource(data.getCrowdfundStatus() == 1 ? R.drawable.icon_stamp_begin : R.drawable.icon_stamp_end);
        //最后一个item 让paddingbottom为0 去掉分割item
        if (snatchs != null && snatchs.size() != 0 && arg0 == snatchs.size() - 1 && arg2.getChildCount() == arg0) {
            holder.llmaster.setPadding(0, 0, 0, 0);
        } else {
//            holder.llmaster.setPadding(0,0,0, Utils.px2dp(5));
        }
        arg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShopDetailActivity.class);
                intent.putExtra("coinsStoreGoods", data);
                intent.putExtra("itemPos", arg0);
                context.startActivity(intent);

            }
        });

        return arg1;
    }

    class ViewHolder {
        ImageView imgGoodIcon;// 商品图片
        TextView tvGoodName;// 商品名字
        TextView tvGoodCost;// 商品价格显示
        TextView tvGoodCount; //剩余人数
        CornerProgressView pbGood; //商品进度条
        LinearLayout llCrowdWinning;
        TextView tvCrowdWinNum;//中奖号码
        ImageView imgCrowdRush; //抢图标
        TextView tvPercent; //完成百分比
        ImageView imgCrowdState; //抢状态
        LinearLayout llmaster;//parentView
    }

}
