package com.example.weather7.di.modules.general;

import com.example.weather7.model.factories.AlarmRequestFactory;
import com.example.weather7.model.factories.NotificationFactory;
import com.example.weather7.model.factories.ThreadFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FactoriesModule {

    @Provides
    @Singleton
    public AlarmRequestFactory provideAlarmRequestFactory(){
        return new AlarmRequestFactory();
    }

    @Provides
    @Singleton
    public ThreadFactory provideThreadFactory(){
        return new ThreadFactory();
    }

    @Provides
    @Singleton
    public NotificationFactory provideNotificationFactory(){
        return new NotificationFactory();
    }
}
