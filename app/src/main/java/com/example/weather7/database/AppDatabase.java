package com.example.weather7.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.weather7.model.City;

@Database(entities = {City.class}, version = 5, exportSchema=  false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CityDao getCityDao();
}
