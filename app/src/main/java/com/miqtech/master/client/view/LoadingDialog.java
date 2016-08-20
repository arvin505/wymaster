package com.miqtech.master.client.view;

import com.miqtech.master.client.R;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

/**
 * @author wuxn
 * 
 */
public class LoadingDialog extends Dialog {

	Context context;
	ProgressDialog mDialog;
	private static LoadingDialog loadingProgress = null;

	public LoadingDialog(Context context) {
		super(context);
		this.context = context;
	}

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	public void dismiss() {
		context = null;
		super.dismiss();
	}

	public static LoadingDialog createDialog(Context context, String text) {
		Log.e("loadingdialog_context--", context+"",null );
		loadingProgress = new LoadingDialog(context, R.style.dialog_transparent);
		loadingProgress.setContentView(R.layout.layout_loading_progress);
		loadingProgress.getWindow().getAttributes().gravity = Gravity.CENTER;
		loadingProgress.setCanceledOnTouchOutside(true);
		loadingProgress.setCancelable(true);// 设置当前progressdialog能否被返回键取消掉

		if (text != null) {
			TextView textView = (TextView) loadingProgress.findViewById(R.id.loading_textView);
			textView.setText(text);
		}
		Log.e("loadingProgress--", loadingProgress+"",null );
		return loadingProgress;
	}

	public static void removeDialog(){
		loadingProgress = null;
	}

	public void onWindowFousChanged(boolean hasFocus) {

		if (loadingProgress == null) {
			return;
		}
	}

}
