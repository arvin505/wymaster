package com.miqtech.master.client.entity;

import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.utils.Utils;

/**
 * Created by xiaoyi on 2016/7/28.
 */
public class MessageCount {
    private int activity;

    private int comment;

    private int sys;

    private int order;

    public void setActivity(int activity) {
        this.activity = activity;
    }

    public int getActivity() {
        return this.activity;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getComment() {
        return this.comment;
    }

    public void setSys(int sys) {
        this.sys = sys;
    }

    public int getSys() {
        return this.sys;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public int getTotal() {
        if (total == 0) {
            total = order + sys + comment + activity;
        }
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    private int total;
}
