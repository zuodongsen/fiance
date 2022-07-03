package com.zds.finance;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Finance {
    public Finance() {
        this.info = "";
        this.date = 0;
        this.id = 0;
        this.amount = 0;
        this.type = 0;
    }

    public Finance(String name_, Date date_, float amount_) {
        this.info = name_;
        this.date = date_.getDate();
        this.amount = amount_;
    }

    public Finance(String name_, String date_, float amount_, int type_) {
        this.info = name_;
        this.date = getString2Date(date_);
        this.id = 0;
        this.amount = amount_;
        this.type = type_;
    }

    public Finance(int id_, String name_, long date_, float amount_, int type_) {
        this.id = id_;
        this.info = name_;
        this.date = date_;
        this.amount = amount_;
        this.type = type_;
    }

    public Finance(String jsStr) {
        try {
            JSONObject js = new JSONObject(jsStr);
            this.info = js.getString("info");
            this.date = js.getLong("date");
            this.amount = new Float(js.getDouble("amount"));
            this.type = js.getInt("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getInfo() {
        return this.info;
    }

    public void setHolder(TextView txtVeiwName_, TextView txtViewId_, TextView txtAmound_, TextView txtType_) {
        txtVeiwName_.setText(this.info);
        txtViewId_.setText(String.valueOf(this.id));
        txtAmound_.setText(String.valueOf(this.amount));
        txtType_.setText(CreateActivity.amountType[this.type]);
    }

    public void setHolder(TextView txtVeiwName_, TextView txtViewId_, TextView txtAmound_, TextView txtType_, TextView txtDate_) {
        txtVeiwName_.setText(this.info);
        txtViewId_.setText(String.valueOf(this.id));
        txtAmound_.setText(String.valueOf(this.amount));
        txtDate_.setVisibility(View.VISIBLE);
        txtType_.setText(CreateActivity.amountType[this.type]);
        txtDate_.setText(this.getDate2String());
    }

    public String getDate2String() {
        return getDate2String(this.date);
    }

    public String toString() {
        return String.format("%d, %s, %d, %.2f, %d", this.id, this.info, this.date, this.amount, this.type);
    }

    public String toJson() {
        JSONObject js = new JSONObject();
        try {
            js.put("id", this.id);
            js.put("type", this.type);
            js.put("amount", this.amount);
            js.put("date", this.date);
            js.put("info", this.info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return js.toString();
    }

    public static String getDate2String(Date date_) {
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日");
        return formatter.format(date_);
    }

    public static String getMonthDay2String(Date date_) {
        SimpleDateFormat formatter = new SimpleDateFormat ("MM月dd日");
        return formatter.format(date_);
    }

    public static String getDate2String(long date_) {
        Date dateTmp = new Date();
        dateTmp.setTime(date_);
        return getDate2String(dateTmp);
    }

    public static String getMonthDay2String(long date_) {
        Date dateTmp = new Date();
        dateTmp.setTime(date_);
        return getMonthDay2String(dateTmp);
    }

    public static String getNowDateTime2String() {
        Date dateTmp = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy_MM_dd_HH_mm");
        return formatter.format(dateTmp);
    }

    public static long getString2Date(String date_) {
        Date dateTmp;
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日");
        try {
            dateTmp = formatter.parse(date_);
        } catch (ParseException e) {
            dateTmp = new Date();
        }
        return dateTmp.getTime();
    }

    public static void clearDb() {
        db.execSQL("delete from finance");
    }

    public static void deleteFromDb(int financeId_) {
        db.execSQL("delete from finance where id = " + financeId_);
    }

    public static void updateToDb(int financeId_, Finance finance_) {
        db.execSQL("update finance set " + updateColName + " where id = " + financeId_,
                new Object[]{finance_.info, finance_.date, finance_.amount, finance_.type});
    }

    public static void insertToDb(Finance finance_) {
        db.execSQL("insert into finance(" + insertColName + ") values(?, ?, ?, ?)",
                new Object[]{finance_.info, finance_.date, finance_.amount, finance_.type});
    }

    public static void inportToDb(List<String> data_) {
        clearDb();
        for (String it : data_) {
            Finance finance = new Finance(it);
            insertToDb(finance);
        }
    }

//    public static void updateIdFromDb(Finance finance_) {
//        for(String it : CreateActivity.amountType_old) {
//            for(String it_new: CreateActivity.amountType) {
//
//            }
//
//        }
//    }

    public static Finance getOneFormDb(int financeId_) {
        List<Finance> allFinances = new ArrayList<Finance>();
        Cursor cursor = db.rawQuery("select " + selectColName + " from finance where id = " + financeId_, null);
        if(cursor == null) {
            return null;
        }
        while(cursor.moveToNext()) {
            Finance finance = new Finance(cursor.getInt(0), cursor.getString(1),
                    cursor.getLong(2), cursor.getFloat(3), cursor.getInt(4));
            allFinances.add(finance);
        }
        return allFinances.get(0);
    }

    public static List<Finance> getOneMonthFormDb(int year, int month) {
        String dataStartStr = String.format("%d年%02d月%02d日", year, month, 1);  //yyyy年MM月dd日
        String dataEndStr = String.format("%d年%02d月%02d日", year, month + 1, 1);  //yyyy年MM月dd日
        long dataStart = getString2Date(dataStartStr);
        long dataEnd = getString2Date(dataEndStr);
        List<Finance> allFinances = new ArrayList<Finance>();
        Cursor cursor = db.rawQuery("select " + selectColName + " from finance where date >= " +
                            dataStart + " and date < " + dataEnd + " order by date", null);
        if(cursor == null) {
            return allFinances;
        }
        while(cursor.moveToNext()) {
            Finance finance = new Finance(cursor.getInt(0), cursor.getString(1),
                    cursor.getLong(2), cursor.getFloat(3), cursor.getInt(4));
            allFinances.add(finance);
        }

        return allFinances;
    }


    public static List<Finance> getAllFormDb() {
        List<Finance> allFinances = new ArrayList<Finance>();
        Cursor cursor = db.rawQuery("select " + selectColName + " from finance order by date", null);
        if(cursor == null) {
            return allFinances;
        }
        while(cursor.moveToNext()) {
            Finance finance = new Finance(cursor.getInt(0), cursor.getString(1),
                    cursor.getLong(2), cursor.getFloat(3), cursor.getInt(4));
            allFinances.add(finance);
        }

        return allFinances;
    }

    public static void initDb(Context context) {
        if(dbHelper != null) {
            return;
        }
        dbHelper = new DataBaseHelper(context, "finance.db", null, 1);
        db = dbHelper.getWritableDatabase();
//        Finance.clearDb(context);
    }

    public String info;
    public long date;
    public float amount;
    public int type;
    public int id;
    private static DataBaseHelper dbHelper = null;
    private static SQLiteDatabase db = null;
    private final static String insertColName= "name, date, amount, type";
    private final static String selectColName= "id, " + insertColName;
    private final static String updateColName= insertColName.replace(",", " = ?,") + " = ?";
}
