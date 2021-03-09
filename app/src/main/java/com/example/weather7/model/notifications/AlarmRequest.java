package com.example.weather7.model.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.example.weather7.utils.DateConverter;

import java.util.Date;

public class AlarmRequest {

    public static final int PENDING_INTENT_REQUEST_CODE_BASE=1;
    public static final long INTERVAL_DAY=AlarmManager.INTERVAL_DAY;
    public static final long INTERVAL_NONE=0;
    public static final long INTERVAL_SPECIFIC_DATE=1;

    // alarm data
    private final int alarmType = AlarmManager.RTC;
    private long triggerTime;
    private long interval;
     // PendingIntent

    // pending data
     // Context
    private int requestCode = PENDING_INTENT_REQUEST_CODE_BASE;
     // Intent
    public final static int PENDING_INTENT_FLAG= PendingIntent.FLAG_CANCEL_CURRENT;

    // intent data
    private Intent intent;
    private String actionID;

    public AlarmRequest(String actionID){
        this.intent=new Intent();

        this.actionID=actionID;
        this.intent.setAction(actionID);

        this.interval=INTERVAL_NONE;
    }

    public void setCityNameInIntent(String cityName){
        intent.putExtra("cityName", cityName);
    }
    public String setIntervalAndTriggerTime(String repeatMode, String date, String time){
        intent.putExtra("mode", repeatMode);

        long addition = 0;

        switch (repeatMode){
            case "Ежедневно":
                interval=INTERVAL_DAY;
                //triggerTime= DateConverter.parseDMYHMForTime(date+" "+time);
                break;
            case "Без повторений":
                interval=INTERVAL_NONE;
                long triggerTime= DateConverter.parseHMForTime(time);

                // если время уже прошло, перенести на след. день
                if (isPast(triggerTime)) addition=24 * 60 * 60 * 1000;

                break;
            case "В определённый день":
                interval=INTERVAL_SPECIFIC_DATE;
                //triggerTime= DateConverter.parseDMYHMForTime(date+" "+time);
                break;
        }
        triggerTime= DateConverter.parseDMYHMForTime(date+" "+time) + addition;

        return DateConverter.convertLongToDMY(triggerTime);
    }

    private boolean isPast(long time){
        long inaccuracy=5000;
        return (time + inaccuracy<new Date().getTime());
    }

    public int getAlarmType() {
        return alarmType;
    }
    public long getTriggerTime() {
        return triggerTime;
    }
    public long getInterval() {
        return interval;
    }
    public int getRequestCode() {
        return requestCode;
    }
    public Intent getIntent() {
        return intent;
    }
}
