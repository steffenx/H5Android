package com.example.steffenxuan.yncloudapp.mainBody.model;

import android.content.Context;
import android.util.Log;

import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpCallback;
import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpUtils;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesUtil;
import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.IotDataLisentener;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.videoMonitorLisentener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.vcrDataApi;
import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.ysyAllUrlApi;

/**
 * Created by Steffen_xuan on 2019/6/5.
 */

public class VideoMonitorModel implements IModel{


    private JSONArray setResult=new JSONArray();
    private JsonParser parser=new JsonParser();
    private String TAG="VideoMonitorModel";

    public void satrtRequestPost(final Context context, final String data, final videoMonitorLisentener lisentener){
        OkHttpUtils.post(vcrDataApi,data,new OkHttpCallback(){
            @Override
            public void onFinish(String status, String msg) {
                super.onFinish(status, msg);

                if(status.equals("success")){
                    if(msg.substring(0,1).equals("<")) {lisentener.onFails(); return;}
                    JsonObject jo=(JsonObject)parser.parse(msg);
                    if (jo.get("code").getAsString().equals("1")){
                        if (jo.get("data").toString().equals("null")) return;
                        jo=jo.get("data").getAsJsonObject();
                        if (jo.get("list").toString().equals("null")) return;
                        jo=jo.get("list").getAsJsonObject();
                        if (jo.get("vcrList").toString().equals("null")) return;
                        JsonArray vcrList=jo.get("vcrList").getAsJsonArray();
                        for (int i=0;i<vcrList.size();i++){

                            if (vcrList.get(i).toString().equals("null")) continue;
                            JsonObject subObject=vcrList.get(i).getAsJsonObject();

                            if (subObject.get("id").toString().equals("null")) continue;
                            String myvarid=subObject.get("id").getAsString();
                            SharedPreferencesUtil.getmInstance().put(context,"vcrListID",subObject.get("id").getAsString());

                            if (subObject.get("videoList").toString().equals("null")) continue;
                            JsonArray videoList=subObject.get("videoList").getAsJsonArray();
                            for (int j=0;j<videoList.size();j++){

                                if (videoList.get(j).toString().equals("null")) continue;
                                subObject=videoList.get(j).getAsJsonObject();
                                JSONObject resuluData=new JSONObject();
                                try {

                                    if (subObject.get("id").toString().equals("null")) resuluData.put("id", "无数据");
                                    else {resuluData.put("id",subObject.get("id").getAsString());}

                                    if (subObject.get("name").toString().equals("null")) resuluData.put("name", "无数据");
                                    else resuluData.put("name", subObject.get("name").getAsString());

                                    if (subObject.get("cdetector").toString().equals("null")) resuluData.put("cdetector", "无数据");
                                    else resuluData.put("cdetector", subObject.get("cdetector").getAsString());

                                    //0离线/1在线
                                    if (subObject.get("sbstatus").toString().equals("null")) resuluData.put("sbstatus", "无数据");
                                    else resuluData.put("sbstatus", subObject.get("sbstatus").getAsString());

                                    ////0禁用/1启用
                                    if (subObject.get("switch").toString().equals("null")) resuluData.put("switch", "无数据");
                                    else resuluData.put("switch", subObject.get("switch").getAsString());

                                    setResult.put(resuluData);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            postYSY(context,data,lisentener,myvarid);

                        }

                        //Log.d("monitorActivityPage",setResult.toString());

                        //lisentener.onSeccess(setResult.toString());
                    }
                    else ToastUtil.AlentLongToast(context,"数据请求失败！");
                }
                else ToastUtil.AlentLongToast(context,"请检查网络连接！");

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

    /**
     * 请求萤石云视频地址
     * @param context
     * @param data
     */
    public void postYSY(final Context context, String data, final videoMonitorLisentener lisentener,String varid){


        String vcrListID=(String)SharedPreferencesUtil.getmInstance().get(context,"vcrListID","0");
        JsonObject joo=(JsonObject)parser.parse(data);
        joo.addProperty("vcr_id",varid);



        OkHttpUtils.post(ysyAllUrlApi,joo.toString(), new OkHttpCallback(){
            @Override
            public void onFinish(String status, String msg) {
                super.onFinish(status, msg);

                if (status.equals("success")){
                    if(msg.substring(0,1).equals("<")) {lisentener.onFails(); return;}
                    JsonObject jo=(JsonObject)parser.parse(msg);

                    if (jo.get("code").getAsString().equals("1")){
                        if (jo.get("data").toString().equals("null")) return;
                        jo=jo.get("data").getAsJsonObject();
                        JsonArray ja=jo.get("list").getAsJsonArray();
                        JSONArray result=new JSONArray();


                        for (int i=0;i<ja.size();i++){
                            try {

                                JSONObject jj=setResult.getJSONObject(i);
                                if (ja.get(i).toString().equals("null")) continue;
                                jo=ja.get(i).getAsJsonObject();

//                                if (jo.get("id").toString().equals("null")) jj.put("vdrid"," ");
//                                else jj.put("vdrid",jo.get("id").getAsString());

                                if (jo.get("picUrl").toString().equals("null")) jj.put("picUrl"," ");
                                else jj.put("picUrl",jo.get("picUrl").getAsString());

                                if (jo.get("liveAddress").toString().equals("null")) jj.put("liveAddress"," ");
                                else jj.put("liveAddress",jo.get("liveAddress").getAsString());

                                if (jo.get("hdAddress").toString().equals("null")) jj.put("hdAddress"," ");
                                else jj.put("hdAddress",jo.get("hdAddress").getAsString());

                                if (jo.get("rtmp").toString().equals("null")) jj.put("rtmp"," ");
                                else jj.put("rtmp",jo.get("rtmp").getAsString());

                                result.put(jj);



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        lisentener.onSeccess(result.toString());

                    }

                }
                else ToastUtil.AlentLongToast(context,"请检查网络连接！");

            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                onFinish("failure", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                super.onResponse(call, response);

                result = response.body().string().toString();

                onFinish("success", result);
            }
        });




    }
}
