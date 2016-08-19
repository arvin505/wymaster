package com.miqtech.master.client.ui;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.ContactMember;
import com.miqtech.master.client.entity.Corps;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.BitmapUtil;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.FileManager;
import com.miqtech.master.client.utils.ImageLoadingListenerAdapter;
import com.miqtech.master.client.utils.ShareToFriendsUtil;
import com.miqtech.master.client.view.ExpertMorePopupWindow;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.constant.WBConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 战队邀请二维码页面
 * Created by wuxn on 2016/5/10.
 */
public class MatchCorpsInviteCodingActivity extends BaseActivity implements View.OnClickListener, IWeiboHandler.Response {
    @Bind(R.id.btnBack)
    ImageButton btnBack;
    @Bind(R.id.btnShare)
    ImageButton btnShare;
    @Bind(R.id.ivCode)
    ImageView ivCode;
    @Bind(R.id.llCreateCommand)
    LinearLayout llCreateCommand;
    @Bind(R.id.btnInvite)
    Button btnInvite;
    @Bind(R.id.rlInviteCoding)
    RelativeLayout rlInviteCoding;
    @Bind(R.id.tvFailureTime)
    TextView tvFailureTime;
    @Bind(R.id.tvTeamName)
    TextView tvTeamName;

    Corps corps;

    private Context context;

    private HashMap<String, String> params = new HashMap<>();

    //qq包名
    String qqPackageName = "com.tencent.mobileqq";
    //微信包名
    String wxPackageName = "com.tencent.mm";
    //娱口令弹窗
    private Dialog entertainTokenDialog;
    //    private SweetSheet mSweetSheet;
    private ShareToFriendsUtil shareToFriendsUtil;
    private ExpertMorePopupWindow popwin;

    private ArrayList mSelectMembers = new ArrayList<Object>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
        popwin.setOnItemClick(itemOnClick);
        shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
        initSinaSso(savedInstanceState);
    }

    public void initSinaSso(Bundle savedInstanceState) {
        if (savedInstanceState != null && shareToFriendsUtil != null) {
            shareToFriendsUtil.getIWeiApiInstance(this).registerApp();
            shareToFriendsUtil.getIWeiApiInstance(this).handleWeiboResponse(getIntent(), this);
            // 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
            // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
            // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
            // 失败返回 false，不调用上述回调
        }
    }


    @Override
    protected void init() {
        super.init();
        setContentView(R.layout.activity_matchcorps_invitecoding);
        ButterKnife.bind(this);
        context = this;
        corps = (Corps) getIntent().getSerializableExtra("corps");
        initData();
        initView();
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView() {
        super.initView();
        if (corps != null) {
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + corps.getQrcode(), ivCode, new ImageLoadingListenerAdapter() {
                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    super.onLoadingComplete(s, view, bitmap);
                    BitmapDrawable drawable = new BitmapDrawable(bitmap);
                    int height = drawable.getIntrinsicHeight();
                    int width = drawable.getIntrinsicWidth();
                    float scale = (float) height / width;
                    ViewGroup.LayoutParams lp = ivCode.getLayoutParams();
                    lp.width = (int) getResources().getDimension(R.dimen.invite_coding_width);
                    lp.height = (int) ((int) getResources().getDimension(R.dimen.invite_coding_width) * scale);
                    ivCode.setLayoutParams(lp);
                }
            });
            tvTeamName.setText(corps.getTeam_name());
            tvFailureTime.setText("该二维码将在" + DateUtil.dateToStrLong(corps.getOver_time()) + "报名结束时失效");
            llCreateCommand.setOnClickListener(this);
            btnInvite.setOnClickListener(this);
            btnBack.setOnClickListener(this);
            btnShare.setOnClickListener(this);
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        if (method.equals(HttpConstant.CORPS_ENTERTAIN_TOKEN)) {
            try {
                String objectStr = object.getString("object");
                JSONObject objObj = new JSONObject(objectStr);
                String entertainTokenStr = objObj.getString("entertainTokenStr");
                String validTime = objObj.getString("validTime");
                createEntertainTokenDialog(entertainTokenStr, validTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (method.equals(HttpConstant.INVITE_TEAMMATE)) {
            showToast("邀请成功");
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
    }

    private void createEntertainToken() {
        showLoading();
        params.clear();
        if (corps != null) {
            params.put("teamId", corps.getTeam_id() + "");
            params.put("userId", WangYuApplication.getUser(context).getId());
            params.put("token", WangYuApplication.getUser(context).getToken());
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.CORPS_ENTERTAIN_TOKEN, params, HttpConstant.CORPS_ENTERTAIN_TOKEN);
        }
    }

    /**
     * 创建娱口令DIALOG
     *
     * @param entertainToken 娱口令
     * @param validTime      时间
     */

    private void createEntertainTokenDialog(String entertainToken, String validTime) {
        entertainTokenDialog = new Dialog(this);
        Window window = entertainTokenDialog.getWindow();
        window.setContentView(R.layout.layout_entertain_token_dialog);
        entertainTokenDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.TOP);
        entertainTokenDialog.setCanceledOnTouchOutside(true);
        Button btnGoQQ = (Button) entertainTokenDialog.findViewById(R.id.btnGoQQ);
        Button btnGoWX = (Button) entertainTokenDialog.findViewById(R.id.btnGoWX);
        TextView tvEndTime = (TextView) entertainTokenDialog.findViewById(R.id.tvEndTime);
        TextView tvEntertainToken = (TextView) entertainTokenDialog.findViewById(R.id.tvEntertainToken);
        //设置字体颜色
        SpannableStringBuilder builder = new SpannableStringBuilder(entertainToken);
        ForegroundColorSpan orangeSpan = new ForegroundColorSpan(getResources().getColor(R.color.orange));
        String[] arr = entertainToken.split(" ");
        if (arr.length > 0) {
            String a1 = arr[0];
            int a1Length = a1.length();
            builder.setSpan(orangeSpan, a1Length, entertainToken.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvEntertainToken.setText(builder);

        } else {
            tvEntertainToken.setText(entertainToken);
        }
        tvEndTime.setText(validTime);
        btnGoQQ.setOnClickListener(this);
        btnGoWX.setOnClickListener(this);
        entertainTokenDialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = entertainTokenDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        entertainTokenDialog.getWindow().setAttributes(lp);


        setClipboardContent(entertainToken);
    }

    /**
     * 设置粘贴板内容
     *
     * @param entertainToken 娱口令
     */
    private void setClipboardContent(String entertainToken) {
        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText(entertainToken);
    }

    private void startOtherApp(String packName) {

        Intent intent = getPackageManager().getLaunchIntentForPackage(packName);
        if (intent != null) {
            startActivity(intent);
            if (entertainTokenDialog != null) {
                entertainTokenDialog.dismiss();
            }
        } else {
            if (packName.equals(qqPackageName)) {
                showToast("并没有安装QQ，请先安装QQ");
            } else if (packName.equals(wxPackageName)) {
                showToast("并没有安装微信，请先安装微信");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llCreateCommand:
                if (entertainTokenDialog == null) {
                    createEntertainToken();
                } else {
                    entertainTokenDialog.show();
                }
                break;
            case R.id.rlBack:
                onBackPressed();
                break;
            case R.id.btnInvite:
                if (corps != null) {
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra("selectMembers", mSelectMembers);
                    intent.putExtra("isYueZhan", false);
                    intent.putExtra("maxInviteMemberSize", 20);
                    intent.putExtra("teamId", corps.getTeam_id() + "");
                    intent.setClass(context, InviteFriendsActivity.class);
                    startActivityForResult(intent, 1);
                }
                break;
            case R.id.btnGoQQ:
                startOtherApp(qqPackageName);
                break;
            case R.id.btnGoWX:
                startOtherApp(wxPackageName);
                break;
            case R.id.btnShare:
                if (popwin != null) {
                    popwin.show();
                } else {
                    popwin = new ExpertMorePopupWindow(context, R.style.Dialog);
                    popwin.setOnItemClick(itemOnClick);
                    shareToFriendsUtil = new ShareToFriendsUtil(context, popwin);
                    popwin.show();
                }
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
        }
    }

    private void uploadInviteMembers(ArrayList<Object> hasCommonMembers) {
        ArrayList<String> result = getInvocationIdsAndPhones(hasCommonMembers);

        showLoading();
        User user = WangYuApplication.getUser(context);
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("teamId", corps.getTeam_id() + "");
        if (!TextUtils.isEmpty(result.get(0))) {
            params.put("invocationIds", result.get(0));
        }
        if (!TextUtils.isEmpty(result.get(1))) {
            params.put("phones", result.get(1));
        }
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INVITE_TEAMMATE, params, HttpConstant.INVITE_TEAMMATE);
    }

    private ArrayList<String> getInvocationIdsAndPhones(ArrayList<Object> hasCommonMembers) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<ContactMember> selectMembers = data.getParcelableArrayListExtra("selectMembers");
            ArrayList<Object> mSelectMembers = new ArrayList<Object>();
            mSelectMembers.addAll(selectMembers);
            if (selectMembers.size() != 0) {
                uploadInviteMembers(mSelectMembers);
            }
        }
//        if (shareToFriendsUtil != null && shareToFriendsUtil.getmSsoHandler() != null) {
//            shareToFriendsUtil.getmSsoHandler().authorizeCallBack(requestCode, resultCode, data);
//        }
        if (shareToFriendsUtil != null && shareToFriendsUtil.getmTencent(this) != null)
            shareToFriendsUtil.getmTencent(this).onActivityResult(requestCode, resultCode, data);

//        if (popwin != null) {
//            popwin.dismiss();
//        }
    }


    View.OnClickListener itemOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (TextUtils.isEmpty(corps.getQrcode()) || TextUtils.isEmpty(corps.getTeam_name()) || TextUtils.isEmpty(corps.getOver_time())) {
                showToast("网络不给力!");
                if (popwin != null && popwin.isShowing()) {
                    popwin.dismiss();
                }
                return;
            }

            Bitmap bitmap = BitmapUtil.mergeInviteShareBitmap(context,
                    AsyncImage.getBitmap(HttpConstant.SERVICE_UPLOAD_AREA + corps.getQrcode()),
                    BitmapUtil.drawableToBitamp(getResources().getDrawable(R.drawable.share_merge_logo)),
                    corps.getTeam_name(),
                    "该二维码将在" + DateUtil.dateToStrLong(corps.getOver_time()) + "报名结束时失效");

            switch (v.getId()) {
                case R.id.llSina:
                    shareToFriendsUtil.shareImageByWeibo(bitmap);
                    break;
                case R.id.llWeChat:
                    //      ivCode.setImageBitmap(bitmap);
                    shareToFriendsUtil.shareInviteImageByWXFriend(bitmap, 0);
                    break;
                case R.id.llFriend:
                    shareToFriendsUtil.shareInviteImageByWXFriend(bitmap, 1);
                    break;
                case R.id.llQQ:
                    shareToFriendsUtil.shareImageByQQ(FileManager.FILE_PATH + FileManager.USER_FOLDER + "/" + FileManager.INVITE_CODING_NAME);
                    break;
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        //       setIntent(intent);
        shareToFriendsUtil.getIWeiApiInstance(this).handleWeiboResponse(intent, this);
    }

    /**
     * 接收微客户端博请求的数据。 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     *
     * @param baseResp 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                Toast.makeText(this, R.string.weibo_share_success, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Toast.makeText(this, R.string.weibo_share_cancel, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Toast.makeText(this, getResources().getString(R.string.weibo_share_fail) + "Error Message: " + baseResp.errMsg,
                        Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shareToFriendsUtil != null) {
            if (shareToFriendsUtil.requestUtil != null) {
                shareToFriendsUtil.requestUtil.removeTag(shareToFriendsUtil.getClass().getName());
            }
            shareToFriendsUtil.requestUtil = null;
            shareToFriendsUtil = null;
        }
    }
}
