package com.miqtech.master.client.entity;

/**
 * Created by Administrator on 2015/12/7 0007.
 */
public class RecreationMatchInfo {
//    "way": 1,
//            "endDate": "2015-11-08 17:31:27",
//            "timeStatus": 3,
//            "type": 2,
//            "mainIcon": "uploads/imgs/amuse/2015/11/10/a5707ae1611a4d288cfb9151ff67099c.jpg",
//            "id": 7,
//            "applyNum": 1,
//            "distance": 0,
//            "title": "娱乐赛事test3",
//            "areaName": "萧山区",
//            "rule": "修改",
//            "netbarName": "杭州潘多拉网吧",
//            "maxNum": 2

    String startDate;
    String reward;
    int way;
    String endDate;
    int timeStatus;
    int type;
    String mainIcon;
    int id;
    int applyNum;
    int distance;
    String title;
    String areaName;
    String rule;
    String netbarName;
    int maxNum;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public int getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(int applyNum) {
        this.applyNum = applyNum;
    }

    public int getWay() {
        return way;
    }

    public void setWay(int way) {
        this.way = way;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getTimeStatus() {
        return timeStatus;
    }

    public void setTimeStatus(int timeStatus) {
        this.timeStatus = timeStatus;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMainIcon() {
        return mainIcon;
    }

    public void setMainIcon(String mainIcon) {
        this.mainIcon = mainIcon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getNetbarName() {
        return netbarName;
    }

    public void setNetbarName(String netbarName) {
        this.netbarName = netbarName;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

}
