package com.miqtech.master.client.entity;

import java.util.List;

/**
 * Created by Administrator on 2015/12/14.
 */
public class MyAmuseSet {
//    isLast":1,
//            "total":2,
//            "currentPage":1
    private int isLast;
    private int total;
    private int currentPage;
    private List<MyAmuse> list;

    public int getIsLast() {
        return isLast;
    }

    public void setIsLast(int isLast) {
        this.isLast = isLast;
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

    public List<MyAmuse> getList() {
        return list;
    }

    public void setList(List<MyAmuse> list) {
        this.list = list;
    }
}
