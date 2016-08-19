package com.miqtech.master.client.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.miqtech.master.client.R;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.InternetBarInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;

@SuppressLint("ResourceAsColor")
public class NetBarAdapter extends BaseAdapter {

	private Context context;
	private List<InternetBarInfo> bars = new ArrayList<InternetBarInfo>();
	private int type = 1;

	public NetBarAdapter(Context context, List<InternetBarInfo> bars) {
		this.context = context;
		this.bars = bars;
	}
	public NetBarAdapter(Context context, List<InternetBarInfo> bars,int type) {
		this.context = context;
		this.bars = bars;
		this.type = type;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return bars.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return bars.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;

		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.nearbybaritem, parent, false);
			holder = new ViewHolder();
			holder.mImgBarHead = (ImageView) view.findViewById(R.id.img_netbar_icon);
			holder.mTvBarName = (TextView) view.findViewById(R.id.tv_netbar_name);
			holder.mTvBarAddress = (TextView) view.findViewById(R.id.tv_netbar_loction);
			holder.mTvBarHourPrice = (TextView) view.findViewById(R.id.tv_netbar_hourprice);
			holder.mTvBarDistance = (TextView) view.findViewById(R.id.tv_netbar_distance);
			//holder.mIvSchedule = (TextView) view.findViewById(R.id.iv_schedule);
			//holder.mIvRecommend = (TextView) view.findViewById(R.id.img_bar_j);//推荐
//			holder.mImgHot = (ImageView)view.findViewById(R.id.hot_netbar);
			//holder.mDiscount = (TextView)view.findViewById(R.id.discount);
			holder.mImgBarhot = (ImageView)view.findViewById(R.id.img_bar_hot);//热门
			holder.mImgBarJ = (ImageView)view.findViewById(R.id.img_bar_j);//推荐
			holder.mImgBarZ = (ImageView)view.findViewById(R.id.img_bar_z);//支
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		if(bars.get(position).getIs_order()==1){
			//holder.mIvSchedule.setVisibility(View.VISIBLE);
			holder.mImgBarZ.setVisibility(View.VISIBLE);
		}else{
			//holder.mIvSchedule.setVisibility(View.GONE);
			holder.mImgBarZ.setVisibility(View.GONE);
		}
		
		if (bars.get(position).getIs_recommend()==1) {
			//holder.mIvRecommend.setVisibility(View.VISIBLE);
			holder.mImgBarJ.setVisibility(View.VISIBLE);
		} else {
			//holder.mIvRecommend.setVisibility(View.GONE);
			holder.mImgBarJ.setVisibility(View.GONE);
		}
		
		if(bars.get(position).getIs_hot()==1){
			//holder.mImgHot.setVisibility(View.VISIBLE);
			holder.mImgBarhot.setVisibility(View.VISIBLE);
		}else{
			//holder.mImgHot.setVisibility(View.GONE);
			holder.mImgBarhot.setVisibility(View.GONE);
		}
		if(bars.get(position).getHas_rebate()==1){
			//holder.mDiscount.setVisibility(View.VISIBLE);
			holder.mTvBarHourPrice.setTextColor(context.getResources().getColor(R.color.red_money));
		}else{
			//holder.mDiscount.setVisibility(View.GONE);
			holder.mTvBarHourPrice.setTextColor(context.getResources().getColor(R.color.black));
		}
		String Name = bars.get(position).getNetbar_name();
		if (TextUtils.isEmpty(Name) || Name.equals("null"))
			Name = "暂无网吧名称信息";
		holder.mTvBarAddress.setText(Name);
		holder.mTvBarName.setText(bars.get(position).getNetbar_name());

		String Address = bars.get(position).getAddress();
		if (TextUtils.isEmpty(Address) || Address.equals("null"))
			Address = "暂无地址信息";
		holder.mTvBarAddress.setText(Address);

		String Price = bars.get(position).getPrice_per_hour();
		if (TextUtils.isEmpty(Price) || Price.equals("null"))
			Price = "0";
		holder.mTvBarHourPrice.setText("￥" + Price + "元/小时");

		double distanceloc = getDistance(bars.get(position).getLatitude(), bars.get(position)
				.getLongitude());
		String distance = bars.get(position).getDistance();
		if (TextUtils.isEmpty(distance) || distance.equals("null"))
			distance = "0.0m";
		else
			distance = DisConversion(Double.valueOf(distance));
		holder.mTvBarDistance.setText(distance);

		AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + bars.get(position).getIcon(),
				holder.mImgBarHead);
		if(type == 2){
			holder.mTvBarDistance.setVisibility(View.GONE);
		}
		return view;
	}

	public String DisConversion(double dis) {
		DecimalFormat df = new DecimalFormat("#.#");
		if ((dis * 1000) <= 999)
			return df.format((dis * 1000)) + "m";
		if (dis > 999)
			return 999 + "+km";
		return df.format(dis) + "km";
	}

	private final double EARTH_RADIUS = 6378137.0;

	// 返回单位是米
	public double getDistance(double latitude1, double longitude1) {
		double Lat1 = rad(latitude1);
		double Lat2 = rad(Constant.latitude);
		double a = Lat1 - Lat2;
		double b = rad(longitude1) - rad(Constant.longitude);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(Lat1) * Math.cos(Lat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}


	private class ViewHolder {
		TextView mDiscount;
		ImageView mImgHot;
		ImageView mImgBarHead;
		ImageView mImgBarhot;
		ImageView mImgBarJ;
		ImageView mImgBarZ;
		TextView mTvBarName;
		TextView mTvBarAddress;
		TextView mTvBarHourPrice;
		TextView mTvBarDistance;
		TextView mIvSchedule, mIvRecommend;
	}
}
