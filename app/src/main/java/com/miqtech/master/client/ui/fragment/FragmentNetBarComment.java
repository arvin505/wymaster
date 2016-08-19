package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.NetBarCommentListViewAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.CommentInfo;
import com.miqtech.master.client.entity.Eva;
import com.miqtech.master.client.entity.NetBarComment;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.view.MyHasErrorListView;
import com.miqtech.master.client.view.layoutmanager.FullLinearLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 网吧主页下的网吧评论
 * Created by Administrator on 2016/3/8.
 */
public class FragmentNetBarComment extends MyBaseFragment implements NetBarCommentListViewAdapter.OnItemPraiseAndHideView {

    //        @Bind(R.id.fragment_comment_recycler)
//    RecyclerView recyclerView;
    @Bind(R.id.fragment_comment_mylistview)
    MyHasErrorListView myListView;
    private Context context;
    private String netbarId;//网吧ID
    private int page = 1;//第几页
    private int pageSize = 10;
    private int total = 0;//上一页的返回的总数
    private User user;
    private int isLast = 1;//是否可以加载更多   0可以  1不可以

    private int lastVisibleItem = 0;//
    private FullLinearLayoutManager layoutManager;
    private Eva eva = new Eva();
    private List<CommentInfo> commentInfoList = new ArrayList<CommentInfo>();
    //        private NetBarCommentAdapterV2 adapterV2;
    private int listId;//在commentInfoList中的位置

    private NetBarCommentListViewAdapter adapter;
    private View footerView;
    RelativeLayout llFooterView;

    private boolean isRequestFinish = false;//是否加载完成
    private boolean isAddHeaderView = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lengthCoding = UMengStatisticsUtil.CODE_2005;
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_netbar_comment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        context = getContext();
        lengthCoding = UMengStatisticsUtil.CODE_2003;
        init();
        loadNetBarComment();
        loadMoreNetBarComment();
    }

    /**
     * 初始化
     */
    private void init() {
        netbarId = getActivity().getIntent().getStringExtra("netbarId");
//        layoutManager = new FullLinearLayoutManager(context);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
        myListView.setErrorView("该网吧暂时有点低调哦~\n" +
                "还没有相关评价");
    }

    /**
     * 请求网吧评价列表
     */
    private void loadNetBarComment() {
        isRequestFinish = false;
        user = WangYuApplication.getUser(context);
        showLoading();
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
        }
        map.put("netbarId", netbarId + "");
        map.put("page", page + "");
        map.put("pageSize", pageSize + "");
        map.put("lastTotal", total + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.NETBAR_EVA_V2, map, HttpConstant.NETBAR_EVA_V2);
    }

    /**
     * 加载更多
     */
    private void loadMoreNetBarComment() {
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE && adapterV2.getItemCount() == lastVisibleItem + 1) {
//                    if (isLast == 0) {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                page++;
//                                loadNetBarComment();
//                            }
//                        }, 1000);
//                    } else {
//                        showToast(context.getResources().getString(R.string.load_no));
//                    }
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
//            }
//        });
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        if (method.equals(HttpConstant.NETBAR_EVA_V2)) {//请求评价列表
            analysisCommentJson(object);
        } else if (method.equals(HttpConstant.NET_BAR_EAV_PRAISE)) {
            String obiStr = null;
            try {
                obiStr = object.getString("object").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            commentInfoList.get(listId).setPraised(Integer.valueOf(obiStr) + 1);
            commentInfoList.get(listId).setIsPraised(1);
//            adapterV2.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
        }
        hideLoading();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
        if (llFooterView != null) {
            llFooterView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        if (llFooterView != null) {
            llFooterView.setVisibility(View.GONE);
        }
    }

    /**
     * 解析评论数据
     *
     * @param object
     */
    private void analysisCommentJson(JSONObject object) {
        try {
            if (object.has("object")) {
                NetBarComment netBarComment = GsonUtil.getBean(object.getString("object").toString(), NetBarComment.class);
                if (page == 1) {
                    commentInfoList.clear();
                    eva = netBarComment.getScores();
//                    adapterV2 = new NetBarCommentAdapterV2(context, commentInfoList, eva);
//                    recyclerView.setAdapter(adapterV2);
//                    adapterV2.setOnItemPraiseAndHideView(this);
                    addHeaderView(eva);
                }
                if (netBarComment.getComments() != null) {
                    isLast = netBarComment.getComments().getIsLast();
                    total = netBarComment.getComments().getTotal();

                    if (netBarComment.getComments().getList() != null && !netBarComment.getComments().getList().isEmpty()) {
                        commentInfoList.addAll(netBarComment.getComments().getList());
                    }

//                    if (llFooterView != null) {
//                        if (isLast == 1) {//隐藏尾部的加载更多
//                            llFooterView.setVisibility(View.GONE);
//                        } else {
//                            llFooterView.setVisibility(View.VISIBLE);
//                        }
//                    }
                } else {//加入没有则移除尾部的加载更多
                    myListView.removeFooterView(footerView);
                }
            }

            if (page == 1 && !object.has("object") && isAddHeaderView == false) {
                addHeaderView(null);
            }

            if (llFooterView != null) {
                if (isLast == 1) {//隐藏尾部的加载更多
                    llFooterView.setVisibility(View.GONE);
                } else {
                    llFooterView.setVisibility(View.VISIBLE);
                }
            }

            if (page == 1 && commentInfoList.size() == 0) {
                myListView.setErrorShow(true);
            } else {
                myListView.setErrorShow(false);
            }
//            adapterV2.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
            isRequestFinish = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void praisingComment(int position) {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            if (commentInfoList.get(position).getIsPraised() == 0) {
                showLoading();
                listId = position;
                Map<String, String> params = new HashMap<>();
                params.put("userId", user.getId());
                params.put("token", user.getToken());
                params.put("evaId", commentInfoList.get(position).getId() + "");
                sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.NET_BAR_EAV_PRAISE, params, HttpConstant.NET_BAR_EAV_PRAISE);
            } else {
                showToast("您已经赞过了");
            }
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
    }

//    @Override
//    public void hideView(View view) {
//        if (isLast == 1) {
//            view.setVisibility(View.INVISIBLE);
//        }
//    }

    @Override
    public void refreView() {
        if (isRequestFinish) {//判断是否加载完成，完成则继续加载
            isRequestFinish = false;
            if (isLast == 0) {//0代表还可以加载更多
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page++;
                        loadNetBarComment();
                    }
                }, 300);
            } else {
                if (!commentInfoList.isEmpty()) {
                    showToast(context.getResources().getString(R.string.load_no));
                }
            }
        }
    }

    private void addHeaderView(Eva eva) {
        adapter = new NetBarCommentListViewAdapter(context, commentInfoList);
        isAddHeaderView = true;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_rating_comment_v2, null);
        RatingBar environment_rb = (RatingBar) view.findViewById(R.id.netbar_environment_rb);
        RatingBar machineConfiguration_rb = (RatingBar) view.findViewById(R.id.netbar_machineConfiguration_rb);
        RatingBar network_rb = (RatingBar) view.findViewById(R.id.netbar_network_rb);
        RatingBar sisterNum_rb = (RatingBar) view.findViewById(R.id.netbar_sisterNum_rb);
        TextView netbarMarking_tv = (TextView) view.findViewById(R.id.netbar_marking_tv);
        TextView netbarByMarking_tv = (TextView) view.findViewById(R.id.netbar_by_marking_tv);
//        LinearLayout layoutGrade = (LinearLayout) view.findViewById(R.id.layout_grade);
        if (eva != null) {
            if (eva.getTotalEva() < 100000) {
                netbarByMarking_tv.setText(eva.getTotalEva() + "人评价");// 网吧评论人数
            } else {
                netbarByMarking_tv.setText("99999+人评价");
            }
            netbarMarking_tv.setText(eva.getAvgScore() + "");// 网吧评价数 例如4.8
            environment_rb.setRating(eva.getEnviroment());// 环境
            machineConfiguration_rb.setRating(eva.getEquipment());// 机器配置
            network_rb.setRating(eva.getNetwork());// 网络流畅
            sisterNum_rb.setRating(eva.getService());// 妹子数量评价
        }
        myListView.addHeaderView(view);

        footerView = LayoutInflater.from(context).inflate(R.layout.layout_footer_view, null);
        llFooterView = (RelativeLayout) footerView.findViewById(R.id.footerView);
        llFooterView.setVisibility(View.GONE);
        myListView.addFooterView(footerView);
        myListView.setAdapter(adapter);
        adapter.setOnItemPraiseAndHideView(this);
    }
}
