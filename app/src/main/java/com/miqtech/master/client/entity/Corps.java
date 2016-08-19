package com.miqtech.master.client.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 战队详细信息
 *
 * @author zhangp
 */
public class Corps implements Serializable {
    private int team_id;//战队Id
    private String team_name;// 	是 	int 	战队名
    private int is_monitor;//是否队长
    private int round;//场次
    private int total_num;//总人数
    private int num;//实际人数
    private int activity_id;//赛事ID
    private String title;//赛事标题
    private String header;//
    private int is_join;
    private String start_time;//赛事开始时间
    private String activity_icon;//赛事icon
    private String netbar_name;//赛事地点
    private List<TeammateInfo> members;//队员
    private String end_time;//比赛结束时间
    private int status;//比赛状态
    private String address;
    private int inviteNum;//战队当前申请人数
    private String qrcode;//战队二维码
    private String over_time;//
    private long header_user_id;
    private int state;//	1已申请2接受邀请

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public long getHeader_user_id() {
        return header_user_id;
    }

    public void setHeader_user_id(long header_user_id) {
        this.header_user_id = header_user_id;
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


    public int getIs_join() {
        return is_join;
    }


    public void setIs_join(int is_join) {
        this.is_join = is_join;
    }


    public int getNum() {
        return num;
    }


    public void setNum(int num) {
        this.num = num;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public int getRound() {
        return round;
    }


    public void setRound(int round) {
        this.round = round;
    }


    public int getTotal_num() {
        return total_num;
    }


    public void setTotal_num(int total_num) {
        this.total_num = total_num;
    }


    public int getActivity_id() {
        return activity_id;
    }


    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
    }


    public String getHeader() {
        return header;
    }


    public void setHeader(String header) {
        this.header = header;
    }


    public String getTeam_name() {
        return team_name;
    }


    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }


    public int getIs_monitor() {
        return is_monitor;
    }


    public void setIs_monitor(int is_monitor) {
        this.is_monitor = is_monitor;
    }

    public String getStart_time() {
        return start_time;
    }


    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }


    public String getActivity_icon() {
        return activity_icon;
    }


    public void setActivity_icon(String activity_icon) {
        this.activity_icon = activity_icon;
    }


    public String getNetbar_name() {
        return netbar_name;
    }


    public void setNetbar_name(String netbar_name) {
        this.netbar_name = netbar_name;
    }


    public List<TeammateInfo> getMembers() {
        return members;
    }


    public void setMembers(List<TeammateInfo> members) {
        this.members = members;
    }

    public int getTeam_Id() {
        return team_id;
    }


    public void setTeam_Id(int team_Id) {
        this.team_id = team_Id;
    }

    public String getOver_time() {
        return over_time;
    }

    public void setOver_time(String over_time) {
        this.over_time = over_time;
    }

    public Corps(int id, String team_name, int is_monitor) {
        super();
        this.team_id = id;
        this.team_name = team_name;
        this.is_monitor = is_monitor;
    }

    public Corps() {
    }

    public int getInviteNum() {
        return inviteNum;
    }

    public void setInviteNum(int inviteNum) {
        this.inviteNum = inviteNum;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
