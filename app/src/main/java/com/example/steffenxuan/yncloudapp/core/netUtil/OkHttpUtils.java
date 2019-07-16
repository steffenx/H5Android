package com.example.steffenxuan.yncloudapp.core.netUtil;

import android.util.Log;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Steffen_xuan on 2019/5/29.
 */

public class OkHttpUtils {

    private static final OkHttpClient CLIENT = new OkHttpClient();

    /**
     * get请求.
     * @param url
     * @param callback
     * */
    public static void get(String url, OkHttpCallback callback) {
        callback.url = url;
        Request request = new Request.Builder().url(url).build();
        CLIENT.newCall(request).enqueue((Callback) callback);
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    /**
     * post请求.
     * */
    public static void post(String url, String json, OkHttpCallback callback) {
        callback.url = url;
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        CLIENT.newCall(request).enqueue(callback);
    }

    /**
     *
     * */
    public static void downFile(String url,final String saveDir, OkHttpCallback callback) {
        callback.url = url;
        Request request = new Request.Builder().url(url).build();
        CLIENT.newCall(request).enqueue((Callback) callback);
    }
}