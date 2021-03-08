package com.example.weather7.model.notifications;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Notification {

    private String cityName;
    private String repeatMode;
    private String date;
    private String time;
    @PrimaryKey
    @NonNull
    private String actionID;

    public Notification(String cityName, String repeatMode, String date, String time, String actionID){
        this.cityName=cityName;
        this.repeatMode=repeatMode;
        this.date=date;
        this.time=time;
        this.actionID=actionID;
    }

    public String getCityName() {
        return cityName;
    }

    public String getRepeatMode() {
        return repeatMode;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getActionID() {
        return actionID;
    }
}
