package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.jpush.service.JPushUtil;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.ShareToFriendsUtil;
import com.miqtech.master.client.view.WeiboLoginImageView;
import com.miqtech.master.client.view.progressbutton.CircularProgressButton;
import com.miqtech.master.client.watcher.Observerable;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * login
 * Created by wuxuenan on 2015/11/19 0019.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    @Bind(R.id.btnBack)
    ImageButton btnBack;
    @Bind(R.id.ivUserNameEmpty)
    ImageView ivUserNameEmpty;
    @Bind(R.id.ivPasswordEmpty)
    ImageView ivPasswordEmpty;
    @Bind(R.id.tvMobileShortcutLogin)
    TextView tvMobileShortcutLogin;//手机号快捷登陆
    @Bind(R.id.ivLoginqq)
    ImageView ivLoginqq;
    @Bind(R.id.ivLoginwechat)
    ImageView ivLoginwechat;
    @Bind(R.id.ivLoginweibo)
    WeiboLoginImageView ivLoginweibo;


    private TextView tvForgetPwd;
    private EditText edtPwd;
    private EditText edtPhone;
    private CircularProgressButton btnLogin;
    private RelativeLayout rlPassword;
    private RelativeLayout rlUserName;
    private TextView btnRegister;
    private Context context;

    public final static int LOGIN_OK = 10;
    /*微博*/
    /**
     * 登陆认证对应的listener
     */
    private AuthListener mLoginListener = new AuthListener();
    private AuthInfo mAuthInfo;
    /*QQ*/
    private BaseUiListener uiListener = new BaseUiListener();
    private Tencent mTencent;
    /*微信*/
    private IWXAPI api;

    private int platform = 3;//1qq2微信3微博,必传.默认为3
    private final static int LOGIN_QQ = 1;
    private final static int LOGIN_WECHAT = 2;
    private final static int LOGIN_WEIBO = 3;

    private String openId;//第三方登陆标识

    private ShareToFriendsUtil shareToFriendsUtil;
    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.equals(" ")) return "";
            else return null;
        }
    };

    private Observerable observerable = Observerable.getInstance();

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        rlPassword = (RelativeLayout) findViewById(R.id.rlPassword);
        rlUserName = (RelativeLayout) findViewById(R.id.rlUserName);
        btnLogin = (CircularProgressButton) findViewById(R.id.btnLogin);
        btnRegister = (TextView) findViewById(R.id.btnRegister);
        tvForgetPwd = (TextView) findViewById(R.id.tvForgetPwd);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtPwd = (EditText) findViewById(R.id.edtPwd);
        edtPwd.setFilters(new InputFilter[]{filter});
        rlPassword.getBackground().setAlpha(20);
        rlUserName.getBackground().setAlpha(20);
//        btnBack.getBackground().setAlpha(120);
//        btnRegister.getBackground().setAlpha(120);
        btnLogin.setIndeterminateProgressMode(true);

        btnLogin.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        tvForgetPwd.setOnClickListener(this);
        ivUserNameEmpty.setOnClickListener(this);
        ivPasswordEmpty.setOnClickListener(this);
        edtPhone.addTextChangedListener(this);
        edtPwd.addTextChangedListener(this);
//        btnLogin.setFocusable(true);
//        btnLogin.setFocusableInTouchMode(true);
//        btnLogin.requestFocus();

        tvMobileShortcutLogin.setOnClickListener(this);
        ivLoginqq.setOnClickListener(this);
        ivLoginwechat.setOnClickListener(this);
//        ivLoginweibo.setOnClickListener(this);
        configurationForLogin();
        shareToFriendsUtil = new ShareToFriendsUtil(context);
    }

    /**
     * 第三方登陆配置
     */
    private void configurationForLogin() {
        /*微博*/
        // 创建授权认证信息
        mAuthInfo = new AuthInfo(getApplicationContext(), Constant.WB_APP_KEY, Constant.WB_REDIRECTURL, Constant.WB_APP_SCOPE);
        ivLoginweibo.setWeiboAuthInfo(mAuthInfo, mLoginListener);
        //微信api注册
        api = WXAPIFactory.createWXAPI(getApplicationContext(), Constant.WX_APP_ID, true);
        api.registerApp(Constant.WX_APP_ID);
        //QQ注册
        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, getApplicationContext());
    }

    @Override
    protected void initData() {
        super.initData();
        context = this;
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnLogin:
                if (btnLogin.getProgress() != 50) {
                    if (btnLogin.getProgress() == -1) {
                        btnLogin.setProgress(0);
                    }
                    checkPhoneAndPwd();
                    //showToast("登录");
                } else {
                    //showToast("不不登录");
                }
                break;
            case R.id.btnRegister:
                //vpLogin.setCurrentItem(1);
                intent = new Intent(context, RegisterActivity.class);
                onBackPressed();
                startActivity(intent);
                break;
            case R.id.tvForgetPwd:
                intent = new Intent(context, RegisterActivity.class);
                intent.putExtra("retrievePassword", 2);
                onBackPressed();
                startActivity(intent);
                break;
            case R.id.ivUserNameEmpty:
                edtPhone.setText("");
                edtPwd.setText("");
                break;
            case R.id.ivPasswordEmpty:
                edtPwd.setText("");
                break;
            case R.id.tvMobileShortcutLogin://手机号快捷登陆
                intent = new Intent(context, MobileLoginOrBindingActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
            case R.id.ivLoginqq:
                thirdLogin(LOGIN_QQ);
                break;
            case R.id.ivLoginwechat:
                thirdLogin(LOGIN_WECHAT);
                break;
            case R.id.ivLoginweibo:
                platform = 3;
                break;
        }
    }

    private void checkPhoneAndPwd() {
        final String strPhone = edtPhone.getText().toString();
        final String strPwd = edtPwd.getText().toString();
        if (!TextUtils.isEmpty(strPhone) && !TextUtils.isEmpty(strPwd) && strPwd.length() > 5) {
            boolean result = strPhone.matches(Constant.PHONE_FORMAT);
            if (result) {
                btnLogin.setProgress(50);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        HashMap params = new HashMap();
                        params.put("username", strPhone);
                        params.put("password", strPwd);
                        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.USER_LOGIN, params, HttpConstant.USER_LOGIN);

                        //将输入法隐藏
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edtPwd.getWindowToken(), 0);
                    }
                }, 1000);
            }
        } else {
            showToast("账号或密码格式不正确");
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        if (HttpConstant.USER_LOGIN.equals(method)) {
            observerable.notifyChange(Observerable.ObserverableType.REWARD_COMMENT, 6);
            initUser(object);
        } else if (method.equals(HttpConstant.THIRD_LOGIN)) {
            toBindingOrtoMain(object);
        }
    }

    private void initUser(final JSONObject object) {
        try {
            if (object.getInt("code") == 0 && object.has("object")) {
                btnLogin.setProgress(100);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String strUser = null;
                        try {
                            strUser = object.getString("object");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        User user = new Gson().fromJson(strUser, User.class);
                        //WangYuApplication.getJpushUtil().setAlias(context.getResources().getString(R.string.alias) + user.getId());
//                        WangYuApplication.getJpushUtil().setAliasWithTags(getResources().getString(R.string.alias_test) + user.getId(), JPushUtil.initTags(user));
                        if (HttpConstant.SERVICE_HTTP_AREA.contains("wy")) {
                            WangYuApplication.getJpushUtil().setAliasWithTags(getResources().getString(R.string.alias_test) + user.getId(), JPushUtil.initTags(user));
                        } else {
                            WangYuApplication.getJpushUtil().setAliasWithTags(getResources().getString(R.string.alias) + user.getId(), JPushUtil.initTags(user));
                        }
                        WangYuApplication.setUser(user);
                        PreferencesUtil.setUser(context, strUser);
                        showToast("登录成功");
                        BroadcastController.sendUserChangeBroadcase(context);
                        //返回标识，成功登录
                        setResult(RESULT_OK);
                        finish();
                    }
                }, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loginFail(final JSONObject object) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (object.getInt("code") == 1) {
                        btnLogin.setProgress(0);
                    } else if (object.getInt("code") == 2) {
                        btnLogin.setProgress(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
        btnLogin.setProgress(-1);
    }


    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        loginFail(object);
    }


    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        if (method.equals(HttpConstant.USER_LOGIN)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnLogin.setProgress(0);
                }
            }, 1000);
            btnLogin.setProgress(-1);
        }
        //showToast(errMsg);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (edtPwd.getText().length() > 0) {
            ivPasswordEmpty.setVisibility(View.VISIBLE);
        } else {
            ivPasswordEmpty.setVisibility(View.INVISIBLE);
        }
        if (edtPhone.getText().length() > 0) {
            ivUserNameEmpty.setVisibility(View.VISIBLE);
        } else {
            ivUserNameEmpty.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (platform == LOGIN_QQ) {
            if (requestCode == Constants.REQUEST_API) {//QQ登陆的回调
                if (resultCode == Constants.RESULT_LOGIN) {
                    mTencent.onActivityResult(requestCode, resultCode, data);
                }
            }
        } else if (platform == LOGIN_WEIBO) {//微博
            ivLoginweibo.onActivityResult(requestCode, resultCode, data);//微博登陆的回调
        } else if (platform == LOGIN_WECHAT) {//微信
        }

//        if (shareToFriendsUtil != null && shareToFriendsUtil.getmSsoHandler() != null) {
//            shareToFriendsUtil.getmSsoHandler().authorizeCallBack(requestCode, resultCode, data);
//        }
    }

    /**
     * 第三方登陆
     *
     * @param loginType 1qq2微信3微博,必传
     */
    private void thirdLogin(int loginType) {
        platform = loginType;
        switch (loginType) {
            case LOGIN_QQ:
//                if (!mTencent.isSessionValid()) {
                platform = LOGIN_QQ;
                mTencent.login(this, "get_user_info", uiListener);
//                    mTencent.login(this, "all", uiListener);
//                }
                break;
            case LOGIN_WECHAT:
                SendAuth.Req req = new SendAuth.Req();
                //授权读取用户信息
                req.scope = "snsapi_userinfo";
                //自定义信息
                req.state = "wangyudashi";
                //向微信发送请求
                api.sendReq(req);
                platform = LOGIN_WEIBO;
//                this.finish();
                break;
            case LOGIN_WEIBO:
                break;
        }
    }

    /**
     * 微博：登入按钮的监听器，接收授权结果。
     */
    private class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            if (accessToken != null && accessToken.isSessionValid()) {
                openId = accessToken.getUid();
                thirdLoginToVerification();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            showToast(R.string.weibo_login_error);
        }

        @Override
        public void onCancel() {
            showToast(R.string.weibo_login_cancel);
        }
    }

    /**
     * QQ登陆实例回调
     */
    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            try {
                JSONObject object = new JSONObject(o.toString());
                if (object.has("openid")) {
                    openId = object.getString("openid");
                }
                thirdLoginToVerification();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            showToast(R.string.qq_login_error);
        }

        @Override
        public void onCancel() {
            showToast(R.string.qq_login_cancel);
        }

    }

    /**
     * 第三方登陆后后台验证
     */
    private void thirdLoginToVerification() {
        showLoading();
        Map<String, String> map = new HashMap<>();
        map.put("openId", openId);
        map.put("platform", platform + "");//1qq2微信3微博,必传
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.THIRD_LOGIN, map, HttpConstant.THIRD_LOGIN);
    }

    /**
     * 判断是否去绑定手机号还是直接登陆跳主页
     *
     * @param object
     */
    private void toBindingOrtoMain(JSONObject object) {
        Intent intent = null;
        try {
            if (!object.has("object")) {//如果第三方登录未绑定手机号,则返回的object为空
                intent = new Intent(context, MobileLoginOrBindingActivity.class);
                intent.putExtra("platform", platform);//	1qq2微信3微博,必传
                intent.putExtra("openId", openId);
                intent.putExtra("type", 1);
                startActivity(intent);
                finish();
            } else {
                User user = new Gson().fromJson(object.getString("object").toString(), User.class);
                //WangYuApplication.getJpushUtil().setAlias(context.getResources().getString(R.string.alias) + user.getId());
                WangYuApplication.getJpushUtil().setAliasWithTags(getResources().getString(R.string.alias) + user.getId(), JPushUtil.initTags(user));
                PreferencesUtil.setUser(context, object.getString("object").toString());
                WangYuApplication.setUser(user);
                showToast("登录成功");
                BroadcastController.sendUserChangeBroadcase(context);
                intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            }
            platform = LOGIN_WEIBO;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        BaseFragment.isLogining = false;
    }
}
