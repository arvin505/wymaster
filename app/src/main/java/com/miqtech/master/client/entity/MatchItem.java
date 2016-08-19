package com.miqtech.master.client.entity;

import java.util.List;

/**
 * Created by admin on 2016/8/2.
 */
public class MatchItem {
    //    "item_name":"英雄联盟",
//            "item_id":1,
//            "item_icon":"uploads/imgs/activity/items/lol.png",
//            "num":6
    String item_name;
    int item_id;
    String item_icon;
    int num;
    List<MatchCondition> condition;


    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getItem_icon() {
        return item_icon;
    }

    public void setItem_icon(String item_icon) {
        this.item_icon = item_icon;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<MatchCondition> getCondition() {
        return condition;
    }

    public void setCondition(List<MatchCondition> condition) {
        this.condition = condition;
    }
}
