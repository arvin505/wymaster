package com.miqtech.master.client.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.baidu.mapapi.SDKInitializer;
import com.bugtags.library.Bugtags;
import com.github.moduth.blockcanary.BlockCanary;
import com.google.gson.Gson;
import com.miqtech.master.client.BuildConfig;
import com.miqtech.master.client.R;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.City;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.http.RequestUtil;
import com.miqtech.master.client.jpush.service.JPushUtil;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.Utils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.pili.pldroid.streaming.StreamingEnv;
import com.squareup.leakcanary.LeakCanary;

import cn.jpush.android.api.JPushInterface;

public class WangYuApplication extends Application {
    private static User user;

    // 屏幕参数
    public static int DENSITY;
    public static int WIDTH;
    public static int HEIGHT;
    public static WangYuApplication appContext;

    private boolean isDownload;
    private Context context;
    public static String USER_AGENT;

    public static JPushUtil jpushUtil;

    /**
     * 线性移动的终点X坐标
     */
    public static int toX;
    /**
     * 线性移动的终点Y坐标
     */
    public static int toY;
    /**
     * 波形散开的点的X值
     */
    public static int circleX;
    /**
     * 波形散开的点的Y值
     */
    public static int circleY;
    /**
     * 状态栏的高度
     */
    public static int statusBarTop;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        BlockCanary.install(this, new AppBlockCanaryContext()).start();
        Bugtags.start(BuildConfig.DEBUG ? Constant.BUGTAGS_KEY_BETA : Constant.BUGTAGS_KEY_LIVE, this, Bugtags.BTGInvocationEventNone);
        // 获取屏幕的长宽高
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        SDKInitializer.initialize(this);
        WIDTH = dm.widthPixels;
        HEIGHT = dm.heightPixels;
        DENSITY = (int) dm.density;
        context = this;

        appContext = this;
        initImageLoader(getApplicationContext());
        if (!TextUtils.isEmpty(PreferencesUtil.getLastRecreationCity(this))) {
            Constant.currentCity = GsonUtil.getBean(PreferencesUtil.getLastRecreationCity(this), City.class);
        }

        RequestUtil.init(this);

        /** JPush相关 **/
        jpushUtil = new JPushUtil(getApplicationContext());
        JPushInterface.setDebugMode(false); // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);
        jpushUtil.setTag(getString(com.miqtech.master.client.R.string.tag));
        User user = getUser(this);
//        jpushUtil.setAliasWithTags(context.getResources().getString(R.string.alias_test) + user.getId(), JPushUtil.initTags(user));
        if (user != null) {
            if (HttpConstant.SERVICE_HTTP_AREA.contains("wy")) {
                //jpushUtil.setAlias(context.getResources().getString(R.string.alias_test) + user.getId());
                jpushUtil.setAliasWithTags(context.getResources().getString(R.string.alias_test) + user.getId(), JPushUtil.initTags(user));
            } else {
                //jpushUtil.setAlias(getResources().getString(R.string.alias) + user.getId());
                jpushUtil.setAliasWithTags(context.getResources().getString(R.string.alias) + user.getId(), JPushUtil.initTags(user));
            }
        } else {
            saveHotFixCode();
        }

        USER_AGENT = getUserAgent();
        ISEMULATOR = Utils.isEmulator(USER_AGENT);
        //直播
        StreamingEnv.init(this);
    }

    public static boolean ISEMULATOR;

    public static WangYuApplication getGlobalContext() {
        return appContext;
    }

    // 获取状态栏高度
    public static int getStatusHeight(Activity activity) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSize(4 * 1024 * 1024).threadPoolSize(3)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static User getUser(Context context) {
        if (user == null) {
            String userStr = PreferencesUtil.getUser(context);
            if (!TextUtils.isEmpty(userStr)) {
                user = new Gson().fromJson(userStr, User.class);
            } else {
                return null;
            }
        }

        return user;
    }

    public static void setUser(User user) {
        WangYuApplication.user = user;
    }

    public static void clearUser() {
        WangYuApplication.user = null;
    }

    public static JPushUtil getJpushUtil() {
        return jpushUtil;
    }


    public String getUserAgent() {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        String version = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String userAgent = "WangYu" + "/" + version + " (Android; Android " + android.os.Build.VERSION.RELEASE + "; UUID "
                + ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId() + "; MODEL "
                + Build.MODEL + "; SERIAL " + Build.SERIAL + "; BOARD " + Build.BOARD + "; DEVICE " + Build.DEVICE
                + ";)";
        return userAgent;
    }

    /**
     * 获取程序包信息
     *
     * @param context
     * @return
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }

    public void setDownload(boolean b) {
        isDownload = b;
    }

    public boolean isDownload() {
        return isDownload;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        /*Nuwa.init(this);
        String path = Constant.FIXPATCH;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        Nuwa.loadPatch(this, path + Constant.PATCNAME);*/
        MultiDex.install(this);
    }

    /**
     * 应用启动的时候检查shared_preference
     * 如果是第一次启动，写入hotfixcode为-1；
     * 请求检查更新会根据hotfixcode的值判断是否下载热修复文件
     * 并将新的hotfixcode 写入shared_preference
     */
    private void saveHotFixCode() {
        int hotFixCode = PreferencesUtil.getHotFixPatchCode(this);
        if (hotFixCode == -1) {
            PreferencesUtil.saveHotFixPatchCode(this, -1);
        }
    }

    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
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
}
