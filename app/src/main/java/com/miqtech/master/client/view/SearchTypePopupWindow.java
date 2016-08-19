package com.miqtech.master.client.view;

import java.util.ArrayList;
import java.util.List;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.NetBarSearchType;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchTypePopupWindow extends PopupWindow {
	private Context context;
	private int[] location;
	private List<NetBarSearchType> types = new ArrayList<NetBarSearchType>();
	private NetBarSearchType currentType;
	public SearchTypeListAdapter adapter;

	public SearchTypePopupWindow(Context context, int[] location, int viewHeight, int titleHeight,
			OnItemClickListener listener, List<NetBarSearchType> types) {
		this.context = context;
		this.location = location;
		this.types = types;
		currentType = types.get(0);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.layout_netbarsearch_view, null);
		ListView lvSearchType = (ListView) layout.findViewById(R.id.lvSearchType);
		adapter = new SearchTypeListAdapter();
		lvSearchType.setAdapter(adapter);
		lvSearchType.setOnItemClickListener(listener);
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		float screenHeight = WangYuApplication.HEIGHT;

		this.setHeight((int) (screenHeight - getStatusBarHeight() - viewHeight - titleHeight));
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setContentView(layout);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.setBackgroundDrawable(new BitmapDrawable());

		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
		layout.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				dismiss();
				return true;
			}
		});
	}

	/**
	 * 获取状态栏高度
	 * 
	 * @return
	 */
	public int getStatusBarHeight() {
		Class<?> c = null;
		Object obj = null;
		java.lang.reflect.Field field = null;
		int x = 0;
		int statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
			return statusBarHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}

	public class SearchTypeListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return types.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return types.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		public void setTypeSelected(int position) {
			NetBarSearchType type = types.get(position);
			currentType = type;
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View v, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (v == null) {
				holder = new ViewHolder();
				v = View.inflate(context, R.layout.layout_area_item, null);
				holder.tvSearchType = (TextView) v.findViewById(R.id.tvArea);
				holder.tvChecked = (TextView) v.findViewById(R.id.tvChecked);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}
			if (currentType != null) {
				if (types.get(position).getSearchType().equals(currentType.getSearchType())) {
					holder.tvChecked.setVisibility(View.VISIBLE);
				} else {
					holder.tvChecked.setVisibility(View.GONE);
				}

			}
			holder.tvSearchType.setText(types.get(position).getSearchType());
			return v;
		}

		private class ViewHolder {
			TextView tvSearchType;
			TextView tvChecked;
		}

	}

}
