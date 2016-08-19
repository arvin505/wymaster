package com.miqtech.master.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.ExpiryListAdapter;
import com.miqtech.master.client.entity.CoinsStoreGoods;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaoyi on 2016/5/11.
 * 兑奖专区 商品列表页
 */
public class ExpiryListActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.prrvExpiry)
    PullToRefreshRecyclerView prrvExpiry;
    @Bind(R.id.tvEmpty)
    TextView tvEmpty;
    RecyclerView recycleExpiry;

    private int page = 1;
    private int pageSize = 12;
    private int isLast = 0;
    private boolean shouldShowMore = true;
    private int lastVisiable = 0;

    private GridLayoutManager layoutManager;
    private ExpiryListAdapter adapter;
    private List<CoinsStoreGoods> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expirylist);
        ButterKnife.bind(this);
        initView();
        loadData();
    }

    public void initView() {
        prrvExpiry.setMode(PullToRefreshBase.Mode.BOTH);
        recycleExpiry= prrvExpiry.getRefreshableView();
        layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == mDatas.size()) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        mDatas = new ArrayList<>();
        adapter = new ExpiryListAdapter(this, mDatas);
        recycleExpiry.setLayoutManager(layoutManager);
        recycleExpiry.setAdapter(adapter);
        setLeftIncludeTitle(getString(R.string.expiry_area));
        getLeftBtn().setImageResource(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        prrvExpiry.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                page = 1;
                shouldShowMore = false;
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (isLast == 0) {
                    recycleExpiry.scrollToPosition(adapter.getItemCount() - 1);
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
                        shouldShowMore = true;
                    }
                    prrvExpiry.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
             if(!isHasNetWork){
                 showToast(getString(R.string.noNeteork));
             }
            }
        });
        adapter.setOnitemClickListener(new ExpiryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(ExpiryListActivity.this, SubjectActivity.class);
                intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.GOODS_DETAIL);
                intent.putExtra("titletName", mDatas.get(position).getCommodityName());
                intent.putExtra("id", mDatas.get(position).getId() + "");
                intent.putExtra("totalCoins", mDatas.get(position).getPrice());
                startActivity(intent);

            }
        });
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
     * 加载数据
     */
    private void loadData() {
        showLoading();
        Map<String, String> params = new HashMap();
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        params.put("areaId", "1");
        LogUtil.e(TAG, "params : " + params.toString());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.COMMODITY_LIST, params, HttpConstant.COMMODITY_LIST);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        LogUtil.e(TAG, "object : " + object.toString());
        prrvExpiry.onRefreshComplete();
        if (adapter != null) {
            adapter.hideFooter();
        }
        if (HttpConstant.COMMODITY_LIST.equals(method)) {
            try {
                JSONObject resultObj = object.getJSONObject("object");
                List<CoinsStoreGoods> newItems = new Gson().fromJson(resultObj.getJSONArray("list").toString(), new TypeToken<List<CoinsStoreGoods>>() {
                }.getType());
                if (page == 1 && mDatas != null) {
                    mDatas.clear();
                }
                isLast = resultObj.getInt("isLast");
                mDatas.addAll(newItems);
                if (page == 1) {
                    adapter.notifyDataSetChanged();
                } else {
                    adapter.notifyItemInserted(adapter.getItemCount());
                }

                if (page > 1) {
                    if (newItems.isEmpty()) {
                        page--;
                    }
                }
                if (mDatas == null || mDatas.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        prrvExpiry.onRefreshComplete();
        if (adapter != null) {
            adapter.hideFooter();
        }
        if (page > 1) {
            page--;
        }
        if (mDatas == null || mDatas.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        prrvExpiry.onRefreshComplete();
        if (adapter != null) {
            adapter.hideFooter();
        }
        if (page > 1) {
            page--;
        }
        if (mDatas == null || mDatas.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }
}
