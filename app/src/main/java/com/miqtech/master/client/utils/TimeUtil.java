package com.miqtech.master.client.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@SuppressLint("SimpleDateFormat")
public class TimeUtil {
    final static SimpleDateFormat HHMM = new SimpleDateFormat("HH:mm");
    final static SimpleDateFormat MMDDHHMM = new SimpleDateFormat("MM月dd日 HH:mm");
    final static SimpleDateFormat YYYYMMDDHHMM = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

    public static long calcSecondBetweenNowAndDate(Date now, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        long nowSecond = cal.getTimeInMillis();
        cal.setTime(date);
        long dateSecond = cal.getTimeInMillis();
        return (dateSecond - nowSecond) / 1000;
    }

    /**
     * 计算日期和当前日期之间的天数
     */
    public static int calcDayBetweenDateAndNow(Date date) {
        if (null == date) {
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        long time1 = cal.getTimeInMillis();
        cal.setTime(new Date());
        long time2 = cal.getTimeInMillis();
        return Integer.valueOf(String.valueOf((time2 - time1) / (1000 * 3600 * 24)));
    }

    // /**
    // * 友好的时间显示
    // */
    // public static String friendlyTime(String datetime) {
    // SimpleDateFormat dateFormat = new
    // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // Date date = null;
    // try {
    // date = dateFormat.parse(datetime);
    // } catch (ParseException e) {
    // e.printStackTrace();
    // }
    // if (null == date) {
    // return "";
    // }
    // int calcDayBetweenDateAndNow = TimeUtil.calcDayBetweenDateAndNow(date);
    // if (calcDayBetweenDateAndNow == 0) {
    // int ct = (int) ((System.currentTimeMillis() - date.getTime()) / 1000);
    // if (ct < 3600) {
    // return Math.max(ct / 60, 1) + "分钟前";
    // }
    // if (ct >= 3600 && ct < 86400) {
    // return ct / 3600 + "小时前";
    // }
    // return "";
    // } else {
    // SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    // if (calcDayBetweenDateAndNow == 1) {
    // return "昨天 " + sdf.format(date);
    // }
    // if (calcDayBetweenDateAndNow == 2) {
    // return "前天 " + sdf.format(date);
    // }
    // sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    // return sdf.format(date);
    // }
    //
    // }

    public static String friendlyTime(String datetime) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = null;
        try {
            time = dateFormat.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date currentDate = new Date();
        if (null == time) {
            return "";
        }
        int ct = (int) ((System.currentTimeMillis() - time.getTime()) / 1000);
        if (ct < 60) {
            return "刚刚";
        }
        if (ct < 3600) {
            return Math.max(ct / 60, 1) + "分钟前";
        }
        if (ct >= 3600 && ct < 86400) {
            if (time.getDay() == currentDate.getDay()) {
                return "今天" + HHMM.format(time);
            }
//			else {
//				return "昨天" + HHMM.format(time);
//			}

        }
//		if (ct >= 86400 && ct < 86400 * 2) { // 86400 * 30
//			
//			return "昨天" + HHMM.format(time);
//		}
//
        if (ct >= 86400 * 3 && ct < 31104000) { // 86400 * 30
            return MMDDHHMM.format(time);
        }

        return YYYYMMDDHHMM.format(time);
    }

    /**
     * 两个时间 相差的天数
     */
    public static String calculateYears(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date1 = null;
        try {
            date1 = sdf.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GregorianCalendar cal1 = new GregorianCalendar();
        cal1.setTime(date1);

        double yearCount = (System.currentTimeMillis() - cal1.getTimeInMillis()) / 31104000000d;
        return (int) yearCount + "";
    }

    /**
     * 毫秒转时间
     *
     * @param ms
     * @return
     */
    public static String calculateTime(long ms) {
        long secound = ms / 1000;
        if (secound <= 60) {
            return secound + "秒";
        } else {
            long m = secound / 60;
            if (m < 60) {
                return m + "分钟" + (secound - m * 60) + "秒";
            } else {
                if (secound / 60 / 60 < 24) {
                    long h = secound / 60 / 60;
                    long rm = (secound - h * 3600) / 60;
                    return h + "小时" + rm + "分钟" + (secound - h * 3600 - rm * 60) + "秒";
                } else {
                    long d = secound / 60 / 60 / 24;
                    long rh = secound % (3600 * 24) / 3600;
                    long rm = (secound - d * 3600 * 24 - rh * 3600) / 60;
                    return d + "        天" + rh + "小时" + rm + "分钟" + (secound - 24 * d * 3600 - rh * 3600 - rm * 60) + "秒";
                }
            }
        }
    }
}
