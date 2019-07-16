package com.example.steffenxuan.yncloudapp;

import android.app.Application;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by Steffen_xuan on 2019/6/2.
 */

public class myApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        QbSdkSelect();
    }

    /**
     * 初始化X5WebView
     */
    public void QbSdkSelect(){
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback callback = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg) {
                //x5內核初始化完成的回调，
                // true表示x5内核加载成功，
                // false表示x5内核加载失败，会自动切换到系统内核。
                Log.d("onViewInitFinished", "onViewInitFinished: "+arg);
            }

            @Override
            public void onCoreInitFinished() {
            }
        };

        QbSdk.initX5Environment(getApplicationContext(), callback);
    }
}
