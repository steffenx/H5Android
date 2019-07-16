package com.example.steffenxuan.yncloudapp.mainBody.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.steffenxuan.yncloudapp.R;
import com.example.steffenxuan.yncloudapp.core.util.CheckPermissionUtil;
import com.example.steffenxuan.yncloudapp.core.util.Des3Util;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesUtil;
import com.example.steffenxuan.yncloudapp.mainBody.presenter.LoginPresenter;
import com.example.steffenxuan.yncloudapp.mainBody.view.ILoginView;
import com.example.steffenxuan.yncloudapp.sqLite.DatabaseOperation_userInfo;
import com.google.gson.JsonObject;

import java.util.Calendar;

/**
 * @author:     yyx
 * @date:
 * @activity：
 * @version:
 * @project:    云平台物联网系统
 * @description:  启动页
 */
public class startup_page extends AppCompatActivity implements ILoginView {
    private String TAG="startup_page";
    private static String userdata;
    private LoginPresenter loginPresenter;
    public static  startup_page startupActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_page);
        startupActivity=this;
        setData();//持有p层login
        //openThread();
        new Thread(new MyRunnable()).start();

        //广告页面
//        Integer time = 1000;    //设置等待时间，单位为毫秒
//        Handler handler = new Handler();
//        //当计时结束时，跳转至主界面
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(new Intent(startup_page.this, Login_page.class));
//                startup_page.this.finish();
//                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
//            }
//        }, time);

    }
    private void setData() {
        this.loginPresenter = new LoginPresenter(this);
    }





    public class MyRunnable implements Runnable {

        private String TAG="startup_page";
        //private String userdata;
       // private LoginPresenter loginPresenter;

        @Override
        public void run() {







            //耗时任务，比如加载网络数据
            //读取偏好设置
            String account=(String) SharedPreferencesUtil.getmInstance().get(startup_page.this,"account","失败");
            String password=(String) SharedPreferencesUtil.getmInstance().get(startup_page.this,"password","失败");

            if(account!="失败"&&password!="失败")
            {



                //解密
                String newpsa= Des3Util.decrypt(account,password);
                //创建json
                JsonObject jo=new JsonObject();
                jo.addProperty("account",account);
                jo.addProperty("password",newpsa);
                userdata=jo.toString();//转换为json格式


                Calendar calendar = Calendar.getInstance();
                //日
                Integer day = calendar.get(Calendar.DAY_OF_MONTH);

                Integer logintime=(Integer) SharedPreferencesUtil.getmInstance().get(startupActivity,"logintime",100);
                Log.d("loginpage", String.valueOf(logintime));
                if(logintime==100){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Jump(Login_page.class);
                        }
                    });


                }
                else if (logintime+1<day){
                    loginPresenter.Login(startupActivity);
                    if(day==30||day==31) SharedPreferencesUtil.getmInstance().put(startupActivity,"logintime",0);
                    else SharedPreferencesUtil.getmInstance().put(startupActivity,"logintime",day);
                    Log.d("loginpage","登录请求");

                }else{
                    //检查token是否过期
                    String token= DatabaseOperation_userInfo.getInstance().getToken(startupActivity,account);
                    JsonObject jst=new JsonObject();
                    jst.addProperty("token",token);
                    loginPresenter.checkToken(startupActivity,jst.toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Jump(CloudMainActivity.class);
                        }
                    });

                    //Jump(CloudMainActivity.class);
                    Log.d("loginpage","无登陆");
                }
                //Jump(CloudMainActivity.class);
                //loginPresenter.Login(startup_page.this);
                Log.d(TAG,userdata);


            }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Jump(Login_page.class);
                    }
                });

            }

        }
    }



    public void openThread(){
        //启动页面
        new Thread(new Runnable() {
            @Override
            public void run() {
                //耗时任务，比如加载网络数据
                //读取偏好设置
                String account=(String) SharedPreferencesUtil.getmInstance().get(startup_page.this,"account","失败");
                String password=(String) SharedPreferencesUtil.getmInstance().get(startup_page.this,"password","失败");

                if(account!="失败"&&password!="失败")
                {



                    //解密
                    String newpsa= Des3Util.decrypt(account,password);
                    //创建json
                    JsonObject jo=new JsonObject();
                    jo.addProperty("account",account);
                    jo.addProperty("password",newpsa);
                    userdata=jo.toString();//转换为json格式
                    setData();//持有p层login

                    Calendar calendar = Calendar.getInstance();
                    //日
                    Integer day = calendar.get(Calendar.DAY_OF_MONTH);

                    Integer logintime=(Integer) SharedPreferencesUtil.getmInstance().get(startup_page.this,"logintime",100);
                    Log.d("loginpage", String.valueOf(logintime));
                    if(logintime==100){

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Jump(Login_page.class);
                            }
                        });
                    }
                    else if (logintime+1<day){
                        loginPresenter.Login(startup_page.this);
                        if(day==30||day==31) SharedPreferencesUtil.getmInstance().put(startup_page.this,"logintime",0);
                        else SharedPreferencesUtil.getmInstance().put(startup_page.this,"logintime",day);
                        Log.d("loginpage","登录请求");
                    }else{
                        //检查token是否过期
                        String token= DatabaseOperation_userInfo.getInstance().getToken(startup_page.this,account);
                        JsonObject jst=new JsonObject();
                        jst.addProperty("token",token);
                        loginPresenter.checkToken(startup_page.this,jst.toString());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Jump(CloudMainActivity.class);
                            }
                        });
                        //Jump(CloudMainActivity.class);
                        Log.d("loginpage","无登陆");
                    }
                    //Jump(CloudMainActivity.class);
                    //loginPresenter.Login(startup_page.this);
                    Log.d(TAG,userdata);

                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Jump(Login_page.class);
                        }
                    });
                }

            }
        }).start();
    }

    /**
     * 界面跳转
     * @param context
     */
    public  void Jump(Class context){
        //跳转至
        Intent intent = new Intent(startup_page.this, context);
        startActivity(intent);
        //结束当前的 Activity
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        startup_page.this.finish();
    }

    @Override
    public String getUserInfo() {
        return userdata;
    }

    @Override
    public void onLoginSeccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Jump(CloudMainActivity.class);
            }
        });
    }

    @Override
    public void onLoginFails() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Jump(Login_page.class);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TimeCurveActivityDebug", "onDestroy: "+"54");
        //this.loginPresenter.onDestory();
        this.loginPresenter = null;//解除持有关系
        this.finish();

        if (userdata!=null){
            userdata=null;
        }
        startupActivity=null;
        //android.os.Process.killProcess(android.os.Process.myPid());
        System.gc();

    }
}
