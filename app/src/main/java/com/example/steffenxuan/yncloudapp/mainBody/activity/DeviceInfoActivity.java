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
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.steffenxuan.yncloudapp.R;
import com.example.steffenxuan.yncloudapp.core.HtmlUrl;
import com.example.steffenxuan.yncloudapp.core.util.NativeWebViewUtil;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesUtil;
import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.presenter.DeviceInfoPresenter;
import com.example.steffenxuan.yncloudapp.mainBody.view.IDeviceInfoView;
import com.example.steffenxuan.yncloudapp.sqLite.DatabaseOperation_userInfo;

public class DeviceInfoActivity extends AppCompatActivity implements IDeviceInfoView {

    public String TAG="DeviceInfoActivityDebug";
    private WebView webview;
    private DeviceInfoPresenter deviceInfoPresenter;
    private android.support.v7.widget.Toolbar toolbar;
    private SwipeRefreshLayout mSwipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        initView();
        setPreenter();
        initMixedPage();
        setActionBar();
    }

    private void initView() {
        webview=(WebView)findViewById(R.id.webview);
        webview.setWebChromeClient(new WebChromeClient());//允许js弹窗
        toolbar =  findViewById(R.id.toolbar);
        mSwipe =  findViewById(R.id.swipelayout);
        setSwipe();
        //toolbar=findViewById(R.id.toolbar);
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
                deviceInfoPresenter.DeviceInfo(DeviceInfoActivity.this);
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

    private void setPreenter(){
        this.deviceInfoPresenter=new DeviceInfoPresenter(this);
    }

    @SuppressLint("JavascriptInterface")
    public void initMixedPage() {
        //封装webview
        NativeWebViewUtil nativeWebViewUtil = new NativeWebViewUtil();
        nativeWebViewUtil.WebViewSetting(this,webview);
        //添加Javascript的映射
        webview.addJavascriptInterface(this,"android");
        webview.loadUrl(HtmlUrl.deviceInfoHtmlUrl);
        deviceInfoPresenter.DeviceInfo(this);

    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        /*显示Home图标*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("设备概况");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public String getDataInfo() {
        Bundle b = getIntent().getBundleExtra("id");
        String base_id = b.getString("baseid");

        return base_id;
    }

    @Override
    public void onGetDataSeccess(String postIotData) {
        Message msg=new Message();
        msg.what=0;
        Bundle bundle=new Bundle();
        bundle.putString("msg",postIotData);
        msg.setData(bundle);
        handler.sendMessage(msg);
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
    public void onGetDataFails() {
        ToastUtil.AlentShortToast(this,"出现错误了！");
        SwipeIsFinish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webview.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.clearHistory(); //清除历史
        webview.removeAllViews();
        webview.removeAllViews();
        webview.destroy();

        handler.removeCallbacksAndMessages(null);

        handler.removeCallbacks(null);

        handler=null;
        this.deviceInfoPresenter.onDestory();
        this.deviceInfoPresenter = null;//解除持有关系
    }
}
