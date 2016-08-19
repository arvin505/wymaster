package com.miqtech.master.client.entity;

import java.util.List;

/**
 * 首页赛事股
 * Created by wuxn on 2016/7/23.
 */
public class HomePageRecommendAndBanner {
    List<Banner> banner;
    List<HeadLine> headlines;
    MyMatch myMatch;

    public List<Banner> getBanner() {
        return banner;
    }

    public void setBanner(List<Banner> banner) {
        this.banner = banner;
    }

    public List<HeadLine> getHeadlines() {
        return headlines;
    }

    public void setHeadlines(List<HeadLine> headlines) {
        this.headlines = headlines;
    }

    public MyMatch getMyMatch() {
        return myMatch;
    }

    public void setMyMatch(MyMatch myMatch) {
        this.myMatch = myMatch;
    }

}
