package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.LivePlayAdapter;
import com.miqtech.master.client.entity.InforItemDetail;
import com.miqtech.master.client.entity.LiveAndVideoData;
import com.miqtech.master.client.entity.LiveInfo;
import com.miqtech.master.client.entity.LiveRoomHistoryVideoInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/7/27.
 */
public class FragmentHistoryLP extends BaseFragment {
    @Bind(R.id.prrvLiveRoom)
    PullToRefreshRecyclerView prrvLiveRoom;
    RecyclerView recyclerView;
    private Context context;
    private LinearLayoutManager layoutManager;
    private LivePlayAdapter adapter;
    private List<LiveInfo> liveDatas=new ArrayList<LiveInfo>(); //直播列表数据
    private List<LiveInfo> videoDatas=new ArrayList<LiveInfo>();//视频数据
    private int page = 1;
    private int isLast=0;
    private UpdataVideoData listener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getContext();
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_lp,container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initView();
    }
    public void setHistoryVideoData(LiveRoomHistoryVideoInfo info,int page){
        if(info!=null) {
                isLast=info.getIsLast();
                if (info.getList() != null && !info.getList().isEmpty()) {
                    if(page==1) {
                        videoDatas.clear();
                        videoDatas.addAll(info.getList());
                        recyclerView.setAdapter(adapter);
                    }else{
                        videoDatas.addAll(info.getList());
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    prrvLiveRoom.onRefreshComplete();
                    if (page == 1) {
                        setErrorView();
                    } else {
                        showToast(R.string.nomore);
                    }
                }
        }
    }
    public void setContext(UpdataVideoData listener){
        this.listener=listener;
    }
    public void refreshComplete(){
        prrvLiveRoom.onRefreshComplete();
    }
    private void initView() {
        recyclerView= prrvLiveRoom.getRefreshableView();
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LivePlayAdapter(context,2, liveDatas,videoDatas);
        prrvLiveRoom.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (isLast == 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            page++;
                            listener.updataVideoData(page);
                        }
                    }, 1000);
                } else {
                    showToast(getResources().getString(R.string.nomore));
                    prrvLiveRoom.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if(!isHasNetWork){
                    showToast(getResources().getString(R.string.noNeteork));
                }
            }
        });
    }
    /**
     * 设置没有数据的错误页面
     */
    private void setErrorView(){
        liveDatas.clear();
        videoDatas.clear();
        recyclerView.setAdapter(adapter);
    }

    /**
     * 跟新历史是视屏数据
     */
    public interface UpdataVideoData{
        //传入page为参数
        void updataVideoData(int page);
    }
}
