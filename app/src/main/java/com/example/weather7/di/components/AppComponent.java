package com.example.weather7.di.components;

import com.example.weather7.di.modules.base.ApisModule;
import com.example.weather7.di.modules.base.AppContextModule;
import com.example.weather7.di.modules.base.AppDatabaseModule;
import com.example.weather7.di.modules.base.FactoriesModule;
import com.example.weather7.di.modules.base.UtilsModule;
import com.example.weather7.di.modules.cities.CitiesRecyclerViewModule;
import com.example.weather7.di.modules.cities.CitiesVMModule;
import com.example.weather7.di.modules.cities.CityRepositoryModule;
import com.example.weather7.di.modules.notifications.NotificationRepositoryModule;
import com.example.weather7.di.modules.notifications.NotificationsVMModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {AppContextModule.class, AppDatabaseModule.class, ApisModule.class, UtilsModule.class, FactoriesModule.class})
@Singleton
public interface AppComponent {

    FCitiesSubcomponent getFCitiesComponent(CitiesVMModule citiesVMModule,
                                            CityRepositoryModule cityRepositoryModule,
                                            CitiesRecyclerViewModule citiesRecyclerViewModule);

    FNotificationsComponent getFNotificationsComponent(NotificationRepositoryModule notificationRepositoryModule,
                                                       NotificationsVMModule notificationsVMModule);
}
