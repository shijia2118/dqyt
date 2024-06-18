package com.hxjt.dqyt.utils;

import androidx.annotation.NonNull;

import com.github.gzuliyujiang.wheelpicker.entity.DateEntity;
import com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity;
import com.github.gzuliyujiang.wheelpicker.entity.TimeEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static String formatWithLeadingZero(int value) {
        return value < 10 ? "0" + value : String.valueOf(value);
    }

    public static boolean isBefore(@NonNull String startDt, String endDt) {
        if(endDt==null) return true;

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.US);


        Date startDate;
        try {
            startDate = sdf.parse(startDt);
        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
        Date endDate;
        try {
            endDate = sdf.parse(endDt);
        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }

        if(startDate == null){
            return false;
        }
        return startDate.compareTo(endDate) <= 0;
    }

    public static boolean isAfter(String startDt, @NonNull String endDt) {
        if(startDt==null) return true;

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.US);


        Date startDate;
        try {
            startDate = sdf.parse(startDt);
        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
        Date endDate;
        try {
            endDate = sdf.parse(endDt);
        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }

        if(endDate == null){
            return false;
        }

        return endDate.compareTo(startDate) >= 0;
    }

    public static int getYear(String value) throws ParseException {
        if(value == null) {
            throw new IllegalArgumentException("value不能为空"+value);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);

        // 解析日期字符串
        Date date = sdf.parse(value);

        if(date == null) {
            throw new IllegalArgumentException("date解析错误"+date);
        }

        // 获取Calendar实例
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(String value) throws ParseException {
        if(value == null) {
            throw new IllegalArgumentException("value不能为空"+value);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);

        // 解析日期字符串
        Date date = sdf.parse(value);

        if(date == null) {
            throw new IllegalArgumentException("date解析错误"+date);
        }

        // 获取Calendar实例
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.MONTH) + 1;
    }

    public static DatimeEntity getDateTimeEntity(String value) throws ParseException {
        if(value == null) {
            return DatimeEntity.now();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);

        // 解析日期字符串
        Date date = sdf.parse(value);

        if(date == null) {
            return DatimeEntity.now();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        DatimeEntity entity = new DatimeEntity();
        entity.setDate(DateEntity.target(calendar));
        entity.setTime(TimeEntity.target(calendar));

        return entity;
    }

}
