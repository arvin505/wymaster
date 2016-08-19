package com.miqtech.master.client.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.CompleteTask;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微博分享页面
 * Created by Administrator on 2016/6/13.
 */
public class ShareWeiboActivity extends BaseActivity implements IWeiboHandler.Response {

    private Context mContext;

    /**
     * 新浪
     */
    private Oauth2AccessToken mAccessToken;
    private IWeiboShareAPI weiboApi;
    private static final String PREFERENCES_NAME = "com_weibo_sdk_android";
    private AuthInfo mAuthInfo;
    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler mSsoHandler;

    private static final String KEY_UID = "uid";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_EXPIRES_IN = "expires_in";


    /**
     * 分享内容
     */
    private String shareTitle;
    private String shareContent;
    private String shareUrl;
    private String imgpath;
    private Bitmap shareWeiboBitmap;
    private Bitmap downloadBmp;

    private WeiboMultiMessage weiboMessage;
    private SendMultiMessageToWeiboRequest request;
    private String weiboToken;

    private byte[] bis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_weibo);
        mContext = this;
        shareTitle = getIntent().getStringExtra("shareTitle");
        shareContent = getIntent().getStringExtra("shareContent");
        shareUrl = getIntent().getStringExtra("shareUrl");
        imgpath = getIntent().getStringExtra("imageUrl");

        bis = getIntent().getByteArrayExtra("bitmap");
        if (bis != null && bis.length > 0) {
            shareWeiboBitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
        }
        shareWeibo();
        // 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
        if (savedInstanceState != null) {
            weiboApi.handleWeiboResponse(getIntent(), this);
        }
    }

    private void shareWeibo() {
        // 创建微博分享接口实例
        weiboApi = WeiboShareSDK.createWeiboAPI(this, Constant.WB_APP_KEY);
        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
        weiboApi.registerApp();
        mAuthInfo = new AuthInfo(mContext, Constant.WB_APP_KEY, Constant.WB_REDIRECTURL, Constant.WB_APP_SCOPE);
        mSsoHandler = new SsoHandler(this, mAuthInfo);

        Oauth2AccessToken accessToken = readAccessToken(mContext);
        if (accessToken.isSessionValid()) {
            reqMsg(accessToken.getToken());
        } else {
            mSsoHandler.authorize(new AuthListener());
        }
    }

    public void reqMsg(final String token) {
        weiboToken = token;
        if (shareWeiboBitmap != null) {//微博分享的是图片，后面代码就不执行
            Oauth2AccessToken accessToken = readAccessToken(mContext);
            if (!accessToken.getToken().equals("")) {
                weiboMessage = new WeiboMultiMessage();//初始化微博的分享消息
                ImageObject imageObject = new ImageObject();
                TextObject textObject = new TextObject();
                imageObject.setImageObject(shareWeiboBitmap);
                textObject.text = "网娱大师战队二维码邀请";
                weiboMessage.imageObject = imageObject;
                weiboMessage.textObject = textObject;
                request = new SendMultiMessageToWeiboRequest();
                request.transaction = String.valueOf(System.currentTimeMillis());
                request.multiMessage = weiboMessage;
                weiboApi.sendRequest((Activity) mContext, request, mAuthInfo, accessToken.getToken(), new AuthListener());
            } else {
                mSsoHandler.authorize(new AuthListener());
            }
            return;
        }
        loadingImage();
    }

    Handler handler = new Handler() {
        Bitmap bmp;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 微博数据的message对象
            TextObject textobj = new TextObject();
            textobj.text = "@" + shareTitle + ":\t" + shareContent + shareUrl;
            weiboMessage = new WeiboMultiMessage();
            if (msg.obj != null) {
                bmp = (Bitmap) msg.obj;
                if (bmp != null) {
                    ImageObject imageObject = new ImageObject();
                    imageObject.setImageObject(bmp);
                    weiboMessage.imageObject = imageObject;
                }
            }
            // 2. 初始化从第三方到微博的消息请求
            request = new SendMultiMessageToWeiboRequest();
            // 用transaction唯一标识一个请求
            request.transaction = String.valueOf(System.currentTimeMillis());
            request.multiMessage = weiboMessage;
            weiboMessage.textObject = textobj;
            // 3. 发送请求消息到微博，唤起微博分享界面
            weiboApi.sendRequest((Activity) mContext, request, mAuthInfo, weiboToken, new AuthListener());
        }
    };

    private void loadingImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    URL imageUrl = new URL(imgpath);
                    HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                    if (conn.getResponseCode() == 200) {
                        InputStream inputStream = conn.getInputStream();
                        downloadBmp = BitmapFactory.decodeStream(inputStream);
                        msg.obj = downloadBmp;
                        handler.sendMessage(msg);
                    } else {
                        downloadBmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_img);
                        msg.obj = downloadBmp;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    downloadBmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_img);
                    msg.obj = downloadBmp;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    /**
     * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
     * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
     * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
     * SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            String code;
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                if (values.toString().indexOf("code") >= 0 && values.toString().indexOf("msg") >= 0) {
                    code = values.getString("code");
                    if (code.equals("0")) {//分享成功
                        loadData();
                    }
                } else {//这个表明是登陆
                    // 保存 Token 到 SharedPreferences
                    writeAccessToken(mContext, mAccessToken);
                    Toast.makeText(mContext, "授权成功", Toast.LENGTH_SHORT).show();
                    reqMsg(mAccessToken.getToken());
                }
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                code = values.getString("code");
                String message = "授权失败";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(mContext, "取消分享", Toast.LENGTH_LONG).show();
            onBackPressed();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(mContext, "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }

    /**
     * 保存 Token 对象到 SharedPreferences。
     *
     * @param context 应用程序上下文环境
     * @param token   Token 对象
     */
    public void writeAccessToken(Context context, Oauth2AccessToken token) {
        if (null == context || null == token) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_UID, token.getUid());
        editor.putString(KEY_ACCESS_TOKEN, token.getToken());
        editor.putLong(KEY_EXPIRES_IN, token.getExpiresTime());
        editor.commit();
    }

    /**
     * 从 SharedPreferences 读取 Token 信息。
     *
     * @param context 应用程序上下文环境
     * @return 返回 Token 对象
     */
    public Oauth2AccessToken readAccessToken(Context context) {
        if (null == context) {
            return null;
        }
        Oauth2AccessToken token = new Oauth2AccessToken();
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        token.setUid(pref.getString(KEY_UID, ""));
        token.setToken(pref.getString(KEY_ACCESS_TOKEN, ""));
        token.setExpiresTime(pref.getLong(KEY_EXPIRES_IN, 0));
        return token;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        weiboApi.handleWeiboResponse(intent, this);
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        if (baseResponse != null) {
            switch (baseResponse.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    showToast(getResources().getString(R.string.weibo_share_success));
                    loadData();
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    showToast(getResources().getString(R.string.weibo_share_cancel));
                    onBackPressed();
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    showToast(getResources().getString(R.string.weibo_share_fail));
                    onBackPressed();
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadBmp != null) {
            downloadBmp.recycle();
        }

        if (shareWeiboBitmap != null) {
            shareWeiboBitmap.recycle();
        }
    }

    private void loadData() {
        User user = WangYuApplication.getUser(mContext);
        if (user == null) {
            onBackPressed();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AFTER_SHARE, params, HttpConstant.AFTER_SHARE);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        if (object.has("extend")) {
            try {
                String extendStr = object.getString("extend");
                showTaskCoins(extendStr);
                onBackPressed();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        showToast("网络错误");
        onBackPressed();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        showToast("网络错误");
        onBackPressed();
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
        Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageCodeProject = new ImageView(mContext);
        imageCodeProject.setImageResource(R.drawable.coin_icon);
        toastView.addView(imageCodeProject, 0);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
