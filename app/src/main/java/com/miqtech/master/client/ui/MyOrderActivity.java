package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.MyPayOrderAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.OrderInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.ToastUtil;
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
 * Created by Administrator on 2015/12/7.
 */
public class MyOrderActivity extends BaseActivity implements View.OnClickListener, MyPayOrderAdapter.DealPayOrderListener,
        AdapterView.OnItemClickListener{

    private PullToRefreshListView prlvOrderlist;

    private HasErrorListView mOrderList;

    private MyPayOrderAdapter mPayOrderAdapter;

    private List<OrderInfo> mPayOrder;

    private Context mContext;// 父容器的上下文

    private int loadPage = 1;

    /**
     * 当前正在操作的订单的position
     */
    private int mCurrentPosition = -1;

    /**
     * 标识是否还有下一页 0：有 1：没有
     */
    private int mIsLastPage = 0;


    @Override
    protected void init() {
        setContentView(R.layout.fragment_order);
        mContext = this;
        initView();
//		initData();
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        LoadOrderData();
        PreferencesUtil.savePayStatue(this, false);
    }

    protected void initView() {
        prlvOrderlist = (PullToRefreshListView) findViewById(R.id.prlvOrderlist);
        prlvOrderlist.setMode(PullToRefreshBase.Mode.BOTH);
        mOrderList = prlvOrderlist.getRefreshableView();
        prlvOrderlist.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                loadPage = 1;
                LoadOrderData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if (mPayOrder.size() > 0) {
                    if (mIsLastPage == 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadPage++;
                                LoadOrderData();
                            }
                        }, 500);
                    } else {
                        showToast(mContext.getResources().getString(R.string.load_no));
                        prlvOrderlist.onRefreshComplete();
                    }
                } else {
                    prlvOrderlist.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
              if(!isHasNetWork){
                  showToast(getString(R.string.noNeteork));
              }
            }
        });
        mPayOrder = new ArrayList<OrderInfo>();

        mOrderList.setErrorView("太低调了,还没有下过任何订单");
        mPayOrderAdapter = new MyPayOrderAdapter(mPayOrder, mContext, this);
        mOrderList.setAdapter(mPayOrderAdapter);
        mOrderList.setOnItemClickListener(this);
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        setLeftIncludeTitle("我的订单");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        loadPage = 1;
        initData();
    }

    /**
     * 支付订单列表加载
     */
    public void LoadOrderData() {
        showLoading();
        User user = WangYuApplication.getUser(mContext);
        if (user!=null){
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("page", loadPage + "");
            map.put("pageSize", "10");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.PAY_ORDER_LIST, map, HttpConstant.PAY_ORDER_LIST);
        }

    }

    /**
     * 删除订单请求
     */
    private void DeleteOrder(String id) {
        if (TextUtils.isEmpty(id)) {
            ToastUtil.showToast("订单号获取发生错误", mContext);
            return;
        }
        showLoading();
        User user = WangYuApplication.getUser(mContext);
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("orderId", id);
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DELETE_ORDER, map, HttpConstant.DELETE_ORDER);
        }

    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        try {
            Object obj = null;
            if (object.has("object")) {
                obj = object.getString("object");
            }
            if (obj == null)
                return;
            if (method.equals(HttpConstant.PAY_ORDER_LIST)) {
                JSONObject dataObj = new JSONObject(obj.toString());
                mIsLastPage = dataObj.getInt("isLast");
                String orderList = dataObj.getString("list");
                if (TextUtils.isEmpty(orderList)) {
                    ToastUtil.showToast("没有订单数据返回", mContext);
                    return;
                }
                List<OrderInfo> _ReserveOrder = new Gson().fromJson(orderList, new TypeToken<List<OrderInfo>>() {
                }.getType());
                if (loadPage == 1)
                    mPayOrder.clear();
                mPayOrder.addAll(_ReserveOrder);
                if (loadPage == 1 && mPayOrder.size() == 0) {
                    mOrderList.setErrorShow(true);
                } else {
                    mOrderList.setErrorShow(false);
                }
                prlvOrderlist.onRefreshComplete();
                mPayOrderAdapter.notifyDataSetChanged();
            } else if (method.equals(HttpConstant.DELETE_ORDER)) {
                if (mCurrentPosition >= 0)
                    mPayOrder.remove(mCurrentPosition);
                mCurrentPosition = -1;
                ToastUtil.showToast("订单已删除", mContext);
                mPayOrderAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
        prlvOrderlist.onRefreshComplete();
        mOrderList.setErrorShow(false);
        ToastUtil.showToast(errMsg, mContext);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        prlvOrderlist.onRefreshComplete();
        mOrderList.setErrorShow(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
        }
    }

    @Override
    public void deleteOrder(String orderid, int position) {
        mCurrentPosition = position;
        DeleteOrder(orderid);
    }

    @Override
    public void continuePay(String orderid) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mPayOrder.isEmpty() || mPayOrder.size() - 1 < position) {
            return;
        }
        OrderInfo oi = mPayOrder.get(position);
        if (oi != null) {
            Intent intent = new Intent(mContext, PayOrderActivity.class);
            intent.putExtra("payId", oi.getOrder_id());
            intent.putExtra("isOrderList", 1);
            intent.putExtra("netbarId", oi.getNetbar_id());
            mContext.startActivity(intent);
        }
    }
}
