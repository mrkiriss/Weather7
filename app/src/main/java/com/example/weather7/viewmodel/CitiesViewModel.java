package com.example.weather7.viewmodel;

import android.content.Intent;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather7.model.City;
import com.example.weather7.model.RepositoryRequest;
import com.example.weather7.model.WeatherMap;
import com.example.weather7.repository.CityRepository;
import com.example.weather7.api.WeatherApi;
import com.example.weather7.view.FragmentRainMap;
import com.example.weather7.view.adapters.DaysAdapter;

import java.util.LinkedList;

public class CitiesViewModel extends ViewModel {

    private ObservableField<String> empty_text= new ObservableField<>("");
    private CityRepository rep;
    private LiveData<Boolean> connection;
    private LiveData<LinkedList<City>> cities;
    private LiveData<City> addCityHeadRequest;
    private LiveData<City> deleteCityRequest;
    private LiveData<DaysAdapter> addDaysInCityRequest;
    private LiveData<Intent> startIntent;
    private LiveData<FragmentRainMap> openRainMap;

    private LiveData<String> error_content;

    public CitiesViewModel(CityRepository rep) {
        this.rep = rep;
        // получаем ссылки на объекты модели которые могут быть ей обновлены
        this.connection=rep.getConnection();
        this.cities=rep.getCities();
        this.addCityHeadRequest=rep.getAddCityHeadRequest();
        this.addDaysInCityRequest=rep.getAddDaysInCityRequest();
        this.error_content=rep.getError_content();
        this.deleteCityRequest=rep.getDeleteCityRequest();
        this.startIntent=rep.getStartIntent();
        this.openRainMap=rep.getOpenRainMap();

        // обновляем список городов
        rep.firstFillingCities();

    }

    public void onClickFind(String city_name){
        rep.runAddingSingleCityFromAPI(city_name);
        empty_text.notifyChange();
    }

    public void refreshCities(){
        rep.firstFillingCities();
    }

    public void processRequest(RepositoryRequest request){
        rep.processRequest(request);
    }

    public LiveData<LinkedList<City>> getCities(){return cities;}
    public LiveData<Boolean> getConnection(){return connection;}
    public  LiveData<City> getAddCityHeadRequest(){return addCityHeadRequest;}
    public  LiveData<City> getDeleteCityRequest(){return deleteCityRequest;}
    public  LiveData<DaysAdapter> getAddDaysInCityRequest(){return addDaysInCityRequest;}
    public  LiveData<String> getError_content(){return error_content;}
    public ObservableField<String> getEmpty_text(){return empty_text;}
    public LiveData<Intent> getStartIntent(){return startIntent;}
    public LiveData<FragmentRainMap> getOpenRainMap(){return  openRainMap;}
}