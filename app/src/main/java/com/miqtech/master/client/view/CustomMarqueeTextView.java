package com.miqtech.master.client.view;

import android.widget.TextView;
import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;

/**
 * Created by admin on 2016/8/9.
 */
public class CustomMarqueeTextView extends TextView {
    public CustomMarqueeTextView(Context context) {
        super(context);
        createView();
    }

    public CustomMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView();
    }

    public CustomMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        createView();
    }

    private void createView() {
        setEllipsize(TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
        setFocusableInTouchMode(true);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        if (focused) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean focused) {
        if (focused) {
            super.onWindowFocusChanged(focused);
        }
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
