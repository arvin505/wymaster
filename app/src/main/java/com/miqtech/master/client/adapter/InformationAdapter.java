package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.InforBanner;
import com.miqtech.master.client.entity.InforItemDetail;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.InformationAtlasActivity;
import com.miqtech.master.client.ui.InformationTopicActivity;
import com.miqtech.master.client.ui.MainActivity;
import com.miqtech.master.client.ui.SubjectActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.view.InforHeadLinesView;
import com.miqtech.master.client.view.SlidingMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/4.
 */
public class InformationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SlidingMenu.isAutoPlay {
    private Context context;
    private List<InforItemDetail> list;
    private String read;
    private String wread;
    private String picture;
    private List<ImageView> imageViews = new ArrayList<ImageView>();
    private LayoutInflater mInflater;

    private final int FOOTER_TYPE = 5;
    private final int HEADER_TYPE = 4;
    private View headView;
    private View footerView;

    private List<InforBanner> bannerList = new ArrayList<InforBanner>();
    InforHeadLinesView myBannerView;


    public InformationAdapter(Context context, List<InforItemDetail> list, List<InforBanner> bannerList) {
        this.context = context;
        this.list = list;
        this.bannerList = bannerList;
        mInflater = LayoutInflater.from(context);
        read = context.getResources().getString(R.string.read);
        wread = context.getResources().getString(R.string.wread);
        picture = context.getResources().getString(R.string.picture);

        if (bannerList.isEmpty()) {
            list.remove(0);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (!bannerList.isEmpty()) {
            if (position == 0) {
                return HEADER_TYPE;
            }
        }
        if (position + 1 == getItemCount()) {
            return FOOTER_TYPE;
        } else {
            return list.get(position).getType();
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View v = null;
        View footerV = null;
        switch (viewType) {
            case InforItemDetail.ATHLETICS_TYPE:
                v = mInflater.inflate(R.layout.information_listview_item_2, parent, false);
                holder = new ViewHolderTwo(v);
                break;
            case FOOTER_TYPE:
                footerV = mInflater.inflate(R.layout.layout_footer_view, parent, false);
                holder = new FooterViewHolder(footerV);
                break;
            case HEADER_TYPE:
                v = LayoutInflater.from(context).inflate(R.layout.layout_banner_infor_fragment, null);
                headView = v;
                holder = new HeaderViewHolder(v);
                break;
            default:
                v = mInflater.inflate(R.layout.information_listview_item_1, parent, false);
                holder = new ViewHolderOne(v);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case InforItemDetail.ATHLETICS_TYPE:
                ViewHolderTwo holderTwo = (ViewHolderTwo) holder;
                showData_2(list.get(position), holderTwo, position);
                final InforItemDetail bean2 = list.get(position);
                holderTwo.item2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("activityId", bean2.getId());
                        intent.setClass(context, InformationAtlasActivity.class);
                        context.startActivity(intent);
                    }
                });
                break;
            case FOOTER_TYPE:
                FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                removeView.removeView(footerViewHolder.footerView);
                break;
            case HEADER_TYPE:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.bannerView.refreshData(bannerList);
                headerViewHolder.bannerView.setScrollDelayTime(3000);
                headerViewHolder.bannerView.startAutoScroll();
                break;
            default:
                ViewHolderOne holderOne = (ViewHolderOne) holder;
                showData_1(list.get(position), holderOne, position);
                final InforItemDetail bean = list.get(position);
                holderOne.item1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = null;
                        if (bean.getType() == 1) {//类型:1图文  跳转
                            intent = new Intent(context, SubjectActivity.class);
                            intent.putExtra("id", bean.getId() + "");
                            intent.putExtra("title", bean.getTitle());
                            intent.putExtra("icon", bean.getIcon());
                            intent.putExtra("matchBrief", bean.getBrief());
                            intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.MATH);
                            context.startActivity(intent);
                        } else if (bean.getType() == 2) {//2专题  跳转
                            intent = new Intent();
                            intent.putExtra("activityId", bean.getId());
                            intent.putExtra("zhuanTitle", bean.getTitle());
                            intent.setClass(context, InformationTopicActivity.class);
                            context.startActivity(intent);
                        }
                    }
                });
                break;
        }
    }

    /**
     * 显示图文 或者专题
     *
     * @param bean
     * @param holder
     * @param position
     */
    private void showData_1(InforItemDetail bean, ViewHolderOne holder, int position) {//type	类型:1图文 2专题 3图集

        if (bean.getTitle() != null) {
            holder.title.setText(bean.getTitle());
        } else {
            holder.title.setText("");
        }
        if (bean.getBrief() != null) {
            holder.content.setText(bean.getBrief());
        } else {
            holder.content.setText("");
        }
        holder.yue.setText(getYueNum(bean.getRead_num()));
        if (bean.getType() == 2) {
            holder.zhuan.setVisibility(View.VISIBLE);
        } else {
            holder.zhuan.setVisibility(View.GONE);
        }

        if (bean.getIcon() != null) {
            AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + bean.getIcon() + "!small", holder.imageicon);
        } else {
            holder.imageicon.setImageResource(R.drawable.default_img);
        }

//        if (position == list.size() - 1) {
//            holder.last_line.setVisibility(View.GONE);
//        }
    }

    /**
     * 显示图集数据
     *
     * @param bean
     * @param viewHolder2
     * @param position
     */
    private void showData_2(InforItemDetail bean, ViewHolderTwo viewHolder2, int position) {//type	类型:1图文 2专题 3图集
        if (bean.getTitle() != null) {
            viewHolder2.tv_title.setText(bean.getTitle());
        } else {
            viewHolder2.tv_title.setText("");
        }
        viewHolder2.tv_yuedu.setText(getYueNum(bean.getRead_num()));
        String[] myImgList = null;
        if (bean.getImgs() != null) {
            myImgList = bean.getImgs().split(",");
            if (myImgList.length > 0) {
                viewHolder2.tv_num.setText(myImgList.length + picture);
            } else {
                viewHolder2.tv_num.setText(0 + picture);
            }
        }
        showImage(myImgList, viewHolder2);

//        if (position == list.size() - 1) {
//            viewHolder2.last_line2.setVisibility(View.GONE);
//        }
    }

    /**
     * 显示图片
     *
     * @param Str
     * @param hh
     */
    private void showImage(String[] Str, ViewHolderTwo hh) {
        if (Str == null || "".equals(Str) || Str.length == 0) {
            hh.fl_imgs_infor_fragment.setVisibility(View.GONE);
            return;
        }
        imageViews.clear();
        imageViews.add(hh.image1);
        imageViews.add(hh.image2);
        imageViews.add(hh.image3);
        hh.fl_imgs_infor_fragment.setVisibility(View.VISIBLE);
        int length;
        if (Str.length > 3) {
            length = 3;
        } else {
            length = Str.length;
        }

        for (ImageView myImg : imageViews) {
            myImg.setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < length; i++) {
            imageViews.get(i).setVisibility(View.VISIBLE);
            AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + Str[i] + "!small", imageViews.get(i));
        }
    }

    /**
     * 得到阅读数的2种形式 ： 6555阅    9.6万阅
     *
     * @param i
     * @return
     */
    private String getYueNum(int i) {
        String str = "";
        if (i < 10000) {
            str = i + read;
        } else {
            str = i % 1000 / 10 + wread;
        }
        return str;
    }

    /**
     * 专题、普通资讯界面的
     */
    class ViewHolderOne extends RecyclerView.ViewHolder {
        ImageView imageicon;
        TextView title;//标题
        TextView content;//内容
        TextView yue;//阅读量
        TextView zhuan;//是否是专题
        LinearLayout item1;

        public ViewHolderOne(View view) {
            super(view);
            item1 = (LinearLayout) view.findViewById(R.id.ll_item1_information);
            imageicon = (ImageView) view.findViewById(R.id.image_iv_item_infor_fragment);
            title = (TextView) view.findViewById(R.id.title_tv_item_infor_fragment);
            content = (TextView) view.findViewById(R.id.content_item_infor_fragment);
            yue = (TextView) view.findViewById(R.id.yue_tv_item_infor_fragment);
            zhuan = (TextView) view.findViewById(R.id.zhuan_tv_item_infor_fragment);
        }
    }

    /**
     * 图集界面的
     */
    class ViewHolderTwo extends RecyclerView.ViewHolder {
        LinearLayout item2;
        TextView tv_title;//标题
        ImageView image1;
        ImageView image2;
        ImageView image3;
        TextView tv_num;//显示几张图的数字
        TextView tv_yuedu;//阅读量
        FrameLayout fl_imgs_infor_fragment;

        public ViewHolderTwo(View view) {
            super(view);
            item2 = (LinearLayout) view.findViewById(R.id.ll_item2_information);
            tv_title = (TextView) view.findViewById(R.id.tv_buttom_title_infor_fragment);
            image1 = (ImageView) view.findViewById(R.id.iv1_infor_fragment);
            image2 = (ImageView) view.findViewById(R.id.iv2_infor_fragment);
            image3 = (ImageView) view.findViewById(R.id.iv3_infor_fragment);
            tv_num = (TextView) view.findViewById(R.id.tv_several_image_infor_fragment);
            tv_yuedu = (TextView) view.findViewById(R.id.tv_yue_several_image_infor_fragment);
            fl_imgs_infor_fragment = (FrameLayout) view.findViewById(R.id.fl_imgs_infor_fragment);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ProgressBar progressBar;
        RelativeLayout footerView;

        public FooterViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.footer_tv);
            progressBar = (ProgressBar) view.findViewById(R.id.footer_progress);
            footerView = (RelativeLayout) view.findViewById(R.id.footerView);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        InforHeadLinesView bannerView;//轮播图

        public HeaderViewHolder(View view) {
            super(view);
            bannerView = (InforHeadLinesView) view.findViewById(R.id.banner_infor_fragment);
            myBannerView = bannerView;
        }
    }

    @Override
    public void startPlay() {
        if (!bannerList.isEmpty() && myBannerView != null) {
            myBannerView.startAutoScroll();
        }
    }

    @Override
    public void stopPlay() {
        if (myBannerView != null) {
            myBannerView.stopAutoScroll();
        }
    }


    public interface RemoveView {
        void removeView(View view);
    }

    ;

    public RemoveView removeView;

    public void setRemoveView(RemoveView hideView) {
        this.removeView = hideView;
    }
}
