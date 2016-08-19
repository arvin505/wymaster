package com.miqtech.master.client.view;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import android.support.annotation.IntDef;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    Corner.TOP_LEFT, Corner.TOP_RIGHT,
    Corner.BOTTOM_LEFT, Corner.BOTTOM_RIGHT
})
public @interface Corner {
  int TOP_LEFT = 0;
  int TOP_RIGHT = 1;
  int BOTTOM_RIGHT = 2;
  int BOTTOM_LEFT = 3;
}
