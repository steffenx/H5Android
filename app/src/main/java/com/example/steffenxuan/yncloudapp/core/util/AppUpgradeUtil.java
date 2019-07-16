package com.example.steffenxuan.yncloudapp.core.util;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.example.steffenxuan.yncloudapp.R;
import com.example.steffenxuan.yncloudapp.core.Service.UpdateService;

import java.io.File;
import java.io.IOException;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by Steffen_xuan on 2019/6/27.
 */

public class AppUpgradeUtil {

    private static AppUpgradeUtil mInstance;
    private static String pathstr;
    private static String downloadApkUrl;
    private static final String apkName="newYNCloudApk";

    public static AppUpgradeUtil getInstance(){
        if (mInstance==null){
            synchronized (AppUpgradeUtil.class){
                if (mInstance==null){
                    mInstance=new AppUpgradeUtil();
                }
            }
        }
        return mInstance;
    }


    public String getVersionCode(Context context){
        String myVersion=" ";
        try {
            PackageManager packageManager=context.getPackageManager();
            PackageInfo packageInfo= null;
            packageInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            myVersion=packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return myVersion;
    }


    /**
     * 显示升级弹窗
     * @param context
     */
    public void showHintDialog(final Context context, final String downloadUrl){
        downloadApkUrl=downloadUrl;
        AlertDialog.Builder builder=new AlertDialog.Builder(context);

        builder.setIcon(R.mipmap.logo)
                .setTitle("版本升级")
                .setMessage("检测到当前有新版本，是否更新?")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //取消更新，则跳转到旧版本的APP的页面
                        ToastUtil.AlentShortToast(context,"暂不更新");
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //6.0以下系统，不需要请求权限,直接下载新版本的app
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            downloadApk(context,downloadUrl);
                        } else {
                            //6.0以上,先检查，申请权限，再下载
                            //checkPermission();
                            downloadApk(context,downloadUrl);
                        }

                    }
                }).create().show();
    }


    /**
     * 判断是否处于WiFi状态
     * getActiveNetworkInfo 是可用的网络，不一定是链接的，getNetworkInfo 是链接的。
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager manager = (ConnectivityManager)context. getSystemService(CONNECTIVITY_SERVICE);
        //NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        //处于WiFi连接状态
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }


    //下载最新版的app
    private void downloadApk(final Context context,String downloadUrl) {
        boolean isWifi = isWifi(context); //是否处于WiFi状态
        if (isWifi) {
            //启动服务
            context.startService(new Intent(context, UpdateService.class));

        } else {
            //弹出对话框，提示是否用流量下载
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
            builder.setTitle("提示");
            builder.setMessage("是否要用流量进行下载更新");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    ToastUtil.AlentShortToast(context,"取消下载");
                }
            });

            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //startService(new Intent(MainActivity.this, UpdateService.class));
                    //启动服务
                    context.startService(new Intent(context, UpdateService.class));
                    ToastUtil.AlentShortToast(context,"开始下载...");
                }
            });
            builder.setCancelable(false);

            android.support.v7.app.AlertDialog dialog = builder.create();
            //设置不可取消对话框
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

    }




    /*
    * 通过DownloadManager下载apk
    * @param context
    */
    public static void downloadApkByDownloadManager(Context context) {
        //开始下载最新版本的apk文件
        DownloadManager downloadManager = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);
        //DownloadManager实现下载
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadApkUrl));
        File file=new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),apkName);
        request.setTitle("易农云")
                .setDestinationUri(Uri.fromFile(file))
                //设置通知在下载中和下载完成都会显示
                //.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                //设置通知只在下载过程中显示，下载完成后不再显示
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        pathstr = file.getAbsolutePath();
        downloadManager.enqueue(request);
    }

    /**Apk的安装
     *
     * @param context
     */
    public static void installApk(Context context) {
        setPermission(pathstr);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //Android 7.0以上要使用FileProvider
        if (Build.VERSION.SDK_INT >= 24) {
            File file = (new File(pathstr));
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, "com.example.steffenxuan.yncloudapp", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(Environment.DIRECTORY_DOWNLOADS, apkName)), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);

    }


    //修改文件权限
    public static void setPermission(String absolutePath) {
        String command = "chmod " + "777" + " " + absolutePath;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
