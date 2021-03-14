package com.example.weather7.di;

import android.content.Context;

import com.example.weather7.di.components.AppComponent;
import com.example.weather7.di.components.DaggerAppComponent;
import com.example.weather7.di.components.FCitiesSubcomponent;
import com.example.weather7.di.components.FNotificationsComponent;
import com.example.weather7.di.modules.base.AppContextModule;
import com.example.weather7.di.modules.cities.CitiesRecyclerViewModule;
import com.example.weather7.di.modules.cities.CitiesVMModule;
import com.example.weather7.di.modules.cities.CityRepositoryModule;
import com.example.weather7.di.modules.notifications.NotificationRepositoryModule;
import com.example.weather7.di.modules.notifications.NotificationsVMModule;

public class ComponentManager {

    private AppComponent appComponent;

    private FCitiesSubcomponent fCitiesSubcomponent;
    private FNotificationsComponent fNotificationsComponent;

    public void initAppComponent(Context context){
        appComponent= DaggerAppComponent.builder()
                .appContextModule(new AppContextModule(context))
                .build();
    }

    public AppComponent getAppComponent(){
        return appComponent;
    }

    public FCitiesSubcomponent getFCitiesComponent(){
        if (fCitiesSubcomponent ==null){
            fCitiesSubcomponent =appComponent.getFCitiesComponent(new CitiesVMModule(), new CityRepositoryModule(), new CitiesRecyclerViewModule());
        }
        return fCitiesSubcomponent;
    }
    public void clearFCitiesComponent(){
        fCitiesSubcomponent=null;
    }

    public FNotificationsComponent getFNotificationsComponent(){
        if (fNotificationsComponent==null){
            fNotificationsComponent=appComponent.getFNotificationsComponent(new NotificationRepositoryModule(),
                    new NotificationsVMModule());
        }
        return fNotificationsComponent;
    }
    public void clearFNotificationsComponent(){
        fNotificationsComponent=null;
    }

}
