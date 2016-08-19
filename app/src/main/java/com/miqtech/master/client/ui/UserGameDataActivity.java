package com.miqtech.master.client.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.SelectGameAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.entity.UserGame;
import com.miqtech.master.client.entity.WarGame;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class UserGameDataActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
    private LinearLayout llUserGameName, llUserGameServe, llUserGameNickName;
    private TextView tvUserGameName;
    private EditText edtUserGameNickName;
    private TextView tvUserGameServe;

    private UserGame userGame;
    private Dialog selectGameDialog;
    private List<WarGame> games;
    private Button btnUserGameSave;
    private ImageView ivDeleteGame;
    private ImageView back;

    private static final int GAME_SERVER_REQUEST = 1;

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.activity_usergamedata);
        initData();
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        llUserGameName = (LinearLayout) findViewById(R.id.llUserGameName);
        llUserGameServe = (LinearLayout) findViewById(R.id.llUserGameServe);
        llUserGameNickName = (LinearLayout) findViewById(R.id.llUserGameNickName);
        tvUserGameName = (TextView) findViewById(R.id.tvUserGameName);
        tvUserGameServe = (TextView) findViewById(R.id.tvUserGameServe);
        edtUserGameNickName = (EditText) findViewById(R.id.edtUserGameNickName);
        btnUserGameSave = (Button) findViewById(R.id.btnUserGameSave);

        //setLeftBtnImage(R.drawable.back);
        //getLeftBtn().setOnClickListener(this);
        back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(this);
        if (TextUtils.isEmpty(userGame.getGame_name())) {
            setLeftIncludeTitle("添加游戏资料");
        } else {
            setLeftIncludeTitle("编辑游戏资料");
            tvUserGameName.setText(userGame.getGame_name());
            if (TextUtils.isEmpty(userGame.getGame_server())) {
                llUserGameServe.setVisibility(View.GONE);
            } else {
                tvUserGameServe.setText(userGame.getGame_server());
                llUserGameServe.setVisibility(View.VISIBLE);
            }
            edtUserGameNickName.setText(userGame.getGame_nickname());
        }
        llUserGameName.setOnClickListener(this);
        llUserGameServe.setOnClickListener(this);
        llUserGameNickName.setOnClickListener(this);
        btnUserGameSave.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        userGame = (UserGame) getIntent().getSerializableExtra("UserGame");
        if (userGame == null) {
            userGame = new UserGame();
        } else {
            ivDeleteGame = (ImageView) findViewById(R.id.iv_title_right);
            ivDeleteGame.setImageResource(R.drawable.delete_game_icon);
            ivDeleteGame.setVisibility(View.VISIBLE);
            ivDeleteGame.setOnClickListener(this);
        }
    }

    private void initGameDialog() {
        if (selectGameDialog == null) {
            selectGameDialog = new Dialog(this, R.style.searchStyle);
            selectGameDialog.setContentView(R.layout.layout_select_game);
            Window dialogWindow = selectGameDialog.getWindow();
            dialogWindow.setWindowAnimations(R.style.windowStyle);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            dialogWindow.setGravity(Gravity.BOTTOM);
            lp.gravity = Gravity.BOTTOM;
            dialogWindow.setAttributes(lp);
            GridView gvGame = (GridView) selectGameDialog.findViewById(R.id.gvGame);
            SelectGameAdapter selectGameAdapter = new SelectGameAdapter(this, games);
            gvGame.setAdapter(selectGameAdapter);
            gvGame.setOnItemClickListener(this);
        }
        selectGameDialog.show();
    }

    private void loadGameItem() {
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MATCH_ITEM, null, HttpConstant.MATCH_ITEM);
    }

    private void saveUserGame() {
        User user = WangYuApplication.getUser(this);
        if (TextUtils.isEmpty(tvUserGameName.getText().toString())) {
            showToast("请选择游戏");
            return;
        }
        if (TextUtils.isEmpty(edtUserGameNickName.getText().toString())) {
            showToast("请输入游戏昵称");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId() + "");
        params.put("token", user.getToken());
        params.put("gameId", userGame.getGame_id() + "");
        if (llUserGameServe.getVisibility() == View.VISIBLE) {
            if (TextUtils.isEmpty(tvUserGameServe.getText().toString())) {
                showToast("请输入服务器名称");
                return;
            } else {
                params.put("gameServer", tvUserGameServe.getText().toString());
                String gameServe = tvUserGameServe.getText().toString();
                System.out.println(gameServe);
            }
        }
        if (userGame.getId() != 0) {
            params.put("userGameId", userGame.getId() + "");
        }
        params.put("gameNickname", edtUserGameNickName.getText().toString());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SAVE_USER_GAME, params, HttpConstant.SAVE_USER_GAME);

    }

    private void deleteUserGame() {
        User user = WangYuApplication.getUser(this);
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId() + "");
        params.put("token", user.getToken());
        params.put("userGameId", userGame.getId() + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DELETE_USER_GAME, params,HttpConstant.DELETE_USER_GAME);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        hideLoading();
        super.onSuccess(object,method);
        if (method.equals(HttpConstant.MATCH_ITEM)) {

            String obj = null;
            try {
                obj = object.getJSONArray("object").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            games = new Gson().fromJson(obj.toString(), new TypeToken<List<WarGame>>() {
            }.getType());
            initGameDialog();
        } else if (method.equals(HttpConstant.SAVE_USER_GAME)) {
            showToast("保存成功");
            Intent intent = new Intent();
            intent.setClass(this, PersonalHomePageActivity.class);
            setResult(RESULT_OK, intent);
            finish();
        } else if (method.equals(HttpConstant.DELETE_USER_GAME)) {
            showToast("删除成功");
            Intent intent = new Intent();
            intent.setClass(this, PersonalHomePageActivity.class);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onError(String method, String errorInfo) {
        super.onError(method, errorInfo);
        hideLoading();
        showToast(errorInfo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llUserGameName:
                if (games == null) {
                    loadGameItem();
                } else {
                    initGameDialog();
                }
                break;
            case R.id.btnUserGameSave:
                saveUserGame();
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.iv_title_right:
                deleteUserGame();
                break;
            case R.id.llUserGameServe:
                if (userGame.getGame_id() == 0) {
                    showToast("请先选择游戏");
                } else {
                    Intent intent = new Intent();
                    intent.setClass(this, GameServersActivity.class);
                    intent.putExtra("gameId", userGame.getGame_id() + "");
                    startActivityForResult(intent, GAME_SERVER_REQUEST);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == GAME_SERVER_REQUEST && intent != null) {
            String gameServer = intent.getStringExtra("gameServer");
            tvUserGameServe.setText(gameServer);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WarGame game = games.get(position);
        userGame.setGame_id(game.getItem_id());
        if (game.getServer_required() == 0) {
            llUserGameServe.setVisibility(View.GONE);
        } else if (game.getServer_required() == 1) {
            llUserGameServe.setVisibility(View.VISIBLE);
        }
        tvUserGameName.setText(game.getItem_name());
        selectGameDialog.dismiss();
    }
}
