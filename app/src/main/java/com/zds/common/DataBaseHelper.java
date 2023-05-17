package com.zds.common;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    List<String> createTableCmdList = new ArrayList<>();
    public DataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        createTableCmdList.add("create table if not exists finance (id integer primary key, type varchar(200), name varchar(200), date integer, amount float(10, 2))");
        createTableCmdList.add("create table if not exists ftpinfo (ip TEXT, port integer, usr TEXT, passwd TEXT, prefix TEXT)");
        createTableCmdList.add("insert into ftpinfo(ip, port, usr, passwd , prefix) values('192.168.1.1', 21, 'dosens', 'dosens', '/')");
        createTableCmdList.add("create table if not exists fat (id integer primary key, date integer, morning float(4,2), noon float(4, 2), night float(4, 2), rope integer, circle integer)");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {

    }

    private void createTable(SQLiteDatabase db) {
        for(String it: this.createTableCmdList) {
            db.execSQL(it);
        }
    }

    public static void initDb(Context context) {
        if(dbHelper != null) {
            return;
        }
        dbHelper = new DataBaseHelper(context, "finance.db", null, 1);
        db = dbHelper.getWritableDatabase();
//        Finance.clearDb(context);
    }

    public static SQLiteDatabase getDb() {
        return db;
    }

    public static void dbExecSQL(String cmd, Object para[]) {
        db.execSQL(cmd, para);
    }

    public static void dbExecSQL(String cmd) {
        db.execSQL(cmd);
    }

    private static DataBaseHelper dbHelper = null;
    private static SQLiteDatabase db = null;
}
