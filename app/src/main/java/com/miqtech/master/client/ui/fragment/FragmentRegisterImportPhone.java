package com.miqtech.master.client.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.RegisterActivity;
import com.miqtech.master.client.ui.SubjectActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.ABTextUtil;
import com.miqtech.master.client.utils.AsyncImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/10.
 */
public class FragmentRegisterImportPhone extends BaseFragment implements View.OnClickListener {

    private View mainView;
    private Context context;

    private EditText et_phone;
    private TextView pact_register;
    private Button btGetCode;

    private Dialog dialog;
    private String telephone;
    private String imgCodeStr;
    private ImageView imgCode;
    private ImageView refreImg;
    private EditText et_auth_code;

    private int type = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_register_import_phone, null);
            context = inflater.getContext();
            initView();
        }
    }

    private void initView() {
        et_phone = (EditText) mainView.findViewById(R.id.et_phone_register);
        pact_register = (TextView) mainView.findViewById(R.id.tv_pact_register);
        btGetCode = (Button) mainView.findViewById(R.id.get_identifying_code_register);

        pact_register.setOnClickListener(this);
        btGetCode.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pact_register://协议
                Intent intent = new Intent(context, SubjectActivity.class);
                intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.AGREEMENT);
                startActivity(intent);
                break;
            case R.id.get_identifying_code_register://获取验证码
                getcode();
                //((RegisterActivity) context).SkipNextFragment(1);
                break;
            case R.id.dialog_register_no_pact://不同意协议时的弹框  确定按钮
                dialog.dismiss();
                break;
            case R.id.dialog_register_on_tv://图片验证码弹框  确定
                sendSMSCodeMobile();
                dialog.dismiss();
                break;
            case R.id.dialog_register_off_tv://图片验证码弹框  取消
                dialog.dismiss();
                break;
            case R.id.dialog_code_refre_iv://图片验证码弹框  刷新图片验证
                AsyncImage.loadYZM(context, HttpConstant.SERVICE_HTTP_AREA+HttpConstant.IMAGE_CODE_REGISTER + telephone, imgCode);
                break;
            case R.id.dialog_imageview_code_iv://图片验证码弹框  刷新图片验证
                AsyncImage.loadYZM(context, HttpConstant.SERVICE_HTTP_AREA+HttpConstant.IMAGE_CODE_REGISTER + telephone, imgCode);
                break;
        }
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mainView;
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        try {
            if (method.equals(HttpConstant.SEND_SMS_CODE_MOBILE)) {
                if (0 == object.getInt("code")) {
                    dialog.dismiss();
                    ((RegisterActivity) context).SkipNextFragment(1);
                } else if (5 == object.getInt("code")) {
                    ((RegisterActivity) context).creatDialogForHint(context, "用户被不存在", null);
                } else {
                    if (object.has("result")) {
                        showToast(object.getString("result"));
                    } else {
                        showToast("网络错误");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        showToast(errMsg);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        try {
            if (method.equals(HttpConstant.SEND_SMS_CODE_MOBILE)) {
                if (object.has("result")) {
                    ((RegisterActivity) context).creatDialogForHint(context, "发送失败", object.getString("result"));
                } else {
                    ((RegisterActivity) context).creatDialogForHint(context, "发送失败", object.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getcode() {
        telephone = et_phone.getText().toString().trim();

        if (!ABTextUtil.isMobile(telephone)) {
            showToast(context.getResources().getString(R.string.phone_form_no));
        } else {
            Constant.register_phone = telephone;
            creatImageCodeDialog();
        }
    }

    /**
     * 创建带有图片验证码的弹框
     */
    private void creatImageCodeDialog() {
        dialog = new Dialog(context, R.style.register_style);
        dialog.setContentView(R.layout.dialog_register_pact_img);
        dialog.setCanceledOnTouchOutside(false);

        et_auth_code = (EditText) dialog.findViewById(R.id.dialog_register_et);
        TextView tvYes = (TextView) dialog.findViewById(R.id.dialog_register_on_tv);
        TextView tvNo = (TextView) dialog.findViewById(R.id.dialog_register_off_tv);
        refreImg = (ImageView) dialog.findViewById(R.id.dialog_code_refre_iv);
        imgCode = (ImageView) dialog.findViewById(R.id.dialog_imageview_code_iv);

        tvYes.setOnClickListener(this);
        tvNo.setOnClickListener(this);
        refreImg.setOnClickListener(this);
        imgCode.setOnClickListener(this);
        AsyncImage.loadYZM(context, HttpConstant.SERVICE_HTTP_AREA+HttpConstant.IMAGE_CODE_REGISTER + telephone, imgCode);
        dialog.show();
    }

    private void sendSMSCodeMobile() {

        imgCodeStr = et_auth_code.getText().toString().trim();
        if (imgCodeStr == null || TextUtils.isEmpty(imgCodeStr)) {
            showToast("请输入验证码");
            return;
        }

        if (((RegisterActivity) context).retrievePassword == 2) {
            type = 2;
        }

        Map<String, String> map = new HashMap<>();
        map.put("mobile", telephone);
        map.put("type", type + "");
        map.put("code", imgCodeStr);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SEND_SMS_CODE_MOBILE, map, HttpConstant.SEND_SMS_CODE_MOBILE);
    }

    /**
     * 当不同意协议时的弹框
     */
    private void showNoAgreeDialog() {
        dialog = new Dialog(context, R.style.register_style);
        dialog.setContentView(R.layout.dialog_register_no_agree_pact);
        TextView tv = (TextView) dialog.findViewById(R.id.dialog_register_no_pact);
        tv.setOnClickListener(this);
        dialog.show();
    }
}
