package com.example.weather7.di.modules.cities;

import com.example.weather7.api.ICitiesNamesApi;
import com.example.weather7.api.IWeatherApi;
import com.example.weather7.database.AppDatabase;
import com.example.weather7.model.factories.ThreadFactory;
import com.example.weather7.repository.cities.CityRepository;
import com.example.weather7.utils.ConnectionManager;
import com.example.weather7.utils.DelayMessageManager;

import dagger.Module;
import dagger.Provides;

@Module
public class CityRepositoryModule {

    @Provides
    @FCitiesScope
    public CityRepository provideCityRepository(AppDatabase db, IWeatherApi weatherApi,
                                                ICitiesNamesApi citiesNamesApi, ConnectionManager connectionManager,
                                                DelayMessageManager delayMessageManager, ThreadFactory threadFactory){
        return new CityRepository(db, weatherApi ,citiesNamesApi, connectionManager, delayMessageManager, threadFactory);
    }
}
