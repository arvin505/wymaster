package com.miqtech.master.client.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 网吧详细信息
 *
 * @author zhangp
 */
public class  InternetBarInfo implements Serializable {

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


    private String activity_value;//
    private int is_activity;//是否显示活动标签1是0否	number
    private int is_benefit;//是否显示惠1是0否	number

    private String id;// 网吧id
    private String netbar_name;// 网吧名称
    private String address;// 网吧地址
    private String telephone;// 网吧电话
    private String distance;// 离我的位置距离
    private String icon;// 图片
    private String presentation;// 网吧详细介绍
    private int seating;// 网吧机位
    private double latitude;// 网吧经度
    private double longitude;// 网吧纬度
    private String area_id;// 约战id
    private List<Map<String, String>> imgs;// 网吧图片介绍
    private int faved ;
    private int rebate;
    private int has_rebate = 0;// 折扣
    private int algorithm;
    private String price_per_hour;
    private int is_hot = 0;// 热门
    private int is_order = 0;// (订支)是否有
    private String sort;
    private int is_recommend = 0;// (荐是否有)
    private String discount_info;
    private List<YueZhan> matches = new ArrayList<YueZhan>();// 约战集合

    // seating 座位数
    // cpu cpu信息
    // memory 内存信息
    // display 显示器信息
    // graphics 显卡信息
    private Eva eva;// 网吧评论数
    private String cpu;// 网吧cpu信息
    private String memory;// 内存信息
    private String display;// 显示器信息
    private String graphics;// 显卡信息

    private NetBarAmuse amuse;

    private String tag; //	网吧标签	string
    private long remain_time_to_end;// 优惠活动多久结束, 单位毫秒
    private long remain_time_to_start; // 多久开始优惠活动, 单位毫秒
    private String pay_word; // 支付按钮文字(支付网费或优惠支付)string
    private String pay_tip;
    private String name; //网吧名	string
    private int levels;  //	网吧等级网吧等级 0无 1会员 2 金牌 3钻石
    private String is_exclusive; //	是否显示专属1是0否	number
    private long again_get_time; //再次领取红包时间,单位毫秒

    private List<NetbarArea> area;
    private List<NetbarService> services;



    public InternetBarInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

    public InternetBarInfo(String id, String netbar_name) {
        super();
        this.id = id;
        this.netbar_name = netbar_name;
    }

    public String getDiscount_info() {
        return discount_info;
    }

    public void setDiscount_info(String discount_info) {
        this.discount_info = discount_info;
    }

    public int getIs_hot() {
        return is_hot;
    }

    public void setIs_hot(int is_hot) {
        this.is_hot = is_hot;
    }

    public String getPrice_per_hour() {
        return price_per_hour;
    }

    public void setPrice_per_hour(String price_per_hour) {
        this.price_per_hour = price_per_hour;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNetbar_name() {
        return netbar_name;
    }

    public int getHas_rebate() {
        return has_rebate;
    }

    public void setHas_rebate(int has_rebate) {
        this.has_rebate = has_rebate;
    }

    public int getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(int algorithm) {
        this.algorithm = algorithm;
    }

    public void setNetbar_name(String netbar_name) {
        this.netbar_name = netbar_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public int getSeating() {
        return seating;
    }

    public void setSeating(int seating) {
        this.seating = seating;
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
        longitude = longitude;
    }

    public List<YueZhan> getMatches() {
        return matches;
    }

    public void setMatches(List<YueZhan> matches) {
        this.matches = matches;
    }

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }

    public int getIs_order() {
        return is_order;
    }

    public void setIs_order(int is_order) {
        this.is_order = is_order;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getIs_recommend() {
        return is_recommend;
    }

    public void setIs_recommend(int is_recommend) {
        this.is_recommend = is_recommend;
    }

    public List<Map<String, String>> getImgs() {
        return imgs;
    }

    public void setImgs(List<Map<String, String>> imgs) {
        this.imgs = imgs;
    }

    public int getFaved() {
        return faved;
    }

    public void setFaved(int faved) {
        this.faved = faved;
    }

    public int getRebate() {
        return rebate;
    }

    public void setRebate(int rebate) {
        this.rebate = rebate;
    }

    public Eva getEva() {
        return eva;
    }

    public void setEva(Eva eva) {
        this.eva = eva;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getGraphics() {
        return graphics;
    }

    public void setGraphics(String graphics) {
        this.graphics = graphics;
    }

    public NetBarAmuse getAmuse() {
        return amuse;
    }

    public void setAmuse(NetBarAmuse amuse) {
        this.amuse = amuse;
    }

    public String getActivity_value() {
        return activity_value;
    }

    public void setActivity_value(String activity_value) {
        this.activity_value = activity_value;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getRemain_time_to_end() {
        return remain_time_to_end;
    }

    public void setRemain_time_to_end(long remain_time_to_end) {
        this.remain_time_to_end = remain_time_to_end;
    }

    public long getRemain_time_to_start() {
        return remain_time_to_start;
    }

    public void setRemain_time_to_start(long remain_time_to_start) {
        this.remain_time_to_start = remain_time_to_start;
    }

    public String getPay_word() {
        return pay_word;
    }

    public void setPay_word(String pay_word) {
        this.pay_word = pay_word;
    }

    public String getPay_tip() {
        return pay_tip;
    }

    public void setPay_tip(String pay_tip) {
        this.pay_tip = pay_tip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevels() {
        return levels;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }

    public String getIs_exclusive() {
        return is_exclusive;
    }

    public void setIs_exclusive(String is_exclusive) {
        this.is_exclusive = is_exclusive;
    }

    public long getAgain_get_time() {
        return again_get_time;
    }

    public void setAgain_get_time(long again_get_time) {
        this.again_get_time = again_get_time;
    }
    public List<NetbarArea> getArea() {
        return area;
    }

    public void setArea(List<NetbarArea> area) {
        this.area = area;
    }

    public List<NetbarService> getServices() {
        return services;
    }

    public void setServices(List<NetbarService> services) {
        this.services = services;
    }

    @Override
    public String toString() {
        return "InternetBarInfo{" +
                "activity_value='" + activity_value + '\'' +
                ", is_activity=" + is_activity +
                ", is_benefit=" + is_benefit +
                ", id='" + id + '\'' +
                ", netbar_name='" + netbar_name + '\'' +
                ", address='" + address + '\'' +
                ", telephone='" + telephone + '\'' +
                ", distance='" + distance + '\'' +
                ", icon='" + icon + '\'' +
                ", presentation='" + presentation + '\'' +
                ", seating=" + seating +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", area_id='" + area_id + '\'' +
                ", imgs=" + imgs +
                ", faved=" + faved +
                ", rebate=" + rebate +
                ", has_rebate=" + has_rebate +
                ", algorithm=" + algorithm +
                ", price_per_hour='" + price_per_hour + '\'' +
                ", is_hot=" + is_hot +
                ", is_order=" + is_order +
                ", sort='" + sort + '\'' +
                ", is_recommend=" + is_recommend +
                ", discount_info='" + discount_info + '\'' +
                ", matches=" + matches +
                ", eva=" + eva +
                ", cpu='" + cpu + '\'' +
                ", memory='" + memory + '\'' +
                ", display='" + display + '\'' +
                ", graphics='" + graphics + '\'' +
                ", amuse=" + amuse +
                ", tag='" + tag + '\'' +
                ", remain_time_to_end=" + remain_time_to_end +
                ", remain_time_to_start=" + remain_time_to_start +
                ", pay_word='" + pay_word + '\'' +
                ", pay_tip='" + pay_tip + '\'' +
                ", name='" + name + '\'' +
                ", levels=" + levels +
                ", is_exclusive='" + is_exclusive + '\'' +
                ", again_get_time=" + again_get_time +
                ", area=" + area +
                ", services=" + services +
                '}';
    }
}
