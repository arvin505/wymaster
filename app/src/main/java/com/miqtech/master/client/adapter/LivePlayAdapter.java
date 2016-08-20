package com.miqtech.master.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.LiveInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.LivePlayListActivity;
import com.miqtech.master.client.ui.LiveRoomActivity;
import com.miqtech.master.client.ui.LiveVideoListActivity;
import com.miqtech.master.client.ui.PlayVideoActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.Utils;
import com.miqtech.master.client.view.CircleImageView;
import com.miqtech.master.client.view.RoundedImageView;
import com.zhy.autolayout.utils.ScreenUtils;

import java.util.List;

/**
 * Created by admin on 2016/7/25.
 */
public class LivePlayAdapter extends RecyclerView.Adapter {
    private static final String TAG = "LivePlayAdapter";
    private List<LiveInfo> liveDatas; //直播列表数据
    private List<LiveInfo> videoDatas;//视频数据
    private Context mContext;
    private LayoutInflater inflater;

    private boolean isCanClick = true;


    public static final int VIEW_EMPTY = -1;
    public static final int VIEW_LIVE_TITLE = 1;
    public static final int VIEW_LIVE_ITEM = 2;
    public static final int VIEW_VIDEO_TITLE = 3;
    public static final int VIEW_VIDEO_ITEM = 4;

    private int type = 0; //0 直播列表标题和视屏列表标题都显示 1 隐藏直播标题 2 隐藏视频标题
    private boolean isHasNetwork=true;

    public LivePlayAdapter(Context context, int type, List<LiveInfo> liveDatas, List<LiveInfo> videoDatas) {
        this.type = type;
        this.liveDatas = liveDatas;
        this.videoDatas = videoDatas;
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
    }
    public void setNetWorkState(boolean isHasNetwork){
        this.isHasNetwork=isHasNetwork;
    }
    public void setIsCanClick(boolean isCanClick) {
        this.isCanClick = isCanClick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        LogUtil.d(TAG, "onCreateViewHolder" + viewType);
        if (viewType == VIEW_LIVE_TITLE) {
            view = inflater.inflate(R.layout.layout_livelist_title_view, null);
            holder = new LivePlayTitle(view);
        } else if (viewType == VIEW_LIVE_ITEM) {
            view = inflater.inflate(R.layout.layout_liveplay_item, null);
            holder = new LivePlayItemHolder(view);
        } else if (viewType == VIEW_VIDEO_TITLE) {
            view = inflater.inflate(R.layout.layout_livelist_title_view, null);
            holder = new LivePlayTitle(view);
        } else if (viewType == VIEW_VIDEO_ITEM) {
            view = inflater.inflate(R.layout.layout_video_item, null);
            holder = new VideoItemHolder(view);
        } else {
            view = inflater.inflate(R.layout.exception_page, parent, false);
            holder = new EmptyHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LivePlayTitle) {
            setTitle((LivePlayTitle) holder, position);
        } else if (holder instanceof LivePlayItemHolder) {
            setLivePlay((LivePlayItemHolder) holder, position);
        } else if (holder instanceof VideoItemHolder) {
            setupVideo((VideoItemHolder) holder, position);
        } else if (holder instanceof EmptyHolder) {
            if(isHasNetwork) {
                if (type == 0) {
                    ((EmptyHolder) holder).errTitle.setText("该栏目下没有视频和直播");
                } else if (type == 1) {
                    ((EmptyHolder) holder).errTitle.setText("直播被怪兽抓走了，我们正在全力营救!");
                } else if (type == 2) {
                    ((EmptyHolder) holder).errTitle.setText("该栏目下没有视频");
                }
            }else{
                ((EmptyHolder) holder).errTitle.setText("网络不给力,请检查网络再试试");
                isHasNetwork=true;
            }
        }
    }


    private void setTitle(LivePlayTitle holder, int position) {
        if ((liveDatas != null && !liveDatas.isEmpty()) && (videoDatas != null && videoDatas.isEmpty())) {
            if (position == 0) {
                holder.tvHotLivePlay.setText(mContext.getResources().getString(R.string.hot_liveplay));
                holder.tvHotLivePlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.live_play_more, 0, 0, 0);
                holder.tvMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 跳转到直播列表
                        Intent intent = new Intent(mContext, LivePlayListActivity.class);
                        mContext.startActivity(intent);
                    }
                });
            }
        } else if ((liveDatas != null && liveDatas.isEmpty()) && (videoDatas != null && !videoDatas.isEmpty())) {
            if (position == 0) {
                holder.tvHotLivePlay.setText(mContext.getResources().getString(R.string.hot_video));
                holder.tvHotLivePlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.video_more, 0, 0, 0);
                holder.rlTitle.setPadding(0, Utils.dp2px(9), 0, 0);
                holder.tvMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 跳转到视频列表
                        Intent intent = new Intent(mContext, LiveVideoListActivity.class);
                        mContext.startActivity(intent);
                    }
                });
            }
        } else if ((liveDatas != null && !liveDatas.isEmpty()) && (videoDatas != null && !videoDatas.isEmpty())) {
            if (position == 0) {
                holder.tvHotLivePlay.setText(mContext.getResources().getString(R.string.hot_liveplay));
                holder.tvHotLivePlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.live_play_more, 0, 0, 0);
                holder.tvMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 跳转到直播列表
                        Intent intent = new Intent(mContext, LivePlayListActivity.class);
                        mContext.startActivity(intent);
                    }
                });
            } else if (position == liveDatas.size() + 1) {
                LogUtil.d(TAG, "setTitle   热门视屏" + position + ":::::" + (liveDatas.size() + 1));
                holder.tvHotLivePlay.setText(mContext.getResources().getString(R.string.hot_video));
                holder.tvHotLivePlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.video_more, 0, 0, 0);
                holder.rlTitle.setPadding(0, 0, 0, 0);
                holder.tvMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 跳转到视频列表
                        Intent intent = new Intent(mContext, LiveVideoListActivity.class);
                        mContext.startActivity(intent);
                    }
                });
            }
        }
        //TODO 设置跳转事件
    }

    /**
     * 填充直播数据
     */
    private void setLivePlay(LivePlayItemHolder holder, int position) {
        if (liveDatas == null || (liveDatas != null && liveDatas.isEmpty())) {
            return;
        }
        LiveInfo data = null;
        if (type == 1) {
            data = liveDatas.get(position);
        } else {
            data = liveDatas.get(position - 1);
        }
        LogUtil.d(TAG, "setLivePlay" + (position - 1) + ":::::");
        if (data != null) {
            AsyncImage.loadPhoto(mContext, data.getIcon(), holder.ivLivePlayPoster);
            AsyncImage.loadAvatar(mContext, HttpConstant.SERVICE_UPLOAD_AREA + data.getUserIcon(), holder.anchorHeader);
            holder.tvTitle.setText(data.getNickname());
            holder.tvContent.setText(data.getTitle());
            holder.tvGameName.setText(data.getGameName());
            if (data.getSex() == 0) {
                holder.anchorSex.setImageResource(R.drawable.live_play_men);
            } else {
                holder.anchorSex.setImageResource(R.drawable.live_play_femen);
            }
            final LiveInfo finalData = data;
            holder.llLivePlayItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isCanClick) {
                        Intent intent = new Intent(mContext, LiveRoomActivity.class);
                        intent.putExtra("id", finalData.getId() + "");
                        intent.putExtra("rtmp",finalData.getRtmp());
                        mContext.startActivity(intent);
                        }

                }
            });
            if (type == 1) {
                if (position % 2 == 0) {
                    holder.llLivePlayItem.setPadding(Utils.dp2px(12), 0, Utils.dp2px(12), 0);
                } else {
                    holder.llLivePlayItem.setPadding(0, 0, Utils.dp2px(12), 0);
                }
                if (position == 0 || position == 1) {
                    holder.topView.setVisibility(View.VISIBLE);
                } else {
                    holder.topView.setVisibility(View.GONE);
                }
            } else {
                if (position % 2 == 1) {
                    holder.llLivePlayItem.setPadding(Utils.dp2px(12), 0, Utils.dp2px(12), 0);
                } else {
                    holder.llLivePlayItem.setPadding(0, 0, Utils.dp2px(12), 0);
                }
            }
            holder.ivLivePlayPoster.measure(-1, -1);
            final int width = (ScreenUtils.getScreenSize(mContext)[0] - Utils.dp2px(12) * 3) / 2;
            ViewGroup.LayoutParams lp = holder.ivLivePlayPoster.getLayoutParams();
            lp.height = width * 3 / 5;
            // 按一定的比例去显示图片高度
            holder.ivLivePlayPoster.setLayoutParams(lp);
        }

    }

    /**
     * 填充视频数据
     */
    private void setupVideo(VideoItemHolder holder, final int position) {
        LiveInfo data = null;
        if (videoDatas == null || (videoDatas != null && videoDatas.isEmpty())) {
            return;
        }

        if (liveDatas != null && !liveDatas.isEmpty()) {
            if (type == 2) {
                data = videoDatas.get(position);
            } else {
                data = videoDatas.get(position - liveDatas.size() - 2);
            }
        } else {
            //如果直播数据为空的时候
            if (type == 2) {
                data = videoDatas.get(position);
            } else {
                data = videoDatas.get(position - 1);
            }
        }
        LogUtil.d(TAG, "setupVideo" + (position - 1) + ":::::");
        if (data != null) {
            AsyncImage.loadPhoto(mContext,data.getIcon(), holder.ivVideoPic);
            holder.tvPlayNum.setText(Utils.calculate(data.getPlayTimes(), 10000, "万"));
            holder.tvVideoTitle.setText(data.getTitle());
            holder.tvVideoContent.setText(data.getNickname());
            holder.tvCommentNum.setText(Utils.calculate(data.getCommentNum(), 10000, "万"));
        }

        final LiveInfo finalData = data;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayVideoActivity.class);
                intent.putExtra("videoId", finalData.getId() + "");
                intent.putExtra("rtmp",finalData.getRtmp());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        int liveNum = 0;
        int videoNum = 0;
        if ((liveDatas != null && liveDatas.isEmpty() && (videoDatas != null && videoDatas.isEmpty()))) {

            return 1;
        } else {

            if (liveDatas != null && !liveDatas.isEmpty()) {
                if (type == 1) {
                    liveNum = liveDatas.size();
                } else {
                    liveNum = liveDatas.size() + 1;
                }
            } else {
                liveNum = 0;
            }
            if (videoDatas != null && !videoDatas.isEmpty()) {
                if (type == 2) {
                    videoNum = videoDatas.size();
                } else {
                    videoNum = videoDatas.size() + 1;
                }
            } else {
                videoNum = 0;
            }
            return liveNum + videoNum;
        }

    }

    @Override
    public int getItemViewType(int position) {
        if ((liveDatas != null && liveDatas.isEmpty() && (videoDatas != null && videoDatas.isEmpty()))) {
            LogUtil.d(TAG, "getItemViewType");

            return VIEW_EMPTY;
        } else if ((liveDatas != null && !liveDatas.isEmpty()) && (videoDatas != null && videoDatas.isEmpty())) {
            if (position == 0) {
                if (type == 1) {
                    return VIEW_LIVE_ITEM;
                } else {
                    return VIEW_LIVE_TITLE;
                }
            } else {
                return VIEW_LIVE_ITEM;
            }
        } else if ((liveDatas != null && liveDatas.isEmpty()) && (videoDatas != null && !videoDatas.isEmpty())) {
            if (position == 0) {
                if (type == 2) {
                    return VIEW_VIDEO_ITEM;
                } else {
                    return VIEW_VIDEO_TITLE;
                }
            } else {
                return VIEW_VIDEO_ITEM;
            }
        } else {
            if (position == 0) {
                return VIEW_LIVE_TITLE;
            } else if (position > 0 && position <= liveDatas.size()) {
                return VIEW_LIVE_ITEM;
            } else if (position == liveDatas.size() + 1) {
                return VIEW_VIDEO_TITLE;
            } else {
                return VIEW_VIDEO_ITEM;
            }

        }
    }

    class LivePlayItemHolder extends RecyclerView.ViewHolder {
        LinearLayout llLivePlayItem;
        RoundedImageView ivLivePlayPoster;
        CircleImageView anchorHeader;
        TextView tvTitle;
        TextView tvContent;
        View topView;
        TextView tvGameName;
        ImageView anchorSex;

        public LivePlayItemHolder(View itemView) {
            super(itemView);
            topView = itemView.findViewById(R.id.topView);//顶部view间距
            llLivePlayItem = (LinearLayout) itemView.findViewById(R.id.llLivePlayItem);
            ivLivePlayPoster = (RoundedImageView) itemView.findViewById(R.id.ivLivePlayPoster);
            anchorHeader = (CircleImageView) itemView.findViewById(R.id.anchorHeader);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            tvGameName = (TextView) itemView.findViewById(R.id.tvGameName);
            anchorSex = (ImageView) itemView.findViewById(R.id.anchorSex);
        }
    }

    class VideoItemHolder extends RecyclerView.ViewHolder {
        LinearLayout llVideoItem;
        RoundedImageView ivVideoPic;
        TextView tvVideoTitle;
        TextView tvVideoContent;
        TextView tvPlayNum;
        TextView tvCommentNum;

        public VideoItemHolder(View itemView) {
            super(itemView);
            llVideoItem = (LinearLayout) itemView.findViewById(R.id.llVideoItem); //整个item
            ivVideoPic = (RoundedImageView) itemView.findViewById(R.id.ivVideoPic);  //视屏图片
            tvVideoTitle = (TextView) itemView.findViewById(R.id.tvVideoTitle); //视屏标题
            tvVideoContent = (TextView) itemView.findViewById(R.id.tvVideoContent);//视屏类容
            tvPlayNum = (TextView) itemView.findViewById(R.id.tvPlayNum); //播放次数
            tvCommentNum = (TextView) itemView.findViewById(R.id.tvCommentNum); //点赞次数
        }
    }

    class LivePlayTitle extends RecyclerView.ViewHolder {
        TextView tvHotLivePlay; //热门直播
        TextView tvMore;  //查看更多
        RelativeLayout rlTitle; //标题

        public LivePlayTitle(View itemView) {
            super(itemView);
            tvHotLivePlay = (TextView) itemView.findViewById(R.id.tvHotLivePlay);
            tvMore = (TextView) itemView.findViewById(R.id.tvMore);
            rlTitle = (RelativeLayout) itemView.findViewById(R.id.rlTitle);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    class EmptyHolder extends RecyclerView.ViewHolder {
        private TextView errTitle;

        public EmptyHolder(View itemView) {
            super(itemView);
            errTitle = (TextView) itemView.findViewById(R.id.tv_err_title);
        }
    }
}
