package com.miqtech.master.client.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.FragmentInfoVideoAdapter;
import com.miqtech.master.client.entity.Catalog;
import com.miqtech.master.client.entity.InforCatalog;
import com.miqtech.master.client.entity.InforItemDetail;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.InformationDetailActivity;
import com.miqtech.master.client.ui.LiveRoomActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.FlowLayout;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshBase;
import com.miqtech.master.client.view.pullToRefresh.PullToRefreshRecyclerView;
import com.miqtech.master.client.view.pullToRefresh.internal.FrameAnimationHeaderLayout;
import com.zhy.autolayout.utils.ScreenUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/4/13.
 */
public class FragmentInfoVideoItem extends BaseFragment implements View.OnClickListener, FragmentInfoVideoAdapter.OnItemClickListener {
    @Bind(R.id.ll_cata_content)
    LinearLayout llCataContent;
    @Bind(R.id.ll_info_menu)
    LinearLayout llInfoMenu;
    @Bind(R.id.recycleVideo)
    PullToRefreshRecyclerView recycleVideo;
    @Bind(R.id.img_pop)
    ImageView imgPop;
    @Bind(R.id.view_pop_down)
    View popDown;
    @Bind(R.id.view_hidden)
    View alphaView;
    private int page = 1;
    private int pageSize = 12;
    private int infoCount = 0;
    private int isLast = 0;
    private boolean shouldShowMore = true;
    private List<InforItemDetail> mDatas = new ArrayList<>();

    private int mPostion;
    private InforCatalog mInfoCatalog;
    private static final String INFOMATION_POSITION = "position";
    private static final String INFOMATION_CATALOG = "catalog";

    private int lastVisiable = 0;

    private GridLayoutManager layoutManager;
    private FragmentInfoVideoAdapter adapter;

    private PopupWindow mPopViewType;
    List<Catalog> videoTypes;

    Catalog selectedCata;

    private int selected;

    private boolean isRefreshOldTwoModule = false;
    private boolean isFirst = true;
    private String intTimeTwo;//开始时间（二级模块 ）
    private String outTimeTwo;//结束时间（二级模块）
    private String idOldTwo;//原模块id
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPostion = getArguments().getInt(INFOMATION_POSITION);
        mInfoCatalog = getArguments().getParcelable(INFOMATION_CATALOG);
        selectedCata = mInfoCatalog.getParent();
    }

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_infovideo, container, false);
    }

    public static FragmentInfoVideoItem newInstance(int postion, InforCatalog catalog) {
        FragmentInfoVideoItem fragment = new FragmentInfoVideoItem();
        Bundle bundle = new Bundle();
        bundle.putInt(INFOMATION_POSITION, postion);
        bundle.putParcelable(INFOMATION_CATALOG, catalog);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setupView();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirst) {
            isFirst = false;
            getInfomations("");
        }


        //计算二级模块的时间,并在切换到其它时上传本次停留的时间
        if (isVisibleToUser) {
            intTimeTwo = DateUtil.getStringToday();
        } else {
            if (!TextUtils.isEmpty(idOldTwo)) {
                outTimeTwo = DateUtil.getStringToday();
                postLogTime(UMengStatisticsUtil.CODE_1002, intTimeTwo, outTimeTwo, idOldTwo);
                outTimeTwo = null;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //当在二级模块出去在回来超过30秒时，上传前一次停留的时间
        if (is30Seconds > UMengStatisticsUtil.LEAVER_APP_TIME) {
            if (!TextUtils.isEmpty(idOldTwo) && !TextUtils.isEmpty(intTimeTwo)) {
                outTimeTwo = DateUtil.getStringToday();
                postLogTime(UMengStatisticsUtil.CODE_1002, intTimeTwo, outTimeTwo, idOldTwo);
                outTimeTwo = null;
                intTimeTwo = DateUtil.getStringToday();
            }
        }
    }


    private void setupView() {
        recycleVideo.setHeaderLayout(new FrameAnimationHeaderLayout(getActivity()));
        recyclerView =recycleVideo.getRefreshableView();
        layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == mDatas.size()) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FragmentInfoVideoAdapter(getActivity(), mDatas);
        recyclerView.setAdapter(adapter);
        recycleVideo.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                isRefreshOldTwoModule = true;
                page = 1;
                shouldShowMore = true;
                getInfomations("");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (adapter != null) {
                    if (isLast == 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                page++;
                                getInfomations("1");
                            }
                        }, 1000);
                    } else {
                        if (shouldShowMore) {
                            showToast(getResources().getString(R.string.nomore));
                            shouldShowMore = false;
                        }
                        recycleVideo.onRefreshComplete();
                    }
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if(!isHasNetWork){
                  showToast(getResources().getString(R.string.noNeteork));
                }
            }
        });
        addVideoType();

        adapter.setOnItemClickListener(this);

        imgPop.setOnClickListener(this);
    }

    /**
     * 加载视频列表
     */
    private void getInfomations(String type) {
       /* showLoading();
        HashMap map = new HashMap();
        map.put("page", page + "");
        map.put("pageSize", pageSize + "");
        map.put("infoCount", infoCount + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INFO_LIST, map, HttpConstant.INFO_LIST);*/
        showLoading();
        Map<String, String> params = new HashMap();
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");

        params.put("id", selectedCata.getId() + "");
        params.put("pid", selectedCata.getPid() + "");
        LogUtil.e(TAG, "params : " + params.toString());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INFORMATION_LIST, params, HttpConstant.INFORMATION_LIST);


        //lengthTargetId代表的是一级目录模块的id,也就是全部资讯,是要和二级目录分开，if里面记录的是二级模块的停留时间
        if (!TextUtils.isEmpty(lengthTargetId) && !lengthTargetId.equals(selectedCata.getId() + "") && !lengthTargetId.equals(idOldTwo)) {
            if (!TextUtils.isEmpty(idOldTwo) && !idOldTwo.equals(selectedCata.getId() + "")) {
                outTimeTwo = DateUtil.getStringToday();
                postLogTime(UMengStatisticsUtil.CODE_1002, intTimeTwo, outTimeTwo, idOldTwo);
                intTimeTwo = DateUtil.getStringToday();
                outTimeTwo = null;
                isRefreshOldTwoModule = false;
            }

            //如果二级目录是第一次就重置进入时间，如果刷新原页面就不重置进入时间
            //主要是为了计算一级模块下的第一个二级目录的进入时间
            if (!isRefreshOldTwoModule) {
                intTimeTwo = DateUtil.getStringToday();
            }
        }
        idOldTwo = selectedCata.getId() + "";
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        recycleVideo.onRefreshComplete();
        if (adapter != null) {
            adapter.hideFooter();
        }
        if (HttpConstant.INFORMATION_LIST.equals(method)) {
            try {
                JSONObject resultObj = object.getJSONObject("object");
                List<InforItemDetail> newItems = new Gson().fromJson(resultObj.getJSONObject("information").getJSONArray("list").toString(), new TypeToken<List<InforItemDetail>>() {
                }.getType());
                if (page == 1 && mDatas != null) {
                    mDatas.clear();
                }
                isLast = resultObj.getJSONObject("information").getInt("isLast");
                mDatas.addAll(newItems);
                if (page == 1) {
                    adapter.notifyDataSetChanged();
                }
                if (page > 1) {
                    if (newItems.isEmpty()) {
                        page--;
                    }
                }
                LogUtil.e(TAG, "---------success---" + mDatas.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        recycleVideo.onRefreshComplete();
        if (adapter != null) {
            adapter.hideFooter();
        }
        if (page > 1) {
            page--;
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        recycleVideo.onRefreshComplete();
        if (adapter != null) {
            adapter.hideFooter();
        }
        if (page > 1) {
            page--;
        }

    }

    /**
     * 视频类别
     */
    private void addVideoType() {
        videoTypes = mInfoCatalog.getSub();
        if (videoTypes == null || videoTypes.isEmpty()) {
            llInfoMenu.setVisibility(View.GONE);
            return;
        } else {
            llInfoMenu.setVisibility(View.VISIBLE);
        }
        Catalog all = new Catalog();
        all.setId(selectedCata.getId());
        all.setPid(selectedCata.getPid());
        all.setType(selectedCata.getType());
        all.setName("全部视频");
        videoTypes.add(0, all);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        llCataContent.removeAllViews();
        for (int i = 0; i < videoTypes.size(); i++) {
            TextView view = (TextView) inflater.inflate(R.layout.layout_video_label, null, false);
            view.setId(i);
            view.setTag(i);
            view.setText(videoTypes.get(i).getName());
            LinearLayout.MarginLayoutParams params = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int margin = Utils.dp2px(6);
            params.setMargins(0, 0, margin, 0);
            view.setLayoutParams(params);
            if (i == 0) {
                view.setBackgroundResource(R.drawable.bg_lable_video_selected);
                view.setTextColor(getResources().getColor(R.color.orange));
            }
            setTypeClickListener(view, i);
            llCataContent.addView(view);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_pop:
                showPopWindow();
                break;
            case R.id.view_pop_empty:
                mPopViewType.dismiss();
                break;
            case R.id.img_close:
                mPopViewType.dismiss();
                break;
        }
    }

    /**
     * 弹出popwindow
     */
    private void showPopWindow() {
        if (mPopViewType == null) {
            mPopViewType = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) {
                @Override
                public void dismiss() {
                    alphaView.setVisibility(View.GONE);
                    viewShow((View) llCataContent.getParent());
                    super.dismiss();
                }
            };
            mPopViewType.setAnimationStyle(R.style.pop_anim);
            View popView = LayoutInflater.from(getContext()).inflate(R.layout.layout_pop_videotype, null);
            FlowLayout llContent = (FlowLayout) popView.findViewById(R.id.ll_pop_content);
            addTabs(llContent, 5);
            View hiddenView = popView.findViewById(R.id.view_pop_empty);
            hiddenView.setOnClickListener(this);
            changeTypeState(llContent, selected);
            mPopViewType.setContentView(popView);
            mPopViewType.setOutsideTouchable(true);
            ImageView imgclose = (ImageView) popView.findViewById(R.id.img_close);
            imgclose.setOnClickListener(this);
            ColorDrawable cd = new ColorDrawable(Color.TRANSPARENT);
            mPopViewType.setBackgroundDrawable(cd);
        }
        alphaView.setVisibility(View.VISIBLE);
        viewHide((View) llCataContent.getParent());
        mPopViewType.showAsDropDown(popDown, 0, 0);

    }

    /**
     * 添加标签
     */
    private void addTabs(FlowLayout linearLayout, int patch) {
        LogUtil.e(TAG, "--patch--" + videoTypes.toString());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        linearLayout.removeAllViews();
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = Utils.dp2px(6);
        params.setMargins(0, 0, margin, margin);
        for (int i = 0; i < videoTypes.size(); i++) {
            TextView view = (TextView) inflater.inflate(R.layout.layout_video_label, null);
            view.setId(i);
            view.setTag(i);
            view.setText(videoTypes.get(i).getName());
            setTypeClickListener(view, i);
            linearLayout.addView(view, params);
        }
    }

    /**
     * change type state
     */
    private void changeTypeState(ViewGroup parent, int tag) {
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                changeTypeState((ViewGroup) child, tag);
            } else {
                if ((Integer) child.getTag() == tag) {
                    ((TextView) child).setTextColor(getResources().getColor(R.color.orange));
                    child.setBackgroundResource(R.drawable.bg_lable_video_selected);
                } else {
                    ((TextView) child).setTextColor(getResources().getColor(R.color.textColorBattle));
                    child.setBackgroundResource(R.drawable.bg_lable_video_unselected);
                }
            }
        }

    }

    /**
     * 设置type 点击
     */
    private void setTypeClickListener(final View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTypeState(llCataContent, (int) view.getTag());
                if (mPopViewType != null) {
                    changeTypeState(((ViewGroup) mPopViewType.getContentView().findViewById(R.id.ll_pop_content)), (int) view.getTag());
                }
                if (mPopViewType != null && mPopViewType.isShowing()) {
                    mPopViewType.dismiss();
                    smoothScrollTypes(position);
                }
                selected = (int) v.getTag();
                page = 1;
                recyclerView.scrollToPosition(0);
                selectedCata = videoTypes.get(position);
                getInfomations("");

            }
        });
    }

    /**
     * 移动scrollview
     */
    private void smoothScrollTypes(int i) {
        View child = llCataContent.getChildAt(i);
        Rect rect = new Rect();
        child.getHitRect(rect);
        ((HorizontalScrollView) llCataContent.getParent()).smoothScrollTo(rect.left, 0);
    }

    /**
     * item 点击事件
     *
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(View view, int position) {
        FragmentInformationV3 parent = (FragmentInformationV3) getParentFragment();
        if (parent.isPopShowing()) {
            return;
        }
        Intent matchIntent = new Intent(getContext(), InformationDetailActivity.class);
        //TODO 此处需要传入指定的vid   为了测试现在暂时写死
        //      matchIntent.putExtra("vid", "XMTI1OTMxODc3Ng==");
        matchIntent.putExtra("id", mDatas.get(position).getId() + "");
        matchIntent.putExtra("type", InformationDetailActivity.INFORMATION_VIDEO);
        getContext().startActivity(matchIntent);

//        Intent intent = new Intent(getContext(), LiveRoomActivity.class);
//        intent.putExtra("videoPath", "rtmp://pili-live-rtmp.wangyuhudong.com/wyds/wyds_test_android_01");
//        intent.putExtra("mediaCodec", 1);
//        intent.putExtra("liveStreaming", 1);
//        startActivity(intent);


    }

    private <T extends View> void viewShow(T view) {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_view_show);
        view.startAnimation(animation);
    }

    private <T extends View> void viewHide(T view) {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_view_hide);
        view.startAnimation(animation);
    }
}
