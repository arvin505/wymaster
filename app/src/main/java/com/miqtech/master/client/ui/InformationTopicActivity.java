package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.ExpertAdapter;
import com.miqtech.master.client.entity.InforCatalog;
import com.miqtech.master.client.entity.InforItemDetail;
import com.miqtech.master.client.entity.InforList;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.ACache;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.utils.Utils;
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
 * 资讯专题
 * Created by zhaosentao on 2015/11/26.
 */
public class InformationTopicActivity extends BaseActivity implements AdapterView.OnItemClickListener,View.OnClickListener {

    @Bind(R.id.information_topic)
    PullToRefreshListView refreshTopic;
    HasErrorListView listView;
    private ACache mCache;//缓存
    private ExpertAdapter adapter;
    private Context context;
    private List<InforItemDetail> inforItemDetails = new ArrayList<InforItemDetail>();

    private int page = 1;//当前分页所在页数（默认值1）
    private int pageSize = 10;//当前分页显示的行数（默认值10）
    private int infoCount = 0;//返回数据的总条数（默认值0）
    private int isLast = 0;//是否最后一条
    private String activityId;//专题id

    private View view;
    private ImageView img;
    private String zhuanTitle;

    private InforCatalog mInfoCatalog;
    private String url;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_infor_topic);
        ButterKnife.bind(this);
        context = InformationTopicActivity.this;
        mCache = ACache.get(context);
        activityId = getIntent().getStringExtra("activityId");
        zhuanTitle = getIntent().getStringExtra("zhuanTitle");
        mInfoCatalog = getIntent().getParcelableExtra("mInfoCatalog");
        url = getIntent().getStringExtra("url");
        lengthCoding = UMengStatisticsUtil.CODE_1003;
        lengthTargetId = activityId + "";
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        if (!Utils.isNetworkAvailable(InformationTopicActivity.this)) {
            loadCahce();
            showToast(context.getResources().getString(R.string.noNeteork));
            return;
        }

        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        setLeftIncludeTitle(zhuanTitle);

        refreshTopic.setMode(PullToRefreshBase.Mode.BOTH);
        refreshTopic.setScrollingWhileRefreshingEnabled(true);
        listView =refreshTopic.getRefreshableView();
        listView.setOnItemClickListener(this);

        refreshTopic.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                infoCount = 0;
                pageSize = 10;
                getTopicData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if (isLast == 0) {
                    page++;
                    getTopicData();
                }else{
                    refreshTopic.onRefreshComplete();
                    showToast(R.string.nomore);
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if(!isHasNetWork) {
                    showToast(getString(R.string.noNeteork));
                }
            }
        });


    }

    @Override
    protected void initData() {
        super.initData();
        page = 1;
        infoCount = 0;
        pageSize = 10;
        getTopicData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = null;
        if (parent.getAdapter().getItemId(position) == -1) {
        } else {
            InforItemDetail detail = (InforItemDetail) parent.getAdapter().getItem(position);
            if (detail.getType() == 1) {//类型:1图文  跳转
                intent = new Intent(this, InformationDetailActivity.class);
                intent.putExtra("id", detail.getId() + "");
                intent.putExtra("type", detail.getType());
                if (mInfoCatalog != null) {
                    intent.putExtra("categoryId", mInfoCatalog.getParent().getId() + "");
                    intent.putExtra("pid", mInfoCatalog.getParent().getPid() + "");
                }
                startActivity(intent);
            } else if (detail.getType() == 2) {//2专题  跳转
                intent = new Intent();
                intent.putExtra("activityId", detail.getId());
                intent.putExtra("zhuanTitle", detail.getTitle());
                intent.putExtra("mInfoCatalog", mInfoCatalog);
                intent.putExtra("url", detail.getIcon());
                intent.setClass(this, InformationTopicActivity.class);
                startActivity(intent);
            } else if (detail.getType() == 3) {//3图集  跳转
                intent = new Intent();
                intent.putExtra("activityId", detail.getId());
                intent.setClass(this, InformationAtlasActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
        }
    }

    /**
     * 请求专题列表数据
     */
    private void getTopicData() {

        Map<String, String> map = new HashMap<>();
        map.put("page", page + "");
        map.put("pageSize", pageSize + "");
        map.put("infoCount", infoCount + "");
        map.put("activityId", activityId + "");
        LogUtil.e("TAG", "send---------------------" + map.toString());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.TOPIC_LIST, map, HttpConstant.TOPIC_LIST);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        refreshTopic.onRefreshComplete();
        if (method.equals(HttpConstant.TOPIC_LIST)) {
            loadTopicData(object);
        }
        hideLoading();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        refreshTopic.onRefreshComplete();
        if (method.equals(HttpConstant.TOPIC_LIST)) {

        }
        showToast(getResources().getString(R.string.noNeteork));
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        refreshTopic.onRefreshComplete();
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载专题列表数据
     *
     * @param object
     */
    private void loadTopicData(JSONObject object) {
        try {
            if ("0".equals(object.getString("code")) && object.has("object")) {
                InforList inforList = GsonUtil.getBean(object.getString("object").toString(), InforList.class);
                if (page == 1) {
                    inforItemDetails.clear();
                }
                infoCount = inforList.getTotal();
                isLast = inforList.getIsLast();

                if (page == 1) {
                    mCache.put(HttpConstant.TOPIC_LIST + activityId, object);
                    addListviewHead(inforList.getRemain());
                    if (inforList.getTitle() != null) {
                        setLeftIncludeTitle(inforList.getTitle());
                    }
                }
                inforItemDetails.addAll(inforList.getList());
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加头部view
     *
     * @param strUrl 图片地址
     */
    private void addListviewHead(String strUrl) {
        if (view != null) {
            listView.removeHeaderView(view);
        }
        adapter = new ExpertAdapter(context, inforItemDetails);
        view = LayoutInflater.from(context).inflate(R.layout.layout_head_img_topic, null);
        img = (ImageView) view.findViewById(R.id.iv_topic_infor_fragment);
        AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + strUrl, img);
        listView.addHeaderView(view);
        listView.setAdapter(adapter);
    }

    /**
     * 加载缓存
     */
    private void loadCahce() {
        loadCahceTask task = new loadCahceTask();
        task.execute();
    }

    private class loadCahceTask extends AsyncTask<Void, Void, Map<String, JSONObject>> {
        Map<String, JSONObject> map;

        @Override
        protected Map<String, JSONObject> doInBackground(Void... params) {
            map = new HashMap<>();
            JSONObject topic_list = mCache.getAsJSONObject(HttpConstant.TOPIC_LIST + activityId);
            if (topic_list != null) {
                map.put(HttpConstant.TOPIC_LIST + activityId, topic_list);
            }
            return map;
        }

        @Override
        protected void onPostExecute(Map<String, JSONObject> stringJSONObjectMap) {
            super.onPostExecute(stringJSONObjectMap);
            for (Map.Entry<String, JSONObject> maps : stringJSONObjectMap.entrySet()) {
                if (maps.getKey().equals(HttpConstant.TOPIC_LIST + activityId)) {
                    loadTopicData(maps.getValue());
                } else {
                    showToast("请检查网络连接");
                }
            }

        }
    }
}
