package com.miqtech.master.client.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.InforBanner;
import com.miqtech.master.client.entity.InforItemDetail;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.BannerPagerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/11.
 */
public class InformationItemAdapter extends RecyclerView.Adapter {

    private final int VIEW_BANNER = 0;      //banner
    private final int VIEW_FOOTER = -1;         //底部加载更多
    private final int VIEW_INFORMATION = 1;    //资讯
    private final int VIEW_SPECIAL = 2;         //专题
    private final int VIEW_ATLAS = 3;        //图集
    private final int VIEW_VIDEO = 4;        //视频
    private final int VIEW_EMPTY = 5;   //为空的时候的界面

    private boolean mShowFooter = false;

    private List<InforItemDetail> mData;
    private List<InforBanner> mBanners;
    private Context mContext;
    private LayoutInflater mInflater;

    public InformationItemAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public List<InforItemDetail> getData() {
        return mData;
    }

    public void setData(List<InforItemDetail> mData) {
        this.mData = mData;
    }

    public void setBanner(List<InforBanner> banners) {
        this.mBanners = banners;
        mulriple = 0;
        initBannerCount();
    }

    private int mulriple = 0;

    private void initBannerCount() {
        while (mBanners != null && mBanners.size() > 0 && mBanners.size() < 4) {
            mulriple++;
            mBanners.addAll(mBanners);
        }
    }

    public List<InforBanner> getBanner() {
        return mBanners;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case VIEW_BANNER:
                view = mInflater.inflate(R.layout.layout_information_item_banner, parent, false);
                holder = new BannerHolder(view);
                break;
            case VIEW_FOOTER:
                view = mInflater.inflate(R.layout.layout_footer_view, parent, false);
                holder = new FooterHolder(view);
                break;
            case VIEW_INFORMATION:
                view = mInflater.inflate(R.layout.information_listview_item_1, parent, false);
                holder = new InformationHolder(view);
                break;
            case VIEW_SPECIAL:
                view = mInflater.inflate(R.layout.information_listview_item_1, parent, false);
                holder = new SpecialHolder(view);
                break;
            case VIEW_ATLAS:
                view = mInflater.inflate(R.layout.information_listview_item_2, parent, false);
                holder = new AtlasHolder(view);
                break;
            case VIEW_EMPTY:
                view = mInflater.inflate(R.layout.exception_page, parent, false);
                holder = new EmptyHolder(view);
                break;
            case VIEW_VIDEO:
                view = mInflater.inflate(R.layout.layout_info_video_item, parent, false);
                holder = new VideoItemHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BannerHolder) {
            initBanner((BannerHolder) holder, mBanners);
        } else if (holder instanceof InformationHolder) {   //图文，专题
            initInformationItem((InformationHolder) holder, (mBanners == null || mBanners.isEmpty() ? position : position - 1));
        } else if (holder instanceof SpecialHolder) {
            initInformationItem((SpecialHolder) holder, (mBanners == null || mBanners.isEmpty() ? position : position - 1));
        } else if (holder instanceof AtlasHolder) {
            initAtlasItem((AtlasHolder) holder, (mBanners == null || mBanners.isEmpty() ? position : position - 1));
        } else if (holder instanceof FooterHolder) {

        } else if (holder instanceof EmptyHolder) {
            ((EmptyHolder) holder).tvHint.setText("当前栏目还没更新资讯啦");
        } else if (holder instanceof VideoItemHolder) {
            setupVideo((VideoItemHolder) holder, (mBanners == null || mBanners.isEmpty() ? position : position - 1));
        }
    }

    private void setupVideo(VideoItemHolder holder, final int position) {
        InforItemDetail data = mData.get(position);
        //TODO:图片
        AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + data.getIcon(), holder.imgCover);
//        AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + Utils.transformUTF8(data.getIcon()), holder.imgCover);
        holder.tvCount.setText(Utils.calculate(data.getRead_num(), 10000, "万"));
        holder.tvTime.setText(data.getTime());
        holder.tvTitle.setText(data.getTitle());
        holder.tvLable.setText(data.getKeyword());
        holder.tvTitleSecond.setText(data.getBrief());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    /**
     * 图文信息
     *
     * @return
     */
    private void initInformationItem(BaseInformationHolder holder, final int position) {
        initTextInformationItem(holder, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    /**
     * 图文，专题信息
     *
     * @return
     */
    private void initTextInformationItem(BaseInformationHolder holder, int position) {
        InforItemDetail data = mData.get(position);
        if (data.getTitle() != null) {
            holder.title.setText(data.getTitle());
        } else {
            holder.title.setText("");
        }
        if (data.getBrief() != null) {
            holder.content.setText(data.getBrief());
        } else {
            holder.content.setText("");
        }
        holder.yue.setText(Utils.calculate(data.getRead_num(), 10000, "万") + "阅");
        if (data.getType() == 2) {
            holder.zhuan.setVisibility(View.VISIBLE);
        } else {
            holder.zhuan.setVisibility(View.GONE);
        }
        if (data.getIcon() != null) {
            //TODO:图片
            String ss = data.getIcon();
            String ss2 = Utils.transformUTF8(data.getIcon());
            AsyncImage.loadNetPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + data.getIcon() + "!small", holder.imageicon);
//            AsyncImage.loadNetPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + Utils.transformUTF8(data.getIcon() + "!small"), holder.imageicon);
        } else {
            holder.imageicon.setImageResource(R.drawable.default_img);
        }
    }

    /**
     * 图集
     *
     * @return
     */
    private void initAtlasItem(AtlasHolder holder, final int position) {
        InforItemDetail atlasitem = mData.get(position);
        if (atlasitem != null) {
            holder.tv_title.setText(atlasitem.getTitle());
            holder.tv_yuedu.setText(Utils.calculate(atlasitem.getRead_num(), 10000, "万") + "阅");
            String[] myImgList = null;
            if (atlasitem.getImgs() != null) {
                myImgList = atlasitem.getImgs().split(",");
                if (myImgList.length > 0) {
                    holder.tv_num.setText(myImgList.length + "图");
                } else {
                    holder.tv_num.setText(0 + "图");
                }
            }
            showImage(myImgList, holder);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, (position));
                }
            }
        });
    }

    /**
     * 显示图片
     */
    private void showImage(String[] urls, AtlasHolder holder) {
        if (urls == null || "".equals(urls) || urls.length == 0) {
            holder.fl_imgs_infor_fragment.setVisibility(View.GONE);
            return;
        }
        List<ImageView> imageViews = new ArrayList<>();
        imageViews.add(holder.image1);
        imageViews.add(holder.image2);
        imageViews.add(holder.image3);
        holder.fl_imgs_infor_fragment.setVisibility(View.VISIBLE);
        int length;
        if (urls.length > 3) {
            length = 3;
        } else {
            length = urls.length;
        }

        for (ImageView myImg : imageViews) {
            myImg.setVisibility(View.INVISIBLE);
        }
        for (int i = 0; i < length; i++) {
            imageViews.get(i).setVisibility(View.VISIBLE);
            //TODO:
            AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + urls[i] + "!small", imageViews.get(i));
//            AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + Utils.transformUTF8( urls[i]+ "!small") , imageViews.get(i));
        }
    }

    @Override
    public int getItemCount() {
        if ((mData == null || mData.isEmpty()) && (mBanners == null || mBanners.isEmpty())) {
            return 1;
        } else {
            int i = mShowFooter ? 1 : 0;
            if (mBanners != null && !mBanners.isEmpty()) {
                i += 1;
            }
            return mData.size() + i;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mBanners == null || mBanners.isEmpty()) {
            if (position == 0) {
                if (mData == null || mData.isEmpty()) {
                    return VIEW_EMPTY;
                } else {
                    return mData.get(position).getType();
                }
            } else if (mShowFooter && position == getItemCount() - 1) {
                return VIEW_FOOTER;
            } else {
                return mData.get(position).getType();
            }
        } else {
            if (position == 0) {
                return VIEW_BANNER;
            } else if (mShowFooter && position == getItemCount() - 1) {
                return VIEW_FOOTER;
            } else {
                return mData.get(position - 1).getType();
            }
        }
    }

    /**
     * banner
     */
    class BannerHolder extends RecyclerView.ViewHolder {
        BannerPagerView viewPager;
        LinearLayout llIndecator;
        TextView tvInforType;
        TextView tvInforTitle;

        public BannerHolder(View view) {
            super(view);
            viewPager = (BannerPagerView) view.findViewById(R.id.vp_banner);
            llIndecator = (LinearLayout) view.findViewById(R.id.ll_indicator);
            tvInforTitle = (TextView) view.findViewById(R.id.banner_extend_intro_infor_fragment);
            tvInforType = (TextView) view.findViewById(R.id.banner_extend_infor_fragment);
        }
    }

    /**
     * 图文资讯
     */
    class InformationHolder extends BaseInformationHolder {

        public InformationHolder(View view) {
            super(view);
        }
    }

    abstract class BaseInformationHolder extends RecyclerView.ViewHolder {
        ImageView imageicon;
        TextView title;//标题
        TextView content;//内容
        TextView yue;//阅读量
        TextView zhuan;//是否是专题

        public BaseInformationHolder(View view) {
            super(view);
            imageicon = (ImageView) view.findViewById(R.id.image_iv_item_infor_fragment);
            title = (TextView) view.findViewById(R.id.title_tv_item_infor_fragment);
            content = (TextView) view.findViewById(R.id.content_item_infor_fragment);
            yue = (TextView) view.findViewById(R.id.yue_tv_item_infor_fragment);
            zhuan = (TextView) view.findViewById(R.id.zhuan_tv_item_infor_fragment);
        }
    }

    /**
     * 图集
     */
    class AtlasHolder extends RecyclerView.ViewHolder {
        TextView tv_title;//标题
        ImageView image1;
        ImageView image2;
        ImageView image3;
        TextView tv_num;//显示几张图的数字
        TextView tv_yuedu;//阅读量
        FrameLayout fl_imgs_infor_fragment;

        public AtlasHolder(View view) {
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_buttom_title_infor_fragment);
            image1 = (ImageView) view.findViewById(R.id.iv1_infor_fragment);
            image2 = (ImageView) view.findViewById(R.id.iv2_infor_fragment);
            image3 = (ImageView) view.findViewById(R.id.iv3_infor_fragment);
            tv_num = (TextView) view.findViewById(R.id.tv_several_image_infor_fragment);
            tv_yuedu = (TextView) view.findViewById(R.id.tv_yue_several_image_infor_fragment);
            fl_imgs_infor_fragment = (FrameLayout) view.findViewById(R.id.fl_imgs_infor_fragment);
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;

    }

    /**
     * 专题
     */
    class SpecialHolder extends BaseInformationHolder {

        public SpecialHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 加载更多
     */
    class FooterHolder extends RecyclerView.ViewHolder {

        public FooterHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 显示底部加载更多
     */
    public void showFooter() {
        notifyItemInserted(getItemCount());
        mShowFooter = true;
    }

    /**
     * 隐藏底部加载更多
     */
    public void hideFooter() {
        notifyItemRemoved(getItemCount() - 1);
        mShowFooter = false;
    }

    private void initBanner(final BannerHolder holder, final List<InforBanner> banners) {
        if (banners == null) {
            return;
        }
        final List<View> bannerViews = new ArrayList<>();
        for (int i = 0; i < banners.size(); i++) {
            View view = mInflater.inflate(R.layout.layout_infomation_banner_item, null);
            ImageView imgCover = (ImageView) view.findViewById(R.id.img_banner_item);
            //TODO:图片
//            AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + banners.get(i).getCover(), imgCover);
            AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + Utils.transformUTF8(banners.get(i).getCover()), imgCover);
            final int position = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBannnerItemClickListener != null) {
                        mBannnerItemClickListener.onBannerItemClick(v, position);
                    }
                }
            });
            bannerViews.add(view);
        }
        if (banners.get(0).getType() == 1) {
            holder.tvInforType.setText(mContext.getResources().getString(R.string.information));
        } else if (banners.get(0).getType() == 2) {
            holder.tvInforType.setText(mContext.getResources().getString(R.string.topic));
        } else if (banners.get(0).getType() == 3) {
            holder.tvInforType.setText(mContext.getResources().getString(R.string.atlas));
        }
        if (banners.size() > 1) {//当banner超过1个时才进行自动滚动
            holder.viewPager.startAutoCycle();
        }
        holder.viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return banners == null ? 0 : Integer.MAX_VALUE;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager) container).removeView(bannerViews.get(position % bannerViews.size()));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ((ViewPager) container).addView(bannerViews.get(position % bannerViews.size()));
                return bannerViews.get(position % bannerViews.size());
            }


            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }
        });
        holder.viewPager.setCurrentItem(bannerViews.size() * 10);
        initDots(banners, holder, mulriple);
        holder.viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                try {
                    int selected = 0;
                    for (int i = 0; i < holder.llIndecator.getChildCount(); i++) {
                        if (i == position % holder.llIndecator.getChildCount()) {
                            holder.llIndecator.getChildAt(i).setSelected(true);
                            selected = i;
                        } else {
                            holder.llIndecator.getChildAt(i).setSelected(false);
                        }
                    }
                    InforBanner banner = banners.get(selected);
                    holder.tvInforTitle.setText(banner.getTitle());
                    if (banner.getType() == 1) {
                        holder.tvInforType.setText(mContext.getResources().getString(R.string.information));
                    } else if (banner.getType() == 2) {
                        holder.tvInforType.setText(mContext.getResources().getString(R.string.topic));
                    } else if (banner.getType() == 3) {
                        holder.tvInforType.setText(mContext.getResources().getString(R.string.atlas));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 随着图片的移动添加显示对应的点
     *
     * @param
     */
    private void initDots(List<InforBanner> list, BannerHolder holder, int mulriple) {
        if (list == null || list.isEmpty()) {
            return;
        }
        holder.llIndecator.removeAllViews();
        for (int i = 0; i * Math.pow(2, mulriple) < list.size(); i++) {
            holder.llIndecator.addView(initDot());
        }
        holder.llIndecator.getChildAt(0).setSelected(true);
        if (mBanners.size() > 0) {
            if (list.get(0).getType() == 1) {
                holder.tvInforTitle.setText(mContext.getResources().getString(R.string.information));
            } else if (list.get(0).getType() == 2) {
                holder.tvInforType.setText(mContext.getResources().getString(R.string.topic));
            } else if (list.get(0).getType() == 3) {
                holder.tvInforType.setText(mContext.getResources().getString(R.string.atlas));
            }
        }
        if (list.size() > 0) {
            holder.tvInforTitle.setText(list.get(0).getTitle());
        }
        if (mulriple == 2 && mBanners.size() == 4) {
            holder.viewPager.stopCycle();
        }
    }

    /**
     * 轮播图的点
     *
     * @return
     */
    private View initDot() {
        return mInflater.inflate(R.layout.headline_indecator, null);
    }

    /**
     * banner点击事件
     */
    public interface OnBannerItemClickListener {
        void onBannerItemClick(View view, int position);
    }

    private OnBannerItemClickListener mBannnerItemClickListener;

    public void setBannerItemClickListener(OnBannerItemClickListener listener) {
        this.mBannnerItemClickListener = listener;
    }

    class EmptyHolder extends RecyclerView.ViewHolder {
        TextView tvHint;
        ImageView imgException;

        public EmptyHolder(View itemView) {
            super(itemView);
            tvHint = (TextView) itemView.findViewById(R.id.tv_err_title);
            imgException = (ImageView) itemView.findViewById(R.id.noDataImage);
        }
    }

    class VideoItemHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvLable;
        TextView tvTitleSecond;
        TextView tvTitle;
        TextView tvTime;
        TextView tvCount;

        public VideoItemHolder(View itemView) {
            super(itemView);
            imgCover = (ImageView) itemView.findViewById(R.id.img_video_cover);
            tvLable = (TextView) itemView.findViewById(R.id.tv_lable);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvTime = (TextView) itemView.findViewById(R.id.tv_info_time);
            tvCount = (TextView) itemView.findViewById(R.id.tv_play_count);
            tvTitleSecond = (TextView) itemView.findViewById(R.id.tv_title_second);
        }
    }
}
