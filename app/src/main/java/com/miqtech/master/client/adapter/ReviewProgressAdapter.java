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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/7.
 */
public class ReviewProgressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Context mContext;
    private ReviewProgressInfo progressInfo;

    public ReviewProgressAdapter(Context context, ReviewProgressInfo info) {
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
            //state.setTitle(stateStrs[state.getState()]);
            if (state.getState() == 9) {
                //state.setTitle(state.getTitle().replace("|", state.getDate()));
            }
            datas.add(state);
            /*if (state.getState() == 1) {
                ReviewProgressInfo.State extraState = progressInfo.new State();
                extraState.setTitle(stateExtras[0]);
                extraState.setCreate_date(state.getCreate_date());
                datas.add(extraState);
            } else if (state.getState() == 3 || state.getState() == 5) {
                ReviewProgressInfo.State extraState = progressInfo.new State();
                extraState.setTitle(stateExtras[1]);
                extraState.setCreate_date(state.getCreate_date());
                datas.add(extraState);
            }*/
        }
 //       Collections.reverse(datas);
        return datas;
    }

    //0提交成功1待审核2审核通过3审核不通过4已发放5申诉待处理6申诉已处理7申诉驳回8结束9待发放
    //private String[] stateStrs = {"提交成功", "审核进行中", "审核通过", "审核不通过", "申述成功，重新发放（预计|发放完成）", "发放中（预计|发放完成）", "已发放"};
    //private String[] stateExtras = {"等待审核", "奖品待发放"};

    private String[] stateStrs = {"提交成功", "等待审核", "审核通过", "审核不通过", "奖品已经发放", "申诉提交成功，等待处理", "申诉已经处理", "申述驳回", "已结束", "奖品待发放（预计|发放完成）"};
    //private String[] stateExtras = {"申述完成,等待重发(预计2016年12月24日发放完成)", "奖品待发放"};

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
            getContentView((ReviewContentHolder) holder, position);
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
            holder.tvState.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            holder.tvTime.setTextColor(mContext.getResources().getColor(R.color.colorActionBarSelected));
            holder.lineDown.setVisibility(View.VISIBLE);
            holder.bottomline.setVisibility(View.VISIBLE);
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
        if (!TextUtils.isEmpty(state.getRemark())) {
            holder.tvRemark.setVisibility(View.VISIBLE);
        } else {
            holder.tvRemark.setVisibility(View.GONE);
        }
        holder.tvState.setText(state.getMsg());
        holder.tvTime.setText(state.getCreate_date());
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
    private void getContentView(ReviewContentHolder holder, int postion) {
        final List<ReviewProgressInfo.Img> imgs;
        if (postion == getItemCount() - 1){
            imgs = progressInfo.getAppeal_imgs();
            holder.tvContent.setText(progressInfo.getAppeal_describes());
            holder.tvTitle.setText("申诉信息");
        }else {
            imgs = progressInfo.getVerify_imgs();
            holder.tvContent.setText(progressInfo.getVerify_describes());
            holder.tvTitle.setText("审核信息");
        }
        holder.llImgs.removeAllViews();
        if (imgs.size() > 0) {
            for (int i = 0; i < imgs.size(); i++) {
                ImageView imageView = new ImageView(mContext);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.width = (int) mContext.getResources().getDimension(R.dimen.image_width);
                lp.height = (int) mContext.getResources().getDimension(R.dimen.image_width);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                lp.setMargins(0, 0, (int) mContext.getResources().getDimension(R.dimen.weekly_view_margin), 0);
                imageView.setPadding(0, 0, (int) mContext.getResources().getDimension(R.dimen.weekly_view_margin), 0);
                imageView.setLayoutParams(lp);

                AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + imgs.get(i).getImg(), imageView);
                holder.llImgs.addView(imageView);

                imageView.setTag(i);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        List<ReviewProgressInfo.Img> urls = imgs;
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

        }
    }

    @Override
    public int getItemCount() {
        return progressInfo.getStates().size() + 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == progressInfo.getStates().size() + 1 || position == progressInfo.getStates().size() + 2) {
            return 2;
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
