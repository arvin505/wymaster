package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 设置密码，修改密码
 * Created by Administrator on 2016/4/18.
 */
public class SetThePasswordActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.et_new_password)
    EditText etNewPassword;
    @Bind(R.id.et_affirm_password)
    EditText etAffirmPassword;
    @Bind(R.id.btnLogin)
    TextView tvSubmit;
    @Bind(R.id.ivEmpty)
    ImageView ivEmpty;
    @Bind(R.id.ivEmptyTwo)
    ImageView ivEmptyTwo;

    private Context context;
    private User user;
    private String oldPassword;
    private String newPassword;
    private String affirmPassword;

    private boolean isOnClick = false;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_set_the_password);
        ButterKnife.bind(this);
        context = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle(getResources().getString(R.string.setting_password));
        oldPassword = getIntent().getStringExtra("oldPassword");
        tvSubmit.setOnClickListener(this);
        getLeftBtn().setOnClickListener(this);
        ivEmpty.setOnClickListener(this);
        ivEmptyTwo.setOnClickListener(this);

        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String s1 = s.toString().trim();
                if (!TextUtils.isEmpty(s1)) {
                    ivEmpty.setVisibility(View.VISIBLE);
                } else {
                    ivEmpty.setVisibility(View.INVISIBLE);
                }

                if (s1.length() < 6) {
                    isOnClick = false;
                } else {
                    isOnClick = true;
                }
            }
        });
        etAffirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String s1 = s.toString().trim();
                if (!TextUtils.isEmpty(s1)) {
                    ivEmptyTwo.setVisibility(View.VISIBLE);
                } else {
                    ivEmptyTwo.setVisibility(View.INVISIBLE);
                }

                if (s1.length() < 6) {
                    tvSubmit.setEnabled(false);
                    tvSubmit.setTextColor(getResources().getColor(R.color.line));
                    tvSubmit.setBackgroundColor(getResources().getColor(R.color.lv_item_content_text));
                } else if (isOnClick) {
                    tvSubmit.setEnabled(true);
                    tvSubmit.setTextColor(getResources().getColor(R.color.white));
                    tvSubmit.setBackgroundColor(getResources().getColor(R.color.orange));
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.btnLogin:
                submit();
                break;
            case R.id.ivEmpty:
                etNewPassword.setText("");
                break;
            case R.id.ivEmptyTwo:
                etAffirmPassword.setText("");
                break;
        }
    }

    private void submit() {
        newPassword = etNewPassword.getText().toString();
        affirmPassword = etAffirmPassword.getText().toString();
        user = WangYuApplication.getUser(context);
        if (user == null) {
            Intent i = new Intent(context, LoginActivity.class);
            startActivity(i);
            return;
        }

        if (TextUtils.isEmpty(newPassword) && TextUtils.isEmpty(affirmPassword)) {
            showToast("请输入密码");
            return;
        }

        if (newPassword.length() < 6) {
            showToast("请输入至少6位密码");
            return;
        }

        if (!newPassword.equals(affirmPassword)) {
            showToast("2次输入的密码不同");
            return;
        }
        showLoading();
        Map<String, String> map = new HashMap<>();

        map.put("newPwd", newPassword);
        if (!TextUtils.isEmpty(oldPassword)) {
            map.put("oldPwd", oldPassword);
        }
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.UPDATE_PASSWORD, map, HttpConstant.UPDATE_PASSWORD);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        user = WangYuApplication.getUser(context);
        if (TextUtils.isEmpty(oldPassword)) {
            user.setIsPasswordNull(0);
            WangYuApplication.setUser(user);
            showToast("设置密码成功");
        } else {
            showToast("修改密码成功");
        }
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        showToast(errMsg);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        try {
            if (object.has("result")) {
                showToast(object.getString("result").toString());
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
