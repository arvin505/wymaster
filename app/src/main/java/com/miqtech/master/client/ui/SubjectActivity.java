package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.SubjectListAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.appmanager.AppManager;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.AtlasDetail;
import com.miqtech.master.client.entity.Subject;
import com.miqtech.master.client.entity.SubjectDetail;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.ShareToFriendsUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.utils.WebInterface;
import com.miqtech.master.client.view.ExpertMorePopupWindow;
import com.miqtech.master.client.view.RefreshLayout;
import com.miqtech.master.client.watcher.Observerable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhaosentao on 2015/12/2.
 */
public class SubjectActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    @Bind(R.id.subjectPhoto)
    ImageView titleImage;
    @Bind(R.id.tvSubjectDetail)
    TextView subjectDetail;
    @Bind(R.id.subWebview)
    WebView webView;
    @Bind(R.id.subject_View)
    RefreshLayout refresh_view;
    @Bind(R.id.subjectinfo_List)
    ListView lvInformation;
    @Bind(R.id.subRel)
    RelativeLayout rl;
    @Bind(R.id.viewSpecial)
    View specialView;
    @Bind(R.id.parent)
    View parentView;

    @Bind(R.id.praise_ll_sub)
    LinearLayout praise_ll;
    @Bind(R.id.collect_ll_sub)
    LinearLayout collect_ll;
    @Bind(R.id.praise_iv_sub)
    ImageView praise_iv;
    @Bind(R.id.praise_number_tv_sub)
    TextView praise_tv;
    @Bind(R.id.collect_iv_sub)
    ImageView collect_iv;
    @Bind(R.id.collect_number_tv_sub)
    TextView collect_tv;
    @Bind(R.id.info_praise_collect_ll)
    LinearLayout show_pra_coll;

    private int page = 1;
    private String id;
    private List<Subject> subjects = new ArrayList<Subject>();
    private SubjectDetail detail;
    private int isLast;
    private SubjectListAdapter adapter;
    private Context context;
    private int REFRESH_TYPE;

    public int type;
    private int OnClickType = 0;
    private String mUrl;
    private User user;
    private String round;
    private String userId = "";
    private String token = "";
    private String url = "";
    private ImageView ivShare;
    private String titletName = "";
    private int coins_ad_type;
    private String coins_ad_url = "";

    //分享
    private ShareToFriendsUtil shareToFriendsUtil;
    //    private SweetSheet mSweetSheet, mSweetSheet2;
    private ExpertMorePopupWindow popwin;

    private final static int REFRESH = 1;
    private final static int ON_LOAD = 2;
    private String matchTitle;
    private String matchIcon;
    private String matchBrief = "";
    private boolean toLogin = false;
    private String invate_url;// 邀请链接
    private String invateTitle = "邀请你使用网娱大师，新用户礼包等你来～";
    private String invateDetail = "加入网娱大师，立即领取新用户奖励，众多福利等你拿～";
    private String totalCoins;
    private int praiseInt;//0取消点赞成功 1点赞成功 -1 操作失败
    private int collectInt;//1表示收藏成功 0表示取消收藏成功 -1表示操作失败
    private AtlasDetail atlasDetail = new AtlasDetail();
    private Observerable watcher = Observerable.getInstance();

    private int budgetOrExchangeType = -1;//从大转盘跳到我的金币页面，1表示显示收支记录，2表示显示兑换记录
    private static final int REQUESTCODE = 100;

    /**
     * MATH：赛事详情 SPECIAL：专题详情 APPLY:报名详情 YUEZHAN：约战详情 RELEASE:发布约战 REDBAG:红包
     * CUSTOMER 客服 ABOUT关于我们 ,SIGNIN 商城签到，EXPLAIN 商城说明,RENWU 任务详情，GOODS_DETAIL
     * 金币商城商品详情; INVITEFRIEND 邀请好友 ， COINS_AD 金币商城 广告;CHOU_JIANG 金币商城抽奖
     * 推广； 福利;AGREEMENT 网娱大师客户端协议 规则详情;CARDEXPLAIN参赛卡说明 服务详情页;LUCKY_DRAW抽奖,SIGNIN_LUCKYDRAW签到抽奖,OPEN_OUTSIDE_URL
     **/
    public final static int MATH = 0, SPECIAL = 1, APPLY = 2, YUEZHAN = 3, RELEASE = 4, NETBAR = 5, REDBAG = 6,
            CUSTOMER = 8, ABOUT = 7, HOW2USEREDBAG = 9, DOWNLOADGAME = 10, MATH_INFO = 11, AD = 12, SIGNIN = 13,
            EXPLAIN = 14, RENWU = 15, GOODS_DETAIL = 16, INVITEFRIEND = 17, COINS_AD = 18, CHOU_JIANG = 19, V_S = 20,
            TUIGUANG = 21, WELFARE = 22, AGREEMENT = 23, RECREATION_DETAILS = 24, START_AD = 25, RULE_DETAIL = 26, CARDEXPLAIN = 27,
            NETBAR_SERVICE = 28, LUCKY_DRAW = 29, SIGNIN_LUCKYDRAW = 30, OPEN_OUTSIDE_URL = 31;


    public final static String HTML5_TYPE = "html5_type";

    String shareMathTitle = "";
    String shareMathContent = "";
    String shareMathUrl = "";
    String shareImgMathUrl = "";
    String netbarId;
    String outSideUrl = "";

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.warinfo_list);
        context = this;
        ButterKnife.bind(this);
        id = getIntent().getStringExtra("id");
        netbarId = getIntent().getStringExtra("netbarId");
        round = getIntent().getStringExtra("round");
        type = getIntent().getIntExtra(HTML5_TYPE, 0);
        user = WangYuApplication.getUser(context);
        url = getIntent().getStringExtra("download_url");
        matchTitle = getIntent().getStringExtra("title");
        matchIcon = getIntent().getStringExtra("icon");
        matchBrief = getIntent().getStringExtra("matchBrief");
        titletName = getIntent().getStringExtra("titletName");
        coins_ad_type = getIntent().getIntExtra("coins_ad_type", 0);
        coins_ad_url = getIntent().getStringExtra("coins_ad_url");
        outSideUrl = getIntent().getStringExtra("outSideUrl");
        if (user != null) {
            userId = user.getId();
            token = user.getToken();
        }
        initView();
        if (type == MATH) {
            shareMathUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.OVER_URL + id;
            shareMathTitle = "网娱大师-电竞赛事独家报名,资讯直播" + matchTitle;
            shareImgMathUrl = HttpConstant.SERVICE_UPLOAD_AREA + matchIcon;
            shareMathContent = matchBrief;
            if (shareMathContent == null || TextUtils.isEmpty(shareMathContent)) {
                shareMathContent = "";
            }
        } else {
        }
        popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
        popwin.setOnItemClick(itemOnClick);
        shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
    }

    @Override
    protected void initView() {
        super.initView();
        adapter = new SubjectListAdapter(context, subjects);
        lvInformation.setAdapter(adapter);
        lvInformation.setOnItemClickListener(this);
        setLeftBtnImage(R.drawable.back);
        if (type == SIGNIN_LUCKYDRAW) {
            setLeftBtnImage(R.drawable.back_white_img);
            getLeftBtn().setBackgroundColor(getResources().getColor(R.color.transparent));
            TextView text_title = (TextView) findViewById(R.id.tvLeftTitle);
            text_title.setVisibility(View.VISIBLE);
            text_title.setText("欢乐大转盘");
            text_title.setTextColor(getResources().getColor(R.color.white));
            setRightBtnImage(R.drawable.goldcoin_question);
            getRightBtn().setOnClickListener(this);
            getRightBtn().setBackgroundColor(getResources().getColor(R.color.transparent));
            findViewById(R.id.rl_toolbar).setBackgroundColor(getResources().getColor(R.color.red_earn_gold_coin));
            getButtomLineView().setVisibility(View.GONE);
        }
        getLeftBtn().setOnClickListener(this);
        loadHtml();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //    webView.resumeTimers();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //   webView.pauseTimers();
    }

    private void loadHtml() {
        switch (type) {
            case MATH:
                infoShowNavigationBar();
                if (user != null) {
                    userId = user.getId();
                    token = user.getToken();
                }
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.V_TWO_SUBJECT_INFO + id + "&userId=" + userId + "&token=" + token;
                setLeftIncludeTitle("资讯详情");
                loadWebView(mUrl);
                break;
            case SPECIAL:
                setRightBtnImage(R.drawable.icon_share_oranger);
                getRightBtn().setOnClickListener(this);
                webView.setVisibility(View.GONE);
                specialView.setVisibility(View.VISIBLE);
                setLeftIncludeTitle("专题资讯");
                loadSbjuect();
                break;
            case APPLY:
                // *2、赛事报名:/activity/web/apply?id={id}&userId={userId}&token={token}&round={round}
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.APPLY_INFO + id + "&userId=" + userId + "&token="
                        + token + "&round=" + round;
                setLeftIncludeTitle("报名详情");
                loadWebView(mUrl);
                break;
            case YUEZHAN:
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.YUEZHAN_INFO + id + "&userId=" + userId + "&token="
                        + token;
                setLeftIncludeTitle("约战详情");
                loadWebView(mUrl);
                break;
            case RELEASE:
                if (id == null) {
                    id = "";
                }
                OnClickType = 0;
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.RELEASE_INFO + "&userId=" + userId + "&token="
                        + token + "&netbarId=" + id;
                webView.setScrollContainer(false);
                setLeftIncludeTitle("发布约战");
                loadWebView(mUrl);
                break;
            case NETBAR:
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.NETBAR_INFO + id;
                // mUrl = "http://www.wangyuhudong.com/";
                setLeftIncludeTitle("详细信息");
                loadWebView(mUrl);
                break;
            case CUSTOMER:
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.CUSTOMER_SERVICE;
                setLeftIncludeTitle("客服");
                setRightTextView("反馈");
                getRightBtn().setOnClickListener(this);
                loadWebView(mUrl);
                break;
            case ABOUT:
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ABOUT_OUR;
                setLeftIncludeTitle("关于我们");
                loadWebView(mUrl);
                break;
            case REDBAG:
                PreferencesUtil.saveWeekRedbagPush(context, false);
                user = WangYuApplication.getUser(context);
                if (user != null) {
                    userId = user.getId();
                    token = user.getToken();
                }
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.RECEIVE_ENVELOPE + userId + "&token=" + token;
                setLeftIncludeTitle("每周领红包");
                loadWebView(mUrl);
                break;
            case HOW2USEREDBAG:
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.HELP_OPEN;
                setLeftIncludeTitle("使用规则");
                loadWebView(mUrl);
                break;
            case DOWNLOADGAME:
                setLeftIncludeTitle("游戏下载");
                loadWebView(url);
                break;
            case MATH_INFO:
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SPECIAL_INFO + id;
                setLeftIncludeTitle("赛事详情");
                loadWebView(mUrl);
                break;
            case AD:
                String ad_url = getIntent().getStringExtra("ad_url");
                loadWebView(ad_url);
                setLeftIncludeTitle("");
                webView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        // TODO Auto-generated method stub
                        super.onReceivedTitle(view, title);
                        setLeftIncludeTitle(title);
                    }
                });
                break;
            case SIGNIN:
                setLeftIncludeTitle("天天签到");
                user = WangYuApplication.getUser(context);
                if (user != null) {
                    userId = user.getId();
                    token = user.getToken();
                }
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SIGN_IN + userId + "&token=" + token;
                loadWebView(mUrl);
                lengthCoding = UMengStatisticsUtil.CODE_3004;
                break;
            case EXPLAIN:
                setLeftIncludeTitle("金币商城说明");
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.STORE_EXPLAIN;
                loadWebView(mUrl);
                break;
            case RENWU:// 任务详情
                setLeftIncludeTitle("任务详情");
                user = WangYuApplication.getUser(context);
                if (user != null) {
                    userId = user.getId();
                    token = user.getToken();
                }
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.RENWU_DETAIL + id + "&userId=" + userId + "&token="
                        + token;
                loadWebView(mUrl);
                break;
            case GOODS_DETAIL:// 商品详情
                if (!TextUtils.isEmpty(titletName)) {
                    setLeftIncludeTitle(titletName);
                } else {
//                    webView.setWebChromeClient(new WebChromeClient() {
//                        @Override
//                        public void onReceivedTitle(WebView view, String title) {
//                            // TODO Auto-generated method stub
//                            super.onReceivedTitle(view, title);
//                            setLeftIncludeTitle(title);
//                        }
//                    });
                    setLeftIncludeTitle("商品详情");
                }
                user = WangYuApplication.getUser(context);
                if (user != null) {
                    userId = user.getId();
                    token = user.getToken();
                }
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.GOODS_DETAILS + userId + "&token=" + token
                        + "&commodityId=" + id;
                loadWebView(mUrl);
                lengthCoding = UMengStatisticsUtil.CODE_3002;
                break;
            case INVITEFRIEND:// 邀请好友
                setLeftIncludeTitle("邀请好友赚积分");
                user = WangYuApplication.getUser(context);
                if (user != null) {
                    userId = user.getId();
                    token = user.getToken();
                }
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INVITE_FRIEND + userId + "&token=" + token;
                loadWebView(mUrl);
                break;
            case COINS_AD:// 金币商城 广告
                LogUtil.d("MyViewClient", "游戏重新刷取页面");
                user = WangYuApplication.getUser(context);
                setLeftIncludeTitle("");
                if (user != null) {
                    userId = user.getId();
                    token = user.getToken();
                }
                // 当type为1-3时，需要拼接地址（其中type=2时，userId和token为必须）；当type=4，直接使用url当跳转地址
                if (2 == coins_ad_type || 1 == coins_ad_type || 3 == coins_ad_type) {
                    mUrl = HttpConstant.SERVICE_HTTP_AREA + coins_ad_url + "&userId=" + userId + "&token=" + token;
                } else if (4 == coins_ad_type) {
                    mUrl = coins_ad_url;
                }
                loadWebView(mUrl);

                webView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        // TODO Auto-generated method stub
                        super.onReceivedTitle(view, title);
                        setLeftIncludeTitle(title);
                    }
                });
                break;
            case V_S:// 首页 报名娱乐赛 海量。。。
                user = WangYuApplication.getUser(context);
                if (user != null) {
                    userId = user.getId();
                    token = user.getToken();
                }
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.V_S + userId + "&token=" + token + "&longitude="
                        + Constant.longitude + "&latitude=" + Constant.latitude + "&areaCode=" + Constant.currentCity;
                loadWebView(mUrl);
                webView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        // TODO Auto-generated method stub
                        super.onReceivedTitle(view, title);
                        setLeftIncludeTitle(title);
                    }
                });
                break;
            case TUIGUANG:  // 推广
                setLeftIncludeTitle(matchTitle);
                loadWebView(coins_ad_url);
                break;
            case WELFARE:  // 福利
                setLeftIncludeTitle(matchTitle);
                loadWebView(coins_ad_url);
                break;
            case AGREEMENT://网娱大师客户端协议
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AGREEMENT;
                loadWebView(mUrl);
                webView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        // TODO Auto-generated method stub
                        super.onReceivedTitle(view, title);
                        setLeftIncludeTitle(title);
                    }
                });
                break;
            case RECREATION_DETAILS:
                setLeftIncludeTitle("娱乐赛详情");
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_WEB_RULE + "amuseId=" + id;
                loadWebView(mUrl);
                break;
            case START_AD:
                String url = getIntent().getStringExtra("url");
                mUrl = url;
                loadWebView(mUrl);
                webView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        // TODO Auto-generated method stub
                        super.onReceivedTitle(view, title);
                        setLeftIncludeTitle(title);
                    }
                });
                break;
            case RULE_DETAIL:
                setLeftIncludeTitle("规则详情");
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_WEB_RULE + "amuseId=" + id;
                loadWebView(mUrl);
                break;
            case CARDEXPLAIN:
                setLeftIncludeTitle("参赛卡说明");
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.CARD_EXPLAIN;
                loadWebView(mUrl);
                break;
            case NETBAR_SERVICE:  //网吧购买服务详情页
                setLeftIncludeTitle("活动详情");
                mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.NETBAR_SERVICE_DETAIL;
//                String id = getIntent().getStringExtra("id");
                mUrl += "id=" + id + "&netbarId=" + netbarId;
                LogUtil.e(TAG, "murl : " + mUrl);
                loadWebView(mUrl);
                break;
            case LUCKY_DRAW:  //抽奖
                lengthCoding = UMengStatisticsUtil.CODE_3003;
                setLeftIncludeTitle("支付每日抽奖");
                netbarId = getIntent().getStringExtra("netbarId");
                String orderId = getIntent().getStringExtra("orderId");
                user = WangYuApplication.getUser(context);
                if (user != null) {
                    userId = user.getId();
                    token = user.getToken();
                    mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.LOTTERY_DRAW + "netbarId=" + netbarId + "&orderId=" + orderId +
                            "&userId=" + userId + "&token=" + token;
                    loadWebView(mUrl);
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case SIGNIN_LUCKYDRAW:  //抽奖
                //  setLeftIncludeTitle("欢乐大转盘");
                user = WangYuApplication.getUser(context);
                if (user != null) {
                    userId = user.getId();
                    token = user.getToken();
                    mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MALL_WHEEL + "userId=" + userId + "&token=" + token;
                    loadWebView(mUrl);
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case OPEN_OUTSIDE_URL://打开外部链接
                loadWebView(outSideUrl);
                webView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        // TODO Auto-generated method stub
                        super.onReceivedTitle(view, title);
                        setLeftIncludeTitle(title);
                    }
                });
        }
    }

    private void loadWebView(String mUrl) {
        WebSettings setting = webView.getSettings();
        WebInterface jsInterface = new WebInterface(this, webView);
        webView.addJavascriptInterface(jsInterface, "browser");
        setting.setJavaScriptEnabled(true);
        //当进入游戏页面的时候设置没有缓存（游戏页面过大导致加载不到js页面）
        if (mUrl.contains("game/gameList?")) {
            setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        try {
            setting.setUserAgentString(setting.getUserAgentString() + " WYMasterBrowser/" + getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        LogUtil.e(TAG, "agent == " + setting.getUserAgentString());
        if (type == DOWNLOADGAME) {
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
            });
        } else {
            webView.setWebViewClient(new MyViewClient());
        }
        webView.setDownloadListener(new MyWebViewDownLoadListener());
        webView.setWebChromeClient(wvcc);
        webView.loadUrl(mUrl); // 加载指定网址
        specialView.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    WebChromeClient wvcc = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (title.equals("-1")) {
                Intent intent = new Intent();
                intent.setClass(context, LoginActivity.class);
                startActivity(intent);
                finish();
                showToast("用户信息失效，请重新登陆");
            }
        }
    };

    private class MyWebViewDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }

    @Override
    protected void onDestroy() {
        if (shareToFriendsUtil != null) {
            if (shareToFriendsUtil.requestUtil != null) {
                shareToFriendsUtil.requestUtil.removeTag(shareToFriendsUtil.getClass().getName());
            }
            shareToFriendsUtil.requestUtil = null;
            shareToFriendsUtil = null;
        }
        watcher = null;
        super.onDestroy();
    }

    public class MyViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            LogUtil.d("MyViewClient", "url" + url);
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } else {
                if (!url.contains("wy_mall_totalcoins") && !url.contains("wy_mall_record") && !url.contains("exchangeHistory")
                        && !url.contains("wy_mall_nologin") && !url.contains("loginstatuserror") && !url.contains("wy_mall_exchangepage")
                        && !url.contains("wy_athletic_hall")
                        && !url.contains("wy_mall_tasks")) {
                    view.loadUrl(url);
                }
            }
            if (url.contains("loginstatuserror")) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivityForResult(intent, REQUESTCODE);
                showToast("请登录");
            }
            if (url.contains("myredbag")) {
                Intent intent = new Intent(context, MyRedBagActivity.class);
                startActivity(intent);
                view.loadUrl(mUrl);
            }
            if (url.contains("wy_mall_tasks")) {
                Intent intent = new Intent(context, CoinsTaskActivity.class);
                startActivity(intent);
            } else if (url.contains("toInvite")) {
                if (popwin != null) {
                    popwin.hideWeiBoAndQQ();
                    popwin.show();
                } else {
                    popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
                    popwin.setOnItemClick(itemOnClick);
                    shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
                    popwin.hideWeiBoAndQQ();
                    popwin.show();
                }

                view.stopLoading();
                invate_url = url;
            } else if (url.contains("exchangeHistory")) {
                getCoinsNum();
                view.stopLoading();
            } else if (url.contains("wy_mall_exchangepage")) {//跳转填写资料界面
                LogUtil.e(TAG, "obj == " + url);
                Map<String, String> map = Utils.getPrizeParams(url);
                Intent intent = new Intent(context, AddressActivity.class);
                intent.putExtra("goodtype", map.get("prizetype"));//兑换类型
                intent.putExtra("goodid", map.get("historyid") + "");//兑换ID
                startActivityForResult(intent, REQUESTCODE);
            } else if (url.contains("wy_mall_totalcoins")) {//跳转我的金币的收支记录
                budgetOrExchangeType = 1;
                getCoinsNum();
            } else if (url.contains("wy_mall_record")) {//跳转我的金币的兑换记录
                budgetOrExchangeType = 2;
                getCoinsNum();
            } else if (url.contains("wy_mall_nologin")) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivityForResult(intent, REQUESTCODE);
            } else if (url.contains("wy_athletic_hall")) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(MainActivity.ATHLETICS, 1);
                startActivity(intent);
            }
            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            LogUtil.d("MyViewClient", "onReceivedError::" + errorCode + "::" + description + "::" + failingUrl);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        if (subjects.size() > 0) {
            //adapter.notifyDataSetChanged();
        }
        if (detail != null) {
            subjectDetail.setText(detail.getTitle());
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + detail.getCover(), titleImage);
        }
        //欢乐大转盘登录失效时候，再次登录成功后需要重新加载html页面
        if (type == SIGNIN_LUCKYDRAW || (type == COINS_AD && coins_ad_type == 3)) {
            loadHtml();
        }
    }

    private void loadSbjuect() {
//            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
//            params.add(new BasicNameValuePair("id", id + ""));
//            httpConnector.callByPost(HttpPortName.SUBJECT_LIST, params);
//            mUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SUBJECT_LIST + "id=" + id;
    }

    /**
     * 获取当前金币总量
     */
    private void getCoinsNum() {
        if (WangYuApplication.getUser(context) != null) {
            user = WangYuApplication.getUser(context);
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.GET_COINS_NUMS, map, HttpConstant.GET_COINS_NUMS);
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.praise_ll_sub:
                praise();
                break;
            case R.id.collect_ll_sub:
                info_fav();
                break;
            case R.id.ibRight:
                if (type == MATH) {
                    if (popwin != null) {
                        popwin.show();
                    } else {
                        popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
                        popwin.setOnItemClick(itemOnClick);
                        shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
                        popwin.show();
                    }
                } else if (type == SIGNIN_LUCKYDRAW) {
                    Intent intent = new Intent(context, CoinsTaskActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        Object obj = null;
        try {
            if (object.has("object")) {
                obj = object.getString("object");
            } else {
                obj = object.toString();
            }
            if (method.equals(HttpConstant.GET_COINS_NUMS)) {// 顶部显示金币的总量
                JSONObject jsonobject = new JSONObject(obj.toString());
                totalCoins = jsonobject.optString("coin");

                Intent intent = new Intent(context, MyGoldCoinsActivity.class);
                intent.putExtra("totalCoins", totalCoins);
                if (budgetOrExchangeType == -1) {
                    intent.putExtra("BudgetOrExchange", 2);
                } else {
                    intent.putExtra("BudgetOrExchange", budgetOrExchangeType);
                }
                startActivity(intent);

                if (budgetOrExchangeType == -1) {
                    finish();
                } else {
                    budgetOrExchangeType = -1;
                }
            }

            if (method.equals(HttpConstant.INFO_PRAISE)) {
                if ("0".equals(object.getString("code")) && object.has("object")) {
                    praiseInt = object.optInt("object");
                    if (praiseInt == 1) {//0取消点赞成功 1点赞成功 -1 操作失败
                        praise_iv.setImageResource(R.drawable.praise_yes);
                        if (atlasDetail.getPraised() == 1) {
                            praise_tv.setText(Utils.getnumberForms(atlasDetail.getPraiseNum(), context));
                        } else {
                            praise_tv.setText(Utils.getnumberForms(atlasDetail.getPraiseNum() + 1, context));
                        }
                    } else if (praiseInt == 0) {
                        if (atlasDetail.getPraised() == 1) {
                            praise_tv.setText(Utils.getnumberForms(atlasDetail.getPraiseNum() - 1, context));
                        } else {
                            praise_tv.setText(Utils.getnumberForms(atlasDetail.getPraiseNum(), context));
                        }
                        praise_iv.setImageResource(R.drawable.praise_no_oranger);
                    } else if (praiseInt == -1) {
                        showToast(getResources().getString(R.string.operationFailure));
                    }
                }
            } else if (method.equals(HttpConstant.INFO_FAV)) {
                if ("0".equals(object.getString("code")) && object.has("object")) {
                    collectInt = object.optInt("object");//1表示收藏成功 0表示取消收藏成功 -1表示操作失败
                    if (collectInt == 1) {
                        collect_iv.setImageResource(R.drawable.collect_yes);
                        watcher.notifyChange(Observerable.ObserverableType.COLLECTSTATE, 3, atlasDetail.getId(), true);
                        if (atlasDetail.getFaved() == 1) {
                            collect_tv.setText(Utils.getnumberForms(atlasDetail.getFavNum(), context));
                        } else {
                            collect_tv.setText(Utils.getnumberForms(atlasDetail.getFavNum() + 1, context));
                        }
                    } else if (collectInt == 0) {
                        if (atlasDetail.getFaved() == 1) {
                            collect_tv.setText(Utils.getnumberForms(atlasDetail.getFavNum() - 1, context));
                        } else {
                            collect_tv.setText(Utils.getnumberForms(atlasDetail.getFavNum(), context));
                        }
                        watcher.notifyChange(Observerable.ObserverableType.COLLECTSTATE, 3, atlasDetail.getId(), false);
                        collect_iv.setImageResource(R.drawable.collect_no_oranger);
                    } else if (collectInt == -1) {
                        showToast(getResources().getString(R.string.operationFailure));
                    }
                }
            } else if (method.equals(HttpConstant.INFO_DETAIL)) {
                if ("0".equals(object.getString("code")) && object.has("object")) {
                    atlasDetail = GsonUtil.getBean(object.getString("object").toString(), AtlasDetail.class);

//                    praise_tv.setText(atlasDetail.getPraiseNum() + "");//显示赞数
                    praise_tv.setText(Utils.getnumberForms(atlasDetail.getPraiseNum(), context));//显示赞数
//                    collect_tv.setText(atlasDetail.getFavNum() + "");//显示收藏数
                    collect_tv.setText(Utils.getnumberForms(atlasDetail.getFavNum(), context));//显示收藏数
                    if (atlasDetail.getFaved() == 1) {
                        collect_iv.setImageResource(R.drawable.collect_yes);
                    }
                    if (atlasDetail.getPraised() == 1) {
                        praise_iv.setImageResource(R.drawable.praise_yes);
                    }

                    matchBrief = atlasDetail.getBrief();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        if (method.equals(HttpConstant.GET_COINS_NUMS)) {
            showToast(getResources().getString(R.string.noNeteork));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * 显示资讯的导航栏
     */
    private void infoShowNavigationBar() {
        setRightBtnImage(R.drawable.icon_share_oranger);
        show_pra_coll.setVisibility(View.VISIBLE);
        loadDetailData();
        collect_ll.setOnClickListener(this);
        praise_ll.setOnClickListener(this);
        getRightBtn().setOnClickListener(this);
    }

    /**
     * 获取详细数据
     */
    private void loadDetailData() {
        user = WangYuApplication.getUser(context);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
        }
        map.put("infoId", id + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INFO_DETAIL, map, HttpConstant.INFO_DETAIL);
    }

    /**
     * 收藏或者取消
     */
    private void info_fav() {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("infoId", id + "");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INFO_FAV, map, HttpConstant.INFO_FAV);
        } else {
            showToast(getResources().getString(R.string.pleaseLogin));
        }
    }

    /**
     * 点赞或者取消点赞
     */
    private void praise() {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("infoId", id + "");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INFO_PRAISE, map, HttpConstant.INFO_PRAISE);
        } else {
            showToast(getResources().getString(R.string.pleaseLogin));
        }
    }


    View.OnClickListener itemOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (type == INVITEFRIEND) {// 邀请好友
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.request);
                if (!(AppManager.getAppManager().findActivity(SubjectActivity.class))) {
                    AppManager.getAppManager().addActivity(SubjectActivity.this);
                }

                switch (v.getId()) {
                    case R.id.llWeChat:
                        shareToFriendsUtil.shareInvateByWXFriend(invateTitle, invateDetail, invate_url, bitmap, 0);
                        break;
                    case R.id.llFriend:
                        shareToFriendsUtil.shareInvateByWXFriend(invateTitle, invateDetail, invate_url, bitmap, 1);
                        break;
                }
            } else {
                switch (v.getId()) {
                    case R.id.llSina:
                        shareToFriendsUtil.shareBySina(shareMathTitle, shareMathContent, shareMathUrl, shareImgMathUrl);
                        break;
                    case R.id.llWeChat:
                        shareToFriendsUtil.shareWyByWXFriend(shareMathTitle, shareMathContent, shareMathUrl, shareImgMathUrl, 0);
                        break;
                    case R.id.llFriend:
                        shareToFriendsUtil.shareWyByWXFriend(shareMathTitle, shareMathContent, shareMathUrl, shareImgMathUrl, 1);
                        break;
                    case R.id.llQQ:
                        shareToFriendsUtil.shareByQQ(shareMathTitle, shareMathContent, shareMathUrl, shareImgMathUrl);
                        break;
                }
            }

        }
    };

}
