package com.example.steffenxuan.yncloudapp.core.netUtil;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Steffen_xuan on 2019/5/29.
 */

public class OkHttpCallback implements Callback {
    private final String TAG = OkHttpCallback.class.getSimpleName();

    public String url;
    public String result;

    public void onFinish(String status, String msg) {
//        Log.d(TAG, "url: " + url + " status：" + status);
    }

    @Override
    public void onFailure(okhttp3.Call call, IOException e) {
//        Log.d(TAG, "请求失败:" + e.toString());
//        onFinish("failure", e.toString());
    }

    @Override
    public void onResponse(okhttp3.Call call, Response response) throws IOException {
//        result = response.body().string().toString();
//        Log.d(TAG, "请求成功: " + result);
//        onFinish("success", result);
    }
}
