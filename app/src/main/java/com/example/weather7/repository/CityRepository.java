package com.example.weather7.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weather7.model.City;
import com.example.weather7.api.WeatherApi;
import com.example.weather7.database.AppDatabase;
import com.example.weather7.database.CityDao;
import com.example.weather7.model.RepositoryRequest;
import com.example.weather7.utils.ConnectionManager;
import com.example.weather7.view.adapters.DaysAdapter;

import org.json.JSONException;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CityRepository {

    public static final String REQUEST_DELETE="delete";

    private CityDao dao;
    private WeatherApi api;
    private MutableLiveData<Boolean> connection = new MutableLiveData<>(); // состояние интернет соединения для выброса уведомления
    private MutableLiveData<LinkedList<City>> cities = new MutableLiveData<>();

    private MutableLiveData<City> addCityHeadRequest = new MutableLiveData<>();
    private MutableLiveData<City> deleteCityRequest = new MutableLiveData<>();
    private MutableLiveData<DaysAdapter> addDaysInCityRequest = new MutableLiveData<>();
    private MutableLiveData<String> error_content = new MutableLiveData<>();

    public CityRepository(AppDatabase db, WeatherApi api){
        this.dao= db.getCityDao();
        this.api=api;
    }

    public void firstFillingCities(){
        // отображаем загрузку городов

        if (checkConnection()){ // работа с api (немного database)

            // посылаем запросы на обновление всех имеющихся городов
            LinkedList<String> cities_names = getNamesFromBase();
            for (String name: cities_names){
                runAddingSingleCityFromAPI(name);
            }
        }else{ // only database
            downloadCitiesFromBase();
        }
    }

    public void processRequest(RepositoryRequest request){

        switch (request.getMode()){
            case REQUEST_DELETE:
                deleteCity((City) request.getObject());
                break;
        }
    }

    // api

    public void runAddingSingleCityFromAPI(String name){
        // в одном потоке и шапка, и дни, Чтобы дни не смогли прийти раньше шапки
        Runnable task = new Runnable() {
            @Override
            public void run() {
                // получаем город
                City city = null;
                try {
                    city = api.startCityHeadDownload(name);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    error_content.postValue("");
                    return;
                }
                // отправляем шапку
                addCityHeadRequest.postValue(city);
                // получаем дни
                DaysAdapter days = null;
                try {
                    days = api.startCityDaysDownload(name, city.getLat(), city.getLon());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    error_content.postValue("");
                    return;
                }
                // отправляем дни
                addDaysInCityRequest.postValue(days);

                // запрос на вставку/обновление базы данных
                insertOrUpdateCityInBase(city);
            }
        };

        Thread thr = new Thread(task);
        thr.start();
    }

    // database
    private void insertOrUpdateCityInBase(City city){
        dao.insert(city);
    }
    private LinkedList<String> getNamesFromBase(){
        LinkedList<String> result = new LinkedList<>();
        result.addAll(dao.getNames());
        return result;
    }
    private void downloadCitiesFromBase(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                LinkedList<City> result=new LinkedList<>();
                List<City> res = dao.getCities();
                result.addAll(res);
                cities.postValue(result);
            }
        };

        Thread thr = new Thread(task);
        thr.start();
    }

    private void deleteCity(City city){
        // удаляем из пользовательского интерфейса
        deleteCityRequest.setValue(city);

        // удаляем из базы
        Runnable task = new Runnable() {
            @Override
            public void run() {
                dao.delete(city);
            }
        };
        Thread thr = new Thread(task);
        thr.start();
    }
    private LinkedList<City> removeCityFromList(String name){
        LinkedList<City> cities = this.cities.getValue();

        if (cities==null) return new LinkedList<>();
        for (City c: cities){
            if (c.getName().equals(name)){
                cities.remove(c);
                break;
            }
        }
        return cities;
    }
    private void changeFavorite(String name){

    }
    private boolean checkConnection(){
        Boolean connection = ConnectionManager.isOnline();
        this.connection.setValue(connection);
        return connection;
    }
    private boolean isRelevant(long upload_time){
        Date date = new Date();
        long current_time = date.getTime();
        // с момента последней загрузки прошло больше часа?
        return ((current_time-upload_time)>1000*60*60*1? false: true);
    }

    public LiveData<Boolean> getConnection(){return connection;}
    public LiveData<LinkedList<City>> getCities(){return cities;}
    public LiveData<City> getAddCityHeadRequest(){return addCityHeadRequest;}
    public LiveData<City> getDeleteCityRequest(){return deleteCityRequest;}
    public LiveData<DaysAdapter> getAddDaysInCityRequest(){return addDaysInCityRequest;}
    public LiveData<String> getError_content(){return error_content;}

}
