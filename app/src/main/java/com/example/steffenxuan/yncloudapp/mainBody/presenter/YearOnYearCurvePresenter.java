package com.example.steffenxuan.yncloudapp.mainBody.presenter;

import android.content.Context;
import android.util.Log;

import com.example.steffenxuan.yncloudapp.mainBody.model.YearOnYearCurveModel;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.YearOnYearCurveLisentener;
import com.example.steffenxuan.yncloudapp.mainBody.view.IYearOnYearCurveView;
import com.example.steffenxuan.yncloudapp.mainBody.view.IView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.ref.WeakReference;

/**
 * Created by Steffen_xuan on 2019/7/9.
 */

public class YearOnYearCurvePresenter extends PresenterFather {

    private String TAG="YearOnYearActivityDebug";

    public  YearOnYearCurvePresenter(IYearOnYearCurveView iYearOnYearCurveView) {
        this.pIModel = new YearOnYearCurveModel();
        this.pIView = new WeakReference<IView>(iYearOnYearCurveView);
    }

    public void  FirstYearOnYear(Context context,String firsttime,String secondtime){
        if (pIModel != null && pIView != null && pIView.get() != null) {
            IYearOnYearCurveView iyearOnYearCurveView=(IYearOnYearCurveView) pIView.get();
            String parameter=iyearOnYearCurveView.get_devices();
            JsonParser parser=new JsonParser();
            JsonObject jo=(JsonObject)parser.parse(parameter);
            jo.addProperty("start_time",Integer.parseInt(firsttime));
            jo.addProperty("end_time",Integer.parseInt(secondtime));
            Log.d(TAG, "get_devices: "+jo.toString());
            ((YearOnYearCurveModel) pIModel).FirstSatrtRequestPost(context, jo.toString(), new YearOnYearCurveLisentener() {
                @Override
                public void onSeccess(String postIotData,String tid) {
                    if (pIView!=null) {
                        if (pIView.get() != null) {
                            ((IYearOnYearCurveView) pIView.get()).onGetFirseDataSeccess(postIotData, tid);
                        }
                    }
                }

                @Override
                public void onEndSeccess(String postIotData) {

                }

                @Override
                public void onFails() {
                    if (pIView!=null) {
                        if (pIView.get() != null) {
                            ((IYearOnYearCurveView) pIView.get()).onGetDataFails();
                        }
                    }
                }
            });
        }
    }

    public void  SecondYearOnYear(Context context,String firsttime,String secondtime){
        if (pIModel != null && pIView != null && pIView.get() != null) {
            IYearOnYearCurveView iyearOnYearCurveView=(IYearOnYearCurveView) pIView.get();
            String parameter=iyearOnYearCurveView.get_devices();
            JsonParser parser=new JsonParser();
            JsonObject jo=(JsonObject)parser.parse(parameter);
            jo.addProperty("start_time",Integer.parseInt(firsttime));
            jo.addProperty("end_time",Integer.parseInt(secondtime));
            Log.d(TAG, "get_devices: "+jo.toString());
            ((YearOnYearCurveModel) pIModel).SecondSatrtRequestPost(context, jo.toString(), new YearOnYearCurveLisentener() {
                @Override
                public void onSeccess(String postIotData, String tid) {

                }

                @Override
                public void onEndSeccess(String postIotData) {
                    if (pIView!=null) {
                        if (pIView.get() != null) {
                            ((IYearOnYearCurveView) pIView.get()).onGetEndDataSeccess(postIotData);
                        }
                    }
                }

                @Override
                public void onFails() {
                    if (pIView!=null) {
                        if (pIView.get() != null) {
                            ((IYearOnYearCurveView) pIView.get()).onGetDataFails();
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
