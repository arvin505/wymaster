package com.miqtech.master.client.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/12/29.
 */
public class NetBarAmuse implements Serializable{

    private int id;
    private String title;
    private String banner;
    private String startDate;
    private int applyNum;
    private int timeStatus;

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

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(int applyNum) {
        this.applyNum = applyNum;
    }

    public int getTimeStatus() {
        return timeStatus;
    }
    public void setTimeStatus(int timeStatus) {
        this.timeStatus = timeStatus;
    }
}
