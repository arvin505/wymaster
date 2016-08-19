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
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.WeekHotAdapter;
import com.miqtech.master.client.entity.Game;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.SubjectActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.view.RefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Administrator on 2015/12/2.
 */
public class FragmentWeekHot extends BaseFragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, RefreshLayout.OnLoadListener {
    RefreshLayout myrefer;
    ListView lvWeekHot;

    private View mainView;
    private Context mContext;
    private WeekHotAdapter adapter;
    private List<Game> gameList = new ArrayList<Game>();
    private int page = 1;
    private int rows = 10;
    private int isLast = 0;
    private int total = 0;
    private int currentPage = 0;
    private boolean isfirst = true;
    private WeekHotAdapter.DownLoadListen listen = new WeekHotAdapter.DownLoadListen() {
        @Override
        public void onDownload(int id) {
            getDownloadUrl(id);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.activity_week_hot, null);
            mContext = inflater.getContext();
            initView();
        }
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //mainView = inflater.inflate(R.layout.activity_week_hot, container, false);
        return mainView;
    }

    private void initView() {
        lvWeekHot = (ListView)mainView.findViewById(R.id.lvWeekHot);
        myrefer = (RefreshLayout)mainView.findViewById(R.id.weekHot_refresh);
        adapter = new WeekHotAdapter(mContext, gameList, listen);
        myrefer.setColorSchemeResources(R.color.cpb_complete_state_selector);
        lvWeekHot.setAdapter(adapter);
        lvWeekHot.setOnItemClickListener(this);
        lvWeekHot.setOnItemLongClickListener(this);
        myrefer.setOnLoadListener(this);
        myrefer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                loadWeekHotData();
            }
        });
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        if (isVisibleToUser && isfirst) {
            loadWeekHotData();
            isfirst = false;
        }
    }

    private void loadWeekHotData() {
        HashMap map = new HashMap();
        map.put("page", page + "");
        map.put("rows", rows + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.GAME_WEEK_HOT_LIST, map, HttpConstant.GAME_WEEK_HOT_LIST);
    }

    private void getDownloadUrl(int gameId) {
        HashMap map = new HashMap();
        map.put("id", gameId + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.GAME_DOWNLOAD, map, HttpConstant.GAME_DOWNLOAD);
    }

    @Override
    public void onLoad() {
        if (isLast == 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    page++;
                    loadWeekHotData();
                }
            },1000);
        } else {
            showToast("没有更多的数据");
            myrefer.setLoading(false);
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {

        try {
            Object obj = object.getString("object");
            if (method.equals(HttpConstant.GAME_WEEK_HOT_LIST)) {
                if (obj == null) {
                    return;
                }
                JSONObject jsonList = new JSONObject(obj.toString());
                String strList = jsonList.getString("list");
                isLast = jsonList.getInt("isLast");
                total = jsonList.getInt("total");
                currentPage = jsonList.getInt("currentPage");
                List<Game> games = new Gson().fromJson(strList, new TypeToken<List<Game>>() {
                }.getType());
                if (page == 1) {
                    gameList.clear();
                }
                gameList.addAll(games);
                initData();
            }

            if (method.equals(HttpConstant.GAME_DOWNLOAD)) {
                JSONObject jsonObj = new JSONObject(obj.toString());
                String url_android = jsonObj.getString("url_android");
                Intent intent = new Intent();
                intent.setClass(mContext, SubjectActivity.class);
                intent.putExtra("download_url", url_android);
                intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.DOWNLOADGAME);
                startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        myrefer.setRefreshing(false);
        myrefer.setLoading(false);
    }

    private void initData() {
        adapter.notifyDataSetChanged();
        myrefer.setLoading(false);
    }

    @Override
    public void onError(String errMsg, String method) {
        myrefer.setRefreshing(false);
        myrefer.setLoading(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
}
