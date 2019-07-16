package com.example.steffenxuan.yncloudapp.mainBody.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.steffenxuan.yncloudapp.R;
import com.example.steffenxuan.yncloudapp.core.HtmlUrl;
import com.example.steffenxuan.yncloudapp.core.util.NativeWebViewUtil;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesUtil;
import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.presenter.IotDataPresenter;
import com.example.steffenxuan.yncloudapp.mainBody.presenter.LoginPresenter;
import com.example.steffenxuan.yncloudapp.mainBody.view.IIotDataView;
import com.google.gson.JsonObject;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Iot_DataActivity extends AppCompatActivity implements IIotDataView {

    private WebView webview;
    private android.support.v7.widget.Toolbar toolbar;
    private IotDataPresenter iotDataPresenter;
    private String a="0";
    //private TitleBar mTitleBar;
    private String idd;//基地id
    private SwipeRefreshLayout mSwipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_data);

        initView();//初始化控件
        setPreenter();//设置p层
        initMixedPage();//初始化H5页面
        setActionBar();//设置title

    }

    private void setPreenter() {
        this.iotDataPresenter = new IotDataPresenter(this);
    }

    private void initView() {
        webview = findViewById(R.id.webview);
        webview.setWebChromeClient(new WebChromeClient());//允许js弹窗
        toolbar =  findViewById(R.id.toolbar);
        mSwipe =  findViewById(R.id.swipelayout);
        setSwipe();
        //mTitleBar=findViewById(R.id.mTitleBar);

    }

    /**
     * 设置下拉刷新球
     */
    private void setSwipe() {
        /*
        * 设置下拉刷新的颜色
        */
        mSwipe.setColorSchemeResources(R.color.swipeColor1,R.color.swipeColor2,R.color.swipeColor3,R.color.swipeColor4,R.color.swipeColor5);
        /*
        * 设置下拉刷新的监听
        */
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                iotDataPresenter.IotData(Iot_DataActivity.this);
            }
        });
        mSwipe.measure(0,0);
        mSwipe.setRefreshing(true);
    }


    public void SwipeIsFinish(){
        if (mSwipe.isRefreshing()) {
            mSwipe.setRefreshing(false);
            ToastUtil.AlentTOPShortToast(this,"已为您加载最新数据");
        }
    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        /*显示Home图标*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("物联数据");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @SuppressLint("JavascriptInterface")
    private void initMixedPage() {

        //封装webview
        NativeWebViewUtil nativeWebViewUtil = new NativeWebViewUtil();
        nativeWebViewUtil.WebViewSetting(this,webview);
        //添加Javascript的映射
        webview.addJavascriptInterface(this,"android");
        webview.loadUrl(HtmlUrl.IotDataHtmlUrl);
        webview.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 开始加载页面时

                iotDataPresenter.IotData(Iot_DataActivity.this);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String base=(String) SharedPreferencesUtil.getmInstance().get(Iot_DataActivity.this,"base","失败");
                if(base.equals("失败")) return;

                // 加载结束
                webview.evaluateJavascript("javascript:get_android_base("+base+","+idd+")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //此处为 js 返回的结果
                        Log.v("Native",value);
                    }
                });
            }
        });
//        mTitleBar.setOnTitleBarListener(new OnTitleBarListener() {
//            @Override
//            public void onLeftClick(View v) {
//                onBackPressed();
//            }
//
//            @Override
//            public void onTitleClick(View v) {
//
//            }
//
//            @Override
//            public void onRightClick(View v) {
//
//            }
//        });

    }


    @JavascriptInterface
    public void set_jsdata(String terminal_id){
        Log.d("出来的时刻",terminal_id);
        iotDataPresenter.IotDataTerminal(Iot_DataActivity.this,terminal_id);
    }

    @JavascriptInterface
    public void set_android_devices(String devices_id,String devices_content){
        Intent i=new Intent();
        i.putExtra("devices_id",devices_id);
        i.putExtra("devices_content",devices_content);
        i.setClass(Iot_DataActivity.this,TimeCurveActivity.class);
        startActivity(i);
    }


    @Override
    public String getUserInfo() {
        String account=(String) SharedPreferencesUtil.getmInstance().get(this,"account","失败");
        Bundle b = getIntent().getBundleExtra("id");
        idd = b.getString("terminal_id");
        Log.d("IotControlActivityDebug", "getUserInfo: "+idd);
        Log.d("Ssss",idd);
        return account+"#"+idd;
    }

    @Override
    public void onGetDataSeccess(final String postIotData) {

        Log.d("Ss",postIotData);
        Message msg=new Message();
        msg.what=0;
        Bundle bundle=new Bundle();
        bundle.putString("msg",postIotData);
        msg.setData(bundle);
        handler.sendMessage(msg);

    }

    @Override
    public void onGetDataFails() {
        //ToastUtil.AlentLongToast(this,"数据获取失败");
        SwipeIsFinish();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.d("Ssss",msg.getData().get("msg").toString());
            webview.evaluateJavascript("javascript:get_android_data("+msg.getData().get("msg").toString()+")", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //此处为 js 返回的结果
                    Log.v("Native",value);
                }
            });
            SwipeIsFinish();
            super.handleMessage(msg);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_iotdata_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;
            case R.id.action_control:
                Intent i=new Intent();
                Bundle b=new Bundle();
                b.putString("baseid",idd);
                i.putExtra("id",b);
                i.setClass(Iot_DataActivity.this,IotControlActivity.class);
                startActivity(i);
                break;
            case R.id.action_contro2:
                Intent i2=new Intent();
                Bundle b2=new Bundle();
                b2.putString("baseid",idd);
                i2.putExtra("id",b2);
                i2.setClass(Iot_DataActivity.this,DeviceInfoActivity.class);
                startActivity(i2);
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.iotDataPresenter.onDestory();
        this.iotDataPresenter = null;//解除持有关系
        webview.clearHistory(); //清除历史
        webview.removeAllViews();
        webview.removeAllViews();
        webview.destroy();
        webview=null;
        handler.removeCallbacksAndMessages(null);
        handler.removeCallbacks(null);
        handler=null;
        mSwipe=null;
        System.gc();
    }



}
