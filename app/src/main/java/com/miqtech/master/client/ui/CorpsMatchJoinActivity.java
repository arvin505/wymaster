package com.miqtech.master.client.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.MatchListAdapter;
import com.miqtech.master.client.adapter.SpinAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.Address;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.entity.WarGame;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.ABTextUtil;
import com.miqtech.master.client.utils.IDCardUtil;
import com.miqtech.master.client.utils.PreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ResourceAsColor")
public class CorpsMatchJoinActivity extends BaseActivity implements OnClickListener {
    private ListView lvMatch;
    private MatchListAdapter adapter;
    private List<Address> address = new ArrayList<Address>();
    private Context context;
    private int matchId;
    private String netbarId;
    private LayoutParams params;
    private Button btn_individual_join, btn_create_corps;

    private Address currentAddress;
    private Spinner spinner;
    private String[] netbarNames;
    private String[] netbarIds;
    private LayoutInflater inflater;
    private int current;
    private EditText mobileNum;
    private EditText idNum;
    private EditText qqNum;
    private EditText trueName;
    private EditText expertisePos;
    private String round;
    private Button submit;
    private ImageView ivBackUp;
    private EditText server;
    private EditText corpsName;
    private Intent intent;
    private User user;
    private int item_id;// 游戏id
    private List<WarGame> games = new ArrayList<WarGame>();
    private RelativeLayout server_rl;
    private Context mContext;

    public final static int REQUEST_CREAT_ZHANDUI_GAME = 1;

    public Address getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(Address currentAddress) {
        this.currentAddress = currentAddress;
    }

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.corps_match);
        context = this;
        inflater = LayoutInflater.from(context);
        netbarNames = getIntent().getStringArrayExtra("netbarName");
        netbarIds = getIntent().getStringArrayExtra("netbarId");
        matchId = getIntent().getIntExtra("matchId", 0);
        if (netbarIds.length > 0) {
            netbarId = netbarIds[0];
        }
        round = getIntent().getStringExtra("round");
        item_id = getIntent().getIntExtra("item_id", -1);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mContext = CorpsMatchJoinActivity.this;
        ivBackUp = (ImageView) findViewById(R.id.ivBack);
        ivBackUp.setImageResource(R.drawable.back);
        setLeftIncludeTitle("创建战队");
        spinner = (Spinner) findViewById(R.id.spinner2);
        submit = (Button) findViewById(R.id.btnSubmit);
        SpinAdapter sinAdapter = new SpinAdapter(context, R.layout.spinner_item, netbarNames);
        sinAdapter.setSpinner(spinner);
        sinAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(sinAdapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (netbarIds.length > 0 && position < netbarIds.length && position > 0) {
                    netbarId = netbarIds[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // spinner.setLayoutAnimation();
        submit.setOnClickListener(this);
        ivBackUp.setOnClickListener(this);

        server = (EditText) findViewById(R.id.server);
        corpsName = (EditText) findViewById(R.id.corpsName);
        mobileNum = (EditText) findViewById(R.id.mobileNumber);
        idNum = (EditText) findViewById(R.id.idNumber);
        qqNum = (EditText) findViewById(R.id.qqNumber);
        trueName = (EditText) findViewById(R.id.userName);
        expertisePos = (EditText) findViewById(R.id.expertisePosition);
        server_rl = (RelativeLayout) findViewById(R.id.server_rl);
        server_rl.setOnClickListener(this);
        server.setOnClickListener(this);

        mobileNum.setText(PreferencesUtil.getUserMobile(context));
        idNum.setText(PreferencesUtil.getUserIDCard(context));
        qqNum.setText(PreferencesUtil.getUserQQ(context));
        trueName.setText(PreferencesUtil.getUserRealName(context));
        expertisePos.setText(PreferencesUtil.getUserLabor(context));
    }

    @Override
    protected void initData() {
        super.initData();
        loadGameItem();
    }

    private void loadGameItem() {
        showLoading();
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MATCH_ITEM, null, HttpConstant.MATCH_ITEM);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    private void individualJoinMatch() {
        showLoading();
        Map<String,String > params = new HashMap<>();
        user = WangYuApplication.getUser(context);
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
        }
        params.put("activityId", matchId + "");
        params.put("netbarId", netbarId);
        params.put("name", trueName.getText().toString().trim() + "");
        params.put("telephone", mobileNum.getText().toString().trim() + "");
        params.put("idcard", idNum.getText().toString().trim() + "");
        params.put("qq", qqNum.getText().toString().trim() + "");
        params.put("labor", expertisePos.getText().toString().trim() + "");
        params.put("server", server.getText().toString().trim() + "");
        params.put("teamName", corpsName.getText().toString().trim() + "");
        params.put("round", round + "");// 场次

        PreferencesUtil.saveUserIDCard(idNum.getText().toString(), context);
        PreferencesUtil.saveUserMobile(mobileNum.getText().toString(), context);
        PreferencesUtil.saveUserRealName(trueName.getText().toString(), context);
        PreferencesUtil.saveUserQQ(qqNum.getText().toString(), context);
        PreferencesUtil.saveUserLabor(expertisePos.getText().toString(), context);

        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.CORPS_ACTIVITY_DETAIL, params, HttpConstant.CORPS_ACTIVITY_DETAIL);
    }

    private boolean submitMatch() {
        if (validateInput(corpsName) && validateInput(server) && validateInput(trueName) && validateInput(idNum)
                && validateInput(mobileNum) && validateInput(qqNum) && validateInput(expertisePos)) {
            return true;
        }
        return false;
    }

    private boolean validateInput(EditText view) {
        boolean isValidate = false;
        switch (view.getId()) {
            case R.id.idNumber:
                isValidate = IDCardUtil.isIDCard(view.getText().toString().trim());
                if (!isValidate) {
                    showToast("身份证格式不正确");
                }
                break;
            case R.id.mobileNumber:
                isValidate = ABTextUtil.isMobile(view.getText().toString().trim());
                if (!isValidate) {
                    showToast("手机号格式不对");
                }
                break;
            case R.id.qqNumber:
                isValidate = !ABTextUtil.isEmpty(view.getText().toString().trim());
                if (!isValidate) {
                    showToast("QQ号不能为空");
                }
                break;
            case R.id.userName:
                isValidate = !ABTextUtil.isEmpty(view.getText().toString().trim());
                if (!isValidate) {
                    showToast("姓名不能为空");
                }
                break;
            case R.id.expertisePosition:
                isValidate = !ABTextUtil.isEmpty(view.getText().toString().trim());
                if (!isValidate) {
                    showToast("擅长位置不能为空");
                }
                break;
            case R.id.corpsName:
                isValidate = !ABTextUtil.isEmpty(view.getText().toString().trim());
                if (!isValidate) {
                    showToast("战队名不能为空");
                }
                break;
            case R.id.server:
                if (server_rl.getVisibility() == View.VISIBLE) {
                    isValidate = !ABTextUtil.isEmpty(view.getText().toString().trim());
                    if (!isValidate) {
                        showToast("服务器不能为空");
                    }
                } else {
                    isValidate = true;
                }
                break;
        }
        return isValidate;
    }

    @Override
    public void onSuccess(JSONObject object,String method){
        // TODO Auto-generated method stub
        super.onSuccess(object, method);
        hideLoading();
        Gson gs = new Gson();
        int teamId;
        JSONObject returnInfo;
        if (method.equals(HttpConstant.MATCH_ITEM)) {
            JSONArray obj = null;
            try {
                obj = object.getJSONArray("object");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            games = new Gson().fromJson(obj.toString(), new TypeToken<List<WarGame>>() {
            }.getType());
            for (WarGame wargam : games) {
                if (wargam.getItem_id() == item_id && wargam.getServer_required() == 1) {
                    server_rl.setVisibility(View.VISIBLE);
                }
            }
        }
        try {
            if (method.equals(HttpConstant.CORPS_ACTIVITY_DETAIL)) {
                returnInfo = object.getJSONObject("object");
                if (returnInfo.has("teamId")) {
                    BroadcastController.sendUserChangeBroadcase(context);
                    teamId = returnInfo.getInt("teamId");
                    intent = new Intent(context, CorpsDetailsV2Activity.class);
                    intent.putExtra("teamId", teamId);
                    intent.putExtra("matchId", matchId);
                    context.startActivity(intent);
                    //MainActivity.requestMsgCount();
                    finish();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String method, String errorInfo) {
        super.onError(method, errorInfo);
        hideLoading();
        if (method.equals(HttpConstant.CORPS_ACTIVITY_DETAIL)) {
            showToast(errorInfo);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.btnSubmit:
                if (submitMatch()) {
                    individualJoinMatch();
                }
                break;
            case R.id.server:// 选择服务器
                intent = new Intent();
                intent.setClass(context, GameServersActivity.class);
                intent.putExtra("gameId", item_id + "");
                startActivityForResult(intent, REQUEST_CREAT_ZHANDUI_GAME);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CREAT_ZHANDUI_GAME && resultCode == RESULT_OK) {
            String gameServer = data.getStringExtra("gameServer");
            server.setText(gameServer);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
