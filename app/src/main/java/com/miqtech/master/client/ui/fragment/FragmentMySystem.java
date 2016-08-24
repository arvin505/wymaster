package com.miqtech.master.client.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.MyMsgAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.EventAgainstDetail;
import com.miqtech.master.client.entity.MyMessage;
import com.miqtech.master.client.entity.MyMessageList;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.EventDetailActivity;
import com.miqtech.master.client.ui.ExchangeDetailActivity;
import com.miqtech.master.client.ui.InternetBarActivityV2;
import com.miqtech.master.client.ui.MainActivity;
import com.miqtech.master.client.ui.MsgTypeSystemDetailActivity;
import com.miqtech.master.client.ui.MyMessageActivity;
import com.miqtech.master.client.ui.MyRedBagActivity;
import com.miqtech.master.client.ui.PersonalCommentDetail;
import com.miqtech.master.client.ui.RewardActivity;
import com.miqtech.master.client.ui.ShopDetailActivity;
import com.miqtech.master.client.ui.ZifaMatchListActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.TimeUtil;
import com.miqtech.master.client.utils.ToastUtil;
import com.miqtech.master.client.view.DeleteView;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.PullableListView;
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
public class FragmentMySystem extends MyBaseFragment implements AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener {

    private View mainView;

    private Context mContext;

    private PullToRefreshListView prlvMyMsg;

    private HasErrorListView lvSystem;

    private MyMsgAdapter adapter;
    private int page = 1;//当前分页所在页数（默认值1）
    private int pageSize = 10;//当前分页显示的行数（默认值10）
    private final int type = 3;//1.订单 2.活动 3.系统 4评论
    private int isAll = 0;//isAll=1时返回所有的数据,可以不传
    private int is_last;
    private User user;
    private List<MyMessage> messges = new ArrayList<MyMessage>();
    private MyMessageList bMyMessageList;
    public boolean isFirst = true;
    private MyMessage msg;
    private DeleteView myDialog;
    private int listId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.message_list, null);
            mContext = inflater.getContext();
            initView();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup parent = (ViewGroup) mainView.getParent();
        if (parent != null) {
            parent.removeAllViewsInLayout();
        }
        return mainView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirst) {
            isFirst = false;
            loadMyReleaseWar();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideLoading();
    }

    private void initView() {
        myDialog = new DeleteView(mContext, R.style.delete_style, R.layout.delete_dialog);
        prlvMyMsg = (PullToRefreshListView) mainView.findViewById(R.id.prlvMyMsg);
        prlvMyMsg.setMode(PullToRefreshBase.Mode.BOTH);
        lvSystem = prlvMyMsg.getRefreshableView();
        lvSystem.setErrorView("太低调了,还没有收到任何系统消息");

        adapter = new MyMsgAdapter(mContext, messges, MyMessageActivity.SELECT_SYSTEM);
        lvSystem.setAdapter(adapter);
        prlvMyMsg.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                pageSize = 10;
                loadMyReleaseWar();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if (messges.size() > 0) {
                    if (is_last == 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                page++;
                                loadMyReleaseWar();
                            }
                        }, 1000);
                    } else {
                        showToast(mContext.getResources().getString(R.string.load_no));
                        prlvMyMsg.onRefreshComplete();
                    }
                } else {
                    prlvMyMsg.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getActivity().getResources().getString(R.string.noNeteork));
                }
            }
        });
        lvSystem.setOnItemClickListener(this);
        lvSystem.setOnItemLongClickListener(this);
    }

    private void initData() {
        adapter.notifyDataSetChanged();
    }

    private void loadMyReleaseWar() {
        showLoading();
        User user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("page", page + "");
        map.put("pageSize", pageSize + "");
        map.put("type", type + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_MESSAGE, map, HttpConstant.MY_MESSAGE);
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

    private void setIsReaded(MyMessage message) {
        showLoading();
        user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("type", message.getType() + "");
        map.put("msgId", message.getId() + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SET_MSG_READED, map, HttpConstant.SET_MSG_READED);
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mainView;
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        hideLoading();
        try {
            if (method.equals(HttpConstant.MY_MESSAGE)) {
                if (object == null) {
                    return;
                }
                bMyMessageList = GsonUtil.getBean(object.getString("object"), MyMessageList.class);
//                is_last = bMyMessageList.getIsLast();
                if (page == 1) {
                    messges.clear();
                }
                if (bMyMessageList != null && bMyMessageList.getList() != null) {
                    is_last = bMyMessageList.getIsLast();
                    messges.addAll(bMyMessageList.getList());
                }
                if (page == 1 && messges.size() == 0) {
                    lvSystem.setErrorShow(true);
                } else {
                    lvSystem.setErrorShow(false);
                }
                initData();
            }
            if (HttpConstant.SET_MSG_READED.equals(method)) {
//                msg.setIs_read(1);
//                MainActivity.systemCount--;
//                BroadcastController.sendUserChangeBroadcase(mContext);
//                ((MyMessageActivity) mContext).refreMessage();
//                adapter.notifyDataSetChanged();
                //loadMyReleaseWar();
            }
            if (method.equals(HttpConstant.DEL_MESSAGE)) {
                if (msg.getIs_read() == 0) {
                    MainActivity.systemCount--;
                    BroadcastController.sendUserChangeBroadcase(mContext);
                    ((MyMessageActivity) mContext).refreMessage();
                }
                messges.remove(listId);
                if (messges.size() == 0) {
                    page = 1;
                    pageSize = 10;
                    loadMyReleaseWar();
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        prlvMyMsg.onRefreshComplete();
    }

    @Override
    public void onError(String errMsg, String method) {
        hideLoading();
        prlvMyMsg.onRefreshComplete();
        lvSystem.setErrorShow(false);
        ToastUtil.showToast(errMsg, mContext);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        prlvMyMsg.onRefreshComplete();
        lvSystem.setErrorShow(false);
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
                msg = messges.get(position);
                deleteMsg(msg.getId());
                myDialog.dismiss();
            }

            @Override
            public void leftOnclick() {
                // TODO Auto-generated method stub
            }
        });
        myDialog.show();
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (myDialog.isShowing() || messges.isEmpty() || messges.size() - 1 < position) {
            return;
        }
        Intent intent;
        msg = messges.get(position);
        if (msg.getIs_read() == 0) {
            msg.setIs_read(1);
            MainActivity.systemCount--;
            BroadcastController.sendUserChangeBroadcase(mContext);
            ((MyMessageActivity) mContext).refreMessage();
            adapter.notifyDataSetChanged();
            setIsReaded(msg);
        }
        switch (msg.getType()) {
            case MyMessageActivity.MsgType_System:
                intent = new Intent(mContext, MsgTypeSystemDetailActivity.class);
                intent.putExtra("title", msg.getTitle());
                intent.putExtra("detail", msg.getContent());
                intent.putExtra("date", TimeUtil.friendlyTime(msg.getCreate_date()));
                mContext.startActivity(intent);
                break;
            case -1:
                intent = new Intent(mContext, MsgTypeSystemDetailActivity.class);
                intent.putExtra("title", msg.getTitle());
                intent.putExtra("detail", msg.getContent());
                intent.putExtra("date", TimeUtil.friendlyTime(msg.getCreate_date()));
                mContext.startActivity(intent);
                break;
            case MyMessageActivity.MsgType_Member:
                intent = new Intent();
                intent.setClass(mContext, InternetBarActivityV2.class);
                // 封装数据
                Bundle bundle = new Bundle();
                bundle.putString("netbarId", msg.getObj_id() + "");
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                break;
            case MyMessageActivity.MsgType_Redbag:
                intent = new Intent(mContext, MyRedBagActivity.class);
                mContext.startActivity(intent);
                break;
            case MyMessageActivity.MsgType_Praise:
                intent = new Intent(mContext, PersonalCommentDetail.class);
                intent.putExtra("parentId", msg.getObj_id() + "");
                mContext.startActivity(intent);
                break;
            case MyMessageActivity.MsgType_Exchange:
                intent = new Intent(mContext, ExchangeDetailActivity.class);
                intent.putExtra("exchangeID", msg.getObj_id() + "");
                mContext.startActivity(intent);
                break;
            case MyMessageActivity.MsgType_ShopDetail:
                intent = new Intent(mContext, ShopDetailActivity.class);
                intent.putExtra("id", msg.getObj_id() + "");
                mContext.startActivity(intent);
                break;
            case MyMessageActivity.MsgType_OET://自发赛
                intent = new Intent(mContext, EventDetailActivity.class);
                intent.putExtra("matchId", msg.getObj_id() + "");
                mContext.startActivity(intent);
                break;
            case MyMessageActivity.MsgType_REWARD://悬赏令
                intent = new Intent(mContext, RewardActivity.class);
                intent.putExtra("rewardId", msg.getObj_id());
                intent.putExtra("isEnd", "1");
                mContext.startActivity(intent);
                break;
        }
    }

    @Override
    public void refreView() {
        showLoading();
        page = 1;
        pageSize = 10;
        loadMyReleaseWar();
    }
}
