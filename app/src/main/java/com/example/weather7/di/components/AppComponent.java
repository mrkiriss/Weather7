package com.example.weather7.di.components;

import com.example.weather7.di.modules.general.ApisModule;
import com.example.weather7.di.modules.general.AppContextModule;
import com.example.weather7.di.modules.general.AppDatabaseModule;
import com.example.weather7.di.modules.general.FactoriesModule;
import com.example.weather7.di.modules.general.UtilsModule;
import com.example.weather7.di.modules.cities.CitiesRecyclerViewModule;
import com.example.weather7.di.modules.cities.CitiesVMModule;
import com.example.weather7.di.modules.cities.CityRepositoryModule;
import com.example.weather7.di.modules.location.LocationRepositoryModule;
import com.example.weather7.di.modules.location.LocationVMModule;
import com.example.weather7.di.modules.notifications.NotificationRepositoryModule;
import com.example.weather7.di.modules.notifications.NotificationsVMModule;
import com.example.weather7.model.notifications.Notification;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {AppContextModule.class, AppDatabaseModule.class, ApisModule.class, UtilsModule.class, FactoriesModule.class})
@Singleton
public interface AppComponent {

    FCitiesSubcomponent getFCitiesSubcomponent(CitiesVMModule citiesVMModule,
                                               CityRepositoryModule cityRepositoryModule,
                                               CitiesRecyclerViewModule citiesRecyclerViewModule);

    FNotificationsSubcomponent getFNotificationsSubcomponent(NotificationRepositoryModule notificationRepositoryModule,
                                                             NotificationsVMModule notificationsVMModule);

    FLocationSubcomponent getFLocationSubcomponent(LocationRepositoryModule locationRepositoryModule,
                                                   LocationVMModule locationVMModule);

    WeatherNotificationReciverSubcomponent getNotificationReciverSubcomponent();
}