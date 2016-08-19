package com.miqtech.master.client.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.MyCollectActivityAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.MyAmuse;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.OfficalEventActivity;
import com.miqtech.master.client.ui.RecreationMatchDetailsActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshListView;
import com.miqtech.master.client.watcher.Observerable;

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
 * Created by Administrator on 2016/1/13.
 */
public class FragmentActivityCollect extends MyBaseFragment implements Observerable.ISubscribe {
    @Bind(R.id.prlvActivity)
    PullToRefreshListView prlvActivity;
    HasErrorListView lvActivity;
    private MyCollectActivityAdapter adapter;
    private List<MyAmuse> mDatas = new ArrayList<>();
    private int page = 1;
    private Map<String, String> params = new HashMap<>();
    private boolean isLoadmore = false;

    private MyAmuse info;

    private boolean isFirst = true;

    private Observerable watcher = Observerable.getInstance();

    private boolean favor = true;

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_collect, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        watcher.subscribe(Observerable.ObserverableType.COLLECTSTATE, this);
        initView();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirst) {
            isFirst = false;
            loadData(params);
        }
    }

    protected void initView() {
        prlvActivity.setMode(PullToRefreshBase.Mode.BOTH);
        lvActivity = prlvActivity.getRefreshableView();
        adapter = new MyCollectActivityAdapter(getContext(), mDatas);
        lvActivity.setErrorView("并没有什么比赛");
        lvActivity.setAdapter(adapter);
        prlvActivity.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                loadData(params);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page++;
                isLoadmore = true;
                loadData(params);
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getActivity().getResources().getString(R.string.noNeteork));
                }
            }
        });

        lvActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDatas == null || mDatas.isEmpty()) {
                    return;
                }
                info = mDatas.get(position);
                if (info != null) {
                    Intent intent = new Intent();
                    switch (info.getType()) {
                        case 1:
                            intent.setClass(getContext(), OfficalEventActivity.class);
                            intent.putExtra("matchId", info.getId() + "");
                            startActivity(intent);
                            break;
                        case 2:
                            intent.setClass(getContext(), RecreationMatchDetailsActivity.class);
                            intent.putExtra("id", info.getId() + "");
                            startActivity(intent);
                            break;
                    }
                }

            }
        });
    }

    private void loadData(Map<String, String> params) {
        params.put("page", page + "");
        User user = WangYuApplication.getUser(getContext());
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        LogUtil.e("tag", "params : " + params.toString());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_ACTIVITY_MYFAVOR, params, HttpConstant.MY_ACTIVITY_MYFAVOR);
    }

    @Override
    public void onDestroy() {
        watcher.unSubscribe(Observerable.ObserverableType.COLLECTSTATE, this);
        watcher = null;
        super.onDestroy();
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        LogUtil.e("data", "params : " + object.toString());
        if (HttpConstant.MY_ACTIVITY_MYFAVOR.equals(method)) {
            try {
                List<MyAmuse> list = initMatchInfo(object);
                if (list != null && !list.isEmpty()) {
                    initMatchView(list);
                } else {
                    initMatchView(list);
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
        prlvActivity.onRefreshComplete();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        prlvActivity.onRefreshComplete();
        if (mDatas != null && !mDatas.isEmpty()) {
            lvActivity.setErrorShow(false);
        } else {
            LogUtil.e("eee", "error === ");
            lvActivity.setErrorShow(true);
        }
    }


    private List<MyAmuse> initMatchInfo(JSONObject object) throws JSONException {
        List<MyAmuse> matchs = new ArrayList<>();
        if (object.getInt("code") == 0 && object.has("object") && object.getString("result").equals("success")) {
            JSONObject objectJson = object.getJSONObject("object");
            if (objectJson.has("list")) {
                JSONArray dataJson = objectJson.getJSONArray("list");
                matchs = GsonUtil.getList(dataJson.toString(), MyAmuse.class);
                return matchs;
            }
        }
        return matchs;
    }

    private void initMatchView(List<MyAmuse> matchs) {
        if (page == 1) {
            mDatas.clear();
        }
        mDatas.addAll(matchs);
        if (!mDatas.isEmpty()) {
            lvActivity.setErrorShow(false);
        } else {
            lvActivity.setErrorShow(true);
        }
        adapter.setData(mDatas);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void refreView() {
        page = 1;
        loadData(params);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (info != null && !favor) {
            if (mDatas.contains(info)) {
                mDatas.remove(info);
                if (!mDatas.isEmpty()) {
                    lvActivity.setErrorShow(false);
                } else {
                    lvActivity.setErrorShow(true);
                }
                adapter.notifyDataSetChanged();
            }
            favor = true;
        }
    }

    @Override
    public <T> void update(T... data) {
        int type = (Integer) data[0];
        if (type == 2) {
            this.favor = (Boolean) data[2];
        }
    }
}
