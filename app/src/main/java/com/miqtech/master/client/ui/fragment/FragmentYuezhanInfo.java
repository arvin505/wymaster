package com.miqtech.master.client.ui.fragment;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.entity.YueZhan;
import com.miqtech.master.client.ui.InternetBarActivityV2;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.YueZhanDetailsActivity;
import com.miqtech.master.client.ui.YueZhanHasApplyActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.view.StickyContainer;
import com.miqtech.master.client.view.StickyScrollView;
import com.miqtech.master.client.view.StickyScrollView.OnScrollChangedListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

public class FragmentYuezhanInfo extends BaseFragment implements OnClickListener, YueZhanDetailsActivity.ChangeUIListener {
	private View mainView;// 页面布局
	private Context context;
	private StickyContainer mCarousel;
	private StickyScrollView mScrollView;
	private TextView tvDate, tvGameStatus, tvAddress, tvPeopleNum, tvIntro, tvContactWay;
	private RelativeLayout rlPeopleNum;

	private LinearLayout ll_location;

	// private UpdateYueZhanListener listener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (mainView == null) {
			context = getActivity();
			mainView = LinearLayout.inflate(context, R.layout.fragment_yuezhaninfo, null);
			findView();
			YueZhanDetailsActivity activity = (YueZhanDetailsActivity) context;
			activity.setFragmentInfo(this);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mScrollView = (StickyScrollView) mainView.findViewById(R.id.carousel_scrollview);
		ViewGroup parent = (ViewGroup) mainView.getParent();
		if (parent != null) {
			parent.removeAllViewsInLayout();
		}
		mScrollView.setOnScrollChangedListener(new OnScrollChangedListener() {

			@Override
			public void onScrollIdle() {
			}

			@Override
			public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
				if (mCarousel == null || mCarousel.isTabCarouselIsAnimating()) {
					return;
				}

				int height = mCarousel.getAllowedVerticalScrollLength();
				final float amtToScroll = Math.max(-t, -mCarousel.getAllowedVerticalScrollLength());
				mCarousel.moveToYCoordinate(0, amtToScroll);
			}
		});

		// updateView(yueZhan);
		return mainView;
	}

	@Override
	public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return null;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updateView(YueZhanDetailsActivity.yueZhan);
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCarousel = (StickyContainer) activity.findViewById(R.id.carousel_header);
	}

	private void findView() {
		tvDate = (TextView) mainView.findViewById(R.id.tvDate);
		tvGameStatus = (TextView) mainView.findViewById(R.id.tvGameStatus);
		tvAddress = (TextView) mainView.findViewById(R.id.tvNetBarAddress);
		tvPeopleNum = (TextView) mainView.findViewById(R.id.tvPeopleNum);
		tvIntro = (TextView) mainView.findViewById(R.id.tvIntro);
		tvContactWay = (TextView) mainView.findViewById(R.id.tvContactWay);
		rlPeopleNum = (RelativeLayout)mainView.findViewById(R.id.rlPeopleNum);
		ll_location = (LinearLayout) mainView.findViewById(R.id.ll_location);

		tvAddress.setOnClickListener(this);
		rlPeopleNum.setOnClickListener(this);
		tvContactWay.setTextIsSelectable(true);
	}

	public void updateView(YueZhan yz) {
		if (yz != null) {
			String dateStr = TimeFormatUtil.formatNoTime(yz.getBegin_time());
			tvDate.setText(dateStr);
			if (yz.getWay() == 1) {
				tvGameStatus.setText("线上");
	//			tvAddress.setOnClickListener(this);
				ll_location.setVisibility(View.GONE);
			} else if (yz.getWay() == 2) {
				tvGameStatus.setText("线下");
				tvAddress.setOnClickListener(this);
				tvAddress.setText(yz.getAddress());
				ll_location.setVisibility(View.VISIBLE);
			}
			tvPeopleNum.setText(yz.getApply_count() + "/" + yz.getPeople_num());
			tvIntro.setText(yz.getSpoils());
			String strRemark = yz.getRemark();
			if (strRemark != null) {
				String[] strs = strRemark.split(" ");
				if (strs.length > 1) {
					String temp = "";
					for (int i = 0; i < strs.length; i++) {
						if(i == strs.length-1){
							temp += strs[i];
						}else{
							temp += strs[i] + "\n";
						}
					}
					tvContactWay.setText(temp);
				} else {
					tvContactWay.setText(strRemark);
				}
			}

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rlPeopleNum:
			User user = WangYuApplication.getUser(context);
			Intent intent = new Intent();
			if(user == null){
				showToast("请登录后查看");
				intent.setClass(context, LoginActivity.class);
				startActivity(intent);
			}else{
				intent.setClass(context, YueZhanHasApplyActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.tvNetBarAddress:
			if(!TextUtils.isEmpty(tvAddress.getText().toString())){
				Intent netbarIntent = new Intent();
				netbarIntent.setClass(context, InternetBarActivityV2.class);
				netbarIntent.putExtra("netbarId", YueZhanDetailsActivity.yueZhan.getNetbar_id()+"");
				startActivity(netbarIntent);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void changeUI() {
		// TODO Auto-generated method stub
		updateView(YueZhanDetailsActivity.yueZhan);
	}

	@Override
	public void onSuccess(JSONObject object, String method) {

	}

	@Override
	public void onError(String errMsg, String method) {

	}
}
