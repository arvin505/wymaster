package com.miqtech.master.client.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.miqtech.master.client.R;

import com.miqtech.master.client.adapter.YueZhanCommentAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.entity.YueZhanComment;
import com.miqtech.master.client.entity.YueZhanComments;

import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.YueZhanDetailsActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;

import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.OnListScrollListener;
import com.miqtech.master.client.view.PullToRefreshLayout;
import com.miqtech.master.client.view.PullToRefreshLayout.OnRefreshListener;
import com.miqtech.master.client.view.StickyContainer;

import com.miqtech.master.client.view.StickyScrollManager;
import com.nineoldandroids.animation.ObjectAnimator;

import org.json.JSONObject;

public class FragmentYuezhanComment extends BaseFragment implements OnListScrollListener, TextWatcher, OnClickListener,
        OnRefreshListener {
    private View mainView;// 页面布局
    private Context context;
    private StickyContainer mCarousel;
    private ListView lvComment;
    private EditText edtComment;
    private YueZhanCommentAdapter adapter;
    private List<YueZhanComment> comments = new ArrayList<YueZhanComment>();
    private LinearLayout llInput;
    private Button btnSend;
    private boolean isFirst = true;
    private int page = 1;
    private int rows = 20;
    private int isLast;

    private UpdateCommentCountsViewListener listener;

    private PullToRefreshLayout refresh_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mainView == null) {
            context = getActivity();
            listener = (UpdateCommentCountsViewListener) getActivity();
            mainView = LinearLayout.inflate(context, R.layout.fragment_yuezhancomment, null);
            initView();
        }

    }

    ;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup parent = (ViewGroup) mainView.getParent();
        if (parent != null) {
            parent.removeAllViewsInLayout();
        }

        return mainView;
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    private void loadComments() {
        Map<String, String> params = new HashMap<>();
        params.put("id", YueZhanDetailsActivity.yueZhan.getId() + "");
        params.put("page", page + "");
        params.put("rows", rows + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.GET_MATCH_COMMENT, params, HttpConstant.GET_MATCH_COMMENT);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirst) {
            isFirst = false;
            loadComments();
        }
    }

    private void initView() {
        lvComment = (ListView) mainView.findViewById(R.id.lvComment);
        edtComment = (EditText) mainView.findViewById(R.id.edtComment);
        btnSend = (Button) mainView.findViewById(R.id.btnSend);
        refresh_view = (PullToRefreshLayout) mainView.findViewById(R.id.refresh_view);
        refresh_view.setOnRefreshListener(this);
        View header = View.inflate(context, R.layout.layout_yuezhandetail_header, null);
        lvComment.addHeaderView(header);
        llInput = (LinearLayout) mainView.findViewById(R.id.llInput);
        adapter = new YueZhanCommentAdapter(context, comments);
        lvComment.setAdapter(adapter);
        edtComment.addTextChangedListener(this);
        btnSend.setOnClickListener(this);
    }

    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mCarousel = (StickyContainer) activity.findViewById(R.id.carousel_header);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        StickyScrollManager manager = new StickyScrollManager(mCarousel, 2);
        manager.setmOnListScrollListener(this);
        lvComment.setOnScrollListener(manager);
        lvComment.setVerticalScrollBarEnabled(false);
    }

    // @Override
    // public void onResume() {
    // // TODO Auto-generated method stub
    // super.onResume();
    // comments.clear();
    // comments.addAll(YueZhanDetailsActivity.yueZhan.getComments().getList());
    // adapter.notifyDataSetChanged();
    // }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
        onDetectedListScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub

    }

    private int oldTop;
    private int oldFirstVisibleItem;

    private void onDetectedListScroll(AbsListView absListView, int firstVisibleItem, int totalItemCount,
                                      int visibleItemCount) {
        View view = absListView.getChildAt(0);
        int top = (view == null) ? 0 : view.getTop();
        int lastItem = firstVisibleItem + visibleItemCount;

        if (firstVisibleItem == oldFirstVisibleItem) {
            if (top > oldTop) {
                // onDetectScrollListener.onUpScrolling();
                showInput();

            } else if (top < oldTop) {
                // onDetectScrollListener.onDownScrolling();
                hideInput();
            }
        } else {
            if (firstVisibleItem < oldFirstVisibleItem) {
                // onDetectScrollListener.onUpScrolling();
                showInput();
            } else {
                // onDetectScrollListener.onDownScrolling();
                hideInput();
            }
        }

        if (lastItem == totalItemCount) {
            if (oldFirstVisibleItem <= lastItem) {
                // onDetectScrollListener.onBottomScrolled();
                // hideInput();
            } else {
                // onDetectScrollListener.onUpScrolling();
                showInput();
            }
        }

        oldTop = top;
        oldFirstVisibleItem = firstVisibleItem;
    }

    boolean showed = true;

    private void showInput() {

        if (!showed) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(llInput, "y", llInput.getBottom() - llInput.getHeight());
            anim.start();
            showed = true;
        }
    }

    private void hideInput() {

        if (showed) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(llInput, "y", llInput.getBottom() + llInput.getHeight());
            anim.start();
            showed = false;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnSend:
                sendComment();
                break;

            default:
                break;
        }
    }

    private void sendComment() {
        User user = WangYuApplication.getUser(WangYuApplication.appContext);
        if (user == null) {
            Intent intent = new Intent();
            intent.setClass(context, LoginActivity.class);
            startActivity(intent);
            return;
        }
        if (edtComment.getText().toString().length() > 0) {
            showLoading();
            Map<String, String> params = new HashMap<>();

            params.put("userId", user.getId());
            params.put("token", user.getToken());
            params.put("matchId", YueZhanDetailsActivity.yueZhan.getId() + "");
            params.put("content", edtComment.getText().toString());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SEND_MATCH_COMMENT, params, HttpConstant.SEND_MATCH_COMMENT);
        }
    }

    public void onSuccess(JSONObject object, String method) {
        // TODO Auto-generated method stub

        hideLoading();
        LogUtil.e("da", "data  " + object.toString());
        try {
            JSONObject obj = object.getJSONObject("object");
            if (method.equals(HttpConstant.SEND_MATCH_COMMENT)) {
                YueZhanComment yueZhanComment = new Gson().fromJson(obj.toString(), YueZhanComment.class);
                comments.add(0, yueZhanComment);
                adapter.notifyDataSetChanged();
                edtComment.setText("");
                listener.updateView();
            } else if (method.equals(HttpConstant.GET_MATCH_COMMENT)) {
                YueZhanComments yueZhanComments = new Gson().fromJson(obj.toString(), YueZhanComments.class);
                isLast = yueZhanComments.getIsLast();
                if (page == 1) {
                    refresh_view.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    refresh_view.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                if (page == 1) {
                    comments.clear();
                }
                comments.addAll(yueZhanComments.getList());
                adapter.setData(comments);
                adapter.notifyDataSetChanged();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        LogUtil.e("da", "sss == " + object.toString());
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        LogUtil.e("da", "eeeee == " + errMsg.toString());
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        // TODO Auto-generated method stub
        refresh_view.refreshFinish(PullToRefreshLayout.SUCCEED);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        // TODO Auto-generated method stub
        if (isLast == 0) {
            page++;
            loadComments();
        } else {
            refresh_view.loadmoreFinish(PullToRefreshLayout.NOMORE);
            showToast("已无更多评论");
        }
    }


    public interface UpdateCommentCountsViewListener {
        void updateView();
    }

}
