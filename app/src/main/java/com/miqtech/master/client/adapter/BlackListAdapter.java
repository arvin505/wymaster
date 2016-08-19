package com.miqtech.master.client.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.BlackUser;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;

public class BlackListAdapter extends BaseAdapter {
	private Context context;
	private List<BlackUser> users;
	private BlackListOnClickListener listener;

	public BlackListAdapter(Context context, List<BlackUser> users, BlackListOnClickListener listener) {
		this.context = context;
		this.users = users;
		this.listener = listener;
	}

	@Override
	public int getCount() {
		return users.size();
	}

	@Override
	public Object getItem(int position) {
		return users.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View v, ViewGroup parent) {
		ViewHolder holder;
		if (v == null) {
			v = View.inflate(context, R.layout.layout_gvblack_item, null);
			holder = new ViewHolder();
			holder.ivHeader = (ImageView) v.findViewById(R.id.iv_gvblack_item);
			holder.tvUserName = (TextView) v.findViewById(R.id.tv_name_gvblack_item);
			holder.ivBlackListType = (ImageView) v.findViewById(R.id.iv_delect_gvblack_item);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		// if (users.get(position).getNickname() == null) {
		if (position == users.size() - 1) {
			holder.tvUserName.setVisibility(View.INVISIBLE);
			holder.ivHeader.setImageResource(R.drawable.delect_begin);
			holder.ivBlackListType.setVisibility(View.INVISIBLE);
		} else {
			AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + users.get(position).getIcon(),
					holder.ivHeader);
			holder.tvUserName.setText(users.get(position).getNickname());

			if (1 == users.get(position).getIsshow()) {
				holder.ivBlackListType.setVisibility(View.VISIBLE);
			} else if (0 == users.get(position).getIsshow()) {
				holder.ivBlackListType.setVisibility(View.INVISIBLE);
			}

			holder.ivBlackListType.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					listener.blackHandle(position);
				}
			});

		}

		// if(position <= 1){
		// holder.tvUserName.setVisibility(View.INVISIBLE);
		// }else{
		// if(users.size() <position){
		// holder.tvUserName.setVisibility(View.INVISIBLE);
		// }else{
		// AsyncImage.loadAvatar(context, HttpConnector.SERVICE_UPLOAD_AREA +
		// users.get(position).getIcon(),
		// holder.ivHeader);
		// holder.tvUserName.setText(users.get(position).getNickname());
		// if (1 == users.get(position).getIsshow()) {
		// holder.ivBlackListType.setVisibility(View.VISIBLE);
		// } else if (0 == users.get(position).getIsshow()) {
		// holder.ivBlackListType.setVisibility(View.INVISIBLE);
		// }
		// }
		// }

		return v;
	}

	private class ViewHolder {
		ImageView ivHeader;
		TextView tvUserName;
		ImageView ivBlackListType;
	}

	public interface BlackListOnClickListener {
		void blackHandle(int position);
	}
}
