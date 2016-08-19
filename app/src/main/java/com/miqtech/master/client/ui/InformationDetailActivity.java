package com.miqtech.master.client.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.InformationRecyclerAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.FirstCommentDetail;
import com.miqtech.master.client.entity.InfoRecommend;
import com.miqtech.master.client.entity.Information;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ShareToFriendsUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.view.ExpertMorePopupWindow;
import com.miqtech.master.client.view.videoControlLayout.MediaController;
import com.miqtech.master.client.view.videoControlLayout.VerticalSeekBar;
import com.miqtech.master.client.watcher.Observerable;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PlayerCode;
import com.pili.pldroid.player.widget.VideoView;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by wuxn on 2016/4/11.
 */
public class InformationDetailActivity extends BaseActivity implements InformationRecyclerAdapter.MyClickListener
        , IjkMediaPlayer.OnCompletionListener,
        IjkMediaPlayer.OnInfoListener,
        IjkMediaPlayer.OnErrorListener,
        IjkMediaPlayer.OnPreparedListener,
        MediaController.ScreenChange, IWeiboHandler.Response,
        View.OnClickListener, InformationRecyclerAdapter.ProcessTheData {
    private Map<String, String> params = new HashMap<String, String>();
    private String id;
    private String categoryId;
    private String pid;
    private Information information;
    @Bind(R.id.header)
    View header;
    @Bind(R.id.rvContent)
    RecyclerView rvContent;
    @Bind(R.id.ll_detail_comment)
    LinearLayout ll_detail_comment;

    @Bind(R.id.aspect_layout)
    RelativeLayout aspect_layout;
    @Bind(R.id.video_view)
    VideoView video_view;
    @Bind(R.id.ib_stop_play_btn)
    ImageButton ib_stop_play_btn;
    @Bind(R.id.ivStartBtn)
    ImageButton ivStartBtn;
    @Bind(R.id.play_control_view)
    ViewStub play_control_view;
    @Bind(R.id.include_buffer_indicator)
    View include_buffer_indicator;
    @Bind(R.id.ll_volume_up_down)
    RelativeLayout ll_volume_up_down;
    @Bind(R.id.sb_volume_progress)
    VerticalSeekBar sb_volume_progress;
    @Bind(R.id.ib_video_lock_screen)
    ImageButton ib_video_lock_screen;
    @Bind(R.id.video_bg_black)
    View video_bg_black;
    @Bind(R.id.tvCommentNum)
    TextView tvCommentNum;
    @Bind(R.id.llComment)
    LinearLayout llComment;
    @Bind(R.id.rlInformation)
    RelativeLayout rlInformation;
    private Observerable observerable = Observerable.getInstance();
    private InformationRecyclerAdapter adapter;
    private Context context;
    private ArrayList<FirstCommentDetail> comments = new ArrayList<FirstCommentDetail>();

    public static final int INFORMATION_IMAGE_TEXT_TYPE = 1;

    public static final int INFORMATION_VIDEO = 2;

    private ShareToFriendsUtil shareToFriendsUtil;
    private ExpertMorePopupWindow popwin;


    //资讯内容数量
    private int infoNum = 0;
    //资讯推荐数量
    private int recommendNum = 0;

    private int type;
    private boolean isLockScreen; //是否是锁屏状态

    private boolean isVolumeOpen; // 是否有声音
    private static final int HideLockScreen = 1;
    private User user;
    private int current;//item在数据中的位置
    private Dialog mDialog;
    private String replyListId;//评论楼中楼id
    private int replyListPosition;//在楼中楼数据中的位置
    private final int ISREFER = 1;
    private int isRefre = 0;//返回时0表示不需要刷新，1需要刷新
    private boolean isBackActivity = false;
    private Animation videoInAnimation;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                ib_video_lock_screen.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
        popwin.setOnItemClick(itemOnClick);
        shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
        initSinaSso(savedInstanceState);
    }

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_informationdetail);
        initView();
        initData();
        initVideoData();
        setOnclickListener();
    }

    public void initSinaSso(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            shareToFriendsUtil.getIWeiApiInstance(this).handleWeiboResponse(getIntent(), this);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        type = getIntent().getIntExtra("type", 1);
        id = getIntent().getStringExtra("id");
        loadInformationDetail();
        lengthCoding = UMengStatisticsUtil.CODE_1003;
        lengthTargetId = id;
    }


    @Override
    protected void initView() {
        super.initView();
        ButterKnife.bind(this);
        context = this;
        setLeftBtnImage(R.drawable.back);
        setRightBtnImage(R.drawable.icon_share_oranger);
        setRightOtherBtnImage(R.drawable.icon_collection);
        getLeftBtn().setOnClickListener(this);
        getRightOtherBtn().setOnClickListener(this);
        getRightBtn().setOnClickListener(this);
        llComment.setOnClickListener(this);
    }

    private void loadInformationDetail() {
        showLoading();
        params.clear();
        if (WangYuApplication.getUser(context) != null) {
            params.put("userId", WangYuApplication.getUser(context).getId());
        }
            params.put("id", id);
       sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INFORMATION_DETAIL, params, HttpConstant.INFORMATION_DETAIL);

    }

    private void loadOfficalCommentList() {
        HashMap params = new HashMap();
        params.put("amuseId", id);
        params.put("page", 1 + "");
        params.put("type", 3 + "");//	评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1）3资讯
        params.put("replySize", 10 + "");
        if (WangYuApplication.getUser(context) != null) {
            params.put("userId", WangYuApplication.getUser(context).getId() + "");
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_COMMENT_LIST, params, HttpConstant.AMUSE_COMMENT_LIST);
    }

    private void initRecreationComment(JSONObject object) {
        try {
            if (object.getInt("code") == 0 && object.has("object")) {
                String strObj = object.getString("object");
                JSONObject jsonObj = new JSONObject(strObj);
                if (jsonObj.has("list")) {
                    String strList = jsonObj.getString("list").toString();
                    List<FirstCommentDetail> comments = GsonUtil.getList(strList, FirstCommentDetail.class);
                    this.comments.clear();
                    this.comments.addAll(comments);
                    adapter.notifyDataSetChanged();
                    adapter.setProcessTheData(this);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收藏或者取消
     */
    private void collectInformation() {
        User user = WangYuApplication.getUser(context);
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("infoId", id + "");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INFO_FAV, map, HttpConstant.INFO_FAV);
        } else {
            ((BaseActivity) context).showToast("请登录");
            Intent intent = new Intent();
            intent.setClass(context, LoginActivity.class);
            ((BaseActivity) context).startActivityForResult(intent, LoginActivity.LOGIN_OK);
        }
    }

    private void setCollectViewType(int type) {
        if (type == 1) {
            setRightOtherBtnImage(R.drawable.icon_collectioned);
        } else if (type == 0) {
            setRightOtherBtnImage(R.drawable.icon_collection);
        }
    }

    /**
     * 用于计算recyleview中的位置
     */
    private void setInformationChildNum() {
        if (information != null) {
            if (information.getInfo() != null) {
                infoNum = 1;
            }
            if (information.getRecommend()!=null && information.getRecommend().size() > 0) {
                recommendNum = information.getRecommend().size();
            }
        }
    }


    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        if (HttpConstant.INFORMATION_DETAIL.equals(method)) {
            if (object.has("object")) {
                try {
                    information =new Gson().fromJson(object.getString("object"), Information.class);
                    LogUtil.d(TAG,"集合是否为空"+information.getRecommend().size());
                    setCollectViewType(information.getInfo().getFaved());
                    tvCommentNum.setText(information.getInfo().getComments_num() + "");
                    adapter = new InformationRecyclerAdapter(this, information, this,
                            information.getInfo().getType() == 4 ? INFORMATION_VIDEO : INFORMATION_IMAGE_TEXT_TYPE, comments);
                    rvContent.setLayoutManager(new LinearLayoutManager(this));
                    rvContent.setAdapter(adapter);
                    loadOfficalCommentList();
                    setInformationChildNum();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (HttpConstant.INFORMATION_UPORDOWN.equals(method)) {

        } else if (HttpConstant.AMUSE_COMMENT_LIST.equals(method)) {
            initRecreationComment(object);
        } else if (HttpConstant.INFO_FAV.equals(method)) {
            int collectInt = object.optInt("object");//1表示收藏成功 0表示取消收藏成功 -1表示操作失败
            if (collectInt == 1) {
                setRightOtherBtnImage(R.drawable.icon_collectioned);
                observerable.notifyChange(Observerable.ObserverableType.COLLECTSTATE, 3, information.getInfo().getId(), true);
            } else if (collectInt == 0) {
                observerable.notifyChange(Observerable.ObserverableType.COLLECTSTATE, 3, information.getInfo().getId(), false);
                setRightOtherBtnImage(R.drawable.icon_collection);
            } else if (collectInt == -1) {
                showToast(getResources().getString(R.string.operationFailure));
            }
        } else if (method.equals(HttpConstant.DEL_COMMENT)) {//删除
            isRefre = 1;
            deleteForSuccess();
        } else if (method.equals(HttpConstant.V2_COMMENT_PRAISE)) {//点赞
            isRefre = 1;
            BroadcastController.sendUserChangeBroadcase(context);
            updatePraiseStatu();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        hideLoading();
        super.onFaild(object, method);
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
//        showToast(errMsg);
    }


    @Override
    public void upOrDownListener(int type) {
        upOrDownHanle(type);
    }

    @Override
    public void recommendListener(InfoRecommend recommend) {
        Intent intent = new Intent();
        intent.setClass(this, InformationDetailActivity.class);
        intent.putExtra("id", recommend.getId() + "");
        intent.putExtra("type", type);
        startActivity(intent);
    }

    @Override
    public void videoListener(String videoUrl) {
        //     videoUrl = "http://img.wangyuhudong.com/d83479578b594ffe8a7d724b2e6e3cd5_1.mp4";
        String url = videoUrl.replace(".mp4", ".m3u8");
        //TODO 有问题需要修改
        rvContent.scrollToPosition(2);
        if (rvContent.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            initPlayerView();
            if (aspect_layout != null) {
                Log.i("initVideoData", "加载动画");
                //动画有问题
                aspect_layout.setVisibility(View.VISIBLE);
                video_bg_black.setVisibility(View.VISIBLE);
                aspect_layout.startAnimation(videoInAnimation);
            }
            mVideoPath = videoUrl;
            LogUtil.d(TAG,"播放地址"+mVideoPath);
            video_view.setVideoPath(mVideoPath);
            video_view.requestFocus();
            video_view.start();
            //播放按钮消失
            ivStartBtn.setVisibility(View.GONE);
            if (include_buffer_indicator != null) {
                include_buffer_indicator.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void imageListener() {

    }

    private static final String TAG = "VideoPlayerActivity";
    private static final int REQ_DELAY_MILLS = 3000;

    private MediaController mMediaController;
    private ViewGroup.LayoutParams mLayoutParams;
    private ViewGroup.LayoutParams videoViewLayoutParams;
    private Pair<Integer, Integer> mScreenSize;

    private String mVideoPath;
    private long mLastPosition = 0;
    private boolean mIsLiveStream = false;

    private int mReqDelayMills = REQ_DELAY_MILLS;
    private boolean mIsCompleted = false;
    private Runnable mVideoReconnect;
    private float radio = 3f / 5f; //屏幕的宽高比
    private int screenHeight;
    private int screenWith;
    public boolean isFullScreen = false;
    private int currentVolume; // 当前音量的大小
    private int maxVolume = 0;// 最大媒体音量
    private AudioManager audioManager;

    private void initVideoData() {
        audioManager = (AudioManager) this
                .getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        videoInAnimation = AnimationUtils.loadAnimation(InformationDetailActivity.this,
                R.anim.video_in_anim);
        videoInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ivStartBtn.setVisibility(View.GONE);
                Log.i("initVideoData", "initVideoData 动画开始");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                aspect_layout.setEnabled(true);
                Log.i("initVideoData", "initVideoData 动画结束");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        boolean useFastForward = true;
        boolean disableProgressBar = false;
        mIsLiveStream = true;
        if (mIsLiveStream) {
            disableProgressBar = true;
            useFastForward = false;
        }
        initPlayerView(); // 初始化视频播放宽度和高度
        mMediaController = new MediaController(this, useFastForward, disableProgressBar, play_control_view);
        video_view.setMediaController(mMediaController);
        video_view.setMediaBufferingIndicator(include_buffer_indicator);
        aspect_layout.setOnTouchListener(onTouchListener);
        AVOptions options = new AVOptions();
        options.setInteger(AVOptions.KEY_MEDIACODEC, 0); // 1 -> enable, 0 -> disable

        Log.i(TAG, "mIsLiveStream:" + mIsLiveStream);
        //设置视频播放的一参数
        if (mIsLiveStream) {
            options.setInteger(AVOptions.KEY_BUFFER_TIME, 1000); // the unit of buffer time is ms
            options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000); // the unit of timeout is ms
            options.setString(AVOptions.KEY_FFLAGS, AVOptions.VALUE_FFLAGS_NOBUFFER); // "nobuffer"
            options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);
        }
        video_view.setAVOptions(options);
        //设置缓冲指示器
        video_view.setMediaBufferingIndicator(include_buffer_indicator);
    }

    /**
     * 初始化播放器大小
     * 宽高比4：3
     */
    private void initPlayerView() {
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int videoHeight = (int) (screenWidth * radio);
        ViewGroup.LayoutParams lp = aspect_layout.getLayoutParams();
        videoViewLayoutParams = video_view.getLayoutParams();
        videoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        videoViewLayoutParams.height = videoHeight;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = videoHeight;
        aspect_layout.setLayoutParams(lp);
        video_view.setLayoutParams(videoViewLayoutParams);
    }

    private void setOnclickListener() {

        video_view.setOnErrorListener(this);
        video_view.setOnCompletionListener(this);
        video_view.setOnInfoListener(this);
        video_view.setOnPreparedListener(this);
        // video_view.setOnVideoSizeChangedListener(this);
        ivStartBtn.setOnClickListener(this);
        ib_video_lock_screen.setOnClickListener(this);
        ib_stop_play_btn.setOnClickListener(this);
    }

    private void upOrDownHanle(int type) {
        if (information != null && information.getInfo() != null) {
            User user = WangYuApplication.getUser(context);
            showLoading();
            params.clear();
            params.put("id", information.getInfo().getId() + "");
            params.put("userId", user.getId());
            params.put("token", user.getToken());
            params.put("type", type + "");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INFORMATION_UPORDOWN, params, HttpConstant.INFORMATION_UPORDOWN);
        }
    }

    @Override
    public void onBackPressed() { // android系统调用
        if (isBackActivity) {
            super.onBackPressed();
            isBackActivity = false;
        }
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
                    if (isLockScreen) {
                        return true;
                    }
                    backLandscape();
                    if (mMediaController != null) {
                        mMediaController.changeFullScreenPic();
                    }
                } else if (aspect_layout != null && aspect_layout.getVisibility() == View.VISIBLE) {
                    video_view.stopPlayback();
                    aspect_layout.setVisibility(View.GONE);
                    video_view.setVideoPath("");
                    if (mMediaController != null) {
                        mMediaController.clearResouce();
                    }
                    return true;
                } else {
                    isBackActivity = true;
                    onBackPressed();
                    return true;
                }
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                //TODO 音量减
                currentVolume -= 1;
                if (currentVolume <= 0) {
                    currentVolume = 0;
                    isVolumeOpen = false;
                    closeMuteSoundState();
                }
                return super.onKeyDown(keyCode, event);
            case KeyEvent.KEYCODE_VOLUME_UP:
                //TODO 音量加
                currentVolume += 1;
                isVolumeOpen = true;
                mMediaController.showMutePic();
                if (currentVolume >= maxVolume) {
                    currentVolume = maxVolume;
                }
                return super.onKeyDown(keyCode, event);
            case KeyEvent.KEYCODE_HOME:
                //TODO home键
                return true;
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("onStop", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFullScreen) {
            backLandscape();
            isFullScreen = false;
        }
        if (video_view.isPlaying()) {
            video_view.pause();
        }
        mMediaController.updatePausePlay();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //把锁屏滞空
        if (isLockScreen) {
            ib_video_lock_screen.setVisibility(View.GONE);
            ib_video_lock_screen.setSelected(false);
            isLockScreen = false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
    }

    private void changeLandscape() {
        Log.i("changeLandscape", "变换横屏");
        hideTitleAndComment();
        aspect_layout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 全屏
        isFullScreen = true;
    }

    private void backLandscape() {
        Log.i("changeLandscape", "变换竖屏");
        //影藏锁屏图标
        ib_video_lock_screen.setVisibility(View.GONE);
        ll_volume_up_down.setVisibility(View.GONE);
        int screenWidth = getWindowManager().getDefaultDisplay().getHeight();
        int videoHeight = (int) (screenWidth * radio);
        aspect_layout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, videoHeight));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 还原
        showTitleAndComment();
        isFullScreen = false;
    }

    private void hideTitleAndComment() {
        if (header != null && header.getVisibility() == View.VISIBLE) {
            header.setVisibility(View.GONE);
        }
        if (ll_detail_comment != null && ll_detail_comment.getVisibility() == View.VISIBLE) {
            ll_detail_comment.setVisibility(View.GONE);
        }
    }

    private void showTitleAndComment() {
        if (header != null && header.getVisibility() == View.GONE) {
            header.setVisibility(View.VISIBLE);
        }
        if (ll_detail_comment != null && ll_detail_comment.getVisibility() == View.GONE) {
            ll_detail_comment.setVisibility(View.VISIBLE);
        }
    }
    //视屏播放时候的回调方法

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        mIsCompleted = true;
        include_buffer_indicator.setVisibility(View.GONE);
        ivStartBtn.setVisibility(View.VISIBLE);
        ib_video_lock_screen.setVisibility(View.GONE);
        isLockScreen = false;
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
        Log.d(TAG, "onError what=" + what + ", extra=" + extra);
        if (what == -10000) {
            //无效播放地址  缓冲指示器消失
            if (extra == PlayerCode.EXTRA_CODE_INVALID_URI || extra == PlayerCode.EXTRA_CODE_EOF) {
                if (include_buffer_indicator != null)
                    include_buffer_indicator.setVisibility(View.GONE);
                return true;
            }
            //播放完成  并且没有播放列表  重新播放
            if (mIsCompleted && extra == PlayerCode.EXTRA_CODE_EMPTY_PLAYLIST) {
                Log.d(TAG, "mVideoView reconnect!!!");
                video_view.removeCallbacks(mVideoReconnect);
                mVideoReconnect = new Runnable() {
                    @Override
                    public void run() {
                        video_view.setVideoPath(mVideoPath);
                    }
                };
                video_view.postDelayed(mVideoReconnect, mReqDelayMills);
                mReqDelayMills += 200;
            } else if (extra == PlayerCode.EXTRA_CODE_404_NOT_FOUND) {
                // NO ts exist
                if (include_buffer_indicator != null)
                    include_buffer_indicator.setVisibility(View.GONE);
            } else if (extra == PlayerCode.EXTRA_CODE_IO_ERROR) {
                // NO rtmp stream exist
                if (include_buffer_indicator != null)
                    include_buffer_indicator.setVisibility(View.GONE);
            }
        }
        // return true means you handle the onError, hence System wouldn't handle it again(popup a dialog).
        return true;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
        Log.d(TAG, "onInfo what=" + what + ", extra=" + extra);

        if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            Log.i(TAG, "onInfo: (MEDIA_INFO_VIDEO_RENDERING_START)");
            if (mMediaController != null) {
                mMediaController.setControlView(true);
            }
            if (include_buffer_indicator != null)
                include_buffer_indicator.setVisibility(View.GONE);
        } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
            Log.i(TAG, "onInfo: (MEDIA_INFO_BUFFERING_START)");
            if (include_buffer_indicator != null)
                include_buffer_indicator.setVisibility(View.VISIBLE);
        } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            Log.i(TAG, "onInfo: (MEDIA_INFO_BUFFERING_END)");
            if (include_buffer_indicator != null)
                include_buffer_indicator.setVisibility(View.GONE);
        } else if (what == IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START) {
            //      Toast.makeText(this, "Audio Start", Toast.LENGTH_LONG).show();
            Log.i(TAG, "duration:" + video_view.getDuration());
        } else if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            //     Toast.makeText(this, "Video Start", Toast.LENGTH_LONG).show();
            Log.i(TAG, "duration:" + video_view.getDuration());
        }
        return true;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        include_buffer_indicator.setVisibility(View.GONE);
        mReqDelayMills = REQ_DELAY_MILLS;
        mReqDelayMills = REQ_DELAY_MILLS;
        video_bg_black.setVisibility(View.GONE);
        mMediaController.show(3000);
        mIsCompleted = false;
    }
    //TODO 此处是获取视频的原始宽高    项目中的宽高暂时写死了
//    @Override
//    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int width, int height, int sarNum, int sarDen) {
//        Log.d(TAG, "onVideoSizeChanged " + iMediaPlayer.getVideoWidth() + "x" + iMediaPlayer.getVideoHeight() + ",width:" + width + ",height:" + height + ",sarDen:" + sarDen + ",sarNum:" + sarNum);
//        //w: 240 *  h: 180
//        if (width < height) {
//            // land video  横屏
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//            mScreenSize = Util.getResolution(this);
//        } else {
//            // port video 竖屏
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
//            mScreenSize = Util.getResolution(this);  //1080 * 1920
//            //    }
//
//            if (width < mScreenSize.first) {
//                height = mScreenSize.first * height / width; //810
//                width = mScreenSize.first; //1080
//            }
//
////        if (width * height < mScreenSize.first * mScreenSize.second) {
////            width = mScreenSize.second * width / height;  //2560
////            height = mScreenSize.second;   //1920
////        }
//            mLayoutParams = aspect_layout.getLayoutParams();
//            videoViewLayoutParams=video_view.getLayoutParams();
//            //视屏播放的高度写死
//            mLayoutParams.width = width;  //1080
//            mLayoutParams.height = (int)(width*radio); //810
//            Log.i("VideoPlayerActivity","mLayoutParams.height"+mLayoutParams.height+"::"+mLayoutParams.width);
//            videoViewLayoutParams.width=width;
//            videoViewLayoutParams.height= (int)(width*radio);
//            video_view.setLayoutParams(videoViewLayoutParams);
//            aspect_layout.setLayoutParams(mLayoutParams);
//        }
//    }

    @Override
    public void changeToLandscape() {
        changeLandscape();

    }

    @Override
    public void changeToPortrait() {
        backLandscape();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivStartBtn: //开始播放
                video_view.setVideoPath(mVideoPath);
                video_view.requestFocus();
                video_view.start();
                //播放按钮消失
                ivStartBtn.setVisibility(View.GONE);
                if (include_buffer_indicator != null) {
                    include_buffer_indicator.setVisibility(View.VISIBLE);
                }
                //       mMediaController.doPauseResume();
                break;
            case R.id.ib_stop_play_btn:
                if (isFullScreen && isLockScreen) {
                    return;
                }
                quitVideo();
                break;
            case R.id.ib_video_lock_screen:
                if (ib_video_lock_screen.isSelected() && isFullScreen) {
                    ib_video_lock_screen.setSelected(false);
                    isLockScreen = false;
                    ib_stop_play_btn.setVisibility(View.VISIBLE);
                    if (!isLockScreen) {
                        mMediaController.show(3000);
                    }
                } else {
                    ib_video_lock_screen.setSelected(true);
                    isLockScreen = true;
                    mMediaController.hide();
                    ib_stop_play_btn.setVisibility(View.GONE);
                }
                break;
            case R.id.ibRight1:
                collectInformation();
                break;
            case R.id.ibRight:
                if (popwin != null) {
                    popwin.show();
                } else {
                    popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
                    popwin.setOnItemClick(itemOnClick);
                    shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
                    popwin.show();
                }
                break;
            case R.id.ibLeft:
                isBackActivity = true;
                onBackPressed();
                break;
            case R.id.llComment:
                if (information != null) {
                    Intent intent = new Intent();
                    intent.setClass(this, CommentsSectionActivity.class);
                    intent.putExtra("amuseId", information.getInfo().getId() + "");
                    intent.putExtra("type", 3);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (shareToFriendsUtil != null && shareToFriendsUtil.getmSsoHandler() != null) {
//            shareToFriendsUtil.getmSsoHandler().authorizeCallBack(requestCode, resultCode, data);
//        }
        if (requestCode == ISREFER) {
            int myIsRefre = data.getIntExtra("isRefre", -1);
            if (myIsRefre == 1) {
                loadOfficalCommentList();
            }
        }
    }


    View.OnClickListener itemOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (information == null) {
                return;
            }
            String sharetitle = "";
            String sharecontent = "";
            if (type == INFORMATION_IMAGE_TEXT_TYPE) {
                sharetitle = "电竞娱乐资讯-" + information.getInfo().getTitle();
                sharecontent = information.getInfo().getBrief();
            } else if (type == INFORMATION_VIDEO) {
                sharetitle = "火热视频-" + information.getInfo().getTitle();
                sharecontent = information.getInfo().getKeyword();
            }
            String shareurl = HttpConstant.SERVICE_HTTP_AREA
                    + HttpConstant.INFORMATION_URL + information.getInfo().getId();
            String imgurl = HttpConstant.SERVICE_UPLOAD_AREA
                    + information.getInfo().getIcon();
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

    private void quitVideo() {
        if (isFullScreen) {
            backLandscape();
        } else {
            if (aspect_layout != null) {
                video_view.stopPlayback();
                video_view.setVideoPath("");
                aspect_layout.setVisibility(View.GONE);
                aspect_layout.clearAnimation();
            }
            if (mMediaController != null) {
                mMediaController.clearResouce();
            }

            //         getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        }
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        float startX;
        float startY;
        long toTime; // 播放的进度位置
        long startTime = 0;
        // 滑动全长的时间

        // 根据窗口（surfaceView）大小，计算出的横向滑动时毫秒/像素
        float secondsPerPixel;

        // 滑动时确定方向的临界值
        final static float check = 30;

        // 横向滑动整屏跳转的时间
        public final static int PROGRESS_VER_SCREEN = 3 * 60 * 1000;

        // 每像素等于多少音量

        /** 是否是调节进度 */
        boolean isChangeProgress = false;
        /** 是否是调节音量 */
        boolean isChangeVolume = false;
        /** 是否是调节亮度 */
        boolean isChangeLight = false;
        int startLight;
        int startVolume;
        long progress;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //全屏  且 锁屏 没有触摸事件   非全屏没有触摸事件
            //TODO 锁屏判断
            Log.i("onTouch", "onTouch2222222" + isFullScreen);
            if (!isFullScreen) {
                return true;
            }
            Log.i("onTouch", "onTouch111111111" + isLockScreen);
            if (isFullScreen && isLockScreen) {
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (isChangeProgress) {
                    mMediaController.changeProgressAndTime(progress, toTime);
                    return true;
                } else if (isChangeVolume) {
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    mMediaController.setCurrentVolume(currentVolume);
                    return true;
                }
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                secondsPerPixel = (float) PROGRESS_VER_SCREEN
                        / (float) v.getWidth(); // 166.66667 ???
                startX = event.getX();
                startY = event.getY();

                startTime = video_view.getCurrentPosition();
                sb_volume_progress.setProgress(currentVolume);   //设置当前音量
                toTime = startTime;
                isChangeProgress = false;
                isChangeLight = false;
                isChangeVolume = false;
                startVolume = currentVolume; // 0??
                Log.i("onTouch", "onTouch" + secondsPerPixel + ":::" + startX + "::" + startY + ":::" + startTime + "::" + currentVolume);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                Log.i("onTouch", " event.getAction() == MotionEvent.ACTION_MOVE");
                float endX = event.getX();
                float endY = event.getY();

                // 计算出横纵两方向的偏离值
                double moveX = (endX - startX);
                double moveY = (endY - startY);

                // 横纵方向的偏离值谁先到达临界值，方向便是谁
                if ((!isChangeProgress) && (!isChangeVolume)) {
                    if (Math.abs(moveX) > check) {
                        isChangeProgress = true;
                        Log.i("onTouch", " isChangeProgress = true;");
                    } else if (Math.abs(moveY) > check) {
                        //如果需要控制亮度  就打开此处
                        isChangeVolume = true;
                        Log.i("onTouch", " isChangeVolume = true;");
                    }
                } else {
                    if (isChangeProgress) {
//                        if(titleLayoutPort!=null && titleLayoutPort.getVisibility()==View.INVISIBLE
//                                && controlLayout!=null && controlLayout.getVisibility()==View.GONE) {
//                            onContainerClick();
//
                        //让控制界面显示出来

                        mMediaController.show(3000);
                        isChangeProgress = true;
                        // 根据横向偏离值的正负，判断快进或者快退
                        toTime = (int) (startTime + (moveX * secondsPerPixel));
                        progress = (toTime * 1000) / video_view.getDuration();
                        if (toTime < 0) {
                            toTime = 0;
                        } else if (toTime >= video_view.getDuration()) { // 获取最大进度值
                            toTime = video_view.getDuration();
                        }
                        mMediaController.changeProgressAndTime(progress, toTime);
                        video_view.seekTo(toTime);
                        Log.i("onTouch", " isChangeVolume = true;" + toTime + "::" + progress);
                    } else if (isChangeVolume) {
                        showVolume();
                        float addVolume = (startY - endY) / v.getHeight()
                                * maxVolume * 4f;
                        int toVolume = (int) (startVolume + addVolume);
                        if (toVolume > maxVolume) {
                            toVolume = maxVolume;
                        } else if (toVolume < 0) {
                            toVolume = 0;
                            closeMuteSoundState();
                        } else {
                            openMuteSoundState();
                        }
                        sb_volume_progress.setProgress(toVolume);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                toVolume, 0);
                    }
                }

            }
            return true;
        }
    };

    private void showVolume() {
        if (null != ll_volume_up_down && isFullScreen && !isLockScreen) {
            ll_volume_up_down.setVisibility(View.VISIBLE);
        }
        hideVolumeAfterMillis();   //2秒之后音量键消失
    }

    protected void hideVolumeAfterMillis() {
        ll_volume_up_down.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != ll_volume_up_down && ll_volume_up_down.getVisibility() == View.VISIBLE) {
                    ll_volume_up_down.setVisibility(View.GONE);
                }
            }
        }, 2000);
    }

    private void openMuteSoundState() {
        isVolumeOpen = !isVolumeOpen;
        //TODO
        mMediaController.showMutePic();
        //    ibMuteSound.setImageResource(com.youku.player.ui.R.drawable.vedio_sound_open);
    }

    private void closeMuteSoundState() {
        isVolumeOpen = !isVolumeOpen;
        mMediaController.closeMutePic();

    }

    public boolean getLockScreenState() {
        return isLockScreen;
    }

    public boolean isLockScreenVisible() {
        if (ib_video_lock_screen.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            return false;
        }
    }

    public void showOrHideLockScreen() {
        if (isFullScreen) {
            if (ib_video_lock_screen.getVisibility() == View.GONE) {
                ib_video_lock_screen.setVisibility(View.VISIBLE);
                mHandler.removeMessages(HideLockScreen);
                mHandler.sendEmptyMessageDelayed(HideLockScreen, 3000);
            }
        }
    }

    public boolean isPlayComplete() {
        return mIsCompleted;
    }

    public void setLockScreenBtnEnable(boolean isEnable) {
        ib_video_lock_screen.setEnabled(isEnable);
    }

    @Override
    public void delectComment(String id, int position) {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            creatDialogForDelect(id);
            current = position;
        } else {
            toLogin();
        }
    }

    @Override
    public void deleteReplyReply(String id, int position, int replyPosition) {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            replyListId = id + "";
            replyListPosition = replyPosition;
            creatDialogForDelect(id);
            current = position;
        } else {
            toLogin();
        }
    }

    @Override
    public void praiseComment(String id, int position) {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            current = position - infoNum - recommendNum;
            Map<String, String> map = new HashMap<>();
            map.put("commentId", id + "");
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.V2_COMMENT_PRAISE, map, HttpConstant.V2_COMMENT_PRAISE);
        } else {
//            showToast(getResources().getString(R.string.pleaseLogin));
            toLogin();
        }
    }

    @Override
    public void replyComment(String id, int position, String nikeName) {
        Intent intent = new Intent(context, PersonalCommentDetail.class);
        intent.putExtra("amuseId", information.getInfo().getId() + "");
        intent.putExtra("type", 3);
        intent.putExtra("parentId", id);
        intent.putExtra("isPopupKeyboard", ISREFER);//判断是否弹出键盘   1  弹出  0不弹出   默认不弹
//        startActivity(intent);
        startActivityForResult(intent, 1);
    }

    @Override
    public void lookComment(String id, int position, String nikeName) {
        Intent intent = new Intent(context, PersonalCommentDetail.class);
        intent.putExtra("amuseId", information.getInfo().getId() + "");
        intent.putExtra("type", 3);
        intent.putExtra("parentId", id);
        intent.putExtra("isPopupKeyboard", 0);
//        startActivity(intent);
        startActivityForResult(intent, ISREFER);
    }

    private void creatDialogForDelect(final String id) {
        mDialog = new Dialog(context, R.style.register_style);
        mDialog.setContentView(R.layout.dialog_register_marked_words);
        TextView title = (TextView) mDialog.findViewById(R.id.dialog_title_register);
        TextView yes = (TextView) mDialog.findViewById(R.id.dialog_register_yes_pact);
        TextView no = (TextView) mDialog.findViewById(R.id.dialog_register_no_pact);
        View line = mDialog.findViewById(R.id.dialog_line_no_pact);
        line.setVisibility(View.VISIBLE);
        no.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText("是否删除评论");
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<>();
                map.put("commentId", id + "");
                map.put("userId", user.getId());
                map.put("token", user.getToken());
                sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DEL_COMMENT, map, HttpConstant.DEL_COMMENT);
                mDialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void toLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * 删除成功后的处理
     */
    private void deleteForSuccess() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        showToast("删除成功");
        int replycount;
        if (!TextUtils.isEmpty(replyListId)) {//删除的是楼中楼的评论
            if (!comments.isEmpty()) {
                replycount = comments.get(current).getReplyCount();
                if (replycount > 1) {
                    comments.get(current).setReplyCount(replycount - 1);
                }
                comments.get(current).getReplyList().remove(replyListPosition);
                replyListId = "";
            }
        } else {//删除的是一层评论
            if (!comments.isEmpty()) {
                comments.remove(current);
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 更新点赞状态
     */
    private void updatePraiseStatu() {
        int praise;
        if (!comments.isEmpty()) {
            if (comments.get(current).getIsPraise() == 0) {
                comments.get(current).setIsPraise(1);
                praise = comments.get(current).getLikeCount();
                comments.get(current).setLikeCount(praise + 1);
            } else {
                comments.get(current).setIsPraise(0);
                praise = comments.get(current).getLikeCount();
                comments.get(current).setLikeCount(praise - 1);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        LogUtil.e(TAG, baseResponse.errMsg + "  errmsg  " + baseResponse.errCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shareToFriendsUtil != null) {
            if (shareToFriendsUtil.requestUtil != null) {
                shareToFriendsUtil.requestUtil.removeTag(shareToFriendsUtil.getClass().getName());
            }
            shareToFriendsUtil.requestUtil = null;
            shareToFriendsUtil = null;
        }
    }
}
