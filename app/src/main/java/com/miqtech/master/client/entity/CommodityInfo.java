package com.miqtech.master.client.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by admin on 2016/5/12.
 */
public class CommodityInfo implements Serializable {
    private int coins;//众筹总金币
    private String introduce;//商品详情
    private String mainIcon;//商品图片
    private String rule;//使用规则

    @Override
    public String toString() {
        return "CommodityInfo{" +
                "coins=" + coins +
                ", introduce='" + introduce + '\'' +
                ", mainIcon='" + mainIcon + '\'' +
                ", rule='" + rule + '\'' +
                ", id=" + id +
                ", commodityName='" + commodityName + '\'' +
                ", imgs='" + imgs + '\'' +
                ", cdkey='" + cdkey + '\'' +
                ", progress=" + progress +
                ", crowdfundStatus=" + crowdfundStatus +
                ", leftNum=" + leftNum +
                ", buyNum=" + buyNum +
                '}';
    }

    private int id;//商品id
    private String commodityName;//商品名称
    private String imgs; //商品图片
    private String cdkey; //兑换码
    private float progress;
    @SerializedName("prize_phone")
    private String prizePhone;//电话号码

    public String getPrizePhone() {
        return prizePhone;
    }

    public void setPrizePhone(String prizePhone) {
        this.prizePhone = prizePhone;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public int getCrowdfundStatus() {

        return crowdfundStatus;
    }

    public void setCrowdfundStatus(int crowdfundStatus) {
        this.crowdfundStatus = crowdfundStatus;
    }

    @SerializedName("crowdfund_status")
    private int crowdfundStatus;

    @SerializedName("left_num") //剩余产品数量
    private int leftNum;

    public int getLeftNum() {
        return leftNum;
    }

    public void setLeftNum(int leftNum) {
        this.leftNum = leftNum;
    }

    public String getCdkey() {
        return cdkey;
    }

    public void setCdkey(String cdkey) {
        this.cdkey = cdkey;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public int getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(int buyNum) {
        this.buyNum = buyNum;
    }

    @SerializedName("buy_num")
    private int buyNum;//购买数量

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getMainIcon() {
        return mainIcon;
    }

    public void setMainIcon(String mainIcon) {
        this.mainIcon = mainIcon;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

}
