package com.miqtech.master.client.entity;

/**
 * Created by admin on 2015/12/29.
 */
public class Config {
    String reg_redbag_money;
    StartUpAD startup_ad;
    String hidden_mall;
    String SHOW_GAME;

    String update_msg;

    String android_verison;



    public String getUpdate_msg() {
        return update_msg;
    }

    public void setUpdate_msg(String update_msg) {
        this.update_msg = update_msg;
    }

    public String getAndroid_verison() {
        return android_verison;
    }

    public void setAndroid_verison(String android_verison) {
        this.android_verison = android_verison;
    }

    public String getReg_redbag_money() {
        return reg_redbag_money;
    }

    public void setReg_redbag_money(String reg_redbag_money) {
        this.reg_redbag_money = reg_redbag_money;
    }

    public StartUpAD getStartup_ad() {
        return startup_ad;
    }

    public void setStartup_ad(StartUpAD startup_ad) {
        this.startup_ad = startup_ad;
    }

    public String getHidden_mall() {
        return hidden_mall;
    }

    public void setHidden_mall(String hidden_mall) {
        this.hidden_mall = hidden_mall;
    }

    public String getSHOW_GAME() {
        return SHOW_GAME;
    }

    public void setSHOW_GAME(String SHOW_GAME) {
        this.SHOW_GAME = SHOW_GAME;
    }


}
