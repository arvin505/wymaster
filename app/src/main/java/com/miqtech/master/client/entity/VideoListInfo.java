package com.miqtech.master.client.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/8/8.
 */
public class VideoListInfo implements Serializable {
    private int isLast ;//是否还有数据
    private  int total;
    private List<LiveInfo> hotLive; //最热视屏
    private List<LiveInfo> newLive; //最新视频

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<LiveInfo> getHotLive() {
        return hotLive;
    }

    public void setHotLive(List<LiveInfo> hotLive) {
        this.hotLive = hotLive;
    }

    public int getIsLast() {
        return isLast;
    }

    public void setIsLast(int isLast) {
        this.isLast = isLast;
    }

    public List<LiveInfo> getNewLive() {
        return newLive;
    }

    public void setNewLive(List<LiveInfo> newLive) {
        this.newLive = newLive;
    }
}
