package com.miqtech.master.client.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.GameSelectAdapter;
import com.miqtech.master.client.adapter.LivePlayAdapter;
import com.miqtech.master.client.entity.LiveAndVideoData;
import com.miqtech.master.client.entity.LiveGameInfo;
import com.miqtech.master.client.entity.LiveInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
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
 * Created by admin on 2016/7/25.
 */
public class LivePlayListActivity extends BaseActivity implements View.OnClickListener , AdapterView.OnItemClickListener{
    @Bind(R.id.prrvLivePlay)
    PullToRefreshRecyclerView prrvLivePlay;
    @Bind(R.id.viewHidden)
    View viewHidden;   //透明View
    @Bind(R.id.llBack)
    LinearLayout llBack; //返回按钮
    @Bind(R.id.llTitle)
    LinearLayout llTitle; //顶部标题
    @Bind(R.id.ivIndicate)
    ImageView ivIndicate; //更多游戏选择
    RecyclerView recyclerView;

    private int page = 1;
    private int pageSize = 10;
    private Context context;
    private int isLast=0;
    private GridLayoutManager layoutManager;
    private LivePlayAdapter adapter;
    private List<LiveInfo> liveDatas=new ArrayList<LiveInfo>(); //直播列表数据
    private List<LiveInfo>videoDatas=new ArrayList<LiveInfo>();//视频数据
    private List<LiveGameInfo> gameInfos= new ArrayList<>();//游戏分类数据
    private PopupWindow selectPopWindow;
    private boolean isPopShowing = false;
    private  Animation animUp;
    private  Animation animDown;
    private LiveAndVideoData liveAndVideoData;
    private int hasNum=1; //是否返回直播数1是0否
    private int gameId=0; //游戏id
    private int gameSize=0;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_live_layout);
        ButterKnife.bind(this);
        context=LivePlayListActivity.this;
        initView();
        initAnimation();
        setOnClickListener();
        initData();

    }

    /**
     * 初始化箭头旋转动画
     */
    private void initAnimation() {
        animUp = AnimationUtils.loadAnimation(this, R.anim.rotate_180_up);
        LinearInterpolator lin = new LinearInterpolator();
        animUp.setInterpolator(lin);
        animDown=AnimationUtils.loadAnimation(this,R.anim.rotate_180_down);
        animDown.setInterpolator(lin);
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
        recyclerView=prrvLivePlay.getRefreshableView();
        layoutManager = new GridLayoutManager(context, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case LivePlayAdapter.VIEW_LIVE_ITEM:
                        return 1;
                    case LivePlayAdapter.VIEW_EMPTY:
                        return 2;
                    default:
                        return -1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LivePlayAdapter(context,1, liveDatas,videoDatas);
        prrvLivePlay.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                page = 1;
                getInfomations(gameId);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                    if (isLast == 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                page++;
                                getInfomations(gameId);
                            }
                        }, 1000);
                    } else {
                        showToast(getResources().getString(R.string.nomore));
                        prrvLivePlay.onRefreshComplete();
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

    @Override
    protected void initData() {
        super.initData();
        getGameList();
        getInfomations(0);
    }
    private void setOnClickListener(){
        llTitle.setOnClickListener(this);
        llBack.setOnClickListener(this);
    }
    private void initTitle(){
        ivIndicate.setVisibility(View.VISIBLE);
    }
    private void getGameList(){
        Map<String, String> params = new HashMap();
        params.put("hasNum", hasNum + "");
       //TODO 传递数据
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.LIVE_GAME_LIST, params, HttpConstant.LIVE_GAME_LIST);
    }

    /**
     * 获取直播列表数据
     */
    private void getInfomations(int gameId){
        showLoading();
        Map<String, String> params = new HashMap();
        params.put("page", page + "");
        if(gameId!=0) {
            params.put("gameId", gameId + "");
        }
        LogUtil.e(TAG, "params : " + params.toString());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.LIVE_LIST, params, HttpConstant.LIVE_LIST);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        prrvLivePlay.onRefreshComplete();
        if (HttpConstant.LIVE_LIST.equals(method)) {
            try {
                if("0".equals(object.getString("code"))&& object.has("object")){
                    Gson gs = new Gson();
                   List<LiveInfo> datas = gs.fromJson(object.getJSONObject("object").getJSONArray("list").toString(), new TypeToken<List<LiveInfo>>() {}.getType());
                   isLast= object.getJSONObject("object").getInt("isLast");
                    if(datas!=null && !datas.isEmpty()){
                        if(page==1){
                            liveDatas.clear();
                            liveDatas.addAll(datas);
                            recyclerView.setAdapter(adapter);
                        }else {
                            adapter.notifyDataSetChanged();
                        }
                    }else{
                        setErrorView();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if(HttpConstant.LIVE_GAME_LIST.equals(method)){
            try {
                if ("0".equals(object.getString("code")) && object.has("object")) {
                    Gson gs = new Gson();
                    gameInfos = gs.fromJson(object.getString("object"), new TypeToken<List<LiveGameInfo>>() {}.getType());
                    gameSize=gameInfos.size();
                    LogUtil.d(TAG,"onSuccess"+gameSize);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        prrvLivePlay.onRefreshComplete();
        hideLoading();
        setErrorView();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        prrvLivePlay.onRefreshComplete();
        hideLoading();
        adapter.setNetWorkState(false);
        setErrorView();
    }
    /**
     * 设置没有数据的错误页面
     */
    private void setErrorView(){
        liveDatas.clear();
        videoDatas.clear();
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llTitle:
                if(!isPopShowing) {
                    showPopWindow();
                }
                break;
            case R.id.llBack:
                LogUtil.d(TAG,"点击返回按钮"+isPopShowing);
                if(isPopShowing) {
                    selectPopWindow.dismiss();
                }else{
                    onBackPressed();
                }
                break;
            case R.id.viewHidden:
                selectPopWindow.dismiss();
                break;
        }
    }
    /**
     *弹出筛选框
     */
    /**
     * 弹出popwindow
     */
    private void showPopWindow() {
        if (selectPopWindow == null) {
            selectPopWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) {
                @Override
                public void dismiss() {
                    super.dismiss();
                    viewHidden.setVisibility(View.GONE);
                    ivIndicate.startAnimation(animDown);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isPopShowing = false;
                            adapter.setIsCanClick(true);
                        }
                    },500);
                }
            };
            selectPopWindow.setAnimationStyle(R.style.pop_anim);
            View popView = LayoutInflater.from(context).inflate(R.layout.ppw_game_select, null);
            GridView gridView = (GridView) popView.findViewById(R.id.gvGame);
            GameSelectAdapter adapter =new GameSelectAdapter(context,gameInfos);
            gridView.setAdapter(adapter);
            viewHidden.setOnClickListener(this);
            gridView.setOnItemClickListener(this);
            selectPopWindow.setContentView(popView);
            selectPopWindow.setOutsideTouchable(true);
            ColorDrawable cd = new ColorDrawable(Color.TRANSPARENT);
            selectPopWindow.setBackgroundDrawable(cd);
        }
        viewHidden.setVisibility(View.VISIBLE);
        selectPopWindow.showAsDropDown(llTitle, 0, 0);
        adapter.setIsCanClick(false);
        ivIndicate.startAnimation(animUp);
        LogUtil.d(TAG,"获取焦点");
        isPopShowing = true;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectPopWindow.dismiss();
        //TODO 再次请求数据
        LogUtil.d(TAG,"onItemClick"+gameInfos.size()+"::::"+position+"::::"+gameSize);
        if(gameInfos!=null && gameSize>position) {
            gameId=gameInfos.get(position).getId();
            getInfomations(gameInfos.get(position).getId());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ivIndicate.clearAnimation();
    }
}
