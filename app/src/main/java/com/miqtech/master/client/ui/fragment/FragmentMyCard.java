package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.MyCardListAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.Card;
import com.miqtech.master.client.entity.CardCompat;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.CardDetailActivity;
import com.miqtech.master.client.ui.PaymentActivity;
import com.miqtech.master.client.ui.baseactivity.RedbagBaseActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ToastUtil;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshListView;
import com.miqtech.master.client.watcher.Observerable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * 历史红包
 * Created by Administrator on 2015/12/4.
 */
public class FragmentMyCard extends MyBaseFragment implements Observerable.ISubscribe {

    private View mainView;

    private Context mContext;

    private String CARD = "CARD";

    private PullToRefreshListView prlvMyHistoryBag;

    private HasErrorListView lvMyHistoryBag;

    private boolean isFirst = true;

    private MyCardListAdapter adapter;

    private List<Card> bags = new ArrayList<Card>();

    private Observerable mWatcher;

    private int page = 1;

    private int pageSize = 20;

    private int isLast;

    private int type;

    private CardCompat mCardCompat;
    private int viewType = 0;
    private String netbarId;
    private float amount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        type = getArguments().getInt("type");
        viewType = getArguments().getInt("VIEW_TYPE");
        mCardCompat = getArguments().getParcelable("card");
        netbarId = getArguments().getString("netbarid");
        amount = getArguments().getFloat("amount");
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_myhistorybag, null);
            mContext = inflater.getContext();
            initView();
        }
        mWatcher = Observerable.getInstance();
        mWatcher.subscribe(Observerable.ObserverableType.CARDUSE, this);
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
        prlvMyHistoryBag = (PullToRefreshListView) mainView.findViewById(R.id.prlvMyHistoryBag);
        prlvMyHistoryBag.setMode(PullToRefreshBase.Mode.BOTH);
        lvMyHistoryBag = prlvMyHistoryBag.getRefreshableView();

        lvMyHistoryBag.setErrorView("还没有卡券，赶紧参加网娱大师活动赢取精美兑换券吧");
        adapter = new MyCardListAdapter(mContext, bags, type);
        if (viewType == 1 && type == 1) {
            adapter.setSelectedRedBag(mCardCompat);
        }
        LogUtil.e(TAG, "type : " + type);
        lvMyHistoryBag.setAdapter(adapter);
        prlvMyHistoryBag.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
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
                        prlvMyHistoryBag.onRefreshComplete();
                    }
                } else {
                    prlvMyHistoryBag.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getActivity().getResources().getString(R.string.noNeteork));
                }
            }
        });
        lvMyHistoryBag.setScrollListener(new HasErrorListView.Scrolllistener() {
            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
                if (lvMyHistoryBag.getLastVisiblePosition() == lvMyHistoryBag.getAdapter().getCount() - 1) {
                    ((RedbagBaseActivity) getActivity()).hiddenBottom(false);
                } else {
                    ((RedbagBaseActivity) getActivity()).hiddenBottom(true);
                }
            }
        });

        lvMyHistoryBag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < bags.size()) {

                    if (viewType == 1 && type == 1) {
                        Card redBag = bags.get(position);
                        if (redBag.getEnabled() == 0) {
                            return;
                        }
                        if (mCardCompat != null && redBag.getId() == mCardCompat.id) {
                            getActivity().finish();
                        }
                        mCardCompat.id = redBag.getId();
                        mCardCompat.value = redBag.getAmount();

                        mCardCompat.cardType = 1;
                        Intent intent = new Intent();
                        intent.putExtra("redBag", mCardCompat);
                        intent.setClass(getActivity(), PaymentActivity.class);
                        getActivity().setResult(RESULT_OK, intent);
                        getActivity().finish();
                    } else {
                        Intent intent = new Intent(getContext(), CardDetailActivity.class);
                        intent.putExtra("card", bags.get(position));
                        intent.putExtra("type", type);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void initData() {
        loadMyCurrentRedBag();
    }

    private void loadMyCurrentRedBag() {
        showLoading();
        User user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("page", page + "");
        map.put("pageSize", pageSize + "");
        map.put("type", type + "");
        if (netbarId != null) {
            map.put("netbarId", netbarId);
        }
        if (amount != 0f) {
            map.put("payAmount", amount + "");
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_CARD, map, CARD);
    }


    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lvMyHistoryBag.getLastVisiblePosition() == lvMyHistoryBag.getAdapter().getCount() - 1) {
            ((RedbagBaseActivity) getActivity()).hiddenBottom(false);
        } else {
            ((RedbagBaseActivity) getActivity()).hiddenBottom(true);
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        try {
            if (method.equals(CARD)) {
                isLast = 1;
                List<Card> newBags = GsonUtil.getList(object.getJSONObject("object").getJSONArray("list").toString(), Card.class);
                if (page == 1) {
                    bags.clear();
                }
                bags.addAll(newBags);
                if (page == 1 && bags.size() == 0) {
                    lvMyHistoryBag.setErrorShow(true);
                } else {
                    lvMyHistoryBag.setErrorShow(false);
                }
                adapter.notifyDataSetChanged();

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        prlvMyHistoryBag.onRefreshComplete();
    }

    @Override
    public void onError(String errMsg, String method) {
        ToastUtil.showToast("数据获取发生失败", mContext);
        lvMyHistoryBag.setErrorShow(false);
        prlvMyHistoryBag.onRefreshComplete();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        lvMyHistoryBag.setErrorShow(false);
        prlvMyHistoryBag.onRefreshComplete();
    }

    @Override
    public void refreView() {
        page = 1;
        loadMyCurrentRedBag();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWatcher.unSubscribe(Observerable.ObserverableType.CARDUSE, this);
        mWatcher = null;
    }

    @Override
    public <T> void update(T... data) {
        page = 1;
        loadMyCurrentRedBag();
    }
}
