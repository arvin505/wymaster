package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 场次信息
 * Created by zhaosentao on 2016/5/11.
 */
public class ActivityInfo implements Serializable {
//    address	网吧地址	string
//    area	地区	string
//    id	赛事id	number
//    name	网吧名称	string
//    over_time	比赛时间	string
//    person_or_team	1个人报名2战队报名
//    team_id	战队id
//    team_name	战队名称(战队二维码扫描返回该字段)	string
//    title	赛事名称	string

    private String address;
    private String area;
    private int id;
    private String name;
    private String over_time;
    private int person_or_team;
    private int team_id;
    private String team_name;
    private String title;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOver_time() {
        return over_time;
    }

    public void setOver_time(String over_time) {
        this.over_time = over_time;
    }

    public int getPerson_or_team() {
        return person_or_team;
    }

    public void setPerson_or_team(int person_or_team) {
        this.person_or_team = person_or_team;
    }

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.address);
//        dest.writeString(this.area);
//        dest.writeInt(this.id);
//        dest.writeString(this.name);
//        dest.writeString(this.over_time);
//        dest.writeInt(this.person_or_team);
//        dest.writeInt(this.team_id);
//        dest.writeString(this.team_name);
//        dest.writeString(this.title);
//    }
//
//
//    protected ActivityInfo(Parcel in) {
//        this.address = in.readString();
//        this.area = in.readString();
//        this.id = in.readInt();
//        this.name = in.readString();
//        this.over_time = in.readString();
//        this.person_or_team = in.readInt();
//        this.team_id = in.readInt();
//        this.team_name = in.readString();
//        this.title = in.readString();
//    }
//
//    public static final Parcelable.Creator<ActivityInfo> CREATOR = new Parcelable.Creator<ActivityInfo>() {
//        public ActivityInfo createFromParcel(Parcel source) {
//            return new ActivityInfo(source);
//        }
//
//        public ActivityInfo[] newArray(int size) {
//            return new ActivityInfo[size];
//        }
//    };
}
