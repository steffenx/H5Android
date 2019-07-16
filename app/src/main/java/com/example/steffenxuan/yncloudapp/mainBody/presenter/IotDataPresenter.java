package com.example.steffenxuan.yncloudapp.mainBody.presenter;

import android.content.Context;
import android.util.Log;

import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.model.IotDataModel;
import com.example.steffenxuan.yncloudapp.mainBody.model.LoginModel;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.IotDataLisentener;
import com.example.steffenxuan.yncloudapp.mainBody.view.IIotDataView;
import com.example.steffenxuan.yncloudapp.mainBody.view.ILoginView;
import com.example.steffenxuan.yncloudapp.mainBody.view.IView;

import java.lang.ref.WeakReference;

/**
 * Created by Steffen_xuan on 2019/6/3.
 */

public class IotDataPresenter extends PresenterFather {
    public  IotDataPresenter(IIotDataView iIotDataView) {
        this.pIModel = new IotDataModel();
        this.pIView = new WeakReference<IView>(iIotDataView);
    }

    public void IotData(Context context){
        if (pIModel != null && pIView != null && pIView.get() != null) {
            //Log.d("kkk","开始传递数据到model");
            IIotDataView iIotDataView=(IIotDataView) pIView.get();
            String accountid=iIotDataView.getUserInfo();
            String account=accountid.substring(0,accountid.indexOf("#"));
            // //Log.d("monitorActivityPage",terminal_id);String terminal_id=accountid.substring(accountid.indexOf("#")+1);
            //Log.d("monitorActivityPage",terminal_id);
            if (account.equals("失败")) ToastUtil.AlentShortToast(context,"用户信息获取失败，请重新登录");
            else {
                ((IotDataModel)pIModel).satrtRequestPost(context, accountid,"first", new IotDataLisentener() {
                    @Override
                    public void onSeccess(String postIotData) {
                        if (pIView!=null){
                            if (pIView.get()!=null){
                                ((IIotDataView)pIView.get()).onGetDataSeccess(postIotData);
                            }
                        }
                    }

                    @Override
                    public void onFails() {
                        if (pIView!=null) {
                            if (pIView.get() != null) {
                                ((IIotDataView) pIView.get()).onGetDataFails();
                            }
                        }
                    }
                });

            }
        }
    }


    public void IotDataTerminal(Context context,String id){
        if (pIModel != null && pIView != null && pIView.get() != null) {
            IIotDataView iIotDataView=(IIotDataView) pIView.get();
            String accountid=iIotDataView.getUserInfo();
            String account=accountid.substring(0,accountid.indexOf("#"));
            String newData=account+"#"+id;
            Log.d("出来的时刻",newData);
            if (account.equals("失败")) ToastUtil.AlentShortToast(context,"用户信息获取失败，请重新登录");
            else {
                ((IotDataModel)pIModel).satrtRequestPost(context, newData,"terminal_id", new IotDataLisentener() {
                    @Override
                    public void onSeccess(String postIotData) {
                        if (pIView!=null) {
                            if (pIView.get() != null) {
                                ((IIotDataView) pIView.get()).onGetDataSeccess(postIotData);
                            }
                        }
                    }

                    @Override
                    public void onFails() {
                        if (pIView!=null) {
                            if (pIView.get() != null) {
                                ((IIotDataView) pIView.get()).onGetDataFails();
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
