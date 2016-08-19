package com.miqtech.master.client.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/1/8.
 */
public class MatchArea implements Serializable{
    int round;
    String areaCode;
    String name;
    List<MatchNetbar> netbars;


    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MatchNetbar> getNetbars() {
        return netbars;
    }

    public void setNetbars(List<MatchNetbar> netbars) {
        this.netbars = netbars;
    }
}
