package com.miqtech.master.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.ZifaMatchListAdapter;
import com.miqtech.master.client.entity.ZifaMatch;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.fragment.FragmentApplyPopupConfirmInformation;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshRecyclerView;
import com.miqtech.master.client.watcher.Observerable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaoyi on 2016/7/5.
 */

public class ZifaMatchListActivity extends BaseActivity implements View.OnClickListener, Observerable.ISubscribe {
    @Bind(R.id.wypszfmatch2)
    PullToRefreshRecyclerView wypszfmatch2;
    private RecyclerView recyclerView;

    private boolean shouldShowMore = true;

    private int page = 1;
    private int pageSize = 10;

    private List<ZifaMatch> mDatas;
    private ZifaMatchListAdapter mAdapter;

    LinearLayoutManager layoutManager;
    private Observerable observerable;

    private int isLast = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zifamatch);
        ButterKnife.bind(this);
        mDatas = new ArrayList<>();
        observerable = Observerable.getInstance();
        observerable.subscribe(Observerable.ObserverableType.ZIFAMATCH, this);
        initView();
        loadData();
    }

    @Override
    protected void initView() {
        getLeftBtn().setImageResource(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        setLeftIncludeTitle(getString(R.string.zifamatch));
        recyclerView = wypszfmatch2.getRefreshableView();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        mDatas = new ArrayList<>();
        mAdapter = new ZifaMatchListAdapter(mDatas, this);
        recyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new ZifaMatchListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                Intent intent = new Intent();
                intent.setClass(ZifaMatchListActivity.this, EventDetailActivity.class);
                intent.putExtra("matchId", mDatas.get(pos).getRoundId() + "");
                startActivity(intent);
            }
        });

        wypszfmatch2.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                page = 1;
                shouldShowMore = true;
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (mAdapter != null) {
                    if (isLast == 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                page++;
                                loadData();
                            }
                        }, 300);
                    } else {
                        if (shouldShowMore) {
                            showToast(getResources().getString(R.string.nomore));
                            shouldShowMore = false;
                        }
                        wypszfmatch2.onRefreshComplete();
                    }
                } else {
                    wypszfmatch2.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getResources().getString(R.string.noNeteork));
                }
            }
        });
    }

    private void loadData() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ZIFAMATCH,
                params,
                HttpConstant.ZIFAMATCH);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        switch (method) {
            case HttpConstant.ZIFAMATCH:
                try {
                    wypszfmatch2.onRefreshComplete();
                    String dataStr = object.getJSONObject("object").getJSONArray("list").toString();
                    List<ZifaMatch> newDatas = initMatchData(dataStr);
                    if (page == 1) {
                        mDatas.clear();
                    }
                    mDatas.addAll(newDatas);
                    if (page == 1) {
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mAdapter.notifyItemInserted(mAdapter.getItemCount());
                    }
                    isLast = object.getJSONObject("object").getInt("isLast");
                    if (page > 1) {
                        if (newDatas.isEmpty()) {
                            page--;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        wypszfmatch2.onRefreshComplete();
        if (page > 1) {
            page--;
        }
    }

    @SuppressWarnings("unchecked")
    private List<ZifaMatch> initMatchData(String dataStr) {
        Gson gson = new Gson();
        List<ZifaMatch> datas = gson.fromJson(dataStr, new TypeToken<List<ZifaMatch>>() {
        }.getType());
        if (datas == null) {
            return Collections.EMPTY_LIST;
        }
        return datas;
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
    public <T> void update(T... data) {
        if (data == null || data.length <= 0) return;
        int type=(Integer)data[1];
        if(type== FragmentApplyPopupConfirmInformation.MATCH_LIST) {
            ZifaMatch match = (ZifaMatch) data[0];
            for (int i = 0; i < mDatas.size(); i++) {
                if (match.getRoundId().equals(mDatas.get(i).getRoundId())) {
                    mDatas.set(i, match);
                    mAdapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        observerable.unSubscribe(Observerable.ObserverableType.ZIFAMATCH, this);
        super.onDestroy();
    }
}