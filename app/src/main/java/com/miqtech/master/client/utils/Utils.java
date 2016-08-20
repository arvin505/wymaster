package com.miqtech.master.client.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.constant.Constant;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/11/17.
 */
public class Utils {
    private static Context mContext;
    private static float mDensity;
    private static long lastClickTime = 0;

    static {
        mContext = WangYuApplication.getGlobalContext();
        mDensity = mContext.getResources().getDisplayMetrics().density;
    }

    public static int dp2px(int dp) {
        return (int) (mDensity * dp + 0.5);
    }

    public static int px2dp(int px) {
        return (int) (px / mDensity + 0.5);
    }

    public static int sp2px(int sp) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 检查当前网络是否可用
     *
     * @param activity
     * @return
     */
    public static boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 检查网络状态  0 没有网络  1有wifi 2 有gprs 3 既有wifi也有gprs
     * @return
     */
    public static int checkNetworkState() {
        int type = 0;
        boolean flag=false;
        //得到网络连接信息
         ConnectivityManager  manager = (ConnectivityManager) WangYuApplication.getGlobalContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        if (!flag) {
            type=0;
        } else {
            NetworkInfo.State gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if((gprs == NetworkInfo.State.CONNECTED || gprs == NetworkInfo.State.CONNECTING)
                    && (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)){
                type = 3;
            }else {
                if (gprs == NetworkInfo.State.CONNECTED || gprs == NetworkInfo.State.CONNECTING) {
                    type = 2;
                }
                //判断为wifi状态下才加载广告，如果是GPRS手机网络则不加载！
                if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                    type = 1;
                }
            }

        }
        return type;
    }
    /**
     * 得到数的形式 ： 6555 , 9.6万,9.6亿
     *
     * @param i
     * @return
     */
    public static String getnumberForms(int i, Context context) {
        String str = "";
        if (i < 10000) {
            str = i + "";
        } else if (i < 100000000) {
            str = i % 1000 / 10 + context.getResources().getString(R.string.wan);
        } else {
            str = i % 10000000 / 10 + context.getResources().getString(R.string.yi);
        }
        return str;
    }

    /**
     * 得到数的形式 ： 6555 , 9.6K,9.6Y
     *
     * @param i
     * @return
     */
    public static String getUnitConversion(int i) {
        String str = "";
        if (i < 10000) {
            str = i + "";
        } else if (i < 100000000) {
            str = i % 10000 + "K";
        } else {
            str = i % 100000000 + "Y";
        }
        return str;
    }

    /**
     * 得到数的形式 ： 666,999+
     *
     * @param i
     * @return
     */
    public static String getNumberforInfo(int i) {
        String str = "";
        if (i < 1000) {
            str = i + "";
        } else {
            str = 999 + "+";
        }
        return str;
    }


    public static String DisConversion(double dis) {
        DecimalFormat df = new DecimalFormat("#.#");
        if ((dis * 1000) <= 999)
            return df.format((dis * 1000)) + "m";
        if (dis > 999)
            return 999 + "+km";
        return df.format(dis) + "km";
    }

    private static final double EARTH_RADIUS = 6378137.0;

    // 返回单位是米
    public static double getDistance(double latitude1, double longitude1) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(Constant.latitude);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(Constant.longitude);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 获取控件的高度，如果获取的高度为0，则重新计算尺寸后再返回高度
     *
     * @param view
     * @return
     */
    public static int getViewMeasuredHeight(View view) {
//        int height = view.getMeasuredHeight();
//        if(0 < height){
//            return height;
//        }
        calcViewMeasure(view);
        return view.getMeasuredHeight();
    }

    private static void calcViewMeasure(View view) {
//        int width = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        int height = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        view.measure(width,height);

        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
    }

    /**
     * 把18888888888变成188****8888
     *
     * @param str
     * @return
     */
    public static String changeString(String str) {
        if (str == null) {
            return null;
        }
        String strMobile = str;
        if (strMobile.length() < 11) {
            return str;
        }
        return strMobile.replace(str.substring(3, 7), "****").trim();
    }

    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context context, String key) {
        if (context == null || TextUtils.isEmpty(key)) {
            return null;
        }
        Context ctx = context.getApplicationContext();
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(),
                        PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }

    /**
     * @param num  需要计算的数字
     * @param unit 单位 ： 1000   10000
     * @return
     */
    public static String calculate(int num, int unit, String unitStr) {
        if (num < unit) {
            return num + "";
        }
        float result = (float) num / (float) unit;
        DecimalFormat df = new DecimalFormat("#.0");
        return df.format(result) + unitStr;
    }

    /**
     * 把带有中文的连接转化成能解析的字符串
     *
     * @param url
     * @return
     */
    public static String transformUTF8(String url) {
        String str = null;
        try {
            str = java.net.URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 禁止按钮的快速点击
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 解析CODE获取场次参数
     *
     * @param code
     * @return
     */

    public static Map<String, String> getMatchParams(String code) {
        String[] codes = code.split("&");
        HashMap<String, String> map = new HashMap<String, String>();
        List<String> codesList = Arrays.asList(codes);
        for (int i = 0; i < codesList.size(); i++) {
            String str = codesList.get(i);
            String[] strs = str.split("=");
            if (strs.length == 2) {
                map.put(strs[0], strs[1]);
            }
        }
        return map;
    }

    /**
     * 解析奖品信息
     *
     * @param
     * @return
     */
    public static Map<String, String> getPrizeParams(String url) {
        if (!url.contains("?")) {
            return null;
        }
        int index = url.lastIndexOf("?");
        url = url.replace(url.substring(0, index + 1), "");
        String[] array = url.split("&");
        Map<String, String> returnParams = new HashMap<>();
        for (String item : array) {
            String[] paramArr = item.split("=");
            returnParams.put(paramArr[0], paramArr[1]);
        }
        return returnParams;
    }

    public static boolean isEmulator(String agent) {
        String[] emulatorNames = WangYuApplication.appContext.getResources().getStringArray(R.array.emulator);
        for (String name : emulatorNames) {
            if (agent.contains(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是2的N次方
     *
     * @param n
     * @return
     */
    public static boolean nCF(int n, int list) {
        boolean b = false;
        int nN = n;
        while (true) {
            int j = n % 2;
            n = n / 2;
            if (j == 1) {
                b = false;
                break;
            }
            if (n == 2) {
                b = true;
                break;
            }
        }

        if (list > 1 && b) {//当总的有4列，16个参赛选手时，第一列为8个参赛4组数据时（最多是8组16个选手）通过了2的n次方，应返回为false
            double ss = Math.pow(2, list - 1);
            if (nN < ss) {
                b = false;
            }
        }
        return b;
    }

    /**
     * 判断app是否在前台运行
     *
     * @param context
     * @return
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (currentPackageName != null && currentPackageName.equals(context.getPackageName())) {
            return true;
        }
        return false;
    }
}
