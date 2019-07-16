package com.example.steffenxuan.yncloudapp.mainBody.presenter;

import android.content.Context;
import android.util.Log;

import com.example.steffenxuan.yncloudapp.mainBody.model.IotControModel;
import com.example.steffenxuan.yncloudapp.mainBody.model.TimeCurveModel;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.IotcontroLisentener;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.TimeCurveLisentent;
import com.example.steffenxuan.yncloudapp.mainBody.view.IIotControView;
import com.example.steffenxuan.yncloudapp.mainBody.view.ITimeCurveView;
import com.example.steffenxuan.yncloudapp.mainBody.view.IView;

import java.lang.ref.WeakReference;

/**
 * Created by Steffen_xuan on 2019/6/19.
 */

public class TimeCurvePresenter extends PresenterFather {

    public  TimeCurvePresenter(ITimeCurveView iTimeCurveView) {
        this.pIModel = new TimeCurveModel();
        this.pIView = new WeakReference<IView>(iTimeCurveView);
    }

    public void  TimeCurve(Context context){
        if (pIModel != null && pIView != null && pIView.get() != null) {
            ITimeCurveView iTimeCurveView=(ITimeCurveView) pIView.get();
            String devices_id=iTimeCurveView.get_devices();
            ((TimeCurveModel) pIModel).satrtRequestPost(context, devices_id, new TimeCurveLisentent() {
                @Override
                public void onSeccess(String postIotData,String tid) {
                    if (pIView!=null) {
                        if (pIView.get() != null) {
                            ((ITimeCurveView) pIView.get()).onGetDataSeccess(postIotData, tid);
                        }
                    }
                }

                @Override
                public void onFails() {
                    if (pIView!=null) {
                        if (pIView.get() != null) {
                            ((ITimeCurveView) pIView.get()).onGetDataFails();
                        }
                    }
                }
            });
        }
    }

    public void  HistoryCurve(Context context){
        if (pIModel != null && pIView != null && pIView.get() != null) {
            ITimeCurveView iTimeCurveView=(ITimeCurveView) pIView.get();
            String parameter=iTimeCurveView.get_devices();
            ((TimeCurveModel) pIModel).historycurvePost(context, parameter, new TimeCurveLisentent() {
                @Override
                public void onSeccess(String postIotData,String tid) {
                    if (pIView!=null) {
                        if (pIView.get() != null) {
                            ((ITimeCurveView) pIView.get()).onGetDataSeccess(postIotData, tid);
                        }
                    }
                }

                @Override
                public void onFails() {
                    if (pIView!=null) {
                        if (pIView.get() != null) {
                            ((ITimeCurveView) pIView.get()).onGetDataFails();
                        }
                    }
                }
            });
        }
    }


    public void  realtimedata(Context context,String terminal_id){
        if (pIModel != null && pIView != null && pIView.get() != null) {
            ITimeCurveView iTimeCurveView=(ITimeCurveView) pIView.get();
            String devices_id=iTimeCurveView.get_devices();
            ((TimeCurveModel) pIModel).realtimePost(context, terminal_id, new TimeCurveLisentent() {
                @Override
                public void onSeccess(String postIotData,String tid) {
                    if (pIView != null) {
                        if (pIView.get() != null) {
                            ((ITimeCurveView) pIView.get()).onrealtimeSeccess(postIotData);
                        }
                    }
                }

                @Override
                public void onFails() {
                    if (pIView!=null) {
                        if (pIView.get() != null) {
                            ((ITimeCurveView) pIView.get()).onGetDataFails();
                        }
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
