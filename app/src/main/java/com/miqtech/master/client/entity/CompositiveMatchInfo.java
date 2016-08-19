package com.miqtech.master.client.entity;

/**
 * 综合赛事
 * Created by Administrator on 2015/11/30.
 */
public class CompositiveMatchInfo {

    private int id;
    private double distance;
    private String icon;    // string	详情信息
    private int applyCount;    //	int	报名人数
    private int state;//	int	赛事状态:1-报名中,2-进行中,3-未开始,4-已结束
    private String summary;//	string	概述
    private String startTime; // string	开始时间
    private String endTime;//	string	结束时间
    private String title;//	string	标题
    private String create_date;//	string	创建时间
    private long itemId;//	long	项目ID
    private int type;//	int	赛事类型:0-官方赛,1-官网线上,2-官方线下,3-官网网吧
    private int sort_num;
    private String createDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "CompositiveMatchInfo{" +
                "id=" + id +
                ", distance=" + distance +
                ", icon='" + icon + '\'' +
                ", applyCount=" + applyCount +
                ", state=" + state +
                ", summary='" + summary + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", title='" + title + '\'' +
                ", create_date='" + create_date + '\'' +
                ", itemId=" + itemId +
                ", type=" + type +
                ", sort_num=" + sort_num +
                ", createDate='" + createDate + '\'' +
                '}';
    }

    public int getSort_num() {
        return sort_num;
    }

    public void setSort_num(int sort_num) {
        this.sort_num = sort_num;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(int applyCount) {
        this.applyCount = applyCount;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

}
