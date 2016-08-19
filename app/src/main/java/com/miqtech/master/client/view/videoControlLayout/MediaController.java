package com.miqtech.master.client.view.videoControlLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.InforItemDetail;
import com.miqtech.master.client.ui.InformationDetailActivity;
import com.pili.pldroid.player.IMediaController;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * Created by jerikc on 15/5/29.
 */
public class MediaController extends FrameLayout implements IMediaController {
    private static final String TAG = "MyMediaController";
    private MediaPlayerControl mPlayer;
    private Context mContext;
    private PopupWindow mWindow;
    private int mAnimStyle;
    private View mAnchor;
    private View mRoot;
    private ProgressBar mProgress;
    private TextView mEndTime, mCurrentTime;
    private long mDuration;
    private boolean mShowing;   //控制视频播放的View是否正在显示
    private boolean mDragging;  //进度条是否正在拖动中
    private boolean mInstantSeeking = true;
    private static int sDefaultTimeout = 3000;
    private static final int SEEK_TO_POST_DELAY_MILLIS = 200;

    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private boolean mFromXml = false;
    private ImageButton mPauseButton;
    private ImageButton mFfwdButton;
    private ImageButton mRewButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private ImageButton ib_mute_sound;
    private boolean mUseFastForward;
    public  boolean isVolumeOpen; // 是否有声音
    private boolean isVideoUiClickable=true;

//    private static final int IC_MEDIA_PAUSE_ID = Resources.getSystem().getIdentifier("ic_media_pause","drawable", "android");
//    private static final int IC_MEDIA_PLAY_ID = Resources.getSystem().getIdentifier("ic_media_play","drawable", "android");
//    private static final int MEDIA_CONTROLLER_ID = Resources.getSystem().getIdentifier("media_controller", "layout", "android");
//    private static final int PRV_BUTTON_ID = Resources.getSystem().getIdentifier("prev","id", "android");
//    private static final int FFWD_BUTTON_ID = Resources.getSystem().getIdentifier("ffwd","id", "android");
//    private static final int NEXT_BUTTON_ID = Resources.getSystem().getIdentifier("next","id", "android");
//    private static final int REW_BUTTON_ID = Resources.getSystem().getIdentifier("rew","id", "android");
//    private static final int PAUSE_BUTTON_ID = Resources.getSystem().getIdentifier("pause","id", "android");
//    private static final int MEDIACONTROLLER_PROGRESS_ID = Resources.getSystem().getIdentifier("mediacontroller_progress","id", "android");
//    private static final int END_TIME_ID = Resources.getSystem().getIdentifier("time","id", "android");
//    private static final int CURRENT_TIME_ID = Resources.getSystem().getIdentifier("time_current","id", "android");

    private AudioManager mAM;
    private Runnable mLastSeekBarRunnable;
    private boolean mDisableProgress = false;
    private int screenHeight;
    private int statusBarHeight;
    private boolean isFullScreen;
    private ViewStub playControlViewStub;
    private View playControlView;
    private ImageButton ib_pause;
    private ImageButton ib_fullscreen;
    private SeekBar sb_progress;
    private float radio = 3f / 4f; //屏幕的宽高比
    private int currentVolume;
    private Context context;
    private Animation mAnimSlideOutBottom;
    private Animation mAnimSlideInBottom;
    private int type; //0代表 InformationDetailActivity 1 代表 PlayVideoActivity
    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = this;
        mFromXml = true;
        initController(context);
    }

    public MediaController(Context context) {
        super(context);
        Log.i(TAG,"初始化 222222");
        if (!mFromXml)
            initController(context);
    //        initFloatingWindow();
    }

    public MediaController(Context context, boolean useFastForward, boolean disableProgressBar,ViewStub playControlViewStub) {
        this(context);
        this.context=context;
        Log.i(TAG, "初始化 1111111");
//        mUseFastForward = useFastForward;
//        mDisableProgress = disableProgressBar;
        this.playControlViewStub=playControlViewStub;
        initAnimation();
        if(playControlView == null){//为空则初始化控件
            playControlViewStub.setLayoutResource(R.layout.activity_control_layout);
            playControlView = playControlViewStub.inflate();
            initControllerView(playControlView);
        }
    }



//    public MediaController(Context context, boolean useFastForward) {
//        this(context);
//        mUseFastForward = useFastForward;
//    }
//
    private void initAnimation() {

        mAnimSlideOutBottom = AnimationUtils.loadAnimation(context,
                R.anim.venvy_slide_out_bottom);
        mAnimSlideInBottom = AnimationUtils.loadAnimation(context,
                R.anim.venvy_slide_in_bottom);
        mAnimSlideInBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ((InformationDetailActivity)context).setLockScreenBtnEnable(false);
                isVideoUiClickable=false;
                playControlView.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((InformationDetailActivity)context).setLockScreenBtnEnable(true);
                isVideoUiClickable=true;
                playControlView.setVisibility(View.VISIBLE);
                playControlView.setEnabled(true);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mAnimSlideOutBottom
                .setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        ((InformationDetailActivity)context).setLockScreenBtnEnable(false);
                        isVideoUiClickable=false;
                        playControlView.setEnabled(false);
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ((InformationDetailActivity)context).setLockScreenBtnEnable(true);
                        isVideoUiClickable=true;
                        playControlView.setEnabled(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
    }

    /**
     * 设置屏幕高度
     * @param screenHeight
     */
    public void setScreenHeight(int screenHeight){
        this.screenHeight=screenHeight;

    }
    private boolean initController(Context context) {
        Log.i(TAG,"初始化 333333");
        mUseFastForward = true;
        mAM = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        currentVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
        return true;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onFinishInflate() {
        Log.i(TAG,"onFinishInflate");
        //TODO
//        if (mRoot != null)
//          initControllerView(mRoot);
    }
//
//    private void initFloatingWindow() {
//        Log.i(TAG,"初始化PopuWindow 4444444");
//        mWindow = new PopupWindow(mContext);
//        mWindow.setFocusable(false);
//        mWindow.setBackgroundDrawable(null);
//        mWindow.setOutsideTouchable(true);
//        mAnimStyle = android.R.style.Animation;
//    }

    /**
     * Create the view that holds the widgets that control playback. Derived
     * classes can override this to create their own.
     *
     * @return The controller view.  创造控制View
     */
    protected View makeControllerView() {
        Log.i(TAG,"makeControllerView");
//        return ((LayoutInflater) mContext
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(MEDIA_CONTROLLER_ID, this);
        return  null;
    }

    /**
     *  初始化控制View
     * @param v
     */
    private void initControllerView(View v) {
        // By default these are hidden.
        ib_pause=(ImageButton)v.findViewById(R.id.ib_pause);
        mCurrentTime= (TextView) v.findViewById(R.id.tv_current_time);
        sb_progress=(SeekBar)v.findViewById(R.id.sb_progress);
        mEndTime=(TextView)v.findViewById(R.id.tv_total_time);
        ib_fullscreen=(ImageButton)v.findViewById(R.id.ib_fullscreen);
        ib_mute_sound=(ImageButton)v.findViewById(R.id.ib_mute_sound);
        if (currentVolume > 0) {
            isVolumeOpen = true;
            showMutePic();
        } else {
            isVolumeOpen = false;
            closeMutePic();
        }
        if (ib_pause != null) {
               ib_pause.requestFocus();
               ib_pause.setOnClickListener(mPauseListener);
        }
        ib_fullscreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("changeLandscape", "点击切换");
                if(!((InformationDetailActivity)getContext()).getLockScreenState()) {
                    setScreenlandscape();
                }
            }
        });
        ib_mute_sound.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isNotClickable())
                    return;
                if (isVolumeOpen && currentVolume>0) {
                    isVolumeOpen=!isVolumeOpen;
                    closeMutePic();
                    currentVolume = mAM
                            .getStreamVolume(AudioManager.STREAM_MUSIC);
                    mAM.setStreamVolume(AudioManager.STREAM_MUSIC, 0,
                            0);
                } else {
                    if(currentVolume>0) {
                        isVolumeOpen = !isVolumeOpen;
                        showMutePic();
                        mAM.setStreamVolume(AudioManager.STREAM_MUSIC,
                                currentVolume, 0);
                    }
                }
            }
        });
        sb_progress.setOnSeekBarChangeListener(mSeekListener);
        sb_progress.setThumbOffset(1);
        sb_progress.setMax(1000);
      //  sb_progress.setEnabled(!mDisableProgress);


//        mPrevButton = (ImageButton) v.findViewById(PRV_BUTTON_ID);
//        if (mPrevButton != null) {
//            mPrevButton.setVisibility(View.GONE);
//        }
//        mNextButton = (ImageButton) v.findViewById(NEXT_BUTTON_ID);
//        if (mNextButton != null) {
//            mNextButton.setVisibility(View.GONE);
//        }
//
//        mFfwdButton = (ImageButton) v.findViewById(FFWD_BUTTON_ID);
//        if (mFfwdButton != null) {
//            mFfwdButton.setOnClickListener(mFfwdListener);
//            if (!mFromXml) {
//                mFfwdButton.setVisibility(mUseFastForward ? View.VISIBLE : View.GONE);
//            }
//        }

//        mRewButton = (ImageButton) v.findViewById(REW_BUTTON_ID);
//        if (mRewButton != null) {
//            mRewButton.setOnClickListener(mRewListener);
//            if (!mFromXml) {
//                mRewButton.setVisibility(mUseFastForward ? View.VISIBLE : View.GONE);
//            }
//        }
//        mPauseButton = (ImageButton) v.findViewById(PAUSE_BUTTON_ID);
//        if (mPauseButton != null) {
//            mPauseButton.setVisibility(View.GONE);
        //    mPauseButton.requestFocus();
       //     mPauseButton.setOnClickListener(mPauseListener);


//        mProgress = (ProgressBar) v.findViewById(MEDIACONTROLLER_PROGRESS_ID);

//        mEndTime = (TextView) v.findViewById(END_TIME_ID);
//        mCurrentTime = (TextView) v.findViewById(CURRENT_TIME_ID);

          playControlView.setVisibility(View.INVISIBLE);
    }
    public void setControlView(boolean isShow){
        if(playControlView!=null && isShow){
            playControlView.setVisibility(VISIBLE);
            updatePausePlay();
        }else{
            playControlView.setVisibility(INVISIBLE);
        }
    }
    /***
     * 设置设置为横屏 竖
     *
     * @param
     */
    public  void setScreenlandscape() {
        if (((Activity)context).getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            Log.i("changeLandscape","1111111111111切换竖屏");
            ((InformationDetailActivity)context).changeToLandscape();
            ib_fullscreen.setImageResource(R.drawable.ib_information_video_smallscreen_btn);

        } else {
            Log.i("changeLandscape","1111111111111切换横屏");

            ((InformationDetailActivity)context).changeToPortrait();
            ib_fullscreen.setImageResource(R.drawable.ib_information_video_fullscreen_btn);
        }
    }
    public void changeFullScreenPic(){
        if(ib_fullscreen!=null){
            ib_fullscreen.setImageResource(R.drawable.ib_information_video_fullscreen_btn);
        }
    }
    public interface  ScreenChange{
        void changeToLandscape();
        void changeToPortrait();
    }
//    private void changeToLandscape(){
//        rootView.setLayoutParams(new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
//                android.widget.RelativeLayout.LayoutParams.MATCH_PARENT));
//        ((Activity)mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        ((Activity)mContext). setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 全屏
//
//        isFullScreen=true;
//    }
//    private void backLandscape() {
//        int screenWidth = ((Activity)mContext).getWindowManager().getDefaultDisplay().getHeight();
//        int videoHeight = (int) (screenWidth * radio);
//        rootView.setLayoutParams(new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, videoHeight));
//        ((Activity)mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 还原
//        isFullScreen = false;
//    }
    /**
     * Control the action when the seekbar dragged by user
     *
     * @param seekWhenDragging
     * True the media will seek periodically
     */
    public void setInstantSeeking(boolean seekWhenDragging) {
        mInstantSeeking = seekWhenDragging;
    }

    private void disableUnsupportedButtons() {
        Log.i(TAG,"disableUnsupportedButtons");
        try {
            //如果播放器不能暂停的话  暂停按钮不能点击
            if (ib_pause != null && !mPlayer.canPause())
                ib_pause.setEnabled(false);
        } catch (IncompatibleClassChangeError ex) {
        }
    }

    /**
     * <p>
     * Change the animation style resource for this controller.
     * </p>
     *
     * <p>
     * If the controller is showing, calling this method will take effect only
     * the next time the controller is shown.
     * </p>
     *
     * @param animationStyle
     * animation style to use when the controller appears and disappears.
     * Set to -1 for the default animation, 0 for no animation,
     * or a resource identifier for an explicit animation.
     *
     */
    public void setAnimationStyle(int animationStyle) {
        mAnimStyle = animationStyle;
    }

    public void setIsScreen(boolean isFullScreen) {
        this.isFullScreen=isFullScreen;
    }

    public interface OnShownListener {
        public void onShown();
    }

    private OnShownListener mShownListener;

    public void setOnShownListener(OnShownListener l) {
        mShownListener = l;
    }

    public interface OnHiddenListener {
        public void onHidden();
    }

    private OnHiddenListener mHiddenListener;

    public void setOnHiddenListener(OnHiddenListener l) {
        mHiddenListener = l;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long pos;
            switch (msg.what) {
                case FADE_OUT:
                    Log.i(TAG,"FADE_OUT");
                    hide();
                    break;
                case SHOW_PROGRESS:
                    Log.i(TAG,"SHOW_PROGRESS");
                    pos = setProgress();
                    if (!mDragging && mShowing) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        updatePausePlay();
                    }
                    break;
            }
        }
    };

    private long setProgress() {
        Log.i(TAG,"setProgress");
        if (mPlayer == null || mDragging)
            return 0;

        long position = mPlayer.getCurrentPosition();
        long duration = mPlayer.getDuration();
        if (sb_progress != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                sb_progress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            sb_progress.setSecondaryProgress(percent * 10);
        }

        mDuration = duration;

        if (mEndTime != null)
            mEndTime.setText(generateTime(mDuration));
        if (mCurrentTime != null)
            mCurrentTime.setText(generateTime(position));

        return position;
    }

    private static String generateTime(long position) {
        Log.i(TAG,"generateTime");
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes,
                    seconds).toString();
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds)
                    .toString();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("onTouchEvent","onTouchEvent");
        show(sDefaultTimeout);
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        Log.i("onTouchEvent","onTrackballEvent");
        show(sDefaultTimeout);
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        Log.i("onTouchEvent","dispatchKeyEvent"+keyCode);
        if (event.getRepeatCount() == 0
                && (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
            doPauseResume();
            show(sDefaultTimeout);
            if (ib_pause != null)
                ib_pause.requestFocus();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                updatePausePlay();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_MENU) {
            hide();
            return true;
        } else {
            show(sDefaultTimeout);
        }
        return super.dispatchKeyEvent(event);
    }

    private OnClickListener mPauseListener = new OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };

    /**
     * 更新暂停播放按钮状态
     */
    public void updatePausePlay() {
        if (playControlViewStub == null || ib_pause == null || mPlayer==null)
            return;
        if (mPlayer.isPlaying()) {
            ib_pause.setImageResource(R.drawable.iv_information_video_play_btn);
        }
        else {
            ib_pause.setImageResource(R.drawable.iv_information_video_pause_btn);
        }
    }

    /**
     *  暂停或播放
     */
    public void doPauseResume() {
        Log.i("MediaController22","doPauseResume:::"+mPlayer.isPlaying());
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        else {
            mPlayer.start();
        }
        updatePausePlay();
    }

    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {

        public void onStartTrackingTouch(SeekBar bar) {
            Log.i("OnSeekBarChangeListener","onStartTrackingTouch");
            mDragging = true;
            show(3600000);
            mHandler.removeMessages(SHOW_PROGRESS);
//            if (mInstantSeeking)
//                Log.i("OnSeekBarChangeListener", "设置音量关闭");
//                mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            Log.i("OnSeekBarChangeListener","onProgressChanged");
            if (!fromuser)
                return;

            final long newposition = (mDuration * progress) / 1000;
            String time = generateTime(newposition);
            if (mInstantSeeking) {
                mHandler.removeCallbacks(mLastSeekBarRunnable);
                mLastSeekBarRunnable = new Runnable() {
                    @Override
                    public void run() {
                        mPlayer.seekTo(newposition);
                    }
                };
                mHandler.postDelayed(mLastSeekBarRunnable, SEEK_TO_POST_DELAY_MILLIS);
            }
            if (mCurrentTime != null)
                mCurrentTime.setText(time);
        }

        public void onStopTrackingTouch(SeekBar bar) {
            Log.i("OnSeekBarChangeListener","onStopTrackingTouch");
            if (!mInstantSeeking)
                mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
            show(sDefaultTimeout);
            mHandler.removeMessages(SHOW_PROGRESS);
            Log.i("OnSeekBarChangeListener", "设置音量开启");
//            mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mDragging = false;
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };

//    private View.OnClickListener mRewListener = new View.OnClickListener() {
//        public void onClick(View v) {
//            long pos = mPlayer.getCurrentPosition();
//            pos -= 5000; // milliseconds
//            mPlayer.seekTo(pos);
//            setProgress();
//
//            show(sDefaultTimeout);
//        }
//    };

//    private View.OnClickListener mFfwdListener = new View.OnClickListener() {
//        public void onClick(View v) {
//            long pos = mPlayer.getCurrentPosition();
//            pos += 15000; // milliseconds
//            mPlayer.seekTo(pos);
//            setProgress();
//            show(sDefaultTimeout);
//        }
//    };

    /**
     * Set the view that acts as the anchor for the control view.
     *
     * - This can for example be a VideoView, or your Activity's main view.
     * - AudioPlayer has no anchor view, so the view parameter will be null.
     *
     * @param view
     * The view to which to anchor the controller when it is visible.
     *  设置播放位置的View
     */
    @Override
    public void setAnchorView(View view) {
        Log.i(TAG,"设置抛锚View 666666");
//        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//        layoutParams.height=view.getWidth()*3/4;
//        view.setLayoutParams(layoutParams);
//        mAnchor = view;
//        //高度为什么没有改变？？？？？
//        Log.i(TAG,"555555555555:::"+mAnchor.getWidth()+"::"+mAnchor.getHeight());
//        if (mAnchor == null) {
//            Log.i(TAG,"mAnchor == null:::");
//            sDefaultTimeout = 0; // show forever
//        }
//        if (!mFromXml) {
//            Log.i(TAG,"mAnchor == null:::");
//            removeAllViews();
//            //把控制View设置到PopuWindow上
//            mRoot = makeControllerView();
//            mWindow.setContentView(mRoot);
//            mWindow.setWidth(LayoutParams.MATCH_PARENT);
//            mWindow.setHeight(LayoutParams.WRAP_CONTENT);
//
//            Log.i(TAG,"mAnchor == null:::"+mRoot.getWidth()+"::"+mRoot.getWidth());
//        }
       // initControllerView(mRoot);
   //     Log.i(TAG,"宽度"+mRoot.getWidth()+"::"+mRoot.getHeight());
    }

    @Override
    public void setMediaPlayer(MediaPlayerControl player) {
        Log.i(TAG,"setMediaPlayer");
        mPlayer = player;
        updatePausePlay();
    }

    @Override
    public void show() {
        Log.i(TAG,"show--------");
        if(((InformationDetailActivity) context).isFullScreen ) {
            Log.i(TAG,"show(int timeout)--------222222");
            if(((InformationDetailActivity) context).getLockScreenState()){
                ((InformationDetailActivity) context).showOrHideLockScreen();
                return ;
            }else{
            if(!((InformationDetailActivity) context).isLockScreenVisible() && !mShowing) {
                ((InformationDetailActivity) context).showOrHideLockScreen();
            }
            }
        }
        show(sDefaultTimeout);
    }
    /**
     * Show the controller on screen. It will go away automatically after
     * 'timeout' milliseconds of inactivity.
     *
     * @param timeout
     * The timeout in milliseconds. Use 0 to show the controller until hide() is called.
     */
    @Override
    public void show(int timeout) {
       Log.i("show","是否显示"+isVideoUiClickable);
        //防止快速点击播放界面动画混乱
        if(!isVideoUiClickable){
            return ;
        }
        //判断一下刚进来的时候是否显示控制View视图 播放完和锁屏状态下不显示
        if(playControlViewStub==null || ((InformationDetailActivity)context).isPlayComplete() ||
                ((InformationDetailActivity) context).getLockScreenState()) {
            return;
        }
        if (!mShowing) {
//            if (mAnchor != null && mAnchor.getWindowToken() != null) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                    mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//                }
//            }
//            if (mPauseButton != null)
//                mPauseButton.requestFocus();
            if(ib_pause!=null)
            ib_pause.requestFocus();
            disableUnsupportedButtons();
            playControlView.setVisibility(View.VISIBLE);
            playControlView.startAnimation(mAnimSlideInBottom);
            Log.i(TAG,"show(int timeout)--------"+isFullScreen+"::"+((InformationDetailActivity) context).isLockScreenVisible());

//            if (mFromXml) {
//                setVisibility(View.VISIBLE);
//            } else {
//                int[] location = new int[2];
//
//                if (mAnchor != null) {
//                    mAnchor.getLocationOnScreen(location);
//                    Rect anchorRect = new Rect(location[0], location[1],
//                            location[0] + mAnchor.getWidth(), location[1]
//                            + mAnchor.getHeight()); //0 75 -1080 885  全频下 0 0 -1920 1080
//                    Log.i(TAG,"555555555555:::"+mAnchor.getWidth()+"::"+mAnchor.getHeight());
//                    mWindow.setAnimationStyle(mAnimStyle);
//                    if(isFullScreen) {
//                        mWindow.showAtLocation(rootView, Gravity.BOTTOM,
//                                anchorRect.left,0);
//            //            mWindow.showAsDropDown(rootView,0,-mWindow.getHeight());
//                    }else{
////                        mWindow.showAtLocation(rootView, Gravity.BOTTOM,
////                                anchorRect.left, 0);
//                        mWindow.showAsDropDown(rootView,0,-mWindow.getHeight());
//                    }




//                } else {
//                    Rect anchorRect = new Rect(location[0], location[1],
//                            location[0] + mRoot.getWidth(), location[1]
//                            + mRoot.getHeight());
//
//                    mWindow.setAnimationStyle(mAnimStyle);
//                    if(isFullScreen) {
//                        mWindow.showAtLocation(rootView, Gravity.BOTTOM,
//                                anchorRect.left,0);
//                //        mWindow.showAsDropDown(rootView,0,-mWindow.getHeight());
//                    }else{
//                        mWindow.showAtLocation(rootView, Gravity.BOTTOM,
//                                anchorRect.left, 0);
//             //           mWindow.showAsDropDown(rootView,0,-mWindow.getHeight());
//                    }
//                }
//                Log.i(TAG,"555555555555 rootView:::"+rootView.getWidth()+"::"+rootView.getHeight());
//            }
            mShowing = true;
            if (mShownListener != null)
                mShownListener.onShown();
        }
        updatePausePlay();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
           //300
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
                    timeout);
        }
    }

    @Override
    public boolean isShowing() {
        Log.i(TAG,"isShowing");
        return mShowing;
    }

    public  int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    @Override
    public void hide() {
        Log.i(TAG, "影藏"+isVideoUiClickable);
        if(!isVideoUiClickable){
            return ;
        }
        if (mShowing) {
//            if (mAnchor != null) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                    mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//                }
//            }
            try {
                mHandler.removeMessages(SHOW_PROGRESS);
                if (mFromXml) {
                    setVisibility(View.GONE);
                }
                else {
                    playControlView.setVisibility(View.GONE);
                    playControlView.startAnimation(mAnimSlideOutBottom);
                }
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "MediaController already removed");
            }
            mShowing = false;
            if (mHiddenListener != null)
                mHiddenListener.onHidden();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        Log.i(TAG,"setEnabled");
        if (ib_pause != null) {
            ib_pause.setEnabled(enabled);
        }
//        if (mFfwdButton != null) {
//            mFfwdButton.setEnabled(enabled);
//        }
//        if (mRewButton != null) {
//            mRewButton.setEnabled(enabled);
//        }
        if (sb_progress != null )
            sb_progress.setEnabled(enabled);
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }
    public void clearResouce(){
        if(mHandler!=null) {
            mHandler.removeCallbacks(mLastSeekBarRunnable);
            mHandler.removeCallbacksAndMessages(null);
        }
    }
    public void showMutePic(){
        ib_mute_sound.setImageResource(R.drawable.vedio_sound_open);
    }
    public void closeMutePic(){
        ib_mute_sound.setImageResource(R.drawable.vedio_sound_close);
    }
    public void setCurrentVolume(int currentVolume){
        this.currentVolume=currentVolume;
    }
    public void changeProgressAndTime(long progress,long time){
        if (mCurrentTime != null) {
            sb_progress.setProgress((int) progress);
            mCurrentTime.setText(getTime((int) time));
            Log.i("onTouch2222","time"+time+"getTime((int) time)：：："+getTime((int) time));
            updatePausePlay();
        }
    }
    /**
     *  格式化时间
     * @param time
     * @return
     */
    @SuppressLint("DefaultLocale")
    private String getTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        if(hour>0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }else{
            return String.format("%02d:%02d", minute, second);
        }
    }
    private long lastClickTime;
    public  boolean isNotClickable() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

}
