package com.example.steffenxuan.yncloudapp.sqLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.steffenxuan.yncloudapp.core.util.SharedPreferencesUtil;
import com.example.steffenxuan.yncloudapp.mainBody.model.Login.loginUserInfo;
import com.example.steffenxuan.yncloudapp.mainBody.model.LoginModel;

import java.sql.DatabaseMetaData;

/**
 * Created by Steffen_xuan on 2019/5/31.
 */


 public class  DatabaseOperation_userInfo {
    private static final String TABLE_NAME="UserInfo" ;
    private String TAG="DatabaseOperation_userInfo";

    private static DatabaseOperation_userInfo mInstance;

    public static DatabaseOperation_userInfo getInstance(){
        if (mInstance==null){
            synchronized (DatabaseOperation_userInfo.class){
                if (mInstance==null){
                    mInstance=new DatabaseOperation_userInfo();
                }
            }
        }
        return mInstance;
    }

    /**
     * 添加数据
     * @param info
     * @param context
     */
    public void addUserInfo(loginUserInfo info, Context context){
        SQLiteDatabase db=DataBaseManager.getInstance(context);
        db.insert(TABLE_NAME,null,addCV(info));
        Log.d(TAG,"添加成功");
    }

    public void removeUserInfo(String mobile,Context context){
        SQLiteDatabase db= DataBaseManager.getInstance(context);
        db.delete(TABLE_NAME,"mobile = ?",new String[]{mobile});
        Log.d(TAG,"删除成功");
    }

    public void updateUserInfo(Context context, loginUserInfo info,String mobile){
        SQLiteDatabase db=DataBaseManager.getInstance(context);
        db.update(TABLE_NAME,addCV(info),"mobile = ?",new String[]{mobile});
        Log.d(TAG,"修改成功");
    }

    public boolean getIsMobile(Context context,String mobile){
        SQLiteDatabase db=DataBaseManager.getInstance(context);
        String sql="select mobile from UserInfo where mobile="+mobile;
        Cursor cursor=db.rawQuery(sql,null);
        if (cursor.getCount()==0) {cursor.close(); return false;}
        else {cursor.close();return true;}
    }

    public String getToken(Context context,String mobile){
        SQLiteDatabase db=DataBaseManager.getInstance(context);
        String sql="select token from UserInfo where mobile="+mobile;
        Cursor cursor=db.rawQuery(sql,null);
        if (cursor.getCount()==0) {cursor.close(); return "0";}
        else {
            if (cursor.moveToFirst()){
                String m=cursor.getString(cursor.getColumnIndex("token"));
                cursor.close();
                return m;
            }
            return "0";
        }
    }

    public String getAvatar(Context context,String mobile){
        SQLiteDatabase db=DataBaseManager.getInstance(context);
        String sql="select avatar from UserInfo where mobile="+mobile;
        Cursor cursor=db.rawQuery(sql,null);
        if (cursor.getCount()==0) {cursor.close(); return "0";}
        else {
            if (cursor.moveToFirst()){
                String m=cursor.getString(cursor.getColumnIndex("avatar"));
                cursor.close();
                return m;
            }
            return "0";
        }
    }

    public String getCompany(Context context,String mobile){
        SQLiteDatabase db=DataBaseManager.getInstance(context);
        String sql="select company from UserInfo where mobile="+mobile;
        Cursor cursor=db.rawQuery(sql,null);
        if (cursor.getCount()==0) {cursor.close(); return "0";}
        else {
            if (cursor.moveToFirst()){
                String m=cursor.getString(cursor.getColumnIndex("company"));
                cursor.close();
                return m;
            }
            return "0";
        }
    }

    public String getAddress(Context context,String mobile){
        SQLiteDatabase db=DataBaseManager.getInstance(context);
        String sql="select address from UserInfo where mobile="+mobile;
        Cursor cursor=db.rawQuery(sql,null);
        if (cursor.getCount()==0) {cursor.close(); return "0";}
        else {
            if (cursor.moveToFirst()){
                String m=cursor.getString(cursor.getColumnIndex("address"));
                cursor.close();
                return m;
            }
            return "0";
        }
    }


    public ContentValues addCV(loginUserInfo info){
        ContentValues values=new ContentValues();
        values.put("user_id",info.getUser_id());
        values.put("username",info.getUsername());
        values.put("nickname",info.getNickname());
        values.put("mobile",info.getMobile());
        values.put("avatar",info.getAvatar());
        values.put("score",info.getScore());
        values.put("city",info.getCity());
        values.put("address",info.getAddress());
        values.put("company",info.getCompany());
        values.put("platform",info.getPlatform());
        values.put("token",info.getToken());
        values.put("createtime",info.getCreatetime());
        return values;
    }
}
