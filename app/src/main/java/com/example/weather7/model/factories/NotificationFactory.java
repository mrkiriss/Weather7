package com.example.weather7.model.factories;

import com.example.weather7.model.notifications.Notification;

public class NotificationFactory {
    public Notification create(String cityName, String repeatMode, String date, String time, String actionID){
        return new Notification( cityName,  repeatMode,  date,  time,  actionID);
    }
}
