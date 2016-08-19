package com.miqtech.master.client.ui;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.MatchListAdapter;
import com.miqtech.master.client.adapter.SpinAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.Address;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.ABTextUtil;
import com.miqtech.master.client.utils.IDCardUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.view.MyAlertView;
import com.miqtech.master.client.view.MyAlertView.Builder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndividualMatchJoinActivity extends BaseActivity implements
        OnClickListener {
    private ListView lvMatch;
    private MatchListAdapter adapter;
    private List<Address> address = new ArrayList<Address>();
    private IndividualMatchJoinActivity context;
    private int matchId;
    private String netbarId;
    private LayoutParams params;
    private Button btn_individual_join, btn_create_corps;

    private Address currentAddress;
    private Spinner spinner;
    private String[] netbarNames;
    private String[] netbarIds;
    private LayoutInflater inflater;
    private int current;
    private EditText mobileNum;
    private EditText idNum;
    private EditText qqNum;
    private EditText trueName;
    private EditText expertisePos;
    private String round;
    private Button submit;
    private ImageView ivBackUp;
    private Builder builder;
    private MyAlertView dialog;
    private User user;

    public Address getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(Address currentAddress) {
        this.currentAddress = currentAddress;
    }

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.individual_match);
        context = this;
        inflater = LayoutInflater.from(context);
        netbarNames = getIntent().getStringArrayExtra("netbarName");
        netbarIds = getIntent().getStringArrayExtra("netbarId");
        matchId = getIntent().getIntExtra("matchId", 0);
        if (netbarIds.length > 0) {
            netbarId = netbarIds[0];
        }
        round = getIntent().getStringExtra("round");
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        ivBackUp = (ImageView) findViewById(R.id.ivBack);
        ivBackUp.setImageResource(R.drawable.back);
        setLeftIncludeTitle("个人报名");
        spinner = (Spinner) findViewById(R.id.spinner1);
        submit = (Button) findViewById(R.id.btnSubmit);
        SpinAdapter sinAdapter = new SpinAdapter(context,
                R.layout.spinner_item, netbarNames);
        sinAdapter.setSpinner(spinner);
        sinAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(sinAdapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (netbarIds.length > 0) {
                    netbarId = netbarIds[position];
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        submit.setOnClickListener(this);
        ivBackUp.setOnClickListener(this);

        mobileNum = (EditText) findViewById(R.id.mobileNumber);
        idNum = (EditText) findViewById(R.id.idNumber);
        qqNum = (EditText) findViewById(R.id.qqNumber);
        trueName = (EditText) findViewById(R.id.editName);
        expertisePos = (EditText) findViewById(R.id.expertisePosition);
    }

    @Override
    protected void initData() {
        super.initData();
        mobileNum.setText(PreferencesUtil.getUserMobile(context));
        idNum.setText(PreferencesUtil.getUserIDCard(context));
        qqNum.setText(PreferencesUtil.getUserQQ(context));
        trueName.setText(PreferencesUtil.getUserRealName(context));
        expertisePos.setText(PreferencesUtil.getUserLabor(context));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void individualJoinMatch() {

        Map<String, String> params = new HashMap<>();
        user = WangYuApplication.getUser(context);
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
        }
        params.put("activityId", matchId + "");
        if (!TextUtils.isEmpty(netbarId)) {
            params.put("netbarId", netbarId);
        } else {
            showToast("请选择参赛的网吧");
            return;
        }

        params.put("name", trueName.getText().toString()
                .trim()
                + "");
        params.put("telephone", mobileNum.getText()
                .toString().trim()
                + "");
        params.put("idcard", idNum.getText().toString()
                .trim()
                + "");
        params.put("qq", qqNum.getText().toString()
                .trim()
                + "");
        params.put("labor", expertisePos.getText()
                .toString().trim()
                + "");
        params.put("round", round + "");// 场次

        PreferencesUtil.saveUserIDCard(idNum.getText().toString(), context);
        PreferencesUtil.saveUserMobile(mobileNum.getText().toString(), context);
        PreferencesUtil.saveUserRealName(trueName.getText().toString(), context);
        PreferencesUtil.saveUserQQ(qqNum.getText().toString(), context);
        PreferencesUtil.saveUserLabor(expertisePos.getText().toString(), context);
        showLoading();
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INDIVIDUAL_ACTIVITY_DETAIL,
                params, HttpConstant.INDIVIDUAL_ACTIVITY_DETAIL);
    }

    private boolean submitMatch() {
        if (validateInput(trueName) && validateInput(mobileNum)
                && validateInput(idNum) && validateInput(qqNum)
                && validateInput(expertisePos)) {
            return true;
        }
        return false;
    }

    private boolean validateInput(EditText view) {
        boolean isValidate = false;
        switch (view.getId()) {
            case R.id.idNumber:
                if (!TextUtils.isEmpty(view.getText().toString())) {
                    isValidate = IDCardUtil.isIDCard(view.getText().toString().trim());
                    if (!isValidate) {
                        showToast("身份证格式不正确");
                    }
                } else {
                    showToast("身份证不能为空");
                }
                break;
            case R.id.mobileNumber:
                isValidate = ABTextUtil.isMobile(view.getText().toString().trim());
                if (!isValidate) {
                    showToast("手机号格式不对");
                }
                break;
            case R.id.qqNumber:
                isValidate = !ABTextUtil.isEmpty(view.getText().toString().trim());
                if (!isValidate) {
                    showToast("QQ号不能为空");
                }
                break;
            case R.id.editName:
                isValidate = !ABTextUtil.isEmpty(view.getText().toString().trim());
                if (!isValidate) {
                    showToast("姓名不能为空");
                }
                break;
            case R.id.expertisePosition:
                isValidate = !ABTextUtil.isEmpty(view.getText().toString().trim());
                if (!isValidate) {
                    showToast("擅长位置不能为空");
                }
                break;
        }
        return isValidate;
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        // TODO Auto-generated method stub
        super.onSuccess(object, method);
        hideLoading();
        if (method.equals(HttpConstant.INDIVIDUAL_ACTIVITY_DETAIL)) {
            BroadcastController.sendUserChangeBroadcase(context);
            builder = new Builder(context);
            builder.setMessage("报名成功");
            builder.setTitle("提示");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public void onError(String method, String errorInfo) {
        super.onError(method, errorInfo);
        hideLoading();
        if (method.equals(HttpConstant.INDIVIDUAL_ACTIVITY_DETAIL)) {
            showToast(errorInfo);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.btnSubmit:
                if (submitMatch()) {
                    individualJoinMatch();
                }
                break;
        }
    }
}
