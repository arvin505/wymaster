package com.miqtech.master.client.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.PersonalCommentDetailAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.FirstCommentDetail;
import com.miqtech.master.client.entity.SecondCommentDetail;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CircleImageView;
import com.miqtech.master.client.view.MyListView;
import com.miqtech.master.client.view.MyScrollView;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshScrollView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 赛事单个评价的详情
 * Created by zhaosentao on 2016/1/7.
 */
public class PersonalCommentDetail extends BaseActivity implements View.OnClickListener,
        PersonalCommentDetailAdapter.DealWithData {

    @Bind(R.id.refresh_personal_comment)
    PullToRefreshScrollView refresh;
    @Bind(R.id.edtComment)
    EditText edtComment;
    @Bind(R.id.tvSend)
    TextView tvSend;
    @Bind(R.id.tvNumber)
    TextView tvNumber;

    @Bind(R.id.comment_top_title_ll_item)
    LinearLayout comment_top_title_ll;//顶部热门评价这几个字栏
    @Bind(R.id.comment_top_title_tv_item)
    TextView comment_top_title_tv;
    @Bind(R.id.ivUserHeader)
    CircleImageView ivUserHeader;//头像
    @Bind(R.id.tvUserName)
    TextView tvUserName;//名字
    @Bind(R.id.tvContent)
    TextView tvContent;//评论内容
    @Bind(R.id.tvTime)
    TextView tvTime;//时间
    @Bind(R.id.tvDelect)
    TextView tvDelect;//删除
    @Bind(R.id.praise_comment_ll_item)
    LinearLayout praiseLl;//赞
    @Bind(R.id.praise_comment_tv_item)
    TextView praiseTv;//赞的显示数量
    @Bind(R.id.praise_comment_iv_item)
    ImageView praiseIv;//赞的图标
    @Bind(R.id.reply_comment_ll_item)
    LinearLayout repltLl;//回复
    @Bind(R.id.comment_detail_ll_item)
    LinearLayout commentDetailLl;//评论的内容（包括头像，名称等等）
    @Bind(R.id.line_view_comment_item)
    View line;//底下的那条线
    @Bind(R.id.reply_reply_comment_ll_item)
    LinearLayout replyReplyLl;
    @Bind(R.id.myListView)
    MyListView listView;

    private String comment;//要发表的评论
    private Context context;
    private PersonalCommentDetailAdapter adapter;
    private List<SecondCommentDetail> secondCommentDetailList = new ArrayList<SecondCommentDetail>();
    private FirstCommentDetail firstCommentDetail = new FirstCommentDetail();
    private User user;

    private int page = 1;//页数
    private int size = 10;//每页的个数
    private int type = 1;// type否	string	评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1）
    private String parentId;//  parentId	否	string	一级评论ID（一级评论列表，不传；楼中楼回复列表接口时，必传。）
    private int replySize = 30;//   replySize	否	string	楼中楼回复数量（不传默认返回5条）
    private final int hasHot = 1;//hasHot	否	string	当需要返回热门评论列表（10条），则传hasHot=1
    private String amuseId;//娱乐赛id    amuseId	是	string	赛事ID
    private String replyIDTwo;//楼中楼被回复的评论ID（从“我的评论”调用跳转时，传此参数）
    private int isLast;
    private int overtwenty;//"我的消息"  是否超过20条   0否1是
    private int lastVisibleItem;
    private String nikeName;

    private boolean isReplyComment = true;
    private String replyStr = "回复 ";
    private String replyId;

    private DisplayMetrics displayMetrics;
    private int windowHigh_half;
    private Display display;

    private int isPopupKeyboard = 0;//判断是否弹出键盘   1  弹出  0不弹出   默认不弹
    private InputMethodManager imm;
    private final int numberWords = 200;//限制输入的字数
    private int words;
    private Dialog mDialog;
    private int listPosition;
    private int isRefre = 0;//返回时0表示不需要刷新，1需要刷新

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_personal_comment_detail);
        ButterKnife.bind(this);
        context = this;
        amuseId = getIntent().getStringExtra("amuseId");
        parentId = getIntent().getStringExtra("parentId");
        type = getIntent().getIntExtra("type", -1);
        isPopupKeyboard = getIntent().getIntExtra("isPopupKeyboard", 0);
        replyIDTwo = getIntent().getStringExtra("replyIDTwo");

        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();

        displayMetrics = new DisplayMetrics();
        display = ((Activity) context).getWindowManager().getDefaultDisplay();
        display.getMetrics(displayMetrics);
        windowHigh_half = display.getHeight() / 2;

        setLeftBtnImage(R.drawable.back);
        setRightBtnImage(R.drawable.personal_comment_spot);


        getLeftBtn().setOnClickListener(this);
        getRightBtn().setOnClickListener(this);
        repltLl.setOnClickListener(this);//回复
        ivUserHeader.setOnClickListener(this);//头像
        tvContent.setOnClickListener(this);//回复
        praiseLl.setOnClickListener(this);
        tvSend.setOnClickListener(this);
        getRightBtn().setVisibility(View.GONE);

        adapter = new PersonalCommentDetailAdapter(context, secondCommentDetailList);
        listView.setAdapter(adapter);
        adapter.setOnDealWithData(this);

        replyId = parentId;
        if (isPopupKeyboard == 1) {
            edtComment.requestFocus();
            InputMethodManager imm = (InputMethodManager) edtComment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
        }
        monitorEditView();

        refresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<MyScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<MyScrollView> refreshView) {
                page = 1;
                size = 10;
                replySize = 30;
                requestData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<MyScrollView> refreshView) {
                if (isLast == 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            page++;
                            requestData();
                        }
                    }, 1000);
                } else {
                    refresh.onRefreshComplete();
                    showToast(getResources().getString(R.string.load_no));
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getString(R.string.noNeteork));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft://返回
                onBackPressed();
                break;
            case R.id.tvSend://提交评论
                submitComment();
                break;
            case R.id.ibRight://点击弹出举报
                creatRportDialog();
                break;
            case R.id.tvRightHandle://查看全部评论
                replyIDTwo = "";
                overtwenty = 0;
                getRightTextview().setVisibility(View.GONE);
                initData();
                break;
            case R.id.reply_comment_ll_item://回复  回复楼主
                replayPersionalComment();
                break;
            case R.id.tvContent://回复  回复楼主
                replayPersionalComment();
                break;
            case R.id.ivUserHeader://点击头像跳转
                Intent intent = new Intent(context, PersonalHomePageActivity.class);
                intent.putExtra("id", firstCommentDetail.getUserId() + "");
                context.startActivity(intent);
                break;
            case R.id.praise_comment_ll_item:
                if (firstCommentDetail != null) {
                    user = WangYuApplication.getUser(context);
                    if (user != null) {
                        showLoading();
                        Map<String, String> map = new HashMap<>();
                        map.put("commentId", parentId + "");
                        map.put("userId", user.getId());
                        map.put("token", user.getToken());
                        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.V2_COMMENT_PRAISE, map, HttpConstant.V2_COMMENT_PRAISE);
                    } else {
                        toLogin();
                    }
                }
                break;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        page = 1;
        size = 10;
        replySize = 30;
        showLoading();
        requestData();
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
        map.put("parentId", parentId + "");
        map.put("replySize", replySize + "");
        if (user != null) {
            map.put("userId", user.getId() + "");
            map.put("token", user.getToken() + "");
        }
        if (!TextUtils.isEmpty(replyIDTwo)) {
            map.put("replyId", replyIDTwo);
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.V2_AMUSE_COMMENT_LIST, map, HttpConstant.V2_AMUSE_COMMENT_LIST);
    }


    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        refresh.onRefreshComplete();
        try {
            if (method.equals(HttpConstant.V2_AMUSE_COMMENT_LIST)) {
                if (0 == object.getInt("code") && object.has("object")) {
                    analysisJSONData(object.getJSONObject("object"));
                }
            } else if (method.equals(HttpConstant.AMUSE_COMMENT)) {
                showToast("评价成功");
                if (isReplyComment) {//回复楼主
                    initData();
                } else {//回复层主
                    addReplyReply(object.getString("object").toString());
                }
                isReplyComment = true;
                edtComment.setText("");
                replyId = parentId;
                edtComment.setHint(replyStr + nikeName);
                isRefre = 1;
            } else if (method.equals(HttpConstant.V2_COMMENT_PRAISE)) {
                int praiseNum;
                BroadcastController.sendUserChangeBroadcase(context);
                if (firstCommentDetail.getIsPraise() == 0) {
                    firstCommentDetail.setIsPraise(1);
                    praiseNum = firstCommentDetail.getLikeCount();
                    firstCommentDetail.setLikeCount(praiseNum + 1);
                    praiseTv.setText(Utils.getnumberForms(praiseNum + 1, context));
                    praiseIv.setImageResource(R.drawable.comment_praise_yes);
                } else {
                    firstCommentDetail.setIsPraise(0);
                    praiseNum = firstCommentDetail.getLikeCount();
                    firstCommentDetail.setLikeCount(praiseNum - 1);
                    praiseTv.setText(Utils.getnumberForms(praiseNum - 1, context));
                    praiseIv.setImageResource(R.drawable.comment_praise_no);
                }
                isRefre = 1;
            } else if (method.equals(HttpConstant.DEL_COMMENT)) {//删除
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                showToast("删除成功");
                secondCommentDetailList.remove(listPosition);
                adapter.notifyDataSetChanged();
                isRefre = 1;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
        showToast(errMsg);
        refresh.onRefreshComplete();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        refresh.setRefreshing(false);
        try {
            if (method.equals(HttpConstant.AMUSE_COMMENT)) {
                if (object.getInt("code") == 1) {
                    showToast(object.getString("result"));
                } else {
                    showToast("评价失败");
                    isReplyComment = true;
                    edtComment.setHint(replyStr + nikeName);
                }
            } else if (method.equals(HttpConstant.DEL_COMMENT)) {//删除
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                showToast("删除失败");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析评论数据
     *
     * @param object
     */
    private void analysisJSONData(JSONObject object) {
        if (object == null) {
            return;
        }
        try {
            isLast = object.getInt("isLast");
            if (page == 1) {
                firstCommentDetail = null;
                FirstCommentDetail newFirstCommentDetail = GsonUtil.getBean(object.getString("parent").toString().trim(), FirstCommentDetail.class);
                firstCommentDetail = newFirstCommentDetail;
                showCommentData();
                secondCommentDetailList.clear();
                if (object.has("overtwenty")) {
                    overtwenty = object.getInt("overtwenty");
                    if (overtwenty == 1) {
                        getRightBtn().setVisibility(View.GONE);
                        setRightTextView("全部评论");
                        getRightTextview().setOnClickListener(this);
                    }
                }
                user = WangYuApplication.getUser(context);
                if (overtwenty == 0) {
                    if (user != null && user.getId().equals(firstCommentDetail.getUserId() + "")) {
                        getRightBtn().setVisibility(View.GONE);
                    } else {
                        getRightBtn().setVisibility(View.VISIBLE);
                    }
                }
                setLeftIncludeTitle(firstCommentDetail.getNickname() + "的评论");
                edtComment.setHint(replyStr + firstCommentDetail.getNickname());
                nikeName = firstCommentDetail.getNickname();
            }
            List<SecondCommentDetail> newSecondCommentDetailList = GsonUtil.getList(object.getString("list").toString().trim(), SecondCommentDetail.class);
            secondCommentDetailList.addAll(newSecondCommentDetailList);
            adapter.notifyDataSetChanged();
            if (secondCommentDetailList.isEmpty()) {
                listView.setVisibility(View.GONE);
            } else {
                listView.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void replyToReply(int position) {
        if (!secondCommentDetailList.isEmpty() && position < secondCommentDetailList.size()) {
            edtComment.setHint(replyStr + secondCommentDetailList.get(position).getNickname());
            replyId = secondCommentDetailList.get(position).getId();
            edtComment.requestFocus();
            imm = (InputMethodManager) edtComment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
        }
    }

    @Override
    public void deleteReplyReply(int position) {
        if (!secondCommentDetailList.isEmpty() && position < secondCommentDetailList.size()) {
            listPosition = position;
            user = WangYuApplication.getUser(context);
            if (user != null) {
                creatDialogForDelect(secondCommentDetailList.get(position).getId());
            } else {
                toLogin();
            }
        }
    }

    /**
     * 提交评论
     */
    private void submitComment() {
        comment = edtComment.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            showToast("请输入评论");
            return;
        }

        Map<String, String> map = new HashMap();
        user = WangYuApplication.getUser(context);
        if (user != null) {
            showLoading();
            map.put("amuseId", amuseId + "");
            map.put("userId", user.getId() + "");
            map.put("token", user.getToken() + "");
            map.put("content", Utils.replaceBlank(comment));
            map.put("type", type + "");// type否	string	评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1）
            map.put("parentId", parentId + "");// parentId	否	string	一级评论ID
            map.put("replyId", replyId + "");//replyId	否	string	楼中楼回复对象的评论ID
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_COMMENT, map, HttpConstant.AMUSE_COMMENT);
        } else {
            toLogin();
        }
    }

    /**
     * 发表评论成功后  刷新界面
     *
     * @param object
     */
    private void addReplyReply(String object) {
        SecondCommentDetail bean = GsonUtil.getBean(object, SecondCommentDetail.class);
        secondCommentDetailList.add(bean);
        adapter.notifyDataSetChanged();
    }


    /**
     * 创建举报弹框
     */
    private void creatRportDialog() {
        final Dialog dialog = new Dialog(context, R.style.delete_style);
        dialog.setContentView(R.layout.layout_report_personal_comment);
        RelativeLayout linearLayout = (RelativeLayout) dialog.findViewById(R.id.report_person_comment);
        int[] location = new int[2];
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        getRightBtn().getLocationOnScreen(location);
        params.x = location[0] / 2 + getRightBtn().getWidth() / 2;
        params.y = -windowHigh_half;
        dialog.getWindow().setAttributes(params);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(context, ReportActivity.class);
                ii.putExtra("type", 2);// 举报类别:1.用户2.评论3.约战4网吧评论
                ii.putExtra("targetId", parentId);// type=1被举报用户的id,type=2评论id,type=3约战id,type=4网吧评价id
                startActivity(ii);
                dialog.dismiss();
            }
        });
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
                    edtComment.setText(myTemp);
                    edtComment.setSelection(myTemp.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(myTemp)) {
                    tvSend.setTextColor(getResources().getColor(R.color.orange));
                } else {
                    tvSend.setTextColor(getResources().getColor(R.color.font_gray));
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
        edtComment.addTextChangedListener(textWatcher);
    }

    private void toLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isRefre", isRefre);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    /**
     * 显示评价的数据
     */
    private void showCommentData() {
        String strDate;
        comment_top_title_ll.setVisibility(View.GONE);
        replyReplyLl.setVisibility(View.GONE);
        //显示头像
        if (!TextUtils.isEmpty(firstCommentDetail.getIcon())) {
            AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + firstCommentDetail.getIcon() + "!small", ivUserHeader);
        } else {
            ivUserHeader.setImageResource(R.drawable.default_head);
        }

        //显示昵称
        if (!TextUtils.isEmpty(firstCommentDetail.getNickname())) {
            tvUserName.setText(firstCommentDetail.getNickname());
        } else {
            tvUserName.setText("");
        }

        //显示评论内容
        if (!TextUtils.isEmpty(firstCommentDetail.getContent())) {
            tvContent.setText(firstCommentDetail.getContent());
        } else {
            tvContent.setText("");
        }

        //显示时间
        if (!TextUtils.isEmpty(firstCommentDetail.getCreateDate())) {
            strDate = TimeFormatUtil.friendlyTime(firstCommentDetail.getCreateDate());
            tvTime.setText(strDate);
        } else {
            tvTime.setText("");
        }

        tvDelect.setVisibility(View.INVISIBLE);

        //显示是否评论的状态
        if (firstCommentDetail.getIsPraise() == 0) {
            praiseIv.setImageResource(R.drawable.comment_praise_no);
        } else if (firstCommentDetail.getIsPraise() == 1) {
            praiseIv.setImageResource(R.drawable.comment_praise_yes);
        }

        line.setVisibility(View.GONE);

        //显示点赞数
        praiseTv.setText(Utils.getnumberForms(firstCommentDetail.getLikeCount(), context));
    }

    private void replayPersionalComment() {
        edtComment.setHint(replyStr + nikeName);
        replyId = parentId;
        edtComment.requestFocus();
        InputMethodManager imm = (InputMethodManager) edtComment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }
}
