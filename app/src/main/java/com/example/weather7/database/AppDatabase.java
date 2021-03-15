package com.example.weather7.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.weather7.model.base.City;
import com.example.weather7.model.notifications.Notification;

@Database(entities = {City.class, Notification.class}, version = 10, exportSchema=  false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CityDao getCityDao();
    public abstract NotificationDao getNotificationDao();
}
