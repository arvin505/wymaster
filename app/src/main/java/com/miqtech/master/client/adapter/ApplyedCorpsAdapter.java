package com.miqtech.master.client.adapter;

import java.util.List;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.Corps;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.MatchCorpsActivity;
import com.miqtech.master.client.utils.ToastUtil;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ApplyedCorpsAdapter extends BaseAdapter implements OnClickListener{

	private Context context;
	private List<Corps> corpsList;
	private LayoutInflater inflater;
	private MatchCorpsActivity.CorpsAction itemAction;
	
	

	public ApplyedCorpsAdapter(Context mContext, List<Corps> corpsList,
			MatchCorpsActivity.CorpsAction itemAction) {
		this.context = mContext;
		this.corpsList = corpsList;
		this.inflater = LayoutInflater.from(context);
		this.itemAction = itemAction;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return corpsList.size();
	}

	@Override
	public Corps getItem(int position) {
		// TODO Auto-generated method stub
		return corpsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CorpsHolder holder;
		Corps corps = corpsList.get(position);
		if(convertView==null){
			convertView = inflater.inflate(R.layout.match_corps_item, null);
			holder =new CorpsHolder();
			holder.corpsName = (TextView) convertView.findViewById(R.id.teamName);
			holder.joinCorps = (Button) convertView.findViewById(R.id.joinCorps);
			holder.total = (TextView) convertView.findViewById(R.id.total);
			holder.header = (TextView) convertView.findViewById(R.id.header);
			convertView.setTag(holder);
		}else{
			holder = (CorpsHolder) convertView.getTag();
		}
		holder.corpsName.setText(corps.getTeam_name());
		holder.total.setText(corps.getNum()+"/"+corps.getTotal_num());
		holder.header.setText(corps.getHeader());
		
		if(corps.getNum()>=corps.getTotal_num()){
			holder.joinCorps.setText("人数已满");
			holder.joinCorps.setEnabled(false);
			holder.joinCorps.setBackgroundResource(R.drawable.join_corps_off);
		}else if(corps.getIs_join()>0){
			holder.joinCorps.setText("已加入");
			holder.joinCorps.setEnabled(false);
			holder.joinCorps.setBackgroundResource(R.drawable.join_corps_off);
		}else{
			holder.joinCorps.setText("加入战队");
			holder.joinCorps.setEnabled(true);
			holder.joinCorps.setBackgroundResource(R.drawable.corps_edit);
			//1-报名进行中；2-报名未开始；3-报名已截止；4-赛事已结束
		}
		if(((MatchCorpsActivity)context).status==4){
			holder.joinCorps.setBackgroundResource(R.color.gray_bg);
			holder.joinCorps.setEnabled(false);
		}
		if(((MatchCorpsActivity)context).status==3){
			holder.joinCorps.setBackgroundResource(R.color.gray_bg);
			holder.joinCorps.setEnabled(false);
		}
		holder.joinCorps.setTag(position);
		holder.joinCorps.setOnClickListener(this);
		return convertView;
	}
	class CorpsHolder{
		public TextView header;
		public TextView total;
		public Button joinCorps;
		public TextView corpsName;
	}
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.joinCorps) {
			if(WangYuApplication.getUser(context)!=null){
				itemAction.onJoinCorps(corpsList.get((Integer)v.getTag()));
			}else{
				ToastUtil.showToast("请登录",context);
				Intent intent =new Intent(context,LoginActivity.class);
				context.startActivity(intent);
			}
			
		}
	}

}
