package com.example.weather7.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.weather7.view.adapters.DaysAdapter;
import com.example.weather7.database.Converters;

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
    @ColumnInfo(name="days_adapter")
    @TypeConverters({Converters.DayAdapterConverter.class})
    private DaysAdapter daysAdapter;

    private long upload_time;

    // создаёт только данные для шапки
    @Ignore
    public City(String name, String lat, String lon, String current_temp, String current_description, Bitmap current_icon){
        this.name=name;
        this.lat=lat;
        this.lon=lon;
        this.current_temp=current_temp;
        this.current_description=current_description;
        this.current_icon=current_icon;
        this.daysAdapter=new DaysAdapter(new LinkedList<>(), name);
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
    public long getUpload_time(){return upload_time;}

    public void setName(String name){this.name= name;}
    public void setCurrent_temp(String current_temp){this.current_temp= current_temp;}
    public void setLat(String lat){this.lat= lat;}
    public void setLon(String lon){this.lon= lon;}
    public void setCurrent_icon(Bitmap current_icon){this.current_icon= current_icon;}
    public void setDaysAdapter(DaysAdapter daysAdapter){ this.daysAdapter=daysAdapter;}
    public void setCurrent_description(String current_description){this.current_description= current_description;}
    public void setUpload_time(long upload_time){this.upload_time=upload_time;}

}
