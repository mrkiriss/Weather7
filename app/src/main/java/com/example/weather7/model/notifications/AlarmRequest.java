package com.example.weather7.model.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.example.weather7.utils.DateConverter;

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
    private final int flag= PendingIntent.FLAG_CANCEL_CURRENT;

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
    public void setIntervalAndTriggerTime(String repeatMode, String date, String time){
        switch (repeatMode){
            case "Ежедневно":
                interval=INTERVAL_DAY;
                triggerTime= DateConverter.parseDMYHMForTime(date+" "+time);
                break;
            case "Без повторений":
                interval=INTERVAL_NONE;
                triggerTime= DateConverter.parseHMForTime(time);
                break;
            case "В определённый день":
                interval=INTERVAL_SPECIFIC_DATE;
                triggerTime= DateConverter.parseDMYHMForTime(date+" "+time);
                break;
        }
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
    public int getFlag() {
        return flag;
    }
    public Intent getIntent() {
        return intent;
    }
}