package com.miqtech.master.client.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.Game;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.GameDetailActivity;
import com.miqtech.master.client.utils.AsyncImage;

@SuppressLint("ResourceAsColor")
public class WeekHotAdapter extends BaseAdapter{
	Context context;
	List<Game> gameList;
	private DownLoadListen downloadListen;

	public WeekHotAdapter(Context context, List<Game> gameList,DownLoadListen listen) {
		this.context = context;
		this.gameList = gameList;
		this.downloadListen = listen;
	}

	@Override
	public int getCount() {
		return gameList.size();
	}

	@Override
	public Object getItem(int position) {
		return gameList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View v, ViewGroup parent) {
		ViewHolder holder;
		if (v == null) {
			v = LayoutInflater.from(context).inflate(R.layout.layout_gamesort_item, parent, false);
			holder = new ViewHolder();
			holder.tvContent = (TextView) v.findViewById(R.id.tv_des);
			holder.tvTitle = (TextView) v.findViewById(R.id.tv_title);
			holder.tvDownloadCount = (TextView) v.findViewById(R.id.tv_download_count);
			holder.btnDownload = (Button) v.findViewById(R.id.btnDownload);
			holder.ivGame = (ImageView) v.findViewById(R.id.iv_game);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		initHodler(holder, position);
		v.setOnClickListener(new OnClickListener() {
			private Intent intent;
			@Override
			public void onClick(View v) {
				Game game = (Game) getItem(position);
				if(game==null){
					return;
				}
				intent = new Intent(context, GameDetailActivity.class);
				intent.putExtra("id",game.getId());
				context.startActivity(intent);
			}
		});
		return v;
	}

	private void initHodler(ViewHolder holder, final int position) {
		Game game = gameList.get(position);
		holder.tvTitle.setText(game.getName());
		holder.tvContent.setText(game.getDes());
		if(game.getDownload_count()>0){
			holder.tvDownloadCount.setText(game.getDownload_count()+"次下载");
		}else{
			holder.tvDownloadCount.setText("");
		}
		holder.btnDownload.setText(game.getAndroid_file_size()+"MB");
		holder.btnDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Game game = (Game) getItem(position);
				if(game==null){
					return;
				}
				downloadListen.onDownload(game.getId());
			}
		});
		AsyncImage.loadGameRoundPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + game.getIcon(), holder.ivGame);
	}

	private class ViewHolder {
		TextView tvTitle;
		TextView tvContent;
		TextView tvDownloadCount;
		Button btnDownload;
		ImageView ivGame;
	}

	public interface DownLoadListen{
		void onDownload(int id);
	}

}
