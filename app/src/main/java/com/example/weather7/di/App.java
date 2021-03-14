package com.example.weather7.di;

import android.app.Application;

public class App extends Application {
    private static App instance;
    private ComponentManager componentManager;

    public static App getInstance(){
        return instance;
    }

    public ComponentManager getComponentManager(){
        return componentManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        componentManager=new ComponentManager();
        componentManager.initAppComponent(instance);
    }
}
