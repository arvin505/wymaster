package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.ChoiceCorpAdapter;
import com.miqtech.master.client.entity.Corps;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.OfficePopupWindowActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/25.
 */
public class FragmentActivityChoiceCorp extends BaseFragment implements View.OnClickListener, ChoiceCorpAdapter.OnItemClickListener {

    RecyclerView rvCrops;
    SwipeRefreshLayout swRefresh;
    private TextView tvClose;
    private TextView tvTitle;
    private TextView tvTotal;
    private TextView tvWhichOne;
    private LinearLayout llBack;
    private ImageView ivBack;
    TextView tvOK;

    private View mainView;
    private Map<String, String> params = new HashMap<>();
    private Context context;
    private int page = 1;

    private String matchId;

    private List<Corps> mData = new ArrayList<>();
    private ChoiceCorpAdapter adapter;
    private LinearLayoutManager layoutManager;
    private int lastVisiableItem = -1;
    private int isLast = 0;
    private int netbarId = -1;

    private int registrationTypes = -1;//0个人报名，1创建临时战队，2加入临时战队
    private int typeApply = -1;//0个人报名，1创建临时战队，2加入临时战队
    private String teamName = "";
    private String round;
    private boolean isFirstCreate = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mainView == null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            mainView = inflater.inflate(R.layout.fragment_choice_crop, null);
            context = getActivity();
            initView();
        }
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mainView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isFirstCreate) {
            showTheTopOfTheStatusBar();
        }
        isFirstCreate = false;
    }

    private void initView() {
        rvCrops = (RecyclerView) mainView.findViewById(R.id.rv_crops);
        swRefresh = (SwipeRefreshLayout) mainView.findViewById(R.id.sw_refresh);
        tvClose = (TextView) mainView.findViewById(R.id.tv_close_popupwindow);
        tvTitle = (TextView) mainView.findViewById(R.id.tv_title_popupwindow);
        tvTotal = (TextView) mainView.findViewById(R.id.tv_total);
        tvWhichOne = (TextView) mainView.findViewById(R.id.tv_select_which_one);
        llBack = (LinearLayout) mainView.findViewById(R.id.ll_back_popupwindow);
        ivBack = (ImageView) mainView.findViewById(R.id.iv_back_popupwindow);
        tvOK = (TextView) mainView.findViewById(R.id.tv_ok_popupwindow);

        swRefresh.setColorSchemeResources(R.color.orange);
        swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                loadCrops();
            }
        });

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvCrops.setLayoutManager(layoutManager);

        adapter = new ChoiceCorpAdapter(mData, getContext(), -1);
        adapter.setOnItemClickListener(this);
        rvCrops.setAdapter(adapter);
        rvCrops.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        showToast(getResources().getString(R.string.load_no));
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
            case R.id.ll_back_popupwindow://返回上个界面
                mData.clear();
                if (adapter != null) {
                    adapter.setTypeView(-1);
                }
                ((OfficePopupWindowActivity) context).setSelectFragment(0);
                break;
            case R.id.tv_ok_popupwindow://确认按钮
                if (adapter.getTypeView() == 2) {
                    if (!TextUtils.isEmpty(teamName.trim())) {
                        ((OfficePopupWindowActivity) context).setTeamName(teamName);
                        ((OfficePopupWindowActivity) context).setSelectFragment(2);
                    } else {
                        showToast("给你的战队起个名字吧");
                    }
                }
                break;
        }
    }

    private void loadCrops() {
        showLoading();
        params.put("activityId", matchId);
        params.put("netbarId", netbarId + "");
        params.put("page", page + "");
        params.put("round", round + "");
        LogUtil.e("params", "params : " + params.toString());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ACTIVITY_APPLIEDTEAMS_V2, params, HttpConstant.ACTIVITY_APPLIEDTEAMS_V2);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        swRefresh.setRefreshing(false);
        adapter.setShowMore(false);
        LogUtil.e("object", "object : " + object.toString());
        if (method.equals(HttpConstant.ACTIVITY_APPLIEDTEAMS_V2))
            try {
                List<Corps> newDatas = initCorps(object.getJSONObject("object").getJSONArray("list").toString());
                isLast = object.getJSONObject("object").getInt("isLast");
                if (newDatas.isEmpty() && isLast == 1 && page == 1) {
                    adapter.setTypeView(1);
                    tvTitle.setText(context.getResources().getText(R.string.creation_corps));
                    ((OfficePopupWindowActivity) context).setRegistrationTypes(1);
                    return;
                }
                ((OfficePopupWindowActivity) context).setRegistrationTypes(2);
                initCorpsView(newDatas);
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
        swRefresh.setRefreshing(false);
        if (adapter != null) {
            adapter.setTypeView(-1);
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        swRefresh.setRefreshing(false);
        hideLoading();
        if (adapter != null) {
            adapter.setTypeView(-1);
        }
    }

    private List<Corps> initCorps(String object) {
        return GsonUtil.getList(object, Corps.class);
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
        Corps corps = mData.get(position);
        ((OfficePopupWindowActivity) context).setTeamID(corps.getTeam_Id() + "");
        ((OfficePopupWindowActivity) context).setTeamName(corps.getTeam_name());
        adapter.setSelected(position);
        adapter.notifyDataSetChanged();
        ((OfficePopupWindowActivity) context).setSelectFragment(2);
    }

    @Override
    public void isShowBtn() {
        if (adapter != null && adapter.getTypeView() == 2) {
            tvOK.setText("确认");
            tvOK.setTextColor(context.getResources().getColor(R.color.lv_item_content_text));
            tvTitle.setText(context.getResources().getText(R.string.creation_corps_name));
            tvOK.setVisibility(View.VISIBLE);
        } else {
            tvOK.setVisibility(View.GONE);
        }
    }

    @Override
    public void onListenerForEditView(EditText editText, TextView textView) {
        if (editText != null) {
            monitorEditView(editText, textView);
        }
    }

    /**
     * 监听输入的变化
     */
    private void monitorEditView(EditText editText, final TextView textView) {
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
                teamName = temp;
                if (!TextUtils.isEmpty(temp)) {
                    tvOK.setTextColor(getResources().getColor(R.color.orange));
                    tvOK.setEnabled(true);
                } else {
                    tvOK.setTextColor(getResources().getColor(R.color.lv_item_content_text));
                    tvOK.setEnabled(false);
                }

                words = numberWords - temp.length();
                if (words != 16) {
                    textView.setText("剩" + words + "字");
                    textView.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.GONE);
                }
            }
        };
        editText.addTextChangedListener(textWatcher);
    }

    /**
     * 显示顶部状态栏
     */
    private void showTheTopOfTheStatusBar() {
        registrationTypes = ((OfficePopupWindowActivity) context).getRegistrationTypes();
        typeApply = ((OfficePopupWindowActivity) context).getTypeApply();
        netbarId = ((OfficePopupWindowActivity) context).getNetbarId();
        matchId = ((OfficePopupWindowActivity) context).getMatchId();
        round = ((OfficePopupWindowActivity) context).getRound();


        tvTitle.setText(context.getResources().getText(R.string.select_corps));
        ivBack.setVisibility(View.VISIBLE);
        tvTotal.setText("/4");
        tvWhichOne.setText("2");
        tvTitle.setText(context.getResources().getText(R.string.select_corps));
        tvClose.setText(context.getResources().getText(R.string.back));
        llBack.setOnClickListener(this);
        tvOK.setOnClickListener(this);

        if (typeApply == 1) {//创建临时战队
            adapter.setTypeView(2);
            tvTitle.setText(context.getResources().getText(R.string.creation_corps));
        } else if (typeApply == 2) {//加入临时战队
            loadCrops();
        }
    }


}
