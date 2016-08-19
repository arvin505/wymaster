package com.miqtech.master.client.adapter;

import java.util.List;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.Game;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyGameAdapter extends BaseAdapter {

	private List<Game> games;

	private Context context;

	public MyGameAdapter(Context context, List<Game> games) {
		this.games = games;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return games.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return games.get(position);
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
			holder = new ViewHolder();
			v = LayoutInflater.from(context).inflate(R.layout.layout_mygame_item, parent, false);
			holder.ivGame = (ImageView)v.findViewById(R.id.ivGame);
			holder.tvGameName = (TextView)v.findViewById(R.id.tvGame);
			holder.tvGameContent = (TextView)v.findViewById(R.id.tvGameContent);
			v.setTag(holder);
		}else{
			holder = (ViewHolder)v.getTag();
		}
		holder.tvGameContent.setText(games.get(position).getIntro());
		holder.tvGameName.setText(games.get(position).getName());
		AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + games.get(position).getIcon(), holder.ivGame);
		return v;
	}

	private class ViewHolder {
		ImageView ivGame;
		TextView tvGameName;
		TextView tvGameContent;
	}
}
