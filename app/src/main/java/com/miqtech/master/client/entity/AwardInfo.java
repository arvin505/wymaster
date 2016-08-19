package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/12/22.
 */
public class AwardInfo implements Parcelable {

    private String name;
    private int awardtype;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAwardtype() {
        return awardtype;
    }

    public void setAwardtype(int awardtype) {
        this.awardtype = awardtype;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.awardtype);
    }

    public AwardInfo() {
    }

    protected AwardInfo(Parcel in) {
        this.name = in.readString();
        this.awardtype = in.readInt();
    }

    public static final Parcelable.Creator<AwardInfo> CREATOR = new Parcelable.Creator<AwardInfo>() {
        public AwardInfo createFromParcel(Parcel source) {
            return new AwardInfo(source);
        }

        public AwardInfo[] newArray(int size) {
            return new AwardInfo[size];
        }
    };
}
