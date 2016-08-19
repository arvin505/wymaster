package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 悬赏令类型为2 独占鳌头类型为3 排行榜时有该字段，没有成绩排行则该字段不存在
 * Created by zhaosentao on 2016/7/26.
 */
public class RewardGrade {
//    grade	成绩	string
//    icon	头像	string
//    nickname	昵称	string
//    user_id	用户id	number

    private String grade;
    private String icon;
    private String nickname;
    private int user_id;

    private int bounty_id;//悬赏令ID
    private int id;
    private String img;//悬赏令成绩图片

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }


    public int getBounty_id() {
        return bounty_id;
    }

    public void setBounty_id(int bounty_id) {
        this.bounty_id = bounty_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
