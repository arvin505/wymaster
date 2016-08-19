package com.miqtech.master.client.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.RecreationCommentAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.FirstCommentDetail;
import com.miqtech.master.client.entity.MatchJoiner;
import com.miqtech.master.client.entity.RecreationMatchDetails;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ShareToFriendsUtil;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.view.CircleImageView;
import com.miqtech.master.client.view.ExpertMorePopupWindow;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.InputRecreationMatchInfoDialog;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshListView;
import com.miqtech.master.client.watcher.Observerable;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.constant.WBConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/11/28 0028.
 */
public class RecreationMatchDetailsActivity extends BaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener,
        TextWatcher, IWeiboHandler.Response, RecreationCommentAdapter.ItemDataDealWith {
    ListView lvRecreattion;
    @Bind(R.id.rlComment)
    RelativeLayout rlComment;
    @Bind(R.id.tvApply)
    TextView tvApply;
    @Bind(R.id.prlvRecreation)
    PullToRefreshListView prlvRecreation;
    @Bind(R.id.tvAttestation)
    TextView tvAttestation;

    @Bind(R.id.rl_root)
    RelativeLayout root;

//    @Bind(R.id.swRefresh_recreation_match_details)
//    SwipeRefreshLayout swipeRefreshLayout;
//    @Bind(R.id.recyclerview_recreation_match_details)
//    RecyclerView recyclerView;

    private ImageView ivImg;
    private TextView tvTitle, tvServer, tvHasApplyNum, tvBeginTime, tvReward, tvRule, tvAllRule, tvJoinerNum, tvNetbar;
    private LinearLayout llJoiner;

    private List<FirstCommentDetail> comments = new ArrayList<FirstCommentDetail>();
    private RecreationMatchDetails recreationMatchDetails;
    private Context context;
    private RecreationCommentAdapter adapter;

    private final int page = 1;

    private String id;

    private final static int MAX_INPUT_NUM = 200;
    private InputRecreationMatchInfoDialog dialog;

    private AlertDialog selectorDialog;
    private AlertDialog noticeDialog;

    private ShareToFriendsUtil shareToFriendsUtil;
    private ExpertMorePopupWindow popwin;

    private int listId;
    private User user;
    private Dialog mDialog;

    private final int type = 1;//评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1）
    private int replySize = 10;//   replySize	否	string	楼中楼回复数量（不传默认返回5条）
    private Observerable observerable = Observerable.getInstance();
    private String replyListPosition;
    private final int ISREFER = 1;//startActivityForResult(intent,)

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_recreationmatchdetails);
        ButterKnife.bind(this);
        context = this;
        lengthCoding = UMengStatisticsUtil.CODE_5000;
        initView();
        initData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
        popwin.setOnItemClick(itemOnClick);
        shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
        initSinaSso(savedInstanceState);
    }

    public void initSinaSso(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            shareToFriendsUtil.getIWeiApiInstance(this).handleWeiboResponse(getIntent(), this);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        prlvRecreation.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        lvRecreattion = prlvRecreation.getRefreshableView();
        prlvRecreation.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<HasErrorListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                initData();
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getString(R.string.noNeteork));
                }
            }
        });
        lvRecreattion.setOnItemClickListener(this);
        rlComment.setOnClickListener(this);
        tvApply.setOnClickListener(this);
        tvAttestation.setOnClickListener(this);

        setLeftIncludeTitle("娱乐赛");
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        setRightBtnImage(R.drawable.icon_share_oranger);
        getRightOtherBtn().setOnClickListener(this);
        setRightOtherBtnImage(R.drawable.icon_collection);
        getRightBtn().setOnClickListener(this);
        findRecreationView();
    }

    @Override
    protected void initData() {
        super.initData();
        showLoading();
        id = getIntent().getStringExtra("id");
        matchDetails(id);
        loadAmuseCommentList(id);
    }


    private void matchDetails(String id) {
        showLoading();
        HashMap params = new HashMap();
        if (WangYuApplication.getUser(context) != null) {
            params.put("userId", WangYuApplication.getUser(context).getId() + "");
            params.put("token", WangYuApplication.getUser(context).getToken() + "");
        }
        params.put("amuseId", id);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_DETAILS, params, HttpConstant.AMUSE_DETAILS);
    }

    private void loadAmuseCommentList(String id) {
        user = WangYuApplication.getUser(context);

        HashMap params = new HashMap();
        params.put("amuseId", id);
        params.put("page", page + "");
        params.put("type", 1 + "");
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
        LogUtil.e("object", "object : " + object.toString());
        prlvRecreation.onRefreshComplete();
        try {
            if (HttpConstant.AMUSE_DETAILS.equals(method)) {
                initRecreationMatchDetails(object);
            } else if (HttpConstant.AMUSE_COMMENT_LIST.equals(method)) {
                initRecreationComment(object);
            } else if (HttpConstant.AMUSE_CANCEL_APPLY.equals(method)) {

                if (object.getInt("code") == 0 && object.getString("result").equals("success")) {
                    createGiveUpDialog();
                    matchDetails(id);
                } else {
                    showToast(object.getString("result"));
                }

            } else if (HttpConstant.AMUSE_APPLY_INFO.equals(method)) {
                if (object.getInt("code") == 0 && object.has("object")) {
                    ArrayList<String> data = new ArrayList<String>();
                    String strObject = object.getString("object");
                    JSONObject jsonObj = new JSONObject(strObject);
                    Iterator it = jsonObj.keys();
                    // 遍历jsonObject数据，添加到Map对象
                    while (it.hasNext()) {
                        String key = String.valueOf(it.next());
                        data.add(key);
                    }
                    initDialogView(data);
                }

            } else if (HttpConstant.AMUSE_APPLY.equals(method)) {
                dialog.dismiss();
                createApplySuccessDialog(object);
                if (object.getInt("code") == 0 && object.getString("result").equals("success")) {
                    matchDetails(id);
                }
            } else if (method.equals(HttpConstant.DEL_COMMENT)) {
                if (0 == object.getInt("code") && "success".equals(object.getString("result"))) {
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
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
            } else if (method.equals(HttpConstant.ACTIVITY_FAVOR)) {
                int hasFavor = object.getJSONObject("object").getInt("has_favor");
                if (hasFavor == 1) {
                    getRightOtherBtn().setImageResource(R.drawable.icon_collectioned);
                    observerable.notifyChange(Observerable.ObserverableType.COLLECTSTATE,2, recreationMatchDetails.getId(), true);
                } else {
                    getRightOtherBtn().setImageResource(R.drawable.icon_collection);
                    observerable.notifyChange(Observerable.ObserverableType.COLLECTSTATE,2, recreationMatchDetails.getId(), false);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            showToast("数据错误");
        }
    }


    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        prlvRecreation.onRefreshComplete();
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createApplySuccessDialog(JSONObject object) throws JSONException {
        if (noticeDialog == null) {
            noticeDialog = new AlertDialog.Builder(context).create();
            noticeDialog.show();
            Window window = noticeDialog.getWindow();
            WindowManager.LayoutParams params =
                    noticeDialog.getWindow().getAttributes();
            window.setContentView(R.layout.layout_hintalert_dialog);
            params.width = (int) (WangYuApplication.WIDTH - getResources().getDimension(R.dimen.dialog_margin) * 2);
            params.height = params.WRAP_CONTENT;
            window.setAttributes(params);
            noticeDialog.setCanceledOnTouchOutside(true);
        }
        ImageView ivResult = (ImageView) noticeDialog.findViewById(R.id.ivResult);
        TextView tvResult = (TextView) noticeDialog.findViewById(R.id.tvResult);
        TextView tvContent = (TextView) noticeDialog.findViewById(R.id.tvContent);
        if (object.getInt("code") == 0 && object.getString("result").equals("success")) {
            ivResult.setImageResource(R.drawable.success_orange_icon);
            tvResult.setText("报名成功");
            tvContent.setVisibility(View.GONE);
        } else {
            ivResult.setImageResource(R.drawable.failed_red_icon);
            tvResult.setText("报名失败");
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(object.getString("result"));
        }
        noticeDialog.show();
    }

    private void createGiveUpDialog() {
        if (noticeDialog == null) {
            noticeDialog = new AlertDialog.Builder(context).create();
            noticeDialog.show();
            Window window = noticeDialog.getWindow();
            WindowManager.LayoutParams params =
                    noticeDialog.getWindow().getAttributes();
            window.setContentView(R.layout.layout_hintalert_dialog);
            params.width = (int) (WangYuApplication.WIDTH - getResources().getDimension(R.dimen.dialog_margin) * 2);
            params.height = params.WRAP_CONTENT;
            window.setAttributes(params);
            noticeDialog.setCanceledOnTouchOutside(true);
        }
        ImageView ivResult = (ImageView) noticeDialog.findViewById(R.id.ivResult);
        TextView tvResult = (TextView) noticeDialog.findViewById(R.id.tvResult);
        TextView tvContent = (TextView) noticeDialog.findViewById(R.id.tvContent);
        tvContent.setVisibility(View.GONE);
        ivResult.setImageResource(R.drawable.success_orange_icon);
        tvResult.setText("放弃成功");
        noticeDialog.show();
    }

    private void initDialogView(ArrayList<String> data) {
        dialog = new InputRecreationMatchInfoDialog(context, R.style.searchStyle);
        TextView tvClose = (TextView) dialog.findViewById(R.id.tvClose);
        TextView tvUpload = (TextView) dialog.findViewById(R.id.tvUpload);
        LinearLayout llContent = (LinearLayout) dialog.findViewById(R.id.llContent);
        ArrayList<String> list = getSortRecreationInfo(data);
        for (int i = 0; i < list.size(); i++) {
            String temp = list.get(i);
            if (temp.equals("name")) {
                View nameView = View.inflate(context, R.layout.layout_recreationmatchinfo_item, null);
                TextView tvTitle = (TextView) nameView.findViewById(R.id.tvTitle);
                EditText edtInput = (EditText) nameView.findViewById(R.id.edtInput);
                tvTitle.setText("姓名:");
                edtInput.setHint("真实姓名");
                edtInput.setTag("name");
                edtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                llContent.addView(nameView);
            } else if (temp.equals("idCard")) {
                View idView = View.inflate(context, R.layout.layout_recreationmatchinfo_item, null);
                TextView tvTitle = (TextView) idView.findViewById(R.id.tvTitle);
                EditText edtInput = (EditText) idView.findViewById(R.id.edtInput);
                tvTitle.setText("身份证:");
                edtInput.setHint("有效的身份证号");
                String digits = "1234567890Xx";
                edtInput.setKeyListener(DigitsKeyListener.getInstance(digits));
                edtInput.setTag("idCard");
                edtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
                llContent.addView(idView);
            } else if (temp.equals("telephone")) {
                View telView = View.inflate(context, R.layout.layout_recreationmatchinfo_item, null);
                TextView tvTitle = (TextView) telView.findViewById(R.id.tvTitle);
                EditText edtInput = (EditText) telView.findViewById(R.id.edtInput);
                tvTitle.setText("联系人:");
                edtInput.setHint("可联系的手机号码");
                edtInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                edtInput.setTag("telephone");
                edtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                llContent.addView(telView);
            } else if (temp.equals("qq")) {
                View qqView = View.inflate(context, R.layout.layout_recreationmatchinfo_item, null);
                TextView tvTitle = (TextView) qqView.findViewById(R.id.tvTitle);
                EditText edtInput = (EditText) qqView.findViewById(R.id.edtInput);
                tvTitle.setText("QQ:");
                edtInput.setHint("有效QQ号");
                edtInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                edtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                edtInput.setTag("qq");
                llContent.addView(qqView);
            } else if (temp.equals("gameAccount")) {
                View gameAccountView = View.inflate(context, R.layout.layout_recreationmatchinfo_item, null);
                TextView tvTitle = (TextView) gameAccountView.findViewById(R.id.tvTitle);
                EditText edtInput = (EditText) gameAccountView.findViewById(R.id.edtInput);
                tvTitle.setText("游戏昵称:");
                edtInput.setHint("常用的游戏昵称");
                edtInput.setTag("gameAccount");
                edtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                llContent.addView(gameAccountView);
            } else if (temp.equals("server")) {
                View serverView = View.inflate(context, R.layout.layout_recreationmatchinfo_item, null);
                TextView tvTitle = (TextView) serverView.findViewById(R.id.tvTitle);
                EditText edtInput = (EditText) serverView.findViewById(R.id.edtInput);
                tvTitle.setText("比赛区服:");
                edtInput.setHint("所在游戏的服务器");
                edtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                edtInput.setTag("server");
                llContent.addView(serverView);
            } else if (temp.equals("teamName")) {
                View teamView = View.inflate(context, R.layout.layout_recreationmatchinfo_item, null);
                TextView tvTitle = (TextView) teamView.findViewById(R.id.tvTitle);
                EditText edtInput = (EditText) teamView.findViewById(R.id.edtInput);
                tvTitle.setText("团队名称:");
                edtInput.setHint("填写您的团队名称（15字以内）");
                edtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
                edtInput.setTag("teamName");
                llContent.addView(teamView);
            }
        }
        tvClose.setOnClickListener(this);
        tvUpload.setOnClickListener(this);
        dialog.showDialog(llContent);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
    }


    private ArrayList<String> getSortRecreationInfo(ArrayList<String> data) {
        ArrayList<String> list = new ArrayList<String>();
        int nameResult = data.indexOf("name");
        if (nameResult != -1) {
            list.add(data.get(nameResult));
        }
        int idCardResult = data.indexOf("idCard");
        if (idCardResult != -1) {
            list.add(data.get(idCardResult));
        }
        int telephoneResult = data.indexOf("telephone");
        if (telephoneResult != -1) {
            list.add(data.get(telephoneResult));
        }
        int qqResult = data.indexOf("qq");
        if (qqResult != -1) {
            list.add(data.get(qqResult));
        }
        int gameAccountResult = data.indexOf("gameAccount");
        if (gameAccountResult != -1) {
            list.add(data.get(gameAccountResult));
        }
        int serverResult = data.indexOf("server");
        if (serverResult != -1) {
            list.add(data.get(serverResult));
        }
        int teamNameResult = data.indexOf("teamName");
        if (teamNameResult != -1) {
            list.add(data.get(teamNameResult));
        }
        return list;
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        prlvRecreation.onRefreshComplete();
    }

    private void initRecreationMatchDetails(JSONObject object) {
        try {
            if (object.getInt("code") == 0 && object.has("object")) {
                String strObj = object.getString("object");
                recreationMatchDetails = GsonUtil.getBean(strObj, RecreationMatchDetails.class);
                initRecreationView();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        if (shareToFriendsUtil != null) {
            if (shareToFriendsUtil.requestUtil != null) {
                shareToFriendsUtil.requestUtil.removeTag(shareToFriendsUtil.getClass().getName());
            }
            shareToFriendsUtil.requestUtil = null;
            shareToFriendsUtil = null;
        }
        observerable = null;
        super.onDestroy();
    }

    private void initMatchJoiner(List<MatchJoiner> joiners) {
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

    private void findRecreationView() {
        // tvTitle,tvServer,tvHasApplyNum,tvBeginTime,tvWinner,tvloser,tvRule,tvAllRule,tvJoinerNum;
        View headerView = View.inflate(context, R.layout.layout_recreationmatch_header, null);
        ivImg = (ImageView) headerView.findViewById(R.id.ivImg);
        tvTitle = (TextView) headerView.findViewById(R.id.tvTitle);
        tvServer = (TextView) headerView.findViewById(R.id.tvServer);
        tvHasApplyNum = (TextView) headerView.findViewById(R.id.tvHasApplyNum);
        tvBeginTime = (TextView) headerView.findViewById(R.id.tvBeginTime);
        tvReward = (TextView) headerView.findViewById(R.id.tvReward);
        tvRule = (TextView) headerView.findViewById(R.id.tvRule);
        tvAllRule = (TextView) headerView.findViewById(R.id.tvAllRule);
        tvJoinerNum = (TextView) headerView.findViewById(R.id.tvJoinerNum);
        llJoiner = (LinearLayout) headerView.findViewById(R.id.llJoiner);
        tvNetbar = (TextView) headerView.findViewById(R.id.tvNetbar);
        tvJoinerNum.setOnClickListener(this);
        lvRecreattion.addHeaderView(headerView);
        adapter = new RecreationCommentAdapter(context, comments);
        adapter.setType(1);
        lvRecreattion.setAdapter(adapter);
        tvAllRule.setOnClickListener(this);
        adapter.setReport(this);

    }

    private void initRecreationView() {
        AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + recreationMatchDetails.getBanner(), ivImg);
        tvTitle.setText(recreationMatchDetails.getTitle());
        tvServer.setText(recreationMatchDetails.getServer());
        tvHasApplyNum.setText(recreationMatchDetails.getApplyNum() + "");
        tvBeginTime.setText(TimeFormatUtil.formatNoSecond(recreationMatchDetails.getStartDate()));
        tvReward.setText(recreationMatchDetails.getReward());
        tvRule.setText(Html.fromHtml(recreationMatchDetails.getRule()));
        tvJoinerNum.setText(recreationMatchDetails.getApplyNum() + "人正在参与");
        if (recreationMatchDetails.getWay().equals("1")) {
            tvNetbar.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(recreationMatchDetails.getNetbarName())) {
                tvNetbar.setText(recreationMatchDetails.getNetbarName());
                tvNetbar.setOnClickListener(this);
            } else {
                tvNetbar.setVisibility(View.GONE);
            }
        } else if (recreationMatchDetails.getWay().equals("2")) {
            tvNetbar.setVisibility(View.GONE);
        }

        if (recreationMatchDetails.getHas_favor() == 1) {
            getRightOtherBtn().setImageResource(R.drawable.icon_collectioned);
        } else {
            getRightOtherBtn().setImageResource(R.drawable.icon_collection);
        }

        initMatchJoiner(recreationMatchDetails.getApplyerList());
        screenWithTimeStatus();

    }

    private void screenWithTimeStatus() {
        //用户报名状态：-1-未登录或登录失效，0-未报名，1-已报名/未提交认证，2-已提交认证
        int applyStatus = recreationMatchDetails.getApplyStatus();
        //赛事时间状态：0-报名即将开始，1-报名中，2-进行中，3-赛事已结束，4-提交已截止
        if (applyStatus == -1 && WangYuApplication.getUser(context) != null) {
            Intent intent = new Intent();
            intent.setClass(context, LoginActivity.class);
            startActivityForResult(intent, UPDATE_VIEWS);
            showToast("登录失效,请重新登录");
            return;
        }
        int timeStatus = recreationMatchDetails.getTimeStatus();
        if (timeStatus == 1) {
            screenWithApplyStatus(applyStatus);
        } else if (timeStatus == 2) {
            //报名预热中
            notStartApplyStatus(applyStatus);
        } else if (timeStatus == 4) {
            //赛事已结束
            endMatchApplyStatus(applyStatus);
        } else if (timeStatus == 5) {
            screenWithApplyStatus(applyStatus);
        } else if (timeStatus == 6) {
            //提交已截止
            cutOffApplyStatus(applyStatus);
        }
    }

    //已截止的状态显示按钮
    private void cutOffApplyStatus(int applyStatus) {
        if (applyStatus == 2) {
            tvApply.setBackgroundColor(getResources().getColor(R.color.orange));
            tvApply.setText("查看结果");
            tvApply.setTextColor(getResources().getColor(R.color.white));
            tvAttestation.setVisibility(View.GONE);
        } else {
            tvApply.setTextColor(getResources().getColor(R.color.font_gray));
            tvApply.setText("已结束");
            tvApply.setBackgroundColor(getResources().getColor(R.color.lv_item_content_text));
            tvAttestation.setVisibility(View.GONE);
            tvAttestation.setVisibility(View.GONE);
        }
    }

    //已结束的状态显示按钮
    private void endMatchApplyStatus(int applyStatus) {
        if (applyStatus == 1) {
            //1-已报名/未提交认证
            tvApply.setTextColor(getResources().getColor(R.color.white));
            tvApply.setBackgroundColor(getResources().getColor(R.color.orange_juice));
            tvApply.setText("放弃参赛");
            tvAttestation.setBackgroundColor(getResources().getColor(R.color.font_gray));
            tvAttestation.setTextColor(getResources().getColor(R.color.lv_item_content_text));
            tvAttestation.setText("提交认证");
            tvAttestation.setVisibility(View.VISIBLE);
            tvAttestation.setOnClickListener(this);
        } else if (applyStatus == 2) {
            //2-已提交认证
            tvApply.setBackgroundColor(getResources().getColor(R.color.orange));
            tvApply.setText("查看结果");
            tvApply.setTextColor(getResources().getColor(R.color.white));
            tvAttestation.setVisibility(View.GONE);
        } else {
            tvApply.setTextColor(getResources().getColor(R.color.font_gray));
            tvApply.setText("已结束");
            tvApply.setBackgroundColor(getResources().getColor(R.color.lv_item_content_text));
            tvAttestation.setVisibility(View.GONE);
            tvAttestation.setVisibility(View.GONE);
        }
    }

    //未结束未截止的状态显示按钮
    private void screenWithApplyStatus(int applyStatus) {
        if (applyStatus == 1) {
            //1-已报名/未提交认证
            tvApply.setTextColor(getResources().getColor(R.color.white));
            tvApply.setBackgroundColor(getResources().getColor(R.color.orange_juice));
            tvApply.setText("放弃参赛");
            tvAttestation.setBackgroundColor(getResources().getColor(R.color.font_gray));
            tvAttestation.setTextColor(getResources().getColor(R.color.lv_item_content_text));
            tvAttestation.setText("提交认证");
            tvAttestation.setVisibility(View.VISIBLE);
        } else if (applyStatus == 2) {
            //2-已提交认证
            tvApply.setBackgroundColor(getResources().getColor(R.color.orange));
            tvApply.setText("查看结果");
            tvApply.setTextColor(getResources().getColor(R.color.white));
            tvAttestation.setVisibility(View.GONE);
        } else {
            tvApply.setTextColor(getResources().getColor(R.color.white));
            tvApply.setText("立即报名");
            tvApply.setBackgroundColor(getResources().getColor(R.color.orange));
            tvAttestation.setVisibility(View.GONE);
        }
    }

    private void notStartApplyStatus(int applyStatus) {
        tvApply.setText("报名即将开始");
        tvApply.setBackgroundColor(getResources().getColor(R.color.lv_item_content_text));
        tvApply.setTextColor(getResources().getColor(R.color.font_gray));
        tvApply.setOnClickListener(null);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                return;
            case R.id.tvJoinerNum:
                toJoinerNum();
                return;
            case R.id.tvClose:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                dialog.dismiss();
                return;
            case R.id.ibRight:
                if (popwin != null) {
                    popwin.show();
                } else {
                    popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
                    popwin.setOnItemClick(itemOnClick);
                    shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
                    popwin.show();
                }
                return;
            case R.id.tvAllRule:
                if (recreationMatchDetails != null) {
                    Intent intent = new Intent();
                    intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.RULE_DETAIL);
                    intent.putExtra("id", recreationMatchDetails.getId() + "");
                    intent.setClass(this, SubjectActivity.class);
                    startActivity(intent);
                }
                return;
            case R.id.tvNetbar:
                if (!TextUtils.isEmpty(recreationMatchDetails.getLatitude()) && !TextUtils.isEmpty(recreationMatchDetails.getLongitude()) && !TextUtils.isEmpty(recreationMatchDetails.getNetbarName())) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putDouble("latitude", Double.parseDouble(recreationMatchDetails.getLatitude()));
                    bundle.putDouble("longitude", Double.parseDouble(recreationMatchDetails.getLongitude()));
                    bundle.putString("netbarTitle", recreationMatchDetails.getNetbarName());
                    intent.putExtras(bundle);
                    intent.setClass(this, BaiduMapActivity.class);
                    startActivity(intent);
                }
                return;
        }

        if (WangYuApplication.getUser(context) == null) {
            toLoginActivity();
        } else {
            switch (v.getId()) {
                case R.id.rlComment:
                    skipCommentSection();
                    break;
                case R.id.tvApply:
                    if (tvApply.getText().toString().equals("放弃参赛") && tvApply.getVisibility() == View.VISIBLE) {
                        cancelDialog();
                    } else if (tvApply.getText().toString().equals("立即报名") && tvApply.getVisibility() == View.VISIBLE) {
                        if (dialog == null) {
                            getApplyMatchInfo();
                        } else {
                            LinearLayout llContent = (LinearLayout) dialog.findViewById(R.id.llContent);
                            dialog.showDialog(llContent);
                        }
                    } else if (tvApply.getText().toString().equals("查看结果") && tvApply.getVisibility() == View.VISIBLE) {
                        toSchedule();
                    }
                    break;
                case R.id.tvAttestation:
                    if (tvAttestation.getText().toString().equals("提交认证")) {
                        totvAttestationActivity();
                    }
                    break;
                case R.id.tvUpload:
                    uploadApplyInfo();
                    break;
                case R.id.tvCancel:
                    selectorDialog.dismiss();
                    break;
                case R.id.tvSure:
                    cancelApply();
                    selectorDialog.dismiss();
                    break;
                case R.id.ibRight1:  //收藏
                    collectActivity();
                    break;
            }
        }
    }

    /**
     * 收藏
     */
    private void collectActivity() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(this);
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("type", "5");
        params.put("id", recreationMatchDetails.getId() + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ACTIVITY_FAVOR, params, HttpConstant.ACTIVITY_FAVOR);
    }

    private void totvAttestationActivity() {
        Intent intent = new Intent();
        intent.setClass(context, RecreationMatchUploadActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("verifyContent", recreationMatchDetails.getVerifyContent());
        startActivityForResult(intent, UPDATE_VIEWS);
    }

    private void toLoginActivity() {
        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        startActivityForResult(intent, UPDATE_VIEWS);
    }

    private void toJoinerNum() {
        if (recreationMatchDetails != null && recreationMatchDetails.getApplyNum() > 0) {
            Intent intent = new Intent();
            intent.setClass(context, JoinerListActivity.class);
            intent.putExtra("type", 1 + "");
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    private void toSchedule() {
        Intent intent = new Intent();
        intent.setClass(context, RecreationMatchScheduleActivityV2.class);
        intent.putExtra("id", id);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_from_top);
    }

    private void cancelDialog() {
        if (selectorDialog == null) {
            selectorDialog = new AlertDialog.Builder(context).create();
            selectorDialog.show();
            Window window = selectorDialog.getWindow();
            WindowManager.LayoutParams lp =
                    selectorDialog.getWindow().getAttributes();
            window.setContentView(R.layout.layout_selectoralert_dialog);
            lp.width = (int) (WangYuApplication.WIDTH - getResources().getDimension(R.dimen.dialog_margin) * 2);
            lp.height = lp.WRAP_CONTENT;
            window.setAttributes(lp);
            selectorDialog.setCanceledOnTouchOutside(true);
        }
        TextView tvContent = (TextView) selectorDialog.findViewById(R.id.tvContent);
        TextView tvCancel = (TextView) selectorDialog.findViewById(R.id.tvCancel);
        TextView tvSure = (TextView) selectorDialog.findViewById(R.id.tvSure);
        tvContent.setText("确认放弃报名？");
        tvCancel.setOnClickListener(this);
        tvSure.setOnClickListener(this);
        selectorDialog.show();
    }

    private void cancelApply() {
        showLoading();
        HashMap params = new HashMap();
        params.put("activityId", id);
        params.put("userId", WangYuApplication.getUser(context).getId() + "");
        params.put("token", WangYuApplication.getUser(context).getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_CANCEL_APPLY, params, HttpConstant.AMUSE_CANCEL_APPLY);
    }

    private void uploadApplyInfo() {
        HashMap params = new HashMap();
        LinearLayout llContent = (LinearLayout) dialog.findViewById(R.id.llContent);

        for (int i = 0; i < llContent.getChildCount(); i++) {
            View childView = llContent.getChildAt(i);
            EditText edtInput = (EditText) childView.findViewById(R.id.edtInput);
            String title = (String) edtInput.getTag();
            if (title.equals("name")) {
                if (TextUtils.isEmpty(title)) {
                    showToast("姓名不能为空");
                    return;
                } else {
                    params.put(title, edtInput.getText().toString());
                }
            }
            if (title.equals("idCard")) {
                String idCard = edtInput.getText().toString();
                if (!TextUtils.isEmpty(idCard) && idCard.matches(Constant.ID_CARD_FORMAT)) {
                    params.put(title, idCard);
                } else {
                    showToast("身份证格式不正确");
                    return;
                }
            } else if (title.equals("telephone")) {
                String telephone = edtInput.getText().toString();
                if (!TextUtils.isEmpty(telephone) && telephone.matches(Constant.PHONE_FORMAT)) {
                    params.put(title, telephone);
                } else {
                    showToast("手机号码格式不正确");
                    return;
                }
            } else if (title.equals("qq")) {
                String qq = edtInput.getText().toString();
                if (!TextUtils.isEmpty(qq)) {
                    params.put(title, qq);
                } else {
                    showToast("qq不能为空");
                    return;
                }
            } else if (title.equals("gameAccount")) {
                String gameAccount = edtInput.getText().toString();
                if (!TextUtils.isEmpty(gameAccount)) {
                    params.put(title, gameAccount);
                } else {
                    showToast("游戏昵称不能为空");
                    return;
                }
            } else if (title.equals("server")) {
                String server = edtInput.getText().toString();
                if (!TextUtils.isEmpty(server)) {
                    params.put(title, server);
                } else {
                    showToast("服务器不能为空");
                    return;
                }
            } else if (title.equals("teamName")) {
                String teamName = edtInput.getText().toString();
                if (!TextUtils.isEmpty(teamName)) {
                    params.put(title, teamName);
                } else {
                    showToast("团队名称不能为空");
                    return;
                }
            }
        }
        showLoading();
        params.put("amuseId", id);
        params.put("userId", WangYuApplication.getUser(context).getId() + "");
        params.put("token", WangYuApplication.getUser(context).getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_APPLY, params, HttpConstant.AMUSE_APPLY);
    }

    private void getApplyMatchInfo() {
        if (WangYuApplication.getUser(context) != null) {
            showLoading();
            HashMap params = new HashMap();
            params.put("amuseId", id);
            params.put("userId", WangYuApplication.getUser(context).getId());
            params.put("token", WangYuApplication.getUser(context).getToken());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_APPLY_INFO, params, HttpConstant.AMUSE_APPLY_INFO);
        } else {
            toLoginActivity();
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
//        if (!TextUtils.isEmpty(edtComment.getText().toString().trim())) {
//            tvSend.setTextColor(getResources().getColor(R.color.orange));
//            tvSend.setOnClickListener(this);
//        } else {
//            tvSend.setTextColor(getResources().getColor(R.color.font_gray));
//            tvSend.setOnClickListener(null);
//        }
//        int leftNum = MAX_INPUT_NUM - edtComment.getText().toString().length();
//        tvLeftNum.setText(leftNum + "");
//        if (edtComment.getLineCount() > 1) {
//            tvLeftNum.setVisibility(View.VISIBLE);
//        } else {
//            tvLeftNum.setVisibility(View.INVISIBLE);
//        }
    }


    View.OnClickListener itemOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recreationMatchDetails == null) {
                return;
            }
            String sharetitle = "趣味电竞收福利-" + recreationMatchDetails.getTitle();
            String sharecontent = "比赛周期:" + DateUtil.strToDatePinYin(recreationMatchDetails.getStartDate()) + "～"
                    + DateUtil.strToDatePinYin(recreationMatchDetails.getEndDate());
            String shareurl = HttpConstant.SERVICE_HTTP_AREA
                    + HttpConstant.AMUSE_URL + recreationMatchDetails.getId();
            String imgurl = HttpConstant.SERVICE_UPLOAD_AREA
                    + recreationMatchDetails.getBanner();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_VIEWS) {
            initData();
        }

        if (requestCode == ISREFER) {
            int myIsRefre = data.getIntExtra("isRefre", -1);
            if (myIsRefre == 1) {
                initData();
            }
        }
    }

    /**
     * 跳到评论区页面
     */
    public void skipCommentSection() {
        Intent intent = new Intent(context, CommentsSectionActivity.class);
        intent.putExtra("amuseId", id);
        intent.putExtra("type", type);
        startActivityForResult(intent, ISREFER);
    }


    @Override
    public void delect(int position) {
        if (!comments.isEmpty() && position < comments.size()) {
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
//            showToast(getResources().getString(R.string.pleaseLogin));
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
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
        } else {
            showToast(getResources().getString(R.string.pleaseLogin));
        }
    }

    private void toLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

}
