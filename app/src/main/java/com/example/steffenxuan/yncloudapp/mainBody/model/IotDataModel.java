package com.example.steffenxuan.yncloudapp.mainBody.model;

import android.content.Context;
import android.nfc.FormatException;
import android.util.Log;

import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpCallback;
import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpUtils;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesUtil;
import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.IotDataLisentener;
import com.example.steffenxuan.yncloudapp.sqLite.DatabaseOperation_userInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.devicesDataApi;

/**
 * Created by Steffen_xuan on 2019/6/3.
 */

public class IotDataModel implements IModel {
    private String token;
    private String terminal_id="00";
    private JsonObject jo=new JsonObject();


    /**
     *
     * @param context 上下文
     * @param data 请求数据 tel#终端id
     * @param tag 标志 （不同的标志干不同的事）
     * @param lisentener 回调监听
     */
    public void satrtRequestPost(final Context context, String data, String tag, final IotDataLisentener lisentener){
        String account=data.substring(0,data.indexOf("#"));
        token=DatabaseOperation_userInfo.getInstance().getToken(context,account);

        String id=data.substring(data.indexOf("#")+1);

        //默认请求基地下的第一个终端数据
        if (tag.equals("first")){
            String base= (String) SharedPreferencesUtil.getmInstance().get(context,"base","失败");

            getListJSON(base,id);
        }
        //选择终端后
        else if(tag.equals("terminal_id")){
            jo.addProperty("token",token);
            jo.addProperty("terminal_id",id);
        }
        else {
            jo.addProperty("token",token);
            jo.addProperty("terminal_id",terminal_id);
        }


        Log.d("monitorActivityPage",jo.toString());
        OkHttpUtils.post(devicesDataApi,jo.toString(),new OkHttpCallback(){

            @Override
            public void onFinish(String status, String msg) {
                super.onFinish(status, msg);

                if (status.equals("success")){
                    if(msg.substring(0,1).equals("<")) {lisentener.onFails(); return;}
                    //创建json解析器
                    JsonParser parse=new JsonParser();
                    //生成json
                    JsonObject jo=(JsonObject)parse.parse(msg);
                    //获取data
                    if (jo.get("data").toString().equals("null")) return;
                    JsonObject jsdata=jo.get("data").getAsJsonObject();
                    //获取list array
                    if (jsdata.get("list").toString().equals("null")) return;
                    JsonArray jalist=jsdata.get("list").getAsJsonArray();

                    //要传输给js的json数据 obj保存的数组
                    JSONArray setResult=new JSONArray();

                    //遍历json array
                    for (int i=0;i<jalist.size();i++){

                        if (jalist.get(i).toString().equals("null")) continue;
                        JsonObject subObject=jalist.get(i).getAsJsonObject();


                        //保存json
                        JSONObject obj=new JSONObject();
                        try {
                            //获得想要的数据添加进json
                            if (subObject.get("id").toString().equals("null")) obj.put("id", "无数据");
                            else obj.put("id", subObject.get("id").getAsString());

                            if (subObject.get("alias").toString().equals("null")) obj.put("alias","无数据");
                            else obj.put("alias", subObject.get("alias").getAsString());

                            if (subObject.get("address").toString().equals("null")) obj.put("address", "无数据");
                            else obj.put("address", subObject.get("address").getAsString());

                            if ( subObject.get("keywords").toString().equals("null")) obj.put("keywords", "无数据");
                            else obj.put("keywords", subObject.get("keywords").getAsString());

                            if ( subObject.get("image").toString().equals("null")) obj.put("image", "--");
                            else obj.put("image", subObject.get("image").getAsString());

                            if(subObject.get("unit").toString().equals("null")) obj.put("unit", "--");
                            else obj.put("unit", subObject.get("unit").getAsString());

                            if(subObject.get("state").toString().equals("null")) obj.put("state", "--");
                            else obj.put("state", subObject.get("state").getAsString());

//                            if(subObject.get("updatetime").toString().equals("null")) obj.put("updatetime   ", "--");
//                            else obj.put("updatetime", subObject.get("updatetime").getAsString());

                            //判断
                            if (subObject.get("devicesData").toString().equals("null")) obj.put("content", "无数据");
                            else{
                                //获取devicesData
                                JsonObject devicesData=subObject.get("devicesData").getAsJsonObject();
                                if (devicesData.get("content").toString().equals("null")) obj.put("content", "--");
                                obj.put("content", devicesData.get("content").getAsString());

                                if (devicesData.get("updatetime").toString().equals("null")) obj.put("updatetime", "--");
                                obj.put("updatetime", devicesData.get("updatetime").getAsString());
                            }

                            //添加入json数组
                            setResult.put(obj);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    //监听回调数据
                    lisentener.onSeccess(setResult.toString());
                }
                else {
                    ToastUtil.AlentLongToast(context,"请检查网络连接！");
                    lisentener.onFails();
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


    /**
     * 第一次进页面默认获取第一个终端数据
     * 查找基地id对应的第一个终端id
     * @param base 登录时保存下来的所有基地列表
     * @param id 要获取数据的基地id
     */
    public void getListJSON(String base,String id){
        if(!base.equals("失败"))
        {

            try {
                JSONArray baseidlist=new JSONArray(base);

                for (int i=0;i<baseidlist.length();i++){
                    JSONObject jo=baseidlist.getJSONObject(i);
                    if(jo.get("id").toString().equals(id)){
                        JSONArray idlist= (JSONArray) jo.get("terminalList");

                        //for (int j=0;j<idlist.length();j++){
                        JSONObject terminal_jo=idlist.getJSONObject(0);
                        terminal_id=terminal_jo.get("id").toString();

                        //}
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }



        jo.addProperty("token",token);
        jo.addProperty("terminal_id",terminal_id);
    }

}
