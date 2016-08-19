package com.miqtech.master.client.adapter;

import java.util.List;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.Fans;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FansAdapter extends BaseAdapter {
	private List<Fans> fans ;
	private Context context ;
	
	public FansAdapter(List<Fans> fans,Context context){
		this.fans = fans ;
		this.context = context ;
	}

	@Override
	public int getCount() {
		return fans.size();
	}

	@Override
	public Object getItem(int position) {
		return fans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder ;
		if(v == null){
			v = View.inflate(context, R.layout.layout_fans_item, null);
			holder = new ViewHolder();
			holder.ivHeader = (ImageView)v.findViewById(R.id.ivHeader);
			holder.tvUserName = (TextView)v.findViewById(R.id.tvUserName);
			v.setTag(holder);
		}else{
			holder = (ViewHolder)v.getTag();
		}
		Fans fan = fans.get(position);
		AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + fan.getIcon(), holder.ivHeader);
		holder.tvUserName.setText(fan.getNickname());
		return v;
	}
	
	private class ViewHolder{
		ImageView ivHeader ;
		TextView tvUserName;
	}

}
