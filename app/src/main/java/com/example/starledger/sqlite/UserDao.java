package com.example.starledger.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.starledger.bean.User;

import java.util.ArrayList;
import java.util.List;


public class UserDao {
    private UserDbHelper mHelper;

    public UserDao(Context context) {
        mHelper = new UserDbHelper(context);
    }
    //添加一个用户
    public void insert(User user) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        String insert_sql = "INSERT INTO users (account,password,gxmsg,history_gold,gold) VALUES (?,?,?,0,0)";
        Object[] obj = { user.getAccount(), user.getPassword(), user.getGxmsg()};
        db.execSQL(insert_sql, obj);
        db.close();
    }
    //全表查询用户
    //根据自增id删除用户
    public boolean delete(int id) {
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            db.delete("users", "id = ?", new String[] { ""+id });
            return true;
        }catch (Exception e) {
            return false;
        }

    }
    //根据自增id更新用户信息[密码，个性，]
    public boolean update(User user){
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            String[] whereArgs = { String.valueOf(user.getId()) };
            ContentValues cv = new ContentValues();
            cv.put("password", user.getPassword());
            cv.put("gxmsg", user.getGxmsg());
            db.update("users",cv,"id=?",whereArgs);
            return true;
        }catch (Exception e) {
            return false;
        }
    }
    //根据自增id更新用户信息[金币相关]
    public boolean update_gold(User user){
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            String[] whereArgs = { String.valueOf(user.getId()) };
            ContentValues cv = new ContentValues();
            cv.put("history_gold", user.getHistory_gold());
            cv.put("gold", user.getGold());
            db.update("users",cv,"id=?",whereArgs);
            return true;
        }catch (Exception e) {
            return false;
        }
    }
}
