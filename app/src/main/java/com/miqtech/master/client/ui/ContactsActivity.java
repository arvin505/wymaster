package com.miqtech.master.client.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;


import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.ContactListAdapter;
import com.miqtech.master.client.entity.ContactMember;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.stickylistheaders.StickyListHeadersListView;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.PingYinUtil;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ContactsActivity extends BaseActivity implements OnClickListener, OnItemClickListener, TextWatcher {

    private Cursor c;

    public static ArrayList<ContactMember> members;

    private MyHandler myHandler;

    private StickyListHeadersListView lvContact;

    private ContactListAdapter adapter;

    private Context context;

    private TextView tvSelectedMembersNum;

    private RelativeLayout rlChoose;

    // private ArrayList<ContactMember> mSelectMembers = new
    // ArrayList<ContactMember>();

    private EditText edtSearch;

    private LinearLayout llHasChoose;

    private TextView tvHasChoose;

    private HorizontalScrollView hsvContent;
    public int maxInviteMemberSize;

    private ImageView back;
    private View viewById;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_contacts);
        context = this;

        initView();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
//		setLeftBtnImage(R.drawable.back);
        setLeftIncludeTitle("邀请好友");
        lvContact = (StickyListHeadersListView) findViewById(R.id.lvContact);
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        llHasChoose = (LinearLayout) findViewById(R.id.llHasChoose);
        tvHasChoose = (TextView) findViewById(R.id.tvHasChoose);
        hsvContent = (HorizontalScrollView) findViewById(R.id.hsvContent);
        back = (ImageView) findViewById(R.id.ivBack);
        back.setOnClickListener(this);
        lvContact.setOnItemClickListener(this);
//		getLeftBtn().setOnClickListener(this);
        edtSearch.addTextChangedListener(this);
        tvHasChoose.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        initContactList();
        maxInviteMemberSize = getIntent().getIntExtra("maxInviteMemberSize", 0);
        for (int i = 0; i < InviteFriendsActivity.hasCommonMembers.size(); i++) {
            Object commonMember = InviteFriendsActivity.hasCommonMembers.get(i);
            addPhoneNumBottomView(commonMember);
        }
        tvHasChoose.setText("已选择" + InviteFriendsActivity.hasCommonMembers.size() + "/" + maxInviteMemberSize + "人");
    }

    private void addPhoneNumBottomView(Object commonMember) {
        String name = "";
        if (commonMember instanceof ContactMember) {
            name = ((ContactMember) commonMember).getContact_name();
        } else if (commonMember instanceof User) {
            name = ((User) commonMember).getNickname();
        }
        StringBuffer sb = new StringBuffer(name);
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

        hsvContent.fullScroll(ScrollView.FOCUS_RIGHT);
        view.setOnClickListener(new RemoveMemberOnClickListener());

        // hsvContent.fullScroll(ScrollView.FOCUS_RIGHT);
    }

    private void initChecked() {

        for (int i = 0; i < members.size(); i++) {
            int index = -1;
            for (int j = 0; j < InviteFriendsActivity.hasCommonMembers.size(); j++) {
                Object commonMember = InviteFriendsActivity.hasCommonMembers.get(j);
                if (commonMember instanceof ContactMember) {
                    if (members.get(i).getContact_name().equals(((ContactMember) commonMember).getContact_name())
                            && members.get(i).getContact_phone()
                            .equals(((ContactMember) commonMember).getContact_phone())) {
                        index = i;
                        members.get(i).setIsChecked(1);
                    }
                }
            }
            if (!(index == i)) {
                members.get(i).setIsChecked(0);
            }
        }
    }

    private void initContactList() {
        showLoading();
        myHandler = new MyHandler();
        new GetContactThead().start();
    }


    // 获取联系人 线程， 开一个子线程防止阻塞主线程
    private class GetContactThead extends Thread {
        @Override
        public void run() {
            super.run();
            members = getContact();
            myHandler.sendEmptyMessage(0);
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            hideLoading();
            // ArrayList<ContactMember> selectMembers =
            // getIntent().getParcelableArrayListExtra("selectMembers");
            initChecked();
            adapter = new ContactListAdapter(context, members, new MembersListener(), maxInviteMemberSize);
            lvContact.setAdapter(adapter);
        }
    }

    public ArrayList<ContactMember> getContact() {
        ArrayList<ContactMember> listMembers = new ArrayList<ContactMember>();
        Cursor cursor = null;
        try {

            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            // 这里是获取联系人表的电话里的信息 包括：名字，名字拼音，联系人id,电话号码；
            // 然后在根据"sort-key"排序
            cursor = getContentResolver().query(uri,
                    new String[]{"display_name", "sort_key", "contact_id", "data1", Photo.PHOTO_ID}, null, null,
                    "sort_key asc");
            if (cursor.moveToFirst()) {
                do {
                    ContactMember contact = new ContactMember();
                    String contact_phone = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String name = cursor.getString(0);
                    String sortKey = getSortKey(cursor.getString(1));
                    int contact_id = cursor.getInt(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    contact.contact_name = name;
                    contact.sortKey = sortKey;
                    contact.contact_phone = contact_phone;
                    contact.setContact_id(contact_id);

                    if (name != null)
                        listMembers.add(contact);
                } while (cursor.moveToNext());
                c = cursor;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return listMembers;
    }

    /**
     * 获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
     *
     * @param sortKeyString 数据库中读取出的sort key
     * @return 英文字母或者#
     */
    private static String getSortKey(String sortKeyString) {
        String key = sortKeyString.substring(0, 1).toUpperCase();
        if (PingYinUtil.isChinese(key)) {
            String sortKey = converterToFirstSpell(key);
            return sortKey;
        } else if (key.matches("[A-Z]")) {
            return key;
        }
        return "#";
    }

    /**
     * 汉字转换位汉语拼音首字母，英文字符不变
     *
     * @param
     * @return 拼音
     */
    public static String converterToFirstSpell(String chines) {
        String pinyinName = "";
        if (chines == null) {
            return "#";
        }
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        if (nameChar[0] > 128) {
            try {
                pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[0], defaultFormat)[0].charAt(0);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        } else {
            pinyinName += nameChar[0];
        }
        return pinyinName;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            // case R.id.rlChoose:
            // mSelectMembers.clear();
            // for (int i = 0; i < members.size(); i++) {
            // if (members.get(i).getIsChecked() == 1) {
            // mSelectMembers.add(members.get(i));
            // }
            // }
            // Intent intent = new Intent();
            // intent.setClass(this, ReleaseWarActivity.class);
            // intent.putParcelableArrayListExtra("selectMembers", mSelectMembers);
            // setResult(RESULT_OK, intent);
            // finish();
            // break;
            case R.id.tvHasChoose:
                setResult(RESULT_OK);
                finish();
                break;

            default:
                break;
        }
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

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

    }

    private class MembersListener implements ContactListAdapter.SelectedMembersListener {

        @Override
        public void cancelSelectedMember(ContactMember member) {
            removeBottomView(member);
            for (int i = 0; i < InviteFriendsActivity.hasCommonMembers.size(); i++) {
                Object commonMember = InviteFriendsActivity.hasCommonMembers.get(i);
                if (commonMember instanceof ContactMember) {
                    if (((ContactMember) commonMember).getIsAddPhone() == 0) {
                        if (((ContactMember) commonMember).getContact_name().equals(member.getContact_name())
                                && ((ContactMember) commonMember).getContact_phone().equals(member.getContact_phone())) {
                            InviteFriendsActivity.hasCommonMembers.remove(i);
                            break;
                        }
                    }
                }
            }
            tvHasChoose
                    .setText("已选择" + InviteFriendsActivity.hasCommonMembers.size() + "/" + maxInviteMemberSize + "人");
        }

        @Override
        public void selectedMember(ContactMember member) {
            if (InviteFriendsActivity.hasCommonMembers.size() < maxInviteMemberSize) {
                addBottomView(member);
                InviteFriendsActivity.hasCommonMembers.add(member);
                tvHasChoose
                        .setText("已选择" + InviteFriendsActivity.hasCommonMembers.size() + "/" + maxInviteMemberSize + "人");
            } else {
                showToast("最多只能加" + maxInviteMemberSize + "人");
            }

        }
    }

    private void addBottomView(ContactMember contactMember) {
        String name = "";
        name = ((ContactMember) contactMember).getContact_name();
        StringBuffer sb = new StringBuffer(name);
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
        view.setTag(contactMember);
        view.setOnClickListener(new RemoveMemberOnClickListener());
        hsvContent.fullScroll(ScrollView.FOCUS_RIGHT);
    }

    private void removeBottomView(ContactMember contactMember) {
        for (int i = 0; i < llHasChoose.getChildCount(); i++) {
            Object commonMember = llHasChoose.getChildAt(i).getTag();
            if (commonMember instanceof ContactMember) {
                if (((ContactMember) commonMember).getIsAddPhone() == 0) {
                    if (((ContactMember) commonMember).getContact_name().equals(contactMember.getContact_name())
                            && ((ContactMember) commonMember).getContact_phone().equals(
                            contactMember.getContact_phone())) {
                        llHasChoose.removeViewAt(i);
                        return;
                    }
                }
            }
        }
    }

    public class ListFilter extends Filter {
        protected FilterResults performFiltering(CharSequence constraint) {
            // NOTE: this function is *always* called from a background thread,
            // and
            // not the UI thread.
            String constraintStr = constraint.toString().toLowerCase(Locale.getDefault());
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                boolean isChinese = PingYinUtil.isChinese(constraintStr);
                if (isChinese) {
                    ArrayList<ContactMember> filterItems = new ArrayList<ContactMember>();
                    //
                    synchronized (this) {
                        for (ContactMember member : members) {
                            if (member.getContact_name().indexOf(constraintStr) != -1) {
                                filterItems.add(member);
                            }
                        }
                        result.count = filterItems.size();
                        result.values = filterItems;
                    }
                } else {
                    ArrayList<ContactMember> filterItems = new ArrayList<ContactMember>();
                    //
                    synchronized (this) {
                        for (ContactMember member : members) {
                            if (PingYinUtil.getPingYin(member.getContact_name()).indexOf(constraintStr) != -1) {
                                filterItems.add(member);
                            } else {
                                String contactJp = PingYinUtil.toJP(member.getContact_name()).toLowerCase();
                                if (contactJp.indexOf(constraintStr) != -1) {
                                    filterItems.add(member);
                                }
                            }
                        }

                        result.count = filterItems.size();
                        result.values = filterItems;
                    }
                }
            } else {
                synchronized (this) {
                    result.count = members.size();
                    result.values = members;
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<ContactMember> filtered = (ArrayList<ContactMember>) results.values;
            takeTheChecked2Result(filtered);
            adapter = new ContactListAdapter(context, filtered, new MembersListener(), maxInviteMemberSize);
            lvContact.setAdapter(adapter);
        }

    }

    private void takeTheChecked2Result(ArrayList<ContactMember> resultMembers) {
        for (int i = 0; i < members.size(); i++) {
            for (int j = 0; j < resultMembers.size(); j++) {
                if (members.get(i).getContact_name().equals(resultMembers.get(j).getContact_name())) {
                    if (members.get(i).getIsChecked() == 0) {
                        resultMembers.get(j).setIsChecked(0);
                    } else if (members.get(i).getIsChecked() == 1) {
                        resultMembers.get(j).setIsChecked(1);
                    }
                }
            }
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
        if (adapter != null && str != null)
            adapter.getFilter().filter(str);
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
