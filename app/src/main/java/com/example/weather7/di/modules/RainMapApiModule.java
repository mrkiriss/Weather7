package com.example.weather7.di.modules;

import com.example.weather7.api.IRainMapApi;
import com.example.weather7.api.IWeatherApi;
import com.example.weather7.api.RainMapApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RainMapApiModule {

    @Singleton
    @Provides
    public IRainMapApi provideRainMapApi(){
        return new RainMapApi();
    }
}
