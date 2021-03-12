package com.example.weather7.api;

import com.example.weather7.model.cities.AutoEnteredCity;

import java.util.ArrayList;

public interface ICitiesApi {
    ArrayList<AutoEnteredCity> downloadCities(String part_of_name);
}
