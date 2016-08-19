package com.screenrecorder.core;

import android.annotation.TargetApi;
import android.hardware.display.VirtualDisplay;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.view.Surface;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xiaoyi on 2016/7/16.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public final class ScreenRecorder extends Thread {
    private static final String TAG = "ScreenRecorder";

    public interface ScreenRecorderListener {
        void frameAvailable(boolean endOfStream);
        void sendAudioFrame(ByteBuffer buffer, int size, long ts, boolean isEof);
    }

    private final int sampleRate;

    private int mWidth;
    private int mHeight;
    private int mDpi;

    private VirtualDisplay mVirtualDisplay;
    private MediaProjection mMediaProjection;
    public static Surface mSurface = null;

    public static AtomicBoolean mQuit = new AtomicBoolean(false);

    private ScreenRecorderListener mScreenRecorderListener;

    private final Handler mDrainHandler = new Handler(Looper.getMainLooper());
    private Runnable mDrainEncoderRunnable = new Runnable() {
        @Override
        public void run() {
            if (!mQuit.get()) {
                mDrainHandler.removeCallbacks(mDrainEncoderRunnable);
                mScreenRecorderListener.frameAvailable(false);
                mDrainHandler.postDelayed(mDrainEncoderRunnable, 10);
            } else {
                mScreenRecorderListener.frameAvailable(true);
                release();
            }
        }
    };

    public ScreenRecorder(MediaProjection mp, Surface surface, int width, int height, int dpi, int sampleRate, ScreenRecorderListener listener) {
        mWidth = width;
        mHeight = height;
        mDpi = dpi;
        mMediaProjection = mp;
        mQuit = new AtomicBoolean(false);
        mSurface = surface;
        mScreenRecorderListener = listener;
        this.sampleRate = sampleRate;
    }

    public final void quit() {
        mQuit.set(true);
    }

    @Override
    public void run() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(TAG + "-display",
                mWidth, mHeight, mDpi, 0,
                mSurface, null, null);
        Log.d(TAG, "created virtual display: " + mVirtualDisplay);

        mDrainHandler.post(mDrainEncoderRunnable);

        AudioCaptureThread audioCaptureThread = new AudioCaptureThread();
        audioCaptureThread.start();

    }

    private final class AudioCaptureThread extends Thread {
        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
            final int buf_sz = AudioRecord.getMinBufferSize(
                    sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) ;
            final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buf_sz);
            try {
                ByteBuffer buf = ByteBuffer.allocateDirect(buf_sz);
                int readBytes;
                audioRecord.startRecording();
                try {
                    while (!mQuit.get()) {
                        // read audio data from internal mic
                        buf.clear();
                        readBytes = audioRecord.read(buf, buf_sz);
                        if (readBytes > 0 && !mQuit.get() && mScreenRecorderListener != null) {
                            // set audio data to encoder
                            mScreenRecorderListener.sendAudioFrame(buf, readBytes, System.nanoTime() / 1000, false);
                        }
                    }
                    buf.clear();
                    readBytes = audioRecord.read(buf, buf_sz);
                    mScreenRecorderListener.sendAudioFrame(buf, readBytes, System.nanoTime() / 1000, true);
                } finally {
                    audioRecord.stop();
                }
            } finally {
                audioRecord.release();
            }
        }
    }

    public void release() {
        try {
            Log.i(TAG, "release");
            quit();
            if (mVirtualDisplay != null) {
                mVirtualDisplay.release();
            }
            if (mMediaProjection != null) {
                mMediaProjection.stop();
            }
        } catch (IllegalStateException e){

        }
    }
}
