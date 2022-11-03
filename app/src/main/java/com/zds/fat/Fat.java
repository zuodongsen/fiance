package com.zds.fat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.TextView;

import com.zds.common.DataBaseHelper;
import com.zds.common.DateTimeTrans;
import com.zds.finance.Finance;

import java.util.ArrayList;
import java.util.List;

public class Fat {

    public Fat(String date_, float morningWeight_, float noonWeight_, float nightWeight_, int ropeNum_, int runningCircleNum_) {
        this.date = DateTimeTrans.getString2Date(date_);
        this.morningWeight = morningWeight_;
        this.noonWeight = noonWeight_;
        this.nightWeight = nightWeight_;
        this.ropeNum = ropeNum_;
        this.runningCircleNum = runningCircleNum_;
    }

    public Fat(int id_, long date_, float morningWeight_, float noonWeight_, float nightWeight_, int ropeNum_, int runningCircleNum_) {
        this.id = id_;
        this.date = date_;
        this.morningWeight = morningWeight_;
        this.noonWeight = noonWeight_;
        this.nightWeight = nightWeight_;
        this.ropeNum = ropeNum_;
        this.runningCircleNum = runningCircleNum_;
    }

    public void setHolder(TextView txtVeiwData_, TextView txtVeiwMorning_, TextView txtVeiwNoon_, TextView txtVeiwNight_,
                          TextView txtVeiwRope_, TextView txtVeiwCircel_, TextView txtVeiwId_) {
        txtVeiwData_.setText(DateTimeTrans.getMonthDay2String(this.date));
        txtVeiwMorning_.setText(String.valueOf(this.morningWeight));
        txtVeiwNoon_.setText(String.valueOf(this.noonWeight));
        txtVeiwNight_.setText(String.valueOf(this.nightWeight));
        txtVeiwRope_.setText(String.valueOf(this.ropeNum));
        txtVeiwCircel_.setText(String.valueOf(this.runningCircleNum));
        txtVeiwId_.setText(String.valueOf(this.id));
    }

    public String getDate2String() {
        return DateTimeTrans.getDate2String(this.date);
    }

    public static List<Fat> getAddFats() {
        List<Fat> allFat = new ArrayList<>();
        Cursor cursor = DataBaseHelper.getDb().rawQuery("select " + selectColName + " from fat order by date", null);
        if(cursor == null) {
            return allFat;
        }
        while(cursor.moveToNext()) {
            Fat fat = new Fat(cursor.getInt(0), cursor.getInt(1),
                    cursor.getFloat(2), cursor.getFloat(3),
                    cursor.getFloat(4), cursor.getInt(5),
                    cursor.getInt(5));
            allFat.add(fat);
        }
        return allFat;
    }

    public static List<Fat> getOneMonthFormDb(int year, int month) {
        String dataStartStr = String.format("%d年%02d月%02d日", year, month, 1);  //yyyy年MM月dd日
        String dataEndStr = String.format("%d年%02d月%02d日", year, month + 1, 1);  //yyyy年MM月dd日
        long dataStart = DateTimeTrans.getString2Date(dataStartStr);
        long dataEnd = DateTimeTrans.getString2Date(dataEndStr);
        List<Fat> fats = new ArrayList<>();
        Cursor cursor = DataBaseHelper.getDb().rawQuery("select " + selectColName + " from fat where date >= " +
                dataStart + " and date < " + dataEnd + " order by date", null);
        if(cursor == null) {
            return fats;
        }
        while(cursor.moveToNext()) {
            Fat fat = new Fat(cursor.getInt(0), cursor.getLong(1),
                    cursor.getFloat(2), cursor.getFloat(3),
                    cursor.getFloat(4), cursor.getInt(5),
                    cursor.getInt(6));
            fats.add(fat);
        }
        return fats;
    }

    public static Fat getOneFormDb(int id) {
        List<Fat> fats = new ArrayList<>();
        Cursor cursor = DataBaseHelper.getDb().rawQuery("select " + selectColName + " from fat where id = " + id, null);
        if(cursor == null) {
            return null;
        }
        while(cursor.moveToNext()) {
            Fat fat = new Fat(cursor.getInt(0), cursor.getLong(1),
                    cursor.getFloat(2), cursor.getFloat(3),
                    cursor.getFloat(4), cursor.getInt(5),
                    cursor.getInt(6));
            fats.add(fat);
        }
        return fats.get(0);
    }

    public static void insertToDb(Fat fat) {
        DataBaseHelper.dbExecSQL("insert into fat(" + insertColName + ") values(?, ?, ?, ?, ?, ?)",
                new Object[]{fat.date, fat.morningWeight, fat.noonWeight, fat.nightWeight, fat.ropeNum, fat.runningCircleNum});
    }

    public static void deleteFromDb(int id) {
        DataBaseHelper.dbExecSQL("delete from fat where id = " + id);
    }

    public static void updateToDb(int id, Fat fat) {
        DataBaseHelper.dbExecSQL("update fat set " + updateColName + " where id = " + id,
                new Object[]{fat.date, fat.morningWeight, fat.noonWeight, fat.nightWeight, fat.ropeNum, fat.runningCircleNum});
    }


    int id;
    long date;
    float morningWeight;
    float noonWeight;
    float nightWeight;
    int ropeNum;
    int runningCircleNum;

    private static SQLiteDatabase db = null;
    private static DataBaseHelper dbHelper = null;

    private final static String insertColName= "date, morning, noon, night, rope, circle";
    private final static String selectColName= "id, " + insertColName;
    private final static String updateColName= insertColName.replace(",", " = ?,") + " = ?";

}
