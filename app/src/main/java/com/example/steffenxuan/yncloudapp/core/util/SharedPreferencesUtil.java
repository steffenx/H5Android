package com.example.steffenxuan.yncloudapp.core.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

/**
 * Created by Steffen_xuan on 2019/5/30.
 */

public class SharedPreferencesUtil {
    private  static  final  String FILE_NAME="YNcloudata";//文件名
    private static SharedPreferencesUtil mInstance;

    public static SharedPreferencesUtil getmInstance(){
        if (mInstance==null){
            synchronized (SharedPreferencesUtil.class){
                if (mInstance==null){
                    mInstance=new SharedPreferencesUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 存数据
     * @param context
     * @param key
     * @param value
     */
    public void put(Context context,String key,Object value){
        SharedPreferences sharedPreferences=context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        //判断类型
        String type= value.getClass().getSimpleName();

        if ("Integer".equals(type)){
            editor.putInt(key,(Integer)value);
        }
        else if("Boolean".equals(type)){
            editor.putBoolean(key,(Boolean)value);
        }
        else if("Float".equals(type)){
            editor.putFloat(key,(Float) value);
        }
        else if("String".equals(type)){
            editor.putString(key,(String) value);
        }
        else if("Long".equals(type)){
            editor.putLong(key,(Long) value);
        }
        editor.apply();//异步保存
    }

    /**
     * 取数据
     * @param context
     * @param key
     * @param defValue 默认值
     * @return
     */
    @Nullable
    public Object get(Context context,String key,Object defValue){
        SharedPreferences sharedPreferences=context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);

        String type= defValue.getClass().getSimpleName();
        if ("Integer".equals(type)){
            return sharedPreferences.getInt(key,(Integer) defValue);
        }
        else if("Boolean".equals(type)){
            return sharedPreferences.getBoolean(key,(Boolean) defValue);
        }
        else if("Float".equals(type)){
            return sharedPreferences.getFloat(key,(Float) defValue);
        }
        else if("String".equals(type)){
            return sharedPreferences.getString(key,(String) defValue);
        }
        else if("Long".equals(type)){
            return sharedPreferences.getLong(key,(Long) defValue);
        }
        return null;
    }
}
