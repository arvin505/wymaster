package com.miqtech.master.client.ui;

import android.app.Dialog;
import android.content.Intent;

import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.MatchListAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.Address;
import com.miqtech.master.client.entity.InternetBarInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchAddressActivity extends BaseActivity implements OnClickListener {
    private ListView lvMatch;
    private MatchListAdapter adapter;
    private List<Address> address = new ArrayList<Address>();
    private MatchAddressActivity context;
    private int matchId;
    public Dialog joinDialog;
    private LayoutParams params;
    private Button btn_individual_join, btn_create_corps;
    private Intent intent;
    private String[] netbarNames;
    private String[] netbarIds;
    private String round;
    private Button individual_Btn;
    private Button corps_Btn;
    private int item_id;// 游戏id
    private ImageView back;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_matchplace);
        context = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();
        lvMatch = (ListView) findViewById(R.id.lvMatchApply);
        back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(this);
        //getLeftBtn().setOnClickListener(this);
        setLeftIncludeTitle("比赛地点");
        //setLeftBtnImage(R.drawable.back);
        joinDialog = new Dialog(context, R.style.searchStyle);
        joinDialog.setContentView(R.layout.join_dialog);
        individual_Btn = (Button) joinDialog.findViewById(R.id.individual_join);
        corps_Btn = (Button) joinDialog.findViewById(R.id.corps_join);
        Window dialogWindow = joinDialog.getWindow();
        dialogWindow.setWindowAnimations(R.style.windowStyle);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);


        btn_individual_join = (Button) joinDialog.findViewById(R.id.individual_join);
        btn_create_corps = (Button) joinDialog.findViewById(R.id.corps_join);
        btn_individual_join.setOnClickListener(this);
        btn_create_corps.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        item_id = getIntent().getIntExtra("item_id", -1);
        matchId = Integer.parseInt((getIntent().getStringExtra("matchId")));
        adapter = new MatchListAdapter(context, address, getIntent().getStringExtra("matchTitle"));
        lvMatch.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        loadAddress();
    }

    private void loadAddress() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(context);
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
        }
        params.put("id", matchId + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MATCH_PLACE, params, HttpConstant.MATCH_PLACE);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        // TODO Auto-generated method stub
        super.onSuccess(object, method);
        hideLoading();
        if (method.equals(HttpConstant.MATCH_PLACE)) {
            JSONArray obj = null;
            try {
                obj = object.getJSONArray("object");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<Address> newAddress = new Gson().fromJson(obj.toString(), new TypeToken<List<Address>>() {
            }.getType());
            address.clear();
            address.addAll(newAddress);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(String method, String errorInfo) {
        // TODO Auto-generated method stub
        super.onError(method, errorInfo);
        hideLoading();
        showToast(errorInfo);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.individual_join:
                intent = new Intent();
                intent.setClass(context, IndividualMatchJoinActivity.class);
                intent.putExtra("netbarName", netbarNames);
                intent.putExtra("netbarId", netbarIds);
                intent.putExtra("matchId", matchId);
                intent.putExtra("round", round);
                context.startActivity(intent);
                joinDialog.dismiss();
                break;
            case R.id.corps_join:
                intent = new Intent();
                intent.setClass(context, CorpsMatchJoinActivity.class);
                intent.putExtra("netbarName", netbarNames);
                intent.putExtra("netbarId", netbarIds);
                intent.putExtra("matchId", matchId);
                intent.putExtra("round", round);
                intent.putExtra("item_id", item_id);
                context.startActivity(intent);
                joinDialog.dismiss();
                break;
        }
    }

    private int state;

    public void joinMatch(int position, int state) {
        this.state = state;
        round = address.get(position).getRound();
        netbarNames = new String[address.get(position).getNetbars().size()];
        netbarIds = new String[address.get(position).getNetbars().size()];
        for (int i = 0; i < address.get(position).getNetbars().size(); i++) {
            InternetBarInfo netbar = address.get(position).getNetbars().get(i);
            netbarNames[i] = netbar.getNetbar_name();
            netbarIds[i] = netbar.getId();
        }
        // -1-未做用户授权;0-未报名;1-个人已报名;2-战队已报名;
        switch (state) {
            case -1:
                intent = new Intent();
                intent.setClass(context, LoginActivity.class);
                context.startActivity(intent);
                showToast("请登录");
                break;
            case 0:
                individual_Btn.setVisibility(View.VISIBLE);
                corps_Btn.setVisibility(View.VISIBLE);
                break;
            case 1:
                individual_Btn.setVisibility(View.GONE);
                corps_Btn.setVisibility(View.VISIBLE);
                break;
            case 2:
                showToast("您已加入战队");
                return;
        }
        joinDialog.show();
    }
}
