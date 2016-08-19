package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.Banner;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.GoldCoinsStoreActivity;
import com.miqtech.master.client.ui.LivePlayAndVideoListActivity;
import com.miqtech.master.client.ui.NetbarListV2Activity;
import com.miqtech.master.client.ui.PLMediaPlayerActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.ACache;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.HeadLinesView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/7/23.
 */
public class DiscoveryFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.discoveryNearbyNetbar)
    View discoveryNearbyNetbar;//附近网吧
    @Bind(R.id.discoveryGoldCoinShop)
    View discoveryGoldCoinShop;//金币商城
    @Bind(R.id.discoveryLivePlay)
    View discoveryLivePlay;// 直播平台
    @Bind(R.id.discoveryRecommend)
    HeadLinesView discoveryRecommend;
    private Context context;
    private int[] iconResIDs;
    private String[] itemTitles;
    private String[] itemHints;
    private List<Banner> banners = new ArrayList<Banner>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        mCache = ACache.get(activity);
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
        loadBannerData();
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
    private void initTitleBar(){

    }

    /**
     * 初始化
     */
    private void initData(){
        iconResIDs = new int[] { R.drawable.live_play_nearly_netbar, R.drawable.live_play_goldcoin_shop, R.drawable.live_play_liveplay,
               };
        itemTitles = getResources().getStringArray(R.array.discovery_item_tilte);
        itemHints=getResources().getStringArray(R.array.discovery_item_hint);
    }

    /**
     * 初始化item数据
     */
    private void initView(){
        initItem(discoveryNearbyNetbar,0);
        initItem(discoveryGoldCoinShop,1);
        initItem(discoveryLivePlay,2);

    }

    private void initItem(View view, int position) {
        view.setOnClickListener(DiscoveryFragment.this);
        ImageView mLeftIcon = (ImageView) view.findViewById(R.id.ivLeftIcon);
        mLeftIcon.setImageResource(iconResIDs[position]);
        TextView mItemTitle = (TextView) view.findViewById(R.id.tvTitle);
        mItemTitle.setText(itemTitles[position]);
        TextView hint=(TextView)view.findViewById(R.id.tvHint);
        if(position==2) {
            //TODO 设置不同的颜色
            hint.setText(itemHints[position]);
        }else{
            hint.setText(itemHints[position]);
        }
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
        params.put("belong", "0");
        LogUtil.e(TAG, "params : " + params.toString());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AD, params, HttpConstant.AD);
    }
    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        LogUtil.e("FragmentRecommend", "return : " + object.toString());
    if (HttpConstant.AD.equals(method)) {
            initAdView(initAData(object));
            mCache.put(HttpConstant.AD + TAG, object);
        }
        hideLoading();
    }
    /**
     * 广告接口数据
     */
    private List<Banner> initAData(JSONObject object) {

        try {
            if (object.getInt("code") == 0 && object.has("object")) {
                JSONArray addArr = object.getJSONArray("object");
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
        Intent intent=null;
     switch (v.getId()){
         case R.id.discoveryNearbyNetbar:
             //TODO 跳转到网吧页面
             intent = new Intent(getContext(), NetbarListV2Activity.class);
             getContext().startActivity(intent);
             break;
         case R.id.discoveryGoldCoinShop:
             //TODO 跳转到金币商城页面
             intent = new Intent();
             intent.setClass(getContext(), GoldCoinsStoreActivity.class);
             startActivity(intent);
             break;
         case R.id.discoveryLivePlay:
             //TODO 跳转到直播平台页面
             intent=new Intent();
             intent.setClass(getActivity(), LivePlayAndVideoListActivity.class);
             startActivity(intent);
             break;
     }
    }
}
