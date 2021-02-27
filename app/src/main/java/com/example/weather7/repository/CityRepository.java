package com.example.weather7.repository;

import android.content.Intent;
import android.net.Uri;

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
    public static final String REQUEST_OPEN_CITY_IN_MAP="city_in_map";
    public static final String REQUEST_REFRESH_CITIES="refresh_cities";

    private CityDao dao;
    private WeatherApi api;
    private MutableLiveData<Boolean> connection = new MutableLiveData<>(); // состояние интернет соединения для выброса уведомления
    private MutableLiveData<LinkedList<City>> cities = new MutableLiveData<>();

    private MutableLiveData<City> addCityHeadRequest = new MutableLiveData<>();
    private long last_post_head_time=0;
    private MutableLiveData<City> deleteCityRequest = new MutableLiveData<>();
    private MutableLiveData<DaysAdapter> addDaysInCityRequest = new MutableLiveData<>();
    private MutableLiveData<Intent> startIntent = new MutableLiveData<>();
    private MutableLiveData<String> error_content = new MutableLiveData<>();

    private LinkedList<String> current_cities_names = new LinkedList<>();


    public CityRepository(AppDatabase db, WeatherApi api){
        this.dao= db.getCityDao();
        this.api=api;
    }

    public void firstFillingCities(){
        // обнуляем данные
        cities.setValue(new LinkedList<>());

        if (checkConnection()){

            // посылаем запросы на обновление всех имеющихся городов
            LinkedList<String> cities_names = getNamesFromBase();
            for (String name: cities_names){
                runAddingSingleCityFromAPI(name);
            }
        }else{
            downloadCitiesFromBase();
        }
    }

    public void processRequest(RepositoryRequest request){

        switch (request.getMode()){
            case REQUEST_DELETE:
                deleteCity((City) request.getObject());
                break;
            case REQUEST_OPEN_CITY_IN_MAP:
                startIntent.setValue(buildCityInMapIntent((City) request.getObject()));
                break;
        }
    }

    // api
    public void runAddingSingleCityFromAPI(String city_name){
        checkConnection();
        // в одном потоке и шапка, и дни, Чтобы дни не смогли прийти раньше шапки
        Runnable task = new Runnable() {
            @Override
            public void run() {
                // форматируем название города
                String name = city_name.substring(0, 1).toUpperCase() + city_name.substring(1).toLowerCase();

                // получаем город
                City city = downloadSingleCity(name);
                if (city==null) return;

                // проверяем на дубликат города
                if (current_cities_names.contains(name)) {
                    // удаляем старый объект города из интерфейса
                    deleteCityRequest.postValue(city);
                    // удаляем из списка нынешних городов на экране
                    current_cities_names.remove(name);
                }
                // отправляем шапку
                synchronized (addCityHeadRequest){
                    System.out.println("Отправлен "+name+new Date().getTime());
                    addCityHeadRequest.postValue(city);
                    // постепенная загрузка, без наложения
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // добавляем в список нынешних городов на экране
                current_cities_names.add(name);
                // получаем дни
                DaysAdapter days =null;
                days = downloadSingleDaysAdapter(name, city.getLat(), city.getLon());

                // отправляем дни
                addDaysInCityRequest.postValue(days);

                // запрос на вставку/обновление базы данных
                insertOrUpdateCityInBase(city);
            }
        };

        Thread thr = new Thread(task);
        thr.start();
    }
    private City downloadSingleCity(String name){
        City city = null;
        try {
            city = api.startCityHeadDownload(name);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            error_content.postValue("Город "+name+" не найден");
            return city;
        }
        // заполняем время загрузки
        Date date = new Date();
        city.setUpload_time(date.getTime());

        return city;
    }
    private DaysAdapter downloadSingleDaysAdapter(String name, String lat, String lon){
        DaysAdapter days = null;
        try {
            days = api.startCityDaysDownload(name, lat, lon);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            error_content.postValue("Прогноз для города"+name+"не получени");
        }
        return days;
    }
    // database
    private void insertOrUpdateCityInBase(City city){
        dao.insert(city);
    }
    private LinkedList<String> getNamesFromBase(){
        return new LinkedList<>(dao.getNames());
    }
    private void downloadCitiesFromBase(){
        Runnable task = () -> {
            LinkedList<City> result=new LinkedList<>();
            List<City> res = dao.getCities();
            result.addAll(res);
            cities.postValue(result);
        };

        Thread thr = new Thread(task);
        thr.start();
    }
    private long getUpload_timeFromBase(String city_name){
        return dao.getUpload_time(city_name);
    }
    private void deleteCity(City city){
        // удаляем из пользовательского интерфейса
        deleteCityRequest.setValue(city);
        // удаляем из списка нынешних городов на экране
        current_cities_names.remove(city.getName());

        // удаляем из базы
        Runnable task = () -> dao.delete(city);
        Thread thr = new Thread(task);
        thr.start();
    }
    private boolean checkConnection(){
        Boolean connection = ConnectionManager.isOnline();
        this.connection.setValue(connection);
        return connection;
    }
    private Intent buildCityInMapIntent(City city){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);
        String data = String.format("geo:%s,%s", city.getLat(), city.getLon());
        i.setData(Uri.parse(data));

        return i;
    }
    public void clear(){
        cities.setValue(new LinkedList<>());
        current_cities_names.clear();
    }
/*
    private boolean isRelevant(long upload_time){
        Date date = new Date();
        long current_time = date.getTime();
        // с момента последней загрузки прошло больше 30 минут?
        return ((current_time-upload_time)>1000*60*60*0.5? false: true);
    }
*/
    public LiveData<Boolean> getConnection(){return connection;}
    public LiveData<LinkedList<City>> getCities(){return cities;}
    public LiveData<City> getAddCityHeadRequest(){return addCityHeadRequest;}
    public LiveData<City> getDeleteCityRequest(){return deleteCityRequest;}
    public LiveData<DaysAdapter> getAddDaysInCityRequest(){return addDaysInCityRequest;}
    public LiveData<String> getError_content(){return error_content;}
    public MutableLiveData<Intent> getStartIntent(){return startIntent;}

}
