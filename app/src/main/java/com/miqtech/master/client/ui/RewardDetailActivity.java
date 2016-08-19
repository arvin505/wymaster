package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.RewardDetail;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 悬赏令获奖详情
 * Created by zhaosentao on 2016/8/3.
 */
public class RewardDetailActivity extends BaseActivity {

    @Bind(R.id.reawrdDetailLlBack)
    LinearLayout llBack;
    @Bind(R.id.rewardDetailIvIcon)
    ImageView ivIcon;//顶部绿色的勾
    @Bind(R.id.rewardDetailTvTitle)
    TextView tvTitle;
    @Bind(R.id.rewardDetailTvRewardInfo)
    TextView tvRewardInfo;//奖品信息
    @Bind(R.id.rewardDetailLlMatchRanking)
    LinearLayout llMatchRanking;//排行榜
    @Bind(R.id.rewardDetailTvMatchRanking)
    TextView tvMatchRanking;
    @Bind(R.id.rewardDetailTvMatchReward)
    TextView tvMatchReward;//赛事奖励
    @Bind(R.id.rewardDetailTvTransactionNumber)
    TextView tvTransactionNumber;//交易号
    @Bind(R.id.rewardDetailTvBottomStatue)
    TextView tvBottomStatue;
    @Bind(R.id.ViewStub)
    ViewStub viewStub;
    @Bind(R.id.rewardDetailLine)
    View line;

    private View errorView;
    private TextView textView;//显示网络请求错误后的文字
    private Context context;
    private User user;
    private String bountyId;
    private RewardDetail rewardDetail;
    private String title;
    private final int REQUESTCODE = 100;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_reward_detail);
        ButterKnife.bind(this);
        context = this;
        bountyId = getIntent().getStringExtra("bountyId");
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        getRewardDetail();
    }

    @OnClick({R.id.reawrdDetailLlBack, R.id.rewardDetailTvBottomStatue})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reawrdDetailLlBack://返回
                onBackPressed();
                break;
            case R.id.rewardDetailTvBottomStatue:
                Intent intent = new Intent(context, AddressActivity.class);
                intent.putExtra("goodid", rewardDetail.getHistoryId() + "");
                intent.putExtra("goodType", TextUtils.isEmpty(rewardDetail.getCommodityType()) ? "" : rewardDetail.getCommodityType());
                startActivityForResult(intent, REQUESTCODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getRewardDetail();
    }

    /**
     * 获取得奖详情
     */
    private void getRewardDetail() {
        if (TextUtils.isEmpty(bountyId)) {
            showEmpty(1);
            return;
        }
        showLoading();
        user = WangYuApplication.getUser(context);
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("bountyId", bountyId);
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.BOUNTY_GRADEINFO, map, HttpConstant.BOUNTY_GRADEINFO);
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        try {
            if (method.equals(HttpConstant.BOUNTY_GRADEINFO)) {
                rewardDetail = new Gson().fromJson(object.getJSONObject("object").toString(), RewardDetail.class);
                if (errorView != null && errorView.getVisibility() == View.VISIBLE) {
                    errorView.setVisibility(View.GONE);
                }
                showData();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        if (method.equals(HttpConstant.BOUNTY_GRADEINFO)) {
            showEmpty(2);
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        if (method.equals(HttpConstant.BOUNTY_GRADEINFO)) {
            showEmpty(1);
        }
    }


    private void showData() {
        if (rewardDetail == null) {
            return;
        }
        ivIcon.setVisibility(View.GONE);
        tvBottomStatue.setVisibility(View.GONE);

        tvTitle.setTextColor(getResources().getColor(R.color.colorActionBarSelected));
        switch (rewardDetail.getStatus()) {//-1未填写信息 0-兑换中，1 发放成功，2-奖励信息过期
            case -1:
                title = getResources().getString(R.string.have_no_info);
                tvBottomStatue.setText(getResources().getString(R.string.file_in));
                tvBottomStatue.setVisibility(View.VISIBLE);
                break;
            case 0:
                title = getResources().getString(R.string.in_conversion);
                break;
            case 1:
                title = getResources().getString(R.string.issued);
                ivIcon.setVisibility(View.VISIBLE);
                tvTitle.setTextColor(getResources().getColor(R.color.shop_font_black));
                break;
            case 2:
                title = getResources().getString(R.string.expired);
                tvTitle.setTextColor(getResources().getColor(R.color.shop_font_black));
                break;
        }
        tvTitle.setText(title);


        //显示奖品信息
        if (!TextUtils.isEmpty(rewardDetail.getAwardName())) {
            tvRewardInfo.setText(rewardDetail.getAwardName());
        } else {
            tvRewardInfo.setText("");
        }

        //是否显示排名
        if (!TextUtils.isEmpty(rewardDetail.getRanking())) {
            llMatchRanking.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            tvMatchRanking.setText(rewardDetail.getRanking());
        } else {
            llMatchRanking.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }

        //赛事名称
        if (!TextUtils.isEmpty(rewardDetail.getMatchName())) {
            tvMatchReward.setText(rewardDetail.getMatchName());
        } else {
            tvMatchReward.setText("");
        }


        //交易号
        if (!TextUtils.isEmpty(rewardDetail.getTran_no())) {
            tvTransactionNumber.setText(rewardDetail.getTran_no());
        } else {
            tvTransactionNumber.setText("");
        }
    }

    /**
     * @param showErrorType 显示错误的显示方式，,1表示无获奖详情，2表示网络错误
     */
    private void showEmpty(int showErrorType) {
        if (errorView == null) {
            viewStub.setLayoutResource(R.layout.exception_page);
            errorView = viewStub.inflate();
            textView = (TextView) errorView.findViewById(R.id.tv_err_title);
            if (showErrorType == 1) {
                textView.setText(context.getResources().getString(R.string.no_reward_info));
            } else if (showErrorType == 2) {
                textView.setText(context.getResources().getString(R.string.error_network));
            }
        } else {
            if (showErrorType == 1) {
                textView.setText(context.getResources().getString(R.string.no_reward_info));
            } else if (showErrorType == 2) {
                textView.setText(context.getResources().getString(R.string.error_network));
            }
            errorView.setVisibility(View.VISIBLE);
        }
    }
}
