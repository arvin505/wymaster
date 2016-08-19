package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.ReviewProgressAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.ReviewProgressInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.view.RefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/10 0010.
 */
public class RecreationMatchScheduleActivityV2 extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.rv_audit)
    RecyclerView rvAudit;

    @Bind(R.id.rv_refresh)
    RefreshLayout rvRefresh;

    private Context context;
    private String id;
    private ReviewProgressAdapter adapter;
    private ReviewProgressInfo mInfo;
    private LinearLayoutManager layoutManager;


    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_recreationmatchschedule_v2);
        ButterKnife.bind(this);
        context = this;
        initView();

    }

    @Override
    protected void initView() {
        super.initView();
        setLeftBtnImage(R.drawable.back_bottom_icon);
        setLeftIncludeTitle("审核进度");
        getLeftBtn().setOnClickListener(this);

        rvRefresh.setColorSchemeResources(R.color.orange);
        rvRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadSchedule();
            }
        });

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAudit.setLayoutManager(layoutManager);

        mInfo = new ReviewProgressInfo();
        List<ReviewProgressInfo.State> states = new ArrayList<>();
        List<ReviewProgressInfo.Img> imgs = new ArrayList<>();
        mInfo.setStates(states);
        mInfo.setVerify_imgs(imgs);
        mInfo.setAppeal_imgs(imgs);

        adapter = new ReviewProgressAdapter(this, mInfo);
        rvAudit.setAdapter(adapter);

        initData();
    }

    @Override
    protected void initData() {
        super.initData();
        id = getIntent().getStringExtra("id");
        loadSchedule();
    }

    private void loadSchedule() {
        showLoading();
        HashMap params = new HashMap();
        params.put("activityId", id);
        params.put("userId", WangYuApplication.getUser(context).getId());
        params.put("token", WangYuApplication.getUser(context).getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_VERIFY_PROGRESS, params, HttpConstant.AMUSE_VERIFY_PROGRESS);
        LogUtil.e("param", "object : " + params.toString());
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        rvRefresh.setRefreshing(false);
        LogUtil.e("tag", "object :" + object.toString());
        if (HttpConstant.AMUSE_VERIFY_PROGRESS.equals(method)) {
            updateViews(object);
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        rvRefresh.setRefreshing(false);
    }


    private void updateViews(JSONObject object) {
        try {
            if (object.getInt("code") == 0 && object.has("object")) {
                String strObj = object.getString("object");
                if (!TextUtils.isEmpty(strObj)) {
                    mInfo = null;
                    mInfo = GsonUtil.getBean(strObj, ReviewProgressInfo.class);
                    adapter.setData(mInfo);
                    adapter.notifyDataSetChanged();
                    ReviewProgressInfo.State state = getCurrentState(mInfo.getStates());

                    if (state != null && isShowComplaint(state)) {
                        //if (mInfo.getAppeal_state() == 0) {
                        setRightTextView("申诉");
                        getRightTextview().setVisibility(View.VISIBLE);
                        // } else {
                        //setRightTextView("查看申诉");
                        //  }

                        getRightTextview().setOnClickListener(this);
                    } else {
                        getRightTextview().setVisibility(View.GONE);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isShowComplaint(ReviewProgressInfo.State state) {
        if (state.getState() == 3 || state.getState() == 4) {
            return true;
        } else if (state.getState() == 9) {
            long current = System.currentTimeMillis();
            String now = TimeFormatUtil.mill2Date(current);

            if (mInfo.getAppeal_state() == 2) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }


    }

    private ReviewProgressInfo.State getCurrentState(List<ReviewProgressInfo.State> states) {
        if (states == null || states.isEmpty()) {
            return null;
        }
        ReviewProgressInfo.State state = states.get(0);
        return state;
    }

    @Override
    public void onError(String errMsg, String method) {
        rvRefresh.setRefreshing(false);
        super.onError(errMsg, method);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvRightHandle:
                if (getRightTextview().getText().toString().equals("申诉")) {
                    goAppeal();
                } else {
                    goAppealStatus();
                }
                break;
            case R.id.ibLeft:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.out_from_bottom);
    }

    private void goAppeal() {
        Intent intent = new Intent();
        if (mInfo.getAppeal_state() == 1) {   //审核申述
            intent.setClass(context, RecreationMatchAppealActivity.class);

        } else if (mInfo.getAppeal_state() == 2) {    //发奖申述
            intent.setClass(context, RecreationMatchAppealAwardActivity.class);
            intent.putParcelableArrayListExtra("category", mInfo.getAppeal_category());
        }

        intent.putExtra("id", id);
        startActivityForResult(intent, 9);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 9 && resultCode == 9) {
            loadSchedule();
        }
    }

    private void goAppealStatus() {
        Intent intent = new Intent();
        intent.setClass(context, RecreationMatchAppealStatusActivityV2.class);
        intent.putExtra("id", id);
        //intent.putExtra("recreationMatchAppeal", recreationMatchAppeal);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("recreationMatchAppeal",recreationMatchAppeal);
//        intent.putExtras(bundle);
        startActivity(intent);
    }
}
