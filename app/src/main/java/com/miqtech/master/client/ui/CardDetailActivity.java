package com.miqtech.master.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.Card;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.watcher.Observerable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/3/28.
 */
public class CardDetailActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.tv_cardname)
    TextView tvCardName;
    @Bind(R.id.tv_card_usetime)
    TextView tvUseTime;
    @Bind(R.id.tv_card_time)
    TextView tvCardTime;
    @Bind(R.id.tv_card_netbar)
    TextView tvCardNetbar;
    @Bind(R.id.tv_use_now)
    TextView tvUseNow;
    @Bind(R.id.img_card_state)
    ImageView imgCardState;

    private Card mCard;

    private int type;

    private Dialog reportDialog;

    private Observerable mWatcher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carddetail);
        ButterKnife.bind(this);
        mCard = getIntent().getExtras().getParcelable("card");
        type = getIntent().getExtras().getInt("type");
        initView();
        mWatcher = Observerable.getInstance();
    }

    public void initView() {
        setLeftIncludeTitle("卡券详情");
        getLeftBtn().setVisibility(View.VISIBLE);
        getRightBtn().setImageResource(R.drawable.coin_tip);
        getRightBtn().setOnClickListener(this);
        getLeftBtn().setImageResource(R.drawable.back);
        getRightBtn().setVisibility(View.VISIBLE);
        getLeftBtn().setOnClickListener(this);

        setupData();
    }

    private void setupData() {
        if (mCard != null) {
            tvCardName.setText(mCard.getName());
            tvCardNetbar.setText("兑换网吧：      " + mCard.getNetbar_name());
            tvCardTime.setText("有效期：          " + TimeFormatUtil.formatNoSecond(mCard.getStart_date()) + " - " + TimeFormatUtil.formatNoSecond(mCard.getEnd_date()));
            if (mCard.getStatus() == 2) {
                imgCardState.setImageResource(R.drawable.icon_card_userd_full);
                tvUseNow.setText("已使用");
            } else {
                imgCardState.setImageResource(R.drawable.icon_card_invalid_full);
                tvUseNow.setText("已失效");
            }
            if (type == 1) {
                imgCardState.setVisibility(View.GONE);
                tvUseNow.setText("马上使用");
                tvUseNow.setOnClickListener(this);
            } else {
                imgCardState.setVisibility(View.VISIBLE);
                tvUseNow.setOnClickListener(null);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                finish();
                break;
            case R.id.ibRight:
                Intent intent = new Intent(this, SubjectActivity.class);
                intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.HOW2USEREDBAG);
                startActivity(intent);
                break;
            case R.id.tv_use_now:
                if (mCard.getType() == 1) {
                    showToast("请在网吧支付界面使用");
                } else {
                    showAlert();
                }
                break;

        }
    }

    /**
     * 使用兑换券提示
     */
    private void showAlert() {
        reportDialog = new Dialog(this);
        Window window = reportDialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setContentView(R.layout.layout_reserve_dialog);

        reportDialog.setCanceledOnTouchOutside(true);
        TextView tvDialogContent = (TextView) reportDialog.findViewById(R.id.tvDialogContent);
        TextView tvDialogSure = (TextView) reportDialog.findViewById(R.id.tvDialogSure);
        final TextView tvDialogCancel = (TextView) reportDialog.findViewById(R.id.tvDialogCancel);

        tvDialogContent.setText("您将使用\"" + mCard.getName() + "\",请确认您所使用的兑换券是在指定的网吧：\"" + mCard.getNetbar_name() + "\"，并联系该网吧工作人员兑换");
        tvDialogSure.setText("确认兑换");

        tvDialogSure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                User user = WangYuApplication.getUser(CardDetailActivity.this);
                if (user != null) {
                    userCard(user);
                    reportDialog.dismiss();
                } else {
                    showToast("请先登录");
                    Intent intent = new Intent();
                    intent.setClass(CardDetailActivity.this, LoginActivity.class);
                    //intent.putExtra(LoginActivity.LOGIN_TYPE, LoginActivity.LOGIN_FOR_PERSONALPAGE);
                    startActivityForResult(intent, 1);
                }
            }
        });
        tvDialogCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                reportDialog.dismiss();
            }
        });
        reportDialog.show();
    }

    /**
     * 使用卡券
     */
    private void userCard(User user) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("id", mCard.getId() + "");
        params.put("token", user.getToken());
        params.put("userId", user.getId());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.USE_CARD, params, HttpConstant.USE_CARD);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        mCard.setStatus(2);
        type = 0;
        setupData();
        mWatcher.notifyChange(Observerable.ObserverableType.CARDUSE);
        showToast("兑换成功，请联系网吧工作人员");
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
