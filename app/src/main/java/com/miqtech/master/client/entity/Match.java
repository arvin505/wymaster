package com.miqtech.master.client.entity;

import java.io.Serializable;
import java.util.List;

public class Match implements Serializable{
    String summary;
    String start_time;
    String icon;
    String title;
    int status;
    String end_time;
    int id;
    int has_favor;
    List<MatchJoiner> members;
    String item_name;
    int info_id;
    int memberCount;
    String rule;
    int way;
    String server_name;
    List<MatchSchedule> schedule;
    int applyNum;

    int mobile_required;
    int idcard_required;
    int nickname_required;
    int qq_required;
    int server_required;

    int item_id;// 游戏的id

    int person_allow;//是否允许个人报名:0-否,1-是
    int team_allow;//是否允许战队报名:0-否,1-是

    public int getMobile_required() {
        return mobile_required;
    }

    public void setMobile_required(int mobile_required) {
        this.mobile_required = mobile_required;
    }

    public int getIdcard_required() {
        return idcard_required;
    }

    public void setIdcard_required(int idcard_required) {
        this.idcard_required = idcard_required;
    }

    public int getNickname_required() {
        return nickname_required;
    }

    public void setNickname_required(int nickname_required) {
        this.nickname_required = nickname_required;
    }

    public int getQq_required() {
        return qq_required;
    }

    public void setQq_required(int qq_required) {
        this.qq_required = qq_required;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHas_favor() {
        return has_favor;
    }

    public void setHas_favor(int has_favor) {
        this.has_favor = has_favor;
    }

    public List<MatchJoiner> getMembers() {
        return members;
    }

    public void setMembers(List<MatchJoiner> members) {
        this.members = members;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getInfo_id() {
        return info_id;
    }

    public void setInfo_id(int info_id) {
        this.info_id = info_id;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getWay() {
        return way;
    }

    public void setWay(int way) {
        this.way = way;
    }

    public String getServer_name() {
        return server_name;
    }

    public void setServer_name(String server_name) {
        this.server_name = server_name;
    }

    public List<MatchSchedule> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<MatchSchedule> schedule) {
        this.schedule = schedule;
    }

    public int getServer_required() {
        return server_required;
    }

    public void setServer_required(int server_required) {
        this.server_required = server_required;
    }

    public int getPerson_allow() {
        return person_allow;
    }

    public void setPerson_allow(int person_allow) {
        this.person_allow = person_allow;
    }

    public int getTeam_allow() {
        return team_allow;
    }

    public void setTeam_allow(int team_allow) {
        this.team_allow = team_allow;
    }

    public int getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(int applyNum) {
        this.applyNum = applyNum;
    }
}
