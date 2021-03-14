package com.example.weather7.di.modules.base;

import android.content.Context;
import android.location.Geocoder;

import com.example.weather7.model.notifications.AlarmRequestFactory;
import com.example.weather7.utils.AlarmManager;
import com.example.weather7.utils.ConnectionManager;
import com.example.weather7.utils.GeolocationManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UtilsModule {


    @Singleton
    @Provides
    public ConnectionManager provideConnectionManager(Context context){
        return new ConnectionManager(context);
    }

    @Singleton
    @Provides
    public AlarmManager provideAlarmManager(Context context){
        return new AlarmManager(context,
                (android.app.AlarmManager)context.getSystemService(Context.ALARM_SERVICE),
                new AlarmRequestFactory());
    }

    @Singleton
    @Provides
    public GeolocationManager provideGeolocationManager(Context context){
        return new GeolocationManager(new Geocoder(context));
    }
}
