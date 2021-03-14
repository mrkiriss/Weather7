package com.example.weather7.di.modules.base;

import android.content.Context;

import com.example.weather7.model.base.ThreadFactory;
import com.example.weather7.model.notifications.AlarmRequestFactory;
import com.example.weather7.model.notifications.NotificationFactory;

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
