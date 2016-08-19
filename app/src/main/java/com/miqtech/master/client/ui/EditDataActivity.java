package com.miqtech.master.client.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.AlbumAdapter;
import com.miqtech.master.client.adapter.HeadImageAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.City;
import com.miqtech.master.client.entity.ImageBean;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.FileUploadUtil;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.view.DeleteView;
import com.miqtech.master.client.view.MyPagerView;
import com.miqtech.master.client.view.DeleteView.IDialogOnclickInterface;

public class EditDataActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
    private ImageView ivUserHead;
    private EditText edtNickName;
    private Button btnSure;
    public List<String> myHeads = new ArrayList<String>();
    private int camera;
    private int album;
    private MyPagerView vPager;
    private GridView imageGrid;
    private HeadImageAdapter adapter;

    public Boolean isWitch = false;
    public File photoFile = null;
    Bitmap photo;
    public String serverPhoto = "";

    private Uri imageUri; // 图片路径
    private String filename; // 图片名称
    private File outputImage;

    private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
    public List<String> photoList = new ArrayList<String>();
    private final static int SCAN_OK = 1;
    public static final int SHOW_HEAD = 0;
    protected static final int UPLOAD_SUCCESS = 2;
    protected static final int UPLOAD_FAILURE = 3;
    private GridView mGroupGridView;

    private List<ImageBean> albumList = new ArrayList<ImageBean>();
    private LayoutInflater inflater;
    private GridView albumGrid;
    private AlbumAdapter albumAdapter;
    private HeadImageAdapter photoAdapter;
    private MyPagerAdapter pagAdapter;
    private View albumView;
    private TextView albumBack;
    private TextView photoBack;
    private TextView tvCurrentCity;
    private RelativeLayout rlCurrentCity;
    private View photoView;
    private View headView;
    private TextView headText;
    private Context mContext;
    private LayoutParams layoutparams;
    public SlidingDrawer slidingView;
    private User user;
    private Intent intent;
    private View topLayout;
    private TextView edtSex;
    private EditText edtPersonSign;
    private DeleteView sexDialog;
    private City city;

    private ImageView back;
    private RelativeLayout rlPassword;
    private TextView tvPassword;

    private boolean isSettingPassword = false;

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        mContext = this;
        setContentView(R.layout.activity_editdata);
        user = WangYuApplication.getUser(mContext);
        initView();
    }

    @Override
    protected void initData() {
        super.initData();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();
//        getLeftBtn().setOnClickListener(this);
        //setLeftBtnImage(R.drawable.back);
        back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(this);
        setLeftIncludeTitle("完善资料");
        inflater = LayoutInflater.from(this);
        camera = R.drawable.camera;
        album = R.drawable.album;
        myHeads.add(0, camera + "");
        myHeads.add(1, album + "");
        topLayout = findViewById(R.id.layoutTop);
        topLayout.setOnClickListener(this);
        topLayout.setFocusableInTouchMode(true);
        topLayout.requestFocusFromTouch();

        sexDialog = new DeleteView(mContext, R.style.delete_style, R.layout.sex_dialog);
        slidingView = (SlidingDrawer) findViewById(R.id.slidingdrawer);
        vPager = (MyPagerView) findViewById(R.id.content);

        ivUserHead = (ImageView) findViewById(R.id.ivUserHead);
        edtNickName = (EditText) findViewById(R.id.edtNickName);
        edtSex = (TextView) findViewById(R.id.edtSex);
        edtPersonSign = (EditText) findViewById(R.id.edtPersonSign);
        rlCurrentCity = (RelativeLayout) findViewById(R.id.rlCurrentCity);
        tvCurrentCity = (TextView) findViewById(R.id.tvMyCurrentCity);
        rlPassword = (RelativeLayout) findViewById(R.id.rlPassword);
        tvPassword = (TextView) findViewById(R.id.tvPassword);

        rlPassword.setOnClickListener(this);
        edtSex.setOnClickListener(this);

        btnSure = (Button) findViewById(R.id.btnSure);

        headView = inflater.inflate(R.layout.my_heads, null);
        headText = (TextView) headView.findViewById(R.id.iv_back_album);
        headText.setVisibility(View.GONE);
        imageGrid = (GridView) headView.findViewById(R.id.head_Gride);

        albumView = (View) inflater.inflate(R.layout.my_album, null);
        albumBack = (TextView) albumView.findViewById(R.id.iv_back_head);
        albumBack.setOnClickListener(this);
        albumGrid = (GridView) albumView.findViewById(R.id.album_Gride);

        photoView = inflater.inflate(R.layout.my_photos, null);
        photoBack = (TextView) photoView.findViewById(R.id.iv_back_album);
        photoBack.setOnClickListener(this);
        mGroupGridView = (GridView) photoView.findViewById(R.id.photo_Gride);


        imageGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new HeadImageAdapter(this, myHeads);
        imageGrid.setAdapter(adapter);
        imageGrid.setOnItemClickListener(this);

        albumGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
        albumAdapter = new AlbumAdapter(this, albumList);
        albumGrid.setAdapter(albumAdapter);
        albumGrid.setOnItemClickListener(this);

        mGroupGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        photoAdapter = new HeadImageAdapter(this, photoList);
        mGroupGridView.setAdapter(photoAdapter);
        mGroupGridView.setOnItemClickListener(this);
        pagAdapter = new MyPagerAdapter();
        vPager.setAdapter(pagAdapter);
        pagAdapter.addItemView(headView);
        pagAdapter.addItemView(albumView);
        pagAdapter.addItemView(photoView);

        tvCurrentCity.setText(user.getCityName());

        ivUserHead.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        rlCurrentCity.setOnClickListener(this);
        edtNickName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingView.close();
            }
        });
        edtNickName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    slidingView.close();
                } else {
                    // 此处为失去焦点时的处理内容
                }
            }
        });
        uploadUserData();

        if (!TextUtils.isEmpty(user.getIconMedia())) {
            AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + user.getIconMedia(), ivUserHead);
        }
        edtNickName.setText(user.getNickname());
        if (user.getSex().equals("0")) {
            edtSex.setText("男");
        } else if (user.getSex().equals("1")) {
            edtSex.setText("女");
        }
        edtPersonSign.setText(user.getSpeech());

        if (user.getIsPasswordNull() == 1) {    //密码是否为空1是0否
            tvPassword.setText("设置");
        } else {
            tvPassword.setText("修改");
        }
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

    private void uploadUserData() {
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_HEADS, null, HttpConstant.MY_HEADS);
        showLoading();
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        hideLoading();
        super.onSuccess(object, method);
        try {
            String obj = object.getJSONArray("object").toString();
            Gson gs = new Gson();
            String objStr = obj.toString();
            if (method.equals(HttpConstant.MY_HEADS)) {
                if (!TextUtils.isEmpty(objStr) && !"success".equals(objStr)) {
                    List<String> headsNew = gs.fromJson(objStr, new TypeToken<List<String>>() {
                    }.getType());
                    myHeads.addAll(headsNew);
                }
                initData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(String method, String errorInfo) {
        hideLoading();
        showToast(errorInfo);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
//        if (!TextUtils.isEmpty(user.getIconMedia())) {
//            AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + user.getIconMedia(), ivUserHead);
//        }
//        edtNickName.setText(user.getNickname());
//        if (user.getSex().equals("0")) {
//            edtSex.setText("男");
//        } else if (user.getSex().equals("1")) {
//            edtSex.setText("女");
//        }
//        edtPersonSign.setText(user.getSpeech());
        super.onResume();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.ivUserHead:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isOpen = imm.isActive();// isOpen若返回true，则表示输入法打开
                if (slidingView.isOpened()) {
                    slidingView.animateClose();
                } else {
                    slidingView.animateOpen();
                }
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.btnSure:
                user = WangYuApplication.getUser(mContext);
                if (user == null) {
                    showToast("请先登录");
                    intent.setClass(mContext, LoginActivity.class);
                    startActivity(intent);
                    return;
                }
                uploadUserFile();
                break;
            case R.id.ivBack:
                onBackPressed();
            case R.id.iv_back_album:
                vPager.setCurrentItem(1);
                vPager.findFocus();
                break;
            case R.id.iv_back_head:
                vPager.setCurrentItem(0);
                break;
            case R.id.layoutTop:
                slidingView.close();
                break;
            case R.id.edtSex:
                int[] location = new int[2];
                v.getLocationOnScreen(location);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
                display.getMetrics(displayMetrics);
                WindowManager.LayoutParams params = sexDialog.getWindow().getAttributes();
                params.gravity = Gravity.BOTTOM;
                params.y = display.getHeight() - location[1];
                sexDialog.getWindow().setAttributes(params);
                sexDialog.setCanceledOnTouchOutside(true); // 点击dialog区域之外的地方，dialog消失
                sexDialog.setDialogOnclickInterface(new IDialogOnclickInterface() {
                    @Override
                    public void rightOnclick() {
                        edtSex.setText("女");
                        sexDialog.dismiss();
                    }

                    @Override
                    public void leftOnclick() {
                        edtSex.setText("男");
                        sexDialog.dismiss();
                    }
                });
                sexDialog.show();
                break;
            case R.id.rlCurrentCity:
                intent = new Intent();
                intent.setClass(this, LocationCityActivity.class);
                intent.putExtra("fromEditData", true);
                startActivityForResult(intent, 4);
                break;
            case R.id.rlPassword:
                intent = new Intent();
                if (user.getIsPasswordNull() == 1) {//密码是否为空1是0否
                    intent.setClass(this, SetThePasswordActivity.class);
                    startActivityForResult(intent, 5);
                    isSettingPassword = true;
                } else {
                    intent.setClass(this, ImputOldPasswordActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }


    public Handler handler = new Handler() {

        private JSONObject objectStr;
        private int code;
        private String result;

        @Override
        public void handleMessage(Message msg) {
            hideLoading();
            switch (msg.what) {
                case SCAN_OK:
                    albumList.clear();
                    albumList.addAll(subGroupOfImage(mGruopMap));
                    albumAdapter.setData(albumList);
                    albumAdapter.notifyDataSetChanged();
                    vPager.setCurrentItem(1);
                    break;
                case SHOW_HEAD:
                    if (isWitch) {
                        AsyncImage.loadPhoto(mContext, photoFile, ivUserHead);
                    } else {
                        AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + serverPhoto, ivUserHead);
                    }
                    break;
                case UPLOAD_SUCCESS:
                    Gson gs = new Gson();
                    String objStr = msg.obj.toString();
                    try {
                        JSONObject returnInfo = new JSONObject(objStr);
                        if (returnInfo.has("object")) {
                            objectStr = returnInfo.getJSONObject("object");
                            User userNew = null;
                            if (!TextUtils.isEmpty(objectStr.toString())) {
                                userNew = gs.fromJson(objectStr.toString(), User.class);
                                userNew.setToken(user.getToken());
                                if (userNew != null) {
                                    WangYuApplication.setUser(userNew);
                                    PreferencesUtil.setUser(mContext, new Gson().toJson(userNew));
                                    AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + user.getIconMedia(),
                                            ivUserHead);
                                    edtNickName.setText(user.getNickname());
                                    finish();
                                }
                            }
                        } else {
                            code = returnInfo.getInt("code");
                            result = returnInfo.getString("result");
                            if (code == -1) {
                                showToast(result + "请重新登录！");
                            } else {
                                showToast(result);
                            }
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case UPLOAD_FAILURE:
                    showToast(result);
                    break;
            }
            super.handleMessage(msg);
        }
    };

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
                ContentResolver mContentResolver = EditDataActivity.this.getContentResolver();
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

    public String imagenames = "";

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.head_Gride:
                if (position == 0) {
                    // 拍照
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // 获得名字
                    imagenames = UUID.randomUUID() + ".jpg";
                    photoFile = new File(Environment.getExternalStorageDirectory()
                            + "/" + imagenames);
                    // 下面这句指定调用相机拍照后的照片存储的路径
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(intent, 1);
                } else if (position == 1) {
                    // 相册
                    // Intent album = new Intent(Intent.ACTION_GET_CONTENT);
                    // album.setType("image/*");
                    // startActivityForResult(album, 2);
                    getImages();
                } else {
                    serverPhoto = myHeads.get(position);
                    isWitch = false;
                    handler.sendEmptyMessage(SHOW_HEAD);
                }
                break;
            case R.id.album_Gride:
                photoList.clear();
                photoList.addAll(mGruopMap.get(albumList.get(position).getFolderName()));
                photoAdapter.notifyDataSetChanged();
                vPager.setCurrentItem(2);
                break;
            case R.id.photo_Gride:

                photoFile = new File(photoList
                        .get(position));
                String name = photoFile.getPath().substring(
                        photoFile.getPath().lastIndexOf("/"));
                imagenames = System.currentTimeMillis()
                        + name.substring(name.indexOf("."));
                startPhotoZoom(Uri.fromFile(photoFile));
                slidingView.animateClose();

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.e(TAG, "---code---" + resultCode + "__" + requestCode);


        if (requestCode == 1) {
            // imagenames = UUID.randomUUID() + ".jpg";
            // photoFile = new File(Environment.getExternalStorageDirectory() +
            // "/" + imagenames);
            if (resultCode == RESULT_OK) {
                startPhotoZoom(Uri.fromFile(photoFile));
            }

        } else if (requestCode == 3) {
            try {
                setPicToView(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(SHOW_HEAD);

        } else if (requestCode == 4 && resultCode == RESULT_OK) {
            city = (City) data.getSerializableExtra("city");
            tvCurrentCity.setText(city.getName());
            showToast(city.getName());
        } else if (requestCode == 5 && resultCode == RESULT_OK && isSettingPassword) {
            tvPassword.setText("修改");
        } else {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 剪裁图片的方法
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        /*Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image*//*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 720);
        intent.putExtra("outputY", 720);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);*/

        // TODO Auto-generated method stub
        // takePhoto();
        // 图片名称 时间命名
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        filename = format.format(date);
        // 创建File对象用于存储拍照的图片 SD卡根目录
        // File outputImage = new
        // File(Environment.getExternalStorageDirectory(),"test.jpg");
        // 存储至DCIM文件夹
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        outputImage = new File(path, filename + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将File对象转换为Uri并启动照相程序
        imageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("output", imageUri);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 340);// 输出图片大小
        intent.putExtra("outputY", 340);
        startActivityForResult(intent, 3);
    }

    /**
     * 保存剪裁之后的图片
     *
     * @param picdata
     * @throws IOException
     */
    private void setPicToView(Intent picdata) throws IOException {
        /*Bundle extras = picdata.getExtras();
        if (extras != null) {
            photo = extras.getParcelable("data");
            // 压缩
            photo = imageZoom(photo);
            photoFile = new File(getCacheDir(), imagenames);
            // photoFilePath = getCacheDir() + "/" + imagenames

            savePic(photo, photoFile.getPath());
            isWitch = true;
            handler.sendEmptyMessage(SHOW_HEAD);
        }*/
        photo = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imageUri);
        photo = imageZoom(photo);
        photoFile = new File(getCacheDir(), imagenames);
        // photoFilePath = getCacheDir() + "/" + imagenames

        savePic(photo, photoFile.getPath());
        isWitch = true;
        handler.sendEmptyMessage(SHOW_HEAD);
    }

    public Bitmap imageZoom(Bitmap bitMap) {
        // 图片允许最大空间 ：单位：KB
        double maxSize = 200.00;
        // 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 70, baos);// 70 -- 压缩率 ；
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

    public static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 70, fos);
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

    public void uploadFile(final Map<String, String> params) {

    }

    /**
     * 上传图片到服务器
     *
     * @param imageFile 包含路径
     */
    public void uploadUserFile() {
        showLoading();
        try {
            final Map<String, String> params = new HashMap<>();
            params.put("userId", user.getId());
            params.put("token", user.getToken());
            params.put("nickname", edtNickName.getText().toString().trim());
            params.put("userhead", serverPhoto);

            if (!(edtPersonSign.getText().toString().equals(user.getSpeech()))) {
                String dddd = edtPersonSign.getText().toString();
                params.put("speech", dddd);
            }

            if (city != null) {
                if (!(city.getAreaCode().equals(user.getCityCode()))) {
                    params.put("areaCode", city.getAreaCode());
                }
            }

            if (edtSex.getText().toString().equals("男")) {
                if (!(edtSex.getText().toString().equals(user.getSex()))) {
                    params.put("sex", "0");
                }
            } else {
                if (!(edtSex.getText().toString().equals(user.getSex()))) {
                    params.put("sex", "1");
                }
            }
            final Map<String, String> fileParams = new HashMap<>();

            if (isWitch) {
                fileParams.put("avatar", photoFile.getAbsolutePath());
            }


            //sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.UPLOAD_HEAD, params, HttpConstant.UPLOAD_HEAD);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileUploadUtil.uploadImageFile(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.UPLOAD_HEAD, params, fileParams, mHandle);
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FileUploadUtil.SUCCESS:
                    hideLoading();
                    try {
                        Bundle resultData = msg.getData();
                        String result = resultData.getString("result");
                        LogUtil.e("result", result);
                        JSONObject object = new JSONObject(result);
                        JSONObject objectStr = null;

                        if (object.has("object")) {
                            objectStr = object.getJSONObject("object");
                            User userNew = null;
                            if (!TextUtils.isEmpty(objectStr.toString())) {
                                userNew = new Gson().fromJson(objectStr.toString(), User.class);
                                userNew.setToken(user.getToken());
                                if (userNew != null) {
                                    WangYuApplication.setUser(userNew);
                                    PreferencesUtil.setUser(mContext, new Gson().toJson(userNew));
                                    AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + user.getIconMedia(),
                                            ivUserHead);
                                    edtNickName.setText(user.getNickname());
                                    BroadcastController.sendUserChangeBroadcase(mContext);
                                    finish();
                                }
                            }
                        } else {
                            int code = object.getInt("code");
                            result = object.getString("result");
                            if (code == -1) {
                                showToast(result + "请重新登录！");
                            } else {
                                showToast(result);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case FileUploadUtil.FAILED:
                    hideLoading();
                    showToast(R.string.uploadfail);
                    break;
            }
        }
    };
}
