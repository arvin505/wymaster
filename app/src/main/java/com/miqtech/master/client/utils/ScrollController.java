package com.miqtech.master.client.utils;

import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/19.
 */
public class ScrollController {

    private static Map<String, ViewGroup> viewPagers;


    static {
        viewPagers = new HashMap<>();
    }

    public static void addViewPager(String key, ViewGroup viewPager) {
        viewPagers.put(key, viewPager);
        LogUtil.e("TAG", "-------view add " + key);
    }

    /**
     * 判断是否由最外层slidingmenu 拦截点击事件，
     *
     * @param ev
     * @return true 不拦截 （由子view处理） false 拦截
     */
    public static boolean shouldIntercept(MotionEvent ev) {
        float x = ev.getRawX();
        float y = ev.getY();
        for (Map.Entry<String, ViewGroup> entry : viewPagers.entrySet()) {
            String key = entry.getKey();
            ViewGroup viewPager = entry.getValue();
            if (!viewPager.isShown()) {
                continue;
            }
            int[] location = new int[2];
            viewPager.getLocationOnScreen(location);
            int viewX = location[0];
            int viewY = location[1];

            if (viewPager instanceof ViewPager && ((ViewPager) viewPager).getCurrentItem() == 0 && (x > viewX && x < viewX + Utils.dp2px(50))) {
                return false;
            }
            if (y > viewY && y < viewY + viewPager.getHeight() && viewPager.isShown()) {
                return true;
            }
            /*Rect rect = new Rect();
            viewPager.getHitRect(rect);
            if (rect.contains((int)ev.getRawX(),(int)ev.getRawY())){
                return true;
            }*/
        }
        return false;
    }

    /**
     * remove view
     */
    public static void removeView(ViewGroup viewGroup) {
        if (viewPagers.containsValue(viewGroup)) {
            viewPagers.remove(viewGroup);
        }
    }

    /**
     * remove view by name
     */
    public static void removeView(String name) {
        if (viewPagers.containsKey(name)) {
            viewPagers.remove(name);
        }
    }

}
