package com.example.weather7.viewmodel;

import android.content.Context;

import androidx.databinding.BaseObservable;

import com.example.weather7.model.WeatherOnDay;

public class ItemDayViewModel  extends BaseObservable {
    private WeatherOnDay day;
    private Context context;

    public ItemDayViewModel(Context context, WeatherOnDay day){
        this.context=context;
        this.day=day;
    }

    public void setDay(WeatherOnDay day){
        this.day=day;
        notifyChange();
    }
}
