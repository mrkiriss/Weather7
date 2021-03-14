package com.example.weather7.di.components;

import com.example.weather7.di.modules.cities.CitiesRecyclerViewModule;
import com.example.weather7.di.modules.cities.CitiesVMModule;
import com.example.weather7.di.modules.cities.CityRepositoryModule;
import com.example.weather7.di.modules.cities.FCitiesScope;
import com.example.weather7.view.cities.FragmentCities;

import dagger.Subcomponent;

@Subcomponent(modules = {CitiesVMModule.class, CityRepositoryModule.class, CitiesRecyclerViewModule.class})
@FCitiesScope
public interface FCitiesSubcomponent {

    void inject(FragmentCities fragmentCities);
}

