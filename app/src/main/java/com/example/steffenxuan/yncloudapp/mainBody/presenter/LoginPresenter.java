package com.example.steffenxuan.yncloudapp.mainBody.presenter;

import android.app.PendingIntent;
import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.activity.Login_page;
import com.example.steffenxuan.yncloudapp.mainBody.model.LoginModel;
import com.example.steffenxuan.yncloudapp.mainBody.model.lisentener.LoginLisentener;
import com.example.steffenxuan.yncloudapp.mainBody.view.ILoginView;
import com.example.steffenxuan.yncloudapp.mainBody.view.IView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Steffen_xuan on 2019/5/29.
 * 登录p层
 */

public class LoginPresenter extends PresenterFather{
    public  LoginPresenter(ILoginView loginView) {
        this.pIModel = new LoginModel();
        this.pIView = new WeakReference<IView>(loginView);
    }

    public void Login(Context context){
        if (pIModel != null && pIView != null && pIView.get() != null) {
            //获取数据
            ILoginView iLoginView=(ILoginView) pIView.get();
            String getuserInfo = iLoginView.getUserInfo();

            try{
                JSONObject jsdata =new JSONObject(getuserInfo);
                String account=jsdata.getString("account");
                String password=jsdata.getString("password");
                /**
                 * 判断数据
                 * */
                if ("".equals(account) && "".equals(password)) {
                    String msg="账号或密码不能为空";
                    ToastUtil.AlentShortToast(Login_page.loginActivity,msg);
                    ((Login_page)context).onLoginFails();
                } else {
                    if (isPhoneNumber(account)) {
                        //请求数据
                        ((LoginModel)pIModel).satrtRequestPost(getuserInfo, context,new LoginLisentener(){
                            //成功
                            @Override
                            public void onSeccess() {
                                if (pIView!=null) {
                                    if (pIView.get() != null) {
                                        ((ILoginView) pIView.get()).onLoginSeccess();
                                    }
                                }

                            }
                            //失败
                            @Override
                            public void onFails() {
                                if (pIView!=null) {
                                    if (pIView.get() != null) {
                                        ((ILoginView) pIView.get()).onLoginFails();
                                    }
                                }
                            }
                        });
                    } else {
                        String msg="输入正确的电话号码";
                        ToastUtil.AlentShortToast(Login_page.loginActivity,msg);
                        ((Login_page)context).onLoginFails();
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            iLoginView = null;
        }
    }

    private static boolean isPhoneNumber(String account) {
        //判断是否为空
        if (TextUtils.isEmpty(account)) {
            return false;
        }
        //判断是否为11位
        if (account.length() == 11) {
            for (int i = 0; i < 11; i++) {
                if (!PhoneNumberUtils.isISODigit(account.charAt(i))) {
                    return false;
                }
            }
            //正则表达式
            Pattern p = Pattern.compile("^1\\d{10}$");
            Matcher m = p.matcher(account);

            if (m.find()) {
                return true;
            }
        }
        if(account!=null) return true;
        return false;
    }

    public void checkToken(Context context,String token) {
        if (pIModel != null && pIView != null && pIView.get() != null) {
            if (!token.equals("null")){
                ((LoginModel)pIModel).RequestcheckToken(context,token,new LoginLisentener(){
                    //成功
                    @Override
                    public void onSeccess() {
                        if (pIView!=null) {
                            if (pIView.get() != null) {
                                ((ILoginView) pIView.get()).onLoginSeccess();
                            }
                        }

                    }
                    //失败
                    @Override
                    public void onFails() {
                        if (pIView!=null) {
                            if (pIView.get() != null) {
                                ((ILoginView) pIView.get()).onLoginFails();
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
