package com.example.weather7.model;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.weather7.model.adapters.DaysAdapter;
import com.example.weather7.model.api.WeatherApi;
import com.example.weather7.model.database.Converters;

import java.util.LinkedList;

@Entity
public class City{
    @PrimaryKey
    @NonNull
    private String name;
    private String lat;
    private String lon;
    private String current_temp;
    @TypeConverters({Converters.BitmapConverter.class})
    private Bitmap current_icon;
    private String current_description;
    @TypeConverters({Converters.DayAdapterConverter.class})
    private DaysAdapter days;

    private long upload_time;

    @Ignore
    public City(int download_mode, String data) {

        WeatherApi downloader = new WeatherApi(download_mode, data);
        downloader.start();
        try {
            downloader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        createHeaderAndAdapter(downloader.getWeather());
        String[] coord=new String[2];

        switch (download_mode){
            case WeatherApi.MODE_ALL:
                this.name=data;
                coord= downloader.getCoordinate();
                break;
            case WeatherApi.MODE_ONLY_WEATHER:
                this.name=downloader.getCity_name();
                coord= data.split(" ");
                break;
        }

        // создание адаптера дней
        // заполнение координат города
        lat=coord[0];
        lon=coord[1];
    }
    public City(){};

    private void createHeaderAndAdapter (LinkedList<WeatherOnDay> days){
        if (days==null || days.size()==0){
            Log.println(Log.WARN, "createHeaderAndAdapter", "empty citys data");
            return;
        }
        // заполнений полей для шапки адаптера
        enterCurrentData(days.get(0));
        days.remove(0);
        // создание адаптера дней
        this.days=new DaysAdapter(days);

    }
    private void enterCurrentData(WeatherOnDay current_day){
        current_icon=current_day.getIcon();
        current_temp=current_day.getTemp()[0]+"/"+current_day.getTemp()[2]+"°C";
        current_description=current_day.getDescription();
    }

    public String getName(){return name;}
    public String getCurrent_temp(){return current_temp;}
    public String getLat(){return lat;}
    public String getLon(){return lon;}
    public Bitmap getCurrent_icon(){return current_icon;}
    public DaysAdapter getDays(){return days;}
    public String getCurrent_description(){return current_description;}
    public long getUpload_time(){return upload_time;}

    public void setName(String name){this.name= name;}
    public void setCurrent_temp(String current_temp){this.current_temp= current_temp;}
    public void setLat(String lat){this.lat= lat;}
    public void setLon(String lon){this.lon= lon;}
    public void setCurrent_icon(Bitmap current_icon){this.current_icon= current_icon;}
    public void setDays(DaysAdapter days){ this.days=days;}
    public void setCurrent_description(String current_description){this.current_description= current_description;}
    public void setUpload_time(long upload_time){this.upload_time=upload_time;}

    public boolean isCity(){
        return (current_description==null? false: true);
    }
    public static class DeficientCity{
        public String name;
        public long upload_time;
        public String lat;
        public String lon;
    }
}
