package com.example.weather7.viewmodel;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather7.model.City;

import java.util.ArrayList;
import java.util.List;

public class CitiesViewModel extends ViewModel {

    ArrayList<City> cities;

    private FragmentCitiesPostman post;

    public CitiesViewModel(FragmentCitiesPostman post) {
        this.post=post;
        cities = new ArrayList<>();
    }

    public void onClickFind(String city_name){
        try {
            City city = new City(city_name);
            cities.add(0, city);
            post.onCitiesChanged(cities);
        }catch (InterruptedException | IndexOutOfBoundsException e){
            // !+++! реализовать вывод ошибки для пользователя
            Log.println(Log.ASSERT, "download error", "Ошибка получения погодных данных для города "+city_name);
        }

    }

    public interface FragmentCitiesPostman{
        void onCitiesChanged(List<City> cities);
    }
}