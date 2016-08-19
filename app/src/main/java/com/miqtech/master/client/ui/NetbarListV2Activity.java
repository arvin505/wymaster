package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.HotInternetBarAdapter;
import com.miqtech.master.client.adapter.RecommendNetbarV2Adapter;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.City;
import com.miqtech.master.client.entity.HotInternetBarInfo;
import com.miqtech.master.client.entity.InternetBarInfo;
import com.miqtech.master.client.entity.NetBarSearchType;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.ACache;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.view.AreaPopupWindow;
import com.miqtech.master.client.view.HasErrorForNetBarListView;
import com.miqtech.master.client.view.MyScrollView;
import com.miqtech.master.client.view.SearchDialog;
import com.miqtech.master.client.view.SearchTypePopupWindow;
import com.miqtech.master.client.view.layoutmanager.FullLinearLayoutManager;
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
 * Created by Administrator on 2015/12/3.
 */
public class NetbarListV2Activity extends BaseActivity implements
//        RefreshLayout.OnLoadListener,
        View.OnClickListener, MyScrollView.MyScrollListener {
    private final String TAG = "NetbarListV2Activity";
    @Bind(R.id.prsvScrollView)
    PullToRefreshScrollView prsvScrollView;
    MyScrollView myScrollView;

    @Bind(R.id.lv_netbarlist)
    HasErrorForNetBarListView lvNetbar;
    @Bind(R.id.img_header_icon)
    ImageView back;
    @Bind(R.id.img_search)
    ImageButton btSearch;
    @Bind(R.id.img_location)
    ImageButton btLocation;

    @Bind(R.id.llAllArea)
    LinearLayout llAllArea;
    @Bind(R.id.llAllPrice)
    LinearLayout llAllPrice;
    @Bind(R.id.tvArea)
    TextView tvArea;
    @Bind(R.id.tvAll)
    TextView tvAll;
    @Bind(R.id.tv_header_title)
    TextView tvTitel;
    @Bind(R.id.llSortView)
    LinearLayout llSortView;


    @Bind(R.id.llAllArea2)
    LinearLayout llAllArea2;
    @Bind(R.id.llAllPrice2)
    LinearLayout llAllPrice2;
    @Bind(R.id.reserations_Title)
    RelativeLayout reserationTitle;
    @Bind(R.id.tvArea2)
    TextView tvArea2;
    @Bind(R.id.tvAll2)
    TextView tvAll2;
    @Bind(R.id.llSortView2)
    LinearLayout llSortView2;


    @Bind(R.id.hot_internet_recycler)
    RecyclerView recyclerView;//热门网吧
    @Bind(R.id.ll_hot_netbar)
    LinearLayout llHotNetbar;//热门网吧一栏所有布局
    @Bind(R.id.view_line2)
    View viewLine2;
    @Bind(R.id.llview)
    LinearLayout llview;


    private boolean isRelease = false;
    private SearchDialog searchDialog;

    private RecommendNetbarV2Adapter adapter;
    private int page = 1;
    private String pageSize = "10";
    private int isLast = 0;
    private List<InternetBarInfo> mDatas = new ArrayList<>();
    private ACache mCache;
    private Handler mHandler = new Handler();
    private List<City> areas = new ArrayList<City>();
    private String areaCode = "";
    // 判断是否是第一次获取城市
    private boolean isFirstLoadArea = true;

    private AreaPopupWindow areaPopup;
    private SearchTypePopupWindow searchTypePopup;
    private List<NetBarSearchType> types = new ArrayList<NetBarSearchType>();
    // 排序规则
    private int type = 0;
    public int searchType = 0;
    private String netbarName = "";
    private Map<String, String> params = new HashMap<>();
    private boolean isSearch = false;
    private List<HotInternetBarInfo> hotInternetBarInfoList = new ArrayList<HotInternetBarInfo>();
    private Context context;

    private FullLinearLayoutManager linearLayoutManager;
    private HotInternetBarAdapter hotInternetBarAdapter;
    private int menuHeight2;//用来判断是否让选择框吸附在顶部

    private boolean isRequestFinish = false;//是否在加载完成
    private View footerView;
    RelativeLayout llFooterView;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_netbarlist_v2);
        ButterKnife.bind(this);
        mCache = ACache.get(this);
        context = this;
        lengthCoding = UMengStatisticsUtil.CODE_2001;
        initView();
        Intent intent = getIntent();
        isRelease = intent.getBooleanExtra("isReleaseWar", false);
        netbarName = intent.getStringExtra("netbarName");
        searchType = intent.getIntExtra("searchType", 0);
        params.put("areaCode", Constant.currentCity.getAreaCode());
        isSearch = intent.getBooleanExtra("isSearch", false);
        showLoading();
        if (isRelease || isSearch) {
            searchNetbar(netbarName);
            initSearchByName();
        } else {
            loadData();
        }
        showTopSelect();
    }

    @Override
    protected void initView() {
       myScrollView= prsvScrollView.getRefreshableView();
        prsvScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<MyScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<MyScrollView> refreshView) {
                loadData();
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getString(R.string.noNeteork));
                }
            }
        });


        adapter = new RecommendNetbarV2Adapter(this, mDatas, 1);
        lvNetbar.setErrorView("并没有搜到网吧呀");
        footerView = LayoutInflater.from(context).inflate(R.layout.layout_footer_view, null);
        llFooterView = (RelativeLayout) footerView.findViewById(R.id.footerView);
        lvNetbar.addFooterView(footerView);
        llFooterView.setVisibility(View.GONE);
        lvNetbar.setAdapter(adapter);

        linearLayoutManager = new FullLinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        llAllPrice.setOnClickListener(this);
        llAllArea.setOnClickListener(this);
        llAllPrice2.setOnClickListener(this);
        llAllArea2.setOnClickListener(this);
        back.setOnClickListener(this);

        btLocation.setVisibility(View.VISIBLE);
        btLocation.setOnClickListener(this);
        btSearch.setVisibility(View.VISIBLE);
        btSearch.setOnClickListener(this);

        searchDialog = new SearchDialog(this, R.style.searchStyle);

        lvNetbar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= mDatas.size()) {
                    return;
                }
                InternetBarInfo netbar = (InternetBarInfo) mDatas.get(position);
                if (netbar == null) {
                    return;
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                if (!isRelease) {
                    bundle.putString("netbarId", netbar.getId());
                    intent.setClass(NetbarListV2Activity.this, InternetBarActivityV2.class);
                } else {
                    intent.setClass(NetbarListV2Activity.this, ReleaseWarActivity.class);
                    intent.putExtra("netbar", netbar);
                }
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
    }

    /**
     * 状态初始化
     */
    private void StateInitialization() {
        page = 1;
        areaCode = "";
        type = 0;
        tvAll.setText("全部");
        tvAll2.setText("全部");
        tvArea.setText("全部区域");
        tvArea2.setText("全部区域");
        params.put("areaCode", Constant.currentCity.getAreaCode());
        params.put("type", "");
    }

    /**
     * 请求数据
     */
    private void loadData() {
        isRequestFinish = false;
        params.put("page", page + "");
        params.put("pagesize", pageSize);
        if (Constant.isLocation) {
            params.put("longitude", Constant.longitude + "");
            params.put("latitude", Constant.latitude + "");
        }
        showLoading();
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.NETBARLISTALL_V2, params, HttpConstant.NETBARLISTALL_V2);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        prsvScrollView.onRefreshComplete();
        hideLoading();
        try {
            if (HttpConstant.NETBARLISTALL_V2.equals(method)) {
                if (object.has("object")) {
                    analysisData(object.get("object").toString());
                }
            } else if (HttpConstant.VALIDCHILDREN.equals(method)) {
                hideLoading();

                List<City> newCity = initCityData(object);
                City city = new City();
                city.setAreaCode("-1");
                city.setName("全部区域");
                areas.add(city);
                areas.addAll(newCity);
                isFirstLoadArea = false;
                showAreaPopup();
            } else if (HttpConstant.SEARCH.equals(method)) {
                List<InternetBarInfo> list = null;
                hideLoading();

                list = initSearchList(object);

                JSONObject objectJson = object.getJSONObject("object");
                if (objectJson.has("isLast")) {
                    isLast = objectJson.getInt("isLast");
                }

                if (list != null && !list.isEmpty()) {
                    initNetbarList(list);
                } else {
                    hideHotAndFooterView();
                    showToast("未找到" + netbarName);
                }
                if (list == null || list.isEmpty()) {
                    lvNetbar.setErrorShow(true);
                } else {
                    lvNetbar.setErrorShow(false);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析网吧列表数据
     *
     * @param Str
     */
    private void analysisData(String Str) {
        try {
            JSONObject object = new JSONObject(Str);
            if (page == 1) {
                mDatas.clear();
                mCache.put(TAG, object);
                if (object.has("hot_netbar")) {
                    hotInternetBarInfoList.clear();
                    hotInternetBarInfoList = GsonUtil.getList(object.get("hot_netbar").toString(), HotInternetBarInfo.class);
                    if (!hotInternetBarInfoList.isEmpty()) {
                        hotInternetBarAdapter = new HotInternetBarAdapter(context, hotInternetBarInfoList);
                        recyclerView.setAdapter(hotInternetBarAdapter);
                        llHotNetbar.setVisibility(View.VISIBLE);
                        int w2 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        int h2 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        llHotNetbar.measure(w2, h2);
                        menuHeight2 = llHotNetbar.getMeasuredHeight();
                        LogUtil.d("move","menuHeight2"+menuHeight2);
                    } else {
                        llHotNetbar.setVisibility(View.GONE);
                        menuHeight2 = 0;
                    }
                } else {
                    llHotNetbar.setVisibility(View.GONE);
                    menuHeight2 = 0;
                }
            }

            if (object.has("all_netbar")) {//解析网吧列表数据
                JSONObject jsonObject = new JSONObject(object.get("all_netbar").toString());
                isLast = jsonObject.getInt("isLast");//得到是否是最后一页
                if (jsonObject.has("list")) {
                    List<InternetBarInfo> newInternetBarInfo = GsonUtil.getList(jsonObject.get("list").toString(), InternetBarInfo.class);
                    mDatas.addAll(newInternetBarInfo);
                }
                if (llFooterView != null) {
                    if (isLast == 1) {//隐藏尾部的加载更多
                        llFooterView.setVisibility(View.GONE);
                    } else {
                        llFooterView.setVisibility(View.VISIBLE);
                    }
                }
            } else {//加入没有则移除尾部的加载更多
                lvNetbar.removeFooterView(footerView);
            }

            if (isLast == 1) {
                showToast(context.getResources().getString(R.string.nomore));
            }

            if (mDatas.isEmpty()) {
                lvNetbar.setErrorShow(true);
            } else {
                lvNetbar.setErrorShow(false);
            }

            adapter.setData(mDatas);
            adapter.notifyDataSetChanged();
            isRequestFinish = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void initNetbarList(List<InternetBarInfo> datas) {
        hideHotAndFooterView();
        mDatas.addAll(datas);
        adapter.setData(mDatas);
        adapter.notifyDataSetChanged();
    }

    /**
     * 隐藏热门网吧和尾部加载更多字样
     */
    private void hideHotAndFooterView() {
        if (page == 1) {
            mDatas.clear();
            llHotNetbar.setVisibility(View.GONE);
            menuHeight2 = 0;
        }
        if (llFooterView != null) {
            if (isLast == 1) {//隐藏尾部的加载更多
                llFooterView.setVisibility(View.GONE);
            }
        }
    }

    private List<InternetBarInfo> initSearchList(JSONObject object) throws JSONException {
        List<InternetBarInfo> netbars = new ArrayList<>();
        if (object.getInt("code") == 0 && object.has("object") && object.getString("result").equals("success")) {
            JSONArray objectJson = object.getJSONObject("object").getJSONArray("list");
            netbars = GsonUtil.getList(objectJson.toString(), InternetBarInfo.class);
            return netbars;
        }
        return netbars;
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        prsvScrollView.onRefreshComplete();
        lvNetbar.setErrorShow(true);
        if (llFooterView != null) {
            llFooterView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        prsvScrollView.onRefreshComplete();
        if (page == 1) {
            lvNetbar.setErrorShow(true);
        }
        if (llFooterView != null) {
            llFooterView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llAllArea://中间部分的地域选择
                myScrollView.scrollTo(0, menuHeight2);
                if (areas.size() > 0) {
                    showAreaPopup();
                } else {
                    loadArea();
                }
                break;
            case R.id.llAllPrice://中间部分的类型选择
                myScrollView.scrollTo(0, menuHeight2);//定位到顶部
                showPricePopup();
                break;
            case R.id.llAllArea2://顶部部分的地域选择
                if (areas.size() > 0) {
                    showAreaPopup();
                } else {
                    loadArea();
                }
                break;
            case R.id.llAllPrice2://顶部部分的类型选择
                showPricePopup();
                break;
            case R.id.img_location:
                Intent intent = new Intent();
                intent.setClass(this, SearchMapActivity.class);
                startActivity(intent);
                break;
            case R.id.img_header_icon:
                finish();
                break;
            case R.id.img_search:
                /** 搜索模式 **/
                searchDialog.show();
                break;
        }
    }

    private void showPricePopup() {

        int[] location = new int[2];
        llSortView2.getLocationOnScreen(location);
        if (searchTypePopup == null) {
            NetBarSearchType t0 = new NetBarSearchType();
            t0.setSearchType("全部");
            t0.setStatus(0);
            NetBarSearchType t1 = new NetBarSearchType();
            t1.setSearchType("按距离");
            t1.setStatus(1);
            NetBarSearchType t2 = new NetBarSearchType();
            t2.setSearchType("按热度");
            t2.setStatus(2);
            NetBarSearchType t3 = new NetBarSearchType();
            t3.setSearchType("按活动");
            t3.setStatus(3);
            // 设置默认城市
            types.add(t0);
            types.add(t1);
            types.add(t2);
            types.add(t3);
            searchTypePopup = new SearchTypePopupWindow(this, location, llSortView2.getHeight(),
                    reserationTitle.getHeight(), new SearchTypeItemOnClickListener(), types);
            searchTypePopup.showAtLocation(llSortView2, Gravity.NO_GRAVITY, 0, location[1] + llSortView2.getHeight());

        } else {
            searchTypePopup.showAtLocation(llSortView2, Gravity.NO_GRAVITY, 0, location[1] + llSortView2.getHeight());
        }
    }

    private void showAreaPopup() {
        int[] location = new int[2];
        llSortView2.getLocationOnScreen(location);
        if (areaPopup == null) {
            areaPopup = new AreaPopupWindow(this, areas, location, llSortView2.getHeight(),
                    reserationTitle.getHeight(), new AreaItemOnClickListener());
            areaPopup.showAtLocation(llSortView2, Gravity.NO_GRAVITY, 0, location[1] + llSortView2.getHeight());

        } else {
            areaPopup.showAtLocation(llSortView2, Gravity.NO_GRAVITY, 0, location[1] + llSortView2.getHeight());

        }
    }

    private class AreaItemOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            // TODO Auto-generated method stub
            if (areas.isEmpty()) {
                return;
            }
            City area = areas.get(position);
            areaCode = area.getAreaCode();
            if (areaCode.equals("-1")) {
                areaCode = Constant.currentCity.getAreaCode();
            }
            areaPopup.dismiss();
            page = 1;
            params.put("areaCode", areaCode);
            myScrollView.scrollTo(0, menuHeight2);//定位到顶部
            loadData();
            areaPopup.adapter.setAreaSelected(position);
            //reserationsListView.setSelection(0);
            tvArea.setText(area.getName());
            tvArea2.setText(area.getName());
        }
    }

    /**
     * 加载地区
     */
    private void loadArea() {
        if (isFirstLoadArea) {
            showLoading();
            Map<String, String> params = new HashMap<>();
            if (Constant.isLocation) {
                params.put("code", Constant.currentCity.getAreaCode());
            }
            LogUtil.e("params", " init list  " + params.toString());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.VALIDCHILDREN, params, HttpConstant.VALIDCHILDREN);
        }
    }

    private List<City> initCityData(JSONObject object) throws JSONException {
        List<City> citys = new ArrayList<>();
        if (object.getInt("code") == 0 && object.has("object") && object.getString("result").equals("success")) {
            JSONArray objectJson = object.getJSONArray("object");
            citys = GsonUtil.getList(objectJson.toString(), City.class);
            return citys;
        }
        return citys;
    }

    private class SearchTypeItemOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            // TODO Auto-generated method stub
            NetBarSearchType netBarSearchType = types.get(position);
            type = netBarSearchType.getStatus();
            searchTypePopup.dismiss();
            page = 1;
            params.put("type", type + "");
            myScrollView.scrollTo(0, menuHeight2);//定位到顶部
            loadData();
            searchTypePopup.adapter.setTypeSelected(position);
            tvAll.setText(netBarSearchType.getSearchType());
            tvAll2.setText(netBarSearchType.getSearchType());
        }
    }

    public void toSearchPage(String texString) {
        showLoading();
        initSearchByName();
        netbarName = texString;
        searchNetbar(netbarName);
    }

    public void initSearchByName() {
        page = 1;
        tvTitel.setText("搜索结果");
        btSearch.setImageResource(R.drawable.icon_search);
        btSearch.setTag(R.drawable.icon_search);
        btSearch.setVisibility(View.VISIBLE);
    }

    public void searchNetbar(String netbarName) {
        showLoading();
        Double longitude = Constant.longitude;
        Double latitude = Constant.latitude;
        Map<String, String> params = new HashMap<>();
        params.put("page", page + "");
        params.put("name", netbarName + "");
        params.put("type", "2");
        String lastCityStr = PreferencesUtil.getLastRecreationCity(this);
        /*if (TextUtils.isEmpty(lastCityStr)) {
        if (Constant.currentCity != null && !TextUtils.isEmpty(Constant.currentCity.getAreaCode())) {
            params.put("areaCode", Constant.currentCity.getAreaCode());
        }
    } else {
        String areaCode = GsonUtil.getBean(lastCityStr, City.class).getAreaCode();
        if (!TextUtils.isEmpty(areaCode)) {
            params.put("areaCode", areaCode);
        }
    }*/

        if (Constant.isLocation) {
            params.put("latitude", Constant.latitude + "");
            params.put("longitude", Constant.longitude + "");
            params.put("areaCode", Constant.currentCity.getAreaCode());

        }
        mDatas.clear();
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SEARCH, params, HttpConstant.SEARCH);
    }

    /**
     * 判断是否显示顶部选择框
     */
    private void showTopSelect() {
        llview.setFocusable(true);
        llview.setFocusableInTouchMode(true);
        llview.requestFocus();

        myScrollView.setOnMyScrollListener(this);
        myScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
//                        int scrollY = view.getScrollY();
//                        int height = view.getHeight();
//                        int scrollViewMeasuredHeight = myScrollView.getChildAt(0).getMeasuredHeight();
//                        if (isLast == 0 && llFooterView != null) {
//                            llFooterView.setVisibility(View.VISIBLE);
//                        }
//                        if ((scrollY + height) == scrollViewMeasuredHeight) {//滑到底部
//                            if (isRequestFinish) {//判断是否加载完成，加载完成则继续加载
//                                isRequestFinish = false;
//                                loadMoreData();
//                            }
//                        }
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 加载更多数据
     */
    private void loadMoreData() {
        if (isLast == 0) {//0代表可以加载更多
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    page++;
                    loadData();
                }
            }, 500);
        } else {
            showToast(context.getResources().getString(R.string.nomore));
        }
    }


    @Override
    public void move(int x, int y, int oldx, int oldy) {
        if (y > menuHeight2) {
            llSortView2.setVisibility(View.VISIBLE);
            viewLine2.setVisibility(View.VISIBLE);
            llSortView.setVisibility(View.INVISIBLE);
        } else {
            llSortView2.setVisibility(View.INVISIBLE);
            viewLine2.setVisibility(View.INVISIBLE);
            llSortView.setVisibility(View.VISIBLE);
        }
        LogUtil.d("move","menuHeight2"+menuHeight2+":::"+y+":::"+oldy);
        if (oldy != y) {
            LogUtil.d("move","11111111");
            int scrollY = myScrollView.getScrollY();
            int height = myScrollView.getHeight();
            int scrollViewMeasuredHeight = myScrollView.getChildAt(0).getMeasuredHeight();
            if (isLast == 0 && llFooterView != null) {
                llFooterView.setVisibility(View.VISIBLE);
            }
            if ((scrollY + height) == scrollViewMeasuredHeight) {//滑到底部
                LogUtil.d("move","22222222");
                if (isRequestFinish) {//判断是否加载完成，加载完成则继续加载
                    isRequestFinish = false;
                    loadMoreData();
                }
            }
        }
    }
}
