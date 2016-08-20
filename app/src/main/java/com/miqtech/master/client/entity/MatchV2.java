package com.miqtech.master.client.entity;

import java.util.ArrayList;

/**
 * 新版赛事
 * Created by wuxn on 2016/7/25.
 */
public class MatchV2 {
    //    applyNum	已报人数	number
//    apply_begin	报名开始时间	string
//    apply_end	报名结束时间	string
//    create_date		string
//    icon	图片	string
//    id		number
//    item_id	游戏类型id	number
//    item_name	游戏类型	string
//    max_num	最大人数	number
//    start_time	开赛时间	string
//    state	0预热中1报名中2进行中3已结束	number
//    summary	摘要	string
//    target	悬赏令目标
//    title	标题	string
//    type	1官方赛2自发赛3悬赏令	number

    //   mode  1-线上赛事,2-线下赛事,3-线上海选+线下决赛
//    regime
    int applyNum;
    String apply_begin;
    String apply_end;
    String create_date;
    String icon;
    int id;
    int item_id;
    String item_name;
    int max_num;
    String start_time;
    int state;
    String summary;
    String target;
    String title;
    int type;
    String sponsor_icon;
    String sponsor;
    int mode;
    int regime;
    boolean hasVisibled = false;
    ArrayList<RoundInfo> rounds;
    float count_down;

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    String end_time;

    public int getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(int applyNum) {
        this.applyNum = applyNum;
    }

    public String getApply_begin() {
        return apply_begin;
    }

    public void setApply_begin(String apply_begin) {
        this.apply_begin = apply_begin;
    }

    public String getApply_end() {
        return apply_end;
    }

    public void setApply_end(String apply_end) {
        this.apply_end = apply_end;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public int getMax_num() {
        return max_num;
    }

    public void setMax_num(int max_num) {
        this.max_num = max_num;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public String getSponsor_icon() {
        return sponsor_icon;
    }

    public void setSponsor_icon(String sponsor_icon) {
        this.sponsor_icon = sponsor_icon;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getRegime() {
        return regime;
    }

    public void setRegime(int regime) {
        this.regime = regime;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public boolean isHasVisibled() {
        return hasVisibled;
    }

    public void setHasVisibled(boolean hasVisibled) {
        this.hasVisibled = hasVisibled;
    }

    public float getCount_down() {
        return count_down;
    }

    public void setCount_down(float count_down) {
        this.count_down = count_down;
    }

    public ArrayList<RoundInfo> getRounds() {
        return rounds;
    }

    public void setRounds(ArrayList<RoundInfo> rounds) {
        this.rounds = rounds;
    }

    public class RoundInfo {
        String date;
        String state;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }
}
