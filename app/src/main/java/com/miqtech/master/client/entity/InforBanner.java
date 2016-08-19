package com.miqtech.master.client.entity;

/**
 * 资讯广告
 * Created by zhaosrntao on 2015/11/25.
 */
public class InforBanner {
//    id	资讯ID
//    title	资讯标题
//    cover	资讯banner图
//    type	类型:1图文 2专题 3图集

    private String title;
    private String cover;
    private int id;
    private int type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "InforBanner{" +
                "title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", id=" + id +
                ", type=" + type +
                '}';
    }
}
