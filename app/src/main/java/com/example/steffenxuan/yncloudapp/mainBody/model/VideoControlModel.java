package com.example.steffenxuan.yncloudapp.mainBody.model;

import android.content.Context;

import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpCallback;
import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpUtils;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.VideoControlLisentener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

import okhttp3.Call;
import okhttp3.Response;

import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.ysyControlUrlApi;
import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.ysyStopControlUrlApi;

/**
 * Created by Steffen_xuan on 2019/6/7.
 */

public class VideoControlModel implements IModel {
    private JsonParser parser=new JsonParser();

    public void satrtRequestPost(String data, final Context context, final VideoControlLisentener lisentener){

        OkHttpUtils.post(ysyControlUrlApi,data,new OkHttpCallback(){

            @Override
            public void onFinish(String status, String msg) {
                super.onFinish(status, msg);
                if(status.equals("success")){
                    if(msg.substring(0,1).equals("<")) {lisentener.onFails(); return;}
                    JsonObject jo=(JsonObject)parser.parse(msg);
                    if (jo.get("code").getAsString().equals("1")){
                        lisentener.onSeccess();
                    }
                    else lisentener.onFails();
                }
                else lisentener.onFails();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                super.onResponse(call, response);

                result = response.body().string().toString();

                onFinish("success", result);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                onFinish("failure", e.toString());
            }
        });

    }
    public void satrtStopRequestPost(String data, final Context context, final VideoControlLisentener lisentener){

        OkHttpUtils.post(ysyStopControlUrlApi,data,new OkHttpCallback(){

            @Override
            public void onFinish(String status, String msg) {
                super.onFinish(status, msg);
                if(status.equals("success")){
                    if(msg.substring(0,1).equals("<")) {lisentener.onFails(); return;}
                    JsonObject jo=(JsonObject)parser.parse(msg);
                    if (jo.get("code").getAsString().equals("1")){
                        lisentener.onSeccess();
                    }
                    else lisentener.onFails();
                }
                else lisentener.onFails();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                super.onResponse(call, response);

                result = response.body().string().toString();

                onFinish("success", result);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                onFinish("failure", e.toString());
            }
        });

    }
}
