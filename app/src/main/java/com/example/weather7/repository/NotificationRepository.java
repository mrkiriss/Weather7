package com.example.weather7.repository;

import android.content.Intent;

import androidx.lifecycle.MutableLiveData;

import com.example.weather7.database.CityDao;
import com.example.weather7.utils.ConverterDate;

import java.nio.channels.MulticastChannel;
import java.util.ArrayList;
import java.util.Date;

public class NotificationRepository {

    private CityDao dao;
    private MutableLiveData<Intent> startIntent;

    public NotificationRepository(CityDao dao){
        this.dao=dao;

        this.startIntent=new MutableLiveData<>();
    }

    public ArrayList<String> getNamesOfCities(){
        ArrayList<String> result = new ArrayList<>();
        result.addAll(dao.getNames());

        return result;
    }

    public void createIncompleteAlarmIntent(String date, String time){
        Intent intent = new Intent();

        startIntent.setValue(intent);
    }

    public String getCurrentTime(){
        return ConverterDate.convertLongToHM(new Date().getTime());
    }

    public String getCurrentDate(){
        return ConverterDate.convertLongToDMY(new Date().getTime());
    }
    public MutableLiveData<Intent> getStartIntent() {
        return startIntent;
    }
}
