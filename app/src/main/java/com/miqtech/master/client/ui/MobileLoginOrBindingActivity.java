package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.jpush.service.JPushUtil;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.ABTextUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.view.MyAlertView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 1、手机快捷登陆
 * 2、手机绑定
 * Created by Administrator on 2016/4/8.
 */
public class MobileLoginOrBindingActivity extends BaseActivity implements View.OnClickListener, MyAlertView.VerificationCodeDialogAction {

    @Bind(R.id.edtPhone)
    EditText edtPhone;
    @Bind(R.id.ivUserNameEmpty)
    ImageView ivUserNameEmpty;
    @Bind(R.id.ivUserNameEmpty2)
    ImageView ivUserNameEmpty2;
    @Bind(R.id.edtAuthCode)
    EditText edtAuthCode;
    //    @Bind(R.id.tvAuthCode)
//    TextView tvAuthCode;
    @Bind(R.id.btnLogin)
    TextView btnLogin;
    @Bind(R.id.llAgreement)
    LinearLayout llAgreement;
    @Bind(R.id.tvMobileSpeedyLogin)
    TextView tvMobileSpeedyLogin;//手机快捷登陆提示语

    @Bind(R.id.tvRightHandle)
    TextView tvRightHandle;

    private String mobileStr;
    private String verificationCode;
    private String imgVerificationCode;
    private MyAlertView dialog;

    private Context context;
    private int type = 0;//0代表手机快捷登陆，1代表手机绑定。(默认为0)

    private String openId;//第三方用户标识,必传
    private int platform;//1qq2微信3微博,必传

    private int recLen = 60;//倒计时
    private boolean isEtPhoneEmpty = false;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_mobile_login_or_binding);
        ButterKnife.bind(this);
        context = this;
        type = getIntent().getIntExtra("type", 0);
        openId = getIntent().getStringExtra("openId");
        platform = getIntent().getIntExtra("platform", -1);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        setLeftBtnImage(R.drawable.back);
        tvRightHandle.setText("获取验证码");
        tvRightHandle.setVisibility(View.VISIBLE);
        if (type == 0) {
            setLeftIncludeTitle(getResources().getString(R.string.mobile_shortcut_login));
            tvMobileSpeedyLogin.setVisibility(View.VISIBLE);
            llAgreement.setVisibility(View.VISIBLE);
        } else if (type == 1) {
            setLeftIncludeTitle(getResources().getString(R.string.mobile_binding));
            tvMobileSpeedyLogin.setVisibility(View.GONE);
            llAgreement.setVisibility(View.GONE);
        }

        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String s1 = s.toString();
                if (TextUtils.isEmpty(s1)) {
                    isEtPhoneEmpty = true;
                } else {
                    isEtPhoneEmpty = false;
                }

                if (recLen == 60) {
                    if (s1.length() < 11) {
                        ivUserNameEmpty.setVisibility(View.INVISIBLE);
                        tvRightHandle.setTextColor(context.getResources().getColor(R.color.black_extend_intro));
                        tvRightHandle.setEnabled(false);
                    } else {
                        ivUserNameEmpty.setVisibility(View.VISIBLE);
                        tvRightHandle.setTextColor(context.getResources().getColor(R.color.orange));
                        tvRightHandle.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtAuthCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String ss = s.toString();

                if (TextUtils.isEmpty(ss)) {
                    ivUserNameEmpty2.setVisibility(View.INVISIBLE);
                } else {
                    ivUserNameEmpty2.setVisibility(View.VISIBLE);
                }

                if (TextUtils.isEmpty(ss) || isEtPhoneEmpty) {
                    btnLogin.setEnabled(false);
                    btnLogin.setTextColor(getResources().getColor(R.color.line));
                    btnLogin.setBackgroundColor(getResources().getColor(R.color.lv_item_content_text));
                } else {
                    btnLogin.setEnabled(true);
                    btnLogin.setTextColor(getResources().getColor(R.color.white));
                    btnLogin.setBackgroundColor(getResources().getColor(R.color.orange));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btnInitialStatu();
    }

    /**
     * 按钮初始状态
     */
    private void btnInitialStatu() {
        tvRightHandle.setTextColor(context.getResources().getColor(R.color.black_extend_intro));
        tvRightHandle.setEnabled(false);
        btnLogin.setEnabled(false);
        btnLogin.setTextColor(getResources().getColor(R.color.line));
        btnLogin.setBackgroundColor(getResources().getColor(R.color.lv_item_content_text));
    }

    @Override
    protected void initData() {
        super.initData();
        tvRightHandle.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        ivUserNameEmpty.setOnClickListener(this);
        ivUserNameEmpty2.setOnClickListener(this);
        llAgreement.setOnClickListener(this);
        getLeftBtn().setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvRightHandle://获取验证码
                getSMSCodeMobile(0);
                break;
            case R.id.btnLogin://快捷登陆或绑定
                login();
                break;
            case R.id.ivUserNameEmpty://
                edtPhone.setText("");
                break;
            case R.id.ivUserNameEmpty2://
                edtAuthCode.setText("");
                break;
            case R.id.llAgreement://
                Intent intent = new Intent(context, SubjectActivity.class);
                intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.AGREEMENT);
                startActivity(intent);
                break;
            case R.id.ibLeft:
                onBackPressed();
                break;
        }
    }

    private void login() {
        mobileStr = edtPhone.getText().toString();
        if (!ABTextUtil.isMobile(mobileStr)) {
            showToast(context.getResources().getString(R.string.phone_form_no));
            return;
        }

        verificationCode = edtAuthCode.getText().toString();
        if (TextUtils.isEmpty(verificationCode)) {
            showToast("请填写验证码");
            return;
        }

        if (type == 0) {//手机号快捷登陆
            Map<String, String> map = new HashMap<>();
            map.put("checkCode", verificationCode);
            map.put("username", mobileStr);
            map.put("type", 2 + "");//1密码登陆2快捷登陆,不传默认1
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.USER_LOGIN, map, HttpConstant.USER_LOGIN);
        } else if (type == 1) {//手机绑定
            Map<String, String> map = new HashMap<>();
            map.put("checkCode", verificationCode);
            map.put("username", mobileStr);
            map.put("platform", platform + "");
            map.put("openId", openId);
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.BIND_MOBILEPHONE, map, HttpConstant.BIND_MOBILEPHONE);
        }

    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        if (method.equals(HttpConstant.SEND_SMS_CODE_MOBILE)) {
            countDown();
        } else if (method.equals(HttpConstant.BIND_MOBILEPHONE)) {
            loginSuccess(object);
        } else if (method.equals(HttpConstant.USER_LOGIN)) {
            loginSuccess(object);
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
            if (method.equals(HttpConstant.SEND_SMS_CODE_MOBILE)) {
                if (object.has("code") && object.getInt("code") == -2) {
                    dialog = new MyAlertView.Builder(context).creatImgVerificationCode(mobileStr, this);
                }
            } else if (method.equals(HttpConstant.BIND_MOBILEPHONE)) {
                if (object.has("code") && object.getInt("code") == -2) {
                    dialog = new MyAlertView.Builder(context).creatImgVerificationCode(mobileStr, this);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPositive(String verificationCode) {
        imgVerificationCode = verificationCode;
        getSMSCodeMobile(1);
    }

    @Override
    public void doNegative() {

    }

    /**
     * 校验验证码并请求短信验证
     *
     * @param imgCodeOrNo 0代表不需要图片验证码的，1代表有图片验证码
     */
    private void getSMSCodeMobile(int imgCodeOrNo) {
        mobileStr = edtPhone.getText().toString();
        if (!ABTextUtil.isMobile(mobileStr)) {
            showToast(context.getResources().getString(R.string.phone_form_no));
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobileStr);
        if (type == 0) {//0代表手机快捷登陆，1代表手机绑定。(默认为0)
            map.put("type", 4 + "");//1:发送注册的验证码2:发送找回密码的验证码4快捷登陆验证码5第三方登录验证码
        } else if (type == 1) {
            map.put("type", 5 + "");//1:发送注册的验证码2:发送找回密码的验证码4快捷登陆验证码5第三方登录验证码
        }

        if (imgCodeOrNo == 1) {
            if (imgVerificationCode == null || TextUtils.isEmpty(imgVerificationCode)) {
                showToast("请输入验证码");
                return;
            }
            map.put("code", imgVerificationCode);
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SEND_SMS_CODE_MOBILE, map, HttpConstant.SEND_SMS_CODE_MOBILE);
    }

    /**
     * 快捷登陆成功后
     *
     * @param object
     */
    private void loginSuccess(JSONObject object) {
        try {
            User user = new Gson().fromJson(object.getString("object"), User.class);
            //WangYuApplication.getJpushUtil().setAlias(context.getResources().getString(R.string.alias) + user.getId());
            WangYuApplication.getJpushUtil().setAliasWithTags(getResources().getString(R.string.alias) + user.getId(), JPushUtil.initTags(user));
            WangYuApplication.setUser(user);
            PreferencesUtil.setUser(context, object.getString("object"));
            BroadcastController.sendUserChangeBroadcase(context);
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 倒计时
    private void countDown() {
        recLen = 60;
        Message message = handler.obtainMessage(1); // Message
        handler.sendMessageDelayed(message, 1000);
//        handler.postDelayed(myRunnale, 1000);
        tvRightHandle.setVisibility(View.VISIBLE);
        tvRightHandle.setTextColor(context.getResources().getColor(R.color.gray));
        tvRightHandle.setEnabled(false);
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
                    tvRightHandle.setText("重新获取" + "(" + recLen + ")");
                    tvRightHandle.setTextColor(context.getResources().getColor(R.color.black_extend_intro));
                    if (recLen > 0) {
                        handler.postDelayed(myRunnale, 1000);
                        ; // send message
                    } else {
                        tvRightHandle.setEnabled(true);
                        tvRightHandle.setTextColor(context.getResources().getColor(R.color.orange));
                        tvRightHandle.setText("重新获取");
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
