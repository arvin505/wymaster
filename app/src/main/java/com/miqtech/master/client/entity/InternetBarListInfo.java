package com.miqtech.master.client.entity;

/**
 * 网吧列表信息
 * Created by Administrator on 2016/3/18.
 */
public class InternetBarListInfo {

//    activity_value		number
//    address	网吧地址	string
//    avgScore	评分	number
//    distance	距离	number
//    icon	网吧图片	string
//    id	网吧id	number
//    is_activity	是否显示活动标签1是0否	number
//    is_benefit	是否显示惠1是0否	number
//    is_hot	是否显示热1是0否	number
//    is_order	是否显示支1是0否	number
//    latitude	网吧纬度	number
//    longitude	网吧经度	number
//    netbar_name	网吧名	string
//    price_per_hour	价格	string

    private String activity_value;
    private String address;
    private String avgScore;
    private String distance;
    private String icon;
    private int id;
    private int is_activity;
    private int is_benefit;
    private int is_hot;
    private int is_order;
    private double latitude;
    private double longitude;
    private String netbar_name;
    private String price_per_hour;

    public String getActivity_value() {
        return activity_value;
    }

    public void setActivity_value(String activity_value) {
        this.activity_value = activity_value;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(String avgScore) {
        this.avgScore = avgScore;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

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

    public int getIs_activity() {
        return is_activity;
    }

    public void setIs_activity(int is_activity) {
        this.is_activity = is_activity;
    }

    public int getIs_benefit() {
        return is_benefit;
    }

    public void setIs_benefit(int is_benefit) {
        this.is_benefit = is_benefit;
    }

    public int getIs_hot() {
        return is_hot;
    }

    public void setIs_hot(int is_hot) {
        this.is_hot = is_hot;
    }

    public int getIs_order() {
        return is_order;
    }

    public void setIs_order(int is_order) {
        this.is_order = is_order;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getNetbar_name() {
        return netbar_name;
    }

    public void setNetbar_name(String netbar_name) {
        this.netbar_name = netbar_name;
    }

    public String getPrice_per_hour() {
        return price_per_hour;
    }

    public void setPrice_per_hour(String price_per_hour) {
        this.price_per_hour = price_per_hour;
    }
}
