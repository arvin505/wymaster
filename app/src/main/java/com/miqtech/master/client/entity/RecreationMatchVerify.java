package com.miqtech.master.client.entity;

import java.util.List;

/**
 * Created by Administrator on 2015/12/10 0010.
 */
public class RecreationMatchVerify {
    int id;
    String createDate;
    String updateDate;
    int valid;
    int activityId;
    int userId;
    String describes;
    int state;
    List<RecreationMatchVerifyImg> imgs;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<RecreationMatchVerifyImg> getImgs() {
        return imgs;
    }

    public void setImgs(List<RecreationMatchVerifyImg> imgs) {
        this.imgs = imgs;
    }
}
