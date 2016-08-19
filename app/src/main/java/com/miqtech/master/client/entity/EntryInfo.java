package com.miqtech.master.client.entity;

/**
 * Created by Administrator on 2015/11/20.
 * 动态入口
 */
public class EntryInfo implements Comparable {

    private int id; //": 1,
    private String title;  //        "title": "附近网吧",
    private int type; //": 1,
    private int sort; //            "sort": 4
    private String icon;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public int compareTo(Object another) {
        return this.getSort() - ((EntryInfo) another).getSort();
    }
}
