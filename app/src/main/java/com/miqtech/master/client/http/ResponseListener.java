package com.miqtech.master.client.http;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/11/18.
 */
public interface ResponseListener<T> {
    public void onSuccess(JSONObject object,String method);

    public void onError(String errMsg,String method);

    public void onFaild(JSONObject object, String method);
 }
