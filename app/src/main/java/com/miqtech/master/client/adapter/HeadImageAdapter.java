package com.miqtech.master.client.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.Utils;

public class HeadImageAdapter extends BaseAdapter {

	private List<String> heads;
	private Context mContext;
	private LayoutInflater inflater;

	public HeadImageAdapter(Context mContext, List<String> heads) {
		super();
		this.heads = heads;
		this.mContext = mContext;
		this.inflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return heads.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return heads.get(position);
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.head_image_item, null);
		}
		ImageView iv_head = (ImageView) convertView;
		android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(
				Utils.dip2px(mContext, 87), Utils.dip2px(mContext, 87));
		iv_head.setLayoutParams(params);
		if (parent.getId() == R.id.head_Gride) {
			if ((position == 0 || position == 1)) {
				int imageRes = Integer.parseInt(heads.get(position));
				AsyncImage.loadPhoto(mContext, imageRes, iv_head);
			} else {
				AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + heads.get(position), iv_head);
			}
		}
		if (parent.getId() == R.id.photo_Gride) {
			File imag = new File(heads.get(position));
			AsyncImage.loadPhoto(mContext, imag, iv_head);
		}

		return convertView;
	}
}
