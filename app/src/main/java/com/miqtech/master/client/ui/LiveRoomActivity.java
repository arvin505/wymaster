package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.LiveRoomAnchorInfo;
import com.miqtech.master.client.entity.LiveRoomInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.fragment.FragmentHistoryLP;
import com.miqtech.master.client.ui.fragment.FragmentInformationLP;
import com.miqtech.master.client.ui.fragment.FragmentTalkLP;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ShareToFriendsUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CustomMarqueeTextView;
import com.miqtech.master.client.view.ExpertMorePopupWindow;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.constant.WBConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/8/12.
 */
public class LiveRoomActivity extends BaseActivity implements View.OnClickListener,FragmentHistoryLP.UpdataVideoData,IWeiboHandler.Response{
    @Bind(R.id.rlSurfaceView)
    RelativeLayout rlSurfaceView; //整个播放布局
    @Bind(R.id.surfaceView)
    SurfaceView surfaceView ;//播放View
    @Bind(R.id.llBufferingIndicator)
    View llBufferingIndicator ;//缓冲指示器
    @Bind(R.id.ivBack)
    ImageView ivBack ;//返回按钮
    @Bind(R.id.tvTitle)
    CustomMarqueeTextView tvTitle ;//视频标题
    @Bind(R.id.ivFullScreenBtn)
    ImageView ivFullScreenBtn ;//全屏按钮
    @Bind(R.id.tvTab1)
    TextView tvTab1 ;//信息tab
    @Bind(R.id.tvTab2)
    TextView tvTab2 ;//聊天tab
    @Bind(R.id.tvTab3)
    TextView tvTab3 ;//往期tab
    @Bind(R.id.tabTag)
    View tabTag; //tab导航view
    @Bind(R.id.moretabViewPager)
    ViewPager moretabViewPager; //
    @Bind(R.id.llNodata)
    LinearLayout llNodata;//没有数据布局
    @Bind(R.id.tvContinue)
    TextView tvContinue; //没有wifi流量继续播放按钮
    @Bind(R.id.llGamePic)
    LinearLayout llGamePic;//游戏图片布局
    @Bind(R.id.ivGamePic1)
    ImageView ivGamePic1; //图片1
    @Bind(R.id.ivGamePic2)
    ImageView ivGamePic2;//图片2
    @Bind(R.id.tvNoAnchorHint)
    TextView tvNoAnchorHint;//主播不在或者没有网络下的提示语
    @Bind(R.id.ivShareIcon)
    ImageView ivShareIcon ;//分享按钮

    private String mVideoPath; // 播放地址
    private Context context;
    private float radio = 3f / 5f; //屏幕的宽高比
    private ViewGroup.LayoutParams surfaceLayoutParams;
    private TranslateAnimation animation; //tabView 平移动画
    private int screenWidth;
    private int lastIndexPositionX = 0;
    private Fragment fragements[] = new Fragment[3];
    private Fragment fragment;
    private AVOptions mAVOptions;
    private PLMediaPlayer mMediaPlayer;
    private boolean isFullScreen=false;
    private String id; //直播id
    private int page=1;
    private int pageSize=10;
    private LiveRoomInfo liveRoomInfo;
    private boolean isExit=false;
    private static final int MSG_EXIT_ROOM = 100;
    private static final int MSG_EXIT_ROOM_DELAY = 2000;
    private static final int MSG_PLAY_TIMES_REQUEST=101;
    private boolean isSubscribe=false;
    private boolean isPlayComplete=false;
    private ConnectivityManager manager;
    private View main;

    private ShareToFriendsUtil shareToFriendsUtil;//分享
    private ExpertMorePopupWindow popwin;//分享弹框
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_EXIT_ROOM:
                    isExit=false;
                    break;
                case MSG_PLAY_TIMES_REQUEST:
                    if (totalTime >=60 && !isPlayComplete) {
                        //倒计时时间
                        //设置倒计时时间
                        countDownRequest();
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                            totalTime=0;
                        }
                    }
                    break;
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState, persistentState);
        initSinaSso(savedInstanceState);
    }

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_liveroom_layout);
        ButterKnife.bind(this);
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        context=this;
        // 初始化视频大小
        initPlayerView(true);
        initView();
        initPlayParameter();
        setOnClickListener();
        initData();
    }
    public void initSinaSso(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            shareToFriendsUtil.getIWeiApiInstance(this).handleWeiboResponse(getIntent(), this);
        }
    }
    @Override
    protected void initData() {
        checkState();
     getLiveRoomData(1);
    }
    private void getLiveRoomData(int page){
        showLoading();
        Map<String, String> params = new HashMap();
        params.put("id",id);
        params.put("page", page + "");
        params.put("pageSize",pageSize+"");
       if(WangYuApplication.getUser(LiveRoomActivity.this)!=null){
           params.put("userId",WangYuApplication.getUser(LiveRoomActivity.this).getId()+"");
       }
        LogUtil.e(TAG, "params : " + params.toString());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.LIVE_ROOM_DETAIL, params, HttpConstant.LIVE_ROOM_DETAIL);
    }
    public void updataSubscribeState(boolean isSubscribe){
        ((FragmentInformationLP)fragements[0]).setSubscribeState(isSubscribe);
        ((FragmentTalkLP)fragements[1]).setSubscribeState(isSubscribe);

    }
    /**
     * 用户离开房间网络请求
     */
    private void leaveRoom(){
        Map<String, String> params = new HashMap();
        params.put("id",id);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.LEAVE_ROOM, params, HttpConstant.LEAVE_ROOM);
    }
    private void countDownRequest(){
        Map<String, String> params = new HashMap();
        params.put("id",id);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.PLAY_TIMES_REQUEST, params, HttpConstant.PLAY_TIMES_REQUEST);
    }

    /**
     * 从新连接请求
     */
    private void reConnectRequest(){
        Log.d(TAG, "重新连接");
        Map<String, String> params = new HashMap();
        params.put("id",id);
        if(WangYuApplication.getUser(LiveRoomActivity.this)!=null){
            params.put("userId",WangYuApplication.getUser(LiveRoomActivity.this).getId()+"");
            params.put("token",WangYuApplication.getUser(LiveRoomActivity.this).getToken());
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.LIVE_STATE, params, HttpConstant.LIVE_STATE);
    }
    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        Log.d(TAG, "onSuccess");
        hideLoading();
        if (HttpConstant.LIVE_ROOM_DETAIL.equals(method)) {
            try {
                if ("0".equals(object.getString("code")) && object.has("object")) {
                    liveRoomInfo = GsonUtil.getBean(object.getJSONObject("object").toString(), LiveRoomInfo.class);
                    if(liveRoomInfo!=null) {
                        if(liveRoomInfo.getInfo()!=null) {
                            mVideoPath=liveRoomInfo.getInfo().getRtmp();
                            prepare();
                            isSubscribe=liveRoomInfo.getInfo().getIsSubscibe()==1?true:false;
                            tvTitle.setText(liveRoomInfo.getInfo().getTitle());
                            setShareBtn(liveRoomInfo.getInfo());
                            initPlayerView(liveRoomInfo.getInfo().getScreen()==0?true:false);
                            ((FragmentInformationLP) fragements[0]).setAnchorData(liveRoomInfo.getInfo());
                            ((FragmentTalkLP)fragements[1]).setAnchorData(liveRoomInfo.getInfo());
                        }
                        if(liveRoomInfo.getHistoryVideo()!=null){
                            ((FragmentHistoryLP)fragements[2]).setHistoryVideoData(liveRoomInfo.getHistoryVideo(),page);
                        }
                    }
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }else if(method.equals(HttpConstant.LEAVE_ROOM)){
            //用户离房
        }else if(method.equals(HttpConstant.PLAY_TIMES_REQUEST)){
            try {
                if("0".equals(object.getString("code")) && "success".equals(object.getString("result"))){
                    //TODO 发送计算播放次数成功  更新播放次数
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(method.equals(HttpConstant.LIVE_STATE)){
            Log.d(TAG, "onSuccess 重新连接");
            try {
                if(0==object.getInt("code")){
                    if(object.getInt("object")==1){
                     //   showToast("重新连接");
                        reConnect();
                        Log.d(TAG, "onSuccess 连接成功");
                    }else{
                        Log.d(TAG, "onSuccess 主播已离开房间");

                        showOtherLiveData();
                        showToast("主播已离开房间");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *   分享按钮的显示与否
     * @param info
     */
    private void setShareBtn(LiveRoomAnchorInfo info) {
        if(info.getCanShare()==0){
            ivShareIcon.setVisibility(View.GONE);
            ivShareIcon.setOnClickListener(null);
        }else{
            ivShareIcon.setVisibility(View.VISIBLE);
            ivShareIcon.setOnClickListener(this);
        }
    }

    /**
     * 显示其他直播数据
     */
    private void showOtherLiveData() {
        if(liveRoomInfo!=null && liveRoomInfo.getOtherLive()!=null && !liveRoomInfo.getOtherLive().isEmpty()) {
            llNodata.setVisibility(View.VISIBLE);
            llGamePic.setVisibility(View.VISIBLE);

            ivGamePic1.setVisibility(View.VISIBLE);
            ivGamePic1.setOnClickListener(this);
            ivGamePic2.setOnClickListener(this);
            tvNoAnchorHint.setText(getString(R.string.anchor_leave));
            if(liveRoomInfo.getOtherLive().size()<=1){
                AsyncImage.loadNetPhoto(context,liveRoomInfo.getOtherLive().get(0).getIcon(),ivGamePic1);
            }else{
                ivGamePic2.setVisibility(View.VISIBLE);
                AsyncImage.loadNetPhoto(context,liveRoomInfo.getOtherLive().get(0).getIcon(),ivGamePic1);
                AsyncImage.loadNetPhoto(context,liveRoomInfo.getOtherLive().get(1).getIcon(),ivGamePic2);
            }

        }
    }


    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        Log.d(TAG, "onFaild");
        ((FragmentHistoryLP) fragements[2]).refreshComplete();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        Log.d(TAG, "onError"+errMsg);
        ((FragmentHistoryLP) fragements[2]).refreshComplete();
        if(method.equals(HttpConstant.LIVE_STATE)){
            reConnect();
        }
    }
    /**
     * 初始化播放器大小
     *
     * @param parentView
     */
    /**
     * 初始化播放器大小
     * 宽高比4：3
     */
    private void initPlayerView(boolean islandscape) {
        int videoHeight = (int) (screenWidth * radio);
        ViewGroup.LayoutParams lp = rlSurfaceView.getLayoutParams();
        surfaceLayoutParams = surfaceView.getLayoutParams();
        if(islandscape){
            surfaceLayoutParams.width =ViewGroup.LayoutParams.MATCH_PARENT;
        }else{
            //TODO 主播端用的宽高比9:16 我这边暂时写死的  后期更改
            surfaceLayoutParams.width =videoHeight*9/16;
        }
        surfaceLayoutParams.height = videoHeight;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = videoHeight;
        LogUtil.d(TAG,"initPlayerView"+"宽度"+lp.width+":::"+lp.height);
        rlSurfaceView.setLayoutParams(lp);
        surfaceView.setLayoutParams(surfaceLayoutParams);
    }

    /**
     * 初始化View
     */
    @Override
    protected void initView() {
        super.initView();
        id=getIntent().getStringExtra("id");
        //TODO 获取播放地址
        mVideoPath = getIntent().getStringExtra("rtmp");
        LogUtil.d(TAG,"播放地址"+mVideoPath+":::id"+id);
        //TODO 去掉写死的播放地址
     //   mVideoPath="rtmp://pili-live-rtmp.wangyuhudong.com/wyds/wyds_test_android_01";
        Intent intent = getIntent();
        String intentAction = intent.getAction();
        if (!TextUtils.isEmpty(intentAction) && intentAction.equals(Intent.ACTION_VIEW)) {
            mVideoPath = intent.getDataString();
        }
        surfaceView.getHolder().addCallback(mCallback);
        moretabViewPager.setOffscreenPageLimit(3);
        fragment = new FragmentInformationLP();
        fragements[0] = fragment;
        fragment = new FragmentTalkLP();
        fragements[1] = fragment;
        fragment = new FragmentHistoryLP();
        fragements[2] = fragment;
        ((FragmentHistoryLP)fragements[2]).setContext(this);
        moretabViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        setTabSelect(1);
        moretabViewPager.setCurrentItem(1);
        llBufferingIndicator.setVisibility(View.VISIBLE);

        popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
        popwin.setOnItemClick(itemOnClick);
        shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
    }
    private void initPlayParameter(){
        mAVOptions = new AVOptions();
        int isLiveStreaming = getIntent().getIntExtra("liveStreaming", 1);
        // 准备超时时间，包括创建资源、建立连接、请求码流等，单位是 ms
       // 默认值是：无
        mAVOptions.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        // 读取视频流超时时间，单位是 ms
       // 默认值是：10 * 1000
        mAVOptions.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        // 当前播放的是否为在线直播，如果是，则底层会有一些播放优化
        // 默认值是：0
        mAVOptions.setInteger(AVOptions.KEY_LIVE_STREAMING, isLiveStreaming);
        if (isLiveStreaming == 1) {
            // 是否开启"延时优化"，只在在线直播流中有效
            // 默认值是：0
            mAVOptions.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);
        }

        // 解码方式，codec＝1，硬解; codec=0, 软解
        int codec = getIntent().getIntExtra("mediaCodec", 0);
        mAVOptions.setInteger(AVOptions.KEY_MEDIACODEC, codec);


        // 是否自动启动播放，如果设置为 1，则在调用 `prepareAsync` 或者 `setVideoPath` 之后自动启动播放，无需调用 `start()`
        // 默认值是：1
        mAVOptions.setInteger(AVOptions.KEY_START_ON_PREPARED, 1);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }
    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        //    prepare();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // release();
            releaseWithoutStop();
        }
    };
    public void releaseWithoutStop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(null);
        }
    }
    private void changeLandscape() {
        Log.i("changeLandscape", "变换横屏"); //0 宽频 1竖屏
        if(liveRoomInfo.getInfo().getScreen()==0) {
            rlSurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 全屏
            ivFullScreenBtn.setImageResource(R.drawable.live_play_back_fullscreen);
            isFullScreen = true;
        }else {
            rlSurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            ivFullScreenBtn.setImageResource(R.drawable.live_play_back_fullscreen);
            isFullScreen = true;

        }
    }

    private void backLandscape() {
        Log.i("changeLandscape", "变换竖屏");
        //影藏锁屏图标
        if(isFullScreen) {
            if(liveRoomInfo.getInfo().getScreen()==0) {
                int videoHeight = (int) (screenWidth * radio);
                rlSurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, videoHeight));
                LogUtil.d(TAG,"backLandscape"+screenWidth+":::"+videoHeight+"::::::");
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 还原
                ivFullScreenBtn.setImageResource(R.drawable.live_play_fullscreen);
                isFullScreen = false;
            }else{
                int videoHeight = (int) (screenWidth * radio);
                ViewGroup.LayoutParams lp = rlSurfaceView.getLayoutParams();
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = videoHeight;
                LogUtil.d(TAG,"initPlayerView"+"宽度"+lp.width+":::"+lp.height);
                rlSurfaceView.setLayoutParams(lp);
                surfaceView.setLayoutParams(surfaceLayoutParams);
                ivFullScreenBtn.setImageResource(R.drawable.live_play_fullscreen);
                isFullScreen = false;
            }
        }
    }
    /**
     * View设置点击事件
     */
    private void setOnClickListener() {
        tvTab1.setOnClickListener(this);
        tvTab2.setOnClickListener(this);
        tvTab3.setOnClickListener(this);
        ivFullScreenBtn.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        moretabViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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

    public void setTabSelect(int select) {
        tvTab1.setTextColor(select == 0 ? getResources().getColor(R.color.light_orange) : getResources().getColor(R.color.shop_font_black));
        tvTab2.setTextColor(select == 1 ? getResources().getColor(R.color.light_orange) : getResources().getColor(R.color.shop_font_black));
        tvTab3.setTextColor(select == 2 ? getResources().getColor(R.color.light_orange) : getResources().getColor(R.color.shop_font_black));
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
            case 2:
                mTabTagWidth = getTextViewLength(tvTab3, tvTab3.getText().toString());

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
       int  x = (screenWidth / 3 * (select)) + screenWidth / 6 - mTabTagWidth / 2;
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
    protected void onResume() {
        super.onResume();
   //     mReqDelayMills = REQ_DELAY_MILLS;
    }

    @Override
    protected void onDestroy() {
        release();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(null);
        super.onDestroy();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // 取消长按menu键弹出键盘事�?
                if (event.getRepeatCount() > 0) {
                    return true;
                }
                if (isFullScreen) {
                    backLandscape();
                } else {
                    if (!isExit) {
                        isExit = true;
                        showToast("再按一次退出直播间");
                        handler.sendEmptyMessageDelayed(MSG_EXIT_ROOM, MSG_EXIT_ROOM_DELAY);
                    } else {
                        isPlayComplete=true;
                        leaveRoom();
                        onBackPressed();
                        return true;
                    }
                }

        }
        return false;
    }

    public void release() {
        Log.e(TAG, "release 释放播放端");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (timer!=null){
            timer.cancel();
            task = null;
        }
        if (task!=null){
            task.cancel();
            task = null;
        }

    }
    private void prepare() {

        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(surfaceView.getHolder());
            return;
        }

        try {
            mMediaPlayer = new PLMediaPlayer(mAVOptions);

            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mMediaPlayer.setOnErrorListener(mOnErrorListener);
            mMediaPlayer.setOnInfoListener(mOnInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);

            // set replay if completed
            // mMediaPlayer.setLooping(true);

            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

            mMediaPlayer.setDataSource(mVideoPath);
            mMediaPlayer.setDisplay(surfaceView.getHolder());
            mMediaPlayer.prepareAsync();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PLMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new PLMediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(PLMediaPlayer mp, int width, int height) {
            Log.i(TAG, "onVideoSizeChanged, width = "+ width + ",height = " + height);
            // resize the display window to fit the screen
            if (width != 0 && height != 0) {
            }
        }
    };
    /**
     * 该对象用于监听播放器的 prepare 过程，该过程主要包括：创建资源、
     * 建立连接、请求码流等等，当 prepare 完成后，
     * SDK 会回调该对象的 onPrepared 接口，下一步则可以调用播放器的 start() 启动播放。
     */
    private PLMediaPlayer.OnPreparedListener mOnPreparedListener = new PLMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(PLMediaPlayer mp) {
            Log.i(TAG, "On Prepared !");
            mMediaPlayer.start();
            //TODO  开始计时
           timeOut();
        }
    };
    private PLMediaPlayer.OnInfoListener mOnInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer mp, int what, int extra) {
            Log.i(TAG, "OnInfo, what = " + what + ", extra = " + extra);
            switch (what) {
                case PLMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    llBufferingIndicator.setVisibility(View.VISIBLE);
                    break;
                case PLMediaPlayer.MEDIA_INFO_BUFFERING_END:
                case PLMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    llBufferingIndicator.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    private PLMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new PLMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(PLMediaPlayer mp, int percent) {
            Log.d(TAG, "onBufferingUpdate: " + percent + "%");
        }
    };
    /**
     *对于直播应用而言，播放器本身是无法判断直播是否结束，这需要通过业务服务器来告知。
     * 当主播端停止推流后，播放器会因为读取不到新的数据而产生超时，从而触发 onCompletion 回调。

     建议的处理方式是：在 onCompletion 回调后，查询业务服务器，
     获知直播是否结束，如果已经结束，则关闭播放器，清理资源；如果直播没有结束，则尝试重连。
     *
     */
    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer mp) {
            Log.d(TAG, "Play Completed !");
         //   showToastTips("Play Completed !");
            isPlayComplete=true;
            reConnectRequest();
        }
    };

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            Log.e(TAG, "Error happened, errorCode = " + errorCode);
            llBufferingIndicator.setVisibility(View.GONE);
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                    showToastTips("无效播放地址");
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                   // showToastTips("404 resource not found !");
                    showToast(context.getResources().getString(R.string.unknownException));
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                //    showToastTips("Connection refused !");
                    showToast(context.getResources().getString(R.string.unknownException));
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                 //   showToastTips("Connection timeout !");
                    showToast("连接超时");
                    showOtherLiveData();
                    break;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                 //   showToastTips("Empty playlist !");
                    showToast(context.getResources().getString(R.string.unknownException));
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                  //  showToastTips("Stream disconnected !");
                    showToast(context.getResources().getString(R.string.unknownException));
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:

                    Log.d(TAG, "mOnErrorListener ERROR_CODE_IO_ERROR");
//                    如果申请的直播并没有在推流，或者直播过程中发生网络错误（比如：WiFi 断开），播放器在请求超时或者播放完当前缓冲区中的数据后，会触发 onError 回调，errorCode 通常是 ERROR_CODE_IO_ERROR。
//
//                    这种情况下，通常要做如下判断来考虑是否需要重连：
//
//                    查询业务服务器，获知直播是否结束，如果没有结束，则可以尝试做重连
//                    判断网络是否断开，如果网络连接是正常的，则可以尝试做重连
//                    重连的使用方法，可以参考如何判断直播结束。
//
//                    需要注意，如果决定做重连，则 onError 回调中，请返回 true，否则会导致触发 onCompletion。
                    if(checkState()){
                        reConnectRequest();
                    }
                //    showToastTips("Network IO Error !");
                    break;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                 //   showToastTips("Unauthorized Error !");
                    showToast(context.getResources().getString(R.string.unknownException));
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    //请求流超时    查询业务服务器，获知直播是否结束，如果没有结束，则可以尝试做重连  判断网络是否断开，如果网络连接是正常的，则可以尝试做重连
                    if(checkState()){
                        reConnectRequest();
                    }
                 //   showToastTips("Prepare timeout !");
                    break;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    //  推流端停止推流
                    release();
                    showToast("主播已离开房间");
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                default:
                   // showToastTips("unknown error !");
                    showToast(context.getResources().getString(R.string.unknownException));
                    break;
            }
            return true;
        }
    };
    private MyTimerTask task;
    private Timer timer;
    private int totalTime = 0;
    private void timeOut() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer == null) {
            timer = new Timer();
        }
        if (task == null) {
            task = new MyTimerTask();
        }
        timer.schedule(task, 1000, 1000);
    }
    View.OnClickListener itemOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String sharetitle = liveRoomInfo.getInfo().getTitle();
            String sharecontent = liveRoomInfo.getInfo().getNickname()+getResources().getString(R.string.live_share);
            String shareurl = HttpConstant.SERVICE_HTTP_AREA
                    +HttpConstant.LIVE_SHARE+id;
            String imgurl = HttpConstant.SERVICE_UPLOAD_AREA
                    +liveRoomInfo.getInfo().getIcon();
            switch (v.getId()) {
                case R.id.llSina:
                    shareToFriendsUtil.shareBySina(sharetitle, sharecontent, shareurl, imgurl);
                    break;
                case R.id.llWeChat:
                    shareToFriendsUtil.shareWyByWXFriend(sharetitle, sharecontent, shareurl, imgurl, 0);
                    break;
                case R.id.llFriend:
                    shareToFriendsUtil.shareWyByWXFriend(sharetitle, sharecontent, shareurl, imgurl, 1);
                    break;
                case R.id.llQQ:
                    shareToFriendsUtil.shareByQQ(sharetitle, sharecontent, shareurl, imgurl);
                    break;
            }
        }
    };

    @Override
    public void onResponse(BaseResponse baseResponse) {
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                Toast.makeText(this, R.string.weibo_share_success, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Toast.makeText(this, R.string.weibo_share_cancel, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Toast.makeText(this, getResources().getString(R.string.weibo_share_fail) + "Error Message: " + baseResponse.errMsg,
                        Toast.LENGTH_LONG).show();
                break;
        }
    }

    public class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            totalTime++;
            handler.sendEmptyMessage(MSG_PLAY_TIMES_REQUEST);
        }
    }
    private void showToastTips(final String tips) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LiveRoomActivity.this, tips, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void reConnect(){
        if (mMediaPlayer==null){
            return;
        }
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDisplay(surfaceView.getHolder());
            mMediaPlayer.setDataSource(mVideoPath);
            mMediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivFullScreenBtn:
                if(isFullScreen){
                    backLandscape();
                }else{
                    changeLandscape();
                }
                break;
            case R.id.tvTab1:
                setTabSelect(0);
                moretabViewPager.setCurrentItem(0);
                break;
            case R.id.tvTab2:
                setTabSelect(1);
                moretabViewPager.setCurrentItem(1);
                break;
            case R.id.tvTab3:
                setTabSelect(2);
                moretabViewPager.setCurrentItem(2);
                break;
            case R.id.ivBack:
                if(isFullScreen) {
                    backLandscape();
                }else{
                    isPlayComplete=true;
                    leaveRoom();
                    onBackPressed();
                }
                break;
            case  R.id.ivGamePic1:
                jumpOtherLive(0);
                break;
            case  R.id.ivGamePic2:
                jumpOtherLive(1);
                break;
            case R.id.tvContinue:
                setNoWifiData(false);
                reConnectRequest();
                LogUtil.d(TAG,"onClick 重新连接");
                break;
            case R.id.ivShareIcon:
                if (popwin != null) {
                    popwin.show();
                } else {
                    popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
                    popwin.setOnItemClick(itemOnClick);
                    shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
                    popwin.show();
                }
                break;
        }

    }
    private void jumpOtherLive(int num){
        Intent intent=null;
        if(liveRoomInfo.getOtherLive().get(num).getType()==1) {
            intent = new Intent(this, LiveRoomActivity.class);
            intent.putExtra("id",  liveRoomInfo.getOtherLive().get(num).getId() + "");
        }else{
            intent = new Intent(context, PlayVideoActivity.class);
            intent.putExtra("videoId", liveRoomInfo.getOtherLive().get(num).getId() + "");
        }
        startActivity(intent);
        finish();
    }
    @Override
    public void updataVideoData(int page) {
        this.page=page;
        getLiveRoomData(page);
    }


    // 底部Tab切换
    public class MyPagerAdapter extends FragmentPagerAdapter {

        // tab标题
        private String[] TITLES = { "信息", "聊天", "往期" };


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragements[position];
        }

    }

    /**
     * 设置没有wifi的数据
     */
   private void setNoWifiData(boolean isShow){
       if(isShow) {
           llNodata.setVisibility(View.VISIBLE);
           tvContinue.setVisibility(View.VISIBLE);
           tvContinue.setOnClickListener(this);
           tvNoAnchorHint.setText(getString(R.string.no_wifi_hint));
           llBufferingIndicator.setVisibility(View.GONE);
       }else{
           llNodata.setVisibility(View.GONE);
           tvContinue.setVisibility(View.GONE);
           tvContinue.setOnClickListener(null);
           llBufferingIndicator.setVisibility(View.VISIBLE);
       }
   }
    private boolean checkState(){
       int type= Utils.checkNetworkState();
        if(type==0){
         showToast(getString(R.string.noNeteork));
            return false;
        }else if(type==1){

        }else if(type==2){
            setNoWifiData(true);
            //暂停播放
            if(mMediaPlayer!=null){
                mMediaPlayer.stop();
            }
            return false;
        }else if(type==3){
        }
        return true;
    }

}
