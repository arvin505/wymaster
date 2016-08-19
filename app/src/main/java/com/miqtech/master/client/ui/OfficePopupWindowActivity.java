package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.widget.LinearLayout;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.FragmentPagerAdpter;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.ui.fragment.FragmentActivityChoiceCorp;
import com.miqtech.master.client.ui.fragment.FragmentChooseTimeAndPlace;
import com.miqtech.master.client.ui.fragment.FragmentConfirmInformation;
import com.miqtech.master.client.ui.fragment.FragmentSelectJoinCard;
import com.miqtech.master.client.view.MyPagerView;


import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/1/25.
 */
public class OfficePopupWindowActivity extends BaseActivity {
    @Bind(R.id.mypagerview)
    MyPagerView myPagerView;
    @Bind(R.id.ll_view)
    LinearLayout llView;
    private Context context;
    private FragmentPagerAdpter adpter;

    private int registrationTypes = -1;//0个人报名，1创建临时战队，2加入临时战队 , 3从战队列表加入战队 。(会变的，例如原状态为加入战队，因没有战队变创建战队)
    private int typeApply = -1;//0个人报名，1创建临时战队，2加入临时战队, 3从战队列表加入战队(确认参赛卡和确认参赛信息)。(固定不变的)

    private int netbarId;//网吧ID
    public String matchId = "";//比赛ID
    private String matchTime = "";//比赛地点
    private String matchAddress = "";//比赛地址
    private String teamID = "";//战队ID
    private String teamName = "";//战队名称
    private String round = "";//场次IDs


    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_office_popupwindow);
        context = this;
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        matchId = getIntent().getStringExtra("id");
        typeApply = getIntent().getIntExtra("typeApply", -1);
        teamID = getIntent().getStringExtra("teamid");
        teamName = getIntent().getStringExtra("teamname");
        matchAddress = getIntent().getStringExtra("address");
        matchTime = getIntent().getStringExtra("date");
        registrationTypes = typeApply;
//        matchId = 77 + "";
        adpter = new FragmentPagerAdpter(this);
        if (typeApply == 0) {
            adpter.addTab(FragmentChooseTimeAndPlace.class, null);
            adpter.addTab(FragmentSelectJoinCard.class, null);
            adpter.addTab(FragmentConfirmInformation.class, null);
        } else if (typeApply == 1 || typeApply == 2) {
            adpter.addTab(FragmentChooseTimeAndPlace.class, null);
            adpter.addTab(FragmentActivityChoiceCorp.class, null);
            adpter.addTab(FragmentSelectJoinCard.class, null);
            adpter.addTab(FragmentConfirmInformation.class, null);
        } else if (typeApply == 3) {
            adpter.addTab(FragmentSelectJoinCard.class, null);
            adpter.addTab(FragmentConfirmInformation.class, null);
        }
        myPagerView.setOffscreenPageLimit(adpter.getCount());
        myPagerView.setAdapter(adpter);
        myPagerView.setCurrentItem(0);
    }

    public void setSelectFragment(int position) {
        myPagerView.setCurrentItem(position);
    }

    @Override
    protected void initData() {
        super.initData();
    }


    public int getNetbarId() {
        return netbarId;
    }

    public void setNetbarId(int netbarId) {
        this.netbarId = netbarId;
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

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getMatchAddress() {
        return matchAddress;
    }

    public void setMatchAddress(String matchAddress) {
        this.matchAddress = matchAddress;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getTeamID() {
        return teamID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getTypeApply() {
        return typeApply;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (typeApply == 1 || typeApply == 2) {
            if (myPagerView.getCurrentItem() == 2) {
                MyBaseFragment myBaseFragment = (MyBaseFragment) adpter.getItem(2);
                myBaseFragment.refreView();
            }
        } else if (typeApply == 0) {
            if (myPagerView.getCurrentItem() == 1) {
                MyBaseFragment myBaseFragment = (MyBaseFragment) adpter.getItem(1);
                myBaseFragment.refreView();
            }
        }
    }
}
