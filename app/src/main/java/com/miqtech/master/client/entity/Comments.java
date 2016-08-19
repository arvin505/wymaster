package com.miqtech.master.client.entity;

import java.util.List;

/**
 * Created by Administrator on 2016/3/9.
 */
public class Comments {
    private List<CommentInfo> list;
    private int total;//总数
    private int isLast;//是否是最后一个
    private int currentPage;

    public List<CommentInfo> getList() {
        return list;
    }

    public void setList(List<CommentInfo> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getIsLast() {
        return isLast;
    }

    public void setIsLast(int isLast) {
        this.isLast = isLast;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
