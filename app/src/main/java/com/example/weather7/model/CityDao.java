package com.example.weather7.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;

@Dao
public interface CityDao {

    @Query("SELECT name FROM city")
    ArrayList<String> getNames();

    @Query("SELECT * FROM city")
    ArrayList<City> getCities();

    @Insert
    void insert(City city);

    @Update
    void update(City city);

    @Delete
    void delete(City city);
}
