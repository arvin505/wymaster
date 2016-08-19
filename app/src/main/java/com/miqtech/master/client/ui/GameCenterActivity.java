package com.miqtech.master.client.ui;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.Game;
import com.miqtech.master.client.entity.GameInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.BannerPagerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/2.
 */
public class GameCenterActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvGame1, tvGame2, tvGame3, tvGame4, tvHotGame, tvHotGameDownloadCounts, tvHotGameDes,
            tvWeeklyBest, tv2LookAll;
    private ImageView ivGame1, ivGame2, ivGame3, ivGame4, ivHotGame, ivDownload1, ivDownload2, ivDownload3,
            ivDownload4;
    private LinearLayout llHotGame, llHotGames, llHot, llBanner, llGame1, llGame2, llGame3, llGame4;
    private RelativeLayout rlHotGame;
    private Button btnHotGameDownload;
    private GameInfo gameInfo;
    private BannerPagerView vpBanner;

    private List<Game> bannerGames;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_gamecenter);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        tvGame1 = (TextView) findViewById(R.id.tvGame1);
        tvGame2 = (TextView) findViewById(R.id.tvGame2);
        tvGame3 = (TextView) findViewById(R.id.tvGame3);
        tvGame4 = (TextView) findViewById(R.id.tvGame4);
        tvHotGame = (TextView) findViewById(R.id.tvHotGame);
        tvHotGameDes = (TextView) findViewById(R.id.tvHotGameDes);
        tvHotGameDownloadCounts = (TextView) findViewById(R.id.tvHotGameDownloadCounts);
        tvWeeklyBest = (TextView) findViewById(R.id.tvWeeklyBest);
        tv2LookAll = (TextView) findViewById(R.id.tv2LookAll);
        ivGame1 = (ImageView) findViewById(R.id.ivGame1);
        ivGame2 = (ImageView) findViewById(R.id.ivGame2);
        ivGame3 = (ImageView) findViewById(R.id.ivGame3);
        ivGame4 = (ImageView) findViewById(R.id.ivGame4);
        ivHotGame = (ImageView) findViewById(R.id.ivHotGame);
        ivDownload1 = (ImageView) findViewById(R.id.ivDownload1);
        ivDownload2 = (ImageView) findViewById(R.id.ivDownload2);
        ivDownload3 = (ImageView) findViewById(R.id.ivDownload3);
        ivDownload4 = (ImageView) findViewById(R.id.ivDownload4);
        llHotGame = (LinearLayout) findViewById(R.id.llHotGame);
        llHotGames = (LinearLayout) findViewById(R.id.llHotGames);
        rlHotGame = (RelativeLayout) findViewById(R.id.rlHotGame);
        btnHotGameDownload = (Button) findViewById(R.id.btnHotGameDownload);
        vpBanner = (BannerPagerView) findViewById(R.id.vpBanner);
        llBanner = (LinearLayout) findViewById(R.id.llBanner);
        llGame1 = (LinearLayout) findViewById(R.id.llGame1);
        llGame2 = (LinearLayout) findViewById(R.id.llGame2);
        llGame3 = (LinearLayout) findViewById(R.id.llGame3);
        llGame4 = (LinearLayout) findViewById(R.id.llGame4);
        // btnHotGameDownload.setOnTouchListener(this);

        getLeftBtn().setOnClickListener(this);
        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("手游中心");

        btnHotGameDownload.setOnClickListener(this);
        ivDownload1.setOnClickListener(this);
        ivDownload2.setOnClickListener(this);
        ivDownload3.setOnClickListener(this);
        ivDownload4.setOnClickListener(this);
        llHotGame.setOnClickListener(this);
        llGame1.setOnClickListener(this);
        llGame2.setOnClickListener(this);
        llGame3.setOnClickListener(this);
        llGame4.setOnClickListener(this);
        tv2LookAll.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        loadGames();
        loadBanner();
    }

    private void loadGames() {
        showLoading();
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.GAME_INDEX, null, HttpConstant.GAME_INDEX);
    }

    private void loadBanner() {
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.GAME_BANNER, null, HttpConstant.GAME_BANNER);
    }


    private void updateUI() {
        updateNewGameUI();
        updateWeekGameUI();
        addHotGameViews();
    }

    private void updateNewGameUI() {
        List<Game> newest = gameInfo.getNewest();
        if (newest != null) {
            int size = newest.size();
            if (size > 0) {
                tvGame1.setText(newest.get(0).getName());
                AsyncImage.loadRoundPhoto(this, HttpConstant.SERVICE_UPLOAD_AREA + newest.get(0).getIcon(), ivGame1);
            }
            if (size > 1) {
                tvGame2.setText(newest.get(1).getName());
                AsyncImage.loadRoundPhoto(this, HttpConstant.SERVICE_UPLOAD_AREA + newest.get(1).getIcon(), ivGame2);
            }
            if (size > 2) {
                tvGame3.setText(newest.get(2).getName());
                AsyncImage.loadRoundPhoto(this, HttpConstant.SERVICE_UPLOAD_AREA + newest.get(2).getIcon(), ivGame3);
            }
            if (size > 3) {
                tvGame4.setText(newest.get(3).getName());
                AsyncImage.loadRoundPhoto(this, HttpConstant.SERVICE_UPLOAD_AREA + newest.get(3).getIcon(), ivGame4);
            }
        }
    }

    private void updateWeekGameUI() {
        List<Game> weeklyHot = gameInfo.getWeeklyHot();
        if (weeklyHot != null) {
            if (weeklyHot.size() > 0) {
                // 获取控件宽度
                // 本周最佳宽度
                int tvWeeklyBestWidth = tvWeeklyBest.getWidth();
                // 布局宽度
                int rlHotWidth = rlHotGame.getWidth();
                // 字体大小
                int fontSize = (int) getResources().getDimensionPixelSize(R.dimen.text_size_normal);
                // 边距大小
                int marginWidth = (int) getResources().getDimensionPixelSize(R.dimen.weekly_view_margin);
                // 名称
                String strHotName = weeklyHot.get(0).getName();
                // 字数
                int strHotNameLength = strHotName.length();
                String fileSize = weeklyHot.get(0).getAndroid_file_size() + "";
                if (fileSize.contains(".")) {
                    String[] fileStr = fileSize.split("\\.");
                    btnHotGameDownload.setText(fileStr[0] + "M");
                } else {
                    btnHotGameDownload.setText(weeklyHot.get(0).getAndroid_file_size() + "M");
                }
                tvHotGame.setText(weeklyHot.get(0).getName());

                if (fontSize * strHotNameLength + tvWeeklyBestWidth + marginWidth * 4 > rlHotWidth) {
                    tvHotGame.setWidth(rlHotWidth - tvWeeklyBestWidth - marginWidth * 4);
                }
                AsyncImage.loadRoundPhoto(this, HttpConstant.SERVICE_UPLOAD_AREA + weeklyHot.get(0).getIcon(),
                        ivHotGame);
                tvHotGameDownloadCounts.setText(weeklyHot.get(0).getDownload_count() + "次");

                tvHotGameDes.setText(weeklyHot.get(0).getDes());
            }
        } else {
            llHotGame.setVisibility(View.GONE);
        }
    }

    private void addHotGameViews() {
        List<Game> games = gameInfo.getHot();
        if (games != null) {
            for (Game game : games) {
                addHotGameView(game);
            }
        }
    }

    private void addHotGameView(Game game) {
        View v = View.inflate(this, R.layout.layout_hotgame_item, null);
        TextView tvGame = (TextView) v.findViewById(R.id.tvGame);
        ImageView ivGame = (ImageView) v.findViewById(R.id.ivGame);
        TextView tvGameDes = (TextView) v.findViewById(R.id.tvGameDes);
        TextView tvGameDownloadCounts = (TextView) v.findViewById(R.id.tvGameDownloadCounts);
        Button btnGameDownload = (Button) v.findViewById(R.id.btnGameDownload);
        btnGameDownload.setOnClickListener(this);
        v.setOnClickListener(this);
        tvGame.setText(game.getName());
        AsyncImage.loadRoundPhoto(this, HttpConstant.SERVICE_UPLOAD_AREA + game.getIcon(), ivGame);
        tvGameDes.setText(game.getDes());
        tvGameDownloadCounts.setText(game.getDownload_count() + "次");
        String fileSize = game.getAndroid_file_size() + "";
        if (fileSize.contains(".")) {
            String[] fileStr = fileSize.split("\\.");
            btnGameDownload.setText(fileStr[0] + "M");
        } else {
            btnGameDownload.setText(game.getAndroid_file_size() + "M");
        }
        v.setTag(game);
        btnGameDownload.setTag(game);
        llHotGames.addView(v);
    }


    private void updatePager() {
        initDots(bannerGames.size());

        ViewPagerAdapter viewAdapter = new ViewPagerAdapter();
        vpBanner.setAdapter(viewAdapter);
        vpBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                for (int i = 0; i < llBanner.getChildCount(); i++) {
//					if (i == arg0) {
//						llBanner.getChildAt(i).setSelected(true);
//					} else {
                    llBanner.getChildAt(i).setSelected(false);
//					}
                }
                System.out.println("--------------------------->>>" + (arg0 % llBanner.getChildCount()));
                llBanner.getChildAt(arg0 % llBanner.getChildCount()).setSelected(true);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        vpBanner.startAutoCycle();
//		 vpBanner.setCurrentItem(bannerGames.size()*20-1);
    }

    private void initDots(int count) {
        for (int j = 0; j < count; j++) {
            llBanner.addView(initDot());
        }
        llBanner.getChildAt(0).setSelected(true);
    }

    private View initDot() {
        return View.inflate(this, R.layout.boot_dot, null);
    }

    class ViewPagerAdapter extends PagerAdapter {

        private List<View> views = new ArrayList<View>();

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // container.addView(data.get(position));
            View view = View.inflate(GameCenterActivity.this, R.layout.advertisement_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.centent_iv);
            TextView textview = (TextView) view.findViewById(R.id.centent_text);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            AsyncImage.loadPhoto(GameCenterActivity.this,
                    HttpConstant.SERVICE_UPLOAD_AREA + bannerGames.get(position % bannerGames.size()).getCover(),
                    imageView);

            // View view = data.get(position % data.size());
            if (view.getParent() != null) {
                ViewGroup parent = (ViewGroup) view.getParent();
                parent.removeView(view);
            }
            views.add(view);
            ((ViewPager) container).addView(view, 0);
            imageView.setTag(bannerGames.get(position % bannerGames.size()));
            imageView.setOnClickListener(GameCenterActivity.this);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position % views.size()));
        }

    }

    private void getDownloadUrl(int gameId) {
        showLoading();
        Map<String, String> map = new HashMap<>();
        map.put("id", gameId + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.GAME_DOWNLOAD, map, HttpConstant.GAME_DOWNLOAD);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        Object obj = null;
        try {
            if (0 == object.getInt("code") && object.has("object")) {
                obj = object.getString("object");
            }
            if (method.equals(HttpConstant.GAME_INDEX)) {
                gameInfo = new Gson().fromJson(obj.toString(), GameInfo.class);
                if (gameInfo != null) {
                    updateUI();
                } else {
                    showToast("数据获取错误，请重试");
                }
            } else if (method.equals(HttpConstant.GAME_BANNER)) {
                bannerGames = new Gson().fromJson(obj.toString(), new TypeToken<List<Game>>() {
                }.getType());
                updatePager();
            } else if (method.equals(HttpConstant.GAME_DOWNLOAD)) {
                LogUtil.e("url", "url == " + obj.toString());
                JSONObject jsonObj = new JSONObject(obj.toString());
                String url_android = jsonObj.getString("url_android");
                Intent intent = new Intent();
                intent.setClass(this, SubjectActivity.class);
                intent.putExtra("download_url", url_android);
                intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.DOWNLOADGAME);
                startActivity(intent);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
        showToast(errMsg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnHotGameDownload:
                int gameId = gameInfo.getWeeklyHot().get(0).getId();
                getDownloadUrl(gameId);
                break;
            case R.id.ivDownload1:
                List<Game> newest = gameInfo.getNewest();
                if (newest != null) {
                    if (newest.size() > 0) {
                        int newestId = newest.get(0).getId();
                        getDownloadUrl(newestId);
                    }
                }
                break;
            case R.id.ivDownload2:
                if (gameInfo != null && !gameInfo.getNewest().isEmpty()) {
                    newest = gameInfo.getNewest();
                    if (newest.size() > 1) {
                        int newestId = newest.get(1).getId();
                        getDownloadUrl(newestId);
                    }
                }
                break;
            case R.id.ivDownload3:
                if (gameInfo != null && !gameInfo.getNewest().isEmpty()) {
                    newest = gameInfo.getNewest();
                    if (newest.size() > 2) {
                        int newestId = newest.get(2).getId();
                        getDownloadUrl(newestId);
                    }
                }
                break;
            case R.id.ivDownload4:
                if (gameInfo != null && !gameInfo.getNewest().isEmpty()) {
                    newest = gameInfo.getNewest();
                    if (newest.size() > 3) {
                        int newestId = newest.get(3).getId();
                        getDownloadUrl(newestId);
                    }
                }
                break;
            case R.id.llHotGameView:
                Game game = (Game) v.getTag();
                Intent intent = new Intent();
                intent.putExtra("id", game.getId());
                intent.setClass(this, GameDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.btnGameDownload:
                game = (Game) v.getTag();
                getDownloadUrl(game.getId());
                break;
            case R.id.llHotGame:
                intent = new Intent();
                intent.putExtra("id", gameInfo.getWeeklyHot().get(0).getId());
                intent.setClass(this, GameDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.llGame1:
                if (gameInfo != null && !gameInfo.getNewest().isEmpty()) {
                    newest = gameInfo.getNewest();
                    if (newest.size() > 0) {
                        int newestId = newest.get(0).getId();
                        intent = new Intent();
                        intent.putExtra("id", newestId);
                        intent.setClass(this, GameDetailActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.llGame2:
                if (gameInfo != null && !gameInfo.getNewest().isEmpty()) {
                    newest = gameInfo.getNewest();
                    if (newest.size() > 1) {
                        int newestId = newest.get(1).getId();
                        intent = new Intent();
                        intent.putExtra("id", newestId);
                        intent.setClass(this, GameDetailActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.llGame3:
                if (gameInfo != null && !gameInfo.getNewest().isEmpty()) {
                    newest = gameInfo.getNewest();
                    if (newest.size() > 2) {
                        int newestId = newest.get(2).getId();
                        intent = new Intent();
                        intent.putExtra("id", newestId);
                        intent.setClass(this, GameDetailActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.llGame4:
                if (gameInfo != null && !gameInfo.getNewest().isEmpty()) {
                    newest = gameInfo.getNewest();
                    if (newest.size() > 3) {
                        int newestId = newest.get(3).getId();
                        intent = new Intent();
                        intent.putExtra("id", newestId);
                        intent.setClass(this, GameDetailActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.centent_iv:
                game = (Game) v.getTag();
                intent = new Intent();
                intent.putExtra("id", game.getId());
                intent.setClass(this, GameDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.tv2LookAll:
                intent = new Intent();
                intent.setClass(this, GameListActivity.class);
                startActivity(intent);
                break;
            case R.id.ibLeft:
                onBackPressed();
                break;
            default:
                break;
        }
    }
}
