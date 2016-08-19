package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.ComplaintAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.RecreationMatchAppeal;
import com.miqtech.master.client.entity.RecreationMatchVerify;
import com.miqtech.master.client.entity.ReviewProgressInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/10 0010.
 */
public class RecreationMatchAppealStatusActivityV2 extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.rv_audit)
    RecyclerView rvAudit;
    private Context context;
    private String id;
    private RecreationMatchAppeal recreationMatchAppeal;
    private ComplaintAdapter adapter;
    private ReviewProgressInfo mInfo;
    private LinearLayoutManager layoutManager;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_appealstatus_v2);
        ButterKnife.bind(this);
        context = this;
        initView();

        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        setLeftBtnImage(R.drawable.back_bottom_icon);
        setLeftIncludeTitle("申诉进度");
        getLeftBtn().setOnClickListener(this);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAudit.setLayoutManager(layoutManager);

        adapter = new ComplaintAdapter(this, mInfo);
        rvAudit.setAdapter(adapter);
    }



    @Override
    protected void initData() {
        super.initData();
        id = getIntent().getStringExtra("id");
        loadSchedule();
    }

    private void loadSchedule() {
        HashMap params = new HashMap();
        params.put("activityId", id);
        params.put("userId", WangYuApplication.getUser(context).getId());
        params.put("token", WangYuApplication.getUser(context).getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_APPEAL_PROGRESS, params, HttpConstant.AMUSE_APPEAL_PROGRESS);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        LogUtil.e("tag", "object :" + object.toString());
        if (HttpConstant.AMUSE_APPEAL_PROGRESS.equals(method)) {
            updateViews(object);
        }
    }

    private void updateViews(JSONObject object) {
        try {
            if (object.getInt("code") == 0 && object.has("object")) {
                String strObj = object.getString("object");
                mInfo = GsonUtil.getBean(strObj, ReviewProgressInfo.class);
                adapter.setData(mInfo);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void appealViewStatus(RecreationMatchVerify recreationMatchVerify, RecreationMatchAppeal recreationMatchAppeal) {
        if (recreationMatchVerify.getState() >= 2) {

            getRightTextview().setTextColor(getResources().getColor(R.color.orange));
            if (recreationMatchAppeal.isAppealable() == true) {
                setRightTextView("申诉");
                getRightTextview().setOnClickListener(this);
            } else {
                if (recreationMatchAppeal.getAppealState() == 0) {
                    setRightTextView("申诉中");
                    getRightTextview().setOnClickListener(this);
                } else if (recreationMatchAppeal.getAppealState() == 1) {
                    setRightTextView("申诉完成");
                    getRightTextview().setOnClickListener(this);
                }
            }
        } else {
            getRightTextview().setOnClickListener(null);
            getRightTextview().setTextColor(getResources().getColor(R.color.font_gray));
            setRightTextView("申诉");
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvRightHandle:
                if (getRightTextview().getText().toString().equals("申诉")) {
                    goAppeal();
                } else {
                    if (recreationMatchAppeal != null) {
                        goAppealStatus();
                    }
                }
                break;
            case R.id.ibLeft:
                onBackPressed();
                break;
        }
    }

    private void goAppeal() {
        Intent intent = new Intent();
        intent.setClass(context, RecreationMatchAppealActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    private void goAppealStatus() {
        Intent intent = new Intent();
        intent.setClass(context, RecreationMatchAppealStatusActivityV2.class);
        intent.putExtra("recreationMatchAppeal", recreationMatchAppeal);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("recreationMatchAppeal",recreationMatchAppeal);
//        intent.putExtras(bundle);
        startActivity(intent);
    }
}
