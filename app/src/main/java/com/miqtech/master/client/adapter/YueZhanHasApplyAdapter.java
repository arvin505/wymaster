package com.miqtech.master.client.adapter;

import java.util.List;

import com.miqtech.master.client.R;

import com.miqtech.master.client.entity.YueZhanApply;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.YueZhanDetailsActivity;
import com.miqtech.master.client.utils.AsyncImage;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class YueZhanHasApplyAdapter extends BaseAdapter {
	private Context context;
	private List<YueZhanApply> applies;
	private HandleListener handleListener;

	public YueZhanHasApplyAdapter(Context context, List<YueZhanApply> applies, HandleListener handleListener) {
		this.context = context;
		this.applies = applies;
		this.handleListener = handleListener;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return applies.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return applies.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (v == null) {
			v = LayoutInflater.from(context).inflate(R.layout.layout_yuezhanapply_item, parent, false);
			holder = new ViewHolder();
			holder.ivHeader = (ImageView) v.findViewById(R.id.ivHeader);
			holder.tvApplyName = (TextView) v.findViewById(R.id.tvApplyName);
			holder.ivDelete = (ImageView) v.findViewById(R.id.ivDelete);
			holder.ivPhone = (ImageView) v.findViewById(R.id.ivPhone);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		if (YueZhanDetailsActivity.yueZhan.getUserStatus() == 1) {
			holder.ivDelete.setVisibility(View.VISIBLE);
			holder.ivDelete.setOnClickListener(new MyListener(position));
			
			holder.ivPhone.setVisibility(View.VISIBLE);
			holder.ivPhone.setOnClickListener(new MyListener(position));
		}
		String releaserId = YueZhanDetailsActivity.yueZhan.getReleaser_id()+"";
		if(releaserId.equals(applies.get(position).getUser_id())){
			holder.ivDelete.setVisibility(View.INVISIBLE);
		}
		AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + applies.get(position).getUser_icon(),
				holder.ivHeader);
		holder.tvApplyName.setText(applies.get(position).getUser_nickname());
		return v;
	}

	private class ViewHolder {
		ImageView ivHeader;
		TextView tvApplyName;
		ImageView ivDelete;
		ImageView ivPhone;
	}

	public class MyListener implements OnClickListener {
		int position;

		MyListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ivDelete:
				handleListener.deletePerson(position);
				break;
			case R.id.ivPhone:
				handleListener.contactPerson(position);
				break;

			default:
				break;
			}
		}
	}

	public interface HandleListener {
		void deletePerson(int position);
		void contactPerson(int position);
	}
}
