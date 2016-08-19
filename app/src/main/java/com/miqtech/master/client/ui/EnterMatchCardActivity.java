package com.miqtech.master.client.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.ActivityCard;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 参赛卡
 */
public class EnterMatchCardActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.edtName)
    EditText edtName;
    @Bind(R.id.edtIDcard)
    EditText edtIDcard;
    @Bind(R.id.edtPhoneNum)
    EditText edtPhoneNum;
    @Bind(R.id.edtQQ)
    EditText edtQQ;
    @Bind(R.id.btnSave)
    Button btnSave;

    private Context context;
    private ActivityCard activityCard;
    private Dialog mDialog;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_entermatchcard);
        ButterKnife.bind(this);
        context = this;
        initView();
        initData();
    }

    @Override
    protected void initData() {
        super.initData();
        loadActivityCard();
    }

    @Override
    protected void initView() {
        super.initView();
//        activityCard = getIntent().getParcelableExtra("activityCard");
        activityCard = (ActivityCard) getIntent().getSerializableExtra("activityCard");
        if (activityCard == null) {
            setLeftIncludeTitle("创建参赛卡");
        } else {
            setLeftIncludeTitle("编辑参赛卡");
            setRightTextView("删除");
            getRightTextview().setOnClickListener(this);
        }
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    //获取参赛卡
    private void loadActivityCard() {
        if (activityCard != null) {
            updateCardView();
        }
    }

    //更新参赛卡VIEW
    private void updateCardView() {
        if (activityCard != null) {
            if (!TextUtils.isEmpty(activityCard.getIdCard())) {
                edtIDcard.setText(activityCard.getIdCard());
            }
            if (!TextUtils.isEmpty(activityCard.getQq())) {
                edtQQ.setText(activityCard.getQq());
            }
            if (!TextUtils.isEmpty(activityCard.getRealName())) {
                edtName.setText(activityCard.getRealName());
            }
            if (!TextUtils.isEmpty(activityCard.getTelephone())) {
                edtPhoneNum.setText(activityCard.getTelephone());
            }
        }
    }

    //上传用户参赛卡
    public void uploadActivityCard() {
        String IDcard = edtIDcard.getText().toString();
        String telephone = edtPhoneNum.getText().toString();
        String realName = edtName.getText().toString();
        String qq = edtQQ.getText().toString();
        HashMap<String, String> params = new HashMap<String, String>();
        User user = WangYuApplication.getUser(context);
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
            if (!TextUtils.isEmpty(realName)) {
                params.put("realname", Utils.replaceBlank(realName));
            } else {
                showToast("真实姓名不能为空");
                return;
            }
            if (!TextUtils.isEmpty(IDcard) && IDcard.matches(Constant.ID_CARD_FORMAT)) {
                params.put("idcard", IDcard);
            } else if (TextUtils.isEmpty(IDcard)) {
                showToast("身份证不能为空");
                return;
            } else {
                showToast("身份证输入有误");
                return;
            }
            if (!TextUtils.isEmpty(telephone) && telephone.matches(Constant.PHONE_FORMAT)) {
                params.put("telephone", telephone);
            } else if (TextUtils.isEmpty(telephone)) {
                showToast("手机号不能为空");
                return;
            } else {
                showToast("手机号输入有误");
                return;
            }
            if (!TextUtils.isEmpty(qq) && qq.length() > 4) {
                params.put("qq", qq);
            } else if (TextUtils.isEmpty(qq)) {
                showToast("QQ号不能为空");
                return;
            } else {
                showToast("QQ号输入有误");
                return;
            }
            if (activityCard != null) {
                params.put("cardId", activityCard.getId() + "");
            }
            showLoading();
            LogUtil.i("parmas", params.toString());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ACTIVITY_CARD_SAVE, params, HttpConstant.ACTIVITY_CARD_SAVE);
        }

    }

    private void createDialog() {
        if (mDialog == null) {
            mDialog = new Dialog(context, R.style.register_style);
            mDialog.setContentView(R.layout.dialog_register_marked_words);
            TextView title = (TextView) mDialog.findViewById(R.id.dialog_title_register);
            TextView yes = (TextView) mDialog.findViewById(R.id.dialog_register_yes_pact);
            TextView no = (TextView) mDialog.findViewById(R.id.dialog_register_no_pact);
            View line = mDialog.findViewById(R.id.dialog_line_no_pact);
            yes.setText("保存");
            no.setText("取消");
            line.setVisibility(View.VISIBLE);
            no.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            title.setText("是否保存此次修改");
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    uploadActivityCard();
                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    finish();
                }
            });
        }
        mDialog.show();
    }

    //输入内容是否变化
    private boolean activityCardInfoChanged() {
        ActivityCard paramActivityCard = (ActivityCard) getIntent().getSerializableExtra("activityCard");
        String phoneNum = Utils.replaceBlank(edtPhoneNum.getText().toString());
        String name = Utils.replaceBlank(edtName.getText().toString());
        String qq = Utils.replaceBlank(edtQQ.getText().toString());
        String id = Utils.replaceBlank(edtIDcard.getText().toString());
        if (paramActivityCard == null) {
            if (TextUtils.isEmpty(phoneNum) && TextUtils.isEmpty(name) && TextUtils.isEmpty(qq) && TextUtils.isEmpty(id)) {
                return false;
            } else {
                return true;
            }
        } else {
            String paramId = paramActivityCard.getIdCard();
            String paramQQ = paramActivityCard.getQq();
            String paramPhoneNum = paramActivityCard.getTelephone();
            String paramName = paramActivityCard.getRealName();
            if (phoneNum.equals(paramPhoneNum) && name.equals(paramName) && qq.equals(paramQQ) && id.equals(paramId)) {
                return false;
            } else {
                return true;
            }

        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        if (object != null && object.has("object")) {
            try {
                if (method.equals(HttpConstant.ACTIVITY_CARD)) {
                    String strObject = object.getString("object");
                    activityCard = GsonUtil.getBean(strObject, ActivityCard.class);
                    updateCardView();
                } else if (method.equals(HttpConstant.ACTIVITY_CARD_SAVE)) {
                    String strObject = object.getString("object");
                    activityCard = GsonUtil.getBean(strObject, ActivityCard.class);
                    Intent intent = new Intent();
                    intent.putExtra("activityCard", activityCard);
                    setResult(RESULT_OK, intent);
                    if (getIntent().getParcelableExtra("activityCard") == null) {
                        showToast("新增参赛卡成功");
                    } else {
                        showToast("修改参赛卡成功");
                    }
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (method.equals(HttpConstant.DELECT_CARD)) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
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
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        showToast(errMsg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                uploadActivityCard();
                break;
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.tvRightHandle:
                isDelectCard();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (activityCardInfoChanged()) {
            createDialog();
            return;
        }
        super.onBackPressed();
    }


    /**
     * 删除参赛卡
     */
    private void delectCard() {
        User user = WangYuApplication.getUser(context);
        if (user != null) {
            showLoading();
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("id", activityCard.getId() + "");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DELECT_CARD, map, HttpConstant.DELECT_CARD);
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        }
    }


    /**
     * 弹框提示是否删除参赛卡
     */
    private void isDelectCard() {
        final Dialog mDialog = new Dialog(context, R.style.register_style);
        mDialog.setContentView(R.layout.dialog_register_marked_words);
        TextView title_tv = (TextView) mDialog.findViewById(R.id.dialog_title_register);
        TextView context_tv = (TextView) mDialog.findViewById(R.id.dialog_content_register);
        TextView ok_bt = (TextView) mDialog.findViewById(R.id.dialog_register_yes_pact);
        TextView no_bt = (TextView) mDialog.findViewById(R.id.dialog_register_no_pact);
        View vv = mDialog.findViewById(R.id.dialog_line_no_pact);
        vv.setVisibility(View.VISIBLE);
        no_bt.setVisibility(View.VISIBLE);

        title_tv.setText("确认删除参赛卡?");
        title_tv.setVisibility(View.VISIBLE);

        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delectCard();
                mDialog.dismiss();
            }
        });

        no_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }
}
