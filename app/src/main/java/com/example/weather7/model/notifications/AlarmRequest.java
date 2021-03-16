package com.example.weather7.model.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.weather7.services.WeatherNotificationReceiver;
import com.example.weather7.utils.DateConverter;

import java.util.Date;

public class AlarmRequest {

    public static final int PENDING_INTENT_REQUEST_CODE_BASE=1;
    public static final long INTERVAL_DAY=AlarmManager.INTERVAL_DAY;
    public static final long INTERVAL_NONE=0;
    public static final long INTERVAL_SPECIFIC_DATE=1;

    // alarm data
    private long triggerTime;
    private long interval;
    private PendingIntent pendingIntent;

    // pending data
    private Context context;
    public final int requestCode = PENDING_INTENT_REQUEST_CODE_BASE;
    private Intent intent;
    public static final int PENDING_INTENT_FLAG= PendingIntent.FLAG_CANCEL_CURRENT;

    //View data
    private String recycledData;

    private long DAY = 24*60*60*1000;

    public AlarmRequest(Context context, String actionID, String cityName, String repeatMode, String date, String time){
        this.interval=INTERVAL_NONE;
        this.context=context;

        this.intent=createIntent(actionID, cityName, repeatMode);
        this.recycledData=fillIntervalAndTriggerTime(repeatMode, date, time);
        this.pendingIntent=createPendingIntent();
    }
    public AlarmRequest(Context context, String actionID){
        this.pendingIntent=createCancelPendingIntent(context, actionID);
    }

    private Intent createIntent(String actionID, String cityName, String repeatMode){
        Intent intent = new Intent();
        intent.setClass(context, WeatherNotificationReceiver.class);
        intent.setAction(actionID);
        intent.putExtra("cityName", cityName);
        intent.putExtra("mode", repeatMode);
        return intent;
    }
    private PendingIntent createPendingIntent(){
        PendingIntent pendingIntent= PendingIntent.getBroadcast(context,
                requestCode, intent, PENDING_INTENT_FLAG);

        return pendingIntent;
    }
    private String fillIntervalAndTriggerTime(String repeatMode, String date, String time){

        long inputTriggerTime;
        switch (repeatMode){
            case "Ежедневно":
                interval=INTERVAL_DAY;
                triggerTime= DateConverter.parseDMYHMForTime(date+" "+time);
                break;
            case "Без повторений":
                interval=INTERVAL_NONE;
                inputTriggerTime= DateConverter.parseHMForTime(time);
                // если время уже прошло, перенести на след. день
                long addition = (isPast(inputTriggerTime)? DAY:0);
                triggerTime= DateConverter.parseHMForTime(time)+addition;
                break;
            case "В определённый день":
                interval=INTERVAL_SPECIFIC_DATE;
                // если время уже прошло, перенести на след. день
                inputTriggerTime= DateConverter.parseDMYHMForTime(date+" "+time);
                if (isPast(inputTriggerTime)){
                    triggerTime= DateConverter.parseHMForTime(time);
                    if (isPast(triggerTime)) triggerTime+=DAY;
                }else{
                    triggerTime= DateConverter.parseDMYHMForTime(date+" "+time);
                }
                break;
        }

        // данные для пользовательского экрана/бд
        return DateConverter.convertLongToDMY(triggerTime);
    }

    private PendingIntent createCancelPendingIntent(Context context, String actionID){
        Intent intent = new Intent(context, WeatherNotificationReceiver.class);
        intent.setAction(actionID);
        return PendingIntent.getBroadcast(context,
                AlarmRequest.PENDING_INTENT_REQUEST_CODE_BASE, intent, AlarmRequest.PENDING_INTENT_FLAG);
    }

    private boolean isPast(long time){
        long inaccuracy=1000;
        return (time + inaccuracy<new Date().getTime());
    }

    public String getRecycledData(){
        return recycledData;
    }
    public PendingIntent getPendingIntent(){
        return  pendingIntent;
    }
    public long getTriggerTime() {
        return triggerTime;
    }
    public long getInterval() {
        return interval;
    }


}
