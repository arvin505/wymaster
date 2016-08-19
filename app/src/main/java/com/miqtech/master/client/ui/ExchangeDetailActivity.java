package com.miqtech.master.client.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.entity.ExchangeInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.watcher.Observerable;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 兑换详情
 * Created by Administrator on 2015/12/3.
 */
public class ExchangeDetailActivity extends BaseActivity implements View.OnClickListener, Observerable.ISubscribe {
    private Context context;
    private ImageView ivshowmode;// 顶部显示的兑换状态图标
    private TextView tvshowmode;// 顶部显示的兑换状态
    private TextView tv_consume_price;// 消费的金额
    private TextView tv_goods_name, tv_goods_creat_time, tv_goods_trade_number, tv_goods_trade_statu, tv_goods_info,
            tv_dialog_name, tv_goods_creat;

    private RelativeLayout rl_goods, rl_goods_creat;
    private LinearLayout ll_dialog;

    private String exchangeID;
    private User user;
    private ExchangeInfo exchangeInfo = new ExchangeInfo();
    private Dialog joinDialog;
    public int current;// 记录弹出框的选项
    private RelativeLayout rldialog_select_1, rldialog_select_2, rldialog_select_3;
    private ImageView ivdialog_select_1, ivdialog_select_2, ivdialog_select_3;
    private TextView dialog_buttom;

    private String duihuan_exception;
    private String duihuan_chuli_finish;
    private String duihuan_chuli;

    private TextView fankui;
    private boolean clickError = false;

    private TextView tvGoosNum;
    private RelativeLayout rlGoosNum;
    private Observerable mWatcher;

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.activity_exchange_detail);
        context = ExchangeDetailActivity.this;
        duihuan_exception = getResources().getString(R.string.duihuanException);
        duihuan_chuli_finish = getResources().getString(R.string.duihuanChuliFinish);
        duihuan_chuli = getResources().getString(R.string.duihuanChuli);
        initView();
        initData();
        mWatcher = Observerable.getInstance();
        mWatcher.subscribe(Observerable.ObserverableType.EXCHANGE,this);
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();
        exchangeID = getIntent().getStringExtra("exchangeID");
        user = WangYuApplication.getUser(context);

        ivshowmode = (ImageView) findViewById(R.id.ivshowmode);
        tvshowmode = (TextView) findViewById(R.id.tvshowmode);
        tv_consume_price = (TextView) findViewById(R.id.tv_consume_price);
        tv_goods_name = (TextView) findViewById(R.id.tv_goods_name);
        tv_goods_creat_time = (TextView) findViewById(R.id.tv_goods_creat_time);
        tv_goods_creat = (TextView) findViewById(R.id.tv_goods_creat);
        tv_goods_trade_number = (TextView) findViewById(R.id.tv_goods_trade_number);
        tv_goods_trade_statu = (TextView) findViewById(R.id.tv_goods_trade_statu);
        tv_goods_info = (TextView) findViewById(R.id.tv_goods_info);
        tv_dialog_name = (TextView) findViewById(R.id.tv_dialog_name);
        rl_goods = (RelativeLayout) findViewById(R.id.rl_goods);
        rl_goods_creat = (RelativeLayout) findViewById(R.id.rl_goods_creat);
        ll_dialog = (LinearLayout) findViewById(R.id.ll_dialog);
        tvGoosNum = (TextView) findViewById(R.id.tv_goods_nums);
        rlGoosNum = (RelativeLayout) findViewById(R.id.rl_goods_num);
        fankui = (TextView) findViewById(R.id.tvRightHandle);
        fankui.setVisibility(View.VISIBLE);
        fankui.setText(getResources().getString(R.string.feedback));
        setLeftIncludeTitle(getResources().getString(R.string.duihuandetail));
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        //setRightTextView("反馈");
        //getRightBtn().setVisibility(View.VISIBLE);
        //getRightBtn().setOnClickListener(this);
        fankui.setOnClickListener(this);
        rl_goods.setOnClickListener(this);

        joinDialog = new Dialog(context, R.style.searchStyle);
        joinDialog.setContentView(R.layout.join_dialog_coins);
        rldialog_select_1 = (RelativeLayout) joinDialog.findViewById(R.id.rldialog_select_1);
        rldialog_select_2 = (RelativeLayout) joinDialog.findViewById(R.id.rldialog_select_2);
        rldialog_select_3 = (RelativeLayout) joinDialog.findViewById(R.id.rldialog_select_3);
        ivdialog_select_1 = (ImageView) joinDialog.findViewById(R.id.ivdialog_select_1);
        ivdialog_select_2 = (ImageView) joinDialog.findViewById(R.id.ivdialog_select_2);
        ivdialog_select_3 = (ImageView) joinDialog.findViewById(R.id.ivdialog_select_3);
        dialog_buttom = (TextView) joinDialog.findViewById(R.id.dialog_buttom);
        Window dialogWindow = joinDialog.getWindow();
        dialogWindow.setWindowAnimations(R.style.windowStyle);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
        rldialog_select_1.setOnClickListener(this);
        rldialog_select_2.setOnClickListener(this);
        rldialog_select_3.setOnClickListener(this);
        dialog_buttom.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        Intent intent = null;
        switch (arg0.getId()) {
            case R.id.rl_goods:// 跳转商品详情
                switch (exchangeInfo.getType()) {
                    case 1://大转盘
                        intent = new Intent();
                        intent.setClass(context, SubjectActivity.class);
                        intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.SIGNIN_LUCKYDRAW);
                        break;
                    case 2://众筹
                        intent = new Intent(ExchangeDetailActivity.this, ShopDetailActivity.class);
                        intent.putExtra("itemPos", -1);
                        intent.putExtra("id", exchangeInfo.getCommodityId());
                        break;
                    case 3://商品详情
                        intent = new Intent(context, SubjectActivity.class);
                        intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.GOODS_DETAIL);
                        intent.putExtra("titletName", exchangeInfo.getName());
                        intent.putExtra("id", exchangeInfo.getCommodityId());
                        intent.putExtra("totalCoins", exchangeInfo.getCoin());
                        break;
                }
                // intent.putExtra("id", 10+"");
                startActivity(intent);
                break;
            case R.id.ibLeft:// 返回
                onBackPressed();
                break;
            case R.id.tvRightHandle:// 反馈
                intent = new Intent(context, FeedBackActivity.class);
                startActivity(intent);
                break;
            case R.id.rldialog_select_1:// 选择：没有收到兑换的商品
                selectDialog(1);
                break;
            case R.id.rldialog_select_2:// 选择：兑换商品已被使用
                selectDialog(2);
                break;
            case R.id.rldialog_select_3:// 选择：金币实际消耗量错误
                selectDialog(3);
                break;
            case R.id.dialog_buttom:// 确定
                tv_dialog_name.setEnabled(false);
                tv_dialog_name.setText(duihuan_chuli);
                Map<String, String> map = new HashMap<>();
                map.put("userId", user.getId());
                map.put("token", user.getToken());
                map.put("exchangeId", exchangeInfo.getId());
                map.put("type", current + "");
                sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MSG_EXCEPTION, map, HttpConstant.MSG_EXCEPTION);
                joinDialog.dismiss();
                tv_dialog_name.setBackgroundColor(getResources().getColor(R.color.gary_bg));
                break;
            default:
                break;
        }

    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        showLoading();
        getDetailData();
    }

    private void getDetailData() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("id", exchangeID);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.EXCHANGE_DETAIL_V2, map, HttpConstant.EXCHANGE_DETAIL_V2);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        try {
            Object obj = object.getString("object");
            String objStr = obj.toString();
            Gson gs = new Gson();
            if (method.equals(HttpConstant.EXCHANGE_DETAIL_V2)) {
                exchangeInfo = gs.fromJson(objStr, ExchangeInfo.class);
                if (clickError) {
                    exchangeInfo.setStatus(2);
                }
                showData();
            } else if (method.equals(HttpConstant.MSG_EXCEPTION)) {
                showToast("反馈成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        showToast(errMsg);
        hideLoading();
    }

    /**
     * 显示数据
     */
    private void showData() {
        clickError = false;
        tv_consume_price.setText("-" + exchangeInfo.getCoin());// 显示消费金额
        tv_goods_name.setText(exchangeInfo.getName());// 商品名字

        if (exchangeInfo != null) {
            if (exchangeInfo.getType() == 2) {//众筹
                if (exchangeInfo.getState() == 0 || exchangeInfo.getState() == 1) {//0未开奖1开奖中
                    rl_goods_creat.setVisibility(View.GONE);
                } else if (exchangeInfo.getState() == 3) {//3已中奖
                    tv_goods_creat.setText("中奖时间:");
                } else {
                    tv_goods_creat.setText("开奖时间:");
                }
            } else if (exchangeInfo.getType() == 1 || exchangeInfo.getType() == 3) {//大转盘或兑奖专
                tv_goods_creat.setText("中奖时间:");
            }
            tv_goods_creat_time.setText(TimeFormatUtil.formatMMDDHHMM(exchangeInfo.getDate()));// 交易创建时间
        }


        tv_goods_trade_number.setText(exchangeInfo.getTranNo());// 交易流水号
        String[] arrState;
        //exchangeInfo.setCdkeys("156461353436,168431436468,846434886466,23746844684,46416846846");
        if (exchangeInfo.getType() == 2) {  //众筹夺宝
            arrState = getResources().getStringArray(R.array.crowdstate);
        } else {
            arrState = getResources().getStringArray(R.array.exchangestate);
        }
        tv_goods_trade_statu.setText(arrState[exchangeInfo.getState()]);// 交易状态
        tvshowmode.setText(arrState[exchangeInfo.getState()]);//顶部交易状态f


        String cdkeys = exchangeInfo.getCdkeys();
        if (cdkeys != null) {
            String[] keyArr = cdkeys.split(",");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < keyArr.length; i++) {
                builder.append(keyArr[i]).append("; ");
                builder.append("\n");
            }
            tvGoosNum.setText(builder.toString().substring(0, builder.length() - 2));
        } else {
            rlGoosNum.setVisibility(View.GONE);
        }
        tv_goods_info.setText(exchangeInfo.getInfomation());// 交易信息
        /*exchangeInfo.setType(2);
        exchangeInfo.setState(3);*/
        if ((exchangeInfo.getType() == 2 && exchangeInfo.getState() == 3) || (exchangeInfo.getType() % 2 == 1 && exchangeInfo.getState() == 0)) {
           /* String extra = "还未填写地址信息";
            String infoStr = exchangeInfo.getInfomation() + "\n" + extra;
            ForegroundColorSpan orangeSpan = new ForegroundColorSpan(getResources().getColor(R.color.orange));
            SpannableStringBuilder builder = new SpannableStringBuilder(infoStr);
            builder.setSpan(orangeSpan, infoStr.length() - extra.length(), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
            tv_goods_info.setText(exchangeInfo.getInfomation());
        }
        showBtnData();
    }

    /**
     * 显示底部按钮的状态
     */
    private void showBtnData() {
        if (exchangeInfo != null) {
            if (exchangeInfo.getType() == 2) {//众筹
                if (exchangeInfo.getState() == 0 || exchangeInfo.getState() == 1 ||
                        exchangeInfo.getState() == 2 || exchangeInfo.getState() == 4) {//0未开奖，1开奖中，2未中奖，4兑奖中，下方无按钮
                    ll_dialog.setVisibility(View.GONE);
                } else if (exchangeInfo.getState() == 3) {//3中奖  显示“立即填写”
                    bottomBtnIsOnclick(getResources().getString(R.string.exchange_now), true);
                } else if (exchangeInfo.getState() == 5 || exchangeInfo.getState() == 6) {//5兑奖成功6兑奖失败
                    if (exchangeInfo.getExceptionDisabled() == 0) {
                        bottomBtnIsOnclick(duihuan_exception, true);
                    } else {
                        bottomBtnIsOnclick(duihuan_exception, false);
                    }
                } else if (exchangeInfo.getState() == 7) {//7异常
                    bottomBtnIsOnclick(duihuan_chuli, false);
                } else if (exchangeInfo.getState() == 8) {//8异常已处理
                    bottomBtnIsOnclick(duihuan_chuli_finish, true);
                } else if (exchangeInfo.getState() == 9) {//9失效  按钮置灰不可点击
                    bottomBtnIsOnclick(getResources().getString(R.string.exchange_now), false);
                }
            } else if (exchangeInfo.getType() == 1 || exchangeInfo.getType() == 3) {//1大转盘抽奖,3兑奖专区
                if (exchangeInfo.getState() == 0) {//兑奖
                    bottomBtnIsOnclick(getResources().getString(R.string.exchange_now), true);
                } else if (exchangeInfo.getState() == 1) {//1兑奖中
                    ll_dialog.setVisibility(View.GONE);
                } else if (exchangeInfo.getState() == 2 || exchangeInfo.getState() == 3) {//2兑奖成功3兑奖失败
                    if (exchangeInfo.getExceptionDisabled() == 0) {
                        bottomBtnIsOnclick(duihuan_exception, true);
                    } else {
                        bottomBtnIsOnclick(duihuan_exception, false);
                    }
                } else if (exchangeInfo.getState() == 4) {//4异常
                    bottomBtnIsOnclick(duihuan_chuli, false);
                } else if (exchangeInfo.getState() == 5) {//5异常已处理
                    bottomBtnIsOnclick(duihuan_chuli_finish, true);
                } else if (exchangeInfo.getState() == 6) {//6失效
                    bottomBtnIsOnclick(getResources().getString(R.string.exchange_now), false);
                }
            }
            tv_dialog_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (exchangeInfo.getType() == 2) {//众筹
                        if (exchangeInfo.getState() == 3) {//中奖
                            skipToWhere(0);
                        } else if (exchangeInfo.getState() == 5 || exchangeInfo.getState() == 6) {//5兑奖成功6兑奖失败
                            skipToWhere(1);
                        } else if (exchangeInfo.getState() == 8) {//异常已处理
                            skipToWhere(2);
                        }
                    } else if (exchangeInfo.getType() == 1 || exchangeInfo.getType() == 3) {//1大转盘抽奖,3兑奖专区
                        if (exchangeInfo.getState() == 0) {//中奖
                            skipToWhere(0);
                        } else if (exchangeInfo.getState() == 2 || exchangeInfo.getState() == 3) {//5兑奖成功6兑奖失败
                            skipToWhere(1);
                        } else if (exchangeInfo.getState() == 5) {//异常已处理
                            skipToWhere(2);
                        }
                    }
                }
            });
        }
    }

    /**
     * 底部按钮显示的状态，
     *
     * @param textStatu 显示名称（可为空）
     * @param isOnClick 是否可点
     */
    private void bottomBtnIsOnclick(String textStatu, boolean isOnClick) {
        tv_dialog_name.setEnabled(isOnClick);
        if (isOnClick) {
            tv_dialog_name.setTextColor(getResources().getColor(R.color.white));
            tv_dialog_name.setBackgroundResource(R.drawable.btn_login);
            ll_dialog.setVisibility(View.VISIBLE);
        } else {
            /*tv_dialog_name.setTextColor(getResources().getColor(R.color.lv_item_content_text));
            tv_dialog_name.setBackgroundColor(getResources().getColor(R.color.gary_bg));*/
            ll_dialog.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(textStatu)) {
            tv_dialog_name.setText(textStatu);
        }
    }

    /**
     * 底部按钮跳转的到哪里
     *
     * @param i 0跳转到填写信息页面，1弹出异常弹框，2跳转到我的消息--系统消息页面
     */
    private void skipToWhere(int i) {
        Intent intent = null;
        switch (i) {
            case 0:
                intent = new Intent(ExchangeDetailActivity.this, AddressActivity.class);
                intent.putExtra("goodid", exchangeInfo.getId());
                intent.putExtra("goodtype", exchangeInfo.getCommodityType() + "");
                startActivity(intent);
                break;
            case 1:
                clearSelect();
                joinDialog.show();
                break;
            case 2:
                intent = new Intent(context, MyMessageActivity.class);
                intent.putExtra("typeFragment", 3);
                startActivity(intent);
                break;
        }
    }


    /**
     * 选择了哪个dialog
     *
     * @param i
     */
    private void selectDialog(int i) {
        current = i;
        clearSelect();
        switch (i) {
            case 1:
                ivdialog_select_1.setImageResource(R.drawable.check_icon);
                break;
            case 2:
                ivdialog_select_2.setImageResource(R.drawable.check_icon);
                break;
            case 3:
                ivdialog_select_3.setImageResource(R.drawable.check_icon);
                break;
            default:
                break;
        }
    }

    /**
     * 清除选择的状态
     */
    private void clearSelect() {
        ivdialog_select_1.setImageResource(R.drawable.unchecked_icon);
        ivdialog_select_2.setImageResource(R.drawable.unchecked_icon);
        ivdialog_select_3.setImageResource(R.drawable.unchecked_icon);
    }

    @Override
    public <T> void update(T... data) {
        bottomBtnIsOnclick("", false);
    }
}
