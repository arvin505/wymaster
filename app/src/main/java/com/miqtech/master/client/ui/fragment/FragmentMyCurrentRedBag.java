package com.miqtech.master.client.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.MyCurrentListAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.CardCompat;
import com.miqtech.master.client.entity.RedBag;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.PaymentActivity;
import com.miqtech.master.client.ui.baseactivity.RedbagBaseActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.LogUtil;
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

import static android.app.Activity.RESULT_OK;

/**
 * 当前红包
 * Created by Administrator on 2015/12/4.
 */
public class FragmentMyCurrentRedBag extends MyBaseFragment implements AdapterView.OnItemClickListener {

    private View mainView;

    private Context mContext;


    private PullToRefreshListView prlvMyCurrentBag;

    private HasErrorListView lvMyCurrentBag;

    private boolean isFirst = true;

    private MyCurrentListAdapter adapter;

    private List<RedBag> bags = new ArrayList<RedBag>();

    private int page = 1;

    private int pageSize = 20;

    private int isLast;

    private int type;

    private OnHeadlineSelectedListener mCallback;

    private String RED_BAG = "RED_BAG";

    private CardCompat mCardCompat;
    private int viewType = 0;
    private String netbarId;
    private float amount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
        viewType = getArguments().getInt("VIEW_TYPE");
        mCardCompat = getArguments().getParcelable("card");
        netbarId = getArguments().getString("netbarid");
        amount = getArguments().getFloat("amount");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_mycurrentbag, null);
            mContext = inflater.getContext();
            initView();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        ViewGroup parent = (ViewGroup) mainView.getParent();
        if (parent != null) {
            parent.removeAllViewsInLayout();
        }
        return mainView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirst) {
            isFirst = false;
            initData();
        }
    }

    private void initView() {
        // TODO Auto-generated method stub
        prlvMyCurrentBag = (PullToRefreshListView) mainView.findViewById(R.id.prlvMyCurrentBag);
        prlvMyCurrentBag.setMode(PullToRefreshBase.Mode.BOTH);
        lvMyCurrentBag = prlvMyCurrentBag.getRefreshableView();
        lvMyCurrentBag.setErrorView("没有可用红包");
        adapter = new MyCurrentListAdapter(mContext, bags, type);
        LogUtil.e("tag", "--------------------------redbat------------" + type);
        lvMyCurrentBag.setAdapter(adapter);
        if (viewType == 1) {
            adapter.setSelectedRedBag(mCardCompat);
        }
        prlvMyCurrentBag.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                loadMyCurrentRedBag();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if (bags.size() > 0) {
                    if (isLast == 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                page++;
                                loadMyCurrentRedBag();
                            }
                        }, 1000);
                    } else {
                        showToast(mContext.getResources().getString(R.string.load_no));
                        prlvMyCurrentBag.onRefreshComplete();
                    }

                } else {
                 prlvMyCurrentBag.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
             if(!isHasNetWork){
                 showToast(getActivity().getResources().getString(R.string.noNeteork));
             }
            }
        });
        lvMyCurrentBag.setOnItemClickListener(this);
        if (viewType == 0) {
            lvMyCurrentBag.setScrollListener(new HasErrorListView.Scrolllistener() {
                @Override
                public void onScroll(int l, int t, int oldl, int oldt) {
                    if (lvMyCurrentBag.getLastVisiblePosition() == lvMyCurrentBag.getAdapter().getCount() - 1) {
                        ((RedbagBaseActivity) getActivity()).hiddenBottom(false);
                    } else {
                        ((RedbagBaseActivity) getActivity()).hiddenBottom(true);
                    }
                }
            });
        }

    }

    private void initData() {
        loadMyCurrentRedBag();
    }

    private void loadMyCurrentRedBag() {
        showLoading();
        User user = WangYuApplication.getUser(mContext);
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());

            if (viewType == 1) {
                map.put("payAmount", mCardCompat.amount + "");
                map.put("netbarId", mCardCompat.netbarId);
            }
            map.put("page", page + "");
            map.put("pageSize", pageSize + "");
            if (netbarId != null) {
                map.put("netbarId", netbarId);
            }
            if (amount != 0f) {
                map.put("payAmount", amount + "");
            }
            if (type == 1) {
                sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.CURRENT_CAN_USE_REDBAG, map, RED_BAG);
            } else {
                sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.HISTORY_REDBAG, map, RED_BAG);
            }
        }
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();
/*        if (lvMyCurrentBag.getAdapter().getCount() == 0 || lvMyCurrentBag.getLastVisiblePosition() == lvMyCurrentBag.getAdapter().getCount() - 1) {
            ((RedbagBaseActivity) getActivity()).hiddenBottom(false);
        } else {
            ((RedbagBaseActivity) getActivity()).hiddenBottom(true);
        }*/
        if (lvMyCurrentBag.getLastVisiblePosition() == lvMyCurrentBag.getAdapter().getCount() - 1) {
            ((RedbagBaseActivity) getActivity()).hiddenBottom(false);
        } else {
            ((RedbagBaseActivity) getActivity()).hiddenBottom(true);
        }

    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        LogUtil.e("tag", "-----------object-----" + object.toString());
        try {
            Object obj = null;
            if (object.has("object")) {
                obj = object.getString("object");
            }
            if (method.equals(RED_BAG)) {
                if (obj == null) {
                    return;
                }
                JSONObject jsonList = new JSONObject(obj.toString());
                String strList = jsonList.getString("list");
                isLast = jsonList.getInt("isLast");
                List<RedBag> newBags = new Gson().fromJson(strList, new TypeToken<List<RedBag>>() {
                }.getType());
                if (page == 1) {
                    bags.clear();
                }
                bags.addAll(newBags);
                if (page == 1 && bags.size() == 0) {
                    lvMyCurrentBag.setErrorShow(true);
                } else {
                    lvMyCurrentBag.setErrorShow(false);
                }
               prlvMyCurrentBag.onRefreshComplete();
                adapter.notifyDataSetChanged();

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        ToastUtil.showToast("数据获取发生失败", mContext);
        lvMyCurrentBag.setErrorShow(false);
       prlvMyCurrentBag.onRefreshComplete();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        lvMyCurrentBag.setErrorShow(false);
      prlvMyCurrentBag.onRefreshComplete();
    }
    // 用来存放fragment的Activtiy必须实现这个接口
    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // 这是为了保证Activity容器实现了用以回调的接口。如果没有，它会抛出一个异常。
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
        if (bags.isEmpty() || bags.size() - 1 < position) {
            return;
        }
        if (viewType == 1 && type == 1) {
            RedBag redBag = bags.get(position);
            if (redBag.getPay_amount_canuse() == 0) {
                return;
            }
            if (mCardCompat != null && redBag.getId() == mCardCompat.id) {
                getActivity().finish();
            }
            mCardCompat.id = redBag.getId();
            mCardCompat.value = redBag.getMoney();
            mCardCompat.needValidate = redBag.getNeed_validate();
            mCardCompat.cardType = 0;

            Intent intent = new Intent();
            intent.putExtra("redBag", mCardCompat);
            intent.setClass(getActivity(), PaymentActivity.class);
            getActivity().setResult(RESULT_OK, intent);
            getActivity().finish();
        }
        mCallback.onArticleSelected(position);
    }

    @Override
    public void refreView() {
        page = 1;
        loadMyCurrentRedBag();
    }
}
