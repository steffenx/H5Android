package com.example.steffenxuan.yncloudapp.mainBody.view;

/**
 * Created by Steffen_xuan on 2019/5/29.
 * 专门获取用户输入信息，以及界面变化回调
 */

public interface ILoginView extends IView {
    String getUserInfo();//获取用户信息
    void onLoginSeccess();//成功
    void onLoginFails();//失败

}
