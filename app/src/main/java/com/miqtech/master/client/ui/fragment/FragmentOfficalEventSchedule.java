package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.MatchArea;
import com.miqtech.master.client.entity.MatchNetbar;
import com.miqtech.master.client.entity.MatchSchedule;
import com.miqtech.master.client.entity.MatchTeam;
import com.miqtech.master.client.ui.BaiduMapActivity;
import com.miqtech.master.client.ui.CorpsDetailsV2Activity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/1/8.
 */
public class FragmentOfficalEventSchedule extends BaseFragment implements View.OnClickListener {

    private static final String ARG_POSITION = "position";

    private int position;
    private Context context;
    private MatchSchedule schedule;

    @Bind(R.id.tvStatus)
    TextView tvStatus;

    @Bind(R.id.llContent)
    LinearLayout llContent;

//    @Bind(R.id.llTeam)
//    LinearLayout llTeam;
//
//    @Bind(R.id.llNetbar)
//    LinearLayout llNetbar;
//
//    @Bind(R.id.tvMore)
//    TextView tvMore;

    public static FragmentOfficalEventSchedule newInstance(int position, MatchSchedule schedule) {
        FragmentOfficalEventSchedule f = new FragmentOfficalEventSchedule();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        b.putSerializable("schedule", schedule);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        schedule = (MatchSchedule) getArguments().get("schedule");
    }


    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_officialeventschedule, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        ButterKnife.bind(this, view);
        addMatchNetbarAreaViews();
    }

    private void addMatchNetbarAreaViews() {
        if (schedule.getStatus() == 1) {
            tvStatus.setText("报名中");
        } else if (schedule.getStatus() == 2) {
            tvStatus.setText("报名预热中");
        } else if (schedule.getStatus() == 3) {
            tvStatus.setText("报名已截止");
        } else if (schedule.getStatus() == 4) {
            tvStatus.setText("赛事已结束");
        } else if (schedule.getStatus() == 5) {
            tvStatus.setText("赛事进行中");
        }
        for (int i = 0; i < schedule.getAreas().size(); i++) {
            addMatchNetbarAreaView(schedule.getAreas().get(i), schedule.getTeams());
        }

        //添加当日已报战队View
        View hasApplyTeam = View.inflate(context, R.layout.layout_matchaddress, null);
        TextView tvLineLeft = (TextView) hasApplyTeam.findViewById(R.id.tvLineLeft);
        TextView tvLineRight = (TextView) hasApplyTeam.findViewById(R.id.tvLineRight);
        LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(0, 1, 1.0f);
        leftParams.setMargins((int) getResources().getDimension(R.dimen.margin_25dp), 0, 0, 0);
        tvLineLeft.setLayoutParams(leftParams);
        LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(0, 1, 1.0f);
        rightParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.margin_25dp), 0);
        tvLineRight.setLayoutParams(rightParams);
        TextView tvHasApplyTeam = (TextView) hasApplyTeam.findViewById(R.id.tvArea);
        tvHasApplyTeam.setText("当日已报战队");
        llContent.addView(hasApplyTeam);
//        llTeam.addView(hasApplyTeam);
        //添加已报战队View
        if (schedule.getTeams() != null) {
            for (int i = 0; i < schedule.getTeams().size(); i++) {
                View joinTeamView = View.inflate(context, R.layout.layout_matchjointeam, null);
                TextView tvTeamName = (TextView) joinTeamView.findViewById(R.id.tvTeamName);
                TextView tvMatchCaptain = (TextView) joinTeamView.findViewById(R.id.tvMatchCaptain);
                TextView tvHasApplyNum = (TextView) joinTeamView.findViewById(R.id.tvHasApplyNum);
                TextView tvTeamNum = (TextView) joinTeamView.findViewById(R.id.tvTeamNum);
                MatchTeam matchTeam = schedule.getTeams().get(i);
                joinTeamView.setTag(schedule.getTeams().get(i));
                joinTeamView.setOnClickListener(this);
                if (!TextUtils.isEmpty(matchTeam.getTeam_name())) {
                    tvTeamName.setText(matchTeam.getTeam_name());
                } else {
                    tvTeamName.setText("暂无战队名称");
                }
                if (!TextUtils.isEmpty(matchTeam.getHeader())) {
                    tvMatchCaptain.setText(matchTeam.getHeader());
                } else {
                    tvMatchCaptain.setText("暂无队长名称");
                }
                tvHasApplyNum.setText(matchTeam.getNum() + "");
                tvTeamNum.setText("/" + matchTeam.getTotal_num());
                llContent.addView(joinTeamView);
//                llTeam.addView(joinTeamView);
            }

        }
    }

    private void addMatchNetbarAreaView(final MatchArea matchArea, List<MatchTeam> teams) {
        //添加地区View
        if (!TextUtils.isEmpty(matchArea.getName())) {
            View areaView = View.inflate(context, R.layout.layout_matchaddress, null);
            TextView tvLineLeft = (TextView) areaView.findViewById(R.id.tvLineLeft);
            TextView tvLineRight = (TextView) areaView.findViewById(R.id.tvLineRight);
            TextView tvArea = (TextView) areaView.findViewById(R.id.tvArea);
            tvArea.setText(matchArea.getName());
            LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(0, 1, 1.0f);
            leftParams.setMargins((int) getResources().getDimension(R.dimen.margin_50dp), 0, 0, 0);
            LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(0, 1, 1.0f);
            rightParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.margin_50dp), 0);
            tvLineLeft.setLayoutParams(leftParams);
            tvLineRight.setLayoutParams(rightParams);
            llContent.addView(areaView);
        }

//        if (matchArea != null && matchArea.getNetbars().size() > 8) {
//            //添加网吧View
//            if (matchArea != null && matchArea.getNetbars() != null) {
//                for (int i = 0; i < 8; i++) {
//                    View netbarAddresView = View.inflate(context, R.layout.layout_matchnetbar, null);
//                    netbarAddresView.setTag(matchArea.getNetbars().get(i));
//                    TextView tvNetbarName = (TextView) netbarAddresView.findViewById(R.id.tvNetbarName);
//                    TextView tvNetbarAddress = (TextView) netbarAddresView.findViewById(R.id.tvNetbarAddress);
//                    MatchNetbar netbar = matchArea.getNetbars().get(i);
//                    if (!TextUtils.isEmpty(netbar.getName())) {
//                        tvNetbarName.setText(netbar.getName());
//                    } else {
//                        tvNetbarName.setText("暂无网吧名称");
//                    }
//                    if (!TextUtils.isEmpty(netbar.getAddress())) {
//                        tvNetbarAddress.setText(netbar.getAddress());
//                    } else {
//                        tvNetbarAddress.setText("暂无网吧地址");
//                    }
//                    netbarAddresView.setOnClickListener(this);
//                    llContent.addView(netbarAddresView);
//                }
//            }
//
//            tvMore.setVisibility(View.VISIBLE);
//            llNetbar.setVisibility(View.VISIBLE);
//            tvMore.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //超过8个的放在下面的linearLayout
//                    for (int i = 8; i < matchArea.getNetbars().size(); i++) {
//                        View netbarAddresView = View.inflate(context, R.layout.layout_matchnetbar, null);
//                        netbarAddresView.setTag(matchArea.getNetbars().get(i));
//                        TextView tvNetbarName = (TextView) netbarAddresView.findViewById(R.id.tvNetbarName);
//                        TextView tvNetbarAddress = (TextView) netbarAddresView.findViewById(R.id.tvNetbarAddress);
//                        MatchNetbar netbar = matchArea.getNetbars().get(i);
//                        if (!TextUtils.isEmpty(netbar.getName())) {
//                            tvNetbarName.setText(netbar.getName());
//                        } else {
//                            tvNetbarName.setText("暂无网吧名称");
//                        }
//                        if (!TextUtils.isEmpty(netbar.getAddress())) {
//                            tvNetbarAddress.setText(netbar.getAddress());
//                        } else {
//                            tvNetbarAddress.setText("暂无网吧地址");
//                        }
//                        netbarAddresView.setOnClickListener(this);
//                        llNetbar.addView(netbarAddresView);
//                    }
//                    tvMore.setVisibility(View.GONE);
//                }
//            });
//
//        } else {
//            tvMore.setVisibility(View.GONE);
//            llNetbar.setVisibility(View.GONE);
            //添加网吧View
            if (matchArea != null && matchArea.getNetbars() != null) {
                for (int i = 0; i < matchArea.getNetbars().size(); i++) {
                    View netbarAddresView = View.inflate(context, R.layout.layout_matchnetbar, null);
                    netbarAddresView.setTag(matchArea.getNetbars().get(i));
                    TextView tvNetbarName = (TextView) netbarAddresView.findViewById(R.id.tvNetbarName);
                    TextView tvNetbarAddress = (TextView) netbarAddresView.findViewById(R.id.tvNetbarAddress);
                    MatchNetbar netbar = matchArea.getNetbars().get(i);
                    if (!TextUtils.isEmpty(netbar.getName())) {
                        tvNetbarName.setText(netbar.getName());
                    } else {
                        tvNetbarName.setText("暂无网吧名称");
                    }
                    if (!TextUtils.isEmpty(netbar.getAddress())) {
                        tvNetbarAddress.setText(netbar.getAddress());
                    } else {
                        tvNetbarAddress.setText("暂无网吧地址");
                    }
                    netbarAddresView.setOnClickListener(this);
                    llContent.addView(netbarAddresView);
                }
            }
//        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.llMatchNetbar:
                MatchNetbar matchNetbar = (MatchNetbar) v.getTag();
                if (matchNetbar != null) {
                    intent.setClass(context, BaiduMapActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("latitude", matchNetbar.getLatitude());
                    bundle.putDouble("longitude", matchNetbar.getLongitude());
                    bundle.putString("netbarTitle", matchNetbar.getName());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }

                break;
            case R.id.llMatchJoinTeam:
                MatchTeam matchTeam = (MatchTeam) v.getTag();
                if (matchTeam != null) {
                    intent.putExtra("teamId", matchTeam.getTeam_id());
                    intent.putExtra("matchId", schedule.getActivityId());
                    intent.putExtra("matchTime", schedule.getDate());
                    intent.setClass(context, CorpsDetailsV2Activity.class);
                    context.startActivity(intent);
                }
                break;
        }
    }
}
