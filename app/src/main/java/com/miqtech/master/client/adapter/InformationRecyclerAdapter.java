package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Movie;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.FirstCommentDetail;
import com.miqtech.master.client.entity.InfoRecommend;
import com.miqtech.master.client.entity.InfoUpAndDown;
import com.miqtech.master.client.entity.InforItemDetail;
import com.miqtech.master.client.entity.Information;
import com.miqtech.master.client.entity.SecondCommentDetail;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.CommentsSectionActivity;
import com.miqtech.master.client.ui.InformationDetailActivity;
import com.miqtech.master.client.ui.LoginActivity;
import com.miqtech.master.client.ui.PersonalHomePageActivity;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GifLoader;
import com.miqtech.master.client.utils.ImageLoadingListenerAdapter;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CircleImageView;
import com.miqtech.master.client.view.GifView;
import com.miqtech.master.client.view.RoundProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by wuxn on 2016/4/13.
 */
public class InformationRecyclerAdapter extends RecyclerView.Adapter {
    private Context context;
    private LayoutInflater inflater;

    private static final int INFORMATION_CONTENT = 0;
    private static final int INFORMATION_TEXT_IAMGE_RECOMMEND = 1;
    private static final int INFORMATION_VIDEO_RECOMMEND = 2;
    private static final int INFORMATION_COMMENT = 3;
    private static final int INFORMATION_COMMENT_TITLE = 4;
    private static final int INFORMATION_COMMENT_MORE = 5;


    private MyClickListener mOnClickListener;
    private Information information;
    private volatile int progress = 0;
    private int type;
    private ArrayList<FirstCommentDetail> comments;

    private int current;//用来计数回复的条数
    private TextView tvReplyContent;
    private TextView tvReplytime;
    private TextView tvDelete;
    private LinearLayout reply_reply_comment_ll_item;
    private String replyColon = " 回复 ";
    private String colon = " : ";
    private User user;
    private boolean isFirst = true;


    public InformationRecyclerAdapter(Context context, Information information, MyClickListener mOnClickListener, int type, ArrayList<FirstCommentDetail> comments) {
        this.context = context;
        this.information = information;
        this.inflater = LayoutInflater.from(context);
        this.mOnClickListener = mOnClickListener;
        this.type = type;
        this.comments = comments;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view;
        switch (viewType) {
            case INFORMATION_CONTENT:
                view = inflater.inflate(R.layout.layout_informationpraise, parent, false);
                holder = new ContentViewHolder(view);
                break;
            case INFORMATION_TEXT_IAMGE_RECOMMEND:
                view = inflater.inflate(R.layout.information_listview_item_1, parent, false);
                view.setPadding(0, 0, 0, 0);
                view.setBackgroundDrawable(null);
                holder = new RecommendViewHolder(view);
                break;
            case INFORMATION_VIDEO_RECOMMEND:
                view = inflater.inflate(R.layout.information_recommend_view_item, parent, false);
                holder = new VideoRecommendViewHolder(view);
                break;
            case INFORMATION_COMMENT:
                view = inflater.inflate(R.layout.layout_comment_item, parent, false);
                holder = new CommentViewHolder(view);
                break;
            case INFORMATION_COMMENT_TITLE:
                view = inflater.inflate(R.layout.layout_infor_comment_title, parent, false);
                holder = new CommentTitleViewHolder(view);
                break;
            case INFORMATION_COMMENT_MORE:
                view = inflater.inflate(R.layout.layout_see_more_data, parent, false);
                holder = new CommentSeeMoreDataViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentViewHolder) {
            final ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
            if (information.getInfo() != null) {
                final InforItemDetail infoDetail = information.getInfo();
                setContentView(infoDetail, contentViewHolder);
                if (type == InformationDetailActivity.INFORMATION_IMAGE_TEXT_TYPE) {
                    setData(contentViewHolder.llContent, infoDetail.getRemark());
                } else if (type == InformationDetailActivity.INFORMATION_VIDEO) {
                    //            setVideoView();
                    //   Log.i("onBindViewHolder","url"+infoDetail.getVideo_url());
                    //播放地址不为空才显示播放页面
                    if (!TextUtils.isEmpty(infoDetail.getVideo_url())) {
                        contentViewHolder.rl__video_bg.setVisibility(View.VISIBLE);
                        AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + infoDetail.getIcon(), contentViewHolder.iv_video_pic);
                        contentViewHolder.iv_video_play_start.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mOnClickListener.videoListener(infoDetail.getVideo_url());
                                //    mOnClickListener.videoListener("http://img.wangyuhudong.com/uploads/video/test_005_hls_time10.m3u8");
                            }
                        });
                    }
                }
            }
            if (information.getUpDown() != null) {
                final InfoUpAndDown upAndDown = information.getUpDown();
                setPraiseView(upAndDown, contentViewHolder);
                String state = information.getUpDown().getState();
                if (state.equals("0")) {
                    //未操作
                    contentViewHolder.tvPraise.setBackgroundResource(R.drawable.information_praise);
                    contentViewHolder.tvPraise.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (WangYuApplication.getUser(context) != null) {
                                mOnClickListener.upOrDownListener(1);
                                setPriseImg("1", contentViewHolder);
                                setPraiseView(calculateUpOrDownScale("1"), contentViewHolder);
                                scaleViewAnim(v);
                            } else {
                                ((BaseActivity) context).showToast("请登录");
                                Intent intent = new Intent();
                                intent.setClass(context, LoginActivity.class);
                                ((BaseActivity) context).startActivityForResult(intent, LoginActivity.LOGIN_OK);
                            }
                        }
                    });
                    contentViewHolder.tvUnPraise.setBackgroundResource(R.drawable.information_unpraise);
                    contentViewHolder.tvUnPraise.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (WangYuApplication.getUser(context) != null) {
                                mOnClickListener.upOrDownListener(2);
                                setPriseImg("2", contentViewHolder);
                                setPraiseView(calculateUpOrDownScale("2"), contentViewHolder);
                                scaleViewAnim(v);
                            } else {
                                ((BaseActivity) context).showToast("请登录");
                                Intent intent = new Intent();
                                intent.setClass(context, LoginActivity.class);
                                ((BaseActivity) context).startActivityForResult(intent, LoginActivity.LOGIN_OK);
                            }
                        }
                    });

                } else if (state.equals("1")) {
                    //顶
                    contentViewHolder.tvPraise.setBackgroundResource(R.drawable.information_praised);
                    contentViewHolder.tvUnPraise.setBackgroundResource(R.drawable.information_unpraise);
                } else if (state.equals("2")) {
                    //踩
                    contentViewHolder.tvPraise.setBackgroundResource(R.drawable.information_praise);
                    contentViewHolder.tvUnPraise.setBackgroundResource(R.drawable.information_unpraised);
                }
            }
        } else if (holder instanceof RecommendViewHolder) {
            final InfoRecommend recommend;
            RecommendViewHolder recommendViewHolder = (RecommendViewHolder) holder;
            List<InfoRecommend> recommends = information.getRecommend();
            if (information.getInfo() != null && position != 0) {
                recommend = recommends.get(position - 1);
                setRecommendView(recommendViewHolder, recommend);
            } else {
                recommend = recommends.get(position);
                setRecommendView(recommendViewHolder, recommend);
            }
            recommendViewHolder.llContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.recommendListener(recommend);

                }
            });

        } else if (holder instanceof VideoRecommendViewHolder) {
            final InfoRecommend recommend;
            VideoRecommendViewHolder videoRecommendViewHolder = (VideoRecommendViewHolder) holder;
            List<InfoRecommend> recommends = information.getRecommend();
            if (information.getInfo() != null && position != 0) {
                recommend = recommends.get(position - 1);
                setVideoRecommendView(videoRecommendViewHolder, recommend);
            } else {
                recommend = recommends.get(position);
                setVideoRecommendView(videoRecommendViewHolder, recommend);
            }
            videoRecommendViewHolder.llContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.recommendListener(recommend);
                }
            });
        } else if (holder instanceof CommentViewHolder) {
            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            commentViewHolder.comment_top_title_ll.setVisibility(View.GONE);
            commentViewHolder.line.setVisibility(View.VISIBLE);
            //第二个position要重新算过

            //推荐的长度，-1是因为要减去评论title item
            if (information.getInfo() != null) {
                showCommentData(commentViewHolder, position - 1, comments, (position - 1) - information.getRecommend().size() - 1);
            } else {
                showCommentData(commentViewHolder, position, comments, position - information.getRecommend().size() - 1);
            }
        } else if (holder instanceof CommentSeeMoreDataViewHolder) {
            CommentSeeMoreDataViewHolder commentSeeMoreDataViewHolder = (CommentSeeMoreDataViewHolder) holder;
            if (!comments.isEmpty()) {
                commentSeeMoreDataViewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (information != null) {
                            Intent intent = new Intent();
                            intent.setClass(context, CommentsSectionActivity.class);
                            intent.putExtra("amuseId", information.getInfo().getId() + "");
                            intent.putExtra("type", 3);
                            context.startActivity(intent);
                        }
                    }
                });
            } else {
                commentSeeMoreDataViewHolder.tvTitle.setVisibility(View.GONE);
            }
        }
    }

    private void setPriseImg(String type, ContentViewHolder contentViewHolder) {
        if (type.equals("1")) {
            //顶
            contentViewHolder.tvPraise.setBackgroundResource(R.drawable.information_praised);
            contentViewHolder.tvUnPraise.setBackgroundResource(R.drawable.information_unpraise);

        } else if (type.equals("2")) {
            //踩
            contentViewHolder.tvPraise.setBackgroundResource(R.drawable.information_praise);
            contentViewHolder.tvUnPraise.setBackgroundResource(R.drawable.information_unpraised);
        }
        contentViewHolder.tvPraise.setOnClickListener(null);
        contentViewHolder.tvUnPraise.setOnClickListener(null);
    }

    /**
     * 点赞后的比例
     *
     * @param type 类型
     */
    private InfoUpAndDown calculateUpOrDownScale(String type) {
        InfoUpAndDown upDown = information.getUpDown();
        int upPercent = upDown.getUpPercent();
        int downPercent = upDown.getDownPercent();
        int downTotal = upDown.getDownTotal();
        int upTotal = upDown.getUpTotal();

        if (type.equals("1")) {
            upTotal = upTotal + 1;
            upDown.setState("1");
        } else if (type.equals("2")) {
            downTotal = downTotal + 1;
            upDown.setState("2");
        }

        NumberFormat numberFormat = NumberFormat.getInstance();

        // 设置精确到小数点后2位

        numberFormat.setMaximumFractionDigits(2);

        String result = numberFormat.format((float) upTotal / (float) (upTotal + downTotal) * 100);

        if (!TextUtils.isEmpty(result)) {
            //upPercent = Integer.parseInt(result);
            upPercent = (int) Double.parseDouble(result);
            downPercent = 100 - upPercent;
        }
        upDown.setDownPercent(downPercent);
        upDown.setUpPercent(upPercent);
        upDown.setDownTotal(downTotal);
        upDown.setUpTotal(upTotal);
        information.setUpDown(upDown);
        return upDown;
    }

    private void scaleViewAnim(View view) {
        Animation animPraise = AnimationUtils.loadAnimation(context, R.anim.anim_praise);
        view.setAnimation(animPraise);
        view.startAnimation(animPraise);
    }

    private void setRecommendView(RecommendViewHolder recommendViewHolder, InfoRecommend recommend) {
        AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + recommend.getIcon(), recommendViewHolder.ivRecommend);
        recommendViewHolder.tvTitle.setText(recommend.getTitle());
        recommendViewHolder.tvContent.setText(recommend.getBrief());
        recommendViewHolder.tvReadNum.setText(recommend.getRead_num() + "阅");
    }

    private void setVideoRecommendView(VideoRecommendViewHolder videoRecommendViewHolder, InfoRecommend recommend) {
        AsyncImage.loadPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + recommend.getIcon(), videoRecommendViewHolder.ivVideo);
        videoRecommendViewHolder.tvTitle.setText(recommend.getTitle());
        videoRecommendViewHolder.tvPlayNum.setText(recommend.getRead_num() + "");
    }

    private void setContentView(InforItemDetail infoDetail, ContentViewHolder contentViewHolder) {
        contentViewHolder.tvTitle.setText(infoDetail.getTitle());
        contentViewHolder.tvDate.setText(infoDetail.getTimer_date());
        contentViewHolder.tvReadNum.setText(infoDetail.getRead_num() + "阅");
        contentViewHolder.tvSource.setText(infoDetail.getSource());
    }

//    private void setHtmlView(final TextView textView, final String content) {
//        textView.setClickable(true);
//        textView.setMovementMethod(LinkMovementMethod.getInstance());
//        final Handler handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                // TODO Auto-generated method stub
//                if (msg.what == 0x101) {
//                    textView.setText((CharSequence) msg.obj);
//                }
//                super.handleMessage(msg);
//            }
//        };
//        // 因为从网上下载图片是耗时操作 所以要开启新线程
//        Thread t = new Thread(new Runnable() {
//            Message msg = Message.obtain();
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                /**
//                 * 要实现图片的显示需要使用Html.fromHtml的一个重构方法：public static Spanned
//                 * fromHtml (String source, Html.ImageGetterimageGetter,
//                 * Html.TagHandler
//                 * tagHandler)其中Html.ImageGetter是一个接口，我们要实现此接口，在它的getDrawable
//                 * (String source)方法中返回图片的Drawable对象才可以。
//                 */
//                Html.ImageGetter imageGetter = new Html.ImageGetter() {
//                    @Override
//                    public Drawable getDrawable(String source) {
//                        // TODO Auto-generated method stub
//                        URL url;
//                        Drawable drawable = null;
//                        float viewWidth = WangYuApplication.WIDTH - context.getResources().getDimension(R.dimen.activity_horizontal_margin) * 2;
//                        try {
//                            url = new URL(source);
//                            drawable = Drawable.createFromStream(
//                                    url.openStream(), null);
//                            int height = drawable.getIntrinsicHeight();
//                            int width = drawable.getIntrinsicWidth();
//                            float scale = (float) height / width;
//                            drawable.setBounds(0, 0,
//                                    (int) viewWidth,
//                                    (int) ((int) viewWidth * scale));
//                        } catch (MalformedURLException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                        return drawable;
//                    }
//                };
//                CharSequence test = Html.fromHtml(content, imageGetter, InformationRecyclerAdapter.this);
//                msg.what = 0x101;
//                msg.obj = test;
//                handler.sendMessage(msg);
//            }
//        });
//        t.start();
//    }

    private void setPraiseView(final InfoUpAndDown upDown, final ContentViewHolder contentViewHolder) {
        progress = 0;
        contentViewHolder.tvPraiseNum.setText(upDown.getUpTotal() + "(" + upDown.getUpPercent() + "%" + ")");
        contentViewHolder.tvUnPraiseNum.setText(upDown.getDownTotal() + "(" + upDown.getDownPercent() + "%" + ")");
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (progress < upDown.getUpPercent()) {
                    progress += 3;

                    contentViewHolder.rpbScale.setProgress(progress);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    @Override
    public int getItemCount() {
        int informationContentNum = 0;
        int informationRecommendNum = 0;
        int commentTitle = 1;
        int commentEnd = 0;
        if (information != null) {
            if (information.getInfo() != null) {
                informationContentNum = 1;
            }
            if (information.getRecommend() != null) {
                informationRecommendNum = information.getRecommend().size();
            }
            if (!comments.isEmpty()) {
                commentEnd = 1;
            }
            return informationContentNum + informationRecommendNum + comments.size() + commentTitle + commentEnd;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (information.getInfo() != null) {
            if (position == 0) {
                return INFORMATION_CONTENT;
            } else if (position <= information.getRecommend().size()) {
                if (type == InformationDetailActivity.INFORMATION_IMAGE_TEXT_TYPE) {
                    return INFORMATION_TEXT_IAMGE_RECOMMEND;
                } else {
                    return INFORMATION_VIDEO_RECOMMEND;
                }
            } else if (position == information.getRecommend().size() + 1) {
                return INFORMATION_COMMENT_TITLE;
            } else {
                if (!comments.isEmpty() && position == information.getRecommend().size() + 1 + comments.size() + 1) {
                    return INFORMATION_COMMENT_MORE;
                }
                return INFORMATION_COMMENT;
            }
        } else {
            if (position <= information.getRecommend().size()) {
                if (type == InformationDetailActivity.INFORMATION_IMAGE_TEXT_TYPE) {
                    return INFORMATION_TEXT_IAMGE_RECOMMEND;
                } else {
                    return INFORMATION_VIDEO_RECOMMEND;
                }
            } else if (position == information.getRecommend().size() + 1) {
                return INFORMATION_COMMENT_TITLE;
            } else {
                if (!comments.isEmpty() && position == information.getRecommend().size() + 1 + comments.size() + 1) {
                    return INFORMATION_COMMENT_MORE;
                }
                return INFORMATION_COMMENT;
            }
        }
    }


    private class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvPraise, tvUnPraise, tvPraiseNum, tvUnPraiseNum, tvReadNum, tvSource;
        LinearLayout llContent;
        RoundProgressBar rpbScale;
        RelativeLayout rl__video_bg;
        RelativeLayout rl_title_and_time;
        ImageView iv_video_play_start, iv_video_pic;

        public ContentViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            llContent = (LinearLayout) itemView.findViewById(R.id.llContent);
            tvPraise = (TextView) itemView.findViewById(R.id.tvPraise);
            tvUnPraise = (TextView) itemView.findViewById(R.id.tvUnPraise);
            tvPraiseNum = (TextView) itemView.findViewById(R.id.tvPraiseNum);
            tvUnPraiseNum = (TextView) itemView.findViewById(R.id.tvUnPraiseNum);
            rpbScale = (RoundProgressBar) itemView.findViewById(R.id.rpbScale);
            tvReadNum = (TextView) itemView.findViewById(R.id.tvReadNum);
            rl__video_bg = (RelativeLayout) itemView.findViewById(R.id.rl__video_bg);
            rl_title_and_time = (RelativeLayout) itemView.findViewById(R.id.rl_title_and_time);
            iv_video_play_start = (ImageView) itemView.findViewById(R.id.iv_video_play_start);
            iv_video_pic = (ImageView) itemView.findViewById(R.id.iv_video_pic);
            tvSource = (TextView) itemView.findViewById(R.id.tvSource);
        }
    }

    private class RecommendViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecommend;
        TextView tvTitle, tvContent, tvReadNum;
        LinearLayout llContent;


        public RecommendViewHolder(View itemView) {
            super(itemView);
            ivRecommend = (ImageView) itemView.findViewById(R.id.image_iv_item_infor_fragment);
            tvTitle = (TextView) itemView.findViewById(R.id.title_tv_item_infor_fragment);
            tvContent = (TextView) itemView.findViewById(R.id.content_item_infor_fragment);
            tvReadNum = (TextView) itemView.findViewById(R.id.yue_tv_item_infor_fragment);
            llContent = (LinearLayout) itemView.findViewById(R.id.ll_item1_information);
        }
    }

    private class VideoRecommendViewHolder extends RecyclerView.ViewHolder {
        ImageView ivVideo;
        TextView tvTitle, tvPlayNum;
        LinearLayout llContent;


        public VideoRecommendViewHolder(View itemView) {
            super(itemView);
            ivVideo = (ImageView) itemView.findViewById(R.id.ivVideo);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvPlayNum = (TextView) itemView.findViewById(R.id.tvPlayNum);
            llContent = (LinearLayout) itemView.findViewById(R.id.llContent);
        }
    }

    private class CommentTitleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public CommentTitleViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }

    private class CommentSeeMoreDataViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public CommentSeeMoreDataViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_see_more_data);
        }
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        LinearLayout comment_top_title_ll;//顶部热门评价这几个字栏
        TextView comment_top_title_tv;
        CircleImageView ivUserHeader;//头像
        TextView tvUserName;//名字
        TextView tvContent;//评论内容
        TextView tvTime;//时间
        TextView tvDelect;//删除

        LinearLayout praiseLl;//赞
        TextView praiseTv;//赞的显示数量
        ImageView praiseIv;//赞的图标

        LinearLayout repltLl;//回复

        LinearLayout replyReplyLl;//楼中楼

        LinearLayout commentDetailLl;//评论的内容（包括头像，名称等等）

        View line;//底下的那条线


        public CommentViewHolder(View view) {
            super(view);
            comment_top_title_ll = (LinearLayout) view.findViewById(R.id.comment_top_title_ll_item);
            comment_top_title_tv = (TextView) view.findViewById(R.id.comment_top_title_tv_item);
            ivUserHeader = (CircleImageView) view.findViewById(R.id.ivUserHeader);
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);
            tvContent = (TextView) view.findViewById(R.id.tvContent);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            tvDelect = (TextView) view.findViewById(R.id.tvDelect);

            praiseLl = (LinearLayout) view.findViewById(R.id.praise_comment_ll_item);
            praiseTv = (TextView) view.findViewById(R.id.praise_comment_tv_item);
            praiseIv = (ImageView) view.findViewById(R.id.praise_comment_iv_item);

            repltLl = (LinearLayout) view.findViewById(R.id.reply_comment_ll_item);

            replyReplyLl = (LinearLayout) view.findViewById(R.id.reply_reply_comment_ll_item);

            commentDetailLl = (LinearLayout) view.findViewById(R.id.comment_detail_ll_item);

            line = view.findViewById(R.id.line_view_comment_item);
        }
    }

    private ArrayList<Element> getParseData(String html) {
        if (!TextUtils.isEmpty(html)) {
            Document doc = Jsoup.parse(html);
            Elements elements = doc.getElementsByTag("p");
            ArrayList<Element> list = new ArrayList<Element>();
            for (Element e : elements) {
                list.add(e);
            }
            return list;
        }
        return null;
    }

    public void setVideoView(final LinearLayout llContent, String html) {

    }

    //图文混编样式
    public void setData(final LinearLayout llContent, String html) {
        if (llContent.getChildCount() == 0) {
            ArrayList<Element> list = getParseData(html);
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    Element element = list.get(i);
                    if (element.hasText()) {
                        String a = element.text();
                        Elements elementSpan = element.getElementsByTag("span");
                        String style = elementSpan.attr("style");
                        setStyle(style);
                        TextView t1 = new TextView(context);
                        t1.setText("\n" + element.text() + "\n");
                        t1.setTextColor(context.getResources().getColor(R.color.font_black));
                        llContent.addView(t1);
                    } else {
                        //                Elements embed = element.getElementsByTag("embed");
                        Elements elementImg = element.getElementsByTag("img");
//                    if (!TextUtils.isEmpty(embed.toString())) {
//                        final String src = embed.attr("src");
//                        if (!TextUtils.isEmpty(src)) {
//                            final ImageView videoView = new ImageView(context);
//                            llContent.addView(videoView);
//                            AsyncImage.loadNetPhotoWithListener(src, videoView, new ImageLoadingListenerAdapter() {
//                                @Override
//                                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                                    Drawable drawable = new BitmapDrawable(bitmap);
//                                    float viewWidth = WangYuApplication.WIDTH - context.getResources().getDimension(R.dimen.activity_horizontal_margin) * 2;
//                                    int height = drawable.getIntrinsicHeight();
//                                    int width = drawable.getIntrinsicWidth();
//                                    float scale = (float) height / width;
//                                    ViewGroup.LayoutParams lp = videoView.getLayoutParams();
//                                    lp.width = (int) viewWidth;
//                                    lp.height = (int) ((int) viewWidth * scale);
//                                }
//                            });
//                            videoView.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    mOnClickListener.videoListener(src
//                                    );
//                                }
//                            });
//                        }
//                    } else
                        if (elementImg != null) {
                            for (int j = 0; j < elementImg.size(); j++) {
                                Element img = elementImg.get(j);
                                final String src = img.attr("src");
                                //src = "http://p2.pstatp.com/large/5c20009323c2594a940";
                                if (!TextUtils.isEmpty(src)) {
                                    final GifView gifView = new GifView(context);
                                    llContent.addView(gifView);
                                    final float viewWidth = WangYuApplication.WIDTH - context.getResources().getDimension(R.dimen.activity_horizontal_margin) * 2;
                                    final GifLoader gifLoader = GifLoader.getInstance();
                                    try {
                                        gifLoader.getGifMovieFromHttp(src, new GifLoader.OnGifLoaderListerner() {
                                            @Override
                                            public void onGifLoader(Movie bytes, String url) {
                                                if (bytes != null) {
                                                    gifView.setViewAttributes();
                                                    gifView.setGifMovie(bytes, viewWidth);
                                                } else {
                                                    AsyncImage.loadNetPhotoWithListener(src, gifView, new ImageLoadingListenerAdapter() {
                                                        @Override
                                                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                                            Drawable drawable = new BitmapDrawable(bitmap);
                                                            float viewWidth = WangYuApplication.WIDTH - context.getResources().getDimension(R.dimen.activity_horizontal_margin) * 2;
                                                            int height = drawable.getIntrinsicHeight();
                                                            int width = drawable.getIntrinsicWidth();
                                                            float scale = (float) height / width;
                                                            ViewGroup.LayoutParams lp = gifView.getLayoutParams();
                                                            lp.width = (int) viewWidth;
                                                            lp.height = (int) ((int) viewWidth * scale);
                                                            gifView.setLayoutParams(lp);
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                    }
                }
            }

        }


    }

    //设置Text样式
    private void setStyle(String style) {
        if (!TextUtils.isEmpty(style)) {
            String[] styleArray = style.split(";");
            ArrayList<String> list = new ArrayList<String>();
            Collections.addAll(list, styleArray);
            Log.e("list", list.toString());
        }
    }


    public interface MyClickListener {
        void upOrDownListener(int type);

        void recommendListener(InfoRecommend recommend);

        void imageListener();

        void videoListener(String videoUrl);
    }

    /**
     * 显示评价的数据
     *
     * @param myHolder 布局ViewHolder
     * @param position 显示的位置
     * @param list     数据
     * @param getid    在数据中的位置
     */
    private void showCommentData(CommentViewHolder myHolder, int position, List<FirstCommentDetail> list, int getid) {
        if (list.isEmpty() && list.size() == 0) {
            return;
        }
        Log.i("information", "showCommentData" + list.size() + "::" + getid);
        FirstCommentDetail bean = list.get(getid);
        String strDate;

        //显示头像
        if (!TextUtils.isEmpty(bean.getIcon())) {
            AsyncImage.loadNetPhoto(context, HttpConstant.SERVICE_UPLOAD_AREA + bean.getIcon() + "!small", myHolder.ivUserHeader);
        } else {
            myHolder.ivUserHeader.setImageResource(R.drawable.default_head);
        }

        //显示昵称
        if (!TextUtils.isEmpty(bean.getNickname())) {
            myHolder.tvUserName.setText(bean.getNickname());
        } else {
            myHolder.tvUserName.setText("");
        }

        //显示评论内容
        if (!TextUtils.isEmpty(bean.getContent())) {
            myHolder.tvContent.setText(bean.getContent());
        } else {
            myHolder.tvContent.setText("");
        }

        //显示时间
        if (!TextUtils.isEmpty(bean.getCreateDate())) {
            strDate = TimeFormatUtil.friendlyTime(bean.getCreateDate());
            myHolder.tvTime.setText(strDate);
        } else {
            myHolder.tvTime.setText("");
        }

        //判断是否显示删除按钮
        if (user != null && user.getId().equals(bean.getUserId() + "")) {
            myHolder.tvDelect.setVisibility(View.VISIBLE);
        } else {
            myHolder.tvDelect.setVisibility(View.INVISIBLE);
        }

        //显示是否评论的状态
        if (bean.getIsPraise() == 0) {
            myHolder.praiseIv.setImageResource(R.drawable.comment_praise_no);
        } else if (bean.getIsPraise() == 1) {
            myHolder.praiseIv.setImageResource(R.drawable.comment_praise_yes);
        }

        //显示点赞数
        myHolder.praiseTv.setText(Utils.getnumberForms(bean.getLikeCount(), context));

        AddReplyReplyView(myHolder.replyReplyLl, position, list, getid);

        //设置监听事件
        MyOnCliclLister myOnCliclLister = new MyOnCliclLister(position, myHolder, list, getid);
        myHolder.repltLl.setOnClickListener(myOnCliclLister);//回复
        myHolder.tvDelect.setOnClickListener(myOnCliclLister);//删除
        myHolder.praiseLl.setOnClickListener(myOnCliclLister);//赞
        myHolder.ivUserHeader.setOnClickListener(myOnCliclLister);//头像
        myHolder.tvContent.setOnClickListener(myOnCliclLister); //点击文字区域 跳到详情
    }

    /**
     * 添加楼中楼的评论
     *
     * @param linearLayout
     * @param position     recyclerview中的位置
     * @param getid        在数据中的位置
     */
    private void AddReplyReplyView(LinearLayout linearLayout, int position, List<FirstCommentDetail> list, int getid) {
        linearLayout.removeAllViews();
        if (list.isEmpty() || list.get(getid).getReplyList() == null || list.get(getid).getReplyList().isEmpty() || linearLayout == null) {
            linearLayout.setVisibility(View.GONE);
            return;
        }
        linearLayout.setVisibility(View.VISIBLE);
        //5条以内，不显示“还有更多”，否则显示
        if (list.get(getid).getReplyCount() < 6) {
            current = list.get(getid).getReplyList().size();
        } else {
            if (list.get(getid).getReplyList().size() > 5) {
                current = 6;
            } else {
                current = list.get(getid).getReplyList().size() + 1;
            }
        }

        for (int i = 0; i < current; i++) {
            linearLayout.addView(creatReplyReplyView(getid, list, i, position));
        }
    }

    /**
     * 创建楼中楼
     *
     * @param getid        在数据中的位置
     * @param list         数据
     * @param isShowButtom
     * @param position     在屏幕中的位置
     * @return
     */
    private View creatReplyReplyView(final int getid, final List<FirstCommentDetail> list, final int isShowButtom, final int position) {

        View view = null;
        view = inflater.inflate(R.layout.layout_item_reply_comment, null);
        reply_reply_comment_ll_item = (LinearLayout) view.findViewById(R.id.reply_reply_comment_ll_item);//最外层布局的
        tvReplyContent = (TextView) view.findViewById(R.id.comment_details_item);//显示的评论
        tvReplytime = (TextView) view.findViewById(R.id.comment_time_item);//显示的时间
        tvDelete = (TextView) view.findViewById(R.id.comment_time_delete);//删除

        if (isShowButtom < 5 && isShowButtom < list.get(getid).getReplyList().size()) {
            final SecondCommentDetail bean = list.get(getid).getReplyList().get(isShowButtom);
            tvReplyContent.setGravity(Gravity.LEFT);
            if (!TextUtils.isEmpty(bean.getNickname()) && !TextUtils.isEmpty(bean.getContent())) {
                tvReplyContent.setText(addconnent(bean));
                tvReplyContent.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                tvReplyContent.setText("");
            }

            //显示时间
            if (!TextUtils.isEmpty(bean.getCreateDate())) {
                String strDate = TimeFormatUtil.friendlyTime(bean.getCreateDate());
                tvReplytime.setText(strDate);
            } else {
                tvReplytime.setText("");
            }

            user = WangYuApplication.getUser(context);
            if (user != null && user.getId().equals(bean.getUserId() + "")) {
                tvDelete.setVisibility(View.VISIBLE);
                tvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processTheData.deleteReplyReply(bean.getId(), getid, isShowButtom);
                    }
                });
            } else {
                tvDelete.setVisibility(View.GONE);
            }
        } else {
            tvReplyContent.setText("更多" + Utils.getnumberForms((list.get(getid).getReplyCount() - 5), context) + "条回复。。。");
            tvReplyContent.setTextColor(context.getResources().getColor(R.color.orange));
            tvReplyContent.setGravity(Gravity.RIGHT);
            tvReplytime.setVisibility(View.GONE);
            tvReplyContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processTheData.lookComment(list.get(getid).getId(), position, list.get(getid).getNickname());
                }
            });
        }

        return view;
    }

    /**
     * 显示楼中楼
     * 当replyName为空时表示回复楼主，不为空时表示回复层主
     *
     * @return 拼接好的内容
     */
    private SpannableStringBuilder addconnent(final SecondCommentDetail bean) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(bean.getNickname());
        int start = bean.getNickname().length();
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(context, PersonalHomePageActivity.class);
                intent.putExtra("id", bean.getUserId() + "");
                context.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(R.color.blue_comment_title));// 设置文本颜色
                // 去掉下划线
                ds.setUnderlineText(false);
            }
        }, 0, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (bean.getParent_id() != bean.getReply_id() && !TextUtils.isEmpty(bean.getReplyname())) {
            int end = start + replyColon.length() + bean.getReplyname().length();
            ssb.append(replyColon + bean.getReplyname());

            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(context, PersonalHomePageActivity.class);
                    intent.putExtra("id", bean.getReplyUserId() + "");
                    context.startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(context.getResources().getColor(R.color.blue_comment_title));// 设置文本颜色
                    // 去掉下划线
                    ds.setUnderlineText(false);
                }
            }, start + replyColon.length(), end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return ssb.append(colon + bean.getContent());
    }

    private class MyOnCliclLister implements View.OnClickListener {
        int position;
        CommentViewHolder viewHolder;
        List<FirstCommentDetail> mylist;
        int getid;

        public MyOnCliclLister(int position, CommentViewHolder viewHolder, List<FirstCommentDetail> mylist, int getid) {
            this.position = position;
            this.viewHolder = viewHolder;
            this.mylist = mylist;
            this.getid = getid;
        }

        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.reply_comment_ll_item://回复  回复楼主
                    processTheData.replyComment(mylist.get(getid).getId(), position, mylist.get(getid).getNickname());
                    break;
                case R.id.tvContent://点击文字区域跳转  单个评论详情
                    processTheData.lookComment(mylist.get(getid).getId(), position, mylist.get(getid).getNickname());
                    break;
                case R.id.tvDelect://删除评论
                    processTheData.delectComment(mylist.get(getid).getId(), getid);
                    break;
                case R.id.praise_comment_ll_item://点赞
                    processTheData.praiseComment(mylist.get(getid).getId(), position);
                    break;
                case R.id.ivUserHeader://点击头像跳转
                    intent = new Intent(context, PersonalHomePageActivity.class);
                    intent.putExtra("id", comments.get(getid).getUserId() + "");
                    context.startActivity(intent);
                    break;
            }
        }
    }

    /**
     * 处理数据
     */
    public interface ProcessTheData {
        /**
         * 删除数据
         *
         * @param id       该条数据的id
         * @param position 该条数据的位置
         */
        void delectComment(String id, int position);

        /**
         * 点赞或者取消
         *
         * @param id       该条数据的id
         * @param position 该条评论的位置
         */
        void praiseComment(String id, int position);

        /**
         * 回复楼主
         *
         * @param id       该条评论的id
         * @param position 该条评论的位置
         * @param nikeName 该条评论的昵称
         */
        void replyComment(String id, int position, String nikeName);

        /**
         * 查看
         *
         * @param id       该条评论的id
         * @param position 该条评论的位置
         * @param nikeName 该条评论的昵称
         */
        void lookComment(String id, int position, String nikeName);

        /**
         * 删除楼中楼
         *
         * @param id            楼中楼ID
         * @param position      该条评论的位子
         * @param replyPosition 楼主楼的位置
         */
        void deleteReplyReply(String id, int position, int replyPosition);

    }


    public ProcessTheData processTheData;

    public void setProcessTheData(ProcessTheData processTheData) {
        this.processTheData = processTheData;
    }
}
