package com.miqtech.master.client.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.CommentsSectionAdapter;
import com.miqtech.master.client.adapter.QuickCommentAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.FirstCommentDetail;
import com.miqtech.master.client.entity.QuickCommentDetail;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.PersonalCommentDetail;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.MyAlertView;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshRecyclerView;
import com.miqtech.master.client.view.snapscrollview.McoySnapPageLayout;
import com.miqtech.master.client.watcher.Observerable;

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
 * 悬赏令评论
 * Created by zhaosentao on 2016/7/25.
 */
public class FragmentRewardComment extends MyBaseFragment implements CommentsSectionAdapter.ProcessTheData, QuickCommentAdapter.OnClickItemListener {

    @Bind(R.id.reawrdUpLlBack)
    LinearLayout back;//返回
    @Bind(R.id.rewardUpTitle)
    TextView tvTitle;//标题
    @Bind(R.id.fragmentRewardCommentRvQuickComment)
    RecyclerView rvQuickComment;//快速评论
    @Bind(R.id.fragmentRewardCommentRvComment)
    PullToRefreshRecyclerView refreshComment;//评论列表
    @Bind(R.id.fragmentRewardLlCommentSend)
    LinearLayout llSend;//发表评论
    @Bind(R.id.fragmentRewardCommentSend)
    TextView tvSend;//发表评论
    @Bind(R.id.tvNumber)
    TextView tvNumber;//剩余字数
    @Bind(R.id.fragmentRewardCommentEt)
    EditText etConnent;
    @Bind(R.id.fragmentRewardCommentLine)
    View viewLine;

    private Context context;
    private Resources resources;
    private String rewardId;//悬赏令id
    private final static String REWARD_ID = "rewardId";
    private String message;//要发表的评论

    private int page = 1;//页数
    private int size = 10;//每页的个数
    private final int type = 6;// type否	评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1）3资讯4自发赛评论6悬赏令
    //    private int parentId;不传  parentId	否	string	一级评论ID（一级评论列表，不传；楼中楼回复列表接口时，必传。）
    private int replySize = 10;//   replySize	否	string	楼中楼回复数量（不传默认返回5条）
    private final int hasHot = 1;//hasHot	否	string	当需要返回热门评论列表（10条），则传hasHot=1
    private int isLast;
    private String hasQuickComment = "1";//是否包含快速评论1是0否

    private LinearLayoutManager mLayoutManager;
    RecyclerView recyclerView;
    private User user;
    private CommentsSectionAdapter adapter;
    private List<FirstCommentDetail> newestCommentlist = new ArrayList<FirstCommentDetail>();//最新评论
    private List<FirstCommentDetail> hotCommentList = new ArrayList<FirstCommentDetail>();//热门评论
    int lastVisibleItem = 0;
    private int current;//item在recyclerview中的位置
    private Dialog mDialog;

    private String parentId;//    parentId	否	string	一级评论ID
    private String replyId;//    replyId	否	string	楼中楼回复对象的评论ID

    private final int numberWords = 200;//限制输入的字数
    private int words;
    private String replyListId;//评论楼中楼id
    private int replyListPosition;//在楼中楼数据中的位置
    private final int ISREFER = 1;

    private LinearLayoutManager quickCommentManager;
    private QuickCommentAdapter quickCommentAdapter;
    private List<QuickCommentDetail> quickCommentDetailList = new ArrayList<QuickCommentDetail>();

    private Observerable observerable = Observerable.getInstance();

    private int commentTotal;//评论总数
    private boolean isDelectComment = false;//删掉的是否是评论

    public static FragmentRewardComment newInstance(String id) {
        FragmentRewardComment fragment = new FragmentRewardComment();
        Bundle bundle = new Bundle();
        bundle.putString(REWARD_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reward_comment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        context = getActivity();
        resources = context.getResources();
        initView();
    }

    private void initView() {
        rewardId = getArguments().getString(REWARD_ID);

        refreshComment.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        recyclerView = refreshComment.getRefreshableView();
        refreshComment.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                refreshComment.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (isLast == 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            page++;
                            requestData();
                        }
                    }, 1000);
                } else {
                    refreshComment.onRefreshComplete();
                    if (!newestCommentlist.isEmpty() && newestCommentlist.size() > 1) {
                        showToast(context.getResources().getString(R.string.load_no));
                    }
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getString(R.string.noNeteork));
                }
            }
        });

        //评论列表
        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        adapter = new CommentsSectionAdapter(context, newestCommentlist, hotCommentList);
        recyclerView.setAdapter(adapter);
        adapter.setProcessTheData(this);

        //快速评论
        quickCommentManager = new LinearLayoutManager(context);
        quickCommentManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        quickCommentAdapter = new QuickCommentAdapter(context, quickCommentDetailList);
        rvQuickComment.setLayoutManager(quickCommentManager);
        rvQuickComment.setAdapter(quickCommentAdapter);
        quickCommentAdapter.setOnClickItemListener(this);

        monitorEditView();
        page = 1;
        requestData();
        observerRecyclerViewIsTop();
    }

    @OnClick({R.id.reawrdUpLlBack, R.id.fragmentRewardLlCommentSend})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reawrdUpLlBack://返回
                observerable.notifyChange(Observerable.ObserverableType.REWARD_COMMENT, 2);
                break;
            case R.id.fragmentRewardLlCommentSend://评论
                submitComment(null);
                break;
        }
    }

    @Override
    public void onClickItem(String id) {
        if (Utils.isFastDoubleClick()) {
            showToast(context.getResources().getString(R.string.please_click_often));
        } else {
            submitComment(id);
        }
    }

    /**
     * 提交评论
     */
    private void submitComment(String quickCommentId) {
        message = etConnent.getText().toString().trim();

        if (TextUtils.isEmpty(quickCommentId) && TextUtils.isEmpty(message)) {
            showToast("请输入评论");
            return;
        }

        Map<String, String> map = new HashMap();
        user = WangYuApplication.getUser(context);
        if (user != null) {
            map.put("amuseId", rewardId + "");
            map.put("userId", user.getId() + "");
            map.put("token", user.getToken() + "");

            if (TextUtils.isEmpty(quickCommentId)) {//为空时表明是正常的评论。不为空时为快速评论
                map.put("content", Utils.replaceBlank(message));
            } else {
                map.put("quickCommentId", quickCommentId);
            }

            map.put("type", type + "");// type否	string	评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1）
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_COMMENT, map, HttpConstant.AMUSE_COMMENT);
        } else {
            toLogin();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            page = 1;
            requestData();
        }
    }

    /**
     * 请求评论数据列表
     */
    private void requestData() {
        showLoading();
        user = WangYuApplication.getUser(context);
        Map<String, String> map = new HashMap<>();
        map.put("amuseId", rewardId + "");
        map.put("page", page + "");
        map.put("size", size + "");
        map.put("type", type + "");
        map.put("hasHot", hasHot + "");
        map.put("replySize", replySize + "");
        if (page == 1) {//当为1时，显示快速评论的内容
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.V4_QUICK_COMMENT_LIST, map, HttpConstant.V4_QUICK_COMMENT_LIST);
        }
        if (user != null) {
            map.put("userId", user.getId() + "");
            map.put("token", user.getToken() + "");
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.V2_AMUSE_COMMENT_LIST, map, HttpConstant.V2_AMUSE_COMMENT_LIST);
    }


    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        refreshComment.onRefreshComplete();
        try {
            if (method.equals(HttpConstant.V4_QUICK_COMMENT_LIST)) {
                //快速评论数据
                if (object.has("object") && !TextUtils.isEmpty(object.getString("object").toString())) {
                    quickCommentDetailList.clear();
                    List<QuickCommentDetail> newQuickCommentDetail = new Gson().fromJson(object.getString("object").toString(), new TypeToken<List<QuickCommentDetail>>() {
                    }.getType());
                    quickCommentDetailList.addAll(newQuickCommentDetail);
                    quickCommentAdapter.notifyDataSetChanged();
                    rvQuickComment.setVisibility(View.VISIBLE);
                    viewLine.setVisibility(View.VISIBLE);
                } else {
                    rvQuickComment.setVisibility(View.GONE);
                    viewLine.setVisibility(View.GONE);
                }
            } else if (method.equals(HttpConstant.V2_AMUSE_COMMENT_LIST)) {//评论列表
                if (0 == object.getInt("code") && object.has("object")) {
                    analysisCommentData(object.getJSONObject("object"));
                }
            } else if (method.equals(HttpConstant.DEL_COMMENT)) {//删除
                if (isDelectComment) {
                    showCommentNum(commentTotal - 1);
                    isDelectComment = false;
                }
                deleteForSuccess();
            } else if (method.equals(HttpConstant.V2_COMMENT_PRAISE)) {//点赞
                BroadcastController.sendUserChangeBroadcase(context);
                updatePraiseStatu();
            } else if (method.equals(HttpConstant.AMUSE_COMMENT)) {//提交评论
                page = 1;
                size = 10;
                requestData();
                etConnent.setText("");
                etConnent.setHint(resources.getString(R.string.speak));
//                showToast("发表成功");
                new MyAlertView.Builder(context).setMessage("1").createComentSuccessOrFail();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        showToast(errMsg);
        refreshComment.onRefreshComplete();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        refreshComment.onRefreshComplete();
        try {
            if (method.equals(HttpConstant.DEL_COMMENT)) {//删除
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                isDelectComment = false;
                showToast("删除失败");
            } else if (method.equals(HttpConstant.AMUSE_COMMENT)) {//提交评论
                if (1 == object.getInt("code")) {
                    showToast(object.getString("result"));
                } else {
                    new MyAlertView.Builder(context).setMessage("0").createComentSuccessOrFail();
                }
            } else {
                showToast(object.getString("result"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delectComment(String id, int position) {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            creatDialogForDelect(id);
            current = position;
            isDelectComment = true;
        } else {
            toLogin();
        }
    }

    @Override
    public void deleteReplyReply(String id, int position, int replyPosition) {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            replyListId = id + "";
            replyListPosition = replyPosition;
            creatDialogForDelect(id);
            current = position;
        } else {
            toLogin();
        }
    }

    @Override
    public void praiseComment(String id, int position) {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            current = position;
            Map<String, String> map = new HashMap<>();
            map.put("commentId", id + "");
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.V2_COMMENT_PRAISE, map, HttpConstant.V2_COMMENT_PRAISE);
        } else {
            toLogin();
        }
    }

    @Override
    public void replyComment(String id, int position, String nikeName) {
        Intent intent = new Intent(context, PersonalCommentDetail.class);
        intent.putExtra("amuseId", rewardId);
        intent.putExtra("type", type);
        intent.putExtra("parentId", id);
        intent.putExtra("isPopupKeyboard", ISREFER);//判断是否弹出键盘   1  弹出  0不弹出   默认不弹
        startActivityForResult(intent, 1);
    }

    @Override
    public void lookComment(String id, int position, String nikeName) {
        Intent intent = new Intent(context, PersonalCommentDetail.class);
        intent.putExtra("amuseId", rewardId);
        intent.putExtra("type", type);
        intent.putExtra("parentId", id);
        intent.putExtra("isPopupKeyboard", 0);
        startActivityForResult(intent, ISREFER);
    }

    /**
     * 解析评论列表数据
     *
     * @param object
     */

    private void analysisCommentData(JSONObject object) {
        try {
            isLast = object.getInt("isLast");
            if (page == 1) {
                newestCommentlist.clear();
                hotCommentList.clear();
                newestCommentlist.add(0, null);
                if (object.has("hotList") && !TextUtils.isEmpty(object.getString("hotList").toString())) {
                    List<FirstCommentDetail> newHotCommentlist = new Gson().fromJson(object.getString("hotList").toString(), new TypeToken<List<FirstCommentDetail>>() {
                    }.getType());
                    if (!newHotCommentlist.isEmpty()) {
                        hotCommentList.add(0, null);
                        hotCommentList.addAll(newHotCommentlist);
                    }
                }
                showCommentNum(object.has("total") ? object.getInt("total") : 0);
            }

            if (object.has("list") && !TextUtils.isEmpty(object.getString("list").toString())) {
                List<FirstCommentDetail> newNewestCommentlist = new Gson().fromJson(object.getString("list").toString(), new TypeToken<List<FirstCommentDetail>>() {
                }.getType());
                newestCommentlist.addAll(newNewestCommentlist);
            }
            adapter.setData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * x显示评论总数
     *
     * @param num
     */
    private void showCommentNum(int num) {
        if (num != 0) {
            commentTotal = num;
            observerable.notifyChange(Observerable.ObserverableType.REWARD_COMMENT, 5, commentTotal);
            tvTitle.setText(getResources().getString(R.string.msg_comment) + "(" + num + ")");
        } else {
            tvTitle.setText(getResources().getString(R.string.msg_comment));
        }
    }


    /**
     * 更新点赞状态
     */
    private void updatePraiseStatu() {
        int praise;
        if (!hotCommentList.isEmpty()) {
            if (current < hotCommentList.size()) {
                if (hotCommentList.get(current).getIsPraise() == 0) {
                    hotCommentList.get(current).setIsPraise(1);
                    praise = hotCommentList.get(current).getLikeCount();
                    hotCommentList.get(current).setLikeCount(praise + 1);
                } else {
                    hotCommentList.get(current).setIsPraise(0);
                    praise = hotCommentList.get(current).getLikeCount();
                    hotCommentList.get(current).setLikeCount(praise - 1);
                }
            } else {
                if (newestCommentlist.get(current - hotCommentList.size()).getIsPraise() == 0) {
                    newestCommentlist.get(current - hotCommentList.size()).setIsPraise(1);
                    praise = newestCommentlist.get(current - hotCommentList.size()).getLikeCount();
                    newestCommentlist.get(current - hotCommentList.size()).setLikeCount(praise + 1);
                } else {
                    newestCommentlist.get(current - hotCommentList.size()).setIsPraise(0);
                    praise = newestCommentlist.get(current - hotCommentList.size()).getLikeCount();
                    newestCommentlist.get(current - hotCommentList.size()).setLikeCount(praise - 1);
                }
            }
        } else {
            if (newestCommentlist.get(current).getIsPraise() == 0) {
                newestCommentlist.get(current).setIsPraise(1);
                praise = newestCommentlist.get(current).getLikeCount();
                newestCommentlist.get(current).setLikeCount(praise + 1);
            } else {
                newestCommentlist.get(current).setIsPraise(0);
                praise = newestCommentlist.get(current).getLikeCount();
                newestCommentlist.get(current).setLikeCount(praise - 1);
            }
        }
        adapter.notifyDataSetChanged();
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
                Map<String, String> map = new HashMap<>();
                map.put("commentId", id + "");
                map.put("userId", user.getId());
                map.put("token", user.getToken());
                sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DEL_COMMENT, map, HttpConstant.DEL_COMMENT);
                mDialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                isDelectComment = false;
            }
        });
        mDialog.show();
    }

    /**
     * 监听输入的变化
     */
    private void monitorEditView() {
        TextWatcher textWatcher = new TextWatcher() {
            String temp;
            String myTemp;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s.toString();
                myTemp = Utils.replaceBlank(temp);
                if (!myTemp.equals(temp)) {
                    etConnent.setText(myTemp);
                    etConnent.setSelection(myTemp.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                words = numberWords - myTemp.length();
                if (words != 200) {
                    tvNumber.setText("剩" + words + "字");
                    tvNumber.setVisibility(View.VISIBLE);
                    llSend.setEnabled(true);
                    llSend.setBackgroundResource(R.drawable.shape_orange_bg_corner);
                } else {
                    tvNumber.setVisibility(View.GONE);
                    llSend.setEnabled(false);
                    llSend.setBackgroundResource(R.drawable.shape_conners_4_solid_cdcdcd);
                }
            }
        };
        etConnent.addTextChangedListener(textWatcher);
        tvNumber.setVisibility(View.GONE);
        llSend.setEnabled(false);
        llSend.setBackgroundResource(R.drawable.shape_conners_4_solid_cdcdcd);
    }

    private void toLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * 删除成功后的处理
     */
    private void deleteForSuccess() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        showToast("删除成功");
        int replycount;
        if (!TextUtils.isEmpty(replyListId)) {//删除的是楼中楼的评论
            if (hotCommentList.isEmpty()) {
                replycount = newestCommentlist.get(current).getReplyCount();
                if (replycount > 1) {
                    newestCommentlist.get(current).setReplyCount(replycount - 1);
                }
                newestCommentlist.get(current).getReplyList().remove(replyListPosition);
            } else {
                if (current < hotCommentList.size()) {
                    replycount = hotCommentList.get(current).getReplyCount();
                    if (replycount > 1) {
                        hotCommentList.get(current).setReplyCount(replycount - 1);
                    }
                    hotCommentList.get(current).getReplyList().remove(replyListPosition);
                } else {
                    replycount = newestCommentlist.get(current - hotCommentList.size()).getReplyCount();
                    if (replycount > 1) {
                        newestCommentlist.get(current - hotCommentList.size()).setReplyCount(replycount - 1);
                    }
                    newestCommentlist.get(current - hotCommentList.size()).getReplyList().remove(replyListPosition);
                }
            }
            replyListId = "";
        } else {//删除的是一层评论
            if (hotCommentList.isEmpty()) {
                newestCommentlist.remove(current);
            } else {
                if (current < hotCommentList.size()) {
                    hotCommentList.remove(current);
                } else {
                    newestCommentlist.remove(current - hotCommentList.size());
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    int firstItem;

    private void observerRecyclerViewIsTop() {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (adapter != null && newState == RecyclerView.SCROLL_STATE_IDLE && firstItem == 0) {
                    observerable.notifyChange(Observerable.ObserverableType.REWARD_COMMENT, 1);
                } else {
                    observerable.notifyChange(Observerable.ObserverableType.REWARD_COMMENT, 0);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstItem = mLayoutManager.findFirstVisibleItemPosition();
            }
        });
    }

    @Override
    public void refreView() {
        page = 1;
        requestData();
    }
}
