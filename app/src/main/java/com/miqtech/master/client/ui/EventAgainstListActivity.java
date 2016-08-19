package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.EventAgainstExpandableAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.EventAgainst;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 赛事对阵列表
 * Created by zhaosentao on 2016/7/5.
 */
public class EventAgainstListActivity extends BaseActivity implements
        PinnedHeaderExpandableListView.OnHeaderUpdateListener {

    @Bind(R.id.eventAgainstPinnedListView)
    PinnedHeaderExpandableListView pinnedListView;
    @Bind(R.id.eventAgainstListViewStub)
    ViewStub viewStub;

    private Context context;
    private User user;
    private String roundId;//赛事场次id
    private String turn;//轮次
    private ArrayList<EventAgainst> eventAgainstList = new ArrayList<EventAgainst>();
    private String title = "";
    private String imgUrl = "";
    private int position;//需要把哪个轮次置顶的position
    private View errorView;


    private EventAgainstExpandableAdapter adapter;


    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_event_against_list);
        ButterKnife.bind(this);
        context = this;
        roundId = getIntent().getStringExtra("roundId");//赛事场次id
        turn = getIntent().getStringExtra("turn");//轮次
        title = getIntent().getStringExtra("title");//赛事标题
        position = getIntent().getIntExtra("position", 0);//
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setLeftBtnImage(R.drawable.back);
        setRightTextView(getResources().getString(R.string.look_at_event_against));
        getRightTextview().setTextColor(getResources().getColor(R.color.shop_font_black));
        getRightTextview().setTextSize(14);
        getButtomLineView().setBackgroundColor(getResources().getColor(R.color.shop_pay_splite_line));
        setLeftIncludeTitle(getResources().getString(R.string.the_event_process));
        adapter = new EventAgainstExpandableAdapter(context, eventAgainstList);
        pinnedListView.setAdapter(adapter);
        loadData();
    }


    @OnClick({R.id.ibLeft, R.id.tvRightHandle})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.tvRightHandle:
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("eventAgainstList", eventAgainstList);
                bundle.putString("title", title);
                bundle.putString("imgUrl", imgUrl);
                Intent intent = new Intent(context, EventAgainstMapActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    private void loadData() {
        if (TextUtils.isEmpty(roundId)) {
            showEmpty(1);
            return;
        }
        showLoading();
        user = WangYuApplication.getUser(context);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
        }
        map.put("roundId", roundId);
        if (!TextUtils.isEmpty(turn)) {
            map.put("turn", turn);
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.EVENT_PROCESS_LIST, map, HttpConstant.EVENT_PROCESS_LIST);
    }

    private void initExpandableListView(int total) {
        pinnedListView.setOnHeaderUpdateListener(this);// 必须在Adapter实例化之后执行
        pinnedListView.setGroupIndicator(null);// 去掉默认的指示箭头
        for (int i = 0; i < total; i++) {
            pinnedListView.expandGroup(i);
        }

        pinnedListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        },false);
    }


    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        try {
            if (method.equals(HttpConstant.EVENT_PROCESS_LIST)) {
                if (object.has("object")) {
                    JSONObject jsonObject = new JSONObject(object.getString("object").toString());
                    List<EventAgainst> newEventAgainst = new Gson().fromJson(jsonObject.getString("process").toString(), new TypeToken<List<EventAgainst>>() {
                    }.getType());
                    eventAgainstList.addAll(newEventAgainst);
                    if (newEventAgainst.isEmpty()) {
                        showEmpty(1);
                    } else {
                        adapter.notifyDataSetChanged();
                        initExpandableListView(eventAgainstList.size());
                        if (position > 1) {
                            pinnedListView.setSelectedGroup(position - 1);
                        }
                    }
                    imgUrl = jsonObject.optString("poster");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        if (method.equals(HttpConstant.EVENT_PROCESS_LIST)) {
            showEmpty(1);
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        if (method.equals(HttpConstant.EVENT_PROCESS_LIST)) {
            showEmpty(2);
        }
    }


    @Override
    public View getPinnedHeader() {
        View headerView = getLayoutInflater().inflate(R.layout.layout_event_against_item, null);
        headerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return headerView;
    }

    @Override
    public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {
        //修改固定头部的标题
        TextView textView = (TextView) headerView.findViewById(R.id.eventAgsinstTvTitle);
        EventAgainst eventAgainst = (EventAgainst) adapter.getGroup(firstVisibleGroupPos);
        textView.setText(eventAgainst.getName());// 更新标题
    }

    /**
     * @param showErrorType 显示错误的显示方式，0表示正常,1表示无该进程，2表示网络错误
     */
    private void showEmpty(int showErrorType) {
        if (errorView == null) {
            viewStub.setLayoutResource(R.layout.exception_page);
            errorView = viewStub.inflate();
            TextView textView = (TextView) errorView.findViewById(R.id.tv_err_title);
            if (showErrorType == 1) {
                textView.setText(context.getResources().getString(R.string.the_event_process_no));
            } else if (showErrorType == 2) {
                textView.setText(context.getResources().getString(R.string.error_network));
            }
            pinnedListView.setVisibility(View.GONE);
        }
    }
}
