package com.miqtech.master.client.jpush.service;

import com.miqtech.master.client.adapter.TowardTAYueZhanAdapter;
import com.miqtech.master.client.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class PushType {
    private final static String SYS = "sys";
    private final static String REDBAG = "redbag";
    private final static String RESERVATION = "reservation";
    private final static String PAY = "pay";
    private final static String YUEZHAN = "match";
    private final static String MATCH = "activity";
    private final static String NETBAR = "netbar";
    private final static String WEEK_REDBAG = "redbag_weekly";
    private final static String KEY = "wycategory";
    private final static String OBJECT_KEY = "wyobject";
    private final static String AMUSE = "amuse";
    private final static String COMMENT = "comment";
    private final static String INFORMATION = "information";
    private final static String COMMODITY = "commodity";//金币商城兑换
    private final static String ROBTREASURE = "robtreasure";//商品众筹
    private final static String AWARD_COMMDITY = "award_commodity";//商品详情
    private final static String MALL = "mall";//金币商城
    private final static String COIN_TASK = "coin_task";//金币任务
    private final static String OET = "oet";//自发赛
    private final static String BOUNTY = "bounty";//悬赏令

    public final static int SYS_ID = 0;// 系统消息
    public final static int REDBAG_ID = 1;// 红包消息
    public final static int RESERVATION_ID = 2;// 预定消息
    public final static int PAY_ID = 3;// 支付消息
    public final static int YUEZHAN_ID = 4;// 约战消息
    public final static int MATCH_ID = 5;// 比赛消息
    public final static int NETBAR_ID = 6;// 网吧消息
    public final static int WEEK_REDBAG_ID = 7;// 周红包消息
    public final static int COMMENT_ID = 8;// 评论推送
    public final static int AMUSE_ID = 9;//娱乐赛推送
    public final static int INFORMATION_ID = 10;//资讯推送
    public final static int COMMODITY_ID = 11;//金币商城兑换
    public final static int ROBTREASURE_ID = 12;//商品众筹
    public final static int AWARD_COMMDITY_ID = 13;//商品详情
    public final static int MALL_ID = 14;//金币商城
    public final static int COIN_TASK_ID = 15;//金币任务
    public final static int OET_ID = 16;//自发赛
    public final static int BOUNTY_ID = 17;//悬赏令

    public JSONObject jsonObject;
    private String value;

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public int getPushType() {
        int type = -1;
        try {
            value = jsonObject.getString(KEY);
        } catch (JSONException e) {
            e.printStackTrace();
            return type;
        }
        if (jsonObject != null && jsonObject.has(KEY)) {
            if (value.equals(SYS)) {
                type = SYS_ID;
            }
            if (value.equals(REDBAG)) {
                type = REDBAG_ID;
            }
            if (value.equals(RESERVATION)) {
                type = RESERVATION_ID;
            }
            if (value.equals(PAY)) {
                type = PAY_ID;
            }
            if (value.equals(YUEZHAN)) {
                type = YUEZHAN_ID;
            }
            if (value.equals(MATCH)) {
                type = MATCH_ID;
            }
            if (value.equals(NETBAR)) {
                type = NETBAR_ID;
            }
            if (value.equals(WEEK_REDBAG)) {
                type = WEEK_REDBAG_ID;
            }
            if (value.equals(COMMENT)) {
                type = COMMENT_ID;
            }
            if (value.equals(AMUSE)) {
                type = AMUSE_ID;
            }
            if (value.equals(INFORMATION)) {
                type = INFORMATION_ID;
            }
            if (value.equals(COMMODITY)) {
                type = COMMODITY_ID;
            }
            if (value.equals(ROBTREASURE)) {
                type = ROBTREASURE_ID;
            }
            if (value.equals(AWARD_COMMDITY)) {
                type = AWARD_COMMDITY_ID;
            }
            if (value.equals(MALL)) {
                type = MALL_ID;
            }
            if (value.equals(COIN_TASK)) {
                type = COIN_TASK_ID;
            }
            if (value.equals(OET)) {
                type = OET_ID;
            }
            if (value.equals(BOUNTY)) {
                type = BOUNTY_ID;
            }
        }
        return type;
    }

    public String getObjectId() {
        String objectId = "-1";
        try {
            objectId = jsonObject.getString(OBJECT_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
            return objectId;
        }
        return objectId;
    }
}
