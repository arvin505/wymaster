package com.miqtech.master.client.entity;

public class YueZhanComment {
//	id	评论id
//	user_nickname	评论人名称
//	user_icon	评论人头像
//	user_id	评论人用户id
//	create_date	评论时间
//	content	评论内容
	String user_nickname;
	String user_icon;
	String create_date;
	String content;
	String id;
	String user_id;
	
	public String getUser_nickname() {
		return user_nickname;
	}
	public void setUser_nickname(String user_nickname) {
		this.user_nickname = user_nickname;
	}
	public String getUser_icon() {
		return user_icon;
	}
	public void setUser_icon(String user_icon) {
		this.user_icon = user_icon;
	}
	public String getCreate_date() {
		return create_date;
	}
	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	
}
