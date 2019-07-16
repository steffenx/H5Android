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
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toolbar;

import com.example.steffenxuan.yncloudapp.R;
import com.example.steffenxuan.yncloudapp.core.HtmlUrl;
import com.example.steffenxuan.yncloudapp.core.util.NativeWebViewUtil;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesUtil;
import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.presenter.IotControPresenter;
import com.example.steffenxuan.yncloudapp.mainBody.presenter.IotDataPresenter;
import com.example.steffenxuan.yncloudapp.mainBody.view.IIotControView;
import com.example.steffenxuan.yncloudapp.mainBody.view.IView;
import com.example.steffenxuan.yncloudapp.sqLite.DatabaseOperation_userInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class IotControlActivity extends AppCompatActivity implements IIotControView {

    private WebView webview;
    private SwipeRefreshLayout mSwipe;
    private android.support.v7.widget.Toolbar toolbar;
    private IotControPresenter iotControPresenter;
    private String firstterminal_id="";
    private String token;


    private Timer timer;
    private TimerTask mtimerTask;
    private boolean isPause=false;//没有暂停
    
    private String TAG="IotControlActivityDebug";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot_control);

        initView();
        setActionBar();
        setPreenter();
        initMixedPage();

    }



    private void initView() {
        webview=(WebView)findViewById(R.id.webview);
        webview.setWebChromeClient(new WebChromeClient());//允许js弹窗
        toolbar=findViewById(R.id.toolbar);
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
                //iotControPresenter.IotControl(IotControlActivity.this);
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

    @SuppressLint("JavascriptInterface")
    public void initMixedPage() {
        //封装webview
        NativeWebViewUtil nativeWebViewUtil = new NativeWebViewUtil();
        nativeWebViewUtil.WebViewSetting(this,webview);
        //添加Javascript的映射
        webview.addJavascriptInterface(this,"android");
        webview.loadUrl(HtmlUrl.iotcontrolHtmlUrl);
        webview.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                String account=(String) SharedPreferencesUtil.getmInstance().get(IotControlActivity.this,"account","失败");
                if (!account.equals("失败")) {
                    token = DatabaseOperation_userInfo.getInstance().getToken(IotControlActivity.this, account);
                }
                // 开始加载页面时
                //iotControPresenter.IotControl(IotControlActivity.this);
                realControldata();
            }

        });
    }

    private void setPreenter() {
        this.iotControPresenter = new IotControPresenter(this);
    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        /*显示Home图标*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("物联控制");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        String base=(String)SharedPreferencesUtil.getmInstance().get(this,"base","失败");
        Bundle b = getIntent().getBundleExtra("id");
        String base_id = b.getString("baseid");
        if (base.equals("失败")) return false;

        try {
            JSONArray baseidlist=new JSONArray(base);

            for (int i=0;i<baseidlist.length();i++){
                JSONObject jo=baseidlist.getJSONObject(i);
                if(jo.get("id").toString().equals(base_id)){
                    JSONArray idlist= (JSONArray) jo.get("terminalList");

                    JSONObject terminal=idlist.getJSONObject(0);
                    firstterminal_id=terminal.get("id").toString();
                    for (int j=0;j<idlist.length();j++){
                    JSONObject terminal_jo=idlist.getJSONObject(j);

                        menu.add(1, Integer.parseInt(terminal_jo.getString("id")), 1,terminal_jo.get("name").toString() );//动态添加一个按钮；
                       // menu.add(1, Integer.parseInt(jo.getString("id")), 1, jo.getString("name"));
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "onOptionsItemSelected: "+"55");
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id!=android.R.id.home){
            Log.d(TAG, "onOptionsItemSelected: "+id);
            firstterminal_id=Integer.toString(id);
            iotControPresenter.IotControl(IotControlActivity.this);
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;
        }
        return true;
    }

    @JavascriptInterface
    public  void getJSdata(String id,String myswitch){
        Log.d(TAG, "getJSdata: "+id+","+myswitch);
        JSONObject jo=new JSONObject();
        try {
            jo.put("token",token);
            jo.put("id",id);
            jo.put("devicetype","final");
            jo.put("isSwitch",myswitch);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getJSdata: "+jo.toString());
        iotControPresenter.controlupdate(IotControlActivity.this,jo.toString());
    }

    @Override
    public String getDataInfo() {
        Bundle b = getIntent().getBundleExtra("id");
        String base_id = b.getString("baseid");
        Log.d(TAG, "getDataInfo: "+base_id+"---"+firstterminal_id);
        return firstterminal_id;
    }


    public void realControldata(){
        destroyTimer();
        timer=new Timer();
        mtimerTask=new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iotControPresenter.IotControl(IotControlActivity.this);
                    }
                });
            }
        };
        //每隔一秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行
        timer.schedule(mtimerTask,0,2000);
        isPause = false;//false 表示没有暂停（默认），true 表示暂停。

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
        realControldata();
        isPause = false;
    }

    @Override
    public void onGetDataSeccess(String postIotData) {
        Message msg=new Message();
        msg.what=0;
        Bundle bundle=new Bundle();
        bundle.putString("msg",postIotData);
        msg.setData(bundle);
        handler.sendMessage(msg);
        if (postIotData.equals("[]")){
            destroyTimer();
        }
        Log.d(TAG, "onOptionsItemSelected: "+postIotData);
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //Log.d("Ssss",msg.getData().get("msg").toString());
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
        //ToastUtil.AlentShortToast(this,"出现问题了！");
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
        this.iotControPresenter.onDestory();
        this.iotControPresenter = null;//解除持有关系
        destroyTimer();
        webview.clearHistory(); //清除历史
        webview.removeAllViews();
        webview.removeAllViews();
        webview.destroy();
        handler.removeCallbacksAndMessages(handler);
        handler.removeCallbacks(null);
        handler=null;
        System.gc();
    }
}
