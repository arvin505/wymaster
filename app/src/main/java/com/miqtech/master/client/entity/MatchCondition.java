package com.miqtech.master.client.entity;

import java.util.List;

/**
 * Created by admin on 2016/8/2.
 */
public class MatchCondition {

    //    num	比赛数
//    state	多个状态	array<object>
//    type	0全类型1官方赛2自发赛3悬赏令	number
    int num;
    List<MatchState> state;
    int type;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<MatchState> getState() {
        return state;
    }

    public void setState(List<MatchState> state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
