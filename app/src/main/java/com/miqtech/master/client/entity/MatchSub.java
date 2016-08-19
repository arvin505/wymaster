package com.miqtech.master.client.entity;


public class MatchSub extends Match{
	String activity_icon;//赛事图标
	String title; 	//赛事标题
	String create_date;//赛事开始时间
	int round;//场次
	String netbar_name;//地点
	public String getActivity_icon() {
		return activity_icon;
	}
	public void setActivity_icon(String activity_icon) {
		this.activity_icon = activity_icon;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCreate_date() {
		return create_date;
	}
	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}
	public int getRound() {
		return round;
	}
	public void setRound(int round) {
		this.round = round;
	}
	public String getNetbar_name() {
		return netbar_name;
	}
	public void setNetbar_name(String netbar_name) {
		this.netbar_name = netbar_name;
	}
	
	
}
