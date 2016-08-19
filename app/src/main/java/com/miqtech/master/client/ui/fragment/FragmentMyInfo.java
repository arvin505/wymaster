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
import com.miqtech.master.client.adapter.InforAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.InforItemDetail;
import com.miqtech.master.client.entity.InforList;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.InformationAtlasActivity;
import com.miqtech.master.client.ui.InformationTopicActivity;
import com.miqtech.master.client.ui.SubjectActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.GsonUtil;
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

/**
 * 我的资讯收藏
 * Created by Administrator on 2015/12/8.
 */
public class FragmentMyInfo extends MyBaseFragment implements AdapterView.OnItemClickListener,
        Observerable.ISubscribe {

    private View mainView;
    private Context mContext;
    private PullToRefreshListView prlvInfo;
    private HasErrorListView mylv;
    private boolean isFirst = true;
    private int page = 1;    //是	int	当前分页所在页数（默认值1）
    private int pageSize = 10;    //是	int	当前分页显示的行数（默认值10）
    private int infoCount = 0;
    private int isLast = 0;
    private InforAdapter adapter;
    private List<InforItemDetail> inforItemDetails = new ArrayList<InforItemDetail>();
    User user;

    private Observerable watcher = Observerable.getInstance();
    private boolean hasfavor = true;

    private InforItemDetail bean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_my_info, null);
            mContext = inflater.getContext();
            initView();
        }
        watcher.subscribe(Observerable.ObserverableType.COLLECTSTATE, this);
    }

    private void initView() {
        prlvInfo = (PullToRefreshListView) mainView.findViewById(R.id.prlvInfo);
        prlvInfo.setMode(PullToRefreshBase.Mode.BOTH);
        mylv = prlvInfo.getRefreshableView();

        mylv.setErrorView("太低调了,还没有收藏任何资讯");

        adapter = new InforAdapter(getActivity(), inforItemDetails);
        mylv.setAdapter(adapter);
        prlvInfo.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                infoCount = 0;
                pageSize = 10;
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if (inforItemDetails.size() > 0) {
                    if (isLast == 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                page++;
                                showLoading();
                                loadData();
                            }
                        }, 1000);
                    } else {
                        showToast(mContext.getResources().getString(R.string.load_no));
                        prlvInfo.onRefreshComplete();
                    }
                } else {
                    prlvInfo.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getActivity().getResources().getString(R.string.noNeteork));
                }
            }
        });
        mylv.setOnItemClickListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirst) {
            isFirst = false;
            loadData();
        }
    }

    private void loadData() {
        user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("page", page + "");
        map.put("pageSize", pageSize + "");
        map.put("infoCount", infoCount + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INFO_FAVLIST, map, HttpConstant.INFO_FAVLIST);
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mainView;
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        hideLoading();
        if (method.equals(HttpConstant.INFO_FAVLIST)) {
            getInforList(object);
        } else if (method.equals(HttpConstant.INFO_FAV)) {
            if (inforItemDetails.size() > 0) {
                inforItemDetails.remove(bean);
                adapter.notifyDataSetChanged();
            }
        }
        prlvInfo.onRefreshComplete();
    }

    @Override
    public void onError(String errMsg, String method) {
        showToast(errMsg);
        hideLoading();
        mylv.setErrorShow(false);
        prlvInfo.onRefreshComplete();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        hideLoading();
        mylv.setErrorShow(false);
        prlvInfo.onRefreshComplete();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (inforItemDetails.isEmpty() || inforItemDetails.size() - 1 < position) {
            return;
        }
        bean = inforItemDetails.get(position);
        Intent intent = null;
        if (bean.getType() == 1) {//类型:1图文  跳转
            intent = new Intent(mContext, SubjectActivity.class);
            intent.putExtra("id", bean.getId() + "");
            intent.putExtra("title", bean.getTitle());
            intent.putExtra("icon", bean.getIcon());
            intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.MATH);
            startActivity(intent);
        } else if (bean.getType() == 2) {//2专题  跳转
            intent = new Intent();
            intent.putExtra("activityId", bean.getId());
            intent.putExtra("zhuanTitle", bean.getTitle());
            intent.setClass(mContext, InformationTopicActivity.class);
            startActivity(intent);
        } else if (bean.getType() == 3) {//3图集  跳转
            intent = new Intent();
            intent.putExtra("activityId", bean.getId());
            intent.setClass(mContext, InformationAtlasActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 得到资讯数据列表，并刷新列表
     *
     * @param object
     */
    private void getInforList(JSONObject object) {
        try {
            if ("0".equals(object.getString("code")) && object.has("object")) {
                InforList newInforList = GsonUtil.getBean(object.getString("object").toString(), InforList.class);
                infoCount = newInforList.getTotal();
                isLast = newInforList.getIsLast();
                if (page == 1) {
                    inforItemDetails.clear();
                }
                inforItemDetails.addAll(newInforList.getList() == null ? new ArrayList<InforItemDetail>() : newInforList.getList());
                if (page == 1) {
                    if (inforItemDetails.size() == 0) {
                        mylv.setErrorShow(true);
                    }
                    mCache.put(HttpConstant.INFO_LIST, object);
                } else {
                    mylv.setErrorShow(false);
                }
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (inforItemDetails != null && !hasfavor) {
            if (inforItemDetails.contains(bean)) {
                inforItemDetails.remove(bean);
                adapter.notifyDataSetChanged();
            }
            hasfavor = true;
            if (inforItemDetails.size() == 0) {
                page = 1;
                infoCount = 0;
                pageSize = 10;
                loadData();
            }
        }
    }

    @Override
    public void refreView() {
        page = 1;
        infoCount = 0;
        pageSize = 10;
        loadData();
    }

    @Override
    public void onDestroy() {
        watcher.unSubscribe(Observerable.ObserverableType.COLLECTSTATE, this);
        watcher = null;
        super.onDestroy();
    }

    @Override
    public <T> void update(T... data) {
        if ((Integer) data[0] == 3) {
            this.hasfavor = (Boolean) data[2];
        }
    }
}
