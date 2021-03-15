package com.example.weather7.di;

import android.content.Context;

import com.example.weather7.di.components.AppComponent;
import com.example.weather7.di.components.DaggerAppComponent;
import com.example.weather7.di.components.FCitiesSubcomponent;
import com.example.weather7.di.components.FLocationSubcomponent;
import com.example.weather7.di.components.FNotificationsSubcomponent;
import com.example.weather7.di.modules.base.AppContextModule;
import com.example.weather7.di.modules.cities.CitiesRecyclerViewModule;
import com.example.weather7.di.modules.cities.CitiesVMModule;
import com.example.weather7.di.modules.cities.CityRepositoryModule;
import com.example.weather7.di.modules.location.LocationRepositoryModule;
import com.example.weather7.di.modules.location.LocationVMModule;
import com.example.weather7.di.modules.notifications.NotificationRepositoryModule;
import com.example.weather7.di.modules.notifications.NotificationsVMModule;

public class ComponentManager {

    private AppComponent appComponent;

    private FCitiesSubcomponent fCitiesSubcomponent;
    private FNotificationsSubcomponent fNotificationsSubcomponent;
    private FLocationSubcomponent fLocationSubcomponent;

    public void initAppComponent(Context context){
        appComponent= DaggerAppComponent.builder()
                .appContextModule(new AppContextModule(context))
                .build();
    }

    public AppComponent getAppComponent(){
        return appComponent;
    }

    public FCitiesSubcomponent getFCitiesSubcomponent(){
        if (fCitiesSubcomponent ==null){
            fCitiesSubcomponent =appComponent.getFCitiesComponent(new CitiesVMModule(), new CityRepositoryModule(), new CitiesRecyclerViewModule());
        }
        return fCitiesSubcomponent;
    }
    public void clearFCitiesSubcomponent(){
        fCitiesSubcomponent=null;
    }

    public FNotificationsSubcomponent getFNotificationsSubcomponent(){
        if (fNotificationsSubcomponent ==null){
            fNotificationsSubcomponent =appComponent.getFNotificationsComponent(new NotificationRepositoryModule(),
                    new NotificationsVMModule());
        }
        return fNotificationsSubcomponent;
    }
    public void clearFNotificationsSubcomponent(){
        fNotificationsSubcomponent =null;
    }

    public FLocationSubcomponent getFLocationSubcomponent(){
        if (fLocationSubcomponent ==null){
            fLocationSubcomponent =appComponent.getFLocationComponent(new LocationRepositoryModule(),
                    new LocationVMModule());
        }
        return fLocationSubcomponent;
    }
    public void clearFLocationSubcomponent(){
        fLocationSubcomponent =null;
    }

}
