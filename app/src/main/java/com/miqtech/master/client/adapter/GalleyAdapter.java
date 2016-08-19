package com.miqtech.master.client.adapter;

import java.util.List;
import java.util.Map;

import com.miqtech.master.client.R;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GalleyAdapter extends BaseAdapter {

	private Context context;
	private List<Map<String, String>> images;

	public GalleyAdapter(Context context, List<Map<String, String>> images) {
		this.context = context;
		this.images = images;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return images.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return getItem(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolderr holderr = null;
		
		if(v == null){
			holderr = new ViewHolderr();
			v = LayoutInflater.from(context).inflate(R.layout.layout_galley_item, null);
			holderr.iv = (ImageView) v.findViewById(R.id.galley_image);
			v.setTag(holderr);
		}else{
			holderr = (ViewHolderr)v.getTag();
		}
		AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + images.get(position).get("url"), holderr.iv);// 显示图片
		return v;
	}

	class ViewHolderr {
		ImageView iv;
	}

}
