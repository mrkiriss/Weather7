package com.example.weather7.di.modules.location;

import com.example.weather7.repository.location.LocationRepository;
import com.example.weather7.viewmodel.LocationViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationVMModule {

    @Provides
    @FLocationScope
    public LocationViewModel provideLocationVM(LocationRepository rep){
        return new LocationViewModel(rep);
    }
}
