package com.example.weather7.di.modules.notifications;

import com.example.weather7.database.AppDatabase;
import com.example.weather7.model.base.ThreadFactory;
import com.example.weather7.model.notifications.NotificationFactory;
import com.example.weather7.repository.notifications.NotificationRepository;
import com.example.weather7.utils.AlarmManager;

import dagger.Module;
import dagger.Provides;

@Module
public class NotificationRepositoryModule {

    @Provides
    @FNotificationsScope
    public NotificationRepository provideNotificationRepository(AppDatabase db, AlarmManager am, ThreadFactory tf, NotificationFactory nf){
        return new NotificationRepository(db, am, tf, nf);
    }
}
