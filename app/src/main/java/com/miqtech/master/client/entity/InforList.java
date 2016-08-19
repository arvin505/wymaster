package com.miqtech.master.client.entity;

import java.util.List;

/**
 * 资讯、专题下的listview
 * Created by zhaosentao on 2015/11/25.
 */
public class InforList {
//    isLast	是	int	是否为最后一页：0-否;1-是;
//    id	是	Long	资讯ID
//    title	是	String	资讯标题
//    icon	是	String	缩略图URL
//    brief	是	String	简介
//    total	是	String	总条数
//    currentPage	是	String	当前页数
//    imgs	是	String	图集图片
//    type	类型:1图文 2专题 3图集


//    private int id;
//    private String title;
//    private String icon;
//    private String brief;
//    private String imgs;
//    private int type;

    private int isLast;
    private int total;
    private int currentPage;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private List<InforItemDetail> list;

    private String remain;//界面顶部的图片（专题界面需要，资讯界面不需要）

    public int getIsLast() {
        return isLast;
    }

    public void setIsLast(int isLast) {
        this.isLast = isLast;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<InforItemDetail> getList() {
        return list;
    }

    public void setList(List<InforItemDetail> list) {
        this.list = list;
    }

    public String getRemain() {
        return remain;
    }

    public void setRemain(String remain) {
        this.remain = remain;
    }
}
