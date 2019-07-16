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
import android.widget.ProgressBar;

import com.example.steffenxuan.yncloudapp.R;
import com.example.steffenxuan.yncloudapp.core.HtmlUrl;
import com.example.steffenxuan.yncloudapp.core.util.NativeTBSX5WebViewUtil;
import com.example.steffenxuan.yncloudapp.core.util.NativeWebViewUtil;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesUtil;
import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.presenter.IotControPresenter;
import com.example.steffenxuan.yncloudapp.mainBody.presenter.TimeCurvePresenter;
import com.example.steffenxuan.yncloudapp.mainBody.view.ITimeCurveView;
import com.example.steffenxuan.yncloudapp.sqLite.DatabaseOperation_userInfo;
import com.google.gson.JsonObject;

import java.util.Timer;
import java.util.TimerTask;


public class TimeCurveActivity extends AppCompatActivity implements ITimeCurveView{

    private WebView webview;
    private SwipeRefreshLayout mSwipe;
    private TimeCurvePresenter timeCurvePresenter;
    private String devices_content;
    private Timer timer;
    private TimerTask mtimerTask;
    private boolean isPause=false;//没有暂停
    private String tid="0";//终端id
    public String TAG="TimeCurveActivityDebug";
    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_curve);

        initView();
        setPreenter();
        setActionBar();
        initMixedPage();
    }


    private void initView() {
        webview=(WebView)findViewById(R.id.webview);
        webview.setWebChromeClient(new WebChromeClient());//允许js弹窗
        toolbar =  findViewById(R.id.toolbar);
        mSwipe =  findViewById(R.id.swipelayout);
        setSwipe();
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
                timeCurvePresenter.TimeCurve(TimeCurveActivity.this);
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
        getSupportActionBar().setTitle("实时曲线");
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
            case R.id.action_contro3:
                Intent i=new Intent();
                i.putExtra("devices_id",getIntent().getStringExtra("devices_id"));
                i.putExtra("devices_content",getIntent().getStringExtra("devices_content"));
                i.setClass(TimeCurveActivity.this,HistoryCurveActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.action_contro2:
                Intent i1=new Intent();
                i1.putExtra("devices_id",getIntent().getStringExtra("devices_id"));
                i1.putExtra("devices_content",getIntent().getStringExtra("devices_content"));
                i1.setClass(TimeCurveActivity.this,YearOnYearCurveActivity.class);
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
        webview.loadUrl(HtmlUrl.realtimeHtmlUrl);
        webview.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 开始加载页面时
                timeCurvePresenter.TimeCurve(TimeCurveActivity.this);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
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

    private void setPreenter() {
        this.timeCurvePresenter = new TimeCurvePresenter(this);
    }


    /**
     * 获取实时数据
     * @param id 终端id
     */
    public void realdevices(final String id){
        destroyTimer();
        timer=new Timer();
        mtimerTask=new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timeCurvePresenter.realtimedata(TimeCurveActivity.this,id);
                    }
                });
            }
        };
        //每隔一秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行
        timer.schedule(mtimerTask,0,5000);
        isPause = false;//false 表示没有暂停（默认），true 表示暂停。
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Message msg=new Message();
//                msg.what=0;
//                Bundle bundle=new Bundle();
//                bundle.putString("msg",id);
//                msg.setData(bundle);
//                get_realdataHandler.sendMessage(msg);
//            }
//        },0,5000);//每隔一秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行

    }

    /**
     * 销毁计时器timer和timertask
     */
    public void destroyTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (mtimerTask != null) {
            mtimerTask.cancel();
            mtimerTask = null;
        }
    }

    /**
     * 暂停计时器
     */
    public void pauseTimer() {
        if (!isPause&&timer!=null) {
            isPause = true;
            timer.cancel();
        }
    }

    /**
     * 重开计时器
     */
    public void resumeTimer() {
        realdevices(tid);
        isPause = false;
    }

//    Handler get_realdataHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            if(msg.what == 0){
//                //Log.d(TAG,msg.getData().get("msg").toString());
//                timeCurvePresenter.realtimedata(TimeCurveActivity.this,msg.getData().get("msg").toString());
//            }
//        }
//    };


    /**
     * 向p层传递数据
     * @return
     */
    @Override
    public String get_devices() {
        String devices_id= getIntent().getStringExtra("devices_id");
        devices_content=getIntent().getStringExtra("devices_content");
        Log.d("出来的时刻", "set_devices: "+devices_content);
        return devices_id;
    }

    /**
     * 历史数据回调
     * @param postRealtimeData
     * @param terminalid 终端id
     */
    @Override
    public void onGetDataSeccess(String postRealtimeData , String terminalid) {
        Log.d(TAG, "handleMessage: "+postRealtimeData);
        Message msg=new Message();
        msg.what=0;
        Bundle bundle=new Bundle();
        bundle.putString("msg",postRealtimeData);
        msg.setData(bundle);
        handler.sendMessage(msg);
        realdevices(terminalid);//开启计时器 发送请求
        tid=terminalid;
        //Log.d(TAG, "onGetDataSeccess: "+postRealtimeData);
    }



    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //Log.d("Ssss",msg.getData().get("msg").toString());
            webview.evaluateJavascript("javascript:get_android_realtimedata("+msg.getData().get("msg").toString()+")", new ValueCallback<String>() {
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


    /**
     * 实时数据回调
     * @param postRealtimeData
     */
    @Override
    public void onrealtimeSeccess(String postRealtimeData) {

        Message msg=new Message();
        msg.what=0;
        Bundle bundle=new Bundle();
        bundle.putString("msg",postRealtimeData);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){

                webview.evaluateJavascript("javascript:get_android_onload("+msg.getData().get("msg").toString()+")", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //此处为 js 返回的结果
                        Log.v("Native",value);
                    }
                });
            }
        }
    };

    @Override
    public void onGetDataFails() {
        SwipeIsFinish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webview.onResume();
        if (timer!=null){
            resumeTimer();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        webview.onPause();
        pauseTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.clearHistory(); //清除历史
        webview.removeAllViews();
        webview.removeAllViews();
        webview.destroy();
        destroyTimer();
        mHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacks(null);
        handler.removeCallbacks(null);
        mHandler=null;
        handler=null;
        this.timeCurvePresenter.onDestory();
        this.timeCurvePresenter = null;//解除持有关系
        System.gc();
    }
}
