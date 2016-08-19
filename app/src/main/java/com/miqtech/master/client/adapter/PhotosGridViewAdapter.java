package com.miqtech.master.client.adapter;

import java.util.List;



import com.miqtech.master.client.R;

import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.UserImage;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.PersonalHomePageActivity;
import com.miqtech.master.client.utils.AsyncImage;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PhotosGridViewAdapter extends BaseAdapter {
	private Context context;
	private List<UserImage> photos;
	public int type;

	public PhotosGridViewAdapter(Context context, List<UserImage> photos, int type) {
		this.context = context;
		this.photos = photos;
		this.type = type;
	}

	@Override
	public int getCount() {
		return photos.size();
	}

	@Override
	public Object getItem(int position) {
		return photos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder;
		if (v == null) {
			holder = new ViewHolder();
			v = View.inflate(context, R.layout.layout_photo_view, null);
			holder.ivPhoto = (ImageView) v.findViewById(R.id.ivPhoto);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		if (position == 0) {
			if (type == PersonalHomePageActivity.SELF_PHOTO) {
				AsyncImage.loadPhoto(context, R.drawable.add_photo_icon, holder.ivPhoto);
			}else{
				AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + photos.get(position).getImg()+"!small",
						holder.ivPhoto);
			}
		} else {
			AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + photos.get(position).getImg()+"!small",
					holder.ivPhoto);
		}
		holder.ivPhoto.setLayoutParams(new LinearLayout.LayoutParams(WangYuApplication.WIDTH / 3,
				WangYuApplication.WIDTH / 3));
		return v;
	}

	private class ViewHolder {
		ImageView ivPhoto;
	}

}
