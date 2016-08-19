package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/13.
 */
public class InforCatalog implements Parcelable {

    private Catalog parent; // 一级目录
    private List<Catalog> sub;  //二级目录

    public Catalog getParent() {
        return parent;
    }

    public void setParent(Catalog parent) {
        this.parent = parent;
    }

    public List<Catalog> getSub() {
        return sub;
    }

    public void setSub(List<Catalog> sub) {
        this.sub = sub;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.parent, flags);
        dest.writeList(this.sub);
    }

    public InforCatalog() {
    }

    protected InforCatalog(Parcel in) {
        this.parent = in.readParcelable(Catalog.class.getClassLoader());
        this.sub = new ArrayList<Catalog>();
        in.readList(this.sub, Catalog.class.getClassLoader());
    }

    public static final Parcelable.Creator<InforCatalog> CREATOR = new Parcelable.Creator<InforCatalog>() {
        @Override
        public InforCatalog createFromParcel(Parcel source) {
            return new InforCatalog(source);
        }

        @Override
        public InforCatalog[] newArray(int size) {
            return new InforCatalog[size];
        }
    };

    @Override
    public String toString() {
        return "InforCatalog{" +
                "parent=" + parent +
                ", sub=" + sub +
                '}';
    }
}
