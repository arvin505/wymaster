package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.ExpiryAreaAdapter;
import com.miqtech.master.client.adapter.SnatchAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.CoinsStoreAd;
import com.miqtech.master.client.entity.CoinsStoreGoods;
import com.miqtech.master.client.entity.CoinsStoreGoodsList;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.BannerPagerView;
import com.miqtech.master.client.view.MyGridView;
import com.miqtech.master.client.view.MyListView;
import com.miqtech.master.client.view.PullableScrollView;
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
 * 金币商城
 * Created by Administrator on 2015/12/3.
 */
public class GoldCoinsStoreActivity extends BaseActivity implements View.OnClickListener, PullableScrollView.MyScrollListener,  Observerable.ISubscribe {
    private static final String TAG = "GoldCoinsStoreActivity";
    private ImageView ivExplain;// 金币商城说明
    private Context context;
    private List<CoinsStoreAd> listAd = new ArrayList<CoinsStoreAd>();// 顶部广告数据
    private User user;// 用户信息
    private CoinsStoreGoodsList storegoodsList;
    private ExpiryAreaAdapter expiryAreaAdapter;//adapter2是兑奖专区的
    private SnatchAdapter snatchAdapter;  //adapter1是众筹夺宝专区
    private int top;// 用来判断当屏幕移动时是否把titlebar置顶
    private int ww;// gridview中图片的宽度
    private String totalCoins;// 金币数量

    private List<CoinsStoreGoods> snatchs = new ArrayList<CoinsStoreGoods>();// 众筹夺宝
    private List<CoinsStoreGoods> expiryAreas = new ArrayList<CoinsStoreGoods>();// 兑奖专区
    private Observerable mWatcher;
    @Bind(R.id.llTitleBarFirst)
    LinearLayout titleBarFirst;// 金币总量，签到，兑换记录，金币任务
    @Bind(R.id.llCoinsTotal)
    LinearLayout totalCoinsFirst;// 金币总量点击
    @Bind(R.id.tvCoinsTotal)
    TextView tvCoinsFirst;// 显示金币总量
    @Bind(R.id.llEverydaySignin)
    LinearLayout signInFirst;// 签到点击
    @Bind(R.id.llDuihuanRecord)
    LinearLayout duihuanFirsr;// 兑换记录点击
    @Bind(R.id.llCoinsTask)
    LinearLayout taskFirst;// 金币任务
    @Bind(R.id.llTitleBarSecond)
    LinearLayout titleBarSecond;
    @Bind(R.id.llCoinsTotalSecond)
    LinearLayout totalCoinsSecond; // 金币总量点击
    @Bind(R.id.tvCoinsTotalSecond)
    TextView tvCoinsSecond; // 显示金币总量
    @Bind(R.id.llEverydaySigninSecond)
    LinearLayout signInSecond;// 签到点击
    @Bind(R.id.llDuihuanRecordSecond)
    LinearLayout duihuanSecond;// 兑换记录点击
    @Bind(R.id.llCoinsTaskSecond)
    LinearLayout taskSecond;// 金币任务
    @Bind(R.id.coinsScrollview)
    PullableScrollView coinsScrollView;
    @Bind(R.id.rlAdvertisement)
    RelativeLayout rlAd;// 广告中的所有布局
    @Bind(R.id.advertisementCoins)
    BannerPagerView ad;// 广告
    @Bind(R.id.llAdvertiseDots)
    LinearLayout adDots;// 广告点
    @Bind(R.id.lvSnatch)
    MyListView lvSnatch; //众筹夺宝listView
    @Bind(R.id.tvSnatchMore)
    TextView tvSnatchMore; //众筹夺宝更多按钮
    @Bind(R.id.gvExpiryArea)
    MyGridView gvExpiryArea; //兑奖专区gridView
    @Bind(R.id.tvExpiryAreaMore)
    TextView tvExpiryAreaMore; //兑奖专区更多按钮
    @Bind(R.id.llSign)
    LinearLayout llSign; //签到抽大奖
    @Bind(R.id.llGame)
    LinearLayout llGame; //游戏
    @Bind(R.id.llExpiryArea)
    LinearLayout llExpiryArea;
    @Bind(R.id.llSnatch)
    LinearLayout llSnatch;


    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        mWatcher = Observerable.getInstance();
        mWatcher.subscribe(Observerable.ObserverableType.CROWDLIST,this);
        setContentView(R.layout.activity_gold_coins_store);
        ButterKnife.bind(this);
        lengthCoding = UMengStatisticsUtil.CODE_3000;
        initView();
        setListener();
        initData();
    }

    private void setListener() {
        coinsScrollView.setOnMyScrollListener(this);

        totalCoinsFirst.setOnClickListener(this);
        signInFirst.setOnClickListener(this);
        duihuanFirsr.setOnClickListener(this);
        taskFirst.setOnClickListener(this);

        totalCoinsSecond.setOnClickListener(this);
        signInSecond.setOnClickListener(this);
        duihuanSecond.setOnClickListener(this);
        taskSecond.setOnClickListener(this);

        ivExplain.setOnClickListener(this);

        tvSnatchMore.setOnClickListener(this);
        tvExpiryAreaMore.setOnClickListener(this);

        llSign.setOnClickListener(this);
        llGame.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();
        lvSnatch.setSelector(new ColorDrawable(Color.TRANSPARENT));  //设置lv的item点击时候颜色为透明
        gvExpiryArea.setSelector(new ColorDrawable(Color.TRANSPARENT));

        setLeftIncludeTitle("金币商城");
        //初始化金币为0
        setFontDiffrentColor("0金币", 0, "0金币".length() - 2, tvCoinsFirst);
        setFontDiffrentColor("0金币", 0, "0金币".length() - 2, tvCoinsSecond);
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        ivExplain = (ImageView) findViewById(R.id.ibRight);
        ivExplain.setImageResource(R.drawable.coin_tip);
        ivExplain.setVisibility(View.VISIBLE);

        context = GoldCoinsStoreActivity.this;

        ad.requestFocus();// 让首页广告获得焦点并置顶

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        rlAd.measure(w, h);
        int menuHeight = rlAd.getMeasuredHeight();
        top = menuHeight;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        ww = (width - Utils.dp2px(36)) / 2;
        snatchAdapter = new SnatchAdapter(context, snatchs);
        lvSnatch.setAdapter(snatchAdapter);
        expiryAreaAdapter = new ExpiryAreaAdapter(context, expiryAreas, ww);
        gvExpiryArea.setAdapter(expiryAreaAdapter);
        coinsScrollView.setVisibility(View.GONE);

    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        getBanner();
        getData();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        showLoading();
        getCoinsNum();
        if (ad != null && listAd.size() > 0) {
            ad.startAutoCycle();
        }
    }


    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        Intent intent = null;
        int viewId = arg0.getId();
        //众筹夺宝 和兑奖专区更多 不需要登录判断
        if (viewId == R.id.tvSnatchMore) {
            Intent crowdList = new Intent(this, CrowdfundingListActivity.class);
            startActivity(crowdList);
        } else if (viewId == R.id.tvExpiryAreaMore) {
            intent = new Intent(this, ExpiryListActivity.class);
            startActivity(intent);
        }
        if (WangYuApplication.getUser(context) != null) {
            if (viewId == R.id.llCoinsTotal || viewId == R.id.llCoinsTotalSecond) {// 金币总的点击
                intent = new Intent();
                intent.setClass(context, MyGoldCoinsActivity.class);
                intent.putExtra("totalCoins", totalCoins);
                intent.putExtra("BudgetOrExchange", 1);
                startActivity(intent);
            } else if (viewId == R.id.llEverydaySignin || viewId == R.id.llEverydaySigninSecond) {// CDK兑换
                intent = new Intent(context, SubjectActivity.class);
                intent.putExtra("coins_ad_type", 3);
                intent.putExtra("coins_ad_url", "cdkey/web/exchange?");
                intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.COINS_AD);
                startActivity(intent);
            } else if (viewId == R.id.llDuihuanRecord || viewId == R.id.llDuihuanRecordSecond) {// 兑换记录
                intent = new Intent();
                intent.setClass(context, MyGoldCoinsActivity.class);
                intent.putExtra("totalCoins", totalCoins);
                intent.putExtra("BudgetOrExchange", 2);
                startActivity(intent);
            } else if (viewId == R.id.llCoinsTask || viewId == R.id.llCoinsTaskSecond) {// 金币任务
                intent = new Intent(context, CoinsTaskActivity.class);
                startActivity(intent);
            } else if (viewId == R.id.ibLeft) {// 返回
                onBackPressed();
            } else if (viewId == R.id.ibRight) {// 金币商城说明
                intent = new Intent();
                intent.setClass(context, SubjectActivity.class);
                intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.EXPLAIN);
                startActivity(intent);
            } else if (viewId == R.id.llSign) {
                //TODO 跳转到签到抽大奖页面
                intent = new Intent();
                intent.setClass(context, SubjectActivity.class);
                intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.SIGNIN_LUCKYDRAW);
                startActivity(intent);
            } else if (viewId == R.id.llGame) {
                intent = new Intent(context, SubjectActivity.class);
                intent.putExtra("coins_ad_type", 3);
                intent.putExtra("coins_ad_url", "game/gameList?");
                intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.COINS_AD);
                startActivity(intent);
            }
        } else {
            if (viewId == R.id.ibLeft || viewId == R.id.ibLeft) {
                switch (viewId) {
                    case R.id.ibLeft:
                        onBackPressed();
                        break;
                    case R.id.ibRight:
                        intent = new Intent();
                        intent.setClass(context, SubjectActivity.class);
                        intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.EXPLAIN);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            } else {
                if(viewId!=R.id.tvSnatchMore && viewId!=R.id.tvExpiryAreaMore) {
                    StartActivity(LoginActivity.class);
                }
            }
        }

    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        LogUtil.e(TAG, "数据" + object.toString());
        try {
            Gson gs = new Gson();
            Object obj = null;
            if (object.has("object")) {
                obj = object.getString("object");
            } else {
                return;
            }
            coinsScrollView.setVisibility(View.VISIBLE);
            String objStr = obj.toString();
            if (method.equals(HttpConstant.COINS_STORE_ADVERTISE_LIST)) {// 顶部广告
                listAd.clear();
                listAd = gs.fromJson(objStr, new TypeToken<List<CoinsStoreAd>>() {}.getType());
                addDateForbanner(listAd);
            } else if (method.equals(HttpConstant.GET_COINS_NUMS)) {// 顶部显示金币的总量
                JSONObject jsonobject = new JSONObject(objStr);
                totalCoins = jsonobject.optString("coin");
                setFontDiffrentColor(this.getResources().getString(R.string.goldCoinNum, totalCoins), 0, this.getResources().getString(R.string.goldCoinNum, totalCoins).length() - 2, tvCoinsFirst);
                setFontDiffrentColor(this.getResources().getString(R.string.goldCoinNum, totalCoins), 0, this.getResources().getString(R.string.goldCoinNum, totalCoins).length() - 2, tvCoinsSecond);

            } else if (method.equals(HttpConstant.COMMODITY_LIST)) {// 金币专区和抽奖专区的数据
                storegoodsList = gs.fromJson(objStr, CoinsStoreGoodsList.class);
                showSnatchData();
                showExpiryAreasData();
            }
        } catch (JsonSyntaxException e) {
            showToast("数据异常");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        try {
            if (object.has("result")) {
                showToast(object.getString("result"));
                if ("-4".equals(object.getString("code")) && method.equals(HttpConstant.GET_COINS_NUMS)) {
                    setFontDiffrentColor("0金币", 0, "0金币".length() - 2, tvCoinsFirst);
                    setFontDiffrentColor("0金币", 0, "0金币".length() - 2, tvCoinsSecond);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
    }

    /**
     * 显示众筹多播数据
     */
    private void showSnatchData() {
        if (storegoodsList == null || storegoodsList.getGrobTreasure() == null || "[]".equals(storegoodsList.getGrobTreasure())) {
            return;
        }
        snatchs.clear();
        llSnatch.setVisibility(View.VISIBLE);
        if (storegoodsList.getGrobTreasure().size() > 3) {
            snatchs.addAll(storegoodsList.getGrobTreasure().subList(0, 3));
        } else {
            snatchs.addAll(storegoodsList.getGrobTreasure());
        }
        snatchAdapter.notifyDataSetChanged();

    }

    /**
     * 显示兑奖专区数据
     */
    private void showExpiryAreasData() {
        if (storegoodsList == null || storegoodsList.getPrizeArea() == null || "[]".equals(storegoodsList.getPrizeArea())) {
            return;
        }
        expiryAreas.clear();
        llExpiryArea.setVisibility(View.VISIBLE);
        if (storegoodsList.getPrizeArea() != null && storegoodsList.getPrizeArea().size() > 3) {
            expiryAreas.addAll(storegoodsList.getPrizeArea().subList(0, 4));
        } else {
            expiryAreas.addAll(storegoodsList.getPrizeArea());
        }
        expiryAreaAdapter.notifyDataSetChanged();
    }

    /**
     * 获取众筹夺宝和兑奖专区的数据
     */
    private void getData() {
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.COMMODITY_LIST, null, HttpConstant.COMMODITY_LIST);
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
            setFontDiffrentColor("0金币", 0, "0金币".length() - 2, tvCoinsFirst);
            setFontDiffrentColor("0金币", 0, "0金币".length() - 2, tvCoinsSecond);
            hideLoading();
        }
    }

    /**
     * @param content 显示的内容
     * @param start   开始角标
     * @param end     结束角标
     * @param tv      显示的控件
     *                还可以添加显示不同颜色的参数
     */
    private void setFontDiffrentColor(String content, int start, int end, TextView tv) {
        if (tv == null) {
            return;
        }
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.orange)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(style);
    }

    /**
     * 获取广告数据
     */
    private void getBanner() {
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.COINS_STORE_ADVERTISE_LIST, null, HttpConstant.COINS_STORE_ADVERTISE_LIST);
    }

    /**
     * 添加广告数据并开始广告轮播
     *
     * @param listAd 广告数据集
     */
    private void addDateForbanner(final List<CoinsStoreAd> listAd) {

        if (listAd.size() == 0 || "[]".equals(listAd)) {
            showToast("暂无广告数据!");
        } else {
            initDots(listAd);
            CoinsViewPagerAdapter adapter = new CoinsViewPagerAdapter(listAd);
            ad.setAdapter(adapter);
            ad.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int arg0) {
                    // TODO Auto-generated method stub
                    for (int i = 0; i < adDots.getChildCount(); i++) {
                        if (i == (arg0 % adDots.getChildCount())) {
                            adDots.getChildAt(i).setSelected(true);
                        } else {
                            adDots.getChildAt(i).setSelected(false);
                        }
                    }
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                    // TODO Auto-generated method stub

                }
            });

            ad.startAutoCycle();
        }

    }

    /**
     * 随着图片的移动显示对应的点
     *
     * @param listAd 广告数据集
     */
    private void initDots(List<CoinsStoreAd> listAd) {
        for (int i = 0; i < listAd.size(); i++) {
            adDots.addView(initDot());
        }
        adDots.getChildAt(0).setSelected(true);
    }

    /**
     * 轮播图的点
     *
     * @return
     */
    private View initDot() {
        return LayoutInflater.from(context).inflate(R.layout.boot_dot_coins_store, null);
    }

    @Override
    public <T> void update(T... data) {
        try {
            List<CoinsStoreGoods> datas = snatchAdapter.getItems();
            datas.add((Integer)data[0], (CoinsStoreGoods)data[1]);
            datas.remove((Integer)data[0] + 1);
            snatchAdapter.setData(datas);
            snatchAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 顶部广告轮播图的适配器
     *
     * @author Administrator
     */
    class CoinsViewPagerAdapter extends PagerAdapter {

        private List<View> views = new ArrayList<View>();
        private List<CoinsStoreAd> adlist;

        public CoinsViewPagerAdapter(List<CoinsStoreAd> adlist) {
            // TODO Auto-generated constructor stub
            super();
            this.adlist = adlist;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            View _view = LayoutInflater.from(context).inflate(R.layout.advertisement_item, null);
            ImageView imageView = (ImageView) _view.findViewById(R.id.centent_iv);
            TextView textview = (TextView) _view.findViewById(R.id.centent_text);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA
                    + listAd.get(position % listAd.size()).getBanner(), imageView);

            views.add(_view);
            ((ViewPager) container).addView(_view, 0);
            imageView.setTag(adlist.get(position % adlist.size()));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent intent = null;
                    CoinsStoreAd coinsStoreAd = (CoinsStoreAd) arg0.getTag();
                    if (!TextUtils.isEmpty(coinsStoreAd.getUrl())) {
                        if (2 == coinsStoreAd.getType() && WangYuApplication.getUser(context) == null) {
                            StartActivity(LoginActivity.class);
                        } else if (coinsStoreAd.getType() == 5) {
                            intent = new Intent(context, DownloadWebView.class);
                            intent.putExtra("download_url", coinsStoreAd.getUrl());
                            intent.putExtra("title", "推广");
                            context.startActivity(intent);
                        } else {
                            Log.i("GoldCoinsStoreActivity", ":::" + coinsStoreAd.getType() + "::" + coinsStoreAd.getUrl());
                            intent = new Intent(context, SubjectActivity.class);
                            intent.putExtra("coins_ad_type", coinsStoreAd.getType());
                            intent.putExtra("coins_ad_url", coinsStoreAd.getUrl());
                            intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.COINS_AD);
                            startActivity(intent);
                        }
                    }
                }
            });

            return _view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            // super.destroyItem(container, position, object);
            ((ViewPager) container).removeView(views.get(position % views.size()));
        }
    }

    @Override
    public void move(int x, int y, int oldx, int oldy) {
        // TODO Auto-generated method stub
        if (y > top) {
            titleBarSecond.setVisibility(View.VISIBLE);
            titleBarFirst.setVisibility(View.INVISIBLE);
        } else {
            titleBarSecond.setVisibility(View.GONE);
            titleBarFirst.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWatcher.unSubscribe(Observerable.ObserverableType.CROWDLIST,this);
    }

    /**
     * 登陆
     *
     * @param cls
     */
    private void StartActivity(Class<?> cls) {
        Intent intent = new Intent(context, cls);
        startActivity(intent);
    }

//    /**
//     * @param i
//     */
//    private void clickCount(int i) {
//        if (UMengStatisticsUtil.isCount) {
//            HashMap<String, String> clickType = new HashMap<String, String>();
//            clickType.put(UMengStatisticsUtil.GOLD_MALL_CLICK, UMengStatisticsUtil.countType[i]);
//            MobclickAgent.onEvent(context, UMengStatisticsUtil.GOLD_MALL, clickType);
//        }
//    }

}
