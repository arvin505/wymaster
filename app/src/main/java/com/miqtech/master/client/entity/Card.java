package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/3/28.
 * 卡券
 */
public class Card implements Parcelable {

    String name; //"name":"污妖王卡",
    int id; //            "id":4,
    String end_date;//:"2016-04-08 10:43:46",
    String netbar_name;//     "netbar_name":"网娱一店2"

    @SerializedName(value = "start_date", alternate = {"begin_date"})
    String start_date;
    int status;  //1 未使用  2 已使用
    int is_valid;
    float money;  //面值   增值券
    float minMoney;   //使用条件
    int type; //券类型  1增值券
    int enabled;

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    float amount;

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getNetbar_name() {
        return netbar_name;
    }

    public void setNetbar_name(String netbar_name) {
        this.netbar_name = netbar_name;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIs_valid() {
        return is_valid;
    }

    public void setIs_valid(int is_valid) {
        this.is_valid = is_valid;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public float getMinMoney() {
        return minMoney;
    }

    public void setMinMoney(float minMoney) {
        this.minMoney = minMoney;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.id);
        dest.writeString(this.end_date);
        dest.writeString(this.netbar_name);
        dest.writeString(this.start_date);
        dest.writeInt(this.status);
        dest.writeInt(this.is_valid);
        dest.writeFloat(this.money);
        dest.writeFloat(this.minMoney);
        dest.writeInt(this.type);
        dest.writeFloat(this.amount);
        dest.writeInt(this.enabled);
    }

    public Card() {
    }

    protected Card(Parcel in) {
        this.name = in.readString();
        this.id = in.readInt();
        this.end_date = in.readString();
        this.netbar_name = in.readString();
        this.start_date = in.readString();
        this.status = in.readInt();
        this.is_valid = in.readInt();
        this.money = in.readFloat();
        this.minMoney = in.readFloat();
        this.type = in.readInt();
        this.amount = in.readFloat();
        this.enabled = in.readInt();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel source) {
            return new Card(source);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };
}
