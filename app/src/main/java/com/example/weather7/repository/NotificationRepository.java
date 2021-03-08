package com.example.weather7.repository;

import android.content.Intent;

import com.example.weather7.database.CityDao;
import com.example.weather7.utils.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class NotificationRepository {

    private CityDao dao;

    public NotificationRepository(CityDao dao){
        this.dao=dao;
    }

    public ArrayList<String> getNamesOfCities(){
        ArrayList<String> result = new ArrayList<>();
        result.addAll(dao.getNames());

        return result;
    }

    public int getCountOfAlarmTasks(){
        return new Random().nextInt();
    }
}
