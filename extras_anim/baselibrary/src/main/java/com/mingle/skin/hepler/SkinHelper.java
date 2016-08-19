package com.mingle.skin.hepler;

import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.mingle.skin.SkinStyle;

/**
 * /**
 *
 * @attr ref android.R.styleable#View_background
 * @attr ref android.R.styleable#TextView_text
 * @attr ref android.R.styleable#TextView_textColor
 * @attr ref android.R.styleable#TextView_textColorHighlight
 * @attr ref android.R.styleable#TextView_textColorHint
 * @attr ref android.R.styleable#TextView_textAppearance
 * @attr ref android.R.styleable#TextView_textColorLink
 * Created by zzz40500 on 15/8/27.
 */
public abstract class SkinHelper {


    public abstract void init(View view, AttributeSet attrs);

    public abstract void setSkinStyle(SkinStyle skinStyle);

    public static SkinHelper create(View v) {

        if (v instanceof TextView) {

            return new TextViewSkinHelper();
        }
        return new ViewSkinHelper();
    }

    public static DefaultViewSkinHelper createDeFault() {

        return new DefaultViewSkinHelper();
    }

    public abstract void setCurrentTheme();
}
