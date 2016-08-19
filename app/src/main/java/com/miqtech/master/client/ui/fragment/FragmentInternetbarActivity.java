package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.NetbarActivityAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.InternetBarInfo;
import com.miqtech.master.client.entity.NetBarAmuse;
import com.miqtech.master.client.entity.NetbarActivityInfo;
import com.miqtech.master.client.entity.NetbarService;
import com.miqtech.master.client.entity.YueZhan;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.RecreationMatchDetailsActivity;
import com.miqtech.master.client.ui.ReleaseWarActivity;
import com.miqtech.master.client.ui.SubjectActivity;
import com.miqtech.master.client.ui.YueZhanDetailsActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.view.layoutmanager.FullLinearLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/3/8.
 */
public class FragmentInternetbarActivity extends BaseFragment implements NetbarActivityAdapter.OnItemClickListener {
    @Bind(R.id.rvNetbarMatch)
    RecyclerView rvNetbarMatch;

    private Context context;

    private InternetBarInfo mNetbarInfo;

    private HashMap<String, String> params = new HashMap<String, String>();

    private NetbarActivityInfo netbarActivityInfo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lengthCoding = UMengStatisticsUtil.CODE_2004;
        isModuleFragment = true;
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_internetbar_activity, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        context = getActivity();
        mNetbarInfo = (InternetBarInfo) getArguments().getSerializable("nerbar");
    }

    private void initData() {
        showLoading();
        if (mNetbarInfo != null) {
            params.clear();
            params.put("netbarId", mNetbarInfo.getId());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.NETBARINFO_ACTIVITY, params, HttpConstant.NETBARINFO_ACTIVITY);
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        if (object == null) {
            return;
        }
        if (object.has("object")) {
            if (method.equals(HttpConstant.NETBARINFO_ACTIVITY)) {
                try {
                    String strObj = object.getString("object");
                    netbarActivityInfo = GsonUtil.getBean(strObj, NetbarActivityInfo.class);
                    NetbarActivityAdapter adapter = new NetbarActivityAdapter(getActivity(), netbarActivityInfo, this);

                    List<YueZhan> matches = netbarActivityInfo.getMatches();
                    FullLinearLayoutManager mLayoutManager = new FullLinearLayoutManager(getContext());
                    mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    rvNetbarMatch.setLayoutManager(mLayoutManager);
                    rvNetbarMatch.setFocusable(false);
                    rvNetbarMatch.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
    }


    @Override
    public void onItemClick(Object object) {
        Intent intent;
        if (object == null) {
            if (WangYuApplication.getUser(getContext()) != null) {
                intent = new Intent(getContext(), ReleaseWarActivity.class);
                mNetbarInfo.setNetbar_name(mNetbarInfo.getName());
                intent.putExtra("netbar", mNetbarInfo);
                startActivity(intent);
            } else {
                intent = new Intent(getContext(), LoginActivity.class);
                startActivityForResult(intent, LoginActivity.LOGIN_OK);
                showToast("请先登录");
            }

        } else {
            if (object instanceof NetbarService) {
                intent = new Intent(getContext(), SubjectActivity.class);
                intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.NETBAR_SERVICE);
                intent.putExtra("id", ((NetbarService) object).getProperty_id() + "");
                intent.putExtra("netbarId", mNetbarInfo.getId());
                startActivity(intent);
            } else if (object instanceof NetBarAmuse) {
                intent = new Intent(getContext(), RecreationMatchDetailsActivity.class);
                intent.putExtra("id", ((NetBarAmuse) object).getId() + "");
                startActivity(intent);
            } else if (object instanceof YueZhan) {
                intent = new Intent(getContext(), YueZhanDetailsActivity.class);
                intent.putExtra("id", ((YueZhan) object).getId() + "");
                startActivity(intent);
            }
        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        LogUtil.e("onStart------------------FragmentInternetbarActivity-------------", "onStart");
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        LogUtil.e("onStop------------------FragmentInternetbarActivity-------------", "onStop");
//    }
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        LogUtil.e("onDestroy------------------FragmentInternetbarActivity-------------", "onDestroy");
//    }

    //
//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        LogUtil.e("onHiddenChanged------------------FragmentInternetbarActivity-------------",hidden+"");
//    }

}
