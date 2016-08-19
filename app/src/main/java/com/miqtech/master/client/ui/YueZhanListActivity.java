package com.miqtech.master.client.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.YueZhanAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.FilterInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.entity.YueZhan;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.ACache;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.RefreshLayout;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/1.
 */
public class YueZhanListActivity extends BaseActivity implements  View.OnClickListener {
    /*@Bind(R.id.img_header_icon)
    ImageView back;
    @Bind(R.id.tv_header_title)
    TextView tvTitle;*/
    @Bind(R.id.prlvYuezhan)
    PullToRefreshListView prlvYuezhan;
    HasErrorListView lvYuezhan;


    private TextView tvFilter;
    private TextView tvFilterType;
    private YueZhanAdapter adapter;
    private ACache mCache;

    private final String TAG = "YueZhanListActivity";

    private FilterInfo filterInfo;
    private List<YueZhan> mDatas;
    private int page = 1;
    private boolean isLoadmore = false;
    private boolean isLoading = false;
    private Handler mHandler = new Handler();
    private Map<String, String> params = new HashMap<>();

    private int total = 0;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_yuezhanlist);
        mCache = ACache.get(this);
        ButterKnife.bind(this);
        initView();
        loadData();
    }


    protected void initView() {
        lvYuezhan=prlvYuezhan.getRefreshableView();
        prlvYuezhan.setMode(PullToRefreshBase.Mode.BOTH);
        setLeftIncludeTitle(getResources().getString(R.string.yuezhan_personal));
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        mDatas = new ArrayList<>();
        adapter = new YueZhanAdapter(this, mDatas);
        lvYuezhan.setErrorView("      yoho，还木有约战呢，赶紧去发起吧，约约约！！！", R.drawable.blank_fight);
        lvYuezhan.addHeaderView(getheaderView());
        lvYuezhan.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        lvYuezhan.setFocusable(false);
        prlvYuezhan.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page++;
                isLoadmore = true;
                //setLoadingState(swRefresh);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                    }
                }, 1000);
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
            if(!isHasNetWork){
                showToast(getString(R.string.noNeteork));
            }
            }
        });
        lvYuezhan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent filterIntent = new Intent(YueZhanListActivity.this, FilterActivity.class);
                    Bundle data = new Bundle();
                    if (filterInfo == null) {
                        filterInfo = new FilterInfo();
                    }
                    filterInfo.setFilterType(Constant.FILTER_BATTLE);
                    data.putParcelable("filter", filterInfo);
                    filterIntent.putExtra("data", data);
                    startActivityForResult(filterIntent, 1);
                    return;
                } else {

                }

            }
        });
        setRightTextView("发起约战");
        getRightTextview().setOnClickListener(this);

    }


    private void loadData() {
        params.put("page", page + "");
        if (Constant.isLocation && !TextUtils.isEmpty(Constant.currentCity.getAreaCode())) {
            params.put("areaCode", Constant.currentCity.getAreaCode());
        }
        User user = WangYuApplication.getUser(this);
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
        }
        if (page > 1 && total > 0) {
            params.put("lastTotal", total + "");
        }
        LogUtil.e("suc", params.toString());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.LATEST_BATTLE, params, HttpConstant.YUEZHAN_LIST);
    }


    @Override
    public void onSuccess(JSONObject object, String method) {
        LogUtil.e("suc", "success");
        super.onSuccess(object, method);
       prlvYuezhan.onRefreshComplete();
        LogUtil.e("data", object.toString());
        if (HttpConstant.YUEZHAN_LIST.equals(method)) {
            try {
                List<YueZhan> list = initYueZhanData(object);
                if (list != null && !list.isEmpty()) {

                    if (page == 1) {
                        //mCache.put(TAG, object);
                    }
                } else {
                    if (isLoadmore) {
                        page--;
                        showToast(R.string.nomore);
                    }
                }
                initYueZhanList(list);
            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.e("xiaoyi", "eeeeee");
            }
        }
        isLoadmore = false;
    }


    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        prlvYuezhan.onRefreshComplete();
        if (mDatas.isEmpty()) {
            lvYuezhan.setErrorShow(true);
        } else {
            lvYuezhan.setErrorShow(false);
        }
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
       prlvYuezhan.onRefreshComplete();
        isLoadmore = false;
        if (mDatas.isEmpty()) {
            lvYuezhan.setErrorShow(true);
        } else {
            lvYuezhan.setErrorShow(false);
        }
    }

    /**
     * list头部，筛选按钮
     *
     * @return
     */
    private View getheaderView() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_gamelist_header, null);
        tvFilter = (TextView) view.findViewById(R.id.tv_filter);
        tvFilterType = (TextView) view.findViewById(R.id.tv_filter_type);
        tvFilterType.setText("全部约战");
        return view;
    }

    //加载缓存
    private void loadCache() {
        LoadCacheTask task = new LoadCacheTask();
        task.execute();
    }

    private List<YueZhan> initYueZhanData(JSONObject object) throws JSONException {
        List<YueZhan> yuezhans = new ArrayList<>();
        if (object.getInt("code") == 0 && object.has("object") && object.getString("result").equals("success")) {
            JSONObject objectJson = object.getJSONObject("object");
            if (objectJson.has("list")) {
                JSONArray dataJson = objectJson.getJSONArray("list");
                yuezhans = GsonUtil.getList(dataJson.toString(), YueZhan.class);
                total = objectJson.getInt("total");
                return yuezhans;
            }
        }
        return yuezhans;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                finish();
                break;
            case R.id.tvRightHandle:
                User user = WangYuApplication.getUser(this);
                Intent intent = new Intent();
                if (user != null) {
                    intent.setClass(this, ReleaseWarActivity.class);
                } else {
                    intent.setClass(this, LoginActivity.class);
                }
                startActivity(intent);
                break;
        }
    }

    class LoadCacheTask extends AsyncTask<Void, Void, List<YueZhan>> {


        @Override
        protected List<YueZhan> doInBackground(Void... params) {
            JSONObject cacheJson = mCache.getAsJSONObject(TAG);
            try {
                if (cacheJson != null) {
                    List<YueZhan> caches = initYueZhanData(cacheJson);
                    return caches;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<YueZhan> datas) {
            super.onPostExecute(datas);
            if (datas == null) {
                return;
            }
            if (mDatas == null || mDatas.isEmpty()) {
                initYueZhanList(datas);
            }
        }
    }

    private void initYueZhanList(List<YueZhan> datas) {
        if (page == 1) {
            mDatas.clear();
        }
        LogUtil.e("view", " init list  " + datas.size());
        mDatas.addAll(datas);
        adapter.setData(mDatas);
        if (mDatas.isEmpty()) {
            lvYuezhan.setErrorShow(true);
        } else {
            lvYuezhan.setErrorShow(false);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            filterInfo = data.getBundleExtra("result").getParcelable("filter");
            // showToast(filterInfo.toString() + "   " + filterInfo.getGameItem().getItem_name());
            loadDataWithFilter(filterInfo);
        }
    }

    public void setFilterText(String filter) {
        if (filter.trim().equals("")) {
            tvFilterType.setText("全部约战");
        } else {
            tvFilterType.setText(filter);
        }
    }

    /**
     * 筛选
     *
     * @param filterInfo 约战状态， 游戏名称， 地区
     */
    private void loadDataWithFilter(FilterInfo filterInfo) {
        page = 1;
        StringBuffer sb = new StringBuffer();
        if (filterInfo.getBattle() != 0) {
            params.put("scope", filterInfo.getBattle() - 1 + "");
            sb.append(FilterActivity.cityTypes[filterInfo.getBattle()] + " ");
        } else {
            if (params.containsKey("scope")) {
                params.remove("scope");
            }
        }
        if (filterInfo.getGameItem().getItem_id() != 0) {
            params.put("category", filterInfo.getGameItem().getItem_id() + "");
            sb.append(filterInfo.getGameItem().getItem_name() + " ");
        } else {
            if (params.containsKey("category")) {
                params.remove("category");
            }
        }
        /*if (filterInfo.getState() != 0) {
            params.put("status", filterInfo.getState() + "");
            sb.append(FilterActivity.stateTypes[filterInfo.getState()]);
        } else {
            if (params.containsKey("status")) {
                params.remove("status");
            }
        }*/
        setFilterText(sb.toString());
        loadData();
    }
}
