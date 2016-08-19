package com.miqtech.master.client.ui;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.LivePlayAdapter;
import com.miqtech.master.client.entity.LiveAndVideoData;
import com.miqtech.master.client.entity.LiveInfo;
import com.miqtech.master.client.entity.VideoListInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.PagerSlidingTabStrip;
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
 * Created by admin on 2016/7/28.
 */
public class LiveVideoListActivity extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.tvTitle)
    TextView tvTitle;//标题
    @Bind(R.id.llBack)
    LinearLayout llBack;//返回按钮
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.tvTab1)
    TextView tvTab1;  //最热视频tab
    @Bind(R.id.tvTab2)
    TextView tvTab2;   //最新视频tab
    @Bind(R.id.tabTag)
    View tabTag;     //tab指示器

    private int newPage = 1;
    private int hotPage=1;
    private int pageSize = 10;
    private Context context;
    private int isNewLast=0;
    private int isHotLast=0;
    private LinearLayoutManager newlayoutManager;
    private LinearLayoutManager hotlayoutManager;
    private LivePlayAdapter newAdapter;
    private LivePlayAdapter hotAdapter;
    private List<LiveInfo> liveDatas=new ArrayList<LiveInfo>(); //直播列表数据
    private List<LiveInfo> newVideoDatas=new ArrayList<LiveInfo>();//最新视频数据
    private List<LiveInfo> hotVideoDatas=new ArrayList<LiveInfo>();//最热视频数据
    private VideoListInfo videoListInfo;
    private List<PullToRefreshRecyclerView> pages;
    private RecyclerView newRecyclerView; //最新视频RecyclerView
    private RecyclerView hotRecyclerView; //最热视屏RecyclerView
    private  PullToRefreshRecyclerView newVideoRV;
    private   PullToRefreshRecyclerView  hotVideoRV;
    private String[] titleStrings = new String[] { "最热视频", "最新视频" };
    private int lastIndexPositionX = 0;
    private int screenWidth;
    private TranslateAnimation animation; //tabView 平移动画
    private int type=0;//0 最新和最热视频都为空 1 最新为空 2 最热为空

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.acitivity_video_layout);
        ButterKnife.bind(this);
        context=LiveVideoListActivity.this;
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        initView();
        setOnClickListener();
        initData();
    }

    private void setOnClickListener() {
        llBack.setOnClickListener(this);
        tvTab1.setOnClickListener(this);
        tvTab2.setOnClickListener(this);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                setTabSelect(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        initTitle();
        pages=new ArrayList<>();
         newVideoRV = (PullToRefreshRecyclerView) LayoutInflater.from(this).inflate(
                R.layout.live_play_listview, null);
        newRecyclerView=newVideoRV.getRefreshableView();
        hotVideoRV = (PullToRefreshRecyclerView) LayoutInflater.from(this).inflate(
                R.layout.live_play_listview, null);
        hotRecyclerView=hotVideoRV.getRefreshableView();
        pages.add(hotVideoRV);
        pages.add(newVideoRV);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public CharSequence getPageTitle(int position) {
                return titleStrings[position];
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return pages.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(pages.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(pages.get(position));
                return pages.get(position);
            }
        });
        newlayoutManager = new LinearLayoutManager(context);
        hotlayoutManager= new LinearLayoutManager(context);

        newlayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        hotlayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        newRecyclerView.setLayoutManager(newlayoutManager);
        hotRecyclerView.setLayoutManager(hotlayoutManager);

        newVideoRV.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                //TODO 去分别下载数据
                type=1;
                newPage = 1;
                getInfomations(1+"",newPage);

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (isNewLast == 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            newPage++;
                            getInfomations(1+"",newPage);
                        }
                    }, 1000);
                } else {
                    showToast(getResources().getString(R.string.nomore));
                    newVideoRV.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if(!isHasNetWork){
                    showToast(getResources().getString(R.string.noNeteork));
                }
            }
        });

        hotVideoRV.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                //TODO 去分别下载数据
                type=2;
                hotPage = 1;
                getInfomations(0+"",hotPage);

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (isHotLast == 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hotPage++;
                            getInfomations(0+"",hotPage);
                        }
                    }, 1000);
                } else {
                    showToast(getResources().getString(R.string.nomore));
                    hotVideoRV.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if(!isHasNetWork){
                    showToast(getResources().getString(R.string.noNeteork));
                }
            }
        });
        setTabSelect(0);
    }
    public void setTabSelect(int select) {
        tvTab1.setTextColor(select == 0 ? getResources().getColor(R.color.light_orange) : getResources().getColor(R.color.shop_font_black));
        tvTab2.setTextColor(select == 1 ? getResources().getColor(R.color.light_orange) : getResources().getColor(R.color.shop_font_black));
        calculateIndexLength(select);
    }
    /**
     * 计算tab下边游标的长度并设置位置
     *
     * @param select
     *
     */
    public void calculateIndexLength(int select) {
        android.view.ViewGroup.LayoutParams layoutParams = tabTag.getLayoutParams();
        int mTabTagWidth = 0;
        switch (select) {
            case 0:
                mTabTagWidth = getTextViewLength(tvTab1, tvTab1.getText().toString());

                break;
            case 1:
                mTabTagWidth = getTextViewLength(tvTab2, tvTab2.getText().toString());

                break;
            default:
                break;
        }

        layoutParams.width = mTabTagWidth + 58;
        tabTag.setLayoutParams(layoutParams);
        int curentPositionX = getTabTagPositionX(select, mTabTagWidth);
        startAnimation(lastIndexPositionX, getTabTagPositionX(select, mTabTagWidth));
        lastIndexPositionX = curentPositionX;
    }
    public int getTabTagPositionX(int select, int mTabTagWidth) {
        int  x = (screenWidth / 2 * (select)) + screenWidth / 4 - mTabTagWidth / 2;
        return x-29;
    }
    /**
     *
     * @param fromX
     *            开始位置
     * @param toX
     *            结束位置
     */
    private void startAnimation(int fromX, int toX) {
        animation = new TranslateAnimation(fromX, toX, 0, 0);
        animation.setDuration(100);
        animation.setFillEnabled(true);
        animation.setFillAfter(true);
        tabTag.startAnimation(animation);
    }
    private  int getTextViewLength(TextView textView, String text) {
        TextPaint paint = textView.getPaint();
        // 得到使用该paint写上text的时候,像素为多少
        int textLength = (int) paint.measureText(text);
        return textLength;
    }

    @Override
    protected void initData() {
        super.initData();
        getInfomations("",1);
    }
    private void initTitle(){
        tvTitle.setText(getString(R.string.live_play_hall_video));
    }
    private void getInfomations(String type,int page){

        showLoading();
        Map<String, String> params = new HashMap();
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        //TODO 传递数据
        if(!TextUtils.isEmpty(type)) {
            params.put("type", type);
        }
        LogUtil.e(TAG, "params : " + params.toString());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.VIDEO_LIST, params, HttpConstant.VIDEO_LIST);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        newVideoRV.onRefreshComplete();
        hotVideoRV.onRefreshComplete();
        if (HttpConstant.VIDEO_LIST.equals(method)) {
            try {
                if ("0".equals(object.getString("code")) && object.has("object")) {
                    videoListInfo = GsonUtil.getBean(object.getJSONObject("object").toString(), VideoListInfo.class);
                    isHotLast = object.getJSONObject("object").getInt("isLast");
                    isNewLast = isHotLast;
                    if (videoListInfo != null) {
                        if (videoListInfo.getNewLive() != null) {
                            if (newPage == 1) {
                                newVideoDatas.clear();
                                newAdapter = new LivePlayAdapter(context, 2, liveDatas, newVideoDatas);
                                newVideoDatas.addAll(videoListInfo.getNewLive());
                                newRecyclerView.setAdapter(newAdapter);
                            } else {
                                newVideoDatas.addAll(videoListInfo.getNewLive());
                                newAdapter.notifyDataSetChanged();
                            }
                        }else{
                            if(type!=2) {
                                setErrorView(1);
                            }
                        }
                        if (videoListInfo.getHotLive() != null) {
                            if (hotPage == 1) {
                                hotVideoDatas.clear();
                                hotAdapter = new LivePlayAdapter(context, 2, liveDatas, hotVideoDatas);
                                hotVideoDatas.addAll(videoListInfo.getHotLive());
                                hotRecyclerView.setAdapter(hotAdapter);
                            } else {
                                hotVideoDatas.addAll(videoListInfo.getHotLive());
                                hotAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if(type!=1) {
                                setErrorView(2);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        newVideoRV.onRefreshComplete();
        hotVideoRV.onRefreshComplete();
        hideLoading();
        setErrorView(type);
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        newVideoRV.onRefreshComplete();
        hotVideoRV.onRefreshComplete();
        hideLoading();
        newAdapter.setNetWorkState(false);
        hotAdapter.setNetWorkState(false);
        setErrorView(0);
    }
    /**
     * 设置没有数据的错误页面
     */
    private void setErrorView(int type){
        liveDatas.clear();
        if(type==0) {
            newVideoDatas.clear();
            newRecyclerView.setAdapter(newAdapter);
            hotVideoDatas.clear();
            hotRecyclerView.setAdapter(hotAdapter);
        }else if(type==1){
            newVideoDatas.clear();
            newRecyclerView.setAdapter(newAdapter);
        }else if(type==2){
            hotVideoDatas.clear();
            hotRecyclerView.setAdapter(hotAdapter);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llBack:
                onBackPressed();
                break;
            case R.id.tvTab1:
                setTabSelect(0);
                viewPager.setCurrentItem(0);
                type=1;
                break;
            case R.id.tvTab2:
                setTabSelect(1);
                viewPager.setCurrentItem(1);
                type=2;
                break;
        }
    }
}
