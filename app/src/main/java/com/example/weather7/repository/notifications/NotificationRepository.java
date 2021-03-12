package com.example.weather7.repository.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.weather7.database.CityDao;
import com.example.weather7.database.NotificationDao;
import com.example.weather7.model.notifications.Notification;
import com.example.weather7.repository.RepositoryRequest;

import java.util.ArrayList;
import java.util.Date;

public class NotificationRepository {

    public static final String REQUEST_DELETE="delete";

    private CityDao cityDao;
    private NotificationDao notificationDao;

    private int numberOfActiveAddingNotificationTasks;

    private MutableLiveData<Notification> addNotificationDataRequest;
    private MutableLiveData<Notification> deleteNotificationDataRequest;
    private MutableLiveData<String> toastContent;

    public NotificationRepository(CityDao cityDao, NotificationDao notificationDao){
        this.cityDao =cityDao;
        this.notificationDao=notificationDao;

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
            Thread thread = new Thread(task);
            thread.start();
        }
    }

    public String getCountOfAlarmTasks(){
        return String.valueOf(notificationDao.getCountOfNotifications());
    }
    public void onRepositoryRequest(RepositoryRequest req){
        switch (req.getMode()){
            case REQUEST_DELETE:
                deleteNotificationFromViewAndBase((Notification) req.getObject());
        }
    }
    public boolean someoneAdderActive(){
        return (numberOfActiveAddingNotificationTasks>0);
    }

    public void addNotificationToViewAndBase(String cityName, String repeatMode,String  date, String time){
        Runnable task = () -> {
            numberOfActiveAddingNotificationTasks++;

            Notification notification = new Notification(cityName, repeatMode, date, time, getCountOfAlarmTasks());
            addNotificationToView(notification);
            addNotificationToBase(notification);

            numberOfActiveAddingNotificationTasks--;
        };

        Thread thread = new Thread(task);
        thread.start();
    }
    public void deleteNotificationFromViewAndBase(Notification notification){
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

    public LiveData<String> getToastContent(){return toastContent;}
    public LiveData<Notification> getAddNotificationDataRequest(){return addNotificationDataRequest;}
    public LiveData<Notification> getDeleteNotificationDataRequest(){return deleteNotificationDataRequest;}

}
