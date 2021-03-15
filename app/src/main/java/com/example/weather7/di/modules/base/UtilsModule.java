package com.example.weather7.di.modules.base;

import android.content.Context;
import android.location.Geocoder;

import com.example.weather7.model.notifications.AlarmRequestFactory;
import com.example.weather7.utils.AlarmManager;
import com.example.weather7.utils.ConnectionManager;
import com.example.weather7.utils.GeolocationManager;

import java.util.Locale;

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
    public GeolocationManager provideGeolocationManager(Geocoder geocoder, Context context){
        return new GeolocationManager(geocoder, context);
    }

    @Provides
    public Geocoder provideGeocoder(Context context){
        Locale aLocale = new Locale.Builder().setLanguage("ru").setScript("Latn").setRegion("RS").build();
        Geocoder geocoder =  new Geocoder(context, aLocale);
        return geocoder;
    }
}
