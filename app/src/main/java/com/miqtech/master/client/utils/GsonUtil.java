package com.miqtech.master.client.utils;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class GsonUtil {

    public static <T> T getBean(String jsonString, Class<T> cls) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, cls);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 效率较低，建议废止
     * @param jsonString
     * @param cls
     * @param <T>
     * @return
     */
    @Deprecated
    public static <T> List<T> getList(String jsonString, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            JsonArray array = new JsonParser().parse(jsonString).getAsJsonArray();
            for (final JsonElement elem : array) {
                list.add(new Gson().fromJson(elem, cls));
            }
        } catch (Exception e) {
            LogUtil.e("netbar", "eeee = " + e.toString());
            e.printStackTrace();
        }
        return list;
    }
}

