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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.widget.ProgressBar;

import com.example.steffenxuan.yncloudapp.R;
import com.example.steffenxuan.yncloudapp.core.HtmlUrl;
import com.example.steffenxuan.yncloudapp.core.util.NativeTBSX5WebViewUtil;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesUtil;
import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.model.VideoControlModel;
import com.example.steffenxuan.yncloudapp.mainBody.presenter.VideoMonitorPresenter;
import com.example.steffenxuan.yncloudapp.mainBody.view.IVideoMonitorView;
import com.example.steffenxuan.yncloudapp.sqLite.DatabaseOperation_userInfo;
import com.google.gson.JsonObject;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class video_monitorActivity extends AppCompatActivity implements IVideoMonitorView {
    //private ProgressBar progressBar;
    private WebView webView;
    private SwipeRefreshLayout mSwipe;
    private VideoMonitorPresenter videoMonitorPresenter;
    private String TAG="video_monitorActivityPage";
    private String json;
    //private TitleBar mTitleBar;
    public String terminal_name="川蒲生态农业";
    private android.support.v7.widget.Toolbar toolbar;

    private String vcrID="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_monitor);

        initView();//初始化控件
        setPreenter();
        initMixedPage();//初始化H5页面
        setActionBar();

    }

    private void setPreenter() {
        this.videoMonitorPresenter = new VideoMonitorPresenter(this);
    }

    private void initView() {
        //progressBar = findViewById(R.id.progressbar);
        webView = findViewById(R.id.webview);
        //mTitleBar=(TitleBar) findViewById(R.id.mTitleBar);
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
                videoMonitorPresenter.videoMonitor(video_monitorActivity.this,vcrID);
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


    private void initMixedPage() {

        //封装X5 webview
        NativeTBSX5WebViewUtil.WebViewSetting(this,webView);
        webView.addJavascriptInterface(this,"android");
        webView.loadUrl(HtmlUrl.videoMonitorHtmlUrl);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                videoMonitorPresenter.videoMonitor(video_monitorActivity.this,vcrID);
            }
        });


    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        /*显示Home图标*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("物联监控");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id!=android.R.id.home){
            videoMonitorPresenter.videoMonitor(video_monitorActivity.this,Integer.toString(id));
            Log.d(TAG, "onOptionsItemSelected: "+Integer.toString(id));
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
    public void get_data(String page,String getid){
        if (page.equals("mcontrol")){

            Bundle b = getIntent().getBundleExtra("id");
            String terminal_id = b.getString("terminal_id");
            Intent i=new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("id",getid);
            bundle.putString("json",json);
            bundle.putString("terminal_id",terminal_id);

            i.putExtras(bundle);
            i.setClass(video_monitorActivity.this, Video_ControlActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView != null && webView.canGoBack()) {
            if ((keyCode == KeyEvent.KEYCODE_BACK) && webView != null && webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public String getUserInfo() {
        //String id= (String) SharedPreferencesUtil.getmInstance().get(this,"川蒲生态农业","错误");
        Bundle b = getIntent().getBundleExtra("id");
        String id = b.getString("terminal_id");
        terminal_name = b.getString("terminal_name");
        String mobile=(String)SharedPreferencesUtil.getmInstance().get(this,"account","0");
        if (!id.equals("错误")){
            String token= DatabaseOperation_userInfo.getInstance().getToken(this,mobile);

            JsonObject jo=new JsonObject();
            jo.addProperty("token",token);
            jo.addProperty("qaedas_id",id);
            Log.d(TAG,jo.toString());
            return jo.toString();
        }
        else {
            return "失败";
        }
    }

    @Override
    public void onGetDataSeccess(String postIotData) {
       Log.d("monitorActivityPage",postIotData);
        json=postIotData;
        //webView.loadUrl("javascript:android_data("+postIotData+")");
        JsonObject jj=new JsonObject();
            jj.addProperty("name",terminal_name);
        Message msg=new Message();
        msg.what=0;
        Bundle bundle=new Bundle();
        bundle.putString("msg",postIotData);
        bundle.putString("name",jj.toString());
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            webView.loadUrl("javascript:android_data_name("+msg.getData().get("name").toString()+")");
            webView.loadUrl("javascript:android_data("+msg.getData().get("msg").toString()+")");
            SwipeIsFinish();
            super.handleMessage(msg);
        }
    };





    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: "+"创建");
        menu.clear();
        String vcridobject=(String)SharedPreferencesUtil.getmInstance().get(this,"varInfo","失败");
        try {
            JSONArray vcridlist=new JSONArray(vcridobject);

            for (int i=0;i<vcridlist.length();i++){
                JSONObject jo=vcridlist.getJSONObject(i);

                menu.add(1, Integer.parseInt(jo.getString("id")), 1,jo.get("name").toString() );//动态添加一个按钮；
                // menu.add(1, Integer.parseInt(jo.getString("id")), 1, jo.getString("name"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "onOptionsItemSelected: "+"55");
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public void onGetDataFails() {
        SwipeIsFinish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
        webView.getSettings().setLightTouchEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.videoMonitorPresenter.onDestory();
        this.videoMonitorPresenter = null;//解除持有关系
        if (webView!=null){
            webView.clearCache(true); //清除缓存
            webView.clearHistory(); //清除历史
            webView.removeAllViews();
            webView.destroy();
        }
        if (json!=null){
            json=null;
        }
        handler.removeCallbacksAndMessages(null);
        handler.removeCallbacks(null);
        handler=null;
        //mTitleBar=null;progressBar=null;
        System.gc();
    }
}
