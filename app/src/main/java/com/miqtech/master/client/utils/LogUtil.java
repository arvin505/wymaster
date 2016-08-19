package com.miqtech.master.client.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.miqtech.master.client.BuildConfig;

/**
 * Created by Administrator on 2015/11/18.
 */
public class LogUtil {
//    private static boolean debug = isDebug(WangYuApplication.getGlobalContext());
    /**
     * BuildConfig.DEBUG debug版本为true
     * 打包出的版本为false
     */
    public static boolean debug = BuildConfig.DEBUG;

    private static boolean isDebug(Context context) {
        try {
            PackageInfo pkginfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 1);
            if (pkginfo != null) {
                ApplicationInfo info = pkginfo.applicationInfo;
                return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void e(String tag, String msg) {
        if (debug) {
            try {
                Log.e(tag, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void i(String tag, String msg) {
        if (debug) {
            try {
                Log.e(tag, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void d(String tag, String msg) {
        if (debug) {
            try {
                Log.e(tag, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
