package com.miqtech.master.client.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.Utils;

public class SearchEditTextAdapter extends BaseAdapter implements Filterable {

	private List<String> items = new ArrayList<String>();
	private ArrayList<String> siteItems;
	private ArrayList<String> newValues;
	private LayoutInflater inflater;
	private Context mContext;

	public SearchEditTextAdapter(Context context) {
		super();
		this.mContext = context;
		siteItems = new ArrayList<String>();
		inflater = LayoutInflater.from(context);
		initData(searchType);
	}

	@Override
	public int getCount() {
		return siteItems.size();
	}

	@Override
	public String getItem(int position) {
		return siteItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {			
			convertView =  (View) inflater.inflate(R.layout.search_item, null);
			holder = new ViewHolder();
			holder.item =(TextView) convertView.findViewById(R.id.content_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(getCount() > position){
			holder.item.setText(siteItems.get(position).toString());
			holder.item.setPadding(Utils.dip2px(mContext, 16), 0, 0, 0);
		}
		
		return convertView;
	}


	int searchType;

	public void initData(int type){
		items = PreferencesUtil.readHistory(type);
		siteItems.clear();
		siteItems.addAll(items);
		searchType = type;
	}

	class ViewHolder {
		TextView item;
	}

	@Override
	public Filter getFilter() {
		 initData(searchType);
		return siteFilter;
	}

	Filter siteFilter = new Filter() {

		@Override
		public CharSequence convertResultToString(Object resultValue) {
			String str = resultValue.toString();
			return str;
		}

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults filterResults = new FilterResults();
			newValues = new ArrayList<String>();
			String filterText = "";
			if (constraint != null) {
				try {
					for (int i = 0; i < items.size(); i++) {
						filterText =  items.get(i);
						if(!filterText.equals("")&&filterText.contains(constraint))
							newValues.add(filterText);
					}
				} catch (Exception e) {
				}
				filterResults.values = newValues;
				filterResults.count = newValues.size();
				
			}
			return filterResults;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			if (results != null && results.count > 0) {
				siteItems = (ArrayList<String>) results.values;
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	};
}
