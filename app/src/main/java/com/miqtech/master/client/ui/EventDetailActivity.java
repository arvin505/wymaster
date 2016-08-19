package com.miqtech.master.client.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.InfoListAdapter;
import com.miqtech.master.client.adapter.MatchProgressAdapter;
import com.miqtech.master.client.adapter.RecreationCommentAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.ActivityCard;
import com.miqtech.master.client.entity.ActivityInfo;
import com.miqtech.master.client.entity.ActivityQrcode;
import com.miqtech.master.client.entity.CoinsStoreGoods;
import com.miqtech.master.client.entity.EventAgainst;
import com.miqtech.master.client.entity.FirstCommentDetail;
import com.miqtech.master.client.entity.InforItemDetail;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.entity.ZifaMatch;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.fragment.FragmentApplyPopupConfirmInformation;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ShareToFriendsUtil;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.view.CornerProgressView;
import com.miqtech.master.client.view.ExpertMorePopupWindow;
import com.miqtech.master.client.view.MyListView;
import com.miqtech.master.client.watcher.Observerable;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
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
/**自发赛
 * Created by admin on 2016/7/6.
 */
public class EventDetailActivity extends BaseActivity implements View.OnClickListener, RecreationCommentAdapter.ItemDataDealWith, IWeiboHandler.Response,
        MatchProgressAdapter.SeeMoreMatchProcess, Observerable.ISubscribe {
    @Bind(R.id.ivOfficalEvent)
    ImageView ivOfficalEvent; //赛事详情图片
    @Bind(R.id.tvSponsor)
    TextView tvSponsor;   //主办方
    @Bind(R.id.tvTitle)
    TextView tvTitle;  //详情标题
    @Bind(R.id.tvApplyTime)
    TextView tvApplyTime; //报名时间
    @Bind(R.id.tvBeginMatchTime)
    TextView tvBeginMatchTime; //开赛时间
    @Bind(R.id.llBeginMatchTime)
    LinearLayout llBeginMatchTime;
    @Bind(R.id.llApplyTime)
    LinearLayout llApplyTime;
    @Bind(R.id.pbMatch)
    CornerProgressView pbMatch; //参赛进度
    @Bind(R.id.tvMatchTeam)
    TextView tvMatchTeam;   //参赛队伍
    @Bind(R.id.tvMatchAward)
    TextView tvMatchAward;  //赛事奖励
    @Bind(R.id.tvMatchContent)
    TextView tvMatchContent; //赛事规则
    @Bind(R.id.tvMatchMore)
    TextView tvMatchMore; //展开
    @Bind(R.id.lvMatchProgress)
    MyListView lvMatchProgress; //赛事进程
    @Bind(R.id.lvInformationDynamic)
    MyListView lvInformationDynamic; //咨讯动态
    @Bind(R.id.lvNewComment)
    MyListView lvNewComment;  // 最新评论
    @Bind(R.id.tvMathchComment)
    TextView tvMathchComment; //评论
    @Bind(R.id.tvApply)
    TextView tvApply; //报名
    @Bind(R.id.llMatchMore)
    LinearLayout llMatchMore; //赛事规则更多
    @Bind(R.id.matchRuleSpliteLine)
    View matchRuleSpliteLine;//赛事规则分割线
    @Bind(R.id.llMatchRule)
    LinearLayout llMatchRule;//赛事规则d
    @Bind(R.id.llCountDown)
    LinearLayout llCountDown; //倒计时文本
    @Bind(R.id.tvMatchState)
    TextView tvMatchState;   //赛事状态
    @Bind(R.id.tvCountDown)
    TextView tvCountDown; //倒计时时间
    @Bind(R.id.bottomLine)
    View bottomLine; //底部分割线
    @Bind(R.id.bottomBackground)
    View bottomBackground; //scrollView最底部的背景view
    private static final String TAG = "EventDetailActivity";
    private Context context;
    private int pageSize = 10;
    private int page = 1;
    private ZifaMatch zifaMatchData;
    private List<EventAgainst> eventAgainstDatas = new ArrayList<EventAgainst>();
    private MatchProgressAdapter matchProgressAdapter;
    private InfoListAdapter infoListAdapter;
    private List<InforItemDetail> newItems;
    private User user;
    private String matchId;
    private int replySize = 10;//   replySize	否	string	楼中楼回复数量（不传默认返回5条）
    private List<FirstCommentDetail> comments = new ArrayList<FirstCommentDetail>();
    private RecreationCommentAdapter adapter;
    private int listId;
    private Dialog mDialog;
    private String replyListPosition;
    private final int ISREFER = 1;//startActivityForResult(intent,)
    private final int type = 4;//评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1）
    private ShareToFriendsUtil shareToFriendsUtil;
    private ExpertMorePopupWindow popwin;
    private boolean isExpend = false;
    private int contentLines = 0; //赛事规则行数
    private String title; //赛事详情的标题
    private Timer timer;
    private int totalTime = 0;
    private MyHandler myHandler;
    private ActivityQrcode activityQrcode = new ActivityQrcode();
    private ActivityInfo activityInfo = new ActivityInfo();
    private ActivityCard activityCard = new ActivityCard();
    private Observerable mWatcher;
    private boolean isUpdataState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
        popwin.setOnItemClick(itemOnClick);
        shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
        initSinaSso(savedInstanceState);
        mWatcher = Observerable.getInstance();
        mWatcher.subscribe(Observerable.ObserverableType.ZIFAMATCH, this);
    }

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_event_detail);
        matchId = getIntent().getStringExtra("matchId");
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        ButterKnife.bind(this);
        context = this;
        myHandler = new MyHandler();
        setTitle();
        setListener();
        setAdapter();
    }

    private void setTitle() {
        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("赛事详情");
        setRightBtnImage(R.drawable.icon_share_oranger);
        //让srollView置顶
        ivOfficalEvent.setFocusable(true);
        ivOfficalEvent.setFocusableInTouchMode(true);
        ivOfficalEvent.requestFocus();
    }

    private void setListener() {
        getLeftBtn().setOnClickListener(this);
        getRightBtn().setOnClickListener(this);
        tvMatchMore.setOnClickListener(this);
        tvMathchComment.setOnClickListener(this);
        tvApply.setOnClickListener(this);

    }

    private void setAdapter() {
        matchProgressAdapter = new MatchProgressAdapter(context, eventAgainstDatas, this);
        lvMatchProgress.setAdapter(matchProgressAdapter);
        adapter = new RecreationCommentAdapter(context, comments);
        adapter.setType(1);
        lvNewComment.setAdapter(adapter);
        adapter.setReport(this);
    }

    @Override
    protected void initData() {
        super.initData();
        showLoading();
        loadEventDetail();

    }

    public void initSinaSso(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            shareToFriendsUtil.getIWeiApiInstance(this).handleWeiboResponse(getIntent(), this);
        }
    }

    View.OnClickListener itemOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (zifaMatchData == null) {
                return;
            }
            String sharetitle = "电竞比赛第一报名入口-" + zifaMatchData.getName();
            String sharecontent = "比赛周期:" + DateUtil.strToDatePinYin(zifaMatchData.getApplyBegin()) + "～"
                    + DateUtil.strToDatePinYin(zifaMatchData.getApplyEnd());
            String shareurl = HttpConstant.SERVICE_HTTP_AREA
                    + HttpConstant.EVENT_ULR + zifaMatchData.getRoundId();
            LogUtil.d(TAG, "分享地址" + shareurl);
            String imgurl = HttpConstant.SERVICE_UPLOAD_AREA
                    + zifaMatchData.getPoster();
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

    //获取赛事详情
    private void loadEventDetail() {
        Map parmas = new HashMap<String, String>();
        if (!TextUtils.isEmpty(matchId)) {
            parmas.put("roundId", matchId);
            parmas.put("page", page + "");
            parmas.put("pageSize", pageSize + "");
            User user = WangYuApplication.getUser(this);
            if (user != null) {
                parmas.put("userId", user.getId());
                parmas.put("token", user.getToken());
            }
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.EVENT_DETAIL, parmas, HttpConstant.EVENT_DETAIL);
        }
    }

    //获取咨询列表
    private void loadInfoList() {
        LogUtil.d(TAG, "加载资讯列表数据");
        Map parmas = new HashMap<String, String>();
        if (!TextUtils.isEmpty(matchId)) {
            parmas.put("roundId", matchId);
            parmas.put("infoCount", 0 + "");
            parmas.put("page", page + "");
            parmas.put("pageSize", 2 + "");
            //http://wy.api.wangyuhudong.com/
          sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.EVENT_INFO_LIST, parmas, HttpConstant.EVENT_INFO_LIST);
        }
    }

    private void loadOfficalCommentList() {
        user = WangYuApplication.getUser(context);
        HashMap params = new HashMap();
        params.put("amuseId", matchId + "");
        params.put("page", 1 + "");
        params.put("type", 4 + "");//	评论类型：1-娱乐赛评论；2-官方赛事评论 4自发赛评论。（不传默认为1）
        params.put("replySize", replySize + "");
        if (user != null) {
            params.put("userId", user.getId() + "");
            params.put("token", user.getToken() + "");
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_COMMENT_LIST, params, HttpConstant.AMUSE_COMMENT_LIST);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        LogUtil.d("Delect", "onSuccess");
        try {
            if (object == null) {
                return;
            }
            if (method.equals(HttpConstant.EVENT_DETAIL)) {
                int code = object.getInt("code");
                if (code == 0 && object.has("object")) {
                    String strObj = object.getString("object");
                    LogUtil.d(TAG, "自发赛详情数据" + strObj);
                    zifaMatchData = new Gson().fromJson(strObj, ZifaMatch.class);
                    updateViews();
                    if (!isUpdataState) {
                        loadInfoList();
                        loadOfficalCommentList();
                    }
                    isUpdataState = false;
                }
            } else if (method.equals(HttpConstant.EVENT_INFO_LIST)) {
                int code = object.getInt("code");
                if (code == 0 && object.has("object")) {
                    JSONObject resultObj = object.getJSONObject("object");
                    if (resultObj.has("list")) {
                        newItems = new Gson().fromJson(resultObj.getJSONArray("list").toString(), new TypeToken<List<InforItemDetail>>() {
                        }.getType());
                        infoListAdapter = new InfoListAdapter(context, newItems, 0);
                        lvInformationDynamic.setAdapter(infoListAdapter);
                            infoListAdapter.setRoundId(matchId);
                    }
                }
            } else if (method.equals(HttpConstant.AMUSE_COMMENT_LIST)) {
                initRecreationComment(object);
            } else if (method.equals(HttpConstant.DEL_COMMENT)) {
                LogUtil.d("Delect", "删除成功2222");
                if (0 == object.getInt("code") && "success".equals(object.getString("result"))) {
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    LogUtil.d("Delect", "删除成功" + replyListPosition);
                    if (!TextUtils.isEmpty(replyListPosition)) {
                        int idd = Integer.parseInt(replyListPosition);
                        int replycount = comments.get(listId).getReplyCount();
                        if (replycount > 1) {
                            comments.get(listId).setReplyCount(replycount - 1);
                        }
                        comments.get(listId).getReplyList().remove(idd);
                        replyListPosition = "";
                    } else {
                        comments.remove(listId);
                        if (!comments.isEmpty() && comments.size() <= 2) {
                            lvNewComment.setVisibility(View.GONE);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    showToast("删除成功");
                }
            } else if (method.equals(HttpConstant.V2_COMMENT_PRAISE)) {
                int praisrNum;
                BroadcastController.sendUserChangeBroadcase(context);
                if (comments.get(listId).getIsPraise() == 0) {
                    praisrNum = comments.get(listId).getLikeCount();
                    comments.get(listId).setIsPraise(1);
                    comments.get(listId).setLikeCount(praisrNum + 1);
                } else if (comments.get(listId).getIsPraise() == 1) {
                    praisrNum = comments.get(listId).getLikeCount();
                    comments.get(listId).setIsPraise(0);
                    comments.get(listId).setLikeCount(praisrNum - 1);
                }
                adapter.notifyDataSetChanged();
            } else if (method.equals(HttpConstant.EVENT_SIGN_REQ)) {
                loadEventDetail();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        LogUtil.d(TAG, "onFaild:::" + method + ":::" + object.toString());
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
        LogUtil.d(TAG, "onError:::" + errMsg);
    }

    private void initRecreationComment(JSONObject object) {
        try {
            if (object.getInt("code") == 0 && object.has("object")) {
                String strObj = object.getString("object");
                JSONObject jsonObj = new JSONObject(strObj);
                if (jsonObj.has("list")) {
                    String strList = jsonObj.getString("list").toString();
                    List<FirstCommentDetail> newComments = GsonUtil.getList(strList, FirstCommentDetail.class);
                    comments.clear();
                    comments.clear();
                    comments.add(0, null);
                    if (newComments != null && !newComments.isEmpty()) {
                        comments.addAll(newComments);
                    }
                    comments.add(null);
                    adapter.notifyDataSetChanged();
                } else {
                    comments.add(0, null);
                    comments.add(null);
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateViews() {
        if (!isUpdataState) {
            activityInfo.setPerson_or_team(1);
            activityInfo.setTitle(zifaMatchData.getName());
            activityInfo.setOver_time(zifaMatchData.getActivityBegin());
            activityQrcode.setActivityInfo(activityInfo);
            activityQrcode.setCard(activityCard);
            activityQrcode.setRound(matchId);
            activityQrcode.setIsMatch(1); //设置自发赛标记
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + zifaMatchData.getPoster(), ivOfficalEvent);
            if (!TextUtils.isEmpty(zifaMatchData.getSponsor())) {
                setFontDiffrentColor(getString(R.string.event_detail_sponsor, zifaMatchData.getSponsor()), 2,
                        getString(R.string.event_detail_sponsor, zifaMatchData.getSponsor()).length(), tvSponsor);
            } else {
                tvSponsor.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(zifaMatchData.getName())) {
                tvTitle.setText(zifaMatchData.getName());
                title = zifaMatchData.getName();
            } else {
                tvTitle.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(zifaMatchData.getApplyBegin()) && !TextUtils.isEmpty(zifaMatchData.getApplyEnd())) {
                tvApplyTime.setText(TimeFormatUtil.formatNoYear2(zifaMatchData.getApplyBegin()) + "-" + TimeFormatUtil.formatNoYear2(zifaMatchData.getApplyEnd()));
            } else {
                llApplyTime.setVisibility(View.GONE);

            }
            if (!TextUtils.isEmpty(zifaMatchData.getActivityBegin())) {
                tvBeginMatchTime.setText(TimeFormatUtil.formatNoYear2(zifaMatchData.getActivityBegin()));
            } else {
                llBeginMatchTime.setVisibility(View.GONE);
            }
            if (zifaMatchData.getApplyNum() <= zifaMatchData.getMaxNum()) {
                setFontDiffrentColorAndSize(getString(R.string.event_detail_apply_num, zifaMatchData.getApplyNum() + "", zifaMatchData.getMaxNum() + ""), 0,
                        (zifaMatchData.getApplyNum() + "").length(), tvMatchTeam, false);
            } else {
                setFontDiffrentColorAndSize(getString(R.string.event_detail_apply_num, zifaMatchData.getApplyNum() + "", zifaMatchData.getMaxNum() + ""), 0,
                        (zifaMatchData.getApplyNum() + "").length(), tvMatchTeam, true);
            }
            pbMatch.setMaxCount(zifaMatchData.getMaxNum());
            pbMatch.setCurrentCount(zifaMatchData.getApplyNum());

            if (!TextUtils.isEmpty(zifaMatchData.getPrizeSetting())) {
                tvMatchAward.setText(zifaMatchData.getPrizeSetting());
            }
            if (!TextUtils.isEmpty(zifaMatchData.getRegimeRule())) {
                tvMatchContent.post(new Runnable() {
                    @Override
                    public void run() {
                        tvMatchContent.setText(zifaMatchData.getRegimeRule());
                        contentLines = tvMatchContent.getLineCount();
                        llMatchMore.setVisibility(contentLines > 5 ? View.VISIBLE : View.GONE);
                        matchRuleSpliteLine.setVisibility(contentLines > 5 ? View.VISIBLE : View.GONE);
                        LogUtil.d("FragmentShopBaseInfo", "行数" + tvMatchContent.getLineCount());
                        tvMatchContent.setMaxLines(5);
                        isExpend = false;
                    }
                });
            } else {
                llMatchRule.setVisibility(View.GONE);
            }
            //TODO 设置赛事进程
            if (zifaMatchData.getEventProcessList() != null && !zifaMatchData.getEventProcessList().isEmpty()) {
                lvMatchProgress.setVisibility(View.VISIBLE);
                eventAgainstDatas.clear();
                eventAgainstDatas.add(0, null);
                eventAgainstDatas.addAll(zifaMatchData.getEventProcessList());
                eventAgainstDatas.add(null);
                matchProgressAdapter.notifyDataSetChanged();
                LogUtil.d(TAG, "大小:::" + zifaMatchData.getEventProcessList().size());
            } else {
                lvMatchProgress.setVisibility(View.GONE);
            }
        }
        tvApply.setText(zifaMatchData.getButtonText());
        tvMatchState.setText(zifaMatchData.getTip());
        LogUtil.d(TAG, "大小:::" + zifaMatchData.getTime());
        if (zifaMatchData.getTime() != 0) {
            tvCountDown.setVisibility(View.VISIBLE);
            tvCountDown.setText(TimeFormatUtil.secToTime((int) (zifaMatchData.getTime() / 1000)));
            totalTime = (int) (zifaMatchData.getTime() / 1000);
            tvMatchState.setTextColor(getResources().getColor(R.color.shop_bg_color));
            timeOut();
        } else {
            tvMatchState.setTextColor(getResources().getColor(R.color.white));
            tvCountDown.setVisibility(View.GONE);
        }
        if (zifaMatchData.getTime() != 0 || !TextUtils.isEmpty(zifaMatchData.getTip())) {
            llCountDown.setVisibility(View.VISIBLE);
            bottomLine.setVisibility(View.GONE);
            bottomBackground.setVisibility(View.VISIBLE);
        } else {
            bottomBackground.setVisibility(View.GONE);
            llCountDown.setVisibility(View.GONE);
            bottomLine.setVisibility(View.VISIBLE);
        }
        if (zifaMatchData.getCanClick() == 0) {
            tvApply.setBackgroundColor(getResources().getColor(R.color.gray_event_detail_btn));
            tvApply.setEnabled(false);
        } else {
            tvApply.setBackgroundColor(getResources().getColor(R.color.orange));
            tvApply.setEnabled(true);
        }


    }

    private MyTimerTask task;

    private void timeOut() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer == null) {
            timer = new Timer();
        }
        if (task == null) {
            task = new MyTimerTask();
        }
        timer.schedule(task, 1000, 1000);
    }

    public class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            totalTime--;
            Message message = new Message();
            myHandler.sendEmptyMessage(0);
        }
    }


    private void setFontDiffrentColorAndSize(String content, int start, int end, TextView tv, boolean isOverFlow) {
        if (tv == null) {
            return;
        }
        SpannableString styledText = new SpannableString(content);
        if (!isOverFlow) {
            styledText.setSpan(new TextAppearanceSpan(this, R.style.apply_num_style1), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            styledText.setSpan(new TextAppearanceSpan(this, R.style.apply_num_style2), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        tv.setText(styledText, TextView.BufferType.SPANNABLE);
    }


    private void setFontDiffrentColor(String content, int start, int end, TextView tv) {
        if (tv == null) {
            return;
        }
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.orange)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(style);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shareToFriendsUtil != null) {
            if (shareToFriendsUtil.requestUtil != null) {
                shareToFriendsUtil.requestUtil.removeTag(shareToFriendsUtil.getClass().getName());
            }
            shareToFriendsUtil.requestUtil = null;
            shareToFriendsUtil = null;
        }
        timeOut();
        mWatcher.unSubscribe(Observerable.ObserverableType.ZIFAMATCH, this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ISREFER && data != null) {
            int myIsRefre = data.getIntExtra("isRefre", -1);
            if (myIsRefre == 1) {
                initData();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.ibRight:
                if (popwin != null) {
                    popwin.show();
                } else {
                    popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
                    popwin.setOnItemClick(itemOnClick);
                    shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
                    popwin.show();
                }
                break;
            case R.id.tvMatchMore:
                //展开赛事内容
                if (!isExpend) {
                    tvMatchContent.setMaxLines(Integer.MAX_VALUE);
                    tvMatchMore.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_up, 0);
                    isExpend = true;
                } else {
                    tvMatchContent.setMaxLines(5);
                    tvMatchMore.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.event_icon_down, 0);
                    isExpend = false;
                }
                break;
            case R.id.tvMathchComment:
                skipCommentSection();
                break;
            case R.id.tvApply:
                //0无1报名2对阵图3签到4赛事进程
                Intent intent = new Intent();
                if (WangYuApplication.getUser(this) != null) {
                    if (zifaMatchData.getButtonType() == 1) {
                        setApplyData();
                    } else if (zifaMatchData.getButtonType() == 2) {
                        Intent intentAgainstMap = new Intent(this, EventAgainstMapActivity.class);
                        intentAgainstMap.putExtra("roundId", matchId);
                        intentAgainstMap.putExtra("title", title);
                        startActivity(intentAgainstMap);
                    } else if (zifaMatchData.getButtonType() == 3) {
                        isUpdataState = true;
                        loadSignData();
                    } else if (zifaMatchData.getButtonType() == 4) {
                        Intent intentProcess = new Intent(this, EventAgainstListActivity.class);
                        intentProcess.putExtra("roundId", matchId);
                        intentProcess.putExtra("title", title);
                        startActivity(intentProcess);
                    }
                } else {
                    intent.setClass(context, LoginActivity.class);
                    startActivityForResult(intent, 1);
                }
                break;

        }

    }

    @Override
    public void delect(int position) {
        if (!comments.isEmpty() && position < comments.size()) {
            listId = position;
            creatDialogForDelect(comments.get(position).getId());
            Log.i(TAG, "删除的用户的id" + comments.get(position).getUserId());
        }
    }

    private void setApplyData() {
        Intent intent = new Intent(context, ApplyPopupWindowActivity.class);
        intent.putExtra("activityQrcode", activityQrcode);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_from_bottom);

    }

    private void loadSignData() {
        user = WangYuApplication.getUser(context);
        HashMap params = new HashMap();
        params.put("roundId", matchId + "");
        if (user != null) {
            params.put("userId", user.getId() + "");
            params.put("token", user.getToken() + "");
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.EVENT_SIGN_REQ, params, HttpConstant.EVENT_SIGN_REQ);
    }

    @Override
    public void praiseComment(int position) {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            listId = position;
            Map<String, String> map = new HashMap<>();
            map.put("commentId", comments.get(position).getId() + "");
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.V2_COMMENT_PRAISE, map, HttpConstant.V2_COMMENT_PRAISE);
        } else {
            showToast(getResources().getString(R.string.pleaseLogin));
        }
    }

    @Override
    public void replyComment(int position) {
        Intent intent = new Intent(context, PersonalCommentDetail.class);
        intent.putExtra("amuseId", matchId);
        intent.putExtra("type", type);
        intent.putExtra("parentId", comments.get(position).getId());
        intent.putExtra("isPopupKeyboard", 1);//判断是否弹出键盘   1  弹出  0不弹出   默认不弹
        startActivityForResult(intent, ISREFER);
    }

    @Override
    public void lookComment(int position) {
        Intent intent = new Intent(context, PersonalCommentDetail.class);
        intent.putExtra("amuseId", matchId);
        intent.putExtra("type", type);
        intent.putExtra("parentId", comments.get(position).getId());
        intent.putExtra("isPopupKeyboard", 0);//判断是否弹出键盘   1  弹出  0不弹出   默认不弹
        startActivityForResult(intent, ISREFER);
    }

    @Override
    public void lookCommentSection() {
        skipCommentSection();
    }

    @Override
    public void delectReplyReply(int position, int replyListid) {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            if (!comments.isEmpty() && !comments.get(position).getReplyList().isEmpty()) {
                listId = position;
                replyListPosition = replyListid + "";
                creatDialogForDelect(comments.get(position).getReplyList().get(replyListid).getId());
            }
        } else {
            toLogin();
        }
    }

    private void toLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * 跳到评论区页面
     */
    public void skipCommentSection() {
        Intent intent = new Intent(context, CommentsSectionActivity.class);
        intent.putExtra("amuseId", matchId + "");
        intent.putExtra("type", type);
        startActivityForResult(intent, ISREFER);
    }

    private void creatDialogForDelect(final String id) {
        mDialog = new Dialog(context, R.style.register_style);
        mDialog.setContentView(R.layout.dialog_register_marked_words);
        TextView title = (TextView) mDialog.findViewById(R.id.dialog_title_register);
        TextView yes = (TextView) mDialog.findViewById(R.id.dialog_register_yes_pact);
        TextView no = (TextView) mDialog.findViewById(R.id.dialog_register_no_pact);
        View line = mDialog.findViewById(R.id.dialog_line_no_pact);
        line.setVisibility(View.VISIBLE);
        no.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText("是否删除评论");
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delectcomment(id);
                LogUtil.d("Delect", "删除" + id);
                mDialog.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void delectcomment(String id) {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("commentId", id + "");
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DEL_COMMENT, map, HttpConstant.DEL_COMMENT);
            LogUtil.d("Delect", "删除q赢球" + id + "::" + user.getId() + ":::" + user.getToken());
        } else {
            showToast(getResources().getString(R.string.pleaseLogin));
        }
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                Toast.makeText(this, R.string.errcode_success, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Toast.makeText(this, R.string.errcode_cancel, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Toast.makeText(this, getResources().getString(R.string.errcode_deny) + "Error Message: " + baseResponse.errMsg,
                        Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void setMore() {
        Intent intent = new Intent(this, EventAgainstListActivity.class);
        intent.putExtra("roundId", matchId);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    @Override
    public void setMatchPic() {
        Intent intent = new Intent(this, EventAgainstMapActivity.class);
        intent.putExtra("roundId", matchId);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, EventAgainstListActivity.class);
        intent.putExtra("roundId", matchId);
        intent.putExtra("position", position);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (totalTime > 0) {
                //倒计时时间
                //设置倒计时时间
                tvCountDown.setText(TimeFormatUtil.secToTime(totalTime));
            } else {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                isUpdataState = true;
                loadEventDetail();
//                tvCountDown.setVisibility(View.GONE);
//                tvMatchState.setTextColor(getResources().getColor(R.color.white));
            }

        }
    }

    @Override
    public <T> void update(T... data) {
        if (data == null || data.length <= 0) return;
        int type = (Integer) data[1];
        //跟新数据 并且通知自发赛列表跟新
        if (type == FragmentApplyPopupConfirmInformation.MATCH_DETAIL) {
            loadEventDetail();
            Observerable.getInstance().notifyChange(Observerable.ObserverableType.ZIFAMATCH, zifaMatchData, FragmentApplyPopupConfirmInformation.MATCH_LIST);
        }
    }
}
