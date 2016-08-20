package com.miqtech.master.client.http;

/**
 * Created by Administrator on 2015/11/19 0019.
 */
public class HttpConstant {
//        public static String SERVICE_HTTP_AREA = "http://172.16.2.62/";
//    public static String SERVICE_HTTP_AREA = "http://172.16.2.14/";
    public static String SERVICE_HTTP_AREA = "http://wy.api.wangyuhudong.com/";
//    public static String SERVICE_HTTP_AREA = "http://api.wangyuhudong.com/";

    /**
     * 埋点地址前缀
     */
//    public static String SERVICE_HTTP_AREA_1 = "http://192.168.30.245:8077/";
//    public static String SERVICE_HTTP_AREA_1 = "http://wy.log.wangyuhudong.com/";
    public static String SERVICE_HTTP_AREA_1 = "http://log.wangyuhudong.com/";


    /**
     * 外网测试的地址
     */
    //    public static String SERVICE_HTTP_AREA = "http://wy.api.wangyuhudong.com/";
    // public static String SERVICE_HTTP_AREA = "http://api.wangyuhudong.com/";

    /**
     * 正式图片获取地址
     */
    public final static String SERVICE_UPLOAD_AREA = "http://img.wangyuhudong.com/";

    /**
     * 微信登陆成功后去获取微信的用户信息，比如openid、头像d等等
     */
    public static final String GET_USER_INFO_BY_WEIXIN_LOGIN = "https://api.weixin.qq.com/sns/oauth2/access_token?";
    /**
     * 第三方登陆后要传给后台的数据
     */
    public static final String THIRD_LOGIN = "thirdLogin?";

    /**
     * 第三方手机绑定
     */
    public static final String BIND_MOBILEPHONE = "bindMobilephone?";


    public static final String USER_LOGIN = "login?";

    /**
     * 首页接口
     */
    public static final String INDEX = "v2/index";

    /**
     * 首页网吧推荐
     */
    public static final String NETBAR_RECOMMEND = "netbar/recommend";

    /**
     * 首页，竞技大厅，资讯banner
     */
    public static final String AD = "ad";

    /**
     * 竞技大厅卡片转动区
     */
    public static final String ATHLETICS_RECOMEND = "athletics/athleticsRecomend";

    /**
     * 竞技大厅卡片转动区 lvOfficialActivity
     */
    public static final String OFFICIAL_ACTIVITY = "athletics/latestActivity";
    /**
     * 资讯模块banner
     */
    public static final String INFO_BANNER = "v2/activity/info/banner";
    /**
     * 资讯列表
     */
    public static final String INFO_LIST = "v2/activity/info/list";
    /**
     * 娱乐赛列表
     */
    public static final String AMUSE_LIST = "v2/amuse/list?";
    /**
     * 娱乐赛详情
     */
    public static final String AMUSE_DETAILS = "v2/amuse/detail?";

    /**
     * v3.1.4评论赛事资讯（官方赛、娱乐赛,资讯）
     */
    public static final String AMUSE_COMMENT_LIST = "v2/amuse/comment_list?";


    /**
     * 约战列表
     */
    public static final String LATEST_BATTLE = "athletics/latestBattle";
    /**
     * 专题列表
     */
    public static final String TOPIC_LIST = "v2/activity/info/subjectList?";
    /**
     * 资讯图集赞
     */
    public static final String INFO_PRAISE = "v2/activity/info/praise?";
    /**
     * 资讯收藏或取消
     */
    public static final String INFO_FAV = "v2/activity/info/fav?";
    /**
     * 资讯详情
     */
    public static final String INFO_DETAIL = "v2/activity/info/detail?";
    /**
     * 综合赛事列表
     */
    public static final String COMPOSITIVE_ACTIVITY_LIST = "v2/compositive/activity/list";

    /**
     * 约战列表
     */
    public static final String YUEZHAN_LIST = "activity/match/list";

    /**
     * 报名人员列表
     */
    public static final String AMUSE_APPLYER_LIST = "v2/amuse/applyer_list";

    // 游戏INDEX
    public static final String GAME_INDEX = "game/index";
    // 游戏BANNER
    public static final String GAME_BANNER = "game/banner";
    // 手游下载
    public static final String GAME_DOWNLOAD = "game/download?";
    // 手游详情
    public static final String GAME_DETAIL = "game/detail?";
    /**
     * 搜索附近网吧 搜索模式
     **/
    public static final String NEARBY_SEARCH_LIST = "netbar/searchLocalNetbar?";
    // 手游搜索
    public static final String GAME_SEARCH = "game/search?";
    // 周热门游戏
    public static final String GAME_WEEK_HOT_LIST = "game/weeklyHot?";
    // 新游排行
    public static final String GAME_NEW_LIST = "game/newest?";
    // 热门排行
    public static final String GAME_HOT_SORT_LIST = "game/hot?";
    // 收藏手游
    public static final String GAME_FAV = "game/favor?";
    // 金币商城首页广告
    public static final String COINS_STORE_ADVERTISE_LIST = "advertise/list";
    // 金币商城首页金币显示的数量
    public static final String GET_COINS_NUMS = "statistics/userCoin?";
    // 获取金币商城列表（金币专区和抽奖专区）
    public static final String GET_COINS_LIST = "commodity/list";
    // 金币收支记录
    public static final String COIN_HISTORY = "statistics/coinHistory?";
    // 每日金币任务
    public static final String COINS_TASK = "malltask/list?";
    @Deprecated
    // 兑换历史
    /**
     * v3.2.0 replace by see @link{EXCHANGE_RECORD}
     */
    public static final String EXCHANGE_HISTORY = "statistics/exchangeHistory?";
    // 今日收益
    public static final String TODAY_INCOME = "statistics/todayIncome?";

    /**
     * 金币商城 兑换详情
     * replace by @link{EXCHANGE_DETAIL_V2} at v320
     */
    @Deprecated
    public static final String EXCHANGE_DETAIL = "commodity/exchangeDetail?";
    // 金币商城 兑换异常
    public static final String MSG_EXCEPTION = "msg/exception?";
    // 会员充值金额数据接口
    public static final String NETBAR_RECHARGE_MONEY = "pay/netbarRechargeMoney?";
    // 可用红包
    public static final String CURRENT_CAN_USE_REDBAG = "my/currentRedbagWithUsedInfo?";
    // 邀请好友成功后回调接口；每日任务-分享
    public static final String AFTER_SHARE = "malltask/shareTask?";
    // 查看用户被邀请的约战列表
    public static final String USER_INVOCATION = "activity/match/userInvocations?";
    // 查看用户被战队邀请
    public static final String TEAM_INVITE = "activity/newInvocation?";
    // 处理战队邀请
    public static final String DO_TEAM_INVITE = "activity/doInvocation?";
    // 接受加入战队申请
    public static final String ACCEPT_TEAM_APPLY = "activity/acceptTeamApply?";
    // 举报
    public static final String REPORT_USER = "/my/inform?";
    // 举报
    public static final String MY_INFORM = "my/inform?";
    // 拉黑/解除拉黑
    public static final String BLACK_OR_NOT = "my/blackOrNot?";
    // 关注TA
    public static final String ATTENTION_USER = "my/concernOrCancel?";
    // 用户的发起的和参与的所有有效约战
    public static final String USER_RELEASE_WAR = "activity/match/getUserAvailableMatches?";
    // 邀请别人(系统用户)参加约战
    public static final String WAR_INVITE_USER = "activity/match/invocationSystemUser?";
    // 粉丝列表
    public static final String USER_FANS = "my/fansList?";
    // 搜索粉丝
    public static final String SEARCH_FANS = "my/searchFans";
    // 关注列表
    public static final String ATTENTION_LIST = "my/concernList?";
    // 搜索关注
    public static final String SEARCH_ATTENTIONS = "my/searchConcern?";
    // 删除照片
    public static final String DELETE_PHOTO = "my/deleteAlbum?";
    // 黑名单列表
    public static final String BLACK_LIST = "my/blackList?";
    // 意见反馈
    public static final String FEEDBACK = "pm/msg?";
    // 我收藏的手游
    public static final String MY_COLLECT_GAME = "my/gameFavor?";
    // 收藏的网吧
    public static final String COLLECT_NETBAR = "my/netbarFavor?";
    // 可用红包
    public static final String CURRENT_REDBAG = "my/currentRedbag?";
    // 历史红包
    public static final String HISTORY_REDBAG = "my/historyRedbag?";
    // 我的消息
    public static final String MY_MESSAGE = "my/myMsg?";
    // 设置已读消息
    public static final String SET_MSG_READED = "msg/read?";
    // 删除我的消息
    public static final String DEL_MESSAGE = "msg/delete?";
    //删除我的消息（最新的）
    public static final String MSG_MULTI_DELETE = "msg/multiDelete?";
    // 我的赛事列表
    public static final String MY_JOIN_MATCH_LIST = "activity/reged?";
    // 退出战队
    public static final String EXIT_CORPS = "activity/exitTeam?";
    // 我的战队
    public static final String MY_CORPS = "activity/myTeamList?";
    // 我发布的约战
    public static final String MY_RELEASE_WAR = "activity/match/pub?";
    // 处理越战邀请
    public static final String DEAL_WITH_INVATE = "/activity/match/dealInvocations?";
    // 我报名的约战
    public static final String MY_APPLY_WAR = "activity/match/reged?";
    // 我的队友
    public static final String MY_TEAMMATE = "activity/myTeammate?";
    // 删除队友
    public static final String DEL_TEAMMATE = "activity/removeTeammate?";
    // 添加队友
    public static final String Add_TEAMMATE = "activity/addTeammate?";
    // 邀请码
    public static final String INVITATION = "my/invitation?";
    // 修改资料
    public static final String EDITUSER = "my/editUser?";
    /**
     * 设置全部消息已读
     */
    public static final String MULTIREAD = "msg/multiRead";

    /**
     * 支付订单列表接口
     */
    public static final String PAY_ORDER_LIST = "netbar/orderList?";
    /**
     * 删除订单
     */
    public static final String DELETE_ORDER = "netbar/deleteOrder?";
    // 上传图片
    public static final String COMMON_UPLOAD = "common/upload?";
    // 网吧评价
    public static final String NETBAR_EVA = "netbar/eva?";
    /**
     * 支付订单详情
     **/
    public static final String PAY_ORDER_DETAILS = "netbar/orderDetail?";
    /**
     * 收藏网吧
     **/
    public static final String NETBAR_COLLECTION = "netbar/favor?";
    /**
     * 取消收藏网吧
     **/
    public static final String NETBAR_CANCEL_COLLECTION = "netbar/unfavor?";
    /**
     * 图片验证码
     */
    public static final String IMAGE_CODE_REGISTER = "checkCode?phone=";
    /**
     * 发送验证码
     */
    public static final String SEND_SMS_CODE_MOBILE = "sendSMSCode?";
    /**
     * 校验验证码
     */
    public static final String CHECK_SMS_CODE = "/checkSMSCode?";
    /**
     * 注册
     */
    public static final String REGISTER = "register";
    /**
     * 找回密码
     */
    public static final String RESET_PASSWORD = "resetPassword";
    /**
     * 邀请码验证
     */
    public static final String CHECK_INVATATION_CODE = "checkInvitationCode?";
    /**
     * 我的收藏的娱乐赛
     */
    public static final String MY_MYAMUSE = "/my/myAmuse?";
    /**
     * 删除娱乐赛事下的评论
     */
    public static final String DEL_COMMENT = "v2/amuse/del_comment";
    /**
     * 赛事（官方赛、娱乐赛）评论列表
     */
    public static final String V2_AMUSE_COMMENT_LIST = "v2/amuse/comment_list?";
    /**
     * v4.0快速评论列表(施亦珎)
     */
    public static final String V4_QUICK_COMMENT_LIST = "v4/comment/quickCommentList";
    /**
     * 点赞评论v2
     */
    public static final String V2_COMMENT_PRAISE = "v2/comment/praise";

    /**
     * 删除参赛卡
     */
    public static final String DELECT_CARD = "v2/activity/card/del";
    /**
     * v3.1.2网吧详情-评价
     */
    public static final String NETBAR_EVA_V2 = "v2/netbar/eva";

    /**
     * 埋点
     */
    public static final String LOG_TIME = "logTime?";

    /**
     * 悬赏令列表
     */
    public static final String BOUNTY_LIST = "v4/bounty/bountyList";

    /**
     * 赏金猎人名单
     */
    public static final String BOUNTY_AWARD = "v4/bounty/award";
    /**
     * 悬赏令点赞
     */
    public static final String BOUNTY_FAV = "v4/bounty/fav";
    /**
     * 悬赏令规则
     */
    public static final String BOUNTY_RULE = "v4/bounty/ruleInfo";
    /**
     * 悬赏令得奖详情
     */
    public static final String BOUNTY_GRADEINFO = "v4/bounty/gradeInfo";
    /**
     * 悬赏令提交成绩
     */
    public static final String BOUNTY_UPGRADE = "v4/bounty/upGrade";

    /*****************************
     * h5页面接口
     *********************************/

    // 赛事详情
    public static final String SUBJECT_INFO = "activity/info/web/detail?id=";
    /**
     * 资讯详情v2
     */
    public static final String V_TWO_SUBJECT_INFO = "v2/activity/info/web/detail?infoId=";

    // 专题资讯详情
    public static final String SPECIAL_INFO = "activity/web/detail?id=";
    // 约战详情
    public static final String YUEZHAN_INFO = "activity/match/web/detail?id=";
    // 报名详情
    public static final String APPLY_INFO = "activity/web/apply?id=";
    // 发布约战
    public static final String RELEASE_INFO = "activity/match/web/release?";
    // 网吧详细介绍
    public static final String NETBAR_INFO = "netbar/web/detail?id=";
    // 客服
    public static final String CUSTOMER_SERVICE = "cs/web/detail";
    // 领取红包
    public static final String RECEIVE_ENVELOPE = "/redbag/web/getRedbag?userId=";
    // 如何开启
    public static final String HELP_OPEN = "redbag/web/help";
    // 关于我们
    public static final String ABOUT_OUR = "/my/web/about";
    // 金币商城天天签到
    public static final String SIGN_IN = "malltask/web/dailySign?userId=";
    // 金币商城商城说明
    public static final String STORE_EXPLAIN = "commodity/mall/introduce";
    // 金币商城 任务详情
    public static final String RENWU_DETAIL = "malltask/web/detail?id=";
    // 金币商品 商品详情
    public static final String GOODS_DETAILS = "commodity/commodityDetail?userId=";
    // 金币商品 抽奖
    public static final String CHOUJIANG = "commodity/lottery?userId=";
    // 金币商品 邀请
    public static final String INVITE_FRIEND = "mall/inviteRecord?userId=";
    // 赛事邀请队友
    public static final String INVITE_TEAMMATE = "activity/invocation?";
    // 首页 报名娱乐赛 海量。。。的接口
    public static final String V_S = "/amuse/list?userId=";
    // 获取未读消息条数
    public static final String GET_MSG_COUNT = "msg/typeCount?";
    //娱乐赛详情v2
    public static final String AMUSE_WEB_RULE = "v2/amuse/web/rule?";
    /**
     * 网娱大师客户端协议
     */
    public static final String AGREEMENT = "agreement";
    /**
     * 支付抽奖
     */
    public static final String LOTTERY_DRAW = "lottery/payedLottery?";

    /**
     * 大转盘
     */
    public static final String MALL_WHEEL = "v2/mall/wheel?";

    /**
     * v3.2.3自发赛赛事进程暨对阵图(施亦珎)
     */
    public static final String EVENT_PROCESS_LIST = "v2/event/eventProcessList";

    /**
     * 修改密码
     */
    public static final String UPDATE_PASSWORD = "updatePassword";

    /***************
     * 分享链接
     *****************/
    // 活动
    public static final String ACTIVITYS_URL = "share/activity/";
    //自发赛
    public static final String EVENT_ULR = "v2/share/eventShare?roundId=";
    // 网吧
    public static final String NETBAR_URL = "v2/share/netbar/";
    // 手游
    public static final String GAME_URL = "share/game/";
    // 赛事
    public static final String ACTIVITY_URL = "share/activity/";
    // 赛事资讯分享
    public static final String OVER_URL = "share/overactivity/";
    // 约战
    public static final String YUEZHAN_URL = "share/match/";
    //娱乐赛分享
    public static final String AMUSE_URL = "/share/amuse/";
    //参赛卡说明
    public static final String CARD_EXPLAIN = "v2/activity/card/faq";
    //资讯分享
    public static final String INFORMATION_URL = "v2/activity/info/share/info?id=";
    //悬赏令分享
    public static final String V2_SHARE_BOUNTY = "v2/share/bountyShare?bountyId=";
    //
    public static final String LIVE_SHARE="v4/live/liveShare?id=";

    /**
     * 竞技项目列表
     */
    public static final String MATCH_ITEM_LIST = "activity/match/item";
    /**
     * 网吧列表（全部）
     */
    public static final String NETBARLISTALL = "netbar/listAll";


    /**
     * 网吧列表
     */
    public static final String NETBARLISTALL_V2 = "v2/netbar/list";


    /**
     * 评论娱乐赛
     */
    public static final String AMUSE_COMMENT = "v2/amuse/comment?";

    /**
     * 放弃报名
     */
    public static final String AMUSE_CANCEL_APPLY = "amuse/verify/cancelApply";
    /**
     * 有效下一级地区
     */
    public static final String VALIDCHILDREN = "common/area/validchildren";

    /**
     * 返回地区信息
     */
    public static final String VALIDATEAERAINFO = "netbar/validateAreaInfo";

    /**
     * 网吧搜索， 地图页
     */
    public static final String SEARCHNETBARFORMAP = "netbar/searchNetbarForMap";

    /**
     * 报名信息填写项
     */
    public static final String AMUSE_APPLY_INFO = "v2/amuse/apply_info";


    /**
     * 报名信息
     */
    public static final String AMUSE_APPLY = "v2/amuse/apply";

    /**
     * 网吧详情
     **/
    public static final String NETBAR_DETAIL = "netbar/detail";

    /**
     * 收藏网吧
     **/
    public static final String NETBAR_FAVOR = "netbar/favor?";

    /**
     * 取消收藏网吧
     **/
    public static final String NETBAR_UNFAVOR = "netbar/unfavor?";


    /**
     * 我的红包-可用红包数量
     */
    @Deprecated
    public static final String MYNLIMITANDREDBAGNUM = "my/minLimitAndRedbagNum";

    /**
     * 支付订单的确认支付
     */
    public static final String ORDER_PAY = "pay/orderPay";

    /**
     * 分享红包
     */
    public static final String SHRAE_REDBAG = "redbag/web/shareRedbag";

    // 赛事详情
    public static final String ACTIVITY_DETAIL = "activity/detail?";

    // 收藏/取消收藏 赛事
    public static final String ACTIVITY_FAVOR = "activity/favor?";

    /**
     * 约战详情
     */
    public static final String MATCH_DETAIL = "activity/match/detail";

    /**
     * 退出约战
     */
    public static final String EXIT_MATCH = "activity/match/cancelApplyMatch";

    /**
     * 报名约战
     */
    public static final String APPLY_MATCH = "activity/match/applyMatch";

    /**
     * 解除约战
     */
    public static final String CANCEL_MATCH = "activity/match/closeMatch";


    /**
     * 获取评论
     */
    public static final String GET_MATCH_COMMENT = "activity/match/comments";

    /**
     * 发布评论
     */
    public static final String SEND_MATCH_COMMENT = "activity/match/sendComment";

    /**
     * 已报名的约战人员
     */
    public static final String MATCH_APPLIERS = "activity/match/appliers";

    /**
     * 邀请别人(通讯录和系统用户)参加约战
     */
    public static final String INVITE_SYS_USER_AND_CONTACT_USER = "activity/match/invocationSystemUserAndContactUser";

    /**
     * 移除约战人员
     */
    public static final String REMOVE_MATCH_PERSON = "activity/match/removeApply";

    /**
     * 活动，关注，粉丝统计
     */
    public static final String MY_STATISTICS = "my/statistics";

    /**
     * 获取他人活动，关注，粉丝统计
     */
    public static final String OTHER_STATISTICS = "otherPeople/statistics";

    /**
     * // 他人资料 *
     */
    public static final String OTHER_PEOPLE = "otherPeople/userinfo";

    /**
     * // 游戏资料列表
     */
    public static final String USER_GAME = "my/userGames";

    /**
     * // 获取他人游戏资料列表
     */
    public static final String OTHER_GAME = "otherPeople/games";

    /**
     * // 活动列表 *
     */
    public static final String MY_ACTIVITY = "my/listActivity";

    /**
     * // 他人活动列表 *
     */
    public static final String OTHER_PEOPLE_ACTIVITY = "otherPeople/activity";

    /**
     * // 获取用户相册
     */
    public static final String MY_LISTALBUM = "my/listAlbum";

    /**
     * // 获取他人相册 *
     */
    public static final String OTHER_PEOPLE_ALBUM = "otherPeople/album";

    /**
     * // 上传照片 *
     */
    public static final String IMPORT_ALBUM = "my/importAlbum?";

    /**
     * // 上传背景 *
     */
    public static final String UPLOAD_PERSONAL_BG = "my/editUser?";

    /**
     * 头像库
     */
    public static final String MY_HEADS = "my/heads";

    /**
     * 上传头像
     **/
    public static final String UPLOAD_HEAD = "my/editUser?";

    /**
     * 城市列表
     **/
    public static final String CITY_LIST = "common/area/allvalidcity?";

    /**
     * 竞技项目
     */
    public static final String MATCH_ITEM = "activity/match/item?";

    /**
     * // 保存游戏资料     *
     */
    public static final String SAVE_USER_GAME = "my/saveUserGame";

    /**
     * 删除个人游戏资料
     */
    public static final String DELETE_USER_GAME = "my/delUserGame?";

    /**
     * 服务器列表
     */
    public static final String GAME_SERVERS = "/game/item/servers?";

    /**
     * 网吧评论列表
     */
    public static final String NET_BAR_COMMENT_LIST = "netbar/evaList?";

    /**
     * // 网吧评论有用 *
     */
    public static final String NET_BAR_EAV_PRAISE = "netbar/eva/praise?";

    /**
     * // 发布约战
     */
    public static final String RELEASE_MATCH = "activity/match/publishBattle?";

    /**
     * 常用联系人
     */
    public static final String COMMON_MEMBER = "activity/commonLinkman?";

    /**
     * 图片上传接口(单图片)
     */
    public static final String UPLOAD_PIC = "common/upload?";

    /**
     * 按名字搜索网吧
     **/
    public static final String NETBAR_NAME_SEARCH_LIST = "netbar/searchNetbar?";

    /**
     * 资讯收藏
     */
    public static final String INFO_FAVLIST = "v2/activity/info/favList?";

    /**
     * 战队详情
     */
    public static final String CORPS_DETAIL = "activity/teamDetail?";

    /**
     * // 加入战队     *
     */
    public static final String JOIN_CORPS = "activity/joinTeam";

    /**
     * // 加入战队申请列表 *
     */
    public static final String JOIN_TEAM_APPLY_LIST = "activity/joinTeamApplyList?";

    /**
     * // 清空申请列表 *
     */
    public static final String EMPTY_TEAM_APPLY_LIST = "activity/clearTeamApply?";

    /**
     * 提交认证
     */
    public static final String AMUSE_VERIFY_COMMIT = "amuse/verify/commit?";

    /**
     * 查看认证进度
     */
    public static final String AMUSE_VERIFY_CHECK = "amuse/verify/check?";

    /**
     * // 已报战队 *
     */
    public static final String APPLYED_CORPS = "activity/alreadyAppliedTeam?";

    /**
     * // 赛事地点 *
     */
    public static final String MATCH_PLACE = "activity/address?";

    /**
     * // 个人赛事报名 *
     */
    public static final String INDIVIDUAL_ACTIVITY_DETAIL = "activity/submitPersonalApply?";

    /**
     * // 战队赛事报名 *
     */
    public static final String CORPS_ACTIVITY_DETAIL = "activity/createTeam?";

    /**
     * // 提交申诉 *
     */
    public static final String AMUSE_APPEAL_COMMIT = "amuse/appeal/commit?";
    /**
     * // 获取区 *
     */
    //
    public static final String COMMON_AREA = "common/area/validchildren?";

    /**
     * 城市列表
     **/
    public static final String CHECK_CITY = "netbar/validateArea?";

    // 版本更新
    public static final String UPDATE_VERSION = "settings/version/client/android";

    /**
     * 奖品类型
     */
    public static final String AWARD_TYPE = "v2/amuse/award_type";
    /**
     * 启动页配置
     */
    public static final String PROCESSED_CONFIG = "processedConfig";


    /**
     * 验证红包
     */
    public static final String VERIFY_REDBAG = "my/redbag/sendSMS";

    /**
     * 完成支付任务
     */
    public static final String MALLTASK_PAYTASK = "malltask/payTask";

    /**
     * 搜索
     */
    public static final String SEARCH = "v2/search";

    /**
     * 审核进度v2
     */
    public static final String AMUSE_VERIFY_PROGRESS = "amuse/verify/progress";

    /**
     * 提交申述v2
     */
    public static final String AMUSE_APPEAL_V2_COMMIT = "amuse/appeal/v2/commit";

    /**
     * 申诉进度v2
     */
    public static final String AMUSE_APPEAL_PROGRESS = "amuse/appeal/progress";

    /**
     * 收藏的赛事
     */
    public static final String MY_ACTIVITY_MYFAVOR = "my/activityFavor";

    /**
     * 已报战队V2
     */
    public static final String ACTIVITY_APPLIEDTEAMS_V2 = "v2/activity/appliedTeams";

    /**
     * 查看报名场次与日期
     */
    public static final String ACTVITY_APPLY_DATES_NETBARS_V2 = "v2/activity/applyDatesAndNetbars?";
    /**
     * 查看我的参赛卡
     */
    public static final String ACTIVITY_CARD = "v2/activity/card/mine?";
    /**
     * 添加,编辑参赛卡
     */
    public static final String ACTIVITY_CARD_SAVE = "v2/activity/card/save?";
    /**
     * 查看我的参赛卡
     */
    public static final String ACTIVITY_MY_CARD = "v2/activity/card/mine";
    /**
     * 创建战队
     */
    public static final String CREATE_TEAM_V2 = "v2/activity/createTeam";
    /**
     * 加入战队
     */
    public static final String JOIN_TEAM_V2 = "v2/activity/joinTeam";
    /**
     * 个人报名
     */
    public static final String APPLY_V2 = "v2/activity/apply";

    /**
     * 网吧详情V2
     */
    public static final String NETBARINFO_V2 = "v2/netbar/baseInfo";

    /**
     * 网吧详情-竞技活动
     */
    public static final String NETBARINFO_ACTIVITY = "v2/netbar/activities";

    /**
     * 服务详情页
     */
    public static final String NETBAR_SERVICE_DETAIL = "netbar/resource/serviceDetail?";

    /**
     * 网吧专属红包
     */
    //  public static final String EXCLUSIVE_REDBAG = "v2/netbar/sendExclusiveRedbag?";

    public static final String EXCLUSIVE_REDBAG = "/v2/netbar/sendCard?";
    /**
     * 使用卡券
     */
    public static final String USE_CARD = "v2/my/useCardCoupon";

    /**
     * 可用卡券
     */
    public static final String MY_CARD = "v2/my/cardCoupon";

    /**
     * 资讯目录
     */
    public static final String INFO_CATALOG = "v2/activity/info/category";

    /**
     * 图文资讯列表页
     */
    public static final String INFORMATION_LIST = "v2/activity/info/informationList";
    /**
     * 资讯详情
     */
    public static final String INFORMATION_DETAIL = "v2/activity/info/infoDetail?";
    /**
     * 资讯顶或踩
     */
    public static final String INFORMATION_UPORDOWN = "v2/activity/info/upOrDown";
    /**
     * 生成娱口令
     */
    public static final String CORPS_ENTERTAIN_TOKEN = "v2/activity/entertainToken?";

    /**
     * 战队场次二维码或战队二维码
     */
    public static final String CORPS_SESSION_QRCODE = "v2/activity/qrcode?";


    /**
     * 商品列表 兑换 众筹夺宝
     */
    public static final String COMMODITY_LIST = "/v2/mall/commodity/list";

    /**
     * 众筹商品详情
     */
    public static final String SHOP_DETAIL = "/v2/mall/commodity/robTreasureDetail";


    /**
     * 兑奖记录
     */
    public static final String EXCHANGE_RECORD = "v2/mall/exchangeRecord";

    /**
     * 兑奖详情
     */
    public static final String EXCHANGE_DETAIL_V2 = "v2/mall/exchangeDetail";

    /**
     * 填写兑奖信息
     */
    public static final String EDIT_MALL_USERINFO = "v2/mall/editMallUserInfo";
    /**
     * 众筹商品支付
     */
    public static final String SHOOP_DETAIL_PAY = "v2/mall/commodity/buyRobTreasure";

    /**
     * 可用红包卡券数量
     */
    public static final String LEFT_TOTAL = "pay/leftTotal";

    /**
     * 识别娱口令
     */
    public static final String DECODE_ENTERTAIN_TOKEN = "v2/activity/decodeEntertainToken";


    /**
     * 幸运转盘
     */
    public static final String LUCKY_WHEEL = "v2/mall/wheel";

    /**
     * 自发赛详情
     */
    public static final String EVENT_DETAIL = "v2/event/eventDetail?";


    /**
     * 自发赛
     */
    public static final String ZIFAMATCH = "v2/event/roundList";

    /**
     * 咨询列表
     */
    public static final String EVENT_INFO_LIST = "v2/event/eventInfoList";
    /**
     * 签到请求
     */
    public static final String EVENT_SIGN_REQ = "v2/event/sign";
    /**
     * 个人报名
     */
    public static final String EVENT_APPLY = "v2/event/eventApply";

    /**
     * 首页赛事
     */
    public static final String MATCH_LIST = "v4/match/matchList";
    /**
     * 直播大厅
     */
    public static final String LIVE_VIDEO_LIST = "v4/live/liveHall";
    /**
     * 游戏分类列表
     */
    public static final String LIVE_GAME_LIST = "/v4/live/gameType";

    /**
     * 全部赛事筛选条件
     */
    public static final String ALL_MATCH_ITEM = "v4/match/matchItem";

    /**
     * 我的悬赏令
     */
    public static final String MY_BOUNTY = "v4/bounty/mybounty";

    /**
     * 直播游戏类型
     */
    public static final String GAMETYPE = "v4/live/gameType";
    /**
     * 直播列表
     */
    public static final String LIVE_LIST = "v4/live/liveList";
    /**
     * 视频列表
     */
    public static final String VIDEO_LIST = "v4/live/liveVideoList";
    /**
     * 直播间详情
     */
    public static final String LIVE_ROOM_DETAIL = "v4/live/liveDetail";

    /**
     * 直播通知
     */
    public static final String LIVE_NOTIFY = "v4/live/liveNotify";

    /**
     * 请求直播
     */
    public static final String LIVE_REQUEST = "v4/live/startLive";

    /**
     * 视屏详情
     */
    public static final String VIDEO_DETAIL = "v4/live/liveVideoInfo";

    /**
     * 离开直播间
     */
    public static final String LEAVE_ROOM = "v4/live/leaveLive";
    /**
     * 订阅
     */
    public static final String LIVE_SUBSCRIBE = "v4/live/subscribe";
    /**
     * 视频播放次数接口
     */
    public static final String PLAY_TIMES_REQUEST = "v4/live/countVideo";

    /**
     * 首页发现接口
     */
    public static final String DISCOVERY_DETAIL = "v4/discover";

    /**
     * 直播是否在线接口
     */
    public static final String LIVE_STATE = "v4/live/liveState";

    /**
     * 场次详情
     */
    public static final String ROUND_INFO = "v4/match/roundInfo";

    /**
     * 直播校验
     */
    public static final String LIVE_CHECK = "v4/live/androidModelCheck";

    /**
     * 我的赛事
     */
    public static final String MY_MATCH = "v4/my/match";
}
