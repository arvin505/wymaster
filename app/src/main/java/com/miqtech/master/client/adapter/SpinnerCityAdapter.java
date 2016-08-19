package com.miqtech.master.client.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.City;
import com.miqtech.master.client.ui.MatchCorpsActivity;

@SuppressLint("ResourceAsColor")
public class SpinnerCityAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private Context context;
	private List<City> citys;

	private Spinner spinner;
	private MatchCorpsActivity.SpinnerInterface onItemAction;

	public SpinnerCityAdapter(Context context, Spinner spinner, List<City> objects, MatchCorpsActivity.SpinnerInterface onItemAction) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.citys = objects;
		this.spinner = spinner;
		this.onItemAction = onItemAction;
	}

	@Override
	public View getDropDownView(final int position, View convertView, ViewGroup parent) {
		SpinnerHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.individual_match_item, null);
			holder = new SpinnerHolder();
			holder.label = (TextView) convertView.findViewById(R.id.tv_netbar_name);
			holder.check = (ImageView) convertView.findViewById(R.id.iv_match_select);
			holder.parent = convertView;
			convertView.setTag(holder);
		}
		holder = (SpinnerHolder) convertView.getTag();
		initData(holder, position);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onItemAction.onItemSelected(true, position);
			}
		});
		return convertView;
	}

	private void initData(SpinnerHolder holder, int position) {
		holder.label.setText(citys.get(position).getName());
		if (spinner.getSelectedItemPosition() == position) {
			holder.check.setVisibility(View.VISIBLE);
		} else {
			holder.check.setVisibility(View.GONE);
		}
	}

	class SpinnerHolder {
		private TextView label;
		private ImageView check;
		View parent;
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
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SpinnerHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.spinner_metch_item, null);
			holder = new SpinnerHolder();
			holder.label = (TextView) convertView.findViewById(R.id.tv_netbar_name);
			holder.check = (ImageView) convertView.findViewById(R.id.iv_match_select);
			holder.parent = convertView;
			convertView.setTag(holder);
		}
		holder = (SpinnerHolder) convertView.getTag();
		initData(holder, position);
		return convertView;
	}
}
