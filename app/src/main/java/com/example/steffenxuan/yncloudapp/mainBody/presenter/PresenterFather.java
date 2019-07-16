package com.example.steffenxuan.yncloudapp.mainBody.presenter;

import com.example.steffenxuan.yncloudapp.mainBody.model.IModel;
import com.example.steffenxuan.yncloudapp.mainBody.view.IView;

import java.lang.ref.WeakReference;

/**
 * Created by Steffen_xuan on 2019/5/29.
 * 所有presenter的父类，因为presenter会持有View 以及Model部分
 */

public class PresenterFather {
    protected IModel pIModel;

    //弱引用
    protected WeakReference<IView> pIView;

}
