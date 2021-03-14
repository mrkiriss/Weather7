package com.example.weather7.repository.location;

import android.location.LocationManager;

import com.example.weather7.api.IWeatherApi;
import com.example.weather7.database.AppDatabase;

public class LocationRepository {

    AppDatabase db;
    IWeatherApi weatherApi;
    LocationManager locationManager;

    public LocationRepository(AppDatabase db, IWeatherApi weatherApi, LocationManager locationManager){

    }
}
