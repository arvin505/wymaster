package com.miqtech.master.client.entity;

import java.io.Serializable;

public class TeammateInfo implements Serializable{
	private String nickname; 
	private String telephone;//队友电话
	private int isCompleted;//队友资料是否完善
	private int member_id;//队员ID
	private String icon;//头像
	private String is_monitor;//是否是队长
	private String id;//用户Id
	private String labor;//擅长位置
	
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public int getIsCompleted() {
		return isCompleted;
	}
	public void setIsCompleted(int isCompleted) {
		this.isCompleted = isCompleted;
	}
	public int getMember_id() {
		return member_id;
	}
	public void setMember_id(int member_id) {
		this.member_id = member_id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getIs_monitor() {
		return is_monitor;
	}
	public void setIs_monitor(String is_monitor) {
		this.is_monitor = is_monitor;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabor() {
		return labor;
	}
	public void setLabor(String labor) {
		this.labor = labor;
	}
	
}
