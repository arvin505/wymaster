package com.miqtech.master.client.ui;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.ABTextUtil;
import com.miqtech.master.client.utils.IDCardUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.view.MyAlertView;
import com.miqtech.master.client.view.MyAlertView.Builder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class JoinCorpsActivity extends BaseActivity implements
        OnClickListener {
    private JoinCorpsActivity context;
    private int teamId;
    private LayoutParams params;
    private Button btn_individual_join, btn_create_corps;
    private LayoutInflater inflater;
    private EditText mobileNum;
    private EditText idNum;
    private EditText qqNum;
    private EditText trueName;
    private EditText expertisePos;
    private Button submit;
    private ImageView ivBackUp;
    private Builder builder;
    private MyAlertView dialog;
    private String teamName;
    private TextView tvTeamName;
    private User user;
    private ImageView back;

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.join_corps);
        context = this;
        inflater = LayoutInflater.from(context);
        teamId = getIntent().getIntExtra("teamId", 0);
        LogUtil.e("info", "teamdi == " + teamId);
        teamName = getIntent().getStringExtra("teamName");
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        ivBackUp = (ImageView) findViewById(R.id.ivBack);
        //ivBackUp.setImageResource(R.drawable.back);
        setLeftIncludeTitle("加入战队");
        submit = (Button) findViewById(R.id.btnSubmit);
        tvTeamName = (TextView) findViewById(R.id.teamName);
        submit.setOnClickListener(this);
        ivBackUp.setOnClickListener(this);
        mobileNum = (EditText) findViewById(R.id.mobileNumber);
        idNum = (EditText) findViewById(R.id.idNumber);
        qqNum = (EditText) findViewById(R.id.qqNumber);
        trueName = (EditText) findViewById(R.id.editName);
        expertisePos = (EditText) findViewById(R.id.expertisePosition);
        tvTeamName.setText(teamName);
        back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mobileNum.setText(PreferencesUtil.getUserMobile(context));
        idNum.setText(PreferencesUtil.getUserIDCard(context));
        qqNum.setText(PreferencesUtil.getUserQQ(context));
        trueName.setText(PreferencesUtil.getUserRealName(context));
        expertisePos.setText(PreferencesUtil.getUserLabor(context));
    }

    private void individualJoinMatch() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        user = WangYuApplication.getUser(context);
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
        }
        params.put("teamId", teamId + "");

        LogUtil.e("info","id sfsf = " + teamId);
        params.put("name", trueName.getText().toString()
                .trim()
                + "");
        params.put("telephone", mobileNum.getText()
                .toString().trim()
                + "");
        params.put("idCard", idNum.getText().toString()
                .trim()
                + "");
        params.put("qq", qqNum.getText().toString()
                .trim()
                + "");
        params.put("labor", expertisePos.getText()
                .toString().trim()
                + "");
        PreferencesUtil.saveUserIDCard(idNum.getText().toString(), context);
        PreferencesUtil.saveUserMobile(mobileNum.getText().toString(), context);
        PreferencesUtil.saveUserRealName(trueName.getText().toString(), context);
        PreferencesUtil.saveUserQQ(qqNum.getText().toString(), context);
        PreferencesUtil.saveUserLabor(expertisePos.getText().toString(), context);

        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.JOIN_CORPS,
                params, HttpConstant.JOIN_CORPS);
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
        hideLoading();
        LogUtil.e("sss","object == " + object.toString());
        super.onSuccess(object,method);
        if (method.equals(HttpConstant.JOIN_CORPS)) {
            builder = new Builder(context);
            builder.setMessage("报名成功");
            builder.setTitle("提示");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //MainActivity.requestMsgCount();
                    finish();
                }
            });
            dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public void onError(String method, String errorInfo) {
        hideLoading();
        super.onError(method, errorInfo);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        if (method.equals(HttpConstant.JOIN_CORPS)) {
            builder = new Builder(context);
            try {
                builder.setMessage(object.getString("result"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
