package com.miqtech.master.client.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/12/10 0010.
 */
public class RecreationMatchAppealImg implements Serializable{
    int id;
    int appealId;
    String img;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAppealId() {
        return appealId;
    }

    public void setAppealId(int appealId) {
        this.appealId = appealId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
