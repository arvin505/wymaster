package com.miqtech.master.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.CrowdfundingAdapter;
import com.miqtech.master.client.entity.CoinsStoreGoods;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshRecyclerView;
import com.miqtech.master.client.watcher.Observerable;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaoyi on 2016/5/9.
 * 众筹夺宝Activity
 */
public class CrowdfundingListActivity extends BaseActivity implements View.OnClickListener, Observerable.ISubscribe {

    @Bind(R.id.prrvDatas)
    PullToRefreshRecyclerView prrvDatas;
    @Bind(R.id.tvErrorPage)
    TextView tvErrorPage;
    RecyclerView recycleDatas;
    private LinearLayoutManager layoutManager;
    private List<CoinsStoreGoods> mDatas;
    private CrowdfundingAdapter adapter;
    private int page = 1;
    private int pageSize = 10;
    private int isLast = 0;
    private int lastVisiable = 0;
    private boolean shouldShowMore = true;
    private Observerable mWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crowdfundinglist);
        ButterKnife.bind(this);
        mWatcher = Observerable.getInstance();
        mWatcher.subscribe(Observerable.ObserverableType.CROWDLIST, this);
        initView();
        loadData();
    }

    public void initView() {
        prrvDatas.setMode(PullToRefreshBase.Mode.BOTH);
        recycleDatas = prrvDatas.getRefreshableView();
        setLeftIncludeTitle(getString(R.string.crowdfunding));
        getLeftBtn().setOnClickListener(this);
        getLeftBtn().setImageResource(R.drawable.back);
        prrvDatas.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                page = 1;
                shouldShowMore = true;
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (isLast == 0) {
                    //     adapter.showFooter();
                    recycleDatas.scrollToPosition(adapter.getItemCount() - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            page++;
                            loadData();
                        }
                    }, 1000);
                } else {
                    if (shouldShowMore) {
                        showToast(getResources().getString(R.string.nomore));
                        shouldShowMore = false;
                    }
                    prrvDatas.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getString(R.string.noNeteork));
                }
            }
        });
        mDatas = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new CrowdfundingAdapter(this, mDatas);
        recycleDatas.setLayoutManager(layoutManager);
        recycleDatas.setAdapter(adapter);
        adapter.setOnItemClickListener(new CrowdfundingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(CrowdfundingListActivity.this, ShopDetailActivity.class);
                intent.putExtra("itemPos", position);
                intent.putExtra("coinsStoreGoods", mDatas.get(position));


                LogUtil.e(TAG, "changestate ... p " + position);
                startActivity(intent);
            }
        });
    }

    private void testData() {
        /*mDatas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            InternetBarInfo internetBarInfo = new InternetBarInfo();
            internetBarInfo.setName("哈哈哈 " + i);
            mDatas.add(internetBarInfo);
        }*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                finish();
                break;
        }
    }

    /**
     * 请求数据
     */
    private void loadData() {
        showLoading();
        Map<String, String> params = new HashMap();
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        params.put("areaId", "3");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.COMMODITY_LIST, params, HttpConstant.COMMODITY_LIST);
        LogUtil.e(TAG, "params : " + params.toString());
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        LogUtil.e(TAG, "object : " + object.toString());
        prrvDatas.onRefreshComplete();
        hideLoading();
        switch (method) {
            case HttpConstant.COMMODITY_LIST:
                try {
                    adapter.hideFooter();
                    //TODO 要替换的部分
                    JSONObject resultObj = object.getJSONObject("object");
                    List<CoinsStoreGoods> newItems;
                    if (resultObj.has("list")) {
                        newItems = new Gson().fromJson(resultObj.getJSONArray("list").toString(), new TypeToken<List<CoinsStoreGoods>>() {
                        }.getType());
                        Collections.sort(newItems, new Comparator<CoinsStoreGoods>() {
                            @Override
                            public int compare(CoinsStoreGoods lhs, CoinsStoreGoods rhs) {
                                return lhs.getCrowdfundStatus() != rhs.getCrowdfundStatus() ?
                                        -(rhs.getCrowdfundStatus() - rhs.getCrowdfundStatus()) :
                                        rhs.getSortNo() - lhs.getSortNo();
                            }
                        });
                    } else {
                        newItems = new ArrayList<>();
                    }
                    if (page == 1) {
                        mDatas.clear();
                    }
                    isLast = resultObj.getInt("isLast");
                    mDatas.addAll(newItems);
                    if (page > 1) {
                        if (newItems.isEmpty()) {
                            page--;
                        }
                    }
                    if (page == 1) {
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.notifyItemInserted(adapter.getItemCount());
                    }
                    if (adapter.getItemCount() == 0) {
                        tvErrorPage.setVisibility(View.VISIBLE);
                    } else {
                        tvErrorPage.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        prrvDatas.onRefreshComplete();
        if (adapter != null) {
            adapter.hideFooter();
        }
        if (page > 1) {
            page--;
        }
        if (adapter.getItemCount() == 0) {
            tvErrorPage.setVisibility(View.VISIBLE);
        } else {
            tvErrorPage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        prrvDatas.onRefreshComplete();
        if (adapter != null) {
            adapter.hideFooter();
        }
        if (page > 1) {
            page--;
        }
        if (adapter.getItemCount() == 0) {
            tvErrorPage.setVisibility(View.VISIBLE);
        } else {
            tvErrorPage.setVisibility(View.GONE);
        }
    }

    //int pos, CoinsStoreGoods data
    @Override
    public <T> void update(T... data) {
        int pos = (Integer) data[0];
        CoinsStoreGoods newData = (CoinsStoreGoods)data[1];
        try {
            CoinsStoreGoods item = mDatas.get(pos);
            item.setProgress(newData.getProgress());
            item.setLeftNum(newData.getLeftNum()); LogUtil.e(TAG, "000000  "  + item.toString());
            item.setCrowdfundStatus(newData.getCrowdfundStatus());
            adapter.notifyItemChanged(pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWatcher.unSubscribe(Observerable.ObserverableType.CROWDLIST, this);
    }
}
