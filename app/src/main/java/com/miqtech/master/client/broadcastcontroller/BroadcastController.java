package com.miqtech.master.client.broadcastcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.miqtech.master.client.utils.LogUtil;

/**
 * 广播控制器
 *
 */
public class BroadcastController {

    public static final String ACTION_USERCHANGE = "miqtech.master.client.ACTION_USERCHANGE";
    public static final String ACTION_ISAUTOPALY = "miqtech.master.client.ACTION_ISAUTOPALY";
    public static final String ACTION_ISLEAVEAPP= "miqtech.master.client.ACTION_ISLEAVEAPP";

    /**
     * 发送用户变化的广播
     *
     * @param context 上下文
     */
    public static void sendUserChangeBroadcase(Context context) {
        context.sendBroadcast(new Intent(ACTION_USERCHANGE));
    }

    /**
     * 注册一个监听用户变化的广播
     *
     * @param context  上下文
     * @param receiver 广播接收器
     */
    public static void registerUserChangeReceiver(Context context,
                                                  BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter(ACTION_USERCHANGE);
        context.registerReceiver(receiver, filter);
    }

    /**
     * 注销一个广播接收器
     *
     * @param context  上下文
     * @param receiver 要注销的广播接收器
     */
    public static void unregisterReceiver(Context context, BroadcastReceiver receiver) {
        try {
            context.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     *
     * @param context
     * @param receiver
     */
    public static void registerIsLeaveAppReceiver(Context context,BroadcastReceiver receiver){
        IntentFilter filter = new IntentFilter(ACTION_ISLEAVEAPP);
        context.registerReceiver(receiver, filter);
    }

    public static void sendIsLeaveAppReceive(Context context){
        context.sendBroadcast(new Intent(ACTION_ISLEAVEAPP));
    }


}