package com.miqtech.master.client.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.Config;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tencent.mm.sdk.constants.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;

/**
 * @author wuxn
 */
public class StartActivity extends BaseActivity implements View.OnClickListener, ImageLoadingListener {
    private MyHandler myHandler;
    private Timer timer;
    private Context context;

    private static int DELAY = 1;

    private int recLen = 3;

    @Bind(R.id.tvSkip)
    TextView tvSkip;
    @Bind(R.id.ivAd)
    ImageView ivAd;
    @Bind(R.id.ivWy)
    ImageView ivWy;

    private Config mConfig = new Config();

    Bundle data;

    private boolean isJumpFromWeb;

    protected void init() {
        setContentView(R.layout.activity_start);
        context = this;
        myHandler = new MyHandler();

        initView();
        initData();
        timeOut();

        //       Log.i("umeng",getDeviceInfo(context));
    }

    /**
     * 获取href中的字符串
     */

    private void getHref() {
        Intent intent = getIntent();
        String scheme = intent.getScheme();
        Uri uri = intent.getData();
        System.out.println("scheme:" + scheme);
        if (uri != null) {
            String dataString = intent.getDataString();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvSkip) {
            Intent intent = new Intent();
            if (isFirstStartOrUpdateVersion()) {
                PreferencesUtil.saveStartInfo(StartActivity.this);
                intent.setClass(StartActivity.this, GuideActivity.class);
                startActivity(intent);
            } else {
                intent.setClass(context, MainActivity.class);
                if (mConfig != null && mConfig.getUpdate_msg() != null) {
                    intent.putExtra("versionInfo", mConfig.getUpdate_msg());
                }
                if (!isJumpFromWeb) {
                    startActivity(intent);
                } else {
                    Intent extraIntent = new Intent();
                    extraIntent.setClassName(StartActivity.this, data.getString("action"));
                    String id = data.getString("id");
                    String key = data.getString("key");
                    extraIntent.putExtra(key, key.contains("activityId") ? Integer.parseInt(id) : id);
                    startActivities(new Intent[]{intent, extraIntent});
                }
            }
            timer.cancel();
            onBackPressed();
        } else if (v.getId() == R.id.ivAd) {
            String strConfig = PreferencesUtil.getConfig(this);
            mConfig = new Gson().fromJson(strConfig, Config.class);
            if (mConfig != null && mConfig.getStartup_ad() != null) {
                int type = mConfig.getStartup_ad().getType();
                String target = mConfig.getStartup_ad().getTarget();
                if (type == 1) {
                    Intent mainIntent = new Intent();
                    mainIntent.setClass(this, MainActivity.class);
                    Intent h5Intent = new Intent();
                    h5Intent.setClass(this, SubjectActivity.class);
                    h5Intent.putExtra("url", target);
                    h5Intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.START_AD);
                    Intent[] intents = new Intent[]{mainIntent, h5Intent};
                    startActivities(intents);
                    timer.cancel();
                } else if (type == 2) {
                    if (target.equals("10")) {
                        Intent mainIntent = new Intent();
                        mainIntent.setClass(this, MainActivity.class);
                        Intent warIntent = new Intent();
                        warIntent.setClass(this, OfficalEventActivity.class);
                        warIntent.putExtra("matchId", mConfig.getStartup_ad().getTarget_id());
                        Intent[] intents = new Intent[]{mainIntent, warIntent};
                        startActivities(intents);
                        timer.cancel();
                    } else if (target.equals("11")) {
                        Intent mainIntent = new Intent();
                        mainIntent.setClass(this, MainActivity.class);
                        Intent recreationIntent = new Intent();
                        recreationIntent.setClass(this, RecreationMatchDetailsActivity.class);
                        recreationIntent.putExtra("id", mConfig.getStartup_ad().getTarget_id());
                        Intent[] intents = new Intent[]{mainIntent, recreationIntent};
                        startActivities(intents);
                        timer.cancel();
                    } else if (target.equals("12")) {
                        Intent mainIntent = new Intent();
                        mainIntent.setClass(this, MainActivity.class);
                        Intent yuezhnIntent = new Intent();
                        yuezhnIntent.setClass(this, YueZhanDetailsActivity.class);
                        yuezhnIntent.putExtra("id", mConfig.getStartup_ad().getTarget_id());
                        Intent[] intents = new Intent[]{mainIntent, yuezhnIntent};
                        startActivities(intents);
                        timer.cancel();
                    }
                }
            }

        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (recLen > 0) {
                tvSkip.setText(recLen + "跳过");
            } else {
                timer.cancel();
                Intent intent = new Intent();
                if (isFirstStartOrUpdateVersion()) {
                    PreferencesUtil.saveStartInfo(StartActivity.this);
                    intent.setClass(StartActivity.this, GuideActivity.class);
                } else {
                    intent.setClass(context, MainActivity.class);
                    if (mConfig != null && mConfig.getUpdate_msg() != null) {
                        intent.putExtra("versionInfo", mConfig.getUpdate_msg());
                    }
                }
                if (!isJumpFromWeb) {
                    startActivity(intent);
                } else {
                    Intent extraIntent = new Intent();
                    extraIntent.setClassName(StartActivity.this, data.getString("action"));
                    String id = data.getString("id");
                    String key = data.getString("key");
                    extraIntent.putExtra(key, key.contains("activityId") ? Integer.parseInt(id) : id);

                    startActivities(new Intent[]{intent, extraIntent});
                }
                finish();
            }

        }
    }

    private void timeOut() {
        timer = new Timer();
        timer.schedule(task, 1000, 1000);
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            recLen--;
            Message message = new Message();
            myHandler.sendEmptyMessage(0);
        }
    };

    protected void initView() {
        super.initView();
        data = getIntent().getBundleExtra("data");
        if (data != null) {
            isJumpFromWeb = true;
        }

        ButterKnife.bind(this);
        tvSkip.getBackground().setAlpha(204);
        tvSkip.setOnClickListener(this);
        ivAd.setOnClickListener(this);
        String strConfig = PreferencesUtil.getConfig(this);
        if (!TextUtils.isEmpty(strConfig)) {
            mConfig = new Gson().fromJson(strConfig, Config.class);
            if (mConfig != null && mConfig.getStartup_ad() != null) {
                AsyncImage.loadNoDefaultPhoto(this, HttpConstant.SERVICE_UPLOAD_AREA + mConfig.getStartup_ad().getImg_url(), this);
                //  AsyncImage.loadNetPhoto(this,HttpConstant.SERVICE_UPLOAD_AREA + mConfig.getStartup_ad().getImg_url());
            }
        }
        String channelName = WangYuApplication.getAppMetaData(context, "UMENG_CHANNEL");
        if (channelName.equals("360手机助手")) {
            ivWy.setImageDrawable(getResources().getDrawable(R.drawable.assistant360));
        } else if (channelName.equals("uc")) {
            ivWy.setImageDrawable(getResources().getDrawable(R.drawable.ppassitant));
        }
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        if (Build.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                        1);
            }
        }
        getWyConfig();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
//            WangYuApplication.USER_AGENT = WangYuApplication.getGlobalContext().getUserAgent();
//        }
//        getConfig();
//    }

    private void getWyConfig() {
        LogUtil.e(TAG, "----------------agent--" + WangYuApplication.USER_AGENT);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.PROCESSED_CONFIG, null, HttpConstant.PROCESSED_CONFIG);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        LogUtil.e("obj", "object :  " + object);
        if (HttpConstant.PROCESSED_CONFIG.equals(method)) {
            try {
                if (object.has("object")) {
                    String jsonObj = object.getString("object");
                    LogUtil.e("obj", "jsonobj :  " + jsonObj);
                    mConfig = GsonUtil.getBean(jsonObj, Config.class);
                    PreferencesUtil.setConfig(this, jsonObj);
                    if (mConfig != null && mConfig.getStartup_ad() != null) {
                        AsyncImage.loadNoDefaultPhoto(this, HttpConstant.SERVICE_UPLOAD_AREA + mConfig.getStartup_ad().getImg_url(), this);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isFirstStartOrUpdateVersion() {
        int localVersionCode = PreferencesUtil.getStartInfo(this);
        if (localVersionCode == 0) {
            return true;
        } else if (localVersionCode < PreferencesUtil.getVersionCode(this)) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    public void onLoadingStarted(String s, View view) {

    }

    @Override
    public void onLoadingFailed(String s, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
        setAdViewHeight(bitmap);
    }

    @Override
    public void onLoadingCancelled(String s, View view) {

    }

    private void setAdViewHeight(Bitmap bitmap) {
        //图片宽高
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        //屏幕宽度
        int screenWidth = WangYuApplication.WIDTH;
        float scale = (float) screenWidth / (float) bitmapWidth;
        int viewHeight = (int) (bitmapHeight * scale);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(screenWidth, viewHeight);
        ivAd.setLayoutParams(lp);
        ivAd.setImageBitmap(bitmap);
    }


    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
