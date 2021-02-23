package com.example.weather7.model;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.weather7.model.database.Converters;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherOnDay {

    private String date;
    private String[] temp;  //{day, night, feels_like_day, feels_like_night}
    private String wind_speed;
    private Bitmap icon;
    private String pressure;
    private String humidity;
    private String clouds;
    private String description;

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
    public String getDescription() {
        return description;
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

    public WeatherOnDay(String date, String[] temp, String wind_speed, Bitmap icon,
                        String pressure, String humidity, String clouds, String description){
        this.date=date;
        this.temp=temp;
        this.wind_speed=wind_speed;
        this.icon=icon;
        this.pressure=pressure;
        this.humidity=humidity;
        this.clouds=clouds;
        this.description=description;
    }

    public String toJsonString(){
        try {
            JSONObject obj = new JSONObject();
            obj.put("date", date);
            obj.put("temp", tempToJson());
            obj.put("wind_speed", wind_speed);
            obj.put("icon", Converters.BitmapConverter.bitmapToString(icon));
            obj.put("pressure", pressure);
            obj.put("humidity", humidity);
            obj.put("clouds", clouds);
            obj.put("description", description);
            return obj.toString();
        }catch (JSONException e){
            Log.println(Log.WARN, "WeatherondayToJson", "конвертация отбъекта провалилась");
            return null;
        }
    }

    public static WeatherOnDay jsonToWeatherOnDay(JSONObject day){
        try {
            String date = day.getString("date");
            String[] temp = day.getString("temp").split(",");
            String wind_speed =day.getString("wind_speed");
            Bitmap icon= Converters.BitmapConverter.StringToBitmap(day.getString("icon"));
            String pressure= day.getString("pressure");
            String humidity= day.getString("humidity");
            String clouds= day.getString("clouds");
            String description= day.getString("description");
            return new WeatherOnDay(date, temp, wind_speed, icon, pressure, humidity, clouds, description);
        }catch (JSONException e){
            Log.println(Log.WARN, "jsonStringToDay", "конвертация отбъекта провалилась");
            return null;
        }
    }

    private String tempToJson(){
        String result="";
        result+=temp[0]+",";
        result+=temp[1]+",";
        result+=temp[2]+",";
        result+=temp[3];
        return result;
    }
}
