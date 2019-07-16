package com.example.steffenxuan.yncloudapp.mainBody.model;

import android.content.Context;
import android.util.Log;

import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpCallback;
import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpUtils;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesUtil;
import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.TimeCurveLisentent;
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

import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.devicesdataListApi;
import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.devicesDataApi;

/**
 * Created by Steffen_xuan on 2019/6/19.
 */

public class TimeCurveModel implements IModel {

    private JsonParser parser=new JsonParser();
    private String TAG="TimeCurveActivityDebug";
    private String terminal_id="0";
    private static String devices_id="0";

    public void satrtRequestPost(final Context context, String data, final TimeCurveLisentent lisentener){
        devices_id=data;
        JsonObject jo=new JsonObject();
        String account=(String) SharedPreferencesUtil.getmInstance().get(context,"account","失败");
        if (!account.equals("失败")){
            String token= DatabaseOperation_userInfo.getInstance().getToken(context,account);
            jo.addProperty("token",token);
            jo.addProperty("devices_id",data);
        }
        OkHttpUtils.post(devicesdataListApi,jo.toString(),new OkHttpCallback(){

            @Override
            public void onFinish(String status, String msg) {
                super.onFinish(status, msg);


                if (status.equals("success")){
                    if(msg.substring(0,1).equals("<")) {lisentener.onFails(); return;}
                    JsonObject jo=(JsonObject)parser.parse(msg);
                    if (jo.get("code").getAsString().equals("1")){
                        //获取daat
                        if (jo.get("data").toString().equals("null")) return;
                        jo=jo.get("data").getAsJsonObject();
                        //获取terminalInfo
                        if (jo.get("terminalInfo").toString().equals("null")) return;
                        jo=jo.get("terminalInfo").getAsJsonObject();
                        //返回的数据
                        JSONObject resultobj=new JSONObject();
                        try {

                            if (jo.get("id").toString().equals("null")) resultobj.put("id","--");
                            else {resultobj.put("id",jo.get("id").getAsString());terminal_id=jo.get("id").getAsString();}
                            if (jo.get("name").toString().equals("null")) resultobj.put("name","--");
                            else resultobj.put("name",jo.get("name").getAsString());

                            //获取list array
                            if (jo.get("list").toString().equals("null")) return;
                            JsonArray jalist=jo.get("list").getAsJsonArray();

                            //要传输给js的json数据 obj保存的数组
                            JSONArray setResult=new JSONArray();

//                            for (int i=jalist.size()-1;i>0;i--){
                            for (int i=jalist.size()-15;i<jalist.size();i++){
                                //保存json
                                JSONObject obj=new JSONObject();


                                if (jalist.get(i).toString().equals("null")) continue;
                                JsonObject subObject=jalist.get(i).getAsJsonObject();

                                //获得想要的数据添加进json
                                if (subObject.get("id").toString().equals("null")) obj.put("id","--");
                                else obj.put("id",subObject.get("id").getAsString());

                                if (subObject.get("content").toString().equals("null")) obj.put("content","0");
                                else obj.put("content",subObject.get("content").getAsString());

                                if (subObject.get("createtime").toString().equals("null")) obj.put("createtime","--");
                                else obj.put("createtime",subObject.get("createtime").getAsString());

                                setResult.put(obj);

                            }
                            resultobj.put("list",setResult);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        lisentener.onSeccess(resultobj.toString(),terminal_id);
                    }
                    else {
                        lisentener.onFails();
                    }
                }
                else {
                    ToastUtil.AlentShortToast(context,"请检查网络连接！");
                    lisentener.onFails();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                super.onResponse(call, response);

                result=response.body().string().toString();

                onFinish("success",result);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                onFinish("failure", e.toString());
            }
        });
    }


    public void realtimePost(final Context context, String data, final TimeCurveLisentent lisentener){
        JsonObject jo=new JsonObject();
        String account=(String) SharedPreferencesUtil.getmInstance().get(context,"account","失败");
        if (!account.equals("失败")){
            String token= DatabaseOperation_userInfo.getInstance().getToken(context,account);
            jo.addProperty("token",token);
            jo.addProperty("terminal_id",data);
        }

        OkHttpUtils.post(devicesDataApi,jo.toString(),new OkHttpCallback(){
            @Override
            public void onFinish(String status, String msg) {
                super.onFinish(status, msg);
                Log.d(TAG, "onFinish: "+devices_id);
                if (status.equals("success")) {
                    if(msg.substring(0,1).equals("<")) {lisentener.onFails(); return;}
                    JsonObject jo_realtime = (JsonObject) parser.parse(msg);
                    if (jo_realtime.get("code").getAsString().equals("1")) {

                        //获取data
                        if (jo_realtime.get("data").toString().equals("null")) return;
                        JsonObject jsdata_realtime=jo_realtime.get("data").getAsJsonObject();
                        //获取list array
                        if (jsdata_realtime.get("list").toString().equals("null")) return;
                        JsonArray jalist_realtime=jsdata_realtime.get("list").getAsJsonArray();

                        //要传输给js的json数据 obj保存的数组
                        //JSONArray setResult=new JSONArray();
                        //保存json
                        JSONObject obj=new JSONObject();

                        //遍历json array
                        for (int i=0;i<jalist_realtime.size();i++){

                            if (jalist_realtime.get(i).toString().equals("null")) continue;
                            JsonObject subObject=jalist_realtime.get(i).getAsJsonObject();


                            if (subObject.get("id").toString().equals("null")) continue;
                            String id=subObject.get("id").toString();
                            if(id.equals(devices_id)){

                                //判断
                                if (subObject.get("devicesData").toString().equals("null")) continue;
                                else{
                                    //获取devicesData
                                    JsonObject devicesData=subObject.get("devicesData").getAsJsonObject();

                                    try {
                                        if (devicesData.get("content").toString().equals("null")) obj.put("content", "0");
                                        obj.put("content", devicesData.get("content").getAsString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }


                        }

                        //监听回调数据
                        lisentener.onSeccess(obj.toString(),"0");


                    }
                    else lisentener.onFails();
                }
                else ToastUtil.AlentShortToast(context,"请检查网络连接！");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                super.onResponse(call, response);
                result=response.body().string().toString();

                onFinish("success",result);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                onFinish("failure", e.toString());
            }
        });
    }


    public void historycurvePost(final Context context, String parameter, final TimeCurveLisentent lisentener){

        OkHttpUtils.post(devicesdataListApi,parameter,new OkHttpCallback(){

            @Override
            public void onFinish(String status, String msg) {
                super.onFinish(status, msg);


                if (status.equals("success")){
                    if(msg.substring(0,1).equals("<")) {lisentener.onFails(); return;}
                    JsonObject jo=(JsonObject)parser.parse(msg);
                    if (jo.get("code").getAsString().equals("1")){

                        //获取daat
                        if (jo.get("data").toString().equals("null")) return;
                        jo=jo.get("data").getAsJsonObject();
                        //获取terminalInfo
                        if (jo.get("terminalInfo").toString().equals("null")) return;
                        jo=jo.get("terminalInfo").getAsJsonObject();
                        //返回的数据
                        JSONObject resultobj=new JSONObject();
                        try {

                            if (jo.get("id").toString().equals("null")) resultobj.put("id","--");
                            else {resultobj.put("id",jo.get("id").getAsString());terminal_id=jo.get("id").getAsString();}
                            if (jo.get("name").toString().equals("null")) resultobj.put("name","--");
                            else resultobj.put("name",jo.get("name").getAsString());

                            //获取list array
                            if (jo.get("list").toString().equals("null")) return;
                            JsonArray jalist=jo.get("list").getAsJsonArray();

                            //要传输给js的json数据 obj保存的数组
                            JSONArray setResult=new JSONArray();

                            for (int i=jalist.size()-1;i>0;i--){

                                //保存json
                                JSONObject obj=new JSONObject();


                                if (jalist.get(i).toString().equals("null")) continue;
                                JsonObject subObject=jalist.get(i).getAsJsonObject();

                                //获得想要的数据添加进json
                                if (subObject.get("id").toString().equals("null")) obj.put("id","--");
                                else obj.put("id",subObject.get("id").getAsString());

                                if (subObject.get("content").toString().equals("null")) obj.put("content","0");
                                else obj.put("content",subObject.get("content").getAsString());

                                if (subObject.get("createtime").toString().equals("null")) obj.put("createtime","--");
                                else obj.put("createtime",subObject.get("createtime").getAsString());

                                setResult.put(obj);

                            }
                            resultobj.put("list",setResult);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        lisentener.onSeccess(resultobj.toString(),terminal_id);
                    }
                    else {
                        lisentener.onFails();
                    }
                }
                else {
                    ToastUtil.AlentShortToast(context,"请检查网络连接！");
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                super.onResponse(call, response);

                result=response.body().string().toString();

                onFinish("success",result);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                onFinish("failure", e.toString());
            }
        });
    }
}
