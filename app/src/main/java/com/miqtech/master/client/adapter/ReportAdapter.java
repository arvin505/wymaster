package com.miqtech.master.client.adapter;

import com.miqtech.master.client.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

public class ReportAdapter extends BaseAdapter {

	private Context context;
	private int selectID;
	private OnMyCheckChangedListener mCheckChange;
	private String[] itemName;

	public ReportAdapter(Context context, String[] itemName) {
		this.context = context;
		this.itemName = itemName;
	}

	public void setSelectID(int selectID) {
		this.selectID = selectID;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return itemName.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return getItem(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder h = null;
		if (v == null) {
			h = new Holder();
			v = LayoutInflater.from(context).inflate(R.layout.layout_comment_report_item, null);
			h.rb = (RadioButton) v.findViewById(R.id.radio_button);
			v.setTag(h);
		} else {
			h = (Holder) v.getTag();
		}
		h.rb.setText(itemName[position]);

		if (selectID == position) {
			h.rb.setChecked(true);
			h.rb.setTextColor(context.getResources().getColor(R.color.white));
		} else {
			h.rb.setChecked(false);
			h.rb.setTextColor(context.getResources().getColor(R.color.colorActionBarSelected));
		}

		h.rb.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				selectID = position;
				if (mCheckChange != null) {
					mCheckChange.setSelectID(selectID);
				}
			}
		});

		return v;
	}

	public void setOncheckChanged(OnMyCheckChangedListener checkChangedListener) {
		mCheckChange = checkChangedListener;
	}

	public interface OnMyCheckChangedListener {
		void setSelectID(int selectID);
	}

	class Holder {
		RadioButton rb;
	}

}
