package com.miqtech.master.client.jpush.service;

import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class JPushUtil {
    public static final String PREFS_NAME = "JPUSH_EXAMPLE";
    public static final String PREFS_DAYS = "JPUSH_EXAMPLE_DAYS";
    public static final String PREFS_START_TIME = "PREFS_START_TIME";
    public static final String PREFS_END_TIME = "PREFS_END_TIME";
    public static final String KEY_APP_KEY = "JPUSH_APPKEY";
    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;
    private static final int MSG_SET_ALIAS_TAGS = 1003;
    protected static final String TAG = "JPush";
    private Context mContext;

    public JPushUtil(Context context) {
        super();
        this.mContext = context;
    }

    public static boolean isEmpty(String s) {
        if (null == s)
            return true;
        if (s.length() == 0)
            return true;
        if (s.trim().length() == 0)
            return true;
        return false;
    }

    // 校验Tag Alias 只能是数字,英文字母和中文
    public static boolean isValidTagAndAlias(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    // 取得AppKey
    public static String getAppKey(Context context) {
        Bundle metaData = null;
        String appKey = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            if (null != ai)
                metaData = ai.metaData;
            if (null != metaData) {
                appKey = metaData.getString(KEY_APP_KEY);
                if ((null == appKey) || appKey.length() != 24) {
                    appKey = null;
                }
            }
        } catch (NameNotFoundException e) {

        }
        return appKey;
    }

    // 取得版本号
    public static String GetVersion(Context context) {
        try {
            PackageInfo manager = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return manager.versionName;
        } catch (NameNotFoundException e) {
            return "Unknown";
        }
    }

    public static void showToast(final String toast, final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static String getImei(Context context, String imei) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } catch (Exception e) {
            LogUtil.e(JPushUtil.class.getSimpleName(), e.getMessage());
        }
        return imei;
    }

    /**
     * init tag
     */
    public static ArrayList<String> initTags(User user) {
        String cityCode = user.getCityCode();
        String[] modules = null;
        if (!TextUtils.isEmpty(user.getModuleInfo())) {
            modules = user.getModuleInfo().split(",");
        }

        ArrayList<String> result = new ArrayList<>();
        if (HttpConstant.SERVICE_HTTP_AREA.contains("wy")) {
            result.add(WangYuApplication.getGlobalContext().getResources().getString(R.string.test_tag));
        } else {
            result.add(WangYuApplication.getGlobalContext().getResources().getString(R.string.tag));
        }
        if (cityCode != null) {
            cityCode = cityCode.substring(0, 2) + "0000";
            if (HttpConstant.SERVICE_HTTP_AREA.contains("wy")) {
                result.add(WangYuApplication.getGlobalContext().getResources().getString(R.string.test_tag_area) + cityCode);
                if (modules != null) {
                    for (String module : modules) {
                        result.add(WangYuApplication.getGlobalContext().getResources().getString(R.string.test_tag_area_infomodule) + cityCode + "_" + module);
                    }
                }
            } else {
                result.add(WangYuApplication.getGlobalContext().getResources().getString(R.string.tag_area) + cityCode);
                if (modules != null) {
                    for (String module : modules) {
                        result.add(WangYuApplication.getGlobalContext().getResources().getString(R.string.tag_area_infomodule) + cityCode + "_" + module);
                    }
                }
            }
        }

        if (HttpConstant.SERVICE_HTTP_AREA.contains("wy")) {
            if (modules != null) {
                for (String module : modules) {
                    result.add(WangYuApplication.getGlobalContext().getResources().getString(R.string.test_tag_infomodule) +  module);
                }
            }
        } else {
            if (modules != null) {
                for (String module : modules) {
                    result.add(WangYuApplication.getGlobalContext().getResources().getString(R.string.tag_infomodule) +  module);
                }
            }
        }
        LogUtil.e("TAG", "result : " + result.toString());
        return result;
    }

    public void setTag(String tag) {
        // String tag = mContext.getResources().getString(R.string.tag);

        // 检查 tag 的有效性
        if (TextUtils.isEmpty(tag)) {
            Toast.makeText(mContext, R.string.error_tag_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        // ","隔开的多个 转换成 Set
        String[] sArray = tag.split(",");
        Set<String> tagSet = new LinkedHashSet<String>();
        for (String sTagItme : sArray) {
            if (!isValidTagAndAlias(sTagItme)) {
                Toast.makeText(mContext, R.string.error_tag_gs_empty, Toast.LENGTH_SHORT).show();
                return;
            }
            tagSet.add(sTagItme);
        }

        // 调用JPush API设置Tag
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, tagSet));
    }

    public void setAlias(String alias) {
        if (TextUtils.isEmpty(alias)) {
            Toast.makeText(mContext, R.string.error_alias_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidTagAndAlias(alias)) {
            Toast.makeText(mContext, R.string.error_tag_gs_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        // 调用JPush API设置Alias
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }


    /**
     * 设置alias 和 tag
     *
     * @param alias
     * @param tags
     */
    public void setAliasWithTags(String alias, ArrayList<String> tags) {

        if (HttpConstant.SERVICE_HTTP_AREA.contains("wy")) {
           if (alias.startsWith("member")){
               alias = alias.replace("member","test_member");
           }
        } else {
            if (alias.startsWith("test_member")){
                LogUtil.e("TAg","tag  : " + 2222222);
            }
        }
        if (!isValidTagAndAlias(alias)) {
            Toast.makeText(mContext, R.string.error_tag_gs_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < tags.size(); i++) {
            String tag = tags.get(i);
            if (!isValidTagAndAlias(tag)) {
                Toast.makeText(mContext, R.string.error_tag_gs_empty, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Bundle bundle = new Bundle();
        bundle.putString("alias", alias);
        bundle.putStringArrayList("tags", tags);
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_SET_ALIAS_TAGS;
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * 设置通知提示方式 - 基础属性
     */
    private void setStyleBasic() {
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(mContext);
        builder.statusBarDrawable = R.drawable.ic_launcher;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL; // 设置为点击后自动消失
        builder.notificationDefaults = Notification.DEFAULT_SOUND; // 设置为铃声（
        JPushInterface.setPushNotificationBuilder(1, builder);
        Toast.makeText(mContext, "Basic Builder - 1", Toast.LENGTH_SHORT).show();
    }

    /**
     * 设置通知栏样式 - 定义通知栏Layout
     */
    private void setStyleCustom() {
        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(mContext,
                R.layout.customer_notitfication_layout, R.id.icon, R.id.title, R.id.text);
        builder.layoutIconDrawable = R.drawable.ic_launcher;
        builder.developerArg0 = "developerArg2";
        JPushInterface.setPushNotificationBuilder(2, builder);
        Toast.makeText(mContext, "Custom Builder - 2", Toast.LENGTH_SHORT).show();
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    // logs = "Set tag and alias success";
                    // Log.i(TAG, logs);
                    break;
                case 6002:
                    // logs =
                    // "Failed to set alias and tags due to timeout. Try again after 60s.";
                    // Log.i(TAG, logs);
                    if (isConnected(mContext)) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 10);
                    } else {
                        // Log.i(TAG, "No network");
                    }
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    // Log.e(TAG, logs);
            }
            // showToast(logs, mContext);
        }

    };

    private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    // Log.i(TAG, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    // Log.i(TAG, logs);
                    if (isConnected(mContext)) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    } else {
                        // Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    // Log.e(TAG, logs);
            }

            //showToast(logs, mContext);
        }

    };
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    LogUtil.e(TAG, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(mContext, (String) msg.obj, null, mAliasCallback);
                    break;
                case MSG_SET_TAGS:
                    LogUtil.e(TAG, "Set tags in handler.");
                    JPushInterface.setAliasAndTags(mContext, null, (Set<String>) msg.obj, mTagsCallback);
                    break;
                case MSG_SET_ALIAS_TAGS:
                    LogUtil.e(TAG, "Set both.");
                    Bundle data = msg.getData();
                    String alias = data.getString("alias");
                    ArrayList<String> tags = data.getStringArrayList("tags");
                    Set<String> tagSet = new HashSet<>();
                    for (String tag : tags) {
                        if (tag.contains("_area_info_module_nu0000")) {
                            tag = tag.replace("_area_info_module_nu0000", "");
                        }
                        tagSet.add(tag);
                    }
                    LogUtil.e("TASG", "result : " + tagSet.toString());
                    LogUtil.e("TASG","result : s  " + alias);
                    JPushInterface.setAliasAndTags(mContext, alias, tagSet, mTagsCallback);
                default:
                    LogUtil.e(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };
}
