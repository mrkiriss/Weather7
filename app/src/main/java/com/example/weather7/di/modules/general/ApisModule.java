package com.example.weather7.di.modules.general;

import com.example.weather7.api.CitiesNamesApi;
import com.example.weather7.api.ICitiesNamesApi;
import com.example.weather7.api.IRainMapApi;
import com.example.weather7.api.IWeatherApi;
import com.example.weather7.api.RainMapApi;
import com.example.weather7.api.WeatherApi;
import com.example.weather7.utils.GeolocationManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApisModule {

    @Singleton
    @Provides
    public IRainMapApi provideRainMapApi(){
        return new RainMapApi();
    }

    @Singleton
    @Provides
    public ICitiesNamesApi provideCitiesNamesApi(){
        return new CitiesNamesApi();
    }

    @Singleton
    @Provides
    public IWeatherApi provideWeatherApi(GeolocationManager geolocationManager){
        return new WeatherApi(geolocationManager);
    }
}
