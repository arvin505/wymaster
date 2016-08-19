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
 * Created by Administrator on 2016/4/18.
 */
public class ImputOldPasswordActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.et_old_password)
    EditText etOldPassword;
    @Bind(R.id.btnLogin)
    TextView tvSubmit;
    @Bind(R.id.ivEmpty)
    ImageView ivEmpty;

    private Context context;
    private User user;
    private String password;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_imput_old_password);
        ButterKnife.bind(this);
        initView();
        context = this;
    }

    @Override
    protected void initView() {
        super.initView();
        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle(getResources().getString(R.string.change_password));
        tvSubmit.setOnClickListener(this);
        getLeftBtn().setOnClickListener(this);
        ivEmpty.setOnClickListener(this);
        etOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String s1 = s.toString();
                if (TextUtils.isEmpty(s1)) {
                    ivEmpty.setVisibility(View.INVISIBLE);
                } else {
                    ivEmpty.setVisibility(View.VISIBLE);
                }


                if (s1.length() < 6) {
                    tvSubmit.setEnabled(false);
                    tvSubmit.setTextColor(getResources().getColor(R.color.line));
                    tvSubmit.setBackgroundColor(getResources().getColor(R.color.lv_item_content_text));
                } else {
                    tvSubmit.setEnabled(true);
                    tvSubmit.setTextColor(getResources().getColor(R.color.white));
                    tvSubmit.setBackgroundColor(getResources().getColor(R.color.orange));
                }
            }
        });
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
                etOldPassword.setText("");
                break;
        }
    }

    private void submit() {
        password = etOldPassword.getText().toString();
        user = WangYuApplication.getUser(context);
        if (user == null) {
            Intent i = new Intent(context, LoginActivity.class);
            startActivity(i);
            return;
        }

        if (TextUtils.isEmpty(etOldPassword.getText().toString())) {
            showToast("请输入原密码");
            return;
        }

        if (password.length() < 6) {
            showToast("请输入至少6位密码");
            return;
        }

        showLoading();
        Map<String, String> map = new HashMap<>();
        map.put("oldPwd", password);
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.UPDATE_PASSWORD, map, HttpConstant.UPDATE_PASSWORD);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        Intent intent = new Intent(this, SetThePasswordActivity.class);
        intent.putExtra("oldPassword",password);
        startActivity(intent);
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
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
