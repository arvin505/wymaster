package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.CorpsMembersAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.Corps;
import com.miqtech.master.client.entity.TeammateInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.view.MyAlertView;
import com.miqtech.master.client.view.MyScanListview;
import com.miqtech.master.client.watcher.Observerable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 新的战队详情
 * Created by zhaosentao on 2016/5/6.
 */
public class CorpsDetailsV2Activity extends BaseActivity implements View.OnClickListener, CorpsMembersAdapter.OnItemRemoveMember {

    @Bind(R.id.tvCorpsDetailsV2DoCorps)
    TextView tvDoCorps;//战队详情底部按钮的处理
    @Bind(R.id.tvCorpsDetailsV2CorpsName)
    TextView tvCorpsName;//战队名称
    @Bind(R.id.llCorpsDetailsV2Match)
    LinearLayout llMatch;//赛事(包括:已报赛事字样)
    @Bind(R.id.rlCorpsDetailsV2Match)
    RelativeLayout rlMatch;//赛事(仅赛事)
    @Bind(R.id.ivCorpsDetailsV2MatchIcon)
    ImageView ivMatchIcon;//赛事icon
    @Bind(R.id.tvCorpsDetailsV2MatchName)
    TextView tvMatchName;//赛事名称
    @Bind(R.id.tvCorpsDetailsV2MatchNum)
    TextView tvMatchNum;//赛事场次
    @Bind(R.id.tvCorpsDetailsV2MatchTime)
    TextView tvMatchTime;//赛事时间
    @Bind(R.id.tvCorpsDetailsV2MatchAddress)
    TextView tvMatchAddress;//赛事地点
    @Bind(R.id.tvCorpsDetailsV2CheckTheList)
    TextView tvCheckList;//查看申请列表，只有队长才显示
    @Bind(R.id.lvCorpsDetailsV2CorpsMembers)
    MyScanListview lvCorpsMember;//战队成员
    @Bind(R.id.tvCorpsDetailsV2ApplyForReminding)
    TextView tvRemind;//申请人数提醒


    private final int JOIN = 1, OUT = 2, DISSOLUTION = 3, END = 4, NOtBEGIN = 5;
    private int doStatus = JOIN;
    private Context context;
    private LayoutInflater inflater;
    private List<TeammateInfo> corpsTeamers = new ArrayList<TeammateInfo>();
    private Corps corps;
    private CorpsMembersAdapter adapter;

    private int memberSize = 5;
    private User user;
    private int teamId;
    private int matchId;
    boolean isMonitor = false;//是否是队长
    private String MyCropInvideID;// 邀请ID
    private int showquit = 0;// 用来标记当前一个页面加入成功是按钮显示为退出战队，0表示默认不变，1表示成功
    private String matchTime;
    //private ApplyStateWatcher watcher = ApplyStateWatcher.getInstance()
    private Observerable observerable = Observerable.getInstance();
    private MyAlertView mDialog;
    private boolean isInCorps = false;//是否在队中
    private final int REQUEST_APPLY_IS_CHANGE = 1;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_corps_detail_v2);
        context = this;
        ButterKnife.bind(this);
        inflater = LayoutInflater.from(context);
        user = WangYuApplication.getUser(context);
        teamId = getIntent().getIntExtra("teamId", 0);
        matchId = getIntent().getIntExtra("matchId", 0);
        showquit = getIntent().getIntExtra("showquit", 0);
        MyCropInvideID = getIntent().getStringExtra("MyCropInvideID");
        matchTime = getIntent().getStringExtra("matchTime");
        initView();
        initData();
        if (teamId <= 0) {
            showToast("网络错误!");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onBackPressed();
                }
            }, 1000);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        setLeftIncludeTitle(getResources().getString(R.string.corpsDetailsV2TeamDetails));
        setRightTextView(getResources().getString(R.string.corpsDetailsV2CodeForInviting));
        setLeftBtnImage(R.drawable.back);
        tvDoCorps.setOnClickListener(this);
        tvCheckList.setOnClickListener(this);
        getLeftBtn().setOnClickListener(this);
        getRightTextview().setOnClickListener(this);
//        rlMatch.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        requsetTeamDetail();
    }

    /**
     * 请求战队详情
     */
    private void requsetTeamDetail() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("teamId", teamId + "");
        params.put("memberSize", memberSize + "");
        if (WangYuApplication.getUser(context) != null) {
            user = WangYuApplication.getUser(context);
            params.put("userId", user.getId());
            params.put("token", user.getToken());
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.CORPS_DETAIL, params, HttpConstant.CORPS_DETAIL);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ibLeft://返回
                onBackPressed();
                break;
            case R.id.rlCorpsDetailsV2Match://赛事跳转
                intent = new Intent();
                intent.setClass(context, OfficalEventActivity.class);
                intent.putExtra("matchId", corps.getActivity_id() + "");
                startActivity(intent);
                break;
            case R.id.tvRightHandle://二维码邀请
                intent = new Intent();
                intent.setClass(this, MatchCorpsInviteCodingActivity.class);
                intent.putExtra("corps", corps);
                startActivity(intent);
                break;
            case R.id.tvCorpsDetailsV2DoCorps://战队详情底部处理按钮
                if (user != null) {
                    switch (doStatus) {
                        case JOIN:
                            if (MyCropInvideID != null) {
                                postdata();
                            } else {
                                intent = new Intent(context, OfficePopupWindowActivity.class);
                                intent.putExtra("teamid", teamId + "");
                                intent.putExtra("teamname", corps.getTeam_name());
                                intent.putExtra("address", corps.getAddress());
                                intent.putExtra("date", DateUtil.strToDatePinYin(matchTime));
                                intent.putExtra("typeApply", 3);//0个人报名，1创建临时战队，2加入临时战队，3从战队详情页加入比赛
                                context.startActivity(intent);
                                overridePendingTransition(R.anim.in_from_bottom, R.anim.out_from_top);
                            }
                            break;
                        case OUT:
                            createMyAlertView(getResources().getString(R.string.show), getResources().getString(R.string.corpsDetailsV2IsQuitTheSuccess), 1);
                            break;
                        case DISSOLUTION:
                            createMyAlertView(getResources().getString(R.string.show), getResources().getString(R.string.corpsDetailsV2IsEndTheSuccess), 1);
                            break;
                    }
                } else {
                    intent = new Intent();
                    intent.setClass(context, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.tvCorpsDetailsV2CheckTheList://查看申请列表
                intent = new Intent();
                intent.setClass(this, MatchApplyListActivity.class);
                intent.putExtra("id", corps.getTeam_Id());
                startActivityForResult(intent, REQUEST_APPLY_IS_CHANGE);
                break;
        }
    }


    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        if (method.equals(HttpConstant.CORPS_DETAIL)) {//战队详情
            try {
                corps = new Gson().fromJson(object.getString("object").toString(), Corps.class);
                if (corps != null) {
                    showData();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (method.equals(HttpConstant.EXIT_CORPS)) {
            observerable.notifyChange(Observerable.ObserverableType.APPLYSTATE);
            finish();
        } else if (method.equals(HttpConstant.DO_TEAM_INVITE)) {// 请求成功后刷新界面
            MyCropInvideID = null;
            showSuccessToast();
            requsetTeamDetail();
        } else if (method.equals(HttpConstant.DEL_TEAMMATE)) {
            requsetTeamDetail();// 刷新
            showToast(getResources().getString(R.string.corpsDetailsV2DeleteTheSuccess));
        } else if (method.equals(HttpConstant.INVITE_TEAMMATE)) {
            showToast(getResources().getString(R.string.corpsDetailsV2InvateTheSuccess));
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        try {
            if (method.equals(HttpConstant.DO_TEAM_INVITE) && object.has("result")) {
                observerable.notifyChange(Observerable.ObserverableType.APPLYSTATE);
                MyCropInvideID = null;
                String errorInfo = object.getString("result");
                if ("战队人员已满".equals(errorInfo)) {
                    createMyAlertView(getResources().getString(R.string.corpsDetailsV2TeamFull), getResources().getString(R.string.corpsDetailsV2JoinFailMessage), 2);
                    MyCropInvideID = null;
                    tvDoCorps.setText(getResources().getString(R.string.corpsDetailsV2TeamFull));
                    tvDoCorps.setEnabled(false);
                    tvDoCorps.setTextColor(getResources().getColor(R.color.line));
                    tvDoCorps.setBackgroundColor(getResources().getColor(R.color.lv_item_content_text));
                    tvCheckList.setVisibility(View.GONE);
                    tvRemind.setVisibility(View.GONE);
                } else if ("该用户已添加".equals(errorInfo)) {
                    createMyAlertView(getResources().getString(R.string.corpsDetailsV2JoinFail), errorInfo, 2);
                    MyCropInvideID = null;
                    tvDoCorps.setText(getResources().getString(R.string.corpsDetailsV2QuitTheSuccess));
                    doStatus = OUT;
                    requsetTeamDetail();
                } else {
                    createMyAlertView(getResources().getString(R.string.corpsDetailsV2JoinFail), errorInfo, 2);
                    MyCropInvideID = null;
                    tvDoCorps.setText(getResources().getString(R.string.corpsDetailsV2JoinTheTeam));
                    doStatus = JOIN;
                    requsetTeamDetail();
                }
            }
        } catch (JSONException e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_APPLY_IS_CHANGE) {
            int isRefresh = data.getIntExtra("isRefresh", 0);
            if (isRefresh == 1) {
                requsetTeamDetail();
            }
        }

    }

    /**
     * 显示战队详情,显示顶部赛事
     */
    private void showData() {
        //显示战队
        corpsTeamers.clear();
        corpsTeamers.addAll(corps.getMembers());
        for (TeammateInfo teamer : corpsTeamers) {
            if (user != null && teamer.getId().equals(user.getId())) {
                doStatus = OUT;
                isInCorps = true;
            }
            if (Integer.parseInt(teamer.getIs_monitor()) > 0) {
                if (user != null && teamer.getId().equals(user.getId())) {
                    tvCheckList.setVisibility(View.VISIBLE);
                    tvRemind.setVisibility(View.VISIBLE);
                    doStatus = DISSOLUTION;
                    isMonitor = true;
                }
            }
        }
        if (isInCorps) {
            getRightTextview().setVisibility(View.VISIBLE);
        } else {
            getRightTextview().setVisibility(View.GONE);
        }

        if (isMonitor) {
            tvCheckList.setVisibility(View.VISIBLE);
            tvRemind.setVisibility(View.VISIBLE);
        } else {
            tvCheckList.setVisibility(View.GONE);
            tvRemind.setVisibility(View.GONE);
        }
        adapter = new CorpsMembersAdapter(context, corpsTeamers, isMonitor);
        lvCorpsMember.setAdapter(adapter);
        adapter.setOnItemRemoveMember(this);
        if (corps.getStatus() == 4) {
            doStatus = END;
        }
        if (corps.getStatus() == 3) {
            doStatus = NOtBEGIN;
        }
        if (1 == showquit) {
            doStatus = OUT;
            showquit = 0;
        }

        switch (doStatus) {
            case JOIN:
                if (MyCropInvideID != null) {
                    tvDoCorps.setText(getResources().getString(R.string.corpsDetailsV2AcceptTheInvitation));
                } else {
                    tvDoCorps.setText(getResources().getString(R.string.corpsDetailsV2JoinTheTeam));
                }
                break;
            case OUT:
                tvDoCorps.setText(getResources().getString(R.string.corpsDetailsV2QuitTheSuccess));
                break;
            case DISSOLUTION:
                tvDoCorps.setText(getResources().getString(R.string.corpsDetailsV2EndTheSuccess));
                break;
            case END:
                tvDoCorps.setText(getResources().getString(R.string.corpsDetailsV2EventEnding));
                tvDoCorps.setEnabled(false);
                tvDoCorps.setTextColor(getResources().getColor(R.color.line));
                tvDoCorps.setBackgroundColor(getResources().getColor(R.color.lv_item_content_text));
                tvCheckList.setVisibility(View.GONE);
                tvRemind.setVisibility(View.GONE);
                break;
            case NOtBEGIN:
                tvDoCorps.setText(getResources().getString(R.string.corpsDetailsV2ApplyEnding));
                tvDoCorps.setEnabled(false);
                tvDoCorps.setTextColor(getResources().getColor(R.color.line));
                tvDoCorps.setBackgroundColor(getResources().getColor(R.color.lv_item_content_text));
                tvCheckList.setVisibility(View.GONE);
                tvRemind.setVisibility(View.GONE);
                break;
        }
        if (corpsTeamers.size() == 5) {
            tvDoCorps.setText(getResources().getString(R.string.corpsDetailsV2TheNumberHasReachedThe));
            tvDoCorps.setEnabled(false);
            tvDoCorps.setTextColor(getResources().getColor(R.color.line));
            tvDoCorps.setBackgroundColor(getResources().getColor(R.color.lv_item_content_text));
            tvCheckList.setVisibility(View.GONE);
            tvRemind.setVisibility(View.GONE);
        }
        //显示战队名称
        tvCorpsName.setText(corps.getTeam_name());
        if (isMonitor && corps.getInviteNum() > 0) {
            tvRemind.setVisibility(View.VISIBLE);
            tvRemind.setText(corps.getInviteNum() + "");
        } else {
            tvRemind.setVisibility(View.GONE);
        }


        //显示赛事
        if (TextUtils.isEmpty(corps.getActivity_icon()) && TextUtils.isEmpty(corps.getTitle()) && TextUtils.isEmpty(corps.getNetbar_name())) {
            llMatch.setVisibility(View.GONE);
        } else {
            llMatch.setVisibility(View.VISIBLE);
            //显示赛事图片
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + corps.getActivity_icon(),
                    ivMatchIcon);
            //显示赛事名称
            if (!TextUtils.isEmpty(corps.getTitle())) {
                tvMatchName.setText(corps.getTitle());
            } else {
                tvMatchName.setText("");
            }
            //显示比赛场次
            tvMatchNum.setText(getResources().getString(R.string.corpsDetailsV2MatchNum, corps.getRound() + ""));
            //显示比赛时间
            tvMatchTime.setText(getResources().getString(R.string.corpsDetailsV2MatchTime, DateUtil.dateToStrLong(corps.getStart_time())));

            //显示比赛地点
            if (!TextUtils.isEmpty(corps.getNetbar_name())) {
                tvMatchAddress.setText(getResources().getString(R.string.corpsDetailsV2MatchAddress, corps.getNetbar_name()));
            } else {
                tvMatchAddress.setText(getResources().getString(R.string.corpsDetailsV2MatchAddress, R.string.corpsDetailsV2NoMatchaddress));
            }
        }

        if (corps.getState() == 1) {//已申请
            tvDoCorps.setText(getResources().getString(R.string.corpsDetailsV2EventHasApplied));
            tvDoCorps.setEnabled(false);
            tvDoCorps.setTextColor(getResources().getColor(R.color.line));
            tvDoCorps.setBackgroundColor(getResources().getColor(R.color.lv_item_content_text));
        }


    }

    public MyAlertView getIsDialog(Context context, String title, String message, final MyAlertView.DialogAction action) {
        return new MyAlertView.Builder(context).setTitle(title).setMessage(message).createIsDiaolg(action);
    }

    @Override
    public void OnItemRemoveMomber(final int memberId) {
        mDialog = getIsDialog(context, getResources().getString(R.string.corpsDetailsV2IsRemoveMember), "", new MyAlertView.DialogAction() {
            @Override
            public void doPositive() {
                deleteTeammate(memberId);
            }

            @Override
            public void doNegative() {

            }

            @Override
            public void doWeiXinShrae(int change) {

            }
        });
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    /**
     * 提交处理请求
     */
    private void postdata() {
        User user = WangYuApplication.getUser(context);
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("phone", user.getTelephone());
        params.put("id", MyCropInvideID);//邀请记录id
        params.put("type", 1 + "");//0拒绝1接受
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DO_TEAM_INVITE, params, HttpConstant.DO_TEAM_INVITE);
    }

    /**
     * 提交删除请求
     *
     * @param memberId 队员ID
     */
    private void deleteTeammate(int memberId) {
        User user = WangYuApplication.getUser(context);
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("memberId", memberId + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DEL_TEAMMATE, params, HttpConstant.DEL_TEAMMATE);
    }

    /**
     * 接受成功后弹窗
     */
    private void showSuccessToast() {
        View vv = LayoutInflater.from(context).inflate(R.layout.layout_yuezhan_dialog, null);
        Toast tt = new Toast(context);
        tt.setView(vv);
        tt.setDuration(Toast.LENGTH_LONG);
        tt.setGravity(Gravity.CENTER, 0, 0);
        tt.show();
    }

    /**
     * 1.创建退出或者解散战队的dialog
     * 2.用户处理战队的邀请
     *
     * @param title
     * @param message
     * @param isType  1、退出或者解散战队的dialog，2、用户处理战队的邀请
     */
    private void createMyAlertView(String title, String message, final int isType) {
        mDialog = getIsDialog(context, title, message, new MyAlertView.DialogAction() {
            @Override
            public void doPositive() {
                switch (isType) {
                    case 1://退出或者解散战队的dialog
                        exitOutTeam();
                        break;
                    case 2://用户处理战队的邀请
                        break;
                }
            }

            @Override
            public void doNegative() {

            }

            @Override
            public void doWeiXinShrae(int change) {

            }
        });
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    /**
     * 退出或解散战队
     */
    private void exitOutTeam() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("teamId", teamId + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.EXIT_CORPS, params, HttpConstant.EXIT_CORPS);
    }
}
