package com.miqtech.master.client.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.DhcpInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.appmanager.AppManager;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.City;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.entity.VersionInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.jpush.service.PushType;
import com.miqtech.master.client.service.DownloadService;
import com.miqtech.master.client.service.MyLocationService;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.fragment.DiscoveryFragment;
import com.miqtech.master.client.ui.fragment.FragmentAthletics;
import com.miqtech.master.client.ui.fragment.FragmentDiscovery;
import com.miqtech.master.client.ui.fragment.FragmentInformationV3;
import com.miqtech.master.client.ui.fragment.FragmentMatch;
import com.miqtech.master.client.ui.fragment.FragmentMyMain;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.view.MyAlertView;
import com.miqtech.master.client.watcher.Observerable;
import com.screenrecorder.core.ScreenRecorder;
import com.screenrecorder.ui.ScreenRecorderActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by xiaoyi on 2016/7/28.
 */
public class MainActivity extends BaseActivity implements Observerable.ISubscribe, View.OnClickListener {
    @Bind(R.id.fragment_content)
    FrameLayout mainContent;
    @Bind(R.id.tv_main_bar_match)
    TextView tvMainBarMatch;
    @Bind(R.id.tv_main_bar_information)
    TextView tvMainBarInformation;
    @Bind(R.id.tv_main_bar_find)
    TextView tvMainBarFind;
    @Bind(R.id.tv_main_bar_mine)
    TextView tvMainBarMine;
    @Bind(R.id.ll_main_bar)
    LinearLayout llMainBar;
    @Bind(R.id.rl_main_header)
    RelativeLayout rlMainHeader;
    @Bind(R.id.tvCurrentCity)
    TextView tvCurrentCity;


    private List<Fragment> fragmentList;
    int mSelected = 0;
    private int changePage = 0;
    private String versionMsg;
    public static final String ATHLETICS = "athletics";
    private Class[] classes = {FragmentMatch.class, FragmentInformationV3.class, FragmentDiscovery.class, FragmentMyMain.class};
    private int[] barSelected = new int[]{R.drawable.icon_bar_match_selected, R.drawable.icon_bar_info_selected,
            R.drawable.icon_bar_find_selected, R.drawable.icon_bar_mine_selected};
    private int[] barUnselected = new int[]{R.drawable.icon_bar_match_unselected,
            R.drawable.icon_bar_info_unselected, R.drawable.icon_bar_find_unselected, R.drawable.icon_bar_mine_unselected};
    private Observerable watcher;
    private boolean isBinded;
    private DownloadService.DownloadBinder binder;
    // FIXME: 2016/7/28
    public static int orderCount = 0, systemCount = 0, activitesCount = 0, commentCount = 0;
    private Dialog updateDialog;
    private VersionInfo versionInfo;
    private Dialog matchDialog;
    private LocationActivityReceiver locReceiver;
    private Intent intent_service;
    private Dialog gpsDialog;
    private static final int REQUEST_CITY_LIST = 1;
    private long lastClickBackTime = -1;
    private static final int QUIT_CONFIRM_TIME = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_v4);
        lengthCoding = UMengStatisticsUtil.CODE_0000;
        ButterKnife.bind(this);
        versionMsg = getIntent().getStringExtra("versionInfo");
        changePage = getIntent().getIntExtra(ATHLETICS, 0);
        initView();
        serviceReceiver();
        if (JPushInterface.isPushStopped(this)) {
            JPushInterface.resumePush(this);
        } else {
            JPushInterface.init(getApplicationContext()); // 初始化 JPush
        }
        watcher = Observerable.getInstance();
        watcher.subscribe(Observerable.ObserverableType.QRCODEDIALOG, this);

        distriBution(getIntent());

        BroadcastController.registerUserChangeReceiver(this, mUserChangeReceiver);

        //registerReceiver(liveReceiver,new IntentFilter("com.miqtech.wymaster.live"));
    }

    private BroadcastReceiver mUserChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收到变化后，更新用户资料
            // FIXME: 2016/7/28
            //updateUserView();
            //getMyStatistics();
        }
    };

    @Nullable
    @OnClick({R.id.tv_main_bar_match, R.id.tv_main_bar_information, R.id.tv_main_bar_find, R.id.tv_main_bar_mine,
            R.id.iv_search, R.id.iv_scan, R.id.tv_iknow, R.id.bt_open, R.id.bt_not_update, R.id.bt_update, R.id.tvCurrentCity,
            R.id.tvCancel, R.id.tvSure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_main_bar_match:
                setSelectItem(0);
                break;
            case R.id.tv_main_bar_information:
                setSelectItem(1);
                break;
            case R.id.tv_main_bar_find:
                setSelectItem(2);
                break;
            case R.id.tv_main_bar_mine:
                setSelectItem(3);
                break;

            // FIXME: 2016/7/28
            case R.id.tv_iknow:
                if (gpsDialog.isShowing()) {
                    gpsDialog.dismiss();
                }
                return;
            case R.id.bt_open:
                openSystemSettings();
                if (gpsDialog.isShowing()) {
                    gpsDialog.dismiss();
                }
                return;
            case R.id.bt_update:
                if (updateDialog != null && versionInfo != null) {
                    updateDialog.dismiss();
                    serviceStart(versionInfo, 1);
                }
                return;
            case R.id.bt_not_update:
                if (updateDialog != null && versionInfo != null) {
                    updateDialog.dismiss();
                }
                return;
            case R.id.iv_search:
                Intent search = new Intent(this, SearchActivity.class);
                startActivity(search);
                return;
            case R.id.tvCurrentCity:
                Intent intent = new Intent();
                intent.setClass(this, LocationCityActivity.class);
                startActivityForResult(intent, REQUEST_CITY_LIST);
                return;
            case R.id.tvCancel:
                if (updateDialog != null && versionInfo != null) {
                    if (versionInfo.getIsCoercive() == 1) {
                        updateDialog.setCanceledOnTouchOutside(false);
                        AppManager.getAppManager().AppExit(this);
                        updateDialog.dismiss();
                    } else {
                        updateDialog.dismiss();
                    }
                }
                return;
            case R.id.tvSure:
                if (updateDialog != null && versionInfo != null) {
                    updateDialog.dismiss();
                    serviceStart(versionInfo, 1);
                }
                return;
            case R.id.iv_scan:
                intent = new Intent();
                intent.setClass(this, CaptureActivity.class);
                startActivity(intent);
                return;
        }
    }

    /**
     * initview
     */
    public void initView() {
        fragmentList = new ArrayList<>();
        fragmentList = new ArrayList<>();
        for (int i = 0; i < classes.length; i++) {
            fragmentList.add(null);
        }

        setSelectItem(changePage);
    }

    public void setSelectItem(int position) {
        mSelected = position;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        //先隐藏所有fragment
        for (Fragment fragment : fragmentList) {
            if (null != fragment) {
                fragmentTransaction.hide(fragment);
            }
        }
        Fragment fragment;
        if (null == fragmentList.get(position)) {
            Bundle bundle = new Bundle();
            // bundle.putString(Constant.TITLE, drawerTitles[position]);
            fragment = Fragment.instantiate(this, classes[position].getName(), bundle);
            fragmentList.set(position, fragment);
            // 如果Fragment为空，则创建一个并添加到界面上
            fragmentTransaction.add(R.id.fragment_content, fragment);
        } else {
            // 如果Fragment不为空，则直接将它显示出来
            fragment = fragmentList.get(position);
            fragmentTransaction.show(fragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
        changeLableState(position);
    }

    private void changeLableState(int index) {
        int childCount = llMainBar.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView child = (TextView) llMainBar.getChildAt(i);
            Drawable icon = getResources().getDrawable(barUnselected[i]);
            int textColor = getResources().getColor(R.color.textColorGray);
            if (index == i) {
                icon = getResources().getDrawable(barSelected[i]);
                textColor = getResources().getColor(R.color.colorActionBarSelected);
            }
            icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
            child.setCompoundDrawables(null, icon, null, null);
            child.setTextColor(textColor);
        }

        if (index == 0) {
            rlMainHeader.setVisibility(View.VISIBLE);
        } else {
            rlMainHeader.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getIntExtra("bottom", -1) != 1) {
            distriBution(intent);
        }
    }

    /**
     * 从通知栏打开应用
     *
     * @param intent
     */
    private void distriBution(Intent intent) {
        int pushType = intent.getIntExtra("channel", -1);
        Intent newIntent = new Intent();
        switch (pushType) {
            case PushType.SYS_ID:
                newIntent.setClass(this, MyMessageActivity.class);
                newIntent.putExtra("typeFragment", 3);
                this.startActivity(newIntent);
                break;
            case PushType.COMMENT_ID:
                newIntent.setClass(this, MyMessageActivity.class);
                newIntent.putExtra("typeFragment", 2);
                this.startActivity(newIntent);
                break;
            case PushType.REDBAG_ID:
                newIntent.setClass(this, MyRedBagActivity.class);
                this.startActivity(newIntent);
                break;
            case PushType.RESERVATION_ID:
//                newIntent.setClass(this, ReserveOrderActivity.class);
//                newIntent.putExtra("orderType", 0);
//                newIntent.putExtra("reserveId", intent.getStringExtra("reserveId"));
//                this.startActivity(newIntent);
                break;
            case PushType.YUEZHAN_ID:
                newIntent.setClass(this, YueZhanDetailsActivity.class);
                newIntent.putExtra("id", intent.getStringExtra("id"));
                this.startActivity(newIntent);
                break;
            case PushType.NETBAR_ID:
                newIntent.setClass(this, InternetBarActivityV2.class);
                newIntent.putExtra("netbarId", intent.getStringExtra("netbarId"));
                this.startActivity(newIntent);
                break;
            case PushType.PAY_ID:
//                newIntent.setClass(this, ReserveOrderActivity.class);
//                newIntent.putExtra("orderType", 1);
//                newIntent.putExtra("reserveId", intent.getStringExtra("reserveId"));
//                this.startActivity(newIntent);
                break;
            case PushType.MATCH_ID:
                newIntent.setClass(this, OfficalEventActivity.class);
                newIntent.putExtra("matchId", intent.getStringExtra("matchId"));
                this.startActivity(newIntent);
                break;
            case PushType.WEEK_REDBAG_ID:
                newIntent.setClass(this, SubjectActivity.class);
                newIntent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.REDBAG);
                this.startActivity(newIntent);
                break;
            case PushType.AMUSE_ID:
                newIntent.setClass(this, RecreationMatchDetailsActivity.class);
                newIntent.putExtra("id", intent.getStringExtra("id"));
                this.startActivity(newIntent);
                break;
            case PushType.INFORMATION_ID:
                int id = Integer.parseInt(intent.getStringExtra("infoId"));
                int type = intent.getIntExtra("infoType", 0);
                toInfoDetail(type, id);
                break;
            case PushType.COMMODITY_ID:
                newIntent.setClass(this, ExchangeDetailActivity.class);
                newIntent.putExtra("exchangeID", intent.getStringExtra("exchangeID"));
                this.startActivity(newIntent);
                break;
            case PushType.ROBTREASURE_ID:
                newIntent.setClass(this, ShopDetailActivity.class);
                newIntent.putExtra("id", intent.getStringExtra("id"));
                this.startActivity(newIntent);
                break;
            case PushType.MALL_ID:
                newIntent.setClass(this, GoldCoinsStoreActivity.class);
                this.startActivity(newIntent);
                break;
            case PushType.COIN_TASK_ID:
                newIntent.setClass(this, CoinsTaskActivity.class);
                this.startActivity(newIntent);
                break;
            case PushType.AWARD_COMMDITY_ID:
                newIntent.setClass(this, SubjectActivity.class);
                newIntent.putExtra("id", intent.getStringExtra("id"));
                newIntent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.GOODS_DETAIL);
                this.startActivity(newIntent);
                break;
            case PushType.OET_ID://自发赛
                newIntent.setClass(this, EventDetailActivity.class);
                newIntent.putExtra("matchId", intent.getStringExtra("id"));
                this.startActivity(newIntent);
                break;
            case PushType.BOUNTY_ID://悬赏令
                newIntent.setClass(this, RewardActivity.class);
                newIntent.putExtra("rewardId", Integer.valueOf(intent.getStringExtra("id")));
                intent.putExtra("isEnd", 1 + "");
                this.startActivity(newIntent);
                break;
        }
    }

    private void toInfoDetail(int type, int id) {
        Intent intent;
        if (type == 1) {//类型:1图文  跳转
            intent = new Intent(this, InformationDetailActivity.class);
            intent.putExtra("id", id + "");
            intent.putExtra("type", type);
            /*intent.putExtra("categoryId", mInfoCatalog.getParent().getId() + "");
            intent.putExtra("pid", mInfoCatalog.getParent().getPid() + "");*/
            startActivity(intent);
        } else if (type == 2) {//2专题  跳转
            intent = new Intent();
            intent.putExtra("activityId", id);
            /*intent.putExtra("zhuanTitle", detail.getTitle());
            intent.putExtra("mInfoCatalog", mInfoCatalog);*/
            //intent.putExtra("url", detail.getIcon());
            intent.setClass(this, InformationTopicActivity.class);
            startActivity(intent);
        } else if (type == 3) {//3图集  跳转
            intent = new Intent();
            intent.putExtra("activityId", id);
            intent.setClass(this, InformationAtlasActivity.class);
            startActivity(intent);
        } else if (type == 4) {   //视频
            intent = new Intent(this, InformationDetailActivity.class);
            intent.putExtra("id", id + "");
            intent.putExtra("type", InformationDetailActivity.INFORMATION_VIDEO);
            startActivity(intent);
        }
    }


    @Override
    public <T> void update(T... data) {
        createMatchDialog((Integer) data[0]);
    }

    private void createMatchDialog(final int type) {
        if (matchDialog == null || !matchDialog.isShowing()) {
            //type 1:未登录  2:二维码已过期 3:已经报名当前场次
            matchDialog = new Dialog(this);

            matchDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (type != 1) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText("");
                    }
                }
            });
            try {
                int dividerID = getResources().getIdentifier("android:id/titleDivider", null, null);
                View divider = matchDialog.findViewById(dividerID);
                divider.setBackgroundColor(Color.TRANSPARENT);
            } catch (Exception e) {            //上面的代码，是用来去除Holo主题的蓝色线条
                e.printStackTrace();
            }
            Window window = matchDialog.getWindow();
            window.setContentView(R.layout.layout_match_status_dialog);
            matchDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            window.setGravity(Gravity.CENTER);
            matchDialog.setCanceledOnTouchOutside(false);
            Button btnCancel = (Button) matchDialog.findViewById(R.id.btnCancel);
            Button btnSure = (Button) matchDialog.findViewById(R.id.btnSure);
            TextView tvContent = (TextView) matchDialog.findViewById(R.id.tvContent);
            MatchDialogListener listener = new MatchDialogListener(type);
            if (type == 1) {
                btnSure.setText("登录");
                btnCancel.setText("取消");
                tvContent.setText("注册或登录后才能报名比赛");
            } else if (type == 2) {
                btnSure.setText("确认");
                btnCancel.setVisibility(View.GONE);
                tvContent.setText("当前二维码已过期");
            } else if (type == 3) {
                tvContent.setText("您已经报名了当前场次\n" +
                        "在[我的]-[我的赛事]中查看");
                btnSure.setText("确认");
                btnCancel.setVisibility(View.GONE);
            }
            btnCancel.setOnClickListener(listener);
            btnSure.setOnClickListener(listener);
            matchDialog.show();
            WindowManager windowManager = getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = matchDialog.getWindow().getAttributes();
            lp.width = (int) (display.getWidth()); //设置宽度
            matchDialog.getWindow().setAttributes(lp);
        }
    }

    //match弹窗点击监听
    private class MatchDialogListener implements View.OnClickListener {
        int type;

        MatchDialogListener(int type) {
            this.type = type;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnSure:
                    if (type == 1) {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, LoginActivity.class);
                        startActivityForResult(intent, LoginActivity.LOGIN_OK);
                        matchDialog.dismiss();
                    } else {
                        matchDialog.dismiss();
                    }

                    break;
                case R.id.btnCancel:
                    matchDialog.dismiss();
                    break;
            }
        }
    }

    /**
     * 注册广播，启动定位service
     */
    private void serviceReceiver() {
        locReceiver = new LocationActivityReceiver();
        IntentFilter locationFilter = new IntentFilter(MyLocationService.ACTIVITY_RESERVE);
        registerReceiver(locReceiver, locationFilter);
        intent_service = new Intent(MainActivity.this, MyLocationService.class);
        this.startService(intent_service);
    }

    private class LocationActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyLocationService.ACTIVITY_RESERVE)) {
                int codeType = intent.getIntExtra(MyLocationService.CODE_TYPE, -1);
                switch (codeType) {
                    case MyLocationService.LOCATION_END:
                        if (Constant.isLocation) {
                            if (Constant.currentCity != null) {
                                showToast("当前定位城市:" + Constant.locCity.getName());
                                stopService(intent_service);
                                if (Constant.locCity != null && Constant.currentCity != null
                                        && !TextUtils.isEmpty(Constant.locCity.getName())
                                        && !TextUtils.isEmpty(Constant.currentCity.getName())
                                        && !(Constant.locCity.getName().equals(Constant.currentCity.getName()))) {//当当前定位城市与上一次定位城市不同时，询问是否刷新页面

                                    createSelectedCityDialog();
                                }

                                tvCurrentCity.setText(Constant.currentCity.getName());
                            } else {
                                showToast("定位失败");
                                openLocDialog();
                            }

                        } else {
                            showToast("定位失败");
                            openLocDialog();
                        }
                        if (!PreferencesUtil.getAreacode(MainActivity.this).equals(Constant.currentCity.getAreaCode())) {
                            if (fragmentList.get(0) != null) {
                                ((FragmentMatch) fragmentList.get(0)).loadData();
                            }
                            PreferencesUtil.saveAreaCode(Constant.currentCity.getAreaCode(), MainActivity.this);
                            LogUtil.e("area", "areacode :" + Constant.currentCity.getAreaCode() + "   city  : " + PreferencesUtil.getAreacode(MainActivity.this));
                        }
                        break;
                }
            }
        }
    }

    /**
     * 提醒打开权限
     */
    private void openLocDialog() {
        if (gpsDialog != null) {
            gpsDialog.show();
            return;
        }
        gpsDialog = new Dialog(this);
        gpsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_open_location, null);

        Button open = (Button) view.findViewById(R.id.bt_open);
        open.setOnClickListener(this);

        TextView tvIknow = (TextView) view.findViewById(R.id.tv_iknow);
        tvIknow.setOnClickListener(this);

        gpsDialog.setContentView(view);
        gpsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        gpsDialog.setCanceledOnTouchOutside(false);
        gpsDialog.show();
    }

    /**
     * 打开系统设置
     */
    private void openSystemSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    private void serviceStart(VersionInfo version, int type) {
        Intent it = new Intent(MainActivity.this, DownloadService.class);
        Bundle bundle = new Bundle();

        LogUtil.e("tag", "===================url====" + version.getUrl());
        LogUtil.e("tag", "===================url====" + version.getPatchCode());
        if (type == 1) {
            //it.putExtra("apkUrl", version.getUrl());
            bundle.putString("apkUrl", version.getUrl());
        } else {
            //it.putExtra("apkUrl", version.getPatchUrl());
            bundle.putString("apkUrl", version.getPatchUrl());
        }
        //it.putExtra("type", type);
        bundle.putInt("type", type);
        //it.putExtra("patchcode", version.getPatchCode());
        bundle.putInt("patchCode", version.getPatchCode());
        it.putExtras(bundle);
        bindService(it, conn, BIND_AUTO_CREATE);

    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBinded = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (DownloadService.DownloadBinder) service;
            System.out.println("服务启动!!!");
            // 开始下载
            isBinded = true;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        getClipboardContent();
    }

    /**
     * 获取粘贴板内容
     */

    private void getClipboardContent() {
        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        CharSequence text = clipboard.getText();
        if (!TextUtils.isEmpty(text)) {
            String decodeEntertainToken = text.toString();
            if (decodeEntertainToken.startsWith("#娱号令#")) {
                decodeEntertainToken(decodeEntertainToken);
            }
        }
    }


    private void decodeEntertainToken(String entertainTokenStr) {

        User user = WangYuApplication.getUser(this);
        if (user == null) {
            createMatchDialog(1);
        } else {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userId", user.getId());
            params.put("token", user.getToken());
            params.put("entertainTokenStr", entertainTokenStr);
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DECODE_ENTERTAIN_TOKEN, params, HttpConstant.DECODE_ENTERTAIN_TOKEN);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        if ((curTime - lastClickBackTime) >= QUIT_CONFIRM_TIME) {
            lastClickBackTime = curTime;
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
        } else {
            if (!TextUtils.isEmpty(lengthCoding)) {
                outTime = DateUtil.getStringToday();
                postLogTime(lengthCoding, lengthTargetId);
                inTime = null;
                outTime = null;
            }
            AppManager.getAppManager().AppExit(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CITY_LIST && resultCode == RESULT_OK) {
            String lastCityStr = PreferencesUtil.getLastRecreationCity(this);
            City lastCity = GsonUtil.getBean(lastCityStr, City.class);
            tvCurrentCity.setText(lastCity.getName());
            if (fragmentList.get(0) != null) {
                ((FragmentMatch) fragmentList.get(0)).adapter.bannerRefresh = true;
                ((FragmentMatch) fragmentList.get(0)).loadData();
            }
        }
    }

    private void createSelectedCityDialog() {
        final Dialog selectedCityDialog = new Dialog(this);
        selectedCityDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_normal_layout, null);
        TextView tvMessage = (TextView) view.findViewById(R.id.message);
        Button btnSure = (Button) view.findViewById(R.id.positiveButton);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                City locationCity = Constant.locCity;
                Constant.currentCity = locationCity;
                tvCurrentCity.setText(locationCity.getName());
                if (fragmentList.get(0) != null) {
                    ((FragmentMatch) fragmentList.get(0)).adapter.bannerRefresh = true;
                    ((FragmentMatch) fragmentList.get(0)).loadData();
                }
                PreferencesUtil.setLastRecreationCity(new Gson().toJson(Constant.currentCity), getApplicationContext());
                selectedCityDialog.dismiss();
            }
        });
        Button btnCancel = (Button) view.findViewById(R.id.negativeButton);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCityDialog.dismiss();
            }
        });
        selectedCityDialog.setContentView(view);
        selectedCityDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        selectedCityDialog.setCanceledOnTouchOutside(false);
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setText("当前定位到的城市是" + Constant.locCity.getName() + ",是否切换到" + Constant.locCity.getName());
        selectedCityDialog.show();
    }

/*    private BroadcastReceiver liveReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent screenIntent = new Intent(MainActivity.this, ScreenRecorderActivity.class);
            startActivity(screenIntent);
            LogUtil.e(TAG,"------onreceiver---------");
        }
    };*/
}

