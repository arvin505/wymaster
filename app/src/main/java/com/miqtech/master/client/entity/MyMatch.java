package com.miqtech.master.client.entity;

/**
 * 我的赛事
 * Created by wuxn on 2016/7/23.
 */
public class MyMatch {
    int id;
    long time;
    String tip;
    String title;
    int type;//1官方赛2自发赛

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
