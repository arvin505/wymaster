package com.miqtech.master.client.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.Subject;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;

public class SubjectListAdapter extends BaseAdapter {
	private List<Subject> subjects;
	private Context context;
	private String matchTitle;

	public SubjectListAdapter(Context context, List<Subject> subjects) {
		this.context = context;
		this.subjects = subjects;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return subjects.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub

		return subjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (v == null) {
			v = LayoutInflater.from(context).inflate(R.layout.subject_item, parent, false);
			holder = new ViewHolder();
			holder.ivSubjectImage = (ImageView) v.findViewById(R.id.ivSubjectImage);
			holder.tvSubjectTitle = (TextView) v.findViewById(R.id.tvSubjectTitle);
			holder.tvSubjectInfo = (TextView) v.findViewById(R.id.tvSubjectIntro);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		initImgView(position, holder);
		return v;
	}

	private class ViewHolder {
		ImageView ivSubjectImage;
		TextView tvSubjectTitle;
		TextView tvSubjectInfo;
	}

	private void initImgView(int position, ViewHolder holder) {
		if (position != -1) {
			Subject sub = subjects.get(position);
			AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + sub.getIcon(), holder.ivSubjectImage);
			holder.tvSubjectTitle.setText(sub.getTitle());
			holder.tvSubjectInfo.setText(sub.getBrief());
		}
	}
}
