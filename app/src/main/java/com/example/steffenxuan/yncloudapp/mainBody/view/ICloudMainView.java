package com.example.steffenxuan.yncloudapp.mainBody.view;

/**
 * Created by Steffen_xuan on 2019/6/3.
 */

public interface ICloudMainView extends IView {

    String getV_Code();//获取版本号
    void onGetDataSeccess(String apkurl);//成功
    void onGetDataFails();//失败

}
