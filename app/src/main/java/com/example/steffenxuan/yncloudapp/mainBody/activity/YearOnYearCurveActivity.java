package com.example.steffenxuan.yncloudapp.mainBody.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.steffenxuan.yncloudapp.R;
import com.example.steffenxuan.yncloudapp.core.HtmlUrl;
import com.example.steffenxuan.yncloudapp.core.util.NativeWebViewUtil;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesUtil;
import com.example.steffenxuan.yncloudapp.mainBody.presenter.TimeCurvePresenter;
import com.example.steffenxuan.yncloudapp.mainBody.presenter.YearOnYearCurvePresenter;
import com.example.steffenxuan.yncloudapp.mainBody.view.IYearOnYearCurveView;
import com.example.steffenxuan.yncloudapp.sqLite.DatabaseOperation_userInfo;
import com.google.gson.JsonObject;

public class YearOnYearCurveActivity extends AppCompatActivity implements IYearOnYearCurveView {

    private WebView webview;
    private android.support.v7.widget.Toolbar toolbar;
    private String devices_content;//传感器数据名称

    private YearOnYearCurvePresenter yearOnYearCurvePresenter;

    private String tid="0";//终端id

    private String TAG="YearOnYearActivityDebug";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year_on_year_curve);

        initView();
        setPreenter();

        initMixedPage();
        setActionBar();
    }

    private void initView() {
        webview=(WebView)findViewById(R.id.webview);
        webview.setWebChromeClient(new WebChromeClient());//允许js弹窗
        toolbar =  findViewById(R.id.toolbar);
    }
    private void setPreenter() {
        this.yearOnYearCurvePresenter = new YearOnYearCurvePresenter(this);
    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        /*显示Home图标*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("数据同比");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_timecurve_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String name=(String) item.getTitle();
        getSupportActionBar().setTitle(name);
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;
            case R.id.action_control:
                Intent i=new Intent();
                i.putExtra("devices_id",getIntent().getStringExtra("devices_id"));
                i.putExtra("devices_content",getIntent().getStringExtra("devices_content"));
                i.setClass(YearOnYearCurveActivity.this,TimeCurveActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.action_contro3:
                Intent i1=new Intent();
                i1.putExtra("devices_id",getIntent().getStringExtra("devices_id"));
                i1.putExtra("devices_content",getIntent().getStringExtra("devices_content"));
                i1.setClass(YearOnYearCurveActivity.this,HistoryCurveActivity.class);
                startActivity(i1);
                finish();
                break;
        }
        return true;
    }


    @SuppressLint("JavascriptInterface")
    public void initMixedPage() {
        //封装webview
        NativeWebViewUtil nativeWebViewUtil = new NativeWebViewUtil();
        nativeWebViewUtil.WebViewSetting(this,webview);

        //添加Javascript的映射
        webview.addJavascriptInterface(this,"android");
        webview.loadUrl(HtmlUrl.yearonyearcurveHtmlUrl);

        webview.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 开始加载页面时

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                devices_content=getIntent().getStringExtra("devices_content");
                JsonObject name=new JsonObject();
                name.addProperty("devices_content",devices_content);
                // 加载结束
                webview.evaluateJavascript("javascript:get_android_name("+name.toString()+")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //此处为 js 返回的结果
                        Log.v("Native",value);
                    }
                });
            }
        });
    }


    @JavascriptInterface
    public void get_timeData(String firstStartTimeStamp ,String firstEndTimeStamp,
                                String secondStartTimeStamp ,String secondEndTimeStamp){
        Log.d(TAG, "get_timeData: "+firstStartTimeStamp);
        yearOnYearCurvePresenter.FirstYearOnYear(this,firstStartTimeStamp,firstEndTimeStamp);
        yearOnYearCurvePresenter.SecondYearOnYear(this,secondStartTimeStamp,secondEndTimeStamp);
    }

    @Override
    public String get_devices() {
        String devices_id= getIntent().getStringExtra("devices_id");
        String account=(String) SharedPreferencesUtil.getmInstance().get(YearOnYearCurveActivity.this,"account","失败");
        JsonObject jo=new JsonObject();
        if(!account.equals("失败")){
            String token= DatabaseOperation_userInfo.getInstance().getToken(this,account);

            jo.addProperty("token",token);
            jo.addProperty("devices_id",devices_id);

            return jo.toString();
        }
        else return "";
    }

    @Override
    public void onGetFirseDataSeccess(String postRealtimeData, String terminalid) {
        //Log.d(TAG, "onGetFirseDataSeccess: "+postRealtimeData);
        Message msg=new Message();
        msg.what=0;
        Bundle bundle=new Bundle();
        bundle.putString("msg",postRealtimeData);
        msg.setData(bundle);
        handler.sendMessage(msg);
        tid=terminalid;
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //Log.d(TAG, "onGetFirseDataSeccess: "+msg.getData().get("msg").toString());
            webview.evaluateJavascript("javascript:get_android_firstYear("+msg.getData().get("msg").toString()+")", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //此处为 js 返回的结果
                    Log.v("Native",value);
                }
            });

            super.handleMessage(msg);
        }
    };

    @Override
    public void onGetEndDataSeccess(String postRealtimeData) {
        Log.d(TAG, "onGetEndDataSeccess: "+postRealtimeData);
        Message msg=new Message();
        msg.what=0;
        Bundle bundle=new Bundle();
        bundle.putString("msg",postRealtimeData);
        msg.setData(bundle);
        secondhandler.sendMessage(msg);
    }

    Handler secondhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //Log.d(TAG, "onGetFirseDataSeccess: "+msg.getData().get("msg").toString());

            webview.evaluateJavascript("javascript:get_android_secondYear("+msg.getData().get("msg").toString()+")", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //此处为 js 返回的结果
                    Log.v("Native",value);
                }
            });
            super.handleMessage(msg);
        }
    };

    @Override
    public void onGetDataFails() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        webview.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webview.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.clearHistory(); //清除历史
        webview.removeAllViews();
        webview.destroy();
        secondhandler.removeCallbacksAndMessages(null);
        secondhandler.removeCallbacks(null);
        secondhandler=null;
        handler.removeCallbacksAndMessages(null);
        handler.removeCallbacks(null);
        handler=null;
        this.yearOnYearCurvePresenter.onDestory();
        this.yearOnYearCurvePresenter=null;
    }
}
