package com.example.steffenxuan.yncloudapp.mainBody.view;

/**
 * Created by Steffen_xuan on 2019/6/17.
 */

public interface IIotControView extends IView {
    String getDataInfo();//获取基地信息
    void onGetDataSeccess(String postIotData);//成功
    void onGetDataFails();//失败
}
