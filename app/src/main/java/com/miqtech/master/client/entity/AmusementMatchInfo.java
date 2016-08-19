package com.miqtech.master.client.entity;

import java.util.List;

/**
 * Created by Administrator on 2015/11/27.
 */
public class AmusementMatchInfo {

    private String id;//string	娱乐赛ID
    private String type	;//	string	娱乐赛类型，1-官网线上；2-官方线下；3-官网网吧
    private int way	;//	string	方式：1-线下；2-线上
    private String summary	;//	string	摘要
    private String reward; //		string	奖励说明
    private String rule	;//	string	规则
    private String mainIcon; // string	图片地址
    private String applyNum	; //	string	报名人数
    private String maxNum; //	string	报名人数上限
    private String title	; //	string	标题
    private String subTitle	;//	string	副标题
    private String netbarName	;//	string	网吧名
    private String areaName	;//	string	地区名
    private int timeStatus	;//	string	时间状态：0-报名即将开始，1-报名中，2-进行中，3-赛事已结束
    private String startDate; //	string	比赛开始时间
    private String endDate	; //	比赛结束时间
    private List<User> applyerList;
    private String banner ;
    private String server;
    private int applyStatus;
    private int virtual_apply;

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }


    public List<User> getApplyerList() {
        return applyerList;
    }

    public void setApplyerList(List<User> applyerList) {
        this.applyerList = applyerList;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWay() {
        return way;
    }

    public void setWay(int way) {
        this.way = way;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getMainIcon() {
        return mainIcon;
    }

    public void setMainIcon(String mainIcon) {
        this.mainIcon = mainIcon;
    }

    public String getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(String applyNum) {
        this.applyNum = applyNum;
    }

    public String getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(String maxNum) {
        this.maxNum = maxNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getNetbarName() {
        return netbarName;
    }

    public void setNetbarName(String netbarName) {
        this.netbarName = netbarName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getTimeStatus() {
        return timeStatus;
    }

    public void setTimeStatus(int timeStatus) {
        this.timeStatus = timeStatus;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
