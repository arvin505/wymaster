package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.ActivityCard;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.EnterMatchCardActivity;
import com.miqtech.master.client.ui.OfficePopupWindowActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.IDCardUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/1/25.
 */
public class FragmentSelectJoinCard extends MyBaseFragment implements View.OnClickListener {

    @Bind(R.id.ll_join_card)
    LinearLayout llJoinCard;
    @Bind(R.id.ll_nocard)
    LinearLayout llNoCard;
    @Bind(R.id.tv_cardname)
    TextView tvCardName;
    @Bind(R.id.tv_cardmobile)
    TextView tvCardMobile;
    @Bind(R.id.tv_cardqq)
    TextView tvCardQQ;
    @Bind(R.id.tv_cardId)
    TextView tvCardId;
    @Bind(R.id.btn_addcard)
    Button btnAddcard;
    @Bind(R.id.tv_modify_card)
    TextView tvModifyCard;
    @Bind(R.id.rl_modify_card)
    RelativeLayout rlModifyCard;

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


    private ActivityCard mCard;
    private Resources mResouces;
    private Context context;
    private final static int REQUEST_CARD = 1;

    private int registrationTypes = -1;//0个人报名，1创建临时战队，2加入临时战队

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_joincard, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mResouces = getResources();
        context = getActivity();
        initView();
        showTheTopOfTheStatusBar();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            loadMyCard();
        }
    }

    private void initView() {
        btnAddcard.setOnClickListener(this);
        rlModifyCard.setOnClickListener(this);
        llBack.setOnClickListener(this);
        tvOK.setOnClickListener(this);
    }

    private void initCardView(ActivityCard card) {
        tvCardId.setText(IDCardUtil.formatIdCard(card.getIdCard()));
        tvCardMobile.setText(mResouces.getString(R.string.card_mobile, card.getTelephone()));
        tvCardName.setText(mResouces.getString(R.string.card_name, card.getRealName()));
        tvCardQQ.setText(mResouces.getString(R.string.card_qq, card.getQq()));
    }

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
        hideLoading();
        try {
            if (method.equals(HttpConstant.ACTIVITY_MY_CARD)) {
                if (object.has("object")) {   //有参赛卡
                    llJoinCard.setVisibility(View.VISIBLE);
                    llNoCard.setVisibility(View.GONE);
                    tvOK.setTextColor(context.getResources().getColor(R.color.orange));
                    tvOK.setEnabled(true);
                    mCard = GsonUtil.getBean(object.getJSONObject("object").toString(), ActivityCard.class);
                    initCardView(mCard);
                } else { //无参赛卡
                    llJoinCard.setVisibility(View.GONE);
                    llNoCard.setVisibility(View.VISIBLE);
                    tvOK.setTextColor(context.getResources().getColor(R.color.font_gray));
                    tvOK.setEnabled(false);
                    tvTitle.setText(context.getResources().getText(R.string.add_card));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_addcard:  //添加参赛卡
                intent = new Intent(context, EnterMatchCardActivity.class);
                getActivity().startActivityForResult(intent, REQUEST_CARD);
                break;
            case R.id.rl_modify_card:  //修改参赛卡
                intent = new Intent();
                intent.setClass(context, EnterMatchCardActivity.class);
                intent.putExtra("activityCard", mCard);
                getActivity().startActivityForResult(intent, REQUEST_CARD);
                break;
            case R.id.ll_back_popupwindow:  //返回上一页
                if (registrationTypes == 0) {
                    ((OfficePopupWindowActivity) context).setSelectFragment(0);
                } else if (registrationTypes == 1 || registrationTypes == 2) {
                    ((OfficePopupWindowActivity) context).setSelectFragment(1);
                } else if (registrationTypes == 3){
                    getActivity().finish();
                }
                break;
            case R.id.tv_ok_popupwindow:  //确认
                if (registrationTypes == 0) {
                    ((OfficePopupWindowActivity) context).setSelectFragment(2);
                } else if (registrationTypes == 1 || registrationTypes == 2) {
                    ((OfficePopupWindowActivity) context).setSelectFragment(3);
                } else  if (registrationTypes == 3){
                    ((OfficePopupWindowActivity) context).setSelectFragment(2);
                }
                break;
        }
    }

    @Override
    public void refreView() {
        loadMyCard();
    }

    /**
     * 显示顶部状态栏
     */
    private void showTheTopOfTheStatusBar() {
        registrationTypes = ((OfficePopupWindowActivity) getActivity()).getRegistrationTypes();

        ivBack.setVisibility(View.VISIBLE);
        tvOK.setVisibility(View.VISIBLE);
        tvOK.setText(context.getResources().getText(R.string.countersign));
        tvTitle.setText(context.getResources().getText(R.string.affirm_card));
        tvClose.setText(context.getResources().getText(R.string.back));

        if (registrationTypes == 0) {
            tvTotal.setText("/3");
            tvWhichOne.setText("2");
        } else if (registrationTypes == 1 || registrationTypes == 2) {
            tvTotal.setText("/4");
            tvWhichOne.setText("3");
        }else if (registrationTypes == 3){
            ivBack.setVisibility(View.GONE);
            tvClose.setText("关闭");
            tvTotal.setText("/2");
            tvWhichOne.setText("1");
        }
    }

}
