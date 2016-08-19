package com.miqtech.master.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.MatchApplyListAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.RefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MatchApplyListActivity extends BaseActivity implements OnClickListener, MatchApplyListAdapter.HandleJoinStatusListener {
    private HasErrorListView lvApply;
    private MatchApplyListAdapter adapter;
    private Context context;
    private List<User> teammates = new ArrayList<User>();
    private int id;
    private ImageView back;
    private TextView right;
    private RefreshLayout refreshLayout;
    private int isRefresh = 0;//用来判断是否在返回时刷新上一个界面.0表示不需要1表示需要

    @Override
    protected void init() {
        super.init();
        context = this;
        setContentView(R.layout.activity_match_apply_list);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        lvApply = (HasErrorListView) findViewById(R.id.lvApply);
        adapter = new MatchApplyListAdapter(context, teammates, this);
        back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(this);
        lvApply.setErrorView(R.string.no_member_apply, R.drawable.icon_apply_list);
        lvApply.setAdapter(adapter);
        right = (TextView) findViewById(R.id.tvHandle);
        right.setText("清空列表");
        setLeftIncludeTitle("申请列表");
        //setLeftBtnImage(R.drawable.back);
        //getLeftBtn().setOnClickListener(this);
        right.setOnClickListener(this);
        refreshLayout = (RefreshLayout) findViewById(R.id.refresh);
        refreshLayout.setColorSchemeResources(R.color.colorActionBarSelected);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadTeamApplyList(id);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        id = getIntent().getIntExtra("id", -1);
        if (id != -1) {
            loadTeamApplyList(id);
        }
    }

    private void loadTeamApplyList(int id) {
        User user = WangYuApplication.getUser(context);
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("teamId", id + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.JOIN_TEAM_APPLY_LIST, params, HttpConstant.JOIN_TEAM_APPLY_LIST);
    }

    private void emptyApplyList(int id) {
        User user = WangYuApplication.getUser(context);
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("teamId", id + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.EMPTY_TEAM_APPLY_LIST, params, HttpConstant.EMPTY_TEAM_APPLY_LIST);
    }

    private void acceptTeammateJoin(String teammateId) {
        User user = WangYuApplication.getUser(context);
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("id", teammateId);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ACCEPT_TEAM_APPLY, params, HttpConstant.ACCEPT_TEAM_APPLY);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        hideLoading();
        LogUtil.e("str", "method == " + object.toString());
        refreshLayout.setRefreshing(false);
        super.onSuccess(object, method);
        if (method.equals(HttpConstant.JOIN_TEAM_APPLY_LIST)) {
            JSONArray obj = null;
            try {
                obj = object.getJSONArray("object");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            List<User> newTeammates = new Gson().fromJson(obj.toString(), new TypeToken<List<User>>() {
            }.getType());
            teammates.clear();
            teammates.addAll(newTeammates);
            adapter.notifyDataSetChanged();
        } else if (method.equals(HttpConstant.ACCEPT_TEAM_APPLY)) {
            showToast("已接受");
            loadTeamApplyList(id);
            isRefresh = 1;
        } else if (method.equals(HttpConstant.EMPTY_TEAM_APPLY_LIST)) {
            showToast("已清空");
            isRefresh = 1;
            teammates.clear();
            adapter.notifyDataSetChanged();
        }
        if (teammates.isEmpty()) {
            lvApply.setErrorShow(true);
        } else {
            lvApply.setErrorShow(false);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isRefresh", isRefresh);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void onError(String method, String errorInfo) {
        super.onError(method, errorInfo);
        hideLoading();
        refreshLayout.setRefreshing(false);
        showToast(errorInfo);
        lvApply.setErrorShow(true);

    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        refreshLayout.setRefreshing(false);
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (teammates.isEmpty()) {
            lvApply.setErrorShow(true);
        } else {
            lvApply.setErrorShow(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvHandle:
                emptyApplyList(id);
                break;

            default:
                break;
        }
    }

    @Override
    public void acceptJoin(String teammateId) {
        acceptTeammateJoin(teammateId);
    }
}
