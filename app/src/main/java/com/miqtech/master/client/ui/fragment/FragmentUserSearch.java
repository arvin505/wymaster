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
import com.miqtech.master.client.adapter.SearchHistoryAdapter;
import com.miqtech.master.client.adapter.UserSearchAdapter;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.PersonalHomePageActivity;
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
public class FragmentUserSearch extends SearchBaseFragment implements SearchHistoryAdapter.OnItemClick {
    private static final String SEARCH_TYPE = "3";
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
    private List<User> mDatas = new ArrayList<>();

    private String oldKey = "";

    private String key;
    private Map<String, String> params = new HashMap<>();

    private UserSearchAdapter adapter;
    private View header;

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
        lvSearch.setErrorView("并木有搜索到相关用户",R.drawable.default_search);
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
        history = PreferencesUtil.readUserHistory(getActivity());
        historyAdapter = new SearchHistoryAdapter(getActivity(), history, this);
        if (history != null && !history.isEmpty()) {
            addHeadView();
        }
        lvSearchHistory.setAdapter(historyAdapter);
        if (header == null) {
            header = getSearchHeadView();
        }
        lvSearch.addHeaderView(header);
        adapter = new UserSearchAdapter(getContext(), mDatas);
        lvSearch.setAdapter(adapter);
        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0 && position <= mDatas.size()) {
                    User user = mDatas.get(position - 1);
                    Intent intent = new Intent(getContext(), PersonalHomePageActivity.class);
                    intent.putExtra("id", user.getId() + "");
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
            LogUtil.e("params", "list : key " + key);
        }

        PreferencesUtil.saveUserHistory(getContext(), key);
        searchIng = true;
        tvSearchKey.setText(getString(R.string.search_key, key));
        params.put("page", page + "");
        params.put("name", key + "");
        params.put("type", SEARCH_TYPE);
/*        String lastCityStr = PreferencesUtil.getLastRecreationCity(getContext());
        if (TextUtils.isEmpty(lastCityStr)) {
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
        lvSearchHistory.setVisibility(View.GONE);
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
        PreferencesUtil.removeHistory(getContext(), PreferencesUtil.USER_HISTORY, key);
        history = PreferencesUtil.readUserHistory(getContext());
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
                PreferencesUtil.removeHistory(getContext(), PreferencesUtil.USER_HISTORY);
                history = PreferencesUtil.readHistory(2);
                historyAdapter.setData(history);
                historyAdapter.notifyDataSetChanged();
            }
        });
        lvSearchHistory.addHeaderView(headView);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        llSearchKey.setVisibility(View.GONE);
        lvSearch.setVisibility(View.VISIBLE);
        lvSearchHistory.setVisibility(View.GONE);
        refreshLayout.setVisibility(View.VISIBLE);
        super.onSuccess(object, method);
        refreshLayout.setRefreshing(false);

        if (isLoadmore) {
            refreshLayout.setLoading(false);
        }
        LogUtil.e("data", object.toString());
        if (HttpConstant.SEARCH.equals(method)) {
            try {
                List<User> list = initUserData(object);
                if (list != null && !list.isEmpty()) {
                    initUserList(list);
                } else {
                    if (isLoadmore) {
                        page--;
                        showToast(R.string.nomore);
                    }
                    initUserList(list);
                    if (page == 1) {
                        showToast("未找到" + key);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        isLoadmore = false;
    }


    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        refreshLayout.setVisibility(View.VISIBLE);
        llSearchKey.setVisibility(View.GONE);
        lvSearch.setVisibility(View.VISIBLE);
        if (mDatas.isEmpty()) {
            lvSearch.setErrorShow(true);
        } else {
            lvSearch.setErrorShow(false);
        }
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        refreshLayout.setVisibility(View.VISIBLE);
        llSearchKey.setVisibility(View.GONE);
        lvSearch.setVisibility(View.VISIBLE);
        lvSearchHistory.setVisibility(View.GONE);
        refreshLayout.setRefreshing(false);
        if (isLoadmore) {
            refreshLayout.setLoading(false);
        }
        isLoadmore = false;
        if (mDatas.isEmpty()) {
            lvSearch.setErrorShow(true);
        } else {
            lvSearch.setErrorShow(false);
        }
    }

    private List<User> initUserData(JSONObject object) throws JSONException {
        List<User> users = new ArrayList<>();
        if (object.getInt("code") == 0 && object.has("object") && object.getString("result").equals("success")) {
            JSONObject objectJson = object.getJSONObject("object");
            if (objectJson.has("list")) {
                JSONArray dataJson = objectJson.getJSONArray("list");
                users = GsonUtil.getList(dataJson.toString(), User.class);
                //total = objectJson.getInt("total");
                return users;
            }
        }
        return users;
    }

    private void initUserList(List<User> datas) {
        if (page == 1) {
            mDatas.clear();
        }
        mDatas.addAll(datas);
        adapter.setData(mDatas);
        if (!mDatas.isEmpty()) {
            lvSearch.setErrorShow(false);
        } else {
            lvSearch.setErrorShow(true);
        }
        if (newSearch) {
            lvSearch.setAdapter(adapter);
            newSearch = false;
        }
        adapter.notifyDataSetChanged();
    }
}
