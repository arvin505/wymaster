package com.miqtech.master.client.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/8.
 */
public class NetbarArea implements Serializable {
    private String area_name; //	座位区	string
    private float price;//	原价	number
    private float rebate_price;//	会员价	number

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }


    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getRebate_price() {
        return rebate_price;
    }

    public void setRebate_price(float rebate_price) {
        this.rebate_price = rebate_price;
    }
}
