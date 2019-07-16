package com.example.steffenxuan.yncloudapp.mainBody.presenter;

import android.content.Context;
import android.util.Log;

import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.model.VideoMonitorModel;
import com.example.steffenxuan.yncloudapp.mainBody.model.VideoMonitorNewModel;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.videoMonitorLisentener;
import com.example.steffenxuan.yncloudapp.mainBody.view.IIotDataView;
import com.example.steffenxuan.yncloudapp.mainBody.view.IView;
import com.example.steffenxuan.yncloudapp.mainBody.view.IVideoMonitorView;

import java.lang.ref.WeakReference;

/**
 * Created by Steffen_xuan on 2019/6/5.
 */

public class VideoMonitorPresenter extends PresenterFather{
    public  VideoMonitorPresenter(IVideoMonitorView ivideoMonitorView) {
        this.pIModel = new VideoMonitorNewModel();
        this.pIView = new WeakReference<IView>(ivideoMonitorView);
    }
    public void videoMonitor(Context context,String vcrdata){
        if (pIModel != null && pIView != null && pIView.get() != null){

            IVideoMonitorView iIotDataView=(IVideoMonitorView)pIView.get();
            String postData=iIotDataView.getUserInfo();
            if (postData.equals("失败")) {
                ToastUtil.AlentShortToast(context,"用户信息失效，请重新登录");
            }else {
                ((VideoMonitorNewModel)pIModel).satrtRequestPost(context, postData,vcrdata, new videoMonitorLisentener() {
                    @Override
                    public void onSeccess(String postIotData) {
                        if (pIView!=null) {
                            if (pIView.get() != null) {
                                ((IVideoMonitorView) pIView.get()).onGetDataSeccess(postIotData);
                            }
                        }
                    }

                    @Override
                    public void onFails() {
                        if (pIView!=null) {
                            if (pIView.get() != null) {
                                ((IVideoMonitorView) pIView.get()).onGetDataFails();
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
