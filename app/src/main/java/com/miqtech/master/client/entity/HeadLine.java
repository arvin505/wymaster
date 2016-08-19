package com.miqtech.master.client.entity;

/**
 * 电竞头条
 * Created by wuxn on 2016/7/23.
 */
public class HeadLine {
    int id;
    String title;
    int type;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
