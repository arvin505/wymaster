package com.miqtech.master.client.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.FragmentInfomationPagerAdapter;
import com.miqtech.master.client.adapter.InfoTypeFilteAdapter;
import com.miqtech.master.client.entity.InforCatalog;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.ACache;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ScrollController;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.view.PagerSlidingTabStrip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/4/8.
 */
public class FragmentInformationV3 extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    @Bind(R.id.vp_tabs)
    PagerSlidingTabStrip tabStrip;
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.img_show_type_pop)
    ImageView imgPop;
    @Bind(R.id.view_pop_down_filter)
    View popDown;
    @Bind(R.id.view_info_alpha)
    View alphaView;
    @Bind(R.id.exception_page)
    LinearLayout llException;
    @Bind(R.id.tv_err_title)
    TextView tvError;
    @Bind(R.id.tvLeftTitle)
    TextView tvLeftTitle;

    private final String INFORMATION_TYPE = "information_type";

    List<BaseFragment> fragmentList = new ArrayList<>();

    private List<InforCatalog> tabs = new ArrayList<>();

    private PopupWindow mPopInfoType;
    private ACache mCache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lengthCoding = UMengStatisticsUtil.CODE_1000;
        isModuleFragment = true;
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_information_v3, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        llException.setVisibility(View.GONE);
        tvError.setText("呀,好像出现了一丢丢问题，点击刷新");
        mCache = ACache.get(getContext());
        getTabs();
        tvLeftTitle.setText(R.string.information);
    }

    /**
     * 获取资讯标签
     */
    private void getTabs() {
        //tabs = new String[]{"推荐", "LOL", "Dota2", "风暴英雄", "剑灵", "视频", "网易新闻", "百度干腾讯"};
        if (!once) {
            once = true;
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INFO_CATALOG, null, HttpConstant.INFO_CATALOG);
        }
    }


    public void dismissPop() {
        if (mPopInfoType != null && mPopInfoType.isShowing()) {
            mPopInfoType.dismiss();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        } else {
            dismissPop();

        }
    }

    /**
     * 初始化视图
     */
    private void setupView() {
        for (int i = 0; i < tabs.size(); i++) {
            InforCatalog tab = tabs.get(i);
            if (tab.getParent().getType() == 2) {
                FragmentInfoVideoItem fragment = FragmentInfoVideoItem.newInstance(i, tab);
                fragmentList.add(fragment);
            } else {
                FragmentInformationItem fragment = FragmentInformationItem.newInstance(i, tab);
                fragmentList.add(fragment);
            }
        }

        ScrollController.addViewPager(getClass().getSimpleName() + "_content", viewPager);
        FragmentInfomationPagerAdapter adapter = new FragmentInfomationPagerAdapter(getChildFragmentManager(), fragmentList, tabs);
        viewPager.setOffscreenPageLimit(tabs.size());
        viewPager.setAdapter(adapter);
        tabStrip.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
        imgPop.setOnClickListener(this);
        alphaView.setOnClickListener(this);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        LogUtil.e(TAG, "object : " + object.toString());
        llException.setVisibility(View.GONE);
        if (HttpConstant.INFO_CATALOG.endsWith(method)) {
            Gson gson = new Gson();
            try {
                tabs.clear();
                List<InforCatalog> data = gson.fromJson(object.getJSONArray("object").toString(), new TypeToken<List<InforCatalog>>() {
                }.getType());
                tabs.addAll(data);
                mCache.put(INFORMATION_TYPE, object.getJSONArray("object"));
                setupView();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        getCache();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_show_type_pop:  //资讯类别选择
                    showPopWindow();
                break;
            case R.id.img_close:
                mPopInfoType.dismiss();
                break;
            case R.id.view_info_alpha:
                mPopInfoType.dismiss();
                break;
            case R.id.exception_page:
                getTabs();
                break;
        }
    }

    private boolean isPopShowing = false;

    public boolean isPopShowing() {
        return isPopShowing;
    }

    /**
     *弹出筛选框
     */
    /**
     * 弹出popwindow
     */
    private void showPopWindow() {
        if (mPopInfoType == null) {
            mPopInfoType = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) {
                @Override
                public void dismiss() {
                    super.dismiss();
                    alphaView.setVisibility(View.GONE);
                    viewPager.setEnabled(true);
                    viewPager.setClickable(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           isPopShowing = false;
                        }
                    },500);
                }
            };
            mPopInfoType.setAnimationStyle(R.style.pop_anim);

            View popView = LayoutInflater.from(getContext()).inflate(R.layout.layout_video_type_pop, null);
            GridView gridView = (GridView) popView.findViewById(R.id.grid_info);
            InfoTypeFilteAdapter adapter = new InfoTypeFilteAdapter(getActivity(), tabs);
            gridView.setAdapter(adapter);
            ImageView imgClose = (ImageView) popView.findViewById(R.id.img_close);
            imgClose.setOnClickListener(this);

            gridView.setOnItemClickListener(this);
            mPopInfoType.setContentView(popView);
            mPopInfoType.setOutsideTouchable(true);

            ColorDrawable cd = new ColorDrawable(Color.TRANSPARENT);
            mPopInfoType.setBackgroundDrawable(cd);
        }
        alphaView.setVisibility(View.VISIBLE);
        mPopInfoType.showAsDropDown(popDown, 0, 0);
        viewPager.setEnabled(false);
        viewPager.setClickable(false);
        isPopShowing = true;
//        mPopInfoType.showAtLocation(llParent, Gravity.CENTER, 0, 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPopInfoType.dismiss();
        viewPager.setCurrentItem(position, true);
    }

    /**
     * get cache
     */
    private void getCache() {
        if (!once) {
            LoadCacheTask task = new LoadCacheTask();
            task.execute();
            once = true;
        }
    }

    private boolean once = false;

    class LoadCacheTask extends AsyncTask<Void, Void, List<InforCatalog>> {
        @Override
        protected List<InforCatalog> doInBackground(Void... params) {
            JSONArray jsonArray = mCache.getAsJSONArray(INFORMATION_TYPE);
            Gson gson = new Gson();
            try {
                List<InforCatalog> datas = gson.fromJson(jsonArray.toString(), new TypeToken<List<InforCatalog>>() {
                }.getType());
                return datas;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<InforCatalog> inforCatalogs) {
            super.onPostExecute(inforCatalogs);
            if (inforCatalogs != null && !inforCatalogs.isEmpty()) {
                tabs.clear();
                tabs.addAll(inforCatalogs);
                setupView();
            } else {
                llException.setVisibility(View.VISIBLE);
                llException.setOnClickListener(FragmentInformationV3.this);
            }
        }
    }
}