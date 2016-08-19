package com.miqtech.master.client.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 2016/5/17.
 */
public class BuyRecordInfo implements Serializable {
    private ArrayList<BuyRecordData> list; //购买记录集合
    private int isLast;//是否到了最后一页
    private int total;//总item数
    private int currentPage;//当前页数

    public int getIsLast() {
        return isLast;
    }

    public void setIsLast(int isLast) {
        this.isLast = isLast;
    }

    public ArrayList<BuyRecordData> getList() {
        return list;
    }

    public void setList(ArrayList<BuyRecordData> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
