package com.example.steffenxuan.yncloudapp.core.util;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Steffen_xuan on 2019/5/29.
 */

public class ToastUtil {

    public static void AlentLongToast(final Context context, final String title){
        new Thread(){
            public void run() {
                Looper.prepare();

                Toast.makeText(context, title, Toast.LENGTH_LONG).show();

                Looper.loop();
            };
        }.start();
    }
    public static void AlentShortToast(final Context context, final String title){
        new Thread(){
            public void run() {
                Looper.prepare();

                Toast.makeText(context, title, Toast.LENGTH_SHORT).show();

                Looper.loop();
            };
        }.start();
    }

    public static void AlentTOPShortToast(final Context context, final String title){
        new Thread(){
            public void run() {
                Looper.prepare();

                //Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
                Toast toast = Toast.makeText(context,
                        title, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 150);
                toast.show();

                Looper.loop();
            };
        }.start();
    }
}
