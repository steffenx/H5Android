package com.example.steffenxuan.yncloudapp.core.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Steffen_xuan on 2019/5/29.
 * 欢迎页面的共享引用
 */

public class SharedPreferencesForWelcomeUtil {
    private static final String spFileName = "welcomePage";
    public static final String FIRST_OPEN = "first_open";

    public static Boolean getBoolean(Context context, String strKey,
                                     Boolean strDefault) {
        //如果不存在此首选项，则返回值。
        SharedPreferences setPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        Boolean result = setPreferences.getBoolean(strKey, strDefault);
        return result;
    }

    public static void putBoolean(Context context, String strKey,
                                  Boolean strData) {
        SharedPreferences activityPreferences = context.getSharedPreferences(
                spFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = activityPreferences.edit();
        editor.putBoolean(strKey, strData);
        editor.commit();
    }
}
