package com.example.weather7.di.modules;

import android.content.Context;

import com.example.weather7.api.IWeatherApi;
import com.example.weather7.api.WeatherApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class WeatherApiModule {

    @Singleton
    @Provides
    public IWeatherApi provideWeatherApi(Context context){
        return new WeatherApi(context);
    }
}
