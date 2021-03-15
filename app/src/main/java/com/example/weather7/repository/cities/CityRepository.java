package com.example.weather7.repository.cities;

import android.content.Intent;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weather7.api.ICitiesNamesApi;
import com.example.weather7.api.IWeatherApi;
import com.example.weather7.model.cities.AutoEnteredCity;
import com.example.weather7.model.base.City;
import com.example.weather7.database.AppDatabase;
import com.example.weather7.database.CityDao;
import com.example.weather7.model.cities.DelayMessage;
import com.example.weather7.repository.RepositoryRequest;
import com.example.weather7.utils.ConnectionManager;
import com.example.weather7.view.cities.adapters.DaysAdapter;

import org.json.JSONException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class CityRepository {

    public static final String REQUEST_DELETE="delete";
    public static final String REQUEST_OPEN_CITY_IN_MAP="city_in_map";
    public static final String REQUEST_OPEN_RAIN_MAP="open_rain_map";

    private final CityDao dao;
    private final IWeatherApi api;
    private final ICitiesNamesApi api_cities;
    private final ConnectionManager connectionManager;

    private final MutableLiveData<LinkedList<City>> cities;
    private final MutableLiveData<City> addCityHeadRequest;
    private final MutableLiveData<City> deleteCityRequest;
    private final MutableLiveData<DaysAdapter> addDaysInCityRequest;
    private final MutableLiveData<Intent> startIntent;
    private final MutableLiveData<List<AutoEnteredCity>> auto_cities;

    private static int firs_filling_status;
    private final LinkedList<String> current_cities_names;

    private final MutableLiveData<Boolean> cities_loading;
    private final MutableLiveData<Boolean> names_cities_loading;
    private final MutableLiveData<Boolean> connection;
    private final MutableLiveData<String> error_content;

    private final int MIN_INPUT_WORD_LENGTH_FOR_AUTO=3;
    private DelayMessage delayMessage;

    public CityRepository(AppDatabase db, IWeatherApi api, ICitiesNamesApi api_cities, ConnectionManager connectionManager){
        this.dao= db.getCityDao();
        this.api=api;
        this.api_cities=api_cities;
        this.connectionManager=connectionManager;

        cities= new MutableLiveData<>();
        addCityHeadRequest= new MutableLiveData<>();
        deleteCityRequest= new MutableLiveData<>();
        addDaysInCityRequest= new MutableLiveData<>();
        startIntent= new MutableLiveData<>();
        auto_cities= new MutableLiveData<>();
        firs_filling_status=0;
        current_cities_names = new LinkedList<>();
        cities_loading= new MutableLiveData<>();
        names_cities_loading= new MutableLiveData<>();
        connection= new MutableLiveData<>();
        error_content= new MutableLiveData<>();
    }

    public void fillingCities(){

        // выход, если оббновление уже запущено
        if (firs_filling_status>0) return;

        clearCitiesList();

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
                startIntent.setValue(buildRainMapIntent((City) request.getObject()));
                break;
        }
    }

    public void respondToInput(String part_of_name){
        if (part_of_name.length()<MIN_INPUT_WORD_LENGTH_FOR_AUTO) return;

        if (delayMessage==null){
            delayMessage=new DelayMessage();
        }

        Runnable task = () -> {
            delayMessage.addToCountActiveCity(1);
            if (delayMessage.someoneActive()) names_cities_loading.postValue(true);
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
            delayMessage.addToCountActiveCity(-1);
            if (!delayMessage.someoneActive()) names_cities_loading.postValue(false);
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
            city = api.getCityHead(name);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            error_content.postValue("Информация о "+name+" не найдена");
            return city;
        }

        return city;
    }
    private DaysAdapter downloadSingleDaysAdapterFromAPI(String name, String lat, String lon){
        DaysAdapter days = null;
        try {
            days = api.getCityDays(name, lat, lon);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            error_content.postValue("Недельный прогноз для "+name+"не получен");
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
        Runnable task = () -> {
            dao.delete(city);
        };
        Thread thr = new Thread(task);
        thr.start();

    }
    private boolean checkConnection(){
        Boolean connection = connectionManager.networkEnable();
        this.connection.setValue(connection);
        return connection;
    }
    private Intent buildCityInMapIntent(City city){
        Intent i = new Intent();
        i.putExtra("class", "");
        i.setAction(Intent.ACTION_VIEW);
        String data = String.format("geo:%s,%s", city.getLat(), city.getLon());
        i.setData(Uri.parse(data));

        return i;
    }
    private Intent buildRainMapIntent(City city){
        Intent i = new Intent();
        i.putExtra("class", "rain");
        i.putExtra("city_name", city.getName());
        i.putExtra("lat", city.getLat());
        i.putExtra("lon", city.getLon());

        return i;
    }
    private void checkLoading(){
        cities_loading.postValue(firs_filling_status > 0);
    }
    private void clearCitiesList(){
        cities.setValue(new LinkedList<>());
        current_cities_names.clear();
    }

    private synchronized void setAddCityHeadRequest(City city){
        addCityHeadRequest.postValue(city);
        // постепенная загрузка, без наложения
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private synchronized void setAddDaysInCityRequest(DaysAdapter days){
        addDaysInCityRequest.postValue(days);
        // постепенная загрузка, без наложения
        try {
            Thread.sleep(1000);
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
    public LiveData<Boolean> getCities_loading(){return cities_loading;}
    public LiveData<Boolean> getNames_cities_loading() {
        return names_cities_loading;
    }
    public LiveData<List<AutoEnteredCity>> getAuto_cities() {
        return auto_cities;
    }

}
