package com.example.steffenxuan.yncloudapp.mainBody.view;

/**
 * Created by Steffen_xuan on 2019/7/9.
 */

public interface IYearOnYearCurveView extends IView {
    String get_devices();//获取基地信息
    void onGetFirseDataSeccess(String postRealtimeData ,String tid);//成功
    void onGetEndDataSeccess(String postRealtimeData);
    void onGetDataFails();//失败
}
