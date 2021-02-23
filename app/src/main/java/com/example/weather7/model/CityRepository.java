package com.example.weather7.model;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.weather7.model.api.WeatherApi;
import com.example.weather7.model.database.AppDatabase;
import com.example.weather7.model.database.CityDao;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CityRepository {

    private CityDao dao;
    private MutableLiveData<Boolean> connection = new MutableLiveData<>(); // состояние интернет соединения для выброса уведомления
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();; // индификатор для отображения значка загрузки
    private MutableLiveData<LinkedList<City>> cities = new MutableLiveData<>();;

    public CityRepository(AppDatabase db){
        this.dao= db.getCityDao();
    }

    // вызывается в конструкторе viewModel
    // должен положить в cities список городов (из базы или обновлённый из сети - зависит от наличия подключения)
    public void downloadCities(){
        checkConnection();
        this.loading.setValue(true);

        Runnable task=null;
        Thread thread = null;
        if (connection.getValue()){
            // запускаем поток для получения городов с использованием api
            task = new Runnable() {
                @Override
                public void run() {
                    downloadCitiesFromApi();
                    // объявляем загрузку оконченной
                    loading.postValue(false);
                    // актуализируем данные в базе
                    if (cities.getValue()!=null) dao.update(cities.getValue());
                }};
        }else{
            // запускаем поток для получения городов без api
            task = new Runnable() {
                @Override
                public void run() {
                    downloadCitiesFromBase();
                    loading.postValue(false);
                }};
        }
        thread = new Thread(task);
        thread.start();
    }
    private void downloadCitiesFromApi(){
        LinkedList<City> result = new LinkedList<>();
        // получаем список объектов городов в которых только имя и время загрузки для проверки актуальности(стоит ли обновлять через api)
        List<City.DeficientCity> defCities= dao.getDeficientCities();
        City city = null;

        for(City.DeficientCity c: defCities){
            if (isRelevant(c.upload_time)){
                city = dao.getCityByName(c.name);
            }else{
                city = new City(WeatherApi.MODE_ONLY_WEATHER, c.lat+" "+c.lon);
            }
            result.add(city);
        }
        this.cities.postValue(result);
    }
    private void downloadCitiesFromBase(){
        LinkedList<City> result=new LinkedList<>();
        List<City> res = dao.getCities();
        result.addAll(res);
        this.cities.postValue(result);
    }

    public void downloadCityFromApi(int mode, String data){
        checkConnection();
        loading.setValue(true);

        if(connection.getValue()){
            City city= new City(mode, data);

            LinkedList<City> deprecated_cities = cities.getValue();
            if (deprecated_cities==null) deprecated_cities=new LinkedList<>();
            deprecated_cities.add(city);
            cities.setValue(deprecated_cities);

            dao.insert(city);
        }else{
            // вызывает уведомление о отсутсвии подключения
            connection.setValue(false);
        }

        loading.setValue(false);
    }


    private void checkConnection(){
        Boolean connection = false;
        // какая-то реализация
        //
        this.connection.setValue(connection);
    }
    private boolean isRelevant(long upload_time){
        Date date = new Date();
        long current_time = date.getTime();
        // с момента последней загрузки прошло больше часа?
        return ((current_time-upload_time)>1000*60*60*1? false: true);
    }

    public MutableLiveData<Boolean> getConnection(){return connection;}
    public MutableLiveData<Boolean> getLoading(){return loading;}
    public MutableLiveData<LinkedList<City>> getCities(){return cities;}

}
