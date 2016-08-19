package com.miqtech.master.client.adapter;

import java.util.ArrayList;
import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.ContactMember;
import com.miqtech.master.client.stickylistheaders.StickyListHeadersAdapter;
import com.miqtech.master.client.ui.ContactsActivity;
import com.miqtech.master.client.ui.InviteFriendsActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ContactListAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer, Filterable {

	private final Context mContext;
	// private String[] mCountries;
	// 首字母集合索引
	private int[] mSectionIndices;
	// 首字母数组
	private Character[] mSectionLetters;

	private LayoutInflater mInflater;

	public ArrayList<ContactMember> members;

	public int selectedCount = 0;
	SelectedMembersListener listener;

	private int maxInviteMemberSize;

	public ContactListAdapter(Context context, ArrayList<ContactMember> members, SelectedMembersListener listener,
			int maxInviteMemberSize) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		this.members = members;
		if (members.size() == 0) {
			return;
		}
		mSectionIndices = getSectionIndices();
		mSectionLetters = getSectionLetters();
		this.listener = listener;
		this.maxInviteMemberSize = maxInviteMemberSize;
	}

	// 增加首字母
	private int[] getSectionIndices() {
		ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
		char lastFirstChar = members.get(0).getSortKey().charAt(0);
		sectionIndices.add(0);
		for (int i = 1; i < members.size(); i++) {
			// 如果字符串第一个字母不等于LASTFIRSTCHAR;
			if (members.get(i).getSortKey().charAt(0) != lastFirstChar) {
				lastFirstChar = members.get(i).getSortKey().charAt(0);
				// 如果不等于首字母 放到字母集合里；
				sectionIndices.add(i);
			}
		}
		int[] sections = new int[sectionIndices.size()];
		for (int i = 0; i < sectionIndices.size(); i++) {
			sections[i] = sectionIndices.get(i);
		}
		return sections;
	}

	private Character[] getSectionLetters() {
		Character[] letters = new Character[mSectionIndices.length];
		for (int i = 0; i < mSectionIndices.length; i++) {
			letters[i] = members.get(mSectionIndices[i]).getSortKey().charAt(0);
		}
		return letters;
	}

	@Override
	public int getCount() {
		return members.size();
	}

	@Override
	public Object getItem(int position) {
		return members.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		// final int index = position;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.layout_contact_item, parent, false);
			holder.tvContactName = (TextView) convertView.findViewById(R.id.tvContactName);
			holder.ivContactImg = (ImageView) convertView.findViewById(R.id.ivContactImg);
			holder.cbContactChecked = (CheckBox) convertView.findViewById(R.id.cbContactChecked);
			holder.rlContact = (RelativeLayout) convertView.findViewById(R.id.rlContact);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (members.get(position).getIsChecked() == 1) {
			holder.cbContactChecked.setChecked(true);
		} else if (members.get(position).getIsChecked() == 0) {
			holder.cbContactChecked.setChecked(false);
		}
		final int p = position;
		holder.tvContactName.setText(members.get(position).getContact_name());
		holder.cbContactChecked.setOnCheckedChangeListener(new MyCheckedChangeListener(holder.cbContactChecked,mContext));
		holder.cbContactChecked.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox cb = (CheckBox) v;
				if (cb.isChecked()) {
					members.get(p).setIsChecked(1);
					listener.selectedMember(members.get(p));
				} else {
					members.get(p).setIsChecked(0);
					listener.cancelSelectedMember(members.get(p));
				}
			}
		});
		return convertView;
	}
	
	private class MyCheckedChangeListener implements OnCheckedChangeListener{
		
		CheckBox cbContactChecked;
		
		Context context ;
		
		private MyCheckedChangeListener(CheckBox cbContactChecked,Context context){
			this.cbContactChecked = cbContactChecked;
			this.context = context;
		}
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if (InviteFriendsActivity.hasCommonMembers.size() >= maxInviteMemberSize) {
				cbContactChecked.setChecked(false);
				((ContactsActivity)context).showToast("最多只能加"+maxInviteMemberSize+"人");
			}
		}
		
	}

	public interface SelectedMembersListener {
		// void selectedMember(ArrayList<ContactMember> members,int position);
		void cancelSelectedMember(ContactMember member);

		void selectedMember(ContactMember member);
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder;

		if (convertView == null) {
			holder = new HeaderViewHolder();
			convertView = mInflater.inflate(R.layout.layout_contact_header_item, parent, false);
			holder.tvFirstLetter = (TextView) convertView.findViewById(R.id.tvFirstLetter);
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder) convertView.getTag();
		}

		CharSequence headerChar = members.get(position).getSortKey().subSequence(0, 1);
		holder.tvFirstLetter.setText(headerChar);

		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		return members.get(position).getSortKey().subSequence(0, 1).charAt(0);
	}

	@Override
	public int getPositionForSection(int section) {
		if (section >= mSectionIndices.length) {
			section = mSectionIndices.length - 1;
		} else if (section < 0) {
			section = 0;
		}
		return mSectionIndices[section];
	}

	@Override
	public int getSectionForPosition(int position) {
		for (int i = 0; i < mSectionIndices.length; i++) {
			if (position < mSectionIndices[i]) {
				return i - 1;
			}
		}
		return mSectionIndices.length - 1;
	}

	@Override
	public Object[] getSections() {
		return mSectionLetters;
	}

	class HeaderViewHolder {
		TextView tvFirstLetter;
	}

	class ViewHolder {
		RelativeLayout rlContact;
		TextView tvContactName;
		ImageView ivContactImg;
		CheckBox cbContactChecked;
	}

	@Override
	public Filter getFilter() {
		return ((ContactsActivity) mContext).new ListFilter();
	}

}
