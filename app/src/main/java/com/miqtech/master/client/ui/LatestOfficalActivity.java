package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.AthleticsListAdapter;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.City;
import com.miqtech.master.client.entity.RecommendInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.RefreshLayout;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/17 0017.
 */
public class LatestOfficalActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    @Bind(R.id.prlvOfficialActivity)
    PullToRefreshListView prlvOfficialActivity;
    HasErrorListView lvOfficialActivity;

    private int total = 0;
    private int pageSize;
    private int currentPage = 1;
    private int isLast;

    private List<RecommendInfo> recreationMatchInfos = new ArrayList<RecommendInfo>();

    private AthleticsListAdapter adapter;

    private Context context;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_officialactivity);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        context = this;
        prlvOfficialActivity.setMode(PullToRefreshBase.Mode.BOTH);
        lvOfficialActivity = prlvOfficialActivity.getRefreshableView();
        lvOfficialActivity.setOnItemClickListener(this);
        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("官方赛事");
        getLeftBtn().setOnClickListener(this);
        adapter = new AthleticsListAdapter(context, recreationMatchInfos);
        lvOfficialActivity.setErrorView("yoho，还木有官方赛事，敬请期待！！！", R.drawable.blank_fight);
        lvOfficialActivity.setAdapter(adapter);
        adapter.notifyDataSetChanged();
       prlvOfficialActivity.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
           @Override
           public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
               currentPage = 1;
               total = 0;
               loadOfficialActivity();
           }

           @Override
           public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
               if (isLast == 0) {
                   currentPage++;
                   new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           loadOfficialActivity();
                       }
                   }, 1000);
               } else {
                  prlvOfficialActivity.onRefreshComplete();
                   showToast(context.getResources().getString(R.string.load_no));
               }
           }

           @Override
           public void isHasNetWork(boolean isHasNetWork) {
               if(!isHasNetWork){
                   showToast(getString(R.string.noNeteork));
               }
           }
       });
    }
    @Override
    protected void initData() {
        super.initData();
        loadOfficialActivity();
    }
    private void loadOfficialActivity() {
        HashMap params = new HashMap();
        params.put("page", currentPage + "");
        if (total != 0) {
            params.put("lastTotal", total + "");
        }
        String lastCityStr = PreferencesUtil.getLastRecreationCity(context);
        if (TextUtils.isEmpty(lastCityStr)) {
            if (Constant.currentCity != null && !TextUtils.isEmpty(Constant.currentCity.getAreaCode())) {
                params.put("areaCode", Constant.currentCity.getAreaCode());
            }
        } else {
            String areaCode = GsonUtil.getBean(lastCityStr, City.class).getAreaCode();
            if (!TextUtils.isEmpty(areaCode)) {
                params.put("areaCode", areaCode);
            }

        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.OFFICIAL_ACTIVITY, params, HttpConstant.OFFICIAL_ACTIVITY);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        if (HttpConstant.OFFICIAL_ACTIVITY.equals(method)) {
            setListView(object);
        }
    }

    private void setListView(JSONObject object) {
        if (object.has("object")) {
            try {
                String objStr = object.getString("object");
                JSONObject jsonObj = new JSONObject(objStr);
                String dataStr = jsonObj.getString("data");
                JSONObject jsonData = new JSONObject(dataStr);
                String listStr = jsonData.getString("list");
                isLast = jsonData.getInt("isLast");
                total = jsonData.getInt("total");
                List<RecommendInfo> newRecreationMatchInfos = GsonUtil.getList(listStr, RecommendInfo.class);
                if (newRecreationMatchInfos.size() == 0 && currentPage == 1) {
                    lvOfficialActivity.setErrorShow(true);
                    currentPage = 1;
                    prlvOfficialActivity.onRefreshComplete();

                } else {
                    if (currentPage == 1) {
                        recreationMatchInfos.clear();
                        prlvOfficialActivity.onRefreshComplete();
                    } else {
                        prlvOfficialActivity.onRefreshComplete();
                    }
                    recreationMatchInfos.addAll(newRecreationMatchInfos);
                    adapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setClass(context, OfficalEventActivity.class);
        intent.putExtra("matchId", recreationMatchInfos.get(position).getId() + "");
        startActivity(intent);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
        }
    }
}
