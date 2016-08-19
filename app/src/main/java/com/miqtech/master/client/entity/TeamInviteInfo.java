package com.miqtech.master.client.entity;

/**
 * 被战队邀请信息:bean
 * 
 * @author Administrator
 * 
 */
public class TeamInviteInfo {
	// invocation_id long 邀请记录的id
	// icon string 头像
	// team_id long 战队id
	// nickname string 昵称
	// name string 战队名

	private int invocation_id;
	private String icon;
	private int team_id;
	private String nickname;
	private String name;

	public int getInvocation_id() {
		return invocation_id;
	}

	public void setInvocation_id(int invocation_id) {
		this.invocation_id = invocation_id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getTeam_id() {
		return team_id;
	}

	public void setTeam_id(int team_id) {
		this.team_id = team_id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
