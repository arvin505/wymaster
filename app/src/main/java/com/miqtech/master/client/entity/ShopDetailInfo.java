package com.miqtech.master.client.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ShopDetailInfo implements Serializable{
	@SerializedName("commodity_info")
	private  CommodityInfo commodityInfo;  //商品的基本信息
	@SerializedName("buy_record")
	private BuyRecordInfo buyRecord; //购买记录集合

	public CommodityInfo getCommodityInfo() {
		return commodityInfo;
	}

	public void setCommodityInfo(CommodityInfo commodityInfo) {
		this.commodityInfo = commodityInfo;
	}

	public BuyRecordInfo getBuyRecord() {
		return buyRecord;
	}

	public void setBuyRecord(BuyRecordInfo buyRecord) {
		this.buyRecord = buyRecord;
	}

	@Override
	public String toString() {
		return "ShopDetailInfo{" +
				"commodityInfo=" + commodityInfo +
				", buyRecord=" + buyRecord +
				'}';
	}
}
