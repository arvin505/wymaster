package com.miqtech.master.client.entity;

import java.io.Serializable;

/**
 * Created by wuxn on 2016/5/24.
 */

public class EnterTainToken implements Serializable{
    int code;
    int enterNum;
    String icon;
    int team_id;
    int id;
    int inviteNum;
    String team_name;
    ActivityQrcode activityQrcode;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getEnterNum() {
        return enterNum;
    }

    public void setEnterNum(int enterNum) {
        this.enterNum = enterNum;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInviteNum() {
        return inviteNum;
    }

    public void setInviteNum(int inviteNum) {
        this.inviteNum = inviteNum;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public ActivityQrcode getActivityQrcode() {
        return activityQrcode;
    }

    public void setActivityQrcode(ActivityQrcode activityQrcode) {
        this.activityQrcode = activityQrcode;
    }
}
