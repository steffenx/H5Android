package com.example.steffenxuan.yncloudapp.mainBody.model;

import android.content.Context;
import android.util.Log;

import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpCallback;
import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpUtils;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesUtil;
import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.IotDataLisentener;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.IotcontroLisentener;
import com.example.steffenxuan.yncloudapp.sqLite.DatabaseOperation_userInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.devicesDataApi;
import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.devicesUpdateApi;

/**
 * Created by Steffen_xuan on 2019/6/17.
 */

public class IotControModel implements IModel {

    private JsonParser parser=new JsonParser();

    public void satrtRequestPost(final Context context, String data, final IotcontroLisentener lisentener){


        JsonObject jo=new JsonObject();
        String account=(String) SharedPreferencesUtil.getmInstance().get(context,"account","失败");
        if (!account.equals("失败")){
            String token= DatabaseOperation_userInfo.getInstance().getToken(context,account);
            jo.addProperty("token",token);
            jo.addProperty("terminal_id",data);
            jo.addProperty("devicetype","final");
        }

        Log.d("IotControlActivityDebug", "onFinish: "+jo.toString());
        OkHttpUtils.post(devicesDataApi,jo.toString(),new OkHttpCallback(){
            @Override
            public void onFinish(String status, String msg) {
                super.onFinish(status, msg);

                if (status.equals("success")){
                    if(msg.substring(0,1).equals("<")) {lisentener.onFails(); return;}
                    JsonObject jo=(JsonObject)parser.parse(msg);

                    if(jo.get("code").getAsString().equals("1")){
                        //获取data
                        if (jo.get("data").toString().equals("null")) return;
                        JsonObject jsdata=jo.get("data").getAsJsonObject();
                        //获取list array
                        if (jsdata.get("list").toString().equals("null")) return;
                        JsonArray jalist=jsdata.get("list").getAsJsonArray();

                        //要传输给js的json数据 obj保存的数组
                        JSONArray setResult=new JSONArray();

                        for (int i=0;i<jalist.size();i++){

                            //保存json
                            JSONObject obj=new JSONObject();

                            if (jalist.get(i).toString().equals("null")) continue;
                            JsonObject subObject=jalist.get(i).getAsJsonObject();
                            try {

                                //获得想要的数据添加进json
                                if (subObject.get("id").toString().equals("null")) obj.put("id","--");
                                else obj.put("id",subObject.get("id").getAsString());

                                if (subObject.get("alias").toString().equals("null")) obj.put("alias","--");
                                else obj.put("alias",subObject.get("alias").getAsString());

                                if (subObject.get("keywords").toString().equals("null")) obj.put("keywords","--");
                                else obj.put("keywords",subObject.get("keywords").getAsString());

                                if (subObject.get("isSwitch").toString().equals("null")) obj.put("isSwitch","0");
                                else obj.put("isSwitch",subObject.get("isSwitch").getAsString());

                                if (subObject.get("state").toString().equals("null")) obj.put("state","0");
                                else obj.put("state",subObject.get("state").getAsString());

                                //[{"id":"75","alias":"肖敏水泵控制","keywords":"水泵控制","isSwitch":"0","state":"3"}]
                                setResult.put(obj);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //Log.d("IotControlActivityDebug", "onFinish: "+obj.toString());
                        }
                        lisentener.onSeccess(setResult.toString());
                    }
                    else {
                        lisentener.onFails();
                    }


                }else ToastUtil.AlentShortToast(context,"请检查网络连接");
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



    public void devicesupdate(final Context context, String data, final IotcontroLisentener lisentener){


        OkHttpUtils.post(devicesUpdateApi,data,new OkHttpCallback(){
            @Override
            public void onFinish(String status, String msg) {
                super.onFinish(status, msg);

                if (status.equals("success")){
                    if(msg.substring(0,1).equals("<")) {lisentener.onFails(); return;}
                    JsonObject jo=(JsonObject)parser.parse(msg);

                    if(jo.get("code").getAsString().equals("1")){

                        ToastUtil.AlentShortToast(context,jo.get("msg").getAsString());
                        lisentener.onSeccess(jo.get("msg").getAsString());
                    }
                    else {
                        ToastUtil.AlentShortToast(context,jo.get("msg").getAsString());
                        lisentener.onFails();
                    }


                }else ToastUtil.AlentShortToast(context,"请检查网络连接");
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
