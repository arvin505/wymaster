package com.miqtech.master.client.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/8/3.
 */
public class LiveAndVideoData implements Serializable {
    private ArrayList<LiveInfo> hotVideo; //热门视屏数据
    private ArrayList<LiveInfo> onLive; //热门直播数据

    public ArrayList<LiveInfo> getHotVideo() {
        return hotVideo;
    }

    public void setHotVideo(ArrayList<LiveInfo> hotVideo) {
        this.hotVideo = hotVideo;
    }

    public ArrayList<LiveInfo> getOnLive() {
        return onLive;
    }

    public void setOnLive(ArrayList<LiveInfo> onLive) {
        this.onLive = onLive;
    }
}
