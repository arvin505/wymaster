package com.miqtech.master.client.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.ChooseAttentionAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.ContactMember;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.PingYinUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChooseAttentionActivity extends BaseActivity implements OnClickListener, TextWatcher,
        ChooseAttentionAdapter.CheckedUserListener {
    private Context context;
    private ListView lvAttention;
    private EditText edtSearch;
    private List<User> fans = new ArrayList<User>();
    private ChooseAttentionAdapter adapter;
    private HorizontalScrollView hsvContent;
    private LinearLayout llHasChoose;
    private TextView tvHasChoose;
    private int maxInviteMemberSize;
    private ImageView back;
    private RelativeLayout rlBack;

    protected void init() {
        super.init();
        setContentView(R.layout.activity_chooseattention);
        context = this;
        maxInviteMemberSize = getIntent().getIntExtra("maxInviteMemberSize", 0);
        initView();
        initData();
    }

    protected void initView() {
        super.initView();
        //setLeftBtnImage(R.drawable.back);
        //getLeftBtn().setOnClickListener(this);
        back = (ImageView) findViewById(R.id.ivBack);
        setLeftIncludeTitle("关注的人");
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        lvAttention = (ListView) findViewById(R.id.lvAttention);
        llHasChoose = (LinearLayout) findViewById(R.id.llHasChoose);
        hsvContent = (HorizontalScrollView) findViewById(R.id.hsvContent);
        tvHasChoose = (TextView) findViewById(R.id.tvHasChoose);
        rlBack = (RelativeLayout) findViewById(R.id.rlBack);
        adapter = new ChooseAttentionAdapter(context, fans, this, maxInviteMemberSize);
        lvAttention.setAdapter(adapter);
        edtSearch.addTextChangedListener(this);
        tvHasChoose.setOnClickListener(this);
        rlBack.setOnClickListener(this);
    }

    protected void initData() {
        super.initData();
        loadUserAttention();

    }

    private void initChecked() {

        for (int i = 0; i < fans.size(); i++) {
            int index = -1;
            for (int j = 0; j < InviteFriendsActivity.hasCommonMembers.size(); j++) {
                Object commonMember = InviteFriendsActivity.hasCommonMembers.get(j);
                if (commonMember instanceof User) {
                    if (fans.get(i).getId().equals(((User) commonMember).getId())) {
                        index = i;
                        fans.get(i).setIsChecked(1);
                    }
                }
            }
            if (!(index == i)) {
                fans.get(i).setIsChecked(0);
            }
        }
    }

    private void addBottomViews() {
        for (Object commonMember : InviteFriendsActivity.hasCommonMembers) {
            addBottomView(commonMember);
        }
    }

    private void addBottomView(Object commonMember) {
        String name = "";
        if (commonMember instanceof ContactMember) {
            name = ((ContactMember) commonMember).getContact_name();
        } else if (commonMember instanceof User) {
            name = ((User) commonMember).getNickname();
        }

        StringBuffer sb = new StringBuffer(name == null ? "" : name);
        try {
            name = PingYinUtil.substring(sb.reverse().toString(), 4, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuffer sb1 = new StringBuffer(name);
        name = sb1.reverse().toString();

        View view = View.inflate(context, R.layout.layout_common_member_img_item, null);
        TextView tvName = (TextView) view.findViewById(R.id.tvMemberName);
        tvName.setText(name);
        llHasChoose.addView(view);
        view.setTag(commonMember);
        view.setOnClickListener(new RemoveMemberOnClickListener());
        hsvContent.fullScroll(ScrollView.FOCUS_RIGHT);
    }

    private void removeBottomViewFromObj(User user) {
        for (int i = 0; i < llHasChoose.getChildCount(); i++) {
            Object commonMember = llHasChoose.getChildAt(i).getTag();
            if (commonMember instanceof User) {
                if (((User) commonMember).getId().equals(user.getId())) {
                    llHasChoose.removeViewAt(i);
                    return;
                }
            }
        }
    }

    private void loadUserAttention() {
        User user = WangYuApplication.getUser(context);
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ATTENTION_LIST, params, HttpConstant.ATTENTION_LIST);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        String obj = null;
        hideLoading();
        super.onSuccess(object, method);
        try {
            obj = object.getJSONObject("object").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(obj.toString())) {
            return;
        }
        if (method.equals(HttpConstant.ATTENTION_LIST)) {

            try {
                JSONObject jsonObj = new JSONObject(obj.toString());
                String listStr = jsonObj.getString("list");
                List<User> newFans = new Gson().fromJson(listStr, new TypeToken<List<User>>() {
                }.getType());
                fans.addAll(newFans);
                adapter.notifyDataSetChanged();
                initChecked();
                addBottomViews();
                tvHasChoose.setText("已选择" + InviteFriendsActivity.hasCommonMembers.size() + "/" + maxInviteMemberSize
                        + "人");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (method.equals(HttpConstant.SEARCH_ATTENTIONS)) {
            System.out.println(obj);
        }
    }

    @Override
    public void onError(String method, String errorInfo) {
        hideLoading();
        super.onError(method, errorInfo);
    }

    public class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String constraintStr = constraint.toString().toLowerCase(Locale.getDefault());
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                boolean isChinese = PingYinUtil.isChinese(constraintStr);
                if (isChinese) {
                    ArrayList<User> filterItems = new ArrayList<User>();
                    //
                    synchronized (this) {
                        for (User fan : fans) {
                            if (fan.getNickname().indexOf(constraintStr) != -1) {
                                filterItems.add(fan);
                            }
                        }
                        result.count = filterItems.size();
                        result.values = filterItems;
                    }
                } else {
                    ArrayList<User> filterItems = new ArrayList<User>();
                    //
                    synchronized (this) {
                        for (User fan : fans) {
                            if (PingYinUtil.getPingYin(fan.getNickname()).indexOf(constraintStr) != -1) {
                                filterItems.add(fan);
                            } else {
                                String contactJp = PingYinUtil.toJP(fan.getNickname()).toLowerCase();
                                if (contactJp.indexOf(constraintStr) != -1) {
                                    filterItems.add(fan);
                                }
                            }
                        }

                        result.count = filterItems.size();
                        result.values = filterItems;
                    }
                }
            } else {
                synchronized (this) {
                    result.count = fans.size();
                    result.values = fans;
                }
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<User> filtered = (ArrayList<User>) results.values;
            takeTheChecked2Result(filtered);
            adapter = new ChooseAttentionAdapter(context, filtered, ChooseAttentionActivity.this, maxInviteMemberSize);
            lvAttention.setAdapter(adapter);
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = s.toString();
        if (adapter != null && str != null) {
            adapter.getFilter().filter(str);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlBack:
                onBackPressed();
                break;
            case R.id.tvHasChoose:
                // Intent intent = new Intent();
                // intent.putParcelableArrayListExtra("selectMembers",
                // (ArrayList<? extends Parcelable>)
                // InviteFriendsActivity.hasCommonMembers);
                // InviteFriendsActivity.hasCommonMembers.clear();
                // AppManager.getAppManager().finishActivity(InviteFriendsActivity.class);
                setResult(RESULT_OK);
                finish();
                break;

            default:
                break;
        }
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
        for (int i = 0; i < InviteFriendsActivity.hasCommonMembers.size(); i++) {
            Object commonMember = InviteFriendsActivity.hasCommonMembers.get(i);
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

    private void takeTheChecked2Result(ArrayList<User> resultMembers) {
        for (int i = 0; i < fans.size(); i++) {
            for (int j = 0; j < resultMembers.size(); j++) {
                if (fans.get(i).getId().equals(resultMembers.get(j).getId())) {
                    if (fans.get(i).getIsChecked() == 0) {
                        resultMembers.get(j).setIsChecked(0);
                    } else if (fans.get(i).getIsChecked() == 1) {
                        resultMembers.get(j).setIsChecked(1);
                    }
                }
            }
        }
    }

    @Override
    public void selectedUser(User user) {
        if (InviteFriendsActivity.hasCommonMembers.size() < maxInviteMemberSize) {
            addBottomView(user);
            InviteFriendsActivity.hasCommonMembers.add(user);
            tvHasChoose.setText("已选择" + InviteFriendsActivity.hasCommonMembers.size() + "/" + maxInviteMemberSize + "人");
        } else {
            showToast("最多只能加" + maxInviteMemberSize + "人");
        }

    }

    @Override
    public void cancelSelectedUser(User user) {
        removeBottomViewFromObj(user);
        for (int i = 0; i < InviteFriendsActivity.hasCommonMembers.size(); i++) {
            Object commonMember = InviteFriendsActivity.hasCommonMembers.get(i);
            if (commonMember instanceof User) {
                if (((User) commonMember).getId().equals(user.getId())) {
                    InviteFriendsActivity.hasCommonMembers.remove(i);
                    break;
                }
            }
        }
        tvHasChoose.setText("已选择" + InviteFriendsActivity.hasCommonMembers.size() + "/" + maxInviteMemberSize + "人");
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
                InviteFriendsActivity.hasCommonMembers.remove(selectMember);
                tvHasChoose.setText("已选择" + InviteFriendsActivity.hasCommonMembers.size() + "/" + maxInviteMemberSize
                        + "人");
                initChecked();
                adapter.notifyDataSetChanged();
            }
        }

    }

}
