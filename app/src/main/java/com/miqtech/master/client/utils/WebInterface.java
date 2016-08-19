package com.miqtech.master.client.utils;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * webview js调本地方法
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WebInterface {
	Activity mContext;
	private WebView webview;
	private Intent intent;

	/** Instantiate the interface and set the context */
	public WebInterface(Context c, WebView webView) {
		mContext = (Activity) c;
		this.webview = webView;
	}

	public void closeInput() {
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/** Show a toast from the web page */
	@JavascriptInterface
	public void close() {
		mContext.finish();
	}
	
}