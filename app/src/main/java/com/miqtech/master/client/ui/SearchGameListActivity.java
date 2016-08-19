package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.WeekHotAdapter;
import com.miqtech.master.client.entity.Game;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.view.RefreshLayout;
import com.miqtech.master.client.view.SearchGame;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索弹框框
 * Created by Administrator on 2015/12/2.
 */
public class SearchGameListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, RefreshLayout.OnLoadListener {


    private Context context;
    private ImageView ivSearchModle;
    private ListView lvSearchGame;
    private List<Game> gameList = new ArrayList<Game>();
    private WeekHotAdapter adapter;
    private String searchText;
    private SearchGame searchGame;
    private RefreshLayout myRef;
    private WeekHotAdapter.DownLoadListen listen = new WeekHotAdapter.DownLoadListen() {
        @Override
        public void onDownload(int id) {
            getDownloadUrl(id);
        }
    };

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.search_game_list);
        context = this;
        initView();
        Intent intent = getIntent();
        if (intent != null) {
            searchText = getIntent().getStringExtra("searchText");
            if (TextUtils.isEmpty(searchText)) {
                return;
            } else {
                requestSearchGame(searchText);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        searchText = intent.getStringExtra("searchText");
        if (searchText.trim().length() == 0) {
            return;
        }
        requestSearchGame(searchText);
    }

    @Override
    protected void initView() {
        super.initView();
        setLeftIncludeTitle("游戏搜索");
        getLeftBtn().setOnClickListener(this);
        setLeftBtnImage(R.drawable.back);
        ivSearchModle = (ImageView) findViewById(R.id.ibRight);
        ivSearchModle.setImageResource(R.drawable.icon_search);
        ivSearchModle.setVisibility(View.VISIBLE);

        lvSearchGame = (ListView) findViewById(R.id.lvSearchGame);
        myRef = (RefreshLayout) findViewById(R.id.myRef);

        adapter = new WeekHotAdapter(context, gameList, listen);
        lvSearchGame.setAdapter(adapter);
        lvSearchGame.setOnItemClickListener(this);
        ivSearchModle.setOnClickListener(this);

        searchGame = new SearchGame(this, R.style.searchStyle);
        myRef.setOnLoadListener(this);
    }

    private void requestSearchGame(String serarchText) {
        showLoading();
        Map<String, String> map = new HashMap<>();
        map.put("gameName", searchText);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.GAME_SEARCH, map, HttpConstant.GAME_SEARCH);
    }

    private void getDownloadUrl(int gameId) {
        showLoading();
        Map<String, String> map = new HashMap<>();
        map.put("id", gameId + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.GAME_DOWNLOAD, map, HttpConstant.GAME_DOWNLOAD);
    }

    @Override
    protected void initData() {
        super.initData();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.ibRight:
                searchGame.show();
                break;
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        try {
            Object obj = object.getString("object");
            if (HttpConstant.GAME_SEARCH.equals(method)) {
                List<Game> games = new Gson().fromJson(obj.toString(), new TypeToken<List<Game>>() {
                }.getType());
                if (gameList.size() > 0) {
                    gameList.clear();
                }
                gameList.addAll(games);
                initData();
            }
            if (method.equals(HttpConstant.GAME_DOWNLOAD)) {
                JSONObject jsonObj = new JSONObject(obj.toString());
                String url_android = jsonObj.getString("url_android");
                Intent intent = new Intent();
                intent.setClass(context, SubjectActivity.class);
                intent.putExtra("download_url", url_android);
                intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.DOWNLOADGAME);
                startActivity(intent);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
        if (HttpConstant.GAME_SEARCH.equals(method)) {
            showToast(errMsg);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}
