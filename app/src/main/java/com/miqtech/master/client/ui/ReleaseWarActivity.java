package com.miqtech.master.client.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.appmanager.AppManager;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.ContactMember;
import com.miqtech.master.client.entity.InternetBarInfo;
import com.miqtech.master.client.entity.KeepYuezhanInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.entity.UserGame;
import com.miqtech.master.client.entity.WarGame;
import com.miqtech.master.client.entity.YueZhan;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.view.SearchDialog;
import com.miqtech.master.client.view.WheelView;

import org.feezu.liuli.timeselector.TimeSelector;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReleaseWarActivity extends BaseActivity implements OnClickListener, TextWatcher {
    private TextView tvInviteMember, edtGameServer;
    private EditText edtWarTitle, edtIntro;
    private TextView tvGame, tvWarStatus, tvTime, tvNetBarName, tvContactWay, tvPeopleNum;
    private Button btnRelease;
    private ImageView back;

    private Context context;
    private List<WarGame> games;

    private AlertDialog matchItemDialog;
    private AlertDialog warStatusDialog;
    private AlertDialog peopleNumDialog;
    private Dialog searchDialog;
    private String otherUserId = "";

    private static final String[] WAR_STATUS = new String[]{"线上", "线下"};

    private static final String[] PEOPLE_NUM = new String[]{"2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
            "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
            "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48",
            "49", "50"};

    public static String initDateTime;
    private Animation shakingAnimation;

    private final static int CONTACT_REQUEST = 1;
    public final static int BAR_REQUEST = 2;
    public final static int CONTACT_WAY_REQUEST = 3;
    public final static int REQUEST_USER_YUEZHAN_GAME = 4;

    private ArrayList mSelectMembers = new ArrayList();
    private ArrayList<String> mContactWays = new ArrayList<String>();
    private UserGame userGame = new UserGame();

    private static InternetBarInfo netbar;
    private String gameId;
    private String gameServer;
    private KeepYuezhanInfo keepYuezhan = new KeepYuezhanInfo();
    private final String KEEP_YUEZHAN_INFO = "_KeepYuezhanInfo";

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.activity_releasewar);
        context = this;
        lengthCoding = UMengStatisticsUtil.CODE_6000;
        initView();
        initData();
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        netbar = (InternetBarInfo) getIntent().getSerializableExtra("netbar");
        otherUserId = getIntent().getStringExtra("id");
        if (netbar != null) {
            tvNetBarName.setText(netbar.getNetbar_name());
            tvWarStatus.setText(WAR_STATUS[1]);
            tvNetBarName.setOnClickListener(this);
            tvNetBarName.setTextColor(getResources().getColor(R.color.gray));
            tvNetBarName.setVisibility(View.VISIBLE);
        } else {
            tvNetBarName.setVisibility(View.GONE);
        }
        shakingAnimation = AnimationUtils.loadAnimation(this, R.anim.shaking);

        if (WangYuApplication.getUser(context) != null) {
            Gson gs = new Gson();
            String userId = WangYuApplication.getUser(context).getId();
            String Str = PreferencesUtil.getKeepYuezhan(context, userId + KEEP_YUEZHAN_INFO);
            if (Str != null) {
                keepYuezhan = gs.fromJson(Str, KeepYuezhanInfo.class);
                edtWarTitle.setText(keepYuezhan.getTitle());
                gameId = keepYuezhan.getGameId();
                tvGame.setText(keepYuezhan.getGameName());
                edtGameServer.setText(keepYuezhan.getGameService());
                edtIntro.setText(keepYuezhan.getContent());
                tvContactWay.setText(keepYuezhan.getContactWay());
                edtGameServer.setVisibility(keepYuezhan.getGameService().length() > 0 ? View.VISIBLE : View.GONE);
            }
        }
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();
        //setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("发起约战");
        edtWarTitle = (EditText) findViewById(R.id.edtWarTitle);
        edtGameServer = (TextView) findViewById(R.id.edtGameServer);
        tvContactWay = (TextView) findViewById(R.id.tvContactWay);
        edtIntro = (EditText) findViewById(R.id.edtIntro);
        tvInviteMember = (TextView) findViewById(R.id.tvInviteMember);
        tvGame = (TextView) findViewById(R.id.tvGame);
        tvWarStatus = (TextView) findViewById(R.id.tvWarStatus);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvNetBarName = (TextView) findViewById(R.id.tvNetBarName);
        tvPeopleNum = (TextView) findViewById(R.id.tvPeopleNum);
        btnRelease = (Button) findViewById(R.id.btnRelease);
        back = (ImageView) findViewById(R.id.ivBack);

        tvInviteMember.setOnClickListener(this);
        tvGame.setOnClickListener(this);
        tvWarStatus.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        tvNetBarName.setOnClickListener(null);
        btnRelease.setOnClickListener(this);
        //getLeftBtn().setOnClickListener(this);
        tvContactWay.setOnClickListener(this);
        tvPeopleNum.setOnClickListener(this);
        edtGameServer.setOnClickListener(this);
        back.setOnClickListener(this);
        // edtPeopleNum.addTextChangedListener(this);
        // 阻止EDITTEXT 弹出键盘
        tvGame.setFocusable(true);
        tvGame.setFocusableInTouchMode(true);
        tvGame.requestFocus(); // 初始让EditText焦点
        tvGame.requestFocusFromTouch();

        initDate();
        searchDialog = new SearchDialog(this, R.style.searchStyle, true, new SelectedNetbarListener());
    }

    private class SelectedNetbarListener implements com.miqtech.master.client.view.SearchDialog.SelectedNetbarListener {

        @Override
        public void selectedNetbar(InternetBarInfo netbar) {
            // TODO Auto-generated method stub
            ReleaseWarActivity.netbar = netbar;
            tvNetBarName.setText(ReleaseWarActivity.netbar.getNetbar_name());
        }
    }

    // 初始化时间
    private void initDate() {
        // Calendar calendar = Calendar.getInstance();
        // calendar.set(calendar.get(Calendar.YEAR),
        // (calendar.get(Calendar.MONTH) + 1),
        // calendar.get(Calendar.DAY_OF_MONTH),
        // calendar.get(Calendar.HOUR_OF_DAY),
        // (calendar.get(Calendar.MINUTE) + 30));
        // initDateTime = calendar.get(Calendar.YEAR) + "-" +
        // calendar.get(Calendar.MONTH) + "-"
        // + calendar.get(Calendar.DAY_OF_MONTH) + " " +
        // calendar.get(Calendar.HOUR_OF_DAY) + ":"
        // + calendar.get(Calendar.MINUTE);
        // tvTime.setText("请输入7日内有效时间");
        tvTime.setHint("请输入7日内有效时间");
    }

    private void loadMatchItem() {
        User user = WangYuApplication.getUser(context);
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MATCH_ITEM, params, HttpConstant.MATCH_ITEM);
    }

    private void showMatchItemDialog() {
        if (games == null) {
            loadMatchItem();
        } else {
            if (matchItemDialog == null) {
                View outerView = LayoutInflater.from(ReleaseWarActivity.this).inflate(R.layout.wheel_view, null);
                WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
                wv.setOffset(2);
                wv.setItems(initGameItems(games));
                wv.setSeletion(0);
                wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                    @Override
                    public void onSelected(int selectedIndex, String item) {
                    }
                });

                matchItemDialog = new AlertDialog.Builder(ReleaseWarActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle("竞技项目").setView(outerView).setPositiveButton("选择", new MatchItemOnClickListenser(wv))
                        .setNegativeButton("取消", new MatchItemOnClickListenser(wv)).show();
            }
            matchItemDialog.show();
        }
    }

    private class MatchItemOnClickListenser implements DialogInterface.OnClickListener {
        WheelView wv;

        MatchItemOnClickListenser(WheelView wv) {
            this.wv = wv;
        }

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            // TODO Auto-generated method stub
            if (arg1 == -1) {
                int index = wv.getSeletedIndex();
                gameId = games.get(index).getItem_id() + "";
                tvGame.setText(games.get(index).getItem_name());
                if (games.get(index).getServer_required() == 0) {
                    edtGameServer.setVisibility(View.GONE);
                } else {
                    edtGameServer.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    private void showWarStatusDialog() {
        if (warStatusDialog == null) {
            View outerView = LayoutInflater.from(ReleaseWarActivity.this).inflate(R.layout.wheel_view, null);
            WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
            wv.setOffset(2);
            wv.setItems(Arrays.asList(WAR_STATUS));
            if (netbar != null) {
                wv.setSeletion(1);
            } else {
                wv.setSeletion(0);
            }
            wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {
                }
            });

            warStatusDialog = new AlertDialog.Builder(ReleaseWarActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("游戏方式").setView(outerView).setPositiveButton("选择", new WarStatusOnClickListenser(wv))
                    .setNegativeButton("取消", new WarStatusOnClickListenser(wv)).show();
        }
        warStatusDialog.show();
    }

    private class WarStatusOnClickListenser implements DialogInterface.OnClickListener {
        WheelView wv;

        WarStatusOnClickListenser(WheelView wv) {
            this.wv = wv;
        }

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            // TODO Auto-generated method stub
            if (arg1 == -1) {
                int index = wv.getSeletedIndex();
                tvWarStatus.setText(WAR_STATUS[index]);
                if (index == 0) {
                    tvNetBarName.setOnClickListener(null);
                    tvNetBarName.setVisibility(View.GONE);
                    tvNetBarName.setTextColor(getResources().getColor(R.color.gary_bg));
                } else {
                    tvNetBarName.setVisibility(View.VISIBLE);
                    tvNetBarName.setOnClickListener(ReleaseWarActivity.this);
                    tvNetBarName.setTextColor(getResources().getColor(R.color.gray));
                }

            }
        }

    }

    private void showPeopleNumDialog() {
        if (peopleNumDialog == null) {
            View outerView = LayoutInflater.from(ReleaseWarActivity.this).inflate(R.layout.wheel_view, null);
            WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
            wv.setOffset(2);
            wv.setItems(Arrays.asList(PEOPLE_NUM));
            wv.setSeletion(0);
            wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {
                }
            });

            peopleNumDialog = new AlertDialog.Builder(ReleaseWarActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("约战人数").setView(outerView).setPositiveButton("选择", new PeopleNumOnClickListenser(wv))
                    .setNegativeButton("取消", new WarStatusOnClickListenser(wv)).show();
        }
        peopleNumDialog.show();
    }

    private class PeopleNumOnClickListenser implements DialogInterface.OnClickListener {
        WheelView wv;

        PeopleNumOnClickListenser(WheelView wv) {
            this.wv = wv;
        }

        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            // TODO Auto-generated method stub
            if (arg1 == -1) {
                int index = wv.getSeletedIndex();
                tvPeopleNum.setText(PEOPLE_NUM[index]);
            }
        }

    }

    private TimeSelector timeSelector;

    private void showDatePickerDialog() {
        /*DateTimePickDialog dateTimePicKDialog = new DateTimePickDialog(ReleaseWarActivity.this, initDateTime);
        dateTimePicKDialog.dateTimePicKDialog(tvTime);*/
        long current = System.currentTimeMillis();
        LogUtil.e("time", "time : " + DateUtil.long2Time(current) + "   after : " + DateUtil.long2Time(calculateTime(current)));
        timeSelector = new TimeSelector(this, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                tvTime.setText(time);
            }
        }, DateUtil.long2Time(current), DateUtil.long2Time(calculateTime(current)));
        timeSelector.show();

    }

    private long calculateTime(long time) {
        time += 1000 * 60 * 60 * 24 * 7;
        return time;
    }

    private List<String> initGameItems(List<WarGame> warGames) {
        List<String> gameItems = new ArrayList<String>();
        for (int i = 0; i < warGames.size(); i++) {
            gameItems.add(warGames.get(i).getItem_name());
        }
        return gameItems;
    }

    private void releaseMatch() {
        // 判断约战标题是否规范
        if (TextUtils.isEmpty(edtWarTitle.getText().toString())) {
            edtWarTitle.startAnimation(shakingAnimation);
            showToast("请输入正确格式的约战标题");
            return;
        }
        // 判断约战游戏是否规范
        if ("去选择".equals(tvGame.getText().toString())) {
            tvGame.startAnimation(shakingAnimation);
            showToast("请选择格式的游戏名称");
            return;
        }

        // 判断服务器是否规范
        if (edtGameServer.getVisibility() == View.VISIBLE && TextUtils.isEmpty(edtGameServer.getText().toString())) {
            edtGameServer.startAnimation(shakingAnimation);
            showToast("请输入正确格式的对战服务器");
            return;
        }
        if (TextUtils.isEmpty(tvTime.getText().toString())) {
            tvTime.startAnimation(shakingAnimation);
            showToast("请选择约战时间");
            return;
        }
        // 判断人数是否规范
        if (TextUtils.isEmpty(tvPeopleNum.getText().toString())) {
            tvPeopleNum.startAnimation(shakingAnimation);
            showToast("请输入选择约战人数");
            return;
        }
        // else if (Integer.parseInt(tvPeopleNum.getText().toString()) <= 1) {
        // tvPeopleNum.startAnimation(shakingAnimation);
        // showToast("请输入大于1的约战人数");
        // return;
        // } else if (Integer.parseInt(tvPeopleNum.getText().toString()) > 20) {
        // tvPeopleNum.startAnimation(shakingAnimation);
        // showToast("请输入小于21的约战人数");
        // return;
        // }
        // 判断联系方式
        if (TextUtils.isEmpty(tvContactWay.getText().toString())) {
            tvContactWay.startAnimation(shakingAnimation);
            showToast("请输入联系方式");
            return;
        }
        int way = 1;
        if (tvWarStatus.getText().toString().equals(WAR_STATUS[0])) {
            way = 1;
        } else if (tvWarStatus.getText().toString().equals(WAR_STATUS[1])) {
            way = 2;
        }
        // 约战网吧
        if (way == 2) {
            if (netbar == null) {
                tvNetBarName.startAnimation(shakingAnimation);
                showToast("请输入约战网吧");
                return;
            }
        }
        WarGame game = new WarGame();
        game.setItem_name("英雄联盟");
        game.setItem_id(1);
        if (games != null) {
            for (int i = 0; i < games.size(); i++) {
                if (tvGame.getText().toString().equals(games.get(i).getItem_name())) {
                    game = games.get(i);
                }
            }
        }
        // if (mSelectMembers.size() != 0) {
        // for (int i = 0; i < mSelectMembers.size(); i++) {
        // if (i != mSelectMembers.size() - 1) {
        // strMember += mSelectMembers.get(i).getContact_phone() + ",";
        // } else {
        // strMember += mSelectMembers.get(i).getContact_phone();
        // }
        // }
        // }

        String inviteIds = "";
        String invitedMan = "";
        for (int i = 0; i < mSelectMembers.size(); i++) {
            Object commonMember = mSelectMembers.get(i);
            if (commonMember instanceof User) {
                inviteIds += ((User) commonMember).getId() + ",";
            } else if (commonMember instanceof ContactMember) {
                invitedMan += ((ContactMember) commonMember).getContact_phone() + ",";
            }
        }
        if (invitedMan.length() > 0) {
            invitedMan = (String) invitedMan.subSequence(0, invitedMan.length() - 1);
        }
        if (inviteIds.length() > 0) {
            inviteIds = (String) inviteIds.subSequence(0, inviteIds.length() - 1);
        }
        User user = WangYuApplication.getUser(context);

        if (user == null) {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("title", edtWarTitle.getText().toString());
        params.put("itemId", game.getItem_id() + "");
        params.put("way", way + "");
        if (edtGameServer.getVisibility() == View.VISIBLE) {
            params.put("server", edtGameServer.getText().toString());
        }
        params.put("beginTime", tvTime.getText().toString());
        if (way == 2) {
            params.put("netbarId", netbar.getId());
            params.put("netbarName", netbar.getNetbar_name());
        }
        params.put("peopleNum", tvPeopleNum.getText().toString());
        params.put("contactWay", tvContactWay.getText().toString());
        params.put("intro", edtIntro.getText().toString());
        params.put("invitedMan", invitedMan);
        if (!TextUtils.isEmpty(otherUserId)) {
            if ((otherUserId + inviteIds).length() > 0) {
                params.put("inviteIds", otherUserId + inviteIds);
            }
        } else {
            if (inviteIds.length() > 0) {
                params.put("inviteIds", inviteIds);
            }
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.RELEASE_MATCH, params, HttpConstant.RELEASE_MATCH);
        showLoading();
    }

    private void updateContactWayView() {
        String temp = "";
        for (int i = 0; i < mContactWays.size(); i++) {
            if (i == mContactWays.size() - 1) {
                temp += mContactWays.get(i);
            } else {
                temp += mContactWays.get(i) + " ";
            }
        }
        tvContactWay.setText(temp);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        // TODO Auto-generated method stub
        super.onSuccess(object, method);
        if (method.equals(HttpConstant.MATCH_ITEM)) {
            String obj = null;
            try {
                obj = object.getJSONArray("object").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            games = new Gson().fromJson(obj.toString(), new TypeToken<List<WarGame>>() {
            }.getType());

            if (games.size() > 0) {
                gameId = games.get(0).getItem_id() + "";
            }
            showMatchItemDialog();
        } else if (method.equals(HttpConstant.RELEASE_MATCH)) {
            hideLoading();
            showToast("约战发布成功");
            try {
                String obj = object.getJSONObject("object").toString();
                YueZhan yueZhan = new Gson().fromJson(obj, YueZhan.class);
                Intent intent = new Intent();
                AppManager appManager = AppManager.getAppManager();
                boolean isExist = appManager.findActivity(PersonalHomePageActivity.class);
                if (isExist) {
                    appManager.finishActivity(ReleaseWar2TaActivity.class);
                }
                intent.setClass(this, YueZhanDetailsActivity.class);
                intent.putExtra("id", yueZhan.getId() + "");
                intent.putExtra("source", ReleaseWarActivity.class.getName());
                startActivity(intent);
                finish();
                BroadcastController.sendUserChangeBroadcase(context);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onError(String method, String errorInfo) {
        // TODO Auto-generated method stub
        super.onError(method, errorInfo);
        hideLoading();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tvInviteMember:
                if (mSelectMembers == null) {
                    mSelectMembers = new ArrayList<>();
                }
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("selectMembers", mSelectMembers);
                intent.putExtra("isYueZhan", true);
                intent.putExtra("maxInviteMemberSize", 50);
                intent.setClass(this, InviteFriendsActivity.class);
                startActivityForResult(intent, CONTACT_REQUEST);
                break;
            case R.id.tvGame:
                showMatchItemDialog();
                edtGameServer.setText("");
                break;
            case R.id.tvWarStatus:
                showWarStatusDialog();
                break;
            case R.id.tvTime:
                showDatePickerDialog();
                break;
            case R.id.btnRelease:
                releaseMatch();
                keepYuezhan.setTitle(edtWarTitle.getText().toString().trim());
                keepYuezhan.setGameId(gameId);
                keepYuezhan.setGameName(tvGame.getText().toString().trim());
                keepYuezhan.setGameService(edtGameServer.getText().toString().trim());
                keepYuezhan.setContactWay(tvContactWay.getText().toString().trim());
                if (!("".equals(edtIntro.getText().toString().trim()))) {
                    keepYuezhan.setContent(edtIntro.getText().toString().trim());
                } else {
                    keepYuezhan.setContent("");
                }
                Gson gs = new Gson();
                String userid = WangYuApplication.getUser(context).getId();
                String Str = gs.toJson(keepYuezhan);
                PreferencesUtil.setKeepYuezhan(context, userid + KEEP_YUEZHAN_INFO, Str);
                break;
            case R.id.tvNetBarName:
                searchDialog.show();
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvContactWay:
                intent = new Intent();
                intent.setClass(context, ContactWayActivity.class);
                intent.putStringArrayListExtra("contactWays", mContactWays);
                startActivityForResult(intent, CONTACT_WAY_REQUEST);
                break;
            case R.id.tvPeopleNum:
                showPeopleNumDialog();
                break;
            case R.id.edtGameServer:// 选择或者修改游戏及服务器
                // 判断约战游戏是否规范
                if ("去选择".equals(tvGame.getText().toString())) {
                    tvGame.startAnimation(shakingAnimation);
                    showToast("请选择格式的游戏名称");
                    return;
                }
                intent = new Intent();
                intent.setClass(context, GameServersActivity.class);
                intent.putExtra("gameId", gameId);
                startActivityForResult(intent, REQUEST_USER_YUEZHAN_GAME);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_REQUEST && resultCode == RESULT_OK) {
            ArrayList<ContactMember> selectMembers = data.getParcelableArrayListExtra("selectMembers");
            mSelectMembers.clear();
            mSelectMembers.addAll(selectMembers);
            tvInviteMember.setText(mSelectMembers.size() + "人");
        } else if (requestCode == BAR_REQUEST && resultCode == RESULT_OK) {
            netbar = (InternetBarInfo) data.getSerializableExtra("netbar");
            tvNetBarName.setText(netbar.getNetbar_name());
        } else if (requestCode == CONTACT_WAY_REQUEST && resultCode == RESULT_OK) {
            mContactWays.clear();
            ArrayList<String> contactWays = data.getStringArrayListExtra("contactWays");
            mContactWays.addAll(contactWays);
            updateContactWayView();
        } else if (requestCode == REQUEST_USER_YUEZHAN_GAME && resultCode == RESULT_OK) {
            gameServer = data.getStringExtra("gameServer");
            edtGameServer.setText(gameServer);
        }
    }

    @Override
    public void afterTextChanged(Editable arg0) {
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }
}
