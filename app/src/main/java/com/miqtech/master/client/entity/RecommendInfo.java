package com.miqtech.master.client.entity;

/**
 * Created by Administrator on 2015/11/28.
 */
public class RecommendInfo {
    private String mainIcon;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    private String startDate;

    public String getMainIcon() {
        return mainIcon;
    }

    public void setMainIcon(String mainIcon) {
        this.mainIcon = mainIcon;
    }

    private String nickname; //     "nickname": "好男人就是我",
    private String server;   //''     "server": "4",
    private int sex;  //   0 男 1 女
    private String netbar_name;
    private String user_id;
    private String id; //": 2,
    private String applyNum;//       "applyNum": 6,
    private String icon;//       "icon": "uploads/imgs/activity/2015/04/01/20150401161859_kdKb5SiRrbuU9KQpWMNT.jpg",
    private String title;//    "title": "第四届DOTA2国际邀请赛",
    private String sort;//   "sort": 2,
    private int way;//   "way": 1,
    private String start_time;//   "start_time": "2014-10-02 00:00:00",
    private String end_time;//
    private int status;
    private String max_num; //'    "max_num": 0,
    private int type; //   "type": 1
    private int category;   //
    private String summary;//摘要


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getNetbar_name() {
        return netbar_name;
    }

    public void setNetbar_name(String netbar_name) {
        this.netbar_name = netbar_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(String applyNum) {
        this.applyNum = applyNum;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getWay() {
        return way;
    }

    public void setWay(int way) {
        this.way = way;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMax_num() {
        return max_num;
    }

    public void setMax_num(String max_num) {
        this.max_num = max_num;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "RecommendInfo{" +
                "nickname='" + nickname + '\'' +
                ", server='" + server + '\'' +
                ", sex=" + sex +
                ", netbar_name='" + netbar_name + '\'' +
                ", user_id='" + user_id + '\'' +
                ", id='" + id + '\'' +
                ", applyNum='" + applyNum + '\'' +
                ", icon='" + icon + '\'' +
                ", title='" + title + '\'' +
                ", sort='" + sort + '\'' +
                ", way=" + way +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", status=" + status +
                ", max_num='" + max_num + '\'' +
                ", type='" + type + '\'' +
                ", category=" + category +
                ", summary='" + summary + '\'' +
                '}';
    }
}
