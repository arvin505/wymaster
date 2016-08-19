package com.miqtech.master.client.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.miqtech.master.client.R;
import com.miqtech.master.client.application.WangYuApplication;
import com.miqtech.master.client.appmanager.AppManager;
import com.miqtech.master.client.entity.User;
import com.miqtech.master.client.http.HttpConstant;
import com.miqtech.master.client.simcpux.Constants;
import com.miqtech.master.client.ui.InternetBarActivityV2;
import com.miqtech.master.client.ui.MyOrderActivity;
import com.miqtech.master.client.ui.PayOrderActivity;
import com.miqtech.master.client.ui.PaymentActivity;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;
import com.miqtech.master.client.utils.LogUtil;
import com.miqtech.master.client.utils.ToastUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	private static int orderId;

	public static int getOrderId() {
		return orderId;
	}

	public static void setOrderId(int orderId) {
		WXPayEntryActivity.orderId = orderId;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		LogUtil.d(TAG, "onPayFinish, errCode = " + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {
                Activity currentActivity = AppManager.getAppManager().currentActivity();
                toPayInfo();
                AppManager.getAppManager().finishActivity(PaymentActivity.class);
                AppManager.getAppManager().finishActivity(currentActivity);
                AppManager.getAppManager().finishActivity(InternetBarActivityV2.class);
                AppManager.getAppManager().finishActivity(MyOrderActivity.class);


                ToastUtil.showToast("支付成功", this);
                sendPayTask();
            } else {
                ToastUtil.showToast("支付失败", this);
            }
        }
        finish();
    }

    private void toPayInfo() {
        if (orderId > 0) {
            Intent intent = new Intent();
			intent.setClass(this, PayOrderActivity.class);
			intent.putExtra("payId", orderId+"");
			intent.putExtra("source", PaymentActivity.class.getName());
            startActivity(intent);
        }
    }

    private void toReserveInfo() {
        if (orderId > 0) {
            Intent intent = new Intent();
//			intent.setClass(this, ReserveOrderActivity.class);
//			intent.putExtra("reserveId", orderId+"");
//			intent.putExtra("source", ReserveActivity.class.getName());
			startActivity(intent);
		}
	}

	private void sendPayTask(){
		Map<String,String> params = new HashMap<>();
		User user = WangYuApplication.getUser(this);
		params.put("userId", user.getId());
		params.put("token", user.getToken());
		sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.MALLTASK_PAYTASK, params, HttpConstant.MALLTASK_PAYTASK);
	}

}