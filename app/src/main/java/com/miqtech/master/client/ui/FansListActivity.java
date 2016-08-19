package com.miqtech.master.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.FansAdapter;
import com.miqtech.master.client.entity.Fans;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.RefreshLayout;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshListView;

public class FansListActivity extends BaseActivity implements OnClickListener, TextWatcher {
    private PullToRefreshListView prlvFans;
    private HasErrorListView lvFans, lvSearchFans;
    private LinearLayout llSearch;
    private EditText edtSearch;
    private ImageView ivCancelSearch;
    private String id;
    private int page = 1;
    private List<Fans> fans = new ArrayList<Fans>();
    private List<Fans> searchFans = new ArrayList<Fans>();
    private FansAdapter adapter;
    private FansAdapter searchAdapter;
    private int isLast;
    private ImageView back;
    private ImageView search;
    private boolean isLoadMore = false;

    private LinearLayout exception_page;

    private int total = 0;

    protected void init() {
        super.init();
        setContentView(R.layout.activity_fanslist);
        initView();
        initData();
    }

    protected void initView() {
        super.initView();
        prlvFans = (PullToRefreshListView)findViewById(R.id.prlvFans);
        prlvFans.setMode(PullToRefreshBase.Mode.BOTH);
        prlvFans.setScrollingWhileRefreshingEnabled(true);
        lvFans =prlvFans.getRefreshableView();
        lvSearchFans = (HasErrorListView) findViewById(R.id.lvSearchFans);
        lvFans.setErrorView("木有粉丝多没面子啊，赶紧去呼叫小伙伴，一起装X一起飞！！！");

        lvSearchFans = (HasErrorListView) findViewById(R.id.lvSearchFans);
        lvSearchFans.setErrorView("后台君让俺告诉你，木有找到那谁~");
        llSearch = (LinearLayout) findViewById(R.id.llSearch);
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        ivCancelSearch = (ImageView) findViewById(R.id.ivCancelSearch);
        exception_page = (LinearLayout) findViewById(R.id.exception_page);

        prlvFans.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                loadUserFans();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if (fans.size() < total) {
                    page++;
                    loadUserFans();
                    isLoadMore = true;
                }else {
                    prlvFans.onRefreshComplete();
                    showToast(R.string.nomore);
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if(!isHasNetWork) {
                    showToast(getString(R.string.noNeteork));
                }
            }
        });

        back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(this);
        adapter = new FansAdapter(fans, this);
        searchAdapter = new FansAdapter(searchFans, this);
        lvFans.setAdapter(adapter);
        lvSearchFans.setAdapter(searchAdapter);
        setLeftIncludeTitle("粉丝列表");
//        getLeftBtn().setOnClickListener(this);
        //      setRightBtnImage(R.drawable.icon_search);
        //    getRightBtn().setOnClickListener(this);
        search = (ImageView) findViewById(R.id.ivHandle);
        search.setImageResource(R.drawable.icon_search);
        search.setOnClickListener(this);
        edtSearch.addTextChangedListener(this);
        lvFans.setOnItemClickListener(new FansOnClickListener());
        lvSearchFans.setOnItemClickListener(new SearchFansOnClickListener());
        ivCancelSearch.setOnClickListener(this);

    }

    protected void initData() {
        super.initData();
        id = getIntent().getStringExtra("id");

        loadUserFans();
    }

    private void loadUserFans() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("userId", id);
        params.put("page", page + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.USER_FANS, params, HttpConstant.USER_FANS);
    }

    private void searchFans(String str) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", id);
        params.put("nickName", str);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SEARCH_FANS, params, HttpConstant.SEARCH_FANS);
    }

    public void onSuccess(JSONObject object, String method) {
        hideLoading();
       prlvFans.onRefreshComplete();
        LogUtil.e("http", "object == " + object.toString());
        super.onSuccess(object, method);
        try {
            String obj = object.getJSONObject("object").toString();
            if (HttpConstant.USER_FANS.equals(method)) {
                if (TextUtils.isEmpty(obj.toString())) {
                    return;
                }
                try {
                    JSONObject jsonObj = new JSONObject(obj.toString());
                    String listStr = jsonObj.getString("list");
                    isLast = jsonObj.getInt("isLast");
                    total = jsonObj.getInt("total");
                    List<Fans> newFans = new Gson().fromJson(listStr, new TypeToken<List<Fans>>() {
                    }.getType());
                    if (newFans != null && !newFans.isEmpty()) {
                        if (page == 1) {
                            fans.clear();
                        }
                        fans.addAll(newFans);
                        adapter.notifyDataSetChanged();
                        exception_page.setVisibility(View.GONE);
                        if (!fans.isEmpty()){
                            lvFans.setErrorShow(false);
                        }
                    } else {
                        if (isLoadMore) {
                            page--;
                            showToast(R.string.nomore);
                        }else {
                            lvFans.setErrorShow(true);
                        }
                        if (page == 1){
                            exception_page.setVisibility(View.VISIBLE);
                        }
                    }
                    if (isLoadMore) {
                        isLoadMore = false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (HttpConstant.SEARCH_FANS.equals(method)) {
                if (TextUtils.isEmpty(obj.toString())) {
                    return;
                }
                List<Fans> newSearchFans = new Gson().fromJson(obj.toString(), new TypeToken<List<Fans>>() {
                }.getType());
                searchFans.clear();
                searchFans.addAll(newSearchFans);
                lvSearchFans.setErrorShow(true);
                searchAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onError(String method, String errorInfo) {
        hideLoading();
        prlvFans.onRefreshComplete();
//        if (isLoadMore) {
//            refresh_view.setLoading(false);
//        }
        if (fans.isEmpty()){
            lvFans.setErrorShow(true);
        }else {
            lvFans.setErrorShow(false);
        }
        if (searchFans.isEmpty()){
            lvSearchFans.setErrorShow(true);
        }else {
            lvSearchFans.setErrorShow(false);
        }


        super.onError(method, errorInfo);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
      prlvFans.onRefreshComplete();
        if (isLoadMore) {
            isLoadMore = false;
            page--;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.ivHandle:

                if (llSearch.getVisibility() == View.VISIBLE) {

                    llSearch.setVisibility(View.GONE);
                    lvSearchFans.setVisibility(View.GONE);
                    // 关闭输入法
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

                } else if (llSearch.getVisibility() == View.GONE) {

                    llSearch.setVisibility(View.VISIBLE);
                    lvSearchFans.setVisibility(View.VISIBLE);
                    // 打开输入法
                    edtSearch.setFocusable(true);
                    edtSearch.setFocusableInTouchMode(true);
                    edtSearch.requestFocus();
                    InputMethodManager inputManager = (InputMethodManager) edtSearch.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(edtSearch, 0);
                }
                break;
            case R.id.ivCancelSearch:
                edtSearch.setText("");
                break;
            default:
                break;
        }
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    public void afterTextChanged(Editable s) {
        // showToast(edtSearch.getText().toString());
        page = 1;
        ivCancelSearch.setVisibility(View.VISIBLE);
        searchFans(edtSearch.getText().toString());
        if (edtSearch.getText().toString().length() == 0) {
            ivCancelSearch.setVisibility(View.GONE);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (llSearch.getVisibility() == View.GONE) {
                finish();
            } else if (llSearch.getVisibility() == View.VISIBLE) {
                llSearch.setVisibility(View.GONE);
                lvSearchFans.setVisibility(View.GONE);
            }
        }
        return false;
    }

    private class FansOnClickListener implements OnItemClickListener {

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (fans.isEmpty()){
                return;
            }
            Fans fan = fans.get(position);
            Intent intent = new Intent();
            intent.setClass(FansListActivity.this, PersonalHomePageActivity.class);
            intent.putExtra("id", fan.getId() + "");
            startActivity(intent);
        }

    }

    private class SearchFansOnClickListener implements OnItemClickListener {

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Fans fan = searchFans.get(position);
            Intent intent = new Intent();
            intent.setClass(FansListActivity.this, PersonalHomePageActivity.class);
            intent.putExtra("id", fan.getId() + "");
            startActivity(intent);
        }

    }
}
