package com.miqtech.master.client.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.miqtech.master.client.adapter.MyCorpsV2Adapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.ActivityCard;
import com.miqtech.master.client.entity.Corps;
import com.miqtech.master.client.entity.TeamInviteInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.CorpsDetailsV2Activity;
import com.miqtech.master.client.ui.EnterMatchCardActivity;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.MyCorpsActivity;
import com.miqtech.master.client.ui.MyMatchActivity;
import com.miqtech.master.client.ui.basefragment.MyBaseFragment;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.ToastUtil;
import com.miqtech.master.client.view.HasErrorListView;
import com.miqtech.master.client.view.MyAlertView;
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
public class FragmentMyCorps extends MyBaseFragment implements AdapterView.OnItemClickListener {

    private View mainView, invateView;

    private MyCorpsActivity mContext;

    private PullToRefreshListView prlvMyCorps;

    private boolean isFirst = true;

    private MyCorpsV2Adapter adapter;

    private List<Corps> corpsList = new ArrayList<Corps>();

    private int page = 1;

    private int rows = 10;

    private int isLast;

    private int currentId = -1;

    private HasErrorListView lvCorps;

    private User user;

    private List<TeamInviteInfo> inviteList = new ArrayList<TeamInviteInfo>();

    private ImageView ivheader;// 邀请的头像
    private TextView tvInvateName;// 邀请人名字
    private TextView tvInvateTeam;// 邀请人战队
    private TextView tvtitle;// 约站标题
    private Button btNo;// 拒绝邀请
    private Button btYes;// 接受邀请

    private View headeView;// 要删除的listView的头部

    private int invideType;// 0拒绝1接受,2接受但人已满
    private int invideId;//邀请ID
    private Boolean isSkip = false;// 用来判断是否进行接受邀请成功后的跳转
    private int teamInvideID;
    public static Boolean isSuccess = false;
    private List<View> viewList = new ArrayList<View>();
    private final static int REQUEST_CARD = 1;
    private ActivityCard activityCard;
    private long activityCardID;//参赛卡ID

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (mainView == null) {
            mainView = inflater.inflate(R.layout.corps_list, null);
            mContext = (MyCorpsActivity) inflater.getContext();

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

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLoading();
        loadTeamInvite();
        loadMyCorps();
    }

    private void initView() {
        prlvMyCorps = (PullToRefreshListView) mainView.findViewById(R.id.prlvMyCorps);
        prlvMyCorps.setMode(PullToRefreshBase.Mode.BOTH);
        lvCorps = prlvMyCorps.getRefreshableView();
        adapter = new MyCorpsV2Adapter(mContext, corpsList);
        prlvMyCorps.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HasErrorListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                page = 1;
                showLoading();
                 loadMyCorps();
                loadTeamInvite();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HasErrorListView> refreshView) {
                if (inviteList.size() > 0) {
                    if (isLast == 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                page++;
                                showLoading();
                                loadMyCorps();
                            }
                        }, 1000);
                    } else {
                        showToast(mContext.getResources().getString(R.string.load_no));
                        prlvMyCorps.onRefreshComplete();
                    }
                } else {
                prlvMyCorps.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getActivity().getResources().getString(R.string.noNeteork));
                }
            }
        });
        lvCorps.setOnItemClickListener(this);
    }

    private void initData() {
//		adapter.notifyDataSetChanged();
    }

    /**
     * 把邀请加入到listView
     */
    private void updateInviteView() {
        lvCorps.setErrorView("太低调了,还没有加入任何战队");

        if (inviteList.size() != 0) {// 有数据才显示加载
            BroadcastController.sendUserChangeBroadcase(mContext);
            for (final TeamInviteInfo bean : inviteList) {
                invateView = LayoutInflater.from(mContext).inflate(R.layout.layout_yuezhan_invate_item, null);
                btNo = (Button) invateView.findViewById(R.id.btno_yuezhan_item);
                btYes = (Button) invateView.findViewById(R.id.btyes_yuezhan_item);
                ivheader = (ImageView) invateView.findViewById(R.id.ivHeader_yuezhan_item);
                tvInvateName = (TextView) invateView.findViewById(R.id.tvname_yuezhan_item_);
                tvInvateTeam = (TextView) invateView.findViewById(R.id.tvteam_yuezhan_item);

                AsyncImage.loadAvatar(mContext, HttpConstant.SERVICE_UPLOAD_AREA + bean.getIcon(), ivheader);
                tvInvateName.setText(bean.getNickname() + "邀请你加入战队");
                tvInvateTeam.setText("战队:" + bean.getName());
                invateView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent i = new Intent(mContext, CorpsDetailsV2Activity.class);
                        i.putExtra("teamId", bean.getTeam_id());
                        i.putExtra("MyCropInvideID", bean.getInvocation_id() + "");
                        mContext.startActivity(i);
                        headeView = invateView;
                    }
                });
                acceptInvate(invateView, bean, btYes);
                refuseInvate(invateView, bean, btNo);
                lvCorps.addHeaderView(invateView);
                viewList.add(invateView);
            }
        }

        lvCorps.setAdapter(adapter);
        loadMyCorps();
    }

    private void loadTeamInvite() {
        User user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("phone", user.getUsername());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.TEAM_INVITE, map, HttpConstant.TEAM_INVITE);
    }

    public void loadMyCorps() {
        User user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("page", page + "");
        map.put("pageSize", rows + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_CORPS, map, HttpConstant.MY_CORPS);
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
            } else {
                obj = object.toString();
            }
            if (method.equals(HttpConstant.MY_CORPS)) {
                if (obj == null) {
                    return;
                }
                JSONObject jsonList = new JSONObject(obj.toString());
                String strList = jsonList.getString("teamList");
                isLast = jsonList.getInt("isLast");

                List<Corps> newCorps = new Gson().fromJson(strList, new TypeToken<List<Corps>>() {
                }.getType());
                if (page == 1) {
                    corpsList.clear();
                }
                corpsList.addAll(newCorps);

                if (page == 1) {
                    if (inviteList.size() == 0 && corpsList.size() == 0) {
                        lvCorps.setErrorShow(true);
                    }

                    if (isSkip == true) {
                        Intent i = new Intent(mContext, CorpsDetailsV2Activity.class);
                        i.putExtra("teamId", teamInvideID);
                        i.putExtra("showquit", 1);
                        mContext.startActivity(i);
                        isSkip = false;
                    }
                } else {
                    lvCorps.setErrorShow(false);
                }
                adapter.notifyDataSetChanged();
            }
            if (method.equals(HttpConstant.EXIT_CORPS)) {

            } else if (method.equals(HttpConstant.TEAM_INVITE)) {
                List<TeamInviteInfo> newInvate = new Gson().fromJson(obj.toString(), new TypeToken<List<TeamInviteInfo>>() {
                }.getType());
                if (inviteList.size() != 0) {
                    inviteList.clear();
                    for (View v : viewList) {
                        lvCorps.removeHeaderView(v);
                    }
                }
                inviteList.addAll(newInvate);
                updateInviteView();
            } else if (method.equals(HttpConstant.DO_TEAM_INVITE)) {
                showdialog();
            } else if (method.equals(HttpConstant.ACTIVITY_CARD)) {//获取参赛卡
                if (object.has("object")) {
                    activityCard = GsonUtil.getBean(object.getString("object").toString(), ActivityCard.class);
                    activityCardID = activityCard.getId();
                    postdata(invideId, invideType);
                } else {
                    Intent intent = new Intent(mContext, EnterMatchCardActivity.class);
                    mContext.startActivityForResult(intent, REQUEST_CARD);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        prlvMyCorps.onRefreshComplete();
    }

    @Override
    public void onError(String errMsg, String method) {
        hideLoading();
        ToastUtil.showToast(errMsg, mContext);
        prlvMyCorps.onRefreshComplete();
        lvCorps.setErrorShow(false);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        prlvMyCorps.onRefreshComplete();
        lvCorps.setErrorShow(false);
        try {
            if (method.equals(HttpConstant.DO_TEAM_INVITE)) {
                if ("用户尚未注册或资料不完善".equals(object.getString("result"))) {
                    showFileDialog(object.getString("result"), "加入失败");
                } else {
                    showFileDialog(object.getString("result"), "加入失败");
                    lvCorps.removeHeaderView(headeView);
                    viewList.remove(headeView);
                    headeView = null;
                }
            } else {
                ToastUtil.showToast(object.getString("result"), mContext);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface ItemAction {
        void onEditAction(int postion);

        void onExitAction(int postion);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getAdapter().getItemId(position) == -1) {
        } else {
            Intent intent = null;
            Corps corps = (Corps) parent.getAdapter().getItem(position);
            user = WangYuApplication.getUser(mContext);
            if (user != null) {
                intent = new Intent(mContext, CorpsDetailsV2Activity.class);
                intent.putExtra("teamId", corps.getTeam_Id());
                intent.putExtra("matchId", corps.getActivity_id());
                intent.putExtra("isJoin", corps.getIs_join());
                mContext.startActivity(intent);
            } else {
                intent = new Intent();
                intent.setClass(mContext, LoginActivity.class);
                mContext.startActivity(intent);
            }
        }
    }

    /**
     * 提交处理邀请的请求
     *
     * @param id   邀请记录id
     * @param type 0拒绝1接受
     */
    private void postdata(int id, int type) {
        showLoading();
        User user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("phone", user.getUsername());
        if (!TextUtils.isEmpty(activityCardID + "")) {//拒绝时可不传，接受时要传
            map.put("cardId", activityCardID + "");
        }
        map.put("id", id + "");
        map.put("type", type + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DO_TEAM_INVITE, map, HttpConstant.DO_TEAM_INVITE);
    }

    /**
     * 接受邀请
     *
     * @param v
     * @param bean
     * @param bt
     */
    private void acceptInvate(final View v, final TeamInviteInfo bean, Button bt) {
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                invideType = 1;
                teamInvideID = bean.getTeam_id();
                invideId = bean.getInvocation_id();
//                postdata(invideId, invideType);
                headeView = v;
                loadMyActivityCard();
            }
        });
    }

    /**
     * 查看我的参赛卡，接受邀请时调用
     * 有参赛卡则获取参赛卡ID
     * 没有参赛卡则跳到添加参赛卡的界面
     */
    private void loadMyActivityCard() {
        showLoading();
        User user = WangYuApplication.getUser(mContext);
        if (user != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userId", user.getId());
            params.put("token", user.getToken());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ACTIVITY_CARD, params, HttpConstant.ACTIVITY_CARD);
        }
    }

    /**
     * 拒绝邀请
     *
     * @param v
     * @param bean
     * @param bt
     */
    private void refuseInvate(final View v, final TeamInviteInfo bean, Button bt) {
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                invideType = 0;
                postdata(bean.getInvocation_id(), invideType);
                headeView = v;
            }
        });
    }

    /**
     * 提示战队邀请处理结果
     */
    private void showdialog() {
        lvCorps.removeHeaderView(headeView);
        viewList.remove(headeView);
        if (invideType == 1) {// 接受加入，并且成功，然后跳转约战详情
            showSuccessToast();
            isSkip = true;
            page = 1;
            showLoading();
            loadMyCorps();
        } else if (invideType == 0) {// 拒绝加入，刷新本界面
            invideType = 1;
        }
        headeView = null;
        BroadcastController.sendUserChangeBroadcase(mContext);
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

    /**
     * 提交处理后 战队已满和只能加入一只队伍的处理
     *
     * @param message
     * @param title
     */
    private void showFileDialog(String message, String title) {
        MyAlertView.Builder builder = new MyAlertView.Builder(mContext);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        builder.create().show();
        // lvCorps.removeHeaderView(headeView);
        // viewList.remove(headeView);
        // headeView = null;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (inviteList != null && isSuccess == true && inviteList.size() != 0) {// 有接受的数据且战队详情界面处理成功，则在返回时去掉相关的数据,刷新界面
            if (headeView != null) {
                lvCorps.removeHeaderView(headeView);
                viewList.remove(headeView);
                headeView = null;
            }
            BroadcastController.sendUserChangeBroadcase(mContext);
            isSuccess = false;
            page = 1;
            showLoading();
            loadMyCorps();
        }
    }

    @Override
    public void refreView() {
        page = 1;
        showLoading();
        // loadMyCorps();
        loadTeamInvite();
    }
}
