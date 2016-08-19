package com.miqtech.master.client.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExchangeInfo {
// 	"id":1,
//	"commodityId":1,
//	"coin":20,
//	"name":"2元红包",
//	"information":"成功交易信息",
//	"date":"2015-09-17 10:04:36",
//	"tranNo":"2015090802001716459814552",
//	"status":1,

//	id 兑换历史ID 
//	commodityId 商品ID 
//	coin 交易价格（金币） 
//	name 商品名 
//	information 交易信息 
//	date 交易时间 
//	tranNo 交易流水号 
//	status 交易状态：0-待处理，1-已处理，2-异常，3-异常已处理 

    String id;
    @SerializedName("commodity_id")
    String commodityId;
    String coin;
    String name;
    String infomation;
    String date;
    @SerializedName("tran_no")
    String tranNo;
    @SerializedName("commodity_type")  //1自有商品2实物3流量话费4虚拟充值(除去流量和话费)
            int commodityType;

    @SerializedName("exception_disabled")
    int exceptionDisabled;  //兑换奖品异常按钮是否失效1是0否

    public int getExceptionDisabled() {
        return exceptionDisabled;
    }

    public void setExceptionDisabled(int exceptionDisabled) {
        this.exceptionDisabled = exceptionDisabled;
    }

    public int getHasAddress() {
        return hasAddress;
    }

    public void setHasAddress(int hasAddress) {
        this.hasAddress = hasAddress;
    }

    public int getCommodityType() {
        return commodityType;
    }

    public void setCommodityType(int commodityType) {
        this.commodityType = commodityType;
    }

    @SerializedName("has_address")
    int hasAddress;//是否填写收货信息1是0否
    int status;
    List<ExchangeInfoIcon> icons;

    String cdkeys;    //多个众筹夺宝的兑换码用逗号隔开
    int type;     // 1大转盘抽奖2众筹夺宝3兑奖专区
    int state;    //type=2(众筹夺宝)0未开奖1开奖中2未中奖3已中奖4兑奖中5兑奖成功6兑奖失败7异常8异常已处理9失效;type=1或3(大转盘或兑奖专区):0未兑奖1兑奖中2兑奖成功3兑奖失败4异常5异常已处理6失效

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCdkeys() {
        return cdkeys;
    }

    public void setCdkeys(String cdkeys) {
        this.cdkeys = cdkeys;
    }

    public String getInfomation() {
        return infomation;
    }

    public void setInfomation(String infomation) {
        this.infomation = infomation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTranNo() {
        return tranNo;
    }

    public void setTranNo(String tranNo) {
        this.tranNo = tranNo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ExchangeInfoIcon> getIcons() {
        return icons;
    }

    public void setIcons(List<ExchangeInfoIcon> icons) {
        this.icons = icons;
    }

}
