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
import com.miqtech.master.client.adapter.CompositiveMatchAdapter;
import com.miqtech.master.client.adapter.SearchHistoryAdapter;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.CompositiveMatchInfo;
import com.miqtech.master.client.http.HttpConstant;

import com.miqtech.master.client.ui.OfficalEventActivity;
import com.miqtech.master.client.ui.RecreationMatchDetailsActivity;
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
 * Created by Administrator on 2015/12/30.
 */
public class FragmentActivitySearch extends SearchBaseFragment implements SearchHistoryAdapter.OnItemClick {

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

    private boolean newSearch = false;

    private final String SEARCH_TYPE = "1";

    private boolean isLoadmore = false;
    private List<CompositiveMatchInfo> mDatas = new ArrayList<>();
    private CompositiveMatchAdapter adapter;

    private String key;
    private Map<String, String> params = new HashMap<>();
    private View header;
    private String oldKey = "";

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
        lvSearch.setErrorView("并没有搜到指定赛事", R.drawable.default_search);
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
        history = PreferencesUtil.readActivityHistory(getActivity());
        historyAdapter = new SearchHistoryAdapter(getActivity(), history, this);
        if (history != null && !history.isEmpty()) {
            addHeadView();
        }
        lvSearchHistory.setAdapter(historyAdapter);
        adapter = new CompositiveMatchAdapter(getContext(), mDatas);
        if (header == null) {
            header = getSearchHeadView();
        }
        lvSearch.addHeaderView(header);
        lvSearch.setAdapter(adapter);
        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position <= mDatas.size()) {
                    CompositiveMatchInfo info = mDatas.get(position - 1);
                    if (info != null) {
                        Intent intent = new Intent();
                        switch (info.getType()) {
                            case 0:
                            case 4:
                                intent.setClass(getContext(), OfficalEventActivity.class);
                                intent.putExtra("matchId", info.getId() + "");
                                startActivity(intent);
                                break;
                            case 1:
                            case 2:
                            case 3:
                                intent.setClass(getContext(), RecreationMatchDetailsActivity.class);
                                intent.putExtra("id", info.getId() + "");
                                startActivity(intent);
                                break;
                        }


                    }
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
        PreferencesUtil.saveActivityHistory(getContext(), key);
        searchIng = true;
        tvSearchKey.setText(getString(R.string.search_key, key));
        params.put("page", page + "");
        params.put("name", key + "");
        params.put("type", SEARCH_TYPE);
        String lastCityStr = PreferencesUtil.getLastRecreationCity(getContext());
/*        if (TextUtils.isEmpty(lastCityStr)) {
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
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SEARCH, params, HttpConstant.SEARCH);
    }

    @Override
    public void changeText(String key) {  //
        if (lvSearchHistory != null) {
            lvSearchHistory.setVisibility(View.GONE);
        }
        lvSearch.setVisibility(View.GONE);
        llSearchKey.setVisibility(View.VISIBLE);
        tvSearchKey.setText(getString(R.string.search_key, key));
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
        PreferencesUtil.removeHistory(getContext(), PreferencesUtil.ACTIVITY_HISTORY, key);
        history = PreferencesUtil.readActivityHistory(getContext());
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
                PreferencesUtil.removeHistory(getContext(), PreferencesUtil.ACTIVITY_HISTORY);
                history = PreferencesUtil.readHistory(1);
                historyAdapter.setData(history);
                historyAdapter.notifyDataSetChanged();
            }
        });
        lvSearchHistory.addHeaderView(headView);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        refreshLayout.setVisibility(View.VISIBLE);
        llSearchKey.setVisibility(View.GONE);
        lvSearchHistory.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);
        lvSearch.setVisibility(View.VISIBLE);

        if (isLoadmore) {
            refreshLayout.setLoading(false);
        }
        LogUtil.e("data", object.toString());
        if (HttpConstant.SEARCH.equals(method)) {
            try {
                List<CompositiveMatchInfo> list = initMatchInfo(object);
                if (list != null && !list.isEmpty()) {
                    initMatchView(list);
                } else {
                    initMatchView(list);
                    if (page == 1) {
                        showToast("未找到" + key);
                    }
                    if (isLoadmore) {
                        page--;
                        showToast(R.string.nomore);
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.e("xiaoyi", "eeeeee");
            }
        }
        isLoadmore = false;

    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        refreshLayout.setRefreshing(false);
        refreshLayout.setVisibility(View.VISIBLE);
        lvSearchHistory.setVisibility(View.GONE);
        if (mDatas != null && !mDatas.isEmpty()) {
            lvSearch.setErrorShow(false);
        } else {
            lvSearch.setErrorShow(true);
        }
        if (isLoadmore) {
            refreshLayout.setLoading(false);
        }
        isLoadmore = false;

    }

    private List<CompositiveMatchInfo> initMatchInfo(JSONObject object) throws JSONException {
        List<CompositiveMatchInfo> matchs = new ArrayList<>();
        if (object.getInt("code") == 0 && object.has("object") && object.getString("result").equals("success")) {
            JSONObject objectJson = object.getJSONObject("object");
            if (objectJson.has("list")) {
                JSONArray dataJson = objectJson.getJSONArray("list");
                matchs = GsonUtil.getList(dataJson.toString(), CompositiveMatchInfo.class);
                return matchs;
            }
        }
        return matchs;
    }

    private void initMatchView(List<CompositiveMatchInfo> matchs) {
        if (page == 1) {
            mDatas.clear();
        }
        mDatas.addAll(matchs);
        adapter.setData(mDatas);
        if (!mDatas.isEmpty()) {
            lvSearch.setErrorShow(false);
        } else {
            lvSearch.setErrorShow(true);
        }
        if (newSearch) {
            newSearch = false;
            lvSearch.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }
}
