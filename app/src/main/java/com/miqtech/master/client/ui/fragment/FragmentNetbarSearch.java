package com.miqtech.master.client.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.RecommendNetbarV2Adapter;
import com.miqtech.master.client.adapter.SearchHistoryAdapter;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.InternetBarInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.InternetBarActivityV2;
import com.miqtech.master.client.ui.basefragment.SearchBaseFragment;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.RefreshLayout;

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
 * Created by Administrator on 2015/12/31.
 */
public class FragmentNetbarSearch extends SearchBaseFragment implements SearchHistoryAdapter.OnItemClick {
    private static final String SEARCH_TYPE = "2";
    @Bind(R.id.lv_history)
    ListView lvSearchHistory;
    @Bind(R.id.refresh_search_activity)
    RefreshLayout refreshLayout;
    @Bind(R.id.lv_search_activity)
    HasErrorListView lvSearch;

    @Bind(R.id.ll_search_key)
    LinearLayout llSearchKey;
    @Bind(R.id.tv_search_key)
    TextView tvSearchKey;


    private SearchHistoryAdapter historyAdapter;
    private List<String> history;
    private View headView;

    private int page = 1;
    private boolean isLoadmore = false;
    private List<InternetBarInfo> mDatas = new ArrayList<>();
    private RecommendNetbarV2Adapter adapter;
    private View header;
    private String key;
    private Map<String, String> params = new HashMap<>();

    private String oldKey = "";

    private boolean newSearch = false;

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_activity, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initView();
        initData();
    }

    private void initView() {
        refreshLayout.setColorSchemeResources(R.color.colorActionBarSelected);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                search(key);
            }
        });
        refreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                page++;
                isLoadmore = true;
                search(key);
            }
        });
    }

    private void initData() {
        history = PreferencesUtil.readNetbarHistory(getActivity());
        historyAdapter = new SearchHistoryAdapter(getActivity(), history, this);
        if (history != null && !history.isEmpty()) {
            addHeadView();
        }
        lvSearchHistory.setAdapter(historyAdapter);

        adapter = new RecommendNetbarV2Adapter(getContext(), mDatas,2);
        if (header == null) {
            header = getSearchHeadView();
        }
        lvSearch.addHeaderView(header);
        lvSearch.setErrorView("并木有搜索到相关网吧",R.drawable.default_search);
        lvSearch.setAdapter(adapter);
        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0 && position <= mDatas.size()) {
                    InternetBarInfo info = mDatas.get(position - 1);
                    Intent intent = new Intent();
                    intent.setClass(getContext(), InternetBarActivityV2.class);
                    intent.putExtra("netbarId", info.getId());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void search(String key) {  //搜索
        showLoading();
        this.key = key;
        if (!oldKey.equals(key)) {
            page = 1;
            oldKey = key;
            newSearch = true;
        }
        PreferencesUtil.saveNetbarHistory(getContext(), key);
        searchIng = true;
        tvSearchKey.setText(getString(R.string.search_key, key));
        params.put("page", page + "");
        params.put("name", key + "");
        params.put("type", SEARCH_TYPE);
        String lastCityStr = PreferencesUtil.getLastRecreationCity(getContext());
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
            //params.put("areaCode", Constant.currentCity.getAreaCode());

        }
        LogUtil.e("tag", "url = " + HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SEARCH);
        LogUtil.e("tag", "param = " + params.toString());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SEARCH, params, HttpConstant.SEARCH);

    }

    @Override
    public void changeText(String key) {  //
        /**
         * umeng 报了一个NullpointerException
         * 感觉这里应该不会报空才对，这里只是设置控件显示
         * 暂时想不明白， 所以try catch  by xiaoyi
         */
        try {
            lvSearchHistory.setVisibility(View.GONE);
            lvSearch.setVisibility(View.GONE);
            llSearchKey.setVisibility(View.VISIBLE);
            tvSearchKey.setText(getString(R.string.search_key, key));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClickItem(String key) {
        lvSearchHistory.setVisibility(View.GONE);
        llSearchKey.setVisibility(View.VISIBLE);
        tvSearchKey.setText(getString(R.string.search_key, key));
        setSearchKey(key);
        search(key);
    }

    @Override
    public void deleteItem(String key) {
        PreferencesUtil.removeHistory(getContext(), PreferencesUtil.NETBAR_HISTORY, key);
        history = PreferencesUtil.readNetbarHistory(getContext());
        if (history.isEmpty() || history.size() <= 0) {
            lvSearchHistory.removeHeaderView(headView);
        }
        historyAdapter.setData(history);
        LogUtil.e("adapter", "his == " + history.toString());
        historyAdapter.notifyDataSetChanged();
    }

    private void addHeadView() {
        if (headView == null) {
            headView = LayoutInflater.from(getContext()).inflate(R.layout.layout_search_his_head, null);
        }
        ImageView clean = (ImageView) headView.findViewById(R.id.tv_clean);
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesUtil.removeHistory(getContext(), PreferencesUtil.NETBAR_HISTORY);
                history = PreferencesUtil.readHistory(0);
                historyAdapter.setData(history);
                historyAdapter.notifyDataSetChanged();
            }
        });
        lvSearchHistory.addHeaderView(headView);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        refreshLayout.setRefreshing(false);
        refreshLayout.setVisibility(View.VISIBLE);
        llSearchKey.setVisibility(View.GONE);
        lvSearch.setVisibility(View.VISIBLE);
        lvSearchHistory.setVisibility(View.GONE);
        hideLoading();
        if (isLoadmore) {
            refreshLayout.setLoading(false);
        }
        LogUtil.e("data", object.toString());

        if (HttpConstant.SEARCH.equals(method)) {
            try {
                List<InternetBarInfo> list = null;
                hideLoading();
                try {
                    list = initSearchList(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (list != null && !list.isEmpty()) {
                    initNetbarList(list);
                } else {
                    initNetbarList(list);
                    if (page == 1){
                        showToast("未找到" + key);
                    }else {
                        showToast(R.string.nomore);
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("xiaoyi", "eeeeee");
            }

            isLoadmore = false;
        }
    }

    private void initNetbarList(List<InternetBarInfo> datas) {
        if (page == 1) {
            mDatas.clear();
        }
        LogUtil.e("view", " init list  " + datas.size());
        mDatas.addAll(datas);
        if (mDatas == null || mDatas.isEmpty()) {
            lvSearch.setErrorShow(true);
        } else {
            lvSearch.setErrorShow(false);
        }
        if (newSearch) {
            lvSearch.setAdapter(adapter);
            newSearch = false;
        }
        adapter.setData(mDatas);
        adapter.notifyDataSetChanged();
        LogUtil.e("change", "notify");
    }


    private List<InternetBarInfo> initSearchList(JSONObject object) throws JSONException {
        List<InternetBarInfo> netbars = new ArrayList<>();
        if (object.getInt("code") == 0 && object.has("object") && object.getString("result").equals("success")) {
            JSONObject objectJson = object.getJSONObject("object");
            if (objectJson.has("list")) {
                JSONArray array = objectJson.getJSONArray("list");
                netbars = GsonUtil.getList(array.toString(), InternetBarInfo.class);
            }
        }
        return netbars;
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        refreshLayout.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(false);
        llSearchKey.setVisibility(View.GONE);
        lvSearch.setErrorShow(true);
        lvSearchHistory.setVisibility(View.GONE);
        if (isLoadmore) {
            refreshLayout.setLoading(false);
        }
        isLoadmore = false;
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        refreshLayout.setVisibility(View.VISIBLE);
        if (page == 1) {
            lvSearch.setErrorShow(true);
        }

    }
}
