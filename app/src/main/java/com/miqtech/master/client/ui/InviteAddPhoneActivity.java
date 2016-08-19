package com.miqtech.master.client.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.ContactMember;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.PingYinUtil;

public class InviteAddPhoneActivity extends BaseActivity implements OnClickListener {
    private EditText edtPhoneNum;
    private Button btnAdd;
    private LinearLayout llContent;
    private Animation shakingAnimation;
    private Context context;
    private LinearLayout llHasChoose;
    private TextView tvHasChoose;
    private HorizontalScrollView hsvContent;
    private int maxInviteMemberSize;
    private ImageView back;

    protected void init() {
        super.init();
        setContentView(R.layout.activity_invite_addphone);
        context = this;
        initView();
        initData();
    }

    protected void initView() {
        super.initView();
        edtPhoneNum = (EditText) findViewById(R.id.edtPhoneNum);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        llContent = (LinearLayout) findViewById(R.id.llContent);
        llHasChoose = (LinearLayout) findViewById(R.id.llHasChoose);
        tvHasChoose = (TextView) findViewById(R.id.tvHasChoose);
        hsvContent = (HorizontalScrollView) findViewById(R.id.hsvContent);
        shakingAnimation = AnimationUtils.loadAnimation(this, R.anim.shaking);

        btnAdd.setOnClickListener(this);

        //setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("添加手机号");
        //getLeftBtn().setOnClickListener(this);
        back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(this);
        tvHasChoose.setOnClickListener(this);
    }

    protected void initData() {
        super.initData();
        maxInviteMemberSize = getIntent().getIntExtra("maxInviteMemberSize", 0);
        for (int i = 0; i < InviteFriendsActivity.hasCommonMembers.size(); i++) {
            Object commonMember = InviteFriendsActivity.hasCommonMembers.get(i);
            if (commonMember instanceof ContactMember) {
                if (((ContactMember) commonMember).getIsAddPhone() == 1) {
                    addPhoneNumView(((ContactMember) commonMember).getContact_phone(), false);
                }
            }

            addPhoneNumBottomView(commonMember);
        }
        tvHasChoose.setText("已选择" + InviteFriendsActivity.hasCommonMembers.size() + "/" + maxInviteMemberSize + "人");

    }

    private void addPhoneNumView(String phoneNum, boolean needToAdd) {
        View view = View.inflate(context, R.layout.layout_phonenum_item, null);
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        TextView tvPhoneNum = (TextView) view.findViewById(R.id.tvPhoneNum);
        TextView tvDelete = (TextView) view.findViewById(R.id.tvDelete);
        String name = "";
        StringBuffer sb = new StringBuffer(phoneNum);
        try {
            name = PingYinUtil.substring(sb.reverse().toString(), 4, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuffer sb1 = new StringBuffer(name);
        name = sb1.reverse().toString();
        tvName.setText(name);
        tvPhoneNum.setText(phoneNum);
        tvDelete.setOnClickListener(new DeletePhoneNumListener(phoneNum));

        edtPhoneNum.setText("");

        ContactMember contactMember = new ContactMember();
        contactMember.setIsAddPhone(1);
        contactMember.setContact_phone(phoneNum);
        contactMember.setContact_name(name);
        view.setTag(contactMember);

        if (needToAdd) {
            InviteFriendsActivity.hasCommonMembers.add(contactMember);
        }

        llContent.addView(view);
        tvHasChoose.setText("已选择" + InviteFriendsActivity.hasCommonMembers.size() + "/" + maxInviteMemberSize + "人");

    }

    private void checkPhoneNum() {
        String phoneNum = edtPhoneNum.getText().toString();
        boolean result = phoneNum.matches(Constant.PHONE_FORMAT);
        if (result) {
            if (InviteFriendsActivity.hasCommonMembers.size() < maxInviteMemberSize) {
                if (!checkHasPhoneNum(phoneNum)) {
                    addPhoneNumView(phoneNum, true);
                    Object commonMember = InviteFriendsActivity.hasCommonMembers.get(InviteFriendsActivity.hasCommonMembers
                            .size() - 1);
                    addPhoneNumBottomView(commonMember);
                } else {
                    showToast("手机号已存在，请勿重复添加");
                }
            } else {
                showToast("最多只能加" + maxInviteMemberSize + "人");
            }

        } else {
            edtPhoneNum.startAnimation(shakingAnimation);
            showToast("手机号码格式不正确");
        }
    }

    private boolean checkHasPhoneNum(String phoneNum) {
        for (int i = 0; i < llContent.getChildCount(); i++) {
            View view = llContent.getChildAt(i);
            ContactMember member = (ContactMember) view.getTag();
            if (phoneNum.equals(member.getContact_phone())) {
                return true;
            }
        }
        return false;
    }

    private void addPhoneNumBottomView(Object commonMember) {
        String name = "";
        if (commonMember instanceof ContactMember) {
            name = ((ContactMember) commonMember).getContact_name();
        } else if (commonMember instanceof User) {
            name = ((User) commonMember).getNickname();
        }
        StringBuffer sb = new StringBuffer(name);
        try {
            name = PingYinUtil.substring(sb.reverse().toString(), 4, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuffer sb1 = new StringBuffer(name);
        name = sb1.reverse().toString();

        View view = View.inflate(context, R.layout.layout_common_member_img_item, null);
        TextView tvName = (TextView) view.findViewById(R.id.tvMemberName);
        tvName.setText(name);
        llHasChoose.addView(view);
        view.setTag(commonMember);
        view.setOnClickListener(new RemoveMemberOnClickListener());

        hsvContent.fullScroll(ScrollView.FOCUS_RIGHT);
    }

    private void removePhoneNumBottomView(ContactMember contactMember) {
        for (int i = 0; i < llHasChoose.getChildCount(); i++) {
            Object commonMember = llHasChoose.getChildAt(i).getTag();
            if (commonMember instanceof ContactMember) {
                if (((ContactMember) commonMember).getIsAddPhone() == 1) {
                    if (((ContactMember) commonMember).getContact_name().equals(contactMember.getContact_name())
                            && ((ContactMember) commonMember).getContact_phone().equals(
                            contactMember.getContact_phone())) {
                        llHasChoose.removeViewAt(i);
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < InviteFriendsActivity.hasCommonMembers.size(); i++) {
            Object commonMember = InviteFriendsActivity.hasCommonMembers.get(i);
            if (commonMember instanceof ContactMember) {
                if (((ContactMember) commonMember).getIsAddPhone() == 1) {
                    if (((ContactMember) commonMember).getContact_name().equals(contactMember.getContact_name())
                            && ((ContactMember) commonMember).getContact_phone().equals(
                            contactMember.getContact_phone())) {
                        InviteFriendsActivity.hasCommonMembers.remove(i);
                        break;
                    }
                }
            }
        }
        tvHasChoose.setText("已选择" + InviteFriendsActivity.hasCommonMembers.size() + "/" + maxInviteMemberSize + "人");
    }

    private class DeletePhoneNumListener implements OnClickListener {

        String phoneNum;

        private DeletePhoneNumListener(String phoneNum) {
            this.phoneNum = phoneNum;
        }

        @Override
        public void onClick(View v) {

            llContent.removeView((View) v.getParent().getParent());
            removePhoneNumBottomView((ContactMember) ((View) v.getParent().getParent()).getTag());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                checkPhoneNum();
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvHasChoose:
                setResult(RESULT_OK);
                finish();
                break;

            default:
                break;
        }
    }

    private void uploadInviteMembers() {
        ArrayList<String> result = getInvocationIdsAndPhones();

        String teamId = getIntent().getStringExtra("teamId");
        showLoading();
        User user = WangYuApplication.getUser(context);
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("teamId", teamId);
        params.put("invocationIds", result.get(0));
        params.put("phones", result.get(1));
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INVITE_TEAMMATE, params, HttpConstant.INVITE_TEAMMATE);
    }

    private ArrayList<String> getInvocationIdsAndPhones() {
        String invocationIds = "";
        String phones = "";
        for (int i = 0; i < InviteFriendsActivity.hasCommonMembers.size(); i++) {
            Object commonMember = InviteFriendsActivity.hasCommonMembers.get(i);
            if (commonMember instanceof User) {
                invocationIds += ((User) commonMember).getId() + ",";
            } else if (commonMember instanceof ContactMember) {
                phones += ((ContactMember) commonMember).getContact_phone() + ",";

            }
        }

        if (phones.length() > 0) {
            phones = (String) phones.subSequence(0, phones.length() - 1);
        }
        if (invocationIds.length() > 0) {
            invocationIds = (String) invocationIds.subSequence(0, invocationIds.length() - 1);
        }

        ArrayList<String> result = new ArrayList<String>();
        result.add(invocationIds);
        result.add(phones);
        return result;
    }

    private class RemoveMemberOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            Object selectMember = v.getTag();
            int childCount = llHasChoose.getChildCount();
            int index = -1;
            for (int i = 0; i < childCount; i++) {
                Object member = llHasChoose.getChildAt(i).getTag();
                if (member instanceof User) {
                    if (selectMember instanceof User) {
                        if (((User) member).getId().equals(((User) selectMember).getId())) {
                            index = i;
                            break;
                        }
                    }
                } else if (member instanceof ContactMember) {
                    if (selectMember instanceof ContactMember) {
                        if (((ContactMember) member).getContact_name().equals(
                                ((ContactMember) selectMember).getContact_name())) {
                            index = i;
                            break;
                        }
                    }

                }
            }
            if (index != -1) {
                llHasChoose.removeViewAt(index);
                InviteFriendsActivity.hasCommonMembers.remove(selectMember);
                tvHasChoose.setText("已选择" + InviteFriendsActivity.hasCommonMembers.size() + "/" + maxInviteMemberSize
                        + "人");
                int removeIndex = -1;
                if (selectMember instanceof ContactMember) {
                    for (int i = 0; i < llContent.getChildCount(); i++) {
                        View view = llContent.getChildAt(i);
                        ContactMember member = (ContactMember) view.getTag();
                        if (((ContactMember) selectMember).getContact_name().equals(member.getContact_name())
                                && ((ContactMember) selectMember).getContact_phone().equals(member.getContact_phone())
                                && member.getIsAddPhone() == 1) {
                            removeIndex = i;
                            break;
                        }
                    }
                    if (removeIndex != -1) {
                        llContent.removeViewAt(removeIndex);
                    }
                }

            }
        }

    }
}
