package com.miqtech.master.client.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/8.
 */
public class NetbarService implements Serializable {

    private String commodity_id; //	资源id	number
    private String interest_num; //	感兴趣数	number
    private String name; //	资源名称	string
    private String property_id; //	资源属性id	number

    public String getCommodity_id() {
        return commodity_id;
    }

    public void setCommodity_id(String commodity_id) {
        this.commodity_id = commodity_id;
    }

    public String getInterest_num() {
        return interest_num;
    }

    public void setInterest_num(String interest_num) {
        this.interest_num = interest_num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProperty_id() {
        return property_id;
    }

    public void setProperty_id(String property_id) {
        this.property_id = property_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url; //	图片地址	string
}
