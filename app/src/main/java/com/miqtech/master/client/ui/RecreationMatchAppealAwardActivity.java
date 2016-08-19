package com.miqtech.master.client.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.AppealCategory;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ToastUtil;
import com.miqtech.master.client.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/1/19.
 */
public class RecreationMatchAppealAwardActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.ll_content)
    LinearLayout llContent;

    @Bind(R.id.et_other_content)
    EditText etOther;

    @Bind(R.id.bt_submit)
    Button btSubmit;

    @Bind(R.id.tvSurplus)
    TextView tvSurplus;

    @Bind(R.id.rl_other)
    RelativeLayout rlOther;


    private int categoryId = -1;
    private ArrayList<AppealCategory> categories;
    private String activityId;
    private final static int MAX_FONTS = 150;

    private AlertDialog selectorDialog;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_matchappeal_award);
        ButterKnife.bind(this);

        categories = getIntent().getParcelableArrayListExtra("category");
        activityId = getIntent().getStringExtra("id");

        LogUtil.e("cate", "cate : " + categories.toString());

        initView();
    }

    @Override
    protected void initView() {
        getLeftBtn().setOnClickListener(this);
        setLeftIncludeTitle("申诉");
        setLeftBtnImage(R.drawable.back);

        btSubmit.setOnClickListener(this);

        initContentView();

        etOther.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int surplusNum = MAX_FONTS - Utils.replaceBlank(etOther.getText().toString()).length();
                tvSurplus.setText("剩余" + surplusNum + "字");
            }
        });
    }

    /**
     * 申诉类型
     */
    private void initContentView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(this, 52));
        for (int i = 0, count = categories.size(); i < count; i++) {
            View view = inflater.inflate(R.layout.layout_appealaward_item, null);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
            ImageView imState = (ImageView) view.findViewById(R.id.im_state);
            View bottomLine = view.findViewById(R.id.line_bottom);

            if (i == categories.size() - 1) {
                bottomLine.setVisibility(View.GONE);
            } else {
                bottomLine.setVisibility(View.VISIBLE);
            }

            tvTitle.setText(categories.get(i).getContent());
            view.setLayoutParams(params);
            final int index = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeState(index);
                    categoryId = categories.get(index).getId();
                    if (categoryId == 6) {
                        /*etOther.setFocusable(true);
                        etOther.setFocusableInTouchMode(true);
                        etOther.requestFocus();
                        openSoftKeyBoard(true);*/
                        rlOther.setVisibility(View.VISIBLE);
                    } else {
                        /*openSoftKeyBoard(false);
                        etOther.setFocusable(false);
                        etOther.setFocusableInTouchMode(false);
                        etOther.clearFocus();
                        etOther.setText("");*/
                        rlOther.setVisibility(View.GONE);
                    }
                }
            });
            llContent.addView(view);
        }
    }

    private void openSoftKeyBoard(boolean open) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            if (open) {
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
            } else {
                try {
                    imm.hideSoftInputFromWindow(getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } else {
            if (!open)
                imm.hideSoftInputFromWindow(getCurrentFocus()
                                .getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void changeState(int postion) {
        for (int i = 0; i < llContent.getChildCount(); i++) {
            TextView tv = (TextView) llContent.getChildAt(i).findViewById(R.id.tv_title);
            ImageView im = (ImageView) llContent.getChildAt(i).findViewById(R.id.im_state);
            if (i == postion) {
                tv.setTextColor(getResources().getColor(R.color.orange));
                im.setImageResource(R.drawable.ic_pay_checked);
            } else {
                tv.setTextColor(getResources().getColor(R.color.textColorBattle));
                im.setImageResource(R.drawable.ic_unchecked);
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.bt_submit:
                if (categoryId == 6) {
                    if (!TextUtils.isEmpty(etOther.getText().toString().trim())) {
                        //submitAppeal();
                        createSureDialog();
                    } else {
                        showToast("请描述您的问题");
                    }
                } else if (categoryId == -1) {
                    //submitAppeal();
                    showToast("请选择申诉的内容");
                } else {
                    createSureDialog();
                }
                break;
            case R.id.tvSure:
                submitAppeal();
                break;
            case R.id.tvCancel:
                selectorDialog.dismiss();
                break;
        }
    }

    private void submitAppeal() {
        showLoading();
        User user = WangYuApplication.getUser(this);
        Map<String, String> params = new HashMap<>();
        params.put("categoryId", categoryId + "");
        params.put("activityId", activityId);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        if (categoryId == 6) {
            params.put("describes", etOther.getText().toString());
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_APPEAL_V2_COMMIT, params, HttpConstant.AMUSE_APPEAL_V2_COMMIT);
    }

    private void createSureDialog() {
        if (selectorDialog == null) {
            selectorDialog = new AlertDialog.Builder(this).create();
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

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        selectorDialog.dismiss();
        if (HttpConstant.AMUSE_APPEAL_V2_COMMIT.equals(method)) {
            ToastUtil.showToast("提交成功", this);
            setResult(9);
            finish();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        selectorDialog.dismiss();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        selectorDialog.dismiss();
    }
}
