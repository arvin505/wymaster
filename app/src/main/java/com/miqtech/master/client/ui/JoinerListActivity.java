package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.JoinerListAdapter;
import com.miqtech.master.client.entity.MatchJoiner;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.RefreshLayout;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshListView;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/1 0001.
 */
public class JoinerListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener{
    HasErrorListView lvJoiner;
    @Bind(R.id.prlvJoiner)
    PullToRefreshListView prlvJoiner;

    private List<MatchJoiner> joiners = new ArrayList<MatchJoiner>();
    private JoinerListAdapter adapter;
    private HashMap params = new HashMap();
    private int page = 1;
    private int pageSize = 10;
    private String id;
    private String type;
    private int isLast;

    private Context context;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_joinerlist);
        ButterKnife.bind(this);
        context = this;
        initView();
        initData();
    }

    @Override
    protected void initData() {
        super.initData();
        id = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");
        if (!TextUtils.isEmpty(id)&&!TextUtils.isEmpty(type)) {
            loadJoiner(id,type);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        prlvJoiner.setMode(PullToRefreshBase.Mode.BOTH);
        lvJoiner=prlvJoiner.getRefreshableView();
        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("参与的人");
        getLeftBtn().setOnClickListener(this);
        adapter = new JoinerListAdapter(this, joiners);
        lvJoiner.setAdapter(adapter);
        lvJoiner.setOnItemClickListener(this);
        prlvJoiner.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                loadJoiner(id,type);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if(isLast == 0){
                    page++;
                    loadJoiner(id,type);
                }else{
                    prlvJoiner.onRefreshComplete();
                    showToast(getResources().getString(R.string.nomore));
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

    private void loadJoiner(String id,String type) {
        params.clear();
        params.put("amuseId", id);
        params.put("type", type);
        params.put("page",page+"");
        params.put("pageSize",pageSize+"");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_APPLYER_LIST, params, HttpConstant.AMUSE_APPLYER_LIST);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        prlvJoiner.onRefreshComplete();
        LogUtil.e("object", "object : " + object.toString());
        if (HttpConstant.AMUSE_APPLYER_LIST.equals(method)) {
            initJoinerList(object);
        }
    }

    private void initJoinerList(JSONObject object) {
        try {
            if (object.getInt("code") == 0 && object.has("object")) {
                String strObj = object.getString("object");
                JSONObject jsonObj = new JSONObject(strObj);
                isLast = jsonObj.getInt("isLast");
                if(page == 1){
                    joiners.clear();
                }
                if(jsonObj.has("list")){
                    List<MatchJoiner> newMatchJoiner = GsonUtil.getList(jsonObj.getString("list"), MatchJoiner.class);
                    joiners.addAll(newMatchJoiner);
                    //               initRecreationView();
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        showToast("出现错误，请稍后重试");
       prlvJoiner.onRefreshComplete();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        showToast("出现错误，请稍后重试");
        prlvJoiner.onRefreshComplete();
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MatchJoiner joiner = joiners.get(position);
        if (joiner != null) {
            Intent intent = new Intent();
            intent.setClass(this, PersonalHomePageActivity.class);
            intent.putExtra("id", joiner.getUserId() + "");
            startActivity(intent);
        }
    }
}
