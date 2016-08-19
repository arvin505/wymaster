package com.miqtech.master.client.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.FragmentPagerAdpter;
import com.miqtech.master.client.adapter.WeekHotAdapter;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.fragment.FragmentHotSort;
import com.miqtech.master.client.ui.fragment.FragmentNewGame;
import com.miqtech.master.client.ui.fragment.FragmentWeekHot;
import com.miqtech.master.client.view.SearchGame;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/2.
 */
public class GameListActivity extends BaseActivity implements
        ViewPager.OnPageChangeListener, View.OnClickListener {
    private Context context;
    private ViewPager gameListPager;
    private LinearLayout llWeekHot;
    private LinearLayout llNewGame;
    private LinearLayout llHotSort;
    private TextView tvWeekHot;
    private TextView tvNewGame;
    private TextView tvHotSort;
    private ImageView imgWeekHot;
    private ImageView imgNewGame;
    private ImageView imgHotSort;
    private ImageView ivSearchModle;
    private SearchGame searchGame;
    private WeekHotAdapter.DownLoadListen listen = new WeekHotAdapter.DownLoadListen() {
        @Override
        public void onDownload(int id) {
            getDownloadUrl(id);
        }
    };

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_game_list);
        context = this;
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setLeftIncludeTitle("热门排行榜");
        getLeftBtn().setOnClickListener(this);
        setLeftBtnImage(R.drawable.back);

        gameListPager = (ViewPager) findViewById(R.id.gameListPager);

        llWeekHot = (LinearLayout) findViewById(R.id.llWeekHot);
        llNewGame = (LinearLayout) findViewById(R.id.llNewGame);
        llHotSort = (LinearLayout) findViewById(R.id.llHotSort);

        tvWeekHot = (TextView) findViewById(R.id.tvWeekHot);
        tvNewGame = (TextView) findViewById(R.id.tvNewGame);
        tvHotSort = (TextView) findViewById(R.id.tvHotSort);

        imgWeekHot = (ImageView) findViewById(R.id.img_WeekHot_select);
        imgNewGame = (ImageView) findViewById(R.id.img_NewGame_select);
        imgHotSort = (ImageView) findViewById(R.id.img_HotSort_select);

        ivSearchModle = (ImageView) findViewById(R.id.ibRight);
        ivSearchModle.setImageResource(R.drawable.icon_search);
        ivSearchModle.setVisibility(View.VISIBLE);

        FragmentPagerAdpter adpter = new FragmentPagerAdpter(this);
        adpter.addTab(FragmentWeekHot.class, null);
        adpter.addTab(FragmentNewGame.class, null);
        adpter.addTab(FragmentHotSort.class, null);
        gameListPager.setAdapter(adpter);
        gameListPager.setOnPageChangeListener(this);
        getLeftBtn().setOnClickListener(this);

        ivSearchModle.setOnClickListener(this);

        llWeekHot.setOnClickListener(this);
        llNewGame.setOnClickListener(this);
        llHotSort.setOnClickListener(this);

        tvWeekHot.setTextColor(getResources().getColor(R.color.blue_gray));
        tvNewGame.setTextColor(getResources().getColor(R.color.gray));
        tvHotSort.setTextColor(getResources().getColor(R.color.gray));

        imgWeekHot.setVisibility(View.VISIBLE);
        imgNewGame.setVisibility(View.GONE);
        imgHotSort.setVisibility(View.GONE);

        searchGame = new SearchGame(this, R.style.searchStyle);
    }

    private void getDownloadUrl(int gameId) {
        showLoading();
        Map<String, String> map = new HashMap<>();
        map.put("id", gameId + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.GAME_DOWNLOAD, map, HttpConstant.GAME_DOWNLOAD);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.llWeekHot:
                gameListPager.setCurrentItem(0);
                setTitleStatus();
                break;
            case R.id.llNewGame:
                gameListPager.setCurrentItem(1);
                setTitleStatus();
                break;
            case R.id.llHotSort:
                gameListPager.setCurrentItem(2);
                setTitleStatus();
                break;
            case R.id.ibRight:
                searchGame.show();
                break;
        }
    }

    private void setTitleStatus() {
        if (gameListPager.getCurrentItem() == 0) {
            tvWeekHot.setTextColor(getResources().getColor(R.color.orange));
            tvNewGame.setTextColor(getResources().getColor(R.color.gray));
            tvHotSort.setTextColor(getResources().getColor(R.color.gray));

            imgWeekHot.setVisibility(View.VISIBLE);
            imgNewGame.setVisibility(View.GONE);
            imgHotSort.setVisibility(View.GONE);
        } else if (gameListPager.getCurrentItem() == 1) {
            tvWeekHot.setTextColor(getResources().getColor(R.color.gray));
            tvNewGame.setTextColor(getResources().getColor(R.color.orange));
            tvHotSort.setTextColor(getResources().getColor(R.color.gray));

            imgWeekHot.setVisibility(View.GONE);
            imgNewGame.setVisibility(View.VISIBLE);
            imgHotSort.setVisibility(View.GONE);
        } else {
            tvWeekHot.setTextColor(getResources().getColor(R.color.gray));
            tvNewGame.setTextColor(getResources().getColor(R.color.gray));
            tvHotSort.setTextColor(getResources().getColor(R.color.orange));

            imgWeekHot.setVisibility(View.GONE);
            imgNewGame.setVisibility(View.GONE);
            imgHotSort.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        setTitleStatus();
    }
}
