package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.FileUploadUtil;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 提交成绩
 * Created by zhaosentao on 2016/8/4.
 */
public class SubmitGradesActivity extends BaseActivity {

    @Bind(R.id.llBack)
    LinearLayout llBack;
    @Bind(R.id.submitGradesTv)
    TextView tvSubmit;
    @Bind(R.id.etRemark)
    EditText etRemark;
    @Bind(R.id.tvWords)
    TextView tvWords;
    @Bind(R.id.ivSubmitGrades)
    ImageView ivSubmitGrades;
    @Bind(R.id.ivSubmitCamera)
    ImageView ivSubmitCamera;


    private Context context;

    private User user;
    private static final int REQUEST_CROP_PHOTO = 3;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private Uri imageUri; // 图片路径
    private String filename; // 图片名称
    private File outputImage;
    private String imgName = "";//上传图片成功后服务器返回的图片名称
    private String remark;//评论
    private final int numberWords = 150;//限制输入的字数
    private int words;
    private String character;
    private String enterCharacter;
    private String hintWord;
    private int fromType; //0 悬赏令 1 直播间 2视频播放

    private String bountyId;

    private boolean isCamera = false;
    private boolean isBannedToPost = false;//是否禁言


    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_submit_grades);
        ButterKnife.bind(this);
        context = this;
        bountyId = getIntent().getStringExtra("bountyId");
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        fromType = getIntent().getIntExtra("fromType", 0);
        character = getResources().getString(R.string.character);
        enterCharacter = getResources().getString(R.string.also_you_can_enter);
        hintWord = getResources().getString(R.string.share_the_mood);
        monitorEditView();
    }

    @OnClick({R.id.llBack, R.id.submitGradesTv, R.id.ivSubmitGrades, R.id.ivSubmitCamera})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llBack://返回
                onBackPressed();
                break;
            case R.id.submitGradesTv://提交
                if (isBannedToPost) {
                    showToast(getResources().getString(R.string.banned_to_post));
                    return;
                }
                if (fromType == 0) {
                    submitGrade();
                } else if (fromType == 1) {
                    if (!TextUtils.isEmpty(imgName) || !TextUtils.isEmpty(Utils.replaceBlank(remark))) {
                        Intent intent = new Intent();
                        intent.putExtra("imgName", imgName);
                        intent.putExtra("remark", Utils.replaceBlank(remark));
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        showToast("请选择图片或编辑文字哦~");
                    }
                }
                break;
            case R.id.ivSubmitGrades://上传图片
                isCamera = false;
                takeAlbum();
                break;
            case R.id.ivSubmitCamera:
                isCamera = true;
                takePhoto();
                break;
        }
    }

    /**
     * 提交是否收藏
     */
    private void submitGrade() {
        if (TextUtils.isEmpty(imgName)) {
            showToast(getResources().getString(R.string.please_upload_img));
            return;
        }
        user = WangYuApplication.getUser(context);
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("bountyId", bountyId);
            if (!TextUtils.isEmpty(remark)) {
                map.put("remark", Utils.replaceBlank(remark));
            }
            map.put("imgSrc", imgName);
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.BOUNTY_UPGRADE, map, HttpConstant.BOUNTY_UPGRADE);
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        if (method.equals(HttpConstant.BOUNTY_UPGRADE)) {
            showToast(getResources().getString(R.string.submit_success));
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        if (method.equals(HttpConstant.BOUNTY_UPGRADE)) {
            showToast(R.string.noNeteork);
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        if (method.equals(HttpConstant.BOUNTY_UPGRADE)) {
            try {
                if (1 == object.getInt("code")) {
                    isBannedToPost = true;
                }
                if (object.has("result")) {
                    showToast(object.getString("result"));
                } else {
                    showToast(getResources().getString(R.string.submit_fail));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void takePhoto() {
//        // 图片名称 时间命名
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//        Date date = new Date(System.currentTimeMillis());
//        filename = format.format(date);
//        // 创建File对象用于存储拍照的图片 SD卡根目录
//        // File outputImage = new
//        // File(Environment.getExternalStorageDirectory(),"test.jpg");
//        // 存储至DCIM文件夹
//        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//        outputImage = new File(path, filename + ".jpg");
//        try {
//            if (outputImage.exists()) {
//                outputImage.delete();
//            }
//            outputImage.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // 将File对象转换为Uri并启动照相程序
//        imageUri = Uri.fromFile(outputImage);
//        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE"); // 照相
////        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 指定图片输出地址
//        startActivityForResult(intent, REQUEST_TAKE_PHOTO); // 启动照相
//        // 拍完照startActivityForResult() 结果返回onActivityResult()函数

        // 拍照
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 获得名字
        imagenames = UUID.randomUUID() + ".jpg";
        photoFile = new File(Environment.getExternalStorageDirectory()
                + "/" + imagenames);
        // 下面这句指定调用相机拍照后的照片存储的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);


    }

    public String imagenames = "";
    public File photoFile = null;


    private void takeAlbum() {
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
        Intent intent = new Intent("android.intent.action.PICK");
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        intent.putExtra("output", imageUri);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 340);// 输出图片大小
        intent.putExtra("outputY", 340);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CROP_PHOTO && resultCode == RESULT_OK) {
            if (data != null) {
                uploadUserPhoto();
            } else {
                reset(2);
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            if (resultCode == RESULT_OK) {
                startPhotoZoom(Uri.fromFile(photoFile));
            }
//            Intent intent = new Intent("com.android.camera.action.CROP"); // 剪裁
//            intent.setDataAndType(imageUri, "image/*");
//            intent.putExtra("scale", true);
//            // 设置宽高比例
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 1);
//            // 设置裁剪图片宽高
//            intent.putExtra("outputX", 340);
//            intent.putExtra("outputY", 340);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//            Toast.makeText(this, "剪裁图片", Toast.LENGTH_SHORT).show();
//            // 广播刷新相册
//            Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            intentBc.setData(imageUri);
//            this.sendBroadcast(intentBc);
//            startActivityForResult(intent, REQUEST_CROP_PHOTO); // 设置裁剪参数显示图片至ImageView
        }
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
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    private void uploadUserPhoto() {
        try {
            final Map<String, String> fileParams = new HashMap<>();
            fileParams.put("pic", outputImage.getAbsolutePath());
            showLoading();
            showToast(getResources().getString(R.string.up_phone));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileUploadUtil.uploadImageFile(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.UPLOAD_PIC, null, fileParams, mHandler);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FileUploadUtil.SUCCESS:
                    hideLoading();
                    try {
                        Bundle resultData = msg.getData();
                        String result = resultData.getString("result");
                        JSONObject object = new JSONObject(result);
                        if (!TextUtils.isEmpty(object.getString("object").toString())) {
                            reset(0);
                            showToast(getResources().getString(R.string.uploadfsuccess));
                            imgName = object.getString("object").toString();
                            if (isCamera) {
                                AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + imgName + "!small", ivSubmitCamera);
                            } else {
                                AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + imgName + "!small", ivSubmitGrades);
                            }
                        } else {
                            reset(1);
                        }
                    } catch (JSONException e) {
                        reset(1);
                        e.printStackTrace();
                    }
                    break;
                case FileUploadUtil.FAILED:
                    hideLoading();
                    reset(1);
                    break;
            }
        }
    };

    /**
     * 监听输入的变化
     */
    private void monitorEditView() {
        TextWatcher textWatcher = new TextWatcher() {
            String temp;
            String myTemp;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s.toString();
                myTemp = Utils.replaceBlank(temp);
                if (!myTemp.equals(temp)) {
                    etRemark.setText(myTemp);
                    etRemark.setSelection(myTemp.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(myTemp)) {
                    etRemark.setHint(hintWord);
                } else {
                    remark = myTemp;
                }
                words = numberWords - myTemp.length();
                tvWords.setText(addconnent(enterCharacter, words + "", character));
            }
        };
        etRemark.addTextChangedListener(textWatcher);
        tvWords.setText(addconnent(enterCharacter, "150", character));
    }

    /**
     * @param cotent1
     * @param content2
     * @param content3
     * @return
     */
    private SpannableStringBuilder addconnent(String cotent1, String content2, String content3) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(cotent1);
        int start = cotent1.length();
        int middle = start + content2.length();
        ssb.append(content2);
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(R.color.colorActionBarSelected));// 设置文本颜色
                // 去掉下划线
                ds.setUnderlineText(false);
            }
        }, start, middle, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ssb.append(content3);
    }

    /**
     * 0图片重置，不需要弹框;1上传图片失败；2获取图片失败,
     *
     * @param type
     */
    private void reset(int type) {
        imgName = "";
        ivSubmitGrades.setImageResource(R.drawable.wanted_dashedframe);
        ivSubmitCamera.setImageResource(R.drawable.wanted_camera);
        if (type == 1) {
            showToast(getResources().getString(R.string.uploadfail));
        } else if (type == 2) {
            showToast(getResources().getString(R.string.get_img_fail));
        }
    }

}
