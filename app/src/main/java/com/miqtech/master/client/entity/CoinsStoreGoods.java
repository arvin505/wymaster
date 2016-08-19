package com.miqtech.master.client.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 金币商城：金币专区和抽奖区每一个item的数据  Bean
 *
 * @author Administrator
 */
public class CoinsStoreGoods implements Serializable {


    int areaId;//商品区ID
    String areaName;//商品区名
    int price;//商品原价，单位：金币
    String mainIcon;//商品图标（主）
    int discountPrice;//商品现价（折后价）,单位：金币
    int id;//商品ID
    String categoryName;// 商品类别名
    int categoryId;//商品类别ID
    String commodityName;//商品名

    @SerializedName("crowdfund_status")
    int crowdfundStatus;    //众筹状态:0-众筹中,1-等待开奖,2-已开奖
    @SerializedName("left_num")
    int leftNum; //剩余购买次数
    @SerializedName("prize_phone")
    String prizePhone;   //中奖号码

    int sortNo;

    public int getSortNo() {
        return sortNo;
    }

    public void setSortNo(int sortNo) {
        this.sortNo = sortNo;
    }

    public int getCrowdfundStatus() {
        return crowdfundStatus;
    }

    public void setCrowdfundStatus(int crowdfundStatus) {
        this.crowdfundStatus = crowdfundStatus;
    }

    public int getLeftNum() {
        return leftNum;
    }

    public void setLeftNum(int leftNum) {
        this.leftNum = leftNum;
    }

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

    float progress;   //进度 单位（%）


    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getMainIcon() {
        return mainIcon;
    }

    public void setMainIcon(String mainIcon) {
        this.mainIcon = mainIcon;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(int discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

}
