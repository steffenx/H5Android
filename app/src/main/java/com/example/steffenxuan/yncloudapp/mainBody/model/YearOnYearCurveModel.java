package com.example.steffenxuan.yncloudapp.mainBody.model;

import android.content.Context;

import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpCallback;
import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpUtils;
import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.YearOnYearCurveLisentener;
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

/**
 * Created by Steffen_xuan on 2019/7/9.
 */

public class YearOnYearCurveModel implements IModel {
    private JsonParser parser=new JsonParser();
    private String terminal_id="0";
    public void FirstSatrtRequestPost(final Context context, String data, final YearOnYearCurveLisentener lisentener){
        OkHttpUtils.post(devicesdataListApi,data,new OkHttpCallback(){
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
                        ToastUtil.AlentShortToast(context,jo.get("msg").toString());
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

    public void SecondSatrtRequestPost(final Context context, String data, final YearOnYearCurveLisentener lisentener){
        OkHttpUtils.post(devicesdataListApi,data,new OkHttpCallback(){
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
                        lisentener.onEndSeccess(resultobj.toString());

                    }
                    else {
                        ToastUtil.AlentShortToast(context,jo.get("msg").toString());
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
