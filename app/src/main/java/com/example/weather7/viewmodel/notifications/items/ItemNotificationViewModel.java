package com.example.weather7.viewmodel.notifications.items;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;

import com.example.weather7.model.notifications.Notification;
import com.example.weather7.repository.notifications.NotificationRepository;
import com.example.weather7.repository.RepositoryRequest;

import javax.inject.Inject;

public class ItemNotificationViewModel extends BaseObservable {

    private Notification notification;
    private String cityName;
    private String repeatMode;
    private String timeAndDate;
    private String actionID;

    private MutableLiveData<RepositoryRequest> request;

    @Inject
    public ItemNotificationViewModel(Notification notification, MutableLiveData<RepositoryRequest> request){
        this.notification = notification;
        this.request=request;

        this.cityName= notification.getCityName();
        this.repeatMode= notification.getRepeatMode()+" для: ";
        this.timeAndDate= "Время: "+notification.getTime()+"   Дата: "+notification.getDate();
        if (notification.getRepeatMode().equals("Ежедневно")) this.timeAndDate= "Время: "+notification.getTime();
        this.actionID= notification.getActionID();
    }

    public void onDeleteClick(){
        request.setValue(new RepositoryRequest(NotificationRepository.REQUEST_DELETE, notification));
    }

    public String getCityName() {
        return cityName;
    }
    public String getRepeatMode() {
        return repeatMode;
    }
    public String getTimeAndDate() {
        return timeAndDate;
    }
}
