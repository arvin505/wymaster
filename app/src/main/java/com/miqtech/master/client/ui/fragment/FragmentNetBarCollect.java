package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.NetBarAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.InternetBarInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.InternetBarActivityV2;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ToastUtil;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshListView;
import com.miqtech.master.client.watcher.Observerable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网吧收藏
 * Created by Administrator on 2015/12/4.
 */
public class FragmentNetBarCollect extends MyBaseFragment implements
        AdapterView.OnItemClickListener, Observerable.ISubscribe{
    private View mainView;

    private Context mContext;

    private PullToRefreshListView prlvNetBar;

    private HasErrorListView lvBar;

    private boolean isFirst = true;

    private NetBarAdapter adapter;

    private List<InternetBarInfo> bars = new ArrayList<InternetBarInfo>();


    private int page = 1;

    private int isLast;

    public static boolean isDelect = false;

    private boolean favor = true;

    private Observerable watcher = Observerable.getInstance();

    private InternetBarInfo bar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_netbarcollect, null);
            mContext = inflater.getContext();
            initView();
        }

        watcher.subscribe(Observerable.ObserverableType.COLLECTSTATE,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        ViewGroup parent = (ViewGroup) mainView.getParent();
        if (parent != null) {
            parent.removeAllViewsInLayout();
        }
        return mainView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirst) {
            isFirst = false;
            initData();
        }
    }

    private void initView() {
        // TODO Auto-generated method stub
        prlvNetBar = (PullToRefreshListView) mainView.findViewById(R.id.prlvNetBar);
        prlvNetBar.setMode(PullToRefreshBase.Mode.BOTH);
        lvBar = prlvNetBar.getRefreshableView();

        lvBar.setErrorView("太低调了,还没有收藏任何网吧");
        adapter = new NetBarAdapter(mContext, bars, 2);
        lvBar.setAdapter(adapter);
        prlvNetBar.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                loadNetBar();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if (bars.size() > 0) {
                    if (isLast == 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                page++;
                                loadNetBar();
                            }
                        }, 1000);
                    } else {
                        showToast(mContext.getResources().getString(R.string.load_no));
                        prlvNetBar.onRefreshComplete();
                    }
                } else {
                  prlvNetBar.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
             if(!isHasNetWork){
                 showToast(getActivity().getResources().getString(R.string.noNeteork));
             }
            }
        });
        lvBar.setOnItemClickListener(this);
    }

    private void initData() {
        loadNetBar();
    }

    private void loadNetBar() {
        User user = WangYuApplication.getUser(mContext);
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("longitude", Constant.longitude + "");
            map.put("latitude", Constant.latitude + "");
            map.put("page", page + "");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.COLLECT_NETBAR, map, HttpConstant.COLLECT_NETBAR);
        }
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mainView;
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        try {
            Object obj = null;
            if (object.has("object")) {
                obj = object.getString("object");
            }
            if (method.equals(HttpConstant.COLLECT_NETBAR)) {
                if (obj == null) {
                    return;
                }
                JSONObject jsonList = new JSONObject(obj.toString());
                String strList = jsonList.getString("list");
                isLast = jsonList.getInt("isLast");
                List<InternetBarInfo> bars = new Gson().fromJson(strList, new TypeToken<List<InternetBarInfo>>() {
                }.getType());
                if (page == 1) {
                    this.bars.clear();
                }
                this.bars.addAll(bars);
                if (page == 1 && bars.size() == 0) {
                    lvBar.setErrorShow(true);
                } else {
                    lvBar.setErrorShow(false);
                }

                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       prlvNetBar.onRefreshComplete();
    }

    @Override
    public void onError(String errMsg, String method) {
        ToastUtil.showToast("数据获取发生失败", mContext);
        prlvNetBar.onRefreshComplete();
        lvBar.setErrorShow(false);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        prlvNetBar.onRefreshComplete();
        lvBar.setErrorShow(false);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
        if (bars.isEmpty() || bars.size() - 1 < position) {
            return;
        }
        bar = bars.get(position);
        String netbarId = bar.getId();
        Intent intent = new Intent();
        intent.putExtra("netbarId", netbarId);
        intent.setClass(mContext, InternetBarActivityV2.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bars != null && !favor) {
            if (bars.contains(bar)) {
                bars.remove(bar);
                adapter.notifyDataSetChanged();
            }
            if (bars.size() == 0) {
                page = 1;
                loadNetBar();
            }
            favor = true;
        }
    }

    @Override
    public void onDestroy() {
        watcher.unSubscribe(Observerable.ObserverableType.COLLECTSTATE,this);
        watcher = null;
        super.onDestroy();
    }
    @Override
    public void refreView() {
        page = 1;
        loadNetBar();
    }


    @Override
    public <T> void update(T... data) {
        if ((Integer)data[0]==1){
            LogUtil.e("tag","---------");
            this.favor = (Boolean)data[2];
        }
    }
}
