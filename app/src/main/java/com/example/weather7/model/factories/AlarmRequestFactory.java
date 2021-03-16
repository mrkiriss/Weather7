package com.example.weather7.model.factories;

import android.content.Context;

import com.example.weather7.model.notifications.AlarmRequest;

public class AlarmRequestFactory {

    public AlarmRequest create(Context appContext, String actionID, String cityName, String repeatMode, String date, String time){
        return new AlarmRequest( appContext, actionID,  cityName,  repeatMode,  date,  time);
    }

    public AlarmRequest create(Context appContext, String actionID){
        return new AlarmRequest(appContext, actionID);
    }
}
