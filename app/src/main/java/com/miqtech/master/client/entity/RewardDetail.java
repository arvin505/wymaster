package com.miqtech.master.client.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 获奖详情
 * Created by zhaosentao on 2016/8/3.
 */
public class RewardDetail {

    //    bounty_id	悬赏令id	number
//    grade	成绩	number
//    icon	头像	string
//    infoStatus	用户信息是否填写	number	0-未填写 1-已填写
//    nickname	昵称	string
//    ranking	成绩排名	number	当type为3 即悬赏令类型为排行榜时返回该字段，其他情况无该字段
//    status	奖励发放状态	number	-1未填写信息 0-兑换中，1 发放成功，2-奖励信息过期
//    tran_no	交易号	string
//    type	悬赏令类型	number	3-排行榜
//    user_id	用户id	number
    @SerializedName("bounty_id")
    private int bountyId;
    private String grade;
    private String icon;
    private int infoStatus;
    private String nickname;
    private String ranking;
    private int status = -1;
    private String tran_no;
    private int type;
    @SerializedName("user_id")
    private String userId;
    @SerializedName("title")
    private String matchName;//赛事名称
    @SerializedName("award_name")
    private String awardName;//奖品名称
    private int historyId;//兑奖id
    @SerializedName("commodity_type")
    private String commodityType;//	1自有商品2实物3流量话费4虚拟充值(除去流量和话费)


    public int getBountyId() {
        return bountyId;
    }

    public void setBountyId(int bountyId) {
        this.bountyId = bountyId;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getInfoStatus() {
        return infoStatus;
    }

    public void setInfoStatus(int infoStatus) {
        this.infoStatus = infoStatus;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTran_no() {
        return tran_no;
    }

    public void setTran_no(String tran_no) {
        this.tran_no = tran_no;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public String getAwardName() {
        return awardName;
    }

    public void setAwardName(String awardName) {
        this.awardName = awardName;
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }


    public String getCommodityType() {
        return commodityType;
    }

    public void setCommodityType(String commodityType) {
        this.commodityType = commodityType;
    }
}
