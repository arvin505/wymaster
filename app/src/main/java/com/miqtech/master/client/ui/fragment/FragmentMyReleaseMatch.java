package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.HomePageAdapter;
import com.miqtech.master.client.adapter.MyReleaseMatchAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.MatchV2;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.OfficalEventActivity;
import com.miqtech.master.client.ui.RecreationMatchDetailsActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
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
 * Created by admin on 2016/8/18.
 */
public class FragmentMyReleaseMatch extends BaseFragment implements AdapterView.OnItemClickListener, MyReleaseMatchAdapter.MatctchItemClickListener {
    private View mainView;
    private Context mContext;
    private PullToRefreshListView prlvMyReleaseMatch;
    private HasErrorListView mListView;

    private boolean isFirstShow = true;
    private User user;

    private int page = 1;
    private int pageSize = 10;
    private int isLast;

    private List<MatchV2> matches = new ArrayList<MatchV2>();
    private MyReleaseMatchAdapter adapter;

    private int officialMatchPosition;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_myreleasematch, null);
            mContext = inflater.getContext();
            initView();
        }
    }

    private void initView() {
        prlvMyReleaseMatch = (PullToRefreshListView) mainView.findViewById(R.id.prlvMyReleaseMatch);
        prlvMyReleaseMatch.setMode(PullToRefreshBase.Mode.BOTH);
        mListView =prlvMyReleaseMatch.getRefreshableView();
        mListView.setErrorView("太低调了,还没有参加任何比赛");
        adapter = new MyReleaseMatchAdapter(matches,mContext);

        adapter.setListener(this);

        mListView.setAdapter(adapter);


        prlvMyReleaseMatch.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if (matches.size() > 0) {
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
                        prlvMyReleaseMatch.onRefreshComplete();
                    }

                } else {
                    prlvMyReleaseMatch.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if(!isHasNetWork){
                    showToast(getActivity().getResources().getString(R.string.noNeteork));
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
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
            map.put("type",1+"");
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
                if (object.has("object")) {
                    ArrayList<MatchV2> newZifiMatchList = new Gson().fromJson(object.getJSONObject("object").getJSONArray("list").toString().trim(),
                            new TypeToken<List<MatchV2>>() {
                    }.getType());


                    if (page == 1) {
                        matches.clear();
                    }
                    matches.addAll(newZifiMatchList);
                    if (page == 1 && matches.size() == 0) {
                        mListView.setErrorShow(true);
                    } else {
                        mListView.setErrorShow(false);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    showToast(object.getString("result"));
                }
            }else if (HttpConstant.ROUND_INFO.equals(method)) {
                String objStr = object.getString("object");
                ArrayList<MatchV2.RoundInfo> rounds = new Gson().fromJson(objStr, new TypeToken<List<MatchV2.RoundInfo>>() {
                }.getType());
                MatchV2 match = matches.get(officialMatchPosition);
                match.setRounds(rounds);
                View matchView = mListView.getChildAt(officialMatchPosition);
                MyReleaseMatchAdapter.MatchOfficialViewHolder matchViewHolder = adapter.getHolder();
                for (int i = 0; i < rounds.size(); i++) {
                    MatchV2.RoundInfo round = rounds.get(i);
                    if (round.getState().equals("报名")) {
                        matchViewHolder.rlApply.setVisibility(View.VISIBLE);
                        matchViewHolder.tvApplyTime.setText(round.getDate()+"正在报名中");
                    } else if (round.getState().equals("进行")) {
                        matchViewHolder.rlDoing.setVisibility(View.VISIBLE);
                        matchViewHolder.tvDoingTime.setText(round.getDate()+"正在进行中");
                    } else if (round.getState().equals("预热")) {
                        matchViewHolder.rlWarmUp.setVisibility(View.VISIBLE);
                        matchViewHolder.tvWarmUpTime.setText(round.getDate()+"正在预热中");
                    }
                }
                matchViewHolder.ivArrows.setImageDrawable(getContext().getResources().getDrawable(R.drawable.match_filter_up));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        prlvMyReleaseMatch.onRefreshComplete();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        prlvMyReleaseMatch.onRefreshComplete();
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
        prlvMyReleaseMatch.onRefreshComplete();
        mListView.setErrorShow(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (matches.isEmpty() || matches.size() - 1 < position) {
            return;
        }
        Intent intent = new Intent(mContext, RecreationMatchDetailsActivity.class);
        intent.putExtra("id", matches.get(position).getId() + "");
        startActivity(intent);
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return mainView;
    }

    @Override
    public void officialMatchOnClick(int officialId) {
        Intent intent = new Intent();
        intent.setClass(getContext(), OfficalEventActivity.class);
        intent.putExtra("matchId", officialId + "");
        startActivity(intent);
    }

    @Override
    public void officialMatchRoundInfoOnClick(int position) {
        int officialMatchId = matches.get(position).getId();
        officialMatchPosition = position;
        loadMatchRoundInfo(officialMatchId);
    }

    private void loadMatchRoundInfo(int officialMatchId) {
        Map<String,String > params = new HashMap<>();
        params.put("id", officialMatchId + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ROUND_INFO, params, HttpConstant.ROUND_INFO);
    }
}
