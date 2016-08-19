package com.miqtech.master.client.ui.fragment;

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
import com.miqtech.master.client.adapter.ActivitiesEventAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.Match;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.MyMatchActivity;
import com.miqtech.master.client.ui.OfficalEventActivity;
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
 * Created by Administrator on 2015/12/5.
 */
public class FragmentMyJoinMatch extends MyBaseFragment implements
        AdapterView.OnItemClickListener{
    private View mainView;

    private MyMatchActivity mContext;

    private PullToRefreshListView prlvMyJoinMatch;

    private HasErrorListView lvMyJoinMatch;

    private boolean isFirst = true;

    private ActivitiesEventAdapter adapter;

    private List<Match> mEvents = new ArrayList<Match>();// 赛事数据集合

    private int page = 1;

    private int rows = 10;

    private int isLast;

    private int currentId = -1;

    private Match match;

    private Intent intent;

    public static int payType = 1;
    public static int reserveType = 2;
//	public static DeleteView myDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.join_match_list, null);
            mContext = (MyMatchActivity) inflater.getContext();
            mContext.setMyJoinMatchFragment(this);
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
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirst) {
            isFirst = false;
            showLoading();
            loadMyReleaseWar();
        }
    }

    private void initView() {
        //myDialog = new DeleteView(mContext, R.style.delete_style,R.layout.delete_dialog);
        prlvMyJoinMatch = (PullToRefreshListView) mainView.findViewById(R.id.prlvMyJoinMatch);
        prlvMyJoinMatch.setMode(PullToRefreshBase.Mode.BOTH);
        lvMyJoinMatch =prlvMyJoinMatch.getRefreshableView();
        lvMyJoinMatch.setErrorView("太低调了,还没有收藏任何比赛");

        adapter = new ActivitiesEventAdapter(mEvents, mContext);
        lvMyJoinMatch.setAdapter(adapter);
        prlvMyJoinMatch.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                showLoading();
                loadMyReleaseWar();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if (mEvents.size() > 0) {
                    if (isLast == 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                page++;
                                showLoading();
                                loadMyReleaseWar();
                            }
                        }, 1000);
                    } else {
                        showToast(mContext.getResources().getString(R.string.load_no));
                       prlvMyJoinMatch.onRefreshComplete();
                    }
                } else {
                    prlvMyJoinMatch.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
             if(!isHasNetWork){
                 showToast(getActivity().getResources().getString(R.string.noNeteork));
             }
            }
        });
        lvMyJoinMatch.setOnItemClickListener(this);
    }

    private void initData() {
        adapter.notifyDataSetChanged();
    }

    public void loadMyReleaseWar() {
        User user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("page", page + "");
        map.put("rows", rows + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_JOIN_MATCH_LIST, map, HttpConstant.MY_JOIN_MATCH_LIST);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        hideLoading();
        try {
            Object obj = null;
            if (object.has("object")) {
                obj = object.getString("object");
            } else {
                obj = object.toString();
            }
            if (method.equals(HttpConstant.MY_JOIN_MATCH_LIST)) {
                if (obj == null) {
                    return;
                }
                JSONObject jsonList = new JSONObject(obj.toString());
                String strList = jsonList.getString("list");
                isLast = jsonList.getInt("isLast");
                List<Match> newMatch = new Gson().fromJson(strList, new TypeToken<List<Match>>() {
                }.getType());
                if (page == 1) {
                    mEvents.clear();
                }
                mEvents.addAll(newMatch);

                if (page == 1 && mEvents.size() == 0) {
                    lvMyJoinMatch.setErrorShow(true);
                } else {
                    lvMyJoinMatch.setErrorShow(false);
                }

                initData();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       prlvMyJoinMatch.onRefreshComplete();
    }

    @Override
    public void onError(String errMsg, String method) {
        hideLoading();
        prlvMyJoinMatch.onRefreshComplete();
        lvMyJoinMatch.setErrorShow(false);
        ToastUtil.showToast(errMsg, mContext);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        prlvMyJoinMatch.onRefreshComplete();
        lvMyJoinMatch.setErrorShow(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mEvents.isEmpty() || mEvents.size() < position) {
            return;
        }
        match = mEvents.get(position);
        intent = new Intent();
        intent.putExtra("matchId", match.getId() + "");
        intent.setClass(mContext, OfficalEventActivity.class);
        startActivity(intent);
    }
    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mainView;
    }

    @Override
    public void refreView() {
        page = 1;
        showLoading();
        loadMyReleaseWar();
    }
}
