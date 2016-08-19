package com.miqtech.master.client.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
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
import com.miqtech.master.client.adapter.InformationItemAdapter;
import com.miqtech.master.client.entity.Catalog;
import com.miqtech.master.client.entity.InforBanner;
import com.miqtech.master.client.entity.InforCatalog;
import com.miqtech.master.client.entity.InforItemDetail;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.InformationAtlasActivity;
import com.miqtech.master.client.ui.InformationDetailActivity;
import com.miqtech.master.client.ui.InformationTopicActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.DateUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.BannerPagerView;
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
 * Created by Administrator on 2015/12/4.
 */
public class FragmentInformationItem extends BaseFragment implements InformationItemAdapter.OnItemClickListener, InformationItemAdapter.OnBannerItemClickListener, View.OnClickListener {
    @Bind(R.id.wypsInformation)
    PullToRefreshRecyclerView wypsInformation;
    @Bind(R.id.ll_info_menu)
    LinearLayout llInfoMenu;
    @Bind(R.id.ll_cata_content)
    LinearLayout llCataContent;
    @Bind(R.id.view_pop_down)
    View popDown;
    @Bind(R.id.view_hidden) 
    View alphaView;
    @Bind(R.id.img_pop)
    ImageView imgPop;

    private int infoCount;
    private boolean shouldShowMore = true;
    private LinearLayoutManager layoutManager;
    private InformationItemAdapter adapter;

    List<Catalog> infoTypes;
    Catalog selectedCata;
    private PopupWindow mPopViewType;
    private int selected;

    private int page = 1;//当前分页所在页数（默认值1）
    private int pageSize = 10;//当前分页显示的行数（默认值10）
    private final int type = 3;//1.订单 2.活动 3.系统 4评论
    private int isAll = 0;//isAll=1时返回所有的数据,可以不传
    private int isLast = 0;//是否最后一条


    public boolean isFirst = true;

    private List<InforBanner> mBanners = new ArrayList<>();
    private List<InforItemDetail> mItemDetails = new ArrayList<>();

    private int listId;

    private int mPostion;
    private InforCatalog mInfoCatalog;
    private static final String INFOMATION_POSITION = "position";
    private static final String INFOMATION_CATALOG = "catalog";

    private int lastVisiable = 0;

    private String intTimeTwo;//开始时间（二级模块 ）
    private String outTimeTwo;//结束时间（二级模块）
    private String idOldTwo = null;//原模块id
    private boolean isRefreshOldTwoModule = false;
    private RecyclerView recyclerInformations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPostion = getArguments().getInt(INFOMATION_POSITION);
        mInfoCatalog = getArguments().getParcelable(INFOMATION_CATALOG);
        selectedCata = mInfoCatalog.getParent();

        lengthCoding = UMengStatisticsUtil.CODE_1001;
        lengthTargetId = selectedCata.getId() + "";
        isModuleFragment = true;
        isViewPageFragment = true;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirst) {
            isFirst = false;
            once = false;
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

    @Override
    public void onResume() {
        super.onResume();
        hideLoading();
    }

    /**
     * 加载数据
     */
    private void getInfomations(String type) {
        once = false;
        showLoading();
        Map<String, String> params = new HashMap();
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        params.put("id", selectedCata.getId() + "");
        params.put("pid", selectedCata.getPid() + "");
        LogUtil.e("getInfomations-------------------------------------", HttpConstant.INFORMATION_LIST);
        LogUtil.e("once---------------getInfomations----------------------", once + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INFORMATION_LIST, params, HttpConstant.INFORMATION_LIST);
        LogUtil.e(TAG, "params : " + params.toString());
        /*params.put("infoCount", infoCount + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.INFO_LIST, params, HttpConstant.INFO_LIST);*/
        getLogTime();
    }

    private void getLogTime() {
        //lengthTargetId代表的是一级目录模块的id,也就是全部资讯,是要和二级目录分开，if里面记录的是二级模块的停留时间
        if (!TextUtils.isEmpty(lengthTargetId) && !lengthTargetId.equals(selectedCata.getId() + "") && !lengthTargetId.equals(idOldTwo)) {
            if (!TextUtils.isEmpty(idOldTwo) && !idOldTwo.equals(selectedCata.getId() + "")) {
                outTimeTwo = DateUtil.getStringToday();
                LogUtil.e("getInfomations-------------------------------------", HttpConstant.LOG_TIME);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initView();
    }


    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_information_item, container, false);
    }

    private boolean once = false;

    @Override
    public void onSuccess(JSONObject object, String method) {
        try {
            if (method.equals(HttpConstant.INFORMATION_LIST)) {
                if (!once) {
                    once = true;
                    hideLoading();
                    adapter.hideFooter();

                    JSONObject resultObj = object.getJSONObject("object");
                    if (page == 1) {
                        List<InforBanner> datas = new Gson().fromJson(resultObj.getJSONArray("banner").toString(), new TypeToken<List<InforBanner>>() {
                        }.getType());
                        mBanners.clear();
                        if (datas != null && !datas.isEmpty()) {
                            mBanners.addAll(datas);
                            adapter.setBanner(mBanners);
                        } else {
                            adapter.setBanner(new ArrayList<InforBanner>());
                        }
                    }
                    List<InforItemDetail> newItems;
                    if (resultObj.getJSONObject("information").has("list")) {
                        newItems = new Gson().fromJson(resultObj.getJSONObject("information").getJSONArray("list").toString(), new TypeToken<List<InforItemDetail>>() {
                        }.getType());
                    } else {
                        newItems = new ArrayList<>();
                    }

                    if (page == 1 && mItemDetails != null) {
                        mItemDetails.clear();
                    }

                    isLast = resultObj.getJSONObject("information").getInt("isLast");
                    mItemDetails.addAll(newItems);
                    adapter.setData(mItemDetails);
                    setupView();

                    wypsInformation.onRefreshComplete();
                    imgPop.setOnClickListener(this);
                    if (page > 1) {
                        if (newItems.isEmpty()) {
                            page--;
                        }
                    }
                }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * initview
     */
    private void initView() {
        wypsInformation.setHeaderLayout(new FrameAnimationHeaderLayout(getActivity()));
        recyclerInformations = wypsInformation.getRefreshableView();
        recyclerInformations.getItemAnimator().setAddDuration(250);
        recyclerInformations.getItemAnimator().setMoveDuration(250);
        recyclerInformations.getItemAnimator().setChangeDuration(250);
        recyclerInformations.getItemAnimator().setRemoveDuration(250);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerInformations.setLayoutManager(layoutManager);
        adapter = new InformationItemAdapter(getActivity());
        recyclerInformations.setAdapter(adapter);

        wypsInformation.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                isRefreshOldTwoModule = true;
                once = false;
                page = 1;
                shouldShowMore = true;
                pageSize = 10;
                getInfomations("1");

                LogUtil.d("ExpiryListActivity","onPullDownToRefresh");
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
                        wypsInformation.onRefreshComplete();
                    }
                }else{
                    wypsInformation.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if(!isHasNetWork){
                    showToast(getResources().getString(R.string.noNeteork));
                }
            }
        });

        adapter.setOnItemClickListener(this);
        adapter.setBannerItemClickListener(this);
        addInfoTypes();
    }

    private void setupView() {
        LogUtil.e("setupView----------------------------", "setupView");
        LogUtil.e("page----------------------------", page + "");
        if (page == 1) {
            bannerDetach();
            LogUtil.e("adapter.notifyDataSetChanged----------------------------", "adapter.notifyDataSetChanged");
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        hideLoading();
        adapter.hideFooter();
        wypsInformation.onRefreshComplete();
        if (page > 1) {
            page--;
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        hideLoading();
        adapter.hideFooter();
        wypsInformation.onRefreshComplete();
        if (page > 1) {
            page--;
        }
    }

    public static FragmentInformationItem newInstance(int postion, InforCatalog catalog) {
        FragmentInformationItem fragment = new FragmentInformationItem();
        Bundle bundle = new Bundle();
        bundle.putInt(INFOMATION_POSITION, postion);
        bundle.putParcelable(INFOMATION_CATALOG, catalog);
        fragment.setArguments(bundle);
        return fragment;
    }


    /**
     * 处理item 点击事件回调方法
     *
     * @param view
     * @param postion
     */
    @Override
    public void onItemClick(View view, int postion) {
        FragmentInformationV3 parent = (FragmentInformationV3) getParentFragment();
        if (parent.isPopShowing()) {
            return;
        }
        InforItemDetail detail = mItemDetails.get(postion);

        Intent intent = null;
        if (detail.getType() == 1) {//类型:1图文  跳转
            intent = new Intent(getActivity(), InformationDetailActivity.class);
            intent.putExtra("id", detail.getId() + "");
            intent.putExtra("type", detail.getType());
            intent.putExtra("categoryId", mInfoCatalog.getParent().getId() + "");
            intent.putExtra("pid", mInfoCatalog.getParent().getPid() + "");
            getActivity().startActivity(intent);
        } else if (detail.getType() == 2) {//2专题  跳转
            intent = new Intent();
            intent.putExtra("activityId", detail.getId());
            intent.putExtra("zhuanTitle", detail.getTitle());
            intent.putExtra("mInfoCatalog", mInfoCatalog);
            intent.putExtra("url", detail.getIcon());
            intent.setClass(getActivity(), InformationTopicActivity.class);
            getActivity().startActivity(intent);
        } else if (detail.getType() == 3) {//3图集  跳转
            intent = new Intent();
            intent.putExtra("activityId", detail.getId());
            intent.setClass(getActivity(), InformationAtlasActivity.class);
            getActivity().startActivity(intent);
        } else if (detail.getType() == 4) {   //视频
            Intent matchIntent = new Intent(getContext(), InformationDetailActivity.class);
            //TODO 此处需要传入指定的vid   为了测试现在暂时写死
            matchIntent.putExtra("vid", "XMTI1OTMxODc3Ng==");
            matchIntent.putExtra("type", InformationDetailActivity.INFORMATION_VIDEO);
            startActivity(matchIntent);
        }

//        InforItemDetail detail = mItemDetails.get(postion);
//        Intent intent = new Intent();
//        intent.setClass(getActivity(), InformationDetailActivity.class);
//        intent.putExtra("categoryId", mInfoCatalog.getParent().getId() + "");
//        intent.putExtra("pid", mInfoCatalog.getParent().getPid() + "");
//        intent.putExtra("id", detail.getId() + "");
//        startActivity(intent);
    }

    /**
     * 处理 banner 点击事件的回调方法
     *
     * @param view
     * @param position
     */
    @Override
    public void onBannerItemClick(View view, int position) {
        Intent intent;
        InforBanner banner = mBanners.get(position);
        if (banner.getType() == 1) {//类型:1图文
            intent = new Intent();
            intent.setClass(getActivity(), InformationDetailActivity.class);
            intent.putExtra("categoryId", mInfoCatalog.getParent().getId() + "");
            intent.putExtra("pid", mInfoCatalog.getParent().getPid() + "");
            intent.putExtra("id", banner.getId() + "");
            startActivity(intent);
        } else if (banner.getType() == 2) {//2专题
            intent = new Intent();
            intent.putExtra("activityId", banner.getId());
            intent.putExtra("zhuanTitle", banner.getTitle());
            intent.setClass(getContext(), InformationTopicActivity.class);
            startActivity(intent);
        } else if (banner.getType() == 3) {//3图集
            intent = new Intent();
            intent.putExtra("activityId", banner.getId()+"");
            intent.setClass(getContext(), InformationAtlasActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 添加标签
     */
    private void addTabs(FlowLayout linearLayout, int patch) {
        LogUtil.e(TAG, "--patch--" + infoTypes.toString());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        linearLayout.removeAllViews();
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = Utils.dp2px(6);
        params.setMargins(0, 0, margin, margin);
        for (int i = 0; i < infoTypes.size(); i++) {
            TextView view = (TextView) inflater.inflate(R.layout.layout_video_label, null);
            view.setId(i);
            view.setTag(i);
            view.setText(infoTypes.get(i).getName());
            setTypeClickListener(view, i);
            linearLayout.addView(view, params);
        }
    }

    private int addTabsByPatch(final LinearLayout parent, int start, int end) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.MarginLayoutParams params = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = Utils.dp2px(6);
        params.setMargins(0, 0, margin, margin);
        int width = 0;
        int patch = 0;
        int index = 0;
        for (int i = start; i <= infoTypes.size() && i < end; i++) {
            LogUtil.e("tag", "index i " + i);
            TextView view = (TextView) inflater.inflate(R.layout.layout_video_label, null, false);
            view.setId(i - 1);
            view.setTag(i - 1);
            view.setText(infoTypes.get(i - 1).getName());
            view.setLayoutParams(params);
            setTypeClickListener(view, i - 1);
            linearLayout.addView(view);
            linearLayout.measure(0, 0);
            width = linearLayout.getMeasuredWidth();
            LogUtil.e("TAG", "index i width : " + width + "   screen   " + ScreenUtils.getScreenSize(getContext())[0]);

            if (width > ScreenUtils.getScreenSize(getContext())[0]) {
                linearLayout.removeView(view);
                index = i - start;
                break;
            }
            patch = i;
        }
        linearLayout.setLayoutParams(params);
        parent.addView(linearLayout);
        return index == 0 ? patch - start + 1 : index;
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
                recyclerInformations.scrollToPosition(0);
                selectedCata = infoTypes.get(position);
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


    private <T extends View> void viewShow(T view) {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_view_show);
        view.startAnimation(animation);
    }

    private <T extends View> void viewHide(T view) {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_view_hide);
        view.startAnimation(animation);
    }

    /**
     * 视频类别
     */
    private void addInfoTypes() {
        infoTypes = mInfoCatalog.getSub();
        if (infoTypes == null || infoTypes.isEmpty()) {
            llInfoMenu.setVisibility(View.GONE);
            return;
        } else {
            llInfoMenu.setVisibility(View.VISIBLE);
        }
        Catalog all = new Catalog();
        all.setId(selectedCata.getId());
        all.setPid(selectedCata.getPid());
        all.setType(selectedCata.getType());
        all.setName("全部资讯");
        infoTypes.add(0, all);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        llCataContent.removeAllViews();
        for (int i = 0; i < infoTypes.size(); i++) {
            TextView view = (TextView) inflater.inflate(R.layout.layout_video_label, null, false);
            view.setId(i);
            view.setTag(i);
            view.setText(infoTypes.get(i).getName());
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

    private void bannerDetach() {
        BannerPagerView banner = (BannerPagerView) getView().findViewById(R.id.vp_banner);
        if (banner != null) {
            banner.detach();
            banner = null;
        }
    }

    /**
     * 提交二级模块
     */
    private void submitBuriedPoint() {

    }
}
