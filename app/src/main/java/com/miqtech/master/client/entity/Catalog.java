package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/4/13.
 */
public class Catalog implements Parcelable {
    private String name;
    private int pid;
    private int id;
    private int type;  //1 资讯 2 视频
    private String img;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        dest.writeInt(this.pid);
        dest.writeInt(this.id);
        dest.writeInt(this.type);
        dest.writeString(this.img);
    }

    public Catalog() {
    }

    protected Catalog(Parcel in) {
        this.name = in.readString();
        this.pid = in.readInt();
        this.id = in.readInt();
        this.type = in.readInt();
        this.img = in.readString();
    }

    public static final Parcelable.Creator<Catalog> CREATOR = new Parcelable.Creator<Catalog>() {
        @Override
        public Catalog createFromParcel(Parcel source) {
            return new Catalog(source);
        }

        @Override
        public Catalog[] newArray(int size) {
            return new Catalog[size];
        }
    };

    @Override
    public String toString() {
        return "Catalog{" +
                "name='" + name + '\'' +
                ", pid=" + pid +
                ", id=" + id +
                ", type=" + type +
                ", img='" + img + '\'' +
                '}';
    }
}
