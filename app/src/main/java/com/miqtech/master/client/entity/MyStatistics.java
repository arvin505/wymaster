package com.miqtech.master.client.entity;

/**
 * Created by Administrator on 2015/12/7.
 */
public class MyStatistics {
    // fansTotal 该用户的粉丝总数
    // concernTotal 该用户关注的人总数
    // activityTotal 该用户的活动赛事总数
    // activityInviteCount 赛事邀请数
    // activityMatchInviteCount 约战邀请数
    // unEvaOrderCount 我的订单未评价数量
    int fansTotal;
    int concernTotal;
    int activityTotal;
    int activityInviteCount;
    int activityMatchInviteCount;
    int unEvaOrderCount;

    public int getFansTotal() {
        return fansTotal;
    }

    public void setFansTotal(int fansTotal) {
        this.fansTotal = fansTotal;
    }

    public int getConcernTotal() {
        return concernTotal;
    }

    public void setConcernTotal(int concernTotal) {
        this.concernTotal = concernTotal;
    }

    public int getActivityTotal() {
        return activityTotal;
    }

    public void setActivityTotal(int activityTotal) {
        this.activityTotal = activityTotal;
    }

    public int getActivityInviteCount() {
        return activityInviteCount;
    }

    public void setActivityInviteCount(int activityInviteCount) {
        this.activityInviteCount = activityInviteCount;
    }

    public int getActivityMatchInviteCount() {
        return activityMatchInviteCount;
    }

    public void setActivityMatchInviteCount(int activityMatchInviteCount) {
        this.activityMatchInviteCount = activityMatchInviteCount;
    }

    public int getUnEvaOrderCount() {
        return unEvaOrderCount;
    }

    public void setUnEvaOrderCount(int unEvaOrderCount) {
        this.unEvaOrderCount = unEvaOrderCount;
    }
}
