package com.miqtech.master.client.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.miqtech.master.client.R;

/**
 * Created by Administrator on 2015/12/4 0004.
 */
public class InputRecreationMatchInfoDialog extends Dialog {
    private Context context;


    public InputRecreationMatchInfoDialog(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public InputRecreationMatchInfoDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        init();
    }

    protected InputRecreationMatchInfoDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        View view = View.inflate(context, R.layout.layout_recreationmatchapply_dialog, null);
        setContentView(view);
        Window dialogWindow = this.getWindow();
        dialogWindow.setWindowAnimations(R.style.windowStyle);
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = lp.MATCH_PARENT;
        lp.height = lp.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
    }

    public void showDialog(final LinearLayout llContent) {

        if (llContent.getChildCount() > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    View firstChild = llContent.getChildAt(0);
                    EditText edtInput = (EditText) firstChild.findViewById(R.id.edtInput);
                    edtInput.setFocusable(true);
                    edtInput.setFocusableInTouchMode(true);
                    edtInput.requestFocus();

                    InputMethodManager inputManager =

                            (InputMethodManager) edtInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.showSoftInput(edtInput, 0);
                }
            }, 300 );
            show();
        }
    }

}
