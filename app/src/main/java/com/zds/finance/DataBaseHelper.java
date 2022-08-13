package com.zds.finance;

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
        createTableCmdList.add("create table if not exists finance (id integer primary key, type varchar(200), name varchar(200), date integer, amount float(10, 8))");
        createTableCmdList.add("create table if not exists ftpinfo (id integer primary key, name varchar(200), usr varchar(200), passwd varchar(200))");
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
}
