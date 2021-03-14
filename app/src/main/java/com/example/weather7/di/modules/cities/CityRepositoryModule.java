package com.example.weather7.di.modules.cities;

import com.example.weather7.api.ICitiesNamesApi;
import com.example.weather7.api.IWeatherApi;
import com.example.weather7.database.AppDatabase;
import com.example.weather7.repository.cities.CityRepository;
import com.example.weather7.utils.ConnectionManager;

import dagger.Module;
import dagger.Provides;

@Module
public class CityRepositoryModule {

    @Provides
    @FCitiesScope
    public CityRepository provideCityRepository(AppDatabase db, IWeatherApi weatherApi, ICitiesNamesApi citiesNamesApi, ConnectionManager connectionManager){
        return new CityRepository(db, weatherApi ,citiesNamesApi, connectionManager);
    }
}
