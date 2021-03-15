package com.example.weather7.repository.location;

import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weather7.api.IWeatherApi;
import com.example.weather7.database.AppDatabase;
import com.example.weather7.database.CityDao;
import com.example.weather7.model.base.City;
import com.example.weather7.model.base.ThreadFactory;
import com.example.weather7.utils.ConnectionManager;
import com.example.weather7.utils.GeolocationManager;
import com.example.weather7.view.cities.adapters.DaysAdapter;

import org.json.JSONException;

import java.io.IOException;

public class LocationRepository {

    private final CityDao cityDao;
    private final IWeatherApi weatherApi;
    private final ConnectionManager connectionManager;
    private final GeolocationManager geolocationManager;
    private final ThreadFactory threadFactory;

    private MutableLiveData<Boolean> networkConnection;
    private MutableLiveData<String> toastContent;
    private MutableLiveData<City> addCityRequest;
    private MutableLiveData<DaysAdapter> addDaysAdapterRequest;

    private int activeFillContentBaseThreadCount;
    private int activeFillContentApiThreadCount;

    public LocationRepository(AppDatabase db, IWeatherApi weatherApi, ConnectionManager connectionManager,
                              GeolocationManager geolocationManager, ThreadFactory threadFactory){
        this.cityDao=db.getCityDao();
        this.weatherApi=weatherApi;
        this.connectionManager=connectionManager;
        this.threadFactory=threadFactory;
        this.geolocationManager=geolocationManager;

        this.networkConnection=new MutableLiveData<>();
        this.toastContent=new MutableLiveData<>();
        this.addCityRequest=new MutableLiveData<>();
        this.addDaysAdapterRequest=new MutableLiveData<>();
    }

    public void fillCityContent(Location location){
        if (location==null || !checkConnection()){
            if (activeFillContentBaseThreadCount>0) return;
            downloadCityContentFromBase();
        }else{
            if (activeFillContentApiThreadCount>0) return;
            downloadCityContentFromApi(location);
        }
    }

    //api
    private void downloadCityContentFromApi(Location location){
        Runnable task = () -> {
            activeFillContentApiThreadCount++;

            City city = downloadSingleCityHeadFromAPI(location);
            if (city==null) return;
            addCityRequest.postValue(city);

            DaysAdapter daysAdapter = downloadSingleDaysAdapterFromAPI(city.getName(), city.getLat(), city.getLon());
            if (daysAdapter==null) return;
            addDaysAdapterRequest.postValue(daysAdapter);
            Log.println(Log.INFO, "locationRep", "location days adapter was post from repository");

            city.setDaysAdapter(daysAdapter);
            updateLocationCityDataInBase(city);

            activeFillContentApiThreadCount--;
        };

        threadFactory.newThread(task).start();
    }
    private City downloadSingleCityHeadFromAPI(Location location){
        City city = null;
        try {
            city = weatherApi.getLocationCityContent(location);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            toastContent.postValue("Информация о текущем местоположении не найдена");
            return city;
        }

        return city;
    }
    private DaysAdapter downloadSingleDaysAdapterFromAPI(String name, String lat, String lon){
        DaysAdapter days = null;
        try {
            days = weatherApi.getCityDays(name, lat, lon);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            toastContent.postValue("Недельный прогноз для "+name+"не получен");
        }
        return days;
    }

    // database
    private void downloadCityContentFromBase(){
        Runnable task = () -> {
            activeFillContentBaseThreadCount++;
            City city = downloadLocationCityFromBase();
            if (city==null){
                toastContent.postValue("Город из локального хранилища не получен");
                return;
            }
            addCityRequest.postValue(city);
            addDaysAdapterRequest.postValue(city.getDaysAdapter());
            activeFillContentBaseThreadCount--;
        };

        threadFactory.newThread(task).start();
    }
    private City downloadLocationCityFromBase(){
        return cityDao.getLocationCity(true);
    }
    private void updateLocationCityDataInBase(City city){
        deleteLocationCityFromBase();
        city.setIsLocationCity(true);
        addLocationCityFromBase(city);
    }
    private void deleteLocationCityFromBase(){
        cityDao.deleteLocationCity(true);
    }
    private void addLocationCityFromBase(City city){
        cityDao.insert(city);
    }

    //general
    private boolean checkConnection(){
        Boolean connection = connectionManager.networkEnable();
        networkConnection.setValue(connection);
        return connection;
    }

    public LiveData<Boolean> getNetworkConnection(){return networkConnection;}
    public LiveData<String> getToastContent() {
        return toastContent;
    }
    public LiveData<City> getAddCityRequest() {
        return addCityRequest;
    }
    public LiveData<DaysAdapter> getAddDaysAdapterRequest() {
        return addDaysAdapterRequest;
    }

}
