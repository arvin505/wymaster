package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/12/2.
 * 竞技项目
 */
public class GameItem implements Parcelable {

    private int item_id; //"item_id" : 1,
    private String item_name; //     "item_name" : "英雄联盟",
    private String item_icon;//  "item_icon" : "a.jpg"

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_icon() {
        return item_icon;
    }

    public void setItem_icon(String item_icon) {
        this.item_icon = item_icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.item_name);
        dest.writeInt(this.item_id);
        dest.writeString(this.item_icon);
    }

    public GameItem() {
    }

    protected GameItem(Parcel in) {
        this.item_name = in.readString();
        this.item_id = in.readInt();
        this.item_icon = in.readString();
    }

    public static final Parcelable.Creator<GameItem> CREATOR = new Parcelable.Creator<GameItem>() {
        public GameItem createFromParcel(Parcel source) {
            return new GameItem(source);
        }

        public GameItem[] newArray(int size) {
            return new GameItem[size];
        }
    };
}
