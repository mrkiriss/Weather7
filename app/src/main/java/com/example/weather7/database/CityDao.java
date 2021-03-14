package com.example.weather7.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.weather7.model.base.City;

import java.util.LinkedList;
import java.util.List;

@Dao
public interface CityDao {

    @Query("SELECT name FROM City")
    List<String> getNames();

    @Query("SELECT * FROM city")
    List<City> getCities();

    @Query("SELECT * FROM City WHERE name = :name")
    City getCityByName(String name);

    @Query("SELECT upload_time FROM City WHERE name = :name")
    long getUpload_time(String name);

    @Query("DELETE FROM city WHERE name = :name")
    void deleteByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(City city);

    @Update
    void update(City city);

    @Update
    void update(LinkedList<City> cities);

    @Delete
    void delete(City city);

}
