package com.miqtech.master.client.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by xiaoyi on 2016/7/5.
 * 自发赛实体
 */
public class ZifaMatch implements Parcelable {

    @SerializedName("round_id")
    private String roundId; //赛事场次id
    @SerializedName("activity_begin")
    private String activityBegin;  //赛事开始时间
    @SerializedName("event_id")
    private String eventId;
    private String name;//赛事名称
    @SerializedName("apply_num")
    private int applyNum; //报名人数
    @SerializedName("to_apply")
    private int toApply;//1显示立即报名0显示查看详情
    private String poster; //图片
    @SerializedName("max_num")
    private int maxNum;//最大人数
    private String sponsor; //主办方
    @SerializedName("apply_begin")
    private String applyBegin; //报名开始时间
    @SerializedName("apply_end")
    private String applyEnd;//报名结束时间
    @SerializedName("prize_setting")
    private String prizeSetting;//赛事奖励
    @SerializedName("regime_rule")
    private String regimeRule; //赛事规则
    private List<EventAgainst> eventProcessList;//赛事进程
    private String buttonText;// 按钮文本
    private int buttonType ;//按钮跳转类型0无1报名2对阵图3签到4赛事进程
    private int canClick;//按钮是否能点击1是0否

    public int getButtonType() {
        return buttonType;
    }

    public void setButtonType(int buttonType) {
        this.buttonType = buttonType;
    }

    public int getCanClick() {
        return canClick;
    }

    public void setCanClick(int canClick) {
        this.canClick = canClick;
    }

    private String tip; //按钮上方提示
    private long time;  //倒计时时间
    @SerializedName(" is_apply")
    private int  isApply;// 是否已报名1是0否
    @SerializedName("is_sign")
    private int isSign ; //是否签到1是0否
    @SerializedName("need_sign")
    private int needSign ;//是否需要签到1是0否
    @SerializedName("need_sign_minute")
    private int needSignMinute ;//比赛开始前多少时间签到

    public int getIsApply() {
        return isApply;
    }

    public void setIsApply(int isApply) {
        this.isApply = isApply;
    }

    public int getIsSign() {
        return isSign;
    }

    public void setIsSign(int isSign) {
        this.isSign = isSign;
    }

    public int getNeedSign() {
        return needSign;
    }

    public void setNeedSign(int needSign) {
        this.needSign = needSign;
    }

    public int getNeedSignMinute() {
        return needSignMinute;
    }

    public void setNeedSignMinute(int needSignMinute) {
        this.needSignMinute = needSignMinute;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public String getApplyBegin() {
        return applyBegin;
    }

    public void setApplyBegin(String applyBegin) {
        this.applyBegin = applyBegin;
    }

    public String getApplyEnd() {
        return applyEnd;
    }

    public void setApplyEnd(String applyEnd) {
        this.applyEnd = applyEnd;
    }

    public String getRegimeRule() {
        return regimeRule;
    }

    public void setRegimeRule(String regimeRule) {
        this.regimeRule = regimeRule;
    }

    public String getPrizeSetting() {
        return prizeSetting;
    }

    public void setPrizeSetting(String prizeSetting) {
        this.prizeSetting = prizeSetting;
    }

    public List<EventAgainst> getEventProcessList() {
        return eventProcessList;
    }

    public void setEventProcessList(List<EventAgainst> eventProcessList) {
        this.eventProcessList = eventProcessList;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getRoundId() {
        return roundId;
    }

    public void setRoundId(String roundId) {
        this.roundId = roundId;
    }

    public String getActivityBegin() {
        return activityBegin;
    }

    public void setActivityBegin(String activityBegin) {
        this.activityBegin = activityBegin;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(int applyNum) {
        this.applyNum = applyNum;
    }

    public int getToApply() {
        return toApply;
    }

    public void setToApply(int toApply) {
        this.toApply = toApply;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }


    public ZifaMatch() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roundId);
        dest.writeString(this.activityBegin);
        dest.writeString(this.eventId);
        dest.writeString(this.name);
        dest.writeInt(this.applyNum);
        dest.writeInt(this.toApply);
        dest.writeString(this.poster);
        dest.writeInt(this.maxNum);
        dest.writeString(this.sponsor);
        dest.writeString(this.applyBegin);
        dest.writeString(this.applyEnd);
        dest.writeString(this.prizeSetting);
        dest.writeString(this.regimeRule);
        dest.writeTypedList(this.eventProcessList);
        dest.writeString(this.buttonText);
        dest.writeInt(this.buttonType);
        dest.writeInt(this.canClick);
        dest.writeString(this.tip);
        dest.writeLong(this.time);
        dest.writeInt(this.isApply);
        dest.writeInt(this.isSign);
        dest.writeInt(this.needSign);
        dest.writeInt(this.needSignMinute);
    }

    protected ZifaMatch(Parcel in) {
        this.roundId = in.readString();
        this.activityBegin = in.readString();
        this.eventId = in.readString();
        this.name = in.readString();
        this.applyNum = in.readInt();
        this.toApply = in.readInt();
        this.poster = in.readString();
        this.maxNum = in.readInt();
        this.sponsor = in.readString();
        this.applyBegin = in.readString();
        this.applyEnd = in.readString();
        this.prizeSetting = in.readString();
        this.regimeRule = in.readString();
        this.eventProcessList = in.createTypedArrayList(EventAgainst.CREATOR);
        this.buttonText = in.readString();
        this.buttonType = in.readInt();
        this.canClick = in.readInt();
        this.tip = in.readString();
        this.time = in.readLong();
        this.isApply = in.readInt();
        this.isSign = in.readInt();
        this.needSign = in.readInt();
        this.needSignMinute = in.readInt();
    }

    public static final Creator<ZifaMatch> CREATOR = new Creator<ZifaMatch>() {
        @Override
        public ZifaMatch createFromParcel(Parcel source) {
            return new ZifaMatch(source);
        }

        @Override
        public ZifaMatch[] newArray(int size) {
            return new ZifaMatch[size];
        }
    };
}
