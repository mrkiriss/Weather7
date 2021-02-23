package com.example.weather7.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.LinkedList;
import java.util.List;

@Dao
public interface CityDao {

    @Query("SELECT name FROM City")
    List<String> getNames();

    @Query("SELECT * FROM city")
    List<City> getCities();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(City city);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LinkedList<City> cities);

    @Update
    void update(City city);

    @Delete
    void delete(City city);
}
