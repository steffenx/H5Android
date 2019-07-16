package com.example.steffenxuan.yncloudapp.core.Service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.steffenxuan.yncloudapp.core.util.AppUpgradeUtil;


/**
 * Created by Steffen_xuan on 2019/6/27.
 */

public class UpdateService extends Service{

    private BroadcastReceiver receiver;//广播接收者

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 开始服务
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        receiverRegist();//注册广播
        AppUpgradeUtil.getInstance().downloadApkByDownloadManager(this);//开始下载
        return Service.START_STICKY;
    }


    //广播接收的注册
    public void receiverRegist() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //安装apk
                AppUpgradeUtil.getInstance().installApk(context);
                stopSelf(); //停止下载的Service
            }
        };
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(receiver, filter); //注册广播
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除注册
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
    }
}
