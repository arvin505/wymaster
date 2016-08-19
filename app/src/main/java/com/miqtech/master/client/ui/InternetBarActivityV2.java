package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.appmanager.AppManager;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.InternetBarInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.ui.fragment.FragmentInternetbarActivity;
import com.miqtech.master.client.ui.fragment.FragmentNetBarComment;
import com.miqtech.master.client.ui.fragment.FragmentNetbarBaseInfo;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.BitmapUtil;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.ImageLoadingListenerAdapter;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ShareToFriendsUtil;
import com.miqtech.master.client.utils.TimeUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.ExpertMorePopupWindow;
import com.miqtech.master.client.view.FlowLayout;
import com.miqtech.master.client.view.layoutmanager.FullScrollView;
import com.miqtech.master.client.watcher.Observerable;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.constant.WBConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 网吧详情
 * Created by admin on 2016/3/8.
 */
public class InternetBarActivityV2 extends BaseActivity implements View.OnClickListener, IWeiboHandler.Response {

    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private Class[] classes = {FragmentNetbarBaseInfo.class, FragmentInternetbarActivity.class, FragmentNetBarComment.class};
    private Observerable observerable = Observerable.getInstance();
    RelativeLayout rl;

    @Bind(R.id.tv_netbar_payword)
    TextView tvPayWord;
    @Bind(R.id.llNetbarInfo)
    LinearLayout llNetbarInfo;
    @Bind(R.id.llNetbarActivity)
    LinearLayout llNetbarActivity;
    @Bind(R.id.llNetbarEvaluate)
    LinearLayout llNetbarEvaluate;
    @Bind(R.id.ivinfoSelect)
    ImageView ivInfoSelect;
    @Bind(R.id.ivActivitySelect)
    ImageView ivActivitySelect;
    @Bind(R.id.ivCommentSelect)
    ImageView ivCommentSelect;

    @Bind(R.id.tv_discount_time)
    TextView tvDiscountTime; //优惠开启时间
    @Bind(R.id.netbar_head)
    ImageView imgNetbarHead; //图集
    @Bind(R.id.ll_netbar_header_pay)
    LinearLayout llNetbarHeaderPay; //支付、充值
    @Bind(R.id.ll_netbar_header_tags)
    FlowLayout llNetbarHeaderTags; //网吧标签
    @Bind(R.id.tv_netbar_header_name)
    TextView tvNetbarHeaderName;  //网吧名称
    @Bind(R.id.tv_netbar_header_price)
    TextView tvNetbarHeaderPrice;  //网吧价格
    @Bind(R.id.ll_netbar_header)
    LinearLayout llNetbarHeader; //网吧详情头部
    @Bind(R.id.tv_nav_natbarname)
    TextView tvNavNetbarname; //网吧名称（导航上的）

    @Bind(R.id.ll_netbar_header_nav)
    LinearLayout llNetbarHeaderNav;   //网吧导航栏
    @Bind(R.id.ll_netbar_hidden)
    LinearLayout llNetbarHidden;   //隐藏部分的

    @Bind(R.id.ll_netbar_nav_pay)
    LinearLayout llNetbarNavPay; //充值部分

    @Bind(R.id.ll_netbar_tab)
    LinearLayout llNetbarTab;

    @Bind(R.id.ll_hidden)
    LinearLayout hidden;

    @Bind(R.id.fullScrollView)
    FullScrollView scrollView;

    @Bind(R.id.ibLeft)
    ImageButton ibLeft;

    /**
     * 吸附部分
     */
    @Bind(R.id.llNetbarInfoTab)
    LinearLayout llNetbarInfoTab; //网吧信息
    @Bind(R.id.tvNetbarInfoTab)
    TextView tvNetbarInfoTab;
    @Bind(R.id.ivinfoSelectTab)
    ImageView ivinfoSelectTab;

    @Bind(R.id.llNetbarActivityTab)
    LinearLayout llNetbarActivityTab; //网吧活动
    @Bind(R.id.tvNetbarActivityTab)
    TextView tvNetbarActivityTab;
    @Bind(R.id.ivActivitySelectTab)
    ImageView ivActivitySelectTab;

    @Bind(R.id.llNetbarEvaluateTab)
    LinearLayout llNetbarEvaluateTab;  //网吧评价
    @Bind(R.id.tvNetbarEvaluateTab)
    TextView tvNetbarEvaluateTab;
    @Bind(R.id.ivCommentSelectTab)
    ImageView ivCommentSelectTab;

    @Bind(R.id.img_share)
    ImageView imgShare;  //分享
    @Bind(R.id.img_favorite)
    ImageView imgFavorite; //收藏

    @Bind(R.id.tv_netbar_nav_discount)
    TextView tvNetbarNavDisount;
    @Bind(R.id.tv_netbar_nav_pay)
    TextView tvNetbarNavPay;


    private int fragmentId;

    private String netbarId;

    private InternetBarInfo mNetbarInfo;

    private int showHeight = 0;

    private boolean show = false;

    private ShareToFriendsUtil shareToFriendsUtil;
    private ExpertMorePopupWindow popwin;

    int oldScrollY = 0;
    int oldHeight = 0;

    long timeMills;
    private String discountStr;

    //定时器
    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            timeMills -= 1000;

            final String payTipStr;
            if (timeMills <= 0) {
                payTipStr = "活动进行中";
                timerTask.cancel();
                timer.cancel();
            } else {
                payTipStr = TimeUtil.calculateTime(timeMills) + discountStr;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvDiscountTime.setText(payTipStr);
                    tvNetbarNavDisount.setText(payTipStr);
                }
            });
        }
    };

    private final String[] clickOnTheEventType = {"网吧详情点击量", "竞技活动点击量", "评价点击量"};
    private Context context;

    @Override
    protected void onResume() {
        LogUtil.e("isLeaveApp-----------onResume--------InternetBarActivityV2------------", UMengStatisticsUtil.isLeaveApp + "");
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
        popwin.setOnItemClick(itemOnClick);
        shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
        initSinaSso(savedInstanceState);
    }

    public void initSinaSso(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            shareToFriendsUtil.getIWeiApiInstance(this).handleWeiboResponse(getIntent(), this);
        }
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
        setContentView(R.layout.activity_internetbar_v2);
        ButterKnife.bind(this);
        context = this;
        lengthCoding = UMengStatisticsUtil.CODE_2000;
        rl = (RelativeLayout) ((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0);
        netbarId = getIntent().getStringExtra("netbarId");
        ibLeft.setImageResource(R.drawable.back_white);

        loadNetbarInfo();
        initData();
        initView();
        setListener();
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView() {
        super.initView();
        llNetbarInfo.setOnClickListener(null);
        llNetbarActivity.setOnClickListener(null);
        llNetbarEvaluate.setOnClickListener(null);
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
        if (!fragmentList.isEmpty() && null == fragmentList.get(position)) {
            Bundle bundle = new Bundle();
            // bundle.putString(Constant.TITLE, drawerTitles[position]);
            //         if (position == 0 && mNetbarInfo != null) {
            bundle.putSerializable("nerbar", mNetbarInfo);
            //       }
            fragment = Fragment.instantiate(InternetBarActivityV2.this, classes[position].getName(), bundle);
            fragmentList.set(position, fragment);
            // 如果Fragment为空，则创建一个并添加到界面上
            fragmentTransaction.add(R.id.fragment_content, fragment);
        } else {
            // 如果Fragment不为空，则直接将它显示出来
            fragment = fragmentList.get(position);
            if (position == 0 && fragment.getArguments() == null && mNetbarInfo != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("nerbar", mNetbarInfo);
            }
            fragmentTransaction.show(fragment);
        }
        if (!isFinishing()) {
            fragmentTransaction.commitAllowingStateLoss();
            changeBottomType(position);
        }
    }

    private void changeBottomType(int index) {
        switch (index) {
            case 0:
                ivInfoSelect.setVisibility(View.VISIBLE);
                ivActivitySelect.setVisibility(View.INVISIBLE);
                ivCommentSelect.setVisibility(View.INVISIBLE);

                ivinfoSelectTab.setVisibility(View.VISIBLE);
                ivActivitySelectTab.setVisibility(View.INVISIBLE);
                ivCommentSelectTab.setVisibility(View.INVISIBLE);
                break;
            case 1:
                ivInfoSelect.setVisibility(View.INVISIBLE);
                ivActivitySelect.setVisibility(View.VISIBLE);
                ivCommentSelect.setVisibility(View.INVISIBLE);

                ivinfoSelectTab.setVisibility(View.INVISIBLE);
                ivActivitySelectTab.setVisibility(View.VISIBLE);
                ivCommentSelectTab.setVisibility(View.INVISIBLE);
                break;
            case 2:
                ivInfoSelect.setVisibility(View.INVISIBLE);
                ivActivitySelect.setVisibility(View.INVISIBLE);
                ivCommentSelect.setVisibility(View.VISIBLE);

                ivinfoSelectTab.setVisibility(View.INVISIBLE);
                ivActivitySelectTab.setVisibility(View.INVISIBLE);
                ivCommentSelectTab.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llNetbarInfo:
                changeBottomType(0);
                setSelectItem(0);
                break;
            case R.id.llNetbarActivity:
                changeBottomType(1);
                setSelectItem(1);
                break;
            case R.id.llNetbarEvaluate:
                changeBottomType(2);
                setSelectItem(2);
                break;
            case R.id.llNetbarInfoTab:
                changeBottomType(0);
                setSelectItem(0);
                break;
            case R.id.llNetbarActivityTab:
                changeBottomType(1);
                setSelectItem(1);
                break;
            case R.id.llNetbarEvaluateTab:
                changeBottomType(2);
                setSelectItem(2);
                break;
            case R.id.netbar_head:
                openPhoto();
                break;
            case R.id.ibLeft:
                finish();
                break;
            case R.id.img_favorite:
                favoriteClick();
                break;
            case R.id.img_share:
                if (popwin != null) {
                    popwin.show();
                } else {
                    popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
                    popwin.setOnItemClick(itemOnClick);
                    shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
                    popwin.show();
                }
                break;
            case R.id.ll_netbar_header_pay:
                if (mNetbarInfo != null && mNetbarInfo.getIs_order() == 1) {
                    jumpToPay();
                } else {
                    showToast("该网吧暂未开通支付功能");
                }
                break;
            case R.id.tv_netbar_nav_pay:
                if (mNetbarInfo != null && mNetbarInfo.getIs_order() == 1) {
                    jumpToPay();
                } else {
                    showToast("该网吧暂未开通支付功能");
                }
                break;
        }
    }

    /**
     * jump to pay
     */
    private void jumpToPay() {
        /** 支付 **/
        Intent intent;
        if (WangYuApplication.getUser(this) != null) {
            if (mNetbarInfo != null) {
                postLogTime(UMengStatisticsUtil.CODE_2014, null, null, mNetbarInfo.getId());
                intent = new Intent();
                intent.putExtra("netbar", mNetbarInfo);
                intent.setClass(this, PaymentActivity.class);
                AppManager.getAppManager().addActivity(this);
                startActivity(intent);
            }

        } else {
            intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            //intent.putExtra(LoginActivity.LOGIN_TYPE, LoginActivity.LOGIN_FOR_LOGIN);
            startActivityForResult(intent, LoginActivity.LOGIN_OK);
            showToast("请先登录");
        }
    }

    /**
     * 打开图集
     */
    private void openPhoto() {
        if (mNetbarInfo != null && mNetbarInfo.getImgs() != null && mNetbarInfo.getImgs().size() > 0) {
            Intent intent = new Intent();
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable("images", (Serializable) mNetbarInfo.getImgs());
            // bundle2.putInt("image_index", (Integer) v.getTag());
            intent.putExtras(bundle2);
            intent.setClass(this, ImagePagerActivity.class);
            startActivity(intent);
        } else {
            showToast("该网吧没有上图集");
        }
    }

    /**
     * 收藏按钮
     */
    private void favoriteClick() {
        if (WangYuApplication.getUser(this) != null) {
            if (mNetbarInfo != null) {
                favorNetbar(mNetbarInfo.getFaved() != 1);
            }
        } else {
            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            //intent.putExtra(LoginActivity.LOGIN_TYPE, LoginActivity.LOGIN_FOR_LOGIN);
            startActivity(intent);
            showToast("请先登录");
        }
    }

    /**
     * 添加标签
     */
    private void addTags() {
        if (mNetbarInfo != null && !TextUtils.isEmpty(mNetbarInfo.getTag())) {
            String[] tags = mNetbarInfo.getTag().split(",");
            LayoutInflater inflater = LayoutInflater.from(this);
            llNetbarHeaderTags.removeAllViews();
            for (int i = 0; i < tags.length && i < 10; i++) {
                TextView view = (TextView) inflater.inflate(R.layout.layout_netbar_tag, null);
                view.setText(tags[i]);
                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = Utils.dp2px(5);
                params.topMargin = Utils.dp2px(5);
                llNetbarHeaderTags.addView(view, params);
            }

        }
    }

    /**
     * 请求数据
     * 因为基本信息fragment的数据和activity里面的是一致的
     * 所以将请求数据的操作放在activity中，然后将对象传递给
     * fragment构造UI
     */
    private void loadNetbarInfo() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        if (Constant.isLocation) {
            params.put("latitude", Constant.latitude + "");
            params.put("longitude", Constant.longitude + "");
        }
        if (WangYuApplication.getUser(this) != null) {
            params.put("userId", WangYuApplication.getUser(this).getId() + "");
            params.put("token", WangYuApplication.getUser(this).getToken());
        }
        params.put("netbarId", netbarId + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.NETBARINFO_V2, params, HttpConstant.NETBARINFO_V2);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        if (method.equals(HttpConstant.NETBARINFO_V2)) {
            if (object.has("object")) {
                try {
                    mNetbarInfo = GsonUtil.getBean(object.getJSONObject("object").toString(), InternetBarInfo.class);
                    setupView();
                    setSelectItem(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (method.equals(HttpConstant.NETBAR_UNFAVOR)) {
            imgFavorite.setImageResource(R.drawable.icon_netbar_favor);
            mNetbarInfo.setFaved(0);
            observerable.notifyChange(Observerable.ObserverableType.COLLECTSTATE, 1, Integer.parseInt(netbarId), false);
            showToast("取消成功");
        } else if (method.equals(HttpConstant.NETBAR_FAVOR)) {
            imgFavorite.setImageResource(R.drawable.icon_netbar_favored);
            mNetbarInfo.setFaved(1);
            observerable.notifyChange(Observerable.ObserverableType.COLLECTSTATE, 1, Integer.parseInt(netbarId), true);
            showToast("收藏成功");
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
    }

    /**
     * 填充ui
     */
    private void setupView() {
        int count = 3;
        if (mNetbarInfo.getLevels() == 0) {
            count = 2;
        }
        for (int i = 0; i < count; i++) {
            fragmentList.add(null);
        }
        if (mNetbarInfo != null) {
            tvNetbarHeaderName.setText(mNetbarInfo.getName());
            if (0 == mNetbarInfo.getLevels()) {
                llNetbarHeaderPay.setBackgroundResource(R.drawable.bg_bar_pay);
                llNetbarEvaluateTab.setVisibility(View.GONE);
                llNetbarEvaluate.setVisibility(View.GONE);
            } else if (1 == mNetbarInfo.getLevels()) {
                llNetbarHeaderPay.setBackgroundResource(R.drawable.bg_bar_pay_member);
            } else if (2 == mNetbarInfo.getLevels()) {
                llNetbarHeaderPay.setBackgroundResource(R.drawable.bg_bar_pay_crown);
            }
            tvNetbarHeaderPrice.setText(getResources().getString(R.string.price_per_hour, mNetbarInfo.getPrice_per_hour()));
            tvNavNetbarname.setText(mNetbarInfo.getName());
            AsyncImage.loadNetPhotoWithListener(HttpConstant.SERVICE_UPLOAD_AREA + mNetbarInfo.getIcon(), imgNetbarHead,
                    new ImageLoadingListenerAdapter() {
                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                            super.onLoadingComplete(s, view, bitmap);
                            llNetbarHeader.setBackgroundDrawable(new BitmapDrawable(getResources(), BitmapUtil.fastblur(bitmap, 12)));
                            navBg = new BitmapDrawable(getResources(), getBitmapBg());
                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                            super.onLoadingFailed(s, view, failReason);
                            Bitmap bitmap = ((BitmapDrawable) imgNetbarHead.getBackground()).getBitmap();
                            //llNetbarHeader.setBackgroundDrawable(new BitmapDrawable(getResources(), BitmapUtil.fastblur(bitmap, 12)));
                            navBg = new BitmapDrawable(getResources(), getBitmapBg());
                        }
                    });

            scrollView.setScrollchangeListener(new FullScrollView.OnScrollChangeListener() {
                @Override
                public void scrollChange(int l, int t, int oldl, int oldt) {
                    if (t >= showHeight) {
                        tvNavNetbarname.setVisibility(View.VISIBLE);
                        llNetbarHidden.setVisibility(View.VISIBLE);
                        if (once) {
                            llNetbarHeaderNav.setBackgroundDrawable(navBg);
                            once = false;
                        }
                        llNetbarTab.setVisibility(View.INVISIBLE);
                        llNetbarHeaderPay.setVisibility(View.INVISIBLE);
                        if (!show) {
                            playAnimShow();
                            show = true;
                        }
                    } else {
                        llNetbarHeaderPay.setVisibility(View.VISIBLE);
                        llNetbarHidden.setVisibility(View.GONE);
                        tvNavNetbarname.setVisibility(View.GONE);
                        if (!once) {
                            llNetbarHeaderNav.setBackgroundDrawable(null);
                            once = true;
                        }
                        llNetbarTab.setVisibility(View.VISIBLE);
                        if (show) {
                            show = false;
                        }
                    }

                    if (t != oldt) {//判断是scrollView在滑动
                        int scrollY = scrollView.getScrollY();
                        int height = scrollView.getHeight();
                        int scrollViewMeasuredHeight = scrollView.getChildAt(0).getMeasuredHeight();
                        if ((scrollY + height) == scrollViewMeasuredHeight) {//滑到底部
                            if (fragmentId == 2) {
                                MyBaseFragment myBaseFragment = (MyBaseFragment) fragmentList.get(2);
                                myBaseFragment.refreView();
                            }
                        }
                    }

                }
            });
            setPayWord();
            imgFavorite.setImageResource((mNetbarInfo.getFaved() == 0 ? R.drawable.icon_netbar_favor
                    : R.drawable.icon_netbar_favored));
        }

        llNetbarInfo.setOnClickListener(this);
        llNetbarActivity.setOnClickListener(this);
        llNetbarEvaluate.setOnClickListener(this);
        showHeight = llNetbarHeader.getHeight() + llNetbarTab.getHeight() - hidden.getHeight();
        llNetbarHidden.setVisibility(View.GONE);
        setPayPosition();
        addTags();

    }

    private boolean once = true;


    /**
     * 设置按钮的文字
     */
    private void setPayWord() {
        tvPayWord.setText(mNetbarInfo.getPay_word());
        if (mNetbarInfo.getLevels() == 0) {
            tvPayWord.setText("暂不支持在线支付");
        }
        tvNetbarNavPay.setText(mNetbarInfo.getPay_word());
        if (!TextUtils.isEmpty(mNetbarInfo.getPay_tip())) {
            tvDiscountTime.setText(mNetbarInfo.getPay_tip());
            tvNetbarNavDisount.setText(mNetbarInfo.getPay_tip());
        } else {
            if (mNetbarInfo.getRemain_time_to_start() != 0) {
                String payTipStr = TimeUtil.calculateTime(mNetbarInfo.getRemain_time_to_start()) + "后开启优惠";
                timeMills = mNetbarInfo.getRemain_time_to_start();
                timer.schedule(timerTask, 1000, 1000);
                discountStr = "后开启优惠";

                tvDiscountTime.setText(payTipStr);
                tvNetbarNavDisount.setText(payTipStr);
            } else if (mNetbarInfo.getRemain_time_to_end() != 0) {
                String payTipStr = TimeUtil.calculateTime(mNetbarInfo.getRemain_time_to_end()) + "后优惠结束";

                timeMills = mNetbarInfo.getRemain_time_to_end();
                timer.schedule(timerTask, 1000, 1000);

                discountStr = "后优惠结束";
                tvDiscountTime.setText(payTipStr);
                tvNetbarNavDisount.setText(payTipStr);
            } else if (mNetbarInfo.getAgain_get_time() != 0) {
                String payTipStr = TimeUtil.calculateTime(mNetbarInfo.getAgain_get_time()) + "后再次领取";
                tvDiscountTime.setText(payTipStr);
                tvNetbarNavDisount.setText(payTipStr);
                timeMills = mNetbarInfo.getAgain_get_time();
                timer.schedule(timerTask, 1000, 1000);
                discountStr = "后再次领取";
            }
        }
        if (TextUtils.isEmpty(tvDiscountTime.getText())) {
            tvDiscountTime.setVisibility(View.GONE);
            tvNetbarNavDisount.setText("暂无优惠活动");
        } else {
            tvDiscountTime.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 设置会员支付、充值 位置
     */
    private void setPayPosition() {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) llNetbarHeaderPay.getLayoutParams();
        int height = llNetbarHeaderPay.getHeight();
        int headerHeight = llNetbarHeader.getHeight();
        params.topMargin = headerHeight - height / 2 + Utils.dp2px(4);
    }

    /**
     * 点击事件
     */
    private void setListener() {
        imgNetbarHead.setOnClickListener(this);
        getLeftBtn().setOnClickListener(this);

        llNetbarActivityTab.setOnClickListener(this);
        llNetbarInfoTab.setOnClickListener(this);
        llNetbarEvaluateTab.setOnClickListener(this);

        imgShare.setOnClickListener(this);
        imgFavorite.setOnClickListener(this);

        tvNetbarNavPay.setOnClickListener(this);
        llNetbarHeaderPay.setOnClickListener(this);

    }

    /**
     * 截取网吧头部部分背景
     */
    private BitmapDrawable navBg;

    private Bitmap getBitmapBg() {
        Bitmap bitmap = ((BitmapDrawable) llNetbarHeader.getBackground()).getBitmap();
        int width = llNetbarHeaderNav.getMeasuredWidth();
        int height = llNetbarHeaderNav.getMeasuredHeight();
        float scale = (float) bitmap.getWidth() / width;
        int bgHeight = (int) (height * scale + 0.5);
        Bitmap bg = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() - bgHeight, bitmap.getWidth(), bgHeight);
        return bg;
    }

    /**
     * 过度动画 显示
     */
    private void playAnimShow() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(llNetbarNavPay, "y", llNetbarNavPay.getTop() - llNetbarNavPay.getHeight()
                , 0);
        anim.start();
        show = true;
    }

    /**
     * 收藏网吧
     */
    private void favorNetbar(boolean isFavor) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("userId", WangYuApplication.getUser(this).getId());
        params.put("token", WangYuApplication.getUser(this).getToken());
        params.put("netbarId", netbarId);
        if (isFavor) {
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.NETBAR_FAVOR, params, HttpConstant.NETBAR_FAVOR);
        } else {
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.NETBAR_UNFAVOR, params, HttpConstant.NETBAR_UNFAVOR);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        shareToFriendsUtil.getIWeiApiInstance(this).handleWeiboResponse(intent, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (shareToFriendsUtil != null && shareToFriendsUtil.getmSsoHandler() != null) {
//            shareToFriendsUtil.getmSsoHandler().authorizeCallBack(requestCode, resultCode, data);
//        }
        if (shareToFriendsUtil != null && shareToFriendsUtil.getmTencent(this) != null)
            shareToFriendsUtil.getmTencent(this).onActivityResult(requestCode, resultCode, data);
        if (resultCode == LoginActivity.LOGIN_OK) {
            loadNetbarInfo();
        }
    }

    /**
     * 接收微客户端博请求的数据。 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     *
     * @param baseResp 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                Toast.makeText(this, R.string.errcode_success, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Toast.makeText(this, R.string.errcode_cancel, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Toast.makeText(this, getResources().getString(R.string.errcode_deny) + "Error Message: " + baseResp.errMsg,
                        Toast.LENGTH_LONG).show();
                break;
        }
    }


    View.OnClickListener itemOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String sharetitle = "";
            String sharecontent;
            int level = mNetbarInfo.getLevels();
            //.会员网吧/非会员网吧
            if (level == 0 || level == 1) {
                sharetitle = "网费一键支付-" + mNetbarInfo.getName();

            }//黄金网吧
            else if (level == 2) {
                sharetitle = "黄金网吧尊享特权-" + mNetbarInfo.getName();
            }
            sharecontent = "网吧地址:" + mNetbarInfo.getAddress();
            String shareurl = HttpConstant.SERVICE_HTTP_AREA
                    + HttpConstant.NETBAR_URL + netbarId;
            String imgurl = HttpConstant.SERVICE_UPLOAD_AREA
                    + mNetbarInfo.getIcon();
            switch (v.getId()) {
                case R.id.llSina:
                    shareToFriendsUtil.shareBySina(sharetitle, sharecontent, shareurl, imgurl);
                    break;
                case R.id.llWeChat:
                    shareToFriendsUtil.shareWyByWXFriend(sharetitle, sharecontent, shareurl, imgurl, 0);
                    break;
                case R.id.llFriend:
                    shareToFriendsUtil.shareWyByWXFriend(sharetitle, sharecontent, shareurl, imgurl, 1);
                    break;
                case R.id.llQQ:
                    shareToFriendsUtil.shareByQQ(sharetitle, sharecontent, shareurl, imgurl);
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        if (shareToFriendsUtil != null) {
            if (shareToFriendsUtil.requestUtil != null) {
                shareToFriendsUtil.requestUtil.removeTag(shareToFriendsUtil.getClass().getName());
            }
            shareToFriendsUtil.requestUtil = null;
            shareToFriendsUtil = null;
        }
        observerable = null;
        timeMills = 0;
        timerTask.cancel();
        timer.cancel();
        timerTask = null;
        timer = null;
        shareToFriendsUtil = null;
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
}
