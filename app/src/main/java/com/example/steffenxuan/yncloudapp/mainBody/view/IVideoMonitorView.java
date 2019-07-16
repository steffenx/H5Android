package com.example.steffenxuan.yncloudapp.mainBody.view;

/**
 * Created by Steffen_xuan on 2019/6/5.
 */

public interface IVideoMonitorView extends IView{
    String getUserInfo();//获取用户信息
    void onGetDataSeccess(String postIotData);//成功
    void onGetDataFails();//失败
}
