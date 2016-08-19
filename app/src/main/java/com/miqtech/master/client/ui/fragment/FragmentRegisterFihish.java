package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.jpush.service.JPushUtil;
import com.miqtech.master.client.ui.MainActivity;
import com.miqtech.master.client.ui.RegisterActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/11.
 */
public class FragmentRegisterFihish extends BaseFragment implements View.OnClickListener {

    private TextView skip_tv;
    private EditText invateCode_ed;
    private Button submit;
    private String invateCode_str = "";
    private View mainView;
    private Context context;
    private int retrievePassword;
    private boolean isFirst = true;
    private boolean isSuccess = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_register_finish, null);
            context = inflater.getContext();
            initView();
        }
    }

    private void initView() {
        invateCode_ed = (EditText) mainView.findViewById(R.id.import_invite_code_et);
        submit = (Button) mainView.findViewById(R.id.submit_finish_bt);

        submit.setOnClickListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isFirst) {
            skip_tv = ((RegisterActivity) context).getRightTv();
            skip_tv.setOnClickListener(this);
            skip_tv.setText("跳过");
            skip_tv.setTextColor(context.getResources().getColor(R.color.orange));
            retrievePassword = ((RegisterActivity) context).getRetrievePassword();

            if (retrievePassword == 2) {
                skip_tv.setEnabled(false);
            } else {
                skip_tv.setOnClickListener(this);
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tvRightHandle://跳过
                toRegister();
                break;
            case R.id.submit_finish_bt:
                checkInvatationCode();
                break;
        }
    }

    private void checkInvatationCode() {

        invateCode_str = invateCode_ed.getText().toString().trim();

        if (TextUtils.isEmpty(invateCode_str)) {
            showToast("请填写邀请码");
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("invitationCode", invateCode_str);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.CHECK_INVATATION_CODE, map, HttpConstant.CHECK_INVATATION_CODE);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        try {
            if (method.equals(HttpConstant.CHECK_INVATATION_CODE)) {
                if (0 == object.getInt("code")) {
                    toRegister();
                    isSuccess = true;
                } else if (1 == object.getInt("code")) {
                    ((RegisterActivity) context).creatDialogForHint(context, "您输入的邀请码有误", null);
                }
            } else if (method.equals(HttpConstant.REGISTER)) {
                if (0 == object.getInt("code") && object.has("object")) {
                    if (isSuccess) {
                        ((RegisterActivity) context).creatDialogImg(context);
                        isSuccess = false;
                    }
                    User user = new Gson().fromJson(object.getString("object"), User.class);
                    //WangYuApplication.getJpushUtil().setAlias(context.getResources().getString(R.string.alias) + user.getId());
                    WangYuApplication.getJpushUtil().setAliasWithTags(getResources().getString(R.string.alias) + user.getId(), JPushUtil.initTags(user));
                    WangYuApplication.setUser(user);
                    PreferencesUtil.setUser(context, object.getString("object"));
                    BroadcastController.sendUserChangeBroadcase(context);
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    ((RegisterActivity) context).finish();
                } else if (object.has("result")) {
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
        try {
            if (object.has("result")) {
                showToast(object.getString("result"));
            } else {
                showToast(object.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mainView;
    }

    private void toRegister() {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", Constant.register_phone);
        map.put("password", ((RegisterActivity) context).getPassword());
        map.put("smsCode", ((RegisterActivity) context).getNoteCode());
        map.put("invitationCode", invateCode_str);
        map.put("androidChannelName", Utils.getAppMetaData(context, "UMENG_CHANNEL"));
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String DEVICE_ID = tm.getDeviceId();
        map.put("deviceId",DEVICE_ID);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.REGISTER, map, HttpConstant.REGISTER);
    }


}
