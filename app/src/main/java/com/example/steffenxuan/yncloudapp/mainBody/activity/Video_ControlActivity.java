package com.example.steffenxuan.yncloudapp.mainBody.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.widget.ProgressBar;

import com.example.steffenxuan.yncloudapp.R;
import com.example.steffenxuan.yncloudapp.core.HtmlUrl;
import com.example.steffenxuan.yncloudapp.core.util.NativeTBSX5WebViewUtil;
import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesUtil;
import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.presenter.VideoControlPresenter;
import com.example.steffenxuan.yncloudapp.mainBody.presenter.VideoMonitorPresenter;
import com.example.steffenxuan.yncloudapp.mainBody.view.IVideoControlView;
import com.example.steffenxuan.yncloudapp.sqLite.DatabaseOperation_userInfo;
import com.google.gson.JsonObject;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

public class Video_ControlActivity extends AppCompatActivity implements IVideoControlView {

    //private ProgressBar progressBar;
    private WebView webView;
    private String id;
    private String qaedas_id;
    private String json;
    private VideoControlPresenter videoControlPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_control);

        initView();//初始化控件
        setPreenter();
        initMixedPage();//初始化H5页面

    }

    private void setPreenter() {
        this.videoControlPresenter = new VideoControlPresenter(this);
    }

    private void initView() {
        //progressBar=findViewById(R.id.progressbar);
        webView=findViewById(R.id.webview);
    }

    private void initMixedPage() {
        NativeTBSX5WebViewUtil.WebViewSetting(this,webView);
        webView.addJavascriptInterface(this,"android");
        webView.loadUrl(HtmlUrl.videoControlHtmlUrl);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 开始加载页面时
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                id = bundle.getString("id");
                json=bundle.getString("json");
                qaedas_id=bundle.getString("terminal_id");
                Log.d("cotrolActivityPage",qaedas_id);
                JSONObject jo=new JSONObject();
                try {
                    jo.put("id",id);
                    jo.put("json",json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("cotrolActivityPage",jo.toString());
                // 加载结束
                webView.loadUrl("javascript:android_data("+jo.toString()+")");
            }
        });
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


    /**
     * 从js获取信息
     * @param control
     * @param getid
     */
    @JavascriptInterface
    public void get_data(String control,String getid){
        Log.d("monitorActivityPage",control);

        //基地id
        //String qaedas_id= (String) SharedPreferencesUtil.getmInstance().get(this,"川蒲生态农业","0");
        //手机号
        String mobile=(String)SharedPreferencesUtil.getmInstance().get(this,"account","0");
        //token
        String token= DatabaseOperation_userInfo.getInstance().getToken(this,mobile);
        //向上
        if(control.equals("up")){
            JsonObject jo =new JsonObject();
            jo.addProperty("token",token);
            jo.addProperty("qaedas_id",qaedas_id);
            jo.addProperty("video_id",getid);
            jo.addProperty("direction","0");
            jo.addProperty("speed","1");
            Log.d("monitorActivityPage",jo.toString());
            videoControlPresenter.VideoControl(this,jo.toString());
        }
        //向左
        if (control.equals("left")){
            JsonObject jo =new JsonObject();
            jo.addProperty("token",token);
            jo.addProperty("qaedas_id",qaedas_id);
            jo.addProperty("video_id",getid);
            jo.addProperty("direction","2");
            jo.addProperty("speed","1");
            videoControlPresenter.VideoControl(this,jo.toString());
        }
        //向右
        if (control.equals("right")){
            JsonObject jo =new JsonObject();
            jo.addProperty("token",token);
            jo.addProperty("qaedas_id",qaedas_id);
            jo.addProperty("video_id",getid);
            jo.addProperty("direction","3");
            jo.addProperty("speed","1");
            videoControlPresenter.VideoControl(this,jo.toString());
        }
        //向下
        if (control.equals("down")){
            JsonObject jo =new JsonObject();
            jo.addProperty("token",token);
            jo.addProperty("qaedas_id",qaedas_id);
            jo.addProperty("video_id",getid);
            jo.addProperty("direction","1");
            jo.addProperty("speed","1");
            videoControlPresenter.VideoControl(this,jo.toString());
        }
        //放大
        if (control.equals("enlarge")){
            JsonObject jo =new JsonObject();
            jo.addProperty("token",token);
            jo.addProperty("qaedas_id",qaedas_id);
            jo.addProperty("video_id",getid);
            jo.addProperty("direction","8");
            jo.addProperty("speed","1");
            videoControlPresenter.VideoControl(this,jo.toString());
        }
        //缩小
        if (control.equals("narrow")){
            JsonObject jo =new JsonObject();
            jo.addProperty("token",token);
            jo.addProperty("qaedas_id",qaedas_id);
            jo.addProperty("video_id",getid);
            jo.addProperty("direction","9");
            jo.addProperty("speed","1");
            videoControlPresenter.VideoControl(this,jo.toString());
        }
        //近焦
        if (control.equals("Nearcoke")){
            JsonObject jo =new JsonObject();
            jo.addProperty("token",token);
            jo.addProperty("qaedas_id",qaedas_id);
            jo.addProperty("video_id",getid);
            jo.addProperty("direction","10");
            jo.addProperty("speed","1");
            videoControlPresenter.VideoControl(this,jo.toString());
        }
        //远焦
        if (control.equals("Remotefocus")){
            JsonObject jo =new JsonObject();
            jo.addProperty("token",token);
            jo.addProperty("qaedas_id",qaedas_id);
            jo.addProperty("video_id",getid);
            jo.addProperty("direction","11");
            jo.addProperty("speed","1");
            videoControlPresenter.VideoControl(this,jo.toString());
        }
        //停止向上
        if (control.equals("stopup")){
            JsonObject jo =new JsonObject();
            jo.addProperty("token",token);
            jo.addProperty("qaedas_id",qaedas_id);
            jo.addProperty("video_id",getid);
            jo.addProperty("direction","0");
            videoControlPresenter.VideoStopControl(this,jo.toString());
        }
        //停止向右
        if (control.equals("stopright")){
            JsonObject jo =new JsonObject();
            jo.addProperty("token",token);
            jo.addProperty("qaedas_id",qaedas_id);
            jo.addProperty("video_id",getid);
            jo.addProperty("direction","3");
            videoControlPresenter.VideoStopControl(this,jo.toString());
        }
        //停止向左
        if (control.equals("stopleft")){
            JsonObject jo =new JsonObject();
            jo.addProperty("token",token);
            jo.addProperty("qaedas_id",qaedas_id);
            jo.addProperty("video_id",getid);
            jo.addProperty("direction","2");
            videoControlPresenter.VideoStopControl(this,jo.toString());
        }
        //停止向下
        if (control.equals("stopdown")){
            JsonObject jo =new JsonObject();
            jo.addProperty("token",token);
            jo.addProperty("qaedas_id",qaedas_id);
            jo.addProperty("video_id",getid);
            jo.addProperty("direction","1");
            videoControlPresenter.VideoStopControl(this,jo.toString());
        }
        //停止放大
        if (control.equals("enlargestop")){
            JsonObject jo =new JsonObject();
            jo.addProperty("token",token);
            jo.addProperty("qaedas_id",qaedas_id);
            jo.addProperty("video_id",getid);
            jo.addProperty("direction","8");
            videoControlPresenter.VideoStopControl(this,jo.toString());
        }

        //停止缩小
        if (control.equals("narrowstop")){
            JsonObject jo =new JsonObject();
            jo.addProperty("token",token);
            jo.addProperty("qaedas_id",qaedas_id);
            jo.addProperty("video_id",getid);
            jo.addProperty("direction","9");
            videoControlPresenter.VideoStopControl(this,jo.toString());
        }

        //停止近焦
        if (control.equals("Nearcokestop")){
            JsonObject jo =new JsonObject();
            jo.addProperty("token",token);
            jo.addProperty("qaedas_id",qaedas_id);
            jo.addProperty("video_id",getid);
            jo.addProperty("direction","10");
            videoControlPresenter.VideoStopControl(this,jo.toString());
        }

        //停止远焦
        if (control.equals("Remotefocusstop")){
            JsonObject jo =new JsonObject();
            jo.addProperty("token",token);
            jo.addProperty("qaedas_id",qaedas_id);
            jo.addProperty("video_id",getid);
            jo.addProperty("direction","11");
            videoControlPresenter.VideoStopControl(this,jo.toString());
        }

    }

    @Override
    public void onSeccess() {
        ToastUtil.AlentShortToast(this,"操作成功");
    }

    @Override
    public void onFails() {
        ToastUtil.AlentShortToast(this,"操作失败");
    }


    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
        webView.getSettings().setLightTouchEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView!=null){
            webView.clearHistory(); //清除历史
            webView.removeAllViews();
            webView.removeAllViews();
            webView.destroy();
        }
        if (json!=null){
            json=null;
        }
        this.videoControlPresenter.onDestory();
        this.videoControlPresenter=null;
        System.gc();
    }
}
