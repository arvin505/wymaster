package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 参赛卡信息
 */
public class ActivityCard implements Serializable {
    private long id;
    private String createDate;
    private String updateDate;
    private int valid;
    private long userId;
    private String realName;
    private String idCard;
    private String telephone;
    private String qq;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }


//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeLong(this.id);
//        dest.writeString(this.createDate);
//        dest.writeString(this.updateDate);
//        dest.writeInt(this.valid);
//        dest.writeLong(this.userId);
//        dest.writeString(this.realName);
//        dest.writeString(this.idCard);
//        dest.writeString(this.telephone);
//        dest.writeString(this.qq);
//    }
//
//    public ActivityCard() {
//    }
//
//    protected ActivityCard(Parcel in) {
//        this.id = in.readLong();
//        this.createDate = in.readString();
//        this.updateDate = in.readString();
//        this.valid = in.readInt();
//        this.userId = in.readLong();
//        this.realName = in.readString();
//        this.idCard = in.readString();
//        this.telephone = in.readString();
//        this.qq = in.readString();
//    }
//
//    public static final Parcelable.Creator<ActivityCard> CREATOR = new Parcelable.Creator<ActivityCard>() {
//        public ActivityCard createFromParcel(Parcel source) {
//            return new ActivityCard(source);
//        }
//
//        public ActivityCard[] newArray(int size) {
//            return new ActivityCard[size];
//        }
//    };
}
