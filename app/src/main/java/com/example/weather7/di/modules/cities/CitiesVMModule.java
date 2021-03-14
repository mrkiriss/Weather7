package com.example.weather7.di.modules.cities;

import com.example.weather7.repository.cities.CityRepository;
import com.example.weather7.viewmodel.cities.CitiesViewModel;

import dagger.Module;
import dagger.Provides;

@Module
public class CitiesVMModule {

    @Provides
    @FCitiesScope
    public CitiesViewModel provideCitiesVM(CityRepository cityRepository){
        return new CitiesViewModel(cityRepository);
    }
}
