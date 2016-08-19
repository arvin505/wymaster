package com.miqtech.master.client.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.CompositiveMatchAdapter;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.CompositiveMatchInfo;
import com.miqtech.master.client.entity.FilterInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.ACache;
import com.miqtech.master.client.utils.GsonUtil;
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
 * Created by Administrator on 2015/11/30.
 */
public class CompositiveMatchActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.img_header_icon)
    ImageView back;
    @Bind(R.id.tv_header_title)
    TextView tvTitle;
    @Bind(R.id.prlvGame)
    PullToRefreshListView prlvGame;
    HasErrorListView lvList;
    private TextView tvFilter;
    private TextView tvFilterType;
    private int page = 1;  //页数

    private CompositiveMatchAdapter adapter;
    private List<CompositiveMatchInfo> mDatas = new ArrayList<>();
    private Handler mHandler = new Handler();
    private boolean isLoadmore = false;
    private boolean isLoading = false;
    private ACache mCache;

    private Map<String,String> params = new HashMap<>();

    private final String TAG = "CompositiveMatchActivity";
    private FilterInfo filterInfo;
    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_composite_game);
        mCache = ACache.get(this);
        ButterKnife.bind(this);
        initView();
        //loadCache();
        loadData(params);
    }

    protected void initView() {
        prlvGame.setMode(PullToRefreshBase.Mode.BOTH);
        lvList=prlvGame.getRefreshableView();
        tvTitle.setText(R.string.game_composite);
        adapter = new CompositiveMatchAdapter(this, mDatas);
        lvList.setErrorView("并没有什么比赛");
        lvList.addHeaderView(getheaderView());
        lvList.setAdapter(adapter);
        lvList.setFocusable(false);
        prlvGame.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                loadData(params);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                isLoadmore = true;
                page++;
                //setLoadingState(swRefresh);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadData(params);
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

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent filterIntent = new Intent(CompositiveMatchActivity.this, FilterActivity.class);
                    Bundle data = new Bundle();
                    if (filterInfo == null) {
                        filterInfo = new FilterInfo();
                    }
                    filterInfo.setFilterType(Constant.FILTER_GAME_COMPOSITE);
                    data.putParcelable("filter", filterInfo);
                    filterIntent.putExtra("data", data);
                    startActivityForResult(filterIntent, 1);
                    return;
                }
                CompositiveMatchInfo matchInfo = (CompositiveMatchInfo) lvList.getAdapter().getItem(position);

                if(matchInfo == null){
                    return;
                }
                Intent intent = new Intent();
                if(matchInfo.getType() == 0){
                    intent.putExtra("matchId", matchInfo.getId() + "");
                    intent.setClass(CompositiveMatchActivity.this, OfficalEventActivity.class);
                }else {
                    intent.setClass(CompositiveMatchActivity.this, RecreationMatchDetailsActivity.class);
                    intent.putExtra("id", matchInfo.getId() + "");
                }


                startActivity(intent);
            }
        });
    }

    private void loadData(Map<String,String> params) {
        showLoading();
        params.put("page", page + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.COMPOSITIVE_ACTIVITY_LIST, params, HttpConstant.COMPOSITIVE_ACTIVITY_LIST);
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
        tvFilterType.setText("全部比赛");
        back.setOnClickListener(this);
        //tvFilter.setOnClickListener(this);
        //tvFilterType.setOnClickListener(this);
        return view;
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object,method);
        prlvGame.onRefreshComplete();
        if (HttpConstant.COMPOSITIVE_ACTIVITY_LIST.equals(method)) {
            try {
                List<CompositiveMatchInfo> list = initMatchInfo(object);
                if (list != null && !list.isEmpty()) {
                    initMatchView(list);
                } else {
                    if (isLoadmore) {
                        page--;
                        showToast(R.string.nomore);
                    }
                    initMatchView(list);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        isLoadmore = false;
        isLoading = false;
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        prlvGame.onRefreshComplete();
        if (mDatas != null && !mDatas.isEmpty()) {
            lvList.setErrorShow(false);
        }else {
            lvList.setErrorShow(true);
        }
        isLoading = false;
    }

    private List<CompositiveMatchInfo> initMatchInfo(JSONObject object) throws JSONException {
        List<CompositiveMatchInfo> matchs = new ArrayList<>();
        if (object.getInt("code") == 0 && object.has("object") && object.getString("result").equals("success")) {
            JSONObject objectJson = object.getJSONObject("object");
            if (objectJson.has("list")) {
                JSONArray dataJson = objectJson.getJSONArray("list");
                matchs = GsonUtil.getList(dataJson.toString(), CompositiveMatchInfo.class);
                return matchs;
            }
        }
        return matchs;
    }

    private void initMatchView(List<CompositiveMatchInfo> matchs) {
        if (page == 1) {
            mDatas.clear();
        }
        mDatas.addAll(matchs);
        adapter.setData(mDatas);
        if (!mDatas.isEmpty()){
            lvList.setErrorShow(false);
        }else {
            lvList.setErrorShow(true);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_header_icon:
                finish();
                break;
        }
    }

    //加载缓存
    private void loadCache() {
        LoadCacheTask task = new LoadCacheTask();
        task.execute();
    }

    class LoadCacheTask extends AsyncTask<Void, Void, List<CompositiveMatchInfo>> {


        @Override
        protected List<CompositiveMatchInfo> doInBackground(Void... params) {
            JSONObject cacheJson = mCache.getAsJSONObject(TAG);
            try {
                if (cacheJson != null) {
                    List<CompositiveMatchInfo> caches = initMatchInfo(cacheJson);
                    return caches;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<CompositiveMatchInfo> datas) {
            super.onPostExecute(datas);
            if (datas == null) {
                return;
            }
            if (mDatas == null || mDatas.isEmpty()) {
                mDatas.addAll(datas);
                initMatchView(mDatas);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1){
            StringBuffer sb = new StringBuffer();
            filterInfo = data.getBundleExtra("result").getParcelable("filter");
            if (filterInfo.getGameItem().getItem_id()!=0){
                params.put("itemId",filterInfo.getGameItem().getItem_id() + "");
                sb.append(filterInfo.getGameItem().getItem_name() + " ");
            }else {
                if (params.containsKey("itemId")){
                    params.remove("itemId");
                }
            }
            if (filterInfo.getState()!=0){
                params.put("state",filterInfo.getState() + "");
                sb.append(FilterActivity.stateTypes[filterInfo.getState()]);
            }else {
                if (params.containsKey("state")){
                    params.remove("state");
                }
            }
            setFilterText(sb.toString());
            loadData(params);
        }
    }

    public void setFilterText(String filter) {
        if (filter.trim().equals("")){
            tvFilterType.setText("全部比赛");
        }else {
            tvFilterType.setText(filter);
        }
    }
}
