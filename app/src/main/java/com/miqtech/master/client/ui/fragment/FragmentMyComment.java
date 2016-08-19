package com.miqtech.master.client.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.MyCommentAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.Mycomment;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.MainActivity;
import com.miqtech.master.client.ui.MyMessageActivity;
import com.miqtech.master.client.ui.PersonalCommentDetail;
import com.miqtech.master.client.ui.ReportActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.DeleteView;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的评论
 * Created by Administrator on 2016/1/18.
 */
public class FragmentMyComment extends MyBaseFragment implements MyCommentAdapter.DealWithCommentItem {

    private PullToRefreshRecyclerView prrvFragmentComment;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayout llComment;
    private EditText edtComment;
    private TextView tvSend;
    private RelativeLayout my_comment_fragment_rl;

    private View mainView;
    private Context mContext;

    private int page = 1;//当前分页所在页数（默认值1）
    private int pageSize = 10;//当前分页显示的行数（默认值10）
    private final int type = 4;//1.订单 2.活动 3.系统 4.评论
    private int isAll = 0;//isAll=1时返回所有的数据,可以不传
    private int is_last = 0;
    private User user;
    public boolean isFirst = true;
    public DeleteView myDialog;
    private int listId;
    private MyCommentAdapter adapter;
    private List<Mycomment> mycommentList = new ArrayList<Mycomment>();
    private Mycomment mycomment;
    private int lastViewItemCount;

    private Dialog dialog;
    private Window mWindow;
    private TextView lookComment;
    private TextView replyComment;
    private TextView reportComment;

    private String comment;
    private String replyStr = "回复 ";
    private int offset;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_my_comment, null);
            mContext = inflater.getContext();
            initView();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup parent = (ViewGroup) mainView.getParent();
        if (parent != null) {
            parent.removeAllViewsInLayout();
        }
        return mainView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirst) {
            isFirst = false;
            showLoading();
            loadMyReleaseComment();
        }
    }

    private void initView() {
        myDialog = new DeleteView(mContext, R.style.delete_style, R.layout.delete_dialog);
        prrvFragmentComment = (PullToRefreshRecyclerView) mainView.findViewById(R.id.prrvFragmentComment);
        prrvFragmentComment.setMode(PullToRefreshBase.Mode.BOTH);
        recyclerView = prrvFragmentComment.getRefreshableView();
        llComment = (LinearLayout) mainView.findViewById(R.id.buttom_send_personal_comment);
        edtComment = (EditText) mainView.findViewById(R.id.edtComment);
        tvSend = (TextView) mainView.findViewById(R.id.tvSend);
        my_comment_fragment_rl = (RelativeLayout) mainView.findViewById(R.id.my_comment_fragment_rl);
        linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new MyCommentAdapter(mContext, mycommentList);
        recyclerView.setAdapter(adapter);
        adapter.setDealWithCommentItem(this);
       prrvFragmentComment.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
           @Override
           public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
               showLoading();
               page = 1;
               loadMyReleaseComment();
           }

           @Override
           public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
               if (is_last == 0) {
                   new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           page++;
                           showLoading();
                           loadMyReleaseComment();
                       }
                   }, 1000);

               } else {
                   showToast(mContext.getResources().getString(R.string.nomore));
                   prrvFragmentComment.onRefreshComplete();
               }
               llComment.setVisibility(View.GONE);
           }

           @Override
           public void isHasNetWork(boolean isHasNetWork) {
               if(!isHasNetWork) {
                   showToast(getActivity().getResources().getString(R.string.noNeteork));
               }
           }
       });
        monitorEditView();
        sendComment();
    }

    /**
     * 监听输入的变化
     */
    private void monitorEditView() {
        TextWatcher textWatcher = new TextWatcher() {
            private CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(temp)) {
                    tvSend.setTextColor(getResources().getColor(R.color.orange));
                } else {
                    tvSend.setTextColor(getResources().getColor(R.color.font_gray));
                }

                if (temp.length() > 199) {
                    showToast("您输入的字数已超过限制");
                }
            }
        };
        edtComment.addTextChangedListener(textWatcher);
    }

    /**
     * 请求消息评论
     */
    private void loadMyReleaseComment() {
        user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("page", page + "");
            map.put("pageSize", pageSize + "");
            map.put("type", type + "");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_MESSAGE, map, HttpConstant.MY_MESSAGE);
        } else {
            showToast(mContext.getResources().getString(R.string.pleaseLogin));
        }
    }

    private void deleteMsg(String msgId) {
        user = WangYuApplication.getUser(mContext);
        if (user != null) {
            showLoading();
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("type", mycomment.getType() + "");
            map.put("msgIds", msgId + "");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MSG_MULTI_DELETE, map, HttpConstant.MSG_MULTI_DELETE);
        } else {
            showToast(mContext.getResources().getString(R.string.pleaseLogin));
        }
    }

    private void setIsReaded(Mycomment message) {
        user = WangYuApplication.getUser(mContext);
        if (user != null) {
            showLoading();
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            map.put("type", message.getType() + "");
            map.put("msgId", message.getId() + "");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SET_MSG_READED, map, HttpConstant.SET_MSG_READED);
        } else {
            showToast(mContext.getResources().getString(R.string.pleaseLogin));
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        try {
            if (method.equals(HttpConstant.MY_MESSAGE)) {
               prrvFragmentComment.onRefreshComplete();
                if (object.has("object")) {
                    showData(object.getJSONObject("object"));
                }
            } else if (HttpConstant.SET_MSG_READED.equals(method)) {

            } else if (method.equals(HttpConstant.MSG_MULTI_DELETE)) {
                mycommentList.remove(listId);
                adapter.notifyDataSetChanged();
                showToast("删除成功");
            } else if (method.equals(HttpConstant.AMUSE_COMMENT)) {
                showToast("评论成功");
                comment = "";
                page = 1;
                edtComment.setText("");
                llComment.setVisibility(View.GONE);
                showLoading();
                loadMyReleaseComment();
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
        llComment.setVisibility(View.GONE);
        prrvFragmentComment.onRefreshComplete();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        llComment.setVisibility(View.GONE);
        prrvFragmentComment.onRefreshComplete();
        try {
            if (object.has("result")) {
                showToast(object.getString("result"));
            } else {
                showToast(object.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mainView;
    }

    /**
     * 解析并显示数据
     *
     * @param object
     */
    private void showData(JSONObject object) {
        try {
            if (object == null) {
                return;
            }
            List<Mycomment> newMycommentList = GsonUtil.getList(object.getString("list").toString().trim(), Mycomment.class);
            if (page == 1) {
                mycommentList.clear();
            }
            is_last = object.getInt("isLast");
            mycommentList.addAll(newMycommentList);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreView() {
        showLoading();
        page = 1;
        loadMyReleaseComment();
    }


    @Override
    public void onItemClickForItem(int position) {
        if (mycommentList.isEmpty()) {
            return;
        }
        listId = position;
        mycomment = mycommentList.get(position);
        if (mycomment.getIs_read() == 0) {
            mycomment.setIs_read(1);
            MainActivity.commentCount--;
            BroadcastController.sendUserChangeBroadcase(mContext);
            ((MyMessageActivity) mContext).refreMessage();
            adapter.notifyDataSetChanged();
            setIsReaded(mycomment);
        }
        showDialog();
    }

    @Override
    public void onItemLongClickForItem(final int position, View view) {
        listId = position;
        int[] location = new int[2];
        // 获取当前view在屏幕中的绝对位置
        // ,location[0]表示view的x坐标值,location[1]表示view的坐标值
        view.getLocationOnScreen(location);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        display.getMetrics(displayMetrics);
        WindowManager.LayoutParams params = myDialog.getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.y = display.getHeight() - location[1];
        myDialog.getWindow().setAttributes(params);
        myDialog.setCanceledOnTouchOutside(true); // 点击dialog区域之外的地方，dialog消失
        myDialog.setDialogOnclickInterface(new DeleteView.IDialogOnclickInterface() {
            @Override
            public void rightOnclick() {
                mycomment = mycommentList.get(position);
                deleteMsg(mycomment.getId() + "");
                myDialog.dismiss();
            }

            @Override
            public void leftOnclick() {
                // TODO Auto-generated method stub
            }
        });
        myDialog.show();
    }

    @Override
    public void hideFooterView(View view) {
        if (is_last != 0) {
            view.setVisibility(View.GONE);
        }
    }

    private void showDialog() {
        dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
        mWindow = dialog.getWindow();
        mWindow.setContentView(R.layout.layout_my_comment_dialog);
        dialog.setCanceledOnTouchOutside(true);
        lookComment = (TextView) dialog.findViewById(R.id.look_comment_tv_dialog);
        replyComment = (TextView) dialog.findViewById(R.id.reply_comment_tv_dialog);
        reportComment = (TextView) dialog.findViewById(R.id.report_comment_tv_dialog);

        MyListener myListener = new MyListener();
        lookComment.setOnClickListener(myListener);
        replyComment.setOnClickListener(myListener);
        reportComment.setOnClickListener(myListener);
    }

    private class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.look_comment_tv_dialog:
                    setDialogDismiss();
                    Intent intent = new Intent(mContext, PersonalCommentDetail.class);
                    intent.putExtra("amuseId", mycomment.getActivity_id() + "");
                    intent.putExtra("type", mycomment.getActivity_type());
                    intent.putExtra("parentId", mycomment.getParent_id());
                    intent.putExtra("replyIDTwo", mycomment.getComment_id() + "");
                    intent.putExtra("isPopupKeyboard", 0);
                    mContext.startActivity(intent);
                    break;
                case R.id.reply_comment_tv_dialog:
                    setDialogDismiss();
                    llComment.setVisibility(View.VISIBLE);
                    edtComment.requestFocus();
                    InputMethodManager imm = (InputMethodManager) edtComment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                    edtComment.setHint(replyStr + mycomment.getNickname());
                    break;
                case R.id.report_comment_tv_dialog:
                    setDialogDismiss();
                    Intent ii = new Intent(mContext, ReportActivity.class);
                    ii.putExtra("type", 2);// 举报类别:1.用户2.评论3.约战4网吧评论
                    ii.putExtra("targetId", mycommentList.get(listId).getComment_id());// type=1被举报用户的id,type=2评论id,type=3约战id,type=4网吧评价id
                    mContext.startActivity(ii);
                    break;
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
        showLoading();
        Map<String, String> map = new HashMap();
        user = WangYuApplication.getUser(mContext);
        if (user != null) {
            map.put("amuseId", mycomment.getActivity_id() + "");
            map.put("userId", user.getId() + "");
            map.put("content", Utils.replaceBlank(comment));
            map.put("type", mycomment.getActivity_type() + "");// type否	string	评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1）
            map.put("parentId", mycomment.getParent_id() + "");// parentId	否	string	一级评论ID
            map.put("replyId", mycomment.getComment_id() + "");//replyId	否	string	楼中楼回复对象的评论ID
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AMUSE_COMMENT, map, HttpConstant.AMUSE_COMMENT);
        } else {
            showToast(getResources().getString(R.string.pleaseLogin));
        }
    }

    /**
     * 1、发送评论
     * 2、监听软键盘是否弹出并做出相应的事件
     */
    private void sendComment() {
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitComment();
            }
        });

        my_comment_fragment_rl.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > 200)) {
                    llComment.setVisibility(View.VISIBLE);
                }
                if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > 200)) {
                    llComment.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setDialogDismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

}
