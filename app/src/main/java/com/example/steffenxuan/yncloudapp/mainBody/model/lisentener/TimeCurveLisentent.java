package com.example.steffenxuan.yncloudapp.mainBody.model.lisentener;

/**
 * Created by Steffen_xuan on 2019/6/19.
 */

public interface TimeCurveLisentent {
    void onSeccess(String postIotData ,String tid);
    void onFails();
}
