package com.miqtech.master.client.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.RecreationCommentAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.FirstCommentDetail;
import com.miqtech.master.client.entity.LiveRoomAnchorInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.CommentsSectionActivity;
import com.miqtech.master.client.ui.LiveRoomActivity;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.PersonalCommentDetail;
import com.miqtech.master.client.ui.SubmitGradesActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CircleImageView;
import com.miqtech.master.client.view.MyListView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/7/27.
 */
public class FragmentTalkLP extends BaseFragment implements  RecreationCommentAdapter.ItemDataDealWith ,View.OnClickListener{
    @Bind(R.id.lvLivePlayComment)
    ListView lvLivePlayComment;
    @Bind(R.id.ivUpAndDownIcon)
    ImageView ivUpAndDownIcon;
    @Bind(R.id.anchorHeader)
    CircleImageView anchorHeader; //主播头像
    @Bind(R.id.anchorTitle)
    TextView anchorTitle;//主播标题
    @Bind(R.id.rlAnchorInformation)
    RelativeLayout rlAnchorInformation;
    @Bind(R.id.ivRecomment)
    ImageView ivRecomment; //评论输入框
    @Bind(R.id.tvAttention)
    TextView tvAttention; //关注按钮
    @Bind(R.id.anchorSex)
    ImageView anchorSex; //主播性别
    @Bind(R.id.onLineNum)
    TextView onLineNum;//在线人数
    @Bind(R.id.fansNum)
    TextView fansNum; //粉丝数量
    @Bind(R.id.tvErrorPage)
    TextView tvErrorPage; //异常页面
    private boolean isFirst=true;
    private Context context;
    private RecreationCommentAdapter adapter;
    private int page = 1;
    private int pageSize = 10;
    private User user;
    private int replySize = 10;//   replySize	否	string	楼中楼回复数量（不传默认返回5条）
    private List<FirstCommentDetail> comments = new ArrayList<FirstCommentDetail>();
    private int listId;
    private Dialog mDialog;
    private String replyListPosition;
    private final int ISREFER = 1;//startActivityForResult(intent,)
    private final int type = 7;//评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1）7直播评论
    private String message; //输入框里面的消息
    private LiveRoomAnchorInfo info;
    private String id;//主播id
    private static final int COMMENT_REQUEST=2;
    private String imgName;//后台返回的图片名
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getContext();
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_talk_lp,container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initView();
        setOnClickListener();
    }

    private void setOnClickListener() {
        ivUpAndDownIcon.setOnClickListener(this);
        ivRecomment.setOnClickListener(this);
        tvAttention.setOnClickListener(this);
    }
    public void setAnchorData(LiveRoomAnchorInfo info){
        setData(info);
    }
    private void setData(LiveRoomAnchorInfo info) {
        this.info=info;
        this.id=info.getUpUserId()+"";
        AsyncImage.loadAvatar(getActivity(), HttpConstant.SERVICE_UPLOAD_AREA + info.getIcon(), anchorHeader);
        anchorTitle.setText(info.getNickname());
        setSubscribeState(info.getIsSubscibe()==1?true:false);
        tvAttention.setOnClickListener(this);
        setFontDiffrentColor(getString(R.string.live_online_num, Utils.calculate(info.getOnlineNum(),10000,"W")),3,getString(R.string.live_online_num, Utils.calculate(info.getOnlineNum(),10000,"W")).length(),onLineNum);
        setFontDiffrentColor(getString(R.string.live_play_fans_num, Utils.calculate(info.getFans(),10000,"W")),3,getString(R.string.live_play_fans_num, Utils.calculate(info.getFans(),10000,"W")).length(),fansNum);
        LogUtil.d(TAG,"订阅状态:::"+info.getIsSubscibe());
        anchorSex.setVisibility(View.VISIBLE);
        if (info.getSex() == 0) {
            anchorSex.setImageResource(R.drawable.live_play_men);
        } else {
          anchorSex.setImageResource(R.drawable.live_play_femen);
        }
        if(info.getIsSubscibe()==1){
            rlAnchorInformation.setVisibility(View.GONE);
        }else{
            rlAnchorInformation.setVisibility(View.VISIBLE);
        }

    }
    private void setFontDiffrentColor(String content, int start, int end, TextView tv) {
        if (tv == null) {
            return;
        }
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.orange)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(style);
    }
    public void setSubscribeState(boolean isSubscribe){
        GradientDrawable bgShape=(GradientDrawable)tvAttention.getBackground();
        if(isSubscribe){
            tvAttention.setText(getResources().getString(R.string.live_room_attentioned));
            tvAttention.setTextColor(getContext().getResources().getColor(R.color.shop_buy_record_gray));
            bgShape.setStroke(Utils.dp2px(1),getContext().getResources().getColor(R.color.shop_buy_record_gray));
            rlAnchorInformation.setVisibility(View.GONE);
        }else{
            tvAttention.setText(getResources().getString(R.string.live_room_attention));
            tvAttention.setTextColor(getContext().getResources().getColor(R.color.light_orange));
            bgShape.setStroke(Utils.dp2px(1),getContext().getResources().getColor(R.color.orange));
            rlAnchorInformation.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isFirst){
            loadOfficalCommentList();
            isFirst=false;
        }
    }
    private void loadOfficalCommentList() {
        user = WangYuApplication.getUser(context);
        HashMap params = new HashMap();
        //TODO  战时写死
        params.put("amuseId", id);
        params.put("page", page+"");
        params.put("type", 7+ "");//	评论类型：1-娱乐赛评论；2-官方赛事评论 4自发赛评论。（不传默认为1） 7直播间评论
        params.put("replySize", replySize + "");
        if (user != null) {
            params.put("userId", user.getId() + "");
            params.put("token", user.getToken() + "");
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_COMMENT_LIST, params, HttpConstant.AMUSE_COMMENT_LIST);
    }
    private void initView() {
        adapter = new RecreationCommentAdapter(context, comments);
        adapter.setType(2);
        lvLivePlayComment.setAdapter(adapter);
        adapter.setReport(this);
    }
    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        LogUtil.d(TAG,"onSuccess"+object.toString());
        tvErrorPage.setVisibility(View.GONE);
        hideLoading();
        try {
            if (object == null) {
                return;
            }
        if (method.equals(HttpConstant.AMUSE_COMMENT_LIST)) {
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
                    if (comments.isEmpty()) {
                        setErrorPage("还没有留言哦，快来说两句吧");
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
        } else if (method.equals(HttpConstant.AMUSE_COMMENT)) {//提交评论
            LogUtil.d(TAG,"onSuccess 提交评论成功");
            page = 1;
            pageSize = 10;
            loadOfficalCommentList();
            showToast("发表成功");
        }else if(method.equals(HttpConstant.LIVE_SUBSCRIBE)){
            try {
                if(object.getInt("code")==0 && "success".equals(object.getString("result"))){
                    info.setIsSubscibe(info.getIsSubscibe()==1?0:1);
                    if(info.getIsSubscibe()==1){
                        rlAnchorInformation.setVisibility(View.GONE);
                    }
                    ((LiveRoomActivity)getActivity()).updataSubscribeState(info.getIsSubscibe()==1?true:false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        try {
            int code=object.getInt("code");
            String result=object.getString("result");
           showToast(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setErrorPage("还没有留言哦，快来说两句吧");
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        LogUtil.d(TAG,"onError"+errMsg);
        hideLoading();
        setErrorPage("网络不给力，请检查网络再试试");
    }

    /**
     * 设置错误页面
     * @param errorHint
     */
    private void setErrorPage(String errorHint){
        tvErrorPage.setVisibility(View.VISIBLE);
        tvErrorPage.setText(errorHint);
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
                    if (newComments != null && !newComments.isEmpty()) {
                        comments.addAll(newComments);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    comments.clear();
                    adapter.notifyDataSetChanged();
                    setErrorPage("还没有留言哦，快来说两句吧");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
    /**
     * 跳到评论区页面
     */
    public void skipCommentSection() {
        Intent intent = new Intent(context, CommentsSectionActivity.class);
        intent.putExtra("amuseId", id);
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
            map.put("commentId", id);
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DEL_COMMENT, map, HttpConstant.DEL_COMMENT);
            LogUtil.d("Delect", "删除q赢球" + id + "::" + user.getId() + ":::" + user.getToken());
        } else {
            showToast(getResources().getString(R.string.pleaseLogin));
        }
    }
    private void toLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivUpAndDownIcon:
                rlAnchorInformation.setVisibility(View.GONE);
                break;
            case R.id.ivRecomment:
                Intent intent=new Intent(getActivity(), SubmitGradesActivity.class);
                intent.putExtra("fromType",1);
                startActivityForResult(intent,COMMENT_REQUEST);
                break;
            case  R.id.tvAttention:
                getAttentionRequest();
                break;
        }
    }
    private void getAttentionRequest(){
        showLoading();
        Map<String, String> params = new HashMap();
        if(WangYuApplication.getUser(getActivity())!=null){
            params.put("userId",WangYuApplication.getUser(getActivity()).getId()+"");
            params.put("token",WangYuApplication.getUser(getActivity()).getToken());
        }
        params.put("upUserId",id+"");
        //TODO 传递数据

        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.LIVE_SUBSCRIBE, params, HttpConstant.LIVE_SUBSCRIBE);
    }

    /**
     * 提交评论
     */
    private void submitComment() {
        Map<String, String> map = new HashMap();
        user = WangYuApplication.getUser(context);
        if (user != null) {
            map.put("amuseId", id);
            map.put("userId", user.getId() + "");
            map.put("token", user.getToken() + "");
            map.put("content", Utils.replaceBlank(message));
            if(!TextUtils.isEmpty(imgName)){
                map.put("img",imgName);
            }
            map.put("type", 7 + "");// type否	string	评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1） 4自发赛 6 悬赏令 7 直播间 8 视频
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_COMMENT, map, HttpConstant.AMUSE_COMMENT);
        } else {
            toLogin();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ISREFER && data != null) {
            int myIsRefre = data.getIntExtra("isRefre", -1);
            if (myIsRefre == 1) {
                loadOfficalCommentList();
            }
        }else if(requestCode==COMMENT_REQUEST && resultCode==SubmitGradesActivity.RESULT_OK && data!=null){
            imgName =data.getStringExtra("imgName");
            message=data.getStringExtra("remark");
            LogUtil.d(TAG,"imgName"+imgName+":::message"+message);
            submitComment();
        }
    }
}
