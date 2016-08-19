package com.miqtech.master.client.entity;


import java.io.Serializable;

/**
 * v3.1.5战队场次二维码接口(施亦珎)
 * Created by zhaosentao on 2016/5/11.
 */
public class ActivityQrcode implements Serializable {

    private ActivityInfo activityInfo;
    private ActivityCard card;
    private String netbarId;//网吧id
    private String id;//赛事活动id
    private String round;//场次
    private String teamId;//战队id
    private int typeApply = -1;//0个人报名，1创建临时战队，2加入临时战队,3战队报名,4加入战队(确认参赛卡和确认参赛信息)。(固定不变的)
    private int isMatch=0;//是否是自发赛  1是 0否

    public int getIsMatch() {
        return isMatch;
    }

    public void setIsMatch(int isMatch) {
        this.isMatch = isMatch;
    }




    public ActivityInfo getActivityInfo() {
        return activityInfo;
    }

    public void setActivityInfo(ActivityInfo activityInfo) {
        this.activityInfo = activityInfo;
    }

    public ActivityCard getCard() {
        return card;
    }

    public void setCard(ActivityCard card) {
        this.card = card;
    }

    public String getNetbarId() {
        return netbarId;
    }

    public void setNetbarId(String netbarId) {
        this.netbarId = netbarId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public int getTypeApply() {
        return typeApply;
    }

    public void setTypeApply(int typeApply) {
        this.typeApply = typeApply;
    }
}
