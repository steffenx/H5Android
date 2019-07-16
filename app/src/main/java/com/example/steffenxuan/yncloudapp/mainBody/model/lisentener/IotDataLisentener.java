package com.example.steffenxuan.yncloudapp.mainBody.model.lisentener;

/**
 * Created by Steffen_xuan on 2019/6/3.
 */

public interface IotDataLisentener {
    void onSeccess(String postIotData);
    void onFails();

}
