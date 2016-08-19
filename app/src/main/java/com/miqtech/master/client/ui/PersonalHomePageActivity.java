package com.miqtech.master.client.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.PhotosGridViewAdapter;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.appmanager.AppManager;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.entity.ActivityAndMatch;
import com.miqtech.master.client.entity.MyFansAndAttention;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.entity.UserGame;
import com.miqtech.master.client.entity.UserImage;
import com.miqtech.master.client.http.FileUploadUtil;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.PreferencesUtil;
import com.miqtech.master.client.view.MyGridView;
import com.miqtech.master.client.view.PullToRefreshLayout;
import com.miqtech.master.client.view.PullToRefreshLayout.OnRefreshListener;
import com.miqtech.master.client.view.PullableScrollView;
import com.miqtech.master.client.view.PullableScrollView.MyScrollListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalHomePageActivity extends BaseActivity implements OnClickListener, MyScrollListener,
        OnRefreshListener, OnItemClickListener {
    private TextView tvEdit, tvUserAddress, tvUserName, tvUserSignature, tvFans, tvFansNum, tvAttentionNum,
            tvAttention, tvAttentionView, tvGame, tvActivity, tvPhoto, tvGame1, tvActivity1, tvPhoto1;
    private ImageView ivUserHeader, ivPersonalPage, ivHandle, ivBack;
    private PullableScrollView pullScrollView;
    private ImageView ivAddGame;
    private PullToRefreshLayout refresh_view;

    private MyFansAndAttention myFansAndAttention;
    private String userId;

    private MyGridView gvPhoto;

    private int pageSize = 10;

    private int currentType = GAME;
    private int isLast;

    private static final int GAME = 1;
    private static final int ACTIVITY = 2;
    private static final int PHOTO = 3;

    private int gameIsLast;
    private int activityIsLast;
    private int photoIsLast;

    private int gamePage = 1;
    private int activityPage = 1;
    private int photoPage = 1;

    private Dialog bgDialog;
    private Dialog photoDialog;
    private Dialog reportDialog;
    private Dialog attDialog;
    private PopupWindow pwHandle;

    // private List<UserGame> userGames;
    //
    // private List<ActivityAndMatch> activityAndMatches;

    private LinearLayout llUserGame, llActivity, llPhoto, llMenu2, llMenu1, llHeader, llContent;

    private RelativeLayout rlHeader, includeHeader;

    private int top;

    private boolean hasLoadActivity = false;
    private boolean hasLoadPhoto = false;

    private Uri imageUri; // 图片路径
    private String filename; // 图片名称
    private File outputImage;
    private String imgName = "";

    private static final int REQUEST_USER_DATA = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_CROP_PHOTO = 3;
    private static final int REQUEST_PHOTO_DATE = 4;
    private static final int REQUEST_PERSONALHOME = 5;
    private static final int REQUEST_HANDLE_PHOTO = 6;

    // 当前上传类型
    private int CURRENT_PHOTO_TYPE = 0;

    private static final int UPLOAD_SUCCESS_BG = 1;
    private static final int UPLOAD_FAILED_BG = 2;
    private static final int UPLOAD_SUCCESS_PHOTO = 3;
    private static final int UPLOAD_FAILED_PHOTO = 4;

    private PhotosGridViewAdapter adapter;

    public static final int OTHER_PHOTO = 1;
    public static final int SELF_PHOTO = 2;

    private ArrayList<UserImage> photos = new ArrayList<UserImage>();

    private boolean isMyShelf;

    private Context context;

    private int isDefriend;

    private int isFan;

    private User otherUser;

    private int uploadType = -1;

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.activity_personalhomepage);
        context = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();
        tvEdit = (TextView) findViewById(R.id.tvEdit);
        tvUserAddress = (TextView) findViewById(R.id.tvUserAddress);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserSignature = (TextView) findViewById(R.id.tvUserSignature);
        tvFansNum = (TextView) findViewById(R.id.tvFansNum);
        tvFans = (TextView) findViewById(R.id.tvFans);
        tvAttentionView = (TextView) findViewById(R.id.tvAttentionView);
        tvAttentionNum = (TextView) findViewById(R.id.tvAttentionNum);
        tvAttention = (TextView) findViewById(R.id.tvAtt);
        tvGame = (TextView) findViewById(R.id.tvGame);
        tvActivity = (TextView) findViewById(R.id.tvActivity);
        tvPhoto = (TextView) findViewById(R.id.tvPhoto);
        llUserGame = (LinearLayout) findViewById(R.id.llUserGame);
        llActivity = (LinearLayout) findViewById(R.id.llActivity);
        llPhoto = (LinearLayout) findViewById(R.id.llPhoto);
        pullScrollView = (PullableScrollView) findViewById(R.id.pullScrollView);
        ivUserHeader = (ImageView) findViewById(R.id.ivUserHeader);
        llMenu2 = (LinearLayout) findViewById(R.id.llUserGameMenu2);
        llMenu1 = (LinearLayout) findViewById(R.id.llMenu1);
        llHeader = (LinearLayout) findViewById(R.id.llHeader);
        tvGame1 = (TextView) findViewById(R.id.tvGame1);
        tvActivity1 = (TextView) findViewById(R.id.tvActivity1);
        tvPhoto1 = (TextView) findViewById(R.id.tvPhoto1);
        rlHeader = (RelativeLayout) findViewById(R.id.rlHeader);
        includeHeader = (RelativeLayout) findViewById(R.id.header);
        llContent = (LinearLayout) findViewById(R.id.llContent);
        ivAddGame = (ImageView) findViewById(R.id.ivAddGame);
        refresh_view = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        ivPersonalPage = (ImageView) findViewById(R.id.ivPersonalPage);
        ivHandle = (ImageView) findViewById(R.id.ivHandle);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        gvPhoto = (MyGridView) findViewById(R.id.gvPhoto);

        pullScrollView.setOnMyScrollListener(this);
        refresh_view.setOnRefreshListener(this);

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        llMenu1.measure(w, h);
        int menuHeight = llMenu1.getMeasuredHeight();

        int w1 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h1 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        llHeader.measure(w1, h1);
        int headerHeight = llHeader.getMeasuredHeight();

        top = headerHeight - menuHeight - 50;
        setLeftIncludeTitle("详细资料");
        ivBack.setImageResource(R.drawable.back);
        ivBack.setOnClickListener(this);
        tvUserSignature.getBackground().setAlpha(125);
        tvGame.setOnClickListener(this);
        tvActivity.setOnClickListener(this);
        tvPhoto.setOnClickListener(this);

        tvGame1.setOnClickListener(this);
        tvActivity1.setOnClickListener(this);
        tvPhoto1.setOnClickListener(this);
        tvAttentionView.setOnClickListener(this);
        tvFans.setOnClickListener(this);

        ivAddGame.setOnClickListener(this);
        tvAttention.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
        tvFansNum.setOnClickListener(this);
        tvAttentionNum.setOnClickListener(this);


    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        userId = getIntent().getStringExtra("id");
        if (TextUtils.isEmpty(userId)) {
            showToast("该用户已设置隐私保护");
            finish();
            return;
        }
        User user = WangYuApplication.getUser(this);
        if (user != null && user.getId().equals(userId)) {
            isMyShelf = true;
        } else {
            isMyShelf = false;
        }
        if (isMyShelf) {
            updatePersonalView(user);
            loadStatistics(user);
            Drawable edit = getResources().getDrawable(R.drawable.edit_icon);
            edit.setBounds(0, 0, edit.getMinimumWidth(), edit.getMinimumHeight());
            tvEdit.setCompoundDrawables(edit, null, null, null);
            AsyncImage.loadNetPhoto(this, HttpConstant.SERVICE_UPLOAD_AREA + user.getBgImg(), ivPersonalPage);
            ivPersonalPage.setOnClickListener(this);
            tvEdit.setText("编辑");
        } else {
            loadOtherPeopleData(userId);
            ivHandle.setVisibility(View.VISIBLE);
            ivHandle.setImageResource(R.drawable.menu_icon);
            ivHandle.setOnClickListener(this);
            Drawable dabaojian = getResources().getDrawable(R.drawable.dabaojian_icon);
            dabaojian.setBounds(0, 0, dabaojian.getMinimumWidth(), dabaojian.getMinimumHeight());
            tvEdit.setCompoundDrawables(dabaojian, null, null, null);
            ivAddGame.setVisibility(View.GONE);
            tvEdit.setText("向TA发起约战");
        }
        if (currentType == GAME) {
            if (isMyShelf) {
                ivAddGame.setVisibility(View.VISIBLE);
                loadUserGame();
            } else {
                loadOtherUserGame();
            }
        } else if (currentType == ACTIVITY) {
            if (isMyShelf) {
                loadUserActivity();
            } else {
                loadOtherUserActivity();
            }
        } else if (currentType == PHOTO) {
            if (isMyShelf) {
                loadUserGame();
            } else {
                loadOtherUserGame();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updatePersonalView(User user) {
        AsyncImage.loadAvatar(this, HttpConstant.SERVICE_UPLOAD_AREA + user.getIcon(), ivUserHeader);

        if (TextUtils.isEmpty(user.getNickname())) {
            tvUserName.setText("");
        } else {
            tvUserName.setText(user.getNickname());
        }

        if (TextUtils.isEmpty(user.getCityName())) {
            tvUserAddress.setText("");
        } else {
            tvUserAddress.setText(user.getCityName());
        }

        if (!TextUtils.isEmpty(user.getSpeech())) {
            tvUserSignature.setText(user.getSpeech());
        } else {
            tvUserSignature.setText("还没有添加个人介绍");
        }
        if (isMyShelf) {
            tvAttention.setVisibility(View.GONE);
        } else {
            tvAttention.setVisibility(View.VISIBLE);
        }

    }

    private void updateFansAndAttentionView() {
        if (myFansAndAttention != null) {
            tvFansNum.setText(myFansAndAttention.getFansTotal() + "");
            tvAttentionNum.setText(myFansAndAttention.getConcernTotal() + "");
        }
    }

    private void loadStatistics(User user) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_STATISTICS, params, HttpConstant.MY_STATISTICS);
    }

    private void loadOtherStatistics(User user) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("checkedUserId", user.getId());
        //params.put("token", user.getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.OTHER_STATISTICS, params, HttpConstant.OTHER_STATISTICS);
    }

    private void loadOtherPeopleData(String userId) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(this);
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
        }
        params.put("checkedUserId", userId);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.OTHER_PEOPLE, params, HttpConstant.OTHER_PEOPLE);

    }

    private void loadUserGame() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(this);
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
            params.put("pageSize", pageSize + "");
            params.put("page", gamePage + "");
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.USER_GAME, params, HttpConstant.USER_GAME);
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void loadOtherUserGame() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("checkedUserId", userId);
        params.put("pageSize", pageSize + "");
        params.put("page", gamePage + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.OTHER_GAME, params, HttpConstant.OTHER_GAME);
    }

    private void addGameViews(List<UserGame> userGames) {
        if (userGames != null) {
            for (UserGame userGame : userGames) {
                addGameView(userGame);
            }
        }
    }

    private void addGameView(UserGame userGame) {
        View gameView = View.inflate(this, R.layout.layout_usergame_item, null);
        ImageView ivUserGame = (ImageView) gameView.findViewById(R.id.ivUserGame);
        TextView tvUserGameData = (TextView) gameView.findViewById(R.id.tvUserGameData);
        TextView tvUserGameServer = (TextView) gameView.findViewById(R.id.tvUserGameServer);
        TextView tvUserName = (TextView) gameView.findViewById(R.id.tvUserName);
        AsyncImage.loadPhoto(this, HttpConstant.SERVICE_UPLOAD_AREA + userGame.getGame_pic(), ivUserGame);
        tvUserGameData.setText(userGame.getGame_level());
        tvUserGameServer.setText(userGame.getGame_server());
        tvUserName.setText(userGame.getGame_nickname());
        llUserGame.addView(gameView);
        gameView.setTag(userGame);
        if (isMyShelf) {
            gameView.setOnClickListener(this);
        }
    }

    private void loadUserActivity() {
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(this);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("pageSize", pageSize + "");
        params.put("page", gamePage + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_ACTIVITY, params, HttpConstant.MY_ACTIVITY);
    }

    private void loadOtherUserActivity() {
        Map<String, String> params = new HashMap<>();
        params.put("checkedUserId", userId);
        params.put("pageSize", pageSize + "");
        params.put("page", activityPage + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.OTHER_PEOPLE_ACTIVITY, params, HttpConstant.OTHER_PEOPLE_ACTIVITY);
    }

    private void addActivityViews(List<ActivityAndMatch> activityAndMatches) {
        if (activityAndMatches != null) {
            for (ActivityAndMatch activityAndMatch : activityAndMatches) {
                addActivityView(activityAndMatch);
            }
        }

    }

    private void addActivityView(ActivityAndMatch activityAndMatch) {
        View activityView = View.inflate(this, R.layout.layout_activityandmatch_item, null);
        TextView tvActivityTime = (TextView) activityView.findViewById(R.id.tvActivityTime);
        TextView tvActivityType = (TextView) activityView.findViewById(R.id.tvActivityType);
        TextView tvActivityAddress = (TextView) activityView.findViewById(R.id.tvActivityAddress);
        TextView tvActivityTitle = (TextView) activityView.findViewById(R.id.tvActivityTitle);
        TextView tvActivityDescribe = (TextView) activityView.findViewById(R.id.tvActivityDescribe);
        TextView tvActivityEnd = (TextView) activityView.findViewById(R.id.tvActivityEnd);
        ImageView ivActivityImg = (ImageView) activityView.findViewById(R.id.ivActivityImg);
        TextView tvLine = (TextView) activityView.findViewById(R.id.tvLine);
        TextView tvLine2 = (TextView) activityView.findViewById(R.id.tvLine2);
        activityView.setTag(activityAndMatch);
        tvActivityTime.setText(DateUtil.dateToStr(activityAndMatch.getBegin_time()));
        if (activityAndMatch.getType() == 1) {
            if (activityAndMatch.getIs_releaser() == 0) {
                tvActivityType.setText("参加约战");
            } else if (activityAndMatch.getIs_releaser() == 1) {
                tvActivityType.setText("发起约战");
            }
        } else if (activityAndMatch.getType() == 2) {
            tvActivityType.setText("参加的赛事");
            tvActivityType.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_blue_gray_bg));
            tvActivityType.setTextColor(getResources().getColor(R.color.white));
        }
        if (activityAndMatch.getWay() == 1) {
            tvActivityAddress.setText("线上");
        } else if (activityAndMatch.getWay() == 2) {
            tvActivityAddress.setText("线下");
            if (activityAndMatch.getType() == 1) {
                tvActivityAddress.setText(activityAndMatch.getAddress());
            }
        }
        if (activityAndMatch.getStatus() == 1 || activityAndMatch.getStatus() == 4) {
            tvActivityEnd.setVisibility(View.VISIBLE);
            tvLine2.setVisibility(View.GONE);
        } else {
            tvActivityEnd.setVisibility(View.GONE);
            tvLine2.setVisibility(View.VISIBLE);
        }
        tvActivityTitle.setText(activityAndMatch.getTitle());
        tvActivityDescribe.setText(activityAndMatch.getContent());
        AsyncImage.loadPersonalHomePhoto(this, HttpConstant.SERVICE_UPLOAD_AREA + activityAndMatch.getBgimg(), ivActivityImg);
        llActivity.addView(activityView);
        activityView.setOnClickListener(this);
    }

    private void loadUserPhotos() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(this);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("pageSize", pageSize + "");
        params.put("page", photoPage + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_LISTALBUM, params, HttpConstant.MY_LISTALBUM);
    }

    private void loadOtherUserPhotos() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        params.put("checkedUserId", userId);
        params.put("pageSize", pageSize + "");
        params.put("page", photoPage + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.OTHER_PEOPLE_ALBUM, params, HttpConstant.OTHER_PEOPLE_ALBUM);
    }

    private void uploadUserPhoto() {
        User user = WangYuApplication.getUser(this);
        showLoading();
        try {
            final Map<String, String> params = new HashMap<>();
            params.put("userId", user.getId());
            params.put("token", user.getToken());
            final Map<String, String> fileParams = new HashMap<>();
            fileParams.put("pic", outputImage.getAbsolutePath());
            uploadType = 1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileUploadUtil.uploadImageFile(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.IMPORT_ALBUM, params, fileParams, mHandler);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadUserBg() {
        User user = WangYuApplication.getUser(this);
        showLoading();
        try {
            final Map<String, String> params = new HashMap<>();
            params.put("userId", user.getId());
            params.put("token", user.getToken());
            params.put("nickname", user.getNickname() + "");
            /*params.put("bgImg", file2String(outputImage, "utf-8"));
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.UPLOAD_PERSONAL_BG, params, HttpConstant.UPLOAD_PERSONAL_BG);
            LogUtil.e("da1", "params = " + params.toString());
            LogUtil.e("da", HttpConstant.SERVICE_HTTP_AREA + HttpConstant.UPLOAD_PERSONAL_BG);*/
            final Map<String, String> fileMap = new HashMap<>();
            fileMap.put("bgImg", outputImage.getAbsolutePath());
            uploadType = 2;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileUploadUtil.uploadImageFile(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.UPLOAD_PERSONAL_BG, params, fileMap, mHandler);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void inform() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(this);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("byUserId", userId);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MY_INFORM, params, HttpConstant.MY_INFORM);
    }

    private void blackList(int type) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(this);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("blackUserId", userId);
        params.put("type", type + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.BLACK_OR_NOT, params, HttpConstant.BLACK_OR_NOT);
    }

    private void attentionUser(int type) {
        showLoading();
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(this);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("concernId", userId);
        params.put("type", type + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ATTENTION_USER, params, HttpConstant.ATTENTION_USER);
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideLoading();
            switch (msg.what) {
                case UPLOAD_SUCCESS_BG:
                    String objStr = (String) msg.obj;
                    try {
                        User user = WangYuApplication.getUser(PersonalHomePageActivity.this);
                        JSONObject jsonObj = new JSONObject(objStr);
                        String jsonStr = jsonObj.getString("object");
                        User newUser = new Gson().fromJson(jsonStr, User.class);
                        newUser.setToken(user.getToken());
                        PreferencesUtil.setUser(PersonalHomePageActivity.this, new Gson().toJson(newUser));
                        WangYuApplication.setUser(newUser);
                        showToast("上传背景成功");
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                            ivPersonalPage.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case UPLOAD_FAILED_BG:
                    showToast((String) msg.obj);
                    break;
                case UPLOAD_SUCCESS_PHOTO:
                    photoPage = 1;
                    loadUserPhotos();
                    break;
                case UPLOAD_FAILED_PHOTO:
                    break;
            }
        }
    };


    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        LogUtil.e("da", "data == " + object.toString());
        hideLoading();
        String obj = "";
        if (object.has("object")) {
            try {
                obj = object.getJSONObject("object").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            obj = object.toString();
        }
        if (HttpConstant.MY_STATISTICS.equals(method) || HttpConstant.OTHER_STATISTICS.equals(method)) {
            myFansAndAttention = new Gson().fromJson(obj.toString(), MyFansAndAttention.class);
            updateFansAndAttentionView();
        } else if (HttpConstant.OTHER_PEOPLE.equals(method)) {
            try {
                JSONObject jsonObj = new JSONObject(obj.toString());
                String userInfoStr = jsonObj.getString("userInfo");
                isFan = jsonObj.getInt("isFan");
                isDefriend = jsonObj.getInt("isDefriend");

                otherUser = new Gson().fromJson(userInfoStr, User.class);
                AsyncImage.loadPersonalHomePhoto(this, HttpConstant.SERVICE_UPLOAD_AREA + otherUser.getBgImg(),
                        ivPersonalPage);
                if (isFan == 0) {
                    tvAttention.setText("关注");
                } else if (isFan == 1) {
                    tvAttention.setText("已关注");
                }
                updatePersonalView(otherUser);
                loadOtherStatistics(otherUser);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (HttpConstant.USER_GAME.equals(method) || HttpConstant.OTHER_GAME.equals(method)) {

            try {
                JSONObject jsonObj = new JSONObject(obj.toString());
                gameIsLast = jsonObj.getInt("isLast");
                String strUserGame = jsonObj.getString("list");
                List<UserGame> newUserGames = new Gson().fromJson(strUserGame, new TypeToken<List<UserGame>>() {
                }.getType());
                if (gamePage == 1) {
                    llUserGame.removeAllViews();
                    refresh_view.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    refresh_view.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                addGameViews(newUserGames);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (HttpConstant.OTHER_PEOPLE_ACTIVITY.equals(method) || HttpConstant.MY_ACTIVITY.equals(method)) {
            try {
                JSONObject jsonObj = new JSONObject(obj.toString());
                activityIsLast = jsonObj.getInt("isLast");
                String strUserActivity = jsonObj.getString("list");
                List<ActivityAndMatch> newActivityAndMatches = new Gson().fromJson(strUserActivity,
                        new TypeToken<List<ActivityAndMatch>>() {
                        }.getType());
                if (activityPage == 1) {
                    llActivity.removeAllViews();
                    refresh_view.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    refresh_view.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                addActivityViews(newActivityAndMatches);
                hasLoadActivity = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (HttpConstant.MY_LISTALBUM.equals(method) || HttpConstant.OTHER_PEOPLE_ALBUM.equals(method)) {
            try {
                JSONObject jsonObj = new JSONObject(obj.toString());
                photoIsLast = jsonObj.getInt("isLast");
                String strUserPhoto = jsonObj.getString("list");
                List<UserImage> newUserImages = new Gson().fromJson(strUserPhoto, new TypeToken<List<UserImage>>() {
                }.getType());
                if (photoPage == 1) {
                    photos.clear();
                    if (isMyShelf) {
                        photos.add(0, new UserImage());
                    }
                    photos.addAll(newUserImages);
                    refresh_view.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    photos.addAll(newUserImages);
                    refresh_view.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
                adapter.notifyDataSetChanged();
                hasLoadPhoto = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (HttpConstant.MY_INFORM.equals(method)) {
            showToast("举报成功");
            pwHandle.dismiss();
        } else if (HttpConstant.BLACK_OR_NOT.equals(method)) {
            if (isDefriend == 1) {
                isDefriend = 0;
                showToast("解除拉黑成功");
            } else if (isDefriend == 0) {
                isDefriend = 1;
                showToast("拉黑成功");
            }
            pwHandle.dismiss();
            reportDialog.dismiss();
            isFan = 0;
            tvAttention.setText("关注");
        } else if (HttpConstant.ATTENTION_USER.equals(method)) {
            if (isFan == 0) {
                isFan = 1;
                showToast("关注成功");
                tvAttention.setText("已关注");
            } else if (isFan == 1) {
                isFan = 0;
                showToast("取消关注成功");
                tvAttention.setText("关注");
            }
            BroadcastController.sendUserChangeBroadcase(context);
        }
        // else if (HttpPortName.OTHER_PEOPLE_ALBUM.equals(method)) {
        // hasLoadPhoto = true;
        // }
    }

    @Override
    public void onError(String method, String errorInfo) {
        super.onError(method, errorInfo);
        hideLoading();
        showToast(errorInfo);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        if (currentType == GAME) {
            if (gamePage == 1) {
                refresh_view.refreshFinish(PullToRefreshLayout.DONE);
            } else {
                refresh_view.loadmoreFinish(PullToRefreshLayout.DONE);
            }
        } else if (currentType == ACTIVITY) {
            if (activityPage == 1) {
                refresh_view.refreshFinish(PullToRefreshLayout.DONE);
            } else {
                refresh_view.loadmoreFinish(PullToRefreshLayout.DONE);
            }
        } else if (currentType == PHOTO) {
            if (photoPage == 1) {
                refresh_view.refreshFinish(PullToRefreshLayout.DONE);
            } else {
                refresh_view.loadmoreFinish(PullToRefreshLayout.DONE);
            }
        }

        if (method.equals(HttpConstant.MY_INFORM)) {
            pwHandle.dismiss();
        }
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {//**
        changeViewAndLoadData(v.getId());
        switch (v.getId()) {
            case R.id.ivAddGame:
                // ivAddGame.setAnimation(AnimationUtils.loadAnimation(this,
                // R.anim.imageview_bottom));
                Intent intent = new Intent();
                intent.setClass(this, UserGameDataActivity.class);
                startActivityForResult(intent, REQUEST_USER_DATA);
                break;
            case R.id.llUserGameItem:
                UserGame userGame = (UserGame) v.getTag();
                intent = new Intent();
                intent.setClass(this, UserGameDataActivity.class);
                intent.putExtra("UserGame", userGame);
                startActivityForResult(intent, REQUEST_USER_DATA);
                break;
            case R.id.ivPersonalPage:
                if (bgDialog == null) {
                    bgDialog = new AlertDialog.Builder(this).create();
                    bgDialog.show();
                    Window window = bgDialog.getWindow();
                    window.setContentView(R.layout.layout_uploadpic_dialog);

                    bgDialog.setCanceledOnTouchOutside(true);
                    TextView tvDialogTitle = (TextView) bgDialog.findViewById(R.id.tvDialogTitle);
                    TextView tvDialogCarema = (TextView) bgDialog.findViewById(R.id.tvDialogCarema);
                    TextView tvDialogPhoto = (TextView) bgDialog.findViewById(R.id.tvDialogPhoto);
                    tvDialogCarema.setOnClickListener(this);
                    tvDialogPhoto.setOnClickListener(this);
                    tvDialogTitle.setText("上传背景图片");
                } else {
                    bgDialog.show();
                }
                CURRENT_PHOTO_TYPE = 1;
                break;
            case R.id.tvDialogCarema:
                takePhoto();
                break;
            case R.id.tvDialogPhoto:
                takeAlbum();
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.ivHandle:
                int screenWidth = WangYuApplication.WIDTH;
                View contentView = LayoutInflater.from(this).inflate(R.layout.layout_report_item, null);
                pwHandle = new PopupWindow(contentView, screenWidth / 2, LayoutParams.WRAP_CONTENT);
                pwHandle.setBackgroundDrawable(new BitmapDrawable());
                pwHandle.setOutsideTouchable(false);
                // 设置此参数获得焦点，否则无法点击
                pwHandle.setFocusable(true);
                contentView.setFocusable(true);
                contentView.setFocusableInTouchMode(true);
                contentView.findViewById(R.id.rlReport).setOnClickListener(this);
                contentView.findViewById(R.id.rlBlackList).setOnClickListener(this);
                contentView.findViewById(R.id.rlBackHome).setOnClickListener(this);
                TextView tvBlackList = (TextView) contentView.findViewById(R.id.tvBlackList);
                if (isDefriend == 0) {
                    tvBlackList.setText("拉黑");
                } else if (isDefriend == 1) {
                    tvBlackList.setText("解除拉黑");
                }
                pwHandle.showAsDropDown(includeHeader, WangYuApplication.WIDTH / 2, 0);

                break;
            case R.id.rlReport:
                User user = WangYuApplication.getUser(this);
                if (user != null) {
                    // inform();
                    intent = new Intent(context, ReportActivity.class);
                    intent.putExtra("type", 1);// 举报类别:1.用户2.评论3.约战4网吧评论
                    intent.putExtra("targetId", userId);// type=1被举报用户的id,type=2评论id,type=3约战id,type=4网吧评价id
                    startActivity(intent);
                } else {
                    showToast("请先登录");
                    intent = new Intent();
                    intent.setClass(this, LoginActivity.class);
                    //intent.putExtra(LoginActivity.LOGIN_TYPE, LoginActivity.LOGIN_FOR_PERSONALPAGE);
                    startActivityForResult(intent, REQUEST_PERSONALHOME);
                }

                break;
            case R.id.rlBackHome:
                intent = new Intent();
                intent.setClass(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.rlBlackList:
                reportDialog = new AlertDialog.Builder(this).create();
                reportDialog.show();
                Window window = reportDialog.getWindow();
                window.setContentView(R.layout.layout_reserve_dialog);

                reportDialog.setCanceledOnTouchOutside(true);
                TextView tvDialogContent = (TextView) reportDialog.findViewById(R.id.tvDialogContent);
                TextView tvDialogSure = (TextView) reportDialog.findViewById(R.id.tvDialogSure);
                TextView tvDialogCancel = (TextView) reportDialog.findViewById(R.id.tvDialogCancel);
                if (isDefriend == 0) {
                    tvDialogContent.setText("你们将自动解除关注关系，他/她不能再关注你或向你发起约战邀请");
                    tvDialogSure.setText("加入黑名单");
                } else if (isDefriend == 1) {
                    tvDialogContent.setText("确定解除黑名单?");
                    tvDialogSure.setText("解除黑名单");
                }
                tvDialogSure.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        User user = WangYuApplication.getUser(context);
                        if (user != null) {
                            if (isDefriend == 0) {
                                blackList(1);
                            } else if (isDefriend == 1) {
                                blackList(0);
                            }
                        } else {
                            showToast("请先登录");
                            Intent intent = new Intent();
                            intent.setClass(PersonalHomePageActivity.this, LoginActivity.class);
                            //intent.putExtra(LoginActivity.LOGIN_TYPE, LoginActivity.LOGIN_FOR_PERSONALPAGE);
                            startActivityForResult(intent, REQUEST_PERSONALHOME);
                        }
                    }
                });
                tvDialogCancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        reportDialog.dismiss();
                        pwHandle.dismiss();
                    }
                });
                reportDialog.show();
                break;
            case R.id.tvAtt:
                user = WangYuApplication.getUser(this);
                if (user == null) {
                    showToast("请先登录");
                    intent = new Intent();
                    intent.setClass(PersonalHomePageActivity.this, LoginActivity.class);
                    //intent.putExtra(LoginActivity.LOGIN_TYPE, LoginActivity.LOGIN_FOR_PERSONALPAGE);
                    startActivityForResult(intent, REQUEST_PERSONALHOME);
                } else {
                    if (isDefriend == 1) {
                        attDialog = new AlertDialog.Builder(this).create();
                        attDialog.show();
                        Window attWindow = attDialog.getWindow();
                        attWindow.setContentView(R.layout.layout_reserve_dialog);

                        attDialog.setCanceledOnTouchOutside(true);
                        TextView tvDialogContent1 = (TextView) attDialog.findViewById(R.id.tvDialogContent);
                        TextView tvDialogSure1 = (TextView) attDialog.findViewById(R.id.tvDialogSure);
                        TextView tvDialogCancel1 = (TextView) attDialog.findViewById(R.id.tvDialogCancel);
                        tvDialogCancel1.setVisibility(View.GONE);
                        tvDialogContent1.setText("TA已经被你拉黑了，去右上角解除黑名单吧");
                        tvDialogSure1.setText("确定");
                        tvDialogSure1.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                attDialog.dismiss();
                            }
                        });
                    } else if (isDefriend == 0) {
                        if (isFan == 0) {
                            attentionUser(1);
                        } else if (isFan == 1) {
                            attentionUser(0);
                        }
                    }

                    // attentionUser(type);
                }
                break;
            case R.id.tvEdit:
                user = WangYuApplication.getUser(this);
                if (user == null) {
                    showToast("请先登录");
                    intent = new Intent();
                    intent.setClass(this, LoginActivity.class);
                    //intent.putExtra(LoginActivity.LOGIN_TYPE, LoginActivity.LOGIN_FOR_PERSONALPAGE);
                    startActivityForResult(intent, REQUEST_PERSONALHOME);
                } else {
                    if (tvEdit.getText().toString().equals("向TA发起约战")) {
                        if (otherUser != null && otherUser.getAcceptMatch() == 0) {
                            showToast("TA不接受约战邀请噢");
                        } else {
                            if (isDefriend == 1) {
                                showToast("你已拉黑TA，不能向TA发起约战");
                            } else if (isDefriend == 0) {
                                intent = new Intent();
                                intent.putExtra("user", otherUser);
                                intent.setClass(this, ReleaseWar2TaActivity.class);
                                startActivity(intent);
                                AppManager.getAppManager().addActivity(this);
                            }
                        }

                    } else if (tvEdit.getText().toString().equals("编辑")) {
                        intent = new Intent();
                        intent.setClass(this, EditDataActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.tvFansNum:
                if (userId != null) {
                    intent = new Intent();
                    intent.setClass(this, FansListActivity.class);
                    intent.putExtra("id", userId);
                    startActivity(intent);
                }

                break;
            case R.id.tvFans:
                if (userId != null) {
                    intent = new Intent();
                    intent.setClass(this, FansListActivity.class);
                    intent.putExtra("id", userId);
                    startActivity(intent);
                }
                break;
            case R.id.tvAttentionNum:
                if (userId != null) {
                    intent = new Intent();
                    intent.setClass(this, AttentionListActivity.class);
                    intent.putExtra("id", userId);
                    startActivity(intent);
                }
                break;
            case R.id.tvAttentionView:
                if (userId != null) {
                    intent = new Intent();
                    intent.setClass(this, AttentionListActivity.class);
                    intent.putExtra("id", userId);
                    startActivity(intent);
                }
                break;
            case R.id.llActivityMatch:
                ActivityAndMatch activityAndMatch = (ActivityAndMatch) v.getTag();
                int type = activityAndMatch.getType();
                intent = new Intent();
                if (type == 1) {
                    intent.setClass(this, YueZhanDetailsActivity.class);
                    intent.putExtra("id", activityAndMatch.getId() + "");
                } else if (type == 2) {
                    intent.setClass(this, OfficalEventActivity.class);
                    intent.putExtra("matchId", activityAndMatch.getId() + "");
                }
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void changeViewAndLoadData(int id) {
        if (id == R.id.tvGame1 || id == R.id.tvGame) {
            llUserGame.setVisibility(View.VISIBLE);
            llActivity.setVisibility(View.GONE);
            llPhoto.setVisibility(View.GONE);
            tvGame.setTextColor(getResources().getColor(R.color.white));
            tvActivity.setTextColor(getResources().getColor(R.color.font_gray));
            tvPhoto.setTextColor(getResources().getColor(R.color.font_gray));
            tvGame1.setTextColor(getResources().getColor(R.color.white));
            tvActivity1.setTextColor(getResources().getColor(R.color.font_gray));
            tvPhoto1.setTextColor(getResources().getColor(R.color.font_gray));
            currentType = GAME;
        } else if (id == R.id.tvActivity || id == R.id.tvActivity1) {
            llUserGame.setVisibility(View.GONE);
            llActivity.setVisibility(View.VISIBLE);
            llPhoto.setVisibility(View.GONE);
            tvGame.setTextColor(getResources().getColor(R.color.font_gray));
            tvActivity.setTextColor(getResources().getColor(R.color.white));
            tvPhoto.setTextColor(getResources().getColor(R.color.font_gray));

            tvGame1.setTextColor(getResources().getColor(R.color.font_gray));
            tvActivity1.setTextColor(getResources().getColor(R.color.white));
            tvPhoto1.setTextColor(getResources().getColor(R.color.font_gray));
            if (!hasLoadActivity) {
                if (isMyShelf) {
                    loadUserActivity();
                } else {
                    loadOtherUserActivity();
                }
            }
            currentType = ACTIVITY;
        } else if (id == R.id.tvPhoto || id == R.id.tvPhoto1) {
            llUserGame.setVisibility(View.GONE);
            llActivity.setVisibility(View.GONE);
            llPhoto.setVisibility(View.VISIBLE);
            tvGame.setTextColor(getResources().getColor(R.color.font_gray));
            tvActivity.setTextColor(getResources().getColor(R.color.font_gray));
            tvPhoto.setTextColor(getResources().getColor(R.color.white));

            tvGame1.setTextColor(getResources().getColor(R.color.font_gray));
            tvActivity1.setTextColor(getResources().getColor(R.color.font_gray));
            tvPhoto1.setTextColor(getResources().getColor(R.color.white));
            if (!hasLoadPhoto) {
                if (isMyShelf) {
                    photos.add(new UserImage());
                    adapter = new PhotosGridViewAdapter(this, photos, SELF_PHOTO);
                    loadUserPhotos();
                } else {
                    adapter = new PhotosGridViewAdapter(this, photos, OTHER_PHOTO);
                    loadOtherUserPhotos();
                }
                gvPhoto.setOnItemClickListener(this);
                gvPhoto.setAdapter(adapter);
            }
            currentType = PHOTO;
        }
        if (isMyShelf) {
            if (llUserGame.getVisibility() == View.VISIBLE) {
                ivAddGame.setVisibility(View.VISIBLE);
            } else {
                ivAddGame.setVisibility(View.GONE);
            }
        }
    }

    private void takeAlbum() {
        // TODO Auto-generated method stub
        // takePhoto();
        // 图片名称 时间命名
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        filename = format.format(date);
        // 创建File对象用于存储拍照的图片 SD卡根目录
        // File outputImage = new
        // File(Environment.getExternalStorageDirectory(),"test.jpg");
        // 存储至DCIM文件夹
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        outputImage = new File(path, filename + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将File对象转换为Uri并启动照相程序
        imageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent("android.intent.action.PICK");
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        intent.putExtra("output", imageUri);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 340);// 输出图片大小
        intent.putExtra("outputY", 340);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    private void takePhoto() {
        // 图片名称 时间命名
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        filename = format.format(date);
        // 创建File对象用于存储拍照的图片 SD卡根目录
        // File outputImage = new
        // File(Environment.getExternalStorageDirectory(),"test.jpg");
        // 存储至DCIM文件夹
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        outputImage = new File(path, filename + ".jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将File对象转换为Uri并启动照相程序
        imageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE"); // 照相
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 指定图片输出地址
        startActivityForResult(intent, REQUEST_TAKE_PHOTO); // 启动照相
        // 拍完照startActivityForResult() 结果返回onActivityResult()函数
    }

    public void move(int x, int y, int oldx, int oldy) {
        if (y > top) {
            llMenu2.setVisibility(View.VISIBLE);
            llMenu1.setVisibility(View.INVISIBLE);
        } else {
            llMenu2.setVisibility(View.GONE);
            llMenu1.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        if (currentType == GAME) {
            gamePage = 1;
        } else if (currentType == ACTIVITY) {
            activityPage = 1;
        } else if (currentType == PHOTO) {
            photoPage = 1;
        }
        initData();
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        if (currentType == GAME) {
            if (gameIsLast == 1) {
                refresh_view.loadmoreFinish(PullToRefreshLayout.NOMORE);
            } else {
                gamePage++;
                loadUserGame();

            }
        } else if (currentType == ACTIVITY) {
            if (activityIsLast == 1) {
                refresh_view.loadmoreFinish(PullToRefreshLayout.NOMORE);
            } else {
                activityPage++;
                loadUserActivity();
            }
        } else if (currentType == PHOTO) {
            if (photoIsLast == 1) {
                refresh_view.loadmoreFinish(PullToRefreshLayout.NOMORE);
            } else {
                photoPage++;
                if (isMyShelf) {
                    loadUserPhotos();
                } else {
                    loadOtherUserPhotos();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_USER_DATA && resultCode == RESULT_OK) {
            gamePage = 1;
            loadUserGame();
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Intent intent = new Intent("com.android.camera.action.CROP"); // 剪裁
            intent.setDataAndType(imageUri, "image/*");
            intent.putExtra("scale", true);
            // 设置宽高比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // 设置裁剪图片宽高
            intent.putExtra("outputX", 340);
            intent.putExtra("outputY", 340);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            Toast.makeText(this, "剪裁图片", Toast.LENGTH_SHORT).show();
            // 广播刷新相册
            Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intentBc.setData(imageUri);
            this.sendBroadcast(intentBc);
            startActivityForResult(intent, REQUEST_CROP_PHOTO); // 设置裁剪参数显示图片至ImageView
        } else if (requestCode == REQUEST_CROP_PHOTO && resultCode == RESULT_OK) {
            if (CURRENT_PHOTO_TYPE == 1) {
                bgDialog.dismiss();

                uploadUserBg();
            } else if (CURRENT_PHOTO_TYPE == 2) {
                photoDialog.dismiss();
                uploadUserPhoto();
            }
        } else if (requestCode == REQUEST_PERSONALHOME && resultCode == RESULT_OK) {
            Intent intent = new Intent(PersonalHomePageActivity.this, PersonalHomePageActivity.class);
            intent.putExtra("id", userId);
            startActivity(intent);
            finish();
        } else if (requestCode == REQUEST_HANDLE_PHOTO && resultCode == RESULT_OK) {
            loadUserPhotos();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (TextUtils.isEmpty(photos.get(position).getImg())) {
            if (photoDialog == null) {
                photoDialog = new AlertDialog.Builder(this).create();
                photoDialog.show();
                Window window = photoDialog.getWindow();
                window.setContentView(R.layout.layout_uploadpic_dialog);

                photoDialog.setCanceledOnTouchOutside(true);
                TextView tvDialogTitle = (TextView) photoDialog.findViewById(R.id.tvDialogTitle);
                TextView tvDialogCarema = (TextView) photoDialog.findViewById(R.id.tvDialogCarema);
                TextView tvDialogPhoto = (TextView) photoDialog.findViewById(R.id.tvDialogPhoto);
                tvDialogCarema.setOnClickListener(this);
                tvDialogPhoto.setOnClickListener(this);
                tvDialogTitle.setText("上传图片");
            } else {
                photoDialog.show();
            }
            CURRENT_PHOTO_TYPE = 2;
        } else {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            if (isMyShelf) {
                ArrayList<UserImage> userPhotos = new ArrayList<UserImage>();
                userPhotos.addAll(photos);
                userPhotos.remove(0);
                bundle.putSerializable("images", userPhotos);
                bundle.putInt("image_index", position - 1);
            } else {
                bundle.putSerializable("images", photos);
                bundle.putInt("image_index", position);
            }
            bundle.putBoolean("isMyShelf", isMyShelf);
            intent.putExtras(bundle);
            intent.setClass(this, PersonalHomePhotoActivity.class);
            startActivityForResult(intent, REQUEST_HANDLE_PHOTO);
        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case FileUploadUtil.SUCCESS:
                    hideLoading();
                    try {
                        Bundle resultData = msg.getData();
                        String result = resultData.getString("result");
                        if (uploadType == 1) {  //头像
                            loadUserPhotos();
                        } else {  //背景
                            JSONObject object = new JSONObject(result);
                            JSONObject data = object.getJSONObject("object");
                            if (data.has("bgImg")) {
                                String bgimg = data.getString("bgImg");
                                AsyncImage.loadNetPhoto(PersonalHomePageActivity.this, HttpConstant.SERVICE_UPLOAD_AREA + bgimg, ivPersonalPage);
                                User newUser = new Gson().fromJson(data.toString(), User.class);
                                newUser.setToken(WangYuApplication.getUser(PersonalHomePageActivity.this).getToken());
                                PreferencesUtil.setUser(PersonalHomePageActivity.this, new Gson().toJson(newUser));
                                WangYuApplication.setUser(newUser);
                            }
                        }
                        //else if (data.has())
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case FileUploadUtil.FAILED:
                    uploadType = -1;
                    hideLoading();
                    showToast(R.string.uploadfail);
                    break;
            }
        }
    };
}
