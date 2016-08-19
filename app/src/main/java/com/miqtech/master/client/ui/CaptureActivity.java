package com.miqtech.master.client.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.ActivityQrcode;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.BitmapUtil;
import com.miqtech.master.client.watcher.Observerable;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.decoding.InactivityTimer;
import com.zxing.decoding.RGBLuminanceSource;
import com.zxing.view.ViewfinderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 二维码扫码页面
 *
 * @author wxn
 */
public class CaptureActivity extends BaseActivity implements Callback, View.OnClickListener {

    @Bind(R.id.viewFinderView)
    ViewfinderView viewfinderView;
    @Bind(R.id.btnBack)
    ImageButton cancelScanButton;
    @Bind(R.id.iBtnOpenFlash)
    ImageButton iBtnOpenFlash;
    @Bind(R.id.iBtnOpenPhotoAlbum)
    ImageButton iBtnOpenPhotoAlbum;

    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private Context context;

    private HashMap<String, String> params = new HashMap<String, String>();
    int ifOpenLight = 0; // 判断是否开启闪光灯

    private Observerable watcher;
    private String netbarId;
    private String id;
    private String round;
    private String teamId;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_capture);
        initData();
        initView();
        watcher = Observerable.getInstance();
    }

    @Override
    protected void initData() {
        super.initData();
        context = this;
    }

    @Override
    protected void initView() {
        super.initView();
        ButterKnife.bind(this);
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        iBtnOpenPhotoAlbum.setOnClickListener(this);
        iBtnOpenFlash.setOnClickListener(this);
    }

    /**
     * 初始化surfaceview
     */
    private void initSurfaceView() {
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.svScan);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

        // quit the scan view
        cancelScanButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CaptureActivity.this.finish();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        initSurfaceView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode 获取结果
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        // FIXME
        if (resultString.equals("")) {
            Toast.makeText(CaptureActivity.this, "扫描失败!", Toast.LENGTH_SHORT)
                    .show();
        } else {
            //    showToast(resultString);
            distinguishCodingType(resultString);
        }
        //      CaptureActivity.this.finish();
    }

    /*
     * 获取带二维码的相片进行扫描
     */
    public void pickPictureFromAblum() {
        // 打开手机中的相册
        Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
        innerIntent.setType("image/*");
        Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
        this.startActivityForResult(wrapperIntent, 1);
    }

    String photo_path;
    Bitmap scanBitmap;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onActivityResult(int, int,
     * android.content.Intent) 对相册获取的结果进行分析
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    try {
                        Uri uri = data.getData();
                        if (!TextUtils.isEmpty(uri.getAuthority())) {
                            Cursor cursor = getContentResolver().query(uri,
                                    new String[]{MediaStore.Images.Media.DATA},
                                    null, null, null);
                            if (null == cursor) {
                                showToast("图片没找到");
                                return;
                            }
                            cursor.moveToFirst();
                            photo_path = cursor.getString(cursor
                                    .getColumnIndex(MediaStore.Images.Media.DATA));
                            cursor.close();
                        } else {
                            photo_path = data.getData().getPath();
                        }
                        showLoading("正在扫描");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Result result = scanningImage(photo_path);
                                if (result != null) {
                                    Message m = mHandler.obtainMessage();
                                    m.what = 1;
                                    m.obj = result.getText();

                                    mHandler.sendMessage(m);
                                } else {
                                    Message m = mHandler.obtainMessage();
                                    m.what = 2;
                                    m.obj = "未在图片中发现二维码!";
                                    mHandler.sendMessage(m);
                                }

                            }
                        }).start();
                    } catch (Exception e) {
                        showToast("未在图片中发现二维码!");
                    }
                    break;
                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            hideLoading();
            switch (msg.what) {
                case 1:
                    String resultString = msg.obj.toString();
                    if (resultString.equals("")) {
                        showToast("扫描失败!");
                    } else {
                        //              showToast(resultString);
                        distinguishCodingType(resultString);
                    }
                    break;
                case 2:
                    showToast("未在图片中发现二维码！");
                    break;
                default:
                    break;
            }

            super.handleMessage(msg);
        }

    };

    /**
     * 扫描二维码图片的方法
     * 目前识别度不高，有待改进
     *
     * @param path
     * @return
     */
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result result = null;
        try {
            result = reader.decode(bitmap1, hints);
            if (result == null) {
                //截图后再次扫码
                scanBitmap = BitmapUtil.cutOutBitmap(BitmapFactory.decodeFile(path), context);
                RGBLuminanceSource source1 = new RGBLuminanceSource(scanBitmap);
                BinaryBitmap bitmap2 = new BinaryBitmap(new HybridBinarizer(source1));
                QRCodeReader reader1 = new QRCodeReader();
                Result result1 = reader1.decode(bitmap2, hints);
                return result1;
            }
            return result;
        } catch (NotFoundException e) {

            try {
                //截图后再次扫码
                scanBitmap = BitmapUtil.cutOutBitmap(BitmapFactory.decodeFile(path), context);
                RGBLuminanceSource source1 = new RGBLuminanceSource(scanBitmap);
                BinaryBitmap bitmap2 = new BinaryBitmap(new HybridBinarizer(source1));
                QRCodeReader reader1 = new QRCodeReader();
                result = reader1.decode(bitmap2, hints);
                return result;
            } catch (NotFoundException e1) {
                e1.printStackTrace();
            } catch (ChecksumException e1) {
                e1.printStackTrace();
            } catch (FormatException e1) {
                e1.printStackTrace();
            } catch (Exception e1) {
                e.printStackTrace();
            }
            return null;

        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }


    // 是否开启闪光灯
    public void openLight() {
        ifOpenLight++;
        switch (ifOpenLight % 2) {
            case 0:
                // 关闭
                CameraManager.get().closeLight();
                iBtnOpenFlash.setImageResource(R.drawable.open_flash_icon);
                break;

            case 1:
                // 打开
                CameraManager.get().openLight(); // 开闪光灯
                iBtnOpenFlash.setImageResource(R.drawable.close_flash_icon);
                break;
            default:
                break;
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iBtnOpenPhotoAlbum:
                pickPictureFromAblum();
                break;
            case R.id.iBtnOpenFlash:
                openLight();
                break;
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        if (method.equals(HttpConstant.CORPS_SESSION_QRCODE)) {
            if (object.has("object")) {
                try {
                    String objStr = object.getString("object");
                    ActivityQrcode activityQrcode = new Gson().fromJson(objStr, ActivityQrcode.class);
                    activityQrcode.setRound(round);
                    activityQrcode.setNetbarId(netbarId);
                    activityQrcode.setId(id);
                    activityQrcode.setTeamId(teamId);
                    Intent intent = new Intent();
                    intent.setClass(this, OfficalEventActivity.class);
                    intent.putExtra("activityQrcode", activityQrcode);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        if (method.equals(HttpConstant.CORPS_SESSION_QRCODE)) {
            try {
                int code = object.getInt("code");
                //0success1无法识别二维码2二维码已过期3已经报名当前场次
                switch (code) {
                    case 1:
                        watcher.notifyChange(Observerable.ObserverableType.QRCODEDIALOG, 2);
                        break;
                    case 2:
                        watcher.notifyChange(Observerable.ObserverableType.QRCODEDIALOG, 2);
                        break;
                    case 3:
                        watcher.notifyChange(Observerable.ObserverableType.APPLYSTATE, 3);
                        break;
                }
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
//        try {
//            if (object.has("result")) {
//                showToast(object.getString("result").toString());
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
    }

    /**
     * @param code 二维码
     */
    private void distinguishCodingType(String code) {
        //如果app扫描二维码出来链接(http://192.168.30.245/load/wy?id=1&round=1&netbarId=1),说明是场次二维码 id为赛事id,round赛事场次,netbarId网吧id
        // 如果app扫描二维码出来链接(http://192.168.30.245/load/wy?id=1),说明是战队二维码,id是战队id
        //扫描赛事二维码返回此链接时(http://192.168.30.245/share/activity/{id}),调用此接口,id为赛事id
        if (code.contains("load/wy?")) {
            //场次
            int index = code.indexOf("?");
            code = code.substring(index + 1, code.length());
            if (code.contains("netbarId")) {
                if (code.contains("netbarId") && code.contains("id") && code.contains("round")) {
                    Map<String, String> map = getMatchParams(code);
                    loadCorpsSession(map.get("id"), map.get("round"), map.get("netbarId"));
                }
            }  //战队
            else if (code.contains("teamId")) {
                if (code.contains("teamId")) {
                    Map<String, String> map = getMatchParams(code);
                    loadJoinCoros(map.get("teamId"));
                }
            }
        } else if (code.contains("share/activity/")) {
            String activityId = code.replace(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ACTIVITYS_URL, "");
            Intent intent = new Intent();
            intent.setClass(this, OfficalEventActivity.class);
            intent.putExtra("matchId", activityId);
            startActivity(intent);
        } else if (code.contains("share/netbar/")) {
            String netbarId = code.substring(code.lastIndexOf("/") + 1);
            Intent intent = new Intent();
            intent.setClass(this, InternetBarActivityV2.class);
            intent.putExtra("netbarId", netbarId);
            startActivity(intent);
        } else if (code.contains("http://") || code.contains("https://")) {
            createOutSideUrlDialog(code);
        }
    }

    private void createOutSideUrlDialog(final String url) {
        final Dialog outSideUrlDialog = new Dialog(this);
        try {
            int dividerID = context.getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = outSideUrlDialog.findViewById(dividerID);
            divider.setBackgroundColor(Color.TRANSPARENT);
        } catch (Exception e) {            //上面的代码，是用来去除Holo主题的蓝色线条
            e.printStackTrace();
        }
        Window window = outSideUrlDialog.getWindow();
        window.setContentView(R.layout.layout_match_status_dialog);
        outSideUrlDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.CENTER);
        outSideUrlDialog.setCanceledOnTouchOutside(false);
        TextView tvTip = (TextView) outSideUrlDialog.findViewById(R.id.tvTip);
        Button btnCancel = (Button) outSideUrlDialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                outSideUrlDialog.dismiss();
            }
        });
        Button btnSure = (Button) outSideUrlDialog.findViewById(R.id.btnSure);
        btnSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, SubjectActivity.class);
                intent.putExtra("outSideUrl", url);
                intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.OPEN_OUTSIDE_URL);
                startActivity(intent);
                outSideUrlDialog.dismiss();
            }
        });
        TextView tvContent = (TextView) outSideUrlDialog.findViewById(R.id.tvContent);
        tvTip.setText("即将打开网址");
        tvContent.setText(url);
        outSideUrlDialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = outSideUrlDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        outSideUrlDialog.getWindow().setAttributes(lp);
    }

    /**
     * 解析CODE获取场次参数
     *
     * @param code
     * @return
     */

    private Map<String, String> getMatchParams(String code) {
        String[] codes = code.split("&");
        HashMap<String, String> map = new HashMap<String, String>();
        List<String> codesList = Arrays.asList(codes);
        for (int i = 0; i < codesList.size(); i++) {
            String str = codesList.get(i);
            String[] strs = str.split("=");
            if (strs.length == 2) {
                map.put(strs[0], strs[1]);
            }
        }
        return map;
    }


    /**
     * 战队场次二维码接口
     *
     * @param id
     * @param round
     * @param netbarId
     */
    private void loadCorpsSession(String id, String round, String netbarId) {
        params.clear();
        params.put("id", id);
        params.put("round", round);
        params.put("netbarId", netbarId);
        if (WangYuApplication.getUser(context) != null) {
            params.put("userId", WangYuApplication.getUser(context).getId());
            params.put("token", WangYuApplication.getUser(context).getToken());
            showLoading();
        } else {
            finish();
            watcher.notifyChange(Observerable.ObserverableType.QRCODEDIALOG, 1);
            return;
        }
        this.id = id;
        this.round = round;
        this.netbarId = netbarId;
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.CORPS_SESSION_QRCODE, params, HttpConstant.CORPS_SESSION_QRCODE);
    }

    /**
     * 战队二维码接口
     *
     * @param teamId
     */
    private void loadJoinCoros(String teamId) {
        params.clear();
        params.put("teamId", teamId);
        if (WangYuApplication.getUser(context) != null) {
            showLoading();
            params.put("userId", WangYuApplication.getUser(context).getId());
            params.put("token", WangYuApplication.getUser(context).getToken());
        } else {
            finish();
            watcher.notifyChange(Observerable.ObserverableType.QRCODEDIALOG, 1);
            return;
        }
        this.teamId = teamId;
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.CORPS_SESSION_QRCODE, params, HttpConstant.CORPS_SESSION_QRCODE);
    }
}
