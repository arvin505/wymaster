package com.miqtech.master.client.entity;

import java.io.Serializable;

/**
 * 网吧每个评论信息
 * 
 * @author Administrator
 * 
 */
public class CommentInfo implements Serializable{
	// id 评论id
	// user_id 评论人id
	// praised 有用数量
	// avgScore 平均分
	// is_anonymous 是否匿名
	// nickname 昵称
	// icon 用户头像
	// imgs 评论图(a.jpg,b.jpg)
	// content 评论内容
	// isPraised 是否有用过 1点过有用 0 没点过有用
//	is_no_comment	是否无文字和图片评论1是0否
	int id;
	int user_id;
	int praised;
	float avgScore;
	int is_anonymous;
	String nickname;
	String icon;
	String imgs;// 评论图(a.jpg,b.jpg)
	String content;
	int isPraised;
	String create_date;
	int is_no_comment;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getPraised() {
		return praised;
	}

	public void setPraised(int praised) {
		this.praised = praised;
	}

	public float getAvgScore() {
		return avgScore;
	}

	public void setAvgScore(float avgScore) {
		this.avgScore = avgScore;
	}

	public int getIs_anonymous() {
		return is_anonymous;
	}

	public void setIs_anonymous(int is_anonymous) {
		this.is_anonymous = is_anonymous;
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

	public String getImgs() {
		return imgs;
	}

	public void setImgs(String imgs) {
		this.imgs = imgs;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getIsPraised() {
		return isPraised;
	}

	public void setIsPraised(int isPraised) {
		this.isPraised = isPraised;
	}

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

	public int getIs_no_comment() {
		return is_no_comment;
	}

	public void setIs_no_comment(int is_no_comment) {
		this.is_no_comment = is_no_comment;
	}
}
