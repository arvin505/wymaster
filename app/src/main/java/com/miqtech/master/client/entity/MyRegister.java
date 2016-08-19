package com.miqtech.master.client.entity;

/**
 * Created by Administrator on 2015/12/10.
 */
public class MyRegister {
    private String title;
    private int type;//1是橘色0是灰色

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
