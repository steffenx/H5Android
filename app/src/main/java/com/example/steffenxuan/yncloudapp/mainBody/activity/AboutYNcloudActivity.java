package com.example.steffenxuan.yncloudapp.mainBody.activity;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.steffenxuan.yncloudapp.R;
import com.example.steffenxuan.yncloudapp.core.HtmlUrl;
import com.example.steffenxuan.yncloudapp.core.util.NativeWebViewUtil;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

public class AboutYNcloudActivity extends AppCompatActivity {

    private WebView webview;
    private TitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_yncloud);
        initView();
        initMixedPage();
    }

    @SuppressLint("JavascriptInterface")
    public void initMixedPage() {
        //封装webview
        NativeWebViewUtil nativeWebViewUtil = new NativeWebViewUtil();
        nativeWebViewUtil.WebViewSetting(this,webview);
        //添加Javascript的映射
        webview.addJavascriptInterface(this,"android");
        webview.loadUrl(HtmlUrl.aboutHtmlUrl);
    }

    private void initView() {
        webview=(WebView)findViewById(R.id.webview);
        webview.setWebChromeClient(new WebChromeClient());//允许js弹窗
        mTitleBar=findViewById(R.id.mTitleBar);
        mTitleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                onBackPressed();
            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onRightClick(View v) {

            }
        });
    }
}
