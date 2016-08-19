package com.miqtech.master.client.entity;

/**
 * 网吧列表的顶部的热门网吧列表
 * Created by Administrator on 2016/3/18.
 */
public class HotInternetBarInfo {

//    icon	网吧图片	string
//    id	网吧id	number
//    netbar_name	网吧名称	string

    private String icon;
    private int id;
    private String netbar_name;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNetbar_name() {
        return netbar_name;
    }

    public void setNetbar_name(String netbar_name) {
        this.netbar_name = netbar_name;
    }
}
