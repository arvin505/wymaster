package com.miqtech.master.client.view.snapscrollview;

import android.content.Context;
import android.view.View;

public class McoyDownPage implements McoySnapPageLayout.McoySnapPage {

    private Context context;
    private View rootView = null;

    public McoyDownPage(Context context, View rootView) {
        this.context = context;
        this.rootView = rootView;
    }


    @Override
    public View getRootView() {
        return rootView;
    }

    @Override
    public boolean isAtTop() {
        return true;
    }

    @Override
    public boolean isAtBottom() {
        return true;
    }

}
