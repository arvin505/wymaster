package com.screenrecorder.services;

import android.annotation.TargetApi;
import android.app.Activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ToastUtil;
import com.miqtech.master.client.utils.Utils;
import com.pili.pldroid.streaming.EncodingType;
import com.pili.pldroid.streaming.StreamingManager;
import com.pili.pldroid.streaming.StreamingProfile;
import com.pili.pldroid.streaming.StreamingState;
import com.pili.pldroid.streaming.StreamingStateListener;
import com.pili.pldroid.streaming.VideoSourceConfig;
import com.screenrecorder.core.ScreenRecorder;
import com.screenrecorder.ui.ScreenRecorderActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 * Created by xiaoyi on 2016/7/27.
 * 直播推流服务
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PiliPushService extends Service implements StreamingStateListener, ScreenRecorder.ScreenRecorderListener {
    /**
     * 默认视频尺寸1280 * 720
     */
    private static final int DEFAULT_WIDTH = 720;
    private static final int DEFAULT_HEIGHT = 1280;
    private static final String TAG = "PiliPushService";
    private boolean isStreaming = false;
    private int mBitRate = 1200;
    private String mPushUrl;
    private MediaProjection mMediaProjection;
    private boolean shouldJump = true;

    int frameInteval = 50;
    /**
     * 视频设置
     * 包括尺寸，方向
     */
    private int mVideoWidth = DEFAULT_WIDTH;
    private int mVideoHeight = DEFAULT_HEIGHT;
    private int mScreenDensity;
    //默认竖屏
    private StreamingProfile.ENCODING_ORIENTATION mVideoOritation = StreamingProfile.ENCODING_ORIENTATION.PORT;

    private int mLastX;
    private int mLastY;

    /**
     * 录屏相关api
     */
    private MediaProjectionManager mMediaProjectionManaer;
    private ScreenRecorder mScreenRecorder;

    /**
     * 音频设置
     */
    private static final int SAMPLE_RATE = 44100;   //采样率

    /**
     * 推流设置
     */
    private StreamingProfile.AVProfile avProfile;
    private StreamingProfile.VideoProfile videoProfile;
    private StreamingProfile.AudioProfile audioProfile;
    private StreamingProfile.Stream stream;
    private StreamingProfile streamingProfile;
    private VideoSourceConfig mVideoSourceConfig;
    private StreamingManager mStreamingManager;
    private Activity mActivity;


    RelativeLayout mFloatLayout;
    private WindowManager wm;
    private WindowManager.LayoutParams wmLayoutParams;

    private int statusHeight = 0;

    /**
     * 防止被kill
     */
    private NotificationManager mNManager;

    private WeakReference<MediaProjection> mWeakReference;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PiliPushBinder();
    }

    /**
     * 设置参数
     *
     * @param videoWidth  视频宽度
     * @param videoHeight 视频高度
     * @param densityDpi  手机屏幕dpi
     * @param orientation 视频方向 0：竖屏 1：横屏
     */
    public void setParams(Activity activity, int videoWidth, int videoHeight, int densityDpi, int orientation, int bitRate, String pushUrl) {
        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;
        mScreenDensity = densityDpi;
        mVideoOritation = orientation == 0 ? StreamingProfile.ENCODING_ORIENTATION.PORT
                : StreamingProfile.ENCODING_ORIENTATION.LAND;
        mActivity = activity;
        mBitRate = bitRate;
        mPushUrl = pushUrl;
        initStreamManager();
        statusHeight = WangYuApplication.getStatusHeight(mActivity);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMediaProjectionManaer = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        if (!isStreaming) {
            if (mFloatLayout != null) {
                wm.removeView(mFloatLayout);
                mFloatLayout = null;
            }
        }
        mNManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setAutoCancel(false);
        builder.setPriority(Notification.PRIORITY_MAX);
        Intent notifyIntent = new Intent(this, ScreenRecorderActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        startForeground(1, notification);

        return START_STICKY;
    }

    private void initStreamManager() {
        audioProfile = new StreamingProfile.AudioProfile(SAMPLE_RATE, 48 * 1024);
        // TODO: 2016/7/21
        //videoProfile = new StreamingProfile.VideoProfile(videoFrameRate, videoBitRate, 30);
        videoProfile = new StreamingProfile.VideoProfile(30, mBitRate * 1024, 25 * 3);
        avProfile = new StreamingProfile.AVProfile(videoProfile, audioProfile);
        try {
            stream = new StreamingProfile.Stream(new JSONObject(mPushUrl));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        streamingProfile = new StreamingProfile();
        streamingProfile
                .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM1)
                .setStream(stream)
                .setAVProfile(avProfile)
                .setPreferredVideoEncodingSize(mVideoWidth, mVideoHeight)
                .setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH3)
                .setEncodingOrientation(mVideoOritation);


        mVideoSourceConfig = new VideoSourceConfig();
        mVideoSourceConfig.setVideoSourceSize(mVideoWidth, mVideoHeight);

        mStreamingManager = new StreamingManager(this, EncodingType.HW_SCREEN_VIDEO_WITH_HW_AUDIO_CODEC);
        mStreamingManager.prepare(streamingProfile);
        mStreamingManager.setStreamingStateListener(this);
    }

    long lastSystemMills;

    @Override
    public void frameAvailable(boolean endOfStream) {
        long currentMills = System.currentTimeMillis();
        if (currentMills - lastSystemMills > frameInteval) {
            if (mStreamingManager != null) {
                mStreamingManager.frameAvailable(endOfStream);
                lastSystemMills = currentMills;
            }
        } else {
            LogUtil.e(TAG, "frame return ----------    " + (currentMills - lastSystemMills));
        }

    }

    @Override
    public void sendAudioFrame(ByteBuffer buffer, int size, long ts, boolean isEof) {
        if (mStreamingManager != null) {
            mStreamingManager.sendAudioFrame(buffer, size, ts, isEof);
        }
    }

    public class PiliPushBinder extends Binder {
        public PiliPushService getService() {
            return PiliPushService.this;
        }
    }

    /**
     * 推流状态回调
     */
    @Override
    public void onStateChanged(StreamingState streamingState, Object o) {
        LogUtil.e(TAG, "streamingState:" + streamingState);
        switch (streamingState) {
            case READY:
                if (!isStreaming) {
                    startActivityForResult();
                }
                break;
            case SHUTDOWN:
                if (isStreaming) {
                    initStreamManager();
                    startPushStream();
                    startPushStream(mMediaProjection);
                }
                break;
            case IOERROR:
                Toast.makeText(WangYuApplication.appContext, "网络错误", 0).show();
                break;

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void startActivityForResult() {
        if (!isStreaming) {
            Intent permissionIntent = mMediaProjectionManaer.createScreenCaptureIntent();
            mActivity.startActivityForResult(permissionIntent, ScreenRecorderActivity.REQUEST_CODE_CAPTURE_PERM);
        }
    }

    public void startPushStream() {
        mStreamingManager.resume();
    }

    /**
     * 开始推流
     */
    public void startPushStream(MediaProjection mediaProjection) {
        isStreaming = true;
        mWeakReference = new WeakReference<MediaProjection>(mediaProjection);
        mMediaProjection = mWeakReference.get();
        mediaProjection = null;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (mStreamingManager.startStreaming(mVideoSourceConfig)) {
                    Surface inputSurface = mStreamingManager.getInputSurface();
                    if (inputSurface != null && mMediaProjection != null) {
                        mScreenRecorder = new ScreenRecorder(mMediaProjection, inputSurface,
                                mVideoWidth, mVideoHeight, mScreenDensity, SAMPLE_RATE, PiliPushService.this);
                        mScreenRecorder.start();
                    } else {
                        mStreamingManager.stopStreaming();
                        isStreaming = false;
                    }
                }
            }
        });
        createFloatView();
    }

    /**
     * 停止推流
     */
    public void stopPush() {
        isStreaming = false;
        if (mStreamingManager != null) {
            mStreamingManager.stopStreaming();
            mStreamingManager.pause();

            mStreamingManager.destroy();
        }
        removeFloatView();
    }

    @Override
    public void onDestroy() {
        mStreamingManager = null;
        if (mScreenRecorder != null) {
            mScreenRecorder.quit();
            mScreenRecorder = null;
        }
        LogUtil.e(TAG, "--state--ondestory------");
        if (mFloatLayout != null) {
            wm.removeView(mFloatLayout);
            mFloatLayout = null;
        }
        if (mMediaProjection != null) {
            mWeakReference.clear();
            mMediaProjection.stop();
            mMediaProjection = null;
        }

        stopForeground(true);
        releaseActivity();
        super.onDestroy();
    }


    /**
     * 停止服务
     */
    public void stopService() {
        stopPush();
        stopSelf();
    }

    public boolean isStreaming() {
        return isStreaming;
    }

    /**
     * 创建悬浮球
     */
    private void createFloatView() {
        if (mFloatLayout != null && mFloatLayout.getVisibility() == View.VISIBLE) {
            return;
        }
        wm = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);
        wmLayoutParams = new WindowManager.LayoutParams();
        wmLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wmLayoutParams.format = PixelFormat.RGBA_8888;
        wmLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

        wmLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmLayoutParams.x = Utils.getScreenWidth(this);
        wmLayoutParams.y = Utils.getScreenHeight(this) / 2;
        wmLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        final LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        mFloatLayout = (RelativeLayout) inflater.inflate(R.layout.layout_live_float, null);
        wm.addView(mFloatLayout, wmLayoutParams);

        final ImageView imageButton = (ImageView) mFloatLayout.findViewById(R.id.float_id);
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int x = Utils.getScreenWidth(PiliPushService.this) - imageButton.getMeasuredWidth() / 2;
                    wmLayoutParams.x = event.getRawX() > x / 2 ? x : 0;
                    wmLayoutParams.y = (int) event.getRawY() - imageButton.getMeasuredHeight() / 2 - statusHeight;
                    wm.updateViewLayout(mFloatLayout, wmLayoutParams);
                } else {
                    wmLayoutParams.x = (int) event.getRawX() - imageButton.getMeasuredWidth() / 2;
                    wmLayoutParams.y = (int) event.getRawY() - imageButton.getMeasuredHeight() / 2 - statusHeight;
                    wm.updateViewLayout(mFloatLayout, wmLayoutParams);
                }
                return false;
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // hide the button
                if (shouldJump) {
                    // v.setVisibility(View.GONE);
                    //  ToastUtil.showToast("click", getApplicationContext());
                    //  mFloatLayout.findViewById(R.id.ll_hidden).setVisibility(View.VISIBLE);
                    Intent intent = null;
                    if (mActivity == null) {
                        intent = new Intent(PiliPushService.this, ScreenRecorderActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        intent = new Intent(mActivity, ScreenRecorderActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mActivity.startActivity(intent);
                    }
                    /*PendingIntent pendingIntent = PendingIntent.getActivity(PiliPushService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    long now = Calendar.getInstance().getTimeInMillis();
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, now, pendingIntent);
                    else
                        alarmManager.set(AlarmManager.RTC_WAKEUP, now, pendingIntent);*/

                }
                /*Intent broadCastIntent = new Intent("com.miqtech.wymaster.live");
                mActivity.sendBroadcast(broadCastIntent);*/
            }
        });


        mFloatLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mFloatLayout.findViewById(R.id.ll_hidden).getVisibility() == View.VISIBLE) {
                    mFloatLayout.findViewById(R.id.ll_hidden).setVisibility(View.GONE);
                    imageButton.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

    }

    private void removeFloatView() {
        if (wm != null && mFloatLayout != null && mFloatLayout.isShown()) {
            wm.removeView(mFloatLayout);
            mFloatLayout = null;
        }
    }

    public void setShouldJump(boolean jump) {
        shouldJump = jump;
    }

    public void releaseActivity() {
        mActivity = null;
    }

}
