package com.example.weather7.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateConverter {
    public static String convertLongToDMY(Long time){
        Date date = new Date(time);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(date);
    }
    public static String convertLongToHM(Long time){
        Date date = new Date(time);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH-mm");
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(date);
    }
    public static String convertLongToMD(Long time){
        Date date = new Date(time);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd");
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(date);
    }
    public static String convertIntToHM(int[] args){
        String hour = String.valueOf(args[0]);
        if (hour.length()==1) hour="0"+hour;
        String minute = String.valueOf(args[1]);
        if (minute.length()==1) minute="0"+minute;

        return hour+"-"+minute;
    }

    public static String getCurrentTime(){
        return convertLongToHM(new Date().getTime());
    }

    public static String getCurrentDate(){
        return convertLongToDMY(new Date().getTime());
    }

    public static long parseDMYHMForTime(String data){
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy HH-mm");
        f.setTimeZone(TimeZone.getDefault());
        long time=0;
        try {
            Date d = f.parse(data);
            time = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static long parseHMForTime(String data){
        return parseDMYHMForTime(getCurrentDate()+" "+data);
    }
}
