package com.miqtech.master.client.view;

import java.util.List;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.City;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AreaPopupWindow extends PopupWindow {
	private Context context;
	private List<City> areas;
	private int[] location;
	public City currentArea;
	public AreaListAdapter adapter ;

	public AreaPopupWindow(Context context, List<City> areas, int[] location, int viewHeight, int titleHeight,
			OnItemClickListener listener) {

		this.context = context;
		this.areas = areas;
		this.location = location;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.layout_screen_view, null);
		ListView lvArea = (ListView) layout.findViewById(R.id.lvArea);
		adapter = new AreaListAdapter();
		//设置默认城市
		if(areas.size()!=0){
			currentArea = areas.get(0);
		}
		lvArea.setAdapter(adapter);
		lvArea.setOnItemClickListener(listener);
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
		Class<?> c;
		Object obj;
		java.lang.reflect.Field field;
		int x;
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

	public class AreaListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return areas.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return areas.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		public void setAreaSelected(int position) {
			City area = areas.get(position);
			currentArea = area;
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View v, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (v == null) {
				holder = new ViewHolder();
				v = View.inflate(context, R.layout.layout_area_item, null);
				holder.tvArea = (TextView) v.findViewById(R.id.tvArea);
				holder.tvChecked = (TextView) v.findViewById(R.id.tvChecked);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}
			if(currentArea!=null){
				if(areas.get(position).getAreaCode().equals(currentArea.getAreaCode())){
					holder.tvChecked.setVisibility(View.VISIBLE);
				}else{
					holder.tvChecked.setVisibility(View.GONE);
				}
				
			}
			holder.tvArea.setText(areas.get(position).getName());
			return v;
		}

		private class ViewHolder {
			TextView tvArea;
			TextView tvChecked;
		}

	}
}
