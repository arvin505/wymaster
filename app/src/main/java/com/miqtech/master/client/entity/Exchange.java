package com.miqtech.master.client.entity;

import com.google.gson.annotations.SerializedName;

public class Exchange {
    String id;
    String date;
    String icon;
    String commodityId;
    String name;

    @SerializedName("create_date")
    String createDate; //	时间
    int state;  //type=2(众筹夺宝)0未开奖1开奖中2未中奖3已中奖4兑奖中5兑奖成功6兑奖失败7异常8异常已处理;
    // type=1或3(大转盘或兑奖专区):0未兑奖1兑奖中2兑奖成功3兑奖失败4异常5异常已处理
    int type;   // 1大转盘抽奖2众筹夺宝3兑奖专区

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
