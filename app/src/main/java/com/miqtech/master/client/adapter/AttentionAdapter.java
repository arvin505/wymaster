package com.miqtech.master.client.adapter;

import java.util.List;




import com.miqtech.master.client.R;

import com.miqtech.master.client.entity.Fans;

import com.miqtech.master.client.http.HttpConstant;

import com.miqtech.master.client.ui.PersonalHomePageActivity;
import com.miqtech.master.client.utils.AsyncImage;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AttentionAdapter extends BaseAdapter {

	private List<Fans> fans;
	private Context context;
	private AttentionListener listener;
	private int type;

	public AttentionAdapter(List<Fans> fans, Context context, AttentionListener listener,int type) {
		this.fans = fans;
		this.context = context;
		this.listener = listener;
		this.type = type ;
	}

	@Override
	public int getCount() {
		return fans.size();
	}

	@Override
	public Object getItem(int position) {
		return fans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder;
		if (v == null) {
			v = View.inflate(context, R.layout.layout_attention_item, null);
			holder = new ViewHolder();
			holder.ivHeader = (ImageView) v.findViewById(R.id.ivHeader);
			holder.tvUserName = (TextView) v.findViewById(R.id.tvUserName);
			holder.ivAtt = (ImageView) v.findViewById(R.id.ivAtt);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		Fans fan = fans.get(position);
		AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + fan.getIcon(), holder.ivHeader);
		holder.tvUserName.setText(fan.getNickname());
		if(type == 1){
			holder.ivAtt.setVisibility(View.VISIBLE);
			if (fan.getIs_valid() == 1) {
				Resources res = context.getResources();
				Bitmap attentionSuccess = BitmapFactory.decodeResource(res, R.drawable.att_success_icon);
				holder.ivAtt.setImageBitmap(attentionSuccess);
			} else if (fan.getIs_valid() == 0) {
				Resources res = context.getResources();
				Bitmap attention = BitmapFactory.decodeResource(res, R.drawable.att_icon);
				holder.ivAtt.setImageBitmap(attention);
			}
		}else{
			holder.ivAtt.setVisibility(View.GONE);
		}
		
		ItemOnClickListener itemClickListener = new ItemOnClickListener(position);
		holder.ivAtt.setOnClickListener(itemClickListener);
		v.setOnClickListener(itemClickListener);
		return v;
	}

	private class ViewHolder {
		ImageView ivHeader;
		TextView tvUserName;
		ImageView ivAtt;
	}

	public interface AttentionListener {
		void attentionUser(int id, int position);

		void cancelAttentionUser(int id, int position);
		
	}

	private class ItemOnClickListener implements OnClickListener {
		int position;

		ItemOnClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ivAtt:
				if (fans.get(position).getIs_valid() == 1) {
					listener.cancelAttentionUser(fans.get(position).getId(),position);
				} else if (fans.get(position).getIs_valid() == 0) {
					listener.attentionUser(fans.get(position).getId(),position);
				}
				break;
			case R.id.llAttention:
				Intent intent = new Intent();
				intent.setClass(context, PersonalHomePageActivity.class);
				intent.putExtra("id", fans.get(position).getId()+"");
				context.startActivity(intent);
				break;
			default:
				break;
			}

		}

	}
}
