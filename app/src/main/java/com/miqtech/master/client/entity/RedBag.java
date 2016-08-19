package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class RedBag implements Parcelable {
    int id;
    int money;
    String begin_date;
    String end_date;
    int day;
    String explain;
    int usable;
    int pay_amount_canuse;
    int need_validate;
    int min_money; //满多少可用
    String name; //专属红包网吧名

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMin_money() {
        return min_money;
    }

    public void setMin_money(int min_money) {
        this.min_money = min_money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    int type;// 8 专属红包  7增值券


    public int getNeed_validate() {
        return need_validate;
    }

    public void setNeed_validate(int need_validate) {
        this.need_validate = need_validate;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getBegin_date() {
        return begin_date;
    }

    public void setBegin_date(String begin_date) {
        this.begin_date = begin_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsable() {
        return usable;
    }

    public void setUsable(int usable) {
        this.usable = usable;
    }

    public int getPay_amount_canuse() {
        return pay_amount_canuse;
    }

    public void setPay_amount_canuse(int pay_amount_canuse) {
        this.pay_amount_canuse = pay_amount_canuse;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.money);
        dest.writeString(this.begin_date);
        dest.writeString(this.end_date);
        dest.writeInt(this.day);
        dest.writeString(this.explain);
        dest.writeInt(this.usable);
        dest.writeInt(this.pay_amount_canuse);
        dest.writeInt(this.need_validate);
        dest.writeInt(this.min_money);
        dest.writeString(this.name);
        dest.writeInt(this.type);
    }

    public RedBag() {
    }

    protected RedBag(Parcel in) {
        this.id = in.readInt();
        this.money = in.readInt();
        this.begin_date = in.readString();
        this.end_date = in.readString();
        this.day = in.readInt();
        this.explain = in.readString();
        this.usable = in.readInt();
        this.pay_amount_canuse = in.readInt();
        this.need_validate = in.readInt();
        this.min_money = in.readInt();
        this.name = in.readString();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<RedBag> CREATOR = new Parcelable.Creator<RedBag>() {
        public RedBag createFromParcel(Parcel source) {
            return new RedBag(source);
        }

        public RedBag[] newArray(int size) {
            return new RedBag[size];
        }
    };
}
