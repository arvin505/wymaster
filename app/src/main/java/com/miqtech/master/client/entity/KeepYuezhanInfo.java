package com.miqtech.master.client.entity;

/**
 * 保存在本地的：发起约战的信息
 * 
 * @author Administrator
 * 
 */
public class KeepYuezhanInfo {
	private String title;// 标题
	private String gameName;// 游戏名字
	private String gameId;// 游戏ID
	private String gameService;// 游戏服务器
	private String contactWay;// 联系方式
	private String content;// 简介

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getGameService() {
		return gameService;
	}

	public void setGameService(String gameService) {
		this.gameService = gameService;
	}

	public String getContactWay() {
		return contactWay;
	}

	public void setContactWay(String contactWay) {
		this.contactWay = contactWay;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
