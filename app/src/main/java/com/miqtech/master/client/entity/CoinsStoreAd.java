package com.miqtech.master.client.entity;

/**
 * 金币商城顶部的广告  Bean
 * @author Administrator
 */
public class CoinsStoreAd {
	
	int id;//广告ID
	int type;//广告类型：1-任务，2-邀请，3-商品，4-外连接
	String url;//当type为1-3时，需要拼接地址；当type=4，直接使用url当跳转地址
	int targetId;//目标ID
	String banner;//广告banner
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getTargetId() {
		return targetId;
	}
	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}
	public String getBanner() {
		return banner;
	}
	public void setBanner(String banner) {
		this.banner = banner;
	}
	

}
