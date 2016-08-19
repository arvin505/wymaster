package com.miqtech.master.client.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.miqtech.master.client.R;

/**
 * Created by Administrator on 2016/1/13.
 */
public class RemarkDialogFragment extends DialogFragment {

    private View view;
    private String detail;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_remark_dialog, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_remark_detail);
        view.findViewById(R.id.tv_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv.setText(detail);
        builder.setView(view);
        return builder.create();
    }

    public void setRemarkContent(String detail) {
        this.detail = detail;
    }

}
