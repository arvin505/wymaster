package com.miqtech.master.client.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.YueZhanAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.ByInvateYueZhan;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.entity.YueZhan;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.SubjectActivity;
import com.miqtech.master.client.ui.YueZhanDetailsActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.ToastUtil;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.MyAlertView;
import com.miqtech.master.client.view.RefreshLayout;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/7.
 */
public class FragmentMyWarApply extends MyBaseFragment implements AdapterView.OnItemClickListener{

    private View mainView, invateView;

    private Context mContext;

    private PullToRefreshListView prlvMyWarApply;

    private HasErrorListView lvMyWarApply;

    private boolean isFirst = true;

    private YueZhanAdapter adapter;

    private List<YueZhan> wars = new ArrayList<YueZhan>();

    private List<ByInvateYueZhan> byinvate = new ArrayList<ByInvateYueZhan>();

    private int page = 1;
    private int rows = 10;
    private int isLast;

    private ImageView ivheader;// 邀请的头像
    private TextView tvInvateName;// 邀请人名字
    private TextView tvInvateTeam;// 邀请人战队
    private TextView tvtitle;// 约站标题
    private Button btNo;// 拒绝邀请
    private Button btYes;// 接受邀请

    private View headeView = null;// 要删除的listView的头部
    private int yuezhanType = 1;// 1-接受（默认），2-拒绝
    private Boolean isSkip = false;
    private int matchID;// 接受约站后跳转是约战详情界面

    public static Boolean isSuccess = false;
    private List<View> viewList = new ArrayList<View>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_mywarapply, null);
            mContext = inflater.getContext();
            initView();
        } else {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup parent = (ViewGroup) mainView.getParent();
        if (parent != null) {
            parent.removeAllViewsInLayout();
        }
        return mainView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirst) {
            isFirst = false;
            initData();
        }
    }

    private void initView() {
        prlvMyWarApply = (PullToRefreshListView) mainView.findViewById(R.id.prlvMyWarApply);
        prlvMyWarApply.setMode(PullToRefreshBase.Mode.BOTH);
        lvMyWarApply =prlvMyWarApply.getRefreshableView();

        prlvMyWarApply.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                // loadMyApplyWar();
                loadUserByInvate();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if (wars.size() > 0) {
                    if (isLast == 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                page++;
                                showLoading();
                                loadMyApplyWar();
                            }
                        }, 1000);
                    } else {
                        showToast(mContext.getResources().getString(R.string.load_no));
                        prlvMyWarApply.onRefreshComplete();
                    }
                } else {
                    prlvMyWarApply.onRefreshComplete();

                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
             if(!isHasNetWork){
                 showToast(getActivity().getResources().getString(R.string.noNeteork));
             }
            }
        });
    }

    private void updateListView() {
        lvMyWarApply.setErrorView("太低调了,还没有报名任何约战");
        adapter = new YueZhanAdapter(mContext, wars);
        if (byinvate.size() != 0) {// 有数据才显示加载
            BroadcastController.sendUserChangeBroadcase(mContext);
            for (final ByInvateYueZhan bean : byinvate) {
                invateView = LayoutInflater.from(mContext).inflate(R.layout.layout_yuezhan_invate_item, null);
                btNo = (Button) invateView.findViewById(R.id.btno_yuezhan_item);
                btYes = (Button) invateView.findViewById(R.id.btyes_yuezhan_item);
                ivheader = (ImageView) invateView.findViewById(R.id.ivHeader_yuezhan_item);
                tvInvateName = (TextView) invateView.findViewById(R.id.tvname_yuezhan_item_);
                tvInvateTeam = (TextView) invateView.findViewById(R.id.tvteam_yuezhan_item);
                tvtitle = (TextView) invateView.findViewById(R.id.tvtitle_yuezhan_item);

                AsyncImage.loadAvatar(mContext, HttpConstant.SERVICE_UPLOAD_AREA + bean.getInviterIcon(), ivheader);
                tvInvateName.setText(bean.getInviterNickname() + "邀你一起参加一场约战");
                // tvInvateTeam.setText("战队:" + bean.getItemName());
                tvInvateTeam.setText(bean.getServer());
                if (bean.getTitle() != null) {
                    tvtitle.setText(bean.getTitle());
                }
                invateView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent i = new Intent(mContext, YueZhanDetailsActivity.class);
                        i.putExtra("id", bean.getMatchId() + "");
                        i.putExtra("inviteId", bean.getInviteId() + "");
                        mContext.startActivity(i);
                        headeView = invateView;
                    }
                });
                acceptInvate(invateView, bean, btYes);
                refuseInvate(invateView, bean, btNo);
                lvMyWarApply.addHeaderView(invateView);
                viewList.add(invateView);
            }
        }
        lvMyWarApply.setAdapter(adapter);
        loadMyApplyWar();
    }

    /**
     * 提交越战邀请的处理
     *
     * @param inviteId 邀请ID
     * @param type     处理：1-接受（默认），2-拒绝
     */
    private void postdata(int inviteId, int type) {
        showLoading();
        User user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("inviteId", inviteId + "");
        map.put("type", type + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DEAL_WITH_INVATE, map, HttpConstant.DEAL_WITH_INVATE);
    }

    private void initData() {
        loadUserByInvate();
        // loadMyApplyWar();
    }

    private void loadMyApplyWar() {
        showLoading();
        User user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("page", page + "");
        map.put("rows", rows + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_APPLY_WAR, map, HttpConstant.MY_APPLY_WAR);
    }

    private void loadUserByInvate() {
        User user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.USER_INVOCATION, map, HttpConstant.USER_INVOCATION);
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mainView;
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        hideLoading();
        try {
            Object obj = null;
            if (object.has("object")) {
                obj = object.getString("object");
            }
            if (method.equals(HttpConstant.MY_APPLY_WAR)) {
                if (obj == null) {
                    return;
                }
                JSONObject jsonList = new JSONObject(obj.toString());
                String strList = jsonList.getString("list");
                isLast = jsonList.getInt("isLast");
                List<YueZhan> newWars = new Gson().fromJson(strList, new TypeToken<List<YueZhan>>() {
                }.getType());
                if (page == 1) {
                    wars.clear();
                }
                wars.addAll(newWars);
                if (page == 1) {

                    if (byinvate.size() == 0 && wars.size() == 0) {
                        lvMyWarApply.setErrorShow(true);
                    }

                    if (isSkip == true) {
                        Intent ii = new Intent(mContext, YueZhanDetailsActivity.class);
                        ii.putExtra("id", matchID + "");
                        mContext.startActivity(ii);
                        isSkip = false;
                    }
                } else {
                    lvMyWarApply.setErrorShow(false);
                }
                adapter.notifyDataSetChanged();
            } else if (method.equals(HttpConstant.USER_INVOCATION)) {
                List<ByInvateYueZhan> newBy = new Gson().fromJson(obj.toString(), new TypeToken<List<ByInvateYueZhan>>() {
                }.getType());
                if (byinvate.size() != 0) {
                    byinvate.clear();
                    for (View v : viewList) {
                        lvMyWarApply.removeHeaderView(v);
                    }
                }
                byinvate.addAll(newBy);
                updateListView();
            } else if (method.equals(HttpConstant.DEAL_WITH_INVATE)) {
                showdialog();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       prlvMyWarApply.onRefreshComplete();
    }

    @Override
    public void onError(String errMsg, String method) {
        hideLoading();
        lvMyWarApply.setErrorShow(false);
        prlvMyWarApply.onRefreshComplete();
        ToastUtil.showToast(errMsg, mContext);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        if (method.equals(HttpConstant.DEAL_WITH_INVATE)) {
            yuezhanType = 3;
            showdialog();
        } else {

        }
        lvMyWarApply.setErrorShow(false);
        prlvMyWarApply.onRefreshComplete();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
        if (arg0.getAdapter().getItemId(position) == -1) {

        } else if (wars.isEmpty()) {

            YueZhan war = (YueZhan) arg0.getAdapter().getItem(position);
            int id = war.getId();
            Intent intent = new Intent();
            intent.setClass(mContext, SubjectActivity.class);
            intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.YUEZHAN);
            intent.putExtra("id", id + "");
            startActivity(intent);
        }
    }

    /**
     * 处理约战邀请
     */
    private void showdialog() {
        lvMyWarApply.removeHeaderView(headeView);
        viewList.remove(headeView);
        if (yuezhanType == 1) {// 接受约战，并且成功，然后跳转约战详情
            showSuccessToast();
            isSkip = true;
            page = 1;
            loadMyApplyWar();
            BroadcastController.sendUserChangeBroadcase(mContext);
        } else if (yuezhanType == 2) {// 拒绝约战，刷新本界面
            yuezhanType = 1;
        } else if (yuezhanType == 3) {// 接受约战，但是约站已满
            MyAlertView.Builder builder = new MyAlertView.Builder(mContext);
            builder.setMessage("太迟啦...约战已经满了");
            builder.setTitle("约战已满");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            builder.create().show();
            yuezhanType = 1;
        }
        headeView = null;
        BroadcastController.sendUserChangeBroadcase(mContext);
    }

    /**
     * 接受邀请
     *
     * @param v
     * @param bean
     * @param bt
     */
    private void acceptInvate(final View v, final ByInvateYueZhan bean, Button bt) {
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                yuezhanType = 1;
                matchID = bean.getMatchId();
                postdata(bean.getInviteId(), yuezhanType);
                headeView = v;
            }
        });
    }

    /**
     * 拒绝邀请
     *
     * @param v
     * @param bean
     * @param bt
     */
    private void refuseInvate(final View v, final ByInvateYueZhan bean, Button bt) {
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                yuezhanType = 2;
                postdata(bean.getInviteId(), yuezhanType);
                headeView = v;
            }
        });
    }

    /**
     * 接受成功后弹窗
     */
    private void showSuccessToast() {
        View vv = LayoutInflater.from(mContext).inflate(R.layout.layout_yuezhan_dialog, null);
        Toast tt = new Toast(mContext);
        tt.setView(vv);
        tt.setDuration(Toast.LENGTH_LONG);
        tt.setGravity(Gravity.CENTER, 0, 0);
        tt.show();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (byinvate != null && byinvate.size() != 0 && isSuccess == true) {// 有接受的数据且约战详情界面处理成功，则在返回时去掉相关的数据,刷新界面
            if (headeView != null) {
                lvMyWarApply.removeHeaderView(headeView);
                viewList.remove(headeView);
                headeView = null;
            }
            BroadcastController.sendUserChangeBroadcase(mContext);
            isSuccess = false;
            page = 1;
            loadMyApplyWar();
        }
    }
    @Override
    public void refreView() {
        page = 1;
        // loadMyApplyWar();
        loadUserByInvate();
    }
}
