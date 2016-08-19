package com.miqtech.master.client.entity;

/**
 * Created by Administrator on 2015/12/8 0008.
 */
public class YueZhan2Info {

//    "id":1740,
//            "releaser_icon":"uploads/imgs/user/random/7.jpg",
//            "sex":0,
//            "begin_time":"2015-12-10 15:22:00",
//            "status":0,
//            "nickname":"呵呵",
//            "item_name":"英雄联盟",
//            "way":1,
//            "people_num":5,
//            "server":"艾欧尼亚",
//            "releaser_id":2049,
//            "apply_count":2
    int id;
    String releaser_icon;
    int apply_count;
    String title;
    int status;
    String begin_time;
    String nickname;
    int way;
    int people_num;
    String server;
    int sex;
    String netbar_name;
    int releaser_id;

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

    public String getReleaser_icon() {
        return releaser_icon;
    }

    public void setReleaser_icon(String realeaser_icon) {
        this.releaser_icon = realeaser_icon;
    }

    public int getApply_count() {
        return apply_count;
    }

    public void setApply_count(int apply_count) {
        this.apply_count = apply_count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getWay() {
        return way;
    }

    public void setWay(int way) {
        this.way = way;
    }

    public int getPeople_num() {
        return people_num;
    }

    public void setPeople_num(int people_num) {
        this.people_num = people_num;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getReleaser_id() {
        return releaser_id;
    }

    public void setReleaser_id(int releaser_id) {
        this.releaser_id = releaser_id;
    }
}
