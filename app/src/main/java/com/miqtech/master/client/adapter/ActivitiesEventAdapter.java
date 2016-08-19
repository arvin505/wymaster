package com.miqtech.master.client.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.Match;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;

import java.util.List;

@SuppressLint("NewApi")
public class ActivitiesEventAdapter extends BaseAdapter {

	private List<Match> mEvents;// 赛事列表
	private ImageView[] mImageViews;
	private Context mContext;
	private View eventItemView;

	public ActivitiesEventAdapter(List<Match> mEvents, Context mContext) {
		super();
		this.mEvents = mEvents;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {

		return mEvents.size();
	}

	@Override
	public Match getItem(int position) {

		return mEvents.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder;
		// if (convertView == null) {
		// LayoutInflater mInflater = LayoutInflater.from(mContext);
		// convertView = mInflater.inflate(R.layout.event_item, null);
		// eveHolder = new Holder();
		// eveHolder.setEveTitle((TextView)
		// convertView.findViewById(R.id.eveTitle));
		// eveHolder.setEvePic((ImageView)
		// convertView.findViewById(R.id.evePic));
		// eveHolder.setEveDeadline((TextView)
		// convertView.findViewById(R.id.deadline));
		// eveHolder.setEveStartTime((TextView)
		// convertView.findViewById(R.id.eveStartTime));
		// eveHolder.setIndex(position);
		// convertView.setOnClickListener(this);
		// convertView.setTag(eveHolder);
		// } else {
		// eveHolder = (Holder) convertView.getTag();
		// }
		// initViewContent(eveHolder);

		if (v == null) {
			v = LayoutInflater.from(mContext).inflate(R.layout.event_item, parent, false);
			holder = new ViewHolder();
			holder.ivEvent = (ImageView) v.findViewById(R.id.ivEvent);
			holder.tvEndTime = (TextView) v.findViewById(R.id.tvEndTime);
			holder.tvStartTime = (TextView) v.findViewById(R.id.tvStartTime);
			holder.tvTitle = (TextView) v.findViewById(R.id.tvTitle);
			holder.tvType = (TextView) v.findViewById(R.id.tvType);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		holder.tvEndTime.setText("开赛:" + DateUtil.getStringDate(mEvents.get(position).getStart_time()));
		holder.tvStartTime.setText(DateUtil.getStringDate(mEvents.get(position).getStart_time()));
		holder.tvTitle.setText(mEvents.get(position).getTitle());
		AsyncImage.loadRoundPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + mEvents.get(position).getIcon(),
				holder.ivEvent);
		holder.tvEndTime.getBackground().setAlpha(150);
		if(mEvents.get(position).getStatus() == 1){
			holder.tvType.setBackground(mContext.getResources().getDrawable(R.drawable.shape_red_bg));
		}else{
			holder.tvType.setBackground(mContext.getResources().getDrawable(R.drawable.shape_blue_gray_bg));
		}
		if(mEvents.get(position).getStatus() == 1){
			holder.tvType.setText("报名进行中");
		}else if(mEvents.get(position).getStatus() == 2){
			holder.tvType.setText("报名未开始");
		}else if(mEvents.get(position).getStatus() == 3){
			holder.tvType.setText("报名已截止");
		}else if(mEvents.get(position).getStatus() == 4){
			holder.tvType.setText("赛事已结束");
		}else if (mEvents.get(position).getStatus() == 5){
			holder.tvType.setText("赛事进行中");
		}
		return v;
	}

	// /**
	// * 填充数据
	// *
	// * @param mHolder
	// */
	// private void initViewContent(Holder mHolder) {
	// if (mHolder.getIndex() != -1) {
	// EventInfo eventInfo = mEvents.get(mHolder.getIndex());
	// mHolder.getEveDeadline().setText(eventInfo.getEveInTime());
	// mHolder.getEveStartTime().setText(eventInfo.getEveStartTime());
	// mHolder.getEveTitle().setText(eventInfo.getEveTitle());
	// mHolder.getEvePic().setBackgroundResource(R.drawable.home_logo);
	// }
	// }

	private class ViewHolder {
		ImageView ivEvent;
		TextView tvEndTime;
		TextView tvType;
		TextView tvTitle;
		TextView tvStartTime;
	}

}
