package com.miqtech.master.client.entity;

/**
 * Created by admin on 2016/4/15.
 */
public class InfoRecommend {
//    brief	简介(如果是视频则为副标题)	string
//    icon	缩略图URL	string
//    id	资讯ID	number
//    imgs	图集图片,逗号隔开
//    keyword	关键字
//    read_num	阅读数	number
//    time	视频的时间
//    title	资讯标题	string
//    type	类型:1图文 2专题 3图集4视频	number

    String brief;
    String icon;
    int id;
    String imgs;
    String keyword;
    int read_num;
    String time;
    String title;
    int type;

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
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

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getRead_num() {
        return read_num;
    }

    public void setRead_num(int read_num) {
        this.read_num = read_num;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
