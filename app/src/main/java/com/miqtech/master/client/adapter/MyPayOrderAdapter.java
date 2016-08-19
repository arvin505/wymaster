package com.miqtech.master.client.adapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.master.client.R;
import com.miqtech.master.client.alipay.PayResult;
import com.miqtech.master.client.alipay.SignUtils;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.appmanager.AppManager;
import com.miqtech.master.client.constant.Constant;
import com.miqtech.master.client.entity.CompleteTask;
import com.miqtech.master.client.entity.OrderInfo;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.http.RequestUtil;
import com.miqtech.master.client.http.ResponseListener;
import com.miqtech.master.client.simcpux.Constants;
import com.miqtech.master.client.simcpux.MD5Util;
import com.miqtech.master.client.ui.InternetBarActivityV2;
import com.miqtech.master.client.ui.MyOrderActivity;
import com.miqtech.master.client.ui.NetbarEvaluateActivity;
import com.miqtech.master.client.ui.PayOrderActivity;
import com.miqtech.master.client.utils.AsyncImage;
import com.miqtech.master.client.utils.TimeUtil;
import com.miqtech.master.client.utils.ToastUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MyPayOrderAdapter extends BaseAdapter implements OnClickListener {

    private List<OrderInfo> mOrders;
    private Context mContext;
    private DealPayOrderListener mPayOrderListener;
    private MyHandler mHandler;

    // 微信支付
    PayReq req;
    final IWXAPI msgApi;
    TextView show;
    Map<String, String> resultunifiedorder;
    StringBuffer sb;

    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    public MyPayOrderAdapter(List<OrderInfo> mEvents, Context mContext, DealPayOrderListener _PayOrderListener) {
        super();
        this.mOrders = mEvents;
        this.mContext = mContext;
        this.mPayOrderListener = _PayOrderListener;
        msgApi = WXAPIFactory.createWXAPI(mContext, null);
    }

    @Override
    public int getCount() {
        return mOrders.size();
    }

    @Override
    public Object getItem(int position) {
        return mOrders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder Holder = null;
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.payorder_item, null);
            Holder = new Holder();

            Holder.setImg_bar_head((ImageView) convertView.findViewById(R.id.img_bar_head));
            Holder.setTvPayType((TextView) convertView.findViewById(R.id.tvPayType));
            Holder.setTv_bar_name((TextView) convertView.findViewById(R.id.tv_bar_name));
            Holder.setTv_red_bag((TextView) convertView.findViewById(R.id.tv_red_bag));
            Holder.setTv_need_pay((TextView) convertView.findViewById(R.id.tv_need_pay));
            Holder.setTv_createdate((TextView) convertView.findViewById(R.id.tv_createdate));
            Holder.setTv_pay_status((TextView) convertView.findViewById(R.id.tv_pay_status));
            Holder.setBtn_gopay((Button) convertView.findViewById(R.id.btn_gopay));
            Holder.setBtn_delete((Button) convertView.findViewById(R.id.btn_delete));

            Holder.getBtn_gopay().setOnClickListener(this);
            Holder.getBtn_delete().setOnClickListener(this);
            Holder.getImg_bar_head().setOnClickListener(this);
            convertView.setTag(Holder);
        } else {
            Holder = (Holder) convertView.getTag();
        }
        Holder.getBtn_gopay().setTag(position);
        Holder.getImg_bar_head().setTag(position);
        Holder.getBtn_delete().setTag(position);
        Holder.setIndex(position);
        initViewContent(Holder);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_gopay:
                Button b = (Button) v.findViewById(R.id.btn_gopay);
                Integer positionPay = Integer.valueOf(v.getTag().toString());
                OrderInfo orderInfo = mOrders.get(positionPay);
                if ("评价".equals(b.getText())) {// 支付完成评价
                    Intent intent = new Intent();
                    intent.setClass(mContext, NetbarEvaluateActivity.class);
                    intent.putExtra("orderInfo", orderInfo);
                    mContext.startActivity(intent);
                } else {// 支付
                    if (orderInfo.getType().equals("1")) {
                        checkAliPlayUser(orderInfo);
                    } else if (orderInfo.getType().equals("2")) {
                        if (!msgApi.isWXAppInstalled()) {
                            ToastUtil.showToast("并没有安装微信", mContext);
                        } else {
                            genPayReq(orderInfo);
                        }
                    }
                }
                break;
            case R.id.btn_delete:
                int positiondelete = Integer.valueOf(v.getTag().toString());
                String orderId = mOrders.get(positiondelete).getOrder_id();
                mPayOrderListener.deleteOrder(orderId, positiondelete);
                break;
            case R.id.img_bar_head:
                int position = Integer.valueOf(v.getTag().toString());
                String netBarId = mOrders.get(position).getNetbar_id();
                Intent intent = new Intent();
                intent.setClass(mContext, InternetBarActivityV2.class);
                intent.putExtra("netbarId", netBarId);
                mContext.startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 填充数据
     *
     * @param mHolder
     */
    private void initViewContent(Holder mHolder) {
        if (mHolder.getIndex() != -1) {
            OrderInfo orderInfo = mOrders.get(mHolder.getIndex());

            String barIcon = orderInfo.getIcon();
            OrderInfo order = mOrders.get(mHolder.getIndex());
            barIcon = order.getIcon();
            AsyncImage.loadPhoto(mContext, HttpConstant.SERVICE_UPLOAD_AREA + barIcon, mHolder.img_bar_head);

            mHolder.getTv_bar_name().setText(orderInfo.getNetbar_name());

            if (orderInfo.getOrderType() == 0) {
                mHolder.getTvPayType().setText("支付网费");
            } else if (orderInfo.getOrderType() == 1) {
                mHolder.getTvPayType().setText("会员充值");
            }
            String Create_date = orderInfo.getCreate_date();
            if (TextUtils.isEmpty(Create_date) || Create_date.equals("null"))
                Create_date = "";
            else
                Create_date = TimeUtil.friendlyTime(Create_date);
            mHolder.getTv_createdate().setText(Create_date);

            String redbag = orderInfo.getRedbag_amount();
            if (TextUtils.isEmpty(redbag) || redbag.equals("null"))
                redbag = "";
            else {
                if (Float.valueOf(redbag) > 0)
                    redbag = "已使用红包支付￥" + redbag;
                else
                    redbag = "";
            }
            if (TextUtils.isEmpty(redbag))
                mHolder.getTv_red_bag().setVisibility(View.GONE);
            else {
                mHolder.getTv_red_bag().setVisibility(View.VISIBLE);
                mHolder.getTv_red_bag().setText(redbag);
            }
            if (orderInfo.getAmount() != null) {
                Double amount = orderInfo.getAmount();
                mHolder.getTv_need_pay().setText("￥" + amount);
            } else {
                mHolder.getTv_need_pay().setText("");
            }


            int status = orderInfo.getStatus();

            if (TextUtils.isEmpty(status + "") || "null".equals(status + ""))
                status = -404;

            mHolder.getBtn_gopay().setText("去支付");
            mHolder.getBtn_gopay().setVisibility(View.GONE);
            mHolder.getBtn_delete().setVisibility(View.GONE);
            if (status == 0) {
                mHolder.getBtn_gopay().setVisibility(View.VISIBLE);
                mHolder.getTv_pay_status().setText("未支付");
            } else if (status == -1) {
                mHolder.getBtn_gopay().setText("继续支付");
                mHolder.getBtn_gopay().setVisibility(View.VISIBLE);
                mHolder.getBtn_delete().setVisibility(View.VISIBLE);
                mHolder.getTv_pay_status().setText("支付失败");
            } else if (status == 1) {
                mHolder.getTv_pay_status().setText("支付成功,待评价");
                mHolder.getBtn_gopay().setBackgroundResource(R.drawable.shape_blue_gray_fram);
                mHolder.getBtn_gopay().setText("评价");
                mHolder.getBtn_gopay().setTextColor(mContext.getResources().getColor(R.color.orange));
                mHolder.getBtn_delete().setVisibility(View.VISIBLE);
                mHolder.getBtn_gopay().setVisibility(View.VISIBLE);
            } else if (status == 2) {
                mHolder.getTv_pay_status().setText("评价成功");
                mHolder.getBtn_gopay().setVisibility(View.GONE);
                mHolder.getBtn_delete().setVisibility(View.VISIBLE);
            }
//			mHolder.getTv_pay_status().setText("支付成功,待评价");
//			mHolder.getBtn_gopay().setBackgroundResource(R.drawable.shape_blue_gray_fram);
//			mHolder.getBtn_gopay().setText("评价");
//			mHolder.getBtn_gopay().setTextColor(mContext.getResources().getColor(R.color.blue_gray));
//			mHolder.getBtn_delete().setVisibility(View.GONE);
//			mHolder.getBtn_gopay().setVisibility(View.VISIBLE);
        }
    }

    class Holder {
        private ImageView img_bar_head;
        private Button btn_gopay, btn_delete;
        private TextView tv_bar_name, tv_red_bag, tv_need_pay, tv_createdate, tv_pay_status, tvPayType;

        public TextView getTvPayType() {
            return tvPayType;
        }

        public void setTvPayType(TextView tvPayType) {
            this.tvPayType = tvPayType;
        }

        private Integer index = -1;

        public ImageView getImg_bar_head() {
            return img_bar_head;
        }

        public void setImg_bar_head(ImageView img_bar_head) {
            this.img_bar_head = img_bar_head;
        }

        public Button getBtn_gopay() {
            return btn_gopay;
        }

        public void setBtn_gopay(Button btn_gopay) {
            this.btn_gopay = btn_gopay;
        }

        public Button getBtn_delete() {
            return btn_delete;
        }

        public void setBtn_delete(Button btn_delete) {
            this.btn_delete = btn_delete;
        }

        public TextView getTv_bar_name() {
            return tv_bar_name;
        }

        public void setTv_bar_name(TextView tv_bar_name) {
            this.tv_bar_name = tv_bar_name;
        }

        public TextView getTv_red_bag() {
            return tv_red_bag;
        }

        public void setTv_red_bag(TextView tv_red_bag) {
            this.tv_red_bag = tv_red_bag;
        }

        public TextView getTv_need_pay() {
            return tv_need_pay;
        }

        public void setTv_need_pay(TextView tv_need_pay) {
            this.tv_need_pay = tv_need_pay;
        }

        public TextView getTv_createdate() {
            return tv_createdate;
        }

        public void setTv_createdate(TextView tv_createdate) {
            this.tv_createdate = tv_createdate;
        }

        public TextView getTv_pay_status() {
            return tv_pay_status;
        }

        public void setTv_pay_status(TextView tv_pay_status) {
            this.tv_pay_status = tv_pay_status;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

    }

    public interface DealPayOrderListener {
        void deleteOrder(String orderid, int position);

        void continuePay(String orderid);
    }

    public interface OnArticleSelectedListener {
        public void onArticleSelected(Uri articleUri);
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
    public String getOrderInfo(String subject, String body, String price, String out_trade_no) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + com.miqtech.master.client.alipay.Constants.PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + com.miqtech.master.client.alipay.Constants.SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + out_trade_no + "\"";

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
                        Intent intent = new Intent();
                        intent.setClass(mContext, PayOrderActivity.class);
                        intent.putExtra("payId", order.getOrder_id() + "");
                        intent.putExtra("source", MyPayOrderAdapter.class.getName());
                        mContext.startActivity(intent);
                        ((FragmentActivity) mContext).finish();
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
                    loadAliPay(order);
                    break;
                }
                default:
                    break;
            }
        }

        ;
    }

    private void sendPayTask() {
        Map<String, String> params = new HashMap<>();
        User user = WangYuApplication.getUser(mContext);
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        RequestUtil.getInstance().excutePostRequest(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MALLTASK_PAYTASK, params,
                new ResponseListener() {
                    @Override
                    public void onSuccess(JSONObject object, String method) {
                        //具体业务子类实现
                        if (object.has("extend")) {
                            try {
                                String extendStr = object.getString("extend");
                                showTaskCoins(extendStr);
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(String errMsg, String method) {

                    }

                    @Override
                    public void onFaild(JSONObject object, String method) {

                    }
                }
                , HttpConstant.MALLTASK_PAYTASK);
    }


    private void showTaskCoins(String extendStr) {
        if (!TextUtils.isEmpty(extendStr) && (!extendStr.equals("{}"))) {
            try {
                JSONObject jsonObj = new JSONObject(extendStr);
                ArrayList<CompleteTask> tasks = new Gson().fromJson(jsonObj.getString("completeTasks"),
                        new TypeToken<List<CompleteTask>>() {
                        }.getType());
                if (tasks != null) {
                    for (int i = 0; i < tasks.size(); i++) {
                        CompleteTask currentTask = tasks.get(i);
                        int type = currentTask.getTaskType();
                        int currentTaskIdentify = currentTask.getTaskIdentify();
                        if (type == 1) {
                            switch (currentTaskIdentify) {
                                case Constant.PAY_NETBAR:
                                    showCoinToast("支付上网费用     +" + currentTask.getCoin() + "金币");
                                    break;
                            }
                        } else if (type == 2) {
                            switch (currentTaskIdentify) {
                                case Constant.NEWUSER_FIRST_PAY:
                                    showCoinToast("首次支付     +" + currentTask.getCoin() + "金币");
                                    break;
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void showCoinToast(String msg) {
        Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageCodeProject = new ImageView(mContext);
        imageCodeProject.setImageResource(R.drawable.coin_icon);
        toastView.addView(imageCodeProject, 0);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

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

//		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
//		signParams.add(new BasicNameValuePair("appid", req.appId));
//		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
//		signParams.add(new BasicNameValuePair("package", req.packageValue));
//		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
//		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
//		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

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
        try {
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
        } catch (Exception e) {
            return "";
        }

    }

}
