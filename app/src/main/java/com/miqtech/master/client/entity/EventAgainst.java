package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * v3.2.3自发赛赛事进程暨对阵图(施亦珎)，bean
 * Created by Administrator on 2016/7/6.
 */
public class EventAgainst implements Parcelable {
//
//    变量名	含义	类型	备注
//    code	0success	number
//    object		array<object>
//    detailList	详情	array<object>
//    expand	是否展开1是0否	number
//    match_time	开始时间
//    name	轮次名称	string
//    over_time	结束时间
//    round_id	赛事场次id	number

//    turn	轮次	number

    private int round_id;
    /**
     * 是否展开1是0否	number
     */
    private int expand;
    private String name;
    private List<EventAgainstDetail> detailList;
    private int turn;
    private String match_time;
    private String over_time;
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getRound_id() {
        return round_id;
    }

    public void setRound_id(int round_id) {
        this.round_id = round_id;
    }

    public int getExpand() {
        return expand;
    }

    public void setExpand(int expand) {
        this.expand = expand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EventAgainstDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<EventAgainstDetail> detailList) {
        this.detailList = detailList;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public String getMatch_time() {
        return match_time;
    }

    public void setMatch_time(String match_time) {
        this.match_time = match_time;
    }

    public String getOver_time() {
        return over_time;
    }

    public void setOver_time(String over_time) {
        this.over_time = over_time;
    }


    public EventAgainst() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.round_id);
        dest.writeInt(this.expand);
        dest.writeString(this.name);
        dest.writeTypedList(this.detailList);
        dest.writeInt(this.turn);
        dest.writeString(this.match_time);
        dest.writeString(this.over_time);
        dest.writeInt(this.state);
    }

    protected EventAgainst(Parcel in) {
        this.round_id = in.readInt();
        this.expand = in.readInt();
        this.name = in.readString();
        this.detailList = in.createTypedArrayList(EventAgainstDetail.CREATOR);
        this.turn = in.readInt();
        this.match_time = in.readString();
        this.over_time = in.readString();
        this.state = in.readInt();
    }

    public static final Creator<EventAgainst> CREATOR = new Creator<EventAgainst>() {
        @Override
        public EventAgainst createFromParcel(Parcel source) {
            return new EventAgainst(source);
        }

        @Override
        public EventAgainst[] newArray(int size) {
            return new EventAgainst[size];
        }
    };
}
