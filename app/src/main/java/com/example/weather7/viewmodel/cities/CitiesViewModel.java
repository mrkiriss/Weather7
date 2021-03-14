package com.example.weather7.viewmodel.cities;

import android.content.Intent;

import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather7.model.cities.AutoEnteredCity;
import com.example.weather7.model.base.City;
import com.example.weather7.repository.RepositoryRequest;
import com.example.weather7.repository.cities.CityRepository;
import com.example.weather7.view.cities.adapters.DaysAdapter;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class CitiesViewModel extends ViewModel {

    private ObservableField<String> text_city;
    private ObservableBoolean progress_visible;
    private ObservableBoolean names_progress_visible;

    private LiveData<Boolean> cities_loading;
    private LiveData<Boolean> names_cities_loading;
    private LiveData<List<AutoEnteredCity>> auto_cities;
    private LiveData<Boolean> connection;
    private LiveData<LinkedList<City>> cities;
    private LiveData<City> addCityHeadRequest;
    private LiveData<City> deleteCityRequest;
    private LiveData<DaysAdapter> addDaysInCityRequest;
    private LiveData<Intent> startIntent;
    private LiveData<String> error_content;

    private CityRepository rep;

    public CitiesViewModel(CityRepository rep) {
        this.rep = rep;

        this.text_city = new ObservableField<>();
        this.progress_visible=new ObservableBoolean();
        this.names_progress_visible=new ObservableBoolean();

        // получаем ссылки на объекты модели которые могут быть ей обновлены
        this.connection=rep.getConnection();
        this.cities=rep.getCities();
        this.addCityHeadRequest=rep.getAddCityHeadRequest();
        this.addDaysInCityRequest=rep.getAddDaysInCityRequest();
        this.error_content=rep.getError_content();
        this.deleteCityRequest=rep.getDeleteCityRequest();
        this.startIntent=rep.getStartIntent();
        this.cities_loading= rep.getCities_loading();
        this.auto_cities=rep.getAuto_cities();
        this.names_cities_loading=rep.getNames_cities_loading();

        progress_visible.set(false);
        names_progress_visible.set(false);
        // реагирование на ввод пользователя
        initTextCityCallback();
        // обновляем список городов
        rep.fillingCities();

    }

    public void onClickFind(String city_name){
        rep.runAddingSingleCityFromAPI(city_name);
        text_city.set("");
    }

    public void refreshCities(){
        rep.fillingCities();
    }

    public void processRequest(RepositoryRequest request){
        rep.processRequest(request);
    }

    private void initTextCityCallback(){
        text_city.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                rep.respondToInput(Objects.requireNonNull(text_city.get()));
            }
        });
    }

    public LiveData<LinkedList<City>> getCities(){return cities;}
    public LiveData<List<AutoEnteredCity>> getAuto_cities(){return auto_cities;}
    public LiveData<Boolean> getConnection(){return connection;}
    public  LiveData<City> getAddCityHeadRequest(){return addCityHeadRequest;}
    public  LiveData<City> getDeleteCityRequest(){return deleteCityRequest;}
    public  LiveData<DaysAdapter> getAddDaysInCityRequest(){return addDaysInCityRequest;}
    public  LiveData<String> getError_content(){return error_content;}
    public ObservableField<String> getText_city(){return text_city;}
    public LiveData<Intent> getStartIntent(){return startIntent;}
    public LiveData<Boolean> getCities_loading(){return cities_loading;}
    public ObservableBoolean getProgress_visible(){return progress_visible;}
    public ObservableBoolean getNames_progress_visible(){return names_progress_visible;}
    public LiveData<Boolean> getNames_cities_loading(){return names_cities_loading;}
    public void setProgress_visible(Boolean visible){this.progress_visible.set(visible);}
    public void setNames_progress_visible(Boolean visible){this.names_progress_visible.set(visible);}
}