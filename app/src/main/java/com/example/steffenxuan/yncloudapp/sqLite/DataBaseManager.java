package com.example.steffenxuan.yncloudapp.sqLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Steffen_xuan on 2019/5/30.
 */

public class DataBaseManager {

    private static DatabaseHelper mdatabaseHelper;
    private final static String dataBaseName="android_YNcloud.db";

    public static SQLiteDatabase getInstance(Context context){
        if (mdatabaseHelper==null){
            synchronized (DataBaseManager.class){
                if (mdatabaseHelper==null){
                    mdatabaseHelper=new DatabaseHelper(context,dataBaseName,null,1);
                }
            }
        }
        return mdatabaseHelper.getWritableDatabase();
    }


    /**
     * 创建数据库
     *
     */
    public void  onCread(Context context){
        mdatabaseHelper =new DatabaseHelper(context,dataBaseName,null,1);
        mdatabaseHelper.getWritableDatabase();
    }

    /**
     * 初始化db
     *
     */
    public void initDb(Context context){
        mdatabaseHelper =new DatabaseHelper(context,dataBaseName,null,1);
    }

    /**
     * 插入数据
     * @param tableName 表名
     * @param values 参数
     */
    public void add(String tableName, ContentValues values){
        SQLiteDatabase db=mdatabaseHelper.getReadableDatabase();
        db.insert(tableName,null,values);
        Log.d("时刻记得你","插入成功");
    }

    public void updateUserInfo(String tableName, ContentValues values,String where,String[] who){
        SQLiteDatabase db=mdatabaseHelper.getReadableDatabase();
        db.update(tableName,values,where,who);
        Log.d("时刻记得你","修改成功");
    }

    /**
     * 查询是都
     *
     */
    public Boolean queryUserInfo_acc(String sql){
        SQLiteDatabase db=mdatabaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql,null);//创建游标
        if (cursor.getCount()==0){
            cursor.close();
            return false;
        }else {
            cursor.close();
            return true;
        }
    }
    public String queryUserInfo_token(String sql){
        SQLiteDatabase db=mdatabaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql,null);//创建游标
        if (cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndex("token"));
        }
        cursor.close();
        return null;
    }

}
