package com.miqtech.master.client.entity;

import java.io.Serializable;

/**
 * Created by admin on 2016/8/17.
 */
public class OtherLiveInfo implements Serializable {
 private String icon;//其他视频图片
    private int id ;//视频id
    private int type; //1直播 2视频

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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
