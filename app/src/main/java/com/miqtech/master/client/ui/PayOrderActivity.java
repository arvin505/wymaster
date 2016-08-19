package com.miqtech.master.client.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.miqtech.master.client.R;
import com.miqtech.master.client.alipay.PayResult;
import com.miqtech.master.client.alipay.SignUtils;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.appmanager.AppManager;
import com.miqtech.master.client.entity.Card;
import com.miqtech.master.client.entity.ExclusiveRedBag;
import com.miqtech.master.client.entity.OrderInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.simcpux.Constants;
import com.miqtech.master.client.simcpux.MD5Util;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ShareToFriendsUtil;
import com.miqtech.master.client.utils.TimeFormatUtil;
import com.miqtech.master.client.view.MyAlertView;
import com.miqtech.master.client.view.MyAlertView.DialogAction;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * 网吧详情类
 */
@SuppressLint("CutPasteId")
public class PayOrderActivity extends BaseActivity implements OnClickListener {
    private ImageView ivBackUp;
    private TextView tvState, tvOrderBeginTime, tvNetbarName, tvTotal, tvDiscount, tvRedbagDiscount, tvRealPay, tvOrderType, tvRelFee, tvAddValue;
    private Button btnToNetbar, btnToCustmer;
    private ImageView ivNetbar;
    //    private ArrayList<BasicNameValuePair> params;
    private User user;
    private Context mContext;
    private String payId;
    private OrderInfo orderInfo;
    private LinearLayout payStatueLayout;
    private Button btn_GoPay;
    private Button btn_GoCancel;
    private Button btnGoEvaluate;

    private MyHandler mHandler;
    // 微信支付
    PayReq req;
    IWXAPI msgApi;
    TextView show;
    Map<String, String> resultunifiedorder;
    StringBuffer sb;

    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    private int isOrderList;
    private String netbarId;
    private MyAlertView redbagDialog;
    private ShareToFriendsUtil shareToFriend;

    private String title = "网娱大师赞助免费上网吧快来领取去开黑吧！";
    private String content = "领取红包可抵扣在线支付金额，分享更可多得，可叠加使用！";
    private String redBagUrl = HttpConstant.SERVICE_HTTP_AREA + HttpConstant.SHRAE_REDBAG + "type=1&id=";//http://test.api.wangyuhudong.com/redbag/web/shareRedbag?type=1&id=1028
    private ImageView ivReadbag;
    private boolean isFirstRedbag = false;

    public interface ChangeStatue {
        void onLoadData();
    }

    @Override
    protected void init() {
        super.init();
        // reserveId
        setContentView(R.layout.pay_order);
        msgApi = WXAPIFactory.createWXAPI(getApplicationContext(), null);
        String className = getIntent().getStringExtra("source");
        if (className != null && className.equals(PaymentActivity.class.getName())) {
            isFirstRedbag = true;
        }
        mContext = this;
        payId = getIntent().getStringExtra("payId");
        isOrderList = getIntent().getIntExtra("isOrderList", 0);
        netbarId = getIntent().getStringExtra("netbarId");
        LogUtil.e("netbarid", "netbarid == " + netbarId);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        ivBackUp = (ImageView) findViewById(R.id.ivBack);
        ivBackUp.setImageResource(R.drawable.back);
        tvState = (TextView) findViewById(R.id.order_state);

        tvOrderBeginTime = (TextView) findViewById(R.id.order_time);
        tvNetbarName = (TextView) findViewById(R.id.order_netbar_name);
        tvTotal = (TextView) findViewById(R.id.order_pc_count);
        tvDiscount = (TextView) findViewById(R.id.order_internet_date);
        tvRedbagDiscount = (TextView) findViewById(R.id.order_inter_time);
        tvRealPay = (TextView) findViewById(R.id.order_netbar_tip);
        ivReadbag = (ImageView) findViewById(R.id.ivRedbag);

        payStatueLayout = (LinearLayout) findViewById(R.id.payStatue);
        btn_GoPay = (Button) findViewById(R.id.goPay);
        btn_GoCancel = (Button) findViewById(R.id.goCancel);
        tvAddValue = (TextView) findViewById(R.id.order_inter_add_value);
        ivNetbar = (ImageView) findViewById(R.id.img_netbar);

        btnToNetbar = (Button) findViewById(R.id.order_to_netbar);
        btnToCustmer = (Button) findViewById(R.id.order_to_custmer);
        btnGoEvaluate = (Button) findViewById(R.id.btnGoEvaluate);
        tvOrderType = (TextView) findViewById(R.id.tvOrderType);

        tvRelFee = (TextView) findViewById(R.id.order_rel_fee);

        shareToFriend = new ShareToFriendsUtil(mContext);
        initViewAction();
    }

    private void initViewAction() {
        setLeftIncludeTitle("订单详情");
        tvState.setText("");
        ivBackUp.setOnClickListener(this);
        btnToNetbar.setOnClickListener(this);
        btnToCustmer.setOnClickListener(this);
        ivNetbar.setOnClickListener(this);
        btn_GoPay.setOnClickListener(this);
        btn_GoCancel.setOnClickListener(this);
        btnGoEvaluate.setOnClickListener(this);
        ivReadbag.setOnClickListener(this);
    }

    private void initRequest() {
        showLoading();
        user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
        }
        map.put("orderId", payId + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.PAY_ORDER_DETAILS, map, HttpConstant.PAY_ORDER_DETAILS);
    }

    /**
     * 显示数据
     */
    public void showData() {
        // -1支付失败0待支付1支付成功
        switch (orderInfo.getStatus()) {
            case -1:
                tvState.setText("支付失败");
                btn_GoPay.setText("继续支付");
                payStatueLayout.setVisibility(View.VISIBLE);
                break;
            case 0:
                tvState.setText("待支付");
                btn_GoPay.setText("支付");
                payStatueLayout.setVisibility(View.VISIBLE);
                break;
            case 1:
                tvState.setText("支付成功");
                payStatueLayout.setVisibility(View.GONE);
                btnGoEvaluate.setVisibility(View.VISIBLE);
                break;
            case 2:
                tvState.setText("评价成功");
                payStatueLayout.setVisibility(View.GONE);
                btnGoEvaluate.setVisibility(View.GONE);
                break;
        }
        if (orderInfo.getNetbar_fav_status().equals("1")) {
            btnToNetbar.setText("取消收藏");
        }
        if (orderInfo.getNetbar_fav_status().equals("0")) {
            btnToNetbar.setText("收藏网吧");
        }
        if (!TextUtils.isEmpty(orderInfo.getCreate_date())) {
            tvOrderBeginTime.setText(TimeFormatUtil.formatNoTime(orderInfo.getCreate_date()));
        }
        if (!TextUtils.isEmpty(orderInfo.getNetbar_name())) {
            tvNetbarName.setText(orderInfo.getNetbar_name());
        }
        if (TextUtils.isEmpty(orderInfo.getTotal_amount() + "")) {
            tvTotal.setText(0 + "元");
        } else {
            tvTotal.setText(orderInfo.getTotal_amount() + "元");
        }
        if (!TextUtils.isEmpty(orderInfo.getRebate())) {
            if (Integer.parseInt(orderInfo.getRebate()) > 0 && Integer.parseInt(orderInfo.getRebate()) < 100) {
                tvDiscount.setText((Integer.parseInt(orderInfo.getRebate()) * 0.1) + "折");
            } else {
                tvDiscount.setText("无折扣");
            }
        }

        if (!TextUtils.isEmpty(orderInfo.getRedbag_amount())) {
            tvRedbagDiscount.setText(orderInfo.getRedbag_amount() + "元");
        } else {
            tvRedbagDiscount.setText(0 + "元");
        }
        if (!TextUtils.isEmpty(orderInfo.getAmount() + "")) {
            tvRealPay.setText(orderInfo.getAmount() + "元");
        } else {
            tvRealPay.setText(0 + "元");
        }
        if (orderInfo.getOrderType() == 0) {
            tvOrderType.setText("支付网费");
        } else if (orderInfo.getOrderType() == 1) {
            tvOrderType.setText("会员充值");
        }

        tvAddValue.setText(orderInfo.getValueAddAmount() + "元");
        DecimalFormat df = new DecimalFormat("###.0");
        tvRelFee.setText(df.format(orderInfo.getValueAddAmount() + orderInfo.getTotal_amount()) + "元");

        AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + orderInfo.getIcon(), ivNetbar);
    }

    private void collectionNetbar(boolean isCollect) {
        showLoading();
        Map<String, String> map = new HashMap<>();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("token", user.getToken());
        }
        map.put("netbarId", orderInfo.getNetbar_id());
        LogUtil.e("netbarid", "map  == " + map.toString());
        if (isCollect) {
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.NETBAR_COLLECTION, map, HttpConstant.NETBAR_COLLECTION);
        } else {
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.NETBAR_CANCEL_COLLECTION, map, HttpConstant.NETBAR_CANCEL_COLLECTION);
        }
    }

    private void diaplayRedbag() {
        if (orderInfo.getCanLottery() == 1) {//可抽奖
            title = orderInfo.getShareRedbagTitle();
            content = orderInfo.getShareRedbagContent();
            initRedbag(orderInfo.getNetbar_id(), orderInfo.getOrder_id());
            ivReadbag.setVisibility(View.VISIBLE);
            if (redbagDialog != null) {
//            if (redbagDialog != null) {
                redbagDialog.show();

                //让布局充满屏幕
                WindowManager windowManager = getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                WindowManager.LayoutParams lp = redbagDialog.getWindow().getAttributes();
                lp.width = (int) (display.getWidth()); //设置宽度
                redbagDialog.getWindow().setAttributes(lp);
                //
                isFirstRedbag = false;
            }
        } else {
            ivReadbag.setVisibility(View.GONE);
        }
    }

    public MyAlertView getRedBagDialog(Context context, String title, String message, String positive,
                                       String Negative, final DialogAction action) {
        return new MyAlertView.Builder(context).setTitle(title).setMessage(message).createRedbag(action);
    }

    private void initRedbag(final String netbarId, final String orderId) {
        title = orderInfo.getShareRedbagTitle();
        content = orderInfo.getShareRedbagContent();
        redbagDialog = getRedBagDialog(mContext, "获得1次抽奖机会", "海量红包，更有神秘实物大礼", "立即抽奖", "",
                new DialogAction() {
                    @Override
                    public void doPositive() {
                        Intent intent = new Intent(mContext, SubjectActivity.class);
                        intent.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.LUCKY_DRAW);
                        intent.putExtra("netbarId", netbarId);
                        intent.putExtra("orderId", orderId);
                        startActivity(intent);
                    }

                    @Override
                    public void doNegative() {
                        if (orderInfo != null && orderInfo.getCanLottery() == 1) {
                            ivReadbag.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void doWeiXinShrae(int change) {

                    }
                });
        redbagDialog.setCanceledOnTouchOutside(false);
        redbagDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void toWeichat(String title, String content, String url, String icon) {
        shareToFriend.shareRedbagWyByWXFriend(title, content, url, icon, 0);
    }

    private void toFriends(String title, String content, String url, String icon) {
        shareToFriend.shareRedbagWyByWXFriend(title, content, url, icon, 1);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        LogUtil.e("obj", "obj == " + object.toString());
        try {
            Object obj = null;
            String objStr = null;
            if (object.has("object")) {
                obj = object.getString("object");
                objStr = obj.toString();
            }
            Gson gs = new Gson();
            if (method.equals(HttpConstant.PAY_ORDER_DETAILS)) {
                if (!TextUtils.isEmpty(objStr) && !"success".equals(objStr)) {
                    orderInfo = gs.fromJson(objStr, OrderInfo.class);
                }
                if (orderInfo != null) {
                    showData();
                    /*Card c = new Card();
                    c.setEnd_date("2016.3.5 12:20:14");
                    c.setName("ssssss");
                    c.setNetbar_name("dfsfsfsf");
                    c.setId(555);
                    orderInfo.setPrize(c);*/
                    if (orderInfo.getPrize() != null && orderInfo.getPrize().getId() > 0) {
                        List<Card> cards = Arrays.asList(orderInfo.getPrize());
                        getAlertDialog(cards);
                    }
                    diaplayRedbag();
                }
            }
            if (method.equals(HttpConstant.DELETE_ORDER)) {
                Intent intent = new Intent(mContext, MyOrderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("order", 1);
                if (isOrderList == 1) {
                    startActivity(intent);
                }
                finish();
            }
            if (method.equals(HttpConstant.NETBAR_COLLECTION)) {
                orderInfo.setNetbar_fav_status("1");
                showData();
            }
            if (method.equals(HttpConstant.NETBAR_CANCEL_COLLECTION)) {
                orderInfo.setNetbar_fav_status("0");
                showData();
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        showToast(errMsg);
        hideLoading();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.order_to_netbar:
                /** 收藏 **/
                if (WangYuApplication.getUser(mContext) != null) {
                    if (orderInfo != null && !TextUtils.isEmpty(orderInfo.getNetbar_fav_status())) {
                        if (orderInfo.getNetbar_fav_status().equals("1")) {
                            //取消收藏
                            collectionNetbar(false);
                        }
                        if (orderInfo.getNetbar_fav_status().equals("0")) {
                            //收藏
                            collectionNetbar(true);
                        }
                    }
                } else {
                    showToast("请先登录");
                }
                break;
            case R.id.order_to_custmer:
//                callTelphone(mContext.getString(R.string.custmer_tel));
                if (orderInfo == null) {
                    return;
                }
                callTelphone(orderInfo.getCs_phone());
                break;
            case R.id.img_netbar:
                Intent intent = new Intent(mContext, InternetBarActivityV2.class);
                if (netbarId == null) {
                    intent.putExtra("netbarId", orderInfo.getNetbar_id());
                } else {
                    intent.putExtra("netbarId", netbarId + "");
                }
                mContext.startActivity(intent);
                break;
            case R.id.goPay:
                toPayOrder();
                break;
            case R.id.goCancel:
                toCancelOrder(payId + "");
                break;
            case R.id.ivRedbag:
                if (redbagDialog != null && orderInfo.getCanLottery() == 1) {
                    redbagDialog.show();
                }
//                redbagDialog.show();
                break;
            case R.id.btnGoEvaluate:
                intent = new Intent();
                intent.setClass(this, NetbarEvaluateActivity.class);
                intent.putExtra("orderInfo", orderInfo);
                startActivity(intent);
                break;
        }
    }

    private void toPayOrder() {
        if (orderInfo != null) {
            if (orderInfo.getType().equals("1")) {
                checkAliPlayUser(orderInfo);
            } else if (orderInfo.getType().equals("2")) {
                if (!msgApi.isWXAppInstalled()) {
                    showToast("并没有安装微信");
                } else {
                    genPayReq(orderInfo);
                }
            }
        }
    }

    private void toCancelOrder(String id) {
        if (TextUtils.isEmpty(id)) {
            showToast("订单号获取发生错误");
            return;
        }
        showLoading();
        User user = WangYuApplication.getUser(mContext);
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("token", user.getToken());
        map.put("orderId", id);
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.DELETE_ORDER, map, HttpConstant.DELETE_ORDER);
    }

    private void callTelphone(String number) {
//		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
//		mContext.startActivity(intent);
        if (!number.matches("[0-9]{1,}")) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void checkAliPlayUser(OrderInfo order) {
        mHandler = new MyHandler(order);
        Runnable checkRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask((FragmentActivity) mContext);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();
                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();
    }

    private void loadAliPay(OrderInfo order) {
        // 订单
//        String orderInfo = getOrderInfo(order.getNetbar_name(), "上网费用", order.getAmount() + "", order.getOrder_id());
        String orderInfo = getOrderInfo(order.getNetbar_name(), "上网费用", order.getAmount() + "", order.getOut_trade_no());

        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask((FragmentActivity) mContext);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * create the order info. 创建订单信息
     */
    public String getOrderInfo(String subject, String body, String price, String order_id) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + com.miqtech.master.client.alipay.Constants.PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + com.miqtech.master.client.alipay.Constants.SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + order_id + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://api.wangyuhudong.com/pay/alipayNotify" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    public String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        java.util.Date date = new java.util.Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    public String sign(String content) {
        return SignUtils.sign(content, com.miqtech.master.client.alipay.Constants.RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }

    private class MyHandler extends Handler {
        OrderInfo order;

        MyHandler(OrderInfo order) {
            this.order = order;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        sendPayTask();
                        Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
                        boolean result = AppManager.getAppManager().findActivity(MyOrderActivity.class);
                        if (result) {
                            AppManager.getAppManager().finishActivity(MyOrderActivity.class);
                        }
                        isFirstRedbag = true;
                        initRequest();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(mContext, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    // Toast.makeText(ReservePaymentActivity.this, "检查结果为：" +
                    // msg.obj,
                    // Toast.LENGTH_SHORT).show();
                    loadAliPay(order);
                    break;
                }
                default:
                    break;
            }
        }

        ;
    }

    // private String genNonceStr() {
    // Random random = new Random();
    // return
    // MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    // }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    private void genPayReq(OrderInfo order) {
        req = new PayReq();
        msgApi.registerApp(Constants.APP_ID);

        req.appId = Constants.APP_ID;
        req.partnerId = Constants.MCH_ID;
        req.prepayId = order.getPrepay_id();
        req.packageValue = "Sign=WXPay";
        req.nonceStr = order.getNonce_str();
        req.timeStamp = String.valueOf(genTimeStamp());

        /*List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));*/

        req.sign = getParams(req.appId, req.partnerId, req.prepayId, req.packageValue, req.nonceStr, req.timeStamp);

        AppManager.getAppManager().addActivity((FragmentActivity) mContext);
        msgApi.sendReq(req);

    }

    public static String getSign(Element xml) {
        Iterator<Element> it = xml.elementIterator();
        StringBuffer paramStr = new StringBuffer();
        while (it.hasNext()) {
            Element el = it.next();
            if (paramStr.toString() != null && paramStr.toString().length() > 0) {
                paramStr.append("&");
            }
            paramStr.append(el.getName() + "=" + el.getText());
        }
        paramStr.append("&key=" + "587F422AC365478BA6045CD122653B0E");

        String result = MD5Util.MD5Encode(paramStr.toString(), "UTF-8").toUpperCase();

        return result;
    }

    private static String getParams(String appid, String partnerid, String prepayid, String packageValue,
                                    String noncestr, String timestamp) {
        // 构建xml
        Document doc = DocumentHelper.createDocument();
        Element xml = doc.addElement("xml");

        // 添加参数
        Element ele = xml.addElement("appid");
        ele.setText(appid);

        ele = xml.addElement("noncestr");
        ele.setText(noncestr);

        ele = xml.addElement("package");
        ele.setText(packageValue);

        ele = xml.addElement("partnerid");
        ele.setText(partnerid);

        ele = xml.addElement("prepayid");
        ele.setText(prepayid);

        ele = xml.addElement("timestamp");
        ele.setText(timestamp);

        // 获取sign
        String sign = getSign(xml);
        return sign;
    }

    private void sendPayTask() {
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(this);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MALLTASK_PAYTASK, params, HttpConstant.MALLTASK_PAYTASK);
    }

    /**
     * 送奖券
     */
    private void getAlertDialog(List<Card> redBags) {

        final Dialog exclusiveDialog = new Dialog(this);
        Window window = exclusiveDialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_exclusive_redbag, null, false);
        window.setContentView(view, params);
        exclusiveDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        exclusiveDialog.setCanceledOnTouchOutside(true);
        exclusiveDialog.show();
        ListView lvExclusive = (ListView) exclusiveDialog.findViewById(R.id.lvExclusive);
        TextView tvExclusiveTitle = (TextView) exclusiveDialog.findViewById(R.id.tvExclusiveTitle);
        tvExclusiveTitle.setText("奖券已放入账户,请在-我的-页面查看");

        TextView tvExclusiveInfo = (TextView) exclusiveDialog.findViewById(R.id.tvExclusiveInfo);
        tvExclusiveInfo.setText("仅限在" + redBags.get(0).getNetbar_name() + "使用");
        Button btnGoUse = (Button) exclusiveDialog.findViewById(R.id.btnGoUse);
        btnGoUse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                exclusiveDialog.dismiss();
                Intent intent = new Intent(PayOrderActivity.this, CardDetailActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("card", orderInfo.getPrize());
                startActivity(intent);
            }
        });
        //设置字体颜色
        SpannableStringBuilder builder = new SpannableStringBuilder(tvExclusiveTitle.getText().toString());
        ForegroundColorSpan orangeSpan = new ForegroundColorSpan(getResources().getColor(R.color.orange));
        builder.setSpan(orangeSpan, 11, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvExclusiveTitle.setText(builder);
        lvExclusive.setAdapter(new ExclusiveRedBagAdapter(redBags));
    }

    private class ExclusiveRedBagAdapter extends BaseAdapter {
        List<Card> redBags;

        ExclusiveRedBagAdapter(List<Card> redBags) {
            this.redBags = redBags;
        }

        @Override
        public int getCount() {
            return redBags.size();
        }

        @Override
        public Object getItem(int position) {
            return redBags.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            ViewHolder holder;
            if (v == null) {
                v = View.inflate(PayOrderActivity.this, R.layout.layout_exclusive_redbag_item, null);
                holder = new ViewHolder();
                holder.tvAmount = (TextView) v.findViewById(R.id.tvAmount);
                holder.tvCanUseAmount = (TextView) v.findViewById(R.id.tvCanUseAmount);
                holder.tvExclusiveTime = (TextView) v.findViewById(R.id.tvExclusiveTime);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            holder.tvAmount.setText(redBags.get(position).getName() + "");
            holder.tvCanUseAmount.setVisibility(View.GONE);
            holder.tvExclusiveTime.setText("有效期：" + redBags.get(position).getEnd_date());
            return v;
        }

        private class ViewHolder {
            TextView tvAmount;
            TextView tvCanUseAmount;
            TextView tvExclusiveTime;
        }
    }
}
