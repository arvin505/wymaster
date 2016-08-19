package com.miqtech.master.client.entity;

import java.io.Serializable;

/**
 * Created by zhaosentao on 2015/11/23.
 */
public class City implements Comparable<City> ,Serializable{
    private String name;
    private String areaCode;
    private String pinyin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    public int compareTo(City city) {
        return this.pinyin.compareToIgnoreCase(city.getPinyin());
    }
}
