package com.example.weather7.model.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.weather7.model.City;

@Database(entities = {City.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CityDao getCityDao();
}
