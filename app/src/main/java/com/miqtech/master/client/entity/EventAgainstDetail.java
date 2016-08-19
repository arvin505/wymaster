package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * v3.2.3自发赛赛事进程暨对阵图(施亦珎)，bean
 * Created by Administrator on 2016/7/6.
 */
public class EventAgainstDetail implements Parcelable{
    //    a_icon	a队图标	string
//    a_is_win	a队是否获胜1是0否
//    a_nickname	a队昵称	string
//    a_score	a队比分
//    a_seat_number	a队序号
//    b_icon	b队图标	string
//    b_is_win	b队是否获胜1是0否
//    b_nickname	b队昵称	string
//    b_score	b队比分
//    b_seat_num	b队序号
//    turn	轮次	number
    //    state	0未开始1进行中2已完结	number

//    id	组id
//    next_id	下一个组的id
//    next_seat	1主场位置2客场位置3决赛4加赛
    private String a_icon;
    private int a_is_win;
    private String a_nickname;
    private int a_score;
    private String a_seat_number;
    private String b_icon;
    private int b_is_win;
    private String b_nickname;
    private int b_score;
    private String b_seat_number;
    private int state;
    private int turn;
    private int id;
    private int next_id;
    private int next_seat;
    private boolean isShowLine = true;

    public String getA_icon() {
        return a_icon;
    }

    public void setA_icon(String a_icon) {
        this.a_icon = a_icon;
    }

    public int getA_is_win() {
        return a_is_win;
    }

    public void setA_is_win(int a_is_win) {
        this.a_is_win = a_is_win;
    }

    public String getA_nickname() {
        return a_nickname;
    }

    public void setA_nickname(String a_nickname) {
        this.a_nickname = a_nickname;
    }

    public int getA_score() {
        return a_score;
    }

    public void setA_score(int a_score) {
        this.a_score = a_score;
    }

    public String getA_seat_number() {
        return a_seat_number;
    }

    public void setA_seat_number(String a_seat_number) {
        this.a_seat_number = a_seat_number;
    }

    public String getB_icon() {
        return b_icon;
    }

    public void setB_icon(String b_icon) {
        this.b_icon = b_icon;
    }

    public int getB_is_win() {
        return b_is_win;
    }

    public void setB_is_win(int b_is_win) {
        this.b_is_win = b_is_win;
    }

    public String getB_nickname() {
        return b_nickname;
    }

    public void setB_nickname(String b_nickname) {
        this.b_nickname = b_nickname;
    }

    public int getB_score() {
        return b_score;
    }

    public void setB_score(int b_score) {
        this.b_score = b_score;
    }

    public String getB_seat_number() {
        return b_seat_number;
    }

    public void setB_seat_number(String b_seat_number) {
        this.b_seat_number = b_seat_number;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNext_id() {
        return next_id;
    }

    public void setNext_id(int next_id) {
        this.next_id = next_id;
    }

    public int getNext_seat() {
        return next_seat;
    }

    public void setNext_seat(int next_seat) {
        this.next_seat = next_seat;
    }

    public boolean isShowLine() {
        return isShowLine;
    }

    public void setShowLine(boolean showLine) {
        isShowLine = showLine;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.a_icon);
        dest.writeInt(this.a_is_win);
        dest.writeString(this.a_nickname);
        dest.writeInt(this.a_score);
        dest.writeString(this.a_seat_number);
        dest.writeString(this.b_icon);
        dest.writeInt(this.b_is_win);
        dest.writeString(this.b_nickname);
        dest.writeInt(this.b_score);
        dest.writeString(this.b_seat_number);
        dest.writeInt(this.state);
        dest.writeInt(this.turn);
        dest.writeInt(this.id);
        dest.writeInt(this.next_id);
        dest.writeInt(this.next_seat);
        dest.writeByte(this.isShowLine ? (byte) 1 : (byte) 0);
    }

    public EventAgainstDetail() {
    }

    protected EventAgainstDetail(Parcel in) {
        this.a_icon = in.readString();
        this.a_is_win = in.readInt();
        this.a_nickname = in.readString();
        this.a_score = in.readInt();
        this.a_seat_number = in.readString();
        this.b_icon = in.readString();
        this.b_is_win = in.readInt();
        this.b_nickname = in.readString();
        this.b_score = in.readInt();
        this.b_seat_number = in.readString();
        this.state = in.readInt();
        this.turn = in.readInt();
        this.id = in.readInt();
        this.next_id = in.readInt();
        this.next_seat = in.readInt();
        this.isShowLine = in.readByte() != 0;
    }

    public static final Creator<EventAgainstDetail> CREATOR = new Creator<EventAgainstDetail>() {
        @Override
        public EventAgainstDetail createFromParcel(Parcel source) {
            return new EventAgainstDetail(source);
        }

        @Override
        public EventAgainstDetail[] newArray(int size) {
            return new EventAgainstDetail[size];
        }
    };
}
