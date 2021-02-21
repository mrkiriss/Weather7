package com.example.weather7.model;

import android.graphics.Bitmap;

import com.example.weather7.DayAdapter;

import java.util.ArrayList;

public class City{
    private String name;
    private String lat;
    private String lon;
    private String current_temp;
    private Bitmap current_icon;
    private String current_date;
    DayAdapter days;

    public City(String name) throws InterruptedException {
        this.name=name;

        WeatherDownloader downloader = new WeatherDownloader(name);
        downloader.start();
        downloader.join();
        // создание адаптера дней
        createHeaderAndAdapter(downloader.getWeather());
        // заполнение координат города
        String[] coord= downloader.getCoordinate().split(" ");
        lat=coord[0];
        lon=coord[1];
    }

    private void createHeaderAndAdapter(ArrayList<WeatherOnDay> days){
        // заполнений полей для шапки адаптера
        enterCurrentData(days.get(0));
        days.remove(0);
        // создание адаптера дней
        this.days=new DayAdapter(days);

    }
    private void enterCurrentData(WeatherOnDay current_day){
        current_date=current_day.getDate();
        current_icon=current_day.getIcon();
        current_temp=current_day.getTemp()[0]+"/"+current_day.getTemp()[2]+"°C";
    }

    public String getName(){return name;}
    public String getCurrent_temp(){return current_temp;}
    public String getCurrent_date(){return current_date;}
    public Bitmap getCurrent_icon(){return current_icon;}
    public DayAdapter getDays(){return days;}
}
