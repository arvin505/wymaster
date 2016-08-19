package com.miqtech.master.client.adapter;

import java.util.List;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.WarGame;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectGameAdapter extends BaseAdapter {
	private Context context ;
	private List<WarGame> games;

	public SelectGameAdapter(Context context ,List<WarGame> games){
		this.context = context ;
		this.games = games ;
	}
	@Override
	public int getCount() {
		return games.size();
	}

	@Override
	public Object getItem(int position) {
		return games.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder ;
		if(v == null){
			holder = new ViewHolder();
			v = View.inflate(context, R.layout.layout_select_game_item, null);
			holder.ivGame = (ImageView)v.findViewById(R.id.ivGame);
			holder.tvGame = (TextView)v.findViewById(R.id.tvGame);
			v.setTag(holder);
		}else{
			holder = (ViewHolder)v.getTag();
		}
		AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + games.get(position).getItem_icon(), holder.ivGame);
		holder.tvGame.setText(games.get(position).getItem_name());
		return v;
	}
	
	private class ViewHolder{
		ImageView ivGame;
		TextView tvGame;
	}

}
