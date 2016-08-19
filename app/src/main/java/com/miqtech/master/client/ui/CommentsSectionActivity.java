package com.miqtech.master.client.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.MyAlertView;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 评论区
 * Created by Administrator on 2016/1/8.
 */
public class CommentsSectionActivity extends BaseActivity implements View.OnClickListener,
        CommentsSectionAdapter.ProcessTheData, QuickCommentAdapter.OnClickItemListener {
    RecyclerView recyclerView;
    @Bind(R.id.prrvCommentList)
    PullToRefreshRecyclerView prrvCommentList;
    @Bind(R.id.buttom_send_comments_section)
    LinearLayout sendMessageLl;
    @Bind(R.id.edtComment)
    EditText sendMessageEt;
    @Bind(R.id.tvSend)
    TextView sendMessageTv;
    @Bind(R.id.tvNumber)
    TextView tvNumber;

    @Bind(R.id.fragmentRewardCommentRvQuickComment)
    RecyclerView rvQuickComment;//快速评论
    @Bind(R.id.fragmentRewardCommentLine)
    View viewLine;
    private LinearLayoutManager quickCommentManager;
    private QuickCommentAdapter quickCommentAdapter;
    private List<QuickCommentDetail> quickCommentDetailList = new ArrayList<QuickCommentDetail>();

    private String message;//要发表的评论
    private int page = 1;//页数
    private int size = 10;//每页的个数
    private int type = 1;// type否	string	评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1）3资讯4自发赛评论6悬赏令7直播间评论8直播视频评论
    //    private int parentId;不传  parentId	否	string	一级评论ID（一级评论列表，不传；楼中楼回复列表接口时，必传。）
    private int replySize = 10;//   replySize	否	string	楼中楼回复数量（不传默认返回5条）
    private final int hasHot = 1;//hasHot	否	string	当需要返回热门评论列表（10条），则传hasHot=1
    private String amuseId;//娱乐赛id    amuseId	是	string	娱乐赛ID
    private int isLast;

    private Context context;
    private LinearLayoutManager mLayoutManager;
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
    private int isRefre = 0;//返回时0表示不需要刷新，1需要刷新

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_comments_section);
        ButterKnife.bind(this);
        context = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        setLeftIncludeTitle("评论区");
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        sendMessageTv.setOnClickListener(this);
        prrvCommentList.setMode(PullToRefreshBase.Mode.BOTH);
        recyclerView = prrvCommentList.getRefreshableView();
        prrvCommentList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                page = 1;
                size = 10;
                requestData();
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
                    prrvCommentList.onRefreshComplete();
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
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        //快速评论
        quickCommentManager = new LinearLayoutManager(context);
        quickCommentManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        quickCommentAdapter = new QuickCommentAdapter(context, quickCommentDetailList);
        rvQuickComment.setLayoutManager(quickCommentManager);
        rvQuickComment.setAdapter(quickCommentAdapter);
        quickCommentAdapter.setOnClickItemListener(this);

        amuseId = getIntent().getStringExtra("amuseId");
        type = getIntent().getIntExtra("type", -1);
        adapter = new CommentsSectionAdapter(context, newestCommentlist, hotCommentList);
        recyclerView.setAdapter(adapter);
        adapter.setProcessTheData(this);

        monitorEditView();
    }

    @Override
    protected void initData() {
        super.initData();
        page = 1;
        size = 10;
        requestData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.tvSend:
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
        message = sendMessageEt.getText().toString().trim();
        if (TextUtils.isEmpty(quickCommentId) && TextUtils.isEmpty(message)) {
            showToast("请输入评论");
            return;
        }
        Map<String, String> map = new HashMap();
        user = WangYuApplication.getUser(context);
        if (user != null) {
            map.put("amuseId", amuseId + "");
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

    /**
     * 请求评论数据列表
     */
    private void requestData() {
        showLoading();
        user = WangYuApplication.getUser(context);
        Map<String, String> map = new HashMap<>();
        map.put("amuseId", amuseId + "");
        map.put("page", page + "");
        map.put("size", size + "");
        map.put("type", type + "");
        map.put("hasHot", hasHot + "");
        map.put("hasHot", hasHot + "");
        map.put("replySize", replySize + "");
        if (page == 1 && type == 3) {//当为1且是资讯时，显示快速评论的内容
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.V4_QUICK_COMMENT_LIST, map, HttpConstant.V4_QUICK_COMMENT_LIST);
        }else{
            rvQuickComment.setVisibility(View.GONE);
            viewLine.setVisibility(View.GONE);
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
        prrvCommentList.onRefreshComplete();
        try {
            if (method.equals(HttpConstant.V2_AMUSE_COMMENT_LIST)) {//评论列表
                if (0 == object.getInt("code") && object.has("object")) {
                    analysisCommentData(object.getJSONObject("object"));
                }
            } else if (method.equals(HttpConstant.DEL_COMMENT)) {//删除
                isRefre = 1;
                deleteForSuccess();
            } else if (method.equals(HttpConstant.V2_COMMENT_PRAISE)) {//点赞
                isRefre = 1;
                BroadcastController.sendUserChangeBroadcase(context);
                updatePraiseStatu();
            } else if (method.equals(HttpConstant.AMUSE_COMMENT)) {//提交评论
                page = 1;
                size = 10;
                requestData();
                sendMessageEt.setText("");
                sendMessageEt.setHint("发表评论");
//                showToast("发表成功");
                new MyAlertView.Builder(context).setMessage("1").createComentSuccessOrFail();
                isRefre = 1;
            } else if (method.equals(HttpConstant.V4_QUICK_COMMENT_LIST)) {
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        showToast(errMsg);
        prrvCommentList.onRefreshComplete();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        prrvCommentList.onRefreshComplete();
        try {
            if (method.equals(HttpConstant.DEL_COMMENT)) {//删除
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                showToast("删除失败");
            } else if (method.equals(HttpConstant.AMUSE_COMMENT)) {//提交评论
//                new MyAlertView.Builder(context).setMessage("0").createComentSuccessOrFail();
                showToast(object.getString("result"));
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
//            showToast(getResources().getString(R.string.pleaseLogin));
            toLogin();
        }
    }

    @Override
    public void replyComment(String id, int position, String nikeName) {
        Intent intent = new Intent(context, PersonalCommentDetail.class);
        intent.putExtra("amuseId", amuseId);
        intent.putExtra("type", type);
        intent.putExtra("parentId", id);
        intent.putExtra("isPopupKeyboard", ISREFER);//判断是否弹出键盘   1  弹出  0不弹出   默认不弹
//        startActivity(intent);
        startActivityForResult(intent, 1);
    }

    @Override
    public void lookComment(String id, int position, String nikeName) {
        Intent intent = new Intent(context, PersonalCommentDetail.class);
        intent.putExtra("amuseId", amuseId);
        intent.putExtra("type", type);
        intent.putExtra("parentId", id);
        intent.putExtra("isPopupKeyboard", 0);
//        startActivity(intent);
        startActivityForResult(intent, ISREFER);
    }

//    @Override
//    public void hideFooterView(View view) {
//        if (isLast != 0 && view != null) {
//            view.setVisibility(View.GONE);
//        }
//    }


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
                    List<FirstCommentDetail> newHotCommentlist = GsonUtil.getList(object.getString("hotList").toString(), FirstCommentDetail.class);
                    if (!newHotCommentlist.isEmpty()) {
                        hotCommentList.add(0, null);
                        hotCommentList.addAll(newHotCommentlist);
                        LogUtil.d("OfficalEventActivity", "热门评论数据不为空" + newHotCommentlist.size());
                    }
                }
            }

            if (object.has("list") && !TextUtils.isEmpty(object.getString("list").toString())) {
                List<FirstCommentDetail> newNewestCommentlist = GsonUtil.getList(object.getString("list").toString(), FirstCommentDetail.class);
                newestCommentlist.addAll(newNewestCommentlist);
                adapter.notifyDataSetChanged();
                LogUtil.d("OfficalEventActivity", "最新评论数据不为空" + newNewestCommentlist.size());
            } else {
                adapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
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
                    sendMessageEt.setText(myTemp);
                    sendMessageEt.setSelection(myTemp.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(myTemp)) {
                    sendMessageTv.setTextColor(getResources().getColor(R.color.orange));
                } else {
                    sendMessageTv.setTextColor(getResources().getColor(R.color.font_gray));
                }

                words = numberWords - myTemp.length();
                if (words != 200) {
                    tvNumber.setText("剩" + words + "字");
                    tvNumber.setVisibility(View.VISIBLE);
                } else {
                    tvNumber.setVisibility(View.GONE);
                }
            }
        };
        sendMessageEt.addTextChangedListener(textWatcher);
    }

    private void toLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ISREFER) {
            int myIsRefre = data.getIntExtra("isRefre", -1);
            if (myIsRefre == 1) {
                initData();
                isRefre = 1;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isRefre", isRefre);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
