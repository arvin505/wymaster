package com.miqtech.master.client.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 约战实体类
 * 
 * @author Administrator
 * 
 */
public class YueZhan implements Serializable{
	// "people_num": 2,
	// "user_id": 5,
	// "spoils": "发呆萨芬",
	// "match_id": 85,
	// "nickname": "飞神",
	// "begin_time": "2015-06-07 11:40:00",
	// "apply_num": 2,
	// "item_name": "穿越火线",
	// "id": 85,
	// "title": "fdsa fdsa"

	int id;// 约战Id
	String title;// 约战标题
	String spoils;// 战利品
	String begin_time;// 开始时间
	String item_name;// 游戏名称
	int item_id;
	String item_pic;// 游戏Icon
	String item_bg_pic_media;
	String userId;// 用户ID
	String nickname;// 昵称
	int way;
	String address;
	int people_num;// 越战人数
	int apply_count;
	int apply_num;
	String releaser;
	String icon;
	String server;
	Long netbar_id;
	int userStatus;//用户状态
	int is_start;
	long releaser_id;
	String releaser_telephone;
	String releaser_icon;
	String remark;
	List<YueZhanApply> applies; 
	YueZhanComments comments;
	int commentsCount;
	int by_merchant ;
	private int releaser_sex;

	private int sex;

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getReleaser_sex() {
		return releaser_sex;
	}

	public void setReleaser_sex(int releaser_sex) {
		this.releaser_sex = releaser_sex;
	}

	private int canShareRedbag;//是否可以分享红包大于0分享
	private int shareRedbagNumber;//红包数量
	private String shareRedbagIcon;//红包Icon
	
	private String beInvited;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSpoils() {
		return spoils;
	}

	public void setSpoils(String spoils) {
		this.spoils = spoils;
	}

	public String getBegin_time() {
		return begin_time;
	}

	public void setBegin_time(String begin_time) {
		this.begin_time = begin_time;
	}

	public String getItem_name() {
		return item_name;
	}

	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}

	public String getItem_pic() {
		return item_pic;
	}

	public void setItem_pic(String item_pic) {
		this.item_pic = item_pic;
	}

	public int getWay() {
		return way;
	}

	public void setWay(int way) {
		this.way = way;
	}

	public int getPeople_num() {
		return people_num;
	}

	public void setPeople_num(int people_num) {
		this.people_num = people_num;
	}

	public int getApply_count() {
		return apply_count;
	}

	public void setApply_count(int apply_count) {
		this.apply_count = apply_count;
	}

	public String getReleaser() {
		return releaser;
	}

	public void setReleaser(String releaser) {
		this.releaser = releaser;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getApply_num() {
		return apply_num;
	}

	public void setApply_num(int apply_num) {
		this.apply_num = apply_num;
	}

	public int getItem_id() {
		return item_id;
	}

	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public Long getNetbar_id() {
		return netbar_id;
	}

	public void setNetbar_id(Long netbar_id) {
		this.netbar_id = netbar_id;
	}

	public int getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(int userStatus) {
		this.userStatus = userStatus;
	}

	public int getIs_start() {
		return is_start;
	}

	public void setIs_start(int is_start) {
		this.is_start = is_start;
	}

	public long getReleaser_id() {
		return releaser_id;
	}

	public void setReleaser_id(long releaser_id) {
		this.releaser_id = releaser_id;
	}

	public String getReleaser_telephone() {
		return releaser_telephone;
	}

	public void setReleaser_telephone(String releaser_telephone) {
		this.releaser_telephone = releaser_telephone;
	}

	public String getReleaser_icon() {
		return releaser_icon;
	}

	public void setReleaser_icon(String releaser_icon) {
		this.releaser_icon = releaser_icon;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<YueZhanApply> getApplies() {
		return applies;
	}

	public void setApplies(List<YueZhanApply> applies) {
		this.applies = applies;
	}

	public YueZhanComments getComments() {
		return comments;
	}

	public void setComments(YueZhanComments comments) {
		this.comments = comments;
	}

	public int getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getBy_merchant() {
		return by_merchant;
	}

	public void setBy_merchant(int by_merchant) {
		this.by_merchant = by_merchant;
	}

	public int getCanShareRedbag() {
		return canShareRedbag;
	}

	public void setCanShareRedbag(int canShareRedbag) {
		this.canShareRedbag = canShareRedbag;
	}

	public int getShareRedbagNumber() {
		return shareRedbagNumber;
	}

	public void setShareRedbagNumber(int shareRedbagNumber) {
		this.shareRedbagNumber = shareRedbagNumber;
	}

	public String getShareRedbagIcon() {
		return shareRedbagIcon;
	}

	public void setShareRedbagIcon(String shareRedbagIcon) {
		this.shareRedbagIcon = shareRedbagIcon;
	}

	public String getItem_bg_pic_media() {
		return item_bg_pic_media;
	}

	public void setItem_bg_pic_media(String item_bg_pic_media) {
		this.item_bg_pic_media = item_bg_pic_media;
	}

	public String getBeInvited() {
		return beInvited;
	}

	public void setBeInvited(String beInvited) {
		this.beInvited = beInvited;
	}

	@Override
	public String toString() {
		return "YueZhan{" +
				"id=" + id +
				", title='" + title + '\'' +
				", spoils='" + spoils + '\'' +
				", begin_time='" + begin_time + '\'' +
				", item_name='" + item_name + '\'' +
				", item_id=" + item_id +
				", item_pic='" + item_pic + '\'' +
				", item_bg_pic_media='" + item_bg_pic_media + '\'' +
				", userId='" + userId + '\'' +
				", nickname='" + nickname + '\'' +
				", way=" + way +
				", address='" + address + '\'' +
				", people_num=" + people_num +
				", apply_count=" + apply_count +
				", apply_num=" + apply_num +
				", releaser='" + releaser + '\'' +
				", icon='" + icon + '\'' +
				", server='" + server + '\'' +
				", netbar_id=" + netbar_id +
				", userStatus=" + userStatus +
				", is_start=" + is_start +
				", releaser_id=" + releaser_id +
				", releaser_telephone='" + releaser_telephone + '\'' +
				", releaser_icon='" + releaser_icon + '\'' +
				", remark='" + remark + '\'' +
				", applies=" + applies +
				", comments=" + comments +
				", commentsCount=" + commentsCount +
				", by_merchant=" + by_merchant +
				", releaser_sex=" + releaser_sex +
				", canShareRedbag=" + canShareRedbag +
				", shareRedbagNumber=" + shareRedbagNumber +
				", shareRedbagIcon='" + shareRedbagIcon + '\'' +
				", beInvited='" + beInvited + '\'' +
				'}';
	}
}
