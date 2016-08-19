package com.miqtech.master.client.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
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
import com.miqtech.master.client.http.RequestUtil;
import com.miqtech.master.client.http.ResponseListener;
import com.miqtech.master.client.simcpux.Util;
import com.miqtech.master.client.ui.ShareWeiboActivity;
import com.miqtech.master.client.view.ExpertMorePopupWindow;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareToFriendsUtil implements ResponseListener {

    private final int THUMB_SIZE = 90;

    private final int THUMB_SIZE_WIDTH = 90;
    private final int THUMB_SIZE_HEIGHT = 140;

    private Context mContext;

    /**
     * 新浪
     */
    private Oauth2AccessToken mAccessToken;
    private IWeiboShareAPI weiboApi;
    private static final String PREFERENCES_NAME = "com_weibo_sdk_android";
    private AuthInfo mWeiboAuth;

    private static final String KEY_UID = "uid";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_EXPIRES_IN = "expires_in";

    /**
     * 微信
     */
    private IWXAPI api;
    /**
     * QQ
     */
    private Tencent mTencent;
    private static boolean sIsWXAppInstalledAndSupported;

    public Tencent getmTencent(Context context) {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(Constant.QQ_APP_ID, context);
        }
        return mTencent;
    }

//    /**
//     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
//     */
//    private SsoHandler mSsoHandler;
//
//    public SsoHandler getmSsoHandler() {
//        return mSsoHandler;
//    }
//
//    public void setmSsoHandler(SsoHandler mSsoHandler) {
//        this.mSsoHandler = mSsoHandler;
//    }

    /**
     * 分享内容
     */
    private String shareTitle;
    private String shareContent;
    private String shareUrl;
    private String imgpath;
    private Bitmap shareWeiboBitmap;
    public static Bitmap shareDownloadBmp;

    public static ExpertMorePopupWindow mSharePopWindow;

    public void setItemClick(View.OnClickListener itemOnClick) {
        this.mSharePopWindow.setOnItemClick(itemOnClick);
    }

    public ShareToFriendsUtil(Context _context) {
        this.mContext = _context;
//        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
//        this.mSsoHandler = new SsoHandler((Activity) _context, getAuthInfo(_context));

    }

    public ShareToFriendsUtil(Context _context, ExpertMorePopupWindow popwin) {
        this.mContext = _context;
        this.mSharePopWindow = popwin;
//        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
//        this.mSsoHandler = new SsoHandler((Activity) _context, getAuthInfo(_context));
    }


//    public void showPopuWindow(View parentView) {
//        this.mSharePopWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
//    }

    /**
     * 分享微信好友/朋友圈
     */
    public void shareWyByWXFriend(final String shareTitle, final String shareContent, final String shareUrl,
                                  String imageUrl, final int flag) {
        if (TextUtils.isEmpty(shareUrl)) {
            ToastUtil.showToast("分享信息未获取到", mContext);
            return;
        }
        api = WXAPIFactory.createWXAPI(mContext, Constant.WX_APP_ID, false);
        api.registerApp(Constant.WX_APP_ID);
        if (!isWXAppInstalledAndSupported(mContext, api)) {
            ToastUtil.showToast("微信客户端未安装，请确认", mContext);
            if (mSharePopWindow != null) {
                mSharePopWindow.dismiss();
            }
            return;
        }
        this.shareTitle = shareTitle;
        this.shareContent = shareContent;
        this.shareUrl = shareUrl;
        Handler handler = new Handler() {
            private Bitmap bmp;

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                bmp = (Bitmap) msg.obj;
                final WXMediaMessage mediaMessage = new WXMediaMessage();
                if (msg.obj != null) {
                    bmp = (Bitmap) msg.obj;
                    if (bmp != null) {
                        mediaMessage.thumbData = bmpToByteArray(
                                Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true), true); // 设置缩略图;
                    }
                }
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = shareUrl;
                mediaMessage.mediaObject = webpage;
                mediaMessage.title = shareTitle;
                mediaMessage.description = shareContent;
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = "urls" + String.valueOf(System.currentTimeMillis());
                req.message = mediaMessage;
                req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
                api.sendReq(req);
            }
        };
        loadingImage(imageUrl, handler);
        if (mSharePopWindow != null) {
            mSharePopWindow.dismiss();
        }
    }

    /**
     * 邀请微信好友/朋友圈
     */
    public void shareInvateByWXFriend(final String shareTitle, final String shareContent, final String shareUrl,
                                      Bitmap bmp, final int flag) {
        if (TextUtils.isEmpty(shareUrl)) {
            ToastUtil.showToast("分享信息未获取到", mContext);
            return;
        }
        api = WXAPIFactory.createWXAPI(mContext, Constant.WX_APP_ID, false);
        api.registerApp(Constant.WX_APP_ID);
        if (!isWXAppInstalledAndSupported(mContext, api)) {
            ToastUtil.showToast("微信客户端未安装，请确认", mContext);
            if (mSharePopWindow != null) {
                mSharePopWindow.dismiss();
            }
            return;
        }
        this.shareTitle = shareTitle;
        this.shareContent = shareContent;
        this.shareUrl = shareUrl;
        // mContext.getResources().getDrawable(R.)

        final WXMediaMessage mediaMessage = new WXMediaMessage();
        if (bmp != null) {
            mediaMessage.thumbData = bmpToByteArray(Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true), true); // 设置缩略图;
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareUrl;
        mediaMessage.mediaObject = webpage;
        mediaMessage.title = shareTitle;
        mediaMessage.description = shareContent;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "urls" + String.valueOf(System.currentTimeMillis());
        req.message = mediaMessage;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

    /**
     * 分享图片
     *
     * @param bitmap
     * @param flag
     */
    public void shareInviteImageByWXFriend(Bitmap bitmap, final int flag) {
        api = WXAPIFactory.createWXAPI(mContext, Constant.WX_APP_ID, false);
        api.registerApp(Constant.WX_APP_ID);
        if (!isWXAppInstalledAndSupported(mContext, api)) {
            ToastUtil.showToast("微信客户端未安装，请确认", mContext);
            if (mSharePopWindow != null) {
                mSharePopWindow.dismiss();
            }
            return;
        }
        WXImageObject imgObj = new WXImageObject(bitmap);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        //设置缩略图
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE_WIDTH, THUMB_SIZE_HEIGHT, true);
        bitmap.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
        //构造一个REQ
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "urls" + String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);
    }

    public void shareRedbagWyByWXFriend(final String shareTitle, final String shareContent, final String shareUrl,
                                        String imageUrl, final int flag) {
        if (TextUtils.isEmpty(shareUrl)) {
            ToastUtil.showToast("分享信息未获取到", mContext);
            return;
        }
        api = WXAPIFactory.createWXAPI(mContext, Constant.WX_APP_ID, false);
        api.registerApp(Constant.WX_APP_ID);
        if (!isWXAppInstalledAndSupported(mContext, api)) {
            ToastUtil.showToast("微信客户端未安装，请确认", mContext);
            if (mSharePopWindow != null) {
                mSharePopWindow.dismiss();
            }
            return;
        }
        this.shareTitle = shareTitle;
        this.shareContent = shareContent;
        this.shareUrl = shareUrl;
        Handler handler = new Handler() {
            private Bitmap bmp;

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                bmp = (Bitmap) msg.obj;
                final WXMediaMessage mediaMessage = new WXMediaMessage();
                if (msg.obj != null) {
                    bmp = (Bitmap) msg.obj;
                    if (bmp != null) {
                        mediaMessage.thumbData = bmpToByteArray(
                                Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true), true); // 设置缩略图;
                    }
                }
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = shareUrl;
                mediaMessage.mediaObject = webpage;
                mediaMessage.title = shareTitle;
                mediaMessage.description = shareContent;
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = "urls" + String.valueOf(System.currentTimeMillis());
                req.message = mediaMessage;
                req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
                api.sendReq(req);
            }
        };
        loadingRedbagImage(imageUrl, handler);
        if (mSharePopWindow != null) {
            mSharePopWindow.dismiss();
        }
    }

    private static boolean isWXAppInstalledAndSupported(Context context, IWXAPI api) {
        // LogOutput.d(TAG, "isWXAppInstalledAndSupported");
        sIsWXAppInstalledAndSupported = api.isWXAppInstalled() && api.isWXAppSupportAPI();
        return sIsWXAppInstalledAndSupported;
    }

    /**
     * 分享微博
     */
    public void shareBySina(String shareTitle, String shareContent, String shareUrl, String imageUrl) {
        Bundle bundle = new Bundle();
        bundle.putString("shareTitle", shareTitle);
        bundle.putString("shareContent", shareContent);
        bundle.putString("shareUrl", shareUrl);
        bundle.putString("imageUrl", imageUrl);
        Intent intent = new Intent(mContext, ShareWeiboActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);

//        if (TextUtils.isEmpty(shareUrl)) {
//            ToastUtil.showToast("分享信息未获取到", mContext);
//            return;
//        }
//        this.shareTitle = shareTitle;
//        this.shareContent = shareContent;
//        this.shareUrl = shareUrl;
//        this.imgpath = imageUrl;
//
//        weiboApi = getIWeiApiInstance(mContext);
//        weiboApi.registerApp();
//        Oauth2AccessToken accessToken = readAccessToken(mContext);
//        if (!accessToken.getToken().equals("")) {
//            reqMsg(accessToken.getToken());
//        } else {
//            mSsoHandler.authorize(new AuthListener());
//        }
        if (mSharePopWindow != null) {
            mSharePopWindow.dismiss();
        }
    }


    public void shareImageByWeibo(Bitmap bitmap) {
        if (bitmap == null) {
            ToastUtil.showToast("分享信息未获取到", mContext);
            return;
        }
        Intent intent = new Intent(mContext, ShareWeiboActivity.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, baos);
        byte[] bitmapByte = baos.toByteArray();
        intent.putExtra("bitmap", bitmapByte);
        mContext.startActivity(intent);

//        this.shareWeiboBitmap = bitmap;
//        weiboApi = getIWeiApiInstance(mContext);
//        weiboApi.registerApp();
//        Oauth2AccessToken accessToken = readAccessToken(mContext);
//        if (!accessToken.getToken().equals("")) {
//            WeiboMultiMessage weiboMessage = new WeiboMultiMessage();//初始化微博的分享消息
//            mWeiboAuth = new AuthInfo(mContext, Constant.WB_APP_KEY, Constant.WB_REDIRECTURL, Constant.WB_APP_SCOPE);
//            ImageObject imageObject = new ImageObject();
//            TextObject textObject = new TextObject();
//            imageObject.setImageObject(bitmap);
//            textObject.text = "网娱大师战队二维码邀请";
//            weiboMessage.imageObject = imageObject;
//            weiboMessage.textObject = textObject;
//            SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
//            request.transaction = String.valueOf(System.currentTimeMillis());
//            request.multiMessage = weiboMessage;
//            weiboApi.sendRequest((Activity) mContext, request, mWeiboAuth, accessToken.getToken(), new AuthListener());
//        } else {
//            mSsoHandler.authorize(new AuthListener());
//        }
        if (mSharePopWindow != null) {
            mSharePopWindow.dismiss();
        }
    }

    /**
     * QQ分享
     **/
    public void shareByQQ(String shareTitle, String shareContent, String shareUrl, String imageUrl) {
        if (TextUtils.isEmpty(shareUrl)) {
            ToastUtil.showToast("分享信息未获取到", mContext);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("title", shareTitle);
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        bundle.putString("targetUrl", shareUrl);
        bundle.putString("summary", shareContent);
        bundle.putString("appName", mContext.getPackageName());
        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, mContext);
        mTencent.shareToQQ((Activity) mContext, bundle, qqShareListener);
        if (mSharePopWindow != null) {
            mSharePopWindow.dismiss();
        }
    }

    public void shareImageByQQ(String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            ToastUtil.showToast("分享信息未获取到", mContext);
            return;
        }
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl);
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        mTencent = Tencent.createInstance(Constant.QQ_APP_ID, mContext);
        mTencent.shareToQQ((Activity) mContext, params, qqShareListener);
        if (mSharePopWindow != null) {
            mSharePopWindow.dismiss();
        }
    }

    IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {
            ToastUtil.showToast("取消分享到QQ", mContext);
            if (mSharePopWindow != null) {
                mSharePopWindow.dismiss();
            }
        }

        @Override
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
            ToastUtil.showToast("分享到QQ成功", mContext);
            sendShareHttp();
            if (mSharePopWindow != null) {
                mSharePopWindow.dismiss();
            }
        }

        @Override
        public void onError(UiError e) {
            // TODO Auto-generated method stub
            ToastUtil.showToast("分享到QQ失败" + e.errorMessage + " 错误码" + e.errorCode, mContext);
            if (mSharePopWindow != null) {
                mSharePopWindow.dismiss();
            }
        }
    };

    public void reqMsg(final String token) {
        if (shareWeiboBitmap != null) {//微博分享的是图片，后面代码就不执行
            Oauth2AccessToken accessToken = readAccessToken(mContext);
            if (!accessToken.getToken().equals("")) {
                WeiboMultiMessage weiboMessage = new WeiboMultiMessage();//初始化微博的分享消息
                mWeiboAuth = new AuthInfo(mContext, Constant.WB_APP_KEY, Constant.WB_REDIRECTURL, Constant.WB_APP_SCOPE);
                ImageObject imageObject = new ImageObject();
                TextObject textObject = new TextObject();
                imageObject.setImageObject(shareWeiboBitmap);
                textObject.text = "网娱大师战队二维码邀请";
                weiboMessage.imageObject = imageObject;
                weiboMessage.textObject = textObject;
                SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
                request.transaction = String.valueOf(System.currentTimeMillis());
                request.multiMessage = weiboMessage;
                weiboApi.sendRequest((Activity) mContext, request, mWeiboAuth, accessToken.getToken(), new AuthListener());
            } else {
//                mSsoHandler.authorize(new AuthListener());
            }
            return;
        }

        Handler handler = new Handler() {
            private Bitmap bmp;

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // 微博数据的message对象
                TextObject textobj = new TextObject();
                textobj.text = "@" + shareTitle + ":\t" + shareContent + shareUrl;
                final WeiboMultiMessage multmess = new WeiboMultiMessage();
                if (msg.obj != null) {
                    bmp = (Bitmap) msg.obj;
                    if (bmp != null) {
                        ImageObject imageObject = new ImageObject();
                        imageObject.setImageObject(bmp);
                        multmess.imageObject = imageObject;
                    }
                }
                // 2. 初始化从第三方到微博的消息请求
                SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
                // 用transaction唯一标识一个请求
                request.transaction = String.valueOf(System.currentTimeMillis());
                request.multiMessage = multmess;
                multmess.textObject = textobj;
                // 3. 发送请求消息到微博，唤起微博分享界面
                weiboApi.sendRequest((Activity) mContext, request, mWeiboAuth, token, new AuthListener());
            }
        };
        loadingImage(imgpath, handler);
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
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(mContext, "取消分享", Toast.LENGTH_LONG).show();
            if (mSharePopWindow != null) {
                mSharePopWindow.dismiss();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(mContext, "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
            if (mSharePopWindow != null) {
                mSharePopWindow.dismiss();
            }
        }
    }

    public byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public IWeiboShareAPI getIWeiApiInstance(Context mcontext) {
        if (weiboApi == null)
            weiboApi = WeiboShareSDK.createWeiboAPI(mcontext, Constant.WB_APP_KEY);
        return weiboApi;
    }

    public AuthInfo getAuthInfo(Context mcontext) {
        if (mWeiboAuth == null)
            // 初始化微博对象
            mWeiboAuth = new AuthInfo(mContext, Constant.WB_APP_KEY, Constant.WB_REDIRECTURL, Constant.WB_APP_SCOPE);
        return mWeiboAuth;
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
        Editor editor = pref.edit();
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

    /**
     * 清空 SharedPreferences 中 Token信息。
     *
     * @param context 应用程序上下文环境
     */
    public void clear(Context context) {
        if (null == context) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    private void loadingImage(final String imagePath, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    URL imageUrl = new URL(imagePath);
                    HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                    // Log.i("imageurl",
                    // "imageurl---"+imagePath+"conn.getResponseCode()"+conn.getResponseCode());
                    if (conn.getResponseCode() == 200) {
                        InputStream inputStream = conn.getInputStream();
                        shareDownloadBmp = BitmapFactory.decodeStream(inputStream);
                        msg.obj = shareDownloadBmp;
                        handler.sendMessage(msg);
                    } else {
                        shareDownloadBmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_img);
                        msg.obj = shareDownloadBmp;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Log.i("imageurl", "imageurl---"+e.getMessage());
                    shareDownloadBmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_img);
                    msg.obj = shareDownloadBmp;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    private void loadingRedbagImage(final String imagePath, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    URL imageUrl = new URL(imagePath);
                    HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                    // Log.i("imageurl",
                    // "imageurl---"+imagePath+"conn.getResponseCode()"+conn.getResponseCode());
                    if (conn.getResponseCode() == 200) {
                        InputStream inputStream = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                    } else {
                        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.redbag_shrae);
                        msg.obj = bmp;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Log.i("imageurl", "imageurl---"+e.getMessage());
                    Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.redbag_shrae);
                    msg.obj = bmp;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    public RequestUtil requestUtil;
    Map<String, String> params = new HashMap<>();


    private void sendShareHttp() {
        requestUtil = RequestUtil.getInstance();
        String shareTask = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AFTER_SHARE;
        User user = WangYuApplication.getUser(mContext);
        if (user == null) {
            return;
        }
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        requestUtil.excutePostRequest(shareTask, params, this, HttpConstant.AFTER_SHARE);
        LogUtil.e("params", "params : " + params.toString());
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        //具体业务子类实现
        LogUtil.e("params", "params : " + object.toString());
        if (object.has("extend")) {
            try {
                String extendStr = object.getString("extend");
                showTaskCoins(extendStr);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        ToastUtil.showToast("请求错误", mContext);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        if (object.has("result")) {
            try {
                ToastUtil.showToast(object.getString("result"), mContext);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageCodeProject = new ImageView(mContext);
        imageCodeProject.setImageResource(R.drawable.coin_icon);
        toastView.addView(imageCodeProject, 0);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}

