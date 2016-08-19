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
import com.miqtech.master.client.adapter.MyAmuseAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.MatchV2;
import com.miqtech.master.client.entity.MyAmuse;
import com.miqtech.master.client.entity.MyAmuseSet;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.EventDetailActivity;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.RecreationMatchDetailsActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.GsonUtil;
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
 * Created by Administrator on 2015/12/14.
 */
public class FragmentMyAmuse extends MyBaseFragment implements AdapterView.OnItemClickListener {

    private View mainView;
    private Context mContext;
    private PullToRefreshListView prlvMyRecreation;
    private HasErrorListView mListView;

    private boolean isFirstShow = true;
    private User user;

    private int page = 1;
    private int pageSize = 10;
    private int isLast;

    private List<MatchV2> myAmuseList = new ArrayList<MatchV2>();
    private MyAmuseAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_my_amuse, null);
            mContext = inflater.getContext();
            initView();
        }
    }

    private void initView() {
        prlvMyRecreation = (PullToRefreshListView) mainView.findViewById(R.id.prlvMyReleaseMatch);
        prlvMyRecreation.setMode(PullToRefreshBase.Mode.BOTH);
        mListView = prlvMyRecreation.getRefreshableView();
        mListView.setErrorView("太低调了,还没有参加任何比赛");
        adapter = new MyAmuseAdapter(mContext, myAmuseList);
        mListView.setAdapter(adapter);
        prlvMyRecreation.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if (myAmuseList.size() > 0) {
                    if (isLast != 1) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                page++;
                                loadData();
                            }
                        }, 1000);

                    } else {
                        showToast(mContext.getResources().getString(R.string.load_no));
                        prlvMyRecreation.onRefreshComplete();
                    }

                } else {
                    prlvMyRecreation.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getActivity().getResources().getString(R.string.noNeteork));
                }
            }
        });
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirstShow) {
            loadData();
            isFirstShow = false;
        }
    }

    private void loadData() {
        user = WangYuApplication.getUser(mContext);

        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("page", page + "");
            map.put("pageSize", pageSize + "");
            map.put("type",2+"");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_MATCH, map, HttpConstant.MY_MATCH);
        } else {
            Intent intent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(intent);
            showToast(mContext.getResources().getString(R.string.login));
        }

    }


    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        try {
            if (method.equals(HttpConstant.MY_MATCH)) {
                if (0 == object.getInt("code") && object.has("result")) {
                    List<MatchV2> myAmuseSet = new Gson().fromJson(object.getJSONObject("object").getJSONArray("list").toString().trim(),
                            new TypeToken<List<MatchV2>>() {
                            }.getType());
                    isLast = object.getJSONObject("object").getInt("isLast");
                    if (page == 1) {
                        myAmuseList.clear();
                    }
                    myAmuseList.addAll(myAmuseSet);
                    if (page == 1 && myAmuseList.size() == 0) {
                        mListView.setErrorShow(true);
                    } else {
                        mListView.setErrorShow(false);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    showToast(object.getString("result"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        prlvMyRecreation.onRefreshComplete();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        prlvMyRecreation.onRefreshComplete();
        mListView.setErrorShow(false);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        prlvMyRecreation.onRefreshComplete();
        mListView.setErrorShow(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (myAmuseList.isEmpty() || myAmuseList.size() - 1 < position) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getContext(), EventDetailActivity.class);
        intent.putExtra("matchId", myAmuseList.get(position) + "");
        startActivity(intent);
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return mainView;
    }

    @Override
    public void refreView() {
        page = 1;
        loadData();
    }
}
