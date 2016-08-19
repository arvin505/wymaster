package com.miqtech.master.client.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.ui.SettingActivity;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DownloadService extends Service {
    private static final int NOTIFY_ID = 0;
    private int progress;
    private NotificationManager mNotificationManager;
    private boolean canceled;
    /* 下载包安装路径 */
    private static String savePath;
    private String apkUrl = "";
    private static String saveFileName;
    private SettingActivity.ICallbackResult callback;
    private DownloadBinder binder;
    private WangYuApplication app;
    private boolean serviceIsDestroy = false;

    public static final int TYPE_APP = 1;
    public static final int TYPE_PATCH = 2;

    private int updateType;

    private int patchCode;

    private Context mContext = this;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    app.setDownload(false);
                    // 下载完毕
                    // 取消通知
                    if (updateType == 1) {
                        mNotificationManager.cancel(NOTIFY_ID);
                        installApk();
                    } else { //热修复完成，写入patchCode
                        //ToastUtil.showToast("热修复完成", DownloadService.this);
                        LogUtil.e("Download", "----------------------fixend------------------");
                        PreferencesUtil.saveHotFixPatchCode(DownloadService.this, patchCode);
                    }
                    break;
                case 2:
                    app.setDownload(false);
                    // 这里是用户界面手动取消，所以会经过activity的onDestroy();方法
                    // 取消通知
                    mNotificationManager.cancel(NOTIFY_ID);
                    break;
                case 1:
                    int rate = msg.arg1;
                    app.setDownload(true);
                    if (rate < 100) {
                        LogUtil.i("downLoad", "downLoad-----进度" + rate);
                        if (mNotification != null) {
                            RemoteViews contentview = mNotification.contentView;
                            contentview.setTextViewText(R.id.content_view_text1, rate + "%");
                            contentview.setProgressBar(R.id.content_view_progress, 100, rate, false);
                        }
                    } else {
                        System.out.println("下载完毕!!!!!!!!!!!");
                        // 下载完毕后变换通知形式
                        if (mNotification != null) {
                            mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                            mNotification.contentView = null;
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            // 更新参数,注意flags要使用FLAG_UPDATE_CURRENT
                            PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                            builder.setContentTitle("下载完成");
                            builder.setContentText("文件已下载完毕");
                            builder.setContentIntent(contentIntent);
                            mNotification = builder.getNotification();
                            //
                            serviceIsDestroy = true;
                            stopSelf();// 停掉服务自身
                        }
                    }
                    if (mNotificationManager != null && mNotification != null) {
                        mNotificationManager.notify(NOTIFY_ID, mNotification);
                    }
                    break;
            }
        }
    };
    private Notification.Builder builder;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        System.out.println("是否执行了 onBind");
        Bundle data = intent.getExtras();
        if(data!=null) {
            LogUtil.e("Bundle", "-----------data------" + data.size());
            for (int i = 0; i < data.size(); i++) {
                LogUtil.e("Bundle", "-----------data-- sssss----" + i);
            }
            apkUrl = data.getString("apkUrl");
            updateType = data.getInt("type", TYPE_APP);
            patchCode = data.getInt("patchCode", -1);
        }
            if (binder == null) {
                binder = new DownloadBinder();
            }
            if (updateType == 1) {
                savePath = getSDPath();
                saveFileName = savePath + "/Download/WYMaster_Client.apk";
            } else {
                savePath = Constant.FIXPATCH;
                saveFileName = savePath + Constant.PATCNAME;
            }
            LogUtil.e("TAG", "---------onStartCommand---------" + updateType + "   ---- +" + patchCode);
            LogUtil.e("TAG", "---------url---------" + apkUrl);
            binder.start();

        return binder;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        System.out.println("downloadservice ondestroy");
        // 假如被销毁了，无论如何都默认取消了。
        app.setDownload(false);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        System.out.println("downloadservice onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        // TODO Auto-generated method stub

        super.onRebind(intent);
        System.out.println("downloadservice onRebind");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        app = (WangYuApplication) getApplication();
    }

    public class DownloadBinder extends Binder {
        public void start() {
            if (downLoadThread == null || !downLoadThread.isAlive()) {
                progress = 0;
                if (updateType == 1) {
                    setUpNotification();
                }
                new Thread() {
                    public void run() {
                        // 下载
                        startDownload();
                    }
                }.start();
            }
        }

        public void cancel() {
            canceled = true;
        }

        public int getProgress() {
            return progress;
        }

        public boolean isCanceled() {
            return canceled;
        }

        public boolean serviceIsDestroy() {
            return serviceIsDestroy;
        }

        public void cancelNotification() {
            mHandler.sendEmptyMessage(2);
        }

        public void addCallback(SettingActivity.ICallbackResult callback) {
            DownloadService.this.callback = callback;
        }
    }

    private void startDownload() {
        // TODO Auto-generated method stub
        canceled = false;
        downloadApk();
    }

    //
    Notification mNotification;

    // 通知栏

    /**
     * 创建通知
     */
    private void setUpNotification() {
        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = "开始下载";
        long when = System.currentTimeMillis();
        builder = new Notification.Builder(mContext);
        builder.setSmallIcon(icon);
        builder.setTicker(tickerText);
        builder.setWhen(when);
        mNotification = builder.getNotification();
        // 放置在"正在运行"栏目中
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.layout_download_view);
        contentView.setTextViewText(R.id.content_view_text1, "网娱大师 正在下载...");
        // 指定个性化视图
        mNotification.contentView = contentView;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 下面两句是 在按home后，点击通知栏，返回之前activity 状态;
        // 有下面两句的话，假如service还在后台下载， 在点击程序图片重新进入程序时，直接到下载界面，相当于把程序MAIN 入口改了 - -
        // 是这么理解么。。。
        // intent.setAction(Intent.ACTION_MAIN);
        // intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 指定内容意图
        mNotification.contentIntent = contentIntent;
        mNotificationManager.notify(NOTIFY_ID, mNotification);
    }

    //
    /**
     * 下载apk
     *
     * @param url
     */
    private Thread downLoadThread;

    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        PreferencesUtil.saveHotFixPatchCode(this, -1);
        mContext.startActivity(i);
    }

    private int lastRate = 0;
    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);
                LogUtil.e("TAG", "---------------constans-----------------");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }

                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.arg1 = progress;
                    if (progress >= lastRate + 1) {
                        mHandler.sendMessage(msg);
                        lastRate = progress;
                        if (callback != null)
                            callback.OnBackResult(progress);
                    }
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(0);
                        // 下载完了，cancelled也要设置
                        canceled = true;
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!canceled);// 点击取消就停止下载.

                fos.close();
                is.close();

                LogUtil.e("path","--------------path-file-----------" + ApkFile.getAbsolutePath());
            } catch (Exception e) {
                LogUtil.e("Exception", "---------------exception----------");
                e.printStackTrace();
            }

        }
    };

    public String getSDPath() {
        File sdDir = null;
        List<String> SDCard = getExtSDCardPath(); // 判断sd卡是否存在
//		if (SDCard.size()>0) {
//			sdDir = new File(SDCard.get(0));// 获取跟目录
//		}else{
//			
//		}   
        sdDir = new File(getInnerSDCardPath());
        return sdDir.toString();
    }

    /**
     * 获取内置SD卡路径
     *
     * @return
     */
    public String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 获取外置SD卡路径
     *
     * @return 应该就一条记录或空
     */
    public List<String> getExtSDCardPath() {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard")) {
                    String[] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory()) {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return lResult;
    }

}
