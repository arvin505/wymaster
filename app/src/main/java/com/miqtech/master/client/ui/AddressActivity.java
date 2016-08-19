package com.miqtech.master.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.watcher.Observerable;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiaoyi on 2016/5/11.
 * 填写地址
 */
public class AddressActivity extends BaseActivity implements TextWatcher, View.OnFocusChangeListener {

    @Bind(R.id.etReceiver)
    EditText etReceiver;
    @Bind(R.id.imgCleanReceiver)
    ImageView imgCleanReceiver;
    @Bind(R.id.etPhoneNum)
    EditText etPhoneNum;
    @Bind(R.id.imgCleanPhone)
    ImageView imgCleanPhone;
    @Bind(R.id.etAddress)
    EditText etAddress;
    @Bind(R.id.imgCleanAddress)
    ImageView imgCleanAddress;
    @Bind(R.id.ibLeft)
    ImageButton ibLeft;
    @Bind(R.id.btnSubmit)
    Button btnSubmit;
    @Bind(R.id.tvReceiver)
    TextView tvReceiver;
    @Bind(R.id.rlReceiver)
    RelativeLayout rlREceiver;
    @Bind(R.id.rlPhone)
    RelativeLayout rlPhone;
    @Bind(R.id.rlAddress)
    RelativeLayout rlAddress;
    String goodType = "1";
    String goodId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);
        goodType = getIntent().getStringExtra("goodtype");
//        if (goodType == null || goodType.equals("null")) {
//            goodType = "1";
//        }

        if (TextUtils.isEmpty(goodType)) {
            goodType = 1 + "";
        }
        goodId = getIntent().getStringExtra("goodid");
        initView();
    }

    public void initView() {
        getLeftBtn().setImageResource(R.drawable.back);
        setLeftIncludeTitle(getString(R.string.add_address));
        etAddress.addTextChangedListener(this);
        etReceiver.addTextChangedListener(this);
        etPhoneNum.addTextChangedListener(this);
        etAddress.setOnFocusChangeListener(this);
        etReceiver.setOnFocusChangeListener(this);
        etPhoneNum.setOnFocusChangeListener(this);
        if (goodType.equals("2") || goodType.equals("1")) {
            tvReceiver.setText(R.string.receiver);
            etReceiver.setText(PreferencesUtil.getName());
        } else {
            tvReceiver.setText(R.string.count);
            etReceiver.setText(PreferencesUtil.getCount());
        }
        switch (goodType) {
            case "1":
                rlREceiver.setVisibility(View.VISIBLE);
                rlAddress.setVisibility(View.VISIBLE);
                rlPhone.setVisibility(View.VISIBLE);
                break;
            case "2":
                rlREceiver.setVisibility(View.VISIBLE);
                rlAddress.setVisibility(View.VISIBLE);
                rlPhone.setVisibility(View.VISIBLE);
                break;
            case "3":
                rlREceiver.setVisibility(View.GONE);
                rlAddress.setVisibility(View.GONE);
                rlPhone.setVisibility(View.VISIBLE);
                break;
            case "4":
                rlREceiver.setVisibility(View.VISIBLE);
                rlAddress.setVisibility(View.GONE);
                rlPhone.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        etAddress.setText(PreferencesUtil.getAddress());
        etPhoneNum.setText(PreferencesUtil.getPhone());
    }

    @OnClick({R.id.imgCleanReceiver, R.id.imgCleanPhone, R.id.imgCleanAddress, R.id.ibLeft, R.id.btnSubmit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgCleanReceiver:
                etReceiver.setText("");
                view.setVisibility(View.GONE);
                break;
            case R.id.imgCleanPhone:
                etPhoneNum.setText("");
                view.setVisibility(View.GONE);
                break;
            case R.id.imgCleanAddress:
                etAddress.setText("");
                view.setVisibility(View.GONE);
                break;
            case R.id.ibLeft:
                finish();
                break;
            case R.id.btnSubmit:
                switch (checkNull()) {
                    case -1:
                        submitAddressInfo();
                        break;
                    case 1:
                        showToast(R.string.receiver_null);
                        break;
                    case 2:
                        showToast(R.string.phonenumber_null);
                        break;
                    case 3:
                        if (goodType.equals("2") || goodType.equals("1")) {
                            showToast(R.string.address_null);
                        } else {
                            showToast(R.string.count_null);
                        }
                        break;
                    case 4:
                        showToast(R.string.address_detail_hint);
                        break;
                }
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
        if (etAddress.isFocused() && etAddress.getText().length() > 0) {
            imgCleanAddress.setVisibility(View.VISIBLE);
            imgCleanPhone.setVisibility(View.GONE);
            imgCleanReceiver.setVisibility(View.GONE);
        }
        if (etPhoneNum.isFocused() && etPhoneNum.getText().length() > 0) {
            imgCleanAddress.setVisibility(View.GONE);
            imgCleanPhone.setVisibility(View.VISIBLE);
            imgCleanReceiver.setVisibility(View.GONE);
        }
        if (etReceiver.isFocused() && etReceiver.getText().length() > 0) {
            imgCleanAddress.setVisibility(View.GONE);
            imgCleanPhone.setVisibility(View.GONE);
            imgCleanReceiver.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (etAddress.isFocused()) {
            if (etPhoneNum.getText().length() > 0) {
                imgCleanAddress.setVisibility(View.VISIBLE);
            }
            imgCleanPhone.setVisibility(View.GONE);
            imgCleanReceiver.setVisibility(View.GONE);
        }
        if (etPhoneNum.isFocused()) {
            imgCleanAddress.setVisibility(View.GONE);
            imgCleanReceiver.setVisibility(View.GONE);
            if (etPhoneNum.getText().length() > 0) {
                imgCleanPhone.setVisibility(View.VISIBLE);
            }
        }
        if (etReceiver.isFocused()) {
            imgCleanAddress.setVisibility(View.GONE);
            imgCleanPhone.setVisibility(View.GONE);
            if (etReceiver.getText().length() > 0) {
                imgCleanReceiver.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 提交地址信息
     */
    private void submitAddressInfo() {
        User user = WangYuApplication.getUser(WangYuApplication.getGlobalContext());
        if (user == null) {
            showToast("请登录");
            Intent intent = new Intent();
            intent.setClass(this, LoginActivity.class);
            startActivityForResult(intent, 6);
        } else {
            showLoading();
            Map<String, String> params = new HashMap<>();
            if (rlREceiver.getVisibility() == View.VISIBLE) {
                if (goodType.equals("2") || goodType.equals("1")) {
                    params.put("name", etReceiver.getText().toString());
                    PreferencesUtil.saveName(etReceiver.getText().toString());

                } else {
                    PreferencesUtil.saveCount(etReceiver.getText().toString());
                    params.put("account", etReceiver.getText().toString());
                }
            }
            if (rlPhone.getVisibility() == View.VISIBLE) {
                params.put("telephone", etPhoneNum.getText().toString());
                PreferencesUtil.savePhone(etPhoneNum.getText().toString());
            }
            if (rlAddress.getVisibility() == View.VISIBLE) {
                params.put("address", etAddress.getText().toString());
                PreferencesUtil.saveAddress(etAddress.getText().toString());
            }
            params.put("id", goodId + "");
            params.put("token", user.getToken());
            params.put("userId", user.getId());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.EDIT_MALL_USERINFO, params, HttpConstant.EDIT_MALL_USERINFO);
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        showToast("兑奖信息填写成功");
        Observerable.getInstance().notifyChange(Observerable.ObserverableType.EXCHANGE,goodId);
        setResult(10);
        finish();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
    }

    /**
     * 检查
     */
    private int checkNull() {
        if (rlREceiver.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etReceiver.getText().toString().trim())) {
            return 1;
        }
        if (rlPhone.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etPhoneNum.getText().toString().trim())) {
            return 2;
        }
        if (rlAddress.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etAddress.getText().toString().trim())) {
            return 3;
        }
        if (rlAddress.getVisibility() == View.VISIBLE && etAddress.getText().toString().trim().length() < 5) {
            return 4;
        }

        return -1;
    }
}
