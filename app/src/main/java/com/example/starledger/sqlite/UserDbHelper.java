package com.example.starledger.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDbHelper extends SQLiteOpenHelper {
    private static final String DBname="user.db";

    public UserDbHelper(Context context) {
        super(context, DBname, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "account TEXT, " +
                "password TEXT, " +
                "gxmsg TEXT, " +
                "history_gold INTEGER, " +
                "gold INTEGER)";

        String createTransactionTable = "CREATE TABLE transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "amount REAL, " +
                "category TEXT, " +
                "description TEXT, " +
                "date TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES users(id))";

        db.execSQL(createUserTable);
        db.execSQL(createTransactionTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS transactions");
        onCreate(db);
    }
}
