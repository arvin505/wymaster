package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/1/19.
 */
public /**
 * 申述状态
 */
class AppealCategory implements Parcelable {
    String content;
    int id;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeInt(this.id);
    }

    public AppealCategory() {
    }

    protected AppealCategory(Parcel in) {
        this.content = in.readString();
        this.id = in.readInt();
    }

    public static final Parcelable.Creator<AppealCategory> CREATOR = new Parcelable.Creator<AppealCategory>() {
        public AppealCategory createFromParcel(Parcel source) {
            return new AppealCategory(source);
        }

        public AppealCategory[] newArray(int size) {
            return new AppealCategory[size];
        }
    };
}
