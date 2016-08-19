package com.miqtech.master.client.entity;

/**
 * Created by admin on 2016/4/14.
 */
public class InfoUpAndDown {
//    downPercent	踩的百分比	number
//    downTotal	踩的数量	number
//    state	0未操作1顶2踩	string
//    upPercent	顶的百分比	number
//    upTotal	顶的数量	number

    int downPercent;
    int downTotal;
    String state;
    int upPercent;
    int upTotal;

    public int getDownPercent() {
        return downPercent;
    }

    public void setDownPercent(int downPercent) {
        this.downPercent = downPercent;
    }

    public int getDownTotal() {
        return downTotal;
    }

    public void setDownTotal(int downTotal) {
        this.downTotal = downTotal;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getUpPercent() {
        return upPercent;
    }

    public void setUpPercent(int upPercent) {
        this.upPercent = upPercent;
    }

    public int getUpTotal() {
        return upTotal;
    }

    public void setUpTotal(int upTotal) {
        this.upTotal = upTotal;
    }
}
