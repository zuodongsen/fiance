package com.zds.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeTrans {

    public static String getDate2String(Date date_) {
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日");
        return formatter.format(date_);
    }

    public static String getMonthDay2String(Date date_) {
        SimpleDateFormat formatter = new SimpleDateFormat ("MM月dd日");
        return formatter.format(date_);
    }

    public static Date getDate(long date_) {
        Date dateTmp = new Date();
        dateTmp.setTime(date_);
        return dateTmp;
    }

    public static String getDate2String(long date_) {
        Date dateTmp = getDate(date_);
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

}
