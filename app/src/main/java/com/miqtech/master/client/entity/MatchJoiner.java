package com.miqtech.master.client.entity;


import java.io.Serializable;

/**
 * Created by Administrator on 2015/12/1 0001.
 */
public class MatchJoiner implements Serializable{

    private String nickname ;
    private String icon;
    private int userId;
    private int id;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MatchJoiner{" +
                "nickname='" + nickname + '\'' +
                ", icon='" + icon + '\'' +
                ", userId=" + userId +
                '}';
    }
}
