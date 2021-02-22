package com.example.weather7.viewmodel;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather7.model.City;
import com.example.weather7.model.WeatherDownloader;

import java.util.ArrayList;
import java.util.List;

public class CitiesViewModel extends ViewModel {

    private MutableLiveData<ArrayList<City>> mutable_cities;
    private ArrayList<City> cities = new ArrayList<>();

    public CitiesViewModel() {
        mutable_cities = new MutableLiveData<>();

    }

    public void onClickFind(String city_name){
        try {
            City city = new City(WeatherDownloader.MODE_ALL, city_name);
            // получаем актуальное состояние списка
            if (mutable_cities.getValue()!=null) cities=mutable_cities.getValue();
            cities.add(0, city);
            mutable_cities.setValue(cities);
        }catch (InterruptedException | IndexOutOfBoundsException e){
            // !+++! реализовать вывод ошибки для пользователя
            Log.println(Log.ASSERT, "download error", "Ошибка получения погодных данных для города "+city_name);
        }

    }

    public MutableLiveData<ArrayList<City>> getMutableCities(){return mutable_cities;}
}