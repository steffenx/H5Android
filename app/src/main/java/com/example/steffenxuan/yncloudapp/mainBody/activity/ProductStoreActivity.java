package com.example.steffenxuan.yncloudapp.mainBody.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.steffenxuan.yncloudapp.R;
import com.example.steffenxuan.yncloudapp.core.HtmlUrl;
import com.example.steffenxuan.yncloudapp.core.util.NativeTBSX5WebViewUtil;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.tencent.smtt.sdk.WebView;

public class ProductStoreActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private WebView webView;
    private TitleBar mTitleBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_store);

        initView();
        initMixedPage();
    }

    private void initView() {
        progressBar = findViewById(R.id.progressbar);
        webView = findViewById(R.id.webview);
        mTitleBar=(TitleBar) findViewById(R.id.mTitleBar);
    }

    private void initMixedPage() {
        //封装X5 webview
        NativeTBSX5WebViewUtil.WebViewSetting(this,webView);
        webView.addJavascriptInterface(this,"android");
        webView.loadUrl(HtmlUrl.productshopHtmlUrl);

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
