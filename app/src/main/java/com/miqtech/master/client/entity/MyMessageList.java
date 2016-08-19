package com.miqtech.master.client.entity;

import java.util.List;

/**
 * Created by Administrator on 2015/12/5.
 */
public class MyMessageList {

    private List<MyMessage> list;
    private int isLast;
    public List<MyMessage> getList() {
        return list;
    }

    public void setList(List<MyMessage> list) {
        this.list = list;
    }

    public int getIsLast() {
        return isLast;
    }

    public void setIsLast(int isLast) {
        this.isLast = isLast;
    }
}
