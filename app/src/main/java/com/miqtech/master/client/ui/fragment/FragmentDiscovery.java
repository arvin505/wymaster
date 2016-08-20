package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.LivePlayAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.Banner;
import com.miqtech.master.client.entity.DiscoveryInfo;
import com.miqtech.master.client.entity.InternetBarInfo;
import com.miqtech.master.client.entity.LiveInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.GoldCoinsStoreActivity;
import com.miqtech.master.client.ui.InternetBarActivityV2;
import com.miqtech.master.client.ui.LivePlayAndVideoListActivity;
import com.miqtech.master.client.ui.NetbarListV2Activity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.FullyGridLayoutManager;
import com.miqtech.master.client.view.HeadLinesView;
import com.miqtech.master.client.view.MyScrollView;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/8/11.
 */
public class FragmentDiscovery extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.discoveryNearbyNetbar)
    View discoveryNearbyNetbar;//附近网吧
    @Bind(R.id.discoveryGoldCoinShop)
    View discoveryGoldCoinShop;//金币商城
    @Bind(R.id.discoveryLivePlay)
    View discoveryLivePlay;// 直播平台
    @Bind(R.id.discoveryRecommend)
    HeadLinesView discoveryRecommend;
    @Bind(R.id.tvLeftTitle)
    TextView tvLeftTitle;
    @Bind(R.id.netBarItem)
    View netBarItem;
    @Bind(R.id.rvContent)
    RecyclerView rvContent;
    @Bind(R.id.spliteLine)
    View spliteLine; //分割线
    @Bind(R.id.spliteLine2)
    View spliteLine2;
    @Bind(R.id.discoveryScrollview)
    PullToRefreshScrollView discoveryScrollview;
    private ScrollView mScrollView;

    private ImageView img_netbar_icon;//网吧icon
    private TextView tv_netbar_name;//网吧名字
    private TextView tv_netbar_loction;//网吧位置
    private ImageView img_bar_hot;
    private ImageView img_bar_j;
    private ImageView img_bar_z;
    private TextView tv_netbar_distance;
    private TextView tv_netbar_hourprice;
    private ImageView iv_is_benefit;
    private View bottomline;

    private Context context;
    private int[] iconResIDs;
    private String[] itemTitles;
    private String[] itemHints;
    private List<Banner> banners = new ArrayList<Banner>();
    private DiscoveryInfo discoveryInfo; //发现数据
    private int liveNum; //直播数量
    private List<LiveInfo> liveDatas = new ArrayList<LiveInfo>(); //直播列表数据
    private List<LiveInfo> videoDatas = new ArrayList<LiveInfo>();//视频数据
    private LivePlayAdapter adapter;
    private FullyGridLayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discovery, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initTitleBar();
        initData();
        initView();
        loadDiscoveryData();
    }

    @Override
    public void onResume() {
        super.onResume();
        discoveryRecommend.startAutoScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
        discoveryRecommend.stopAutoScroll();
    }

    /**
     * 设置发现标题
     */
    private void initTitleBar() {
        tvLeftTitle.setText(context.getResources().getString(R.string.main_bar_find));
    }

    /**
     * 初始化
     */
    private void initData() {
        iconResIDs = new int[]{R.drawable.live_play_nearly_netbar, R.drawable.live_play_goldcoin_shop, R.drawable.live_play_liveplay};
        itemTitles = getResources().getStringArray(R.array.discovery_item_tilte);
        itemHints = getResources().getStringArray(R.array.discovery_item_hint);
    }

    /**
     * 初始化item数据
     */
    private void initView() {
        mScrollView = discoveryScrollview.getRefreshableView();

        discoveryScrollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MyScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<MyScrollView> refreshView) {
                loadDiscoveryData();
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getActivity().getResources().getString(R.string.noNeteork));
                }
            }
        });
        layoutManager = new FullyGridLayoutManager(context, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case LivePlayAdapter.VIEW_LIVE_ITEM:
                        return 1;
                    case LivePlayAdapter.VIEW_EMPTY:
                        return 0;
                    default:
                        return -1;
                }
            }
        });
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        rvContent.setLayoutManager(layoutManager);
        adapter = new LivePlayAdapter(context, 1, liveDatas, videoDatas);

        img_netbar_icon = (ImageView) netBarItem.findViewById(R.id.img_netbar_icon);
        tv_netbar_name = (TextView) netBarItem.findViewById(R.id.tv_netbar_name);
        tv_netbar_loction = (TextView) netBarItem.findViewById(R.id.tv_netbar_loction);
        img_bar_hot = (ImageView) netBarItem.findViewById(R.id.img_bar_hot);
        img_bar_j = (ImageView) netBarItem.findViewById(R.id.img_bar_j);
        img_bar_z = (ImageView) netBarItem.findViewById(R.id.img_bar_z);
        tv_netbar_distance = (TextView) netBarItem.findViewById(R.id.tv_netbar_distance);
        tv_netbar_hourprice = (TextView) netBarItem.findViewById(R.id.tv_netbar_hourprice);
        iv_is_benefit = (ImageView) netBarItem.findViewById(R.id.iv_is_benefit);
        bottomline = netBarItem.findViewById(R.id.bottomline);

        netBarItem.setOnClickListener(this);
        rvContent.setFocusable(false);

        initItem(discoveryNearbyNetbar, 0);
        initItem(discoveryGoldCoinShop, 1);
        initItem(discoveryLivePlay, 2);

    }

    private void setNetBarItemData(InternetBarInfo internetBarInfo) {
        tv_netbar_name.setText(internetBarInfo.getNetbar_name());
        tv_netbar_loction.setText(internetBarInfo.getAddress());
        tv_netbar_hourprice.setText(this.getResources().getString(R.string.price_per_hour, internetBarInfo.getPrice_per_hour()));
        if (internetBarInfo.getIs_hot() == 1) {//热：符合热度值算法的
            img_bar_hot.setVisibility(View.VISIBLE);
        } else {
            img_bar_hot.setVisibility(View.GONE);
        }
        if (internetBarInfo.getIs_activity() == 1) {//活动：网吧当前有正在进行中的通过资源商城购买的服务
            img_bar_j.setVisibility(View.VISIBLE);
        } else {
            img_bar_j.setVisibility(View.GONE);
        }
        if (internetBarInfo.getIs_order() == 1) {//是否显示“支”
            img_bar_z.setVisibility(View.VISIBLE);
        } else {
            img_bar_z.setVisibility(View.GONE);
        }
        if (internetBarInfo.getIs_benefit() == 1) {//是否显示“优惠”
            iv_is_benefit.setVisibility(View.VISIBLE);
        } else {
            iv_is_benefit.setVisibility(View.INVISIBLE);
        }
        String distance = internetBarInfo.getDistance();
        if (TextUtils.isEmpty(distance) || distance.equals("null")) {
            distance = "0.0m";
        } else {
            distance = Utils.DisConversion(Double.valueOf(distance));
        }
        tv_netbar_distance.setText(distance);
        bottomline.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(internetBarInfo.getIcon())) {//避免在加载更多时出现闪一下的现状
            AsyncImage.loadNetPhoto(getActivity(), HttpConstant.SERVICE_UPLOAD_AREA + internetBarInfo.getIcon(), img_netbar_icon);
        }
    }

    private void initItem(View view, int position) {
        view.setOnClickListener(FragmentDiscovery.this);
        ImageView mLeftIcon = (ImageView) view.findViewById(R.id.ivLeftIcon);
        mLeftIcon.setImageResource(iconResIDs[position]);
        TextView mItemTitle = (TextView) view.findViewById(R.id.tvTitle);
        mItemTitle.setText(itemTitles[position]);
        TextView hint = (TextView) view.findViewById(R.id.tvHint);
        if(position==1){
            hint.setVisibility(View.GONE);
        }
        if (position == 2) {
            setFontDiffrentColor(getString(R.string.live_play_hint, liveNum), 0, (liveNum + "").length(), hint);
        } else {
            hint.setText(itemHints[position]);
        }
    }

    private void setFontDiffrentColor(String content, int start, int end, TextView tv) {
        if (tv == null) {
            return;
        }
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.orange)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(style);
    }

    /**
     * 发现数据
     */
    private void loadDiscoveryData() {
        Map<String, String> params = new HashMap<>();
        if (Constant.isLocation) {
            params.put("areaCode", Constant.currentCity.getAreaCode());
            params.put("latitude", Constant.latitude + "");
            params.put("longitude", Constant.longitude + "");
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DISCOVERY_DETAIL, params, HttpConstant.DISCOVERY_DETAIL);
    }

    /**
     * banner接口请求
     */
    private void loadBannerData() {
        Map<String, String> params = new HashMap<>();
        if (Constant.isLocation) {
            params.put("area_code", Constant.currentCity.getAreaCode());
        }

        User user = WangYuApplication.getUser(getContext());
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
        }
        params.put("belong", 2 + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AD, params, HttpConstant.AD);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        discoveryScrollview.onRefreshComplete();
        hideLoading();
        if (HttpConstant.AD.equals(method)) {
            initAdView(initAData(object));
            mScrollView.smoothScrollTo(0,20);
        } else if (HttpConstant.DISCOVERY_DETAIL.equals(method)) {
            initDiscoveryData(object);
            loadBannerData();
        }

    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        discoveryScrollview.onRefreshComplete();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        discoveryScrollview.onRefreshComplete();
    }

    /**
     * 发现接口数据
     */
    private void initDiscoveryData(JSONObject object) {
        try {
            if (object.getInt("code") == 0 && object.has("object")) {
                discoveryInfo = GsonUtil.getBean(object.getJSONObject("object").toString(), DiscoveryInfo.class);
                if (discoveryInfo != null) {
                    liveNum = discoveryInfo.getLiveNum();
                    initItem(discoveryLivePlay, 2);
                    if (discoveryInfo.getNetbar() != null) {
                        netBarItem.setVisibility(View.VISIBLE);
                        spliteLine.setVisibility(View.VISIBLE);
                        spliteLine2.setVisibility(View.VISIBLE);
                        setNetBarItemData(discoveryInfo.getNetbar());
                    } else {
                        spliteLine.setVisibility(View.GONE);
                        netBarItem.setVisibility(View.GONE);
                        spliteLine2.setVisibility(View.GONE);
                    }
                    if (discoveryInfo.getLiveList() != null && !discoveryInfo.getLiveList().isEmpty()) {
                        rvContent.setVisibility(View.VISIBLE);
                        liveDatas.clear();
                        liveDatas.addAll(discoveryInfo.getLiveList());
                        rvContent.setAdapter(adapter);
                    } else {
                        liveDatas.clear();
                        rvContent.setVisibility(View.GONE);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 广告接口数据
     */
    private List<Banner> initAData(JSONObject object) {

        try {
            if (object.getInt("code") == 0 && object.has("object")) {
                JSONArray addArr = object.getJSONObject("object").getJSONArray("banner");
                banners = GsonUtil.getList(addArr.toString(), Banner.class);
                return banners;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initAdView(List<Banner> data) {
        discoveryRecommend.refreshData(data);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.discoveryNearbyNetbar:
                intent = new Intent(getContext(), NetbarListV2Activity.class);
                getContext().startActivity(intent);
                break;
            case R.id.discoveryGoldCoinShop:
                intent = new Intent();
                intent.setClass(getContext(), GoldCoinsStoreActivity.class);
                startActivity(intent);
                break;
            case R.id.discoveryLivePlay:
                intent = new Intent();
                intent.setClass(getActivity(), LivePlayAndVideoListActivity.class);
                startActivity(intent);
                break;
            case R.id.netBarItem:
                intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("netbarId", discoveryInfo.getNetbar().getId());
                intent.setClass(getActivity(), InternetBarActivityV2.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
