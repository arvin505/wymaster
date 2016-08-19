package com.miqtech.master.client.entity;

public class MyMessage {

//	isLast	是否为最后一页：0-否;1-是;
//	id	消息id
//	is_read	=0未读>0已读
//	title	标题
//	content	内容
//	type	0系统消息 1红包消息 2会员消息 3预定消息 4支付消息 5赛事消息 6约战消息
//	create_date	时间
//	obj_id	跳转详情时使用的对象id 默认值是0

	String id;
	String user_id;
	String content;
	int is_read;
	String create_date;
	String title;
	int type;
	int obj_id;

	public int getObj_id() {
		return obj_id;
	}

	public void setObj_id(int obj_id) {
		this.obj_id = obj_id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getIs_read() {
		return is_read;
	}

	public void setIs_read(int is_read) {
		this.is_read = is_read;
	}

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

}
