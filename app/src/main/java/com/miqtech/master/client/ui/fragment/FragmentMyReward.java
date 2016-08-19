package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.MyRewardAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.Reward;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.RewardActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaoyi on 2016/8/3.
 */
public class FragmentMyReward extends BaseFragment {
    @Bind(R.id.prlvMyReward)
    PullToRefreshListView prlvMyReward;
    private HasErrorListView lvMyJoinMatch;
    MyRewardAdapter adapter;
    private List<Reward> mDatas = new ArrayList<>();
    int status = 0;
    private int page = 1;
    private int pageSize = 5;
    private int isLast = 0;

    private Context context;

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_reward, container, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        status = getArguments().getInt("status");
        initView();
        loadData();
        context = getActivity();
    }

    private void initView() {
        prlvMyReward.setMode(PullToRefreshBase.Mode.BOTH);
        lvMyJoinMatch = prlvMyReward.getRefreshableView();
        lvMyJoinMatch.setErrorView("赶快去参与悬赏令，赢取精美奖品吧");

        adapter = new MyRewardAdapter(mDatas, getContext());
        lvMyJoinMatch.setAdapter(adapter);

        prlvMyReward.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if (mDatas.size() > 0) {
                    if (isLast != 1) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                page++;
                                loadData();
                            }
                        }, 1000);

                    } else {
                        showToast(getResources().getString(R.string.load_no));
                        prlvMyReward.onRefreshComplete();
                    }

                } else {
                    prlvMyReward.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getActivity().getResources().getString(R.string.noNeteork));
                }
            }
        });

        lvMyJoinMatch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent2 = new Intent(context, RewardActivity.class);
                intent2.putExtra("isEnd", status + "");
                intent2.putExtra("rewardId", mDatas.get(position).getId());
                intent2.putExtra("isOne",1);
                context.startActivity(intent2);
            }
        });
    }

    private void loadData() {
        showLoading();
        User user = WangYuApplication.getUser(WangYuApplication.appContext);
        Map<String, String> params = new HashMap<>();
        params.put("isEnd", status + "");
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        params.put("token", user.getToken() + "");
        params.put("userId", user.getId() + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_BOUNTY, params, HttpConstant.MY_BOUNTY);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        prlvMyReward.onRefreshComplete();
        try {
            Gson gson = new Gson();
            switch (method) {
                case HttpConstant.MY_BOUNTY:
                    List<Reward> newData = gson.fromJson(object.getJSONObject("object").getJSONArray("list").toString(),
                            new TypeToken<List<Reward>>() {
                            }.getType());
                    isLast = object.getJSONObject("object").getInt("isLast");
                    if (page == 1) {
                        mDatas.clear();
                    }
                    mDatas.addAll(newData);
                    if (page == 1 && mDatas.size() == 0) {
                        lvMyJoinMatch.setErrorShow(true);
                    } else {
                        lvMyJoinMatch.setErrorShow(false);
                    }
                    adapter.notifyDataSetChanged();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        prlvMyReward.onRefreshComplete();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        prlvMyReward.onRefreshComplete();
        if (mDatas == null || mDatas.isEmpty()) {
            lvMyJoinMatch.setErrorShow(true);
        }
        if (page > 1) {
            page--;
        }
    }
}
