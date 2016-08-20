package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.utils.Utils;

/**
 * Created by xiaoyi on 2016/7/28.
 */
public class MessageCount implements Parcelable{
    private int activity;

    private int comment;

    private int sys;

    private int order;

    public void setActivity(int activity) {
        this.activity = activity;
    }

    public int getActivity() {
        return this.activity;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getComment() {
        return this.comment;
    }

    public void setSys(int sys) {
        this.sys = sys;
    }

    public int getSys() {
        return this.sys;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public int getTotal() {
        if (total == 0) {
            total = order + sys + comment + activity;
        }
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    private int total;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.activity);
        dest.writeInt(this.comment);
        dest.writeInt(this.sys);
        dest.writeInt(this.order);
        dest.writeInt(this.total);
    }

    public MessageCount() {
    }

    protected MessageCount(Parcel in) {
        this.activity = in.readInt();
        this.comment = in.readInt();
        this.sys = in.readInt();
        this.order = in.readInt();
        this.total = in.readInt();
    }

    public static final Creator<MessageCount> CREATOR = new Creator<MessageCount>() {
        @Override
        public MessageCount createFromParcel(Parcel source) {
            return new MessageCount(source);
        }

        @Override
        public MessageCount[] newArray(int size) {
            return new MessageCount[size];
        }
    };
}
