package com.miqtech.master.client.entity;

/**
 * 查看用户被邀请的约战列表 ：数据
 * @author Administrator
 *
 */
public class ByInvateYueZhan {
//	
//    "server": "test",
//    "itemName": "英雄联盟",
//    "inviteId": 55,
//    "itemIcon": "uploads/imgs/activity/items/lol.png",
//    "remark": "1",
//    "title": "test",
//    "userIcon": "uploads/imgs/user/random/4.jpg",
//    "matchId": 416
	
//	matchId 约战ID 
//	title 约战标题
//	server 服务器 
//	remark 说明 
//	title 约战标题 
//	itemName 项目名称 
//	itemIcon 约战项目ICON 
//	inviteId 邀请人ID 
//	inviterNickname 邀请人的昵称 
//	inviterIcon 邀请人的ICON 
//	userIcon 登陆用户的ICON 
	
	private String server;
	private String itemName;
	private int inviteId;
	private String itemIcon;
	private String remark;
	private String title;
	private String userIcon;
	private int matchId;
	private String inviterNickname;
	private String inviterIcon;
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public int getInviteId() {
		return inviteId;
	}
	public void setInviteId(int inviteId) {
		this.inviteId = inviteId;
	}
	public String getItemIcon() {
		return itemIcon;
	}
	public void setItemIcon(String itemIcon) {
		this.itemIcon = itemIcon;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUserIcon() {
		return userIcon;
	}
	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}
	public int getMatchId() {
		return matchId;
	}
	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}
	public String getInviterNickname() {
		return inviterNickname;
	}
	public void setInviterNickname(String inviterNickname) {
		this.inviterNickname = inviterNickname;
	}
	public String getInviterIcon() {
		return inviterIcon;
	}
	public void setInviterIcon(String inviterIcon) {
		this.inviterIcon = inviterIcon;
	}
	
}
