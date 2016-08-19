package com.miqtech.master.client.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.MatchCorpsAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.Corps;
import com.miqtech.master.client.entity.MatchNetbar;
import com.miqtech.master.client.entity.MatchSchedule;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.MatchCorpsPopWindow;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshRecyclerView;
import com.miqtech.master.client.watcher.Observerable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MatchCorpsActivityV2 extends BaseActivity implements OnClickListener, MatchCorpsAdapter.OnItemClickListener,
        MatchCorpsPopWindow.FilterCallback,
        Observerable.ISubscribe {

    RecyclerView rvMatchCorps;

    @Bind(R.id.prrvMatchCorps)
    PullToRefreshRecyclerView prrvMatchCorps;

    private int page = 1;

    private String matchId;

    private List<Corps> mData = new ArrayList<>();

    private MatchCorpsAdapter matchCorpsAdapter;

    private int lastVisiableItem = -1;

    private int isLast = 0;

    private LinearLayoutManager layoutManager = new LinearLayoutManager(this);

    private MatchCorpsPopWindow popWindow;

    private View mFilter;

    private List<MatchSchedule> mMatchSchedules;

    private Map<String, String> params = new HashMap<>();

    private String netbarName;

    private String firstDate;

    private boolean showPop = false;

    private Observerable observerable = Observerable.getInstance();

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_match_corps_v2);

        ButterKnife.bind(this);

        observerable.subscribe(Observerable.ObserverableType.APPLYSTATE, this);

        matchId = getIntent().getStringExtra("matchId");

        firstDate = getIntent().getStringExtra("schedule");
        netbarName = getIntent().getStringExtra("netbar");
        if (TextUtils.isEmpty(netbarName)) {
            netbarName = "";
        }
        loadAppliedCorps(params);

        initView();

    }

    @Override
    protected void onDestroy() {
        observerable.unSubscribe(Observerable.ObserverableType.APPLYSTATE, this);
        observerable = null;
        super.onDestroy();
    }

    @Override
    protected void initView() {
        prrvMatchCorps.setMode(PullToRefreshBase.Mode.BOTH);
        rvMatchCorps = prrvMatchCorps.getRefreshableView();
        prrvMatchCorps.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                page = 1;
                loadAppliedCorps(params);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (isLast == 0) {
                    matchCorpsAdapter.setShowMore(true);
                    matchCorpsAdapter.notifyDataSetChanged();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            page++;
                            loadAppliedCorps(params);
                        }
                    }, 1000);
                } else {
                    prrvMatchCorps.onRefreshComplete();
                    showToast(getResources().getString(R.string.load_no));
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getString(R.string.noNeteork));
                }
            }
        });
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        setLeftIncludeTitle("已报战队");

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvMatchCorps.setLayoutManager(layoutManager);

        matchCorpsAdapter = new MatchCorpsAdapter(this, mData, DateUtil.strToDatePinYin(firstDate) + netbarName);

        matchCorpsAdapter.setOnitemclickListener(this);
        rvMatchCorps.setAdapter(matchCorpsAdapter);

    }

    private void loadAppliedCorps(Map<String, String> params) {
        showLoading();
        params.put("activityId", matchId);
        //params.put("activityId", "21");
        params.put("page", page + "");
        User user = WangYuApplication.getUser(this);
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
        }
        LogUtil.e("params", "params : " + params.toString());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ACTIVITY_APPLIEDTEAMS_V2, params, HttpConstant.ACTIVITY_APPLIEDTEAMS_V2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        prrvMatchCorps.onRefreshComplete();
        matchCorpsAdapter.setShowMore(false);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        prrvMatchCorps.onRefreshComplete();
        LogUtil.e("object", "object : " + object.toString());
        matchCorpsAdapter.setShowMore(false);
        if (HttpConstant.ACTIVITY_APPLIEDTEAMS_V2.equals(method)) {
            List<Corps> newData = initCorpsData(object);
            try {
                isLast = object.getJSONObject("object").getInt("isLast");
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateCorpsView(newData);
        } else if (HttpConstant.ACTVITY_APPLY_DATES_NETBARS_V2.equals(method)) {
            try {
                mMatchSchedules = GsonUtil.getList(object.getJSONArray("object").toString(), MatchSchedule.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            popWindow = new MatchCorpsPopWindow(this, mMatchSchedules, this);
            showPop = true;
            popWindow.showAsDropDown((View) mFilter.getParent());
        }

    }

    /**
     * json转化为corp数据
     *
     * @param object
     * @return
     */
    private List<Corps> initCorpsData(JSONObject object) {
        try {
            JSONObject dataObj = object.getJSONObject("object");
            List<Corps> newData = GsonUtil.getList(dataObj.getJSONArray("list").toString(), Corps.class);
            return newData;
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void updateCorpsView(List<Corps> data) {
        if (page == 1) {
            mData.clear();
        }
        mData.addAll(data);
        /*matchCorpsAdapter.setTitle(DateUtil.strToDatePinYin(mMatchSchedules.get(0).getDate()) +
                mMatchSchedules.get(0).getAreas().get(0).getNetbars().get(0).getName());*/
        matchCorpsAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(View view, int postion) {
        Corps corps = null;
        LogUtil.e("postion", "index  ppp : " + postion);
        switch (view.getId()) {
            case R.id.tv_filter: // 筛选
                mFilter = view;
                if (popWindow == null) {
                    loadApplyDatesAndNetbars();
                } else {
                    if (!showPop) {
                        popWindow.showAsDropDown((View) view.getParent());
                    }
                }
                break;
            case R.id.bt_join:  //加入战队
                corps = mData.get(postion);
                int status = corps.getStatus();
                if (status == 1 || status == 2) {
                    Intent intent = new Intent();
                    intent.setClass(this, OfficePopupWindowActivity.class);
                    intent.putExtra("typeApply", 3);
                    intent.putExtra("id", matchId);
                    intent.putExtra("teamid", corps.getTeam_Id() + "");
                    intent.putExtra("date", DateUtil.strToDatePinYin(firstDate));
                    intent.putExtra("address", netbarName);
                    intent.putExtra("teamname", corps.getTeam_name() + "");
                    User user = WangYuApplication.getUser(this);
                    if (user != null) {
                        startActivity(intent);
                        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_from_top);
                    } else {
                        Intent login = new Intent(this, LoginActivity.class);
                        startActivity(login);
                    }
                } /*else if (status == 2) {
                    showToast("所选日期未开始报名");
                } */ else if (status == 3) {
                    showToast("所选日期已停止报名");
                } else if (status == 4) {
                    //赛事结束
                    showToast("当日比赛已结束，请选择其他日期");
                } else if (status == 5) {
                    //赛事进行中
                    showToast("赛事已经开始，请选择其他日期");
                }

                break;
            case R.id.ll_item:
                corps = mData.get(postion);
                Intent intent = new Intent(this, CorpsDetailsV2Activity.class);
                intent.putExtra("teamId", corps.getTeam_Id());
                intent.putExtra("matchId", matchId);
                startActivity(intent);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
            showPop = false;
        } else {
            super.onBackPressed();
        }

    }

    /**
     * 获取比赛场次，日期
     */
    private void loadApplyDatesAndNetbars() {
        Map<String, String> params = new HashMap<>();
        params.put("activityId", matchId);
        //params.put("activityId", "21");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ACTVITY_APPLY_DATES_NETBARS_V2, params, HttpConstant.ACTVITY_APPLY_DATES_NETBARS_V2);
    }

    @Override
    public void callback(int scheduleId, long netbarId, int areaId) {
        showPop = false;
        if (scheduleId == -1) {
            return;
        }
        page = 1;
        params.put("round", scheduleId + "");
        params.put("netbarId", netbarId + "");
        matchCorpsAdapter.setTitle(DateUtil.strToDatePinYin(mMatchSchedules.get(scheduleId - 1).getDate()) + getNetbarName(scheduleId, netbarId, areaId)
        );
        matchCorpsAdapter.notifyItemChanged(0);
        loadAppliedCorps(params);
    }

    private String getNetbarName(int matchId, long netbarId, int areaId) {
        List<MatchNetbar> netbars = mMatchSchedules.get(matchId - 1).getAreas().get(areaId).getNetbars();
        for (MatchNetbar netbar : netbars) {
            if (netbar.getId() == netbarId) {
                return netbar.getName();
            }
        }
        return "";
    }

    @Override
    public <T> void update(T... data) {
        loadAppliedCorps(params);
    }
}
