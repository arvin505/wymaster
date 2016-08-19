package com.miqtech.master.client.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/8/6.
 */
public class LiveRoomInfo implements Serializable{
   private LiveRoomAnchorInfo info;//直播间信息
    private LiveRoomHistoryVideoInfo historyVideo;//往期视频
    private List<OtherLiveInfo> otherLive; //其他视频

    public List<OtherLiveInfo> getOtherLive() {
        return otherLive;
    }

    public void setOtherLive(List<OtherLiveInfo> otherLive) {
        this.otherLive = otherLive;
    }

    public LiveRoomHistoryVideoInfo getHistoryVideo() {
        return historyVideo;
    }

    public void setHistoryVideo(LiveRoomHistoryVideoInfo historyVideo) {
        this.historyVideo = historyVideo;
    }

    public LiveRoomAnchorInfo getInfo() {
        return info;
    }

    public void setInfo(LiveRoomAnchorInfo info) {
        this.info = info;
    }
}
