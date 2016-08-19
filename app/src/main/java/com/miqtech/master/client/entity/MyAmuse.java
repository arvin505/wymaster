package com.miqtech.master.client.entity;

/**
 * 我的娱乐赛
 * Created by Administrator on 2015/12/14.
 */
public class MyAmuse {

//    mainIcon	图片地址
//    title	标题
//    status	0报名中1进行中2已结束
//    start_time	比赛开始时间

    private int id;
    private String mainIcon;
    private int status;
//    private String start_time;
    private String startDate;
    private String title;

    private String start_time;

    private String icon;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMainIcon() {
        return mainIcon;
    }

    public void setMainIcon(String mainIcon) {
        this.mainIcon = mainIcon;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
