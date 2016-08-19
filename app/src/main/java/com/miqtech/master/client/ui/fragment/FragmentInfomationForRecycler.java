package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.InformationAdapter;
import com.miqtech.master.client.entity.InforBanner;
import com.miqtech.master.client.entity.InforItemDetail;
import com.miqtech.master.client.entity.InforList;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.MainActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.ACache;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.InforHeadLinesView;
import com.nostra13.universalimageloader.core.ImageLoader;

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
 * Created by Administrator on 2016/1/4.
 */
public class FragmentInfomationForRecycler extends BaseFragment {

    @Bind(R.id.swRefresh_infor_fragment)
    SwipeRefreshLayout swRefresh;//下拉刷新
    @Bind(R.id.recycle)
    RecyclerView recyclerView;

    InforHeadLinesView bannerView;//轮播图

    private ImageLoader mImageLoader;//图片下载
    private ACache mCache;//缓存
    private List<InforBanner> bannerList = new ArrayList<InforBanner>();
    private Context context;
    private List<InforItemDetail> inforItemDetails = new ArrayList<InforItemDetail>();

    private View view;

    private int page = 1;//当前分页所在页数（默认值1）
    private int pageSize = 10;//当前分页显示的行数（默认值10）
    private int infoCount = 0;//返回数据的总条数（默认值0）
    private int isLast = 0;//是否最后一条

    private InformationAdapter adapter;
    LinearLayoutManager mLayoutManager;
    int lastVisibleItem = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoader.getInstance();
        mCache = ACache.get(getActivity());
        context = (MainActivity) getActivity();
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_information_recycler, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        if (!Utils.isNetworkAvailable(getActivity())) {
            loadCache();
            showToast(context.getResources().getString(R.string.noNeteork));
            return;
        }

        swRefresh.setColorSchemeResources(R.color.cpb_complete_state_selector);
        swRefresh.setRefreshing(true);
        loadBannerData();
//        loadInforData();
        swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                infoCount = 0;
                pageSize = 10;
                loadBannerData();
//                loadInforData();
            }
        });

        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (adapter != null && newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == adapter.getItemCount()) {
                    if (isLast == 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                page++;
                                loadInforData();
                            }
                        }, 1000);
                    } else {
                        showToast(context.getResources().getString(R.string.load_no));
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    /**
     * 请求banner数据
     */
    private void loadBannerData() {
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INFO_BANNER, null, HttpConstant.INFO_BANNER);
    }

    /**
     * 请求资讯列表数据
     */
    private void loadInforData() {
        HashMap map = new HashMap();
        map.put("page", page + "");
        map.put("pageSize", pageSize + "");
        map.put("infoCount", infoCount + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INFO_LIST, map, HttpConstant.INFO_LIST);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        LogUtil.e("sss", "object == " + object);
        if (method.equals(HttpConstant.INFO_BANNER)) {//广告
            mCache.put(HttpConstant.INFO_BANNER, object);
            getBannerData(object);
            loadInforData();
        } else if (method.equals(HttpConstant.INFO_LIST)) {//资讯列表
            getInforList(object);
        }
        swRefresh.setRefreshing(false);
    }

    @Override
    public void onError(String errMsg, String method) {
        swRefresh.setRefreshing(false);
        showToast(context.getResources().getString(R.string.noNeteork));
        if (method.equals(HttpConstant.INFO_BANNER)) {
            loadInforData();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        swRefresh.setRefreshing(false);
        if (method.equals(HttpConstant.INFO_BANNER)) {
            loadInforData();
        }
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到数据并显示广告
     *
     * @param object
     */
    private void getBannerData(JSONObject object) {

        try {
            if ("0".equals(object.getString("code")) && object.has("object")) {
                JSONArray jsonArray = object.getJSONArray("object");
                bannerList = GsonUtil.getList(jsonArray.toString(), InforBanner.class);
                addHead();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addHead() {
        if (bannerList.size() > 8) {
            List<InforBanner> newBannerList = new ArrayList<InforBanner>();
            for (int i = 0; i < 8; i++) {
                newBannerList.add(bannerList.get(i));
            }
            bannerList.clear();
            bannerList.addAll(newBannerList);
        }

        adapter = new InformationAdapter(context, inforItemDetails, bannerList);
        recyclerView.setAdapter(adapter);
        adapter.setRemoveView(new InformationAdapter.RemoveView() {
            @Override
            public void removeView(View view) {
                if (isLast != 0 && view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 得到资讯数据列表，并刷新列表
     *
     * @param object
     */
    private void getInforList(JSONObject object) {
        try {
            if ("0".equals(object.getString("code")) && object.has("object")) {
                InforList newInforList = GsonUtil.getBean(object.getString("object").toString(), InforList.class);
                infoCount = newInforList.getTotal();
                isLast = newInforList.getIsLast();
                if (page == 1) {
                    inforItemDetails.clear();
                    inforItemDetails.add(0, null);
                }
                inforItemDetails.addAll(newInforList.getList());
                if (page == 1) {
                    mCache.put(HttpConstant.INFO_LIST, object);
                }
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCache() {
        LoadCacheTask cacheTask = new LoadCacheTask();
        cacheTask.execute();
    }

    private class LoadCacheTask extends AsyncTask<Void, Void, Map<String, JSONObject>> {
        Map<String, JSONObject> map;

        @Override
        protected Map<String, JSONObject> doInBackground(Void... params) {
            map = new HashMap<>();
            JSONObject infor_banner = mCache.getAsJSONObject(HttpConstant.INFO_BANNER);
            if (infor_banner != null) {
                map.put(HttpConstant.INFO_BANNER, infor_banner);
            }

            JSONObject infor_list = mCache.getAsJSONObject(HttpConstant.INFO_LIST);

            if (infor_list != null) {
                map.put(HttpConstant.INFO_LIST, infor_list);
            }
            return map;
        }

        @Override
        protected void onPostExecute(Map<String, JSONObject> stringJSONObjectMap) {
            super.onPostExecute(stringJSONObjectMap);
            for (Map.Entry<String, JSONObject> maps : stringJSONObjectMap.entrySet()) {
                if (HttpConstant.INFO_BANNER.equals(maps.getKey())) {
                    getBannerData(maps.getValue());
                } else if (HttpConstant.INFO_LIST.equals(maps.getKey())) {
                    getInforList(maps.getValue());
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerView != null) {
            bannerView.startAutoScroll();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bannerView != null) {
            bannerView.stopAutoScroll();
        }
    }
}
