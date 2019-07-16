package com.example.steffenxuan.yncloudapp.mainBody.presenter;

import android.content.Context;
import android.util.Log;

import com.example.steffenxuan.yncloudapp.mainBody.model.IModel;
import com.example.steffenxuan.yncloudapp.mainBody.model.IotControModel;
import com.example.steffenxuan.yncloudapp.mainBody.model.IotDataModel;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.IotDataLisentener;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.IotcontroLisentener;
import com.example.steffenxuan.yncloudapp.mainBody.view.IIotControView;
import com.example.steffenxuan.yncloudapp.mainBody.view.IIotDataView;
import com.example.steffenxuan.yncloudapp.mainBody.view.IView;

import java.lang.ref.WeakReference;

/**
 * Created by Steffen_xuan on 2019/6/17.
 */

public class IotControPresenter extends PresenterFather {
    String TAG="IotControPresenter";
    public  IotControPresenter(IIotControView iIotControView) {
        this.pIModel = new IotControModel();
        this.pIView = new WeakReference<IView>(iIotControView);
    }

    public void IotControl(Context context){
        if (pIModel != null && pIView != null && pIView.get() != null) {
            Log.d(TAG, "IotControl: "+"kongzhi");
            IIotControView iIotControView=(IIotControView) pIView.get();
            String terminal_id=iIotControView.getDataInfo();
            ((IotControModel) pIModel).satrtRequestPost(context, terminal_id, new IotcontroLisentener() {
                @Override
                public void onSeccess(String postIotData) {
                    if (pIView!=null) {
                        if (pIView.get() != null) {
                            ((IIotControView) pIView.get()).onGetDataSeccess(postIotData);
                        }
                    }
                }

                @Override
                public void onFails() {
                    if (pIView!=null) {
                        if (pIView.get() != null) {
                            ((IIotControView) pIView.get()).onGetDataFails();
                        }
                    }
                }
            });
        }
    }

    public void controlupdate(Context context ,String data){
        if (pIModel != null && pIView != null && pIView.get() != null) {

            ((IotControModel) pIModel).devicesupdate(context, data, new IotcontroLisentener() {
                @Override
                public void onSeccess(String postIotData) {
                    if (pIView.get()!=null){
                        ((IIotControView)pIView.get()).onGetDataSeccess(postIotData);
                    }
                }

                @Override
                public void onFails() {
                    if (pIView.get()!=null){
                        ((IIotControView)pIView.get()).onGetDataFails();
                    }
                }
            });
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
