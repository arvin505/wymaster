package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.ActivityCard;
import com.miqtech.master.client.entity.ActivityInfo;
import com.miqtech.master.client.entity.ActivityQrcode;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.ApplyPopupWindowActivity;
import com.miqtech.master.client.ui.EnterMatchCardActivity;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.OfficePopupWindowActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.IDCardUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.watcher.Observerable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 扫码报名确认信息
 * Created by zhaosentao on 2016/5/11.
 */
public class FragmentApplyPopupConfirmInformation extends MyBaseFragment implements View.OnClickListener {
    @Bind(R.id.ll_back_popupwindow)
    LinearLayout llBack;//返回或者关闭
    @Bind(R.id.iv_back_popupwindow)
    ImageView ivBack;//返回或者关闭的图片（目前关闭时不需要图片）
    @Bind(R.id.tv_close_popupwindow)
    TextView tvBack;//显示返回或者关闭等字样
    @Bind(R.id.tv_title_popupwindow)
    TextView tvTopTitle;//顶部的标题
    @Bind(R.id.tv_select_which_one)
    TextView tvSelectWhichOne;//显示进到了哪一步
    @Bind(R.id.tv_total)
    TextView tvTotal;//总的步骤数
    @Bind(R.id.tv_ok_popupwindow)
    TextView tvOkOrNext;//确定或者下一步


    @Bind(R.id.tvFragmentInfoMatchCorpsName)
    TextView tvMatchCorpsName;//战队名字
    @Bind(R.id.tvFragmentInfoMatchTitle)
    TextView tvMatchTitle;//赛事名称
    @Bind(R.id.tvFragmentInfoMatchTime)
    TextView tvMatchTime;//赛事时间
    @Bind(R.id.tvFragmentInfoMatchAddress)
    TextView tvMatchAddress;//赛事地点
    @Bind(R.id.tvFragmentInfoNetBarName)
    TextView tvNetBarName;//赛事网吧名称

    @Bind(R.id.llFragmentInfoMatchCard)
    LinearLayout llMatchCard;//参赛卡
    @Bind(R.id.tvFragmentInfoChangeCard)
    TextView tvChangeCard;//修改参赛卡
    @Bind(R.id.tvFragmentInfoCardName)
    TextView tvCardName;//参赛卡名称
    @Bind(R.id.tvFragmentInfoCareMobile)
    TextView tvCardMobile;//参赛卡手机号
    @Bind(R.id.tvFragmentInfoCardQQ)
    TextView tvCardQQ;//参赛卡的QQ号
    @Bind(R.id.tvFragmentInfoCardID)
    TextView tvCardId;//参赛卡的身份证号码

    @Bind(R.id.llFragmentInfoAddMatchCard)
    LinearLayout llAddMatchCard;
    @Bind(R.id.btnFragmentInfoAddCard)
    Button btnAddCard;//添加参赛卡

   private static final String TAG="FragmentApply";
    public  static final int MATCH_LIST=1;
    public  static final int MATCH_DETAIL=2;
    private final int REQUEST_CARD = 1;
    private Context mContext;
    private Resources mResouces;
    private ActivityInfo activityInfo = new ActivityInfo();
    private ActivityCard activityCard = new ActivityCard();
    private ActivityQrcode activityQrcode = new ActivityQrcode();
    private User user;
    private int typeApply = -1;//0个人报名，1创建临时战队，2加入临时战队,3战队报名,4加入战队(确认参赛卡和确认参赛信息)。(固定不变的)


    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_apply_popup_confirm_information, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            showTheTopOfTheStatusBar();
//            showMatchInfo();
//            showMatchCard();
//        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.d(TAG,"onViewCreated");
        ButterKnife.bind(this, view);
        mContext = getActivity();
        mResouces = getResources();
        initView();
        showTheTopOfTheStatusBar();
        showMatchInfo();
        if(activityQrcode!=null && activityQrcode.getIsMatch()==1){
            loadMyCard();
        }else {
            showMatchCard();
        }
    }

    private void initView() {
        llBack.setOnClickListener(this);
        tvOkOrNext.setOnClickListener(this);
        btnAddCard.setOnClickListener(this);
        tvChangeCard.setOnClickListener(this);

        Bundle bundle = getArguments();
        activityQrcode = (ActivityQrcode) bundle.getSerializable("activityQrcode");
        activityInfo = activityQrcode.getActivityInfo();
        activityCard = activityQrcode.getCard();
        typeApply = activityQrcode.getTypeApply();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_back_popupwindow://关闭
                ((ApplyPopupWindowActivity) mContext).onBackPressed();
                break;
            case R.id.tv_ok_popupwindow://下一步
                if (typeApply == 0) {//个人报名
                    registrants();
                } else if (typeApply == 4) {//加入战队
                    joinTeam();
                } else {
                    ((ApplyPopupWindowActivity) mContext).setSelectFragment(1);
                }
                break;
            case R.id.btnFragmentInfoAddCard://添加参赛卡
                intent = new Intent(mContext, EnterMatchCardActivity.class);
                getActivity().startActivityForResult(intent, REQUEST_CARD);
                break;
            case R.id.tvFragmentInfoChangeCard:  //修改参赛卡
                intent = new Intent();
                intent.setClass(mContext, EnterMatchCardActivity.class);
                intent.putExtra("activityCard", activityCard);
                getActivity().startActivityForResult(intent, REQUEST_CARD);
                break;
        }
    }

    @Override
    public void refreView() {
        LogUtil.d(TAG,"refreView");
        loadMyCard();
    }

    /**
     * 请求参赛卡
     */
    private void loadMyCard() {
        showLoading();
        User user = WangYuApplication.getUser(getContext());
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ACTIVITY_MY_CARD, params, HttpConstant.ACTIVITY_MY_CARD);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        LogUtil.d(TAG,"onSuccess"+object.toString());
        hideLoading();
        try {
            if (method.equals(HttpConstant.ACTIVITY_MY_CARD)) {
                if (object.has("object")) {
                    activityCard = new Gson().fromJson(object.getString("object").toString(), ActivityCard.class);
                } else {
                    activityCard = null;
                }
                showMatchCard();
            } else if (method.equals(HttpConstant.APPLY_V2)) {
                ((ApplyPopupWindowActivity) mContext).setSuccess(true);
                ((ApplyPopupWindowActivity) mContext).setSelectFragment(1);
            } else if(method.equals(HttpConstant.EVENT_APPLY)){
                if(activityQrcode.getIsMatch()==1){
                    Observerable.getInstance().notifyChange(Observerable.ObserverableType.ZIFAMATCH,null,MATCH_DETAIL);
                }
                ((ApplyPopupWindowActivity) mContext).setSuccess(true);
                ((ApplyPopupWindowActivity) mContext).setSelectFragment(1);
            }else if (method.equals(HttpConstant.JOIN_TEAM_V2)) {
                ((ApplyPopupWindowActivity) mContext).setSuccess(true);
                ((ApplyPopupWindowActivity) mContext).setSelectFragment(1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        LogUtil.d(TAG,"onError"+errMsg+":::");
        showToast(mResouces.getString(R.string.error_network));
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        LogUtil.d(TAG,"onFaild"+object.toString());
        if (object.has("code")) {
            try {
                if (-1 == object.getInt("code")) {
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (method.equals(HttpConstant.APPLY_V2)) {
            ((ApplyPopupWindowActivity) mContext).setSuccess(false);
            ((ApplyPopupWindowActivity) mContext).setSelectFragment(1);
        } else if (method.equals(HttpConstant.JOIN_TEAM_V2)) {
            ((ApplyPopupWindowActivity) mContext).setSuccess(false);
            ((ApplyPopupWindowActivity) mContext).setSelectFragment(1);
        }else if(method.equals(HttpConstant.EVENT_APPLY)){
            ((ApplyPopupWindowActivity) mContext).setSuccess(false);
            ((ApplyPopupWindowActivity) mContext).setSelectFragment(1);
        }
    }

    /**
     * 显示赛事信息
     */
    private void showMatchInfo() {
        if (activityInfo != null) {

            //当扫码的是战队二维码  则显示战队名称
            if (activityQrcode.getTypeApply() == 4) {
//                tvMatchCorpsName.setTextSize(getResources().getDimension(R.dimen.text_size_14));
                tvMatchCorpsName.setTextSize(14);
                tvMatchCorpsName.setTextColor(getResources().getColor(R.color.lv_item_content_text));
                tvMatchCorpsName.setText(getResources().getString(R.string.corpsDetailsV2MatchCorpsName, activityInfo.getTeam_name()));

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvMatchTitle.getLayoutParams();
                params.setMargins(0,0,0,0);
                tvMatchTitle.setLayoutParams(params);
            }

            //显示赛事名称
            if (!TextUtils.isEmpty(activityInfo.getTitle())) {
                tvMatchTitle.setText(mResouces.getString(R.string.fragmentMatchTitle, activityInfo.getTitle()));
            } else {
                tvMatchTitle.setText(mResouces.getString(R.string.fragmentMatchTitle, ""));
            }

            //显示赛事时间
            if (!TextUtils.isEmpty(activityInfo.getOver_time())) {
                tvMatchTime.setText(mResouces.getString(R.string.fragmentMatchTime, activityInfo.getOver_time()));
            } else {
                tvMatchTime.setText(mResouces.getString(R.string.fragmentMatchTime, ""));
            }

            //显示赛事地点
            if (!TextUtils.isEmpty(activityInfo.getArea())) {
                tvMatchAddress.setText(mResouces.getString(R.string.fragmentMatchAddress, activityInfo.getArea()));
            } else {
                tvMatchAddress.setText(mResouces.getString(R.string.fragmentMatchAddress, ""));
            }

            //显示赛事网吧名称
            if (!TextUtils.isEmpty(activityInfo.getName())) {
                tvNetBarName.setText(activityInfo.getName());
            } else {
                tvNetBarName.setText("");
            }
        }
    }

    /**
     * 个人报名
     */
    private void registrants() {
        user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            if(activityQrcode.getIsMatch()==1){
                map.put("roundId",activityQrcode.getRound());
                sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.EVENT_APPLY, map, HttpConstant.EVENT_APPLY);
            }else{
                map.put("activityId", activityQrcode.getActivityInfo().getId() + "");
                map.put("netbarId", activityQrcode.getNetbarId());
                map.put("round", activityQrcode.getRound());
                sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.APPLY_V2, map, HttpConstant.APPLY_V2);
            }
        } else {
            toLogin();
        }
    }

    /**
     * 加入战队
     */
    private void joinTeam() {
        user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("teamId", activityInfo.getTeam_id() + "");//战队id
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.JOIN_TEAM_V2, map, HttpConstant.JOIN_TEAM_V2);
        } else {
            toLogin();
        }
    }

    private void toLogin() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);
    }

    /**
     * 显示参赛卡数据
     */
    private void showMatchCard() {
        if (activityInfo != null && activityCard != null) {
            llAddMatchCard.setVisibility(View.GONE);
            llMatchCard.setVisibility(View.VISIBLE);
            tvCardId.setText(IDCardUtil.formatIdCard(activityCard.getIdCard()));
            tvCardMobile.setText(mResouces.getString(R.string.card_mobile, activityCard.getTelephone()));
            tvCardName.setText(mResouces.getString(R.string.card_name, activityCard.getRealName()));
            tvCardQQ.setText(mResouces.getString(R.string.card_qq, activityCard.getQq()));
        } else {
            llMatchCard.setVisibility(View.GONE);
            llAddMatchCard.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示顶部的状态
     */
    private void showTheTopOfTheStatusBar() {
        tvBack.setText(mResouces.getString(R.string.FragmentInfoClose));
        tvSelectWhichOne.setText("1");
        if (typeApply == 4) {//加入战队
            tvTopTitle.setText(mResouces.getString(R.string.FragmentInfoCorpsInfo));
            tvOkOrNext.setText(mResouces.getString(R.string.FragmentInfojoin));
            tvTotal.setText("/2");
        } else if (typeApply == 0) {//个人报名
            tvTopTitle.setText(mResouces.getString(R.string.FragmentInfoActivityInfo));
            tvOkOrNext.setText(mResouces.getString(R.string.sign_up));
            tvTotal.setText("/2");
        } else {//加入战队或者创建战队
            tvTopTitle.setText(mResouces.getString(R.string.FragmentInfoActivityInfo));
            tvOkOrNext.setText(mResouces.getString(R.string.sign_up));
            tvTotal.setText("/4");
        }
    }
}
