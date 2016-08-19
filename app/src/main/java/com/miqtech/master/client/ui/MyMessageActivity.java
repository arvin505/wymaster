package com.miqtech.master.client.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.FragmentPagerAdpter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.ui.fragment.FragmentMyActivities;
import com.miqtech.master.client.ui.fragment.FragmentMyComment;
import com.miqtech.master.client.ui.fragment.FragmentMyOrder;
import com.miqtech.master.client.ui.fragment.FragmentMySystem;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 我的消息
 * Created by Administrator on 2015/12/4.
 */
public class MyMessageActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    @Bind(R.id.message_order_tv)
    TextView order_tv;
    @Bind(R.id.message_activitis_tv)
    TextView activities_tv;
    @Bind(R.id.message_system_tv)
    TextView system_tv;
    @Bind(R.id.img_order_select)
    ImageView order_iv;
    @Bind(R.id.img_activitis_select)
    ImageView activities_iv;
    @Bind(R.id.img_system_select)
    ImageView system_iv;

    @Bind(R.id.message_order_ll)
    LinearLayout order_ll;
    @Bind(R.id.message_activitis_ll)
    LinearLayout activities_ll;
    @Bind(R.id.message_system_ll)
    LinearLayout system_ll;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.message_comment_ll)
    LinearLayout commentLl;
    @Bind(R.id.message_comment_tv)
    TextView commentTv;
    @Bind(R.id.img_comment_select)
    ImageView commentIv;


    //@Bind(R.id.order_drop)
    private TextView order_drop;
    //@Bind(R.id.activitis_drop)
    private TextView activitis_drop;
    //@Bind(R.id.system_drop)
    private TextView system_drop;
    private TextView commentDrop;

    private Context mContext;
    //        private FragmentPagerAdpterForMessage adapter;
    private FragmentPagerAdpter adapter;

    public static final int SELECT_ORDER = 0;
    public static final int SELECT_ACTIVITIES = 1;
    public static final int SELECT_COMMENT = 2;
    public static final int SELECT_SYSTEM = 3;

    public final static int Type_Order = 1;
    public final static int Type_Activites = 2;
    public final static int Type_System = 3;
    public final static int Type_Comment = 4;

    //0系统消息 1红包消息 2会员消息 3预定消息 4支付消息 5赛事消息 6约战消息
    //0系统消息 1红包消息 2会员消息 3预定消息 4支付消息 5赛事消息 6约战消息7娱乐赛提示消息8评论点赞消息9评论消息12众筹夺宝,10商品兑换
    public final static int MsgType_Redbag = 1;
    public final static int MsgType_System = 0;
    public final static int MsgType_Member = 2;
    public final static int MsgType_Reserve = 3;
    public final static int MsgType_Pay = 4;
    public final static int MsgType_Match = 5;
    public final static int MsgType_Yuezhan = 6;
    public final static int MsgType_Praise = 8;
    public final static int MsgType_Amuse = 7;
    public final static int MsgType_Exchange = 10;
    public final static int MsgType_ShopDetail = 12;
    public final static int MsgType_OET = 16;//自发赛
    private int type;
    private MyBaseFragment fragment;
    private int current;
    private int typeFragment = 0;


    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_my_message);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        order_drop = (TextView) findViewById(R.id.order_drop);
        activitis_drop = (TextView) findViewById(R.id.activitis_drop);
        system_drop = (TextView) findViewById(R.id.system_drop);
        commentDrop = (TextView) findViewById(R.id.comment_drop);

        mContext = MyMessageActivity.this;
        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("我的消息");
        setRightTextView("清除未读");
        getButtomLineView().setVisibility(View.GONE);

//        adapter = new FragmentPagerAdpterForMessage(this);
        adapter = new FragmentPagerAdpter(this);
        adapter.addTab(FragmentMyOrder.class, null);
        adapter.addTab(FragmentMyActivities.class, null);
        adapter.addTab(FragmentMyComment.class, null);
        adapter.addTab(FragmentMySystem.class, null);
        viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.setAdapter(adapter);
//        selectfragment(SELECT_ORDER);

        order_ll.setOnClickListener(this);
        activities_ll.setOnClickListener(this);
        system_ll.setOnClickListener(this);
        commentLl.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);
        getLeftBtn().setOnClickListener(this);
        getRightTextview().setOnClickListener(this);

        typeFragment = getIntent().getIntExtra("typeFragment", 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                selectfragment(typeFragment);
            }
        }, 100);
    }

    @Override
    protected void initData() {
        super.initData();
        int index = viewPager.getCurrentItem();
        fragment = (MyBaseFragment) adapter.getItem(index);
        fragment.refreView();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectfragment(position);
        current = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferencesUtil.clearPushStatue(mContext);
        refreMessage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.message_order_ll:
                selectfragment(SELECT_ORDER);
                break;
            case R.id.message_activitis_ll:
                selectfragment(SELECT_ACTIVITIES);
                break;
            case R.id.message_system_ll:
                selectfragment(SELECT_SYSTEM);
                break;
            case R.id.message_comment_ll:
                selectfragment(SELECT_COMMENT);
                break;
            case R.id.tvRightHandle:
                creatDialogForClearUp();
                break;
        }
    }

    private void creatDialogForClearUp() {
        final Dialog mDialog = new Dialog(mContext, R.style.register_style);
        mDialog.setContentView(R.layout.dialog_register_marked_words);
        TextView title_tv = (TextView) mDialog.findViewById(R.id.dialog_title_register);
        TextView context_tv = (TextView) mDialog.findViewById(R.id.dialog_content_register);
        TextView ok_bt = (TextView) mDialog.findViewById(R.id.dialog_register_yes_pact);
        TextView no_bt = (TextView) mDialog.findViewById(R.id.dialog_register_no_pact);
        View vv = mDialog.findViewById(R.id.dialog_line_no_pact);
        vv.setVisibility(View.VISIBLE);
        no_bt.setVisibility(View.VISIBLE);

        title_tv.setText(getResources().getString(R.string.clear_up_unread_messages));
        title_tv.setVisibility(View.VISIBLE);

        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUnread();
                mDialog.dismiss();
//                showLoading();
            }
        });

        no_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    /**
     * 设置全部消息已读1.订单 2.活动 3.系统
     */
    public void clearUnread() {
//        type = 1 + viewPager.getCurrentItem();
        switch (viewPager.getCurrentItem()) {
            case 0:
                type = Type_Order;
                break;
            case 1:
                type = Type_Activites;
                break;
            case 2:
                type = Type_Comment;
                break;
            case 3:
                type = Type_System;
                break;
        }
        User user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("type", type + "");
            map.put("isAll", 1 + "");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MULTIREAD, map, HttpConstant.MULTIREAD);
        } else {
            showToast(getResources().getString(R.string.login));
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        try {
            if (method.equals(HttpConstant.MULTIREAD)) {
                if (0 == object.getInt("code")) {
                    showToast("清除成功");
                    refreChildView();
                }
            } else {
                showToast("清除失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        try {
            if (object.has("result")) {
                if (method.equals(HttpConstant.MULTIREAD)) {
                    showToast("清除失败");
                } else {
                    showToast(object.getString("result"));
                }
            } else {
                showToast(object.toString());
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

    private void refreChildView() {
        int index = viewPager.getCurrentItem();
        fragment = (MyBaseFragment) adapter.getItem(index);
        fragment.refreView();
        if (index == 0) {
            MainActivity.orderCount = 0;
            BroadcastController.sendUserChangeBroadcase(mContext);
        } else if (index == 1) {
            MainActivity.activitesCount = 0;
            BroadcastController.sendUserChangeBroadcase(mContext);
        } else if (index == 3) {
            BroadcastController.sendUserChangeBroadcase(mContext);
            MainActivity.systemCount = 0;
        } else if (index == 2) {
            BroadcastController.sendUserChangeBroadcase(mContext);
            MainActivity.commentCount = 0;
        }
        refreMessage();
    }

    public void refreMessage() {

        if (MainActivity.orderCount > 0) {
            order_drop.setVisibility(View.VISIBLE);
            order_drop.setText(Utils.getnumberForms(MainActivity.orderCount, mContext));
        } else {
            order_drop.setVisibility(View.GONE);
        }
        if (MainActivity.activitesCount > 0) {
            activitis_drop.setVisibility(View.VISIBLE);
            activitis_drop.setText(Utils.getnumberForms(MainActivity.activitesCount, mContext));
        } else {
            activitis_drop.setVisibility(View.GONE);
        }
        if (MainActivity.systemCount > 0) {
            system_drop.setVisibility(View.VISIBLE);
            system_drop.setText(Utils.getnumberForms(MainActivity.systemCount, mContext));
        } else {
            system_drop.setVisibility(View.GONE);
        }
        if (MainActivity.commentCount > 0) {
            commentDrop.setVisibility(View.VISIBLE);
            commentDrop.setText(Utils.getnumberForms(MainActivity.commentCount, mContext));
        } else {
            commentDrop.setVisibility(View.GONE);
        }

        isOnclickForRightTextView(current);
    }

    /**
     * 选择那个fragment
     *
     * @param i
     */
    private void selectfragment(int i) {
        clearALlStatu();
        viewPager.setCurrentItem(i);
        isOnclickForRightTextView(i);
        switch (i) {
            case SELECT_ORDER://订单
                order_iv.setVisibility(View.VISIBLE);
                order_tv.setTextColor(getResources().getColor(R.color.orange));
                break;
            case SELECT_ACTIVITIES://活动
                activities_iv.setVisibility(View.VISIBLE);
                activities_tv.setTextColor(getResources().getColor(R.color.orange));
                break;
            case SELECT_SYSTEM://系统
                system_iv.setVisibility(View.VISIBLE);
                system_tv.setTextColor(getResources().getColor(R.color.orange));
                break;
            case SELECT_COMMENT://评论
                commentIv.setVisibility(View.VISIBLE);
                commentTv.setTextColor(getResources().getColor(R.color.orange));
                break;
        }
    }

    /**
     * 判断右边顶部是否可点
     *
     * @param j
     */
    private void isOnclickForRightTextView(int j) {
        switch (j) {
            case SELECT_ORDER:
                if (MainActivity.orderCount == 0) {
                    getRightTextview().setTextColor(getResources().getColor(R.color.gray));
                    getRightTextview().setEnabled(false);
                } else {
                    getRightTextview().setTextColor(getResources().getColor(R.color.orange));
                    getRightTextview().setEnabled(true);
                }
                break;
            case SELECT_ACTIVITIES:
                if (MainActivity.activitesCount == 0) {
                    getRightTextview().setTextColor(getResources().getColor(R.color.gray));
                    getRightTextview().setEnabled(false);
                } else {
                    getRightTextview().setTextColor(getResources().getColor(R.color.orange));
                    getRightTextview().setEnabled(true);
                }
                break;
            case SELECT_SYSTEM:
                if (MainActivity.systemCount == 0) {
                    getRightTextview().setTextColor(getResources().getColor(R.color.gray));
                    getRightTextview().setEnabled(false);
                } else {
                    getRightTextview().setTextColor(getResources().getColor(R.color.orange));
                    getRightTextview().setEnabled(true);
                }
                break;
            case SELECT_COMMENT:
                if (MainActivity.commentCount == 0) {
                    getRightTextview().setTextColor(getResources().getColor(R.color.gray));
                    getRightTextview().setEnabled(false);
                } else {
                    getRightTextview().setTextColor(getResources().getColor(R.color.orange));
                    getRightTextview().setEnabled(true);
                }
                break;
        }
    }

    /**
     * 情况状态
     */
    private void clearALlStatu() {
        order_iv.setVisibility(View.INVISIBLE);
        activities_iv.setVisibility(View.INVISIBLE);
        system_iv.setVisibility(View.INVISIBLE);
        commentIv.setVisibility(View.INVISIBLE);
        order_tv.setTextColor(getResources().getColor(R.color.gray));
        activities_tv.setTextColor(getResources().getColor(R.color.gray));
        system_tv.setTextColor(getResources().getColor(R.color.gray));
        commentTv.setTextColor(getResources().getColor(R.color.gray));
    }

}
