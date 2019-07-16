package com.example.steffenxuan.yncloudapp.mainBody.presenter;

import android.content.Context;

import com.example.steffenxuan.yncloudapp.mainBody.model.DeviceInfoModel;
import com.example.steffenxuan.yncloudapp.mainBody.model.IotControModel;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.DeviceInfoLisentener;
import com.example.steffenxuan.yncloudapp.mainBody.view.IDeviceInfoView;
import com.example.steffenxuan.yncloudapp.mainBody.view.IIotControView;
import com.example.steffenxuan.yncloudapp.mainBody.view.IView;

import java.lang.ref.WeakReference;

/**
 * Created by Steffen_xuan on 2019/6/24.
 */

public class DeviceInfoPresenter extends PresenterFather {

    public  DeviceInfoPresenter(IDeviceInfoView iDeviceInfoView) {
        this.pIModel = new DeviceInfoModel();
        this.pIView = new WeakReference<IView>(iDeviceInfoView);
    }

    public void DeviceInfo(Context context){
        if (pIModel != null && pIView != null && pIView.get() != null) {

            IDeviceInfoView iDeviceInfoView=(IDeviceInfoView) pIView.get();
            String token=iDeviceInfoView.getDataInfo();

            if (!token.equals("0")){
                ((DeviceInfoModel) pIModel).satrtRequestPost(context, token, new DeviceInfoLisentener() {
                    @Override
                    public void onSeccess(String postIotData) {
                        if (pIView!=null) {
                            if (pIView.get() != null) {
                                ((IDeviceInfoView) pIView.get()).onGetDataSeccess(postIotData);
                            }
                        }
                    }

                    @Override
                    public void onFails() {
                        if (pIView!=null) {
                            if (pIView.get() != null) {
                                ((IDeviceInfoView) pIView.get()).onGetDataFails();
                            }
                        }
                    }
                });
            }

        }
    }

    public void onDestory(){
        if (this.pIModel!=null){
            this.pIModel = null;
        }
        if (this.pIView!=null){
            this.pIView=null;
        }
        System.gc();
    }
}
