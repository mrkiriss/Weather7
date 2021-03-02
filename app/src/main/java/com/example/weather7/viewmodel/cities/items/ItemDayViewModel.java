package com.example.weather7.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.example.weather7.R;
import com.example.weather7.model.WeatherOnDay;

public class ItemDayViewModel  extends BaseObservable {
    private WeatherOnDay day;

    private String date;
    private Bitmap icon;
    private String date_tempDayNight;
    private String tempDay;
    private String tempNight;
    private String wind_speed;
    private String pressure;
    private String humidity;
    private String clouds;
    private String description;

    public ObservableBoolean expandable;

    public String getDate() {
        return date;
    }
    public String getDate_tempDayNight() {
        return date_tempDayNight;
    }
    public String getTempDay() {
        return tempDay;
    }
    public String getTempNight() {
        return tempNight;
    }
    public Bitmap getIcon() {
        return icon;
    }
    public ObservableBoolean getExpandable() {
        return expandable;
    }
    public String getWind_speed() {
        return "Скорость ветра: "+wind_speed+"м/с";
    }
    public String getPressure() {
        return "Давление: "+pressure+"Па";
    }
    public String getHumidity() {
        return "Влажность: "+humidity+"%";
    }
    public String getClouds() {
        return "Облачность: "+clouds+"%";
    }
    public String getDescription(){return description;}

    public ItemDayViewModel( WeatherOnDay day){
        this.day=day;

        // значения для шапки
        this.date=day.getDate();
        this.icon=day.getIcon();
        this.date_tempDayNight=day.getDateAndDayNightTemp();
        this.description=day.getDescription();
        // значения для таблицы
        this.tempDay=day.getDayTemp();
        this.tempNight=day.getNightTemp();
        this.wind_speed=day.getWind_speed();
        this.pressure=day.getPressure();
        this.humidity=day.getHumidity();
        this.clouds=day.getClouds();

        this.expandable=new ObservableBoolean(false);
    }

    public void setDay(WeatherOnDay day){
        this.day=day;
        notifyChange();
    }

    public void changeExpandable(){
        if (expandable.get()) {
            expandable.set(false);
        } else {
            expandable.set(true);
        }
    }
    @BindingAdapter("android:srcCompact")
    public static void loadIcon(ImageView iv, Bitmap bitmap) {
        iv.setImageBitmap(bitmap);
    }
}
