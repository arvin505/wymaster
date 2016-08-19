package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.Mid1VpAdapter;
import com.miqtech.master.client.adapter.RecommendBattleAdapter;
import com.miqtech.master.client.adapter.RecommendMatchAdapter;
import com.miqtech.master.client.adapter.RecommendNetbarV2Adapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.Banner;
import com.miqtech.master.client.entity.EntryInfo;
import com.miqtech.master.client.entity.InternetBarInfo;
import com.miqtech.master.client.entity.Mid1Info;
import com.miqtech.master.client.entity.Mid2Info;
import com.miqtech.master.client.entity.RecommendInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.CompositiveMatchActivity;
import com.miqtech.master.client.ui.DownloadWebView;
import com.miqtech.master.client.ui.EventDetailActivity;
import com.miqtech.master.client.ui.GoldCoinsStoreActivity;
import com.miqtech.master.client.ui.InternetBarActivityV2;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.MainActivity;

import com.miqtech.master.client.ui.NetbarListV2Activity;
import com.miqtech.master.client.ui.OfficalEventActivity;
import com.miqtech.master.client.ui.RecreationMatchDetailsActivity;
import com.miqtech.master.client.ui.SubjectActivity;
import com.miqtech.master.client.ui.YueZhanDetailsActivity;
import com.miqtech.master.client.ui.YueZhanListActivity;
import com.miqtech.master.client.ui.ZifaMatchListActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.ACache;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.ImageUtils;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.ScrollController;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CircleImageView;
import com.miqtech.master.client.view.EntrysView;
import com.miqtech.master.client.view.HeadLinesView;
import com.miqtech.master.client.view.MyGridView;
import com.miqtech.master.client.view.MyScrollView;
import com.miqtech.master.client.view.SlidingMenu;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshScrollView;
import com.miqtech.master.client.view.pullToRefresh.internal.FrameAnimationHeaderLayout;

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
 * Created by Administrator on 2015/11/19.
 */
public class FragmentRecommend extends BaseFragment implements View.OnClickListener, SlidingMenu.isAutoPlay {
    @Bind(R.id.recommend_head)
    HeadLinesView headLinesView; //轮播广告
    @Bind(R.id.entry)
    EntrysView entrysView;   //动态入口
    @Bind(R.id.battle_content)
    ListView lvHotBattle;   //热门约站列表
    @Bind(R.id.recommend_bar_content)
    ListView lvNetBar;   //推荐网吧列表
    @Bind(R.id.match_content)
    MyGridView gvHotMatch;       //热门赛事
    @Bind(R.id.wyscroll_recommend)
    PullToRefreshScrollView mRefreshScrollView;
    @Bind(R.id.mid1_img)
    ImageView ivMid1;    //腰图1
    @Bind(R.id.ll_icon_content)
    LinearLayout llMidIconContent;     //腰图头像
    @Bind(R.id.vp_mid1_match)
    ViewPager vpMidViewPager;      //腰图1下部viewpager
    @Bind(R.id.view_mid2_vp)
    ViewPager vpMid2;     //腰图2
    @Bind(R.id.mid2_indicator)
    LinearLayout llMid2Indecator;  //腰图2 indecatior
    @Bind(R.id.tv_hotmatch_more)
    TextView hotmatchMore;
    @Bind(R.id.tv_hotbattle_more)
    TextView hotbattleMore;
    @Bind(R.id.tv_netbar_more)
    TextView netbarMore;

    @Bind(R.id.tv_mid1_lable)
    TextView tvMid1Lable;

    @Bind(R.id.content_hotmatch)
    RelativeLayout llHotMatch;
    @Bind(R.id.content_mid1)
    LinearLayout llMid1;

    @Bind(R.id.rl_mid2)
    RelativeLayout rlMid2;

    Handler mHandler = new Handler();
    //缓存
    private ACache mCache;
    private final String TAG = "FragmentRecommend";
    //切换时的位置
    private int scollX = 0;

    private String[] lables = {"官方赛", "娱乐赛", "约战", "推广", "福利"};
    private Context context;
    private List<Banner> banners = new ArrayList<Banner>();
    private ScrollView mScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCache = ACache.get(getActivity());
        context = getActivity();
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommend, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //      loadCache();
        showLoading();
        ButterKnife.bind(this, view);
        setListner();
        //loadCache();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setLoadingState(mSRLayout);
//      //          loadData();
//            }
//        }, 200);
        loadData();

    }

    private void setListner() {
        mRefreshScrollView.setHeaderLayout(new FrameAnimationHeaderLayout(getActivity()));

        mRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MyScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<MyScrollView> refreshView) {
                loadData();
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getActivity().getResources().getString(R.string.noNeteork));
                }
            }
        });
        mScrollView = mRefreshScrollView.getRefreshableView();
        lvHotBattle.setFocusable(false);
        lvNetBar.setFocusable(false);

        netbarMore.setOnClickListener(this);
        hotbattleMore.setOnClickListener(this);
        hotmatchMore.setOnClickListener(this);

        //((MainActivity) context).mMenu.setOnIsAutoPlay(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        // mScrollView.scrollTo(scollX, 0);
        headLinesView.startAutoScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
        headLinesView.stopAutoScroll();
        scollX = mScrollView.getScrollX();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_hotmatch_more:
                Intent matchIntent = new Intent(getActivity(), CompositiveMatchActivity.class);
                startActivity(matchIntent);
                break;
            case R.id.tv_hotbattle_more:
                Intent battleIntent = new Intent(getActivity(), YueZhanListActivity.class);
                startActivity(battleIntent);
                break;
            case R.id.tv_netbar_more:
                Intent netbarIntent = new Intent(getActivity(), NetbarListV2Activity.class);
                startActivity(netbarIntent);
                break;
        }
    }

    /**
     * 首页接口数据
     *
     * @param object
     */
    private Map<String, Object> initIndexData(JSONObject object) {
        Map<String, Object> data = new HashMap<>();
        try {
            if (object.getInt("code") == 0 && object.has("object")) {
                JSONObject dataObject = object.getJSONObject("object");
                if (dataObject.has("entry")) {
                    JSONArray entryArr = dataObject.getJSONArray("entry");
                    List<EntryInfo> entrys = GsonUtil.getList(entryArr.toString(), EntryInfo.class);
                    data.put("entry", entrys);
                }

                if (dataObject.has("hotActivity")) {
                    JSONArray hotBattleArr = dataObject.getJSONArray("hotActivity");
                    List<RecommendInfo> battleInfos = GsonUtil.getList(hotBattleArr.toString(), RecommendInfo.class);
                    data.put("hotActivity", battleInfos);
                    llHotMatch.setVisibility(View.VISIBLE);
                } else {
                    //llHotMatch.setVisibility(View.GONE);
                }
                if (dataObject.has("mid1")) {
                    Mid1Info info = new Mid1Info();
                    RecommendInfo match = GsonUtil.getBean(dataObject.getJSONObject("mid1").getJSONObject("match").toString(), RecommendInfo.class);
                    JSONArray iconsArr = dataObject.getJSONObject("mid1").getJSONArray("icons");
                    String[] icons = new String[iconsArr.length()];
                    for (int i = 0; i < iconsArr.length(); i++) {
                        icons[i] = ((JSONObject) iconsArr.get(i)).getString("icon");
                    }
                    info.setIcons(icons);
                    info.setMatchInfo(match);
                    data.put("mid1", info);
                    llMid1.setVisibility(View.VISIBLE);
                } else {
                    // llMid1.setVisibility(View.GONE);
                }

                if (dataObject.has("hotBattle")) {
                    JSONArray hotBattleArr = dataObject.getJSONArray("hotBattle");
                    List<RecommendInfo> battleInfos = GsonUtil.getList(hotBattleArr.toString(), RecommendInfo.class);
                    data.put("hotBattle", battleInfos);
                }


                if (dataObject.has("mid2")) {
                    Mid2Info mid2Info = GsonUtil.getBean(dataObject.getJSONObject("mid2").toString(), Mid2Info.class);
                    List<Mid2Info> mid2Infos = new ArrayList<>();
                    mid2Infos.add(mid2Info);
                    data.put("mid2", mid2Infos);
                    rlMid2.setVisibility(View.VISIBLE);
                } else {
                    //rlMid2.setVisibility(View.GONE);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private void initIndexView(Map<String, Object> data) {
        for (final Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getKey().equals("entry")) {
                entrysView.setData((List<EntryInfo>) entry.getValue());
                entrysView.setOnItemClickListner(new EntrysView.OnItemClickListner() {
                    @Override
                    public void onItemClick(View view, int position, ImageView imageView) {
                        EntryInfo info = ((List<EntryInfo>) entry.getValue()).get(position);
                        switch (info.getType()) {
                            case 1:
                                Intent intent = new Intent(getContext(), NetbarListV2Activity.class);
                                getContext().startActivity(intent);
                                break;
                            case 2:
                               ((MainActivity) getActivity()).setSelectItem(2);
                                break;
                            case 3:
                                intent = new Intent();
                                intent.setClass(getContext(), GoldCoinsStoreActivity.class);
                                startActivity(intent);
                                break;
                            case 4:
                                intent = new Intent();
                                if (WangYuApplication.getUser(getContext()) != null) {
                                    intent.setClass(getContext(), SubjectActivity.class);
                                    intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.REDBAG);
                                    getContext().startActivity(intent);
                                } else {
                                    showToast("请登录");
                                    intent.setClass(getContext(), LoginActivity.class);
                                    startActivity(intent);
                                }
                                break;
                            case 5:
                                intent = new Intent(getContext(), ZifaMatchListActivity.class);
                                startActivity(intent);
                                if (PreferencesUtil.getIsFirstShowHot(context)) {
                                    imageView.setVisibility(View.GONE);
                                    PreferencesUtil.setIsFirstShowHot(context, false);
                                }
                                break;
                        }
                    }
                });
            }

            if (entry.getKey().equals("hotActivity")) {
                final RecommendMatchAdapter adapter = new RecommendMatchAdapter(gvHotMatch, getContext(), (List<RecommendInfo>) entry.getValue());
                gvHotMatch.setAdapter(adapter);
                gvHotMatch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        RecommendInfo match = (RecommendInfo) adapter.getItem(position);
                        Intent intent;
                        switch (match.getType()) {
                            case 1: //10官方比赛
                                intent = new Intent();
                                intent.setClass(getContext(), OfficalEventActivity.class);
                                intent.putExtra("matchId", match.getId() + "");
                                LogUtil.e("tag", "time0 " + System.currentTimeMillis());
                                startActivity(intent);
                                LogUtil.e("tag", "time1 " + System.currentTimeMillis());
                                break;
                            case 2:  //11娱乐赛
                                intent = new Intent();
                                intent.setClass(getContext(), RecreationMatchDetailsActivity.class);
                                intent.putExtra("id", match.getId() + "");
                                startActivity(intent);
                                break;
                        }
                    }
                });
            }
            if (entry.getKey().equals("mid1")) {
                mid1((Mid1Info) entry.getValue());
            }

            if (entry.getKey().equals("hotBattle")) {
                RecommendBattleAdapter adapter = new RecommendBattleAdapter(getContext(), (List<RecommendInfo>) entry.getValue());
                lvHotBattle.setAdapter(adapter);
                fixListViewHeight(lvHotBattle);
            }

            if (entry.getKey().equals("mid2")) {
                mid2((List<Mid2Info>) entry.getValue());
            }
        }
    }

    public void loadData() {
        loadIndexData();
        loadBannerData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadNetbarData();
            }
        }, 1400);
    }

    /**
     * 主页接口请求
     */
    private void loadIndexData() {
        Map<String, String> params = new HashMap<>();
        if (Constant.isLocation) {
            params.put("areaCode", Constant.currentCity.getAreaCode());
        } else {
            String aeraCode = PreferencesUtil.getAeraCode(getContext());
            if (!TextUtils.isEmpty(aeraCode)) {
                params.put("areaCode", aeraCode);
            }
        }
        LogUtil.e("params", "index params : " + params.toString());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INDEX, params, HttpConstant.INDEX);
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

    /**
     * netbar 接口请求
     */
    private void loadNetbarData() {
        Map<String, String> params = new HashMap<>();
        if (Constant.isLocation) {
            params.put("areaCode", Constant.currentCity.getAreaCode());
            params.put("latitude", Constant.latitude + "");
            params.put("longitude", Constant.longitude + "");
        } else {
            String aeraCode = PreferencesUtil.getAeraCode(getContext());
            if (!TextUtils.isEmpty(aeraCode)) {
                params.put("areaCode", aeraCode);
            }
            String[] ll = PreferencesUtil.getLatitude$Longitude(context);
            if (ll != null) {
                params.put("latitude", ll[0] + "");
                params.put("longitude", ll[1] + "");
            }
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.NETBAR_RECOMMEND, params, HttpConstant.NETBAR_RECOMMEND);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        LogUtil.e("FragmentRecommend", "return : " + object.toString());
        if (HttpConstant.INDEX.equals(method)) {
            initIndexView(initIndexData(object));
            mCache.put(HttpConstant.INDEX, object);
        } else if (HttpConstant.NETBAR_RECOMMEND.equals(method)) {
            try {
                if (object.getInt("code") == 0 && object.has("object")) {
                    JSONArray netbarArr = object.getJSONArray("object");
                    List<InternetBarInfo> netbars = GsonUtil.getList(netbarArr.toString(), InternetBarInfo.class);
                    initNetBarView(netbars);
                    mCache.put(HttpConstant.NETBAR_RECOMMEND, object);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (HttpConstant.AD.equals(method)) {
            initAdView(initAData(object));
            mCache.put(HttpConstant.AD + TAG, object);
        }
        mRefreshScrollView.onRefreshComplete();
        hideLoading();
    }

    /**
     * 加载缓存
     */
    private void loadCache() {
        LoadCacheTask cacheTask = new LoadCacheTask();
        cacheTask.execute();
    }

    class LoadCacheTask extends AsyncTask<Void, Void, Map<String, JSONObject>> {

        private Map<String, Object> indexData;
        private List<InternetBarInfo> netbarData;
        private List<Banner> adData;

        @Override
        protected Map<String, JSONObject> doInBackground(Void... params) {

            JSONObject adCache = mCache.getAsJSONObject(HttpConstant.AD + TAG);
            if (adCache != null && !adCache.isNull("object")) {
                adData = initAData(adCache);
            }
            JSONObject indexCache = mCache.getAsJSONObject(HttpConstant.INDEX);
            if (indexCache != null && !indexCache.isNull("object")) {
                indexData = initIndexData(indexCache);
            }

            JSONObject netbarCache = mCache.getAsJSONObject(HttpConstant.NETBAR_RECOMMEND);
            if (netbarCache != null && !netbarCache.isNull("object")) {
                try {
                    netbarData = GsonUtil.getList(netbarCache.getJSONArray("object").toString(), InternetBarInfo.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(Map<String, JSONObject> map) {
            if (indexData != null && !indexData.isEmpty()) {
                initIndexView(indexData);
            }
            if (adData != null && !adData.isEmpty()) {
                initAdView(adData);
            }
            if (netbarData != null && !netbarData.isEmpty()) {
                initNetBarView(netbarData);
            }
        }
    }

    private boolean showCache = true;

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        mRefreshScrollView.onRefreshComplete();
        if (showCache) {
            loadCache();
            showCache = false;
        }
    }

    /**
     * 网吧接口数据
     */
    private void initNetBarView(final List<InternetBarInfo> list) {
        try {
            if (list != null && !list.isEmpty()) {
                RecommendNetbarV2Adapter adapter = new RecommendNetbarV2Adapter(getContext(), list, 0);
                lvNetBar.setAdapter(adapter);
                lvNetBar.setOverScrollMode(2);
                lvNetBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        InternetBarInfo netbar = list.get(position);
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("netbarId", netbar.getId());
                        intent.setClass(getContext(), InternetBarActivityV2.class);
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(0, 0);
                    }
                });
                fixListViewHeight(lvNetBar);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        headLinesView.refreshData(data);
    }


    /**
     * 腰图1
     */
    private void mid1(final Mid1Info midInfo) {
        LogUtil.e("midinfo", "midinfo == " + midInfo.toString());
        if (midInfo == null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vpMidViewPager.getLayoutParams();
            params.height = 0;
            vpMidViewPager.setLayoutParams(params);
            return;
        } else {
            vpMidViewPager.setVisibility(View.VISIBLE);
            ivMid1.setVisibility(View.VISIBLE);
        }
        llMidIconContent.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dp2px(52), Utils.dp2px(42));

        //foreach add icon

        for (int i = 0; i < midInfo.getIcons().length; i++) {
            CircleImageView icon = new CircleImageView(getContext());
            icon.setLayoutParams(params);
            icon.setPadding(Utils.dp2px(2), 0, Utils.dp2px(2), 0);
            icon.setBorderColor(Color.WHITE);
            icon.setBorderWidth(Utils.dp2px(3));
            AsyncImage.loadAvatar(getContext(), HttpConstant.SERVICE_UPLOAD_AREA + midInfo.getIcons()[i], icon);
            llMidIconContent.addView(icon);
            /*icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(getContext(), "icon click", 0).show();
                }
            });*/
        }
        AsyncImage.loadNetPhoto(getContext(), HttpConstant.SERVICE_UPLOAD_AREA + midInfo.getMatchInfo().getIcon(), ivMid1);

        if (!TextUtils.isEmpty(midInfo.getMatchInfo().getType() + "")) {
            tvMid1Lable.setText(lables[0]);
        }
        //tvMid1Lable.setVisibility(View.GONE);
        PagerAdapter adapter = getMid1Adapter(midInfo.getMatchInfo());
        ivMid1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                LogUtil.e("data", midInfo.getMatchInfo().toString());
                switch (midInfo.getMatchInfo().getType()) {
                    case 1: //10官方比赛
                        intent = new Intent();
                        intent.setClass(getContext(), OfficalEventActivity.class);
                        intent.putExtra("matchId", midInfo.getMatchInfo().getId() + "");
                        startActivity(intent);
                        break;
                    case 2:  //11娱乐赛
                        intent = new Intent();
                        intent.setClass(getContext(), RecreationMatchDetailsActivity.class);
                        intent.putExtra("id", midInfo.getMatchInfo().getId() + "");
                        startActivity(intent);
                        break;
                    case 3:  //12约战
                        intent = new Intent(getContext(), YueZhanDetailsActivity.class);
                        intent.putExtra("id", midInfo.getMatchInfo().getId() + "");
                        startActivity(intent);
                        break;
                }
            }
        });
        vpMidViewPager.setAdapter(adapter);
        ScrollController.addViewPager(getClass().getSimpleName() + "_mid1", vpMidViewPager);

    }

    /**
     * mid1 腰图 adapter
     */
    private PagerAdapter getMid1Adapter(RecommendInfo info) {
        List<View> viewList = new ArrayList<>();
        View item1 = LayoutInflater.from(getContext()).inflate(R.layout.view_mid1_vp_item1, null);
        TextView tvTitle = (TextView) item1.findViewById(R.id.tv_mid1_item1_title);
        TextView tvCount = (TextView) item1.findViewById(R.id.tv_mid1_item1_count);
        TextView tvStartTime = (TextView) item1.findViewById(R.id.tv_mid1_item1_starttime);

        tvTitle.setText(info.getTitle());
        tvCount.setText(info.getApplyNum());
        tvStartTime.setText(TimeFormatUtil.formatNoTime(info.getStart_time()));
        viewList.add(item1);

        return new Mid1VpAdapter(viewList);
    }

    /**
     * mid2 腰图
     */
    private void mid2(List<Mid2Info> mid2Infos) {

        List<View> viewList = new ArrayList<>();
        int height = 0;
        for (final Mid2Info mid2Info : mid2Infos) {
            View viewItem = LayoutInflater.from(getContext()).inflate(R.layout.view_mid2_vp_item, null);
            ImageView imageView = (ImageView) viewItem.findViewById(R.id.mid2_item_img);
            int screenWidth = Utils.getScreenWidth(getContext());
            int imageHeight = ImageUtils.calculateImgHeight(screenWidth, R.drawable.img_test2);
            height = imageHeight;
            TextView lable = (TextView) viewItem.findViewById(R.id.tv_mid2_lable);
            if (!TextUtils.isEmpty(mid2Info.getType() + "")) {
                lable.setText(lables[mid2Info.getType() - 1]);
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, imageHeight);
            imageView.setLayoutParams(params);

            AsyncImage.loadNetPhoto(getContext(), HttpConstant.SERVICE_UPLOAD_AREA + mid2Info.getIcon(), imageView);
            viewList.add(viewItem);
            viewItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;
                    switch (mid2Info.getType()) {
                        case 4://5-推广/广告
                            intent = new Intent(getContext(), SubjectActivity.class);
                            intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.TUIGUANG);
                            intent.putExtra("coins_ad_url", mid2Info.getUrl());
                            intent.putExtra("title", mid2Info.getTitle());
                            // Toast.makeText(context,"" + banner.getUrl(),0).show();
                            startActivity(intent);
                            break;
                        case 1: //10官方比赛
                            intent = new Intent();
                            intent.setClass(getContext(), OfficalEventActivity.class);
                            intent.putExtra("matchId", mid2Info.getId() + "");
                            startActivity(intent);
                            break;
                        case 2:  //11娱乐赛
                            intent = new Intent();
                            intent.setClass(getContext(), RecreationMatchDetailsActivity.class);
                            intent.putExtra("id", mid2Info.getId() + "");
                            startActivity(intent);
                            break;
                        case 3:  //12约战
                            intent = new Intent(getContext(), YueZhanDetailsActivity.class);
                            intent.putExtra("id", mid2Info.getId() + "");
                            startActivity(intent);
                            break;
                        case 5:  //13福利
                            intent = new Intent(getContext(), SubjectActivity.class);
                            intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.TUIGUANG);
                            //Toast.makeText(context,"" + banner.getUrl(),0).show();
                            intent.putExtra("coins_ad_url", mid2Info.getUrl());
                            intent.putExtra("title", mid2Info.getTitle());
                            startActivity(intent);
                            break;
                        case 14: //下载
                            intent = new Intent(getContext(), DownloadWebView.class);
                            intent.putExtra("download_url", mid2Info.getUrl());
                            intent.putExtra("title", mid2Info.getTitle());
                            startActivity(intent);
                    }
                }
            });
        }
        PagerAdapter adapter = new Mid1VpAdapter(viewList);
        vpMid2.setAdapter(adapter);

        vpMid2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < llMid2Indecator.getChildCount(); i++) {
                    if (i == (position % llMid2Indecator.getChildCount())) {
                        llMid2Indecator.getChildAt(i).setSelected(true);
                    } else {
                        llMid2Indecator.getChildAt(i).setSelected(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ScrollController.addViewPager(getClass().getSimpleName() + "_mid2", vpMidViewPager);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        vpMid2.setLayoutParams(layoutParams);
        initDots(mid2Infos);
    }

    /**
     * 随着图片的移动显示对应的点
     *
     * @param
     */
    private void initDots(List list) {
        llMid2Indecator.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            llMid2Indecator.addView(initDot());
        }
        llMid2Indecator.getChildAt(0).setSelected(true);
        llMid2Indecator.setVisibility(View.GONE);
    }

    /**
     * 轮播图的点
     *
     * @return
     */
    private View initDot() {
        return LayoutInflater.from(getContext()).inflate(R.layout.headline_indecator, null);
    }

    @Override
    public void startPlay() {
        if (!banners.isEmpty() && headLinesView != null) {
            headLinesView.startAutoScroll();
        }
    }

    @Override
    public void stopPlay() {
        if (headLinesView != null) {
            headLinesView.stopAutoScroll();
        }
    }
}
