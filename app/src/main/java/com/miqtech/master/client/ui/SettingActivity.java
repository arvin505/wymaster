package com.miqtech.master.client.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.entity.VersionInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.service.DownloadService;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.ABTextUtil;
import com.miqtech.master.client.utils.FileSizeUtil;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.screenrecorder.core.ScreenRecorder;
import com.screenrecorder.ui.ScreenRecorderActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/8.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.invate_setting)
    RelativeLayout invateCode;
    @Bind(R.id.privacy_setting)
    RelativeLayout privacy;
    @Bind(R.id.service_setting)
    RelativeLayout service;
    @Bind(R.id.cache_setting)
    RelativeLayout clearCache;
    @Bind(R.id.feedback_setting)
    RelativeLayout feedback;
    @Bind(R.id.marking_setting)
    RelativeLayout marking;
    @Bind(R.id.about_setting)
    RelativeLayout aboutUs;
    @Bind(R.id.update_setting)
    RelativeLayout update;
    @Bind(R.id.cache_number_setting)
    TextView cacheNum;
    @Bind(R.id.rlLivePlay)
    RelativeLayout  rlLivePlay;
    @Bind(R.id.exit_bt)
    Button exit_bt;

    @Bind(R.id.host_setting)
    RelativeLayout hostSetting;
    @Bind(R.id.rlRecorder)
    RelativeLayout rlRecorder;

    private WangYuApplication app;


    private Context mContext;
    private User user;
    private File cacheFile;
    private String cacheCount;

    private boolean isBinded;

    private DownloadService.DownloadBinder binder;


    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mContext = SettingActivity.this;
        setLeftIncludeTitle(getResources().getString(R.string.setting));
        setLeftBtnImage(R.drawable.back);

        if (LogUtil.debug) {
            hostSetting.setVisibility(View.VISIBLE);
        } else {
            hostSetting.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (WangYuApplication.getUser(mContext) == null) {
            exit_bt.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        super.initData();

        cacheFile = ImageLoader.getInstance().getDiscCache().getDirectory();
        cacheCount = FileSizeUtil.getAutoFileOrFilesSize(cacheFile);
        cacheNum.setText(cacheCount);

        app = (WangYuApplication) getApplication();


        getLeftBtn().setOnClickListener(this);
        invateCode.setOnClickListener(this);
        privacy.setOnClickListener(this);
        service.setOnClickListener(this);
        clearCache.setOnClickListener(this);
        feedback.setOnClickListener(this);
        marking.setOnClickListener(this);
        aboutUs.setOnClickListener(this);
        exit_bt.setOnClickListener(this);
        update.setOnClickListener(this);
        rlLivePlay.setOnClickListener(this);

        hostSetting.setOnClickListener(this);
        rlRecorder.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.invate_setting:
                invateDialog();
                break;
            case R.id.privacy_setting:
                user = WangYuApplication.getUser(mContext);
                if (user == null) {
                    mLogin();
                } else {
                    intent = new Intent(mContext, IntimacySettingActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.service_setting:
                intent = new Intent(mContext, SubjectActivity.class);
                intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.CUSTOMER);
                startActivity(intent);
                break;
            case R.id.cache_setting:
                clearCacheDialog();
                break;
            case R.id.feedback_setting:
                intent = new Intent(mContext, FeedBackActivity.class);
                startActivity(intent);
                break;
            case R.id.marking_setting:
                break;
            case R.id.about_setting:
                intent = new Intent(mContext, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.exit_bt:
                exit_ID();
                break;
            case R.id.update_setting:
                if (!app.isDownload()) {
                    sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.UPDATE_VERSION, null, HttpConstant.UPDATE_VERSION);
                    showLoading();
                } else {
                    showToast("正在更新请稍等");
                }
                break;
            case R.id.host_setting:  //host设置
                intent = new Intent(mContext, HostSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.rlLivePlay:
                intent=new Intent(mContext, LivePlayLineSelectActivity.class);
                startActivity(intent);
                break;
            case R.id.rlRecorder:
                intent = new Intent(mContext, ScreenRecorderActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        //try {

        if (method.equals(HttpConstant.INVITATION)) {
            showToast("填写成功");
        } else if (method.equals(HttpConstant.UPDATE_VERSION)) {
            initUpdate(object);
        }

//        }catch (JSONException e){
//            e.printStackTrace();
//        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
        showToast(errMsg);
    }

    private void initUpdate(JSONObject object) {
        if (object.has("object")) {
            try {
                String objectStr = object.getString("object");
                VersionInfo versionInfo = new Gson().fromJson(objectStr, VersionInfo.class);
                int localVersonCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
                if (versionInfo.getVersionCode() > localVersonCode) {
                    createUpdateDialog(versionInfo);
                } else {
                    showToast("当前已是最新版本");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void createUpdateDialog(final VersionInfo versionInfo) {
        final AlertDialog updateDialog = new AlertDialog.Builder(mContext).create();
        updateDialog.show();
        updateDialog.setCancelable(true);
        updateDialog.setCanceledOnTouchOutside(true);
        Window window = updateDialog.getWindow();
        WindowManager.LayoutParams lp =
                updateDialog.getWindow().getAttributes();
        window.setContentView(R.layout.layout_selectoralert_dialog);
        lp.width = (int) (WangYuApplication.WIDTH - getResources().getDimension(R.dimen.dialog_margin) * 2);
        lp.height = lp.WRAP_CONTENT;
        window.setAttributes(lp);
        TextView tvContent = (TextView) updateDialog.findViewById(R.id.tvContent);
        TextView tvCancel = (TextView) updateDialog.findViewById(R.id.tvCancel);
        TextView tvSure = (TextView) updateDialog.findViewById(R.id.tvSure);
        tvContent.setText("检测到当前版本已更新，确认下载?");
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.dismiss();
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceStart(versionInfo);
                updateDialog.dismiss();
            }
        });

    }

    private void serviceStart(VersionInfo version) {
        Intent it = new Intent(SettingActivity.this, DownloadService.class);
        bindService(it, conn, Context.BIND_AUTO_CREATE);
        it.putExtra("apkUrl", version.getUrl());
        startService(it);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        try {
            if (method.equals(HttpConstant.INVITATION)) {
                if (object.has("result")) {
                    showToast(object.getString("result"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void clearCacheDialog() {
        final Dialog mDialog = new Dialog(mContext, R.style.register_style);
        mDialog.setContentView(R.layout.dialog_register_marked_words);
        TextView title_tv = (TextView) mDialog.findViewById(R.id.dialog_title_register);
        TextView context_tv = (TextView) mDialog.findViewById(R.id.dialog_content_register);
        TextView ok_bt = (TextView) mDialog.findViewById(R.id.dialog_register_yes_pact);
        TextView no_bt = (TextView) mDialog.findViewById(R.id.dialog_register_no_pact);
        View vv = mDialog.findViewById(R.id.dialog_line_no_pact);
        vv.setVisibility(View.VISIBLE);
        no_bt.setVisibility(View.VISIBLE);

        title_tv.setText(getResources().getString(R.string.show));
        title_tv.setVisibility(View.VISIBLE);

        context_tv.setText(getResources().getString(R.string.whether_to_clear_the_cache));
        context_tv.setVisibility(View.VISIBLE);

        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageLoader.getInstance().getDiscCache().clear();
                cacheFile = ImageLoader.getInstance().getDiscCache().getDirectory();
                cacheCount = FileSizeUtil.getAutoFileOrFilesSize(cacheFile);
                cacheNum.setText(cacheCount);
                mDialog.dismiss();
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


    private void invateDialog() {
        final Dialog mDialog = new Dialog(mContext, R.style.register_style);
        mDialog.setContentView(R.layout.dialog_register_marked_words);
        TextView title_tv = (TextView) mDialog.findViewById(R.id.dialog_title_register);
        TextView ok_bt = (TextView) mDialog.findViewById(R.id.dialog_register_yes_pact);
        TextView no_bt = (TextView) mDialog.findViewById(R.id.dialog_register_no_pact);
        final EditText invateCode_et = (EditText) mDialog.findViewById(R.id.dialog_content_register_et);
        invateCode_et.setVisibility(View.VISIBLE);
        View vv = mDialog.findViewById(R.id.dialog_line_no_pact);
        vv.setVisibility(View.VISIBLE);
        no_bt.setVisibility(View.VISIBLE);

        title_tv.setText(getResources().getString(R.string.please_imput_invate_code));
        title_tv.setVisibility(View.VISIBLE);
        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = invateCode_et.getText().toString().trim();
                if (!ABTextUtil.isEmpty(code) && ABTextUtil.isInteger(code)) {
                    setInvitation(code);
                    mDialog.dismiss();
                } else {
                    showToast(getResources().getString(R.string.invate_code_is_error));
                }
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
     * 设置邀请码
     */
    private void setInvitation(String code) {
        showLoading();
        user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
        }
        map.put("invitationCode", code);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INVITATION, map, HttpConstant.INVITATION);
    }

    private void mLogin() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
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

    private ICallbackResult callback = new ICallbackResult() {
        @Override
        public void OnBackResult(Object result) {
            if ("finish".equals(result)) {
                finish();
                return;
            }
            int i = (Integer) result;
        }
    };

    public interface ICallbackResult {
        public void OnBackResult(Object result);
    }

    private void exit_ID() {
        final Dialog mDialog = new Dialog(mContext, R.style.register_style);
        mDialog.setContentView(R.layout.dialog_register_marked_words);
        TextView title_tv = (TextView) mDialog.findViewById(R.id.dialog_title_register);
        TextView context_tv = (TextView) mDialog.findViewById(R.id.dialog_content_register);
        TextView ok_bt = (TextView) mDialog.findViewById(R.id.dialog_register_yes_pact);
        TextView no_bt = (TextView) mDialog.findViewById(R.id.dialog_register_no_pact);
        View vv = mDialog.findViewById(R.id.dialog_line_no_pact);
        vv.setVisibility(View.VISIBLE);
        no_bt.setVisibility(View.VISIBLE);

        title_tv.setText(getResources().getString(R.string.exit_from_the_current_account));
        title_tv.setVisibility(View.VISIBLE);

        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WangYuApplication.clearUser();
                PreferencesUtil.setUser(mContext, "");
                showToast(getResources().getString(R.string.exit_success));
                BroadcastController.sendUserChangeBroadcase(mContext);
                finish();
                mDialog.dismiss();
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
}
