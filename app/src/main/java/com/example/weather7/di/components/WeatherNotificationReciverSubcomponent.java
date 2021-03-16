package com.example.weather7.di.components;

import com.example.weather7.services.WeatherNotificationReceiver;

import dagger.Subcomponent;

@Subcomponent
public interface WeatherNotificationReciverSubcomponent {
    void inject(WeatherNotificationReceiver weatherNotificationReceiver);
}
