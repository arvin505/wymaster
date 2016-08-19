package com.miqtech.master.client.utils;

import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/11/26.
 * 格式化时间字符串
 */
public class TimeFormatUtil {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat returnFormatNoTime = new SimpleDateFormat("yyyy/MM/dd");
    private static SimpleDateFormat returnFormatNoYear = new SimpleDateFormat("MM/dd HH:mm");
    private static SimpleDateFormat returnFormatNoYear2 = new SimpleDateFormat("MM.dd HH:mm");
    private static SimpleDateFormat returnFormatNoYearNoTime = new SimpleDateFormat("MM/dd");
    private static SimpleDateFormat returnFormatNoSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    final static SimpleDateFormat HHMM = new SimpleDateFormat("HH:mm");
    final static SimpleDateFormat MMDDHHMM = new SimpleDateFormat("MM月dd日 HH:mm");
    final static SimpleDateFormat YYYYMMDDHHMM = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");


    /**
     * @param timeStr
     * @return
     */
    public static String formatNoTime(String timeStr) {
        if (timeStr == null) {
            return "";
        }
        try {
            Date date = dateFormat.parse(timeStr);
            return returnFormatNoTime.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return timeStr;
        }
    }

    public static String formatNoYear(String timeStr) {
        if (timeStr == null) {
            return "";
        }
        try {
            Date date = dateFormat.parse(timeStr);
            return returnFormatNoYear.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return timeStr;
        }
    }

    public static String formatNoYear2(String timeStr) {
        if (timeStr == null) {
            return "";
        }
        try {
            Date date = dateFormat.parse(timeStr);
            return returnFormatNoYear2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return timeStr;
        }
    }

    public static String formatNoSecond(String timeStr) {
        if (timeStr == null) {
            return "";
        }
        try {
            Date date = dateFormat.parse(timeStr);
            return returnFormatNoSecond.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return timeStr;
        }
    }

    public static String friendlyTime(String datetime) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = null;
        try {
            time = dateFormat.parse(datetime);
        } catch (Exception e) {
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

        }
        if (ct >= 86400 * 3 && ct < 31104000) { // 86400 * 30
            return MMDDHHMM.format(time);
        }
        return YYYYMMDDHHMM.format(time);
    }

    public static int differentTime(String datetime) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = null;
        try {
            time = dateFormat.parse(datetime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == time) {
            return 0;
        }
        int ct = (int) ((System.currentTimeMillis() - time.getTime()) / 1000);
        return ct;
    }

    public static String formatNoYearNoTime(String timeStr) {
        if (timeStr == null) {
            return "";
        }
        try {
            Date date = dateFormat.parse(timeStr);
            return returnFormatNoYearNoTime.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return timeStr;
        }
    }

    public static String mill2Date(long currentMill) {
        try {
            Date date = new Date(currentMill);
            return dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatMMDDHHMM(String time) {
        if (time == null) {
            return "";
        }
        try {
            Date date = dateFormat.parse(time);
            return MMDDHHMM.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return time;
        }
    }

    public static String formatWithMatchStr(String time, String matchStr) {
        if (time == null) {
            return "";
        }
        try {
            Date date = dateFormat.parse(time);
            return new SimpleDateFormat(matchStr).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return time;
        }
    }

    public static String secToTime(int time) {
        String timeStr = null;
        int day = 0; //天
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
//                if (hour > 99)
//                    return "99:59:59";
//                minute = minute % 60;
//                second = time - hour * 3600 - minute * 60;
//                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
                if (hour < 24) {
                    minute = minute % 60;
                    second = time - hour * 3600 - minute * 60;
                    timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
                } else {
                    day = hour / 24;
                    hour = hour % 24;
                    minute = minute % 60;
                    second = time - day * 3600 * 24 - hour * 3600 - minute * 60;
                    timeStr = unitFormat(day) + "天 " + unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
                }
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

}
