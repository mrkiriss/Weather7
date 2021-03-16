package com.example.weather7.services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.weather7.api.IWeatherApi;
import com.example.weather7.database.AppDatabase;
import com.example.weather7.database.NotificationDao;
import com.example.weather7.di.App;
import com.example.weather7.model.factories.ThreadFactory;
import com.example.weather7.utils.AlarmManager;
import com.example.weather7.view.MainActivity;
import com.example.weather7.viewmodel.notifications.NotificationsViewModel;

import org.json.JSONException;

import java.io.IOException;

import javax.inject.Inject;

public class WeatherNotificationReceiver extends BroadcastReceiver {


    private NotificationDao notificationDao;
    @Inject
     AppDatabase appDatabase;
    @Inject
     AlarmManager alarmManager;
    @Inject
     ThreadFactory threadFactory;
    @Inject
     IWeatherApi weatherApi;

    private Context context;
    private Intent intent;

    String[] notificationContentIfError = new String[]{"", "Ошибка. Проверьте интернет-соединение"};
    private String cityName;
    private String actionID;
    private MutableLiveData<String[]> content;
    private PendingIntent contentIntent;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        App.getInstance().getComponentManager().getWeatherNotificationReciverSubcomponent().inject(this);

        this.intent=intent;
        this.context=context;
        this.content=new MutableLiveData<>();

        prepareData();

        // подписываемся на получение данных для уведомления
        content.observeForever(strings -> showNotification());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification(){
        alarmManager.showNotification(context, actionID, cityName, content.getValue(), contentIntent);
        checkNeedForDeletion();

        // отписываемся от получения данных
        content.removeObserver(strings -> showNotification());
    }

    private void prepareData(){
        cityName = intent.getStringExtra("cityName");
        actionID = intent.getAction();

        getNotificationContent();

        Intent startAppIntent = new Intent(context, MainActivity.class);
        startAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        contentIntent = PendingIntent.getActivity(context, 1,startAppIntent, PendingIntent.FLAG_ONE_SHOT);
    }
    private void getNotificationContent(){
        Runnable task = () -> {
            try {
                content.postValue(weatherApi.getNotificationContent(cityName));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                content.postValue(notificationContentIfError);
                Toast.makeText(context, "Контент уведомления не был получен" , Toast.LENGTH_SHORT).show();
            }
        };
        threadFactory.newThread(task).start();

    }

    private void checkNeedForDeletion(){
        if (!intent.getStringExtra("mode").equals("Ежедневно")) {
            notificationDao = appDatabase.getNotificationDao();
            deleteNotificationFromBase(actionID);
        }
    }
    private void deleteNotificationFromBase(String actionID){
        notificationDao.deleteByActionID(actionID);
        NotificationsViewModel.onNotificationsChanged.notifyPropertyChanged(0);
    }


}
