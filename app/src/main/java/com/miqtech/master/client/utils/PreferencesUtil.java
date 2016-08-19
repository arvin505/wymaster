package com.miqtech.master.client.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreferencesUtil {
    private static final String PUSH_SYS_STATUE = "push_sys_statue";// 系统消息
    private static final String PUSH_RESERVATION_STATUE = "push_reservation_statue";// 预定订单消息
    private static final String PUSH_NETBAR_STATUE = "push_netbar_statue";// 网吧
    private static final String PUSH_PAY_STATUE = "push_pay_statue";// 支付
    private static final String PUSH_YUEZHAN_STATUE = "push_activity_statue";// 活动
    private static final String PUSH_MATCH_STATUE = "push_math_statue";// 比赛
    private static final String PUSH_REDBAG_STATUE = "push_redbag_statue";// 红包
    private static final String PUSH_WEEK_REDBAG_STATUE = "push_week_redbag_statue";// 周红包
    private static final String SELECT_CITY = "mycity";// 选择的城市
    private static final String SELECT_CITY_CODE = "mycitycode";// 选择的城市
    private static final String HAS_SELECT_CITIES = "history_cities";

    private static final String VERSION = "version";
    // 首页引导页
    private static final String MAIN_GUIDE = "main_guide";
    // 约战引导页
    private static final String YUEZHAN_GUIDE = "yuezhan_guide";
    // 我的引导页
    private static final String MY_GUIDE = "my_guide";
    // 网吧详情引导页
    private static final String NETBAR_GUIDE = "netbar_guide";

    private static final String SHOW_HOT = "show_hot";

    /**
     * 悬赏令引导页
     */
    private static final String SHOW_REWARD = "show_reward";

    private static final String IDCard = "idCard";
    private static final String Mobile = "mobile";
    private static final String RealName = "realName";
    private static final String QQ = "qq";
    private static final String Labor = "labor";

    private static SharedPreferences preferences;

    public static String USER = "User";

    public static String HISTORY = "History";

    public static String ACTIVITY_HISTORY = "Activity_history";

    public static String NETBAR_HISTORY = "Netbar_history";

    public static String USER_HISTORY = "User_history";

    public static String GAME_HISTORY = "GameHistory";

    public static String USERNAME = "UserName";

    public static String LAST_RECREATION_CITY = "lastRecreationCity";

    public static SharedPreferences getPreferences(Context context) {
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return preferences;
    }

    private static Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static String getUser(Context context) {
        return getPreferences(context).getString(USER, null);
    }

    public static void setUser(Context context, String userStr) {
        Editor editor = getEditor(context);
        editor.putString(USER, userStr);
        editor.commit();
    }

    public static void setKeepYuezhan(Context context, String user_keepyuezhan, String Str) {
        Editor editor = getEditor(context);
        editor.putString(user_keepyuezhan, Str);
        editor.commit();
    }

    public static String getKeepYuezhan(Context context, String user_keepyuezhan) {
        return getPreferences(context).getString(user_keepyuezhan, null);
    }

    public static void clearUser(Context context) {
        Editor editor = getEditor(context);
        editor.putString(USER, null);
        editor.commit();
    }


    /**
     * 主页是否显示过指导页
     *
     * @param
     * @return
     */
    public static boolean getFirstMainGuideStatus(Context context) {
        return getPreferences(context).getBoolean(MAIN_GUIDE, true);
    }

    /**
     * 设置首页自发赛是否显示过hot
     *
     * @param
     * @param status
     */
    public static void setIsFirstShowHot(Context context, boolean status) {
        Editor editor = getEditor(context);
        editor.putBoolean(SHOW_HOT, status);
        editor.commit();
    }

    /**
     * 首页自发赛是否显示过hot
     *
     * @param
     * @return
     */
    public static boolean getIsFirstShowHot(Context context) {
        return getPreferences(context).getBoolean(SHOW_HOT, true);
    }

    /**
     * 设置主页指导已经显示过
     *
     * @param
     * @param status
     */
    public static void setFirstMainGuideStatus(Context context, boolean status) {
        Editor editor = getEditor(context);
        editor.putBoolean(MAIN_GUIDE, status);
        editor.commit();
    }

    /**
     * 我的页面是否显示过
     *
     * @param context
     * @return
     */
    public static boolean getFirstMyGuideStatu(Context context) {
        return getPreferences(context).getBoolean(MY_GUIDE, true);
    }

    /**
     * 设置我的页面是否显示过
     *
     * @param context
     * @param status
     */
    public static void setFirstMyGuideStatu(Context context, boolean status) {
        Editor editor = getEditor(context);
        editor.putBoolean(MY_GUIDE, status);
        editor.commit();
    }

    /**
     * 网吧详情页面是否显示过
     *
     * @param context
     * @return
     */
    public static boolean getFirstNetBarGuideStatu(Context context) {
        return getPreferences(context).getBoolean(NETBAR_GUIDE, true);
    }

    /**
     * 设置网吧页面是否显示过
     *
     * @param context
     * @param status
     */
    public static void setFirstNetBarGuideStatu(Context context, boolean status) {
        Editor editor = getEditor(context);
        editor.putBoolean(NETBAR_GUIDE, status);
        editor.commit();
    }

    /**
     * 是否显示悬赏令
     *
     * @param context
     * @return
     */
    public static boolean getFirstRewardGuideStatu(Context context) {
        return getPreferences(context).getBoolean(SHOW_REWARD, true);
    }

    /**
     * 设置悬赏令是否显示过
     *
     * @param context
     * @param status
     */
    public static void setFirstRewardGuideStatu(Context context, boolean status) {
        Editor editor = getEditor(context);
        editor.putBoolean(SHOW_REWARD, status);
        editor.commit();
    }

    /**
     * 约战知道是否显示过
     *
     * @param
     * @return
     */
    public static boolean getFirstYueZhanGuideStatus(Context context) {
        return getPreferences(context).getBoolean(YUEZHAN_GUIDE, true);
    }

    /**
     * 设置约战页面是否显示过
     *
     * @param context
     * @param status
     */
    public static void setFirstYueZhanGuideStatus(Context context, boolean status) {
        Editor editor = getEditor(context);
        editor.putBoolean(YUEZHAN_GUIDE, status);
        editor.commit();
    }

    public static void setUserName(Context context, String userName) {
        Editor editor = getEditor(context);
        editor.putString(USERNAME, userName);
        editor.commit();
    }

    public static String getUserName(Context context) {
        return getPreferences(context).getString(USERNAME, null);
    }

    public static void setYueZhanGuideStatus(Context context, boolean status) {
        Editor editor = getEditor(context);
        editor.putBoolean(YUEZHAN_GUIDE, status);
        editor.commit();
    }

    public static void saveHistoryCities(Context context, String cities) {
        Editor editor = getEditor(context);
        editor.putString(HAS_SELECT_CITIES, cities);
        editor.commit();
    }

    public static String getHistoryCities(Context context) {
        return getPreferences(context).getString(HAS_SELECT_CITIES, null);
    }

    /**
     * 保存搜索记录
     */
    public static void saveHistory(Context context, String newText) {
        Editor editor = getEditor(context);
        String longhistory = getPreferences(context).getString(HISTORY, "");
        if (!longhistory.contains(newText) && !TextUtils.isEmpty(newText.trim())) {
            newText += "|";
            StringBuffer sb = new StringBuffer(longhistory);
            sb.insert(0, newText);
            longhistory = sb.toString();
            String[] str_ = longhistory.split("\\|");
            int lastIndex = longhistory.lastIndexOf("|");
            if (str_.length > 10) {
                longhistory = longhistory.substring(0, lastIndex);
                lastIndex = longhistory.lastIndexOf("|");
                longhistory = longhistory.substring(0, lastIndex + 1);
            }
        }
        editor.putString(HISTORY, longhistory);
        editor.commit();
    }

    /**
     * 保存游戏搜索记录
     */
    public static void saveGameHistory(Context context, String newText) {
        Editor editor = getEditor(context);
        String longhistory = getPreferences(context).getString(GAME_HISTORY, "");
        if (!longhistory.contains(newText) && !TextUtils.isEmpty(newText.trim())) {
            newText += "|";
            StringBuffer sb = new StringBuffer(longhistory);
            sb.insert(0, newText);
            longhistory = sb.toString();
            String[] str_ = longhistory.split("\\|");
            int lastIndex = longhistory.lastIndexOf("|");
            if (str_.length > 10) {
                longhistory = longhistory.substring(0, lastIndex);
                lastIndex = longhistory.lastIndexOf("|");
                longhistory = longhistory.substring(0, lastIndex + 1);
            }
        }
        editor.putString(GAME_HISTORY, longhistory);
        editor.commit();
    }

    /**
     * 读取搜索历史
     */
    public static List<String> readHistory(int type) {
        /*String longhistory = getPreferences(context).getString(HISTORY, "");
        String[] hisArrays = longhistory.split("\\|");
        List<String> items = Arrays.asList(hisArrays);
        if (items.size() == 1 && TextUtils.isEmpty(items.get(0).toString())) {
            return new ArrayList<>();
        }*/
        if (type == 0) {
            return readNetbarHistory(WangYuApplication.getGlobalContext());
        } else if (type == 1) {
            return readActivityHistory(WangYuApplication.getGlobalContext());
        } else {
            return readUserHistory(WangYuApplication.getGlobalContext());
        }
    }

    /**
     * 读取游戏搜索历史
     */
    public static List<String> readGameHistory(Context context) {
        String longhistory = getPreferences(context).getString(GAME_HISTORY, "");
        String[] hisArrays = longhistory.split("\\|");
        List<String> items = Arrays.asList(hisArrays);
        return items;
    }

    /**
     * 清除历史记录
     */
    public static void removeHistory(Context context, String key) {
        Editor editor = getEditor(context);
        editor.putString(key, "");
        editor.commit();
    }

    /**
     * 清除单条历史记录
     */
    public static void removeHistory(Context context, String key, String value) {
        Editor editor = getEditor(context);
        String longhistory = getPreferences(context).getString(key, "");
        if (longhistory.contains(value + "|")) {
            longhistory = longhistory.replace((value + "|"), "");
        }
        editor.putString(key, longhistory);
        editor.commit();
    }


    /**
     * 清除游戏历史记录
     */
    public static void removeGameHistory(Context context) {
        Editor editor = getEditor(context);
        editor.putString(GAME_HISTORY, "");
        editor.commit();
    }

    /**
     * 保存启动信息
     *
     * @param context
     */
    public static void saveStartInfo(Context context) {
        Editor editor = getEditor(context);
        int versionCode = getVersionCode(context);
        editor.putInt(VERSION, versionCode);
        editor.commit();
    }

    public static int getStartInfo(Context context) {
        return getPreferences(context).getInt(VERSION, 0);
    }

    // 版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    // 版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
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

    public static void saveReserStatue(Context context, Boolean isReservation) {
        Editor editor = getEditor(context);
        editor.putBoolean(PUSH_RESERVATION_STATUE, isReservation);
        editor.commit();
    }

    public static void saveSysStatue(Context context, Boolean isReservation) {
        Editor editor = getEditor(context);
        editor.putBoolean(PUSH_SYS_STATUE, isReservation);
        editor.commit();
    }

    public static void saveNetbarStatue(Context context, Boolean isReservation) {
        Editor editor = getEditor(context);
        editor.putBoolean(PUSH_NETBAR_STATUE, isReservation);
        editor.commit();
    }

    public static void savePayStatue(Context context, Boolean isReservation) {
        Editor editor = getEditor(context);
        editor.putBoolean(PUSH_PAY_STATUE, isReservation);
        editor.commit();
    }

    public static void saveYuezhanStatue(Context context, Boolean isReservation) {
        Editor editor = getEditor(context);
        editor.putBoolean(PUSH_YUEZHAN_STATUE, isReservation);
        editor.commit();
    }

    public static void saveMatchStatue(Context context, Boolean isReservation) {
        Editor editor = getEditor(context);
        editor.putBoolean(PUSH_MATCH_STATUE, isReservation);
        editor.commit();
    }

    public static void saveRedbagStatue(Context context, Boolean isReservation) {
        Editor editor = getEditor(context);
        editor.putBoolean(PUSH_REDBAG_STATUE, isReservation);
        editor.commit();
    }

    public static void saveWeekRedbagPush(Context context, Boolean isReservation) {
        Editor editor = getEditor(context);
        editor.putBoolean(PUSH_WEEK_REDBAG_STATUE, isReservation);
        editor.commit();
    }

    public static boolean getSysPush(Context context) {
        return getPreferences(context).getBoolean(PUSH_SYS_STATUE, false);
    }

    public static boolean getPayPush(Context context) {
        return getPreferences(context).getBoolean(PUSH_PAY_STATUE, false);
    }

    public static boolean getRedbagPush(Context context) {
        return getPreferences(context).getBoolean(PUSH_REDBAG_STATUE, false);
    }

    public static boolean getNetbarPush(Context context) {
        return getPreferences(context).getBoolean(PUSH_NETBAR_STATUE, false);
    }

    public static boolean getMatch(Context context) {
        return getPreferences(context).getBoolean(PUSH_MATCH_STATUE, false);
    }

    public static boolean getWeekRedbagPush(Context context) {
        return getPreferences(context).getBoolean(PUSH_WEEK_REDBAG_STATUE, false);
    }

    public static boolean getActivityPush(Context context) {
        return getPreferences(context).getBoolean(PUSH_YUEZHAN_STATUE, false);
    }

    public static boolean getReservation(Context context) {
        return getPreferences(context).getBoolean(PUSH_RESERVATION_STATUE, false);
    }

    public static void clearPushStatue(Context context) {
        Editor editor = getEditor(context);
        editor.putBoolean(PUSH_MATCH_STATUE, false);
        editor.putBoolean(PUSH_RESERVATION_STATUE, false);
        editor.putBoolean(PUSH_SYS_STATUE, false);
        editor.putBoolean(PUSH_NETBAR_STATUE, false);
        editor.putBoolean(PUSH_PAY_STATUE, false);
        editor.putBoolean(PUSH_YUEZHAN_STATUE, false);
        editor.putBoolean(PUSH_REDBAG_STATUE, false);
        editor.putBoolean(PUSH_WEEK_REDBAG_STATUE, false);
        editor.commit();
    }

    public static boolean getMyMessagePush(Context context) {
        return getReservation(context) | getSysPush(context) | getPayPush(context) | getRedbagPush(context)
                | getNetbarPush(context) | getMatch(context) | getWeekRedbagPush(context) | getActivityPush(context);
    }

    public static void saveAreaCode(String areacode, Context context) {
        Editor editor = getEditor(context);
        editor.putString("AREACODE", areacode);
        editor.commit();
    }

    public static String getAreacode(Context context) {
        return getPreferences(context).getString("AREACODE", "");
    }

    public static void saveUserMobile(String mobile, Context context) {
        Editor editor = getEditor(context);
        User user = WangYuApplication.getUser(context);
        if (user != null) {
            editor.putString(Mobile + user.getId(), mobile);
        }
        editor.commit();
    }

    public static void saveUserRealName(String realName, Context context) {
        Editor editor = getEditor(context);
        User user = WangYuApplication.getUser(context);
        if (user != null) {
            editor.putString(RealName + user.getId(), realName);
        }
        editor.commit();
    }

    public static void saveUserQQ(String qq, Context context) {
        Editor editor = getEditor(context);
        User user = WangYuApplication.getUser(context);
        if (user != null) {
            editor.putString(QQ + user.getId(), qq);
        }
        editor.commit();
    }

    public static void saveUserLabor(String labor, Context context) {
        Editor editor = getEditor(context);
        User user = WangYuApplication.getUser(context);
        if (user != null) {
            editor.putString(Labor + user.getId(), labor);
        }
        editor.commit();
    }

    public static String getUserIDCard(Context context) {
        User user = WangYuApplication.getUser(context);
        if (user == null) {
            return "";
        }
        if (user != null && user.getIdCard().length() > 0) {
            return user.getIdCard();
        } else {
            return getPreferences(context).getString(IDCard + user.getId(), "");
        }
    }

    public static String getUserMobile(Context context) {
        User user = WangYuApplication.getUser(context);
        if (user == null) {
            return "";
        }
        if (user.getTelephone().length() > 0) {
            return user.getTelephone();
        } else {
            return getPreferences(context).getString(Mobile + user.getId(), "");
        }
    }

    public static String getUserRealName(Context context) {
        User user = WangYuApplication.getUser(context);
        if (user == null) {
            return "";
        }
        if (user.getRealName().length() > 0) {
            return user.getRealName();
        } else {
            return getPreferences(context).getString(RealName + user.getId(), "");
        }
    }

    public static String getUserQQ(Context context) {
        User user = WangYuApplication.getUser(context);
        if (user == null) {
            return "";
        }
        if (user.getQq().length() > 0) {
            return user.getQq();
        } else {
            return getPreferences(context).getString(QQ + user.getId(), "");
        }
    }

    public static void saveUserIDCard(String idCard, Context context) {
        Editor editor = getEditor(context);
        User user = WangYuApplication.getUser(context);
        if (user != null) {
            editor.putString(IDCard + user.getId(), idCard);
        }
        editor.commit();
    }

    public static String getUserLabor(Context context) {
        User user = WangYuApplication.getUser(context);
        if (user != null) {
            return getPreferences(context).getString(Labor + user.getId(), "");
        }
        return getPreferences(context).getString(Labor, "");
    }

    public static void setLastRecreationCity(String city, Context context) {
        Editor editor = getEditor(context);
        editor.putString(LAST_RECREATION_CITY, city);
        editor.commit();
    }

    public static String getLastRecreationCity(Context context) {
        return getPreferences(context).getString(LAST_RECREATION_CITY, null);
    }

    public static String getAeraCode(Context context) {
        return getPreferences(context).getString("Aera_code", "");
    }

    public static void saveAeraCode(Context context, String aeracode) {
        Editor editor = getEditor(context);
        editor.putString("Aera_code", aeracode);
        editor.commit();
    }

    public static void saveLatitude$Longitude(Context context, String latitude, String longitude) {
        Editor editor = getEditor(context);
        editor.putString("latitude&longitude", latitude + "@@@" + longitude);
        editor.commit();
    }

    public static String[] getLatitude$Longitude(Context context) {
        String data = getPreferences(context).getString("latitude&longitude", null);
        if (data != null) {
            /*double[] ll = new double[2];
            String[] arr = data.split("|");
            *//*ll[0] = Double.parseDouble(arr[0]);
            ll[1] = Double.parseDouble(arr[1]);*//*
            LogUtil.e("arr","arr : " + arr.toString());*/
            for (String str : data.split("@@@")) {
                LogUtil.e("arr", "arr : " + str);
            }
            return data.split("@@@");
        } else {
            return null;
        }
    }

    public static String getConfig(Context context) {
        return getPreferences(context).getString("Config", null);
    }

    public static void setConfig(Context context, String config) {
        Editor editor = getEditor(context);
        editor.putString("Config", config);
        editor.commit();
    }

    /**
     * 保存赛事搜索记录
     */
    public static void saveActivityHistory(Context context, String newText) {
        save(context, newText, ACTIVITY_HISTORY);
    }

    /**
     * 保存网吧搜索记录
     */
    public static void saveNetbarHistory(Context context, String newText) {
        save(context, newText, NETBAR_HISTORY);
    }

    /**
     * 保存用户搜索记录
     */
    public static void saveUserHistory(Context context, String newText) {
        save(context, newText, USER_HISTORY);
    }

    /**
     * 保存
     */
    private static void save(Context context, String newText, String key) {
        Editor editor = getEditor(context);
        String longhistory = getPreferences(context).getString(key, "");
        if (!longhistory.contains(newText) && !TextUtils.isEmpty(newText.trim())) {
            newText += "|";
            StringBuffer sb = new StringBuffer(longhistory);
            sb.insert(0, newText);
            longhistory = sb.toString();
            String[] str_ = longhistory.split("\\|");
            int lastIndex = longhistory.lastIndexOf("|");
            if (str_.length > 10) {
                longhistory = longhistory.substring(0, lastIndex);
                lastIndex = longhistory.lastIndexOf("|");
                longhistory = longhistory.substring(0, lastIndex + 1);
            }
        }
        editor.putString(key, longhistory);
        editor.commit();
    }

    /**
     * 赛事搜索历史
     */
    public static List<String> readActivityHistory(Context context) {
        return read(context, ACTIVITY_HISTORY);
    }

    /**
     * 网吧搜索历史
     */
    public static List<String> readNetbarHistory(Context context) {
        return read(context, NETBAR_HISTORY);
    }

    /**
     * 用户搜索历史
     */
    public static List<String> readUserHistory(Context context) {
        return read(context, USER_HISTORY);
    }

    /**
     * 读取搜索历史
     */
    private static List<String> read(Context context, String key) {
        String longhistory = getPreferences(context).getString(key, "");
        String[] hisArrays = longhistory.split("\\|");
        List<String> items = Arrays.asList(hisArrays);
        if (items.size() == 1 && TextUtils.isEmpty(items.get(0).toString())) {
            return new ArrayList<>();
        }
        List returnList = new ArrayList();
        for (int i = 0; i < items.size() && i < 5; i++) {
            returnList.add(items.get(i));
        }
        return returnList;
    }

    /**
     * 设置hotfix patchcode
     */
    public static void saveHotFixPatchCode(Context context, int patchCode) {
        Editor editor = getPreferences(context).edit();
        editor.putInt("hotfixcode", patchCode).commit();
    }

    /**
     * 获取本地hotfix patch code
     */
    public static int getHotFixPatchCode(Context context) {
        return getPreferences(context).getInt("hotfixcode", -1);
    }

    /**
     * 兑换 地址
     *
     * @param address
     */
    public static void saveAddress(String address) {
        Editor editor = getPreferences(WangYuApplication.appContext).edit();
        editor.putString("address", address);
        editor.commit();
    }

    public static String getAddress() {
        return getPreferences(WangYuApplication.appContext).getString("address", "");
    }

    /**
     * 兑换姓名
     */
    public static void saveName(String name) {
        Editor editor = getPreferences(WangYuApplication.appContext).edit();
        editor.putString("name", name);
        editor.commit();
    }

    public static String getName() {
        return getPreferences(WangYuApplication.appContext).getString("name", "");
    }

    /**
     * 兑换账号
     */
    public static void saveCount(String count) {
        Editor editor = getPreferences(WangYuApplication.appContext).edit();
        editor.putString("count", count);
        editor.commit();
    }

    public static String getCount() {
        return getPreferences(WangYuApplication.appContext).getString("count", "");
    }

    /**
     * 兑换 电话
     */
    public static void savePhone(String phone) {
        Editor editor = getPreferences(WangYuApplication.appContext).edit();
        editor.putString("phone", phone);
        editor.commit();
    }

    public static String getPhone() {
        return getPreferences(WangYuApplication.appContext).getString("phone", "");
    }

    public static void saveLive(String live) {
        Editor editor = getPreferences(WangYuApplication.appContext).edit();
        editor.putString("live", live);
        editor.commit();
    }

    public static String getLive() {
        return getPreferences(WangYuApplication.appContext).getString("live", "");
    }

}
