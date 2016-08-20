package com.miqtech.master.client.jpush.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.miqtech.master.client.appmanager.AppManager;
import com.miqtech.master.client.ui.MainActivity;
import com.miqtech.master.client.ui.MyMessageActivity;
import com.miqtech.master.client.ui.RewardActivity;
import com.miqtech.master.client.ui.SubjectActivity;
import com.miqtech.master.client.ui.fragment.FragmentMyMain;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    private final static String packageName = "com.miqtech.master.client";
    private String key;
    private PushType pt = new PushType();
    int msgType = -1;//消息类型

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        LogUtil.e(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
        try {
            if (extras != null) {
                pt.setJsonObject(new JSONObject(extras));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            LogUtil.e(TAG, "[MyReceiver] 接收Registration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            LogUtil.e(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            LogUtil.e(TAG, "[MyReceiver] 接收到推送下来的通知");
            if (extras != null) {
                if (Utils.isRunningForeground(context)) {
                    toStartActivity(context, AppManager.getAppManager().currentActivity().getClass(), alert);
                }
            }
            @SuppressWarnings("unused")
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            /** 订单推送 **/
            int statue = isStartActivity(context, MainActivity.class);
            switch (pt.getPushType()) {
                case PushType.SYS_ID:
                    PreferencesUtil.saveSysStatue(context, true);
                    msgType = MyMessageActivity.Type_System;
                    break;
                case PushType.REDBAG_ID:
                    PreferencesUtil.saveRedbagStatue(context, true);
                    msgType = MyMessageActivity.Type_System;
                    break;
                case PushType.RESERVATION_ID:
                    PreferencesUtil.saveReserStatue(context, true);
                    msgType = MyMessageActivity.Type_Order;
                    break;
                case PushType.YUEZHAN_ID:
                    PreferencesUtil.saveYuezhanStatue(context, true);
                    msgType = MyMessageActivity.Type_Activites;
                    break;
                case PushType.NETBAR_ID:
                    PreferencesUtil.saveNetbarStatue(context, true);
                    break;
                case PushType.PAY_ID:
                    PreferencesUtil.savePayStatue(context, true);
                    msgType = MyMessageActivity.Type_Order;
                    break;
                case PushType.MATCH_ID:
                    PreferencesUtil.saveMatchStatue(context, true);
                    msgType = MyMessageActivity.Type_Activites;
                    break;
                case PushType.WEEK_REDBAG_ID:
                    PreferencesUtil.saveWeekRedbagPush(context, true);
                    msgType = MyMessageActivity.Type_System;
                case PushType.COMMENT_ID:
                    PreferencesUtil.saveWeekRedbagPush(context, true);
                    msgType = MyMessageActivity.Type_Comment;
                    break;
                case PushType.AMUSE_ID:
                    PreferencesUtil.saveMatchStatue(context, true);
                    msgType = MyMessageActivity.Type_Activites;
                    break;
                case PushType.COMMODITY_ID:
                    PreferencesUtil.saveMatchStatue(context, true);
                    msgType = MyMessageActivity.Type_System;
                    break;
                case PushType.ROBTREASURE_ID:
                    PreferencesUtil.saveMatchStatue(context, true);
                    msgType = MyMessageActivity.Type_System;
                    break;
                case PushType.AWARD_COMMDITY_ID:
                    PreferencesUtil.saveMatchStatue(context, true);
                    break;
                case PushType.MALL_ID:
                    PreferencesUtil.saveMatchStatue(context, true);
                    msgType = MyMessageActivity.Type_System;
                    break;
                case PushType.COIN_TASK_ID:
                    PreferencesUtil.saveMatchStatue(context, true);
                    break;
                case PushType.OET_ID:
                    PreferencesUtil.saveMatchStatue(context, true);
                    msgType = MyMessageActivity.MsgType_OET;
                    break;
                case PushType.BOUNTY_ID:
                    PreferencesUtil.saveMatchStatue(context, true);
                    break;
            }

            if (statue == 1) {
                if (FragmentMyMain.getInstance() != null) {
                    // FIXME: 2016/8/4  消息数增加？？？
                    //FragmentMyMain.getInstance().refreshPush(msgType);
                }
            }
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            //用户点击通知栏打开相应的页面
            toStartActivity(context, null, null);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            LogUtil.e(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            // 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
            // 打开一个网页等..
        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            LogUtil.e(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            LogUtil.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    private void toStartActivity(Context context, Class<?> cls, String alert) {
        Intent intent = new Intent();
        if (cls == null) {
            switch (isStartActivity(context, MainActivity.class)) {
                case -1:
                    ComponentName cn = new ComponentName(packageName, packageName + ".ui.StartActivity");
                    intent.setComponent(cn);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("bottom", 0);
                    break;
                case 1:
                    intent.setClass(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("bottom", 0);
                    break;
            }
        } else {
            intent.setClass(context, cls);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("bottom", 1);
        }

        intent.putExtra("alert", alert);
        switch (pt.getPushType()) {
            case PushType.SYS_ID:
//			intent.setClass(context, MyMessageActivity.class);
                intent.putExtra("channel", PushType.SYS_ID);
//			context.startActivity(intent);
                break;
            case PushType.COMMENT_ID:
//			intent.setClass(context, MyMessageActivity.class);
                intent.putExtra("channel", PushType.COMMENT_ID);
//			context.startActivity(intent);
                break;
            case PushType.REDBAG_ID:
//			intent.setClass(context, MyRedBagActivity.class);
                intent.putExtra("channel", PushType.REDBAG_ID);
//			context.startActivity(intent);
                break;
            case PushType.RESERVATION_ID:
//			intent.setClass(context, ReserveOrderActivity.class);
                intent.putExtra("orderType", 0);
                intent.putExtra("reserveId", pt.getObjectId());
                intent.putExtra("channel", PushType.RESERVATION_ID);
//			context.startActivity(intent);
                break;
            case PushType.YUEZHAN_ID:
//			intent.setClass(context, YueZhanDetailsActivity.class);
                intent.putExtra("id", pt.getObjectId());
                intent.putExtra("channel", PushType.YUEZHAN_ID);
//			context.startActivity(intent);
                break;
            case PushType.AMUSE_ID:
                intent.putExtra("id", pt.getObjectId());
                intent.putExtra("channel", PushType.AMUSE_ID);
//			context.startActivity(intent);
                break;
            case PushType.NETBAR_ID://跳网吧
                intent.putExtra("netbarId", pt.getObjectId());
                intent.putExtra("channel", PushType.NETBAR_ID);
//			context.startActivity(intent);
                break;
            case PushType.PAY_ID:
//			intent.setClass(context, ReserveOrderActivity.class);
                intent.putExtra("orderType", 1);
                intent.putExtra("reserveId", pt.getObjectId());
                intent.putExtra("channel", PushType.PAY_ID);
//			context.startActivity(intent);
                break;
            case PushType.MATCH_ID:
                intent.putExtra("matchId", pt.getObjectId());
                intent.putExtra("channel", PushType.MATCH_ID);
//			context.startActivity(intent);
                break;
            case PushType.WEEK_REDBAG_ID:
//			intent.setClass(context, SubjectActivity.class);
                intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.REDBAG);
                intent.putExtra("channel", PushType.WEEK_REDBAG_ID);
//			context.startActivity(intent);
                break;
            case PushType.INFORMATION_ID:
                intent.putExtra("infoId", pt.getObjectId());
                try {
                    String wyextend = pt.getJsonObject().getString("wyextend");
                    JSONObject object = new JSONObject(wyextend);
                    intent.putExtra("infoType", object.getInt("type"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtra("channel", PushType.INFORMATION_ID);
                break;
            case PushType.COMMODITY_ID://兑换
                intent.putExtra("exchangeID", pt.getObjectId() + "");
                intent.putExtra("channel", PushType.COMMODITY_ID);
                break;
            case PushType.ROBTREASURE_ID://众筹
                intent.putExtra("id", pt.getObjectId() + "");
                intent.putExtra("channel", PushType.ROBTREASURE_ID);
                break;
            case PushType.AWARD_COMMDITY_ID://商品详情
                intent.putExtra("id", pt.getObjectId() + "");
                intent.putExtra("channel", PushType.AWARD_COMMDITY_ID);
                break;
            case PushType.MALL_ID://金币商城
                intent.putExtra("channel", PushType.MALL_ID);
                break;
            case PushType.COIN_TASK_ID://金币任务
                intent.putExtra("channel", PushType.COIN_TASK_ID);
                break;
            case PushType.OET_ID://自发赛
                intent.putExtra("id", pt.getObjectId() + "");
                intent.putExtra("channel", PushType.OET_ID);
                break;
            case PushType.BOUNTY_ID://自发赛
                intent.putExtra("id", pt.getObjectId() + "");
                intent.putExtra("channel", PushType.BOUNTY_ID);
                break;
        }
        if (intent != null && context != null) {
            context.startActivity(intent);
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    protected <T> int isStartActivity(Context context, Class<T> activity) {
        Intent intent = new Intent(context, activity);
        ComponentName cmpName = intent.resolveActivity(context.getPackageManager());
        int bIsExist = -1;
        if (cmpName != null) { // 说明系统中存在这个activity
            ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
            LogUtil.i(TAG, "---startAndExit---taskInfoList.size:" + taskInfoList.size());
            for (RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                    bIsExist = 1;
                    break;
                }
            }
        }
        return bIsExist;
    }
}
