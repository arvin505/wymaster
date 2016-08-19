package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.YueZhanAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.MyMessage;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.entity.YueZhan;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.SubjectActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.ToastUtil;
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

/**
 * Created by Administrator on 2015/12/7.
 */
public class FragmentMyWarRelease extends MyBaseFragment implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private View mainView;

    private Context mContext;

    private PullToRefreshListView prlvMyWarRelease;

    private HasErrorListView lvMyWarRelease;

    private boolean isFirst = true;

    private YueZhanAdapter adapter;

    private List<YueZhan> wars = new ArrayList<YueZhan>();

    private int page = 1;

    private int rows = 10;

    private int isLast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_mywarrelease, null);
            mContext = inflater.getContext();
            initView();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
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
        prlvMyWarRelease = (PullToRefreshListView) mainView.findViewById(R.id.prlvMyWarRelease);
        prlvMyWarRelease.setMode(PullToRefreshBase.Mode.BOTH);
        lvMyWarRelease = prlvMyWarRelease.getRefreshableView();

        lvMyWarRelease.setErrorView("太低调了,还没有发布任何约战");

        adapter = new YueZhanAdapter(mContext, wars);
        lvMyWarRelease.setAdapter(adapter);
       prlvMyWarRelease.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
          @Override
          public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
              page = 1;
              loadMyReleaseWar();
          }

          @Override
          public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
              if (wars.size() > 0) {
                  if (isLast == 0) {
                      new Handler().postDelayed(new Runnable() {
                          @Override
                          public void run() {
                              page++;
                              loadMyReleaseWar();
                          }
                      }, 1000);
                  } else {
                      showToast(mContext.getResources().getString(R.string.load_no));
                      prlvMyWarRelease.onRefreshComplete();
                  }
              } else {
                  prlvMyWarRelease.onRefreshComplete();
              }
          }

          @Override
          public void isHasNetWork(boolean isHasNetWork) {
              if(!isHasNetWork){
                  showToast(getString(R.string.noNeteork));
              }
          }
      });
        lvMyWarRelease.setOnItemClickListener(this);
    }

    private void initData() {
        loadMyReleaseWar();
    }

    private void loadMyReleaseWar() {
        showLoading();
        User user = WangYuApplication.getUser(mContext);
        if (user!=null) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("page", page + "");
            map.put("rows", rows + "");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_RELEASE_WAR, map, HttpConstant.MY_RELEASE_WAR);
        }
    }


    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mainView;
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        hideLoading();
        try {
            Object obj = null;
            if (object.has("object")) {
                obj = object.getString("object");
            }
            if (method.equals(HttpConstant.MY_RELEASE_WAR)) {
                if (obj == null) {
                    return;
                }
                JSONObject jsonList = new JSONObject(obj.toString());
                String strList = jsonList.getString("list");
                isLast = jsonList.getInt("isLast");
                List<YueZhan> newWars = new Gson().fromJson(strList, new TypeToken<List<YueZhan>>() {
                }.getType());
                if (page == 1) {
                    wars.clear();
                }
                wars.addAll(newWars);
                if (page == 1 && wars.size() == 0) {
                    lvMyWarRelease.setErrorShow(true);
                } else {
                    lvMyWarRelease.setErrorShow(false);
                }
               prlvMyWarRelease.onRefreshComplete();
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        hideLoading();
        prlvMyWarRelease.onRefreshComplete();
        lvMyWarRelease.setErrorShow(false);
        ToastUtil.showToast(errMsg, mContext);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        prlvMyWarRelease.onRefreshComplete();
        lvMyWarRelease.setErrorShow(false);

    }

    private void setIsReaded(MyMessage message) {
        User user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("type", message.getType() + "");
        map.put("msgId", message.getId() + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SET_MSG_READED, map, HttpConstant.SET_MSG_READED);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
        if (wars.isEmpty() || wars.size() - 1 < position) {
            return;
        }
        YueZhan war = wars.get(position);
        int id = war.getId();
        Intent intent = new Intent();
        intent.setClass(mContext, SubjectActivity.class);
        intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.YUEZHAN);
        intent.putExtra("id", id + "");
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override
    public void refreView() {
        page = 1;
        loadMyReleaseWar();
    }
}
