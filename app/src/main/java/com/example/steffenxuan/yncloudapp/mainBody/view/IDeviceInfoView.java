package com.example.steffenxuan.yncloudapp.mainBody.view;

/**
 * Created by Steffen_xuan on 2019/6/24.
 */

public interface IDeviceInfoView extends IView {

    String getDataInfo();//获取基地信息
    void onGetDataSeccess(String postIotData);//成功
    void onGetDataFails();//失败
}
