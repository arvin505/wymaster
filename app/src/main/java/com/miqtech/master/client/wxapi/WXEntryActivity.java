package com.miqtech.master.client.wxapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.appmanager.AppManager;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.CompleteTask;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.jpush.service.JPushUtil;
import com.miqtech.master.client.ui.MainActivity;
import com.miqtech.master.client.ui.MobileLoginOrBindingActivity;
import com.miqtech.master.client.ui.SubjectActivity;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.ShareToFriendsUtil;
import com.miqtech.master.client.utils.ToastUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    private String openid;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
        context = this;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {

    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        int result = 0;
        if (resp instanceof SendAuth.Resp) {
            String code;
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    result = R.string.wechat_login_success;
                    SendAuth.Resp newResp = (SendAuth.Resp) resp;
                    //获取微信传回的code
                    code = newResp.code;
                    showToast(code);
                    Map<String, String> map = new HashMap<>();
                    map.put("appid", Constant.WX_APP_ID);
                    map.put("secret", Constant.WX_APP_SECRET);
                    map.put("code", code);
                    map.put("grant_type", "authorization_code");
                    sendHttpPost(HttpConstant.GET_USER_INFO_BY_WEIXIN_LOGIN, map, HttpConstant.GET_USER_INFO_BY_WEIXIN_LOGIN);
                    showToast(result);
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL://取消
                    result = R.string.wechat_login_cancel;
                    showToast(result);
                    finish();
                    return;
                case BaseResp.ErrCode.ERR_AUTH_DENIED://拒绝
                    result = R.string.wechat_login_error;
                    showToast(result);
                    finish();
                    return;
                default:
                    break;
            }
        } else {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    result = R.string.errcode_success;
                /*if (AppManager.getAppManager().findActivity(SubjectActivity.class)) {*/
                    User user = WangYuApplication.getUser(this);
                    Map<String, String> map = new HashMap<>();
                    if (user != null) {
                        map.put("userId", user.getId());
                        map.put("token", user.getToken());
                    }
                    LogUtil.e("params", "params sssss ： " + map.toString());
                    sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AFTER_SHARE, map, HttpConstant.AFTER_SHARE);
                    //}
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    result = R.string.errcode_cancel;
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    result = R.string.errcode_deny;
                    break;
                default:
                /*result = R.string.errcode_unknown;
                User user = WangYuApplication.getUser(this);
                Map<String, String> map = new HashMap<>();
                if (user != null) {
                    map.put("userId", user.getId());
                    map.put("token", user.getToken());
                }
                sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AFTER_SHARE, map, HttpConstant.AFTER_SHARE);*/
                    result = R.string.errcode_failed;
                    break;
            }
            if (ShareToFriendsUtil.mSharePopWindow != null) {
                ShareToFriendsUtil.mSharePopWindow.dismiss();

                ShareToFriendsUtil.mSharePopWindow = null;
            }
            ToastUtil.showToastResources(result, WXEntryActivity.this);
            this.finish();
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        LogUtil.e("params", "params : " + object.toString());
        if (method.equals(HttpConstant.AFTER_SHARE)) {
            AppManager.getAppManager().removeActivity(SubjectActivity.class);
            if (object.has("extend")) {
                try {
                    String extendStr = object.getString("extend");
                    showTaskCoins(extendStr);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


        } else if (method.equals(HttpConstant.THIRD_LOGIN)) {
            toBindingOrtoMain(object);
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
        if (method.equals(HttpConstant.GET_USER_INFO_BY_WEIXIN_LOGIN)) {//因为微信登陆成功后去请求信息时返回值得字段里是没有code的，所以它请求成功后是在onfail里，而不是onsuccess
            thirdLogin(object);
        } else {
            showToast(object.toString());
        }
    }

    /**
     * 第三方登陆后绑定
     *
     * @param object
     */
    private void thirdLogin(JSONObject object) {
        try {
            if (object.has("openid")) {
                openid = object.getString("openid");
            }
            showLoading();
            Map<String, String> map = new HashMap<>();
            map.put("openId", openid);
            map.put("platform", 2 + "");//1qq2微信3微博,必传
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.THIRD_LOGIN, map, HttpConstant.THIRD_LOGIN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                intent.putExtra("platform", 2);//	1qq2微信3微博,必传
                intent.putExtra("openId", openid);
                intent.putExtra("type", 1);
                startActivity(intent);
                finish();
            } else {
                User user = new Gson().fromJson(object.getString("object").toString(), User.class);
                //WangYuApplication.getJpushUtil().setAlias(context.getResources().getString(R.string.alias) + user.getId());
                WangYuApplication.getJpushUtil().setAliasWithTags(getResources().getString(R.string.alias) + user.getId(), JPushUtil.initTags(user));
                WangYuApplication.setUser(user);
                PreferencesUtil.setUser(context, object.getString("object").toString());
                showToast("登录成功");
                BroadcastController.sendUserChangeBroadcase(context);
                intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showTaskCoins(String extendStr) {
        if (!TextUtils.isEmpty(extendStr) && (!extendStr.equals("{}"))) {
            try {
                JSONObject jsonObj = new JSONObject(extendStr);
                ArrayList<CompleteTask> tasks = new Gson().fromJson(jsonObj.getString("completeTasks"),
                        new TypeToken<List<CompleteTask>>() {
                        }.getType());
                if (tasks != null) {
                    for (int i = 0; i < tasks.size(); i++) {
                        CompleteTask currentTask = tasks.get(i);
                        int type = currentTask.getTaskType();
                        int currentTaskIdentify = currentTask.getTaskIdentify();
                        if (type == 1) {
                            switch (currentTaskIdentify) {
                                case Constant.SHARE:
                                    showCoinToast("分享     +" + currentTask.getCoin() + "金币");
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void showCoinToast(String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageCodeProject = new ImageView(context);
        imageCodeProject.setImageResource(R.drawable.coin_icon);
        toastView.addView(imageCodeProject, 0);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}