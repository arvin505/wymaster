package com.miqtech.master.client.ui;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.GameImageAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.Game;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.view.CircleFlowIndicator;
import com.miqtech.master.client.view.ViewFlow;
import com.miqtech.master.client.watcher.Observerable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaosentao on 2015/12/2.
 */
public class GameDetailActivity extends BaseActivity implements View.OnClickListener {

    private Game game;
    private List<Game> recommendations;
    private ViewFlow viewFlow;
    private TextView tvName;
    private TextView tvVersion;
    private TextView tvCollectNum;
    private TextView tvCollect;
    private TextView tvDownloadNum;
    private TextView tvAppSize;
    private TextView btnDownload;
    private TextView tvIntro;
    private LinearLayout llGame;
    private int id = -1;
    private TextView tvTextHandle;

    private Observerable observerable = Observerable.getInstance();

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.frag_layout);
        initView();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initData();
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();

        tvName = (TextView) findViewById(R.id.tvName);
        tvVersion = (TextView) findViewById(R.id.tvVersion);
        tvCollectNum = (TextView) findViewById(R.id.tvCollectNum);
        tvDownloadNum = (TextView) findViewById(R.id.tvDownloadNum);
        tvAppSize = (TextView) findViewById(R.id.tvAppSize);
        btnDownload = (TextView) findViewById(R.id.btnDownload);
        viewFlow = (ViewFlow) findViewById(R.id.viewflow);
        tvIntro = (TextView) findViewById(R.id.tvIntro);
        tvCollect = (TextView) findViewById(R.id.tvCollect);
        llGame = (LinearLayout) findViewById(R.id.llGame);
        tvTextHandle = (TextView) findViewById(R.id.tvTextHandle);

        setLeftIncludeTitle("手游详情");
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        btnDownload.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        if (id == -1) {
            id = getIntent().getIntExtra("id", -1);
        }
        loadGameDetail(id);
    }

    private void loadGameDetail(int id) {
        if (id != -1) {
            showLoading();
            Map<String, String> map = new HashMap<>();
            map.put("id", id + "");
            User user = WangYuApplication.getUser(this);
            if (user != null) {
                map.put("userId", user.getId());
                map.put("token", user.getToken());
            }
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.GAME_DETAIL, map, HttpConstant.GAME_DETAIL);
        }
    }

    private void setBanner() {
        if (game.getImgs().size() > 0) {
            viewFlow.setAdapter(new GameImageAdapter(this, game.getImgs()));
            viewFlow.setmSideBuffer(game.getImgs().size()); // 实际图片张数，
            // 我的ImageAdapter实际图片张数为3
            CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
            viewFlow.setFlowIndicator(indic);
            viewFlow.setTimeSpan(3000);
            viewFlow.setSelection(game.getImgs().size() * 1000); // 设置初始位置
            viewFlow.startAutoFlowTimer(); // 启动自动播放
        }
    }

    private void setView() {
        tvIntro.setMaxLines(Integer.MAX_VALUE);
        tvName.setText(game.getName());
        tvCollectNum.setText(game.getFavor_count() + "");
        tvDownloadNum.setText(game.getDownload_count() + "次下载");
        tvAppSize.setText(game.getAndroid_file_size() + "Mb");
        tvIntro.setText(game.getIntro());
        int introLines = tvIntro.getLineCount();

        if (introLines > 10) {
            tvTextHandle.setVisibility(View.VISIBLE);
            tvIntro.setMaxLines(10);
            tvTextHandle.setOnClickListener(this);
            tvTextHandle.setText("展开");
        } else {
            tvTextHandle.setVisibility(View.GONE);
            tvIntro.setMaxLines(Integer.MAX_VALUE);
        }

        tvVersion.setText("版本" + game.getVersion());
        if (game.getHas_favor() == 1) {
            tvCollect.setBackgroundResource(R.drawable.icon_collectioned);
        } else {
            tvCollect.setBackgroundResource(R.drawable.icon_collection);
        }
        tvCollect.setOnClickListener(this);
        addRecommendationViews();
    }

    private void addRecommendationViews() {
        llGame.removeAllViews();
        for (int i = 0; i < recommendations.size(); i++) {
            addRecommendationView(recommendations.get(i), i);
        }
    }

    private void addRecommendationView(Game recommendation, int index) {
        View view = View.inflate(this, R.layout.layout_recommendation_item, null);
        ImageView ivGame = (ImageView) view.findViewById(R.id.ivGame);
        TextView tvGameName = (TextView) view.findViewById(R.id.tvGameName);
        AsyncImage.loadPhoto(this, HttpConstant.SERVICE_UPLOAD_AREA + recommendation.getIcon(), ivGame);
        tvGameName.setText(recommendation.getName());
        view.setTag(index);
        view.setOnClickListener(this);
        llGame.addView(view);
    }

    private void getDownloadUrl(int gameId) {
        Map<String, String> map = new HashMap<>();
        map.put("id", gameId + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.GAME_DOWNLOAD, map, HttpConstant.GAME_DOWNLOAD);
    }


    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        try {
            Object obj = null;
            if (object.has("object")) {
                obj = object.getString("object");
            }
            if (method.equals(HttpConstant.GAME_DETAIL)) {
                try {
                    JSONObject jsonObj = new JSONObject(obj.toString());
                    String gameStr = jsonObj.getString("game");
                    game = new Gson().fromJson(gameStr, Game.class);
                    recommendations = new Gson().fromJson(jsonObj.getString("recommendations"),
                            new TypeToken<List<Game>>() {
                            }.getType());
                    setBanner();
                    setView();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if (method.equals(HttpConstant.GAME_DOWNLOAD)) {
                JSONObject jsonObj = new JSONObject(obj.toString());
                String url_android = jsonObj.getString("url_android");
                Intent intent = new Intent();
                intent.setClass(this, SubjectActivity.class);
                intent.putExtra("download_url", url_android);
                intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.DOWNLOADGAME);
                startActivity(intent);
            } else if (method.equals(HttpConstant.GAME_FAV)) {
                initData();
                JSONObject jsonObj = new JSONObject(obj.toString());
                if (jsonObj.has("isFavor")) {
                    if ("false".equals(jsonObj.getString("isFavor"))) {

                        observerable.notifyChange(Observerable.ObserverableType.COLLECTSTATE,
                                4, game.getId(), false);
                        showToast("取消收藏成功");
                    } else {
                        showToast("收藏成功");
                        observerable.notifyChange(Observerable.ObserverableType.COLLECTSTATE,
                                4, game.getId(), true);

                    }
                }

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        observerable = null;
        super.onDestroy();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        if (method.equals(HttpConstant.GAME_FAV)) {
            showToast("收藏失败");
        } else {
            try {
                showToast(object.getString("result"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.llRecommendation:
                int index = (Integer) v.getTag();
                id = recommendations.get(index).getId();
                loadGameDetail(id);
                break;
            case R.id.btnDownload:
                getDownloadUrl(game.getId());
                break;
            case R.id.tvCollect:
                User user = WangYuApplication.getUser(this);
                if (user == null) {
                    Intent intent = new Intent();
                    intent.setClass(this, LoginActivity.class);
                    startActivity(intent);
                    showToast("登录后才能收藏");
                } else {
                    collectGame(user);
                }
                break;
            case R.id.tvTextHandle:
                String handleStr = tvTextHandle.getText().toString();
                if (handleStr.equals("展开")) {
                    tvIntro.setMaxLines(Integer.MAX_VALUE);
                    tvTextHandle.setText("收起");
                } else if (handleStr.equals("收起")) {
                    tvIntro.setMaxLines(10);
                    tvTextHandle.setText("展开");
                }
                break;
            default:
                break;
        }
    }

    private void collectGame(User user) {
        showLoading();
        Map<String, String> map = new HashMap<>();
        map.put("id", id + "");
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.GAME_FAV, map, HttpConstant.GAME_FAV);
    }
}
