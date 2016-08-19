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
import com.miqtech.master.client.ui.PlayVideoActivity;
import com.pili.pldroid.player.IMediaController;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * Created by jerikc on 15/5/29.
 */
public class LiveMediaController extends FrameLayout implements IMediaController {
    private static final String TAG = "MyMediaController";
    private MediaPlayerControl mPlayer;
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
    private ImageButton ib_mute_sound;
    private boolean mUseFastForward;
    public  boolean isVolumeOpen; // 是否有声音
    private boolean isVideoUiClickable=true;
    private AudioManager mAM;
    private Runnable mLastSeekBarRunnable;
    private ViewStub playControlViewStub;
    private View playControlView;
    private ImageButton ibPause;
    private ImageButton ibFullscreen;
    private SeekBar sbProgress;
    private float radio = 3f / 4f; //屏幕的宽高比
    private int currentVolume;
    private Context context;
    private Animation mAnimSlideOutBottom;
    private Animation mAnimSlideInBottom;
    private int type; //0代表 InformationDetailActivity 1 代表 PlayVideoActivity
    private boolean isFullScreen=false; //是否横屏
    private int playOriention; //播放方向
    public LiveMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFromXml = true;
        initController(context);
    }

    public LiveMediaController(Context context) {
        super(context);
        Log.i(TAG,"初始化 222222");
        if (!mFromXml)
            initController(context);
    }

    public LiveMediaController(Context context, ViewStub playControlViewStub) {
        this(context);
        this.context=context;
        Log.i(TAG, "初始化 1111111");
        this.playControlViewStub=playControlViewStub;
        initAnimation();
        if(playControlView == null){//为空则初始化控件
            playControlViewStub.setLayoutResource(R.layout.activity_play_control_layout);
            playControlView = playControlViewStub.inflate();
            initControllerView(playControlView);
        }
    }
    public void setScreenOrientation(boolean isFullScreen){
        this.isFullScreen=isFullScreen;
    }
    public void setPlayOriention(int playOriention){
        this.playOriention=playOriention;
    }
    private void initAnimation() {

        mAnimSlideOutBottom = AnimationUtils.loadAnimation(context,
                R.anim.venvy_slide_out_bottom);
        mAnimSlideInBottom = AnimationUtils.loadAnimation(context,
                R.anim.venvy_slide_in_bottom);
        mAnimSlideInBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isVideoUiClickable=false;
                playControlView.setEnabled(false);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
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
                        isVideoUiClickable=false;
                        playControlView.setEnabled(false);
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        isVideoUiClickable=true;
                        playControlView.setEnabled(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
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
    }

    /**
     *  初始化控制View
     * @param v
     */
    private void initControllerView(View v) {
        ibPause=(ImageButton)v.findViewById(R.id.ibPause);
        sbProgress=(SeekBar)v.findViewById(R.id.sbProgress);
        ibFullscreen=(ImageButton)v.findViewById(R.id.ibFullscreen);
        if (ibPause != null) {
               ibPause.requestFocus();
               ibPause.setOnClickListener(mPauseListener);
        }
        ibFullscreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("changeLandscape", "点击切换");
                   setScreenlandscape();
            }
        });
        sbProgress.setOnSeekBarChangeListener(mSeekListener);
        sbProgress.setThumbOffset(1);
        sbProgress.setMax(1000);
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
        if(playOriention==0) {
            if (((Activity) context).getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                Log.i("changeLandscape", "1111111111111切换横屏");
                ((PlayVideoActivity) context).changeToLandscape();
                ibFullscreen.setImageResource(R.drawable.live_play_back_fullscreen);

            } else {
                Log.i("changeLandscape", "1111111111111切换竖屏");
                ((PlayVideoActivity) context).changeToPortrait();
                ibFullscreen.setImageResource(R.drawable.live_play_fullscreen);
            }
        }else{
            if(isFullScreen) {
                ((PlayVideoActivity) context).changeToPortrait();
                ibFullscreen.setImageResource(R.drawable.live_play_fullscreen);
            }else{
                ((PlayVideoActivity) context).changeToLandscape();
                ibFullscreen.setImageResource(R.drawable.live_play_back_fullscreen);
            }
        }
    }
    public void changeFullScreenPic(){
        if(ibFullscreen!=null){
            ibFullscreen.setImageResource(R.drawable.live_play_fullscreen);
        }
    }
    public interface  ScreenChange{
        void changeToLandscape();
        void changeToPortrait();
    }
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
            if (ibPause != null && !mPlayer.canPause())
                ibPause.setEnabled(false);
        } catch (IncompatibleClassChangeError ex) {
        }
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
        if (sbProgress != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                sbProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            sbProgress.setSecondaryProgress(percent * 10);
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
            if (ibPause != null)
                ibPause.requestFocus();
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
        if (playControlViewStub == null || ibPause == null || mPlayer==null)
            return;
        if (mPlayer.isPlaying()) {
            ibPause.setImageResource(R.drawable.live_play_play_btn);
        } else {
            ibPause.setImageResource(R.drawable.live_play_pause_btn);
        }
    }

    /**
     *  暂停或播放
     */
    public void doPauseResume() {
        Log.i("MediaController22","doPauseResume:::"+mPlayer.isPlaying());
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
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
            mDragging = false;
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };
    @Override
    public void setMediaPlayer(MediaPlayerControl player) {
        Log.i(TAG,"setMediaPlayer");
        mPlayer = player;
        updatePausePlay();
    }

    @Override
    public void show() {
        Log.i(TAG,"show--------");
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
        if (!mShowing) {
            if(ibPause!=null)
            ibPause.requestFocus();
            disableUnsupportedButtons();
            playControlView.setVisibility(View.VISIBLE);
            playControlView.startAnimation(mAnimSlideInBottom);
            mShowing = true;
            if (mShownListener != null)
                mShownListener.onShown();
        }
        updatePausePlay();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
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

    @Override
    public void hide() {
        Log.i(TAG, "影藏"+isVideoUiClickable);
        if(!isVideoUiClickable){
            return ;
        }
        if (mShowing) {
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
        if (ibPause != null) {
            ibPause.setEnabled(enabled);
        }
        if (sbProgress != null )
            sbProgress.setEnabled(enabled);
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }

    @Override
    public void setAnchorView(View view) {

    }

    public void clearResouce(){
        if(mHandler!=null) {
            mHandler.removeCallbacks(mLastSeekBarRunnable);
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
