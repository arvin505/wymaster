package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/11/30.
 */
public class FilterInfo implements Parcelable {
    private int enjoyType;  //参与类型  0全部， 1个人， 2团队
    private int battle; //约战范围 0 全部 ， 1 当前城市， 2 全部线上
    private int location ; //地理位置 0 当前城市， 1 全国
    private int state;   //状态  0 全部  1 报名中 可参与， 2 已结束
    private AwardInfo awardInfo;

    public AwardInfo getAwardInfo() {
        return awardInfo;
    }

    public void setAwardInfo(AwardInfo awardInfo) {
        this.awardInfo = awardInfo;
    }

    private int filterType;  //筛选分类 1 比赛筛选  2 约战筛选 3综合比赛筛选
    private GameItem gameItem;

    public int getEnjoyType() {
        return enjoyType;
    }

    public void setEnjoyType(int enjoyType) {
        this.enjoyType = enjoyType;
    }

    public int getBattle() {
        return battle;
    }

    public void setBattle(int battle) {
        this.battle = battle;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }



    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public GameItem getGameItem() {
        return gameItem;
    }

    public void setGameItem(GameItem gameItem) {
        this.gameItem = gameItem;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.enjoyType);
        dest.writeInt(this.battle);
        dest.writeInt(this.location);
        dest.writeInt(this.state);
        dest.writeParcelable(this.awardInfo, 0);
        dest.writeInt(this.filterType);
        dest.writeParcelable(this.gameItem, 0);
    }

    public FilterInfo() {
    }

    protected FilterInfo(Parcel in) {
        this.enjoyType = in.readInt();
        this.battle = in.readInt();
        this.location = in.readInt();
        this.state = in.readInt();
        this.awardInfo = in.readParcelable(AwardInfo.class.getClassLoader());
        this.filterType = in.readInt();
        this.gameItem = in.readParcelable(GameItem.class.getClassLoader());
    }

    public static final Parcelable.Creator<FilterInfo> CREATOR = new Parcelable.Creator<FilterInfo>() {
        public FilterInfo createFromParcel(Parcel source) {
            return new FilterInfo(source);
        }

        public FilterInfo[] newArray(int size) {
            return new FilterInfo[size];
        }
    };
}
