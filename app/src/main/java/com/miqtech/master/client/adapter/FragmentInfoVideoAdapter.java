package com.miqtech.master.client.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.InforItemDetail;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;

import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.utils.Utils;

import java.util.List;

/**
 * Created by Administrator on 2016/4/13.
 */
public class FragmentInfoVideoAdapter extends RecyclerView.Adapter {

    private List<InforItemDetail> mDatas;
    private Context mContext;
    private LayoutInflater inflater;
    private boolean mShowFooter = false;

    private final int VIEW_ITEM = 1;
    private final int VIEW_FOOTER = 2;
    private final int VIEW_EMPTY = -1;

    public FragmentInfoVideoAdapter(Context context, List<InforItemDetail> datas) {
        this.mDatas = datas;
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        if (viewType == VIEW_FOOTER) {
            view = inflater.inflate(R.layout.layout_footer_view, null);
            holder = new FooterHoder(view);
        } else if (viewType == VIEW_ITEM) {
            view = inflater.inflate(R.layout.layout_info_video_item, null);
            holder = new VideoItemHolder(view);
        } else {
            view = inflater.inflate(R.layout.exception_page, null);
            holder = new EmptyHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterHoder) {
            //
        } else if (holder instanceof VideoItemHolder) {
            setupVideo((VideoItemHolder) holder, position);
        } else if (holder instanceof EmptyHolder) {
            ((EmptyHolder) holder).errTitle.setText("该栏目下没有视频");
        }
    }

    /**
     * 填充ui
     *
     * @return
     */
    private void setupVideo(VideoItemHolder holder, final int position) {
        InforItemDetail data = mDatas.get(position);
        if (data != null) {
            AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + data.getIcon(), holder.imgCover);
            holder.tvCount.setText(Utils.calculate(data.getRead_num(), 10000, "万"));
            if (data.getTime().contains("-")){
                holder.tvTime.setText(TimeFormatUtil.formatNoSecond(data.getTime()));
            }else {
                holder.tvTime.setText(data.getTime());
            }

            holder.tvTitle.setText(data.getTitle());
            holder.tvLable.setText(data.getKeyword());
            holder.tvTitleSecond.setText(data.getBrief());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mDatas == null || mDatas.isEmpty()) {
            return 1;
        }
        int i = mShowFooter ? 1 : 0;
        return mDatas.size() + i;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDatas == null || mDatas.isEmpty()) {
            return VIEW_EMPTY;
        }
        if (position == mDatas.size()) {
            return VIEW_FOOTER;
        } else {
            return VIEW_ITEM;
        }
    }

    class VideoItemHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvLable;
        TextView tvTitle;
        TextView tvTime;
        TextView tvCount;
        TextView tvTitleSecond;

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

    class FooterHoder extends RecyclerView.ViewHolder {

        public FooterHoder(View itemView) {
            super(itemView);
        }

    }

    public void showFooter() {
        notifyItemInserted(getItemCount());
        mShowFooter = true;
    }

    public void hideFooter() {
        notifyItemRemoved(getItemCount() - 1);
        mShowFooter = false;
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
