package com.miqtech.master.client.ui.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.AmusementMatchAdapter;

import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.AmusementMatchInfo;
import com.miqtech.master.client.entity.FilterInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.FilterActivity;
import com.miqtech.master.client.ui.RecreationMatchDetailsActivity;
import com.miqtech.master.client.ui.basefragment.AmuseGameBaseFragment;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.RefreshLayout;
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
 * Created by Administrator on 2015/11/26.
 */
public class FragmentGameOfficialOffline extends AmuseGameBaseFragment implements View.OnClickListener {

    //@Bind(R.id.tv_filter_type)
    private TextView tvFilterType;    //筛选条件
    //@Bind(R.id.tv_filter)
    private TextView tvFilter;   //筛选按钮*/
    HasErrorListView lvGame;

    @Bind(R.id.prlvGame)
    PullToRefreshListView prlvGame;

    private int page = 1;  //页数
    private final String TYPE = "2"; // 比赛类型

    private AmusementMatchAdapter adapter;
    private List<AmusementMatchInfo> mDatas = new ArrayList<>();
    private Handler mHandler = new Handler();
    private boolean isLoadmore = false;
    private View rootView;
    private boolean isFirst = true;

    FilterInfo filterInfo;
    private int itemId = 0;  //游戏类型ID
    private int takeType = 0;  //参与方式，1-个人；2-团队
    private int awardtype = 0;  //奖品类型

    private boolean currentCity = true;

    private Map<String, String> params = new HashMap<>();


    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e("life", "oncreate");
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_game_officical_online, null);
        ButterKnife.bind(this, rootView);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirst) {
                //loadCache();
                isFirst = false;
                loadData(params);
                return;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.e("life", "oncreateView");
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeAllViewsInLayout();
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // findViews(view);
        initView();


    }

    /**
     * list头部，筛选按钮
     *
     * @return
     */
    private View getheaderView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_gamelist_header, null);
        tvFilter = (TextView) view.findViewById(R.id.tv_filter);
        tvFilterType = (TextView) view.findViewById(R.id.tv_filter_type);
        /*tvFilter.setOnClickListener(this);
        tvFilterType.setOnClickListener(this);*/
        tvFilterType.setText("全部比赛");
        return view;
    }


    private void initView() {
        prlvGame.setMode(PullToRefreshBase.Mode.BOTH);
        lvGame=prlvGame.getRefreshableView();
        adapter = new AmusementMatchAdapter(getContext(), mDatas);
        lvGame.addHeaderView(getheaderView());
        lvGame.setErrorView("并没有什么比赛~", R.drawable.blank_fight);
        lvGame.setAdapter(adapter);
        lvGame.setFocusable(false);
        prlvGame.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                loadData(params);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page++;
                isLoadmore = true;
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
                showToast(getActivity().getResources().getString(R.string.noNeteork));
            }
            }
        });
        lvGame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(getActivity(), FilterActivity.class);
                    //filterInfo = ((AmusementGameActivity) getActivity()).getFilterInfo();
                    if (filterInfo == null) {
                        filterInfo = new FilterInfo();
                        filterInfo.setFilterType(Constant.FILTER_GAME_OFFLINE);
                    }
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("filter", filterInfo);
                    intent.putExtra("data", bundle);
                    getActivity().startActivityForResult(intent, 1);
                    return;
                } else {
                    AmusementMatchInfo matchInfo = (AmusementMatchInfo) lvGame.getAdapter().getItem(position);
                    Intent intent = new Intent();
                    intent.setClass(getContext(), RecreationMatchDetailsActivity.class);
                    intent.putExtra("id", matchInfo.getId());
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        LogUtil.e("init", object.toString());

       prlvGame.onRefreshComplete();
        if (HttpConstant.AMUSE_LIST.equals(method)) {
            try {
                List<AmusementMatchInfo> list = initAmuseData(object);
                if (list != null && !list.isEmpty()) {
                    initAmuseView(list);
                    if (page == 1) {
                        //mCache.put(HttpConstant.AMUSE_LIST + TYPE, object);
                    }

                } else {
                    if (page == 1) {
                        mDatas.clear();
                        adapter.notifyDataSetChanged();
                    }
                    if (isLoadmore) {
                        page--;
                        showToast(R.string.nomore);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
                LogUtil.e("xiaoyi", "eeeeee");
            }
        }
        isLoadmore = false;
        if (mDatas.isEmpty()) {
            lvGame.setErrorShow(true);
        } else {
            lvGame.setErrorShow(false);
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        prlvGame.onRefreshComplete();
        isLoadmore = false;
        if (page > 1) {
            page--;
        } else {
            page = 0;
        }
        if (mDatas.isEmpty()) {
            lvGame.setErrorShow(true);
        } else {
            lvGame.setErrorShow(false);
        }
    }


    //加载数据
    private void loadData(Map<String, String> params) {
            if (params == null) {    //默认类型
                params = new HashMap<>();
            }
            params.put("page", page + "");
            params.put("type", TYPE);
            if (Constant.isLocation) {
                if (currentCity) {
                    params.put("areaCode", Constant.currentCity.getAreaCode());
                } else {
                    if (params.containsKey("areaCode")) {
                        params.remove("areaCode");
                    }
                }

                params.put("longitude", Constant.longitude + "");
                params.put("latitude", Constant.latitude + "");
            }
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_LIST, params, HttpConstant.AMUSE_LIST);


    }

    private void initAmuseView(List<AmusementMatchInfo> matchs) {

        if (page == 1) {
            mDatas.clear();
        }
        mDatas.addAll(matchs);
        LogUtil.e("matche", matchs.toString());
        adapter.setData(mDatas);
        adapter.notifyDataSetChanged();
    }


    private List<AmusementMatchInfo> initAmuseData(JSONObject object) throws JSONException {
        List<AmusementMatchInfo> matchs = new ArrayList<>();
        if (object.getInt("code") == 0 && object.has("object") && object.getString("result").equals("success")) {
            JSONObject objectJson = object.getJSONObject("object");
            if (objectJson.has("data")) {
                JSONObject dataJson = objectJson.getJSONObject("data");
                matchs = GsonUtil.getList(dataJson.getJSONArray("list").toString(), AmusementMatchInfo.class);
                return matchs;
            }
        }
        return matchs;
    }

    //加载缓存
    private void loadCache() {
        LoadCacheTask task = new LoadCacheTask();
        task.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_filter_type:
                //showToast("filter type");
                break;
            case R.id.tv_filter:
                //showToast("filter");
                break;
        }
    }

    @Override
    public void loadDataWithFilter(FilterInfo filterInfo) {
        this.filterInfo = filterInfo;
        StringBuffer sb = new StringBuffer();
        if (filterInfo.getGameItem().getItem_id() != 0) {
            itemId = filterInfo.getGameItem().getItem_id();
            params.put("itemId", "" + itemId);
            sb.append(filterInfo.getGameItem().getItem_name() + " ");
        } else {
            itemId = 0;
            if (params.containsKey("itemId"))
                params.remove("itemId");
        }

        if (filterInfo.getLocation() == 0){
            currentCity = true;
            params.put("isCurrentCity", "1");
            //sb.append(FilterActivity.locations[0]);
        }else {
            currentCity = false;
            if (params.containsKey("isCurrentCity")){
                params.remove("isCurrentCity");
            }
            //sb.append(FilterActivity.locations[1]);
        }

        if (filterInfo.getAwardInfo() != null && filterInfo.getAwardInfo().getAwardtype() != 0) {
            awardtype = filterInfo.getAwardInfo().getAwardtype();
            params.put("awardtype", awardtype + "");
            sb.append(filterInfo.getAwardInfo().getName() + " ");
        } else {
            awardtype = 0;
            if (params.containsKey("awardtype"))
                params.remove("awardtype");
        }

        if (filterInfo.getEnjoyType() != 0) {
            takeType = filterInfo.getEnjoyType();
            params.put("takeType", "" + takeType);
            sb.append(FilterActivity.enjoyTypes[takeType]);
        } else {
            takeType = 0;
            if (params.containsKey("takeType"))
                params.remove("takeType");
        }
        if (sb.length() > 0){
            setFilterText(sb.toString());
            loadData(params);
        }
    }

    @Override
    public void setFilterText(String filter) {
        if (!TextUtils.isEmpty(filter) && !filter.equals("null")) {
            tvFilterType.setText(filter);
        } else {
            tvFilterType.setText("全部比赛");
        }
    }

    class LoadCacheTask extends AsyncTask<Void, Void, List<AmusementMatchInfo>> {


        @Override
        protected List<AmusementMatchInfo> doInBackground(Void... params) {
            JSONObject cacheJson = mCache.getAsJSONObject(HttpConstant.AMUSE_LIST + TYPE);
            try {
                if (cacheJson != null) {
                    List<AmusementMatchInfo> caches = initAmuseData(cacheJson);
                    return caches;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<AmusementMatchInfo> datas) {
            super.onPostExecute(datas);
            if (datas == null) {
                return;
            }
            if (mDatas == null || mDatas.isEmpty()) {
                initAmuseView(datas);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }
}
