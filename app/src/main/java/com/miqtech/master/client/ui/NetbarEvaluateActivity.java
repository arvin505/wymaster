package com.miqtech.master.client.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.AlbumAdapter;
import com.miqtech.master.client.adapter.EvaluateImgAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.CompleteTask;
import com.miqtech.master.client.entity.ImageBean;
import com.miqtech.master.client.entity.OrderInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.FileUploadUtil;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.MyPagerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * 订单评价
 * Created by Administrator on 2015/12/7.
 */

public class NetbarEvaluateActivity extends BaseActivity implements View.OnClickListener, TextWatcher,
        RatingBar.OnRatingBarChangeListener, AdapterView.OnItemClickListener {

    private LinearLayout llUploadPics;
    private RatingBar rbEnvironment, rbComputerCon, rbNet, rbNetbarServe;
    private EditText edtEvaluate;
    private CheckBox cbAnonymity;
    private Button btnSure;
    private TextView btnImg;
    private TextView tvSurplus;
    public SlidingDrawer drawer;
    private MyPagerView vpContent;

    public List<String> pics = new ArrayList<String>();
    private List<ImageBean> albumList = new ArrayList<ImageBean>();
    public List<String> photoList = new ArrayList<String>();
    private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
    private List<String> needUploadPics = new ArrayList<String>();
    private ArrayList<String> uploadPics = new ArrayList<String>();

    private LinearLayout llNetbarServe, llNet, llComputerCon, llEnvironment;

    private int camera;
    private int album;
    private EvaluateImgAdapter adapter;
    private AlbumAdapter albumAdapter;
    private EvaluateImgAdapter photoAdapter;
    private MyPagerAdapter pagAdapter;

    public String imagenames = "";
    public File photoFile = null;
    public String serverPhoto;
    Bitmap photo;
    private int evaluateNum = 150;

    private Context mContext;

    private final static int SCAN_OK = 1;
    private final static int LOAD_OK = 2;
    private final static int UPLOAD_SUCCESS_PHOTO = 3;
    private final static int UPLOAD_FAILED_PHOTO = 4;
    public static final int SHOW_HEAD = 0;

    private static final int REQUEST_ZOOM = 3;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_netbar_evaluate);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mContext = NetbarEvaluateActivity.this;
        camera = R.drawable.camera;
        album = R.drawable.album;
        pics.add(0, camera + "");
        pics.add(1, album + "");
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        setLeftIncludeTitle("我要评价");
        rbEnvironment = (RatingBar) findViewById(R.id.rbEnvironment);
        rbComputerCon = (RatingBar) findViewById(R.id.rbComputerCon);
        rbNet = (RatingBar) findViewById(R.id.rbNet);
        rbNetbarServe = (RatingBar) findViewById(R.id.rbNetbarServe);
        edtEvaluate = (EditText) findViewById(R.id.edtEvaluate);
        btnImg = (TextView) findViewById(R.id.btnImg);
        btnSure = (Button) findViewById(R.id.btnSure);
        tvSurplus = (TextView) findViewById(R.id.tvSurplus);
        cbAnonymity = (CheckBox) findViewById(R.id.cbAnonymity);
        drawer = (SlidingDrawer) findViewById(R.id.slidingdrawer);
        vpContent = (MyPagerView) findViewById(R.id.vpContent);
        llUploadPics = (LinearLayout) findViewById(R.id.llUploadPics);
        llNetbarServe = (LinearLayout) findViewById(R.id.llNetbarServe);
        llNet = (LinearLayout) findViewById(R.id.llNet);
        llComputerCon = (LinearLayout) findViewById(R.id.llComputerCon);
        llEnvironment = (LinearLayout) findViewById(R.id.llEnvironment);
        edtEvaluate.setOnClickListener(this);

        llNetbarServe.setOnClickListener(this);
        llNet.setOnClickListener(this);
        llComputerCon.setOnClickListener(this);
        llEnvironment.setOnClickListener(this);
        rbEnvironment.setOnRatingBarChangeListener(this);
        rbComputerCon.setOnRatingBarChangeListener(this);
        rbNet.setOnRatingBarChangeListener(this);
        rbNetbarServe.setOnRatingBarChangeListener(this);
        edtEvaluate.addTextChangedListener(this);
        btnImg.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        edtEvaluate.addTextChangedListener(this);

        View headView = LayoutInflater.from(this).inflate(R.layout.my_heads, null);
        TextView headText = (TextView) headView.findViewById(R.id.iv_back_album);
        headText.setVisibility(View.GONE);
        GridView imageGrid = (GridView) headView.findViewById(R.id.head_Gride);

        View albumView = (View) LayoutInflater.from(this).inflate(R.layout.my_album, null);
        TextView albumBack = (TextView) albumView.findViewById(R.id.iv_back_head);
        albumBack.setOnClickListener(this);
        GridView albumGrid = (GridView) albumView.findViewById(R.id.album_Gride);

        View photoView = LayoutInflater.from(this).inflate(R.layout.my_photos, null);
        TextView photoBack = (TextView) photoView.findViewById(R.id.iv_back_album);
        photoBack.setOnClickListener(this);
        GridView mGroupGridView = (GridView) photoView.findViewById(R.id.photo_Gride);

        imageGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new EvaluateImgAdapter(this, pics);
        imageGrid.setAdapter(adapter);
        imageGrid.setOnItemClickListener(this);

        albumGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
        albumAdapter = new AlbumAdapter(this, albumList);
        albumGrid.setAdapter(albumAdapter);
        albumGrid.setOnItemClickListener(this);

        mGroupGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        photoAdapter = new EvaluateImgAdapter(this, photoList);
        mGroupGridView.setAdapter(photoAdapter);
        mGroupGridView.setOnItemClickListener(this);
        pagAdapter = new MyPagerAdapter();
        vpContent.setAdapter(pagAdapter);
        pagAdapter.addItemView(headView);
        pagAdapter.addItemView(albumView);
        pagAdapter.addItemView(photoView);

        rbEnvironment.setOnRatingBarChangeListener(myRatingBarChangeListener);
        rbComputerCon.setOnRatingBarChangeListener(myRatingBarChangeListener);
        rbNetbarServe.setOnRatingBarChangeListener(myRatingBarChangeListener);
        rbNet.setOnRatingBarChangeListener(myRatingBarChangeListener);

    }

    @Override
    protected void initData() {
        super.initData();
        getCameraImages();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        try {
            Object obj = null;
            if (object.has("object")) {
                obj = object.getString("object");
            } else {
                obj = object.toString();
            }

            if (HttpConstant.NETBAR_EVA.equals(method)) {
                int coinNum = 0;
                JSONObject jsonObj = new JSONObject(obj.toString());
                if (jsonObj.has("extend")) {
                    String strExtend = jsonObj.getString("extend");
                    if (!TextUtils.isEmpty(strExtend) && (!strExtend.equals("{}"))) {
                        JSONObject jsonExtend = new JSONObject(strExtend);
                        ArrayList<CompleteTask> tasks = new Gson().fromJson(jsonExtend.getString("completeTasks"),
                                new TypeToken<List<CompleteTask>>() {
                                }.getType());
                        if (tasks.size() > 0) {
                            CompleteTask task = tasks.get(0);
                            coinNum = task.getCoin();
                        }
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("coinNum", coinNum);
                intent.setClass(this, ThankEvaluateActivity.class);
                startActivity(intent);
                finish();
                BroadcastController.sendUserChangeBroadcase(mContext);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        showToast(errMsg);
    }

    public void setPicWithoutCut(Intent data, Uri defaultUri) {
        Uri uri = null;
        Bitmap image = null;
        if (data != null) {
            uri = data.getData();
        } else if (defaultUri != null) {
            uri = defaultUri;
        }
        if (uri != null) {

            try {
                //这个方法是根据Uri获取Bitmap图片的静态方法
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                if (image != null) {
                    //photo.setImageBitmap(image);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bundle extras = data.getExtras();
            if (extras != null) {
                //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
                image = extras.getParcelable("data");
                if (image != null) {
                    //photo.setImageBitmap(image);
                }
            }
        }

        if (image != null) {
            OutputStream baos = null;
            try {
                baos = new FileOutputStream(photoFile);
                image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 把数据写入文件
                showLoading();
                final Map<String, String> fileMap = new HashMap<>();
                fileMap.put("bgImg", photoFile.getAbsolutePath());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String ss = FileUploadUtil.uploadImageFile(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.UPLOAD_PIC, null, fileMap, myHandler);
                        LogUtil.e("ss", "st  = " + ss);
                    }
                }).start();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (baos != null) {
                        baos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 剪裁图片的方法
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 720);
        intent.putExtra("outputY", 720);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 保存剪裁之后的图片
     *
     * @param picdata
     * @throws IOException
     */
    private void setPicToView(Intent picdata) throws IOException {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            photo = extras.getParcelable("data");
            // 压缩
            photo = imageZoom(photo);
            photoFile = new File(getCacheDir(), imagenames);
            savePic(photo, photoFile.getPath());
            handler.sendEmptyMessage(SHOW_HEAD);
        }

    }

    public Bitmap imageZoom(Bitmap bitMap) {
        // 图片允许最大空间 ：单位：KB
        double maxSize = 200.00;
        // 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 70 -- 压缩率 ；
        // 100是不压缩、压缩率为0；
        byte[] b = baos.toByteArray();

        // 将字节换成KB
        double mid = b.length / 1024;
        // 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            // 获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            // 开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
            // （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i), bitMap.getHeight() / Math.sqrt(i));
            return bitMap;
        }

        return bitMap;
    }

    private static void savePic(Bitmap b, String strFileName) {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * 图片的缩放方法
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
        return bitmap;
    }

    private void getCameraImages() {
        showLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取SDcard卡的路径
                String sdcardPath = Environment.getExternalStorageDirectory().toString();

                ContentResolver mContentResolver = NetbarEvaluateActivity.this.getContentResolver();
                Cursor mCursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{
                                MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA}, MediaStore.Images.Media.MIME_TYPE
                                + "=? OR " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media._ID + " DESC"); // 按图片ID降序排列

                if (mCursor != null && mCursor.getCount() > 0) {
                    while (mCursor.moveToNext()) {
                        // 打印LOG查看照片ID的值
                        long id = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media._ID));
                        //Log.i("MediaStore.Images.Media_ID=", id + "");

                        // 过滤掉不需要的图片，只获取拍照后存储照片的相册里的图片
                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        if (path != null && path.startsWith(sdcardPath + "/DCIM/100MEDIA") || path.startsWith(sdcardPath + "/DCIM/Camera/")
                                || path.startsWith(sdcardPath + "DCIM/100Andro")) {
                            pics.add(path);
                        }
                    }
                }
                if (mCursor != null) {
                    mCursor.close();
                }
                handler.sendEmptyMessage(LOAD_OK);
            }
        }).start();
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
     */
    public void getImages() {
        // 显示进度条
        showLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = NetbarEvaluateActivity.this.getContentResolver();
                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?", new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);
                if (mCursor == null) {
                    return;
                }
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    // 获取该图片的父路径名
                    String parentName = new File(path).getParentFile().getName();

                    // 根据父路径名将图片放入到mGruopMap中
                    if (!mGruopMap.containsKey(parentName)) {
                        List<String> chileList = new ArrayList<String>();
                        chileList.add(path);
                        mGruopMap.put(parentName, chileList);
                    } else {
                        mGruopMap.get(parentName).add(path);
                    }
                }
                // 通知Handler扫描图片完成
                handler.sendEmptyMessage(SCAN_OK);
                mCursor.close();
            }
        }).start();
    }

    public Handler handler = new Handler() {

        // private JSONObject objectStr;
        // private int code;
        // private String result;

        @Override
        public void handleMessage(Message msg) {
            hideLoading();
            switch (msg.what) {
                case SCAN_OK:
                    albumList.clear();
                    albumList.addAll(subGroupOfImage(mGruopMap));
                    albumAdapter.notifyDataSetChanged();
                    vpContent.setCurrentItem(1);
                    break;
                case SHOW_HEAD:
                    uploadaAndAddPic2View();
                    break;
                case LOAD_OK:
                    hideLoading();
                    adapter.notifyDataSetChanged();
                    break;
                case UPLOAD_SUCCESS_PHOTO:
                    try {
                        JSONObject jsonObj = new JSONObject(msg.obj.toString());
                        String imgName = jsonObj.getString("object");
                        needUploadPics.add(serverPhoto);
                        uploadPics.add(imgName);
                        addPicView(imgName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case UPLOAD_FAILED_PHOTO:
                    showToast("上传失败");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void uploadaAndAddPic2View() {
        if (needUploadPics.size() < 4) {
            if (needUploadPics.size() > 0) {
                for (int i = 0; i < needUploadPics.size(); i++) {
                    if (needUploadPics.get(i).equals(photoFile.getPath())) {
                        showToast("请勿上传同一张图片");
                        return;
                    } else {
                        uploadUserPhoto();
                        return;
                    }
                }
            } else {
                uploadUserPhoto();
                needUploadPics.add(photoFile.getPath());
            }
        } else {
            showToast("最多传三张图片");
        }
    }

    private void addPicView(String imgName) {
        View view = View.inflate(this, R.layout.layout_img_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.ivImg);
        AsyncImage.loadPhoto(this, HttpConstant.SERVICE_UPLOAD_AREA + imgName, imageView);
        view.setTag(imgName);
        llUploadPics.addView(view);
        view.setOnClickListener(this);
    }

    private void uploadUserPhoto() {
        showLoading();
        showLoading();
        final Map<String, String> fileMap = new HashMap<>();
        fileMap.put("pic", photoFile.getPath());
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ss = FileUploadUtil.uploadImageFile(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.UPLOAD_PIC, null, fileMap, myHandler);
                LogUtil.e("ss", "st  = " + ss);
            }
        }).start();
    }

    private UploadUserPicHandler myHandler = new UploadUserPicHandler();

    private class UploadUserPicHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideLoading();
            Bundle data = msg.getData();
            String imgResult = data.getString("result");
            try {
                JSONObject jsonResult = new JSONObject(imgResult);
                if (jsonResult.getInt("code") == 0) {
                    showToast("上传成功");
                    String imgName = jsonResult.getString("object");
                    needUploadPics.add(serverPhoto);
                    uploadPics.add(imgName);
                    addPicView(imgName);
                } else {
                    showToast("上传失败");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadNetbarEva() {
        OrderInfo orderInfo = (OrderInfo) getIntent().getSerializableExtra("orderInfo");
        User user = WangYuApplication.getUser(this);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("enviroment", rbEnvironment.getRating() + "");
        map.put("equipment", rbComputerCon.getRating() + "");
        map.put("network", rbNet.getRating() + "");
        map.put("service", rbNetbarServe.getRating() + "");
        map.put("netbarId", orderInfo.getNetbar_id());
        map.put("orderId", orderInfo.getOrder_id());

        if (cbAnonymity.isChecked()) {
            map.put("isAnonymous", 1 + "");
        } else {
            map.put("isAnonymous", 0 + "");
        }
        if (uploadPics.size() > 0) {
            String imgs = getImgNames();
            map.put("imgs", imgs);
        }
        if (!TextUtils.isEmpty(edtEvaluate.getText().toString().trim())) {
            map.put("content", edtEvaluate.getText().toString());
        }
        // rbEnvironment.get
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.NETBAR_EVA, map, HttpConstant.NETBAR_EVA);
        showLoading();
    }

    private String getImgNames() {
        String tempName = "";
        for (int i = 0; i < uploadPics.size(); i++) {
            if (i != uploadPics.size() - 1) {
                tempName += uploadPics.get(i) + ",";
            } else {
                tempName += uploadPics.get(i);
            }
        }
        return tempName;
    }

    /**
     * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中 所以需要遍历HashMap将数据组装成List
     *
     * @param mGruopMap
     * @return
     */
    private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap) {
        List<ImageBean> list = new ArrayList<ImageBean>();
        if (mGruopMap.size() == 0) {
            return list;
        }

        Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = it.next();
            ImageBean mImageBean = new ImageBean();
            String key = entry.getKey();
            List<String> value = entry.getValue();

            mImageBean.setFolderName(key);
            mImageBean.setImageCounts(value.size());
            mImageBean.setTopImagePath(value.get(0));// 获取该组的第一张图片
            list.add(mImageBean);
        }

        return list;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnImg:
                if (drawer.isOpened()) {
                    drawer.animateClose();
                } else {
                    drawer.animateOpen();
                }
                break;
            case R.id.iv_back_album:
                vpContent.setCurrentItem(1);
                vpContent.findFocus();
                break;
            case R.id.iv_back_head:
                vpContent.setCurrentItem(0);
                break;
            case R.id.btnSure:
                uploadNetbarEva();
                break;
            case R.id.ibLeft:
                if (drawer.isOpened()) {
                    drawer.animateClose();
                } else {
                    onBackPressed();
                }
                break;
            case R.id.llComputerCon:
                if (drawer.isOpened()) {
                    drawer.animateClose();
                }
                break;
            case R.id.llNet:
                if (drawer.isOpened()) {
                    drawer.animateClose();
                }
                break;
            case R.id.llNetbarServe:
                if (drawer.isOpened()) {
                    drawer.animateClose();
                }
                break;
            case R.id.llEnvironment:
                if (drawer.isOpened()) {
                    drawer.animateClose();
                }
                break;
            case R.id.edtEvaluate:
                if (drawer.isOpened()) {
                    drawer.animateClose();
                }
                break;
            case R.id.llImgView:
                Intent intent = new Intent();
                intent.setClass(this, NetbarEvaluatePhotoActivity.class);
                intent.putStringArrayListExtra("images", uploadPics);
                String imgName = (String) v.getTag();
                int index = uploadPics.indexOf(imgName);
                intent.putExtra("image_index", index);
                startActivityForResult(intent, 4);
                break;
            // case R.id.scrollView:
            // if(drawer.isOpened()){
            // drawer.animateClose();
            // }
            // break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(s.toString().trim())) {
            int surplus = evaluateNum - s.toString().length();
            tvSurplus.setText("剩余" + surplus + "字");
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

    }

    class MyPagerAdapter extends PagerAdapter {

        public List<View> list = new ArrayList<View>();

        public MyPagerAdapter() {
            super();
        }

        @Override
        public int getCount() {

            if (list != null && list.size() > 0) {
                return list.size();
            } else {
                return 0;
            }
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void addItemView(View view) {
            list.add(view);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.head_Gride:

                break;
            case R.id.album_Gride:
                photoList.clear();
                photoList.addAll(mGruopMap.get(albumList.get(position).getFolderName()));
                photoAdapter.notifyDataSetChanged();
                vpContent.setCurrentItem(2);
                break;
            case R.id.photo_Gride:
                break;
        }

    }

    ;

    private RatingBar.OnRatingBarChangeListener myRatingBarChangeListener = new RatingBar.OnRatingBarChangeListener() {

        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            if (rbEnvironment.getRating() > 0 && rbComputerCon.getRating() > 0 && rbNetbarServe.getRating() > 0
                    && rbNet.getRating() > 0) {
                btnSure.setEnabled(true);
            } else {
                btnSure.setEnabled(false);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            setPicWithoutCut(data, null);
            //          startPhotoZoom(Uri.fromFile(photoFile));
        } else if (requestCode == 3) {
            /**
             * 非空判断大家一定要验证，如果不验证的话， 在剪裁之后如果发现不满意，要重新裁剪，丢弃
             * 当前功能时，会报NullException，可以根据不同情况在合适的 地方做判断处理类似情况
             *
             */
            if (data != null) {
                setPicWithoutCut(data, null);
            }
        } else if (requestCode == 4) {
            if (data != null) {
                ArrayList<String> newImages = data.getStringArrayListExtra("images");
                int position = data.getIntExtra("position", 0);
                if (position < needUploadPics.size()) {
                    needUploadPics.remove(position);
                }
                llUploadPics.removeAllViews();
                uploadPics.clear();
                uploadPics.addAll(newImages);
                for (String string : newImages) {
                    addPicView(string);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
