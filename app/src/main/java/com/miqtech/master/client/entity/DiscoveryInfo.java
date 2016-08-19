package com.miqtech.master.client.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/8/10.
 */
public class DiscoveryInfo implements Serializable {
    private int liveNum ;//直播数量
    private List<LiveInfo> liveList; //直播
    private  InternetBarInfo  netbar;//网吧

    public List<LiveInfo> getLiveList() {
        return liveList;
    }

    public void setLiveList(List<LiveInfo> liveList) {
        this.liveList = liveList;
    }

    public InternetBarInfo getNetbar() {
        return netbar;
    }

    public void setNetbar(InternetBarInfo netbar) {
        this.netbar = netbar;
    }

    public int getLiveNum() {
        return liveNum;
    }

    public void setLiveNum(int liveNum) {
        this.liveNum = liveNum;
    }
}
