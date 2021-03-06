package com.miqtech.master.client.adapter;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.City;
import com.miqtech.master.client.entity.ContactMember;
import com.miqtech.master.client.listviewfilter.IPinnedHeader;
import com.miqtech.master.client.ui.LocationCityActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.view.PinnedHeaderListView;

public class ContactPinnedHeaderAdapter extends BaseAdapter implements OnScrollListener, IPinnedHeader, Filterable {
	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SECTION = 1;
	private static final int TYPE_MAX_COUNT = TYPE_SECTION + 1;

	LayoutInflater mLayoutInflater;
	int mCurrentSectionPosition = 0, mNextSectionPostion = 0;

	// array list to store section positions
	List<Integer> mListSectionPos;

	// array list to store list view data
	List<Object> mListItems;

	// context object
	Context mContext;

	public ContactPinnedHeaderAdapter(Context context, List<Object> listItems, List<Integer> listSectionPos) {
		this.mContext = context;
		this.mListItems = listItems;
		this.mListSectionPos = listSectionPos;

		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mListItems.size();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return !mListSectionPos.contains(position);
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		return mListSectionPos.contains(position) ? TYPE_SECTION : TYPE_ITEM;
	}

	@Override
	public Object getItem(int position) {
		return mListItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mListItems.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			int type = getItemViewType(position);

			switch (type) {
			case TYPE_ITEM:
				convertView = mLayoutInflater.inflate(R.layout.layout_common_member_item, null);
				// holder.textView = (TextView)
				// convertView.findViewById(R.id.tvContactName);
				// holder.ivContactImg =
				// (ImageView)convertView.findViewById(R.id.ivContactImg);
				// holder.cbContactChecked =
				// (CheckBox)convertView.findViewById(R.id.cbContactChecked);
				break;
			case TYPE_SECTION:
				convertView = mLayoutInflater.inflate(R.layout.section_row_view, null);
				// holder.textView = (TextView)
				// convertView.findViewById(R.id.row_title);
				break;
			}
			// convertView.setTag(holder);
		}
		// else {
		// holder = (ViewHolder) convertView.getTag();
		// }
		if (mListItems.get(position) instanceof ContactMember) {
			((TextView) convertView.findViewById(R.id.tvContactName))
					.setText(((ContactMember) mListItems.get(position)).getContact_name());
			AsyncImage.loadAvatar(mContext, R.drawable.default_head,
					(ImageView) convertView.findViewById(R.id.ivContactImg));
		} else {
			((TextView) convertView.findViewById(R.id.row_title)).setText((String) mListItems.get(position));
		}

		return convertView;
	}

	@Override
	public int getPinnedHeaderState(int position) {
		// hide pinned header when items count is zero OR position is less than
		// zero OR
		// there is already a header in list view
		if (getCount() == 0 || position < 0 || mListSectionPos.indexOf(position) != -1) {
			return PINNED_HEADER_GONE;
		}

		// the header should get pushed up if the top item shown
		// is the last item in a section for a particular letter.
		mCurrentSectionPosition = getCurrentSectionPosition(position);
		mNextSectionPostion = getNextSectionPosition(mCurrentSectionPosition);
		if (mNextSectionPostion != -1 && position == mNextSectionPostion - 1) {
			return PINNED_HEADER_PUSHED_UP;
		}

		return PINNED_HEADER_VISIBLE;
	}

	public int getCurrentSectionPosition(int position) {
		String listChar = "";
		if (mListItems.get(position) instanceof City) {
			listChar = ((City) mListItems.get(position)).getPinyin().substring(0, 1).toUpperCase(Locale.getDefault());

		} else {
			listChar = mListItems.get(position).toString().substring(0, 1).toUpperCase(Locale.getDefault());
		}
		return mListItems.indexOf(listChar);
	}

	public int getNextSectionPosition(int currentSectionPosition) {
		int index = mListSectionPos.indexOf(currentSectionPosition);
		if ((index + 1) < mListSectionPos.size()) {
			return mListSectionPos.get(index + 1);
		}
		return mListSectionPos.get(index);
	}

	@Override
	public void configurePinnedHeader(View v, int position) {
		// set text in pinned header
		TextView header = (TextView) v;
		mCurrentSectionPosition = getCurrentSectionPosition(position);
		if (mListItems.get(mCurrentSectionPosition) instanceof City) {
			header.setText(((City) mListItems.get(mCurrentSectionPosition)).getName());
		} else {
			header.setText((String) mListItems.get(mCurrentSectionPosition));
		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (view instanceof PinnedHeaderListView) {
			((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
	}

	@Override
	public Filter getFilter() {
		return ((LocationCityActivity) mContext).new ListFilter();
	}

	public static class ViewHolder {
		public TextView textView;
		public ImageView ivContactImg;
		public CheckBox cbContactChecked;
	}

}
