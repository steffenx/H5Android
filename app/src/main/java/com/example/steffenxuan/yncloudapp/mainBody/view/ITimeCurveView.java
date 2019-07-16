package com.example.steffenxuan.yncloudapp.mainBody.view;

/**
 * Created by Steffen_xuan on 2019/6/19.
 */

public interface ITimeCurveView extends IView {

    String get_devices();//获取基地信息
    void onGetDataSeccess(String postRealtimeData ,String tid);//成功
    void onrealtimeSeccess(String postRealtimeData);
    void onGetDataFails();//失败
}
