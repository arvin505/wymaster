package com.miqtech.master.client.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * 友盟自定义事件统计
 * 1、统计行为的点击量
 * 2、统计行为的停留或使用时间
 * Created by Administrator on 2016/4/20.
 */
public class UMengStatisticsUtil {

    /**
     * 统计行为点击量
     *
     * @param context
     * @param eventId 时间ID
     * @param keySet
     * @param values
     */
    public static void statisticsCLick(Context context, String eventId, String keySet, String values) {
        if (isCount) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(keySet, values);
            MobclickAgent.onEvent(context, eventId, map);
        }
    }

    /**
     * 统计行为停留或使用时间
     *
     * @param context
     * @param eventId  事件id
     * @param keySet
     * @param values
     * @param duration 停留或使用时间（得自己算好）
     */
    public static void statisticsStayTime(Context context, String eventId, String keySet, String values, int duration) {
        if (isCount) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(keySet, values);
            MobclickAgent.onEventValue(context, eventId, map, duration);
        }
    }

    /**
     * 是否统计自定义事件的数量
     */
    public static final boolean isCount = true;//是否统计自定义事件的数量
    public static final String WANG_FEI = "WANG_FEI";
//    /*网吧详情*/
//    public static final String INTERNET_BAE = "internet_bar";//网吧详情点击量,时间ID
//    public static final String INTERNET_BAR_CLICK = "网吧模块点击量";//keySet
//    public static final String[] internetBarClickType = {"网吧详情点击量0", "普通网吧点击量1", "会员网吧点击量2", "黄金网吧点击量3", "钻石网吧点击量4",
//            "基础信息点击量5", "竞技活动点击量6", "评价点击量7", "支付点击量8", "确认支付点击量9", "支付成功点击量10", "取消支付点击量11",
//            "收藏点击量12", "取消收藏点击量13"};
//
//    public static final String INTERNET_BAR_TIME = "internet_bar_time";//网吧模块停留时间,时间ID
//    public static final String INTERNET_BAR_TIME_STAY = "网吧模块使用时长";
//    public static final String[] internetBatTimeStay = {"网吧列表停留时长0", "网吧详情停留时长1", "网吧功能使用时长2", "基本信息停留时间3",
//            "竞技活动停留时间4", "评价停留时间5"};//values
//
//
//    /*金币商城*/
//    public static final String GOLD_MALL = "gold_mall";//金币商城点击量计数,事件ID
//    public static final String GOLD_MALL_CLICK = "金币商城模块的点击量";//参数键
//    public static final String[] goldMallClick = {"签到点击量", "兑换记录点击量", "金币任务点击量", "金币专区点击量", "小抽怡情点击量",
//            "点击立即兑换点击量", "点击立即提交点击量"};//参数键值
//
//    public static final String GOLD_MALL_TIME = "gold_mall_time";//金币商城停留时间，时间id
//    public static final String GOLD_MALL_TIME_STAY = "金币商城模块的使用时长";//参数键
//    public static final String[] goldMallTimeType = {"金币商城使用时间0", "商品详情页使用时间1", "金币专区详情页停留时间2", "小抽详情页停留时间3",
//            "签到页面停留时长4", "金币任务停留时间5"};//参数键值


    /**
     * 当离开app超过30秒重新计算使用时长并提交原先的使用使用
     */
    public static final int LEAVER_APP_TIME = 30000;



    /**
     * 是否离开过app去别的app
     */
    public static boolean isLeaveApp = false;

    /**
     * 进入时间,格式yyyy-MM-dd HH:mm:ss
     */
    public static String inTime;
    /**
     * 离开时间,格式yyyy-MM-dd HH:mm:ss
     */
    public static String outTime;


    /**
     * app平均使用时长
     */
    public static final String CODE_0000 = "0000";
    /**
     * 资讯模块平均使用时长
     */
    public static final String CODE_1000 = "1000";
    /**
     * 资讯一级模块人均使用时长
     */
    public static final String CODE_1001 = "1001";
    /**
     * 资讯二级模块人均使用时长
     */
    public static final String CODE_1002 = "1002";
    /**
     * 每个资讯人均使用时长
     */
    public static final String CODE_1003 = "1003";
    /**
     * 资讯评论区人均使用时长
     */
    public static final String CODE_1004 = "1004";
    /**
     * 网吧人均使用时长
     */
    public static final String CODE_2000 = "2000";
    /**
     * 网吧列表人均停留时长
     */
    public static final String CODE_2001 = "2001";
    /**
     * 网吧详情人均使用时长
     */
    public static final String CODE_2002 = "2002";
    /**
     * 网吧基本信息人均使用时长
     */
    public static final String CODE_2003 = "2003";
    /**
     * 网吧竞技活动人均使用时长
     */
    public static final String CODE_2004 = "2004";
    /**
     * 网吧评价人均停留时长
     */
    public static final String CODE_2005 = "2005";
    /**
     * 2014	支付网费点击量
     */
    public static final String CODE_2014 = "2014";
    /**
     * 金币商城人均使用时间
     */
    public static final String CODE_3000 = "3000";
    /**
     * 金币商城列表人均使用时长
     */
    public static final String CODE_3001 = "3001";
    /**
     * 金币商城商品详情页人均使用时长
     */
    public static final String CODE_3002 = "3002";
    /**
     * 刮奖游戏人均使用时长
     */
    public static final String CODE_3003 = "3003";
    /**
     * 签到人均使用时长
     */
    public static final String CODE_3004 = "3004";
    /**
     * 金币任务人均使用时长
     */
    public static final String CODE_3005 = "3005";
    /**
     * 小抽怡情人均使用时长
     */
    public static final String CODE_3006 = "3006";
    /**
     * 金币专区人均使用时长
     */
    public static final String CODE_3007 = "3007";
    /**
     * 娱乐赛评价使用时长
     */
    public static final String CODE_4000 = "4000";
    /**
     * 官方赛人均使用时长
     */
    public static final String CODE_5000 = "5000";
    /**
     * 约战人均使用时长
     */
    public static final String CODE_6000 = "6000";

}
