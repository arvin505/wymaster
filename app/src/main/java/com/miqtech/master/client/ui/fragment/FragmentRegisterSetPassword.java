package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.miqtech.master.client.R;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.RegisterActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/11.
 */
public class FragmentRegisterSetPassword extends BaseFragment implements View.OnClickListener {

    private EditText password_et;
    private EditText repassword_et;
    private Button submit_bt;
    private String password_str;
    private String repassword_str;
    private View mainView;
    private Context context;
    private int retrievePassword;
    private boolean isFirst = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_register_set_password, null);
            context = inflater.getContext();
            initView();
        }
    }

    private void initView() {
        password_et = (EditText) mainView.findViewById(R.id.import_password_et);
        repassword_et = (EditText) mainView.findViewById(R.id.import_repassword_et);
        submit_bt = (Button) mainView.findViewById(R.id.submit_bt);

        submit_bt.setOnClickListener(this);
        retrievePassword = ((RegisterActivity) context).getRetrievePassword();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isFirst) {
            ((RegisterActivity) context).getRightTv().setVisibility(View.GONE);
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_bt:
                password_str = password_et.getText().toString().trim();
                repassword_str = repassword_et.getText().toString().trim();
                toDetermineWhethePasswords();
                break;
        }
    }

    /**
     * 判断密码并提交
     */
    private void toDetermineWhethePasswords() {
        if (TextUtils.isEmpty(password_str) || TextUtils.isEmpty(repassword_str)) {
            showToast("请把2个密码输完整");
            return;
        }

        if (password_str.length() < 6) {
            showToast("请输入至少6位数的密码");
            return;
        }

        if (password_str.equals(repassword_str)) {
            if ((retrievePassword == 2)) {
                Map<String, String> map = new HashMap<>();
                map.put("mobile", Constant.register_phone);
                map.put("password", password_str);
                map.put("code", ((RegisterActivity) context).getNoteCode());
                sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.RESET_PASSWORD, map, HttpConstant.RESET_PASSWORD);
            } else {
                ((RegisterActivity) context).setPassword(password_str);
                ((RegisterActivity) context).SkipNextFragment(3);
//                Map<String, String> map = new HashMap<>();
//                map.put("mobile", Constant.register_phone);
//                map.put("password", password_str);
//                sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.REGISTER, map, HttpConstant.REGISTER);
            }

        } else {
            showToast("2次输入的密码不一致");
            password_et.setText("");
            repassword_et.setText("");
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        try {
            if (method.equals(HttpConstant.REGISTER)) {
//                if (0 == object.getInt("code")) {
//                    ((RegisterActivity) context).SkipNextFragment(3);
//
//                } else {
//                    showToast(object.getString("result"));
//                }
            } else if (method.equals(HttpConstant.RESET_PASSWORD)) {
                if (0 == object.getInt("code")) {
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                    ((RegisterActivity) context).finish();
                } else {
                    showToast(object.getString("result"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        showToast(errMsg);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mainView;
    }
}
