package com.example.weather7.model;

import android.graphics.Bitmap;

public class WeatherOnDay {
    String date;
    String[] temp;  //{day, night, feels_like_day, feels_like_night}
    String wind_speed;
    Bitmap icon;
    String pressure;
    String humidity;
    String clouds;

    // погода на день недели
    public WeatherOnDay(String date, String[] temp, String wind_speed, Bitmap icon,
                        String pressure, String humidity, String clouds){
        this.date=date;
        this.temp=temp;
        this.wind_speed=wind_speed;
        this.icon=icon;
        this.pressure=pressure;
        this.humidity=humidity;
        this.clouds=clouds;
    }
    // погода нынешняя
    public WeatherOnDay(String date, String[] temp, String wind_speed, Bitmap icon){
        this.date=date;
        this.temp=temp;
        this.wind_speed=wind_speed;
        this.icon=icon;
    }
}
