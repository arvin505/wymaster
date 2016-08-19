package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.AthleticsListAdapter;
import com.miqtech.master.client.adapter.RecreationMatchAdapter;
import com.miqtech.master.client.adapter.YueZhanListAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.Banner;
import com.miqtech.master.client.entity.City;
import com.miqtech.master.client.entity.RecommendInfo;
import com.miqtech.master.client.entity.RecreationMatchInfo;
import com.miqtech.master.client.entity.YueZhan2Info;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.AmusementGameActivity;
import com.miqtech.master.client.ui.LatestOfficalActivity;
import com.miqtech.master.client.ui.MainActivity;
import com.miqtech.master.client.ui.OfficalEventActivity;
import com.miqtech.master.client.ui.RecreationMatchDetailsActivity;
import com.miqtech.master.client.ui.YueZhanListActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.ACache;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.ScrollController;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.view.HeadLinesView;
import com.miqtech.master.client.view.MyGridView;
import com.miqtech.master.client.view.MyScrollView;
import com.miqtech.master.client.view.RecommendViewPager;
import com.miqtech.master.client.view.SlidingMenu;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshScrollView;
import com.miqtech.master.client.view.pullToRefresh.internal.FrameAnimationHeaderLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2015/11/23 0023.
 */
public class FragmentAthletics extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener,
         MyScrollView.MyScrollListener, SlidingMenu.isAutoPlay {
    @Bind(R.id.wyPSAthletics)
    PullToRefreshScrollView wyPSAthletics;
    @Bind(R.id.athleticsHead)
    HeadLinesView athleticsHead;
    @Bind(R.id.head_vp_img)
    ViewPager viewPager;
    @Bind(R.id.lvOfficialActivity)
    ListView lvOfficialActivity;
    @Bind(R.id.gvRecreationlMatch)
    MyGridView gvRecreationlMatch;
    @Bind(R.id.lvPlayYuezhan)
    ListView lvPlayYuezhan;
    @Bind(R.id.tvOfficialActivity)
    TextView tvOfficialActivity;
    @Bind(R.id.tvOfficialBottom)
    TextView tvOfficialBottom;
    @Bind(R.id.tvRecreationlMatch)
    TextView tvRecreationlMatch;
    @Bind(R.id.tvRecreationBottom)
    TextView tvRecreationBottom;
    @Bind(R.id.tvPlayerYuezhan)
    TextView tvPlayer;
    @Bind(R.id.tvPlayBottom)
    TextView tvPlayBottom;
    @Bind(R.id.llOfficialActivity)
    LinearLayout llOfficialActivity;
    @Bind(R.id.llRecreationlMatch)
    LinearLayout llRecreationlMatch;
    @Bind(R.id.llPlayerYuezhan)
    LinearLayout llPlayerYuezhan;


    @Bind(R.id.tvOfficialActivity1)
    TextView tvOfficialActivity1;
    @Bind(R.id.tvOfficialBottom1)
    TextView tvOfficialBottom1;
    @Bind(R.id.tvRecreationlMatch1)
    TextView tvRecreationlMatch1;
    @Bind(R.id.tvRecreationBottom1)
    TextView tvRecreationBottom1;
    @Bind(R.id.tvPlayerYuezhan1)
    TextView tvPlayer1;
    @Bind(R.id.tvPlayBottom1)
    TextView tvPlayBottom1;
    @Bind(R.id.llOfficialActivity1)
    LinearLayout llOfficialActivity1;
    @Bind(R.id.llRecreationlMatch1)
    LinearLayout llRecreationlMatch1;
    @Bind(R.id.llPlayerYuezhan1)
    LinearLayout llPlayerYuezhan1;
    @Bind(R.id.llTitle)
    LinearLayout llTitle;
    @Bind(R.id.llTitle1)
    LinearLayout llTitle1;
    Context context;
    @Bind(R.id.ll_gridview)
    LinearLayout llGridView;

    @Bind(R.id.vpRecommend)
    RecommendViewPager vpRecommend;

    private View officialActivityHeader;
    private View recreationMatchHeader;
    private View playYuezhanHeader;

    private int currentTpye = 1;
    private int officialActivity = 1;
    private int recreationMatch = 2;
    private int playerYuezhan = 3;

    private boolean recomendActivityFirst = true;
    private boolean playerYuezhanFirst = true;

    private Handler mHandler = new Handler();

    public int athleticsReuqest = 1;

    //缓存
    private ACache mCache;
    private List<RecreationMatchInfo> recreationMatchInfos;
    private List<RecommendInfo> newOfficialMatchs;
    private int top;
    private MyScrollView mScrollView;

    private List<Banner> banners = new ArrayList<Banner>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCache = ACache.get(getActivity());
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_athletics, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        ButterKnife.bind(this, view);
        initView();
        initData();
    }

    private void initView() {
        mScrollView = wyPSAthletics.getRefreshableView();
        llOfficialActivity.setOnClickListener(this);
        llRecreationlMatch.setOnClickListener(this);
        llPlayerYuezhan.setOnClickListener(this);
        llOfficialActivity1.setOnClickListener(this);
        llRecreationlMatch1.setOnClickListener(this);
        llPlayerYuezhan1.setOnClickListener(this);
        gvRecreationlMatch.setOnItemClickListener(this);
        gvRecreationlMatch.setFocusable(false);
        lvOfficialActivity.setOnItemClickListener(this);
        mScrollView.setOnMyScrollListener(this);
        lvOfficialActivity.setFocusable(false);
        lvPlayYuezhan.setFocusable(false);
        //     scrollView.setOnMyScrollListener(this);
        float headerHeight = getResources().getDimension(R.dimen.athletics_add_height);
        float margin20 = getResources().getDimension(R.dimen.margin_20dp);
        float margin25 = getResources().getDimension(R.dimen.margin_25dp);
        float galleryHeight = getResources().getDimension(R.dimen.gallery_height);
        top = (int) (headerHeight + margin20 + margin25 + galleryHeight);
        wyPSAthletics.setHeaderLayout(new FrameAnimationHeaderLayout(getActivity()));
        wyPSAthletics.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MyScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<MyScrollView> refreshView) {
                loadBanner();
                loadAthleticsRecomend();
                if (currentTpye == officialActivity) {
                    loadOfficialActivity();
                } else if (currentTpye == recreationMatch) {
                    loadRecomendActivity();
                } else if (currentTpye == playerYuezhan) {
                    loadPlayerYuezhan();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if(!isHasNetWork){
                    showToast(getActivity().getResources().getString(R.string.noNeteork));
                }
            }
        });

     //   ((MainActivity) context).mMenu.setOnIsAutoPlay(this);
    }


    private void initViewPager(List<Banner> banners) {
        athleticsHead.refreshData(banners);
        ScrollController.addViewPager(getClass().getSimpleName() + "_middle", viewPager);
        athleticsHead.startAutoScroll();
    }

    private void initData() {
        loadAthleticsRecomendCache();
        loadOfficialActivityCache();
        loadBannerCache();
        loadBanner();
        loadAthleticsRecomend();
        loadOfficialActivity();
    }

    private void loadBanner() {
        HashMap params = new HashMap();
        params.put("belong", 1 + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AD, params, HttpConstant.AD);
    }

    private void loadAthleticsRecomend() {
        HashMap params = new HashMap();
        String lastCityStr = PreferencesUtil.getLastRecreationCity(context);
        if (TextUtils.isEmpty(lastCityStr)) {
            if (Constant.currentCity != null && !TextUtils.isEmpty(Constant.currentCity.getAreaCode())) {
                params.put("areaCode", Constant.currentCity.getAreaCode());
            }
        } else {
            String areaCode = GsonUtil.getBean(lastCityStr, City.class).getAreaCode();
            if (!TextUtils.isEmpty(areaCode)) {
                params.put("areaCode", areaCode);
            }

        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ATHLETICS_RECOMEND, params, HttpConstant.ATHLETICS_RECOMEND);
    }

    private void loadOfficialActivity() {
        Map<String, String> params = new HashMap<>();
        String lastCityStr = PreferencesUtil.getLastRecreationCity(context);
        if (TextUtils.isEmpty(lastCityStr)) {
            if (Constant.currentCity != null && !TextUtils.isEmpty(Constant.currentCity.getAreaCode())) {
                params.put("areaCode", Constant.currentCity.getAreaCode());
            }
        } else {
            String areaCode = GsonUtil.getBean(lastCityStr, City.class).getAreaCode();
            if (!TextUtils.isEmpty(areaCode)) {
                params.put("areaCode", areaCode);
            }

        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.OFFICIAL_ACTIVITY, params.isEmpty() ? null : params, HttpConstant.OFFICIAL_ACTIVITY);
    }

    private void loadRecomendActivity() {
        HashMap params = new HashMap();
        String lastCityStr = PreferencesUtil.getLastRecreationCity(context);
        if (TextUtils.isEmpty(lastCityStr)) {
            if (Constant.currentCity != null && !TextUtils.isEmpty(Constant.currentCity.getAreaCode())) {
                params.put("areaCode", Constant.currentCity.getAreaCode());
            }
        } else {
            String areaCode = GsonUtil.getBean(lastCityStr, City.class).getAreaCode();
            if (!TextUtils.isEmpty(areaCode)) {
                params.put("areaCode", areaCode);
            }
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_LIST, params, HttpConstant.AMUSE_LIST);
    }

    private void loadPlayerYuezhan() {
        HashMap params = new HashMap();
        if (WangYuApplication.getUser(context) != null) {
            params.put("userId", WangYuApplication.getUser(context).getId());
            params.put("token", WangYuApplication.getUser(context).getToken());
        }
        String lastCityStr = PreferencesUtil.getLastRecreationCity(context);
        if (TextUtils.isEmpty(lastCityStr)) {
            if (Constant.currentCity != null) {
                params.put("areaCode", Constant.currentCity.getAreaCode());
            }
        } else {
            params.put("areaCode", GsonUtil.getBean(lastCityStr, City.class).getAreaCode());
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.LATEST_BATTLE, params.isEmpty() ? null : params, HttpConstant.LATEST_BATTLE);
    }

    private void loadAthleticsRecomendCache() {
        JSONObject athleticsRecomendCache = mCache.getAsJSONObject(HttpConstant.ATHLETICS_RECOMEND);
        if (athleticsRecomendCache != null && !athleticsRecomendCache.isNull("object")) {
            try {
                initAthleticsRecomend(athleticsRecomendCache);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadOfficialActivityCache() {
        JSONObject officialCache = mCache.getAsJSONObject(HttpConstant.OFFICIAL_ACTIVITY);
        if (officialCache != null && !officialCache.isNull("object")) {
            try {
                initOfficialActivity(officialCache);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadBannerCache() {
        JSONObject bannerCache = mCache.getAsJSONObject(HttpConstant.AD + "_1");
        if (bannerCache != null && !bannerCache.isNull("object")) {
            try {
                String strBanner = bannerCache.getString("object");
                banners = GsonUtil.getList(strBanner, Banner.class);
                initViewPager(banners);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadRecreationActivityCache() {
        JSONObject amuseListCache = mCache.getAsJSONObject(HttpConstant.AMUSE_LIST);
        if (amuseListCache != null && !amuseListCache.isNull("object")) {
            try {
                initAmuseList(amuseListCache);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadPlayerYuezhanCache() {
        JSONObject latestBattleCache = mCache.getAsJSONObject(HttpConstant.LATEST_BATTLE);
        if (latestBattleCache != null && !latestBattleCache.isNull("object")) {
            try {
                initLatestBattle(latestBattleCache);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        wyPSAthletics.onRefreshComplete();
        LogUtil.e("xiaoyi", "xiaoyi  == " + object.toString());
        try {
            if (HttpConstant.AD.equals(method)) {
                String strBanner = object.getString("object");
                banners = GsonUtil.getList(strBanner, Banner.class);
                initViewPager(banners);
                mCache.put(HttpConstant.AD + "_1", object);
            } else if (HttpConstant.ATHLETICS_RECOMEND.equals(method)) {
                initAthleticsRecomend(object);
                mCache.put(HttpConstant.ATHLETICS_RECOMEND, object);
            } else if (HttpConstant.OFFICIAL_ACTIVITY.equals(method)) {
                initOfficialActivity(object);
                mCache.put(HttpConstant.OFFICIAL_ACTIVITY, object);
            } else if (HttpConstant.AMUSE_LIST.equals(method)) {
                initAmuseList(object);
                recomendActivityFirst = false;
                mCache.put(HttpConstant.AMUSE_LIST, object);
            } else if (HttpConstant.LATEST_BATTLE.equals(method)) {
                initLatestBattle(object);
                mCache.put(HttpConstant.LATEST_BATTLE, object);
                playerYuezhanFirst = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        wyPSAthletics.onRefreshComplete();
    }

    private void initAthleticsRecomend(JSONObject object) throws JSONException {
        String strMatch = object.getString("object");
        List<RecommendInfo> matchs = GsonUtil.getList(strMatch, RecommendInfo.class);
        vpRecommend.refreshData(matchs);
    }

    private void initOfficialActivity(JSONObject object) throws JSONException {
        String strObj = object.getString("object");
        JSONObject jsonObj = new JSONObject(strObj);
        String strData = jsonObj.getString("data");
        JSONObject jsonData = new JSONObject(strData);
        String strList = jsonData.getString("list");
        String strRecommend = jsonObj.getString("recommend");
        final RecommendInfo recommend = GsonUtil.getBean(strRecommend, RecommendInfo.class);
        newOfficialMatchs = GsonUtil.getList(strList, RecommendInfo.class);
        AthleticsListAdapter athleticsListAdapter = new AthleticsListAdapter(context, newOfficialMatchs);
        if (officialActivityHeader == null) {
            officialActivityHeader = View.inflate(context, R.layout.layout_athletics_header, null);

            lvOfficialActivity.addHeaderView(officialActivityHeader);
        }
        ImageView ivImg = (ImageView) officialActivityHeader.findViewById(R.id.ivImg);
        ivImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOfficialActivity(recommend.getId() + "");
            }
        });
        TextView tvTitle = (TextView) officialActivityHeader.findViewById(R.id.tv_recomment_title);
        TextView tvTime = (TextView) officialActivityHeader.findViewById(R.id.tv_recomment_time);
        tvTime.setText("开始时间:" + TimeFormatUtil.formatNoTime(recommend.getStart_time()));
        tvTitle.setText(recommend.getTitle());
        TextView tvHandleTitle = (TextView) officialActivityHeader.findViewById(R.id.tvHandleTitle);
        tvHandleTitle.setText("最新活动");

        AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + recommend.getIcon(), ivImg);
        tvHandleTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, LatestOfficalActivity.class);
                startActivity(intent);
            }
        });
        lvOfficialActivity.setAdapter(athleticsListAdapter);
    }

    private void initAmuseList(JSONObject object) throws JSONException {
        String strObj = object.getString("object");
        JSONObject jsonObj = new JSONObject(strObj);
        String strData = jsonObj.getString("data");
        String strRecommend = jsonObj.getString("recommend");
        JSONObject jsonData = new JSONObject(strData);
        final RecommendInfo recommend = GsonUtil.getBean(strRecommend, RecommendInfo.class);
        recreationMatchInfos = GsonUtil.getList(jsonData.getString("list"), RecreationMatchInfo.class);

        RecreationMatchAdapter athleticsListAdapter = new RecreationMatchAdapter(gvRecreationlMatch, context, recreationMatchInfos);
        if (recreationMatchHeader == null) {
            recreationMatchHeader = View.inflate(context, R.layout.layout_athletics_header, null);

            llGridView.addView(recreationMatchHeader, 0);
        }
        TextView tvTitle = (TextView) recreationMatchHeader.findViewById(R.id.tv_recomment_title);
        TextView tvTime = (TextView) recreationMatchHeader.findViewById(R.id.tv_recomment_time);
        tvTime.setText("开始时间:" + TimeFormatUtil.formatNoTime(recommend.getStartDate()));
        tvTitle.setText(recommend.getTitle() + "");
        ImageView ivImg = (ImageView) recreationMatchHeader.findViewById(R.id.ivImg);
        TextView tvHandleTitle = (TextView) recreationMatchHeader.findViewById(R.id.tvHandleTitle);
        tvHandleTitle.setText("最新比赛");
        tvHandleTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getContext(), AmusementGameActivity.class);
                startActivity(intent);
            }
        });
        ivImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecreationMatchActivity(recommend.getId() + "");
            }
        });
        AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + recommend.getMainIcon(), ivImg);
        //AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + ((RecreationMatchInfo)recreationMatchInfos.get(0)).getMainIcon(), ivImg);
        gvRecreationlMatch.setAdapter(athleticsListAdapter);
    }

    private void initLatestBattle(JSONObject object) throws JSONException {
        String strObj = object.getString("object");
        JSONObject jsonObj = new JSONObject(strObj);
        String strData = jsonObj.getString("list");

        List<YueZhan2Info> newBattle = GsonUtil.getList(strData, YueZhan2Info.class);
        YueZhanListAdapter battleListAdapter = new YueZhanListAdapter(context, newBattle);
        if (playYuezhanHeader == null) {
            playYuezhanHeader = View.inflate(context, R.layout.layout_athletics_header, null);
            lvPlayYuezhan.addHeaderView(playYuezhanHeader);
        }
        TextView tvHandleTitle = (TextView) playYuezhanHeader.findViewById(R.id.tvHandleTitle);
        tvHandleTitle.setText("热门约战");
        playYuezhanHeader.findViewById(R.id.ivImg).setVisibility(View.GONE);
        tvHandleTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent yuezhanList = new Intent(getContext(), YueZhanListActivity.class);
                startActivity(yuezhanList);
            }
        });
        lvPlayYuezhan.setAdapter(battleListAdapter);
    }


    @Override
    public void onError(String errMsg, String method) {

        wyPSAthletics.onRefreshComplete();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.llOfficialActivity || v.getId() == R.id.llOfficialActivity1) {
            currentTpye = officialActivity;
            tvOfficialActivity.setTextColor(getResources().getColor(R.color.orange));
            tvRecreationlMatch.setTextColor(getResources().getColor(R.color.gray));
            tvPlayer.setTextColor(getResources().getColor(R.color.gray));
            tvOfficialBottom.setVisibility(View.VISIBLE);
            tvRecreationBottom.setVisibility(View.INVISIBLE);
            tvPlayBottom.setVisibility(View.INVISIBLE);
            tvOfficialActivity1.setTextColor(getResources().getColor(R.color.orange));
            tvRecreationlMatch1.setTextColor(getResources().getColor(R.color.gray));
            tvPlayer1.setTextColor(getResources().getColor(R.color.gray));
            tvOfficialBottom1.setVisibility(View.VISIBLE);
            tvRecreationBottom1.setVisibility(View.INVISIBLE);
            tvPlayBottom1.setVisibility(View.INVISIBLE);
            lvOfficialActivity.setVisibility(View.VISIBLE);
            gvRecreationlMatch.setVisibility(View.GONE);
            lvPlayYuezhan.setVisibility(View.GONE);
            athleticsReuqest = 1;
            if (recreationMatchHeader != null) {
                recreationMatchHeader.setVisibility(View.GONE);
            }
        } else if (v.getId() == R.id.llRecreationlMatch || v.getId() == R.id.llRecreationlMatch1) {
            currentTpye = recreationMatch;
            tvOfficialActivity.setTextColor(getResources().getColor(R.color.gray));
            tvRecreationlMatch.setTextColor(getResources().getColor(R.color.orange));
            tvPlayer.setTextColor(getResources().getColor(R.color.gray));
            tvOfficialBottom.setVisibility(View.INVISIBLE);
            tvRecreationBottom.setVisibility(View.VISIBLE);
            tvPlayBottom.setVisibility(View.INVISIBLE);
            tvOfficialActivity1.setTextColor(getResources().getColor(R.color.gray));
            tvRecreationlMatch1.setTextColor(getResources().getColor(R.color.orange));
            tvPlayer1.setTextColor(getResources().getColor(R.color.gray));
            tvOfficialBottom1.setVisibility(View.INVISIBLE);
            tvRecreationBottom1.setVisibility(View.VISIBLE);
            tvPlayBottom1.setVisibility(View.INVISIBLE);
            lvOfficialActivity.setVisibility(View.GONE);
            gvRecreationlMatch.setVisibility(View.VISIBLE);
            lvPlayYuezhan.setVisibility(View.GONE);
            if (recomendActivityFirst) {
                loadRecreationActivityCache();
                loadRecomendActivity();
            }
            if (recreationMatchHeader != null) {
                recreationMatchHeader.setVisibility(View.VISIBLE);
            }
            athleticsReuqest = 2;
        } else if (v.getId() == R.id.llPlayerYuezhan || v.getId() == R.id.llPlayerYuezhan1) {
            currentTpye = playerYuezhan;
            tvOfficialActivity.setTextColor(getResources().getColor(R.color.gray));
            tvRecreationlMatch.setTextColor(getResources().getColor(R.color.gray));
            tvPlayer.setTextColor(getResources().getColor(R.color.orange));
            tvOfficialBottom.setVisibility(View.INVISIBLE);
            tvRecreationBottom.setVisibility(View.INVISIBLE);
            tvPlayBottom.setVisibility(View.VISIBLE);
            tvOfficialActivity1.setTextColor(getResources().getColor(R.color.gray));
            tvRecreationlMatch1.setTextColor(getResources().getColor(R.color.gray));
            tvPlayer1.setTextColor(getResources().getColor(R.color.orange));
            tvOfficialBottom1.setVisibility(View.INVISIBLE);
            tvRecreationBottom1.setVisibility(View.INVISIBLE);
            tvPlayBottom1.setVisibility(View.VISIBLE);
            lvOfficialActivity.setVisibility(View.GONE);
            gvRecreationlMatch.setVisibility(View.GONE);
            lvPlayYuezhan.setVisibility(View.VISIBLE);
            if (playerYuezhanFirst) {
                loadPlayerYuezhanCache();
                loadPlayerYuezhan();
            }
            athleticsReuqest = 3;
            if (recreationMatchHeader != null) {
                recreationMatchHeader.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gvRecreationlMatch:
                startRecreationMatchActivity(recreationMatchInfos.get(position).getId() + "");
                break;
            case R.id.lvOfficialActivity:
                if ((int) parent.getAdapter().getItemId(position) != -1) {
                    startOfficialActivity(newOfficialMatchs.get((int) parent.getAdapter().getItemId(position)).getId() + "");
                }
                break;
        }
    }

    private void startRecreationMatchActivity(String id) {
        Intent intent = new Intent();
        intent.setClass(context, RecreationMatchDetailsActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    private void startOfficialActivity(String id) {
        Intent intent = new Intent();
        intent.setClass(context, OfficalEventActivity.class);
        intent.putExtra("matchId", id);
        startActivity(intent);
    }

    // FIXME: 2016/7/28
    /*@Override
    public void changeData() {
        if (athleticsReuqest == 1) {
            loadAthleticsRecomend();
            loadOfficialActivity();
        } else if (athleticsReuqest == 2) {
            loadAthleticsRecomend();
            loadRecomendActivity();
        } else if (athleticsReuqest == 3) {
            loadAthleticsRecomend();
            loadPlayerYuezhan();
        }
    }*/

    @Override
    public void move(int x, int y, int oldx, int oldy) {
        if (y > top) {
            llTitle1.setVisibility(View.VISIBLE);
            llTitle.setVisibility(View.INVISIBLE);
        } else {
            llTitle1.setVisibility(View.INVISIBLE);
            llTitle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void startPlay() {
        if (!banners.isEmpty() && athleticsHead != null) {
            athleticsHead.startAutoScroll();
        }
    }

    @Override
    public void stopPlay() {
        if (athleticsHead != null) {
            athleticsHead.stopAutoScroll();
        }
    }
}
