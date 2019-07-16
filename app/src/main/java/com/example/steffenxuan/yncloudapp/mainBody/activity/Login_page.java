package com.example.steffenxuan.yncloudapp.mainBody.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.session.MediaSession;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.JavascriptInterface;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.steffenxuan.yncloudapp.R;
import com.example.steffenxuan.yncloudapp.core.HtmlUrl;
import com.example.steffenxuan.yncloudapp.core.util.NativeWebViewUtil;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesForWelcomeUtil;
import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.model.LoginModel;
import com.example.steffenxuan.yncloudapp.mainBody.presenter.LoginPresenter;
import com.example.steffenxuan.yncloudapp.mainBody.view.ILoginView;

/**
 * @author:     yyx
 * @date:   userInfo
 * @activity：   activity_login_page
 * @version:
 * @project:    云平台物联网系统
 * @description:  登录页
 * 实现登录view传输数据
 */
public class Login_page extends AppCompatActivity implements ILoginView{

    private WebView webview;
    private LoginPresenter loginPresenter;
    private String JS_userInfo;
    public static  Login_page loginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 判断是否是第一次开启应用
        isFirstRunApp();

        setContentView(R.layout.activity_login_page);

        loginActivity=this;

        initView();//初始化控件
        initMixedPage();//初始化H5页面
        setData();
    }

    /**
     * 是否第一次运行app
     */
    public void isFirstRunApp() {
        boolean isFirstOpen = SharedPreferencesForWelcomeUtil.getBoolean(this, SharedPreferencesForWelcomeUtil.FIRST_OPEN, true);
        // 如果是第一次启动，则先进入功能引导页
        if (isFirstOpen) {
            Intent intent = new Intent(this, welcome_page.class);
            startActivity(intent);
            finish();//当我们成功跳到b的时候，当我们点击功能菜单点击返回时，我们成功返回到了A的activity中
            return;
        }
    }


    @SuppressLint("JavascriptInterface")
    public void initMixedPage() {
        //封装webview
        NativeWebViewUtil nativeWebViewUtil = new NativeWebViewUtil();
        nativeWebViewUtil.WebViewSetting(this,webview);
        //添加Javascript的映射
        webview.addJavascriptInterface(this,"android");
        webview.loadUrl(HtmlUrl.loginHtmlUrl);
    }

    private void initView() {
        webview=(WebView)findViewById(R.id.webview);
        webview.setWebChromeClient(new WebChromeClient());//允许js弹窗
    }

    /**
     * javascript调用android
     * @data: h5页面用户输入的用户信息
     *
     */
    @JavascriptInterface
    public void get_data(String data){
        Log.d("出来的时刻",data);
        JS_userInfo=data;
        loginPresenter.Login(Login_page.this);

    }

    /**
     * 持有p层
     */
    private void setData() {
        this.loginPresenter = new LoginPresenter(this);
    }

    /**
     * 传输数据
     * @return
     */
    @Override
    public String getUserInfo() {
        return JS_userInfo;
    }

    /**
     * 验证通过回掉
     */
    @Override
    public void onLoginSeccess() {
        //消息线程
        Jump(CloudMainActivity.class);
        ToastUtil.AlentShortToast(Login_page.this,"登录成功！");
//        new Thread(){
//            public void run() {
//                Looper.prepare();
//                try {
//                    Thread.sleep(500);//延时1.5s
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//
//                Looper.loop();
//            };
//        }.start();

    }


    @Override
    public void onLoginFails() {
        //ToastUtil.AlentShortToast(Login_page.this,"账户或密码错误！");
        Message msg=new Message();
        msg.what=0;
        handler.sendMessage(msg);
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            webview.evaluateJavascript("javascript:seterror("+"0"+")", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //此处为 js 返回的结果
                    //Log.d("monitorActivityPage",value);
                }
            });
            super.handleMessage(msg);
        }
    };


    public  void Jump(Class context){
        //跳转至 MainActivity
        Intent intent = new Intent(Login_page.this, context);
        startActivity(intent);
        //结束当前的 Activity
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        Login_page.this.finish();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.loginPresenter.onDestory();
        this.loginPresenter = null;//解除持有关系
        webview.clearHistory(); //清除历史
        webview.removeAllViews();
        webview.removeAllViews();
        webview.destroy();
        handler.removeCallbacksAndMessages(null);
        handler.removeCallbacks(null);
        handler=null;
        loginActivity=null;
        System.gc();
    }
}
