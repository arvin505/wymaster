package com.miqtech.master.client.ui;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.RewardPrizeAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.Reward;
import com.miqtech.master.client.entity.RewardGrade;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 悬赏令得奖名单
 * Created by zhaosentao on 2016/7/28.
 */
public class RewardThePrizeActivity extends BaseActivity {

    @Bind(R.id.rewardPrizeListView)
    PullToRefreshListView pullToRefreshListView;
    @Bind(R.id.reawrdPrizeLlBack)
    LinearLayout llBack;
    @Bind(R.id.rewardPrizeTitle)
    TextView tvTitle;

    private HasErrorListView listView;
    private Context context;
    private User user;
    private String bountyId;
    private RewardPrizeAdapter adapter;
    private List<RewardGrade> rewardGradeList = new ArrayList<RewardGrade>();
    private int page = 1;
    private int pageSize = 10;

    private int countAll;
    private int isLast = 0;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_reward_the_prize);
        ButterKnife.bind(this);
        context = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        bountyId = getIntent().getStringExtra("bountyId");

        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshListView.setScrollingWhileRefreshingEnabled(true);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setErrorView(getResources().getString(R.string.no_bounty_hunter_list));

        adapter = new RewardPrizeAdapter(context, rewardGradeList);
        listView.setAdapter(adapter);

        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if (isLast != 1) {
                    page++;
                    loadData();
                }else{
                    showToast(getResources().getString(R.string.load_no));
                    pullToRefreshListView.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    listView.setErrorView(getResources().getString(R.string.error_network));
                    listView.setErrorShow(true);
                }
            }
        });

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        loadData();
    }

    private void loadData() {
        user = WangYuApplication.getUser(context);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
        }
        map.put("bountyId", bountyId);
        map.put("page", page + "");
        map.put("pageSize", pageSize + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.BOUNTY_AWARD, map, HttpConstant.BOUNTY_AWARD);
    }


    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        pullToRefreshListView.onRefreshComplete();
        try {
            if (method.equals(HttpConstant.BOUNTY_AWARD)) {
                JSONObject jsonObject = object.getJSONObject("object");
                if (0 == jsonObject.getInt("total")) {
                    listView.setErrorView(getResources().getString(R.string.no_bounty_hunter_list));
                    listView.setErrorShow(true);
                } else {
                    if (page == 1) {
                        rewardGradeList.clear();
                        countAll = jsonObject.getInt("total");
                        isLast = jsonObject.getInt("isLast");
                        tvTitle.setText(getResources().getString(R.string.a_bounty_hunter_list) + "(" + jsonObject.getInt("total") + ")");
                    }

                    List<RewardGrade> rewardGrades = new Gson().fromJson(jsonObject.getString("list").toString(), new TypeToken<List<RewardGrade>>() {
                    }.getType());

                    rewardGradeList.addAll(rewardGrades);
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        pullToRefreshListView.onRefreshComplete();
        listView.setErrorView(getResources().getString(R.string.error_network));
        listView.setErrorShow(true);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        pullToRefreshListView.onRefreshComplete();
        listView.setErrorView(getResources().getString(R.string.error_network));
        listView.setErrorShow(true);
    }
}
