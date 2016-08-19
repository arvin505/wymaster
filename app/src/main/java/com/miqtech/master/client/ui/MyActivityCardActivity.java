package com.miqtech.master.client.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.ActivityCard;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.GsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 我的参赛卡.
 */
public class MyActivityCardActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.ll_join_card)
    LinearLayout llJoinCard;
    @Bind(R.id.ll_nocard)
    LinearLayout llNoCard;
    @Bind(R.id.tv_cardname)
    TextView tvCardname;
    @Bind(R.id.tv_cardmobile)
    TextView tvCardmobile;
    @Bind(R.id.tv_cardqq)
    TextView tvCardqq;
    @Bind(R.id.tv_cardId)
    TextView tvCardId;
    @Bind(R.id.btn_addcard)
    Button btnAddcard;

    private Context context;

    private ActivityCard activityCard;

    private final static int REQUEST_CARD = 1;

    private boolean hasObject = false;
    private Dialog mDialog;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_myactivitycard);
        ButterKnife.bind(this);
        context = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("我的参赛卡");
        setRightTextView("了解参赛卡");
        getRightTextview().setOnClickListener(this);
        btnAddcard.setOnClickListener(this);
        llJoinCard.setOnClickListener(this);
        getLeftBtn().setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        loadMyActivityCard();
    }

    private void loadMyActivityCard() {
        showLoading();
        User user = WangYuApplication.getUser(context);
        if (user != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userId", user.getId());
            params.put("token", user.getToken());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ACTIVITY_CARD, params, HttpConstant.ACTIVITY_CARD);
        }
    }

    private void updateActivityCardView() {
        if (hasObject) {
            llNoCard.setVisibility(View.GONE);
            llJoinCard.setVisibility(View.VISIBLE);
        } else {
            llNoCard.setVisibility(View.VISIBLE);
            llJoinCard.setVisibility(View.GONE);
        }
        String IDcard = activityCard.getIdCard();
        String qq = activityCard.getQq();
        String realName = activityCard.getRealName();
        String telephone = activityCard.getTelephone();
        if (TextUtils.isEmpty(IDcard)) {
            tvCardId.setText("暂无身份证号码");
        } else {
            tvCardId.setText(IDcard);
        }
        if (TextUtils.isEmpty(qq)) {
            tvCardqq.setText(getResources().getString(R.string.card_qq, "暂无QQ号码"));
        } else {
            tvCardqq.setText(getResources().getString(R.string.card_qq, qq));
        }
        if (TextUtils.isEmpty(realName)) {
            tvCardname.setText(getResources().getString(R.string.card_name, "暂无真实姓名"));
        } else {
            tvCardname.setText(getResources().getString(R.string.card_name, realName));
        }
        if (TextUtils.isEmpty(telephone)) {
            tvCardmobile.setText(getResources().getString(R.string.card_mobile, "暂无手机号码"));
        } else {
            tvCardmobile.setText(getResources().getString(R.string.card_mobile, telephone));
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        if (HttpConstant.ACTIVITY_CARD.equals(method)) {
            if (object != null) {
                try {
                    if (!object.has("object")) {
                        hasObject = false;
                    } else {
                        hasObject = true;
                        activityCard = GsonUtil.getBean(object.getString("object"), ActivityCard.class);
                        updateActivityCardView();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_addcard:
                intent.setClass(context, EnterMatchCardActivity.class);
                startActivityForResult(intent, REQUEST_CARD);
                break;
            case R.id.ll_join_card:
                intent.setClass(context, EnterMatchCardActivity.class);
                intent.putExtra("activityCard", activityCard);
                startActivityForResult(intent, REQUEST_CARD);
                break;
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.tvRightHandle:
                intent.setClass(context,SubjectActivity.class);
                intent.putExtra(SubjectActivity.HTML5_TYPE,SubjectActivity.CARDEXPLAIN);
                startActivity(intent);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CARD && resultCode == RESULT_OK) {
            activityCard = (ActivityCard) data.getSerializableExtra("activityCard");
            if (activityCard != null) {
                hasObject = true;
                updateActivityCardView();
            } else {
                hasObject = false;
                llNoCard.setVisibility(View.VISIBLE);
                llJoinCard.setVisibility(View.GONE);
            }
        }
    }
}
