package com.example.weather7.viewmodel;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.ViewModel;

import com.example.weather7.model.City;

public class ItemCityViewModel extends BaseObservable {

    private City city;
    private Context context;

    public ItemCityViewModel(Context context, City city){
        this.context=context;
        this.city=city;
    }

    public void setCity(City city){
        this.city=city;
        notifyChange();
    }
}
