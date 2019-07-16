package com.example.steffenxuan.yncloudapp.mainBody.model;

import android.content.Context;
import android.util.Log;

import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpCallback;
import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpUtils;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesUtil;
import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.DeviceInfoLisentener;
import com.example.steffenxuan.yncloudapp.sqLite.DatabaseOperation_userInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.loginApi;
import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.qaedasDataApi;
import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.tokenApi;

/**
 * Created by Steffen_xuan on 2019/6/24.
 */

public class DeviceInfoModel implements IModel {

    String TAG="DeviceInfoActivityDebug";

    private JsonParser parser=new JsonParser();

    public void satrtRequestPost(final Context context, final String data, final DeviceInfoLisentener lisentener){


        JsonObject json=new JsonObject();
        String account=(String) SharedPreferencesUtil.getmInstance().get(context,"account","失败");
        if(!account.equals("失败")){
            String token= DatabaseOperation_userInfo.getInstance().getToken(context,account);
            json.addProperty("token",token);
        }else json.addProperty("token","0");


        OkHttpUtils.post(qaedasDataApi,json.toString(),new OkHttpCallback(){
            @Override
            public void onFinish(String status, String msg) {
                super.onFinish(status, msg);

                if (status.equals("success")){
                    if(msg.substring(0,1).equals("<")) {lisentener.onFails(); return;}
                    JsonObject jo=(JsonObject)parser.parse(msg);

                    if(jo.get("code").getAsString().equals("1")){
                        //获取data
                        if (jo.get("data").toString().equals("null")) return;
                        jo=jo.get("data").getAsJsonObject();

                        if (jo.get("list").toString().equals("null")) return;
                        JsonArray jalist=jo.get("list").getAsJsonArray();

                        //保存json
                        JSONObject obj = new JSONObject();
                        for (int i=0;i<jalist.size();i++) {

                            if (jalist.get(i).toString().equals("null")) continue;
                            JsonObject subObject = jalist.get(i).getAsJsonObject();

                            if (subObject.get("id").getAsString().equals(data)){
                                //获得想要的数据添加进json

                                //Log.d(TAG, "onFinish: "+subObject.get("qaedasCount").toString());
                                try {
                                    if (subObject.get("qaedasCount").toString().equals("null")) obj.put("qaedasCount","--");
                                    else obj.put("qaedasCount",subObject.get("qaedasCount").getAsJsonObject());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        Log.d(TAG, "onFinish: "+obj.toString());
                        lisentener.onSeccess(obj.toString());
                    }
                    else {
                        ToastUtil.AlentShortToast(context,jo.get("msg").getAsString());
                        lisentener.onFails();
                    }
                }
                else ToastUtil.AlentShortToast(context,"请检查网连接！");
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
