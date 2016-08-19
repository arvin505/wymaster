package com.miqtech.master.client.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.PreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/8.
 */
public class IntimacySettingActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rlAllowHomePage, rlAllowYueZhan, rlBlacklist;
    private ImageView ivAllowPage, ivAllowYueZhan;

    protected void init() {
        super.init();
        setContentView(R.layout.activity_intimacy);
        initView();
        initData();

    }

    protected void initData() {
        super.initData();
        User user = WangYuApplication.getUser(this);
        if (user != null) {
            int acceptAccess = user.getAcceptAccess();
            int acceptMatch = user.getAcceptMatch();
            if (acceptAccess == 1) {
                ivAllowPage.setImageDrawable(getResources().getDrawable(R.drawable.privacy_on_icon));
            } else {
                ivAllowPage.setImageDrawable(getResources().getDrawable(R.drawable.privacy_off_icon));
            }
            if (acceptMatch == 1) {
                ivAllowYueZhan.setImageDrawable(getResources().getDrawable(R.drawable.privacy_on_icon));
            } else {
                ivAllowYueZhan.setImageDrawable(getResources().getDrawable(R.drawable.privacy_off_icon));
            }
        }
    }

    protected void initView() {
        rlAllowHomePage = (RelativeLayout) findViewById(R.id.rlAllowHomePage);
        rlAllowYueZhan = (RelativeLayout) findViewById(R.id.rlAllowYueZhan);
        rlBlacklist = (RelativeLayout) findViewById(R.id.rlBlackList);
        ivAllowPage = (ImageView) findViewById(R.id.ivAllowPage);
        ivAllowYueZhan = (ImageView) findViewById(R.id.ivAllowYueZhan);
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        rlAllowHomePage.setOnClickListener(this);
        rlAllowYueZhan.setOnClickListener(this);
        rlBlacklist.setOnClickListener(this);
        setLeftIncludeTitle("隐私设置");
    }

    private void setAllowHomePage() {
        showLoading();
        User user = WangYuApplication.getUser(this);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        int type = user.getAcceptAccess();
        if (type == 1) {
            map.put("acceptAccess", 0 + "");
        } else if (type == 0) {
            map.put("acceptAccess", 1 + "");
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.EDITUSER, map, HttpConstant.EDITUSER);
    }

    private void setAllowYueZhan() {
        showLoading();
        User user = WangYuApplication.getUser(this);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        int type = user.getAcceptMatch();
        if (type == 1) {
            map.put("acceptMatch", 0 + "");
        } else if (type == 0) {
            map.put("acceptMatch", 1 + "");
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.EDITUSER, map, HttpConstant.EDITUSER);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        try {
            if (!TextUtils.isEmpty(object.toString())) {
                User user = WangYuApplication.getUser(this);
                String token = user.getToken();
                JSONObject jsonObj = new JSONObject(object.toString());
                String objStr = jsonObj.getString("object");
                User newUser = new Gson().fromJson(objStr, User.class);
                newUser.setToken(token);
                WangYuApplication.setUser(newUser);
                PreferencesUtil.setUser(this, new Gson().toJson(newUser));

                int acceptAccess = newUser.getAcceptAccess();
                int acceptMatch = newUser.getAcceptMatch();
                if (acceptAccess == 1) {
                    ivAllowPage.setImageDrawable(getResources().getDrawable(R.drawable.privacy_on_icon));
                } else {
                    ivAllowPage.setImageDrawable(getResources().getDrawable(R.drawable.privacy_off_icon));
                }
                if (acceptMatch == 1) {
                    ivAllowYueZhan.setImageDrawable(getResources().getDrawable(R.drawable.privacy_on_icon));
                } else {
                    ivAllowYueZhan.setImageDrawable(getResources().getDrawable(R.drawable.privacy_off_icon));
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        showToast(errMsg);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlAllowHomePage:
                setAllowHomePage();
                break;
            case R.id.rlAllowYueZhan:
                setAllowYueZhan();
                break;
            case R.id.rlBlackList:
                Intent intent = new Intent();
                intent.setClass(this, BlackListActivity.class);
                startActivity(intent);
                break;
            case R.id.ibLeft:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
