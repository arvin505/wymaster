package com.miqtech.master.client.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.RegisterActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/11.
 */
public class FragmentRegisterImportCode extends BaseFragment implements View.OnClickListener {

    private TextView show_phone_tv;
    private EditText authCode_et;
    private TextView authCode_error_show;
    private Button submit_authCode;
    private TextView countDown_tv;

    private View mainView;
    private Context context;
    private boolean isFirst = true;
    private int recLen = 60;

    private Dialog dialog;
    private String imgCodeStr;
    private ImageView imgCode;
    private ImageView refreImg;
    private EditText et_auth_code;

    private String smsCodeMobile;

    private int type = 1;
    private int retrievePassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_register_import_code, null);
            context = inflater.getContext();
            initView();
        }
    }

    private void initView() {
        show_phone_tv = (TextView) mainView.findViewById(R.id.receive_telephone_tv);
        authCode_et = (EditText) mainView.findViewById(R.id.import_auth_code_et);
        authCode_error_show = (TextView) mainView.findViewById(R.id.auth_code_error_tv);
        submit_authCode = (Button) mainView.findViewById(R.id.submit_auth_code_bt);
        countDown_tv = ((RegisterActivity) context).getRightTv();
        countDown_tv.setTextColor(context.getResources().getColor(R.color.dark_gray));
        countDown_tv.setOnClickListener(this);

        submit_authCode.setOnClickListener(this);
        retrievePassword = ((RegisterActivity) context).getRetrievePassword();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirst) {
            show_phone_tv.setText(Utils.changeString(Constant.register_phone));
            countDown();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_auth_code_bt://提交短信验证码
                checkSMSCode();
                break;
            case R.id.tvRightHandle://获取验证码
                creatImageCodeDialog();
                break;
            case R.id.dialog_register_on_tv://图片验证码弹框  确定
                sendSMSCodeMobile();
                dialog.dismiss();
                break;
            case R.id.dialog_register_off_tv://图片验证码弹框  取消
                dialog.dismiss();
                break;
            case R.id.dialog_code_refre_iv://图片验证码弹框  刷新图片验证
                AsyncImage.loadYZM(context, HttpConstant.SERVICE_HTTP_AREA+HttpConstant.IMAGE_CODE_REGISTER + Constant.register_phone, imgCode);
                break;
            case R.id.dialog_imageview_code_iv://图片验证码弹框  刷新图片验证
                AsyncImage.loadYZM(context, HttpConstant.SERVICE_HTTP_AREA+HttpConstant.IMAGE_CODE_REGISTER + Constant.register_phone, imgCode);
                break;
        }
    }

    private void checkSMSCode() {
        smsCodeMobile = authCode_et.getText().toString().trim();
        if (smsCodeMobile == null || TextUtils.isEmpty(smsCodeMobile)) {
            showToast("请输入验证码");
            return;
        }
        if (((RegisterActivity) context).getRetrievePassword() == 2) {
            type = 2;
        }

        Map<String, String> map = new HashMap<>();
        map.put("mobile", Constant.register_phone);
        map.put("type", type + "");
        map.put("checkCode", smsCodeMobile);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.CHECK_SMS_CODE, map, HttpConstant.CHECK_SMS_CODE);
    }

    private void sendSMSCodeMobile() {
        imgCodeStr = et_auth_code.getText().toString().trim();
        if (imgCodeStr == null || TextUtils.isEmpty(imgCodeStr)) {
            showToast("请输入验证码");
            return;
        }

        if (((RegisterActivity) context).retrievePassword == 2) {
            type = 2;
        }

        Map<String, String> map = new HashMap<>();
        map.put("mobile", Constant.register_phone);
        map.put("type", type + "");
        map.put("code", imgCodeStr);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SEND_SMS_CODE_MOBILE, map, HttpConstant.SEND_SMS_CODE_MOBILE);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        try {
            if (method.equals(HttpConstant.SEND_SMS_CODE_MOBILE)) {
                if (0 == object.getInt("code")) {
                    dialog.dismiss();
                    countDown();
                } else {
                    if (object.has("result")) {
                        showToast(object.getString("result"));
                    } else {
                        showToast("网络错误");
                    }
                }
            } else if (method.equals(HttpConstant.CHECK_SMS_CODE)) {
                countDown_tv.setVisibility(View.GONE);
                if (0 == object.getInt("code")) {
                    countDown_tv.setText("");
                    ((RegisterActivity) context).setNoteCode(smsCodeMobile);
                    ((RegisterActivity) context).SkipNextFragment(2);
                    handler.removeCallbacks(myRunnale);
                    countDown_tv.setEnabled(true);
                } else {
                    authCode_et.setText("");
                    if (object.has("result")) {
                        showToast(object.getString("result"));
                    } else {
                        showToast("网络错误");
                    }
                    //creatImageCodeDialog();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        showToast(errMsg);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        try {
            if (method.equals(HttpConstant.SEND_SMS_CODE_MOBILE)) {
                if (object.has("result")) {
                    ((RegisterActivity) context).creatDialogForHint(context, "发送失败", object.getString("result"));
                } else {
                    ((RegisterActivity) context).creatDialogForHint(context, "发送失败", object.toString());
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建带有图片验证码的弹框
     */
    private void creatImageCodeDialog() {
        dialog = new Dialog(context, R.style.register_style);
        dialog.setContentView(R.layout.dialog_register_pact_img);
        dialog.setCanceledOnTouchOutside(false);

        et_auth_code = (EditText) dialog.findViewById(R.id.dialog_register_et);
        TextView tvYes = (TextView) dialog.findViewById(R.id.dialog_register_on_tv);
        TextView tvNo = (TextView) dialog.findViewById(R.id.dialog_register_off_tv);
        refreImg = (ImageView) dialog.findViewById(R.id.dialog_code_refre_iv);
        imgCode = (ImageView) dialog.findViewById(R.id.dialog_imageview_code_iv);

        tvYes.setOnClickListener(this);
        tvNo.setOnClickListener(this);
        refreImg.setOnClickListener(this);
        imgCode.setOnClickListener(this);
        AsyncImage.loadYZM(context, HttpConstant.SERVICE_HTTP_AREA+HttpConstant.IMAGE_CODE_REGISTER + Constant.register_phone, imgCode);
        dialog.show();
    }

    // 倒计时
    private void countDown() {
        recLen = 60;
        Message message = handler.obtainMessage(1); // Message
        handler.sendMessageDelayed(message, 1000);
//        handler.postDelayed(myRunnale,1000);
        countDown_tv.setVisibility(View.VISIBLE);
        countDown_tv.setTextColor(context.getResources().getColor(R.color.gray));
        countDown_tv.setEnabled(false);
    }

    Runnable myRunnale = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(1);
        }
    };

    final Handler handler = new Handler() {

        public void handleMessage(Message msg) { // handle message
            switch (msg.what) {
                case 1:
                    recLen--;
                    countDown_tv.setText(recLen + "重新获取");
                    if (recLen > 0) {
                        handler.postDelayed(myRunnale, 1000);; // send message
                    } else {
                        countDown_tv.setEnabled(true);
                        countDown_tv.setTextColor(context.getResources().getColor(R.color.orange));
                        countDown_tv.setText("重新获取");
                        countDown_tv.setEnabled(true);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mainView;
    }
}
