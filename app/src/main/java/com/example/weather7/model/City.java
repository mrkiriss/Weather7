package com.example.weather7.model;

import android.graphics.Bitmap;

import androidx.room.Entity;

import com.example.weather7.DayAdapter;

import java.util.ArrayList;

@Entity
public class City{
    private String name="";
    private String lat;
    private String lon;
    private String current_temp;
    private Bitmap current_icon;
    private String current_date;
    DayAdapter days;

    public City(int download_mode, String data) throws InterruptedException {

        WeatherDownloader downloader = new WeatherDownloader(download_mode, data);
        downloader.start();
        downloader.join();
        createHeaderAndAdapter(downloader.getWeather());
        String[] coord=new String[2];

        switch (download_mode){
            case WeatherDownloader.MODE_ALL:
                this.name=data;
                coord= downloader.getCoordinate();
                break;
            case WeatherDownloader.MODE_ONLY_WEATHER:
                this.name=downloader.getCity_name();
                coord= data.split(" ");
                break;
        }

        // создание адаптера дней
        // заполнение координат города
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
