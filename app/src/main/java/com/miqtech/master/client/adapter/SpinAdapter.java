package com.miqtech.master.client.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.miqtech.master.client.R;

@SuppressLint("ResourceAsColor")
public class SpinAdapter extends ArrayAdapter<String> {

	private LayoutInflater inflater;
	private Context context;
	private String[] netbarNames;

	private Spinner spinner;

	public Spinner getSpinner() {
		return spinner;
	}

	public void setSpinner(Spinner spinner) {
		this.spinner = spinner;
	}
	
	public SpinAdapter(Context context, int resource, String[] objects) {
		super(context, resource, objects);
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.netbarNames = objects;
		this.spinner = spinner;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		SpinnerHolder holder;
		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.individual_match_item, null);
			holder = new SpinnerHolder();
			holder.label = (TextView) convertView
					.findViewById(R.id.tv_netbar_name);
			holder.check = (ImageView) convertView
					.findViewById(R.id.iv_match_select);
			holder.parent = convertView;
			convertView.setTag(holder);
		}
		holder = (SpinnerHolder) convertView.getTag();
		initData(holder, position);
		return convertView;
	}
	private void initData(SpinnerHolder holder, int position) {
		holder.label.setText(netbarNames[position]);
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
}
