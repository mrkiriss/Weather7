package com.example.weather7.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather7.model.City;
import com.example.weather7.repository.CityRepository;
import com.example.weather7.repository.api.WeatherApi;

import java.util.LinkedList;

public class CitiesViewModel extends ViewModel {

    private CityRepository rep;
    private LiveData<Boolean> connection;
    private LiveData<Boolean> loading;
    private LiveData<LinkedList<City>> cities;

    public CitiesViewModel(CityRepository rep) {
        this.rep = rep;
        // получаем ссылки на объекты модели которые могут быть ей обновлены
        this.connection=rep.getConnection();
        this.loading=rep.getLoading();
        this.cities=rep.getCities();
        // обновляем список городов
        rep.downloadCities();

    }

    public void onClickFind(String city_name){
        rep.downloadCityFromApi(WeatherApi.MODE_ALL, city_name);
    }

    public void processRequest(String request){
        rep.processRequest(request);
    }

    public LiveData<LinkedList<City>> getCities(){return cities;}
    public LiveData<Boolean> getConnection(){return connection;}
    public LiveData<Boolean> getLoading(){return loading;}
}