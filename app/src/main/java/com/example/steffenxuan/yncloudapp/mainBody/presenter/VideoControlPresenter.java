package com.example.steffenxuan.yncloudapp.mainBody.presenter;

import android.content.Context;

import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.model.VideoControlModel;
import com.example.steffenxuan.yncloudapp.mainBody.model.VideoMonitorModel;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.VideoControlLisentener;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.videoMonitorLisentener;
import com.example.steffenxuan.yncloudapp.mainBody.view.IVideoControlView;
import com.example.steffenxuan.yncloudapp.mainBody.view.IVideoMonitorView;
import com.example.steffenxuan.yncloudapp.mainBody.view.IView;

import java.lang.ref.WeakReference;

/**
 * Created by Steffen_xuan on 2019/6/7.
 */

public class VideoControlPresenter extends PresenterFather {
    public  VideoControlPresenter(IVideoControlView iVideoControlView) {
        this.pIModel = new VideoControlModel();
        this.pIView = new WeakReference<IView>(iVideoControlView);
    }

    public void VideoControl(Context context,String postdata){
        if (pIModel != null && pIView != null && pIView.get() != null){

            final IVideoControlView iVideoControlView=(IVideoControlView)pIView.get();

                ((VideoControlModel)pIModel).satrtRequestPost(postdata,context, new VideoControlLisentener() {

                    @Override
                    public void onSeccess() {
                        if (pIView!=null) {
                            if (pIView.get() != null) {
                                ((IVideoControlView) pIView.get()).onSeccess();
                            }
                        }
                    }

                    @Override
                    public void onFails() {
                        if (pIView!=null) {
                            if (pIView.get() != null) {
                                ((IVideoControlView) pIView.get()).onFails();
                            }
                        }
                    }
                });
        }
    }
    public void VideoStopControl(Context context,String postdata){
        if (pIModel != null && pIView != null && pIView.get() != null){

            final IVideoControlView iVideoControlView=(IVideoControlView)pIView.get();

            ((VideoControlModel)pIModel).satrtStopRequestPost(postdata,context, new VideoControlLisentener() {

                @Override
                public void onSeccess() {
                    if (pIView!=null) {
                        if (pIView.get() != null) {
                            ((IVideoControlView) pIView.get()).onSeccess();
                        }
                    }
                }

                @Override
                public void onFails() {
                    if (pIView!=null) {
                        if (pIView.get() != null) {
                            ((IVideoControlView) pIView.get()).onFails();
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
