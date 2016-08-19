package com.miqtech.master.client.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.RecreationCommentAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.FirstCommentDetail;
import com.miqtech.master.client.entity.LiveRoomAnchorInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CircleImageView;
import com.miqtech.master.client.view.CustomMarqueeTextView;
import com.miqtech.master.client.view.videoControlLayout.LiveMediaController;
import com.miqtech.master.client.view.videoControlLayout.MediaController;
import com.miqtech.master.client.view.videoControlLayout.VerticalSeekBar;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.PlayerCode;
import com.pili.pldroid.player.widget.PLVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by admin on 2016/8/4.
 */
public class PlayVideoActivity extends BaseActivity implements View.OnClickListener, RecreationCommentAdapter.ItemDataDealWith, PLMediaPlayer.OnCompletionListener,
        PLMediaPlayer.OnInfoListener,
        PLMediaPlayer.OnErrorListener,
        PLMediaPlayer.OnPreparedListener, PLMediaPlayer.OnVideoSizeChangedListener, MediaController.ScreenChange {
    @Bind(R.id.rlSurfaceView)
    RelativeLayout rlSurfaceView; //整个播放布局
    @Bind(R.id.videoView)
    PLVideoView videoView;//播放View
    @Bind(R.id.llBufferingIndicator)
    View llBufferingIndicator;//缓冲指示器
    @Bind(R.id.ivBack)
    ImageView ivBack;//返回按钮
    @Bind(R.id.tvTitle)
    CustomMarqueeTextView tvTitle;//视频标题
    @Bind(R.id.ivRecomment)
    ImageView ivRecomment; //评论输入
    @Bind(R.id.lvLivePlayComment)
    ListView lvLivePlayComment;//评论页面
    @Bind(R.id.tvErrorPage)
    TextView tvErrorPage;// 错误页面
    @Bind(R.id.playControlView)
    ViewStub playControlView;//播放器暂停开始 进度条控制界面
    @Bind(R.id.rlVolumeUpDown)
    RelativeLayout rlVolumeUpDown;  //全屏下的音量界面
    @Bind(R.id.sbVolumeProgress)
    VerticalSeekBar sbVolumeProgress; //音量垂直进度条

    private String mVideoPath; // 播放地址
    private Runnable mVideoReconnect;
    private boolean mIsCompleted = false; //是否播放完成
    private static final int REQ_DELAY_MILLS = 3000;
    private int mReqDelayMills = REQ_DELAY_MILLS;
    private Context context;
    private float radio = 3f / 5f; //屏幕的宽高比
    private int screenWidth;
    private AVOptions mAVOptions;
    private boolean isFullScreen = false;
    private User user;
    private int replySize = 10;//   replySize	否	string	楼中楼回复数量（不传默认返回5条）
    private int page = 1;
    private int pageSize = 10;
    private List<FirstCommentDetail> comments = new ArrayList<FirstCommentDetail>();
    private String replyListPosition;
    private int listId;
    private Dialog mDialog;
    private RecreationCommentAdapter adapter;
    private final int ISREFER = 1;//startActivityForResult(intent,)
    private final int type = 8;//评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1） 8视屏评论
    private String message; //输入框里面的消息
    private View headerView;
    private CircleImageView anchorHeader; //主播头像
    private TextView anchorTitle;  //标题
    private TextView fansNum;    //粉丝数量
    private TextView tvAttention; //关注数
    private TextView anchorContent; //类容
    private ImageView anchorSex;//主播性别
    private AudioManager audioManager;
    private int currentVolume; //当前音量
    private int maxVolume;  //最大音量
    private LiveMediaController mMediaController;
    private boolean isStop = false;
    private long currentPosition;
    private String videoId;  //视频的id
    private LiveRoomAnchorInfo videoInfo;//视频详情信息
    private static final int  COMMENT_REQUEST=3;
    private String imgName;//后台返回的图片名
    private ViewGroup.LayoutParams videoViewLayoutParams;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_play_video_layout);
        ButterKnife.bind(this);
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        context = this;
        // 初始化视频大小
        initPlayerView(true);
        initView();
        initData();
        initPlayParameter();
        setOnClickListener();
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
        videoViewLayoutParams = videoView.getLayoutParams();
        if(islandscape){
            videoViewLayoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT;
        }else{
            videoViewLayoutParams.width =videoHeight*9/16;
        }
       videoViewLayoutParams.height = videoHeight;
       LogUtil.d(TAG,"宽"+videoViewLayoutParams.width+":::"+videoViewLayoutParams.height);
        LogUtil.d(TAG,"222222宽"+screenWidth+":::"+videoHeight);
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = videoHeight;
        rlSurfaceView.setLayoutParams(lp);
        videoView.setLayoutParams(videoViewLayoutParams);

    }

    /**
     * 初始化View
     */
    @Override
    protected void initView() {
        super.initView();
        //TODO 获取播放地址
        videoId = getIntent().getStringExtra("videoId");
        mVideoPath = getIntent().getStringExtra("rtmp");
        LogUtil.d(TAG,"播放地址"+mVideoPath);
        Intent intent = getIntent();
        String intentAction = intent.getAction();
        if (!TextUtils.isEmpty(intentAction) && intentAction.equals(Intent.ACTION_VIEW)) {
            mVideoPath = intent.getDataString();
        }
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mMediaController = new LiveMediaController(this, playControlView);
        mMediaController.setPlayOriention(0);
        mMediaController.setScreenOrientation(false); //设置非全屏

        videoView.setMediaController(mMediaController);
      //  videoView.setMediaBufferingIndicator(llBufferingIndicator);
        //TODO 设置触摸控制音量和进度事件

        headerView = getLayoutInflater().inflate(R.layout.fragment_information_lp, null);
        lvLivePlayComment.addHeaderView(headerView);
        initHeadView();

        adapter = new RecreationCommentAdapter(context, comments);
        adapter.setType(2);
        lvLivePlayComment.setAdapter(adapter);
        adapter.setReport(this);
        llBufferingIndicator.setVisibility(View.VISIBLE);

    }

    private void initHeadView() {
        headerView.findViewById(R.id.bgView).setVisibility(View.GONE);
        anchorHeader = (CircleImageView) headerView.findViewById(R.id.anchorHeader);
        anchorTitle = (TextView) headerView.findViewById(R.id.anchorTitle);
        fansNum = (TextView) headerView.findViewById(R.id.fansNum);
        tvAttention = (TextView) headerView.findViewById(R.id.tvAttention);
        anchorContent = (TextView) headerView.findViewById(R.id.anchorContent);
        anchorSex=(ImageView)headerView.findViewById(R.id.anchorSex);
        tvAttention.setOnClickListener(this);
    }

    /**
     * 设置主播数据
     * @param info
     */
    private void setData(LiveRoomAnchorInfo info) {
        anchorContent.setVisibility(View.GONE);
        if (info != null) {
            mVideoPath=info.getRtmp();
            //开始播放
            videoView.setVideoPath(mVideoPath);
            videoView.requestFocus();
            videoView.start();
            AsyncImage.loadAvatar(this, HttpConstant.SERVICE_UPLOAD_AREA + info.getIcon(), anchorHeader);
            anchorTitle.setText(info.getNickname());
            setFontDiffrentColor(getString(R.string.live_play_fans_num, Utils.calculate(info.getFans(), 10000, "W")), 3, getString(R.string.live_play_fans_num, Utils.calculate(info.getFans(), 10000, "W")).length(), fansNum);
            setSubscribeState(info.getIsSubscibe()==1?true:false);
            tvTitle.setText(info.getTitle());
            tvAttention.setOnClickListener(this);
            anchorSex.setVisibility(View.VISIBLE);
            if (info.getSex() == 0) {
                anchorSex.setImageResource(R.drawable.live_play_men);
            } else {
                anchorSex.setImageResource(R.drawable.live_play_femen);
            }
         //   initPlayerView(info.getScreen()==0?true:false);
            if(info.getScreen()==0){
                mMediaController.setPlayOriention(0);
            }else{
                mMediaController.setPlayOriention(1);
            }
        }
    }
    public void setSubscribeState(boolean isSubscribe){
        GradientDrawable bgShape=(GradientDrawable)tvAttention.getBackground();
        if(isSubscribe){
            tvAttention.setText(getResources().getString(R.string.live_room_attentioned));
            tvAttention.setTextColor(this.getResources().getColor(R.color.shop_buy_record_gray));
            bgShape.setStroke(Utils.dp2px(1),this.getResources().getColor(R.color.shop_buy_record_gray));
        }else{
            tvAttention.setText(getResources().getString(R.string.live_room_attention));
            tvAttention.setTextColor(this.getResources().getColor(R.color.light_orange));
            bgShape.setStroke(Utils.dp2px(1),this.getResources().getColor(R.color.orange));
        }
    }
    private void setFontDiffrentColor(String content, int start, int end, TextView tv) {
        if (tv == null) {
            return;
        }
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.orange)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(style);
    }

    @Override
    protected void initData() {
        super.initData();
        getVideoData();
        loadOfficalCommentList();
    }

    private void getVideoData() {
        showLoading();
        Map<String, String> params = new HashMap();
        params.put("videoId", videoId);
        if(WangYuApplication.getUser(PlayVideoActivity.this)!=null){
            params.put("userId",WangYuApplication.getUser(PlayVideoActivity.this).getId()+"");
            params.put("token",WangYuApplication.getUser(PlayVideoActivity.this).getToken()+"");
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.VIDEO_DETAIL, params, HttpConstant.VIDEO_DETAIL);
    }

    private void loadOfficalCommentList() {
        user = WangYuApplication.getUser(context);
        HashMap params = new HashMap();
        //TODO  战时写死
        params.put("amuseId", videoId );
        params.put("page", page + "");
        params.put("type", 8 + "");//	评论类型：1-娱乐赛评论；2-官方赛事评论 4自发赛评论。（不传默认为1）
        params.put("replySize", replySize + "");
        if (user != null) {
            params.put("userId", user.getId() + "");
            params.put("token", user.getToken() + "");
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_COMMENT_LIST, params, HttpConstant.AMUSE_COMMENT_LIST);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        LogUtil.d(TAG, "onSuccess" + object.toString());
        hideLoading();
        tvErrorPage.setVisibility(View.GONE);
        try {
            if (object == null) {
                return;
            }
            if (method.equals(HttpConstant.AMUSE_COMMENT_LIST)) {
                initRecreationComment(object);
            } else if (method.equals(HttpConstant.DEL_COMMENT)) {
                if (0 == object.getInt("code") && "success".equals(object.getString("result"))) {
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    LogUtil.d("Delect", "删除成功" + replyListPosition);
                    if (!TextUtils.isEmpty(replyListPosition)) {
                        int idd = Integer.parseInt(replyListPosition);
                        int replycount = comments.get(listId).getReplyCount();
                        if (replycount > 1) {
                            comments.get(listId).setReplyCount(replycount - 1);
                        }
                        comments.get(listId).getReplyList().remove(idd);
                        replyListPosition = "";
                    } else {
                        comments.remove(listId);
                    }
                    adapter.notifyDataSetChanged();
                    showToast("删除成功");
                }
            } else if (method.equals(HttpConstant.V2_COMMENT_PRAISE)) {
                int praisrNum;
                BroadcastController.sendUserChangeBroadcase(context);
                if (comments.get(listId).getIsPraise() == 0) {
                    praisrNum = comments.get(listId).getLikeCount();
                    comments.get(listId).setIsPraise(1);
                    comments.get(listId).setLikeCount(praisrNum + 1);
                } else if (comments.get(listId).getIsPraise() == 1) {
                    praisrNum = comments.get(listId).getLikeCount();
                    comments.get(listId).setIsPraise(0);
                    comments.get(listId).setLikeCount(praisrNum - 1);
                }
                adapter.notifyDataSetChanged();
            } else if (method.equals(HttpConstant.AMUSE_COMMENT)) {//提交评论
                page = 1;
                pageSize = 10;
                loadOfficalCommentList();
                showToast("发表成功");
            } else if (method.equals(HttpConstant.VIDEO_DETAIL)) {
                try {
                    if (object.getInt("code") == 0 && object.has("object")) {
                        videoInfo = GsonUtil.getBean(object.getJSONObject("object").toString(), LiveRoomAnchorInfo.class);
                        setData(videoInfo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(method.equals(HttpConstant.LIVE_SUBSCRIBE)){
                try {
                    if(object.getInt("code")==0 && "success".equals(object.getString("result"))){
                        videoInfo.setIsSubscibe(videoInfo.getIsSubscibe()==1?0:1);
                        setSubscribeState(videoInfo.getIsSubscibe()==1?true:false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        try {
            int code = object.getInt("code");
            String result = object.getString("result");
            showToast(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
    }
    private void initRecreationComment(JSONObject object) {
        try {
            if (object.getInt("code") == 0 && object.has("object")) {
                String strObj = object.getString("object");
                JSONObject jsonObj = new JSONObject(strObj);
                if (jsonObj.has("list")) {
                    String strList = jsonObj.getString("list").toString();
                    List<FirstCommentDetail> newComments = GsonUtil.getList(strList, FirstCommentDetail.class);
                    comments.clear();
                    if (newComments != null && !newComments.isEmpty()) {
                        comments.addAll(newComments);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    comments.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void initPlayParameter() {
        mAVOptions = new AVOptions();
        mAVOptions.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);//连接时间
        mAVOptions.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000); //读取视频流超时时间
        mAVOptions.setInteger(AVOptions.KEY_MEDIACODEC, 0); //1 硬解 0 软解
        mAVOptions.setInteger(AVOptions.KEY_START_ON_PREPARED, 1); //1 自动播放 0 不自动播放
        videoView.setAVOptions(mAVOptions);
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        LogUtil.d(TAG, "initPlayParameter:::" + mVideoPath);

    }

    private void changeLandscape() {
        if(videoInfo.getScreen()==0){
            Log.i("changeLandscape", "变换横屏");
            rlSurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            videoView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 全屏
            isFullScreen = true;
            mMediaController.setScreenOrientation(true);
        }else{
            rlSurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            videoView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
                isFullScreen = true;
                mMediaController.setScreenOrientation(true);
            Log.i("changeLandscape", "222222变换横屏");
        }
    }

    private void backLandscape() {
        Log.i("changeLandscape", "变换竖屏");
        //影藏锁屏图标
        if (isFullScreen) {
            if(videoInfo.getScreen()==0) {
                int screenWidth = getWindowManager().getDefaultDisplay().getHeight();
                int videoHeight = (int) (screenWidth * radio);
                rlSurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, videoHeight));
                videoView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        videoHeight));
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 还原
                isFullScreen = false;
                mMediaController.setScreenOrientation(false);
            }else{
                int videoHeight = (int) (screenWidth * radio);
                ViewGroup.LayoutParams lp = rlSurfaceView.getLayoutParams();
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = videoHeight;
                LogUtil.d(TAG,"initPlayerView"+"宽度"+lp.width+":::"+lp.height);
                rlSurfaceView.setLayoutParams(lp);
                videoView.setLayoutParams(videoViewLayoutParams);
                LogUtil.d(TAG,"22222222宽"+videoViewLayoutParams.width+":::"+videoViewLayoutParams.height);
                isFullScreen = false;
                mMediaController.setScreenOrientation(false);
            }
        }
    }

    /**
     * View设置点击事件
     */
    private void setOnClickListener() {
        videoView.setOnErrorListener(this);
        videoView.setOnCompletionListener(this);
        videoView.setOnInfoListener(this);
        videoView.setOnPreparedListener(this);
        ivBack.setOnClickListener(this);
        ivRecomment.setOnClickListener(this);

        rlSurfaceView.setOnTouchListener(onTouchListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //   if(!videoView.isPlaying() && isStop){
        LogUtil.d(TAG, "当前位置  onStop" + currentPosition + ":::" + videoView.isPlaying());
        videoView.start();
        videoView.seekTo(currentPosition);
        isStop = false;
        //   }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(null);
        mMediaController = null;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivFullScreenBtn:
                if (isFullScreen) {
                    backLandscape();
                } else {
                    changeLandscape();
                }
                break;
            case R.id.ivBack:
                if (isFullScreen) {
                    backLandscape();
                } else {
                    clearResource();
                    onBackPressed();
                }
                break;
            case R.id.ivRecomment:
                Intent intent=new Intent(this, SubmitGradesActivity.class);
                intent.putExtra("fromType",1);
                startActivityForResult(intent,COMMENT_REQUEST);
                break;
            case R.id.tvAttention:
                getAttentionRequest();
                break;
        }
    }

    /**
     * 离开播放间释放资源
     */
    private void clearResource(){
        videoView.stopPlayback();
        if (mMediaController != null) {
            mMediaController.clearResouce();
        }
    }
    private void getAttentionRequest(){
        showLoading();
        Map<String, String> params = new HashMap();
        if(WangYuApplication.getUser(this)!=null){
            params.put("userId",WangYuApplication.getUser(this).getId()+"");
            params.put("token",WangYuApplication.getUser(this).getToken());
            params.put("upUserId",videoInfo.getUpUserId()+"");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.LIVE_SUBSCRIBE, params, HttpConstant.LIVE_SUBSCRIBE);
        }else{
            toLogin();
        }

    }
    /**
     * 提交评论
     */
    private void submitComment() {
        Map<String, String> map = new HashMap();
        user = WangYuApplication.getUser(context);
        if (user != null) {
            map.put("amuseId", videoId + "");
            map.put("userId", user.getId() + "");
            map.put("token", user.getToken() + "");
            map.put("content", Utils.replaceBlank(message));
            if(!TextUtils.isEmpty(imgName)){
                map.put("img",imgName);
            }
            map.put("type", 8 + "");// type否	string	评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1）
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_COMMENT, map, HttpConstant.AMUSE_COMMENT);
        } else {
            toLogin();
        }
    }

    @Override
    public void delect(int position) {
        if (!comments.isEmpty() && position < comments.size()) {
            listId = position;
            creatDialogForDelect(comments.get(position).getId());
            Log.i(TAG, "删除的用户的id" + comments.get(position).getUserId());
        }
    }

    @Override
    public void praiseComment(int position) {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            listId = position;
            Map<String, String> map = new HashMap<>();
            map.put("commentId", comments.get(position).getId() + "");
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.V2_COMMENT_PRAISE, map, HttpConstant.V2_COMMENT_PRAISE);
        } else {
            showToast(getResources().getString(R.string.pleaseLogin));
        }
    }

    @Override
    public void replyComment(int position) {
        Intent intent = new Intent(context, PersonalCommentDetail.class);
        intent.putExtra("amuseId", videoId);
        intent.putExtra("type", type);
        intent.putExtra("parentId", comments.get(position).getId());
        intent.putExtra("isPopupKeyboard", 1);//判断是否弹出键盘   1  弹出  0不弹出   默认不弹
        startActivityForResult(intent, ISREFER);
    }

    @Override
    public void lookComment(int position) {
        Intent intent = new Intent(context, PersonalCommentDetail.class);
        intent.putExtra("amuseId", videoId);
        intent.putExtra("type", type);
        intent.putExtra("parentId", comments.get(position).getId());
        intent.putExtra("isPopupKeyboard", 0);//判断是否弹出键盘   1  弹出  0不弹出   默认不弹
        startActivityForResult(intent, ISREFER);
    }

    @Override
    public void lookCommentSection() {
        skipCommentSection();
    }

    @Override
    public void delectReplyReply(int position, int replyListid) {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            if (!comments.isEmpty() && !comments.get(position).getReplyList().isEmpty()) {
                listId = position;
                replyListPosition = replyListid + "";
                creatDialogForDelect(comments.get(position).getReplyList().get(replyListid).getId());
            }
        } else {
            toLogin();
        }
    }

    /**
     * 跳到评论区页面
     */
    public void skipCommentSection() {
        Intent intent = new Intent(context, CommentsSectionActivity.class);
        intent.putExtra("amuseId", videoId + "");
        intent.putExtra("type", type);
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
                delectcomment(id);
                LogUtil.d("Delect", "删除" + id);
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

    private void delectcomment(String id) {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("commentId", id + "");
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DEL_COMMENT, map, HttpConstant.DEL_COMMENT);
            LogUtil.d("Delect", "删除q赢球" + id + "::" + user.getId() + ":::" + user.getToken());
        } else {
            showToast(getResources().getString(R.string.pleaseLogin));
        }
    }

    private void toLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ISREFER && data != null) {
            int myIsRefre = data.getIntExtra("isRefre", -1);
            if (myIsRefre == 1) {
                loadOfficalCommentList();
            }
        }else if(requestCode==COMMENT_REQUEST && resultCode==SubmitGradesActivity.RESULT_OK && data!=null){
            imgName =data.getStringExtra("imgName");
            message=data.getStringExtra("remark");
            submitComment();
        }
    }
    @Override
    public void changeToLandscape() {
        changeLandscape();
    }

    @Override
    public void changeToPortrait() {
        backLandscape();
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
        /** 是否是调节音量 */
        boolean isChangeVolume = false;
        int startVolume;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //全屏  且 锁屏 没有触摸事件   非全屏没有触摸事件
            //TODO 锁屏判断
            Log.i("onTouch", "onTouch2222222" + isFullScreen);
            if (!isFullScreen) {
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (isChangeVolume) {
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    return true;
                }
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                secondsPerPixel = (float) PROGRESS_VER_SCREEN
                        / (float) v.getWidth(); // 166.66667 ???
                startX = event.getX();
                startY = event.getY();

                startTime = videoView.getCurrentPosition();
                sbVolumeProgress.setProgress(currentVolume);   //设置当前音量
                toTime = startTime;
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
                if ((!isChangeVolume)) {
                    if (Math.abs(moveX) > check) {
                        //TODO 调节进度条
                        Log.i("onTouch", " isChangeProgress = true;");
                    } else if (Math.abs(moveY) > check) {
                        //如果需要控制亮度  就打开此处
                        isChangeVolume = true;
                        Log.i("onTouch", " isChangeVolume = true;");
                    }
                } else {
                    if (isChangeVolume) {
                        showVolume();
                        float addVolume = (startY - endY) / v.getHeight()
                                * maxVolume * 4f;
                        int toVolume = (int) (startVolume + addVolume);
                        if (toVolume > maxVolume) {
                            toVolume = maxVolume;
                        } else if (toVolume < 0) {
                            toVolume = 0;
                        }
                        sbVolumeProgress.setProgress(toVolume);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                toVolume, 0);
                    }
                }

            }
            return true;
        }
    };

    private void showVolume() {
        if (null != rlVolumeUpDown && isFullScreen) {
            rlVolumeUpDown.setVisibility(View.VISIBLE);
        }
        hideVolumeAfterMillis();   //2秒之后音量键消失
    }

    protected void hideVolumeAfterMillis() {
        rlVolumeUpDown.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != rlVolumeUpDown && rlVolumeUpDown.getVisibility() == View.VISIBLE) {
                    rlVolumeUpDown.setVisibility(View.GONE);
                }
            }
        }, 2000);
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
                    if (mMediaController != null) {
                        mMediaController.changeFullScreenPic();
                    }
                } else {
                    clearResource();
                    onBackPressed();
                    return true;
                }
            case KeyEvent.KEYCODE_HOME:
                //TODO home键
                return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentPosition = videoView.getCurrentPosition();
        LogUtil.d(TAG, "当前位置  onPause" + currentPosition);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videoView.isPlaying()) {
            videoView.pause();
        }
        isStop = true;
        LogUtil.d(TAG, "当前位置  onStop" + currentPosition);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {
        llBufferingIndicator.setVisibility(View.GONE);
        mIsCompleted = true;
    }

    @Override
    public boolean onError(PLMediaPlayer plMediaPlayer, int extra) {
            if (extra == PlayerCode.EXTRA_CODE_INVALID_URI || extra == PlayerCode.EXTRA_CODE_EOF) {
                if (llBufferingIndicator != null)
                    llBufferingIndicator.setVisibility(View.GONE);
                return true;
            }
            //播放完成  并且没有播放列表  重新播放
            if (mIsCompleted && extra == PlayerCode.EXTRA_CODE_EMPTY_PLAYLIST) {
                Log.d(TAG, "mVideoView reconnect!!!");
                videoView.removeCallbacks(mVideoReconnect);
                mVideoReconnect = new Runnable() {
                    @Override
                    public void run() {
                        videoView.setVideoPath(mVideoPath);
                    }
                };
                videoView.postDelayed(mVideoReconnect, mReqDelayMills);
                mReqDelayMills += 200;
            } else if (extra == PlayerCode.EXTRA_CODE_404_NOT_FOUND) {
                // NO ts exist
                if (llBufferingIndicator != null)
                    llBufferingIndicator.setVisibility(View.GONE);
            } else if (extra == PlayerCode.EXTRA_CODE_IO_ERROR) {
                // NO rtmp stream exist
                if (llBufferingIndicator != null)
                    llBufferingIndicator.setVisibility(View.GONE);
            }
        return true;
    }

    @Override
    public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
        if (what == PLMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            Log.i(TAG, "onInfo: (MEDIA_INFO_VIDEO_RENDERING_START)");
            if (mMediaController != null) {
                mMediaController.setControlView(true);
            }
            if (llBufferingIndicator != null)
                llBufferingIndicator.setVisibility(View.GONE);
        } else if (what == PLMediaPlayer.MEDIA_INFO_BUFFERING_START) {
            Log.i(TAG, "onInfo: (MEDIA_INFO_BUFFERING_START)");
            if (llBufferingIndicator != null)
                llBufferingIndicator.setVisibility(View.VISIBLE);
        } else if (what == PLMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            Log.i(TAG, "onInfo: (MEDIA_INFO_BUFFERING_END)");
            if (llBufferingIndicator != null)
                llBufferingIndicator.setVisibility(View.GONE);
        } else if (what == PLMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START) {
            Log.i(TAG, "duration:" + videoView.getDuration());
        } else if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            Log.i(TAG, "duration:" + videoView.getDuration());
        }
        return true;
    }

    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer) {
        videoView.requestLayout();
        llBufferingIndicator.setVisibility(View.GONE);
        mReqDelayMills = REQ_DELAY_MILLS;
        mMediaController.show(3000);
        mIsCompleted = false;
    }

    @Override
    public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int i, int i1) {

    }
}
