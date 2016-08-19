package com.miqtech.master.client.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.http.FileUploadUtil;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.FileManager;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/12 0012.
 */
public class RecreationMatchAppealActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    @Bind(R.id.edtContent)
    EditText edtContent;
    @Bind(R.id.ivImg1)
    ImageView ivImg1;
    @Bind(R.id.ivImg2)
    ImageView ivImg2;
    @Bind(R.id.ivImg3)
    ImageView ivImg3;
    @Bind(R.id.btnUpload)
    Button btnUpload;
    @Bind(R.id.tvSurplus)
    TextView tvSurplus;
    private String id;

    private final static int MAX_FONTS = 150;
    private AlertDialog photoDialog;
    private AlertDialog selectorDialog;
    private Context context;
    private File filePic;

    private ArrayList<String> pics = new ArrayList<String>();

    private final int REQUEST_ALBUMN = 0;
    private final int REQUEST_CAMERA = 1;
    private final int REQUEST_CUT_PIC = 2;

    private UploadUserPicHandler myHandler = new UploadUserPicHandler();

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_appeal);
        ButterKnife.bind(this);
        context = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        edtContent.addTextChangedListener(this);
        ivImg1.setOnClickListener(this);
        ivImg2.setOnClickListener(this);
        ivImg3.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        setLeftBtnImage(R.drawable.back_bottom_icon);
        setLeftIncludeTitle("提交申诉");
        getLeftBtn().setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        id = getIntent().getStringExtra("id");
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        if (HttpConstant.AMUSE_APPEAL_V2_COMMIT.equals(method)) {
            setResult(9);
            finish();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        LogUtil.e("error", object.toString());
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
    }

    private void createSureDialog() {
        if (selectorDialog == null) {
            selectorDialog = new AlertDialog.Builder(context).create();
            selectorDialog.show();
            Window window = selectorDialog.getWindow();
            WindowManager.LayoutParams lp =
                    selectorDialog.getWindow().getAttributes();
            window.setContentView(R.layout.layout_selectoralert_dialog);
            lp.width = (int) (WangYuApplication.WIDTH - getResources().getDimension(R.dimen.dialog_margin) * 2);
            lp.height = lp.WRAP_CONTENT;
            window.setAttributes(lp);
            selectorDialog.setCanceledOnTouchOutside(true);
        }
        TextView tvContent = (TextView) selectorDialog.findViewById(R.id.tvContent);
        TextView tvCancel = (TextView) selectorDialog.findViewById(R.id.tvCancel);
        TextView tvSure = (TextView) selectorDialog.findViewById(R.id.tvSure);
        tvContent.setText("确认提交？");
        tvCancel.setOnClickListener(this);
        tvSure.setOnClickListener(this);
        selectorDialog.show();
    }


    private void uploadAppealCommit() {
        showLoading();
        HashMap<String, String> params = new HashMap<>();
        params.put("activityId", id);
        params.put("userId", WangYuApplication.getUser(context).getId());
        params.put("token", WangYuApplication.getUser(context).getToken());
        params.put("describes", Utils.replaceBlank(edtContent.getText().toString()));
        StringBuilder sb = new StringBuilder();
        if (pics.size() > 0) {
            for (int i = 0; i < pics.size(); i++) {
                sb.append(pics.get(i)).append(",");
            }
            String imgs = sb.toString().substring(0, sb.toString().length() - 1);
            params.put("img", imgs);
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_APPEAL_V2_COMMIT, params, HttpConstant.AMUSE_APPEAL_V2_COMMIT);
        selectorDialog.dismiss();
        LogUtil.e("error", "params : " + params.toString());
        LogUtil.e("error" , "url : " + HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_APPEAL_V2_COMMIT );
    }

    private void screenPhoto() {
        filePic = FileManager.getFile(context, FileManager.USER_FOLDER, System.currentTimeMillis() + ".jpg");
        Intent getImage = new Intent(Intent.ACTION_PICK, null);
        getImage.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(getImage, REQUEST_ALBUMN);
    }

    private void openCarema() {
        filePic = FileManager.getFile(context, FileManager.USER_FOLDER, System.currentTimeMillis() + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 下面这句指定调用相机拍照后的照片存储的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(filePic));
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    /**
     * 裁剪图片方法实现
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
        intent.putExtra("outputX", 1080);
        intent.putExtra("outputY", 1080);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CUT_PIC);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            if (bitmap != null) {
                OutputStream baos = null;
                try {
                    baos = new FileOutputStream(filePic);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 把数据写入文件
                    showLoading();
                    final Map<String, String> fileMap = new HashMap<>();
                    fileMap.put("bgImg", filePic.getAbsolutePath());
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
            } else {
                //              Toast.makeText(mContext, getString(R.string.user_pic_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setPicWithoutCut(Intent data) {

        Uri uri = data.getData();
        Bitmap image = null;
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
                baos = new FileOutputStream(filePic);
                image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 把数据写入文件
                showLoading();
                final Map<String, String> fileMap = new HashMap<>();
                fileMap.put("bgImg", filePic.getAbsolutePath());
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

    private class UploadUserPicHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String imgResult = data.getString("result");
            try {
                JSONObject jsonResult = new JSONObject(imgResult);
                if (jsonResult.getInt("code") == 0) {
                    photoDialog.dismiss();
                    showToast("上传成功");
                    String picName = jsonResult.getString("object");
                    setPicsView(picName);
                } else {
                    photoDialog.dismiss();
                    showToast("上传失败");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setPicsView(String picName) {
        hideLoading();
        pics.add(picName);
        if (pics.size() == 1) {
            ivImg1.setTag(picName);
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + picName, ivImg1);
        } else if (pics.size() == 2) {
            ivImg2.setTag(picName);
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + picName, ivImg2);
        } else if (pics.size() == 3) {
            ivImg3.setTag(picName);
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + picName, ivImg3);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ALBUMN:
                    //startPhotoZoom(data.getData());
                    setPicWithoutCut(data);
                    break;
                case REQUEST_CAMERA:
                    //startPhotoZoom(Uri.fromFile(filePic));
                    data = new Intent();
                    data.setData(Uri.fromFile(filePic));
                    setPicWithoutCut(data);
                    break;
                case REQUEST_CUT_PIC:
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivImg1:
                if (pics.size() < 3) {
                    createUploadPicDialog();
                }
                break;
            case R.id.ivImg2:
                if (pics.size() < 3) {
                    createUploadPicDialog();
                }
                break;
            case R.id.ivImg3:
                if (pics.size() < 3) {
                    createUploadPicDialog();
                }
                break;
            case R.id.tvDialogPhoto:
                screenPhoto();
                break;
            case R.id.tvDialogCarema:
                openCarema();
                break;
            case R.id.btnUpload:
                if (Utils.replaceBlank(edtContent.getText().toString()).length() > 0) {
                    createSureDialog();
                } else {
                    showToast("请输入要求输入的参赛信息");
                }
                break;
            case R.id.tvSure:
                uploadAppealCommit();
                break;
            case R.id.tvCancel:
                selectorDialog.dismiss();
                break;
            case R.id.ibLeft:
                onBackPressed();
                break;
        }
    }

    private void createUploadPicDialog() {
        if (photoDialog == null) {
            photoDialog = new AlertDialog.Builder(this).create();
            photoDialog.show();
            Window window = photoDialog.getWindow();
            window.setContentView(R.layout.layout_uploadpic_dialog);

            photoDialog.setCanceledOnTouchOutside(true);
            TextView tvDialogTitle = (TextView) photoDialog.findViewById(R.id.tvDialogTitle);
            TextView tvDialogCarema = (TextView) photoDialog.findViewById(R.id.tvDialogCarema);
            TextView tvDialogPhoto = (TextView) photoDialog.findViewById(R.id.tvDialogPhoto);
            tvDialogCarema.setOnClickListener(this);
            tvDialogPhoto.setOnClickListener(this);
            tvDialogTitle.setText("上传图片");
        }
        photoDialog.show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int surplusNum = MAX_FONTS - Utils.replaceBlank(edtContent.getText().toString()).length();
        tvSurplus.setText("剩余" + surplusNum + "字");
    }
}
