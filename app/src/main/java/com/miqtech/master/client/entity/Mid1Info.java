package com.miqtech.master.client.entity;

import java.util.Arrays;

/**
 * Created by Administrator on 2015/11/23.
 * 腰图
 */
public class Mid1Info {

    private RecommendInfo matchInfo;

    private String icons[];

    public RecommendInfo getMatchInfo() {
        return matchInfo;
    }

    public void setMatchInfo(RecommendInfo matchInfo) {
        this.matchInfo = matchInfo;
    }

    public String[] getIcons() {
        return icons;
    }

    public void setIcons(String[] icons) {
        this.icons = icons;
    }

    @Override
    public String toString() {
        return "Mid1Info{" +
                "matchInfo=" + matchInfo +
                ", icons=" + Arrays.toString(icons) +
                '}';
    }
}
