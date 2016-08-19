package com.miqtech.master.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.City;

import java.util.List;

public class CityAdapter extends BaseAdapter {

	private List<City> citys;
	private Context mContext;
	private LayoutInflater inflater;

	public CityAdapter(List<City> citys, Context mContext) {
		super();
		this.citys = citys;
		this.mContext = mContext;
		this.inflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return citys.size();
	}

	@Override
	public City getItem(int position) {
		// TODO Auto-generated method stub
		return citys.get(position);
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.layout_city_item, null);
		}
		TextView tv_city = (TextView) convertView;
		tv_city.setText(citys.get(position).getName());
		return convertView;
	}
}
