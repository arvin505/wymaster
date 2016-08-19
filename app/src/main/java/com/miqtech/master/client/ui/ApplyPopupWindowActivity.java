package com.miqtech.master.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.FragmentPagerAdpter;
import com.miqtech.master.client.entity.ActivityQrcode;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.ui.fragment.FragmentApplyPopupConfirmInformation;
import com.miqtech.master.client.ui.fragment.FragmentApplyPopupIsFail;
import com.miqtech.master.client.ui.fragment.FragmentApplyPopupSelectIdentity;
import com.miqtech.master.client.ui.fragment.FragmentApplyPopupThree;
import com.miqtech.master.client.view.MyPagerView;


import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 扫描二维码后开始的报名流程
 * Created by zhaosentao on 2016/5/11.
 */
public class ApplyPopupWindowActivity extends BaseActivity {

    @Bind(R.id.PagerViewApplyPupoAct)
    MyPagerView myPagerView;
    private FragmentPagerAdpter adpter;

    private int registrationTypes = -1;//0个人报名，1创建临时战队，2加入临时战队,3战队报名,4加入战队 (会变的，例如原状态为加入战队，因没有战队变创建战队)
    private int typeApply = -1;//0个人报名，1创建临时战队，2加入临时战队,3战队报名,4加入战队(第四个是扫战队二维码加入战队)。(固定不变的)

    private ActivityQrcode activityQrcode = new ActivityQrcode();
    private boolean isSuccess = false;//用来判断是否成功

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_apply_popupwindow);
        ButterKnife.bind(this);
        activityQrcode = (ActivityQrcode) getIntent().getSerializableExtra("activityQrcode");
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        if (activityQrcode != null) {
            if (activityQrcode.getActivityInfo().getPerson_or_team() == 1) {
                typeApply = 0;
            } else if (activityQrcode.getActivityInfo().getPerson_or_team() == 2) {
                typeApply = 3;
            }
            if (!TextUtils.isEmpty(activityQrcode.getTeamId())) {//加入战队
                typeApply = 4;
            }
        } else {
            showToast(getResources().getString(R.string.ApplyPopupQrcodeError));
            finish();
        }
        activityQrcode.setTypeApply(typeApply);
        registrationTypes = typeApply;
        adpter = new FragmentPagerAdpter(this);
        Bundle bundle = new Bundle();
        bundle.putSerializable("activityQrcode", activityQrcode);
        if (typeApply == 3) {
            adpter.addTab(FragmentApplyPopupConfirmInformation.class, bundle);
            adpter.addTab(FragmentApplyPopupSelectIdentity.class, null);
            adpter.addTab(FragmentApplyPopupThree.class, bundle);
            adpter.addTab(FragmentApplyPopupIsFail.class, bundle);
        } else if (typeApply == 4 || typeApply == 0) {
            adpter.addTab(FragmentApplyPopupConfirmInformation.class, bundle);
            adpter.addTab(FragmentApplyPopupIsFail.class, bundle);
        }
        myPagerView.setOffscreenPageLimit(adpter.getCount());
        myPagerView.setAdapter(adpter);
        myPagerView.setCurrentItem(0);
    }

    public void setSelectFragment(int position) {
        myPagerView.setCurrentItem(position);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.out_from_bottom);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.out_from_bottom);
    }


    public int getRegistrationTypes() {
        return registrationTypes;
    }

    public void setRegistrationTypes(int registrationTypes) {
        this.registrationTypes = registrationTypes;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (myPagerView.getCurrentItem() == 0) {
            MyBaseFragment myBaseFragment = (MyBaseFragment) adpter.getItem(0);
            myBaseFragment.refreView();
        }
    }
}
