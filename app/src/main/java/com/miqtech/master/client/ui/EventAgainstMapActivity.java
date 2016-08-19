package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.EventAgainst;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.BitmapUtil;
import com.miqtech.master.client.view.EventAgainstLinearLayout;
import com.miqtech.master.client.view.HVScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 赛事对阵图
 * Created by zhaosentao on 2016/7/12.
 */
public class EventAgainstMapActivity extends BaseActivity implements HVScrollView.isHVScrollViewMobile {

    @Bind(R.id.eventAgainstMapTvTitle)
    TextView tvTitle;
    @Bind(R.id.eventAgainstMapLlClose)
    LinearLayout llClose;
    @Bind(R.id.evemtAgainstMapLlAgainst)
    EventAgainstLinearLayout llAgainst;
    @Bind(R.id.eventAgainstViewStub)
    ViewStub viewStub;
    @Bind(R.id.eventAgainstBg)
    ImageView imageView;//背景图，那个奖杯
    @Bind(R.id.eventAgainstMapFyTitle)
    FrameLayout eventAgainstMapFyTitle;
    @Bind(R.id.hvScrollView)
    HVScrollView hvScrollView;
    @Bind(R.id.evemtAgainstMapWhite)
    View evemtAgainstMapWhite;//标题下的白色部分，当列数超过3个是隐藏掉

    private View errorView;
    private Context context;
    private User user;
    private String roundId;//赛事场次id
    private String turn;//轮次
    private ArrayList<EventAgainst> eventAgainstList = new ArrayList<EventAgainst>();
    private String title = "";
    private Bitmap bitmap;//标题栏背景
    private Bitmap newbitmap;//标题栏背景模糊的图片

    @Override
    protected void init() {
        super.init();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_event_against_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//变横屏
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        context = this;
        eventAgainstList = getIntent().getParcelableArrayListExtra("eventAgainstList");
        title = getIntent().getStringExtra("title");//赛事标题
        if (eventAgainstList != null && !eventAgainstList.isEmpty()) {//有数据时直接显示，没有则去请求网络加载
            dealWithData(eventAgainstList, getIntent().getStringExtra("imgUrl").toString());
        } else {
            roundId = getIntent().getStringExtra("roundId");//赛事场次id
            turn = getIntent().getStringExtra("turn");//轮次
            if (!TextUtils.isEmpty(roundId)) {
                loadData();
            } else {
                showEmpty(1);
            }
        }
        //显示标题
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        } else {
            tvTitle.setText("");
        }
        hvScrollView.setIsHVScrollViewMobile(this);
    }

    @OnClick({R.id.eventAgainstMapLlClose})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.eventAgainstMapLlClose:
                onBackPressed();
                break;
        }
    }

    private void loadData() {
        showLoading();
        user = WangYuApplication.getUser(context);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
        }
        map.put("roundId", roundId);
        if (!TextUtils.isEmpty(turn)) {
            map.put("turn", turn);
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.EVENT_PROCESS_LIST, map, HttpConstant.EVENT_PROCESS_LIST);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        try {
            if (method.equals(HttpConstant.EVENT_PROCESS_LIST)) {
                if (object.has("object")) {
                    JSONObject jsonObject = new JSONObject(object.getString("object").toString());
                    ArrayList<EventAgainst> newEventAgainst = new Gson().fromJson(jsonObject.getString("process").toString(), new TypeToken<List<EventAgainst>>() {
                    }.getType());
                    dealWithData(newEventAgainst, jsonObject.optString("poster"));
                    if (newEventAgainst.isEmpty()) {
                        showEmpty(1);
                    }
                }
            }
        } catch (JSONException e) {
            showEmpty(1);
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        showEmpty(2);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        showEmpty(1);
    }

    /**
     * 处理数据
     *
     * @param eventAgainsts 对阵图数据f
     * @param imgUrl        标题栏图片地址
     */
    private void dealWithData(ArrayList<EventAgainst> eventAgainsts, String imgUrl) {
        if (!eventAgainsts.isEmpty() && eventAgainsts.size() < 4) {//是否显示标题栏下的白色区域
            evemtAgainstMapWhite.setVisibility(View.VISIBLE);
        } else {
            evemtAgainstMapWhite.setVisibility(View.GONE);
        }
        llAgainst.setData(eventAgainsts);
        showBackground(imgUrl);
    }

    /**
     * @param showErrorType 显示错误的显示方式，0表示正常,1表示无该进程，2表示网络错误
     */
    private void showEmpty(int showErrorType) {
        if (errorView == null) {
            viewStub.setLayoutResource(R.layout.exception_page);
            errorView = viewStub.inflate();
            TextView textView = (TextView) errorView.findViewById(R.id.tv_err_title);
            if (showErrorType == 1) {
                textView.setText(context.getResources().getString(R.string.the_event_process_no));
            } else if (showErrorType == 2) {
                textView.setText(context.getResources().getString(R.string.error_network));
            }
            imageView.setVisibility(View.GONE);
        }
    }

    private void showBackground(String imgUrl) {
        if (!imgUrl.isEmpty()) {
            bitmap = ImageLoader.getInstance().loadImageSync(HttpConstant.SERVICE_UPLOAD_AREA + imgUrl);
            if (bitmap == null) {
                return;
            }
            newbitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth() / 4, bitmap.getHeight() / 4);
            eventAgainstMapFyTitle.setBackgroundDrawable(new BitmapDrawable(BitmapUtil.fastblur(newbitmap, 25)));
            eventAgainstMapFyTitle.getBackground().setAlpha(100);
        }
    }

    @Override
    public void isMobile(boolean isMobile) {
        if (evemtAgainstMapWhite.getVisibility() == View.VISIBLE && isMobile) {
            evemtAgainstMapWhite.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
        }
        if (newbitmap != null) {
            newbitmap.recycle();
        }
    }
}

