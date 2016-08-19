package com.miqtech.master.client.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 订单信息
 */
public class OrderInfo implements Serializable {
    // 预定订单ID
    private String reserve_id;
    // 支付订单ID
    private String order_id;
    private String icon;// 网吧头像
    private String netbar_name;// 网吧名称
    private String netbar_id;//
    private int price;// 网吧预定单价
    private Double amount;// 实际支付的定金
    private String create_date;// 下单时间
    private String seating;// 预定机位数量
    private String reservation_time;// 预定上机时长
    private String telephone;// 网吧电话
    private String overpay = "0";//
    private int is_receive;// 订单是否被商家接受：0-false;1-true;
    private int is_valid;// 0:已取消1:待处理2:已支付
    private String arrive;// 1已到店0未到店
    private int hours;// 	上网时长

    private String out_trade_no;// (商户订单号)
    private String trade_no;// (官方订单号)
    private String type;// (支付类型:1-支付宝;2-财付通;)
    private int status;// -1支付失败0新建订单1支付成功
    private int state;// -1支付失败0新建订单1支付成功
    private String user_nickname;// (网吧会员的名称)
    private String redbag_amount;// (红包抵消金额)
    private String score_amount;// (积分抵消金额)
    private float total_amount;// (上网总金额)
    private String nonce_str;
    private String prepay_id;
    private String user_id;
    private boolean is_related;//	是否连座：0-false;1-true;
    private String begin_time;// 	开始时间
    private String end_time;//	结束时间
    private String netbar_tel;// 	网吧电话

    private Card prize;


    private String order_status;// 	-1支付失败0待支付1支付成功
    private String rebate;// 	网吧折扣
    private String rebate_amount;// 	红包抵扣
    private int refureshImg = 0;
    private String netbar_fav_status;//网吧是否收藏

    private int canShareRedbag;
    private int shareRedbagNumber;
    private String shareRedbagIcon;
    private String shareRedbagTitle;
    private String shareRedbagContent;
    @SerializedName("value_added_amount")
    private float valueAddAmount;

    public float getValueAddAmount() {
        return valueAddAmount;
    }

    public void setValueAddAmount(float valueAddAmount) {
        this.valueAddAmount = valueAddAmount;
    }

    private int orderType;

    private String cs_phone;

    private int canLottery;//1可抽奖0否

    public String getNetbar_fav_status() {
        return netbar_fav_status;
    }

    public void setNetbar_fav_status(String netbar_fav_status) {
        this.netbar_fav_status = netbar_fav_status;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getRebate() {
        return rebate;
    }

    public void setRebate(String rebate) {
        this.rebate = rebate;
    }

    public String getRebate_amount() {
        return rebate_amount;
    }

    public void setRebate_amount(String rebate_amount) {
        this.rebate_amount = rebate_amount;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getNetbar_tel() {
        return netbar_tel;
    }

    public void setNetbar_tel(String netbar_tel) {
        this.netbar_tel = netbar_tel;
    }

    public boolean isIs_related() {
        return is_related;
    }

    public void setIs_related(boolean is_related) {
        this.is_related = is_related;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getReserve_id() {
        return reserve_id;
    }

    public void setReserve_id(String reserve_id) {
        this.reserve_id = reserve_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public int getRefureshImg() {
        return refureshImg;
    }

    public void setRefureshImg(int refureshImg) {
        this.refureshImg = refureshImg;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNetbar_name() {
        return netbar_name;
    }

    public void setNetbar_name(String netbar_name) {
        this.netbar_name = netbar_name;
    }

    public String getNetbar_id() {
        return netbar_id;
    }

    public void setNetbar_id(String netbar_id) {
        this.netbar_id = netbar_id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getSeating() {
        return seating;
    }

    public void setSeating(String seating) {
        this.seating = seating;
    }

    public String getReservation_time() {
        return reservation_time;
    }

    public void setReservation_time(String reservation_time) {
        this.reservation_time = reservation_time;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getOverpay() {
        return overpay;
    }

    public void setOverpay(String overpay) {
        this.overpay = overpay;
    }

    public int getIs_receive() {
        return is_receive;
    }

    public void setIs_receive(int is_receive) {
        this.is_receive = is_receive;
    }

    public int getIs_valid() {
        return is_valid;
    }

    public void setIs_valid(int is_valid) {
        this.is_valid = is_valid;
    }

    public String getArrive() {
        return arrive;
    }

    public void setArrive(String arrive) {
        this.arrive = arrive;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getRedbag_amount() {
        return redbag_amount;
    }

    public void setRedbag_amount(String redbag_amount) {
        this.redbag_amount = redbag_amount;
    }

    public String getScore_amount() {
        return score_amount;
    }

    public void setScore_amount(String score_amount) {
        this.score_amount = score_amount;
    }

    public float getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(float total_amount) {
        this.total_amount = total_amount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getPrepay_id() {
        return prepay_id;
    }

    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }

    public int getState() {

        return state;
    }

    public int getCanShareRedbag() {
        return canShareRedbag;
    }

    public void setCanShareRedbag(int canShareRedbag) {
        this.canShareRedbag = canShareRedbag;
    }

    public int getShareRedbagNumber() {
        return shareRedbagNumber;
    }

    public void setShareRedbagNumber(int shareRedbagNumber) {
        this.shareRedbagNumber = shareRedbagNumber;
    }

    public String getShareRedbagIcon() {
        return shareRedbagIcon;
    }

    public void setShareRedbagIcon(String shareRedbagIcon) {
        this.shareRedbagIcon = shareRedbagIcon;
    }

    public String getShareRedbagTitle() {
        return shareRedbagTitle;
    }

    public void setShareRedbagTitle(String shareRedbagTitle) {
        this.shareRedbagTitle = shareRedbagTitle;
    }

    public String getShareRedbagContent() {
        return shareRedbagContent;
    }

    public void setShareRedbagContent(String shareRedbagContent) {
        this.shareRedbagContent = shareRedbagContent;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public boolean is_related() {
        return is_related;
    }

    public String getCs_phone() {
        return cs_phone;
    }

    public void setCs_phone(String cs_phone) {
        this.cs_phone = cs_phone;
    }

    public int getCanLottery() {
        return canLottery;
    }

    public void setCanLottery(int canLottery) {
        this.canLottery = canLottery;
    }

    public Card getPrize() {
        return prize;
    }

    public void setPrize(Card prize) {
        this.prize = prize;
    }
}
