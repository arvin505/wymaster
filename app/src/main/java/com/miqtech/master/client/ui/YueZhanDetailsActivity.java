package com.miqtech.master.client.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.FragmentPagerAdpter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.entity.YueZhan;

import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

import com.miqtech.master.client.ui.fragment.FragmentMyWarApply;
import com.miqtech.master.client.ui.fragment.FragmentYuezhanComment;
import com.miqtech.master.client.ui.fragment.FragmentYuezhanInfo;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ShareToFriendsUtil;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.view.ExpertMorePopupWindow;
import com.miqtech.master.client.view.MyAlertView;
import com.miqtech.master.client.view.StickyContainer;
import com.miqtech.master.client.view.MyAlertView.DialogAction;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.constant.WBConstants;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("ResourceAsColor")
public class YueZhanDetailsActivity extends BaseActivity implements OnClickListener, FragmentYuezhanComment.UpdateCommentCountsViewListener,
        IWeiboHandler.Response {

    private ViewPager vPager;
    private StickyContainer mCarousel;
    private FragmentPagerAdpter adapter;
    private TextView tvInfoDetail, tvHasApply, tvComment, tvMerChant;
    private ImageView ivHeader, ivGame;
    private TextView tvName, tvIntro;
    private Button btnHandle;
    public static YueZhan yueZhan;
    private Context context;
    private RelativeLayout rlInfo, rlComment;
    private ChangeUIListener listener;
    private FragmentYuezhanInfo fragmentInfo;
    private ImageView ivShare;
    private RelativeLayout parent;

    private MyAlertView redbagDialog;
    private ShareToFriendsUtil shareToFriendsUtil;
    private ExpertMorePopupWindow popwin;


    private String title = "网娱大师赞助免费上网吧快来领取去开黑吧！";
    private String content = "领取红包可抵扣在线支付金额，分享更可多得，可叠加使用！";
    private String redBagUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SHRAE_REDBAG + "type=3&id=";// http://test.api.wangyuhudong.com/redbag/web/shareRedbag?type=1&id=1028
    private ImageView ivReadbag;
    private String yuezhanId;
    private boolean isFirstRedbag = false;
    private String inviteId;// 邀请约战列表需要传的邀请ID
    private ImageView back;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
        popwin.setOnItemClick(itemOnClick);
        shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
        initSinaSso(savedInstanceState);
        lengthCoding = 10022 + "";
    }

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.activity_yuezhandetails);
        context = this;
        initView();

        String className = getIntent().getStringExtra("source");
        /*if (className != null && className.equals(ReleaseWarActivity.class.getName())) {
            isFirstRedbag = true;
		}*/
        initData();
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        yuezhanId = getIntent().getStringExtra("id");
        LogUtil.e("id", " id   22 == " + yuezhanId);
        inviteId = getIntent().getStringExtra("inviteId");
        loadYuezhanDetail(yuezhanId);
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();
        parent = (RelativeLayout) findViewById(R.id.rlParent);
        vPager = (ViewPager) findViewById(R.id.carousel_pager);
        mCarousel = (StickyContainer) findViewById(R.id.carousel_header);
        tvInfoDetail = (TextView) findViewById(R.id.tvDetailInfo);
        tvComment = (TextView) findViewById(R.id.tvComment);
        tvName = (TextView) findViewById(R.id.tvYueZhanUserName);
        tvIntro = (TextView) findViewById(R.id.tvYueZhanTitle);
        ivHeader = (ImageView) findViewById(R.id.ivYueZhanHeader);
        btnHandle = (Button) findViewById(R.id.btnYueZhanHandle);
        ivGame = (ImageView) findViewById(R.id.ivGame);
        ivReadbag = (ImageView) findViewById(R.id.ivRedbag);
        back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(this);
        setLeftIncludeTitle("约战详情");
        btnHandle.setOnClickListener(this);
        adapter = new FragmentPagerAdpter(this);

        tvInfoDetail.setText("详细信息");
        tvComment.setText("评论");

        rlInfo = (RelativeLayout) findViewById(R.id.rlInfo);
        rlComment = (RelativeLayout) findViewById(R.id.rlComment);
        ivShare = (ImageView) findViewById(R.id.iv_title_right);
        ivShare.setImageResource(R.drawable.icon_share_oranger);
        ivShare.setVisibility(View.VISIBLE);

        tvInfoDetail.setTextColor(getResources().getColor(R.color.blue_gray));
        tvComment.setTextColor(getResources().getColor(R.color.white));
        rlInfo.setBackgroundResource(R.color.white);
        rlComment.setBackgroundResource(R.color.blue_gray);

        rlInfo.setOnClickListener(this);
        rlComment.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivReadbag.setOnClickListener(this);
        ivHeader.setOnClickListener(this);
    }

    private void loadYuezhanDetail(String id) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(WangYuApplication.appContext);
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
        }
        params.put("id", id);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MATCH_DETAIL, params, HttpConstant.MATCH_DETAIL);
    }

    private void updateYueZhanMainView() {
        AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + yueZhan.getItem_pic(), ivGame);
        AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + yueZhan.getReleaser_icon(), ivHeader);
        tvName.setText(yueZhan.getNickname());
        tvComment.setText("评论" + "  (" + yueZhan.getCommentsCount() + ")");
        tvIntro.setText(yueZhan.getTitle());
        btnHandle.setBackgroundResource(R.drawable.shape_yellow_bg);
        if (yueZhan.getUserStatus() == 1) {
            btnHandle.setText("取消约战");
        } else if (yueZhan.getUserStatus() == 2) {
            btnHandle.setText("退出约战");
        } else if (yueZhan.getUserStatus() == 3) {
            if (inviteId != null) {
                btnHandle.setText("接受邀请");
            } else {
                btnHandle.setText("参加约战");
            }
        } else if (yueZhan.getUserStatus() == -1) {
            btnHandle.setText("参加约战");
        }
        if (yueZhan.getIs_start() == 1) {
            btnHandle.setBackgroundResource(R.drawable.shape_dark_gray_bg);
            btnHandle.setText("已过期");
            btnHandle.setOnClickListener(null);
        }
        String strBeginTime = yueZhan.getBegin_time();

        if (!TextUtils.isEmpty(strBeginTime)) {
            Date beginTime = DateUtil.strToDateLong(strBeginTime);
            Date nowTime = DateUtil.getNowDateShort();
        }
        // tvIntro.setText(yueZhan.);
        diaplayRedbag();
    }

    private void addYueZhanAdapter() {
        adapter.addTab(FragmentYuezhanInfo.class, null);
        adapter.addTab(FragmentYuezhanComment.class, null);
        vPager.setAdapter(adapter);
        vPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    mCarousel.restoreYCoordinate(75, vPager.getCurrentItem());
                    setTabState();
                }
            }
        });
    }

    public void setFragmentInfo(FragmentYuezhanInfo fragmentInfo) {
        this.fragmentInfo = fragmentInfo;
    }


    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        try {
            String obj = "";

            if (object.has("object")) {
                obj = object.getJSONObject("object").toString();
            } else {
                obj = object.toString();
            }

            if (method.equals(HttpConstant.MATCH_DETAIL)) {
                yueZhan = new Gson().fromJson(obj.toString(), YueZhan.class);
                if (vPager.getChildCount() == 0) {
                    addYueZhanAdapter();
                } else {
                    fragmentInfo.changeUI();
                }

                if (!("".equals(yueZhan.getBeInvited())) && yueZhan.getBeInvited() != null) {
                    inviteId = yueZhan.getBeInvited();
                }

                updateYueZhanMainView();
            } else if (method.equals(HttpConstant.EXIT_MATCH)) {
                showToast("退出成功");
                BroadcastController.sendUserChangeBroadcase(context);
                initData();
                //FragmentMyWarApply.isSuccess = true;
            } else if (method.equals(HttpConstant.APPLY_MATCH)) {
                showToast("报名成功");
                BroadcastController.sendUserChangeBroadcase(context);
                initData();
            } else if (method.equals(HttpConstant.CANCEL_MATCH)) {
                showToast("解散成功");
                BroadcastController.sendUserChangeBroadcase(context);
                finish();
            } else if (method.equals(HttpConstant.DEAL_WITH_INVATE)) {
                showToast();
                inviteId = null;
                BroadcastController.sendUserChangeBroadcase(context);
                FragmentMyWarApply.isSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        if (HttpConstant.DEAL_WITH_INVATE.equals(method)) {
            MyAlertView.Builder builder = new MyAlertView.Builder(context);
            builder.setMessage("太迟啦...约战已经满了");
            builder.setTitle("约战已满");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            builder.create().show();
            inviteId = null;
            btnHandle.setBackgroundResource(R.color.font_gray);
            btnHandle.setText("约战已满");
            btnHandle.setOnClickListener(null);
            //FragmentMyWarApply.isSuccess = true;
        } else {
            try {
                showToast(object.getString("result"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String method, String errorInfo) {
        super.onError(method, errorInfo);
    }

    private void setTabState() {
        if (vPager.getCurrentItem() == 0) {
            tvInfoDetail.setTextColor(getResources().getColor(R.color.blue_gray));
            // tvHasApply.setTextColor(getResources().getColor(R.color.gray));
            tvComment.setTextColor(getResources().getColor(R.color.white));
            rlInfo.setBackgroundResource(R.color.white);
            rlComment.setBackgroundResource(R.color.blue_gray);
        }
        // else if (vPager.getCurrentItem() == 1) {
        // tvInfoDetail.setTextColor(getResources().getColor(R.color.gray));
        // tvHasApply.setTextColor(getResources().getColor(R.color.blue_gray));
        // tvComment.setTextColor(getResources().getColor(R.color.gray));
        // vInfoDetail.setVisibility(View.GONE);
        // vHasApply.setVisibility(View.VISIBLE);
        // vComment.setVisibility(View.GONE);
        // }
        else if (vPager.getCurrentItem() == 1) {
            tvInfoDetail.setTextColor(getResources().getColor(R.color.white));
            // tvHasApply.setTextColor(getResources().getColor(R.color.gray));
            tvComment.setTextColor(getResources().getColor(R.color.blue_gray));
            rlInfo.setBackgroundResource(R.color.blue_gray);
            rlComment.setBackgroundResource(R.color.white);
        }
    }


    public void initSinaSso(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            shareToFriendsUtil.getIWeiApiInstance(this).handleWeiboResponse(getIntent(), this);
        }
    }

    /**
     * 接收微客户端博请求的数据。 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     *
     * @param baseRequest 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                Toast.makeText(this, R.string.errcode_success, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Toast.makeText(this, R.string.errcode_cancel, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Toast.makeText(this, getResources().getString(R.string.errcode_deny) + "Error Message: " + baseResp.errMsg,
                        Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        shareToFriendsUtil.getIWeiApiInstance(context).handleWeiboResponse(intent, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (shareToFriendsUtil != null && shareToFriendsUtil.getmSsoHandler() != null) {
//            shareToFriendsUtil.getmSsoHandler().authorizeCallBack(requestCode, resultCode, data);
//        }
        if (shareToFriendsUtil != null && shareToFriendsUtil.getmTencent(this) != null)
            shareToFriendsUtil.getmTencent(this).onActivityResult(requestCode, resultCode, data);
    }

    OnClickListener itemOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (yueZhan == null) {
                return;
            }
            String sharetitle = "华山论剑，不服SOLO-" + yueZhan.getNickname() + "的约战";
//            String sharecontent = "游戏类型:" + yueZhan.getItem_name() + "\n"
//                    + "开始时间:" + TimeFormatUtil.formatNoTime(yueZhan.getBegin_time()) + "\n" + "服务器:" + yueZhan.getServer();
            String sharecontent = yueZhan.getItem_name() + "\n" + yueZhan.getServer() + "\n" + DateUtil.strToDatePinYin(yueZhan.getBegin_time());
            String shareurl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.YUEZHAN_URL + yueZhan.getId();
            String imgurl = HttpConstant.SERVICE_UPLOAD_AREA + yueZhan.getIcon();
            switch (v.getId()) {
                case R.id.llSina:
                    shareToFriendsUtil.shareBySina(sharetitle, sharecontent, shareurl, imgurl);
                    break;
                case R.id.llWeChat:
                    shareToFriendsUtil.shareWyByWXFriend(sharetitle, sharecontent, shareurl, imgurl, 0);
                    break;
                case R.id.llFriend:
                    shareToFriendsUtil.shareWyByWXFriend(sharetitle, sharecontent, shareurl, imgurl, 1);
                    break;
                case R.id.llQQ:
                    shareToFriendsUtil.shareByQQ(sharetitle, sharecontent, shareurl, imgurl);
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.btnYueZhanHandle:
                if (yueZhan == null) {
                    return;
                }
                if (yueZhan.getUserStatus() == -1) {
                    Intent intent = new Intent();
                    intent.setClass(context, LoginActivity.class);
                    startActivity(intent);
                } else if (yueZhan.getUserStatus() == 1) {
                    // 解除约战
                    showCancelMatchDialog();
                } else if (yueZhan.getUserStatus() == 2) {
                    // 退出约战
                    showExitMatchDialog();
                } else if (yueZhan.getUserStatus() == 3) {

                    if (inviteId != null) {
                        // 处理邀请
                        dealmatch();
                    } else {
                        // 报名
                        showApplyMatchDialog();
                    }
                }

                break;
            case R.id.rlComment:
                vPager.setCurrentItem(1);
                break;
            case R.id.rlInfo:
                vPager.setCurrentItem(0);
                break;
            case R.id.iv_title_right:
                if (popwin != null) {
                    popwin.show();
                } else {
                    popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
                    popwin.setOnItemClick(itemOnClick);
                    shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
                    popwin.show();
                }
                break;
            case R.id.ivRedbag:
                if (redbagDialog != null && !redbagDialog.isShowing()) {
                    redbagDialog.show();
                }
                break;
            case R.id.ivYueZhanHeader:
                Intent intent = new Intent();
                intent.setClass(this, PersonalHomePageActivity.class);
                intent.putExtra("id", yueZhan.getReleaser_id() + "");
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    private void diaplayRedbag() {
        if (yueZhan.getCanShareRedbag() > 0) {
            initRedbag(yueZhan.getShareRedbagNumber());
            ivReadbag.setVisibility(View.VISIBLE);
            if (redbagDialog != null && isFirstRedbag) {
                redbagDialog.show();
            }
        } else {
            ivReadbag.setVisibility(View.GONE);
        }
    }

    private void initRedbag(int redbagCount) {
        redbagDialog = getRedBagDialog(context, "恭喜获得" + redbagCount + "个红包", "分享给小伙伴吧", "立即分享", "",
                new DialogAction() {
                    @Override
                    public void doPositive() {

                    }

                    @Override
                    public void doNegative() {

                    }

                    @Override
                    public void doWeiXinShrae(int change) {

                    }
                });
        redbagDialog.setCanceledOnTouchOutside(false);
        redbagDialog.setOnKeyListener(new OnKeyListener() {
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

    private void toWeichat(String title, String content, String url, String icon) {
        shareToFriendsUtil.shareRedbagWyByWXFriend(title, content, url, icon, 0);
    }

    private void toFriends(String title, String content, String url, String icon) {
        shareToFriendsUtil.shareRedbagWyByWXFriend(title, content, url, icon, 1);
    }

    private void showApplyMatchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setIcon(R.drawable.icon);
        builder.setMessage("报名约战，您的手机号码会显示给发起者，便于联络。");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                applyMatch();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.show();
    }

    private void showCancelMatchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setIcon(R.drawable.icon);
        builder.setMessage("你确定要取消该约战???");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                cancelMatch();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.show();
    }

    private void showExitMatchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setIcon(R.drawable.icon);
        builder.setMessage("确定退出约战???");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                exitMatch();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.show();
    }

    // 解除约战
    private void cancelMatch() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(WangYuApplication.appContext);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("id", yueZhan.getId() + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.CANCEL_MATCH, params, HttpConstant.CANCEL_MATCH);
    }

    // 退出约战
    private void exitMatch() {
        showLoading();
        Map<String, String> params = new HashMap<>();

        User user = WangYuApplication.getUser(WangYuApplication.appContext);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("id", yueZhan.getId() + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.EXIT_MATCH, params, HttpConstant.EXIT_MATCH);
    }

    // 报名约战
    private void applyMatch() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(WangYuApplication.appContext);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("id", yueZhan.getId() + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.APPLY_MATCH, params, HttpConstant.APPLY_MATCH);
    }

    // 从被邀请约战列表调过来，处理约战邀请
    private void dealmatch() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(WangYuApplication.appContext);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("inviteId", inviteId);
        params.put("type", 1 + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DEAL_WITH_INVATE, params, HttpConstant.DEAL_WITH_INVATE);
    }

    public interface ChangeUIListener {
        void changeUI();
    }

    @Override
    public void updateView() {
        // TODO Auto-generated method stub
        yueZhan.setCommentsCount(yueZhan.getCommentsCount() + 1);
        tvComment.setText("评论" + "  (" + yueZhan.getCommentsCount() + ")");
    }

    /**
     * 接受成功后弹窗
     */
    private void showToast() {
        View vv = LayoutInflater.from(YueZhanDetailsActivity.this).inflate(R.layout.layout_yuezhan_dialog, null);
        Toast tt = new Toast(YueZhanDetailsActivity.this);
        tt.setView(vv);
        tt.setDuration(Toast.LENGTH_LONG);
        tt.setGravity(Gravity.CENTER, 0, 0);
        tt.show();
        initData();
    }

    public static MyAlertView getRedBagDialog(Context context, String title, String message, String positive,
                                              String Negative, final DialogAction action) {
        return new MyAlertView.Builder(context).setTitle(title).setMessage(message).createRedbag(action);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shareToFriendsUtil != null) {
            if (shareToFriendsUtil.requestUtil != null) {
                shareToFriendsUtil.requestUtil.removeTag(shareToFriendsUtil.getClass().getName());
            }
            shareToFriendsUtil.requestUtil = null;
            shareToFriendsUtil = null;
        }
    }
}
