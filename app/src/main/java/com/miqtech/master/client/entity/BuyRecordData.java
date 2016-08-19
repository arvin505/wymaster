package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by admin on 2016/5/12.
 */
public class BuyRecordData implements Parcelable {

    @SerializedName("create_date")
    private String createData; //时间
    @SerializedName("prize_phone")
    private String prizePhone;//中奖号码
    @SerializedName("pay_coin")
    private  int payCoin;//消费金币

    public String getCreateData() {
        return createData;
    }

    public void setCreateData(String createData) {
        this.createData = createData;
    }

    public String getPrizePhone() {
        return prizePhone;
    }

    public void setPrizePhone(String prizePhone) {
        this.prizePhone = prizePhone;
    }

    public int getPayCoin() {
        return payCoin;
    }

    public void setPayCoin(int payCoin) {
        this.payCoin = payCoin;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.createData);
        dest.writeString(this.prizePhone);
        dest.writeInt(this.payCoin);
    }

    public BuyRecordData() {
    }

    protected BuyRecordData(Parcel in) {
        this.createData = in.readString();
        this.prizePhone=in.readString();
        this.payCoin = in.readInt();
    }

    public static final Parcelable.Creator<BuyRecordData> CREATOR = new Parcelable.Creator<BuyRecordData>() {
        public BuyRecordData createFromParcel(Parcel source) {
            return new BuyRecordData(source);
        }

        public BuyRecordData[] newArray(int size) {
            return new BuyRecordData[size];
        }
    };
}
