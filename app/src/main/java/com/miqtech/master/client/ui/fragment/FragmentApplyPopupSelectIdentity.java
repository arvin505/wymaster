package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.ui.ApplyPopupWindowActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 选择参赛的身份
 * Created by zhaosent on 2016/5/11.
 */
public class FragmentApplyPopupSelectIdentity extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.ll_back_popupwindow)
    LinearLayout llBack;//返回或者关闭
    @Bind(R.id.iv_back_popupwindow)
    ImageView ivBack;//返回或者关闭的图片（目前关闭时不需要图片）
    @Bind(R.id.tv_close_popupwindow)
    TextView tvBack;//显示返回或者关闭等字样
    @Bind(R.id.tv_title_popupwindow)
    TextView tvTopTitle;//顶部的标题
    @Bind(R.id.tv_select_which_one)
    TextView tvSelectWhichOne;//显示进到了哪一步
    @Bind(R.id.tv_total)
    TextView tvTotal;//总的步骤数
    @Bind(R.id.tv_ok_popupwindow)
    TextView tvOkOrNext;//确定或者下一步

    @Bind(R.id.llFragmentIdentityCaptain)
    LinearLayout llCaptain;//队长
    @Bind(R.id.llFragmentIdentityIronman)
    LinearLayout llIronman;//队员


    private Context mContext;
    private Resources mResouces;

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_apply_popu_select_identity, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        mResouces = getResources();
        initView();
        showTheTopOfTheStatusBar();
    }

    private void initView() {
        llBack.setOnClickListener(this);
        llCaptain.setOnClickListener(this);
        llIronman.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back_popupwindow://返回
                ((ApplyPopupWindowActivity) mContext).setSelectFragment(0);
                break;
            case R.id.llFragmentIdentityCaptain://创建战队
                ((ApplyPopupWindowActivity) mContext).setRegistrationTypes(1);
                ((ApplyPopupWindowActivity) mContext).setSelectFragment(2);
                break;
            case R.id.llFragmentIdentityIronman://加入战队
                ((ApplyPopupWindowActivity) mContext).setRegistrationTypes(2);
                ((ApplyPopupWindowActivity) mContext).setSelectFragment(2);
                break;
        }
    }

    /**
     * 显示顶部的状态
     */
    private void showTheTopOfTheStatusBar() {
        tvSelectWhichOne.setText("2");
        tvBack.setText(mResouces.getString(R.string.back));
        ivBack.setVisibility(View.VISIBLE);
        tvTopTitle.setText(mResouces.getString(R.string.FragmentIdentitySelectIdentity));
        tvTotal.setText("/4");
    }
}
