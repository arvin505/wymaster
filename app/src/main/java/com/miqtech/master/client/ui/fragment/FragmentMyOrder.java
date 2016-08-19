package com.miqtech.master.client.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.MyOrderMsgAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.MyMessage;
import com.miqtech.master.client.entity.MyMessageList;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.MainActivity;
import com.miqtech.master.client.ui.MyMessageActivity;
import com.miqtech.master.client.ui.PayOrderActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.view.DeleteView;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.RefreshLayout;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/4.
 */
public class FragmentMyOrder extends MyBaseFragment implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    PullToRefreshListView prlvOrderMsg;
    HasErrorListView lvOrder;
    private View mainView;
    private Context mContext;
    private boolean isFirst = true;

    private int page = 1;//当前分页所在页数（默认值1）
    private int pageSize = 10;//当前分页显示的行数（默认值10）
    private final int type = 1;//1.订单 2.活动 3.系统
    private int isAll = 0;//isAll=1时返回所有的数据,可以不传

    private int is_last;
    private User user;
    private MyOrderMsgAdapter adapter;
    private List<MyMessage> myMessageList = new ArrayList<MyMessage>();
    private MyMessageList bMyMessageList;
    private MyMessage msg;
    public DeleteView myDialog;
    private int listId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mainView == null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            mainView = inflater.inflate(R.layout.fragment_order_msg, null);
            mContext = inflater.getContext();
            initView();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirst) {
            loadOrderMessage();
            isFirst = false;
        }
    }

    private void initView() {
        myDialog = new DeleteView(mContext, R.style.delete_style, R.layout.delete_dialog);
        prlvOrderMsg = (PullToRefreshListView) mainView.findViewById(R.id.prlvOrderMsg);
        prlvOrderMsg.setMode(PullToRefreshBase.Mode.BOTH);
        prlvOrderMsg.setScrollingWhileRefreshingEnabled(true);
   //     mRefresh.setColorSchemeResources(R.color.cpb_complete_state_selector);
        lvOrder =prlvOrderMsg.getRefreshableView();
     //   lvOrder = (HasErrorListView) mainView.findViewById(R.id.lv_order_msg);

        lvOrder.setErrorView("太低调了,还没有任何订单消息");

        adapter = new MyOrderMsgAdapter(mContext, myMessageList);
        lvOrder.setAdapter(adapter);
        lvOrder.setOnItemClickListener(this);
        lvOrder.setOnItemClickListener(this);
        prlvOrderMsg.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                pageSize = 10;
                loadOrderMessage();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if (myMessageList.size() > 0) {
                    if (is_last == 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                page++;
                                loadOrderMessage();
                            }
                        }, 1000);
                    } else {
                        showToast(mContext.getResources().getString(R.string.load_no));
                        prlvOrderMsg.onRefreshComplete();
                    }
                } else {
                    prlvOrderMsg.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if(!isHasNetWork) {
                    showToast(getActivity().getResources().getString(R.string.noNeteork));
                }
            }
        });
//        mRefresh.setOnLoadListener(this);
//        mRefresh.setOnRefreshListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (myMessageList.isEmpty() || myDialog.isShowing() || myMessageList.size() - 1 < position) {
            return;
        }
        msg = myMessageList.get(position);
        Intent intent;
        if (msg.getIs_read() == 0) {
            msg.setIs_read(1);
            MainActivity.systemCount--;
            BroadcastController.sendUserChangeBroadcase(mContext);
            ((MyMessageActivity) mContext).refreMessage();
            adapter.notifyDataSetChanged();
            setIsReaded(msg);
        }
        intent = new Intent(mContext, PayOrderActivity.class);
        intent.putExtra("payId", msg.getObj_id() + "");
        intent.putExtra("isOrderList", 0);
        mContext.startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        listId = position;
        int[] location = new int[2];
        // 获取当前view在屏幕中的绝对位置
        // ,location[0]表示view的x坐标值,location[1]表示view的坐标值
        view.getLocationOnScreen(location);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        display.getMetrics(displayMetrics);
        WindowManager.LayoutParams params = myDialog.getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.y = display.getHeight() - location[1];
        myDialog.getWindow().setAttributes(params);
        myDialog.setCanceledOnTouchOutside(true); // 点击dialog区域之外的地方，dialog消失
        myDialog.setDialogOnclickInterface(new DeleteView.IDialogOnclickInterface() {
            @Override
            public void rightOnclick() {
                msg = myMessageList.get(position);
                deleteMsg(msg.getId());
                myDialog.dismiss();
            }

            @Override
            public void leftOnclick() {

            }
        });
        myDialog.show();
        return false;
    }
    public void loadOrderMessage() {
        showLoading();
        user = WangYuApplication.getUser(mContext);
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("page", page + "");
            map.put("pageSize", pageSize + "");
            map.put("type", type + "");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_MESSAGE, map, HttpConstant.MY_MESSAGE);
        } else {
            hideLoading();
            showToast("请登陆");
        }

    }

    private void setIsReaded(MyMessage message) {
        user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("type", message.getType() + "");
        map.put("msgId", message.getId() + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SET_MSG_READED, map, HttpConstant.SET_MSG_READED);
    }

    private void deleteMsg(String msgId) {
        showLoading();
        User user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("type", msg.getType() + "");
        map.put("msgId", msgId + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DEL_MESSAGE, map, HttpConstant.DEL_MESSAGE);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        hideLoading();
        try {
            if (method.equals(HttpConstant.MY_MESSAGE)) {
                if (0 == object.getInt("code") && object.has("object")) {
                    bMyMessageList = GsonUtil.getBean(object.getString("object"), MyMessageList.class);
                    is_last = bMyMessageList.getIsLast();
                    if (page == 1) {
                        myMessageList.clear();
                    }
                    myMessageList.addAll(bMyMessageList.getList());

                    if (page == 1 && myMessageList.size() == 0) {
                        lvOrder.setErrorShow(true);
                    } else {
                        lvOrder.setErrorShow(false);
                    }
                    prlvOrderMsg.onRefreshComplete();
                    adapter.notifyDataSetChanged();
                }
                if (HttpConstant.SET_MSG_READED.equals(method)) {
//                    msg.setIs_read(1);
//                    MainActivity.systemCount--;
//                    BroadcastController.sendUserChangeBroadcase(mContext);
//                    ((MyMessageActivity) mContext).refreMessage();
//                    adapter.notifyDataSetChanged();
                }
                if (method.equals(HttpConstant.DEL_MESSAGE)) {
                    if (msg.getIs_read() == 0) {
                        MainActivity.systemCount--;
                        BroadcastController.sendUserChangeBroadcase(mContext);
                        ((MyMessageActivity) mContext).refreMessage();
                    }
                    myMessageList.remove(listId);
                    if (myMessageList.size() == 0) {
                        page = 1;
                        pageSize = 10;
                        loadOrderMessage();
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        hideLoading();
        prlvOrderMsg.onRefreshComplete();
        lvOrder.setErrorShow(false);
        showToast(mContext.getResources().getString(R.string.noNeteork));
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        prlvOrderMsg.onRefreshComplete();
        lvOrder.setErrorShow(false);
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mainView;
    }

    @Override
    public void refreView() {
        page = 1;
        pageSize = 10;
        loadOrderMessage();
    }

    @Override
    public void onResume() {
        super.onResume();
        hideLoading();
    }
}
