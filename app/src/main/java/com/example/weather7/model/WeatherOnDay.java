package com.example.weather7.model;

import android.graphics.Bitmap;

import java.util.Calendar;

public class WeatherOnDay {


    private String date;
    private String[] temp;  //{day, night, feels_like_day, feels_like_night}
    private String wind_speed;
    private Bitmap icon;
    private String pressure;
    private String humidity;
    private String clouds;

    public String getDate() {
        return date;
    }
    public String getWind_speed() {
        return wind_speed;
    }
    public String getPressure() {
        return pressure;
    }
    public String getHumidity() {
        return humidity;
    }
    public String getClouds() {
        return clouds;
    }
    public Bitmap getIcon() {
        return icon;
    }
    public String[] getTemp() {
        return temp;
    }
    public String getDayNightTemp(){return temp[0]+"/"+temp[1]+"°C";}
    public  String getDayTemp(){return temp[0]+"/"+temp[2]+"°C";}
    public  String getNightTemp(){return temp[1]+"/"+temp[3]+"°C";}

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
