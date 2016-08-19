package com.miqtech.master.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.MatchJoiner;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;

import java.util.List;

public class JoinerGridViewAdapter extends BaseAdapter {
	private List<MatchJoiner> joiners;
	private Context context;

	public JoinerGridViewAdapter(Context context,List<MatchJoiner> joiners) {
		this.joiners = joiners;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(joiners.size()>21){
			return 21;
		}else{
			return joiners.size();
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return joiners.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (v == null) {
			v = LayoutInflater.from(context).inflate(R.layout.layout_matchjoiner_item, parent, false);
			holder = new ViewHolder();
			v.setTag(holder);
			holder.ivJoiner = (ImageView) v.findViewById(R.id.ivUserHeader);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		float screenWidth = WangYuApplication.WIDTH - 2 * context.getResources().getDimension(R.dimen.ten_dp);
		float itemWidth = screenWidth / 7 - context.getResources().getDimension(R.dimen.ten_dp);
		LayoutParams para = holder.ivJoiner.getLayoutParams();
		MatchJoiner joiner = joiners.get(position);
		para.height = (int) itemWidth;
		para.width = (int) itemWidth;
		holder.ivJoiner.setLayoutParams(para);
		AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + joiner.getIcon(), holder.ivJoiner);

		if (joiners.size() > 21 && position == 20) {

			AsyncImage.loadPhoto(context,

					R.drawable.more_dian,

					holder.ivJoiner);
		}
		return v;
	}

	private class ViewHolder {
		private ImageView ivJoiner;
	}

}
