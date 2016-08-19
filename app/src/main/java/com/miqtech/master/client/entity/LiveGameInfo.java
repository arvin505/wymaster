package com.miqtech.master.client.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by admin on 2016/8/4.
 */
public class LiveGameInfo implements Serializable {

    private String icon ;//游戏图片
    private int id; //游戏类型id
    @SerializedName("live_num")
    private int liveNum ; //直播数
    @SerializedName("video_num")
    private int videoNum; //视频数
    private String name; //游戏类型

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

    public int getLiveNum() {
        return liveNum;
    }

    public void setLiveNum(int liveNum) {
        this.liveNum = liveNum;
    }

    public int getVideoNum() {
        return videoNum;
    }

    public void setVideoNum(int videoNum) {
        this.videoNum = videoNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
