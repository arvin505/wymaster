package com.miqtech.master.client.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.MessageCount;
import com.miqtech.master.client.entity.MyStatistics;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.AttentionListActivity;
import com.miqtech.master.client.ui.EditDataActivity;
import com.miqtech.master.client.ui.FansListActivity;
import com.miqtech.master.client.ui.GameCenterActivity;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.MainActivity;
import com.miqtech.master.client.ui.MyActivityCardActivity;
import com.miqtech.master.client.ui.MyCollectActivity;
import com.miqtech.master.client.ui.MyCorpsActivity;
import com.miqtech.master.client.ui.MyMatchActivity;
import com.miqtech.master.client.ui.MyMessageActivity;
import com.miqtech.master.client.ui.MyOrderActivity;
import com.miqtech.master.client.ui.MyRedBagActivity;
import com.miqtech.master.client.ui.MyRewardActivity;
import com.miqtech.master.client.ui.SettingActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CircleImageView;
import com.miqtech.master.client.watcher.Observerable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiaoyi on 2016/7/23.
 * 我的
 */
public class FragmentMyMain extends BaseFragment {
    @Bind(R.id.tvRightHandle)
    TextView tvRightHandle;
    @Bind(R.id.ibRight)
    ImageButton ibRight;
    @Bind(R.id.ibRight1)
    ImageButton ibRight1;
    @Bind(R.id.praise_iv_sub)
    ImageView praiseIvSub;
    @Bind(R.id.praise_number_tv_sub)
    TextView praiseNumberTvSub;
    @Bind(R.id.praise_ll_sub)
    LinearLayout praiseLlSub;
    @Bind(R.id.collect_iv_sub)
    ImageView collectIvSub;
    @Bind(R.id.collect_number_tv_sub)
    TextView collectNumberTvSub;
    @Bind(R.id.collect_ll_sub)
    LinearLayout collectLlSub;
    @Bind(R.id.info_praise_collect_ll)
    LinearLayout infoPraiseCollectLl;
    @Bind(R.id.buttom_line)
    View buttomLine;
    @Bind(R.id.rl_toolbar)
    RelativeLayout rlToolbar;
    @Bind(R.id.img_my_header)
    CircleImageView imgMyHeader;
    @Bind(R.id.tv_my_activities)
    TextView tvMyActivities;
    @Bind(R.id.tv_my_attentions)
    TextView tvMyAttentions;
    @Bind(R.id.tv_my_fans)
    TextView tvMyFans;
    @Bind(R.id.btn_my_edit)
    Button btnMyEdit;
    @Bind(R.id.tv_my_order_count)
    TextView tvMyOrderCount;
    @Bind(R.id.rl_my_order)
    RelativeLayout rlMyOrder;
    @Bind(R.id.rl_my_wallet)
    RelativeLayout rlMyWallet;
    @Bind(R.id.rl_my_match_card)
    RelativeLayout rlMyMatchCard;
    @Bind(R.id.rl_my_matchs)
    RelativeLayout rlMyMatchs;
    @Bind(R.id.rl_my_team)
    RelativeLayout rlMyTeam;
    @Bind(R.id.rl_my_reward)
    RelativeLayout rlMyReward;
    @Bind(R.id.rl_my_collect)
    RelativeLayout rlMyCollect;
    @Bind(R.id.rl_my_gamecenter)
    RelativeLayout rlMyGamecenter;
    @Bind(R.id.tvLeftTitle)
    TextView tvLeftTitle;
    @Bind(R.id.ibLeft)
    ImageButton ibLeft;
    @Bind(R.id.img_bar_right)
    ImageView imgMessage;
    @Bind(R.id.tv_bar_message_count)
    TextView tvMessageCount;
    private MyStatistics mData;
    private boolean isFirst = true;
    private MessageCount messageCount;
    private boolean shouldUpdate = false;
    private static FragmentMyMain mInstance;


    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_main, container, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    private void initView() {
        imgMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        User user = WangYuApplication.getUser(WangYuApplication.appContext);
        if (user != null) {
            AsyncImage.loadAvatar(getContext(), HttpConstant.SERVICE_UPLOAD_AREA + user.getIcon(), imgMyHeader);
            getMyStatistics();
            requestMsgCount();
            btnMyEdit.setText("编辑资料");
        } else {
            cleanInfo();
        }

        tvLeftTitle.setText(R.string.main_bar_mine);
        ibLeft.setImageResource(R.drawable.icon_settings);
        ibLeft.setVisibility(View.VISIBLE);
        initView();
        mInstance = this;
        BroadcastController.registerUserChangeReceiver(getContext(), mUserChangeReceiver);
    }

    /**
     * 清空登陆状态
     */
    private void cleanInfo() {
        btnMyEdit.setText("立即登录");
        tvMessageCount.setVisibility(View.GONE);
        tvMyOrderCount.setVisibility(View.GONE);
        tvMyActivities.setText("0");
        tvMyFans.setText("0");
        tvMyAttentions.setText("0");
        imgMyHeader.setImageResource(R.drawable.default_head);
    }

    @OnClick({R.id.btn_my_edit, R.id.rl_my_order, R.id.rl_my_wallet, R.id.rl_my_match_card, R.id.rl_my_matchs,
            R.id.rl_my_team, R.id.rl_my_reward, R.id.rl_my_collect, R.id.rl_my_gamecenter, R.id.ibLeft, R.id.img_bar_right,
            R.id.tv_my_activities, R.id.tv_my_attentions, R.id.tv_my_fans})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_my_edit:
                shouldUpdate = true;
                jumpToActivity(EditDataActivity.class);
                break;
            case R.id.rl_my_order:
                shouldUpdate = true;
                jumpToActivity(MyOrderActivity.class);
                break;
            case R.id.rl_my_wallet:
                jumpToActivity(MyRedBagActivity.class);
                break;
            case R.id.rl_my_match_card:
                jumpToActivity(MyActivityCardActivity.class);
                break;
            case R.id.rl_my_matchs:
                jumpToActivity(MyMatchActivity.class);
                break;
            case R.id.rl_my_team:
                jumpToActivity(MyCorpsActivity.class);
                break;
            case R.id.rl_my_reward:
                jumpToActivity(MyRewardActivity.class);
                break;
            case R.id.rl_my_collect:
                jumpToActivity(MyCollectActivity.class);
                break;
            case R.id.rl_my_gamecenter:
                jumpToActivity(GameCenterActivity.class);
                break;
            case R.id.ibLeft:
                jumpToActivity(SettingActivity.class);
                break;
            case R.id.img_bar_right:
                jumpToActivity(MyMessageActivity.class);
                break;
            case R.id.tv_my_activities:
                break;
            case R.id.tv_my_attentions:
                shouldUpdate = true;
                Intent intent = new Intent();
                User user = WangYuApplication.getUser(WangYuApplication.appContext);
                intent.putExtra("id", user.getId());
                jumpToActivity(AttentionListActivity.class, intent);
                break;
            case R.id.tv_my_fans:
                intent = new Intent();
                user = WangYuApplication.getUser(WangYuApplication.appContext);
                intent.putExtra("id", user.getId());
                jumpToActivity(FansListActivity.class, intent);
                break;
        }
    }


    /**
     * activity跳转
     *
     * @param clazz
     */
    private void jumpToActivity(Class clazz) {
        User user = WangYuApplication.getUser(WangYuApplication.appContext);
        Intent intent;
        if (user != null || clazz == GameCenterActivity.class || clazz == SettingActivity.class) {
            intent = new Intent(getContext(), clazz);
            startActivity(intent);
        } else {
            intent = new Intent(getContext(), LoginActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    /**
     * activity跳转
     */
    private void jumpToActivity(Class clazz, Intent intent) {
        User user = WangYuApplication.getUser(WangYuApplication.appContext);
        if (user != null || clazz == GameCenterActivity.class) {
            intent.setClass(getActivity(), clazz);
            startActivity(intent);
        } else {
            intent = new Intent(getContext(), LoginActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    /**
     * 获取粉丝数
     */
    private void getMyStatistics() {
        User user = WangYuApplication.getUser(getContext());
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_STATISTICS, map, HttpConstant.MY_STATISTICS);
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        try {
            if (method.equals(HttpConstant.MY_STATISTICS)) {
                mData = GsonUtil.getBean(object.getString("object").toString(), MyStatistics.class);
                setViews(mData);
            } else if (method.equals(HttpConstant.GET_MSG_COUNT)) {
                messageCount = GsonUtil.getBean(object.getString("object").toString(), MessageCount.class);
                if (messageCount.getTotal() > 0) {
                    tvMessageCount.setText(Utils.getnumberForms(messageCount.getTotal(), WangYuApplication.getGlobalContext()));
                    tvMessageCount.setVisibility(View.VISIBLE);
                } else {
                    tvMessageCount.setVisibility(View.GONE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置界面
     */
    private void setViews(MyStatistics data) {
        tvMyFans.setText(data.getFansTotal() + "");
        tvMyOrderCount.setText("待评价x" + data.getUnEvaOrderCount() + "");
        tvMyAttentions.setText(data.getConcernTotal() + "");
        tvMyActivities.setText(data.getActivityTotal() + "");
        if (data.getUnEvaOrderCount() > 0) {
            tvMyOrderCount.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 请求未读的消息
     */
    public void requestMsgCount() {
        Map<String, String> map = new HashMap<>();
        User user = WangYuApplication.getUser(WangYuApplication.appContext);

        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.GET_MSG_COUNT, map, HttpConstant.GET_MSG_COUNT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldUpdate) {
            User user = WangYuApplication.getUser(WangYuApplication.appContext);
            if (user != null) {
                AsyncImage.loadAvatar(getContext(), HttpConstant.SERVICE_UPLOAD_AREA + user.getIcon(), imgMyHeader);
                getMyStatistics();
                requestMsgCount();
                btnMyEdit.setText("编辑资料");
            } else {
                cleanInfo();
            }
            shouldUpdate = false;
        }
    }

    public static FragmentMyMain getInstance() {
        return mInstance;
    }

    public void refreshPush(int msgType) {
        if (messageCount != null) {
            messageCount.setTotal(messageCount.getTotal() + 1);
            tvMessageCount.setText(Utils.getnumberForms(messageCount.getTotal(), WangYuApplication.getGlobalContext()));
            tvMessageCount.setVisibility(View.VISIBLE);
        }
    }

    private BroadcastReceiver mUserChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            shouldUpdate = true;
        }
    };
}
