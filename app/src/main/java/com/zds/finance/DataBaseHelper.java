package com.zds.finance;

import android.content.Context;
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
        createTableCmdList.add("create table finance (id integer primary key, name varchar(200), date integer, amount float(10, 8), type integer)");
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
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    private void createTable(SQLiteDatabase db) {
        for(String it: this.createTableCmdList) {
            db.execSQL(it);
        }
    }
}
