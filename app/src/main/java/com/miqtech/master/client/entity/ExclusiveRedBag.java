package com.miqtech.master.client.entity;

/**
 * Created by wuxn on 2016/3/23.
 */
public class ExclusiveRedBag {
    int money;
    int id;
    int min_money;
    String end_date;

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMin_money() {
        return min_money;
    }

    public void setMin_money(int min_money) {
        this.min_money = min_money;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
}
