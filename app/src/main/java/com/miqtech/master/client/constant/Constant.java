package com.miqtech.master.client.constant;


import com.miqtech.master.client.entity.AwardInfo;
import com.miqtech.master.client.entity.City;
import com.miqtech.master.client.entity.GameItem;

import java.util.List;

/**
 * Created by wuxuenan on 2015/11/19 0019.
 */
public class Constant {

    public static double latitude;//当前城市的纬度
    public static double longitude;//当前城市的经度
    public static String cityName;//城市名
    public static City currentCity = new City();
    public static City locCity = new City();
    public static boolean isLocation = false;

    public static String register_phone = "15056897426";

    /**
     * 微信分享app_id
     **/
    public static final String WX_APP_ID = "wxb10451ed2c4a6ce3";
    public static final String WX_APP_SECRET = "d95b2512200cb6696c63e6fec2110a4d";

    /**
     * QQ分享app_id
     **/
    public static final String QQ_APP_ID = "1104513102";
    public static final String QQ_APP_KEY = "L3PVlr3bpXd9I63d";

    /**
     * 微博分享app_id
     **/
    public static final String WB_APP_KEY = "3734649134";
    public static final String WB_APP_SECRET = "57bba34da9bae335b351f2057283bbf1";
    public static final String WB_REDIRECTURL = "http://www.wangyuhudong.com";
    public static final String WB_APP_SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";


    public static final String PHONE_FORMAT = "^(13|15|18|17|12|14|16|19)\\d{9}$";
    public static final String EMAIL_FORMAT = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    public static final String ID_CARD_FORMAT = "^\\d{15}|^\\d{17}([0-9]|X|x)$";


    public static final int FILTER_GAME = 1; //比赛筛选
    public static final int FILTER_BATTLE = 2; //约战筛选
    public static final int FILTER_GAME_COMPOSITE = 3; //综合比赛筛选
    public static final int FILTER_GAME_OFFLINE = 4;   //线下比赛
    public static List<GameItem> gameItems;  //竞技项目
    public static List<AwardInfo> awardInfos;  //奖品类型


    //每日任务
    public static final int OPEN_APP = 1;//打开客户端

    public static final int JOIN_YUEZHAN = 2;//参加约战

    public static final int RELEASE_YUEZHAN = 3;//发起约战

    public static final int RESERVE_NETBAR = 4; //预定网吧

    public static final int PAY_NETBAR = 5;//支付网费

    public static final int ATTENTION_OTHER = 6;//关注他人

    public static final int COLLECT_GAME = 7;//收藏手游

    public static final int SHARE = 8;//分享

    //新手任务
    public static final int NEWUSER_UPDATEDATA = 1;//完善用户信息

    public static final int NEWUSER_JOIN_MATCH = 2;//完善参赛信息

    public static final int NEWUSER_FIRST_PAY = 3;//首次支付

    public static final int NEWUSER_FIRST_RELEASE_YUEZHAN = 4;//首次发布约战

    public static final int NEWUSER_FIRST_EXCHANGE = 5;//首次兑换商品

    public static final String FIXPATCH = "data/data/com.miqtech.master.client/files" + "/hotfixpath/";

    public static final String PATCNAME = "wymasterpatch.jar";

    public static final String BUGTAGS_KEY_LIVE = "475936a4af1b9e3bcfbd0e6bea245c25";
    public static final String BUGTAGS_KEY_BETA = "52a10a4f9910e4da9f9c6f845b9118fc";


}
