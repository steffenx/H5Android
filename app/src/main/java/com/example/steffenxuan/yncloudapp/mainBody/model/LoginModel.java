package com.example.steffenxuan.yncloudapp.mainBody.model;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpCallback;
import com.example.steffenxuan.yncloudapp.core.netUtil.OkHttpUtils;

import com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI;
import com.example.steffenxuan.yncloudapp.core.util.Des3Util;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesUtil;
import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.activity.Login_page;
import com.example.steffenxuan.yncloudapp.mainBody.activity.startup_page;
import com.example.steffenxuan.yncloudapp.mainBody.model.Login.loginUserInfo;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.LoginLisentener;
import com.example.steffenxuan.yncloudapp.sqLite.DataBaseManager;
import com.example.steffenxuan.yncloudapp.sqLite.DatabaseOperation_userInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.lang.annotation.Target;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.loginApi;
import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.qaedasDataApi;
import static com.example.steffenxuan.yncloudapp.core.netUtil.RequestAPI.tokenApi;

/**
 * Created by Steffen_xuan on 2019/5/29.
 * @data:   请求的json数据
 * @logolisentener: 登录监听
 */

public class LoginModel implements IModel {

    private final String TAG="LoginModel";
    private JsonParser parse =new JsonParser();//json解析器
    private JsonObject resultdata;//返回的json
    private static String logindata;//登录的信息
    private String account;
    private Context targetcontext;


    public void satrtRequestPost(String data, Context context, final LoginLisentener lisentener){
        logindata=data;
        targetcontext=context;

        /**
         * post请求体
         * @loginApi：请求的api
         * @OkHttpCallback： 回掉
         * */
        OkHttpUtils.post(loginApi,data,new OkHttpCallback(){
            /**
             * 请求后数据处理
             * @param status 请求状态
             * @param msg 请求返回信息
             */
            @Override
            public void onFinish(String status, String msg) {
                super.onFinish(status, msg);
                //如果请求成功
                if(status.equals("success")){
                    try {
                        if(msg.substring(0,1).equals("<")) {lisentener.onFails(); return;}
                        //解析返回json
                        resultdata=(JsonObject) parse.parse(msg);
                        Log.d(TAG, "onFinish: "+resultdata);
                        //如果返回code=1，登录成功
                        if(resultdata.get("code").getAsString().equals("1")){
                            //回掉监听
                            //lisentener.onSeccess();
                            resultdata=resultdata.get("data").getAsJsonObject();

                            //保存数据线程
                            new Thread(){
                                public void run() {
                                    try {
                                        //获取偏好设置电话号码
                                        String mobile=(String) SharedPreferencesUtil.getmInstance().get(targetcontext,"account","0");
                                        Log.d(TAG,mobile);
                                        //查询数据库中是否有本数据
                                        Boolean isMobile=DatabaseOperation_userInfo.getInstance().getIsMobile(targetcontext,mobile);
                                        if (isMobile){
                                            DatabaseOperation_userInfo.getInstance().updateUserInfo(targetcontext,dbsaveUserInfo(),mobile);
                                            String a=DatabaseOperation_userInfo.getInstance().getToken(targetcontext,mobile);
                                            Log.d(TAG,a);
                                        }
                                        else {
                                            DatabaseOperation_userInfo.getInstance().addUserInfo(dbsaveUserInfo(),targetcontext);
                                        }



                                    } catch (JsonIOException e) {
                                        e.printStackTrace ();
                                        Log.d(TAG,"保存数据失败");
                                    }

                                };
                            }.start();


                            //如果不是从启动页进来的话
                            if(targetcontext.hashCode()!= startup_page.class.hashCode()){


                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        savelogindata();//保存登录信息

                                        //添加登录时间
                                        Calendar calendar = Calendar.getInstance();
                                        //日
                                        Integer day = calendar.get(Calendar.DAY_OF_MONTH);

                                        if(day==30||day==31) SharedPreferencesUtil.getmInstance().put(targetcontext,"logintime",0);
                                        else SharedPreferencesUtil.getmInstance().put(targetcontext,"logintime",day);
                                    }
                                }).start();

                                JsonObject userinfo =resultdata.get("userinfo").getAsJsonObject();
                                //account=userinfo.get("mobile").getAsString();
                                String getToken=userinfo.get("token").getAsString();
                                RequestProjectId(targetcontext,getToken,lisentener);//请求基地获取id并存储
                                Log.d(TAG, "onFinish: "+account+getToken);



                                //SharedPreferencesUtil.getmInstance().put(targetcontext,"logintime",day);


                                //请求基地列表
//                                String mymobile=(String) SharedPreferencesUtil.getmInstance().get(targetcontext,"account","错误");
//                                if(!mymobile.equals("失败")){
//                                    RequestProjectId(targetcontext,mymobile,lisentener);//请求基地获取id并存储
//                                    Log.d(TAG, "onFinish: "+mymobile);
//                                }
//                                else {
//
//                                }

                            }



                        }else {
                            ToastUtil.AlentShortToast(targetcontext,"账户或密码错误！");
                            lisentener.onFails();
                        }
                    }catch (JsonIOException e){
                        e.printStackTrace();
                    }
                }else {
                    ToastUtil.AlentShortToast(targetcontext,"请检查网络连接！");
                    lisentener.onFails();
                }
            }

            /**
             * 请求失败
             * @param call
             * @param e
             */
            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                onFinish("failure", e.toString());
            }

            /**
             * 请求成功
             * @param call
             * @param response 返回json
             * @throws IOException
             */
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                super.onResponse(call, response);

                result = response.body().string().toString();

                onFinish("success", result);
            }
        });
    }

    /**
     * 请求基地id
     * @param context
     * @param mytoken
     */
    public void RequestProjectId(final Context context ,final String mytoken,final LoginLisentener lisentener){

                //String token=DatabaseOperation_userInfo.getInstance().getToken(context,mobile);
                JsonObject jo=new JsonObject();
                jo.addProperty("token",mytoken);
                Log.d(TAG, "ok: "+jo.toString());
                OkHttpUtils.post(qaedasDataApi,jo.toString(),new OkHttpCallback(){
                    @Override
                    public void onFinish(String status, String msg) {
                        super.onFinish(status, msg);

                        if (status.equals("success")){
                            Log.d(TAG, "ok: "+"获取基地");
                            if(msg.substring(0,1).equals("<")) { return;}
                            //解析json
                            JsonObject qaedasData=(JsonObject) parse.parse(msg);

                            //判断请求是否成功
                            if (qaedasData.get("code").getAsString().equals("1")){


                                //得到data
                                if(qaedasData.get("data").toString().equals("null")) return;
                                qaedasData=qaedasData.get("data").getAsJsonObject();

                                //得到list
                                if(qaedasData.get("list").toString().equals("null")) return;
                                final JsonArray qaedasDataList=qaedasData.get("list").getAsJsonArray();




                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {


                                        JsonObject qaedasID=qaedasDataList.get(0).getAsJsonObject();
                                        String defaultkey;
                                        Integer defaultvalue;
                                        if(qaedasID.get("name").toString().equals("null")) return;
                                        else {
                                            defaultkey=qaedasID.get("name").getAsString();
                                        }

                                        if(qaedasID.get("id").toString().equals("null")) return;
                                        else {
                                            defaultvalue=qaedasID.get("id").getAsInt();
                                        }
//                                        defaultkey="肖敏生态鱼养殖基地";
//                                        defaultvalue=22;
                                        Log.d("CloudMainActivityDebug", "onFinish: "+defaultvalue);
                                        Log.d("CloudMainActivityDebug", "onFinish: "+defaultkey);

                                        SharedPreferencesUtil.getmInstance().put(context,"defaultvalue",defaultvalue);
                                        SharedPreferencesUtil.getmInstance().put(context,"defaultkey",defaultkey);
                                        Log.d(TAG, "ok: "+"默认基地保存成功");

                                    }
                                }).start();



                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {


                                        JSONArray setResult=new JSONArray();

                                        for (int i=0;i<qaedasDataList.size();i++){
                                            //保存json
                                            JSONObject obj=new JSONObject();

                                            try {

                                                //遍历
                                                if (qaedasDataList.get(i).toString().equals("null")) continue;
                                                JsonObject subObject=qaedasDataList.get(i).getAsJsonObject();

                                                //得到key value
                                                String key,value;
                                                if(subObject.get("name").toString().equals("null")) continue;
                                                else {
                                                    key=subObject.get("name").getAsString();
                                                    obj.put("name",key);
                                                }

                                                if(subObject.get("id").toString().equals("null")) continue;
                                                else {
                                                    value=subObject.get("id").getAsString();
                                                    obj.put("id",value);
                                                }

                                                //得到终端
                                                if(subObject.get("terminalList").toString().equals("null")) continue;
                                                JsonArray terminalList=subObject.get("terminalList").getAsJsonArray();
                                                JSONArray terminalListResult=new JSONArray();
                                                for (int j=0;j<terminalList.size();j++){
                                                    if (terminalList.get(j).toString().equals("null")) continue;
                                                    JsonObject terminalObject=terminalList.get(j).getAsJsonObject();
                                                    JSONObject terminalResult=new JSONObject();
                                                    if (terminalObject.get("id").toString().equals("null")) continue;
                                                    else  terminalResult.put("id",terminalObject.get("id").getAsString());
                                                    if (terminalObject.get("name").toString().equals("null")) continue;
                                                    else  terminalResult.put("name",terminalObject.get("name").getAsString());
                                                    terminalListResult.put(terminalResult);

                                                }
                                                obj.put("terminalList",terminalListResult);
                                                //存偏好设置
                                                SharedPreferencesUtil.getmInstance().put(context,key,value);
                                                //保存json偏好
                                                setResult.put(obj);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        SharedPreferencesUtil.getmInstance().put(context,"base",setResult.toString());
                                        Log.d(TAG, "ok: "+"基地保存成功");

                                    }
                                }).start();


                                Log.d(TAG, "ok: "+"登录成功");
                                lisentener.onSeccess();

                            }
                            else {
                                lisentener.onFails();
                            }
                        }
                        else {
                            ToastUtil.AlentShortToast(context,"请重新登录！");
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                        onFinish("failure", e.toString());
                        Log.d(TAG,"失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        super.onResponse(call, response);

                        result = response.body().string().toString();

                        onFinish("success", result);
                    }
                });


    }


    /**
     * 判断token是否可用
     * @param context
     * @param urldata
     * @param lisentener
     */
    public void RequestcheckToken(final Context context , final String urldata,final LoginLisentener lisentener){
        OkHttpUtils.post(tokenApi,urldata,new OkHttpCallback(){

            @Override
            public void onFinish(String status, String msg) {
                super.onFinish(status, msg);
                if(status.equals("success")){
                    if(msg.substring(0,1).equals("<")) {lisentener.onFails(); return;}
                    //Log.d(TAG, "onFinish: "+msg.substring(0,1));
                    //解析返回json
                    resultdata=(JsonObject) parse.parse(msg);
                    //如果返回code=1，登录成功
                    if(resultdata.get("code").getAsString().equals("401")){
                        lisentener.onFails();
                    }else {
                        ToastUtil.AlentShortToast(context,"欢迎使用易农云！");
                        lisentener.onSeccess();

                    }
                }else ToastUtil.AlentShortToast(context,"请检查网络连接！");
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

    /**
     * 保存数据库
     * @return
     */
    public loginUserInfo dbsaveUserInfo(){
        //解析回传json中data数据

        //解析回传json中userinfo数据
        JsonObject userinfo =resultdata.get("userinfo").getAsJsonObject();

        //用户信息对象
        loginUserInfo info=new loginUserInfo();
        info.setUser_id(userinfo.get("id").getAsInt());
        info.setUsername(userinfo.get("username").getAsString());
        info.setNickname(userinfo.get("nickname").getAsString());
        info.setMobile(userinfo.get("mobile").getAsString());
        info.setAvatar(userinfo.get("avatar").getAsString());
        info.setScore(userinfo.get("score").getAsInt());
        info.setCity(userinfo.get("city").getAsString());
        info.setAddress(userinfo.get("address").getAsString());
        info.setCompany(userinfo.get("company").getAsString());
        info.setPlatform(userinfo.get("platform").getAsString());
        info.setToken(userinfo.get("token").getAsString());
        info.setCreatetime(userinfo.get("createtime").getAsString());
        Log.d(TAG, "dbsaveUserInfo: "+"数据库保存成功");
        return info;

    }

    /**
     * 保存用户偏好
     * @return
     */
    public boolean savelogindata(){
        try {

            JSONObject jsd=new JSONObject(logindata);
            Log.d(TAG, "savelogindata: "+resultdata.get("userinfo"));
            //得到用户名密码
            JsonObject userinfo =resultdata.get("userinfo").getAsJsonObject();
            account=userinfo.get("mobile").getAsString();
            //account=jsd.getString("account");
            String password=jsd.getString("password");
            String enStr = Des3Util.encrypt(account, password);//加密

            //保存
            SharedPreferencesUtil.getmInstance().put(targetcontext,"account",account);
            SharedPreferencesUtil.getmInstance().put(targetcontext,"password",enStr);

            Log.d(TAG,"用户偏好保存成功");
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"用户偏好保存失败");
            return false;
        }
    }
}
