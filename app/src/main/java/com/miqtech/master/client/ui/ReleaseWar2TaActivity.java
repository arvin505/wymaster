package com.miqtech.master.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.TowardTAYueZhanAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.appmanager.AppManager;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.entity.YueZhan;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

public class ReleaseWar2TaActivity extends BaseActivity implements OnItemClickListener, OnClickListener {
    private ListView lvReleaseWar;
    private TextView tvReleaseWar2Ta;
    private User otherUser;
    private User user;
    private List<YueZhan> wars = new ArrayList<YueZhan>();
    private TowardTAYueZhanAdapter adapter;
    private Context context;
    private AlertDialog inviteDialog;
    private ImageView back;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_releasewar2ta);
        user = WangYuApplication.getUser(this);
        context = this;
        initView();
        initData();
    }

    @Override
    protected void initData() {
        super.initData();
        otherUser = (User) getIntent().getSerializableExtra("user");
        loadUserWars();
    }

    @Override
    protected void initView() {
        super.initView();
        lvReleaseWar = (ListView) findViewById(R.id.lvReleaseWar);
        tvReleaseWar2Ta = (TextView) findViewById(R.id.tvReleaseWar2Ta);
        //setLeftBtnImage(R.drawable.back);
        back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(this);
        setLeftIncludeTitle("向TA约战");
        //getLeftBtn().setOnClickListener(this);
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        tvReleaseWar2Ta.measure(w, h);
        int height = tvReleaseWar2Ta.getMeasuredHeight();
        int width = tvReleaseWar2Ta.getMeasuredWidth();
        adapter = new TowardTAYueZhanAdapter(context, wars, height);
        lvReleaseWar.setAdapter(adapter);
        lvReleaseWar.setOnItemClickListener(this);
        tvReleaseWar2Ta.setOnClickListener(this);
    }

    private void loadUserWars() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(this);
        params.put("userId", user.getId());
        params.put("token", user.getToken());

        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.USER_RELEASE_WAR, params, HttpConstant.USER_RELEASE_WAR);
    }

    private void inviteUser(int id) {
        showLoading();
        User user = WangYuApplication.getUser(this);
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("id", id + "");
        params.put("invocationIds", otherUser.getId());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.WAR_INVITE_USER, params, HttpConstant.WAR_INVITE_USER);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        hideLoading();
        super.onSuccess(object,method);
        try {

            if (HttpConstant.USER_RELEASE_WAR.equals(method)) {
                String obj = object.getJSONArray("object").toString();
                List<YueZhan> newWars = new Gson().fromJson(obj.toString(), new TypeToken<List<YueZhan>>() {
                }.getType());
                wars.addAll(newWars);
                adapter.notifyDataSetChanged();
            } else if (HttpConstant.WAR_INVITE_USER.equals(method)) {
                showToast("邀请约战成功");
                inviteDialog.dismiss();
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(String method, String errorInfo) {
        hideLoading();
        showToast(errorInfo);
        if (inviteDialog!=null&& inviteDialog.isShowing()){
            inviteDialog.dismiss();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final YueZhan yueZhan = wars.get(position);
        if (yueZhan != null) {
            inviteDialog = new AlertDialog.Builder(this).create();
            inviteDialog.show();
            Window attWindow = inviteDialog.getWindow();
            attWindow.setContentView(R.layout.layout_reserve_dialog);

            inviteDialog.setCanceledOnTouchOutside(true);
            TextView tvDialogContent = (TextView) inviteDialog.findViewById(R.id.tvDialogContent);
            TextView tvDialogSure = (TextView) inviteDialog.findViewById(R.id.tvDialogSure);
            TextView tvDialogCancel = (TextView) inviteDialog.findViewById(R.id.tvDialogCancel);
            tvDialogContent.setText("确认邀请他进入这场约战吗？");
            tvDialogSure.setText("确定");
            tvDialogCancel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    inviteDialog.dismiss();
                }
            });
            tvDialogSure.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    inviteUser(yueZhan.getId());
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvReleaseWar2Ta:
                if (otherUser != null) {
                    Intent intent = new Intent();
                    intent.setClass(this, ReleaseWarActivity.class);
                    AppManager.getAppManager().addActivity(this);
                    intent.putExtra("id", otherUser.getId());
                    startActivity(intent);
                }
                break;
            case R.id.ivBack:
                onBackPressed();
                break;

            default:
                break;
        }
    }
}
