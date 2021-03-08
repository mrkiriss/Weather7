package com.example.weather7.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.weather7.model.notifications.Notification;

import java.util.List;

@Dao
public interface NotificationDao {

    @Query("SELECT * FROM Notification")
    List<Notification> getNotifications();

    @Query("SELECT COUNT(*) FROM Notification")
    int getCountOfNotifications();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Notification notification);

    @Delete
    void delete(Notification notification);

    @Query("DELETE FROM Notification WHERE actionID = :actionID")
    void deleteByActionID(String actionID);
}
