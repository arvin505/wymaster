package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.InforAdapter;
import com.miqtech.master.client.entity.InforBanner;
import com.miqtech.master.client.entity.InforItemDetail;
import com.miqtech.master.client.entity.InforList;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.InformationAtlasActivity;
import com.miqtech.master.client.ui.InformationDetailActivity;
import com.miqtech.master.client.ui.InformationTopicActivity;
import com.miqtech.master.client.ui.MainActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.ACache;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.InforHeadLinesView;
import com.miqtech.master.client.view.MyListViewForInfo;
import com.miqtech.master.client.view.RefreshLayout;
import com.miqtech.master.client.view.SlidingMenu;
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
 * 资讯
 * Created by Administrator on 2015/11/25.
 */
public class FragmentInformation extends BaseFragment implements AdapterView.OnItemClickListener, RefreshLayout.OnLoadListener,
        SlidingMenu.isAutoPlay {

    @Bind(R.id.swRefresh_infor_fragment)
    RefreshLayout swRefresh;//下拉刷新
    @Bind(R.id.lv_infor_fragment)
    MyListViewForInfo lvInfo;

//    @Bind(R.id.banner_infor_fragment)
//    InforHeadLinesView banner_infor_fragment;//轮播图

    InforHeadLinesView bannerView;//轮播图

    private ImageLoader mImageLoader;//图片下载
    private ACache mCache;//缓存
    private List<InforBanner> bannerList = new ArrayList<InforBanner>();
    private InforAdapter adapter;
    private Context context;
    private List<InforItemDetail> inforItemDetails = new ArrayList<InforItemDetail>();

    private View view;

    private int page = 1;//当前分页所在页数（默认值1）
    private int pageSize = 10;//当前分页显示的行数（默认值10）
    private int infoCount = 0;//返回数据的总条数（默认值0）
    private int isLast = 0;//是否最后一条

    public FragmentInformation() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoader.getInstance();
        mCache = ACache.get(getActivity());
        context = (MainActivity) getActivity();
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_information, container, false);
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
        loadInforData();
        lvInfo.setOnItemClickListener(this);
        swRefresh.setOnLoadListener(this);
        swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                infoCount = 0;
                pageSize = 10;
                loadBannerData();
                loadInforData();
            }
        });

        //((MainActivity) context).mMenu.setOnIsAutoPlay(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getAdapter().getItemId(position) == -1) {

        } else {
            InforItemDetail bean = (InforItemDetail) parent.getAdapter().getItem(position);
            Intent intent = null;
            if (bean.getType() == 1) {//类型:1图文  跳转
                intent = new Intent(context, InformationDetailActivity.class);
                intent.putExtra("id", bean.getId() + "");
                intent.putExtra("type",bean.getType()+"");
                context.startActivity(intent);
            } else if (bean.getType() == 2) {//2专题  跳转
                intent = new Intent();
                intent.putExtra("activityId", bean.getId());
                intent.putExtra("zhuanTitle", bean.getTitle());
                intent.setClass(context, InformationTopicActivity.class);
                context.startActivity(intent);
            } else if (bean.getType() == 3) {//3图集  跳转
                intent = new Intent();
                intent.putExtra("activityId", bean.getId());
                intent.setClass(context, InformationAtlasActivity.class);
                context.startActivity(intent);
            }
        }
    }

    @Override
    public void onLoad() {
        if (isLast == 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    page++;
                    loadInforData();
                }
            }, 1000);
        } else {
            swRefresh.setLoading(false);
            showToast(context.getResources().getString(R.string.load_no));
        }
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
        } else if (method.equals(HttpConstant.INFO_LIST)) {//资讯列表
            getInforList(object);
        }
        swRefresh.setRefreshing(false);
        swRefresh.setLoading(false);
    }

    @Override
    public void onError(String errMsg, String method) {
        swRefresh.setRefreshing(false);
        swRefresh.setLoading(false);
        showToast(context.getResources().getString(R.string.noNeteork));
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        swRefresh.setRefreshing(false);
        swRefresh.setLoading(false);
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
        if (view != null) {
            lvInfo.removeHeaderView(view);
        }

        if (bannerList.size() > 8) {
            List<InforBanner> newBannerList = new ArrayList<InforBanner>();
            for (int i = 0; i < 8; i++) {
                newBannerList.add(bannerList.get(i));
            }
            bannerList.clear();
            bannerList.addAll(newBannerList);
        }
        adapter = new InforAdapter(getActivity(), inforItemDetails);
        view = LayoutInflater.from(context).inflate(R.layout.layout_banner_infor_fragment, null);
        bannerView = (InforHeadLinesView) view.findViewById(R.id.banner_infor_fragment);
        lvInfo.addHeaderView(view);
        lvInfo.setAdapter(adapter);
        bannerView.refreshData(bannerList);
        bannerView.setScrollDelayTime(3000);
        bannerView.startAutoScroll();
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
                }
                inforItemDetails.addAll(newInforList.getList());
                if (page == 1) {
                    mCache.put(HttpConstant.INFO_LIST, object);
                }
                adapter.notifyDataSetChanged();
                swRefresh.setLoading(false);
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

    @Override
    public void startPlay() {
        if (!bannerList.isEmpty() && bannerView != null) {
            bannerView.startAutoScroll();
        }
    }

    @Override
    public void stopPlay() {
        if (bannerView != null) {
            bannerView.stopAutoScroll();
        }
    }
}
