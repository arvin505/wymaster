package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.LivePlayAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.LiveAndVideoData;
import com.miqtech.master.client.entity.LiveInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshRecyclerView;
import com.screenrecorder.ui.ScreenRecorderActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/8/11.
 */
public class LivePlayAndVideoListActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.rvContent)
    PullToRefreshRecyclerView rvContent;
    @Bind(R.id.tvLeftTitle)
    TextView tvLeftTitle;//顶部标题
    private Context context;
    private GridLayoutManager layoutManager;
    private LivePlayAdapter adapter;
    private List<LiveInfo> liveDatas = new ArrayList<LiveInfo>(); //直播列表数据
    private List<LiveInfo> videoDatas = new ArrayList<LiveInfo>();//视频数据
    private LiveAndVideoData liveAndVideoData;
    private RecyclerView recyclerView;

    @Override
    protected void init() {
        super.init();
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_liveplay_and_video_layout);
        LogUtil.d(TAG,"开始");
        ButterKnife.bind(this);
        context = LivePlayAndVideoListActivity.this;
        initView();
        initData();

    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
        recyclerView = rvContent.getRefreshableView();
        rvContent.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                getInfomations();
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getString(R.string.noNeteork));
                }
            }
        });
        layoutManager = new GridLayoutManager(context, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //TODO 此处去设置是girdView 还是listView
                switch (adapter.getItemViewType(position)) {
                    case LivePlayAdapter.VIEW_LIVE_ITEM:
                        return 1;
                    case LivePlayAdapter.VIEW_LIVE_TITLE:
                        return 2;
                    case LivePlayAdapter.VIEW_VIDEO_ITEM:
                        return 2;
                    case LivePlayAdapter.VIEW_VIDEO_TITLE:
                        return 2;
                    case LivePlayAdapter.VIEW_EMPTY:
                        return 2;
                    default:
                        return -1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LivePlayAdapter(context, 0, liveDatas, videoDatas);
    }

    @Override
    protected void initData() {
        super.initData();
        getInfomations();
        checkLive();
    }

    private void initTitle() {
        tvLeftTitle.setText(getString(R.string.live_play_hall_title));
        getLeftBtn().setOnClickListener(this);
    }

    /**
     * 直播机型校验
     */
    private void checkLive(){
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.LIVE_CHECK,null,HttpConstant.LIVE_CHECK);
    }

    private boolean canLive(){
        User user = WangYuApplication.getUser(WangYuApplication.appContext);
        if (user == null|| user.getIsUp()!=1 ) {
            return false;
        }
        if (Build.VERSION.SDK_INT<21){
            return false;
        }
        return true;
    }

    private void getInfomations() {
        showLoading();
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.LIVE_VIDEO_LIST, null, HttpConstant.LIVE_VIDEO_LIST);
    }

    @Override
    public void onSuccess(final JSONObject object, String method) {
        super.onSuccess(object, method);
        rvContent.onRefreshComplete();
        hideLoading();
        if (HttpConstant.LIVE_VIDEO_LIST.equals(method)) {
            try {
                if ("0".equals(object.getString("code")) && object.has("object")) {
                    liveAndVideoData = GsonUtil.getBean(object.getJSONObject("object").toString(), LiveAndVideoData.class);
                    if (liveAndVideoData != null) {
                        if (liveAndVideoData.getOnLive() != null) {
                            liveDatas.clear();
                            liveDatas.addAll(liveAndVideoData.getOnLive());
                        }
                        if (liveAndVideoData.getHotVideo() != null) {
                            videoDatas.clear();
                            videoDatas.addAll(liveAndVideoData.getHotVideo());
                        }
                        recyclerView.setAdapter(adapter);
                    } else {
                        //TODO 没有数据显示错误页面
                        setErrorView();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (method.equals(HttpConstant.LIVE_CHECK)){
            try {
                if (object.getInt("object") == 1){
                    if (canLive()) {
                        setRightTextView("");
                        Drawable icon = getResources().getDrawable(R.drawable.icon_live_go);
                        icon.setBounds(0,0,icon.getMinimumWidth(),icon.getMinimumHeight());
                        getRightTextview().setCompoundDrawables(icon,null,null,null);
                        getRightTextview().setOnClickListener(this);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        rvContent.onRefreshComplete();
        hideLoading();
        try {
            if (object.has("result")) {
                showToast(object.getString("result"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setErrorView();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        LogUtil.d(TAG, "onError");
        rvContent.onRefreshComplete();
        adapter.setNetWorkState(false);
        setErrorView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.tvRightHandle:
                Intent intent = new Intent(this, ScreenRecorderActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 设置没有数据的错误页面
     */
    private void setErrorView() {
        liveDatas.clear();
        videoDatas.clear();
        recyclerView.setAdapter(adapter);
    }
}
