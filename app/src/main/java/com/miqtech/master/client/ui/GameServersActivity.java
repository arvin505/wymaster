package com.miqtech.master.client.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class GameServersActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
    private ListView lvGameServers;
    private List<String> gameServerList;
    private int yuezhanForGame = 0;
    private ImageView back;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_gameservers);
        yuezhanForGame = getIntent().getIntExtra("yuezhanForGame", 0);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        lvGameServers = (ListView) findViewById(R.id.lvGameServers);
        back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(this);
        //getLeftBtn().setOnClickListener(this);
        setLeftIncludeTitle("选择服务器");
        //setLeftBtnImage(R.drawable.back);
        lvGameServers.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        loadGameServers();
    }

    private void loadGameServers() {
        String gameId = getIntent().getStringExtra("gameId");
        User user = WangYuApplication.getUser(this);
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId() + "");
        params.put("token", user.getToken());
        params.put("itemId", gameId);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.GAME_SERVERS, params, HttpConstant.GAME_SERVERS);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object,method);
        if (HttpConstant.GAME_SERVERS.equals(method)) {
            String obj = null;
            try {
                obj = object.getJSONArray("object").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            gameServerList = new Gson().fromJson(obj.toString(), new TypeToken<List<String>>() {
            }.getType());
            ArrayAdapter adapter = new ArrayAdapter(this, R.layout.layout_gameserver_item, gameServerList);
            lvGameServers.setAdapter(adapter);

        }
    }

    @Override
    public void onError(String method, String errorInfo) {
        // TODO Auto-generated method stub
        hideLoading();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String gameServer = gameServerList.get(position);
        Intent intent = new Intent();
        intent.putExtra("gameServer", gameServer);
        setResult(RESULT_OK, intent);
        finish();
    }
}
