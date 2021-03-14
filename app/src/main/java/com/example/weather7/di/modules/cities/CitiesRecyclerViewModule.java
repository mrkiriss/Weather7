package com.example.weather7.di.modules.cities;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.weather7.view.cities.adapters.CitiesAdapter;

import dagger.Module;
import dagger.Provides;

@Module
public class CitiesRecyclerViewModule {

    @Provides
    @FCitiesScope
    public CitiesAdapter provideCitiesAdapter(){
        return new CitiesAdapter();
    }

    @Provides
    public LinearLayoutManager provideManager(Context context){
        return new LinearLayoutManager(context);
    }
}
