package com.example.weather7.model.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.room.Room;

import com.example.weather7.R;
import com.example.weather7.api.WeatherApi;
import com.example.weather7.database.AppDatabase;
import com.example.weather7.database.NotificationDao;
import com.example.weather7.view.MainActivity;

import org.json.JSONException;

import java.io.IOException;

public class WeatherNotificationReceiver extends BroadcastReceiver {

    private String cityName;
    private String actionID;

    private NotificationDao notificationDao;

    String[] notificationContent = new String[]{"", "Ошибка. Проверьте интернет-соединение"};

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        cityName = intent.getStringExtra("cityName");
        actionID = intent.getAction();

        notificationDao = Room.databaseBuilder(context,
                AppDatabase.class, "database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build().getNotificationDao();
        deleteNotificationFromBase(actionID);

        String[] content = getNotificationContent(cityName, context);

        Intent startAppIntent = new Intent(context, MainActivity.class);
        startAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 1,startAppIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManagerCompat notificationManager= NotificationManagerCompat.from(context);
        NotificationChannel channel = new NotificationChannel(actionID, "chanel"+actionID, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, intent.getAction())
                .setSmallIcon(R.drawable.ic_app_icon)
                .setContentTitle(cityName+content[0])
                .setContentText(content[1])
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(new long[]{500, 500, 500, 500});

        notificationManager.notify(Integer.parseInt(intent.getAction()), builder.build());

    }

    private String[] getNotificationContent(String cityName, Context context){
        WeatherApi weatherApi = new WeatherApi(context);
        Runnable task = () -> {
            try {
                notificationContent = weatherApi.getNotificationContent(cityName);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(task);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return notificationContent;
        }

        return notificationContent;
    }
    private void deleteNotificationFromBase(String actionID){
        notificationDao.deleteByActionID(actionID);
    }
}
