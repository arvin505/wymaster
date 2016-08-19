package com.miqtech.master.client.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/1/8.
 */
public class MatchSchedule implements Serializable {

    /*3.1.1、 查看报名日期与场地*/
//    activityId	是	long	赛事ID
//    round	是	int	场次
//    date	是	date	赛事日期
//    status	是	int	赛事场次报名状态:1-报名中,2-报名未开始,3-报名已截止
//    hasApply	是	int	当前用户报名状态:0-未报名,1-个人报名,2-战队报名
//    areas	是	Map	地区信息
//    netbars	是	Map	网吧信息
//    name	是	String	地区或网吧名
//    areaCode	是	String	地区或网吧的地区码

    int id;
    int activityId;
    List<MatchTeam> teams;
    List<MatchArea> areas;
    int round;
    String date;
    int status;
    int hasApply;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public List<MatchTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<MatchTeam> teams) {
        this.teams = teams;
    }

    public List<MatchArea> getAreas() {
        return areas;
    }

    public void setAreas(List<MatchArea> areas) {
        this.areas = areas;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getHasApply() {
        return hasApply;
    }

    public void setHasApply(int hasApply) {
        this.hasApply = hasApply;
    }
}
