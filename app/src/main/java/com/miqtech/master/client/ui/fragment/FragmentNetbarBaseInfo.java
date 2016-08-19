package com.miqtech.master.client.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.InternetBarInfo;
import com.miqtech.master.client.entity.NetbarArea;
import com.miqtech.master.client.entity.NetbarService;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.BaiduMapActivity;
import com.miqtech.master.client.ui.SubjectActivity;
import com.miqtech.master.client.ui.basefragment.BaseFragment;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.utils.AsyncImage;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/3/8.
 */
public class FragmentNetbarBaseInfo extends BaseFragment implements View.OnClickListener {

    private InternetBarInfo mNetbarInfo;


    private Dialog dialog;
    private Window mWindow;
    private LinearLayout dialog_ll;
    private LinearLayout llContent;
    private LayoutInflater inflater;
    private String[] netbarLevels = new String[]{"普通网吧", "会员网吧", "黄金网吧", "钻石网吧"};

    @Override
    public View onViewInit(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        llContent = (LinearLayout) inflater.inflate(R.layout.fragment_netbar_baseinfo, container, false);
        return llContent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetbarInfo = (InternetBarInfo) getArguments().getSerializable("nerbar");
        lengthCoding = UMengStatisticsUtil.CODE_2004;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        inflater = LayoutInflater.from(getContext());
        setupView();
    }

    private void setupView() {
        if (mNetbarInfo != null) {
            if (mNetbarInfo.getLevels() == 0) {  //非会员网吧
                initNetbarInfoView();
                initConfigView();
            } else { //会员及以上等级网吧
                initNetbarActivityView();
                initDiscountView();
                initNetbarPriceView();
                initNetbarInfoView();
                initConfigView();
            }
        }
    }

    /**
     * 价目表
     */
    private void initNetbarPriceView() {
        View itemView = inflater.inflate(R.layout.layout_netbar_price, null, false);
        LinearLayout llprice = (LinearLayout) itemView.findViewById(R.id.ll_netbar_price);
        List<NetbarArea> areas = mNetbarInfo.getArea();
        if (areas == null || areas.isEmpty()) {
            return;
        }
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.orange));
        llprice.removeAllViews();
        for (NetbarArea area : areas) {
            View view = inflater.inflate(R.layout.layout_netbar_price_item, null);
            TextView areaName = (TextView) view.findViewById(R.id.tv_area_name);
            TextView normalPrice = (TextView) view.findViewById(R.id.tv_price_normal);
            TextView memberPrice = (TextView) view.findViewById(R.id.tv_price_member);

            areaName.setText(area.getArea_name());
            String price = getString(R.string.price_per_hour_v3, area.getPrice() + "");
            SpannableStringBuilder style = new SpannableStringBuilder(price);
            style.setSpan(span, 1, style.length() - 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            normalPrice.setText(style);

            String memPrice = getString(R.string.price_per_hour_v3, area.getRebate_price() + "");
            style = new SpannableStringBuilder(memPrice);
            style.setSpan(span, 1, style.length() - 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            memberPrice.setText(style);
            llprice.addView(view);
        }
        llContent.addView(itemView);
    }

    /**
     * 网吧信息
     */
    private void initNetbarInfoView() {
        View itemView = inflater.inflate(R.layout.layout_netbar_barinfo, null, false);
        TextView tvNetbarName;
        TextView tvLevel;
        TextView tvAddress;
        TextView tvPhone;
        TextView tvServer;
        ImageView imgCertificate;
        tvNetbarName = (TextView) itemView.findViewById(R.id.tv_netbar_name);
        tvLevel = (TextView) itemView.findViewById(R.id.tv_netbar_level);
        tvAddress = (TextView) itemView.findViewById(R.id.tv_netbar_address);
        tvPhone = (TextView) itemView.findViewById(R.id.tv_netbar_phone);
        tvServer = (TextView) itemView.findViewById(R.id.tv_netbar_server);
        imgCertificate = (ImageView) itemView.findViewById(R.id.img_certificate);
        tvNetbarName.setText(mNetbarInfo.getName());
        tvLevel.setText(netbarLevels[mNetbarInfo.getLevels()]);
        if (0 != mNetbarInfo.getLevels()) {
            imgCertificate.setVisibility(View.VISIBLE);
        } else {
            imgCertificate.setVisibility(View.GONE);
        }
        tvPhone.setText(mNetbarInfo.getTelephone());
        tvAddress.setText(mNetbarInfo.getAddress());
        tvAddress.setOnClickListener(this);
        tvPhone.setOnClickListener(this);
        tvServer.setText(mNetbarInfo.getTag());
        llContent.addView(itemView);
    }

    /**
     * 配置信息
     */
    private void initConfigView() {
        View itemView = inflater.inflate(R.layout.layout_netbar_configuration, null, false);
        TextView tvCount;//机位
        TextView tvCpu; //cpu
        TextView tvDisplay; //显卡
        TextView tvMemory;  //内存
        tvCount = (TextView) itemView.findViewById(R.id.tv_netbar_count);
        tvCpu = (TextView) itemView.findViewById(R.id.tv_netbar_cpu);
        tvDisplay = (TextView) itemView.findViewById(R.id.tv_netbar_display);
        tvMemory = (TextView) itemView.findViewById(R.id.tv_netbar_memory);
        tvCpu.setText(mNetbarInfo.getCpu());
        tvMemory.setText(mNetbarInfo.getMemory());
        tvCount.setText(mNetbarInfo.getSeating() + "");
        tvDisplay.setText(mNetbarInfo.getDisplay());
        llContent.addView(itemView);
    }

    /**
     * 购买的活动
     */
    private void initNetbarActivityView() {
        for (int i = 0; i < mNetbarInfo.getServices().size(); i++) {
            View itemView = inflater.inflate(R.layout.layout_netbar_services, null, false);
            ImageView imgServer;
            TextView tvTitle;
            TextView tvCount;
            LinearLayout llServer;
            imgServer = (ImageView) itemView.findViewById(R.id.im_server);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_server_title);
            tvCount = (TextView) itemView.findViewById(R.id.tv_interested);
            llServer = (LinearLayout) itemView.findViewById(R.id.ll_server);
            NetbarService service = mNetbarInfo.getServices().get(i);
            final int position = i;
            if (service != null) {
                tvTitle.setText(service.getName());
                tvCount.setText(getString(R.string.interested_count, service.getInterest_num() + ""));
                AsyncImage.loadNetPhoto(getContext(), HttpConstant.SERVICE_UPLOAD_AREA + service.getUrl(), imgServer);
                llServer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        jumpToServerDetail(position);
                    }
                });
            }
            llContent.addView(itemView);
        }
    }

    /**
     * 公告与优惠
     */
    private void initDiscountView() {
        View itemView = inflater.inflate(R.layout.layout_netbar_discount, null, false);
        TextView tvDiscount;
        tvDiscount = (TextView) itemView.findViewById(R.id.tv_discount_content);
        if (!TextUtils.isEmpty(mNetbarInfo.getDiscount_info())) {
            tvDiscount.setText(mNetbarInfo.getDiscount_info());
        } else {
            tvDiscount.setText("当前网吧暂无公告，敬请关注本店其他活动");
        }
        llContent.addView(itemView);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.tv_netbar_address) { //地址
            /** 地图导航 **/
            if (mNetbarInfo == null || mNetbarInfo.getLatitude() == 0 | mNetbarInfo.getLongitude() == 0) {
                showToast("该网吧暂无数据");
                return;
            }
            Intent intent = new Intent();
            intent.setClass(getContext(), BaiduMapActivity.class);
            // 封装数据
            Bundle bundle = new Bundle();
            bundle.putDouble("latitude", mNetbarInfo.getLatitude());
            bundle.putDouble("longitude", mNetbarInfo.getLongitude());
            bundle.putString("netbarTitle", mNetbarInfo.getNetbar_name());
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (view.getId() == R.id.tv_netbar_phone) {  //电话
            showdialog();
        }
    }

    /**
     * jump to h5
     */
    private void jumpToServerDetail(int position) {
        NetbarService service = mNetbarInfo.getServices().get(position);
        Intent intent = new Intent(getContext(), SubjectActivity.class);
        intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.NETBAR_SERVICE);
        intent.putExtra("id", service.getProperty_id() + "");
        intent.putExtra("netbarId", mNetbarInfo.getId() + "");
        startActivity(intent);
    }

    /**
     * 拨打电话
     */
    private void showdialog() {
        dialog = new AlertDialog.Builder(getContext()).create();
        dialog.show();
        mWindow = dialog.getWindow();
        mWindow.setContentView(R.layout.layout_internet_bar_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog_ll = (LinearLayout) dialog.findViewById(R.id.dialog_phone_ll);
        TextView tv = new TextView(getContext());
        tv.setText(mNetbarInfo.getTelephone());
        tv.setTextColor(getResources().getColor(R.color.colorActionBarSelected));
        tv.setTextSize(18);
        dialPhone(tv, mNetbarInfo.getTelephone());
        dialog_ll.addView(tv);
    }

    private void dialPhone(TextView tv, final String str) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (str != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + str));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    dialog.dismiss();
                }
            }
        });
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        LogUtil.e("onStart------------------FragmentNetbarBaseInfo-------------","onStart");
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        LogUtil.e("onStop------------------FragmentNetbarBaseInfo-------------","onStop");
//    }
//
//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        LogUtil.e("onHiddenChanged------------------FragmentNetbarBaseInfo-------------",hidden+"");
//    }

}
