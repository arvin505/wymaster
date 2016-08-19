package com.miqtech.master.client.ui.baseactivity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bugtags.library.Bugtags;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.appmanager.AppManager;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.CompleteTask;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.http.RequestUtil;
import com.miqtech.master.client.http.ResponseListener;
import com.miqtech.master.client.jpush.service.PushType;
import com.miqtech.master.client.ui.CoinsTaskActivity;
import com.miqtech.master.client.ui.EventDetailActivity;
import com.miqtech.master.client.ui.ExchangeDetailActivity;
import com.miqtech.master.client.ui.GoldCoinsStoreActivity;
import com.miqtech.master.client.ui.InformationAtlasActivity;
import com.miqtech.master.client.ui.InformationDetailActivity;
import com.miqtech.master.client.ui.InformationTopicActivity;
import com.miqtech.master.client.ui.InternetBarActivityV2;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.MyMessageActivity;
import com.miqtech.master.client.ui.MyRedBagActivity;
import com.miqtech.master.client.ui.OfficalEventActivity;
import com.miqtech.master.client.ui.RecreationMatchDetailsActivity;
import com.miqtech.master.client.ui.ShopDetailActivity;
import com.miqtech.master.client.ui.SubjectActivity;
import com.miqtech.master.client.ui.YueZhanDetailsActivity;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ShareToFriendsUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.LoadingDialog;
import com.miqtech.master.client.view.MyAlertView;
import com.miqtech.master.client.view.MyToast;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2015/11/18 0018.
 */
public abstract class BaseActivity extends FragmentActivity implements ResponseListener {
    protected RequestUtil mRequestUtil;
    LoadingDialog progressDialog = null;
    private Toast toast = null;

    public static final boolean TRANSLUANTNAVAGATIONBAR = false;    //是否设置透明状态栏

    public RelativeLayout mRlToolbar;

    public String TAG = getClass().getSimpleName();

    public static final int UPDATE_VIEWS = 10;

    private boolean isLoginShowing = false;

    public final int WEIBOREQUESTCODE = 765;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        mRequestUtil = RequestUtil.getInstance();
        RequestUtil.init(getApplication());
        init();
        inTime = DateUtil.getStringToday();
        setTransluantNavationBar();
        if (lengthCoding != null) {
            BroadcastController.registerIsLeaveAppReceiver(this, broadcastReceiverIsLeaveApp);
        }
        distriBottom(getIntent());
    }

    protected void init() {

    }


    protected void initView() {

    }


    protected void initData() {

    }

    private void setTransluantNavationBar() {
        if (TRANSLUANTNAVAGATIONBAR) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        }
    }

    /**
     * 沉浸式状态栏重新计算高度
     */
    public void setToolbarHeight() {
        if (TRANSLUANTNAVAGATIONBAR) {
            mRlToolbar = (RelativeLayout) findViewById(R.id.rl_toolbar);
            if (mRlToolbar == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mRlToolbar.getLayoutParams();
                params.setMargins(0, Utils.getStatusHeight(this), 0, 0);
                mRlToolbar.setLayoutParams(params);
            }
        }
    }

    public void showToast(String msg) {
//        LogUtil.e("error", "msg == " + msg + "   class  " + getClass());
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public void showToast(int resid) {
        if (toast == null) {
            toast = Toast.makeText(this, resid, Toast.LENGTH_SHORT);
        } else {
            toast.setText(resid);
        }
        toast.show();
    }

    /**
     * 要显示的文本
     *
     * @param text
     */
    public void showLoading(String text) {
        if (progressDialog == null) {
            progressDialog = LoadingDialog.createDialog(WangYuApplication.appContext, text);
            progressDialog.show();
        } else if (!progressDialog.isShowing()) {
            try {
                progressDialog.show();
            } catch (AndroidRuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示Loading框
     * <p/>
     * 提示文本默认为“正在加载”
     */
    public void showLoading() {
        try {
            this.showLoading(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将显示的Loading框关闭
     */
    public void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * 设置头部的标题
     *
     * @param title 标题名称
     */
    protected void setLeftIncludeTitle(String title) {
        TextView text_title = (TextView) findViewById(R.id.tvLeftTitle);
        text_title.setVisibility(View.VISIBLE);
        text_title.setText(title);
    }

    /**
     * 获取左上角的按钮
     */
    protected ImageButton getLeftBtn() {
        ImageButton buttonLeft = (ImageButton) findViewById(R.id.ibLeft);
        buttonLeft.setVisibility(View.VISIBLE);
        return buttonLeft;
    }

    protected void setLeftBtnImage(int res) {
        ImageButton ibLeft = (ImageButton) findViewById(R.id.ibLeft);
        ibLeft.setImageResource(res);
        ibLeft.setVisibility(View.VISIBLE);
    }

    protected ImageButton getRightBtn() {
        ImageButton buttonRight = (ImageButton) findViewById(R.id.ibRight);
        buttonRight.setVisibility(View.VISIBLE);
        return buttonRight;
    }

    protected void setRightBtnImage(int res) {
        ImageButton buttonRight = (ImageButton) findViewById(R.id.ibRight);
        buttonRight.setImageResource(res);
        buttonRight.setVisibility(View.VISIBLE);
    }

    protected ImageButton getRightOtherBtn() {
        ImageButton buttonRight = (ImageButton) findViewById(R.id.ibRight1);
        buttonRight.setVisibility(View.VISIBLE);
        return buttonRight;
    }

    protected void setRightOtherBtnImage(int res) {
        ImageButton buttonRight = (ImageButton) findViewById(R.id.ibRight1);
        buttonRight.setImageResource(res);
        buttonRight.setVisibility(View.VISIBLE);
    }

    protected void setRightTextView(String title) {
        TextView tvRightHandle = (TextView) findViewById(R.id.tvRightHandle);
        tvRightHandle.setVisibility(View.VISIBLE);
        tvRightHandle.setText(title);
    }

    protected TextView getRightTextview() {
        TextView tvRightHandle = (TextView) findViewById(R.id.tvRightHandle);
        tvRightHandle.setVisibility(View.VISIBLE);
        return tvRightHandle;
    }

    protected View getButtomLineView() {
        View view = findViewById(R.id.buttom_line);
        view.setVisibility(View.VISIBLE);
        return view;
    }

    private boolean once = false;


    @Override
    protected void onPause() {
        super.onPause();
        Bugtags.onPause(this);
        if (!WangYuApplication.ISEMULATOR) {
            MobclickAgent.onPause(this);
        } /*else {
            showToast(WangYuApplication.USER_AGENT);
        }*/
        mRequestUtil.cancleAllCallback(this);
    }

    /**
     * 新浪微博回调方法在onNewIntent中调用，检查发现
     * 应用中并没有走noNewIntent，暂时并未找到原因，
     * 暂时使用此种折中方式处理
     * 已知存在bug：分享失败，以及取消分享，同样会调用此段代码
     * 导致没有成功分享也能得到金币奖励
     */
    public void sendWeiboShareTask() {
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(this);
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
            LogUtil.e("params", "params : " + params.toString());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AFTER_SHARE, params, HttpConstant.AFTER_SHARE);
            LogUtil.e("params", "params : " + params.toString());
        }
    }


    /**
     * @param resId 资源id
     *              重写findviewByid
     */
    protected <T> T findView(int resId) {
        return (T) super.findViewById(resId);
    }

    /**
     * 后台请求结果回调 success
     *
     * @param object
     */
    @Override
    public void onSuccess(JSONObject object, String method) {
        LogUtil.e(TAG, "requset : success " + method);
        LogUtil.e(TAG, "object : success " + object.toString());
        try {
            hideLoading();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, "method" + method + "  result:" + object.toString());
        //具体业务子类实现
        if (object.has("extend")) {
            try {
                String extendStr = object.getString("extend");
                showTaskCoins(extendStr);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 后台请求结果回调
     *
     * @param errMsg
     */
    @Override
    public void onError(String errMsg, String method) {
//        LogUtil.e("request", "requset : error " + getClass().getCanonicalName());
//        LogUtil.e("request", "requset : method " + method);
//        LogUtil.e("request", "requset : errMsg " + errMsg);
        hideLoading();
        //具体业务子类实现
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        hideLoading();
        LogUtil.e(TAG, "requset : faild " + method);
        LogUtil.e(TAG, "object : faild " + object.toString());
        try {
            if (object.getInt("code") == -1) {
                /*if (this instanceof  MainActivity){
                    ((MainActivity)this).loginOverdue();
                    return;
                }*/
                if (!isLoginShowing) {
                    showToast("登录失效，请重新登录");
                    Intent intent = new Intent();
                    LogUtil.e("login", "hererwrw .... " + method);
                    intent.setClass(this, LoginActivity.class);
                    startActivityForResult(intent, 6);
                    setLoginShowing(true);
                }
                return;
            } else {
                if (object.has("result") && !method.equals(HttpConstant.EXCLUSIVE_REDBAG) && !method.equals(HttpConstant.CORPS_SESSION_QRCODE)) {
                    showToast(object.getString("result"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setLoginShowing(boolean showing) {
        this.isLoginShowing = showing;
    }

    public boolean isLoginShowing() {
        return isLoginShowing;
    }

    /**
     * 后台请求
     *
     * @param url 后台地址
     * @params 请求参数*
     */
    protected void sendHttpPost(String url, Map<String, String> params, String method) {
        String urlStr = url;
        if (urlStr.contains("?")) {
            urlStr = urlStr.replace("?", "");
        }
        urlStr += "?";
        if (params != null && !params.isEmpty()) {
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry m = (Map.Entry) it.next();
                urlStr += m.getKey() + "=" + m.getValue() + "&";
            }
        }
        if (params != null && !params.isEmpty()) {
            urlStr = urlStr.substring(0, urlStr.length() - 1);
        }
        LogUtil.e(TAG, "-----------URL----------\n" + urlStr);
        mRequestUtil.excutePostRequest(url, params, this, method);
    }


    @Override
    public void finish() {
        AppManager.getAppManager().finishActivity(this);
    }

    public void superFinish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        mRequestUtil.removeTag(getClass().getName());
        LoadingDialog.removeDialog();
        if (ShareToFriendsUtil.mSharePopWindow != null) {
            ShareToFriendsUtil.mSharePopWindow = null;
        }
        if (ShareToFriendsUtil.shareDownloadBmp != null) {
            ShareToFriendsUtil.shareDownloadBmp.recycle();
        }

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        LogUtil.e("activity", "--result---" + getClass().getSimpleName());
        if (requestCode == WEIBOREQUESTCODE) {
            sendWeiboShareTask();
        }
        if (resultCode == LoginActivity.LOGIN_OK) {
            initData();
        }
    }


    private void showTaskCoins(String extendStr) {
        if (!TextUtils.isEmpty(extendStr) && (!extendStr.equals("{}"))) {
            try {
                JSONObject jsonObj = new JSONObject(extendStr);
                ArrayList<CompleteTask> tasks = new Gson().fromJson(jsonObj.getString("completeTasks"),
                        new TypeToken<List<CompleteTask>>() {
                        }.getType());
                if (tasks != null) {
                    for (int i = 0; i < tasks.size(); i++) {
                        CompleteTask currentTask = tasks.get(i);
                        int type = currentTask.getTaskType();
                        int currentTaskIdentify = currentTask.getTaskIdentify();
                        if (type == 1) {
                            switch (currentTaskIdentify) {
                                case Constant.OPEN_APP:
                                    showCoinToast("打开客户端     +" + currentTask.getCoin() + "金币");
                                    break;
                                case Constant.JOIN_YUEZHAN:
                                    showCoinToast("参加约战     +" + currentTask.getCoin() + "金币");
                                    break;
                                case Constant.RELEASE_YUEZHAN:
                                    showCoinToast("发布约战     +" + currentTask.getCoin() + "金币");
                                    break;
                                case Constant.RESERVE_NETBAR:
                                    showCoinToast("预定网吧     +" + currentTask.getCoin() + "金币");
                                    break;
                                case Constant.PAY_NETBAR:
                                    showCoinToast("支付上网费用     +" + currentTask.getCoin() + "金币");
                                    break;
                                case Constant.ATTENTION_OTHER:
                                    showCoinToast("关注他人     +" + currentTask.getCoin() + "金币");
                                    break;
                                case Constant.COLLECT_GAME:
                                    showCoinToast("收藏手游     +" + currentTask.getCoin() + "金币");
                                    break;
                                case Constant.SHARE:
                                    showCoinToast("分享     +" + currentTask.getCoin() + "金币");
                                    break;
                                default:
                                    break;
                            }
                        } else if (type == 2) {
                            switch (currentTaskIdentify) {
                                case Constant.NEWUSER_UPDATEDATA:
                                    showCoinToast("完善用户信息     +" + currentTask.getCoin() + "金币");
                                    break;
                                case Constant.NEWUSER_JOIN_MATCH:
                                    showCoinToast("完善参赛信息     +" + currentTask.getCoin() + "金币");
                                    break;
                                case Constant.NEWUSER_FIRST_PAY:
                                    showCoinToast("首次支付     +" + currentTask.getCoin() + "金币");
                                    break;
                                case Constant.NEWUSER_FIRST_RELEASE_YUEZHAN:
                                    showCoinToast("首次发布约战     +" + currentTask.getCoin() + "金币");
                                    break;
                                case Constant.NEWUSER_FIRST_EXCHANGE:
                                    showCoinToast("首次兑换商品     +" + currentTask.getCoin() + "金币");
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void showCoinToast(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageCodeProject = new ImageView(getApplicationContext());
        imageCodeProject.setImageResource(R.drawable.coin_icon);
        toastView.addView(imageCodeProject, 0);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    /**
     * 上传接口的模块编码(用来区别是哪个使用时长)
     */
    public String lengthCoding = null;
    /**
     * 上传接口的模块id(用来区别是哪个使用时长)
     */
    public String lengthTargetId = null;

    private boolean isReceiveBroadcast = false;//是否接收过离开app后别的activity上传埋点数据的广播

    /**
     * 判断是否超过30秒
     */
    public double is30Seconds;
    /**
     * 进入时间
     */
    public String inTime;
    /**
     * 出去时间
     */
    public String outTime;

    @Override
    public void onBackPressed() {
        //    super.onBackPressed();
        if (!TextUtils.isEmpty(lengthCoding)) {
            outTime = DateUtil.getStringToday();
//            LogUtil.e("outTime-----------onBackPressed--------------------", outTime);
//            LogUtil.e("lengthCoding-----------onBackPressed--------------------", lengthCoding);
            postLogTime(lengthCoding, lengthTargetId);
            inTime = null;
            outTime = null;
        }
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!WangYuApplication.ISEMULATOR) {
            MobclickAgent.onResume(this);
        } /*else {
            showToast(WangYuApplication.USER_AGENT);
        }*/
        Bugtags.onResume(this);
        if (!once) {
            setToolbarHeight();
            once = true;
        }
//        LogUtil.e("isLeaveApp-----------onResume--------------------", UMengStatisticsUtil.isLeaveApp + "");
        if (UMengStatisticsUtil.isLeaveApp) {//离开过app后才继续往下判断是否超过30秒，是否去上传
            is30Seconds = System.currentTimeMillis() - is30Seconds;
//            LogUtil.e("is30Seconds-----------onResume--------------------", is30Seconds + "");
            if (is30Seconds > UMengStatisticsUtil.LEAVER_APP_TIME) {
                BroadcastController.sendIsLeaveAppReceive(this);
                if (!TextUtils.isEmpty(lengthCoding)) {
//                    postLogTime(lengthCoding, lengthTargetId);
                    is30Seconds = System.currentTimeMillis();
                    inTime = DateUtil.getStringToday();
//                    LogUtil.e("inTime-----------onResume--------------------", inTime);
                    outTime = null;
                }
            }
            UMengStatisticsUtil.isLeaveApp = false;
        }

        if (isReceiveBroadcast) {
            if (lengthCoding != UMengStatisticsUtil.CODE_0000) {
                inTime = DateUtil.getStringToday();
            }
            isReceiveBroadcast = false;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Bugtags.onDispatchTouchEvent(this, ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!TextUtils.isEmpty(lengthCoding) && !isRunningForeground(this)) {
            is30Seconds = System.currentTimeMillis();
            outTime = DateUtil.getStringToday();
            UMengStatisticsUtil.outTime = DateUtil.getStringToday();
//            LogUtil.e("outTime-----------onStop--------------------", outTime);
//            LogUtil.e("is30Seconds-----------onStop--------------------", is30Seconds + "");
            UMengStatisticsUtil.isLeaveApp = true;
        }
    }

    private BroadcastReceiver broadcastReceiverIsLeaveApp = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!TextUtils.isEmpty(lengthCoding)) {
                isReceiveBroadcast = true;
                outTime = UMengStatisticsUtil.outTime;
//                LogUtil.e("broadcastReceiverIsLeaveApp------------", inTime + "---------------" + outTime);
                postLogTime(lengthCoding, lengthTargetId);
                is30Seconds = System.currentTimeMillis();
                inTime = null;
                if (lengthCoding == UMengStatisticsUtil.CODE_0000) {
                    inTime = DateUtil.getStringToday();
                }
                outTime = null;
            }
        }
    };

    /**
     * 埋点
     *
     * @param code     类型,必传
     * @param targetId 目标id 资讯那边要传的
     */
    public void postLogTime(String code, String targetId) {
        Map<String, String> map = new HashMap<>();
        map.put("code", code);//类型,必传
        if (!TextUtils.isEmpty(targetId)) {
            map.put("targetId", targetId);//	目标id
        }
        map.put("inTime", inTime);//时间,单位秒,必传
        map.put("outTime", outTime);//时间,单位秒,必传
        if (WangYuApplication.getUser(getApplicationContext()) != null) {
            map.put("userId", WangYuApplication.getUser(getApplicationContext()).getId());
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA_1 + HttpConstant.LOG_TIME, map, HttpConstant.LOG_TIME);
    }

    /**
     * 埋点
     *
     * @param code       类型,必传
     * @param inTimeTwo  开始时间
     * @param outTimeTwo 结束时间
     * @param targetId   目标id 资讯那边要传的
     */
    public void postLogTime(String code, String inTimeTwo, String outTimeTwo, String targetId) {
        Map<String, String> map = new HashMap<>();
        map.put("code", code);//类型,必传
        if (!TextUtils.isEmpty(targetId)) {
            map.put("targetId", targetId);//	目标id
        }
        map.put("inTime", inTimeTwo);//时间,单位秒,必传
        map.put("outTime", outTimeTwo);//时间,单位秒,必传
        if (WangYuApplication.getUser(getApplicationContext()) != null) {
            map.put("userId", WangYuApplication.getUser(getApplicationContext()).getId());
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA_1 + HttpConstant.LOG_TIME, map, HttpConstant.LOG_TIME);
    }

    /**
     * 判断app是否在前台运行
     *
     * @param context
     * @return
     */
    public boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (currentPackageName != null && currentPackageName.equals(getPackageName())) {
            return true;
        }
        return false;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            String FRAGMENTS_TAG = "android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getIntExtra("bottom", -1) == 1) {
            distriBottom(intent);
        }
    }


    /**
     * @param intent
     */
    private void distriBottom(final Intent intent) {
        final int pushType = intent.getIntExtra("channel", -1);
        String alert = intent.getStringExtra("alert");
        if (pushType != -1) {
            MyToast myToast = MyToast.MakeText(this, alert, pushType, intent, MyToast.LENGTH_LONG);
            myToast.show();
        }
    }

}
