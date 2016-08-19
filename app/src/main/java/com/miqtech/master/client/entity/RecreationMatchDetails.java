package com.miqtech.master.client.entity;

import java.util.List;

/**
 * Created by wuxuenan on 2015/11/30 0030.
 */
public class RecreationMatchDetails {

    //    itemName	是	string	游戏名称
//    itemIcon	是	string	游戏图标url
//    title	是	string	娱乐赛标题
//    virtual_apply	是	string	虚拟报名人数
//    reward	是	string	奖励说明
//    startDate	是	string	娱乐赛开始时间
//    endDate	是	string	娱乐赛结束时间
//    applyNum	是	int	报名人数
//    verifyContent	是	string	认证内容
//    banner	是	string	banner图的url
//    maxNum	是	string	最多支持报名数量(0为无限制)
//    server	是	string	服务器
//    rule	是	string	比赛规则
//    applyerList	是	array	报名者列表（最多10个）
//    timeStatus	是	int	赛事时间状态：0-报名即将开始，1-报名中，2-进行中，3-赛事已结束，4-提交已截止
//    applyStatus	是	int	用户报名状态：-1-未登录或登录失效，0-未报名，1-已报名/未提交认证，2-已提交认证
    int id;
    String itemName;
    String itemIcon;
    String title;
    int virtual_apply;
    String reward;
    String startDate;
    String endDate;
    int applyNum;
    String verifyContent;
    String banner;
    int maxNum;
    String server;
    String rule;
    List<MatchJoiner> applyerList;
    int timeStatus;
    int applyStatus;
    String way;
    String netbarId;
    String longitude;
    String latitude;
    String netbarName;
    private int has_favor;

    public int getHas_favor() {
        return has_favor;
    }

    public void setHas_favor(int has_favor) {
        this.has_favor = has_favor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemIcon() {
        return itemIcon;
    }

    public void setItemIcon(String itemIcon) {
        this.itemIcon = itemIcon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getVirtual_apply() {
        return virtual_apply;
    }

    public void setVirtual_apply(int virtual_apply) {
        this.virtual_apply = virtual_apply;
    }

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

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(int applyNum) {
        this.applyNum = applyNum;
    }

    public String getVerifyContent() {
        return verifyContent;
    }

    public void setVerifyContent(String verifyContent) {
        this.verifyContent = verifyContent;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public List<MatchJoiner> getApplyerList() {
        return applyerList;
    }

    public void setApplyerList(List<MatchJoiner> applyerList) {
        this.applyerList = applyerList;
    }

    public int getTimeStatus() {
        return timeStatus;
    }

    public void setTimeStatus(int timeStatus) {
        this.timeStatus = timeStatus;
    }

    public int getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(int applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public String getNetbarId() {
        return netbarId;
    }

    public void setNetbarId(String netbarId) {
        this.netbarId = netbarId;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getNetbarName() {
        return netbarName;
    }

    public void setNetbarName(String netbarName) {
        this.netbarName = netbarName;
    }
}
