package com.miqtech.master.client.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by admin on 2016/8/6.
 */
public class LiveRoomAnchorInfo implements Serializable {
    @SerializedName("comment_num")
   private int commentNum; //评论数
    private int fans;//粉丝数
    private String icon;//主播头像
    private String introduce;//介绍
    @SerializedName("is_subscibe")
    private int isSubscibe;//是否已订阅
    private String nickname;//直播昵称
    private int onlineNum ;//在线人数
    private String rtmp;//播放地址
    private int screen;//0宽屏1竖屏
    @SerializedName("up_user_id")
    private int upUserId; //主播的id
    private String title; //标题
    private int sex;//0  男 1 女

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUpUserId() {
        return upUserId;
    }

    public void setUpUserId(int upUserId) {
        this.upUserId = upUserId;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getIsSubscibe() {
        return isSubscibe;
    }

    public void setIsSubscibe(int isSubscibe) {
        this.isSubscibe = isSubscibe;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getOnlineNum() {
        return onlineNum;
    }

    public void setOnlineNum(int onlineNum) {
        this.onlineNum = onlineNum;
    }

    public String getRtmp() {
        return rtmp;
    }

    public void setRtmp(String rtmp) {
        this.rtmp = rtmp;
    }

    public int getScreen() {
        return screen;
    }

    public void setScreen(int screen) {
        this.screen = screen;
    }
}
