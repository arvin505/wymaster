package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.ImagePagerForInfoAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.AtlasDetail;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.ACache;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ShareToFriendsUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.ExpertMorePopupWindow;
import com.miqtech.master.client.watcher.Observerable;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.viewpagerindicator.HackyViewPager;
import com.viewpagerindicator.PageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * 资讯图集详情
 * Created by zhaosentao on 2015/11/28.
 */
public class InformationAtlasActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.pager)
    HackyViewPager pager;
    @Bind(R.id.indicator)
    PageIndicator mIndicator;
    @Bind(R.id.praise_iv_atlas)
    ImageView praise_iv;
    @Bind(R.id.praise_number_tv_atlas)
    TextView praise_tv;
    @Bind(R.id.collect_iv_atlas)
    ImageView collect_iv;
    @Bind(R.id.collect_number_tv_atlas)
    TextView collect_tv;
    @Bind(R.id.share_iv_atlas)
    ImageView share_iv;
    @Bind(R.id.title_tv_atlas)
    TextView title_tv;
    @Bind(R.id.content_tv_atlas)
    TextView content_tv;//内容
    @Bind(R.id.img_numbet_tv_atlas)
    TextView img_num_tv;//显示移动到哪页
    @Bind(R.id.back_atlas)
    ImageView back;
    @Bind(R.id.rl_atlas)
    RelativeLayout rl;
    @Bind(R.id.praise_ll_atlas)
    LinearLayout praise_ll;
    @Bind(R.id.collect_ll_atlas)
    LinearLayout collect_ll;
    @Bind(R.id.ll_commend)
    LinearLayout ll_commend;
    @Bind(R.id.tv_rednum)
    TextView tvRednum;


    private Observerable observerable = Observerable.getInstance();

    DisplayImageOptions options;
    private static final String STATE_POSITION = "STATE_POSITION";


    private ACache mCache;
    private String[] images;
    private String[] contentStr;


    private int totalNum;
    private User user;
    private int pagerPosition;
    private int current = 1;
    private int praiseInt;//0取消点赞成功 1点赞成功 -1 操作失败
    private int collectInt;//1表示收藏成功 0表示取消收藏成功 -1表示操作失败
    private String infoId;//资讯id
    private Context mContext;
    private AtlasDetail atlasDetail = new AtlasDetail();

    String sharetitle;
    String sharecontent;
    String shareurl;
    String imgurl;
    private ExpertMorePopupWindow popwin;
    private ShareToFriendsUtil shareToFriendsUtil;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        popwin = new ExpertMorePopupWindow(mContext, R.style.Dialog);
        popwin.setOnItemClick(itemOnClick);
        shareToFriendsUtil = new ShareToFriendsUtil(mContext, popwin);
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }
    }

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_information_atlas);
        ButterKnife.bind(this);
        mContext = InformationAtlasActivity.this;
        mCache = ACache.get(mContext);
        infoId = getIntent().getStringExtra("activityId");
        lengthCoding = UMengStatisticsUtil.CODE_1003;
        lengthTargetId = infoId + "";
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
//        showDetailData();
        setListenet();
        if (!Utils.isNetworkAvailable(InformationAtlasActivity.this)) {
            loadCahce();
            showToast(getResources().getString(R.string.noNeteork));
            return;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        loadDetailData();
    }

    private void setListenet() {
        back.setOnClickListener(this);
        collect_ll.setOnClickListener(this);
        share_iv.setOnClickListener(this);
        praise_ll.setOnClickListener(this);
        ll_commend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_atlas:
                onBackPressed();
                break;
            case R.id.praise_ll_atlas:
                praise();
                break;
            case R.id.collect_ll_atlas:
                info_fav();
                break;
            case R.id.share_iv_atlas:
                if (popwin != null) {
                    popwin.show();
                } else {
                    popwin = new ExpertMorePopupWindow(mContext, R.style.Dialog);
                    popwin.setOnItemClick(itemOnClick);
                    shareToFriendsUtil = new ShareToFriendsUtil(mContext, popwin);
                    popwin.show();
                }
                break;
            case R.id.ll_commend:
                Intent intent = new Intent();
                intent.setClass(this, CommentsSectionActivity.class);
                intent.putExtra("amuseId", atlasDetail.getId() + "");
                intent.putExtra("type", 3);
                startActivity(intent);
                break;
        }
    }

    /**
     * 获取详细数据
     */
    private void loadDetailData() {
        LogUtil.d(TAG,"loadDetailData");
        user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            //map.put("token", user.getToken());
        }
        if (TextUtils.isEmpty(infoId)) {
            showToast("id失效");
            return;
        }
        map.put("id", infoId + "");
       sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INFORMATION_DETAIL, map, HttpConstant.INFORMATION_DETAIL);
    }

    /**
     * 收藏或者取消
     */
    private void info_fav() {
        user = WangYuApplication.getUser(mContext);
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("infoId", infoId + "");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INFO_FAV, map, HttpConstant.INFO_FAV);
        } else {
            showToast(getResources().getString(R.string.pleaseLogin));
        }
    }

    /**
     * 点赞或者取消点赞
     */
    private void praise() {
        user = WangYuApplication.getUser(mContext);
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("infoId", infoId + "");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INFO_PRAISE, map, HttpConstant.INFO_PRAISE);
        } else {
            showToast(getResources().getString(R.string.pleaseLogin));
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        LogUtil.e(TAG, "object : " + object.toString());
        try {
            if (method.equals(HttpConstant.INFO_PRAISE)) {
                if ("0".equals(object.getString("code")) && object.has("object")) {
                    praiseInt = object.optInt("object");
                    if (praiseInt == 1) {//0取消点赞成功 1点赞成功 -1 操作失败
                        praise_iv.setImageResource(R.drawable.praise_yes);
                        praise_tv.setTextColor(getResources().getColor(R.color.orange));
                        if (atlasDetail.getPraised() == 1) {
                            praise_tv.setText(Utils.getnumberForms(atlasDetail.getPraiseNum(), mContext));
                        } else {
                            praise_tv.setText(Utils.getnumberForms(atlasDetail.getPraiseNum() + 1, mContext));
                        }
                    } else if (praiseInt == 0) {
                        if (atlasDetail.getPraised() == 1) {
                            praise_tv.setText(Utils.getnumberForms(atlasDetail.getPraiseNum() - 1, mContext));
                        } else {
                            praise_tv.setText(Utils.getnumberForms(atlasDetail.getPraiseNum(), mContext));
                        }
                        praise_iv.setImageResource(R.drawable.praise_no);
                        praise_tv.setTextColor(getResources().getColor(R.color.white));
                    } else if (praiseInt == -1) {
                        showToast(getResources().getString(R.string.operationFailure));
                    }
                }
            } else if (method.equals(HttpConstant.INFO_FAV)) {
                if ("0".equals(object.getString("code")) && object.has("object")) {
                    collectInt = object.optInt("object");//1表示收藏成功 0表示取消收藏成功 -1表示操作失败
                    if (collectInt == 1) {
                        collect_iv.setImageResource(R.drawable.collect_yes);
                        collect_tv.setTextColor(getResources().getColor(R.color.orange));
                        observerable.notifyChange(Observerable.ObserverableType.COLLECTSTATE, 3, atlasDetail.getId(), true);
                        if (atlasDetail.getFaved() == 1) {
                            collect_tv.setText(Utils.getnumberForms(atlasDetail.getFavNum(), mContext));
                        } else {
                            collect_tv.setText(Utils.getnumberForms(atlasDetail.getFavNum() + 1, mContext));
                        }
                    } else if (collectInt == 0) {
                        if (atlasDetail.getFaved() == 1) {
                            collect_tv.setText(Utils.getnumberForms(atlasDetail.getFavNum() - 1, mContext));
                        } else {
                            collect_tv.setText(Utils.getnumberForms(atlasDetail.getFavNum(), mContext));
                        }
                        collect_iv.setImageResource(R.drawable.collect_no);
                        collect_tv.setTextColor(getResources().getColor(R.color.white));
                        observerable.notifyChange(Observerable.ObserverableType.COLLECTSTATE, 3, atlasDetail.getId(), false);
                    } else if (collectInt == -1) {
                        showToast(getResources().getString(R.string.operationFailure));
                    }
                }
            } else if (method.equals(HttpConstant.INFORMATION_DETAIL)) {
                if ("0".equals(object.getString("code")) && object.has("object")) {
                    atlasDetail = GsonUtil.getBean(object.getJSONObject("object").getJSONObject("info").toString(), AtlasDetail.class);
                    showDetailData();
                    //mCache.put(HttpConstant.INFO_DETAIL + infoId, object);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
    }

    @Override
    protected void onDestroy() {
        observerable = null;
        if (shareToFriendsUtil != null) {
            if (shareToFriendsUtil.requestUtil != null) {
                shareToFriendsUtil.requestUtil.removeTag(shareToFriendsUtil.getClass().getName());
            }
            shareToFriendsUtil.requestUtil = null;
            shareToFriendsUtil = null;
        }
        super.onDestroy();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示请求到的详细数据
     */
    public void showDetailData() {
        if (atlasDetail.getImgs() == null) {
            return;
        }
        images = atlasDetail.getImgs().split(",");
        if (!TextUtils.isEmpty(atlasDetail.getIntroduces())) {
            contentStr = atlasDetail.getIntroduces().split("\\|\\|\\|");
        } else {
            contentStr = null;
        }
        totalNum = images.length;
        options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.default_img)
                .showImageOnFail(R.drawable.default_img).resetViewBeforeLoading(true).cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        pager.setAdapter(new ImagePagerForInfoAdapter(images, mContext, options));
        pager.setCurrentItem(0);

        tvRednum.setText(atlasDetail.getComments_num() + "");

        if (contentStr == null || contentStr.length == 0) {
            if (!TextUtils.isEmpty(atlasDetail.getRemark())) {
                content_tv.setText(atlasDetail.getRemark());
            } else {
                content_tv.setText("");
            }
        } else {
            if (TextUtils.isEmpty(contentStr[0])) {
                content_tv.setText("");
            } else {
                content_tv.setText(contentStr[0]);
            }
        }
        if (totalNum > 0) {
            img_num_tv.setText(current + "/" + totalNum);
        }
        mIndicator.setViewPager(pager);
        showNumber();

        if (!TextUtils.isEmpty(atlasDetail.getTitle())) {//显示标题
            title_tv.setText(atlasDetail.getTitle());
        } else {
            title_tv.setText("");
        }
//        if (!TextUtils.isEmpty(atlasDetail.getRemark())) {//显示内容
//            content_tv.setText(Html.fromHtml(atlasDetail.getRemark()));
//        } else {
//            content_tv.setText("");
//        }
        praise_tv.setText(atlasDetail.getPraiseNum() + "");//显示赞数
        collect_tv.setText(atlasDetail.getFavNum() + "");//显示收藏数

        if (atlasDetail.getFaved() == 1) {
            collect_iv.setImageResource(R.drawable.collect_yes);
            collect_tv.setTextColor(getResources().getColor(R.color.orange));
        } else {
            collect_iv.setImageResource(R.drawable.collect_no);
            collect_tv.setTextColor(getResources().getColor(R.color.white));
        }

        if (atlasDetail.getPraised() == 1) {
            praise_iv.setImageResource(R.drawable.praise_yes);
            praise_tv.setTextColor(getResources().getColor(R.color.orange));
        } else {
            praise_iv.setImageResource(R.drawable.praise_no);
            praise_tv.setTextColor(getResources().getColor(R.color.white));
        }

        if (!TextUtils.isEmpty(atlasDetail.getTitle())) {
            sharetitle = "网娱大师-电竞赛事独家报名,资讯直播" + atlasDetail.getTitle();
        } else {
            sharetitle = "网娱大师-电竞赛事独家报名,资讯直播";
        }
        sharecontent = atlasDetail.getBrief();
        shareurl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.OVER_URL + infoId;
        imgurl = HttpConstant.SERVICE_UPLOAD_AREA + images[0];

    }


    View.OnClickListener itemOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.llSina:
                    shareToFriendsUtil.shareBySina(sharetitle, sharecontent, shareurl, imgurl);
                    break;
                case R.id.llWeChat:
                    shareToFriendsUtil.shareWyByWXFriend(sharetitle, sharecontent, shareurl, imgurl, 0);
                    break;
                case R.id.llFriend:
                    shareToFriendsUtil.shareWyByWXFriend(sharetitle, sharecontent, shareurl, imgurl, 1);
                    break;
                case R.id.llQQ:
                    shareToFriendsUtil.shareByQQ(sharetitle, sharecontent, shareurl, imgurl);
                    break;
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, pager.getCurrentItem());
    }

    /**
     * 显示图片移到哪张了
     */
    private void showNumber() {
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current = position + 1;
                img_num_tv.setText(current + "/" + totalNum);
                if (contentStr != null && contentStr.length != 0) {
                    if (position < contentStr.length) {
                        content_tv.setText(contentStr[position]);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /**
     * 加载缓存
     */
    private void loadCahce() {
        InforAtlasCacheTask task = new InforAtlasCacheTask();
        task.execute();
        LogUtil.d(TAG,"loadCahce");
    }

    class InforAtlasCacheTask extends AsyncTask<Void, Void, Map<String, JSONObject>> {
        Map<String, JSONObject> map;

        @Override
        protected Map<String, JSONObject> doInBackground(Void... params) {
            map = new HashMap<>();
            JSONObject infor_detail = mCache.getAsJSONObject(HttpConstant.TOPIC_LIST + infoId);
            if (infor_detail != null) {
                map.put(HttpConstant.TOPIC_LIST + infoId, infor_detail);
            }
            return map;
        }

        @Override
        protected void onPostExecute(Map<String, JSONObject> stringJSONObjectMap) {
            super.onPostExecute(stringJSONObjectMap);
            for (Map.Entry<String, JSONObject> maps : stringJSONObjectMap.entrySet()) {
                if (maps.getKey().equals(HttpConstant.TOPIC_LIST + infoId)) {
                    try {
                        atlasDetail = GsonUtil.getBean(maps.getValue().getString("object"), AtlasDetail.class);
                        LogUtil.d(TAG,"loadCahce 成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    showDetailData();
                } else {
                    showToast("请检查网络连接");
                }
            }
        }
    }

}
