package com.example.weather7.repository.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weather7.database.AppDatabase;
import com.example.weather7.database.CityDao;
import com.example.weather7.database.NotificationDao;
import com.example.weather7.model.factories.NotificationFactory;
import com.example.weather7.model.factories.ThreadFactory;
import com.example.weather7.model.notifications.Notification;
import com.example.weather7.repository.RepositoryRequest;
import com.example.weather7.utils.AlarmManager;

import java.util.ArrayList;

public class NotificationRepository {

    public static final String REQUEST_DELETE="delete";

    private CityDao cityDao;
    private NotificationDao notificationDao;
    private ThreadFactory threadFactory;
    private NotificationFactory notificationFactory;
    private final AlarmManager alarmManager;

    private int numberOfActiveAddingNotificationTasks;

    private MutableLiveData<Notification> addNotificationDataRequest;
    private MutableLiveData<Notification> deleteNotificationDataRequest;
    private MutableLiveData<String> toastContent;

    public NotificationRepository(AppDatabase db, AlarmManager alarmManager, ThreadFactory threadFactory, NotificationFactory notificationFactory){
        this.cityDao =db.getCityDao();
        this.notificationDao=db.getNotificationDao();
        this.alarmManager=alarmManager;
        this.threadFactory=threadFactory;
        this.notificationFactory=notificationFactory;

        this.addNotificationDataRequest=new MutableLiveData<>();
        this.toastContent=new MutableLiveData<>();
        this.deleteNotificationDataRequest=new MutableLiveData<>();

        this.numberOfActiveAddingNotificationTasks=0;
    }

    public ArrayList<String> getNamesOfCities(){
        ArrayList<String> result = new ArrayList<>();
        result.addAll(cityDao.getNames());
        return result;
    }
    public void fillingNotifications(){
        ArrayList<Notification> content = new ArrayList<>(notificationDao.getNotifications());
        for (Notification notification: content){
            Runnable task = () -> addNotificationToView(notification);

            threadFactory.newThread(task).start();
        }
    }
    public void onRepositoryRequest(RepositoryRequest req){
        switch (req.getMode()){
            case REQUEST_DELETE:
                cancelAlarmTask((Notification) req.getObject());
        }
    }
    public boolean someoneAdderActive(){
        return (numberOfActiveAddingNotificationTasks>0);
    }

    // создаёт задачу через AlarmManager, отображает на экране, добавляет в базу
    public void createAlarmTask(String cityName, String repeatMode, String date, String time){
        String actionID = getCountOfAlarmTasks();
        String recycledData = alarmManager.createAlarmTask(actionID, cityName, repeatMode, date, time).getRecycledData();

        addNotificationToViewAndBase(cityName, repeatMode, recycledData, time);
    }
    private void cancelAlarmTask(Notification notification){
        alarmManager.cancelAlarmTask(notification.getActionID());

        deleteNotificationFromViewAndBase(notification);
    }
    private void addNotificationToViewAndBase(String cityName, String repeatMode,String  date, String time){
        Runnable task = () -> {
            numberOfActiveAddingNotificationTasks++;

            Notification notification = notificationFactory.create(cityName, repeatMode, date, time, getCountOfAlarmTasks());
            addNotificationToView(notification);
            addNotificationToBase(notification);

            numberOfActiveAddingNotificationTasks--;
        };

        threadFactory.newThread(task).start();
    }
    private void deleteNotificationFromViewAndBase(Notification notification){
        deleteNotificationFromView(notification);
        deleteNotificationFromBase(notification);
    }

    private void deleteNotificationFromView(Notification notification){
        deleteNotificationDataRequest.setValue(notification);
    }
    private void deleteNotificationFromBase(Notification notification){
        notificationDao.deleteByActionID(notification.getActionID());
    }

    private synchronized void addNotificationToView(Notification notification){
        addNotificationDataRequest.postValue(notification);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void addNotificationToBase(Notification notification){
        notificationDao.insert(notification);
    }

    private String getCountOfAlarmTasks(){
        return String.valueOf(notificationDao.getCountOfNotifications());
    }

    public LiveData<String> getToastContent(){return toastContent;}
    public LiveData<Notification> getAddNotificationDataRequest(){return addNotificationDataRequest;}
    public LiveData<Notification> getDeleteNotificationDataRequest(){return deleteNotificationDataRequest;}

}
