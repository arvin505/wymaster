package com.miqtech.master.client.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.ImageBean;
import com.miqtech.master.client.utils.AsyncImage;

public class AlbumAdapter extends BaseAdapter {
	private List<ImageBean> list;
	private Point mPoint = new Point(0, 0);// 用来封装ImageView的宽和高的对象
	protected LayoutInflater mInflater;
	private Context mContext;

	public void setData(List<ImageBean> list){
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public AlbumAdapter(Context context, List<ImageBean> list) {
		this.list = list;
		mInflater = LayoutInflater.from(context);
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		ImageBean mImageBean = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.my_album_item, null);
			viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.group_image);
			viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.group_title);
			viewHolder.mTextViewCounts = (TextView) convertView.findViewById(R.id.group_count);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String path = mImageBean.getTopImagePath();
		viewHolder.mTextViewTitle.setText(mImageBean.getFolderName());
		viewHolder.mTextViewCounts.setText(Integer.toString(mImageBean.getImageCounts()));
		// 给ImageView设置路径Tag,这是异步加载图片的小技巧
		viewHolder.mImageView.setTag(path);
		File imag = new File(path);
		AsyncImage.loadPhoto(mContext, imag, viewHolder.mImageView);
		return convertView;
	}

	public static class ViewHolder {
		public ImageView mImageView;
		public TextView mTextViewTitle;
		public TextView mTextViewCounts;
	}

}
