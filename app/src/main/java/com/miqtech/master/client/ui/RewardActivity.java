package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.Reward;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.ui.basefragment.RewardBaseFragment;
import com.miqtech.master.client.ui.fragment.FragmentRewardComment;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.BitmapUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.utils.ShareToFriendsUtil;
import com.miqtech.master.client.view.ExpertMorePopupWindow;
import com.miqtech.master.client.view.MyAlertView;
import com.miqtech.master.client.view.snapscrollview.McoyDownPage;
import com.miqtech.master.client.view.snapscrollview.McoyUpPage;
import com.miqtech.master.client.view.snapscrollview.McoySnapPageLayout;
import com.miqtech.master.client.watcher.Observerable;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.constant.WBConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 悬赏令
 * Created by zhaosentao on 2016/7/22.
 */
public class RewardActivity extends BaseActivity implements McoyUpPage.GetRewardCommentId, Observerable.ISubscribe,
        IWeiboHandler.Response {

    @Bind(R.id.mcoySnapPageLayout)
    McoySnapPageLayout mcoySnapPageLayout;
    @Bind(R.id.ViewStub)
    ViewStub viewStub;
    @Bind(R.id.guideLl)
    LinearLayout guideLl;

    private Context context;
    private User user;
    private McoyUpPage mcoyUpPage;
    private McoyDownPage mcoyDownPage;
    private View upView;
    private View downView;
    private View errorView;
    private TextView textView;

    private Observerable observerable = Observerable.getInstance();
    private boolean isToLogin = false;//是否去登陆过

    private int position = 0;//每一个悬赏令对应在fragmentlist中的位置
    private FragmentTransaction transaction;
    private List<RewardBaseFragment> fragmentList = new ArrayList<RewardBaseFragment>();
    private Map<Integer, Integer> integerMap = new HashMap<>();
    private MyBaseFragment commentBaseFragment;

    private List<Reward> rewardList = new ArrayList<>();
    private int rewardPosition = 0;//悬赏令在数据集中的位置
    private Reward bean;
    private int rewardId = -1;//悬赏令id，每次卡片翻页都是刷新
    private String isEnd = "-1";//0表示进行中。1表示已结束
    private int id;//悬赏令id，前一个页面传过来的

    private MyAlertView ruleDialog;//规则弹窗
    private ShareToFriendsUtil shareToFriendsUtil;//分享
    private ExpertMorePopupWindow popwin;//分享弹框

    private Bitmap bitmap;
    private List<Drawable> drawableList = new ArrayList<>();//保存处理过的图片
    private int isOne = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSinaSso(savedInstanceState);
    }

    public void initSinaSso(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            shareToFriendsUtil.getIWeiApiInstance(this).handleWeiboResponse(getIntent(), this);
        }
    }

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_reward);
        ButterKnife.bind(this);
        context = this;
        isEnd = getIntent().getStringExtra("isEnd");
        id = getIntent().getIntExtra("rewardId", -1);
        isOne = getIntent().getIntExtra("isOne", -1);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        upView = LayoutInflater.from(context).inflate(R.layout.layout_reward_up_page, null);
        downView = LayoutInflater.from(context).inflate(R.layout.layout_reward_down_page, null);

        mcoyUpPage = new McoyUpPage(context, upView, mcoySnapPageLayout, isOne);
        mcoyDownPage = new McoyDownPage(context, downView);

        mcoySnapPageLayout.setSnapPages(mcoyUpPage, mcoyDownPage);

        mcoyUpPage.setOnGetRewardCommentId(this);
        observerable.subscribe(Observerable.ObserverableType.REWARD_COMMENT, this);

        popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
        popwin.setOnItemClick(itemOnClick);
        shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);

        loadData();
        isShowGuide();
    }

    @Override
    public void getRewardCommentId(final int id, int position) {
        this.rewardId = id;
        this.rewardPosition = position;
        mcoySnapPageLayout.setPageSnapListener(new McoySnapPageLayout.PageSnapedListener() {
            @Override
            public void onSnapedCompleted(int derection) {
                if (derection == 1) {//卡片滑到评论
                    setOnShowComment(id);
                    mcoySnapPageLayout.setOnRecyclerviewIsTop(true);
                } else if (derection == -1) {//评论滑到卡片
                    if (isToLogin) {
                        loadData();
                    }
                    if (!fragmentList.isEmpty() &&
                            !integerMap.isEmpty() &&
                            integerMap.containsKey(rewardId) &&
                            fragmentList.get(integerMap.get(rewardId)) != null) {
                        fragmentList.get(integerMap.get(rewardId)).hideCommentView();
                    }
                }
            }
        });
    }

    @Override
    public void isFavReward(Reward bean) {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            this.bean = bean;
            submitIsFav(bean.getId());
        } else {
            toLogin();
        }
    }

    @Override
    public void showBackground(String imgUrl, final LinearLayout linearLayout) {
        if (imgUrl == null) {
            return;
        }

        if (!drawableList.isEmpty() && rewardPosition < drawableList.size() && drawableList.get(rewardPosition) != null) {
            linearLayout.setBackgroundDrawable(drawableList.get(rewardPosition));
            linearLayout.getBackground().setAlpha(77);
        } else {
            AsyncImage.loadNetPhotoWithListener(HttpConstant.SERVICE_UPLOAD_AREA + imgUrl + "!small", new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (bitmap == null) {
                        return;
                    }
                    Drawable drawable = new BitmapDrawable(BitmapUtil.fastblur(bitmap, 25));
                    linearLayout.setBackgroundDrawable(drawable);
                    linearLayout.getBackground().setAlpha(77);
                    drawableList.add(drawable);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
        }
    }

    /**
     * 提交是否收藏
     *
     * @param bountyId 悬赏令id
     */
    private void submitIsFav(int bountyId) {
        user = WangYuApplication.getUser(context);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("bountyId", bountyId + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.BOUNTY_FAV, map, HttpConstant.BOUNTY_FAV);
    }

    /**
     * 请求悬赏令列表
     */
    private void loadData() {
        showLoading();
        user = WangYuApplication.getUser(context);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
        }
        map.put("infoCount", "0");
        map.put("page", "1");
        map.put("pageSize", "30");
        map.put("isEnd", isEnd);//	0-正在进行中 1-已经结束  （只要正在进行中的）
        if ((isEnd.equals("1") || isOne == 1) && id != -1) {
            map.put("bountyId", id + "");
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.BOUNTY_LIST, map, HttpConstant.BOUNTY_LIST);
    }

    /**
     * 请求悬赏令规则
     */
    private void getRewardRule() {
        showLoading();
        user = WangYuApplication.getUser(context);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
        }
        map.put("bountyId", rewardId + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.BOUNTY_RULE, map, HttpConstant.BOUNTY_RULE);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        try {
            JSONObject jsonObject = object.getJSONObject("object");
            if (method.equals(HttpConstant.BOUNTY_LIST)) {//悬赏令列表

                List<Reward> newRewardList = new Gson().fromJson(jsonObject.getString("list").toString(), new TypeToken<List<Reward>>() {
                }.getType());

                if (newRewardList.isEmpty()) {
                    showEmpty(1);
                } else {
                    refreshData(newRewardList);
                }
                isToLogin = false;

            } else if (method.equals(HttpConstant.BOUNTY_FAV)) {//悬赏令收藏
                if (jsonObject.has("has_favor") && !TextUtils.isEmpty(jsonObject.getString("has_favor"))) {
                    if (0 == jsonObject.getInt("has_favor")) {//取消收藏
                        int num = bean.getFavNum();
                        bean.setFavNum(num - 1);
                        bean.setHas_favor(0);
                    } else if (1 == jsonObject.getInt("has_favor")) {//收藏成功
                        int num = bean.getFavNum();
                        bean.setFavNum(num + 1);
                        bean.setHas_favor(1);
                    }
                    mcoyUpPage.showFavNum(bean);
                }
            } else if (method.equals(HttpConstant.BOUNTY_RULE)) {//悬赏令规则
                showRuleDialog(jsonObject.getString("rule"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        if (method.equals(HttpConstant.BOUNTY_LIST)) {
            showEmpty(2);
        } else if (method.equals(HttpConstant.BOUNTY_FAV)) {
            showToast(getResources().getString(R.string.noNeteork));
        } else if (method.equals(HttpConstant.BOUNTY_RULE)) {
            showToast(getResources().getString(R.string.noNeteork));
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        try {
            if (method.equals(HttpConstant.BOUNTY_LIST)) {
                if (object.has("code") && object.getInt("code") == -4) {
                    toLogin();
                } else if (object.has("code") && object.getInt("code") == -1) {

                } else {
                    showEmpty(1);
                }
            } else if (method.equals(HttpConstant.BOUNTY_FAV)) {
                showToast(getResources().getString(R.string.fav_fiald));
            } else if (method.equals(HttpConstant.BOUNTY_RULE)) {
                showToast(getResources().getString(R.string.noNeteork));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void toLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * 显示对应的评论
     *
     * @param id 悬赏令id
     */
    public void setOnShowComment(int id) {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        RewardBaseFragment baseFragment;
        for (MyBaseFragment fragment : fragmentList) {
            if (null != fragment) {
                transaction.hide(fragment);
            }
        }

        if (integerMap.containsKey(id)) {
            baseFragment = fragmentList.get(integerMap.get(id));
            if (baseFragment == null) {
                baseFragment = FragmentRewardComment.newInstance(id + "");
                transaction.add(R.id.rewardDownPage, baseFragment);
                fragmentList.add(integerMap.get(id), baseFragment);
            } else {
                transaction.show(baseFragment);
            }
        } else {
            baseFragment = FragmentRewardComment.newInstance(id + "");
            transaction.add(R.id.rewardDownPage, baseFragment);
            integerMap.put(id, position);
            fragmentList.add(baseFragment);
            position++;
        }
        commentBaseFragment = baseFragment;
        transaction.commitAllowingStateLoss();
    }

    /**
     * @param showErrorType 显示错误的显示方式，0表示正常,1表示无悬赏令，2表示网络错误
     */
    private void showEmpty(int showErrorType) {
        if (errorView == null) {
            viewStub.setLayoutResource(R.layout.exception_page);
            errorView = viewStub.inflate();
            textView = (TextView) errorView.findViewById(R.id.tv_err_title);
            RelativeLayout exceptionFl = (RelativeLayout) errorView.findViewById(R.id.exceptionFl);
            LinearLayout exceptionLlBack = (LinearLayout) errorView.findViewById(R.id.exceptionLlBack);
            if (showErrorType == 1) {
                textView.setText(context.getResources().getString(R.string.no_reward_list));
            } else if (showErrorType == 2) {
                textView.setText(context.getResources().getString(R.string.error_network));
            }
            mcoySnapPageLayout.setVisibility(View.GONE);
            exceptionFl.setVisibility(View.VISIBLE);
            exceptionLlBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {
            if (showErrorType == 1) {
                textView.setText(context.getResources().getString(R.string.no_reward_list));
            } else if (showErrorType == 2) {
                textView.setText(context.getResources().getString(R.string.error_network));
            }
            errorView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示悬赏令规则
     *
     * @param rule 规则详细
     */
    private void showRuleDialog(String rule) {
        ruleDialog = new MyAlertView.Builder(context).setMessage(rule).createRule();
        //按返回键时
        ruleDialog.setCanceledOnTouchOutside(false);
        ruleDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = ruleDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        ruleDialog.getWindow().setAttributes(lp);
        ruleDialog.show();
    }


    @Override
    public <T> void update(T... data) {
        int isTop = (Integer) data[0];
        switch (isTop) {
            case 0://0表示评论没滑到顶部
                mcoySnapPageLayout.setOnRecyclerviewIsTop(false);
                break;
            case 1://1表示评论滑到顶部
                mcoySnapPageLayout.setOnRecyclerviewIsTop(true);
                break;
            case 2://2表示返回卡片页面
                mcoySnapPageLayout.snapToPrev();
                break;
            case 3://悬赏令规则
                getRewardRule();
                break;
            case 4://悬赏令分享
                if (popwin != null) {
                    popwin.show();
                } else {
                    popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
                    popwin.setOnItemClick(itemOnClick);
                    shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
                    popwin.show();
                }
                break;
            case 5://5表示删除或者评论后同步卡片评论数字
                if (rewardList.isEmpty()) {
                    return;
                }
                int commentNum = (Integer) data[1];
                rewardList.get(rewardPosition).setCommentNum(commentNum);
                mcoyUpPage.showCommentNum(rewardList.get(rewardPosition));
                break;
            case 6://当在没有登陆情况下从悬赏令跳到悬赏令评论或者提交成绩时且登陆后在返回悬赏令的时候需要重新刷新数据
                resetData();
                break;
            case 7:
                Timer timer = (Timer) data[1];
                TimerTask timerTask = (TimerTask) data[2];
                timerList.add(timer);
                timerTaskList.add(timerTask);
                break;
            case 8:
                mcoySnapPageLayout.setShowListView(true);//当为排行榜时，在显示排行榜名单时，卡片与评论不能上下滑动
                break;
            case 9:
                mcoySnapPageLayout.setShowListView(false);
                break;
        }
    }

    private List<Timer> timerList = new ArrayList<>();
    private List<TimerTask> timerTaskList = new ArrayList<>();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mcoySnapPageLayout.getCurrentScreen() == 1 && commentBaseFragment != null) {
            commentBaseFragment.refreView();
        }

        if (popwin != null && popwin.isShowing()) {
            popwin.dismiss();
        }
    }

    View.OnClickListener itemOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String sharetitle = rewardList.get(rewardPosition).getTitle();
            String sharecontent = getResources().getString(R.string.are_you_go);
            String shareurl = HttpConstant.SERVICE_HTTP_AREA
                    + HttpConstant.V2_SHARE_BOUNTY + rewardId;
            String imgurl = HttpConstant.SERVICE_UPLOAD_AREA
                    + rewardList.get(rewardPosition).getIcon();
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

    /**
     * 接收微客户端博请求的数据。 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     *
     * @param baseResponse 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
    @Override
    public void onResponse(BaseResponse baseResponse) {
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                Toast.makeText(this, R.string.weibo_share_success, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Toast.makeText(this, R.string.weibo_share_cancel, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Toast.makeText(this, getResources().getString(R.string.weibo_share_fail) + "Error Message: " + baseResponse.errMsg,
                        Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoading();
        if (observerable != null) {
            observerable.unSubscribe(Observerable.ObserverableType.REWARD_COMMENT, this);
            observerable = null;
        }
        if (ruleDialog != null && ruleDialog.isShowing()) {
            ruleDialog.dismiss();
        }
        ruleDialog = null;

        if (bitmap != null) {
            bitmap.recycle();
        }
        bitmap = null;

        for (Timer timer : timerList) {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
        for (TimerTask task : timerTaskList) {
            if (task != null) {
                task.cancel();
                task = null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mcoySnapPageLayout.getCurrentScreen() == 1) {
            mcoySnapPageLayout.snapToPrev();
            return;
        }
        super.onBackPressed();
    }

    private void refreshData(List<Reward> rewards) {
        rewardList.clear();
        List<Reward> rewards1 = new ArrayList<Reward>();
        if (id != -1) {
            boolean isAdd = false;
            for (Reward bean : rewards) {
                if (isAdd) {
                    rewardList.add(bean);
                } else {
                    if (id == bean.getId()) {
                        rewardList.add(bean);
                        isAdd = true;
                    } else {
                        rewards1.add(bean);
                    }
                }
            }

            if (!isAdd) {
                showToast(getResources().getString(R.string.reward_end));
            }
            rewardList.addAll(rewards1);
        } else {
            rewardList.addAll(rewards);
        }
        mcoyUpPage.setUpPageData(rewardList);
    }

    /**
     * 重置原始参数，并请求数据
     */
    private void resetData() {
        if (rewardId > 0) {
            id = rewardId;
        }
        position = 0;
        rewardList.clear();
        drawableList.clear();
        isToLogin = true;
        if (mcoySnapPageLayout.getCurrentScreen() == 0) {//
            fragmentList.clear();
            integerMap.clear();
            loadData();
        } else if (mcoySnapPageLayout.getCurrentScreen() == 1) {
            MyBaseFragment myBaseFragment = fragmentList.get(integerMap.get(rewardId));
            myBaseFragment.refreView();
            commentBaseFragment = myBaseFragment;
            fragmentList.clear();
            integerMap.clear();
        }
    }

    private void isShowGuide() {
        if (PreferencesUtil.getFirstRewardGuideStatu(context)) {
            guideLl.setVisibility(View.VISIBLE);
            PreferencesUtil.setFirstRewardGuideStatu(context, false);
        }
        guideLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guideLl.setVisibility(View.GONE);
            }
        });
    }

}
