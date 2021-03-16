package com.example.weather7.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.weather7.R;
import com.example.weather7.model.factories.AlarmRequestFactory;
import com.example.weather7.model.notifications.AlarmRequest;

public class AlarmManager {

    private final Context appContext;
    private final android.app.AlarmManager alarmManager;
    private final AlarmRequestFactory alarmRequestFactory;


    public AlarmManager(Context appContext, android.app.AlarmManager alarmManager, AlarmRequestFactory alarmRequestFactory){
        this.appContext=appContext;
        this.alarmManager=alarmManager;
        this.alarmRequestFactory=alarmRequestFactory;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showNotification(Context context, String actionID, String cityName, String[] content, PendingIntent contentIntent){
        NotificationManagerCompat notificationManager= NotificationManagerCompat.from(context);
        NotificationChannel channel = new NotificationChannel(actionID, "chanel"+actionID, NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, actionID)
                .setSmallIcon(R.drawable.ic_app_icon)
                .setContentTitle(cityName+content[0])
                .setContentText(content[1])
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(new long[]{500, 500, 500, 500});

        notificationManager.notify(Integer.parseInt(actionID), builder.build());
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
        if (alarmRequest.getInterval() == AlarmRequest.INTERVAL_NONE || alarmRequest.getInterval() == AlarmRequest.INTERVAL_SPECIFIC_DATE) {
            alarmManager.set(android.app.AlarmManager.RTC_WAKEUP,
                    alarmRequest.getTriggerTime(), alarmRequest.getPendingIntent());
        } else {
            alarmManager.setRepeating(android.app.AlarmManager.RTC_WAKEUP,
                    alarmRequest.getTriggerTime(), alarmRequest.getInterval(), alarmRequest.getPendingIntent());
        }
    }
}
