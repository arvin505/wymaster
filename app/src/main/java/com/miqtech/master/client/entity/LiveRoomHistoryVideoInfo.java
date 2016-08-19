package com.miqtech.master.client.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/8/6.
 */
public class LiveRoomHistoryVideoInfo implements Serializable {
    private List<LiveInfo> list; // 历史视屏集合
    private int isLast;// 0有数据 1 没有数据

    public List<LiveInfo> getList() {
        return list;
    }

    public void setList(List<LiveInfo> list) {
        this.list = list;
    }

    public int getIsLast() {
        return isLast;
    }

    public void setIsLast(int isLast) {
        this.isLast = isLast;
    }
}
