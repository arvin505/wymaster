package com.miqtech.master.client.adapter;

import java.util.List;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.InviteFriendsActivity;
import com.miqtech.master.client.utils.AsyncImage;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class InviteCommonAdapter extends BaseAdapter {

	private Context context;
	private List<User> commonContacts;
	private CommonMemberListener listener;
	private int maxInviteMemberSize;

	public InviteCommonAdapter(Context context, List<User> commonContacts, CommonMemberListener listener,int maxInviteMemberSize) {
		this.context = context;
		this.commonContacts = commonContacts;
		this.listener = listener;
		this.maxInviteMemberSize = maxInviteMemberSize;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return commonContacts.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return commonContacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View v, ViewGroup parent) {
		ViewHolder holder = null;
		if (v == null) {
			holder = new ViewHolder();
			v = View.inflate(context, R.layout.layout_common_member_item, null);
			holder.ivContactImg = (ImageView) v.findViewById(R.id.ivContactHeader);
			holder.tvContactName = (TextView) v.findViewById(R.id.tvContactName);
			holder.cbContactChecked = (CheckBox) v.findViewById(R.id.cbContactChecked);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		User commonUser = commonContacts.get(position);
		AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + commonUser.getIcon(), holder.ivContactImg);
		holder.tvContactName.setText(commonUser.getNickname());
		if (commonContacts.get(position).getIsChecked() == 1) {
			holder.cbContactChecked.setChecked(true);
		} else if (commonContacts.get(position).getIsChecked() == 0) {
			holder.cbContactChecked.setChecked(false);
		}
		holder.cbContactChecked.setOnCheckedChangeListener(new MyCheckedChangeListener(holder.cbContactChecked,context));
		holder.cbContactChecked.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {
					commonContacts.get(position).setIsChecked(1);
					listener.selectMember(position);
				} else {
					commonContacts.get(position).setIsChecked(0);
					listener.cancelSelectMember(position);
				}
			}
		});
		return v;
	}

	private class MyCheckedChangeListener implements OnCheckedChangeListener {

		CheckBox cbContactChecked;

		Context context;

		private MyCheckedChangeListener(CheckBox cbContactChecked, Context context) {
			this.cbContactChecked = cbContactChecked;
			this.context = context;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if (InviteFriendsActivity.hasCommonMembers.size() >= maxInviteMemberSize) {
				cbContactChecked.setChecked(false);
				((InviteFriendsActivity) context).showToast("最多只能加" + maxInviteMemberSize + "人");
			}
		}

	}

	private class ViewHolder {
		ImageView ivContactImg;
		TextView tvContactName;
		CheckBox cbContactChecked;
	}

	public interface CommonMemberListener {
		public void selectMember(int position);

		public void cancelSelectMember(int position);
	}
}
