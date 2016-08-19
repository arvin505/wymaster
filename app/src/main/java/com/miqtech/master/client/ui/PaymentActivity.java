package com.miqtech.master.client.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.miqtech.master.client.R;
import com.miqtech.master.client.alipay.PayResult;
import com.miqtech.master.client.alipay.SignUtils;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.appmanager.AppManager;
import com.miqtech.master.client.entity.ALIPayEntity;
import com.miqtech.master.client.entity.CardCompat;
import com.miqtech.master.client.entity.ExclusiveRedBag;
import com.miqtech.master.client.entity.InternetBarInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.entity.WXPayEntity;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.simcpux.Constants;
import com.miqtech.master.client.simcpux.MD5;
import com.miqtech.master.client.simcpux.MD5Util;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.GsonUtil;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.UMengStatisticsUtil;
import com.miqtech.master.client.wxapi.WXPayEntryActivity;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PaymentActivity extends BaseActivity implements OnClickListener, TextWatcher {

    private Button mBtnMakeSurePay;

    private ImageView mIvNetBar;

    private EditText mEvEnterAmount;

    private CheckBox mCBoxWeiX, mCBoxZFB;

    private TextView mTvNetName, mTvPirce, mTvChoiceRedBag, tvDiscountNum, tvAmount, tvMinLimit, tvRelFee;

    private RelativeLayout rlWeiXPay, rlZFBPay;
    private ImageButton nrlGoBack;
    private RelativeLayout rlDiscount;

    private RelativeLayout rlRedBag;

    private LinearLayout llRedBag;

    private TextView tvCanUseRedBagNum;


    private InternetBarInfo netBarInfo = null;

    public static String PAY_TYPE = "pay_type";

    public static final int RESERVE_PAY = 1;

    public static final int PAY = 2;

    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    private Context context;

    private ALIPayEntity aliPayEntity;


    private static final int REQUEST_BAG = 1;

    private static final BigDecimal _100 = new BigDecimal(100);

    private CardCompat redBag = new CardCompat();

    private int limitMoney;

    @Bind(R.id.ll_order_info)
    LinearLayout llOrderInfo;
    @Bind(R.id.ll_pay)
    LinearLayout llPay;
    @Bind(R.id.ll_verify)
    LinearLayout llVerify;
    @Bind(R.id.tv_pay_type)
    TextView tvPayType;
    @Bind(R.id.tv_pay_money)
    TextView tyPayMoney;
    @Bind(R.id.tv_red_bag)
    TextView tvRedBag;
    @Bind(R.id.tv_netbar_discount)
    TextView tvNetbarDiscound;
    @Bind(R.id.tv_real_money)
    TextView tvRealMoney;
    @Bind(R.id.bt_cancle)
    ImageView imCancle;
    @Bind(R.id.bt_verify)
    Button btVerify;
    @Bind(R.id.tv_real_fee)
    TextView tvRealFee;
    @Bind(R.id.tvDiscountType)
    TextView tvDiscountType;
    @Bind(R.id.im_verify_cancle)
    ImageView imVerifyCancle;
    @Bind(R.id.et_verify)
    EditText etVerify;

    private int step = 1;
    private TimeCount timer;

    // 微信支付
    PayReq req;


    IWXAPI msgApi;


    TextView show;
    Map<String, String> resultunifiedorder;
    StringBuffer sb;

    private int orderId;
    private int redbagNum;

    @Override
    protected void init() {
        setContentView(R.layout.paymentactivity);
        ButterKnife.bind(this);
        msgApi = WXAPIFactory.createWXAPI(getApplicationContext(), null);
        context = this;
        FindView();
        initData();
        initView();
        timer = new TimeCount(60000L, 1000L);
    }

    protected void FindView() {
        mBtnMakeSurePay = (Button) findViewById(R.id.btn_makesurepay);

        mEvEnterAmount = (EditText) findViewById(R.id.ev_enteramount);

        mIvNetBar = (ImageView) findViewById(R.id.iv_netbar_head);

        mTvChoiceRedBag = (TextView) findViewById(R.id.tv_choiceredbag);
        mTvNetName = (TextView) findViewById(R.id.tv_netbar_name);
        mTvPirce = (TextView) findViewById(R.id.tv_netbar_price);

        rlWeiXPay = (RelativeLayout) findViewById(R.id.rlweixpay);
        rlZFBPay = (RelativeLayout) findViewById(R.id.rlzfbpay);

        mCBoxWeiX = (CheckBox) findViewById(R.id.cbtn_weix);
        mCBoxZFB = (CheckBox) findViewById(R.id.cbtn_zfb);

        rlDiscount = (RelativeLayout) findViewById(R.id.rlDiscount);
        tvAmount = (TextView) findViewById(R.id.tvAmount);
        tvDiscountNum = (TextView) findViewById(R.id.tvDiscountNum);
        tvCanUseRedBagNum = (TextView) findViewById(R.id.tvCanUseRedBagNum);
        rlRedBag = (RelativeLayout) findViewById(R.id.rlRedBag);
        llRedBag = (LinearLayout) findViewById(R.id.llRedBag);
        tvMinLimit = (TextView) findViewById(R.id.tvMinLimit);
        tvRelFee = (TextView) findViewById(R.id.tvRelFee);
        imCancle.setOnClickListener(this);
        btVerify.setOnClickListener(this);
        imVerifyCancle.setOnClickListener(this);
    }

    protected void initView() {
        setLeftIncludeTitle("一键支付");
        setLeftBtnImage(R.drawable.back);
        nrlGoBack = getLeftBtn();

        nrlGoBack.setOnClickListener(this);
        rlWeiXPay.setOnClickListener(this);
        rlZFBPay.setOnClickListener(this);

        mIvNetBar.setOnClickListener(this);
        rlRedBag.setOnClickListener(this);
        mBtnMakeSurePay.setOnClickListener(this);
        rlDiscount.setOnClickListener(this);
        mEvEnterAmount.addTextChangedListener(this);

        mTvNetName.setText(netBarInfo.getName());
        mTvPirce.setText("¥" + netBarInfo.getPrice_per_hour() + "/小时");
        if (netBarInfo.getHas_rebate() == 1) {
            rlDiscount.setVisibility(View.VISIBLE);
            tvDiscountNum.setText(netBarInfo.getRebate() / 10 + "折");
        } else if (netBarInfo.getHas_rebate() == 0) {
            rlDiscount.setVisibility(View.GONE);
        }
        AsyncImage.loadPhoto(this, HttpConstant.SERVICE_UPLOAD_AREA + netBarInfo.getIcon(), mIvNetBar);
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        netBarInfo = (InternetBarInfo) getIntent().getSerializableExtra("netbar");
        loadMinLimitAndRedbagNum();
        loadHasNoExclusive();
    }

    private void loadMinLimitAndRedbagNum() {
        User user = WangYuApplication.getUser(this);
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        params.put("netbarId", netBarInfo.getId() + "");
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.LEFT_TOTAL, params, HttpConstant.LEFT_TOTAL);

    }

    private void loadOrder() {
        double price = Double.parseDouble(mEvEnterAmount.getText().toString());
        if (price > 0) {
            User user = WangYuApplication.getUser(this);
            Map<String, String> params = new HashMap<>();
            params.put("userId", user.getId());
            params.put("token", user.getToken());
            params.put("body", "支付网费" + "--" + netBarInfo.getName() + "--"
                    + mEvEnterAmount.getText().toString());
            params.put("netbar_id", netBarInfo.getId() + "");
            params.put("origAmount", mEvEnterAmount.getText().toString());

            params.put("orderType", "0");
            if (!TextUtils.isEmpty(etVerify.getText().toString())) {
                params.put("smsCode", etVerify.getText().toString().trim());
            }
            if (redBag.cardType == 0) {
                if (redBag.id != 0) {
                    params.put("rids", redBag.id + "");
                }
                params.put("amount", getAmount() + "");
            } else {
                if (redBag.id != 0) {
                    params.put("cardId", redBag.id + "");
                }
                params.put("amount", (getAmount() + redBag.value) + "");
            }
            if (mCBoxWeiX.isChecked()) {
                params.put("type", 0 + "");
                if (!msgApi.isWXAppInstalled()) {
                    showToast("并没有安装微信");
                    return;
                }
            } else {
                params.put("type", 1 + "");
            }
            params.put("isNewAccount", "1");
            LogUtil.e("obj", "paramount == " + params.toString());
            showLoading();
            sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.ORDER_PAY, params, HttpConstant.ORDER_PAY);
        } else {
            showToast("请输入正确金额");
        }

    }

    private double getAmount() {
        double payAmount = 0;
        double amount = 0;
        int hasRebate = netBarInfo.getHas_rebate();
        String amountStr = mEvEnterAmount.getText().toString();
        if (!TextUtils.isEmpty(amountStr)) {
            amount = Double.parseDouble(amountStr);
            BigDecimal totalAmount = new BigDecimal(amount);
            if (hasRebate == 1) {
                int discount = netBarInfo.getRebate();
                String algorithm = netBarInfo.getAlgorithm() + "";
                //###后台大神说没有用到这个参数，所以默认按照先打折再减去红包金额处理//
                if (algorithm.equals("1")) {// 1 先打折在减红包金额
                    BigDecimal afterRebate = totalAmount.multiply(new BigDecimal(discount)).divide(_100, 2,
                            BigDecimal.ROUND_HALF_UP);
                    payAmount = redBag.cardType == 2 ? afterRebate.doubleValue() : afterRebate.subtract(new BigDecimal(redBag.value)).doubleValue();
                } else if (false/*algorithm.equals("2")*/) {// 2先减去红包金额再打折
                    BigDecimal beforeRebate = redBag.cardType == 1 ? totalAmount : totalAmount.subtract(new BigDecimal(redBag.value));
                    BigDecimal payAmountBigDecimal = beforeRebate.multiply(new BigDecimal(discount)).divide(_100, 2,
                            BigDecimal.ROUND_HALF_UP);
                    payAmount = payAmountBigDecimal.doubleValue();
                }
            } else if (hasRebate == 0) {
                payAmount = redBag.cardType == 1 ? totalAmount.doubleValue() : totalAmount.subtract(new BigDecimal(redBag.value)).doubleValue();
            }
            if (payAmount <= 0) {
                payAmount = 0;
            }
        }

        return payAmount;
    }

    private void initRedBagView() {
        double amount = getAmount();
        tvAmount.setText(amount + "");
        tvCanUseRedBagNum.setText(redBag.value + "元");
    }

    private void checkAliPlayUser() {
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(PaymentActivity.this);
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

    private void loadAliPay() {
        // 订单
        String orderInfo = getOrderInfo(netBarInfo.getName(), "上网费用", getAmount() + "");

        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PaymentActivity.this);
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
    public String getOrderInfo(String subject, String body, String price) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + com.miqtech.master.client.alipay.Constants.PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + com.miqtech.master.client.alipay.Constants.SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + aliPayEntity.getOut_trade_no() + "\"";

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
        Date date = new Date();
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

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    LogUtil.e("ali", "sdk_pay_1");
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        sendPayTask();
                        Toast.makeText(PaymentActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        /*boolean result = AppManager.getAppManager().findActivity(MyOrderActivity.class);
                        AppManager.getAppManager().finishActivity(InternetBarActivityvV2.class);
                        if (result) {
                            AppManager.getAppManager().finishActivity(MyOrderActivity.class);
                        }*/
                        Intent intent = new Intent();
                        intent.setClass(context, PayOrderActivity.class);
                        intent.putExtra("payId", orderId + "");
                        intent.putExtra("source", PaymentActivity.class.getName());
                        intent.putExtra("netbarid", netBarInfo.getId());
                        startActivity(intent);
                        finish();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(PaymentActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(PaymentActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    LogUtil.e("ali", "sdk_pay_1");
                     /*Toast.makeText(PaymentActivity.this, "检查结果为：" +
                     msg.obj,
                     Toast.LENGTH_SHORT).show();*/
                    loadAliPay();
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    private void sendPayTask() {
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(this);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MALLTASK_PAYTASK, params, HttpConstant.MALLTASK_PAYTASK);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.rlweixpay:
                mCBoxWeiX.setChecked(true);
                mCBoxZFB.setChecked(false);
                break;
            case R.id.rlzfbpay:
                mCBoxWeiX.setChecked(false);
                mCBoxZFB.setChecked(true);
                break;
            case R.id.rlRedBag:
                if (!TextUtils.isEmpty(mEvEnterAmount.getText().toString())) {
                    double price = Double.parseDouble(mEvEnterAmount.getText().toString());
                    if (price >= limitMoney) {
                        Intent intent = new Intent();
                        intent.setClass(context, MyRedBagActivity.class);
                        redBag.netbarId = netBarInfo.getId();
                        redBag.amount = price;
                        intent.putExtra("netbarid", netBarInfo.getId());
                        intent.putExtra("amount", Float.valueOf(mEvEnterAmount.getText().toString()));
                        intent.putExtra("redBag", redBag);
                        startActivityForResult(intent, REQUEST_BAG);
                    } else {
                        showToast("红包满" + limitMoney + "元才可以用");
                    }
                } else {
                    showToast("请输入支付金额");
                }


                break;
            case R.id.btn_makesurepay:
                if (!TextUtils.isEmpty(mEvEnterAmount.getText().toString())) {
                    if (step == 1) { // show orderinfo
                        showOrderInfo();
                    } else if (step == 2) {
                        showVerifyView();
                    } else if (step == 3) {
                        if (redBag.needValidate == 0) {
                            loadOrder();
                        } else {
                            if (redBag.needValidate == 1 && !TextUtils.isEmpty(etVerify.getText().toString())) {
                                loadOrder();
                            } else {
                                showToast("请输入验证码");
                            }
                        }
                    }

                } else {
                    showToast("请输入金额");
                }
                break;
            case R.id.iv_netbar_head:
                break;
            case R.id.bt_cancle:
                step = 1;
                llOrderInfo.setVisibility(View.GONE);
                llPay.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_verify:
                verifyRedBag();
                break;
            case R.id.im_verify_cancle:
                step = 2;
                llOrderInfo.setVisibility(View.VISIBLE);
                llPay.setVisibility(View.GONE);
                llVerify.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void showVerifyView() {
        llPay.setVisibility(View.GONE);
        llOrderInfo.setVisibility(View.GONE);
        llVerify.setVisibility(View.VISIBLE);
        step = 3;
    }

    private void showOrderInfo() {
        llPay.setVisibility(View.GONE);
        llOrderInfo.setVisibility(View.VISIBLE);
        tvPayType.setText("上网金额");
        if (redBag.cardType == 1) {
            tvDiscountType.setText("增值券赠送");
        } else {
            tvDiscountType.setText("红包抵扣");
        }
        tvRedBag.setText((redBag.cardType == 1 ? "+" : "-") + getString(R.string.price, redBag.value + ""));
        tyPayMoney.setText(getString(R.string.price, mEvEnterAmount.getText().toString()));
        tvNetbarDiscound.setText(netBarInfo.getRebate() / 10 + "折");
        tvRealMoney.setText(getString(R.string.price, getAmount() + ""));
        tvRealFee.setText(tvRelFee.getText());
        if (redBag.needValidate == 1) {
            step = 2;
        } else {
            step = 3;
        }
    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    private void genPayReq(WXPayEntity wxPayEntity) {
        req = new PayReq();
        msgApi.registerApp(Constants.APP_ID);

        req.appId = Constants.APP_ID;
        req.partnerId = Constants.MCH_ID;
        req.prepayId = wxPayEntity.getPrepay_id();
        req.packageValue = "Sign=WXPay";
        req.nonceStr = genNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());
        // showToast("orderId1"+wxPayEntity.getOrderId());
        WXPayEntryActivity.setOrderId(wxPayEntity.getOrderId());

        req.sign = getParams(req.appId, req.partnerId, req.prepayId, req.packageValue, req.nonceStr, req.timeStamp);

        AppManager.getAppManager().addActivity(this);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PaymentActivity.REQUEST_BAG) {

            if (data != null) {
                redBag = data.getParcelableExtra("redBag");
                tvCanUseRedBagNum.setText(redBag.value + "元" + (redBag.cardType == 0 ? "红包" : "卡券"));
                double amount = getAmount();
                tvAmount.setText(amount + "");
                tvRelFee.setText(redBag.cardType == 1 ? Double.parseDouble(mEvEnterAmount.getText().toString()) + redBag.value + "" : Double.parseDouble(mEvEnterAmount.getText().toString()) + "");
            } else {
                redBag = new CardCompat();
                tvCanUseRedBagNum.setText(redbagNum + "个红包可用");
                double amount = getAmount();
                tvAmount.setText(amount + "");
                tvRelFee.setText(Float.parseFloat(mEvEnterAmount.getText().toString())+"");
            }
        }
    }


    @Override
    public void afterTextChanged(Editable arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void onTextChanged(CharSequence str, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
        String amount = str.toString();
        if (mEvEnterAmount.getText().length() == 0) {
            tvAmount.setText("0.0");
            tvRelFee.setText("0.0");
            return;
        }
        if (!amount.startsWith(".")) {
            tvAmount.setText(getAmount() + "");
            tvRelFee.setText(redBag.cardType == 1 ? Double.parseDouble(mEvEnterAmount.getText().toString()) + redBag.value + "" : Double.parseDouble(mEvEnterAmount.getText().toString()) + "");
        } else {
            mEvEnterAmount.setText("");
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        hideLoading();
        if (HttpConstant.LEFT_TOTAL.equals(method)) {
            try {
                if (object.getInt("code") == 0 && object.has("object") && object.getString("result").equals("success")) {
                    JSONObject objectJson = object.getJSONObject("object");
                    boolean hasLimitMoney = objectJson.has("limit_money");
                    if (hasLimitMoney) {
                        limitMoney = objectJson.getInt("limit_money");
                        tvMinLimit.setText("上网金额在" + limitMoney + "元以上才可使用");
                    }
                    boolean hasRedBagNumber = objectJson.has("redbag_number");
                    // if (hasRedBagNumber) {
                    redbagNum = objectJson.getInt("leftTotal");
                    // tvMinLimt.setText("上网金额在"+"limitMoney"+"元以上才可使用");
                    tvCanUseRedBagNum.setText(redbagNum + "个红包卡券可用");
                    //  }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (HttpConstant.ORDER_PAY.equals(method)) {//支付
            try {
                LogUtil.e("return", object.toString());
                if (object.getInt("code") == 0 && object.has("object") && object.getString("result").equals("success")) {
                    JSONObject jsonObj = object.getJSONObject("object");
                    orderId = jsonObj.getInt("orderId");
                    if (UMengStatisticsUtil.isCount) {
                        HashMap<String, String> payInfo = new HashMap<String, String>();
                        payInfo.put("p_price", getAmount() + "");
                        MobclickAgent.onEventValue(context, UMengStatisticsUtil.WANG_FEI, payInfo, 0);
                    }
                    boolean hasSelfPay = jsonObj.has("self_pay");
                    if (hasSelfPay) {
                        int self_pay = jsonObj.getInt("self_pay");
                        if (self_pay == 1) {
                            showToast("支付成功");
                           /* boolean result = AppManager.getAppManager().findActivity(MyOrderActivity.class);
                            AppManager.getAppManager().finishActivity(InternetBarActivityV2.class);
                            if (result) {
                                AppManager.getAppManager().finishActivity(MyOrderActivity.class);
                            }*/
                            Intent intent = new Intent();
                            intent.setClass(context, PayOrderActivity.class);
                            intent.putExtra("payId", orderId + "");
                            intent.putExtra("source", PaymentActivity.class.getName());
                            startActivity(intent);
                            finish();
                            return;
                        }
                        if (mCBoxWeiX.isChecked()) {
                            WXPayEntity wxPayEntity = GsonUtil.getBean(jsonObj.toString(), WXPayEntity.class);
                            genPayReq(wxPayEntity);
                        } else {
                            aliPayEntity = GsonUtil.getBean(jsonObj.toString(), ALIPayEntity.class);
                            if (aliPayEntity != null) {
                                checkAliPlayUser();
                            }
                        }
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (method.equals(HttpConstant.VERIFY_REDBAG)) {
            showToast("发送成功");
            timer.start();
        } else if (method.equals(HttpConstant.EXCLUSIVE_REDBAG)) {

            if (object.has("object")) {
                try {
                    String strObj = object.getString("object");
                    List<ExclusiveRedBag> redBags = GsonUtil.getList(strObj, ExclusiveRedBag.class);
                    getAlertDialog(redBags);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        hideLoading();
        if (!method.equals(HttpConstant.EXCLUSIVE_REDBAG)) {
            showToast(errMsg);
        }
    }


    private void verifyRedBag() {
        showLoading();
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(this);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.VERIFY_REDBAG, params, HttpConstant.VERIFY_REDBAG);
    }

    //获取是否有优惠
    private void loadHasNoExclusive() {
        User user = WangYuApplication.getUser(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("netbarId", netBarInfo.getId() + "");
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.EXCLUSIVE_REDBAG, params, HttpConstant.EXCLUSIVE_REDBAG);
    }

    private void getAlertDialog(List<ExclusiveRedBag> redBags) {

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
        Button btnGoUse = (Button) exclusiveDialog.findViewById(R.id.btnGoUse);
        btnGoUse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                exclusiveDialog.dismiss();
            }
        });
        //设置字体颜色
        SpannableStringBuilder builder = new SpannableStringBuilder(tvExclusiveTitle.getText().toString());
        ForegroundColorSpan orangeSpan = new ForegroundColorSpan(getResources().getColor(R.color.orange));
        builder.setSpan(orangeSpan, 11, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvExclusiveTitle.setText(builder);
        lvExclusive.setAdapter(new ExclusiveRedBagAdapter(redBags));
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            btVerify.setText("重新发送");
            btVerify.setClickable(true);
            btVerify.setBackgroundResource(R.drawable.shape_orange_bg_corner);
        }


        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            btVerify.setClickable(false);
            btVerify.setBackgroundResource(R.drawable.shape_verifycode_bg);
            btVerify.setText("(" + millisUntilFinished / 1000 + "秒" + ")" + "重新获取");
        }
    }

    private class ExclusiveRedBagAdapter extends BaseAdapter {
        List<ExclusiveRedBag> redBags;

        ExclusiveRedBagAdapter(List<ExclusiveRedBag> redBags) {
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
                v = View.inflate(context, R.layout.layout_exclusive_redbag_item, null);
                holder = new ViewHolder();
                holder.tvAmount = (TextView) v.findViewById(R.id.tvAmount);
                holder.tvCanUseAmount = (TextView) v.findViewById(R.id.tvCanUseAmount);
                holder.tvExclusiveTime = (TextView) v.findViewById(R.id.tvExclusiveTime);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            holder.tvAmount.setText(redBags.get(position).getMoney() + "");
            holder.tvCanUseAmount.setText("满" + redBags.get(position).getMin_money() + "元可用");
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
