package com.example.weather7.di.components;

import com.example.weather7.di.modules.location.FLocationScope;
import com.example.weather7.di.modules.location.LocationRepositoryModule;
import com.example.weather7.di.modules.location.LocationVMModule;
import com.example.weather7.view.FragmentLocation;

import dagger.Subcomponent;

@Subcomponent(modules = {LocationRepositoryModule.class, LocationVMModule.class})
@FLocationScope
public interface FLocationSubcomponent {

    public void inject(FragmentLocation fragmentLocation);
}
