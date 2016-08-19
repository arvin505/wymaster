package com.miqtech.master.client.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.entity.ReviewProgressInfo;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.ImagePagerActivity;
import com.miqtech.master.client.ui.fragment.RemarkDialogFragment;
import com.miqtech.master.client.utils.AsyncImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/7.
 */
public class ComplaintAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Context mContext;
    private ReviewProgressInfo progressInfo;

    private String[] stateStrs = {"提交成功", "申述处理中", "申述已处理", "申述驳回"};
    private String[] stateExtras = {"等待重审"};

    public ComplaintAdapter(Context context, ReviewProgressInfo info) {
        this.mContext = context;
        this.progressInfo = info;
        this.inflater = LayoutInflater.from(mContext);
    }

    public void setData(ReviewProgressInfo info) {
        this.progressInfo = info;
        info.setStates(initReviewStateData(info.getStates()));
    }

    private List<ReviewProgressInfo.State> initReviewStateData(List<ReviewProgressInfo.State> states) {
        List<ReviewProgressInfo.State> datas = new ArrayList<>();
        for (ReviewProgressInfo.State state : states) {
            //state.setTitle(stateStrs[state.getState() - 1]);
            datas.add(state);
            if (state.getState() == 1) {
                ReviewProgressInfo.State extraState = progressInfo.new State();
               // extraState.setTitle(stateExtras[0]);
                extraState.setCreate_date(state.getCreate_date());
                datas.add(extraState);
            }
        }

        Collections.reverse(datas);
        return datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = inflater.inflate(R.layout.layout_review_progress_item, null);
            return new StateViewHolder(view);
        } else if (viewType == 0) {
            View view = inflater.inflate(R.layout.layout_review_progress_header, null);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.layout_review_content, null);
            return new ReviewContentHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StateViewHolder) {
            getStateView(position, (StateViewHolder) holder);
        } else if (holder instanceof ReviewContentHolder) {
            getContentView((ReviewContentHolder) holder);
        }
    }

    /**
     * 显示状态
     */
    private void getStateView(int position, StateViewHolder holder) {
        if (position == 1) {
            holder.imState.setImageResource(R.drawable.pro_big);
            holder.lineUp.setVisibility(View.INVISIBLE);
            holder.tvState.setTextColor(mContext.getResources().getColor(R.color.colorActionBarSelected));
            holder.tvTime.setTextColor(mContext.getResources().getColor(R.color.colorActionBarSelected));
            holder.lineDown.setVisibility(View.VISIBLE);
            holder.bottomline.setVisibility(View.VISIBLE);
            holder.tvState.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            holder.tvState.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            holder.imState.setImageResource(R.drawable.pro_small);
            holder.lineUp.setVisibility(View.VISIBLE);
            holder.tvState.setTextColor(mContext.getResources().getColor(R.color.colorActionBarUnSelected));
            holder.tvTime.setTextColor(mContext.getResources().getColor(R.color.colorActionBarUnSelected));
            if (position != progressInfo.getStates().size()) {
                holder.lineDown.setVisibility(View.VISIBLE);
                holder.bottomline.setVisibility(View.VISIBLE);
            } else {
                holder.lineDown.setVisibility(View.INVISIBLE);
                holder.bottomline.setVisibility(View.GONE);
            }

        }

        final ReviewProgressInfo.State state = progressInfo.getStates().get(position - 1);
        holder.tvState.setText(state.getMsg());
        holder.tvTime.setText(state.getCreate_date());

        if (!TextUtils.isEmpty(state.getRemark())) {
            holder.tvRemark.setVisibility(View.VISIBLE);
        } else {
            holder.tvRemark.setVisibility(View.GONE);
        }

        holder.tvRemark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemarkDialogFragment remarkDialogFragment = new RemarkDialogFragment();
                remarkDialogFragment.setRemarkContent(state.getRemark());
                remarkDialogFragment.show(((Activity) mContext).getFragmentManager(), "remarkdialog");

            }
        });
    }

    /**
     * 显示审核的内容
     */
    private void getContentView(ReviewContentHolder holder) {
        if (progressInfo.getVerify_imgs().size() > 0) {
            for (int i = 0; i < progressInfo.getVerify_imgs().size(); i++) {
                ImageView imageView = new ImageView(mContext);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.width = (int) mContext.getResources().getDimension(R.dimen.image_width);
                lp.height = (int) mContext.getResources().getDimension(R.dimen.image_width);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                lp.setMargins(0, 0, (int) mContext.getResources().getDimension(R.dimen.weekly_view_margin), 0);
                imageView.setPadding(0, 0, (int) mContext.getResources().getDimension(R.dimen.weekly_view_margin), 0);
                imageView.setLayoutParams(lp);

                AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + progressInfo.getVerify_imgs().get(i).getImg(), imageView);
                holder.llImgs.addView(imageView);
                imageView.setTag(i);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        List<ReviewProgressInfo.Img> urls = progressInfo.getVerify_imgs();
                        ArrayList<Map<String, String>> imgs = new ArrayList<Map<String, String>>();
                        for (int i = 0; i < urls.size(); i++) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("url", urls.get(i).getImg());
                            imgs.add(map);
                        }
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("images", imgs);
                        bundle.putInt("image_index", position);
                        intent.putExtras(bundle);
                        intent.setClass(mContext, ImagePagerActivity.class);
                        mContext.startActivity(intent);
                    }
                });
            }
            holder.tvContent.setText(progressInfo.getVerify_describes());
            holder.tvTitle.setText("申述进度");
        }
    }

    @Override
    public int getItemCount() {
        return progressInfo.getStates().size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == progressInfo.getStates().size() + 1) {
            return 2;
        } else if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    class StateViewHolder extends RecyclerView.ViewHolder {
        private ImageView imState;
        private View lineUp;
        private View lineDown;
        private TextView tvState;
        private TextView tvTime;
        private View bottomline;
        private TextView tvRemark;

        public StateViewHolder(View itemView) {
            super(itemView);
            imState = (ImageView) itemView.findViewById(R.id.im_state);
            lineDown = itemView.findViewById(R.id.line_down);
            lineUp = itemView.findViewById(R.id.line_up);
            tvState = (TextView) itemView.findViewById(R.id.tv_state);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            bottomline = itemView.findViewById(R.id.bottomline);
            tvRemark = (TextView) itemView.findViewById(R.id.tv_remark);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class ReviewContentHolder extends RecyclerView.ViewHolder {
        TextView tvContent;
        LinearLayout llImgs;
        TextView tvTitle;

        public ReviewContentHolder(View itemView) {
            super(itemView);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            llImgs = (LinearLayout) itemView.findViewById(R.id.llImgs);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
