package com.example.weather7.repository;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weather7.api.CitiesApi;
import com.example.weather7.api.RainMapApi;
import com.example.weather7.model.AutoEnteredCity;
import com.example.weather7.model.City;
import com.example.weather7.api.WeatherApi;
import com.example.weather7.database.AppDatabase;
import com.example.weather7.database.CityDao;
import com.example.weather7.model.DelayMessage;
import com.example.weather7.model.RepositoryRequest;
import com.example.weather7.utils.ConnectionManager;
import com.example.weather7.view.FragmentRainMap;
import com.example.weather7.view.adapters.DaysAdapter;

import org.json.JSONException;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class CityRepository {

    public static final String REQUEST_DELETE="delete";
    public static final String REQUEST_OPEN_CITY_IN_MAP="city_in_map";
    public static final String REQUEST_OPEN_RAIN_MAP="open_rain_map";

    private CityDao dao;
    private WeatherApi api;
    private RainMapApi api_rain;
    private CitiesApi api_cities;

    private MutableLiveData<LinkedList<City>> cities = new MutableLiveData<>();
    private  MutableLiveData<City> addCityHeadRequest = new MutableLiveData<>();
    private MutableLiveData<City> deleteCityRequest = new MutableLiveData<>();
    private MutableLiveData<DaysAdapter> addDaysInCityRequest = new MutableLiveData<>();
    private MutableLiveData<Intent> startIntent = new MutableLiveData<>();
    private MutableLiveData<FragmentRainMap> openRainMap = new MutableLiveData<>();
    private MutableLiveData<List<AutoEnteredCity>> auto_cities = new MutableLiveData<>();

    private static int firs_filling_status=0;
    private LinkedList<String> current_cities_names = new LinkedList<>();

    private MutableLiveData<Boolean> cities_loading=new MutableLiveData<>();
    private MutableLiveData<Boolean> names_cities_loading=new MutableLiveData<>();
    private MutableLiveData<Boolean> connection = new MutableLiveData<>();
    private MutableLiveData<String> error_content = new MutableLiveData<>();

    private final int MIN_INPUT_WORD_LENGTH_FOR_AUTO=4;
    private DelayMessage delayMessage;

    public CityRepository(AppDatabase db, WeatherApi api, RainMapApi api_rain, CitiesApi api_cities){
        this.dao= db.getCityDao();
        this.api=api;
        this.api_rain=api_rain;
        this.api_cities=api_cities;
    }

    public void firstFillingCities(){

        // выход, если оббновление уже запущено
        if (firs_filling_status>0) return;

        clear();

        LinkedList<String> cities_names = getNamesFromBase();
        if (checkConnection()){
            // посылаем запросы на обновление всех имеющихся городов
            for (String name: cities_names){
                runAddingSingleCityFromAPI(name);
            }
        }else{
            for (String name: cities_names){
                runAddingSingleCityFromBase(name);
            }
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
            case REQUEST_OPEN_RAIN_MAP:

                break;
        }
    }

    public void respondToInput(String part_of_name){
        if (part_of_name.length()<MIN_INPUT_WORD_LENGTH_FOR_AUTO) return;

        if (delayMessage==null){
            delayMessage=new DelayMessage();
            //names_cities_loading=delayMessage.getNames_cities_loading();
        }

        Runnable task = () -> {
            names_cities_loading.postValue(true);
            try {
                System.out.println(part_of_name);
                List<AutoEnteredCity> result = api_cities.downloadCities(part_of_name);
                if (result.size()==0){
                    throw new IOException();
                }
                auto_cities.postValue(result);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            names_cities_loading.postValue(false);
        };

        delayMessage.processMessage(task);
    }

    // api
    public void runAddingSingleCityFromAPI(String city_name){
        checkConnection();
        // в одном потоке и шапка, и дни, Чтобы дни не смогли прийти раньше шапки
        Runnable task = () -> {
            // добавляем поток в индикатор
            firs_filling_status++;
            checkLoading();

            // форматируем название города
            String name="";
            if (city_name.length()>1) {
                name = city_name.substring(0, 1).toUpperCase() + city_name.substring(1).toLowerCase();
            }else{
                name = city_name.toUpperCase();
            }

            // получаем город
            City city = downloadSingleCityFromAPI(name);
            if (city==null){
                firs_filling_status--;
                checkLoading();
                return;
            }

            // проверяем на дубликат города
            if (current_cities_names.contains(name)) {
                // удаляем старый объект города из интерфейса
                deleteCityRequest.postValue(city);
                // удаляем из списка нынешних городов на экране
                current_cities_names.remove(name);
            }
            // отправляем шапку
            setAddCityHeadRequest(city);

            // добавляем в список нынешних городов на экране
            current_cities_names.add(name);
            // получаем дни
            DaysAdapter days = downloadSingleDaysAdapterFromAPI(name, city.getLat(), city.getLon());;
            if (days==null){
                firs_filling_status--;
                checkLoading();
                return;
            }

            // отправляем дни
            setAddDaysInCityRequest(days);

            // удаляем поток из индикатора
            firs_filling_status--;
            checkLoading();
            // запрос на вставку/обновление базы данных
            insertOrUpdateCityInBase(city);
        };

        Thread thr = new Thread(task);
        thr.start();
    }

    private City downloadSingleCityFromAPI(String name){
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
    private DaysAdapter downloadSingleDaysAdapterFromAPI(String name, String lat, String lon){
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
    private void runAddingSingleCityFromBase(String city_name){
        String name = city_name;
        // в одном потоке и шапка, и дни, Чтобы дни не смогли прийти раньше шапки
        Runnable task = () -> {
            // добавляем поток в индикатор
            firs_filling_status++;
            checkLoading();

            // получаем город
            City city = downloadSingleCityFromBase(name);
            if (city==null) {
                firs_filling_status--;
                checkLoading();
                return;
            }

            // отправляем шапку
            setAddCityHeadRequest(city);

            // добавляем в список нынешних городов на экране
            current_cities_names.add(name);

            // получаем дни
            DaysAdapter days =downloadSingleDaysAdapterFromBase(name);

            if (days==null){
                firs_filling_status--;
                checkLoading();
                return;
            }

            // отправляем дни
            setAddDaysInCityRequest(days);

            // удаляем поток из индикатора
            firs_filling_status--;
            checkLoading();
        };

        Thread thr = new Thread(task);
        thr.start();
    }
    private City downloadSingleCityFromBase(String name){
        return dao.getCityByName(name);
    }
    private DaysAdapter downloadSingleDaysAdapterFromBase(String name){
        return dao.getCityByName(name).getDaysAdapter();
    }

    // general
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
    private void checkLoading(){
        cities_loading.postValue(firs_filling_status > 0);
    }
    private void clear(){
        cities.setValue(new LinkedList<>());
        current_cities_names.clear();
    }

    private synchronized void setAddCityHeadRequest(City city){
        addCityHeadRequest.postValue(city);
        // постепенная загрузка, без наложения
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private synchronized void setAddDaysInCityRequest(DaysAdapter days){
        addDaysInCityRequest.postValue(days);
        // постепенная загрузка, без наложения
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public LiveData<Boolean> getConnection(){return connection;}
    public LiveData<LinkedList<City>> getCities(){return cities;}
    public LiveData<City> getAddCityHeadRequest(){return addCityHeadRequest;}
    public LiveData<City> getDeleteCityRequest(){return deleteCityRequest;}
    public LiveData<DaysAdapter> getAddDaysInCityRequest(){return addDaysInCityRequest;}
    public LiveData<String> getError_content(){return error_content;}
    public LiveData<Intent> getStartIntent(){return startIntent;}
    public LiveData<FragmentRainMap> getOpenRainMap(){return openRainMap;}
    public LiveData<Boolean> getCities_loading(){return cities_loading;}
    public LiveData<Boolean> getNames_cities_loading() {
        return names_cities_loading;
    }
    public LiveData<List<AutoEnteredCity>> getAuto_cities() {
        return auto_cities;
    }

}
