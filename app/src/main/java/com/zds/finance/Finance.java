package com.zds.finance;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.TextView;

import com.zds.common.DataBaseHelper;
import com.zds.common.DateTimeTrans;

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
        this.type = "";
    }

    public Finance(String name_, Date date_, float amount_) {
        this.info = name_;
        this.date = date_.getDate();
        this.amount = amount_;
    }

    public Finance(String name_, String date_, float amount_, String type_) {
        this.info = name_;
        this.date = DateTimeTrans.getString2Date(date_);
        this.id = 0;
        this.amount = amount_;
        this.type = type_;
    }

    public Finance(int id_, String name_, long date_, float amount_, String type_) {
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
            this.type = js.getString("type");
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
        txtType_.setText(this.type);
    }

    public void setHolder(TextView txtVeiwName_, TextView txtViewId_, TextView txtAmound_, TextView txtType_, TextView txtDate_) {
        txtVeiwName_.setText(this.info);
        txtViewId_.setText(String.valueOf(this.id));
        txtAmound_.setText(String.valueOf(this.amount));
        txtDate_.setVisibility(View.VISIBLE);
        txtType_.setText(this.type);
        txtDate_.setText(this.getDate2String());
    }

    public String getDate2String() {
        return DateTimeTrans.getDate2String(this.date);
    }

    public String toString() {
        return String.format("%d, %s, %d, %.2f, %s", this.id, this.info, this.date, this.amount, this.type);
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

    public static void clearDb() {
        DataBaseHelper.dbExecSQL("delete from finance");
    }

    public static void deleteFromDb(int financeId_) {
        DataBaseHelper.dbExecSQL("delete from finance where id = " + financeId_);
    }

    public static void updateToDb(int financeId_, Finance finance_) {
        DataBaseHelper.dbExecSQL("update finance set " + updateColName + " where id = " + financeId_,
                new Object[]{finance_.info, finance_.date, finance_.amount, finance_.type});
    }

    public static void insertToDb(Finance finance_) {
        DataBaseHelper.dbExecSQL("insert into finance(" + insertColName + ") values(?, ?, ?, ?)",
                new Object[]{finance_.info, finance_.date, finance_.amount, finance_.type});
    }

    public static void inportToDb(List<String> data_) {
        clearDb();
        for (String it : data_) {
            Finance finance = new Finance(it);
            insertToDb(finance);
        }
    }

    public static Finance getOneFormDb(int financeId_) {
        List<Finance> allFinances = new ArrayList<Finance>();
        Cursor cursor = DataBaseHelper.getDb().rawQuery("select " + selectColName + " from finance where id = " + financeId_, null);
        if(cursor == null) {
            return null;
        }
        while(cursor.moveToNext()) {
            Finance finance = new Finance(cursor.getInt(0), cursor.getString(1),
                    cursor.getLong(2), cursor.getFloat(3), cursor.getString(4));
            allFinances.add(finance);
        }
        return allFinances.get(0);
    }

    public static List<Finance> getOneMonthFormDb(int year, int month) {
        String dataStartStr = String.format("%d年%02d月%02d日", year, month, 1);  //yyyy年MM月dd日
        String dataEndStr = String.format("%d年%02d月%02d日", year, month + 1, 1);  //yyyy年MM月dd日
        long dataStart = DateTimeTrans.getString2Date(dataStartStr);
        long dataEnd = DateTimeTrans.getString2Date(dataEndStr);
        List<Finance> allFinances = new ArrayList<Finance>();
        Cursor cursor = DataBaseHelper.getDb().rawQuery("select " + selectColName + " from finance where date >= " +
                            dataStart + " and date < " + dataEnd + " order by date", null);
        if(cursor == null) {
            return allFinances;
        }
        while(cursor.moveToNext()) {
            Finance finance = new Finance(cursor.getInt(0), cursor.getString(1),
                    cursor.getLong(2), cursor.getFloat(3), cursor.getString(4));
            allFinances.add(finance);
        }

        return allFinances;
    }


    public static List<Finance> getAllFormDb() {
        List<Finance> allFinances = new ArrayList<Finance>();
        Cursor cursor = DataBaseHelper.getDb().rawQuery("select " + selectColName + " from finance order by date", null);
        if(cursor == null) {
            return allFinances;
        }
        while(cursor.moveToNext()) {
            Finance finance = new Finance(cursor.getInt(0), cursor.getString(1),
                    cursor.getLong(2), cursor.getFloat(3), cursor.getString(4));
            allFinances.add(finance);
        }

        return allFinances;
    }

    public String info;
    public long date;
    public float amount;
    public String type;
    public int id;

    private final static String insertColName= "name, date, amount, type";
    private final static String selectColName= "id, " + insertColName;
    private final static String updateColName= insertColName.replace(",", " = ?,") + " = ?";
}
