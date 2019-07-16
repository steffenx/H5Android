package com.example.steffenxuan.yncloudapp.mainBody.presenter;

import android.content.Context;

import com.example.steffenxuan.yncloudapp.mainBody.model.CloudMainModel;
import com.example.steffenxuan.yncloudapp.mainBody.model.IotControModel;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.CloudMainLisentener;
import com.example.steffenxuan.yncloudapp.mainBody.view.ICloudMainView;
import com.example.steffenxuan.yncloudapp.mainBody.view.IIotControView;
import com.example.steffenxuan.yncloudapp.mainBody.view.IView;

import java.lang.ref.WeakReference;

/**
 * Created by Steffen_xuan on 2019/6/27.
 */

public class CloudMainPresenter extends PresenterFather {
    public  CloudMainPresenter(ICloudMainView iCloudMainView) {
        this.pIModel = new CloudMainModel();
        this.pIView = new WeakReference<IView>(iCloudMainView);
    }

    public void judgmentUpgrade(Context context){
        if (pIModel != null && pIView != null && pIView.get() != null) {
            ICloudMainView iCloudMainView=(ICloudMainView) pIView.get();
            String v_code=iCloudMainView.getV_Code();
            ((CloudMainModel) pIModel).satrtRequestPost(context, v_code, new CloudMainLisentener() {
                @Override
                public void onSeccess(String apkurl) {
                    if (pIView!=null) {
                        if (pIView.get() != null) {
                            ((ICloudMainView) pIView.get()).onGetDataSeccess(apkurl);
                        }
                    }
                }

                @Override
                public void onFails() {
                    if (pIView!=null){
                        if (pIView.get()!=null){
                            ((ICloudMainView)pIView.get()).onGetDataFails();
                        }
                    }
                }
            });
        }
    }
}
