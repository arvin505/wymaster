package com.miqtech.master.client.adapter;

import java.util.List;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.ChooseAttentionActivity;
import com.miqtech.master.client.ui.InviteFriendsActivity;
import com.miqtech.master.client.utils.AsyncImage;

import android.content.Context;
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
import android.widget.TextView;

public class ChooseAttentionAdapter extends BaseAdapter implements Filterable {
	private Context context;
	private List<User> fans;
	private CheckedUserListener listener;
	private int maxInviteMemberSize;

	public ChooseAttentionAdapter(Context context, List<User> fans, CheckedUserListener listener,int maxInviteMemberSize) {
		this.context = context;
		this.fans = fans;
		this.listener = listener;
		this.maxInviteMemberSize = maxInviteMemberSize;
	}

	@Override
	public int getCount() {
		return fans.size();
	}

	@Override
	public Object getItem(int position) {
		return fans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		ViewHolder holder;
		if (v == null) {
			v = View.inflate(context, R.layout.layout_chooseattention_item, null);
			holder = new ViewHolder();
			holder.ivHeader = (ImageView) v.findViewById(R.id.ivHeader);
			holder.tvUserName = (TextView) v.findViewById(R.id.tvUserName);
			holder.cbChoose = (CheckBox) v.findViewById(R.id.cbChoose);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		if(fans.get(position).getIsChecked() == 1){
			holder.cbChoose.setChecked(true);
		}else if(fans.get(position).getIsChecked() == 0){
			holder.cbChoose.setChecked(false);
		}
		AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + fans.get(position).getIcon(),
				holder.ivHeader);
//		MyCheckBoxCheckedChangeListener checkListener = new MyCheckBoxCheckedChangeListener(position);
		holder.tvUserName.setText(fans.get(position).getNickname());
		// holder.cbChoose.setOnCheckedChangeListener(checkListener);
		holder.cbChoose.setOnCheckedChangeListener(new MyCheckedChangeListener(holder.cbChoose, context));
		holder.cbChoose.setOnClickListener(new MyCheckedClickListener(position));
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
				((ChooseAttentionActivity) context).showToast("最多只能加" + maxInviteMemberSize + "人");
			}
		}

	}

	private class ViewHolder {
		ImageView ivHeader;
		TextView tvUserName;
		CheckBox cbChoose;
	}

	@Override
	public Filter getFilter() {
		return ((ChooseAttentionActivity) context).new ListFilter();
	}


	private class MyCheckedClickListener implements OnClickListener {
		int position;

		MyCheckedClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cbChoose:
				if (((CheckBox) v).isChecked()) {
//					for (int i = 0; i < InviteFriendsActivity.hasCommonMembers.size(); i++) {
//						Object member = InviteFriendsActivity.hasCommonMembers.get(i);
//						if (member instanceof User) {
//							if (fans.get(position).getId().equals(((User) member).getId())) {
//								ToastUtil.showToast("请不要重复选择同一个联系人", context);
//								((CheckBox) v).setChecked(false);
//								return;
//							}
//						}
//					}
					fans.get(position).setIsChecked(1);
					listener.selectedUser(fans.get(position));
				} else {
					fans.get(position).setIsChecked(0);
					listener.cancelSelectedUser(fans.get(position));
				}

				break;

			default:
				break;
			}
		}

	}

	public interface CheckedUserListener {
		void selectedUser(User user);

		void cancelSelectedUser(User user);
	}

}
