package com.miqtech.master.client.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.FragmentPagerAdpter;
import com.miqtech.master.client.adapter.SearchEditTextAdapter;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.basefragment.SearchBaseFragment;
import com.miqtech.master.client.ui.fragment.FragmentActivitySearch;
import com.miqtech.master.client.ui.fragment.FragmentNetbarSearch;
import com.miqtech.master.client.ui.fragment.FragmentUserSearch;


import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/12/28.
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.et_search)
    AutoCompleteTextView etSearch;
    @Bind(R.id.back)
    ImageView btCancle;
    @Bind(R.id.vp_search_fragment)
    ViewPager vpSearch;

    @Bind(R.id.netbarBottom)
    TextView tvnetbarBottom;
    @Bind(R.id.activityBottom)
    TextView tvActivityBottom;
    @Bind(R.id.userBottom)
    TextView tvUserBottom;

    @Bind(R.id.tv_search_activity)
    TextView tvSearchActivity;   //y
    @Bind(R.id.tv_Search_netbar)
    TextView tvSearchNetbar;
    @Bind(R.id.tv_search_user)
    TextView tvSearchUser;

    @Bind(R.id.im_clean)
    ImageView imClean;

    FragmentPagerAdpter adapter;
    private SearchEditTextAdapter searchAdapter;

    private List<TextView> tvList = new ArrayList<>();
    private List<View> lineList = new ArrayList<>();

    boolean shouldChange = true;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initView();
    }

    private void search(String key) {
        //if ()
    }

    public void initView() {
        lineList.add(tvnetbarBottom);
        lineList.add(tvActivityBottom);
        lineList.add(tvUserBottom);

        tvList.add(tvSearchNetbar);
        tvList.add(tvSearchActivity);
        tvList.add(tvSearchUser);

        tvSearchActivity.setOnClickListener(this);
        tvSearchNetbar.setOnClickListener(this);
        tvSearchUser.setOnClickListener(this);
        imClean.setOnClickListener(this);

        adapter = new FragmentPagerAdpter(this);
        adapter.addTab(FragmentNetbarSearch.class, null);
        adapter.addTab(FragmentActivitySearch.class, null);
        adapter.addTab(FragmentUserSearch.class, null);
        vpSearch.setAdapter(adapter);
        searchAdapter = new SearchEditTextAdapter(this);
        etSearch.setAdapter(searchAdapter);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //search
                    //showToast(etSearch.getText().toString());
                    //PreferencesUtil.saveHistory(SearchActivity.this, etSearch.getText().toString());
                    /*searchAdapter.initData();
                    searchAdapter.notifyDataSetChanged();*/

                    //掉起fragment搜索界面
                    int index = vpSearch.getCurrentItem();
                    SearchBaseFragment fragment = (SearchBaseFragment) adapter.getItem(index);
                    fragment.search(etSearch.getText().toString());
                } else {

                }
                return false;
            }
        });
        btCancle.setOnClickListener(this);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int index = vpSearch.getCurrentItem();
                SearchBaseFragment fragment = (SearchBaseFragment) adapter.getItem(index);
                if (!fragment.searchIng && shouldChange) {
                    fragment.changeText(etSearch.getText().toString());
                }
                if (etSearch.getText().length() > 0) {
                    imClean.setVisibility(View.VISIBLE);
                } else {
                    imClean.setVisibility(View.GONE);
                }
                setSearchAdapter(index);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        vpSearch.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setViewPagerSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(etSearch.getWindowToken(),0);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.tv_search_activity:
                setViewPagerSelect(1);
                vpSearch.setCurrentItem(1, true);
                break;
            case R.id.tv_Search_netbar:
                setViewPagerSelect(0);
                vpSearch.setCurrentItem(0, true);
                break;
            case R.id.tv_search_user:
                setViewPagerSelect(2);
                vpSearch.setCurrentItem(2, true);
                break;
            case R.id.im_clean:
                etSearch.setText("");
                break;

        }
    }

    private void setViewPagerSelect(int position) {
        for (int i = 0; i < tvList.size(); i++) {
            if (i != position) {
                tvList.get(i).setTextColor(getResources().getColor(R.color.colorActionBarUnSelected));
                lineList.get(i).setVisibility(View.GONE);
            }
        }
        if (position == 0) {
            //tvCity.setVisibility(View.GONE);
        } else {
            // tvCity.setVisibility(View.VISIBLE);
        }
        shouldChange = false;
        etSearch.setText("");
        shouldChange = true;
        tvList.get(position).setTextColor(getResources().getColor(R.color.colorActionBarSelected));
        lineList.get(position).setVisibility(View.VISIBLE);


    }

    public void setEtSearch(String key) {
        etSearch.setText(key);
    }

    public void setSearchAdapter(int type){
        searchAdapter.initData(type);
        searchAdapter.notifyDataSetChanged();
    }
}
