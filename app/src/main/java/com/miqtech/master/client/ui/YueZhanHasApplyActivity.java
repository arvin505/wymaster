package com.miqtech.master.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.YueZhanHasApplyAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.ContactMember;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.entity.YueZhanApply;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.view.MyListView;
import com.miqtech.master.client.view.RefreshLayout;

public class YueZhanHasApplyActivity extends BaseActivity implements OnClickListener, YueZhanHasApplyAdapter.HandleListener,
        OnItemClickListener, RefreshLayout.OnLoadListener {

    private MyListView lvHasApply;

    private YueZhanHasApplyAdapter adapter;

    private int position = -1;

    private RefreshLayout refresh_view;

    private int page = 1;

    private int rows = 20;

    private int isLast;
    private boolean isLoadMore = false;

    private List<YueZhanApply> applies = new ArrayList<YueZhanApply>();

    private final static int CONTACT_REQUEST = 1;

    private Dialog contactDialog;

    private ImageView back;

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.activity_yuezhanhasapply);
        initView();
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();
        lvHasApply = (MyListView) findViewById(R.id.lvHasApply);
        refresh_view = (RefreshLayout) findViewById(R.id.refresh_view);
        refresh_view.setOnLoadListener(this);
        refresh_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                loadMatchAppliers();
            }
        });
        refresh_view.setColorSchemeResources(R.color.colorActionBarSelected);
//		getLeftBtn().setOnClickListener(this);
        //setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("约战报名");
        if (YueZhanDetailsActivity.yueZhan.getUserStatus() == 1) {
            View header = View.inflate(this, R.layout.layout_yuezhanhasapplylist_header, null);
            View viewAdd = header.findViewById(R.id.ivAdd);
            lvHasApply.addHeaderView(header);
            viewAdd.setOnClickListener(this);
        }
        adapter = new YueZhanHasApplyAdapter(this, applies, this);
        lvHasApply.setAdapter(adapter);
        lvHasApply.setOnItemClickListener(this);

        back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        loadMatchAppliers();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        loadMatchAppliers();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.ivAdd:
                Intent intent = new Intent();
                ArrayList<ContactMember> mSelectMembers = new ArrayList<ContactMember>();
                intent.putParcelableArrayListExtra("selectMembers", mSelectMembers);
                intent.setClass(this, InviteFriendsActivity.class);
                intent.putExtra("maxInviteMemberSize", 50);
                intent.putExtra("isYueZhan", true);
                startActivityForResult(intent, CONTACT_REQUEST);
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.individual_join:
                YueZhanApply applie = applies.get(position);
                Uri uri = Uri.parse("smsto:" + applie.getTelephone());
                Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(sendIntent);
                contactDialog.dismiss();
                break;
            case R.id.corps_join:
                YueZhanApply applie1 = applies.get(position);
                // Intent phoneIntent = new Intent(Intent.ACTION_CALL,
                // Uri.parse("tel:" + applie1.getTelephone()));
                // startActivity(phoneIntent);

                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + applie1.getTelephone()));
                phoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(phoneIntent);
                contactDialog.dismiss();
                break;
            default:
                break;
        }
    }

    private Dialog createDialog() {
        contactDialog = new Dialog(this, R.style.searchStyle);
        contactDialog.setContentView(R.layout.join_dialog);
        Window dialogWindow = contactDialog.getWindow();
        dialogWindow.setWindowAnimations(R.style.windowStyle);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);

        Button contactMsg = (Button) contactDialog.findViewById(R.id.individual_join);
        Button contactPhone = (Button) contactDialog.findViewById(R.id.corps_join);

        contactMsg.setText("短信联系");
        contactPhone.setText("电话联系");

        contactMsg.setOnClickListener(this);
        contactPhone.setOnClickListener(this);

        return contactDialog;
    }

    @Override
    public void deletePerson(int position) {
        // TODO Auto-generated method stub
        this.position = position;
        showDialog();
    }

    @Override
    public void contactPerson(int position) {
        // TODO Auto-generated method stub
        this.position = position;
        if (contactDialog == null) {
            contactDialog = createDialog();
        }
        contactDialog.show();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setIcon(R.drawable.icon);
        builder.setMessage("确认移除？？？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                removePerson(position);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.show();
    }

    private void loadMatchAppliers() {
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(WangYuApplication.appContext);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("id", YueZhanDetailsActivity.yueZhan.getId() + "");
        params.put("page", page + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MATCH_APPLIERS, params, HttpConstant.MATCH_APPLIERS);
    }

    private void invicationMembers(List<Object> hasCommonMembers) {
        ArrayList<String> result = getInvocationIdsAndPhones(hasCommonMembers);
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(WangYuApplication.appContext);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("id", YueZhanDetailsActivity.yueZhan.getId() + "");
        params.put("invocationIds", result.get(0) + "");
        params.put("phoneNums", result.get(1) + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INVITE_SYS_USER_AND_CONTACT_USER, params, HttpConstant.INVITE_SYS_USER_AND_CONTACT_USER);
    }

    private ArrayList<String> getInvocationIdsAndPhones(List<Object> hasCommonMembers) {
        String invocationIds = "";
        String phones = "";
        for (int i = 0; i < hasCommonMembers.size(); i++) {
            Object commonMember = hasCommonMembers.get(i);
            if (commonMember instanceof User) {
                invocationIds += ((User) commonMember).getId() + ",";
            } else if (commonMember instanceof ContactMember) {
                phones += ((ContactMember) commonMember).getContact_phone() + ",";

            }
        }

        if (phones.length() > 0) {
            phones = (String) phones.subSequence(0, phones.length() - 1);
        }
        if (invocationIds.length() > 0) {
            invocationIds = (String) invocationIds.subSequence(0, invocationIds.length() - 1);
        }

        ArrayList<String> result = new ArrayList<String>();
        result.add(invocationIds);
        result.add(phones);
        return result;
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        // TODO Auto-generated method stub
        refresh_view.setRefreshing(false);
        String obj = null;
        try {
            obj = object.getJSONObject("object").toString();
            if (method.equals(HttpConstant.REMOVE_MATCH_PERSON)) {
                page = 1;
                showToast("移除成功");
                loadMatchAppliers();
            } else if (method.equals(HttpConstant.MATCH_APPLIERS)) {
                try {
                    JSONObject jsonObj = new JSONObject(obj.toString());
                    isLast = jsonObj.getInt("isLast");
                    List<YueZhanApply> newApplies = new Gson().fromJson(jsonObj.getString("list"),
                            new TypeToken<List<YueZhanApply>>() {
                            }.getType());
                    if (page == 1) {
                        applies.clear();
                    }
                    if (!newApplies.isEmpty()) {
                        applies.addAll(newApplies);
                        adapter.notifyDataSetChanged();
                    } else {
                        if (isLoadMore) {
                            page--;
                            refresh_view.setLoading(false);
                            showToast(R.string.nomore);
                            isLoadMore = false;
                        }
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if (method.equals(HttpConstant.INVITE_SYS_USER_AND_CONTACT_USER)) {
                showToast("邀请成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String method, String errorInfo) {
        // TODO Auto-generated method stub
        if (isLoadMore) {
            refresh_view.setLoading(false);
        }
        refresh_view.setRefreshing(false);
        showToast(errorInfo);
    }

    private void removePerson(int position) {
        YueZhanApply apply = applies.get(position);
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(WangYuApplication.appContext);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("applyId", apply.getApply_id() + "");
        params.put("matchId", YueZhanDetailsActivity.yueZhan.getId() + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.REMOVE_MATCH_PERSON, params, HttpConstant.REMOVE_MATCH_PERSON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_REQUEST && resultCode == RESULT_OK) {
            ArrayList<ContactMember> selectMembers = data.getParcelableArrayListExtra("selectMembers");
            ArrayList<Object> mSelectMembers = new ArrayList<Object>();
            mSelectMembers.addAll(selectMembers);
            if (selectMembers.size() != 0) {
                invicationMembers(mSelectMembers);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int itemPosition = (int) parent.getAdapter().getItemId(position);
        if (itemPosition!=-1&&itemPosition < applies.size()) {
            YueZhanApply apply = applies.get((int) parent.getAdapter().getItemId(position));
            if (apply != null) {
                if (!TextUtils.isEmpty(apply.getUser_id())) {
                    Intent intent = new Intent();
                    intent.putExtra("id", apply.getUser_id());
                    intent.setClass(this, PersonalHomePageActivity.class);
                    startActivity(intent);
                }
            }
        }

    }

    @Override
    public void onLoad() {
        page++;
        isLoadMore = true;
        loadMatchAppliers();
    }
}
