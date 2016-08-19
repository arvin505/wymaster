package com.miqtech.master.client.adapter;

import java.util.List;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.Image;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GameImageAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<Image> urls ;

	public GameImageAdapter(Context context,List<Image> urls) {
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.urls = urls ;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;   //返回很大的值使得getView中的position不断增大来实现循环
	}

	@Override
	public Object getItem(int position) {
		return urls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.image_item, null);
		}
		AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + urls.get(position % urls.size()).getUrl(), ((ImageView) convertView.findViewById(R.id.imgView)));
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		return convertView;
	}


}
