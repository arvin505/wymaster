package com.miqtech.master.client.ui.basefragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.CompleteTask;
import com.miqtech.master.client.entity.Fans;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.http.RequestUtil;
import com.miqtech.master.client.http.ResponseListener;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.utils.ACache;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ShareToFriendsUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.view.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/19.
 */
public abstract class BaseFragment extends Fragment implements ResponseListener {
    protected RequestUtil mRequestUtil;
    protected Activity activity;
    protected View rootView;
    protected ACache mCache;
    protected Toast toast;
    LoadingDialog progressDialog = null;

    public String TAG = getClass().getSimpleName();
    public boolean isFirstCreate = false;//是否是第一次
    public boolean isViewPageFragment = false;
    public String isOnHiddenChanged = null;
    public String isSetUserVisibleHint = null;
    public static boolean isLogining = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isViewPageFragment) {
            inTime = DateUtil.getStringToday();
        }
        activity = getActivity();
        mCache = ACache.get(activity);
        mRequestUtil = RequestUtil.getInstance();
        isFirstCreate = true;
        if (lengthCoding != null) {
            BroadcastController.registerIsLeaveAppReceiver(activity, broadcastReceiverIsLeaveApp);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == rootView) {
            return onViewInit(inflater, container, savedInstanceState);
        } else {
            return rootView;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != rootView) {
            ViewGroup viewGroup = (ViewGroup) rootView.getParent();
            if (null != viewGroup) {
                viewGroup.removeView(rootView);
            }
        }
        mRequestUtil.removeTag(getClass().getName());
        if (ShareToFriendsUtil.mSharePopWindow != null) {
            ShareToFriendsUtil.mSharePopWindow = null;
        }
    }

    public abstract View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * 渐变动画
     *
     * @param contentView
     * @link http://hukai.me/android-training-course-in-chinese/animations/crossfade.html
     */
    protected void fadeInAnim(View contentView) {
        contentView.setAlpha(0.6f);
        contentView.setVisibility(View.VISIBLE);
        contentView.animate()
                .alpha(1f)
                .setDuration(getResources()
                        .getInteger(android.R.integer.config_mediumAnimTime));
    }


    @Override
    public void onPause() {
        super.onPause();
        mRequestUtil.cancleAllCallback(this);
    }

    /**
     * 后台请求
     *
     * @param url 后台地址
     * @params 请求参数*
     */
    protected void sendHttpPost(String url, Map<String, String> params, String method) {
        LogUtil.e("BaseActivity", "base url : " + url);
        if (params != null) {
            LogUtil.e("BaseActivity", "base params : " + params.toString());
        }
        mRequestUtil.excutePostRequest(url, params, this, method);
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
    }

    public void fixListViewHeight(AbsListView listView) {      // 如果没有设置数据适配器，则ListView没有子项，返回。
        try {
            ListAdapter listAdapter = listView.getAdapter();
            int totalHeight;
            if (listAdapter == null) {
                return;
            }
        /*for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listViewItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listViewItem.measure(0, 0);
            // 计算所有子项的高度和
            totalHeight += listViewItem.getMeasuredHeight();
        }*/
            View childView = listAdapter.getView(0, null, listView);
            childView.measure(0, 0);
            int childHeight = childView.getMeasuredHeight();
            totalHeight = childHeight * listAdapter.getCount();
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            // listView.getDividerHeight()获取子项间分隔符的高度
            // params.height设置ListView完全显示需要的高度
            params.height = totalHeight/* + (listView.getDividerHeight() * (listAdapter.getCount() - 1))*/;
            listView.setLayoutParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showToast(String msg) {
        if (getActivity() == null) {
            return;
        }

        if (msg == null) {
            msg = "";
        }
        if (toast == null) {
            toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public void showToast(int resId) {
        /**
         * 可能会出现fragment销毁，或者没有加载完成时
         * 调用出现出现 fragment not attached to Activity 的错误
         */
        try {
            if (toast == null) {
                toast = Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT);
            } else {
                toast.setText(getResources().getString(resId));
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 要显示的文本
     *
     * @param text
     */
    public void showLoading(String text) {
        if (null == getContext()) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = LoadingDialog.createDialog(getContext(), text);
            progressDialog.show();
        } else if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 显示Loading框
     * <p/>
     * 提示文本默认为“正在加载”
     */
    public void showLoading() {
        this.showLoading(null);
    }

    /**
     * 将显示的Loading框关闭
     */
    public void hideLoading() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LoadingDialog getProgressDialog() {
        return progressDialog;
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        try {
            hideLoading();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, "method" + method + "  result:" + object.toString());
        //具体业务子类实现
        if (object.has("extend")) {
            try {
                if (WangYuApplication.getUser(getContext()) != null) {
                    String extendStr = object.getString("extend");
                    showTaskCoins(extendStr);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        hideLoading();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        hideLoading();
        try {
            if (object.getInt("code") == -1 && !isLogining) {
                showToast("登录失效，请重新登录");
                Intent intent = new Intent();
                intent.setClass(getContext(), LoginActivity.class);
                isLogining = true;
                startActivityForResult(intent, 0);
                return;
            } else {
                showToast(object.getString("result"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
        Toast toast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
        ViewGroup toastView = (ViewGroup) toast.getView();
        ImageView imageCodeProject = new ImageView(getContext());
        imageCodeProject.setImageResource(R.drawable.coin_icon);
        toastView.addView(imageCodeProject, 0);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

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

    /**
     * 上传接口的模块编码(用来区别是哪个使用时长)
     */
    public String lengthCoding = null;
    /**
     * 上传接口的模块id(用来区别是哪个使用时长)
     */
    public String lengthTargetId = "";

    public boolean isLeaveAppFragment = false;
    public boolean isModuleFragment = false;


    private BroadcastReceiver broadcastReceiverIsLeaveApp = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!TextUtils.isEmpty(lengthCoding)) {
                if (!TextUtils.isEmpty(isOnHiddenChanged) || !TextUtils.isEmpty(isSetUserVisibleHint)) {
                    outTime = UMengStatisticsUtil.outTime;
//                    LogUtil.e("broadcastReceiverIsLeaveApp------basefragment------进入时间:", inTime + "---------------出去时间:" + outTime + "--------编码:" + lengthCoding);
                    postLogTime(lengthCoding, lengthTargetId);
                    inTime = DateUtil.getStringToday();
                    outTime = null;
                    isOnHiddenChanged = "";
                    isSetUserVisibleHint = "";
                }
            }
        }
    };

    @Override
    public void onStart() {
        if (!TextUtils.isEmpty(lengthCoding) && UMengStatisticsUtil.isLeaveApp) {
//            LogUtil.e("is30Seconds----------baseFragment---------onstart----时间:", is30Seconds + "");
            isLeaveAppFragment = true;
            is30Seconds = System.currentTimeMillis() - is30Seconds;
        }
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        if (!TextUtils.isEmpty(lengthCoding)) {
            is30Seconds = System.currentTimeMillis();
            outTime = DateUtil.getStringToday();
//            LogUtil.e("is30Seconds----------baseFragment---------onstop----时间:", is30Seconds + "");
        }
        super.onStop();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!TextUtils.isEmpty(lengthCoding)) {
            isOnHiddenChanged = hidden + "";
            if (hidden) {
                outTime = DateUtil.getStringToday();
                postLogTime(lengthCoding, lengthTargetId);
                inTime = null;
                outTime = null;
            } else {
                inTime = DateUtil.getStringToday();
            }
//            LogUtil.e("onHiddenChanged------------baseFragment-------------------编码:", lengthCoding + "资讯id:-----------" + lengthTargetId + "-----------------进入时间:" + inTime);
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDestroy() {
        if (isModuleFragment && !TextUtils.isEmpty(lengthCoding)) {
            outTime = DateUtil.getStringToday();
            postLogTime(lengthCoding, lengthTargetId);
            inTime = null;
            outTime = null;
        }
        super.onDestroy();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!TextUtils.isEmpty(lengthCoding)) {
            if (isVisibleToUser) {
                isSetUserVisibleHint = isVisibleToUser + "";
                inTime = DateUtil.getStringToday();
//                LogUtil.e("inTime--jsnlfcakwrjbfaikwrjbfgjwbnr", inTime + "");
            } else {
//                LogUtil.e("isFirstCreate", isFirstCreate + "");
                if (isFirstCreate) {
                    isFirstCreate = false;
                } else {
                    outTime = DateUtil.getStringToday();
                    postLogTime(lengthCoding, lengthTargetId);
                    inTime = null;
                    outTime = null;
                }
            }
//            LogUtil.e("setUserVisibleHint------------baseFragment-------------------编码", lengthCoding + "资讯id:-----------" + lengthTargetId + "-----------------进入时间:" + inTime);
        }
    }

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
        if (WangYuApplication.getUser(getActivity()) != null) {
            map.put("userId", WangYuApplication.getUser(getActivity()).getId());
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
        if (WangYuApplication.getUser(getActivity()) != null) {
            map.put("userId", WangYuApplication.getUser(getActivity()).getId());
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA_1 + HttpConstant.LOG_TIME, map, HttpConstant.LOG_TIME);
    }

}
