package com.miqtech.master.client.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

import org.json.JSONObject;

public class ContactWayActivity extends BaseActivity implements OnClickListener {

	private EditText edtYY, edtQQ, edtWeChat;

	private TextView tvYY, tvQQ, tvWeChat;
	private Button btnSave;
	private ImageView back;


	private ArrayList<String> contactWays;

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		super.init();
		setContentView(R.layout.activity_contactway);
		initView();
		initData();
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		super.initData();
		contactWays = getIntent().getStringArrayListExtra("contactWays");
		for (int i = 0; i < contactWays.size(); i++) {
			String contactWay = contactWays.get(i);
			String[] contactWayArray = contactWay.split(":");
			if (contactWayArray[0].equals(tvQQ.getText().toString().replace(":", ""))) {
				edtQQ.setText(contactWayArray[1]);
			} else if (contactWayArray[0].equals(tvYY.getText().toString().replace(":", ""))) {
				edtYY.setText(contactWayArray[1]);
			} else if (contactWayArray[0].equals(tvWeChat.getText().toString().replace(":", ""))) {
				edtWeChat.setText(contactWayArray[1]);
			}
		}
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		edtYY = (EditText) findViewById(R.id.edtYY);
		edtQQ = (EditText) findViewById(R.id.edtQQ);
		edtWeChat = (EditText) findViewById(R.id.edtWechat);
		btnSave = (Button) findViewById(R.id.btnSave);
		tvYY = (TextView) findViewById(R.id.tvYY);
		tvQQ = (TextView) findViewById(R.id.tvQQ);
		tvWeChat = (TextView) findViewById(R.id.tvWeChat);
		back = (ImageView) findViewById(R.id.ivBack);
		back.setOnClickListener(this);
		setLeftIncludeTitle("联系方式");
		//setLeftBtnImage(R.drawable.back);
		btnSave.setOnClickListener(this);
//		getLeftBtn().setOnClickListener(this);
	}

	@Override
	public void onSuccess(JSONObject object, String method) {
		// TODO Auto-generated method stub
		super.onSuccess(object,method);
		hideLoading();
	}

	@Override
	public void onError(String method, String errorInfo) {
		// TODO Auto-generated method stub
		super.onError(method, errorInfo);
	}

	private void saveContact() {
		if (TextUtils.isEmpty(edtYY.getText().toString()) && TextUtils.isEmpty(edtQQ.getText().toString())
				&& TextUtils.isEmpty(edtWeChat.getText().toString())) {
			showToast("请至少输入一个");
			return;
		}
		contactWays.clear();
		if (!TextUtils.isEmpty(edtYY.getText().toString())) {
			contactWays.add(tvYY.getText().toString()+edtYY.getText().toString());
		}
		if (!TextUtils.isEmpty(edtQQ.getText().toString())) {
			contactWays.add(tvQQ.getText().toString()+edtQQ.getText().toString());
		}
		if (!TextUtils.isEmpty(edtWeChat.getText().toString())){
			contactWays.add(tvWeChat.getText().toString()+edtWeChat.getText().toString());
		}
		Intent intent = new Intent();
		intent.putStringArrayListExtra("contactWays", contactWays);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnSave:
			saveContact();
			break;
		case R.id.ivBack:
			onBackPressed();
			break;
		default:
			break;
		}
	}
}
