package com.example.weather7.di.modules.general;

import android.content.Context;

import androidx.room.Room;

import com.example.weather7.database.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppDatabaseModule {

    @Singleton
    @Provides
    public AppDatabase provideDatabase(Context context){
        return Room.databaseBuilder(context,
                AppDatabase.class, "database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }
}
