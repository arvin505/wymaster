package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.CoinsStoreAd;
import com.miqtech.master.client.entity.CoinsStoreGoods;
import com.miqtech.master.client.entity.ShopDetailInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.fragment.FragmentShopBaseInfo;
import com.miqtech.master.client.ui.fragment.FragmentShopBuyRecord;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.ImageLoadingListenerAdapter;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.BannerPagerView;
import com.miqtech.master.client.view.CornerProgressView;
import com.miqtech.master.client.view.layoutmanager.FullScrollView;
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
 * 众筹详情
 * Created by admin on 2016/3/8.
 */
public class ShopDetailActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.fullScrollView)
    FullScrollView fullScrollView;//滚动View
    @Bind(R.id.adBanner)
    BannerPagerView adBanner;//广告页面
    @Bind(R.id.llAdvertiseDots)
    LinearLayout llAdvertiseDots; //广告页面的圆点
    @Bind(R.id.ibBack)
    ImageButton ibBack; //返回按钮
    @Bind(R.id.tvGoodName)
    TextView tvGoodName; //商品名字
    @Bind(R.id.tvGoodCost)
    TextView tvGoodCost;// 商品价格
    @Bind(R.id.pbGood)
    CornerProgressView pbGood; //圆角进度条
    @Bind(R.id.tvSurplusPeople)
    TextView tvSurplusPeople; //剩余人数
    @Bind(R.id.tvProgressNum)
    TextView tvProgressNum; //进度数量
    @Bind(R.id.tvActivityTimes)
    TextView tvActivityTimes;// 活动次数按钮
    @Bind(R.id.llShopDetail)
    LinearLayout llShopDetail;// 商品详情tab
    @Bind(R.id.tvShopDetail)
    TextView tvShopDetail;
    @Bind(R.id.ivShopDetailTab)
    ImageView ivShopDetailTab;

    @Bind(R.id.llExpiryRule)
    LinearLayout llExpiryRule; //兑奖规则tab
    @Bind(R.id.tvExpiryRule)
    TextView tvExpiryRule;
    @Bind(R.id.ivExpiryRuleTab)
    ImageView ivExpiryRuleTab;

    @Bind(R.id.llBuyRecord)
    LinearLayout llBuyRecord; //购买记录tab
    @Bind(R.id.tvBuyRecord)
    TextView tvBuyRecord;
    @Bind(R.id.ivBuyRecordTab)
    ImageView ivBuyRecordTab;
    @Bind(R.id.llInstantBuy)
    LinearLayout llInstantBuy;
    @Bind(R.id.tvInstantBuy)
    TextView tvInstantBuy; //立即购买按钮
    @Bind(R.id.vsPay)
    ViewStub vsPay;     //支付弹层
    @Bind(R.id.viewBg)
    View viewBg;      //半透明黑色背景
    @Bind(R.id.ivOpeningPrize)
    ImageView ivOpeningPrize;//开奖中
    @Bind(R.id.rlLuckyer)
    RelativeLayout rlLuckyer; //幸运用户View
    @Bind(R.id.tvUserTelNum)
    TextView tvUserTelNum; //幸运号码
    @Bind(R.id.tvNum)
    TextView tvNum; //幸运号
    private View payView; //支付View
    private int perPrice = 1;//单价
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private Class[] classes = {FragmentShopBaseInfo.class, FragmentShopBaseInfo.class, FragmentShopBuyRecord.class};
    private List<CoinsStoreAd> listAd = new ArrayList<CoinsStoreAd>();// 顶部广告数据
    private int fragmentId;
    long timeMills;
    private Context context;
    private ImageButton ibMinus;
    private TextView tvPayNum;
    private ImageButton ibPlus;
    private TextView tvPayGoldCoinNum;
    private TextView tvPayBtn;
    private int buyNum = 1; //购买数量
    private FrameLayout frameLayout;
    private String id;//商品id
    private ShopDetailInfo shopDetailInfo; //商品信息
    private CoinsStoreGoods coinsStoreGoods;
    private Animation outAnimation;
    private Animation inAnimation;

    private boolean once = false;
    private Observerable mWatcher;

    private int itemPos;
    private int isLast = 0;//是否可以加载更多   0可以  1不可以
    private int page = 1;//第几页
    private int pageSize=10;
    private boolean shouldShowMore = true;
    private boolean isPayUpdataData=false;  //点击支付后更新数据标记

    @Override
    protected void onResume() {
        super.onResume();
        if (adBanner != null && listAd.size() > 0) {
            adBanner.startAutoCycle();
        }
        setupView(); //设置数据
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWatcher = Observerable.getInstance();
    }


    @Override
    protected void init() {
        super.init();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }*/
        setContentView(R.layout.activity_shop_detail);
        ButterKnife.bind(this);
        context = this;
        lengthCoding = UMengStatisticsUtil.CODE_2000;
        itemPos = getIntent().getIntExtra("itemPos",-1);
        coinsStoreGoods = (CoinsStoreGoods) getIntent().getSerializableExtra("coinsStoreGoods");
        if(coinsStoreGoods!=null) {
            id = coinsStoreGoods.getId() + "";
        }else {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("id"))) {
                id = getIntent().getStringExtra("id");
            }
        }
        LogUtil.e(TAG, "changestate ...it + " + itemPos);
        setListener();
        initAnimation();
        loadShopDetailData();  //加载详情页面的数据
    }

    private void initAnimation() {
        outAnimation = AnimationUtils.loadAnimation(this, R.anim.push_bottom_out);
        outAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (tvPayBtn != null) {
                    tvPayBtn.setEnabled(false);
                    tvInstantBuy.setEnabled(true);
                    frameLayout.setEnabled(false);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (payView != null) {
                    payView.setVisibility(View.GONE);
                    frameLayout.setEnabled(true);
                    payView.clearAnimation();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        inAnimation = AnimationUtils.loadAnimation(this, R.anim.push_bottom_in);
        inAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (payView != null) {
                    tvPayBtn.setEnabled(false);
                }
                tvInstantBuy.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (payView != null) {
                    tvPayBtn.setEnabled(true);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setSelectItem(int position) {
        fragmentId = position;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        //先隐藏所有fragment
        for (Fragment fragment : fragmentList) {
            if (null != fragment) {
                fragmentTransaction.hide(fragment);
            }
        }
        Fragment fragment;
        if (null == fragmentList.get(position)) {
            Bundle bundle = new Bundle();
            if (position != 2) {
                bundle.putInt("position", position);
                if (shopDetailInfo != null) {
                    bundle.putSerializable("commodity_info", shopDetailInfo.getCommodityInfo());
                }
            } else {
                bundle.putInt("position", position);
                if(shopDetailInfo!=null && shopDetailInfo.getBuyRecord()!=null )
                bundle.putParcelableArrayList("data",shopDetailInfo.getBuyRecord().getList());
            }
            fragment = Fragment.instantiate(ShopDetailActivity.this, classes[position].getName(), bundle);
            fragmentList.set(position, fragment);
            // 如果Fragment为空，则创建一个并添加到界面上
            fragmentTransaction.add(R.id.fragmentContent, fragment);
        } else {
            // 如果Fragment不为空，则直接将它显示出来
            fragment = fragmentList.get(position);
            if (position == 0 && fragment.getArguments() == null) {
                Bundle bundle = new Bundle();
                if (position != 2) {
                    bundle.putInt("position", position);
                    if (shopDetailInfo != null) {
                        bundle.putSerializable("commodity_info", shopDetailInfo.getCommodityInfo());
                    }
                } else {
                    bundle.putInt("position", position);
                    if(shopDetailInfo!=null && shopDetailInfo.getBuyRecord()!=null )
                    bundle.putParcelableArrayList("data",shopDetailInfo.getBuyRecord().getList());
                }
            }
            fragmentTransaction.show(fragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
        changeBottomType(position);
    }

    private void changeBottomType(int index) {
        tvShopDetail.setTextColor(index == 0 ? getResources().getColor(R.color.orange) : getResources().getColor(R.color.shop_font_black));
        ivShopDetailTab.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
        calculateTabLength(ivShopDetailTab, tvShopDetail, tvShopDetail.getText().toString());

        tvExpiryRule.setTextColor(index == 1 ? getResources().getColor(R.color.orange) : getResources().getColor(R.color.shop_font_black));
        ivExpiryRuleTab.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
        calculateTabLength(ivExpiryRuleTab, tvExpiryRule, tvExpiryRule.getText().toString());

        tvBuyRecord.setTextColor(index == 2 ? getResources().getColor(R.color.orange) : getResources().getColor(R.color.shop_font_black));
        ivBuyRecordTab.setVisibility(index == 2 ? View.VISIBLE : View.GONE);
        calculateTabLength(ivBuyRecordTab, tvExpiryRule, tvBuyRecord.getText().toString());

    }

    /**
     * 计算tab下边游标的长度并设置位置
     *
     * @param
     */
    public void calculateTabLength(ImageView iv, TextView tv, String content) {
        android.view.ViewGroup.LayoutParams layoutParams = iv.getLayoutParams();
        int contentLength = getTextViewLength(tv, content);
        layoutParams.width = contentLength + 16;
        iv.setLayoutParams(layoutParams);
    }

    /**
     * 计算tv文字的长度
     *
     * @param textView
     * @param text
     * @return
     */
    public int getTextViewLength(TextView textView, String text) {
        TextPaint paint = textView.getPaint();
        // 得到使用该paint写上text的时候,像素为多少
        int textLength = (int) paint.measureText(text);
        return textLength;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llShopDetail:
                changeBottomType(0);
                setSelectItem(0);
                break;
            case R.id.llExpiryRule:
                changeBottomType(1);
                setSelectItem(1);
                break;
            case R.id.llBuyRecord:
                changeBottomType(2);
                setSelectItem(2);
                break;
            case R.id.ibBack:
                finish();
                break;
            case R.id.tvInstantBuy:
                if (Utils.isFastDoubleClick()) {
                    return;
                }
                showPayView();
                showAlphaAnimation();
                break;
            case R.id.ibMinus:
                if (buyNum > 1) {
                    buyNum -= 1;
                    tvPayNum.setText(buyNum + "");
                    setFontDiffrentColor(("共" + perPrice * buyNum + "金币"), 1, ("共" + perPrice * buyNum + "金币").length(), tvPayGoldCoinNum);
                } else {
                    //      showToast("购买数量不能小于1");
                }
                break;
            case R.id.ibPlus:
                if (shopDetailInfo != null && shopDetailInfo.getCommodityInfo() != null) {
                    //此处去掉限制 下次优化
              //      if (buyNum < shopDetailInfo.getCommodityInfo().getLeftNum()) {
                        buyNum += 1;
                        tvPayNum.setText(buyNum + "");
                        setFontDiffrentColor(("共" + perPrice * buyNum + "金币"), 1, ("共" + perPrice * buyNum + "金币").length(), tvPayGoldCoinNum);
//                    } else {
//                        //   showToast("购买数量不能超过剩余商品数量");
//                    }
                }
                break;
            case R.id.tvPayBtn:
                //TODO 后台请求获取数据
                buyRequst();
                break;
            case R.id.flEmptyView:
                goneOccupySeatView();
                hideAlphaAnimation();
                break;
        }
    }

    private void hideAlphaAnimation() {
        viewBg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_view_hide));
        viewBg.setVisibility(View.GONE);
    }

    private void showAlphaAnimation() {
        viewBg.setVisibility(View.VISIBLE);
        viewBg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_view_show));
    }

    private void buyRequst() {
        //  showLoading();
        Map<String, String> params = new HashMap<>();
        if (WangYuApplication.getUser(this) != null) {
            params.put("userId", WangYuApplication.getUser(this).getId() + "");
            params.put("token", WangYuApplication.getUser(this).getToken());
        } else {
            StartActivity(LoginActivity.class);
            return;
        }
        params.put("id", id);
        params.put("amount", buyNum + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SHOOP_DETAIL_PAY, params, HttpConstant.SHOOP_DETAIL_PAY);
    }

    public void showPayView() {
        if (payView == null) {// 为空则初始化控件
            vsPay.setLayoutResource(R.layout.viewstub_pay);
            payView = vsPay.inflate();
            findViewByNickName();
        }
        payView.startAnimation(inAnimation);
        //TODO 网络获取单价
        setFontDiffrentColor(("共" + perPrice * buyNum + "金币"), 1, ("共" + perPrice * buyNum + "金币").length(), tvPayGoldCoinNum);
        payView.setVisibility(View.VISIBLE);
    }

    public void goneOccupySeatView() {
        if (payView != null && payView.getVisibility() == View.VISIBLE) {
            payView.startAnimation(outAnimation);
        }
    }

    private void findViewByNickName() {
        ibMinus = (ImageButton) payView.findViewById(R.id.ibMinus);
        tvPayNum = (TextView) payView.findViewById(R.id.tvPayNum);
        ibPlus = (ImageButton) payView.findViewById(R.id.ibPlus);
        tvPayGoldCoinNum = (TextView) payView.findViewById(R.id.tvPayGoldCoinNum);
        tvPayBtn = (TextView) payView.findViewById(R.id.tvPayBtn);
        frameLayout = (FrameLayout) payView.findViewById(R.id.flEmptyView);
        tvPayNum.setText(buyNum + "");
        ibMinus.setOnClickListener(this);
        ibPlus.setOnClickListener(this);
        tvPayBtn.setOnClickListener(this);
        frameLayout.setOnClickListener(this);
    }

    /**
     * 加载商品详情数据
     */
    private void loadShopDetailData() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        if (WangYuApplication.getUser(this) != null) {
            params.put("userId", WangYuApplication.getUser(this).getId() + "");
            params.put("token", WangYuApplication.getUser(this).getToken());
        }
        params.put("id", id);
        params.put("page",page+"");
        params.put("pageSize",pageSize+"");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SHOP_DETAIL, params, HttpConstant.SHOP_DETAIL);
    }

    @Override
    public void onSuccess(final JSONObject object, String method) {
        super.onSuccess(object, method);
        Log.i("ShopDetailActivity", "数据" + object.toString());
        if(fragmentList!=null && fragmentList.get(2) instanceof  FragmentShopBuyRecord) {
            ((FragmentShopBuyRecord)fragmentList.get(2)).hideFooter();
        }
        try {
            if (object.getString("code").equals(0 + "") && method.equals(HttpConstant.SHOOP_DETAIL_PAY)) {
                goneOccupySeatView();
                hideAlphaAnimation();   showToast("购买成功");
                page=1;
                isPayUpdataData=true;
                loadShopDetailData();
            }
            Gson gs = new Gson();
            Object obj = null;
            if (object.has("object")) {
                obj = object.getString("object");
            } else {
                return;
            }
            if (method.equals(HttpConstant.SHOP_DETAIL)) {
                shopDetailInfo = null;
                shopDetailInfo = GsonUtil.getBean(object.getJSONObject("object").toString(), ShopDetailInfo.class);
                isLast = shopDetailInfo.getBuyRecord().getIsLast();
               if (!isPayUpdataData) {
                   if(page==1) {
                       setBannerData();
                       setSelectItem(0);
                   }
               }else{
                   isPayUpdataData=false;
                   changeListData();
               }
                setData();
                if (fragmentList != null && fragmentList.get(2) instanceof FragmentShopBuyRecord) {
                    ((FragmentShopBuyRecord) fragmentList.get(2)).setData(shopDetailInfo.getBuyRecord().getList(),page);
                }
            }
        } catch (JsonSyntaxException e) {
            showToast("数据异常");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void changeListData() {
        if(coinsStoreGoods==null){
            return ;
        }
        coinsStoreGoods.setProgress(shopDetailInfo.getCommodityInfo().getProgress());
        coinsStoreGoods.setLeftNum(shopDetailInfo.getCommodityInfo().getLeftNum());
        coinsStoreGoods.setCrowdfundStatus(shopDetailInfo.getCommodityInfo().getCrowdfundStatus());
        mWatcher.notifyChange(Observerable.ObserverableType.CROWDLIST,itemPos, coinsStoreGoods);
        LogUtil.e(TAG, "changestate ...it  ch d  + " + itemPos);
    }
    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        if(fragmentList!=null && fragmentList.get(2) instanceof  FragmentShopBuyRecord) {
            ((FragmentShopBuyRecord)fragmentList.get(2)).hideFooter();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(fragmentList!=null && fragmentList.get(2) instanceof  FragmentShopBuyRecord) {
            ((FragmentShopBuyRecord)fragmentList.get(2)).hideFooter();
        }
    }

    private void setBannerData() {
        if (shopDetailInfo != null && shopDetailInfo.getCommodityInfo() != null && shopDetailInfo.getCommodityInfo().getImgs() != null) {
            listAd.clear();
            String image = shopDetailInfo.getCommodityInfo().getImgs();
            String[] images = image.split(",");
            for (int i = 0; i < images.length; i++) {
                CoinsStoreAd ad = new CoinsStoreAd();
                ad.setBanner(images[i]);
                listAd.add(ad);
            }
            addDateForbanner(listAd);
        }
    }

    private void setData() {
        if (shopDetailInfo != null && shopDetailInfo.getCommodityInfo() != null) {
            if (shopDetailInfo.getCommodityInfo().getBuyNum() == 0) {
                tvActivityTimes.setText("您未参与本次活动哦！");
            } else {
                String content = this.getResources().getString(R.string.participation_time, shopDetailInfo.getCommodityInfo().getBuyNum());
                setFontDiffrentColor(content, 4, content.length() - 1, tvActivityTimes);
            }
            tvGoodCost.setText(shopDetailInfo.getCommodityInfo().getCoins() + ""); //设置商品价格
            if (shopDetailInfo.getCommodityInfo().getCrowdfundStatus() == 2) {
                ivOpeningPrize.setVisibility(View.GONE);
                rlLuckyer.setVisibility(View.VISIBLE);
                tvUserTelNum.setText(Utils.changeString(shopDetailInfo.getCommodityInfo().getPrizePhone()));
                tvNum.setText(shopDetailInfo.getCommodityInfo().getCdkey());
            } else if (shopDetailInfo.getCommodityInfo().getCrowdfundStatus() == 1) {
                rlLuckyer.setVisibility(View.GONE);
                ivOpeningPrize.setVisibility(View.VISIBLE);
            } else if (shopDetailInfo.getCommodityInfo().getCrowdfundStatus() == 0){
                rlLuckyer.setVisibility(View.GONE);
                ivOpeningPrize.setVisibility(View.GONE);
                setFontDiffrentColor("还剩" + shopDetailInfo.getCommodityInfo().getLeftNum() + "人", 2, ("还剩" + shopDetailInfo.getCommodityInfo().getLeftNum() + "人").length() - 1, tvSurplusPeople);
                setFontDiffrentColor("进度" + (int) shopDetailInfo.getCommodityInfo().getProgress() + "%", 2, ("进度" + (int) shopDetailInfo.getCommodityInfo().getProgress() + "%").length() - 1, tvProgressNum);
            }
            llInstantBuy.setVisibility(shopDetailInfo.getCommodityInfo().getCrowdfundStatus() == 0 ? View.VISIBLE : View.GONE);
        }

    }
//    public  void remindUserDialog() {
//        final AlertDialog dialog = new AlertDialog.Builder(ShopDetailActivity.this).create();
//        dialog.show();
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(true);
//        Window window = dialog.getWindow();
//        View view = LayoutInflater.from(ShopDetailActivity.this).inflate(R.layout.dialog_register_marked_words, null);
//        window.setContentView(view);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//        TextView title_tv=(TextView)view.findViewById(R.id.dialog_title_register);
//        TextView ok_bt = (TextView) view.findViewById(R.id.dialog_register_yes_pact);
//        TextView no_bt = (TextView) view.findViewById(R.id.dialog_register_no_pact);
//        View vv = view.findViewById(R.id.dialog_line_no_pact);
//        vv.setVisibility(View.VISIBLE);
//        no_bt.setVisibility(View.VISIBLE);
//        title_tv.setVisibility(View.VISIBLE);
//        setFontDiffrentColor(getResources().getString(R.string.tips),10,getResources().getString(R.string.tips).length()-2,title_tv);
//        ok_bt.setText(getResources().getString(R.string.earnBtn));
//        ok_bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showToast("去专区金币吧！");
//                dialog.dismiss();
//            }
//        });
//
//        no_bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//    }

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
            adBanner.setAdapter(adapter);
            adBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int arg0) {
                    // TODO Auto-generated method stub
                    for (int i = 0; i < llAdvertiseDots.getChildCount(); i++) {
                        if (i == (arg0 % llAdvertiseDots.getChildCount())) {
                            llAdvertiseDots.getChildAt(i).setSelected(true);
                        } else {
                            llAdvertiseDots.getChildAt(i).setSelected(false);
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
            adBanner.startAutoCycle();
        }

    }

    /**
     * 随着图片的移动显示对应的点
     *
     * @param
     */
    private void initDots(List<CoinsStoreAd> listAd) {
        for (int i = 0; i < listAd.size(); i++) {
            llAdvertiseDots.addView(initDot());
        }
        llAdvertiseDots.getChildAt(0).setSelected(true);
    }

    /**
     * 轮播图的点
     *
     * @return
     */
    private View initDot() {
        return LayoutInflater.from(context).inflate(R.layout.boot_dot_coins_store, null);
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
          final  ImageView imageView = (ImageView) _view.findViewById(R.id.centent_iv);
            TextView textview = (TextView) _view.findViewById(R.id.centent_text);
            AsyncImage.loadNetPhotoWithListener(HttpConstant.SERVICE_UPLOAD_AREA
                    + listAd.get(position % listAd.size()).getBanner(), imageView, new ImageLoadingListenerAdapter() {
                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    Drawable drawable = new BitmapDrawable(bitmap);
                    float viewWidth = WangYuApplication.WIDTH;
                    int height = drawable.getIntrinsicHeight();
                    int width = drawable.getIntrinsicWidth();
                    float scale = (float) height / width;
                    ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                    lp.width = (int) viewWidth;
                    lp.height = (int) ((int) viewWidth * scale);
                    imageView.setLayoutParams(lp);
                }
            });
//            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA
//                    + listAd.get(position % listAd.size()).getBanner(), imageView);
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

    private void StartActivity(Class<?> cls) {
        Intent intent = new Intent(context, cls);
        startActivity(intent);
    }

    /**
     * 填充ui
     */
    private void setupView() {
        for (int i = 0; i < 3; i++) {
            fragmentList.add(null);
        }
        if (coinsStoreGoods == null) {
            return;
        }
        perPrice = coinsStoreGoods.getPrice();
        tvGoodName.setText(coinsStoreGoods.getCommodityName()); //设置商品名称
        tvGoodCost.setText(coinsStoreGoods.getPrice() + ""); //设置商品价格
        pbGood.setMaxCount(100);
        pbGood.setCurrentCount(coinsStoreGoods.getProgress());
        setFontDiffrentColor("还剩" + coinsStoreGoods.getLeftNum() + "人", 2, ("还剩" + coinsStoreGoods.getLeftNum() + "人").length() - 1, tvSurplusPeople);
        setFontDiffrentColor("进度" + (int) coinsStoreGoods.getProgress() + "%", 2, ("进度" + (int) coinsStoreGoods.getProgress() + "%").length() - 1, tvProgressNum);
        llInstantBuy.setVisibility(coinsStoreGoods.getCrowdfundStatus() == 0 ? View.VISIBLE : View.GONE);
        fullScrollView.setScrollchangeListener(new FullScrollView.OnScrollChangeListener() {
            @Override
            public void scrollChange(int l, int t, int oldl, int oldt) {
                if (t != oldt) {//判断是scrollView在滑动
                    int scrollY = fullScrollView.getScrollY();
                    int height = fullScrollView.getHeight();
                    int scrollViewMeasuredHeight = fullScrollView.getChildAt(0).getMeasuredHeight();
                    if ((scrollY + height) == scrollViewMeasuredHeight) {//滑到底部
                        if (fragmentId == 2) {
                            if (isLast == 0) {
                                ((FragmentShopBuyRecord)fragmentList.get(2)).showFooter();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        page++;
                                        loadShopDetailData();
                                    }
                                }, 300);
                            }else{
                                if (shouldShowMore) {
                                    showToast(getResources().getString(R.string.nomore));
                                    shouldShowMore = false;
                                }
                            }
                        }
                    }
                }

            }
        });

    }

    /**
     * 点击事件
     */
    private void setListener() {
        ibBack.setOnClickListener(this);
        llShopDetail.setOnClickListener(this);
        llExpiryRule.setOnClickListener(this);
        llBuyRecord.setOnClickListener(this);
        tvInstantBuy.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == LoginActivity.LOGIN_OK) {
        }
    }

    @Override
    protected void onDestroy() {
        timeMills = 0;
        super.onDestroy();
    }

    public void setToolbarHeight() {
        if (TRANSLUANTNAVAGATIONBAR) {
            mRlToolbar = (RelativeLayout) findViewById(R.id.rl_toolbar);
            if (mRlToolbar == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mRlToolbar.getLayoutParams();
                params.setMargins(0, Utils.getStatusHeight(this) / 2, 0, 0);
                mRlToolbar.setLayoutParams(params);
            }
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

}
