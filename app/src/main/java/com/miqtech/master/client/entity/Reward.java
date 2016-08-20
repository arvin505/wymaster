package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 悬赏令卡片数据
 * Created by zhaosentao on 2016/7/25.
 */
public class Reward {

    private int id;
    private int commentNum;//	评论数
    private int favNum;//点赞数
    private String cover;//详情图
    private String icon;//列表图
    private int is_win;//	用户是否获奖,0-未获奖 1-已获奖
    private String start_time;
    private String end_time;
    private String target;
    private String title;
    private int type;//1-普通 2-独占鳌头3-排行榜
    private int infoStatus;//(用户没有中奖怎没有该字段) 0-没有填写信息 1-填写信息
    private int checkStatus;//审查状态 -1未到结束时间 0-正在进行中 1-已经结束
    private int award_type;//1-自有商品 3-实物 4-虚拟商品
    private String reward;//悬赏令奖品
    private ArrayList<RewardGrade> gradeList;//悬赏令类型为2 独占鳌头类型为3 排行榜时有该字段，没有成绩排行则该字段不存在
    private int status;//0-用户未获奖 1-奖品为自由商品且发放成功 2-奖品非网娱自有且用户未填写信息 3-奖品非网娱自有商品且用户填写信息 5-发放奖品异常

    private boolean isShowListView = false;//当type为排行榜，且结束时，用来记录listView是否弹出显示了
    private int isEnd;  //0未结束 1 已结束


    private int has_favor;//0-未点赞 1-已点赞 (未登录状态无该字段)
    private long time;//倒计时毫秒级(悬赏令结束后该字段不返回)
    private int applyNum;

    private String state;

    private long count_down;

    public int getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(int applyNum) {
        this.applyNum = applyNum;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsEnd() {
        return isEnd;
    }

    public void setIsEnd(int isEnd) {
        this.isEnd = isEnd;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getFavNum() {
        return favNum;
    }

    public void setFavNum(int favNum) {
        this.favNum = favNum;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getIs_win() {
        return is_win;
    }

    public void setIs_win(int is_win) {
        this.is_win = is_win;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }


    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getInfoStatus() {
        return infoStatus;
    }

    public void setInfoStatus(int infoStatus) {
        this.infoStatus = infoStatus;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public int getAward_type() {
        return award_type;
    }

    public void setAward_type(int award_type) {
        this.award_type = award_type;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public ArrayList<RewardGrade> getGradeList() {
        return gradeList;
    }

    public void setGradeList(ArrayList<RewardGrade> gradeList) {
        this.gradeList = gradeList;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isShowListView() {
        return isShowListView;
    }

    public void setShowListView(boolean showListView) {
        isShowListView = showListView;
    }

    public int getHas_favor() {
        return has_favor;
    }

    public void setHas_favor(int has_favor) {
        this.has_favor = has_favor;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setIsShowListView(boolean isShowListView) {
        this.isShowListView = isShowListView;
    }

    public long getCount_down() {
        return count_down;
    }

    public void setCount_down(long count_down) {
        this.count_down = count_down;
    }
}
