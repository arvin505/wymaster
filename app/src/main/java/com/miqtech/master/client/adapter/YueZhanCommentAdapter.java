package com.miqtech.master.client.adapter;

import java.util.List;

import com.miqtech.master.client.R;

import com.miqtech.master.client.entity.YueZhanComment;

import com.miqtech.master.client.http.HttpConstant;

import com.miqtech.master.client.ui.PersonalHomePageActivity;
import com.miqtech.master.client.utils.AsyncImage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class YueZhanCommentAdapter extends BaseAdapter {
	private Context context;
	private List<YueZhanComment> comments;

	public YueZhanCommentAdapter(Context context, List<YueZhanComment> comments) {
		this.context = context;
		this.comments = comments;
	}

	public void setData(List<YueZhanComment> data){
		this.comments = data;
	}

	@Override
	public int getCount() {
		return comments.size();
	}

	@Override
	public Object getItem(int position) {
		return comments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder;
		if (v == null) {
			v = LayoutInflater.from(context).inflate(R.layout.layout_yuezhancomment_item, parent, false);
			holder = new ViewHolder();
			holder.ivHeader = (ImageView) v.findViewById(R.id.ivHeader);
			holder.tvName = (TextView) v.findViewById(R.id.tvName);
			holder.tvComment = (TextView) v.findViewById(R.id.tvComment);
			holder.tvDate = (TextView) v.findViewById(R.id.tvDate);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + comments.get(position).getUser_icon(),
				holder.ivHeader);
		holder.tvName.setText(comments.get(position).getUser_nickname());
		holder.tvComment.setText(comments.get(position).getContent());
		holder.tvDate.setText(comments.get(position).getCreate_date());

		if (!("".equals(comments.get(position).getUser_id())) && comments.get(position).getUser_id() != null) {
			toPersonalHome(holder.ivHeader, comments.get(position).getUser_id());
		}
		return v;
	}

	// 跳到个人主页
	private void toPersonalHome(View v, final String id) {
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, PersonalHomePageActivity.class);
				intent.putExtra("id", id);
				context.startActivity(intent);
			}
		});
	}

	private class ViewHolder {
		ImageView ivHeader;
		TextView tvName;
		TextView tvComment;
		TextView tvDate;
	}

}
