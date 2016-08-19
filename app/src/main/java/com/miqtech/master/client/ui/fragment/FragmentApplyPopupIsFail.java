package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.ActivityQrcode;
import com.miqtech.master.client.ui.ApplyPopupWindowActivity;
import com.miqtech.master.client.ui.CorpsDetailsV2Activity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.view.SuperLoadingProgress;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 扫码最后一补
 * Created by zhaosentao on 2016/5/12.
 */
public class FragmentApplyPopupIsFail extends BaseFragment implements SuperLoadingProgress.LoadingFinish {

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

    @Bind(R.id.SpFragmentIsFail)
    SuperLoadingProgress superLoadingProgress;//提示图片
    @Bind(R.id.tvFragmentIsFail)
    TextView tvHint;//提示文字


    private Context mContext;
    private Resources mResouces;
    private int registrationTypes = -1;//0个人报名，1创建临时战队，2加入临时战队,3战队报名,4加入战队 (会变的，例如原状态为加入战队，因没有战队变创建战队)

    private boolean isSuccess = false;
    private boolean isFirstCreate = true;
    private ActivityQrcode activityQrcode;


    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_apply_popup_is_fail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        mResouces = getResources();
        superLoadingProgress.setLoadingFinish(this);
        Bundle bundle = getArguments();
        activityQrcode = (ActivityQrcode) bundle.getSerializable("activityQrcode");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isFirstCreate) {
            showTheTopOfTheStatusBar();
            showHint();
        }
        isFirstCreate = false;
    }

    private void showHint() {
        isSuccess = ((ApplyPopupWindowActivity) mContext).isSuccess();
        if (isSuccess) {
            superLoadingProgress.setLodingColor(mResouces.getColor(R.color.orange));
            switch (registrationTypes) {
                case 0://0个人报名
                    tvHint.setText(mResouces.getString(R.string.FragmentIsFailSuccess));
                    break;
                case 1://1创建临时战队
                    tvHint.setText(mResouces.getString(R.string.FragmentIsFailCreateCorps));
                    break;
                case 2://2加入临时战队(扫的是赛事报名)
                    tvHint.setText(mResouces.getString(R.string.FragmentIsFailApplySuccess));
                    break;
                case 4://4加入战队(扫的是战队报名)
                    tvHint.setText(mResouces.getString(R.string.FragmentIsFailApplySuccess));
                    break;
            }
        } else {
            superLoadingProgress.setLodingColor(mResouces.getColor(R.color.red_show_fail_progress));
            switch (registrationTypes) {
                case 0://0个人报名
                    tvHint.setText(mResouces.getString(R.string.FragmentIsFailFail));
                    break;
                case 1://1创建临时战队
                    tvHint.setText(mResouces.getString(R.string.FragmentIsFailCreateCorpsFail));
                    break;
                case 2://2加入临时战队(扫的是赛事报名)
//                    tvHint.setText(mResouces.getString(R.string.FragmentIsFailApplyFail));
                    tvHint.setVisibility(View.GONE);
                    break;
                case 4://4加入战队(扫的是战队报名)
//                    tvHint.setText(mResouces.getString(R.string.FragmentIsFailApplyFail));
                    tvHint.setVisibility(View.GONE);
                    break;
            }
        }
        superLoadingProgress.finishIsSuccess(isSuccess);
    }

    @Override
    public void loadingFinish() {
        if (registrationTypes == 0) {
            ((ApplyPopupWindowActivity) mContext).onBackPressed();
        } else if (registrationTypes == 2 || registrationTypes == 4 || registrationTypes == 1) {
            if (isSuccess) {
                int teamId = 0;
                try {
                    teamId = Integer.parseInt(activityQrcode.getTeamId());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(mContext, CorpsDetailsV2Activity.class);
                intent.putExtra("teamId", teamId);
                mContext.startActivity(intent);
                ((ApplyPopupWindowActivity) mContext).onBackPressed();
            } else {
                ((ApplyPopupWindowActivity) mContext).onBackPressed();
            }
        }
    }

    /**
     * 显示顶部的状态
     */
    private void showTheTopOfTheStatusBar() {
        registrationTypes = ((ApplyPopupWindowActivity) mContext).getRegistrationTypes();
        if (registrationTypes == 4) {//加入战队
            tvTopTitle.setText(mResouces.getString(R.string.corpsDetailsV2JoinTheTeam));
            tvSelectWhichOne.setText("2");
            tvTotal.setText("/2");
        } else if (registrationTypes == 0) {//个人报名
            tvTopTitle.setText(mResouces.getString(R.string.FragmentIsFailCompleteRegistration));
            tvSelectWhichOne.setText("2");
            tvTotal.setText("/2");
        } else if (registrationTypes == 1) {//创建战队
            tvTopTitle.setText(mResouces.getString(R.string.creation_corps_name));
            tvSelectWhichOne.setText("4");
            tvTotal.setText("/4");
        } else if (registrationTypes == 2) {//加入战队
            tvTopTitle.setText(mResouces.getString(R.string.corpsDetailsV2JoinTheTeam));
            tvSelectWhichOne.setText("4");
            tvTotal.setText("/4");
        }
    }

}
