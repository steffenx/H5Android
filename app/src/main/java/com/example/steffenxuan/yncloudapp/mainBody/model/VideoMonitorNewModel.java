package com.example.steffenxuan.yncloudapp.mainBody.model;

import android.content.Context;
import android.util.Log;

import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpCallback;
import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpUtils;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesUtil;
import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.videoMonitorLisentener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.vcrDataApi;
import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.ysyAllUrlApi;

/**
 * Created by Steffen_xuan on 2019/7/13.
 */

public class VideoMonitorNewModel implements IModel {


    private JSONArray setResult;
    private JsonParser parser=new JsonParser();
    private String TAG="VideoMonitorNewModel";
    String nowvcrid;

    public void satrtRequestPost(final Context context, final String data, final String vcrid ,final videoMonitorLisentener lisentener){
        nowvcrid=vcrid;
        OkHttpUtils.post(vcrDataApi,data,new OkHttpCallback(){
            @Override
            public void onFinish(String status, String msg) {
                super.onFinish(status, msg);

                if(status.equals("success")){
                    setResult=new JSONArray();
                    if(msg.substring(0,1).equals("<")) {lisentener.onFails(); return;}
                    JsonObject jo=(JsonObject)parser.parse(msg);

                    if (jo.get("code").getAsString().equals("1")){
                        //获取data
                        if (jo.get("data").toString().equals("null")) return;
                        jo=jo.get("data").getAsJsonObject();
                        //获取list
                        if (jo.get("list").toString().equals("null")) return;
                        jo=jo.get("list").getAsJsonObject();
                        //获取vcrlist
                        if (jo.get("vcrList").toString().equals("null")) return;
                        final JsonArray vcrList=jo.get("vcrList").getAsJsonArray();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //保存所有id
                                JSONArray vcrInfo=new JSONArray();
                                //获取所有vcrid
                                for (int l=0;l<vcrList.size();l++){
                                    if (vcrList.get(l).toString().equals("null")) continue;
                                    JsonObject infoObject=vcrList.get(l).getAsJsonObject();
                                    JSONObject vcr=new JSONObject();
                                    try {
                                        //保存id
                                        if (infoObject.get("id").toString().equals("null")) continue;
                                        vcr.put("id",infoObject.get("id").getAsString());

                                        //保存终端
                                        if (infoObject.get("name").toString().equals("null")) continue;
                                        vcr.put("name",infoObject.get("name").getAsString());

                                        vcrInfo.put(vcr);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                SharedPreferencesUtil.getmInstance().put(context,"varInfo",vcrInfo.toString());
                                //Log.d(TAG, "run: "+vcrInfo.toString());
                            }
                        }).start();




                            if (!vcrid.equals("0")){
                                //循环vcrlist
                                for (int i=0;i<vcrList.size();i++) {
                                    if (vcrList.get(i).toString().equals("null")) continue;
                                    JsonObject subObject = vcrList.get(i).getAsJsonObject();

                                    if (subObject.get("id").toString().equals("null")) continue;
                                    String myvarid = subObject.get("id").getAsString();

                                    if (vcrid.equals(myvarid)) {
                                        Log.d(TAG, "onFinish: "+vcrid+myvarid);
                                        if (subObject.get("videoList").toString().equals("null")) continue;
                                        JsonArray videoList = subObject.get("videoList").getAsJsonArray();
                                        Log.d(TAG, "onFinish: "+videoList);
                                        for (int j = 0; j < videoList.size(); j++) {

                                            if (videoList.get(j).toString().equals("null")) continue;
                                            subObject = videoList.get(j).getAsJsonObject();
                                            Log.d(TAG, "onFinish: "+subObject);
                                            JSONObject resuluData = new JSONObject();
                                            try {

                                                if (subObject.get("id").toString().equals("null"))
                                                    resuluData.put("id", "无数据");
                                                else {
                                                    resuluData.put("id", subObject.get("id").getAsString());
                                                }

                                                if (subObject.get("name").toString().equals("null"))
                                                    resuluData.put("name", "无数据");
                                                else
                                                    resuluData.put("name", subObject.get("name").getAsString());

                                                if (subObject.get("cdetector").toString().equals("null"))
                                                    resuluData.put("cdetector", "无数据");
                                                else
                                                    resuluData.put("cdetector", subObject.get("cdetector").getAsString());

                                                //0离线/1在线
                                                if (subObject.get("sbstatus").toString().equals("null"))
                                                    resuluData.put("sbstatus", "无数据");
                                                else
                                                    resuluData.put("sbstatus", subObject.get("sbstatus").getAsString());

                                                ////0禁用/1启用
                                                if (subObject.get("switch").toString().equals("null"))
                                                    resuluData.put("switch", "无数据");
                                                else
                                                    resuluData.put("switch", subObject.get("switch").getAsString());

                                                setResult.put(resuluData);


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        //Log.d(TAG, "onFinish: "+setResult);
                                    }
                                }
                            }
                            else {
                                if (vcrList.get(0).toString().equals("null")) return;
                                JsonObject subObject=vcrList.get(0).getAsJsonObject();

                                if (subObject.get("id").toString().equals("null")) return;
                                nowvcrid=subObject.get("id").getAsString();

                                if (subObject.get("videoList").toString().equals("null")) return;
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
                            }


                        postYSY(context,data,lisentener,nowvcrid);
                    }
                    else {ToastUtil.AlentLongToast(context,"数据请求失败！");lisentener.onFails();}
                }
                else {ToastUtil.AlentLongToast(context,"请检查网络连接！");lisentener.onFails();}

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

                    }else {lisentener.onFails();}

                }
                else {ToastUtil.AlentLongToast(context,"请检查网络连接！");lisentener.onFails();}

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
