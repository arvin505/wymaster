package com.miqtech.master.client.view;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.broadcastcontroller.BroadcastController;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.PreferencesUtil;


/**
 * @author zhangp
 */
@SuppressLint("WrongViewCast")
public class MyAlertView extends Dialog {
    public MyAlertView(Context context) {
        super(context);
    }

    public MyAlertView(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private EditText input;

        public EditText getInput() {
            return input;
        }

        public void setInput(EditText input) {
            this.input = input;
        }

        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param message
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public MyAlertView create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final MyAlertView dialog = new MyAlertView(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_normal_layout, null);
            dialog.setContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton)).setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton)).setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeButton)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
                }
            } else {
                layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
            }
            if (message != null) {
                layout.findViewById(R.id.message).setVisibility(View.VISIBLE);
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                ((LinearLayout) layout.findViewById(R.id.message)).removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.message)).addView(contentView, new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            }
            dialog.setContentView(layout);
            return dialog;
        }

        /**
         * 创建红包弹框
         *
         * @param action 弹框点击事件
         * @return
         */
        public MyAlertView createRedbag(final DialogAction action) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final MyAlertView dialog = new MyAlertView(context, R.style.Dialog);
            dialog.setContentView(R.layout.redbag_dialog);
//            final View layout = inflater.inflate(R.layout.redbag_dialog, null);
            final Button positive = (Button) dialog.findViewById(R.id.positiveButton);
            final Button dialogDismiss = (Button) dialog.findViewById(R.id.negativeButton);
            final TextView tvTitle = (TextView) dialog.findViewById(R.id.title);
            final TextView tvMessage = (TextView) dialog.findViewById(R.id.message);
//            dialog.setContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action.doPositive();
                    dialog.dismiss();
                }
            });

            dialogDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action.doNegative();
                    dialog.dismiss();
                }
            });

            if (!TextUtils.isEmpty(title)) {
                tvTitle.setText(title);
            }

            if (!TextUtils.isEmpty(message)) {
                tvMessage.setText(message);
            }
            return dialog;
        }

        //创建悬赏令规则弹窗
        public MyAlertView createRule() {
            final MyAlertView dialog = new MyAlertView(context, R.style.Dialog);
            dialog.setContentView(R.layout.layout_reward_rule);
            TextView tvRuleContent = (TextView) dialog.findViewById(R.id.tvRuleContent);
            ImageView ivClose = (ImageView) dialog.findViewById(R.id.ivClose);

            if (!TextUtils.isEmpty(message)) {
                tvRuleContent.setText(message);
            }

            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            return dialog;
        }

        //创建推送弹窗
        public MyAlertView createNotification(final DialogAction action) {
            final MyAlertView dialog = new MyAlertView(context, R.style.Dialog);
            dialog.setContentView(R.layout.layout_notification_dialog);
            TextView tvNotification = (TextView) dialog.findViewById(R.id.tvNotification);

            if (!TextUtils.isEmpty(message)) {
                tvNotification.setText(message);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }, 1000 * 8);
            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.dialogWindowAnim);
            dialog.show();
            return dialog;
        }

        //创建评论弹框
        public MyAlertView createComentSuccessOrFail() {
            final MyAlertView dialog = new MyAlertView(context, R.style.Dialog);
            dialog.setContentView(R.layout.layout_comment_toast);
            TextView textView = (TextView) dialog.findViewById(R.id.comment_toast_tv);
            ImageView imageView = (ImageView) dialog.findViewById(R.id.comment_toast_iv);

            if (!TextUtils.isEmpty(message)) {
                if (message.equals("1")) {
                    textView.setText(context.getResources().getString(R.string.comment_success));
                    imageView.setImageResource(R.drawable.add_success);
                } else if (message.equals("0")) {
                    textView.setText(context.getResources().getString(R.string.comment_fail));
                    imageView.setImageResource(R.drawable.wanted_alert_close);
                }
            }

            //按返回键时
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            dialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 1500);

            return dialog;
        }


        public MyAlertView create(int layoutId) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final MyAlertView dialog = new MyAlertView(context, R.style.Dialog);
            View layout = inflater.inflate(layoutId, null);
            dialog.setContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton)).setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton)).setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeButton)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
                }
            } else {
                layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
            }
            input = (EditText) layout.findViewById(R.id.invitation);
            input.setVisibility(View.VISIBLE);
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            }
            dialog.setContentView(layout);
            return dialog;
        }

        /**
         * 创建图片验证码
         *
         * @param mobileStr 手机号
         * @param action    dialog事件
         * @return
         */
        public MyAlertView creatImgVerificationCode(final String mobileStr, final VerificationCodeDialogAction action) {
            final MyAlertView dialog = new MyAlertView(context, R.style.register_style);
            dialog.setContentView(R.layout.dialog_register_pact_img);
            dialog.setCanceledOnTouchOutside(false);

            final EditText et_auth_code = (EditText) dialog.findViewById(R.id.dialog_register_et);
            TextView tvYes = (TextView) dialog.findViewById(R.id.dialog_register_on_tv);
            TextView tvNo = (TextView) dialog.findViewById(R.id.dialog_register_off_tv);
            TextView tvVerificationCodeTitle = (TextView) dialog.findViewById(R.id.tvVerificationCodeTitle);
            ImageView refreImg = (ImageView) dialog.findViewById(R.id.dialog_code_refre_iv);
            final ImageView imgCode = (ImageView) dialog.findViewById(R.id.dialog_imageview_code_iv);

            if (!TextUtils.isEmpty(title)) {
                tvVerificationCodeTitle.setText(title);
            }

            tvYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(et_auth_code.getText().toString())) {
                        Toast.makeText(context, "请输入验证码", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        action.doPositive(et_auth_code.getText().toString());
                    }
                }
            });
            tvNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    action.doNegative();
                }
            });
            refreImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncImage.loadYZM(context, HttpConstant.SERVICE_HTTP_AREA + HttpConstant.IMAGE_CODE_REGISTER + mobileStr, imgCode);
                }
            });
            imgCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncImage.loadYZM(context, HttpConstant.SERVICE_HTTP_AREA + HttpConstant.IMAGE_CODE_REGISTER + mobileStr, imgCode);
                }
            });
            AsyncImage.loadYZM(context, HttpConstant.SERVICE_HTTP_AREA + HttpConstant.IMAGE_CODE_REGISTER + mobileStr, imgCode);
            dialog.show();
            return dialog;
        }

        /**
         * 用来提醒用户是否进行上一步的操作
         *
         * @return
         */
        public MyAlertView createIsDiaolg(final DialogAction action) {
            final MyAlertView mDialog = new MyAlertView(context, R.style.register_style);
            mDialog.setContentView(R.layout.dialog_register_marked_words);
            TextView title_tv = (TextView) mDialog.findViewById(R.id.dialog_title_register);
            TextView context_tv = (TextView) mDialog.findViewById(R.id.dialog_content_register);
            TextView ok_bt = (TextView) mDialog.findViewById(R.id.dialog_register_yes_pact);
            TextView no_bt = (TextView) mDialog.findViewById(R.id.dialog_register_no_pact);
            View vv = mDialog.findViewById(R.id.dialog_line_no_pact);
            vv.setVisibility(View.VISIBLE);
            no_bt.setVisibility(View.VISIBLE);

            //显示标题
            if (!TextUtils.isEmpty(title)) {
                title_tv.setVisibility(View.VISIBLE);
                title_tv.setText(title);
            } else {
                title_tv.setVisibility(View.GONE);
                title_tv.setText("");
            }

            //显示提醒内容
            if (!TextUtils.isEmpty(message)) {
                context_tv.setVisibility(View.VISIBLE);
                context_tv.setText(message);
            } else {
                context_tv.setVisibility(View.GONE);
                context_tv.setText("");
            }

            ok_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action.doPositive();
                    mDialog.dismiss();
                }
            });

            no_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action.doNegative();
                    mDialog.dismiss();
                }
            });

            mDialog.show();
            return mDialog;
        }
    }

    public interface DialogAction {
        void doPositive();

        void doNegative();

        void doWeiXinShrae(int change);
    }

    public interface VerificationCodeDialogAction {
        void doPositive(String verificationCode);

        void doNegative();
    }
}
