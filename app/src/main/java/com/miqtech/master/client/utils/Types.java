package com.miqtech.master.client.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/26.
 */
public class Types {
    public static final Map<Integer, String> BANNERTYPE = new HashMap<>();
    static {
        initBannerType();
    }

    private static void initBannerType(){
        BANNERTYPE.put(1,"网吧");
        BANNERTYPE.put(2,"手游");
        BANNERTYPE.put(3,"赛事");
        BANNERTYPE.put(4,"网娱官方活动");
        BANNERTYPE.put(5,"推广/广告");

        BANNERTYPE.put(10,"官方比赛");
        BANNERTYPE.put(11,"娱乐赛");
        BANNERTYPE.put(12,"约战");
        BANNERTYPE.put(13,"福利");
        BANNERTYPE.put(15,"资讯");
        BANNERTYPE.put(16,"自发赛");

        BANNERTYPE.put(20,"资讯");
        BANNERTYPE.put(21,"攻略");
        BANNERTYPE.put(22,"推荐");
        BANNERTYPE.put(23,"专题");
        BANNERTYPE.put(24,"独家");
        BANNERTYPE.put(25,"视频");
    }
}
