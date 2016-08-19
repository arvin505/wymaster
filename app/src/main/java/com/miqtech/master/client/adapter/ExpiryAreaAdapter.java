package com.miqtech.master.client.adapter;

import java.util.List;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.CoinsStoreGoods;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.SubjectActivity;
import com.miqtech.master.client.utils.AsyncImage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * 金币专区
 * 
 * @author Administrator
 */
public class ExpiryAreaAdapter extends BaseAdapter {
	private List<CoinsStoreGoods> goodsCoins;
	private Context context;
	private int width, height;

	public ExpiryAreaAdapter(Context context, List<CoinsStoreGoods> goodsCoins, int width) {
		this.context = context;
		this.goodsCoins = goodsCoins;
		this.width = width;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(goodsCoins==null){
			return 0;
		}
		return goodsCoins.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return goodsCoins.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder ;
		if (arg1 == null) {
			holder = new ViewHolder();
			arg1 = LayoutInflater.from(context).inflate(R.layout.item_coins_store, null);
			holder.parentView=(LinearLayout)arg1.findViewById(R.id.parentView);
			holder.icon = (ImageView) arg1.findViewById(R.id.img_goods_coins_duihuan);
			holder.goodsName = (TextView) arg1.findViewById(R.id.tvGoodsName);
			holder.goodsPrice = (TextView) arg1.findViewById(R.id.tvGoodsCoinsPrice);
			holder.goodsBt = (LinearLayout) arg1.findViewById(R.id.llGoodsDetail);
			arg1.setTag(holder);
		} else {
			holder = (ViewHolder) arg1.getTag();
		}

		CoinsStoreGoods bean = goodsCoins.get(arg0);

		AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + bean.getMainIcon(), holder.icon);// 显示图片

		holder.goodsName.setText(bean.getCommodityName());// 显示商品名字

		holder.goodsPrice.setText(bean.getPrice() + "");// 商品价格

		holder.icon.setLayoutParams(new LayoutParams(width, width));
        holder.parentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, SubjectActivity.class);
				intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.GOODS_DETAIL);
				intent.putExtra("titletName", goodsCoins.get(arg0).getCommodityName());
				intent.putExtra("id", goodsCoins.get(arg0).getId() + "");
				intent.putExtra("totalCoins", goodsCoins.get(arg0).getPrice());
				context.startActivity(intent);
			}
		});

		return arg1;
	}

	class ViewHolder {
		ImageView icon;// 商品图片
		TextView goodsName;// 商品名字
		TextView goodsPrice;// 商品价格显示
		LinearLayout goodsBt;// 商品按钮点击
		LinearLayout parentView; //item
	}

}
