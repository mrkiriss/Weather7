package com.example.weather7.model.base;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.weather7.database.ConvertersCities;
import com.example.weather7.view.cities.adapters.DaysAdapter;

import java.util.LinkedList;

@Entity
public class City{
    @PrimaryKey
    @NonNull
    private String name;
    private String timezone;
    private String lat;
    private String lon;
    private String current_temp;
    @TypeConverters({ConvertersCities.BitmapConverter.class})
    private Bitmap current_icon;
    private String current_description;
    @ColumnInfo(name="days_adapter")
    @TypeConverters({ConvertersCities.DayAdapterConverter.class})
    private DaysAdapter daysAdapter;

    private boolean isLocationCity;

    // создаёт только данные для шапки
    @Ignore
    public City(String name, String timezone, String lat, String lon, String current_temp, String current_description, Bitmap current_icon){
        this.name=name;
        this.lat=lat;
        this.lon=lon;
        this.current_temp=current_temp;
        this.current_description=current_description;
        this.current_icon=current_icon;
        this.daysAdapter=new DaysAdapter(new LinkedList<>(), name);
        this.timezone=timezone;
    };

    public City(){};

    public boolean isCity(){
        return (current_description != null);
    }

    public String getName(){return name;}
    public String getCurrent_temp(){return current_temp;}
    public String getLat(){return lat;}
    public String getLon(){return lon;}
    public Bitmap getCurrent_icon(){return current_icon;}
    public DaysAdapter getDaysAdapter(){return daysAdapter;}
    public String getCurrent_description(){return current_description;}
    public String getTimezone(){return timezone;}
    public boolean getIsLocationCity(){return isLocationCity;}

    public void setName(String name){this.name= name;}
    public void setCurrent_temp(String current_temp){this.current_temp= current_temp;}
    public void setLat(String lat){this.lat= lat;}
    public void setLon(String lon){this.lon= lon;}
    public void setCurrent_icon(Bitmap current_icon){this.current_icon= current_icon;}
    public void setDaysAdapter(DaysAdapter daysAdapter){ this.daysAdapter=daysAdapter;}
    public void setCurrent_description(String current_description){this.current_description= current_description;}
    public void setTimezone(String timezone){this.timezone=timezone;}
    public void setIsLocationCity(boolean isLocationCity){this.isLocationCity=isLocationCity;}

}
