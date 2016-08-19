package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.ActivityCard;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.OfficePopupWindowActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.IDCardUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/1/27.
 */
public class FragmentConfirmInformation extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.ll_back_popupwindow)
    LinearLayout llBack;
    @Bind(R.id.tv_close_popupwindow)
    TextView tvClose;
    @Bind(R.id.iv_back_popupwindow)
    ImageView ivBack;
    @Bind(R.id.tv_title_popupwindow)
    TextView tvTitle;
    @Bind(R.id.tv_total)
    TextView tvTotal;
    @Bind(R.id.tv_select_which_one)
    TextView tvWhichOne;
    @Bind(R.id.tv_ok_popupwindow)
    TextView tvOK;

    @Bind(R.id.tv_cardname)
    TextView tvCardName;
    @Bind(R.id.tv_cardmobile)
    TextView tvCardMobile;
    @Bind(R.id.tv_cardqq)
    TextView tvCardQQ;
    @Bind(R.id.tv_cardId)
    TextView tvCardId;
    @Bind(R.id.tv_match_time)
    TextView tvMatchTime;
    @Bind(R.id.tv_match_address)
    TextView tvMatchAddress;
    @Bind(R.id.tv_corp_name)
    TextView tvCorpsName;
    @Bind(R.id.ll_corp_name)
    LinearLayout llCorpsName;

    private Context context;
    private String activityId;//活动ID
    private String netbarId;//网吧ID
    private String round;//场次ID
    private String teamName;//战队名
    private String teamId;//战队ID
    private String matchTime;
    private String matchAddress;
    private User user;
    private ActivityCard mCard;
    private Resources mResouces;
    private int registrationTypes = -1;//0个人报名，1创建临时战队，2加入临时战队

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirm_information, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mResouces = getResources();
        context = getActivity();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getData();
            showTheTopOfTheStatusBar();
            loadMyCard();
        }
    }

    private void loadMyCard() {
        showLoading();
        user = WangYuApplication.getUser(getContext());
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ACTIVITY_MY_CARD, params, HttpConstant.ACTIVITY_MY_CARD);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back_popupwindow://返回
                if (registrationTypes == 0) {
                    ((OfficePopupWindowActivity) context).setSelectFragment(1);
                } else if (registrationTypes == 1 || registrationTypes == 2) {
                    ((OfficePopupWindowActivity) context).setSelectFragment(2);
                } else if (registrationTypes == 3) {
                    ((OfficePopupWindowActivity) context).setSelectFragment(0);
                }
                break;
            case R.id.tv_ok_popupwindow://提交
                showLoading();
                if (registrationTypes == 0) {//个人报名
                    registrants();
                } else if (registrationTypes == 1) {//创建战队
                    createTeam();
                } else if (registrationTypes == 2 || registrationTypes == 3) {//加入战队
                    joinTeam();
                }
                break;
        }
    }


    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        try {
            if (method.equals(HttpConstant.ACTIVITY_MY_CARD)) {
                if (object.has("object")) {   //有参赛卡
                    mCard = GsonUtil.getBean(object.getJSONObject("object").toString(), ActivityCard.class);
                    initCardView(mCard);
                }
            } else if (method.equals(HttpConstant.CREATE_TEAM_V2)) {//战队创建
                showToast("报名成功");
                ((OfficePopupWindowActivity) context).onBackPressed();
            } else if (method.equals(HttpConstant.JOIN_TEAM_V2)) {
                showToast("报名成功");
                ((OfficePopupWindowActivity) context).onBackPressed();
            } else if (method.equals(HttpConstant.APPLY_V2)) {
                showToast("报名成功");
                ((OfficePopupWindowActivity) context).onBackPressed();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
        showToast(errMsg);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
    }

    /**
     * 创建战队
     */
    private void createTeam() {
        user = WangYuApplication.getUser(context);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("activityId", activityId);
            map.put("netbarId", netbarId);
            map.put("round", round);
            map.put("teamName", teamName);
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.CREATE_TEAM_V2, map, HttpConstant.CREATE_TEAM_V2);
        } else {
            toLogin();
        }
    }

    /**
     * 加入战队
     */
    private void joinTeam() {
        user = WangYuApplication.getUser(context);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("teamId", teamId);//战队id
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.JOIN_TEAM_V2, map, HttpConstant.JOIN_TEAM_V2);
        } else {
            toLogin();
        }
    }

    /**
     * 个人报名
     */
    private void registrants() {
        user = WangYuApplication.getUser(context);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("activityId", activityId);
            map.put("netbarId", netbarId);
            map.put("round", round);
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.APPLY_V2, map, HttpConstant.APPLY_V2);
        } else {
            toLogin();
        }
    }


    private void initCardView(ActivityCard card) {
        tvCardId.setText(IDCardUtil.formatIdCard(card.getIdCard()));
        tvCardMobile.setText(mResouces.getString(R.string.card_mobile, card.getTelephone()));
        tvCardName.setText(mResouces.getString(R.string.card_name, card.getRealName()));
        tvCardQQ.setText(mResouces.getString(R.string.card_qq, card.getQq()));
        tvMatchTime.setText(matchTime);
        tvMatchAddress.setText(matchAddress);
        if (registrationTypes == 1 || registrationTypes == 2) {
            llCorpsName.setVisibility(View.VISIBLE);
            tvCorpsName.setText(teamName);
        } else if (registrationTypes == 0) {
            llCorpsName.setVisibility(View.GONE);
        } else if (registrationTypes == 3) {
            llCorpsName.setVisibility(View.VISIBLE);
            tvCorpsName.setText(teamName);
        }

    }

    private void toLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * 得到赛事地点、时间等等数据
     */
    private void getData() {
        teamId = ((OfficePopupWindowActivity) context).getTeamID();
        teamName = ((OfficePopupWindowActivity) context).getTeamName();
        activityId = ((OfficePopupWindowActivity) context).getMatchId();
        netbarId = ((OfficePopupWindowActivity) context).getNetbarId() + "";
        round = ((OfficePopupWindowActivity) context).getRound();
        matchTime = ((OfficePopupWindowActivity) context).getMatchTime();
        matchAddress = ((OfficePopupWindowActivity) context).getMatchAddress();
        registrationTypes = ((OfficePopupWindowActivity) context).getRegistrationTypes();
    }

    /**
     * 显示顶部状态栏
     */
    private void showTheTopOfTheStatusBar() {
        ivBack.setVisibility(View.VISIBLE);
        tvOK.setVisibility(View.VISIBLE);
        tvOK.setText(context.getResources().getText(R.string.sign_up));
        tvTitle.setText(context.getResources().getText(R.string.affirm_information));
        tvClose.setText(context.getResources().getText(R.string.back));

        llBack.setOnClickListener(this);
        tvOK.setOnClickListener(this);

        if (registrationTypes == 0) {
            tvTotal.setText("/3");
            tvWhichOne.setText("3");
        } else if (registrationTypes == 1 || registrationTypes == 2) {
            llCorpsName.setVisibility(View.GONE);
            tvCardName.setText(teamName);
            tvTotal.setText("/4");
            tvWhichOne.setText("4");
        } else if (registrationTypes == 3) {
            tvTotal.setText("/2");
            tvWhichOne.setText("2");
        }
        if (getProgressDialog() != null) {
            getProgressDialog().setCanceledOnTouchOutside(false);
            getProgressDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
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
    }
}
