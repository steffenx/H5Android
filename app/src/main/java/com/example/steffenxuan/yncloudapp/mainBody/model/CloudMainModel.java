package com.example.steffenxuan.yncloudapp.mainBody.model;

import android.content.Context;
import android.util.Log;

import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpCallback;
import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpUtils;
import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.activity.UpdateRecords;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.CloudMainLisentener;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.DeviceInfoLisentener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.devicesUpdateApi;
import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.upgradeAppApi;

/**
 * Created by Steffen_xuan on 2019/6/27.
 */

public class CloudMainModel implements IModel {
    private JsonParser parser=new JsonParser();
    public void satrtRequestPost(final Context context, final String data, final CloudMainLisentener lisentener){

        OkHttpUtils.post(upgradeAppApi,data,new OkHttpCallback(){

            @Override
            public void onFinish(String status, String msg) {
                super.onFinish(status, msg);

                if (status.equals("success")){
                    if(msg.substring(0,1).equals("<")) {lisentener.onFails(); return;}
                    JsonObject jo=(JsonObject)parser.parse(msg);
                    if (jo.get("code").getAsString().equals("1")){
                        lisentener.onSeccess(jo.get("msg").getAsString());
                    }



                }

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
