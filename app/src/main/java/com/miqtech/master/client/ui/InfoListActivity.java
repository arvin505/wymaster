package com.miqtech.master.client.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.InfoListAdapter;
import com.miqtech.master.client.entity.InforItemDetail;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/7/8.
 */
public class InfoListActivity extends BaseActivity implements View.OnClickListener{
    private PullToRefreshListView prlvInfo;
    private Context context;
    private HasErrorListView lvInfo;
    private int page = 1;
    private int pageSize=10;
    private InfoListAdapter infoListAdapter;
    private List<InforItemDetail> newItems=new ArrayList<InforItemDetail>();
    private int isLast = 0;
    private String roundId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_info_list);
        initView();
        setTitle();
        initData();
    }

    private void setTitle() {
        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("资讯动态");
        getLeftBtn().setOnClickListener(this);
    }
    @Override
    protected void initData() {
        super.initData();
        infoListAdapter=new InfoListAdapter(context,newItems,1);
        lvInfo.setErrorView("还没有动态");
        lvInfo.setAdapter(infoListAdapter);
        loadInfoList();
    }
    //获取咨询列表
    private void loadInfoList() {
        LogUtil.d(TAG, "加载资讯列表数据");
        Map parmas = new HashMap<String, String>();
        if (!TextUtils.isEmpty(roundId)) {
            parmas.put("roundId", roundId);
            parmas.put("infoCount", 0+"");
            parmas.put("page", page + "");
            parmas.put("pageSize", pageSize + "");
//            User user = WangYuApplication.getUser(this);
//            if (user != null) {
//                parmas.put("userId", user.getId());
//                parmas.put("token", user.getToken());
//            }
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.EVENT_INFO_LIST, parmas, HttpConstant.EVENT_INFO_LIST);
        }
    }
    @Override
    protected void initView() {
        super.initView();
        roundId= getIntent().getStringExtra("roundId");
        context = InfoListActivity.this;
        prlvInfo = (PullToRefreshListView)findViewById(R.id.prlvInfoLists);
        prlvInfo.setMode(PullToRefreshBase.Mode.BOTH);
        prlvInfo.setScrollingWhileRefreshingEnabled(true);
        lvInfo =prlvInfo.getRefreshableView();
        prlvInfo.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                loadInfoList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
              if (isLast == 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            page++;
                            loadInfoList();
                        }
                    }, 1000);
                } else {
                  showToast(getResources().getString(R.string.nomore));
                  prlvInfo.onRefreshComplete();
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
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        prlvInfo.onRefreshComplete();
        try {
            if (object == null) {
                return;
            }
         if(method.equals(HttpConstant.EVENT_INFO_LIST)){
                int code = object.getInt("code");
                if (code == 0 && object.has("object")) {
                    JSONObject resultObj = object.getJSONObject("object");
                    if (resultObj.has("list")) {
                        List<InforItemDetail> datas = new Gson().fromJson(resultObj.getJSONArray("list").toString(), new TypeToken<List<InforItemDetail>>() {
                        }.getType());
                        if(datas!=null && !datas.isEmpty()) {
                            if (page == 1) {
                                newItems.clear();
                            }
                            newItems.addAll(datas);
                            infoListAdapter.notifyDataSetChanged();
                            isLast = resultObj.getInt("isLast");
                        }else{
                            if(page==1){
                                lvInfo.setErrorShow(true);
                            }else{
                                lvInfo.setErrorShow(false);
                            }
                        }
                    }
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        lvInfo.setErrorShow(false);
        prlvInfo.onRefreshComplete();
        LogUtil.d(TAG, "onFaild:::" + method);
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
        lvInfo.setErrorShow(false);
        prlvInfo.onRefreshComplete();
        LogUtil.d(TAG,"onError:::"+errMsg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibLeft:
                onBackPressed();
                break;
        }

    }
}
