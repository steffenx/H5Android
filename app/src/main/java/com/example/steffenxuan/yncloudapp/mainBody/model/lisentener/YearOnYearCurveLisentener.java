package com.example.steffenxuan.yncloudapp.mainBody.model.lisentener;

/**
 * Created by Steffen_xuan on 2019/7/11.
 */

public interface YearOnYearCurveLisentener {
    void onSeccess(String postIotData ,String tid);
    void onEndSeccess(String postIotData);
    void onFails();
}
