package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.InviteCommonAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.ContactMember;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.PingYinUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InviteFriendsActivity extends BaseActivity implements OnClickListener,
        InviteCommonAdapter.CommonMemberListener, OnItemClickListener {
    private ListView lvInviteFriend;
    private LinearLayout llHasChoose;
    private TextView tvHasChoose;
    private ImageView back;
    private Context context;
    private List<User> commonContacts = new ArrayList<User>();

    public static ArrayList hasCommonMembers = new ArrayList();

    private InviteCommonAdapter adapter;

    private int maxInviteMemberSize;

    private static int REQUEST_CONTACT = 1;
    private static int REQUEST_ADD_PHONE = 2;
    private static int REQUEST_ATTENTION = 3;

    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_invitefriend);
        context = this;
        maxInviteMemberSize = getIntent().getIntExtra("maxInviteMemberSize", 0);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        lvInviteFriend = (ListView) findViewById(R.id.lvInviteFriend);
        llHasChoose = (LinearLayout) findViewById(R.id.llHasChoose);
        tvHasChoose = (TextView) findViewById(R.id.tvHasChoose);

        View contactView = View.inflate(context, R.layout.layout_invitefriend_header, null);
        TextView tvContact = (TextView) contactView.findViewById(R.id.tvInviteItem);
        tvContact.setText("手机通讯录");
        View.inflate(context, R.layout.layout_invitefriend_header, null);
        //setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("邀请好友");
        back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(this);
        setListViewHeader();
        //getLeftBtn().setOnClickListener(this);
        tvHasChoose.setOnClickListener(this);
        lvInviteFriend.setOnItemClickListener(this);
    }

    private void setListViewHeader() {
        View contactView = View.inflate(context, R.layout.layout_invitefriend_header, null);
        TextView tvContact = (TextView) contactView.findViewById(R.id.tvInviteItem);
        tvContact.setText("手机通讯录");
        Drawable contactDrawable = getResources().getDrawable(R.drawable.add_contact);
        // / 这一步必须要做,否则不会显示.
        contactDrawable.setBounds(0, 0, contactDrawable.getMinimumWidth(), contactDrawable.getMinimumHeight());
        tvContact.setCompoundDrawables(contactDrawable, null, null, null);

        View phoneView = View.inflate(context, R.layout.layout_invitefriend_header, null);
        TextView tvPhone = (TextView) phoneView.findViewById(R.id.tvInviteItem);
        tvPhone.setText("添加手机号");
        Drawable phoneDrawable = getResources().getDrawable(R.drawable.add_phone);
        // / 这一步必须要做,否则不会显示.
        phoneDrawable.setBounds(0, 0, phoneDrawable.getMinimumWidth(), phoneDrawable.getMinimumHeight());
        tvPhone.setCompoundDrawables(phoneDrawable, null, null, null);

        View attentionView = View.inflate(context, R.layout.layout_invitefriend_header, null);
        TextView tvAttention = (TextView) attentionView.findViewById(R.id.tvInviteItem);
        tvAttention.setText("我关注的人");
        Drawable attentionDrawable = getResources().getDrawable(R.drawable.add_attention);
        // / 这一步必须要做,否则不会显示.
        attentionDrawable.setBounds(0, 0, attentionDrawable.getMinimumWidth(), attentionDrawable.getMinimumHeight());
        tvAttention.setCompoundDrawables(attentionDrawable, null, null, null);

        View lineView = View.inflate(context, R.layout.layout_invitefriend_header1, null);
        lvInviteFriend.addHeaderView(contactView);
        lvInviteFriend.addHeaderView(phoneView);
        lvInviteFriend.addHeaderView(attentionView);
        lvInviteFriend.addHeaderView(lineView);
        adapter = new InviteCommonAdapter(context, commonContacts, this, maxInviteMemberSize);
        lvInviteFriend.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        super.initData();
        ArrayList<Parcelable> selectMembers = getIntent().getParcelableArrayListExtra("selectMembers");
        hasCommonMembers.addAll(selectMembers);
        loadCommonMembers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyContactListView();
        llHasChoose.removeAllViews();
        addChooseMemberViews();
        tvHasChoose.setText("已选择" + hasCommonMembers.size() + "/" + maxInviteMemberSize + "人");
    }

    private void loadCommonMembers() {
        showLoading();
        User user = WangYuApplication.getUser(context);
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.COMMON_MEMBER, params, HttpConstant.COMMON_MEMBER);
    }

    private void notifyContactListView() {
        if (adapter != null) {
            // 判断我关注的人中 是否取消选择
            for (int i = 0; i < commonContacts.size(); i++) {
                boolean hasContact = false;
                User commonContact = commonContacts.get(i);
                for (int j = 0; j < hasCommonMembers.size(); j++) {
                    Object member = hasCommonMembers.get(j);
                    if (member instanceof User) {
                        if (commonContact.getId().equals(((User) member).getId())) {
                            hasContact = true;
                        }
                    }
                }
                if (hasContact) {
                    commonContact.setIsChecked(1);
                } else {
                    commonContact.setIsChecked(0);
                }
            }
            adapter.notifyDataSetChanged();

        }
    }

    private void addChooseMemberFromPosition(int position) {
        User member = commonContacts.get(position);
        View v = View.inflate(context, R.layout.layout_common_member_img_item, null);
        TextView tvMemberName = (TextView) v.findViewById(R.id.tvMemberName);
        String tempName = member.getNickname();
        StringBuffer sb = new StringBuffer(tempName);
        try {
            tempName = PingYinUtil.substring(sb.reverse().toString(), 4, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuffer sb1 = new StringBuffer(tempName);
        tvMemberName.setText(sb1.reverse().toString());
        llHasChoose.addView(v);
        hasCommonMembers.add(member);
        v.setTag(member);
        v.setOnClickListener(new RemoveMemberOnClickListener());
        tvHasChoose.setText("已选择" + hasCommonMembers.size() + "/" + maxInviteMemberSize + "人");
    }

    private void addChooseMemberViews() {
        for (Object member : hasCommonMembers) {
            addChooseMemberView(member);
        }
    }

    private void addChooseMemberView(Object member) {
        View v = View.inflate(context, R.layout.layout_common_member_img_item, null);
        TextView tvMemberName = (TextView) v.findViewById(R.id.tvMemberName);
        // Object commonMember = hasCommonMembers.get(i);
        String tempName = "";
        if (member instanceof User) {
            tempName = ((User) member).getNickname();

        } else if (member instanceof ContactMember) {
            tempName = ((ContactMember) member).getContact_name();
        }
        StringBuffer sb = new StringBuffer(tempName);
        try {
            tempName = PingYinUtil.substring(sb.reverse().toString(), 4, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuffer sb1 = new StringBuffer(tempName);
        tvMemberName.setText(sb1.reverse().toString());
        llHasChoose.addView(v);
        v.setOnClickListener(new RemoveMemberOnClickListener());
        v.setTag(member);
    }

    private void removeChooseMember(int position) {
        User selectMember = commonContacts.get(position);
        int childCount = llHasChoose.getChildCount();
        int index = 0;
        for (int i = 0; i < childCount; i++) {
            Object member = llHasChoose.getChildAt(i).getTag();
            if (member instanceof User) {
                if (selectMember instanceof User) {
                    if (((User) member).getId().equals(((User) selectMember).getId())) {
                        index = i;
                        break;
                    }
                }
            }
        }
        llHasChoose.removeViewAt(index);
        hasCommonMembers.remove(selectMember);

        tvHasChoose.setText("已选择" + hasCommonMembers.size() + "/" + maxInviteMemberSize + "人");
    }

    private void uploadInviteMembers() {
        ArrayList<String> result = getInvocationIdsAndPhones();

        String teamId = getIntent().getStringExtra("teamId");
        showLoading();
        User user = WangYuApplication.getUser(context);
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("teamId", teamId);
        params.put("invocationIds", result.get(0));
        params.put("phones", result.get(1));
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INVITE_TEAMMATE, params, HttpConstant.INVITE_TEAMMATE);
    }

    private ArrayList<String> getInvocationIdsAndPhones() {
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
        hideLoading();
        super.onSuccess(object,method);
        try {
            if (method.equals(HttpConstant.COMMON_MEMBER)) {
                JSONArray obj = object.getJSONArray("object");
                List<User> newCommonContacts = new Gson().fromJson(obj.toString(), new TypeToken<List<User>>() {
                }.getType());
                commonContacts.addAll(newCommonContacts);
                adapter.notifyDataSetChanged();
                notifyContactListView();
            } else if (method.equals(HttpConstant.INVITE_TEAMMATE)) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String method, String errorInfo) {
        super.onError(method, errorInfo);
        hideLoading();
        showToast(errorInfo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvHasChoose:
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra("selectMembers",  hasCommonMembers);
                setResult(RESULT_OK, intent);
                finish();
                hasCommonMembers.clear();
                break;
            default:
                break;
        }
    }

    @Override
    public void selectMember(int position) {
        if (hasCommonMembers.size() < maxInviteMemberSize) {
            addChooseMemberFromPosition(position);
        } else {
            showToast("最多只能加" + maxInviteMemberSize + "人");
        }

    }

    @Override
    public void cancelSelectMember(int position) {
        removeChooseMember(position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra("selectMembers", (ArrayList<? extends Parcelable>) hasCommonMembers);
            setResult(RESULT_OK, intent);
            finish();
            hasCommonMembers.clear();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        // TODO Auto-generated method stub
        int currentPosition = parent.getSelectedItemPosition();
        if (currentPosition == -1) {
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.llInviteHeader:
                    if (position == 0) {
                        intent.putParcelableArrayListExtra("selectMembers", new ArrayList<ContactMember>());
                        intent.putExtra("maxInviteMemberSize", maxInviteMemberSize);
                        intent.setClass(this, ContactsActivity.class);
                        startActivityForResult(intent, REQUEST_CONTACT);
                    } else if (position == 1) {
                        intent.putExtra("maxInviteMemberSize", maxInviteMemberSize);
                        intent.setClass(this, InviteAddPhoneActivity.class);
                        startActivityForResult(intent, REQUEST_ADD_PHONE);
                    } else if (position == 2) {
                        intent.putExtra("maxInviteMemberSize", maxInviteMemberSize);
                        intent.setClass(this, ChooseAttentionActivity.class);
                        startActivityForResult(intent, REQUEST_ATTENTION);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private class RemoveMemberOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            Object selectMember = v.getTag();
            int childCount = llHasChoose.getChildCount();
            int index = -1;
            for (int i = 0; i < childCount; i++) {
                Object member = llHasChoose.getChildAt(i).getTag();
                if (member instanceof User) {
                    if (selectMember instanceof User) {
                        if (((User) member).getId().equals(((User) selectMember).getId())) {
                            index = i;
                            break;
                        }
                    }
                } else if (member instanceof ContactMember) {
                    if (selectMember instanceof ContactMember) {
                        if (((ContactMember) member).getContact_name().equals(
                                ((ContactMember) selectMember).getContact_name())) {
                            index = i;
                            break;
                        }
                    }

                }
            }
            if (index != -1) {
                llHasChoose.removeViewAt(index);
                hasCommonMembers.remove(selectMember);
                tvHasChoose.setText("已选择" + hasCommonMembers.size() + "/" + maxInviteMemberSize + "人");
                notifyContactListView();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hasCommonMembers.clear();
    }
}
