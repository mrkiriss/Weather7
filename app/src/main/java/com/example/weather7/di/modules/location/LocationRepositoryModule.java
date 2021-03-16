package com.example.weather7.di.modules.location;

import com.example.weather7.api.IWeatherApi;
import com.example.weather7.database.AppDatabase;
import com.example.weather7.model.factories.ThreadFactory;
import com.example.weather7.repository.location.LocationRepository;
import com.example.weather7.utils.ConnectionManager;
import com.example.weather7.utils.GeolocationManager;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationRepositoryModule {

    @Provides
    @FLocationScope
    public LocationRepository provideLocationRepository(AppDatabase db, IWeatherApi weatherApi,
                                                        GeolocationManager gm, ConnectionManager connectionManager, ThreadFactory threadFactory){
        return new LocationRepository(db, weatherApi, connectionManager, gm, threadFactory);
    }
}
