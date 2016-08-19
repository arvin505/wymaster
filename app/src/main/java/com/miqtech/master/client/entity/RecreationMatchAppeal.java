package com.miqtech.master.client.entity;

import android.os.Parcel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/12/10 0010.
 */
public class RecreationMatchAppeal implements Serializable {
    String appealRemark;
    int appealState;
    boolean appealable;
    List<RecreationMatchAppealImg> appealImgs;

    protected RecreationMatchAppeal(Parcel in) {
        appealRemark = in.readString();
        appealState = in.readInt();
        appealable = in.readByte() != 0;
    }
    public String getAppealRemark() {
        return appealRemark;
    }

    public void setAppealRemark(String appealRemark) {
        this.appealRemark = appealRemark;
    }

    public int getAppealState() {
        return appealState;
    }

    public void setAppealState(int appealState) {
        this.appealState = appealState;
    }

    public boolean isAppealable() {
        return appealable;
    }

    public void setAppealable(boolean appealable) {
        this.appealable = appealable;
    }

    public List<RecreationMatchAppealImg> getAppealImgs() {
        return appealImgs;
    }
    public void setAppealImgs(List<RecreationMatchAppealImg> appealImgs) {
        this.appealImgs = appealImgs;
    }
}
