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
import com.miqtech.master.client.adapter.MyGameAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.Game;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.GameDetailActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
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
 * 游戏收藏
 * Created by Administrator on 2015/12/4.
 */
public class FragmentGameCollect extends MyBaseFragment implements AdapterView.OnItemClickListener,
        Observerable.ISubscribe {

    private View mainView;

    private Context mContext;

    private PullToRefreshListView prlvGame;

    private HasErrorListView lvGame;

    private boolean isFirst = true;

    private MyGameAdapter adapter;

    private List<Game> games = new ArrayList<Game>();

    private int page = 1;

    private int pageSize = 10;

    private int isLast;
    User user;

    private Game game;
    private boolean hasfavor = true;

    private Observerable watcher = Observerable.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_gamecollect, null);
            mContext = inflater.getContext();
            initView();
        }
        watcher.subscribe(Observerable.ObserverableType.COLLECTSTATE, this);
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
        prlvGame = (PullToRefreshListView) mainView.findViewById(R.id.prlvGame);
        prlvGame.setMode(PullToRefreshBase.Mode.BOTH);
        lvGame = prlvGame.getRefreshableView();

        lvGame.setErrorView("太低调了,还没有收藏任何游戏");
        adapter = new MyGameAdapter(mContext, games);
        lvGame.setAdapter(adapter);
        prlvGame.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                loadMyCollectGame();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if (games.size() > 0) {
                    if (isLast == 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                page++;
                                loadMyCollectGame();
                            }
                        }, 1000);

                    } else {
                        showToast(mContext.getResources().getString(R.string.load_no));
                        prlvGame.onRefreshComplete();

                    }
                } else {
                    prlvGame.onRefreshComplete();

                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getActivity().getResources().getString(R.string.noNeteork));
                }
            }
        });
        lvGame.setOnItemClickListener(this);

    }

    private void initData() {
        loadMyCollectGame();
    }

    private void loadMyCollectGame() {
        user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("page", page + "");
        map.put("pageSize", pageSize + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_COLLECT_GAME, map, HttpConstant.MY_COLLECT_GAME);
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
            if (method.equals(HttpConstant.MY_COLLECT_GAME)) {
                if (obj == null) {
                    return;
                }
                JSONObject jsonList = new JSONObject(obj.toString());
                String strList = jsonList.getString("list");
                isLast = jsonList.getInt("isLast");
                List<Game> newGames = new Gson().fromJson(strList, new TypeToken<List<Game>>() {
                }.getType());
                if (page == 1) {
                    games.clear();
                }
                games.addAll(newGames);
                if (page == 1 && games.size() == 0) {
                    lvGame.setErrorShow(true);
                } else {
                    lvGame.setErrorShow(false);
                }
                adapter.notifyDataSetChanged();
            } else if (method.equals(HttpConstant.GAME_FAV)) {
                games.remove(game);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        prlvGame.onRefreshComplete();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        lvGame.setErrorShow(false);
        prlvGame.onRefreshComplete();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        lvGame.setErrorShow(false);
        prlvGame.onRefreshComplete();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
        if (games.isEmpty() || games.size() - 1 < position) {
            return;
        }
        game = games.get(position);
        Intent intent = new Intent();
        intent.putExtra("id", game.getId());
        intent.setClass(mContext, GameDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        watcher.unSubscribe(Observerable.ObserverableType.COLLECTSTATE, this);
        watcher = null;
        super.onDestroy();
    }

    @Override
    public void refreView() {
        page = 1;
        loadMyCollectGame();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (games != null && !hasfavor) {
            if (games.contains(game)) {
                games.remove(game);
                adapter.notifyDataSetChanged();
                hasfavor = true;
            }
            if (games.size() == 0) {
                page = 1;
                loadMyCollectGame();
            }
        }

    }

    @Override
    public <T> void update(T... data) {
        int type = (Integer) data[0];
        if (type == 4) {
            boolean favor = (Boolean) data[2];
            this.hasfavor = favor;
        }
    }
}
