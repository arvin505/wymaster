package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xiaoyi on 2016/5/20.
 * 红包卡券兼容类型 支付界面使用
 */

public class CardCompat implements Parcelable {
    public String netbarId;
    public double amount;
    public long id;  //红包卡券id
    public int cardType;  // 类型  0 ，红包， 1 卡券
    public float value;    //面值
    public int needValidate;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.netbarId);
        dest.writeDouble(this.amount);
        dest.writeLong(this.id);
        dest.writeInt(this.cardType);
        dest.writeFloat(this.value);
        dest.writeInt(this.needValidate);
    }

    public CardCompat() {
    }

    protected CardCompat(Parcel in) {
        this.netbarId = in.readString();
        this.amount = in.readDouble();
        this.id = in.readLong();
        this.cardType = in.readInt();
        this.value = in.readFloat();
        this.needValidate = in.readInt();
    }

    public static final Creator<CardCompat> CREATOR = new Creator<CardCompat>() {
        @Override
        public CardCompat createFromParcel(Parcel source) {
            return new CardCompat(source);
        }

        @Override
        public CardCompat[] newArray(int size) {
            return new CardCompat[size];
        }
    };
}
