package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.ChoiceCorpAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.ActivityQrcode;
import com.miqtech.master.client.entity.Corps;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.ApplyPopupWindowActivity;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 1、创建战队或者选择战队
 * 2、创建或者选择后显示的状态
 * Created by zhaosentao on 2016/5/11.
 */
public class FragmentApplyPopupThree extends BaseFragment implements View.OnClickListener, ChoiceCorpAdapter.OnItemClickListener {

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

    @Bind(R.id.llFragmentIdentityCreateCorps)
    LinearLayout llCreateCorps;//创建战队
    @Bind(R.id.etFragmentIdentityCorpsName)
    EditText etCorpName;//战队名字
    @Bind(R.id.tvFragmentIdentityWordsNumber)
    TextView tvWordsNum;//显示输入了多少字
    @Bind(R.id.llFragmentIdentityCropsExplain)
    TextView tvCorpsExplain;//输入战队名字上方的文字解释

    @Bind(R.id.swRefreshFragmentIdentity)
    SwipeRefreshLayout swRefresh;
    @Bind(R.id.ryFragmentIdentity)
    RecyclerView recyclerView;


    private Context mContext;
    private Resources mResouces;
    private int registrationTypes = -1;
    private ActivityQrcode activityQrcode = new ActivityQrcode();
    private User user;
    private int page = 1;
    private int isLast = 0;
    private ChoiceCorpAdapter adapter;
    private LinearLayoutManager layoutManager;
    private int lastVisiableItem = -1;
    private List<Corps> mData = new ArrayList<>();//战队列表数据
    private Corps corps;//点击的那个战队
    private boolean isFirstCreate = true;
    private boolean isFirstShowToast = true;
    private boolean isOnClickPost = false;

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_apply_popup_three, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        mResouces = getResources();
        Bundle bundle = getArguments();
        activityQrcode = (ActivityQrcode) bundle.getSerializable("activityQrcode");
        initView();
//        showTheTopOfTheStatusBar();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isFirstCreate) {
            showTheTopOfTheStatusBar();
            if (registrationTypes == 2) {
                loadCrops();
            }
        }
        isFirstCreate = false;

        //当下一步按钮不为空，战队名字etCorpName不为空，且有etCorpNamenei内有内容是就有上角的按钮可点，颜色为橘色
        if (tvOkOrNext != null && etCorpName != null && !TextUtils.isEmpty(etCorpName.getText().toString())) {
            tvOkOrNext.setTextColor(getResources().getColor(R.color.orange));
            tvOkOrNext.setEnabled(true);
        }

    }

    private void initView() {
        llBack.setOnClickListener(this);
        tvOkOrNext.setOnClickListener(this);
        monitorEditView();
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        adapter = new ChoiceCorpAdapter(mData, getContext(), -1);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(layoutManager);
        swRefresh.setColorSchemeResources(R.color.orange);
        swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                loadCrops();
                isFirstShowToast = true;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisiableItem + 1 == adapter.getItemCount()) {
                    if (isLast == 0) {
                        adapter.setShowMore(true);
                        adapter.notifyDataSetChanged();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                page++;
                                loadCrops();
                            }
                        }, 1000);
                    } else {
                        if (isFirstShowToast) {
                            adapter.setShowMore(false);
                            adapter.notifyDataSetChanged();
                            showToast(getResources().getString(R.string.load_no));
                            isFirstShowToast = false;
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisiableItem = layoutManager.findLastVisibleItemPosition();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back_popupwindow://返回
                ((ApplyPopupWindowActivity) mContext).setSelectFragment(1);
                break;
            case R.id.tv_ok_popupwindow://下一步
                if (isOnClickPost) {
                    showToast("请不要重复点击");
                    return;
                } else {
                    isOnClickPost = true;
                }
                if (registrationTypes == 1) {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(),
                                0);
                    }
                    if (etCorpName != null && TextUtils.isEmpty(etCorpName.getText().toString())) {
                        showToast("战队名不能为空!");
                        return;
                    }
                    createTeam();
                } else if (registrationTypes == 2) {
                    if (corps != null) {
                        joinTeam();
                    }
                }
                break;
        }
    }

    //加载该赛事该网吧下的战队
    private void loadCrops() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("page", page + "");
//        params.put("activityId", matchId);
//        params.put("netbarId", netbarId + "");
//        params.put("round", round + "");//场次
        params.put("activityId", activityQrcode.getActivityInfo().getId() + "");
        params.put("netbarId", activityQrcode.getNetbarId() + "");
        params.put("round", activityQrcode.getRound() + "");//场次
        user = WangYuApplication.getUser(mContext);
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ACTIVITY_APPLIEDTEAMS_V2, params, HttpConstant.ACTIVITY_APPLIEDTEAMS_V2);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        isOnClickPost = false;
        if (swRefresh != null && swRefresh.isRefreshing()) {
            swRefresh.setRefreshing(false);
        }
        try {
            if (method.equals(HttpConstant.CREATE_TEAM_V2)) {//战队创建
                if (object.has("object")) {
                    JSONObject jsonObject = object.getJSONObject("object");
                    if (jsonObject.has("teamId")) {
                        activityQrcode.setTeamId(jsonObject.getString("teamId"));
                    }
                }
                ((ApplyPopupWindowActivity) mContext).setSuccess(true);
                ((ApplyPopupWindowActivity) mContext).setRegistrationTypes(1);
                ((ApplyPopupWindowActivity) mContext).setSelectFragment(3);
            } else if (method.equals(HttpConstant.ACTIVITY_APPLIEDTEAMS_V2)) {
                List<Corps> newDatas = new Gson().fromJson(object.getJSONObject("object").getJSONArray("list").toString(), new TypeToken<List<Corps>>() {
                }.getType());
                isLast = object.getJSONObject("object").getInt("isLast");
                if (newDatas.isEmpty() && isLast == 1 && page == 1) {//创建战队
                    registrationTypes = 1;
                    ((ApplyPopupWindowActivity) mContext).setRegistrationTypes(1);
                    swRefresh.setVisibility(View.GONE);
                    llCreateCorps.setVisibility(View.VISIBLE);
                    tvCorpsExplain.setText(mResouces.getString(R.string.FragmentIdentityCorpNameExplain));
                    tvTopTitle.setText(mResouces.getString(R.string.creation_corps_name));
                    tvOkOrNext.setText(mResouces.getString(R.string.countersign));
                    tvOkOrNext.setEnabled(true);
                    return;
                }
                if (!newDatas.isEmpty() && page == 1) {
                    registrationTypes = 2;
                    ((ApplyPopupWindowActivity) mContext).setRegistrationTypes(2);
                    tvOkOrNext.setTextColor(getResources().getColor(R.color.orange));
                    tvOkOrNext.setEnabled(true);
                }
                initCorpsView(newDatas);
            } else if (method.equals(HttpConstant.JOIN_TEAM_V2)) {//加入战队
                activityQrcode.setTeamId(corps.getTeam_id() + "");
                ((ApplyPopupWindowActivity) mContext).setSuccess(true);
                ((ApplyPopupWindowActivity) mContext).setRegistrationTypes(2);
                ((ApplyPopupWindowActivity) mContext).setSelectFragment(3);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        isOnClickPost = false;
        if (swRefresh != null && swRefresh.isRefreshing()) {
            swRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        isOnClickPost = false;
        if (swRefresh != null && swRefresh.isRefreshing()) {
            swRefresh.setRefreshing(false);
        }
        if (object.has("code")) {
            try {
                if (-1 == object.getInt("code")) {
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (method.equals(HttpConstant.JOIN_TEAM_V2)) {//加入战队
            ((ApplyPopupWindowActivity) mContext).setSuccess(false);
            ((ApplyPopupWindowActivity) mContext).setRegistrationTypes(2);
            ((ApplyPopupWindowActivity) mContext).setSelectFragment(3);
        } else if (method.equals(HttpConstant.CREATE_TEAM_V2)) {//战队创建
            ((ApplyPopupWindowActivity) mContext).setSuccess(false);
            ((ApplyPopupWindowActivity) mContext).setRegistrationTypes(1);
            ((ApplyPopupWindowActivity) mContext).setSelectFragment(3);
        }
    }

    private void initCorpsView(List<Corps> data) {
        if (page == 1) {
            mData.clear();
        }
        mData.addAll(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemclick(int position) {
        if (mData.isEmpty()) {
            return;
        }
        corps = mData.get(position);
        adapter.setSelected(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void isShowBtn() {

    }

    @Override
    public void onListenerForEditView(EditText editText, TextView textView) {

    }

    /**
     * 创建战队
     */
    private void createTeam() {
        user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("teamName", etCorpName.getText().toString());
            map.put("activityId", activityQrcode.getActivityInfo().getId() + "");
            map.put("netbarId", activityQrcode.getNetbarId());
            map.put("round", activityQrcode.getRound());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.CREATE_TEAM_V2, map, HttpConstant.CREATE_TEAM_V2);
        } else {
            toLogin();
        }
    }

    /**
     * 加入战队
     */
    private void joinTeam() {
        user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("teamId", corps.getTeam_id() + "");//战队id
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.JOIN_TEAM_V2, map, HttpConstant.JOIN_TEAM_V2);
        } else {
            toLogin();
        }
    }

    private void toLogin() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);
    }

    /**
     * 监听输入的变化
     */
    private void monitorEditView() {
        TextWatcher textWatcher = new TextWatcher() {
            String temp;
            int words;
            int numberWords = 16;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(temp)) {
                    tvOkOrNext.setTextColor(getResources().getColor(R.color.orange));
                    tvOkOrNext.setEnabled(true);
                } else {
                    tvOkOrNext.setTextColor(getResources().getColor(R.color.lv_item_content_text));
                    tvOkOrNext.setEnabled(false);
                }

                words = numberWords - temp.length();
                if (words != 16) {
                    tvWordsNum.setText("剩" + words + "字");
                    tvWordsNum.setVisibility(View.VISIBLE);
                } else {
                    tvWordsNum.setVisibility(View.GONE);
                }
            }
        };
        etCorpName.addTextChangedListener(textWatcher);
    }

    /**
     * 显示顶部的状态
     */
    private void showTheTopOfTheStatusBar() {
        registrationTypes = ((ApplyPopupWindowActivity) mContext).getRegistrationTypes();
        tvSelectWhichOne.setText("3");
        tvBack.setText(mResouces.getString(R.string.back));
        ivBack.setVisibility(View.VISIBLE);
        tvTotal.setText("/4");
        tvOkOrNext.setText(mResouces.getString(R.string.FragmentThreeNext));
        tvOkOrNext.setTextColor(getResources().getColor(R.color.lv_item_content_text));
        tvOkOrNext.setEnabled(false);

        if (registrationTypes == 1) {
            tvCorpsExplain.setText(mResouces.getString(R.string.corp_name_explain));
            swRefresh.setVisibility(View.GONE);
            llCreateCorps.setVisibility(View.VISIBLE);
            tvTopTitle.setText(mResouces.getString(R.string.creation_corps_name));
            tvOkOrNext.setText(mResouces.getString(R.string.countersign));
        } else if (registrationTypes == 2) {
            llCreateCorps.setVisibility(View.GONE);
            swRefresh.setVisibility(View.VISIBLE);
            tvTopTitle.setText(mResouces.getString(R.string.select_corps));
            tvOkOrNext.setText(mResouces.getString(R.string.FragmentInfojoin));
        }
    }

}
