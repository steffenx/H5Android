package com.example.steffenxuan.yncloudapp.sqLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.steffenxuan.yncloudapp.core.util.ToastUtil;
import com.example.steffenxuan.yncloudapp.mainBody.activity.Login_page;

/**
 * Created by Steffen_xuan on 2019/5/29.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context mcontext;
    private static final String CREATETABLE_USERINFO="create table UserInfo("+
            "id integer primary key autoincrement,"+
            "user_id integer,"+
            "username text,"+
            "nickname text,"+
            "mobile text,"+
            "avatar text,"+
            "score integer,"+
            "city varchar(30),"+
            "address text,"+
            "company text,"+
            "platform varchar(10),"+
            "token varchar(50),"+
            "createtime varchar(20))";
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mcontext=context;
    }

    /**
     * 数据库创建
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATETABLE_USERINFO);
        //ToastUtil.AlentShortToast(mcontext,"数据保存成功");
    }

    /**
     * 数据库升级
     * @param sqLiteDatabase
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
