package com.example.steffenxuan.yncloudapp.mainBody.view;

/**
 * Created by Steffen_xuan on 2019/6/3.
 */

public interface IIotDataView extends IView {
    String getUserInfo();//获取用户信息
    void onGetDataSeccess(String postIotData);//成功
    void onGetDataFails();//失败
}
