package com.miqtech.master.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.NetbarCommentAdapter;
import com.miqtech.master.client.adapter.NetbarCommentAdapter.ItemPostDate;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.CommentInfo;
import com.miqtech.master.client.entity.CommentListBean;
import com.miqtech.master.client.entity.Eva;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.view.MyListView;
import com.miqtech.master.client.view.RefreshLayout;

import butterknife.Bind;

/**
 * 查看网吧评论详情
 *
 * @author Administrator
 */
public class NetbarCommentDetailActivity extends BaseActivity implements OnClickListener,
        ItemPostDate, RefreshLayout.OnLoadListener {

    private RatingBar environment_rb;// 网吧环境评分
    private RatingBar machineConfiguration_rb;// 网吧机器评分
    private RatingBar network_rb;// 网吧网络评分
    private RatingBar sisterNum_rb;// 网吧妹子数量评分
    private TextView netbarMarking_tv;// 网吧评分数
    private TextView netbarByMarking_tv;// 网吧被评人数
    private Button netbarToMarking_bt;// 查看评价
    private MyListView lvComment;
    private RefreshLayout refrl;

    private NetbarCommentAdapter adapter;
    private View viewHead1, viewHead2;
    private User user;
    private String netbarId;

    private Context context;

    private int page = 1;// 页数
    private int rows = 10;// 10条数据
    private int totalComment = 0;// 有几条评论
    private int changeID;// 记录哪条评论被赞或者举报
    /**
     * 标识是否还有下一页 0：有 1：没有
     */
    private int mIsLastPage = 0;

    private List<CommentInfo> commentinfoList = new ArrayList<CommentInfo>();
    private CommentListBean bean = new CommentListBean();
    private Eva _Eva;
    private ImageView back;
    public static boolean isRefresh = false;// 没登录前，去评论界面（提交评论时登录过后）或点赞（登录过后）变true，回来刷新界面

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.activity_netbar_comment_detail);
        context = NetbarCommentDetailActivity.this;
        // user = WangYuApplication.getUser(context);
        netbarId = getIntent().getStringExtra("netbarId");
        _Eva = (Eva) getIntent().getSerializableExtra("_Eva");
        initView();
        initData();
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();

        lvComment = (MyListView) findViewById(R.id.netbat_comment_lv);
        refrl = (RefreshLayout) findViewById(R.id.refresh_view_comment);
        refrl.setOnLoadListener(this);
        refrl.setColorSchemeResources(R.color.colorActionBarSelected);
        refrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getCommentListDate();
            }
        });
        back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(this);
        setLeftIncludeTitle("评价");
        //setLeftBtnImage(R.drawable.back);
        //getLeftBtn().setOnClickListener(this);

        AddHeadListview();
    }

    private void AddHeadListview() {
        adapter = new NetbarCommentAdapter(context, commentinfoList);
        viewHead1 = LayoutInflater.from(context).inflate(R.layout.layout_rating_comment, null);
        environment_rb = (RatingBar) viewHead1.findViewById(R.id.netbar_environment_rb);
        machineConfiguration_rb = (RatingBar) viewHead1.findViewById(R.id.netbar_machineConfiguration_rb);
        network_rb = (RatingBar) viewHead1.findViewById(R.id.netbar_network_rb);
        sisterNum_rb = (RatingBar) viewHead1.findViewById(R.id.netbar_sisterNum_rb);
        netbarMarking_tv = (TextView) viewHead1.findViewById(R.id.netbar_marking_tv);
        netbarByMarking_tv = (TextView) viewHead1.findViewById(R.id.netbar_by_marking_tv);
        netbarToMarking_bt = (Button) viewHead1.findViewById(R.id.netbar_to_marking_bt);
        netbarToMarking_bt.setVisibility(View.GONE);
        if (_Eva.getTotalEva() < 100000) {
            netbarByMarking_tv.setText(_Eva.getTotalEva() + "评价");// 网吧评论人数
        } else {
            netbarByMarking_tv.setText("99999+人评价");
        }
        netbarMarking_tv.setText(_Eva.getAvgScore() + "");// 网吧评价数 例如4.8
        environment_rb.setRating(_Eva.getEnviroment());// 环境
        machineConfiguration_rb.setRating(_Eva.getEquipment());// 机器配置
        network_rb.setRating(_Eva.getNetwork());// 网络流畅
        sisterNum_rb.setRating(_Eva.getService());// 妹子数量评价

        viewHead2 = LayoutInflater.from(context).inflate(R.layout.layout_comment_second_head, null);

        lvComment.addHeaderView(viewHead1);
        lvComment.addHeaderView(viewHead2);

        lvComment.setAdapter(adapter);
        adapter.setItemPostDate(this);
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        getCommentListDate();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.ivBack:// 返回
                onBackPressed();
                break;
            default:
                break;
        }
    }

    /**
     * 获取网吧评论列表
     */
    private void getCommentListDate() {
        user = WangYuApplication.getUser(context);
        Map<String, String> params = new HashMap<>();
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
        }
        params.put("netbarId", netbarId);
        params.put("totalAmount", totalComment + "");
        params.put("page", page + "");
        params.put("pageSize", rows + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.NET_BAR_COMMENT_LIST, params, HttpConstant.NET_BAR_COMMENT_LIST);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        // TODO Auto-generated method stub
        hideLoading();
        super.onSuccess(object, method);
        LogUtil.e("d", "object  == " + object.toString());
        Gson gs = new Gson();
        if (method.equals(HttpConstant.NET_BAR_COMMENT_LIST)) {
            String obj = null;
            try {
                obj = object.getJSONObject("object").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String obiStr = obj.toString();
            bean = gs.fromJson(obiStr, CommentListBean.class);
            mIsLastPage = bean.getIsLast();
            totalComment = bean.getTotal();
            if (page == 1) {
                commentinfoList.clear();
            }

            if (bean != null && !(bean.getList().isEmpty())) {
                commentinfoList.addAll(bean.getList());
            }
            adapter.notifyDataSetChanged();

        } else if (method.equals(HttpConstant.NET_BAR_EAV_PRAISE)) {
            String obiStr = null;
            try {
                obiStr = object.get("object").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            commentinfoList.get(changeID).setPraised(Integer.valueOf(obiStr) + 1);
            commentinfoList.get(changeID).setIsPraised(1);
            adapter.notifyDataSetChanged();
        }
        refrl.setLoading(false);
        refrl.setRefreshing(false);
    }

    @Override
    public void onError(String method, String errorInfo) {
        // TODO Auto-generated method stub
        hideLoading();
        refrl.setLoading(false);
        refrl.setRefreshing(false);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        refrl.setLoading(false);
        refrl.setRefreshing(false);
    }

    @Override
    public void useful(int position) {
        user = WangYuApplication.getUser(context);
        if (user != null) {
            if (commentinfoList.get(position).getIsPraised() == 0) {
                //showLoading();
                changeID = position;
                Map<String, String> params = new HashMap<>();
                params.put("userId", user.getId());
                params.put("token", user.getToken());
                params.put("evaId", commentinfoList.get(position).getId() + "");
                sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.NET_BAR_EAV_PRAISE, params, HttpConstant.NET_BAR_EAV_PRAISE);
            } else {
                showToast("您已经赞过了");
            }
        } else {
            login();
            isRefresh = true;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (isRefresh && WangYuApplication.getUser(context) != null) {
            page = 1;
            getCommentListDate();
            isRefresh = false;
        }
    }

    @Override
    public void report(int position) {
        Intent ii = new Intent(context, ReportActivity.class);
        ii.putExtra("type", 4);// 举报类别:1.用户2.评论3.约战4网吧评论
        ii.putExtra("targetId", commentinfoList.get(position).getId());// type=1被举报用户的id,type=2评论id,type=3约战id,type=4网吧评价id
        startActivity(ii);
    }

    private void login() {
        Intent ii = new Intent(context, LoginActivity.class);
        startActivity(ii);
    }

    @Override
    public void onLoad() {
        // TODO Auto-generated method stub

        if (mIsLastPage == 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    page++;
                    getCommentListDate();
                }
            }, 1000);
        } else {
            showToast(context.getResources().getString(R.string.load_no));
            refrl.setLoading(false);
            refrl.setRefreshing(false);
        }

    }
}
