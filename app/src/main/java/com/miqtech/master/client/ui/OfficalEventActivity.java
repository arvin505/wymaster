package com.miqtech.master.client.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.RecreationCommentAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.ActivityQrcode;
import com.miqtech.master.client.entity.FirstCommentDetail;
import com.miqtech.master.client.entity.Match;
import com.miqtech.master.client.entity.MatchJoiner;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.ui.fragment.FragmentOfficalEventSchedule;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.GsonUtil;

import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ShareToFriendsUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.view.CircleImageView;
import com.miqtech.master.client.view.ExpertMorePopupWindow;
import com.miqtech.master.client.view.MyListView;
import com.miqtech.master.client.view.PagerSlidingTabStrip;
import com.miqtech.master.client.watcher.Observerable;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.constant.WBConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/1/5.
 */
public class OfficalEventActivity extends BaseActivity implements View.OnClickListener, IWeiboHandler.Response, RecreationCommentAdapter.ItemDataDealWith {
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.ivOfficalEvent)
    ImageView ivOfficalEvent;
    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.tvGame)
    TextView tvGame;
    @Bind(R.id.tvCity)
    TextView tvCity;
    @Bind(R.id.tvApplyNum)
    TextView tvApplyNum;
    @Bind(R.id.tvMatchTime)
    TextView tvMatchTime;
    @Bind(R.id.tvContent)
    TextView tvContent;
    @Bind(R.id.tvApply)
    TextView tvApply;
    @Bind(R.id.tvJoiners)
    TextView tvJoiners;
    @Bind(R.id.tvRule)
    TextView tvRule;
    @Bind(R.id.llJoiner)
    LinearLayout llJoiner;
    @Bind(R.id.rlOfficalEvent)
    RelativeLayout rlOfficalEvent;

    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabs;

    @Bind(R.id.tvAllTeams)
    TextView tvAllTeams;


    @Bind(R.id.match_comment_listview)
    MyListView myListView;

    @Bind(R.id.rlComment)
    RelativeLayout rlComment;

    private RecreationCommentAdapter adapter;
    private List<FirstCommentDetail> comments = new ArrayList<FirstCommentDetail>();
    private int listId;
    private User user;
    private Dialog mDialog;
    private final int type = 2;//评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1）
    private int replySize = 10;//   replySize	否	string	楼中楼回复数量（不传默认返回5条）

    private int pageSize = 6;
    private Context context;
    private Match match;
    private String id;
    private ShareToFriendsUtil shareToFriendsUtil;
    private ExpertMorePopupWindow popwin;


    private Dialog dialog;
    private Window mWindow;
    private TextView tvDialogTitle;
    private TextView tvRegistrants;
    private TextView tvCreateTeam;
    private TextView tvJoinTeam;
    private String replyListPosition;
    private final int ISREFER = 1;//startActivityForResult(intent,)

    private Observerable observerable = Observerable.getInstance();

    private ActivityQrcode activityQrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e("tag", "time2 " + System.currentTimeMillis());
        popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
        popwin.setOnItemClick(itemOnClick);
        shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
        initSinaSso(savedInstanceState);
    }

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_officalevent);
        lengthCoding = UMengStatisticsUtil.CODE_4000;
        id = getIntent().getStringExtra("matchId");
        activityQrcode = (ActivityQrcode) getIntent().getSerializableExtra("activityQrcode");
        if (activityQrcode != null) {
            id = activityQrcode.getActivityInfo().getId() + "";
        }
        initView();
        initData();
    }
    @Override
    protected void initView() {
        super.initView();
        ButterKnife.bind(this);
        ivOfficalEvent.setFocusable(true);
        ivOfficalEvent.setFocusableInTouchMode(true);
        ivOfficalEvent.requestFocus();
        ivOfficalEvent.requestFocusFromTouch();

        setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("官方活动");
        setRightBtnImage(R.drawable.icon_share_oranger);
        setRightOtherBtnImage(R.drawable.icon_collection);
        getRightOtherBtn().setOnClickListener(this);
        getRightBtn().setOnClickListener(this);
        getLeftBtn().setOnClickListener(this);
        rlComment.setOnClickListener(this);
        tvAllTeams.setOnClickListener(this);

        tvRule.setOnClickListener(this);
        context = this;


        adapter = new RecreationCommentAdapter(context, comments);
        adapter.setType(1);
        myListView.setAdapter(adapter);
        adapter.setReport(this);
    }

    @Override
    protected void initData() {
        super.initData();
        showLoading();
        loadOfficalEventDetail();
        loadOfficalCommentList();
    }

    public void initSinaSso(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            shareToFriendsUtil.getIWeiApiInstance(this).handleWeiboResponse(getIntent(), this);
        }
    }

    //获取赛事详情
    private void loadOfficalEventDetail() {
        Map parmas = new HashMap<String, String>();
//        id = getIntent().getStringExtra("matchId");
        if (!TextUtils.isEmpty(id)) {
            parmas.put("id", id);
            parmas.put("pageSize", pageSize + "");
            User user = WangYuApplication.getUser(this);
            if (user != null) {
                parmas.put("userId", user.getId());
                parmas.put("token", user.getToken());
            }
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ACTIVITY_DETAIL, parmas, HttpConstant.ACTIVITY_DETAIL);
        }
    }

    private void loadMatchSchedule() {

    }

    //更新VIEW
    private void updateViews() {
        AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + match.getIcon(), ivOfficalEvent);
        if (!TextUtils.isEmpty(match.getTitle())) {
            tvTitle.setText(match.getTitle());
        }
        if (!TextUtils.isEmpty(match.getItem_name())) {
            tvGame.setText(match.getItem_name());
        }
        tvApplyNum.setText(match.getApplyNum() + "");
        if (!TextUtils.isEmpty(match.getStart_time()) && !TextUtils.isEmpty(match.getEnd_time())) {
            tvMatchTime.setText(DateUtil.dateToStrLong(match.getStart_time()) + "至" + DateUtil.dateToStrLong(match.getEnd_time()));
        }
        if (!TextUtils.isEmpty(match.getSummary())) {
            tvContent.setText(match.getSummary());
        }
        if (match.getSchedule() != null) {
            MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
            pager.setAdapter(adapter);
            tabs.setViewPager(pager);
        }
        if (match.getHas_favor() == 1) {
            setRightOtherBtnImage(R.drawable.icon_collectioned);
        } else if (match.getHas_favor() == 0) {
            setRightOtherBtnImage(R.drawable.icon_collection);
        }
        initMatchJoiner(match.getMembers());

        // 赛事状态：1-报名中；2-报名预热中；3-报名已截止;4-赛事已结束5赛事进行中 报名按钮状态
        if (match.getStatus() == 1) {
            tvApply.setOnClickListener(this);
            tvApply.setText("报名中");
        } else if (match.getStatus() == 2) {
            tvApply.setText("报名预热中");
        } else if (match.getStatus() == 3) {
            tvApply.setText("报名已截止");
        } else if (match.getStatus() == 4) {
            tvApply.setText("赛事已结束");
        } else if (match.getStatus() == 5) {
            tvApply.setText("赛事进行中");
            tvApply.setOnClickListener(this);
        } else {
            tvApply.setText("赛事已结束");
        }

        if (activityQrcode != null) {
            Intent intent = new Intent(context, ApplyPopupWindowActivity.class);
            intent.putExtra("activityQrcode", activityQrcode);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_from_bottom);
        }
    }

    private void initMatchJoiner(List<MatchJoiner> joiners) {
        if (joiners == null) {
            return;
        }
        tvJoiners.setText(match.getApplyNum() + "人正在参与");
        tvJoiners.setOnClickListener(this);
        llJoiner.removeAllViews();
        int screenWidth = WangYuApplication.WIDTH;
        float joinerMargin = getResources().getDimension(R.dimen.joiner_margin);
        float joinerViewsWidth = screenWidth - joinerMargin * 2;
        float joinerViewWidth = joinerViewsWidth / 6;
        float headerWidth = joinerViewWidth - getResources().getDimension(R.dimen.dimen_7_5dp) * 2;
        for (int i = 0; i < joiners.size(); i++) {
            if (i < 6) {
                View headerView = View.inflate(context, R.layout.layout_matchjoiner_item, null);
                CircleImageView ivUserHeader = (CircleImageView) headerView.findViewById(R.id.ivUserHeader);
                ViewGroup.LayoutParams lp = new RelativeLayout.LayoutParams((int) joinerViewWidth, (int) joinerViewWidth);
                headerView.setLayoutParams(lp);
                ViewGroup.LayoutParams headerLp = new RelativeLayout.LayoutParams((int) headerWidth, (int) headerWidth);
                ivUserHeader.setLayoutParams(headerLp);
                AsyncImage.loadAvatar(context, HttpConstant.SERVICE_UPLOAD_AREA + joiners.get(i).getIcon(), ivUserHeader);
                llJoiner.addView(headerView);
            }
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        LogUtil.d("Delect","onSuccess");
        hideLoading();
        try {
            if (object == null) {
                return;
            }
            if (method.equals(HttpConstant.ACTIVITY_DETAIL)) {
                int code = object.getInt("code");
                if (code == 0 && object.has("object")) {
                    String strObj = object.getString("object");
                    match = new Gson().fromJson(strObj, Match.class);
                    updateViews();
                }
            } else if (method.equals(HttpConstant.ACTIVITY_FAVOR)) {
                if (match == null) {
                    return;
                }
                if (match.getHas_favor() == 1) {
                    match.setHas_favor(0);
                } else if (match.getHas_favor() == 0) {
                    match.setHas_favor(1);
                }
                if (match.getHas_favor() == 1) {
                    setRightOtherBtnImage(R.drawable.icon_collectioned);
                    observerable.notifyChange(Observerable.ObserverableType.COLLECTSTATE, 2, match.getId(), true);
                } else if (match.getHas_favor() == 0) {
                    setRightOtherBtnImage(R.drawable.icon_collection);
                    observerable.notifyChange(Observerable.ObserverableType.COLLECTSTATE, 2, match.getId(), false);
                }
            } else if (method.equals(HttpConstant.AMUSE_COMMENT_LIST)) {
                initRecreationComment(object);
            } else if (method.equals(HttpConstant.DEL_COMMENT)) {
                LogUtil.d("Delect","删除成功22222");
                if (0 == object.getInt("code") && "success".equals(object.getString("result"))) {
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    LogUtil.d("Delect","删除成功"+replyListPosition);
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        LogUtil.d("Delect","onFaild"+object.toString()+":::"+method);
        hideLoading();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        LogUtil.d("Delect","onError");
        hideLoading();
        if (mDialog != null) {
            mDialog.dismiss();
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
            case R.id.ibRight1:
                collectEvent();
                break;
            case R.id.rlComment:
                skipCommentSection();
                break;
            case R.id.tvApply:
                Intent intent = new Intent();
                if (WangYuApplication.getUser(this) != null) {
                   /* if (match.getPerson_allow() == 0 && match.getTeam_allow() == 0) {
                        showToast("暂未开放报名,请联系活动主办方");
                    } else {
                        if (match.getStatus() == 1 || match.getStatus() == 3 || match.getStatus() == 5) {
                            showDialog();
                        }
                    }*/
                    if (match.getStatus() == 1 || match.getStatus() == 5) {
                        if (match.getPerson_allow() == 1 || match.getTeam_allow() == 1) {
                            showDialog();
                        } else {
                            showToast(context.getResources().getString(R.string.no_apply_way));
                        }
                    }
                } else {
                    intent.setClass(context, LoginActivity.class);
                    startActivityForResult(intent, 1);
                }
                break;
            case R.id.tvAllTeams:
                if (match.getSchedule() != null && match.getSchedule().size() > 0) {
                    intent = new Intent();
                    intent.setClass(this, MatchCorpsActivityV2.class);
                    intent.putExtra("matchId", id + "");
                    intent.putExtra("schedule", match.getSchedule().get(0).getDate());
                    if (!match.getSchedule().get(0).getAreas().isEmpty() && !match.getSchedule().get(0).getAreas().get(0).getNetbars().isEmpty()) {
                        intent.putExtra("netbar", match.getSchedule().get(0).getAreas().get(0).getNetbars().get(0).getName());
                        startActivity(intent);
                    } else {
                        showToast("目前暂无队伍报名");
                    }

                }
                break;
            case R.id.tvRule:
                if (match != null) {
                    intent = new Intent();
                    intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.MATH_INFO);
                    intent.putExtra("id", match.getId() + "");
                    intent.setClass(this, SubjectActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.tvJoiners:
                if (match.getMembers() != null && match.getMembers().size() > 0) {
                    intent = new Intent();
                    intent.setClass(context, JoinerListActivity.class);
                    intent.putExtra("type", 2 + "");
                    intent.putExtra("id", match.getId() + "");
                    startActivity(intent);
                }
                break;
        }
    }

    //收藏
    private void collectEvent() {
        if (!TextUtils.isEmpty(id)) {
            if (WangYuApplication.getUser(context) != null) {
                showLoading();
                Map<String, String> params = new HashMap<String, String>();
                User user = WangYuApplication.getUser(context);
                params.put("userId", user.getId());
                params.put("token", user.getToken());
                params.put("type", "3");
                params.put("id", id);
                sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ACTIVITY_FAVOR, params, HttpConstant.ACTIVITY_FAVOR);
            } else {
                Intent intent = new Intent();
                intent.setClass(context, LoginActivity.class);
                startActivityForResult(intent, 1);
            }
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return DateUtil.strToDatePinYin(match.getSchedule().get(position).getDate());
        }

        @Override
        public int getCount() {
            return match.getSchedule().size();
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentOfficalEventSchedule.newInstance(position, match.getSchedule().get(position));
        }
    }


    View.OnClickListener itemOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (match == null) {
                return;
            }
            String sharetitle = "电竞比赛第一报名入口-" + match.getTitle();
            String sharecontent = "比赛周期:" + DateUtil.strToDatePinYin(match.getStart_time()) + "～"
                    + DateUtil.strToDatePinYin(match.getEnd_time());
            String shareurl = HttpConstant.SERVICE_HTTP_AREA
                    + HttpConstant.ACTIVITYS_URL + match.getId();
            String imgurl = HttpConstant.SERVICE_UPLOAD_AREA
                    + match.getIcon();
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

    private void loadOfficalCommentList() {
        user = WangYuApplication.getUser(context);
        HashMap params = new HashMap();
        params.put("amuseId", id);
        params.put("page", 1 + "");
//        params.put("type", type + "");//	评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1）
        params.put("type", 2 + "");//	评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1）
        params.put("replySize", replySize + "");
        if (user != null) {
            params.put("userId", user.getId() + "");
            params.put("token", user.getToken() + "");
        }
        LogUtil.d(TAG,"amuseId"+id+"::replySize"+replySize);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_COMMENT_LIST, params, HttpConstant.AMUSE_COMMENT_LIST);
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
                    comments.add(0, null);
                    comments.addAll(newComments);
                    comments.add(null);
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳到评论区页面
     */
    public void skipCommentSection() {
        Intent intent = new Intent(context, CommentsSectionActivity.class);
        intent.putExtra("amuseId", id);
        intent.putExtra("type", type);
        LogUtil.d("OfficalEventActivity","id"+id+"::type"+type);
        startActivityForResult(intent, ISREFER);
    }


    @Override
    public void delect(int position) {
        if (!comments.isEmpty() && position < comments.size()) {
            listId = position;
            creatDialogForDelect(comments.get(position).getId());
        }

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
        intent.putExtra("amuseId", id);
        intent.putExtra("type", type);
        intent.putExtra("parentId", comments.get(position).getId());
        intent.putExtra("isPopupKeyboard", 1);//判断是否弹出键盘   1  弹出  0不弹出   默认不弹
        startActivityForResult(intent, ISREFER);
    }

    @Override
    public void lookComment(int position) {
        Intent intent = new Intent(context, PersonalCommentDetail.class);
        intent.putExtra("amuseId", id);
        intent.putExtra("type", type);
        intent.putExtra("parentId", comments.get(position).getId());
        intent.putExtra("isPopupKeyboard", 0);//判断是否弹出键盘   1  弹出  0不弹出   默认不弹
        startActivityForResult(intent, ISREFER);
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

    private void delectcomment(String position) {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("commentId", position + "");
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DEL_COMMENT, map, HttpConstant.DEL_COMMENT);
            LogUtil.d("Delect","发送删除请求"+position);
        } else {
            showToast(getResources().getString(R.string.pleaseLogin));
        }
    }

    @Override
    public void lookCommentSection() {
        skipCommentSection();
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
                LogUtil.d("Delect","删除id"+id);
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


    private void showDialog() {
        dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        mWindow = dialog.getWindow();
        mWindow.setContentView(R.layout.layout_my_comment_dialog);
        dialog.setCanceledOnTouchOutside(true);
        tvDialogTitle = (TextView) dialog.findViewById(R.id.tv_dialog_name);
        tvRegistrants = (TextView) dialog.findViewById(R.id.look_comment_tv_dialog);
        tvCreateTeam = (TextView) dialog.findViewById(R.id.reply_comment_tv_dialog);
        tvJoinTeam = (TextView) dialog.findViewById(R.id.report_comment_tv_dialog);

        tvDialogTitle.setText(context.getResources().getText(R.string.entrants));
        tvRegistrants.setText(context.getResources().getText(R.string.registrants));
        tvCreateTeam.setText("我是队长，我要创建战队");
        tvJoinTeam.setText("我是队员，我要加入战队");

        if (match.getPerson_allow() == 1) {
            tvRegistrants.setVisibility(View.VISIBLE);
        } else {
            tvRegistrants.setVisibility(View.GONE);
        }

        if (match.getTeam_allow() == 1) {
            tvJoinTeam.setVisibility(View.VISIBLE);
            tvCreateTeam.setVisibility(View.VISIBLE);
        } else {
            tvJoinTeam.setVisibility(View.GONE);
            tvCreateTeam.setVisibility(View.GONE);
        }

        MyListener myListener = new MyListener();
        tvRegistrants.setOnClickListener(myListener);
        tvCreateTeam.setOnClickListener(myListener);
        tvJoinTeam.setOnClickListener(myListener);
    }

    private class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.look_comment_tv_dialog://个人报名
                    intent = new Intent(context, OfficePopupWindowActivity.class);
                    intent.putExtra("id", match.getId() + "");
                    intent.putExtra("typeApply", 0);////0个人报名，1创建临时战队，2加入临时战队
                    startActivity(intent);
                    dialog.dismiss();
                    break;
                case R.id.reply_comment_tv_dialog://创建临时战队
                    intent = new Intent(context, OfficePopupWindowActivity.class);
                    intent.putExtra("id", match.getId() + "");
                    intent.putExtra("typeApply", 1);////0个人报名，1创建临时战队，2加入临时战队
                    startActivity(intent);
                    dialog.dismiss();
                    break;
                case R.id.report_comment_tv_dialog://加入临时战队
                    intent = new Intent(context, OfficePopupWindowActivity.class);
                    intent.putExtra("id", match.getId() + "");
                    intent.putExtra("typeApply", 2);////0个人报名，1创建临时战队，2加入临时战队
                    startActivity(intent);
                    dialog.dismiss();
                    break;
            }
        }
    }

    private void toLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
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
    protected void onDestroy() {
        super.onDestroy();
        tabs.removeScrollTAG();
        if (shareToFriendsUtil != null) {
            if (shareToFriendsUtil.requestUtil != null) {
                shareToFriendsUtil.requestUtil.removeTag(shareToFriendsUtil.getClass().getName());
            }
            shareToFriendsUtil.requestUtil = null;
            shareToFriendsUtil = null;
        }
    }
}
