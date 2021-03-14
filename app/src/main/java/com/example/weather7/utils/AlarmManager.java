package com.example.weather7.utils;

import android.content.Context;

import com.example.weather7.model.notifications.AlarmRequest;
import com.example.weather7.model.notifications.AlarmRequestFactory;

public class AlarmManager {

    private final Context appContext;
    private final android.app.AlarmManager alarmManager;
    private final AlarmRequestFactory alarmRequestFactory;


    public AlarmManager(Context appContext, android.app.AlarmManager alarmManager, AlarmRequestFactory alarmRequestFactory){
        this.appContext=appContext;
        this.alarmManager=alarmManager;
        this.alarmRequestFactory=alarmRequestFactory;
    }

    public AlarmRequest createAlarmTask(String actionID, String cityName, String repeatMode, String date, String time){
        AlarmRequest alarmRequest = alarmRequestFactory.create(appContext, actionID, cityName, repeatMode, date, time);
        scheduleAlarmRequest(alarmRequest);
        return alarmRequest;
    }
    public void cancelAlarmTask(String actionID){
        AlarmRequest alarmRequest = alarmRequestFactory.create(appContext, actionID);
        planOutAlarmRequest(alarmRequest);
    }

    private void planOutAlarmRequest(AlarmRequest alarmRequest){
        alarmManager.cancel(alarmRequest.getPendingIntent());
    }
    private void scheduleAlarmRequest(AlarmRequest alarmRequest){
        if (alarmRequest.getInterval() == AlarmRequest.INTERVAL_DAY) {
            if (alarmRequest.getInterval() == AlarmRequest.INTERVAL_NONE || alarmRequest.getInterval() == AlarmRequest.INTERVAL_SPECIFIC_DATE) {
                alarmManager.set(android.app.AlarmManager.RTC_WAKEUP,
                        alarmRequest.getTriggerTime(), alarmRequest.getPendingIntent());
            } else {
                alarmManager.setRepeating(android.app.AlarmManager.RTC_WAKEUP,
                        alarmRequest.getTriggerTime(), alarmRequest.getInterval(), alarmRequest.getPendingIntent());
            }
        }
    }
}
