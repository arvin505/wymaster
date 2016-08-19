package com.miqtech.master.client.adapter;

import java.util.List;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.YueZhan;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class TowardTAYueZhanAdapter extends BaseAdapter {
	private Context context;
	private List<YueZhan> wars;
	private int height ;

	public TowardTAYueZhanAdapter(Context context, List<YueZhan> wars,int height) {
		this.context = context;
		this.wars = wars;
		this.height = height;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return wars.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return wars.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (v == null) {
			v = View.inflate(context, R.layout.layout_mywar_item, null);
			holder = new ViewHolder();
			holder.ivYueZhan = (ImageView) v.findViewById(R.id.ivYueZhan);
			holder.ivYueZhanHeader = (ImageView) v.findViewById(R.id.ivYueZhanHeader);
			holder.ivYueZhanMerchant = (ImageView) v.findViewById(R.id.ivYueZhanMerchant);
			holder.tvYueZhanTitle = (TextView) v.findViewById(R.id.tvYueZhanTitle);
			holder.tvYueZhanNetBar = (TextView) v.findViewById(R.id.tvYueZhanNetBar);
			holder.tvYueZhanTime = (TextView) v.findViewById(R.id.tvYueZhanTime);
			holder.tvYueZhanDes = (TextView) v.findViewById(R.id.tvYueZhanDes);
			holder.tvYueZhanPepNum = (TextView) v.findViewById(R.id.tvYueZhanPepNum);
			holder.tvYueZhanServer = (TextView) v.findViewById(R.id.tvYueZhanServer);
			holder.llContent = (LinearLayout)v.findViewById(R.id.llContent);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + wars.get(position).getItem_bg_pic_media(),
				holder.ivYueZhan);
		AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + wars.get(position).getReleaser_icon(),
				holder.ivYueZhanHeader);
		holder.tvYueZhanPepNum.setText(wars.get(position).getApply_count() + "/" + wars.get(position).getPeople_num());
		holder.tvYueZhanTitle.setText(wars.get(position).getTitle());
		if (wars.get(position).getWay() == 1) {
			holder.tvYueZhanNetBar.setText("【线上】");
		} else {
			holder.tvYueZhanNetBar.setText(wars.get(position).getAddress());
		}

		if (wars.get(position).getBy_merchant() == 1) {
			holder.ivYueZhanMerchant.setVisibility(View.VISIBLE);
		} else {
			holder.ivYueZhanMerchant.setVisibility(View.GONE);
		}
		if(position == wars.size()-1){
			LinearLayout layout = new LinearLayout(context);
			LayoutParams lpl = new LayoutParams(LayoutParams.MATCH_PARENT, height);
			layout.setLayoutParams(lpl);
			holder.llContent.addView(layout);
		}
		holder.tvYueZhanServer.setText(wars.get(position).getServer());
		holder.tvYueZhanTime.setText(DateUtil.dateToStrLong(wars.get(position).getBegin_time()));
		holder.tvYueZhanDes.setText(wars.get(position).getSpoils());
//		v.setOnClickListener(new OnClickListener() {
//			private Intent intent;
//
//			@Override
//			public void onClick(View v) {
//				intent = new Intent(context, YueZhanDetailsActivity.class);
//				intent.putExtra("id", wars.get(position).getId() + "");
//				context.startActivity(intent);
//			}
//		});
		return v;
	}

	private class ViewHolder {
		ImageView ivYueZhan;
		ImageView ivYueZhanHeader;
		ImageView ivYueZhanMerchant;
		TextView tvYueZhanTitle;
		TextView tvYueZhanNetBar;
		TextView tvYueZhanTime;
		TextView tvYueZhanDes;
		TextView tvYueZhanPepNum;
		TextView tvYueZhanServer;
		LinearLayout llContent;
	}

}
